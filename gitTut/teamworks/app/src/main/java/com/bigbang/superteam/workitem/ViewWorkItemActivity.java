package com.bigbang.superteam.workitem;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.DisplayFullscreenImage;
import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.Attachment;
import com.bigbang.superteam.dataObjs.Project;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 15/05/2015.
 */
public class ViewWorkItemActivity extends BaseActivity {


    protected ImageLoader imageLoader;
    @InjectView(R.id.tv_itemTitle)
    TextView tvTitle;
    @InjectView(R.id.tv_headerTitle)
    TextView tvHeaderTitle;
    @InjectView(R.id.tv_itemDescription)
    TextView tvDescription;
    @InjectView(R.id.tv_itemBudget)
    TextView tvBudget;
    @InjectView(R.id.tv_itemPriority)
    TextView tvPriority;
    @InjectView(R.id.tv_itemLocation)
    TextView tvLocation;
    @InjectView(R.id.tv_itemStarttime)
    TextView tvStartTime;
    @InjectView(R.id.tv_itemEndTime)
    TextView tvEndTime;
    @InjectView(R.id.tv_itemActualTime)
    TextView tvActualTime;
    @InjectView(R.id.tv_itemAssignedto)
    TextView tvAssignTo;
    @InjectView(R.id.tv_ccto)
    TextView tvCCTo;
    @InjectView(R.id.tv_itemTypeofWork)
    TextView tvworkType;
    @InjectView(R.id.tv_createdby)
    TextView tvCreatedBy;
    @InjectView(R.id.ll_optional)
    LinearLayout optionalLayout;
    @InjectView(R.id.ll_raw11)
    LinearLayout raw1;
    @InjectView(R.id.ll_raw12)
    LinearLayout raw2;
    @InjectView(R.id.ll_raw13)
    LinearLayout raw3;
    @InjectView(R.id.ll_raw14)
    LinearLayout raw4;
    @InjectView(R.id.ll_raw15)
    LinearLayout raw5;
    @InjectView(R.id.ll_raw16)
    LinearLayout raw6;
    @InjectView(R.id.ll_raw17)
    LinearLayout raw7;
    @InjectView(R.id.ll_master)
    LinearLayout master;
    @InjectView(R.id.fl_parent)
    RelativeLayout frameTitle;
    @InjectView(R.id.tv_option1)
    TextView op1;
    @InjectView(R.id.tv_option11)
    TextView op11;
    @InjectView(R.id.tv_option2)
    TextView op2;
    @InjectView(R.id.tv_option22)
    TextView op22;
    @InjectView(R.id.tv_option3)
    TextView op3;
    @InjectView(R.id.tv_option33)
    TextView op33;
    @InjectView(R.id.tv_option4)
    TextView op4;
    @InjectView(R.id.tv_option44)
    TextView op44;
    @InjectView(R.id.tv_option5)
    TextView op5;
    @InjectView(R.id.tv_option55)
    TextView op55;
    @InjectView(R.id.tv_option6)
    TextView op6;
    @InjectView(R.id.tv_option66)
    TextView op66;
    @InjectView(R.id.tv_option7)
    TextView op7;
    @InjectView(R.id.tv_option77)
    TextView op77;
    @InjectView(R.id.ll_attachmentLayout)
    LinearLayout attachmentLayout;
    @InjectView(R.id.iv_taskimage)
    ImageView taskImageView;
    @InjectView(R.id.btnExpDetails)
    Button btnExpense;

    Context mContext;
    WorkItem workItem;
    int taskCode = 0;
    DisplayImageOptions options;
    ArrayList<Attachment> attachmentsData;
    ArrayList<View> viewList;
    SQLiteDatabase db;

