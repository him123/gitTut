package com.bigbang.superteam.workitem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.Approval;
import com.bigbang.superteam.dataObjs.Project;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.dataObjs.WorkTransaction;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 30/06/2015.
 */
public class WorkApprovalDetail_Activity extends BaseActivity {

    public String UpdateType = "";
    @InjectView(R.id.img_profile_small)
    ImageView imgLogo;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.tvApprovalType)
    TextView tvApprovalType;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.tvDescription)
    TextView tvDescription;
    @InjectView(R.id.tvAmount)
    TextView tvAmount;
    @InjectView(R.id.tvDates1)
    TextView tvDateTime;
    @InjectView(R.id.tvDates)
    TextView tvDates;
    @InjectView(R.id.tv_createdby)
    TextView tvCreatedBy;
    @InjectView(R.id.tvt_createdBy)
    TextView tvtCteatedBY;
    @InjectView(R.id.etInvoiceDate)
    TextView etInvoiceDate;

    @InjectView(R.id.rlDueDate)
    RelativeLayout rlDueDate;
    @InjectView(R.id.rlAmount)
    RelativeLayout rlAmount;
    @InjectView(R.id.rlDates)
    RelativeLayout rlDates;
    @InjectView(R.id.rlCreatedBy)
    RelativeLayout rlCreatedBy;

    Approval approval;
    WorkItem workItem;
    Project project;
    WorkTransaction workUpdate;
    TransparentProgressDialog progressDialog;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;
    Context mContext;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workapproval);

        Init();

        approval = (Approval) getIntent().getSerializableExtra("Object");
        Log.e("Work Approval Details", " Id=" + approval.getId());
        rlAmount.setVisibility(View.GONE);
        rlDates.setVisibility(View.GONE);

        mContext = this;
        setObjects();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.rlAccept)
    @SuppressWarnings("unused")
    public void Accept(View view) {
        if(Util.isOnline(getApplicationContext())){
            StartRejectOrApproval(true);
        }else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rlReject)
    @SuppressWarnings("unused")
    public void Reject(View view) {
        if(Util.isOnline(getApplicationContext())){
            StartRejectOrApproval(false);
        }else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void Init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();

        Util.setAppFont(mContainer, mFont);

        progressDialog = new TransparentProgressDialog(WorkApprovalDetail_Activity.this, R.drawable.progressdialog, false);

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_profilepic).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.empty_profilepic).showImageOnFail(R.drawable.empty_profilepic).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        imageLoader.init(ImageLoaderConfiguration
                .createDefault(getApplicationContext()));

        imageLoader.displayImage("", imgWhite, options1);
        imageLoader.displayImage("", imgLogo, options);
    }

    private void setObjects() {

        int type = Integer.parseInt(approval.getType());
        switch (type) {

            case Constant.APPROVAL_NEWPRJECT:
                tvApprovalType.setText("New Project");
                break;
            case Constant.CREATE_NEWWORKITEM:
                tvApprovalType.setText("New Task");
                break;
            case Constant.APPROVE_WORKITEMUPDATE:
                tvApprovalType.setText("Task Update");
                break;
            case Constant.EDIT_WORKITEM_APPROVAL:
                tvApprovalType.setText("Edit Task");
                break;
            case Constant.EDIT_PROJECT_APPROVAL:
                tvApprovalType.setText("Edit Project");
                break;
            default:
                break;

        }

        tvName.setText(approval.getTitle().toUpperCase());
        String logo = approval.getImage();
        imageLoader.displayImage(logo, imgLogo, options);

        if (logo.length() > 0) tvName.setVisibility(View.GONE);
        else tvName.setVisibility(View.VISIBLE);

        if (Integer.parseInt(approval.getType()) == Constant.APPROVAL_NEWPRJECT) {
            UpdateType = "" + Constant.APPROVAL_NEWPRJECT;
            project = new Project();
            Cursor crsr = db.rawQuery("select * from " + Constant.ProjectTable + " where " + Project.PROJECT_ID + " = " + approval.getId(), null);
            if (crsr != null && crsr.getCount() > 0) {
                crsr.moveToFirst();
                project.setProject_name(crsr.getString(crsr.getColumnIndex("" + Project.PROJECT_NAME)));
                project.setDescription(crsr.getString(crsr.getColumnIndex("" + Project.DESCRIPTION)));
                project.setPriority(crsr.getString(crsr.getColumnIndex("" + Project.PRIORITY)));
                project.setStartDate(crsr.getString(crsr.getColumnIndex("" + Project.START_DATE)));
                project.setEndDate(crsr.getString(crsr.getColumnIndex("" + Project.END_DATE)));
                project.setEstimatedWorkTime(crsr.getString(crsr.getColumnIndex("" + Project.ESTIMATED_TIME)));
                project.setProjectAssignedTo(crsr.getString(crsr.getColumnIndex("" + Project.ASSIGNED_TO)));
                project.setProjectCCTo(crsr.getString(crsr.getColumnIndex("" + Project.CC_TO)));
                project.setUserCode(crsr.getInt(crsr.getColumnIndex(Project.USER_CODE)));
                project.setProject_Id(crsr.getInt(crsr.getColumnIndex(Project.PROJECT_ID)));
                project.setProjectTasks(crsr.getString(crsr.getColumnIndex("" + Project.PROJECT_TASKS)));
                project.setProjectImage(crsr.getString(crsr.getColumnIndex(Project.PROJECT_IMAGE)));
                setViewProject();
            }
            crsr.close();
        } else if (Integer.parseInt(approval.getType()) == Constant.CREATE_NEWWORKITEM) {
            UpdateType = "" + Constant.CREATE_NEWWORKITEM;
            workItem = new WorkItem();
            Cursor crsr = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + approval.getId(), null);
            if (crsr != null) {
                crsr.moveToFirst();
                workItem.setTaskCode(crsr.getInt(crsr.getColumnIndex(WorkItem.TASK_ID)));
                workItem.setUserCode(crsr.getInt(crsr.getColumnIndex(WorkItem.USER_CODE)));
                workItem.setTaskImage(crsr.getString(crsr.getColumnIndex(WorkItem.TASK_IMAGE)));
                workItem.setTitle(crsr.getString(crsr.getColumnIndex(WorkItem.TASK_NAME)));
                workItem.setDescription(crsr.getString(crsr.getColumnIndex(WorkItem.DESCRIPTION)));
                workItem.setBudget(crsr.getString(crsr.getColumnIndex(WorkItem.BUDGET)));
                workItem.setPriority(crsr.getString(crsr.getColumnIndex(WorkItem.PRIORITY)));
                workItem.setWorkLocation(crsr.getString(crsr.getColumnIndex(WorkItem.WORK_LOCATION)));
                workItem.setStartDate((crsr.getString(crsr.getColumnIndex(WorkItem.START_DATE))));
                workItem.setEndDate((crsr.getString(crsr.getColumnIndex(WorkItem.END_DATE))));
                workItem.setEstimatedWorkTime(crsr.getString(crsr.getColumnIndex(WorkItem.ESTIMATED_TIME)));
                workItem.setTaskAssignedTo(crsr.getString(crsr.getColumnIndex(WorkItem.ASSIGNED_TO)));
                workItem.setTaskType(crsr.getString(crsr.getColumnIndex(WorkItem.TASK_TYPE)));
                workItem.setProjectCode(crsr.getInt(crsr.getColumnIndex(WorkItem.PROJECT_CODE)));
                workItem.setTaskCodeAfter(crsr.getString(crsr.getColumnIndex(WorkItem.TASK_AFTER)));
                workItem.setFrequency(crsr.getString(crsr.getColumnIndex(WorkItem.FREQUENCY)));
                workItem.setDayCodesSelected(crsr.getString(crsr.getColumnIndex(WorkItem.DAYCODES_SELECTED)));
                workItem.setCustomerName(crsr.getString(crsr.getColumnIndex(WorkItem.CUSTOMER_NAME)));
                workItem.setCustomerContact(crsr.getString(crsr.getColumnIndex(WorkItem.CUSTOMER_CONTACT)));
                workItem.setCustomerType(crsr.getString(crsr.getColumnIndex(WorkItem.CUSTOMER_TYPE)));
                workItem.setInvoiceAmount(crsr.getString(crsr.getColumnIndex(WorkItem.INVOICE_AMOUNT)));
                workItem.setInvoiceDate((crsr.getString(crsr.getColumnIndex(WorkItem.INVOICE_DATE))));
                workItem.setDueDate((crsr.getString(crsr.getColumnIndex(WorkItem.DUE_DATE))));
                workItem.setOutStandingAmt(crsr.getString(crsr.getColumnIndex(WorkItem.OUTSTANDING_AMT)));
                workItem.setPastHistory(crsr.getString(crsr.getColumnIndex(WorkItem.PAST_HISTORY)));
                workItem.setSpVendorName(crsr.getString(crsr.getColumnIndex(WorkItem.VENDOR_NAME)));
                workItem.setSpVendorPreference(crsr.getString(crsr.getColumnIndex(WorkItem.VENDOR_PREFERENCE)));
                workItem.setSpAdvancePaid(crsr.getString(crsr.getColumnIndex(WorkItem.ADVANCE_PAID)));
                workItem.setStatus(crsr.getString(crsr.getColumnIndex(WorkItem.STATUS)));
                SetViewWorkItemApproval();
                crsr.close();
            }
        } else if (Integer.parseInt(approval.getType()) == Constant.APPROVE_WORKITEMUPDATE) {
            UpdateType = "" + Constant.APPROVE_WORKITEMUPDATE;
            Log.e("Task Code", approval.getId());
            Cursor crsr = db.rawQuery("select * from " + Constant.WorkTransaction + " where " + WorkTransaction.TRANSACTION_CODE + " = " + approval.getId(), null);
            if (crsr != null) {
                Log.e("crsr Coutn", "" + crsr.getCount());
                if (crsr.getCount() > 0) {
                    crsr.moveToFirst();
                    workUpdate = new WorkTransaction();
                    workUpdate.setTr_code(crsr.getInt(crsr.getColumnIndex(WorkTransaction.TRANSACTION_CODE)));
                    workUpdate.setUser_code(crsr.getInt(crsr.getColumnIndex(WorkTransaction.USER_CODE)));
                    workUpdate.setTask_code(crsr.getInt(crsr.getColumnIndex(WorkTransaction.TASK_CODE)));
                    workUpdate.setUpdate_type("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.UPDATE_TYPE)));
                    workUpdate.setMessage("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.MESSAGE)));
                    workUpdate.setMessage_type("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.MESSAGE_TYPE)));
                    workUpdate.setLink("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.MESSAGE_LINK)));
                    workUpdate.setStart_date("" + (crsr.getString(crsr.getColumnIndex(WorkTransaction.START_DATE))));
                    workUpdate.setEnd_date("" + (crsr.getString(crsr.getColumnIndex(WorkTransaction.END_DATE))));
                    workUpdate.setDelegate_to("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.DELEGATE_TO)));
                    workUpdate.setDiscription("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.DISCRIPTION)));
                    workUpdate.setAmount("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.AMOUNT)));
                    workUpdate.setInvoice_date("" + (crsr.getString(crsr.getColumnIndex(WorkTransaction.INVOICE_DATE))));
                    workUpdate.setStatus("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.STATUS)));
                    workUpdate.setCreated_on("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.CREATED_ON)));
                    setViewWorkUpdate();
                    crsr.close();
                }
            }
        } else if (approval.getType().equals("" + Constant.EDIT_WORKITEM_APPROVAL)) {

            try{
                JSONObject jsonObject=new JSONObject(""+approval.getData());
                tvTitle.setText(jsonObject.optString("name"));
                tvDescription.setText(jsonObject.optString("description"));
            }catch (Exception e){e.printStackTrace();}

            tvCreatedBy.setText(Util.getUsernames("" + approval.getUserId(),mContext));
            tvtCteatedBY.setText(getString(R.string.edited_by));
        } else if (approval.getType().equals("" + Constant.EDIT_PROJECT_APPROVAL)) {

            try{
                JSONObject jsonObject=new JSONObject(""+approval.getData());
                tvTitle.setText(jsonObject.optString("name"));
                tvDescription.setText(jsonObject.optString("description"));
            }catch (Exception e){e.printStackTrace();}

            tvCreatedBy.setText(Util.getUsernames("" + approval.getUserId(),mContext));
            tvtCteatedBY.setText(getString(R.string.edited_by));
        }
    }

    private void SetViewWorkItemApproval() {
        tvTitle.setText(workItem.getTitle());
        String desc = workItem.getDescription();// +"\n Type:" + workItem.getTaskType();
        tvDescription.setText(desc);
        rlDates.setVisibility(View.VISIBLE);
        tvCreatedBy.setText(Util.getUsernames("" + workItem.getUserCode(),mContext));
        String startDate=""+getDate(workItem.getStartDate());
        String endDate=""+getDate(workItem.getEndDate());
        tvDates.setText( startDate+ " to " + "\n" + endDate);

        try{
            if(workItem.getTaskType().equals(Constant.WORKTYPES[Constant.REGULAR])){
                tvDateTime.setText(getResources().getString(R.string.time));
                SimpleDateFormat sdf=new SimpleDateFormat("hh:mm a");
                startDate=sdf.format(Util.SDF.parse(startDate));
                endDate=sdf.format(Util.SDF.parse(endDate));
                tvDates.setText( startDate+ " to " + endDate);
            }
        }catch (Exception e){e.printStackTrace();}
    }

    private void setViewWorkUpdate() {

        Log.e("In func", " Set View Work Update");
        String qry = "select " + WorkItem.TASK_NAME + " from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + workUpdate.getTask_code();
        Log.e("Querry", " " + qry);
        Cursor crsr = db.rawQuery(qry, null);
        if (crsr != null) {
            crsr.moveToFirst();
            tvTitle.setText(""+crsr.getString(crsr.getColumnIndex(WorkItem.TASK_NAME)));
            crsr.close();
        }
        String desc = "";
        if (workUpdate.getUpdate_type().equals("Done")) {
            desc = getString(R.string.done) + ": " + workUpdate.getDiscription();
        } else if (workUpdate.getUpdate_type().equals("Postponed")) {
            desc = workUpdate.getDiscription() + "\n Postponed Request to: " + getDate(workUpdate.getStart_date()) + "\n to :: " + getDate(workUpdate.getEnd_date());
        } else if (workUpdate.getUpdate_type().equals("Delegate")) {
            desc = workUpdate.getDiscription() + "\n Delegate Request to :" + Util.getUsernames(workUpdate.getDelegate_to(),mContext);
        } else if (workUpdate.getUpdate_type().equals("Expense")) {
            rlAmount.setVisibility(View.VISIBLE);
            rlDueDate.setVisibility(View.VISIBLE);
            desc = workUpdate.getDiscription();
            etInvoiceDate.setText(getDate(workUpdate.getInvoice_date()).substring(0, 11));
            tvAmount.setText("" + workUpdate.getAmount());
        }
        tvDescription.setText(desc);
        tvCreatedBy.setText(Util.getUsernames("" + workUpdate.getUser_code(),mContext));
        tvtCteatedBY.setText(getString(R.string.updated_by));
    }

    private void setViewProject() {
        tvTitle.setText(project.getProject_name());
        tvDescription.setText(project.getDescription());
        tvDates.setText(getDate(project.getStartDate()) + "\n to \n" + getDate(project.getEndDate()));
        rlDates.setVisibility(View.VISIBLE);
        tvCreatedBy.setText(Util.getUsernames("" + project.getUserCode(),mContext));
    }

    private String getDate(String xDate) {
        String sdate = xDate;
        Calendar tempCal = null;
        try {
            tempCal = Calendar.getInstance();
            tempCal.setTime(Util.sdf.parse(Util.utcToLocalTime(xDate)));
            sdate = Util.SDF.format(tempCal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdate;
    }

    public void StartRejectOrApproval(boolean flag) {
        if (Integer.parseInt(approval.getType()) == Constant.APPROVE_WORKITEMUPDATE) {
            Cursor crsr =db.rawQuery("select * from " + Constant.WorkTransaction + " where " + WorkTransaction.TRANSACTION_CODE + " = " + approval.getId(), null);
            if (crsr != null) {
                crsr.moveToFirst();
                String type = crsr.getString(crsr.getColumnIndex(WorkTransaction.UPDATE_TYPE));
                String amt = crsr.getString(crsr.getColumnIndex(WorkTransaction.AMOUNT));
                new Upload(type, amt, Boolean.toString(flag)).execute();
                crsr.close();
            }
        } else {
            new Upload(null, "", Boolean.toString(flag)).execute();
        }
    }

    public void setDataStatusProject(String answer, String id) {
        Cursor crsr = db.rawQuery("select * from " + Constant.ProjectTable + " where " + Project.PROJECT_ID + " = " + id, null);
        ContentValues objValues = new ContentValues();
        if (answer.equals("true"))
            objValues.put(Project.STATUS, "Approved");
        else
            objValues.put(Project.STATUS, "Rejected");
        if (crsr != null && crsr.getCount() > 0) {
            crsr.moveToFirst();
            db.update(Constant.ProjectTable, objValues, Project.PROJECT_ID + " = ?", new String[]{id + ""});
            crsr.close();
        }
    }

    private void setDataStatusCreate(String answer, String id) {
        Cursor crsr = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + id, null);
        ContentValues objValues = new ContentValues();
        if (answer.equals("true"))
            objValues.put("Work_Status", "Approved");
        else
            objValues.put("Work_Status", "Rejected");
        if (crsr != null && crsr.getCount() > 0) {
            crsr.moveToFirst();
            db.update(Constant.WorkItemTable, objValues, WorkItem.TASK_ID + " = ?", new String[]{id + ""});
            crsr.close();
        }
    }

    private void setDataStatusUpdate(String answer, String id) {
        Cursor crsr = db.rawQuery("select * from " + Constant.WorkTransaction + " where " + WorkTransaction.TRANSACTION_CODE + " = " + id, null);
        ContentValues objValues = new ContentValues();
        if (answer.equals("true"))
            objValues.put("status", "Approved");
        else
            objValues.put("status", "Rejected");
        if (crsr != null && crsr.getCount() > 0) {
            crsr.moveToFirst();
            db.update(Constant.WorkTransaction, objValues, WorkTransaction.TRANSACTION_CODE + " = ?", new String[]{id + ""});
            crsr.close();
        }
    }

    class Upload extends AsyncTask<Void, String, String> {
        String type, amt, answer;

        public Upload(String t, String a, String ans) {
            type = t;
            answer = ans;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("Pre Execute", " Started Pre Execute");
            // progress.show();
            if (progressDialog != null)
                progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            String resp = "";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = null;
                HttpPost poster = null;

                HashMap<String, String> localmap = new HashMap<String, String>();
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                localmap.put("transactionid", "" + approval.getTransactionID());
                if (Integer.parseInt(approval.getType()) == Constant.APPROVE_WORKITEMUPDATE) {
                    localmap.put("workitemupdateid", "" + approval.getId());
                    localmap.put("userid", "" + approval.getUserId());
                    localmap.put("type", "" + approval.getTitle());
                    localmap.put("note", "" + approval.getDescription());

                    if (amt != null && amt.length() > 0)
                        localmap.put("expenseamt", amt);
                    else
                        localmap.put("expenseamt", "0");
                    localmap.put("type", type);
                    poster = new HttpPost(
                            Constant.URL + "approveWorkItemUpdate");
                } else if (Integer.parseInt(approval.getType()) == Constant.APPROVAL_NEWPRJECT || Integer.parseInt(approval.getType()) == Constant.EDIT_PROJECT_APPROVAL) {
                    localmap.put("projectid", "" + approval.getId());
                    localmap.put("userid", "" + approval.getUserId());
                    localmap.put("note", "" + approval.getDescription());
                    poster = new HttpPost(
                            Constant.URL + "approveProject");
                } else if (Integer.parseInt(approval.getType()) == Constant.CREATE_NEWWORKITEM || Integer.parseInt(approval.getType()) == Constant.EDIT_WORKITEM_APPROVAL) {
                    poster = new HttpPost(Constant.URL + "approveWorkItem");
                    localmap.put("workitemid", "" + approval.getId());
                    localmap.put("userid", "" + approval.getUserId());
                    localmap.put("note", "" + approval.getDescription());
                }

                if (poster != null) {
                    localmap.put("approved", answer);
                    Log.e("Calling Service:", localmap.toString());
                    entity.addPart("JSON", new StringBody(Util.prepareJsonString(localmap)));
                    entity.addPart("TokenID", new StringBody(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_TOKEN)));
                    entity.addPart("UserID", new StringBody(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID)));
                    poster.setEntity(entity);
                    response = client.execute(poster);
                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(response.getEntity()
                                    .getContent()));
                    String line = null;
                    while ((line = rd.readLine()) != null) {
                        resp += line;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("Resp Upload 66666", "" + resp);
            return resp;
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);

            if (progressDialog != null) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }

            Log.e("Response", "Received :-" +o);
            JSONObject json = null;  //your response
            try {
                json = new JSONObject(o);
                Toast.makeText(mContext, json.optString("message"), Toast.LENGTH_SHORT).show();
                if (json.getString("status").equals("Success")) {

                    if (Integer.parseInt(approval.getType()) == Constant.APPROVE_WORKITEMUPDATE)
                        setDataStatusUpdate(answer, approval.getId());
                    else if (Integer.parseInt(approval.getType()) == Constant.APPROVAL_NEWPRJECT)
                        setDataStatusProject(answer, approval.getId());
                    else if (Integer.parseInt(approval.getType()) == Constant.EDIT_PROJECT_APPROVAL)
                        setDataStatusUpdate(answer, approval.getId());
                    else if (Integer.parseInt(approval.getType()) == Constant.EDIT_WORKITEM_APPROVAL)
                        setDataStatusUpdate(answer, approval.getId());
                    else
                        setDataStatusCreate(answer, approval.getId());

                    int approvalStatus=1;
                    if(answer.equals("true")){
                        approvalStatus=2;
                    }
                    else{
                        approvalStatus=3;
                    }
                    ContentValues values = new ContentValues();
                    values.put("Status", "" + approvalStatus);

                    db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + approval.getTransactionID() + "\"", null);
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //progress.hide();
        }
    }

}
