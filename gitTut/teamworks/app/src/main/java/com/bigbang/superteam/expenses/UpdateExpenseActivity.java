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
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UpdateExpenseActivity extends BaseActivity {

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
    @InjectView(R.id.rl_start_time_)
    RelativeLayout rl_start_time_;
    @InjectView(R.id.rl_end_time_)
    RelativeLayout rl_end_time_;
    @InjectView(R.id.rlAddExpHead)
    RelativeLayout rlAddExpHead;

    @InjectView(R.id.rlApprovedBy)
    RelativeLayout rlApprovedBy;
    @InjectView(R.id.tvApprovedBy)
    TextView tvApprovedBy;
    @InjectView(R.id.tv_approved_by)
    TextView tv_approved_by;

    @InjectView(R.id.tvApproved)
    TextView tvApproved;
    @InjectView(R.id.tvReject)
    TextView tvReject;
    @InjectView(R.id.rl_next_back)
    LinearLayout rl_next_back;


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
    double totalAppAmt = 0.0;

    //Upload Attachment
    public static final int BROWSE_ACTIVITY = 101;
    private static final int SELECT_FILE = 109;
    String camera_pathname = "";
    TextView textViewAttachment;
    RelativeLayout relativeAttachment;

    ExpenseType type = null;

    String roleId;
    boolean isMember;

    boolean isDisable = false;
    boolean isDisableApprover = false;
    boolean isDisableRequester = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_expense);

        Bundle b = getIntent().getExtras();
        type = (ExpenseType) b.get("expense");
        isMember = b.getBoolean("isMember");

        roleId = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_ROLE_ID);

        init();
    }

    private void init() {
        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
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

        if (roleId.equalsIgnoreCase("1")) { //Super Admin ------- Others
            if (type.status.equalsIgnoreCase("Pending")) {
                //Button data
                tvApproved.setText(getResources().getString(R.string.accept));
                tvReject.setText(getResources().getString(R.string.reject));

                //Approval requester Data
                isDisableRequester = true;
                disableRequesterData();
            } else {
                //Buttton Data
                rl_next_back.setVisibility(View.GONE);

                //Approval requester Data
                isDisable = true;
                disableAllData();
            }
        } else if (roleId.equalsIgnoreCase("2")) { //Admin
            if (isMember) { //Others
                if (type.status.equalsIgnoreCase("Pending")) {
                    //Button Data
                    tvApproved.setText(getResources().getString(R.string.accept));
                    tvReject.setText(getResources().getString(R.string.reject));

                    //Approval requester Data
                    isDisableRequester = true;
                    disableRequesterData();
                } else {
                    //Button Data
                    rl_next_back.setVisibility(View.GONE);

                    //Approval requester Data
                    isDisable = true;
                    disableAllData();
                }
            } else { //Own
                if (type.status.equalsIgnoreCase("Pending")) {
                    //Button Data
                    tvApproved.setText(getResources().getString(R.string.update));
                    tvReject.setText(getResources().getString(R.string.withraw));

                    //Approval requester Data
                    isDisableApprover = true;
                    disableApprovalData();
                } else {
                    //Button Data
                    rl_next_back.setVisibility(View.GONE);

                    //Approval requester Data
                    isDisable = true;
                    disableAllData();
                }
            }
        } else { //Manager & Team Member ----- Own
            if (type.status.equalsIgnoreCase("Pending")) {
                //Button Data
                tvApproved.setText(getResources().getString(R.string.update));
                tvReject.setText(getResources().getString(R.string.withraw));

                //Approval requester Data
                isDisableApprover = true;
                disableApprovalData();
            } else {
                //Button Data
                rl_next_back.setVisibility(View.GONE);

                //Approval requester Data
                isDisable = true;
                disableAllData();
            }
        }

        if (type.status.equalsIgnoreCase("Pending")) {
            rlApprovedBy.setVisibility(View.GONE);
        } else if (type.status.equalsIgnoreCase("Accept")) {
            rlApprovedBy.setVisibility(View.VISIBLE);
            tv_approved_by.setText(getResources().getString(R.string.approved_by));
        } else if (type.status.equalsIgnoreCase("Reject")) {
            rlApprovedBy.setVisibility(View.VISIBLE);
            tv_approved_by.setText(getResources().getString(R.string.rejected_by));
        } else if (type.status.equalsIgnoreCase("Withdraw")) {
            rlApprovedBy.setVisibility(View.GONE);
        }

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
                            fillExpenseData();
                        } else {
                            Toast.makeText(UpdateExpenseActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void fillExpenseData() {

        tv_start_time.setText(dateFormat(type.startDate));
        tv_end_time.setText(dateFormat(type.endDate));

        //set start Calendat 2016\/08\/03 00:00:00
        String startDateArray[] = type.startDate.split("/");
        if (startDateArray != null && startDateArray.length > 0) {
            startCalendar.set(Calendar.YEAR, Integer.parseInt(startDateArray[0]));
            startCalendar.set(Calendar.MONTH, Integer.parseInt(startDateArray[1]) - 1);
            startCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(startDateArray[2].substring(0, 2)));
        }

        //Ser end calendar
        String endDateArray[] = type.endDate.split("/");
        if (endDateArray != null && endDateArray.length > 0) {
            endCalendar.set(Calendar.YEAR, Integer.parseInt(endDateArray[0]));
            endCalendar.set(Calendar.MONTH, Integer.parseInt(endDateArray[1]) - 1);
            endCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endDateArray[2].substring(0, 2)));
        }

        for (int i = 0; i < type.expenseDetailsList.size(); i++) {
            addExpenseDetailView(type.expenseDetailsList.get(i), i);
        }

        etTotalReqAmt.setText(String.format("%.2f", type.totalExpenseRequested));
        etTotalAppAmt.setText(String.format("%.2f", type.totalExpenseApproved));

        etComments.setText(type.requesterComment);
        etApprovalsComments.setText(type.approverComment);

        tvApprovedBy.setText(type.approvedBy.getFirstName() + " " + type.approvedBy.getLastName());
    }

    private void addExpenseDetailView(ExpenseDetailList list, int position) {
        final View child = LayoutInflater.from(this).inflate(R.layout.layout_expense_head, null);

        final Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) child;
        Util.setAppFont(mContainer, mFont);

        Spinner spnExpenseType = (Spinner) child.findViewById(R.id.spnExpenseType);
        final EditText etRequestedAmt = (EditText) child.findViewById(R.id.etRequestedAmt);
        final EditText etApprovedAmt = (EditText) child.findViewById(R.id.etApprovedAmt);
        EditText etBillNo = (EditText) child.findViewById(R.id.etBillNo);

        View viewSeparator = (View) child.findViewById(R.id.viewSeparator);
        ImageView imgDeleteExpense = (ImageView) child.findViewById(R.id.imgDeleteExpense);

        final RelativeLayout rlAttachment = (RelativeLayout) child.findViewById(R.id.rlAttachment);
        final ImageView imgDeleteAttachment = (ImageView) child.findViewById(R.id.imgDeleteAttachment);
        final TextView tv_attachment = (TextView) child.findViewById(R.id.tv_attachment);
        RelativeLayout rlAddAttachment = (RelativeLayout) child.findViewById(R.id.rlAddAttachment);

        if (expenseType != null && expenseType.length > 0) {
            ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, expenseType);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnExpenseType.setAdapter(aa);
        }

        //etRequestedAmt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});
        //etApprovedAmt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});

        if (position == 0) {
            viewSeparator.setVisibility(View.GONE);
            imgDeleteExpense.setVisibility(View.GONE);
        }

        if (list.billAttachmentPath.length() <= 0)
            rlAttachment.setVisibility(View.GONE);

        for (int j = 0; j < expenseType.length; j++) {
            if (expenseType[j].equalsIgnoreCase(list.expenseType)) {
                spnExpenseType.setSelection(j);
            }
        }

        etRequestedAmt.setTag(type.expenseDetailsList.get(position).id); //For id --- required in update

        etRequestedAmt.setText(String.format("%.2f", list.amountRequested));
        etApprovedAmt.setText(String.format("%.2f", list.amountApproved));
        etBillNo.setText(list.billNo);
        tv_attachment.setText(list.billAttachmentPath);

        imgDeleteAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlAttachment.setVisibility(View.GONE);
                tv_attachment.setText("");
            }
        });

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

        //___________________ To make to requested amount total ______________________
        final BaseInputConnection tfInputReqAmt = new BaseInputConnection(etRequestedAmt, true);

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

        //___________________ To make to requested amount total ______________________
        final BaseInputConnection tAInputReqAmt = new BaseInputConnection(etApprovedAmt, true);

        etApprovedAmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                totalAppAmt = 0.0;
                double expense = 0.0;

                if (etApprovedAmt.getText().toString().length() == 1 && etApprovedAmt.getText().toString().equalsIgnoreCase(".")) {
                    etApprovedAmt.setText("0.");
                    etApprovedAmt.setSelection(etApprovedAmt.getText().length());
                    etApprovedAmt.setError(null);
                } else if (etApprovedAmt.getText().toString().length() > 0) {
                    double limit = Double.parseDouble(etApprovedAmt.getText().toString());
                    if (limit > 999999.99) {
                        tAInputReqAmt.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                        toast("You can add maximum approved amount 999999.99");
                    }

                    etApprovedAmt.setError(null);
                }

                int count = layoutExpensesHead.getChildCount();
                for (int j = 0; j < count; j++) {
                    View childView = layoutExpensesHead.getChildAt(j);
                    final EditText eTxtReqExpense = (EditText) childView.findViewById(R.id.etApprovedAmt);
                    if (eTxtReqExpense.getText().length() > 0) {
                        expense = Double.parseDouble(eTxtReqExpense.getText().toString());
                        totalAppAmt += expense;
                    }
                }
                etTotalAppAmt.setText(String.format("%.2f", totalAppAmt));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (isDisable) {

            spnExpenseType.setEnabled(false);

            etRequestedAmt.setEnabled(false);
            etRequestedAmt.setTextColor(getResources().getColor(R.color.gray));

            etApprovedAmt.setEnabled(false);
            etApprovedAmt.setTextColor(getResources().getColor(R.color.gray));

            etBillNo.setEnabled(false);
            etBillNo.setTextColor(getResources().getColor(R.color.gray));

            rlAddAttachment.setClickable(false);

            rlAttachment.setClickable(false);

            imgDeleteExpense.setClickable(false);

            imgDeleteAttachment.setClickable(false);
        }

        if (isDisableRequester) {
            spnExpenseType.setEnabled(false);

            etRequestedAmt.setEnabled(false);
            etRequestedAmt.setTextColor(getResources().getColor(R.color.gray));

            etBillNo.setEnabled(false);
            etBillNo.setTextColor(getResources().getColor(R.color.gray));

            rlAddAttachment.setClickable(false);

            rlAttachment.setClickable(false);

            imgDeleteExpense.setClickable(false);

            imgDeleteAttachment.setClickable(false);
        }

        if (isDisableApprover) {
            etApprovedAmt.setEnabled(false);
            etApprovedAmt.setTextColor(getResources().getColor(R.color.gray));
        }

        layoutExpensesHead.addView(child);
    }

    public static String dateFormat(String dtStart) {

        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = format.parse(dtStart);
            SimpleDateFormat sdf = new SimpleDateFormat(" d" + " MMM" + "," + " yyyy");
            newDate = sdf.format(convertedDate);
            return newDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    private void disableAllData() {
        rl_start_time_.setClickable(false);
        rl_end_time_.setClickable(false);
        tv_start_time.setTextColor(getResources().getColor(R.color.gray));
        tv_end_time.setTextColor(getResources().getColor(R.color.gray));

        rlAddExpHead.setClickable(false);

        etTotalReqAmt.setEnabled(false);
        etTotalReqAmt.setFocusable(false);
        etTotalReqAmt.setTextColor(getResources().getColor(R.color.gray));

        etTotalAppAmt.setEnabled(false);
        etTotalAppAmt.setFocusable(false);
        etTotalAppAmt.setTextColor(getResources().getColor(R.color.gray));

        etComments.setEnabled(false);
        etComments.setFocusable(false);
        etComments.setTextColor(getResources().getColor(R.color.gray));

        etApprovalsComments.setEnabled(false);
        etApprovalsComments.setFocusable(false);
        etApprovalsComments.setTextColor(getResources().getColor(R.color.gray));
    }

    private void disableRequesterData() {
        rl_start_time_.setClickable(false);
        rl_end_time_.setClickable(false);
        tv_start_time.setTextColor(getResources().getColor(R.color.gray));
        tv_end_time.setTextColor(getResources().getColor(R.color.gray));

        rlAddExpHead.setClickable(false);

        etTotalReqAmt.setEnabled(false);
        etTotalReqAmt.setFocusable(false);
        etTotalReqAmt.setTextColor(getResources().getColor(R.color.gray));

        etComments.setEnabled(false);
        etComments.setFocusable(false);
        etComments.setTextColor(getResources().getColor(R.color.gray));
    }

    private void disableApprovalData() {
        etTotalAppAmt.setEnabled(false);
        etTotalAppAmt.setFocusable(false);
        etTotalAppAmt.setTextColor(getResources().getColor(R.color.gray));

        etApprovalsComments.setEnabled(false);
        etApprovalsComments.setFocusable(false);
        etApprovalsComments.setTextColor(getResources().getColor(R.color.gray));
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
        addExpenseHead();
    }

    @OnClick(R.id.rl_start_time_)
    void selectStartDate() {
        showDialog(START_DATE);
    }

    @OnClick(R.id.rl_end_time_)
    void selectEndDate() {
        showDialog(END_DATE);
    }

    @OnClick(R.id.rl_approved)
    void approvedUpdate() {
        if (tvApproved.getText().toString().equalsIgnoreCase(getResources().getString(R.string.accept))) {
            //Approved api call
            approveExpense();
        } else {
            //update api call
            updateExpense();
        }
    }

    @OnClick(R.id.rl_reject)
    void rejectWithdraw() {
        if (tvReject.getText().toString().equalsIgnoreCase(getResources().getString(R.string.reject))) {
            //Reject api call
            rejectExpense();
        } else {
            //Withdraw api call
            withdrawExpense();
        }
    }

    private void withdrawExpense() {
        if (Util.isOnline(this)) {

            if (progressDialog != null)
                progressDialog.show();

            RestClient.getCommonService().withdrawExpense(Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERID),
                    type.expenseId + "",
                    Constant.AppName,
                    Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_TOKEN),
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {
                                Map<String, String> map = Util.readStatus(response);
                                boolean isSuccess = map.get("status").equals("Success");
                                String json = Util.getString(response.getBody().in());
                                JSONObject jObj = new JSONObject(json);

                                String status = jObj.optString("status");
                                if (status.equals(Constant.InvalidToken)) {
                                    TeamWorkApplication.LogOutClear(UpdateExpenseActivity.this);
                                    return;
                                }

                                if (isSuccess) {
                                    toast(jObj.getString("message"));

                                    finish();
                                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);

                                } else {
                                    toast(jObj.getString("message"));
                                }

                                if (progressDialog != null)
                                    if (progressDialog.isShowing()) progressDialog.dismiss();

                            } catch (Exception e) {
                                e.printStackTrace();
                                if (progressDialog != null)
                                    if (progressDialog.isShowing()) progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (progressDialog != null)
                                if (progressDialog.isShowing()) progressDialog.dismiss();
                        }
                    });
        } else {
            toast(Constant.network_error);
        }
    }

    private void updateExpense() {
        if (Util.isOnline(this)) {
            if (isValidateUpdate()) {

                if (progressDialog != null)
                    progressDialog.show();

                JSONObject jsonObject = new JSONObject();
                try {
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
                    String startTime = sdf2.format(startCalendar.getTime());
                    String endTime = sdf2.format(endCalendar.getTime());

                    jsonObject.put("expenseId", type.expenseId);
                    jsonObject.put("startDate", startTime + " 00:00:00");
                    jsonObject.put("endDate", endTime + " 00:00:00");
                    jsonObject.put("totalExpenseRequested", etTotalReqAmt.getText().toString());
                    jsonObject.put("totalExpenseApproved", etTotalAppAmt.getText().toString());
                    jsonObject.put("requesterComment", etComments.getText().toString());
                    jsonObject.put("approverComment", etApprovalsComments.getText().toString());

                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < layoutExpensesHead.getChildCount(); i++) {

                        View child = layoutExpensesHead.getChildAt(i);

                        JSONObject innerObject = new JSONObject();

                        innerObject.put("id", child.findViewById(R.id.etRequestedAmt).getTag());
                        innerObject.put("expenseType", ((Spinner) child.findViewById(R.id.spnExpenseType)).getSelectedItem());
                        innerObject.put("amountRequested", ((EditText) child.findViewById(R.id.etRequestedAmt)).getText().toString());
                        innerObject.put("amountApproved", ((EditText) child.findViewById(R.id.etApprovedAmt)).getText().toString());
                        innerObject.put("billNo", ((EditText) child.findViewById(R.id.etBillNo)).getText().toString());
                        innerObject.put("billAttachmentPath", ((TextView) child.findViewById(R.id.tv_attachment)).getText().toString());

                        jsonArray.put(innerObject);
                    }

                    if (jsonArray != null)
                        jsonObject.put("expenseDetailsList", jsonArray);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                RestClient.getCommonService().updateExpense(jsonObject.toString(),
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
                                        TeamWorkApplication.LogOutClear(UpdateExpenseActivity.this);
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

    private void approveExpense() {
        if (Util.isOnline(this)) {
            if (isValidateApprover()) {

                if (progressDialog != null)
                    progressDialog.show();

                JSONObject jsonObject = new JSONObject();
                try {
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
                    String startTime = sdf2.format(startCalendar.getTime());
                    String endTime = sdf2.format(endCalendar.getTime());

                    jsonObject.put("expenseId", type.expenseId);
                    jsonObject.put("startDate", startTime + " 00:00:00");
                    jsonObject.put("endDate", endTime + " 00:00:00");
                    jsonObject.put("totalExpenseRequested", etTotalReqAmt.getText().toString());
                    jsonObject.put("totalExpenseApproved", etTotalAppAmt.getText().toString());
                    jsonObject.put("requesterComment", etComments.getText().toString());
                    jsonObject.put("approverComment", etApprovalsComments.getText().toString());

                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < layoutExpensesHead.getChildCount(); i++) {

                        View child = layoutExpensesHead.getChildAt(i);

                        JSONObject innerObject = new JSONObject();

                        innerObject.put("id", child.findViewById(R.id.etRequestedAmt).getTag());
                        innerObject.put("expenseType", ((Spinner) child.findViewById(R.id.spnExpenseType)).getSelectedItem());
                        innerObject.put("amountRequested", ((EditText) child.findViewById(R.id.etRequestedAmt)).getText().toString());
                        innerObject.put("amountApproved", ((EditText) child.findViewById(R.id.etApprovedAmt)).getText().toString());
                        innerObject.put("billNo", ((EditText) child.findViewById(R.id.etBillNo)).getText().toString());
                        innerObject.put("billAttachmentPath", ((TextView) child.findViewById(R.id.tv_attachment)).getText().toString());

                        jsonArray.put(innerObject);
                    }

                    if (jsonArray != null)
                        jsonObject.put("expenseDetailsList", jsonArray);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                RestClient.getCommonService().approveRejectExpense(jsonObject.toString(),
                        Constant.AppName,
                        null,
                        Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERID),
                        Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_TOKEN),
                        "true",
                        new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                try {
                                    Map<String, String> map = Util.readStatus(response);
                                    boolean isSuccess = map.get("status").equals("Success");
                                    String json = Util.getString(response.getBody().in());
                                    JSONObject jObj = new JSONObject(json);

                                    String status = jObj.optString("status");
                                    if (status.equals(Constant.InvalidToken)) {
                                        TeamWorkApplication.LogOutClear(UpdateExpenseActivity.this);
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

    private void rejectExpense() {
        if (Util.isOnline(this)) {

            if (progressDialog != null)
                progressDialog.show();

            JSONObject jsonObject = new JSONObject();
            try {
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
                String startTime = sdf2.format(startCalendar.getTime());
                String endTime = sdf2.format(endCalendar.getTime());

                jsonObject.put("expenseId", type.expenseId);
                jsonObject.put("startDate", startTime + " 00:00:00");
                jsonObject.put("endDate", endTime + " 00:00:00");
                jsonObject.put("totalExpenseRequested", etTotalReqAmt.getText().toString());
                jsonObject.put("totalExpenseApproved", etTotalAppAmt.getText().toString());
                jsonObject.put("requesterComment", etComments.getText().toString());
                jsonObject.put("approverComment", etApprovalsComments.getText().toString());

                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < layoutExpensesHead.getChildCount(); i++) {

                    View child = layoutExpensesHead.getChildAt(i);

                    JSONObject innerObject = new JSONObject();

                    innerObject.put("id", child.findViewById(R.id.etRequestedAmt).getTag());
                    innerObject.put("expenseType", ((Spinner) child.findViewById(R.id.spnExpenseType)).getSelectedItem());
                    innerObject.put("amountRequested", ((EditText) child.findViewById(R.id.etRequestedAmt)).getText().toString());
                    if (((EditText) child.findViewById(R.id.etApprovedAmt)).getText().toString().length() > 0)
                        innerObject.put("amountApproved", ((EditText) child.findViewById(R.id.etApprovedAmt)).getText().toString());
                    else
                        innerObject.put("amountApproved", "0");
                    innerObject.put("billNo", ((EditText) child.findViewById(R.id.etBillNo)).getText().toString());
                    innerObject.put("billAttachmentPath", ((TextView) child.findViewById(R.id.tv_attachment)).getText().toString());

                    jsonArray.put(innerObject);
                }

                if (jsonArray != null)
                    jsonObject.put("expenseDetailsList", jsonArray);

            } catch (Exception e) {
                e.printStackTrace();
            }

            RestClient.getCommonService().approveRejectExpense(jsonObject.toString(),
                    Constant.AppName,
                    null,
                    Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERID),
                    Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_TOKEN),
                    "false",
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {
                                Map<String, String> map = Util.readStatus(response);
                                boolean isSuccess = map.get("status").equals("Success");
                                String json = Util.getString(response.getBody().in());
                                JSONObject jObj = new JSONObject(json);

                                String status = jObj.optString("status");
                                if (status.equals(Constant.InvalidToken)) {
                                    TeamWorkApplication.LogOutClear(UpdateExpenseActivity.this);
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

        } else {
            toast(Constant.network_error);
        }
    }

    private boolean isValidateApprover() {

        if (layoutExpensesHead.getChildCount() == 0) {
            toast("Atleast one header required");
            return false;
        }

        for (int i = 0; i < layoutExpensesHead.getChildCount(); i++) {
            View child = layoutExpensesHead.getChildAt(i);
            if (((EditText) child.findViewById(R.id.etApprovedAmt)).getText().toString().length() == 0) {
                toast("Please enter approved amount");
                return false;
            }
        }

        /*for (int i = 0; i < layoutExpensesHead.getChildCount(); i++) {
            View child = layoutExpensesHead.getChildAt(i);
            if (Double.parseDouble(((EditText) child.findViewById(R.id.etApprovedAmt)).getText().toString()) <= 0) {
                toast("Approved amount must be greater than 0");
                return false;
            }
        }*/

        for (int i = 0; i < layoutExpensesHead.getChildCount(); i++) {
            View child = layoutExpensesHead.getChildAt(i);
            if ((Double.parseDouble(((EditText) child.findViewById(R.id.etApprovedAmt)).getText().toString()))
                    > (Double.parseDouble(((EditText) child.findViewById(R.id.etRequestedAmt)).getText().toString()))) {
                toast("Approved amount must be less than or equal to requested amount");
                return false;
            }
        }

        return true;
    }

    private boolean isValidateUpdate() {

        if (startCalendar.after(endCalendar)) {
            toast("End date is greater than or equal to start date");
            return false;
        }

        if (layoutExpensesHead.getChildCount() == 0) {
            toast("Please add atleast one expense");
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


    private void addExpenseHead() {

        final View child = LayoutInflater.from(this).inflate(R.layout.layout_expense_head, null);

        final Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) child;
        Util.setAppFont(mContainer, mFont);

        RelativeLayout rlAddAttachment = (RelativeLayout) child.findViewById(R.id.rlAddAttachment);
        RelativeLayout rlChildApprovals = (RelativeLayout) child.findViewById(R.id.rlChildApprovals);
        Spinner spnExpenseType = (Spinner) child.findViewById(R.id.spnExpenseType);
        final EditText etRequestedAmt = (EditText) child.findViewById(R.id.etRequestedAmt);
        final TextView tv_attachment = (TextView) child.findViewById(R.id.tv_attachment);
        final ImageView imgDeleteAttachment = (ImageView) child.findViewById(R.id.imgDeleteAttachment);
        final RelativeLayout rlAttachment = (RelativeLayout) child.findViewById(R.id.rlAttachment);
        ImageView imgDeleteExpense = (ImageView) child.findViewById(R.id.imgDeleteExpense);
        EditText etApprovedAmt = (EditText) child.findViewById(R.id.etApprovedAmt);

        etRequestedAmt.requestFocus();

        //etRequestedAmt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});
        //etApprovedAmt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});

        rlAttachment.setVisibility(View.GONE);

        etApprovedAmt.setText("0.00");
        etApprovedAmt.setEnabled(false);
        etApprovedAmt.setTextColor(getResources().getColor(R.color.gray));

        etRequestedAmt.setTag(0); //For id --- required in update

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

        //___________________ To make to requested amount total ______________________
        final BaseInputConnection tfInputReqAmt = new BaseInputConnection(etRequestedAmt, true);

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
