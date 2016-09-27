package com.bigbang.superteam.expenses;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.BaseInputConnection;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.WorkItem_GCM;
import com.bigbang.superteam.adapter.AttachmentAdapter;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.DecimalDigitsInputFilter;
import com.bigbang.superteam.util.ImageFilePath;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddExpensesActivity extends BaseActivity {

    @InjectView(R.id.layoutExpensesHead)
    LinearLayout layoutExpensesHead;
    @InjectView(R.id.tv_start_time)
    TextView tv_start_time;
    @InjectView(R.id.tv_end_time)
    TextView tv_end_time;
    @InjectView(R.id.etTotalReqAmt)
    EditText etTotalReqAmt;
    @InjectView(R.id.rlApprovels)
    RelativeLayout rlApprovels;
    @InjectView(R.id.etTotalAppAmt)
    EditText etTotalAppAmt;
    @InjectView(R.id.etComments)
    EditText etComments;
    @InjectView(R.id.tv_app_comments)
    TextView tv_app_comments;
    @InjectView(R.id.etApprovalsComments)
    EditText etApprovalsComments;

    TransparentProgressDialog progressDialog;

    //Date Selection
    public static final int END_DATE = 105;
    public static final int START_DATE = 106;
    Calendar calendar;
    Calendar startCalendar, endCalendar;
    int curMonth, curYear, curDate;

    //Expense Type
    String[] expenseType;
    double totalReqAmt = 0.0;

    //Upload Attachment
    public static final int BROWSE_ACTIVITY = 101;
    private static final int SELECT_FILE = 109;
    String camera_pathname = "";
    TextView textViewAttachment;
    RelativeLayout relativeAttachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);
        init();
    }

    private void init() {
        final Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        ButterKnife.inject(this);

        progressDialog = new TransparentProgressDialog(this, R.drawable.progressdialog, false);

        calendar = Calendar.getInstance();
        curMonth = calendar.get(calendar.MONTH);
        curYear = calendar.get(calendar.YEAR);
        curDate = calendar.get(calendar.DAY_OF_MONTH);

        tv_start_time.setText("" + Util.dateFormatLocal("" + curDate + "/" + (curMonth + 1) + "/" + curYear));
        tv_end_time.setText("" + Util.dateFormatLocal("" + curDate + "/" + (curMonth + 1) + "/" + curYear));

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        rlApprovels.setVisibility(View.GONE);
        tv_app_comments.setVisibility(View.GONE);
        etApprovalsComments.setVisibility(View.GONE);

        fillExpenseType();
    }

    private void fillExpenseType() {

        if (Util.isOnline(this)) {

            if (progressDialog != null)
                progressDialog.show();

            RestClient.getCommonService().getExpenseType(new Callback<String[]>() {
                @Override
                public void success(String[] type, Response response) {
                    try {
                        Map<String, String> map = Util.readStatus(response);
                        boolean isSuccess = map.get("status").equals("Success");
                        String json = Util.getString(response.getBody().in());
                        JSONObject jObj = new JSONObject(json);

                        if (isSuccess) {
                            expenseType = type;
                            addExpenseHead(true);
                        } else {
                            toast(jObj.getString("message"));
                        }
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        Log.d("", "Exception: " + e);
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    progressDialog.dismiss();
                }
            });

        } else {
            toast(Constant.network_error);
        }
    }

    private void addExpenseHead(boolean isFirstTime) {

        final View child = LayoutInflater.from(this).inflate(R.layout.layout_expense_head, null);

        final Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) child;
        Util.setAppFont(mContainer, mFont);

        RelativeLayout rlAddAttachment = (RelativeLayout) child.findViewById(R.id.rlAddAttachment);
        RelativeLayout rlChildApprovals = (RelativeLayout) child.findViewById(R.id.rlChildApprovals);
        Spinner spnExpenseType = (Spinner) child.findViewById(R.id.spnExpenseType);
        final EditText etRequestedAmt = (EditText) child.findViewById(R.id.etRequestedAmt);
        View viewSeparator = (View) child.findViewById(R.id.viewSeparator);
        final TextView tv_attachment = (TextView) child.findViewById(R.id.tv_attachment);
        final ImageView imgDeleteAttachment = (ImageView) child.findViewById(R.id.imgDeleteAttachment);
        final RelativeLayout rlAttachment = (RelativeLayout) child.findViewById(R.id.rlAttachment);
        ImageView imgDeleteExpense = (ImageView) child.findViewById(R.id.imgDeleteExpense);

        if (isFirstTime) {
            viewSeparator.setVisibility(View.GONE);
            imgDeleteExpense.setVisibility(View.GONE);
        }

        etRequestedAmt.requestFocus();
        //etRequestedAmt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});

        rlChildApprovals.setVisibility(View.GONE);
        rlAttachment.setVisibility(View.GONE);

        rlAddAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    textViewAttachment = tv_attachment;
                    relativeAttachment = rlAttachment;

                    String mimeType = "*/*";
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType(mimeType);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, BROWSE_ACTIVITY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imgDeleteAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlAttachment.setVisibility(View.GONE);
                tv_attachment.setText("");
            }
        });

        if (expenseType != null && expenseType.length > 0) {
            ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, expenseType);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnExpenseType.setAdapter(aa);
        }

        final BaseInputConnection tfInputReqAmt = new BaseInputConnection(etRequestedAmt, true);

        //___________________ To make to requested amount total ______________________
        etRequestedAmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                totalReqAmt = 0.0;
                double expense = 0.0;

                if (etRequestedAmt.getText().toString().length() == 1 && etRequestedAmt.getText().toString().equalsIgnoreCase(".")) {
                    etRequestedAmt.setText("0.");
                    etRequestedAmt.setSelection(etRequestedAmt.getText().length());
                    etRequestedAmt.setError(null);
                } else if (etRequestedAmt.getText().toString().length() > 0) {
                    double limit = Double.parseDouble(etRequestedAmt.getText().toString());
                    if (limit > 999999.99) {
                        tfInputReqAmt.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                        toast("You can add maximum requested amount 999999.99");
                    }

                    etRequestedAmt.setError(null);
                    /*expense = Double.parseDouble(etRequestedAmt.getText().toString());
                    totalReqAmt += expense;*/
                }

                int count = layoutExpensesHead.getChildCount();
                for (int j = 0; j < count; j++) {
                    View childView = layoutExpensesHead.getChildAt(j);
                    final EditText eTxtReqExpense = (EditText) childView.findViewById(R.id.etRequestedAmt);
                    if (eTxtReqExpense.getText().length() > 0) {
                        expense = Double.parseDouble(eTxtReqExpense.getText().toString());
                        totalReqAmt += expense;
                    }
                }
                etTotalReqAmt.setText(String.format("%.2f", totalReqAmt));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgDeleteExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutExpensesHead.removeView(child);
                if (etRequestedAmt.getText().toString().length() > 0) {
                    double total = Double.parseDouble(etTotalReqAmt.getText().toString())
                            - Double.parseDouble(etRequestedAmt.getText().toString());
                    etTotalReqAmt.setText(String.format("%.2f", total));
                }
            }
        });

        layoutExpensesHead.addView(child);
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.rlAddExpHead)
    @SuppressWarnings("unused")
    public void AddHead(View view) {
        addExpenseHead(false);
    }

    @OnClick(R.id.rl_start_time_)
    void selectStartDate() {
        showDialog(START_DATE);
    }

    @OnClick(R.id.rl_end_time_)
    void selectEndDate() {
        showDialog(END_DATE);
    }

    @OnClick(R.id.rl_next)
    @SuppressWarnings("unused")
    public void Next(View view) {

        if (Util.isOnline(this)) {
            if (isValidate()) {

                if (progressDialog != null)
                    progressDialog.show();

                JSONObject jsonObject = new JSONObject();
                try {
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
                    String startTime = sdf2.format(startCalendar.getTime());
                    String endTime = sdf2.format(endCalendar.getTime());

                    jsonObject.put("startDate", startTime + " 00:00:00");
                    jsonObject.put("endDate", endTime + " 00:00:00");
                    jsonObject.put("totalExpenseRequested", etTotalReqAmt.getText().toString());
                    jsonObject.put("requesterComment", etComments.getText().toString());

                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < layoutExpensesHead.getChildCount(); i++) {

                        View child = layoutExpensesHead.getChildAt(i);

                        JSONObject innerObject = new JSONObject();

                        innerObject.put("expenseType", ((Spinner) child.findViewById(R.id.spnExpenseType)).getSelectedItem());
                        innerObject.put("amountRequested", ((EditText) child.findViewById(R.id.etRequestedAmt)).getText().toString());
                        innerObject.put("billNo", ((EditText) child.findViewById(R.id.etBillNo)).getText().toString());
                        innerObject.put("billAttachmentPath", ((TextView) child.findViewById(R.id.tv_attachment)).getText().toString());

                        jsonArray.put(innerObject);
                    }

                    if (jsonArray != null)
                        jsonObject.put("expenseDetailsList", jsonArray);

                    Log.i("Add Expense", "Input JSON : " + jsonObject.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                RestClient.getCommonService().addExpense(jsonObject.toString(),
                        Constant.AppName,
                        Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ID),
                        Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERID),
                        Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_TOKEN), new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                try {
                                    Map<String, String> map = Util.readStatus(response);
                                    boolean isSuccess = map.get("status").equals("Success");
                                    String json = Util.getString(response.getBody().in());
                                    JSONObject jObj = new JSONObject(json);

                                    String status = jObj.optString("status");
                                    if (status.equals(Constant.InvalidToken)) {
                                        TeamWorkApplication.LogOutClear(AddExpensesActivity.this);
                                        return;
                                    }

                                    if (isSuccess) {
                                        toast(jObj.getString("message"));

                                        finish();
                                        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);

                                    } else {
                                        toast(jObj.getString("message"));
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    Log.d("", "Exception: " + e);
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                progressDialog.dismiss();
                            }
                        });
            }
        } else {
            toast(Constant.network_error);
        }
    }

    private boolean isValidate() {

        if (startCalendar.after(endCalendar)) {
            toast("End date is greater than or equal to start date");
            return false;
        }

        for (int i = 0; i < layoutExpensesHead.getChildCount(); i++) {
            View child = layoutExpensesHead.getChildAt(i);
            if (((EditText) child.findViewById(R.id.etRequestedAmt)).getText().toString().length() == 0) {
                toast("Please enter requested amount");
                return false;
            }
        }

        for (int i = 0; i < layoutExpensesHead.getChildCount(); i++) {
            View child = layoutExpensesHead.getChildAt(i);
            if (Double.parseDouble(((EditText) child.findViewById(R.id.etRequestedAmt)).getText().toString()) <= 0) {
                toast("Requested amount must be greater than 0");
                return false;
            }
        }
        return true;
    }

    //_________________________________ Date Selection ______________________________________

    protected Dialog onCreateDialog(int id) {
        if (id == END_DATE) {
            DatePickerDialog d = new DatePickerDialog(this,
                    DatePickerDialog.THEME_HOLO_LIGHT, mytoDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            d.getDatePicker().setMaxDate(new Date().getTime());
            return d;
        }
        if (id == START_DATE) {

            DatePickerDialog d = new DatePickerDialog(this,
                    DatePickerDialog.THEME_HOLO_LIGHT, myfromDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            d.getDatePicker().setMaxDate(new Date().getTime());
            return d;
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener mytoDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, month);
            endCalendar.set(Calendar.DAY_OF_MONTH, day);

            if (startCalendar.after(endCalendar)) {
                Toast.makeText(getApplicationContext(), "End date is greater than or equal to start date", Toast.LENGTH_SHORT).show();
            }

            SimpleDateFormat sdf1 = new SimpleDateFormat(" d" + " MMM" + "," + " yyyy");
            String sdate = sdf1.format(endCalendar.getTime());
            tv_end_time.setText(sdate);
        }
    };
    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, day);

            SimpleDateFormat sdf1 = new SimpleDateFormat(" d" + " MMM" + "," + " yyyy");
            String sdate = sdf1.format(startCalendar.getTime());
            tv_start_time.setText(sdate);
        }
    };

    //_____________________________ Upload Attachment _________________________________
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("resultCode", ">>" + requestCode + ":" + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case BROWSE_ACTIVITY:
                    if (null == data) return;
                    Uri uri = data.getData();
                    String file = ImageFilePath.getPath(getApplicationContext(), uri);
                    if (Arrays.asList("txt", "csv", "xls", "xlsx", "png", "docx", "doc", "jpg",
                            "jpeg", "pdf").contains(Util.getFilenameType(file))) {
                        AddAttachment(file);
                    } else {
                        toast(getResources().getString(R.string.file_not_supported));
                    }
                    break;
                case SELECT_FILE:
                    try {
                        // When an Image is picked
                        if (data != null) {
                            // Get the Image from data
                            Uri selectedImageUri = data.getData();
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            camera_pathname = cursor.getString(columnIndex);
                            cursor.close();
                            Log.d("img path", camera_pathname);
                            AddAttachment(camera_pathname);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void AddAttachment(String path) {
        if (path != null && path.length() > 4) {
            if (Util.checkFileSize(path)) {
                if (Util.isOnline(getApplicationContext())) {
                    new uploadFile(path).execute();
                } else {
                    toast(getResources().getString(R.string.network_error));
                }
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.image_size_exceeds_limit), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class uploadFile extends AsyncTask<Void, String, String> {

        String filePath;

        private uploadFile(String filePath) {
            this.filePath = filePath;
            if (progressDialog != null)
                progressDialog.show();

        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            String resp = "";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = null;
                HttpPost poster = new HttpPost(
                        Constant.URL1 + "uploadAttachment");

                FileBody fbody = null;
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                if (filePath.length() > 0) {
                    File image = new File(filePath);
                    fbody = new FileBody(image);
                    entity.addPart("File", fbody);
                }

                entity.addPart("CompanyID", new StringBody(read(Constant.SHRED_PR.KEY_COMPANY_ID)));
                entity.addPart("UserID", new StringBody(read(Constant.SHRED_PR.KEY_USERID)));
                entity.addPart("TokenID", new StringBody(read(Constant.SHRED_PR.KEY_TOKEN)));

                poster.setEntity(entity);

                response = client.execute(poster);
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity()
                                .getContent()));
                String line = null;
                while ((line = rd.readLine()) != null) {
                    resp += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("Resp Upload 222", "" + resp);
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("result", ">>" + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.optString("status");
                if (status.equals("Success")) {
                    String data = jsonObject.optString("data");
                    //set data in textview text
                    if (data != null && data.length() > 0) {
                        if (textViewAttachment != null) {
                            textViewAttachment.setText(data + "");
                            relativeAttachment.setVisibility(View.VISIBLE);
                        }
                    }

                    textViewAttachment = null;
                    Log.i("Upload Expenses", "Path :" + data);
                } else {
                    toast("" + jsonObject.optString("message"));
                }
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