    int downloadCounter = 0, exitCounter = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workitem);
        ButterKnife.inject(this);
        Init();
        taskCode = getIntent().getIntExtra(WorkItem.TASK_ID, 0);
        imageLoader = imageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        InitializeWorkItem();
        SetValuesAndViews();
    }


    @Override
    public void onBackPressed() {
        if (downloadCounter == 0) {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
        } else {
            Toast.makeText(getApplicationContext(), "Download will be stopped. Press back again to exit", Toast.LENGTH_SHORT).show();
            if (exitCounter == 1) {
                super.onBackPressed();
                finish();
                overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
            } else {
                exitCounter++;
            }
        }
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        if (downloadCounter == 0) {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
        } else {
            Toast.makeText(getApplicationContext(), "Download will be stopped. Press back again to exit", Toast.LENGTH_SHORT).show();
            if (exitCounter == 1) {
                super.onBackPressed();
                finish();
                overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
            } else {
                exitCounter++;
            }
        }
    }

    private void InitializeAttachments(String attachmentlist) {
        try{

            if(attachmentlist.length()>0){
                if(attachmentlist.endsWith(",")) attachmentlist=attachmentlist.substring(0,attachmentlist.length()-1);
            }

            Cursor crsr = db.rawQuery("select * from " + Constant.AttachmentTable + " where " + Attachment.ATTACHMENT_ID + " IN (" + attachmentlist + ")", null);
            //Cursor crsr = Util.getDb(mContext).rawQuery("select * from " + Constant.AttachmentTable + " where " + Attachment.WORKITEM_ID + " = " + taskCode, null);
            attachmentsData = new ArrayList<>();
            viewList = new ArrayList<>();
            Log.e("Attachment:", "Count=" + crsr.getCount());
            if (crsr != null && crsr.getCount() > 0) {
                crsr.moveToFirst();
                do {
                    Attachment attachment = new Attachment();
                    attachment.setAttachmentId(crsr.getString(crsr.getColumnIndex(Attachment.ATTACHMENT_ID)));
                    attachment.setUserId(crsr.getString(crsr.getColumnIndex(Attachment.USER_ID)));
                    attachment.setWorkItemId(crsr.getString(crsr.getColumnIndex(Attachment.WORKITEM_ID)));
                    attachment.setPath(crsr.getString(crsr.getColumnIndex(Attachment.PATH)));
                    attachment.setUploadedDownloaded(crsr.getString(crsr.getColumnIndex(Attachment.UPLOADEDDOWNLOADED)));
                    if (attachment.getPath() != null && attachment.getPath().length() > 4) {
                        attachmentsData.add(attachment);
                        AddAttachment(attachment);
                    }
                } while (crsr.moveToNext());
                crsr.close();
            }
        }catch (Exception e){e.printStackTrace();}
    }

    private void InitializeWorkItem() {
        workItem = new WorkItem();
        Cursor crsr = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + taskCode, null);
        if (crsr != null) {
            crsr.moveToFirst();
            workItem = WorkItem.getWorkItemfromCursor(crsr, crsr.getPosition());
            crsr.close();
        }
    }

    private void Init() {

        mContext = this;

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        taskImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ViewWorkItemActivity.this, DisplayFullscreenImage.class);
                if (workItem.getTaskImage() != null && workItem.getTaskImage().length() > 4) {
                    myIntent.putExtra("URLURI", workItem.getTaskImage());
                    startActivity(myIntent);
                }
            }
        });
        frameTitle.setVisibility(View.VISIBLE);
    }

    private void SetValuesAndViews() {

        tvHeaderTitle.setText("" + workItem.getTitle());
        tvTitle.setText("" + workItem.getTitle());
        tvDescription.setText("" + workItem.getDescription());
        tvBudget.setText("" + workItem.getBudget());
        tvPriority.setText("" + workItem.getPriority());
        tvLocation.setText("" + workItem.getWorkLocation());
        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(Util
                    .sdf.parse(Util.utcToLocalTime(workItem.getStartDate())));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getEndDate())));
            String sdate = Util.SDF.format(startCalendar.getTime());
            tvStartTime.setText("" + sdate);
            sdate = Util.SDF.format(endCalendar.getTime());
            tvEndTime.setText("" + sdate);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        String str = GetEstimatedWorkTime(workItem.getEstimatedWorkTime());
        tvActualTime.setText("" + str);
        String userNames = Util.getUsernames(workItem.getTaskAssignedTo(),mContext);
        tvAssignTo.setText("" + userNames);
        userNames = "";
        userNames = Util.getUsernames(workItem.getTaskCCTo(),mContext);
        tvCCTo.setText("" + userNames);

        tvworkType.setText("" + workItem.getTaskType());
        final String workType = workItem.getTaskType();
        if (workType.equals("Project")) //For Project
            setViewProject();
        else if (workType.equals("Regular"))
            setViewRegular();
        else if (workType.equals("One Time"))
            setViewOneTime();
        else if (workType.equals("Service Call"))
            setViewServiceAndSalesCall();
        else if (workType.equals("Sales Call"))
            setViewServiceAndSalesCall();
        else if (workType.equals("Shopping/Purchase"))
            setViewShippingAndPurchase();
        else if (workType.equals("Collection"))
            setViewCollection();

        tvCreatedBy.setText(Util.getUsernames(workItem.getUserCode() + "",mContext));
        if (workItem.getTaskImage() != null && workItem.getTaskImage().length() > 4) {
            imageLoader.displayImage(workItem.getTaskImage(), taskImageView);
        } else {
            int counter = 0;
            char c = workItem.getTitle().toUpperCase().charAt(counter);
            while (c == ' ') {
                counter++;
                c = workItem.getTitle().charAt(counter);
            }
            if (c >= 'A' && c <= 'Z') {
                taskImageView.setImageResource(Util.getimgSrc(c));
            }
        }
        btnExpense.setVisibility(View.GONE);

        Log.e("attachments", ">>" + workItem.getAttachments());
        InitializeAttachments("" + workItem.getAttachments());
    }

    private String GetEstimatedWorkTime(String estimatedWorkTime) {
        String str[] = estimatedWorkTime.split(",");
        if (str.length > 0) {
            return str[0] + ":Hours " + str[1] + ":Minutes";
        }
        return "";
    }

    private void setViewOneTime() {
        optionalLayout.setVisibility(View.GONE);
    }

    private void setViewRegular() {
        optionalLayout.setVisibility(View.VISIBLE);
        op1.setText(getString(R.string.frequency));
        op11.setText("" + workItem.getFrequency());
        op2.setText(getString(R.string.days));
        if (workItem.getFrequency().equals("Weekly")) {
            op22.setText("" + getDaysString(workItem.getDayCodesSelected()));
        } else if (workItem.getFrequency().equals("Monthly")) {
            op22.setText("" + workItem.getDayCodesSelected());
        } else {
            raw2.setVisibility(View.GONE);
        }
        raw3.setVisibility(View.GONE);
        raw4.setVisibility(View.GONE);
        raw5.setVisibility(View.GONE);
        raw6.setVisibility(View.GONE);
        raw7.setVisibility(View.GONE);

        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getStartDate())));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getEndDate())));
            String sdate = Util.SDF.format(startCalendar.getTime());
            tvStartTime.setText("" + sdate.substring(11));
            sdate = Util.SDF.format(endCalendar.getTime());
            tvEndTime.setText("" + sdate.substring(11));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getDaysString(String dayCodesSelected) {
        if (dayCodesSelected != null && dayCodesSelected.length() > 0) {
            String[] days = dayCodesSelected.split(",");
            if (days.length > 0) {
                String str = "";
                for (int i = 0; i < days.length; i++) {
                    int code = Integer.parseInt(days[i]);
                    if(str.length()==0) str="" + getResources().getStringArray(R.array.week_days)[code];
                    else str = str + "," + " " + getResources().getStringArray(R.array.week_days)[code];
                }
                return str;
            }
        }
        return "";
    }

    private void setViewServiceAndSalesCall() {
        optionalLayout.setVisibility(View.VISIBLE);
        op1.setText(getString(R.string.customer_name));
        if (isNum(workItem.getCustomerName())) {
            op11.setText("" + getCustomername(workItem.getCustomerName()));
        } else {
            op11.setText("" + workItem.getCustomerName());
        }
        op2.setText(getString(R.string.customer_contact));
        op22.setText("" + workItem.getCustomerContact());
        op3.setText(getString(R.string.customer_type));
        op33.setText("" + workItem.getCustomerType());
        if (workItem.getCustomerType().equals(Constant.REPEAT)) {
            op33.setText("" + getString(R.string.repeat_call));
        } else {
            op33.setText("" + getString(R.string.new_call));
        }
        op4.setText(getString(R.string.past_history));
        op44.setText("" + workItem.getPastHistory());
        raw5.setVisibility(View.GONE);
        raw6.setVisibility(View.GONE);
        raw7.setVisibility(View.GONE);
    }

    private String getCustomername(String customerName) {
        Cursor cursor = db.rawQuery("select * from " + Constant.tableCustomers + " where Id like " + customerName, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                Log.e("Name Founded Customer", " ::" + cursor.getString(cursor.getColumnIndex("Name")));
                return cursor.getString(cursor.getColumnIndex("Name"));
            }
            cursor.close();
        }
        return customerName;
    }

    private boolean isNum(String customerName) {
        try {
            int x = Integer.parseInt(customerName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void setViewShippingAndPurchase() {
        optionalLayout.setVisibility(View.VISIBLE);
        op1.setText(getString(R.string.vendor_name));
        if (workItem.getSpVendorName().contains(",") || isNum(workItem.getSpVendorName())) {
            op11.setText("" + getVendorNames(workItem.getSpVendorName()));
        } else {
            op11.setText(workItem.getSpVendorName());
        }
        op2.setText(getString(R.string.vensor_selection));
        op22.setText("" + workItem.getSpVendorPreference());
        op3.setText(getString(R.string.advance_paid));
        op33.setText("" + workItem.getSpAdvancePaid());
        raw2.setVisibility(View.VISIBLE);
        raw3.setVisibility(View.VISIBLE);
        raw4.setVisibility(View.GONE);
        raw5.setVisibility(View.GONE);
        raw6.setVisibility(View.GONE);
        raw7.setVisibility(View.GONE);
    }

    private String getVendorNames(String spVendorName) {

        String[] ids = spVendorName.split(",");
        String str = "";
        for (int i = 0; i < ids.length; i++) {
            Cursor cursor = db.rawQuery("select * from " + Constant.tableVendors + " where ID like " + ids[i], null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    Log.e("Name Founded Customer", " ::" + cursor.getString(cursor.getColumnIndex("Name")));
                    if (i == ids.length - 1)
                        str = str + cursor.getString(cursor.getColumnIndex("Name"));
                    else
                        str = str + cursor.getString(cursor.getColumnIndex("Name")) + ", ";
                }
            }
            if(cursor!=null) cursor.close();
        }
        if (str.length() > 0)
            if (str.charAt(str.length() - 1) == ',')
                return str.substring(0, str.length());
            else
                return str;
        else
            return spVendorName;
    }

    private void setViewCollection() {
        optionalLayout.setVisibility(View.VISIBLE);

        op1.setText(getString(R.string.customer_name));

        op2.setText(getString(R.string.customer_contact));
        op3.setText(getString(R.string.invoice_amount));
        op4.setText(getString(R.string.invoice_date));
        op5.setText(getString(R.string.due_date));
        op6.setText(getString(R.string.outstanding_amount));

        if (isNum(workItem.getCustomerName())) {
            op11.setText("" + getCustomername(workItem.getCustomerName()));
        } else {
            op11.setText("" + workItem.getCustomerName());
        }

//        op11.setText("" + workItem.getCustomerName());
        op22.setText("" + workItem.getCustomerContact());
        op33.setText("" + workItem.getInvoiceAmount());

        try {
            Calendar invCalendar = Calendar.getInstance();
            invCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getInvoiceDate())));
            Calendar dueCalendar = Calendar.getInstance();
            dueCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getDueDate())));
            String sdate = Util.SDF.format(invCalendar.getTime());
            op44.setText("" + sdate.substring(0, 11));
            sdate = Util.SDF.format(dueCalendar.getTime());
            op55.setText("" + sdate.substring(0, 11));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        op66.setText("" + workItem.getOutStandingAmt());
        raw7.setVisibility(View.GONE);
    }

    private void setViewProject() {
        op1.setText("" + getString(R.string.project));
        op2.setText("" + getString(R.string.depent_workItems));

        String name = getProjectName(workItem.getProjectCode());
        if (name.length() > 0) op11.setText(name);
        String name1 = getTaskNames(workItem.getTaskCodeAfter());
        if (name1.length() > 0) op22.setText(name1);
        raw3.setVisibility(View.GONE);
        raw4.setVisibility(View.GONE);
        raw5.setVisibility(View.GONE);
        raw6.setVisibility(View.GONE);
        raw7.setVisibility(View.GONE);
//        op33.setText("" + workItem.getTaskCodeAfter());
    }

    private String getTaskNames(String taskCodeAfter) {
        if (taskCodeAfter != null && taskCodeAfter.length() > 0) {
            String tasks[] = taskCodeAfter.split(",");
            String tasknames = "";

            for (int i = 0; i < tasks.length; i++) {
                Cursor cursor = db.rawQuery("select " + WorkItem.TASK_NAME + " from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " =" + tasks[i], null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if (i == tasks.length - 1)
                        tasknames = tasknames + " " + cursor.getString(cursor.getColumnIndex(WorkItem.TASK_NAME));
                    else
                        tasknames = tasknames + " " + cursor.getString(cursor.getColumnIndex(WorkItem.TASK_NAME)) + " " + ",";
                    cursor.close();
                }
            }
            return tasknames;
        }
        return "";
    }

    private String getProjectName(int projectCode) {
        String query = "select * from " + Constant.ProjectTable + " where " + Project.PROJECT_ID + " = " + projectCode;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String projName = cursor.getString(cursor.getColumnIndex(Project.PROJECT_NAME));
            cursor.close();
            return projName;
        }
        if(cursor!=null) cursor.close();
        return "";
    }

    public void AddAttachment(final Attachment attachment) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.listraw_attachment, null);
        final TextView txtFilePath = (TextView) addView.findViewById(R.id.tv_attachmentFile);
        final Button BtnDownload = (Button) addView.findViewById(R.id.btn_Attachment);
        final ProgressBar pb = (ProgressBar) addView.findViewById(R.id.pb_Attachment);
        pb.setVisibility(View.GONE);
        txtFilePath.setText(getFilename(attachment.getPath()));
        if (attachment.getUploadedDownloaded().equals("true")) {
            BtnDownload.setBackgroundResource(R.drawable.view_attachment);
        } else {
            BtnDownload.setBackgroundResource(R.drawable.download_attachment);
        }

        BtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attachment.getUploadedDownloaded().equals("false")) {
                    new DownloadFile(ViewWorkItemActivity.this, attachment.getPath(), attachment.getAttachmentId(), BtnDownload, pb).execute();
                    downloadCounter++;
                    attachment.setPath(Constant.storageDirectory + getFilename(attachment.getPath()));
                    attachment.setUploadedDownloaded("true");
                } else {
                    openFile(attachment.getPath());
                }
            }
        });
        attachmentLayout.addView(addView);
        viewList.add(addView);
    }

    public String getFilename(String path) {
        String name[] = path.split("/");
        return name[name.length - 1];
    }

    public void openFile(String path) {
        String s = path.substring(path.lastIndexOf(".") + 1);
        if (s.equals("png") || s.equals("jpg") || s.equals("gif")) {
            Intent myIntent = new Intent(this, DisplayFullscreenImage.class);
            myIntent.putExtra("URLURI", "file:///" + path);
            this.startActivity(myIntent);
        } else {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                File file = new File(path);
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String ext = file.getName().substring(file.getName().indexOf(".") + 1);
                String type = mime.getMimeTypeFromExtension(ext);
                intent.setDataAndType(Uri.fromFile(file), type);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.no_application_available_to_open_this_file), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //////////////////////////////

    //////////////////////////
    public class DownloadFile extends AsyncTask<String, Void, String> {
        // ProgressDialog mProgressDialog;
        Context context;
        String urlDownload;
        String transactionCode;
        String fileName;
        Button downloadBtn;
        ProgressBar pb;

        public DownloadFile(ViewWorkItemActivity viewWorkItemActivity, String path, String attachmentId, Button btnDownload, ProgressBar pb) {
            this.context = viewWorkItemActivity;
            this.urlDownload = path;
            this.transactionCode = attachmentId;
            this.downloadBtn = btnDownload;
            this.pb = pb;
            try {
                this.fileName = Util.getFileNameFromUrl(new URL(path));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        protected void onPreExecute() {
            downloadBtn.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);
            pb.setMax(100);
        }

        protected String doInBackground(String... params) {
            URL url = null;
            try {
                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("UserID", "" + "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID)));
                params1.add(new BasicNameValuePair("TokenID", "" + "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_TOKEN)));
                if (params1 != null) {
                    String paramString = URLEncodedUtils
                            .format(params1, "utf-8");
                    urlDownload += "?" + paramString;
                }
                url = new URL(urlDownload);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                File folder = new File(Constant.storageDirectory);

                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                File file = new File(folder, fileName);

                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = urlConnection.getInputStream();

                //this is the total size of the file
                int totalSize = urlConnection.getContentLength();
                //variable to store total downloaded bytes
                int downloadedSize = 0;

                //create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0; //used to store a temporary size of the buffer

                //now, read through the input buffer and write the contents to the file
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    //add the data in the buffer to the file in the file output stream (the file on the sd card
                    fileOutput.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    //this is where you would do something to report the prgress, like this maybe
                    //updateProgress(downloadedSize, totalSize);
                    Log.w("DOWNLOAD", "progress " + downloadedSize + " / " + totalSize);

                }
                //close the output stream when done
                fileOutput.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException p) {
                p.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //catch some possible errors...
            return "done";
        }

        private void publishProgress(int i) {
            Log.v("DOWNLOAD", "PROGRESS ... " + i);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            pb.setProgress(Integer.parseInt(String.valueOf(values[0])));
        }

        protected void onPostExecute(String result) {
            downloadBtn.setBackgroundResource(R.drawable.view_attachment);
            if (result.equals("done")) {
                ContentValues userValues = new ContentValues();
                userValues.put(Attachment.UPLOADEDDOWNLOADED, "true");
                userValues.put(Attachment.PATH, Constant.storageDirectory + fileName);
                db.update(Constant.AttachmentTable, userValues, Attachment.ATTACHMENT_ID + " = ?", new String[]{transactionCode + ""});
            }
            pb.setVisibility(View.GONE);
            downloadBtn.setVisibility(View.VISIBLE);
            downloadCounter--;
        }
    }
}
