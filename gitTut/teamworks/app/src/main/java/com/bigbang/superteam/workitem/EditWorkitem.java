package com.bigbang.superteam.workitem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.WorkItem_GCM;
import com.bigbang.superteam.adapter.AttachmentAdapter;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.Attachment;
import com.bigbang.superteam.dataObjs.OfflineWork;
import com.bigbang.superteam.dataObjs.StaticData;
import com.bigbang.superteam.dataObjs.User;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.ImageFilePath;
import com.bigbang.superteam.util.InputFilterMinMax;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 06/07/2015.
 */
public class EditWorkitem extends BaseActivity implements View.OnClickListener {

    public static final int BROWSE_ACTIVITY = 101;
    public static final int END_DATE = 105;
    public static final int START_DATE = 106;
    public static final int START_TIME = 107;
    public static final int END_TIME = 108;
    private static final int SELECT_FILE = 109;
    private static final int REQUEST_CAMERA = 110;
    private static final int DUE_DATE = 111;
    private static final int INVOICE_DATE = 112;
    private static final int CROP_FROM_CAMERA = 113;
    public static Boolean Active = false;
    public int WorkTypeViews[] = new int[Constant.WORKTYPES.length];
    WorkItem workItem;
    Calendar startCalendar, endCalendar, invoiceCalendar, dueDateCalendar, calendar;


    @InjectView(R.id.viewTaskDetails)
    View viewTaskDetails;
    @InjectView(R.id.viewTaskAttachments)
    View viewTaskAttachments;
    @InjectView(R.id.tvTaskDetails)
    TextView tvTaskDetails;
    @InjectView(R.id.tvTaskAttachments)
    TextView tvTaskAttachments;
    @InjectView(R.id.rlDetails)
    RelativeLayout rlDetails;
    @InjectView(R.id.rlAttachments)
    RelativeLayout rlAttachments;

    @InjectView(R.id.listAttachments)
    ListView listAttachments;

    @InjectView(R.id.btn_selectImage)
    Button selectImageBtn;
    @InjectView(R.id.tvInvoiceDate)
    TextView tvInvoiceDate;
    @InjectView(R.id.tvDueDate)
    TextView tvDueDate;
    @InjectView(R.id.btn_CancelImage)
    Button cancelImage;
    @InjectView(R.id.et_ActualHrs)
    EditText actualHours;
    @InjectView(R.id.et_ActualMins)
    EditText actualMins;
    @InjectView(R.id.et_WorkTitle)
    EditText workTitleET;
    @InjectView(R.id.et_workDescription)
    EditText workDescriptionET;
    @InjectView(R.id.et_workBudget)
    EditText workBudgetET;
    @InjectView(R.id.et_SSCustomerName)
    EditText ssCustomerNameET;
    @InjectView(R.id.et_SSCustomerContact)
    EditText ssCustomerContactET;
    @InjectView(R.id.et_PastHistory)
    EditText pastHistoryET;
    @InjectView(R.id.et_CollCustomerName)
    EditText collCustomernameET;
    @InjectView(R.id.et_CollCustomerContact)
    EditText collCustomerContactET;
    @InjectView(R.id.et_Invoiceamount)
    EditText invoiceAmountET;
    @InjectView(R.id.et_OutStandingAmount)
    EditText outstandingAmtET;
    @InjectView(R.id.et_VendorName)
    EditText vendorNameET;
    @InjectView(R.id.et_AdvancePaid)
    EditText advancePaidET;
    @InjectView(R.id.et_address)
    EditText etAddress;
    /////////////////////////
    @InjectView(R.id.tv_assignTo)
    TextView tvAssigneed;
    @InjectView(R.id.tv_ccWorkitem)
    TextView tvCC;
    //    @InjectView(R.id.tv_selectedDays)
//    TextView tvSelectedDays;
    @InjectView(R.id.tvVendors)
    TextView tvVendors;
//    @InjectView(R.id.spinner_priority)
//    Spinner prioritySpinner;
    @InjectView(R.id.spinner_SSCustomerName)
    Spinner ssCustomerNameSpinner;
    @InjectView(R.id.spinner_ColCustomerName)
    Spinner ColCustomerNameSpinner;
    @InjectView(R.id.iv_selectedImage)
    ImageView selectedImage;
    @InjectView(R.id.ll_sales_service)
    LinearLayout ll_sales;
    @InjectView(R.id.ll_collection_service)
    LinearLayout ll_collection;
    @InjectView(R.id.ll_purchase)
    LinearLayout ll_purchase;
    @InjectView(R.id.ll_SSOthersName)
    RelativeLayout llSSOthersName;
    @InjectView(R.id.ll_ColOthersName)
    RelativeLayout llColOthersName;
    @InjectView(R.id.ll_otherVendorName)
    RelativeLayout llVendorOthersName;
    @InjectView(R.id.rlDependentTask)
    RelativeLayout rlDependentTask;

    @InjectView(R.id.tv_taskList)
    TextView tvTasksList;
    //    @InjectView(R.id.rg_workCategory)
//    RadioGroup workCategoryGroup;
    @InjectView(R.id.rlRegularTypeTask)
    RelativeLayout rlRegularTypeTask;
    @InjectView(R.id.rlDaily)
    RelativeLayout rlDaily;
    @InjectView(R.id.rlWeekly)
    RelativeLayout rlWeekly;
    @InjectView(R.id.rlMonthly)
    RelativeLayout rlMonthly;
    @InjectView(R.id.tvDaily)
    TextView tvDaily;
    @InjectView(R.id.tvWeekly)
    TextView tvWeekly;
    @InjectView(R.id.tvMonthly)
    TextView tvMonthly;
    @InjectView(R.id.tv_selectedDays)
    TextView tvDays;

    @InjectView(R.id.rlHighPriority)
    RelativeLayout rlHighPriority;
    @InjectView(R.id.rlMediumPriority)
    RelativeLayout rlMediumPriority;
    @InjectView(R.id.rlLowPriority)
    RelativeLayout rlLowPriority;
    @InjectView(R.id.tvHighPriority)
    TextView tvHighPriority;
    @InjectView(R.id.tvMediumPriority)
    TextView tvMediumPriority;
    @InjectView(R.id.tvLowPriority)
    TextView tvLowPriority;

    @InjectView(R.id.rg_customerType)
    RadioGroup customerTypeGroup;
    @InjectView(R.id.rg_purchasePreference)
    RadioGroup purchasePreferenceGroup;
    String invoiceDate = "", dueDate = "";
    int noofMembers = 0, monthDays = 31, weekDays = 7;
    //    boolean weekDaysSelected[] = new boolean[7];
//    boolean monthDaysSelected[] = new boolean[31];
    Uri globalUri = null;
    CreateWorkActivity CameraActivity = null;
    Cursor cursor = null;
    String camera_pathname = "";
    ImageLoader imageLoader;
    //    ProgressDialog progress;
    TransparentProgressDialog progressDialog;

    ArrayList<User> listUser=new ArrayList<>();
    ArrayList<User> listAssignee=new ArrayList<>();
    ArrayList<User> listCC=new ArrayList<>();

    ArrayList<Customer> customerList = new ArrayList<>();
    ArrayList<Customer> vendorList = new ArrayList<>();
    ArrayList<WorkItem> projectWorkList = new ArrayList<>();
    ArrayList<HashMap<String, String>> listDays = new ArrayList<>();
//    boolean vendorsSelected[];
    //    String membersString[];
    String customersString[];
    //    boolean assignMembersSelected[];
//    boolean ccMembersSelected[];
//    boolean workItemsSelected[];
//    String workString[];
    Boolean dueDateFlag = false, invoiceDateFlag = false;
    @InjectView(R.id.tv_headerTitle)
    TextView tvHeaderTitle;
    String strFrequency = Constant.DAILY;
    String strPriority = Constant.MEDIUM;

    @InjectView(R.id.tvStartingFrom)
    TextView tvStartingFrom;
    @InjectView(R.id.tvEndsBy)
    TextView tvEndsBy;

    ArrayList<HashMap<String, String>> attachmentData = new ArrayList<>();
    AttachmentAdapter attachmentAdapter;
    boolean isFirstTime=true;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editworkitem);

        workItem = (WorkItem) getIntent().getSerializableExtra("WorkItem");
        ButterKnife.inject(this);
        Init();
        GetUserList();
        getCustomerList();
        getVendorsList();

        if (workItem != null) {
            setViewAsPerWorkItem();
        }
        createURI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Active = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("edit_workitem"));

        if (read(Constant.SHRED_PR.KEY_ASSIGNEE_CC).equals("1")) {
            //Assignee:
            write(Constant.SHRED_PR.KEY_ASSIGNEE_CC, "0");

            String strAssignee = "";
            try {
                JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
                listAssignee.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                    User user = new User();
                    user.setFirstName("" + jsonObject1.optString(User.FIRST_NAME));
                    user.setLastName("" + jsonObject1.optString(User.LAST_NAME));
                    user.setUser_Image("" + jsonObject1.optString(User.USER_IMAGE));
                    user.setUser_Id(jsonObject1.optInt(User.USER_ID));
                    user.setSelected(jsonObject1.optBoolean(User.SELECTED));

                    if (user.isSelected()) {
                        if (strAssignee.length() == 0)
                            strAssignee = user.getFirstName() + " " + user.getLastName();
                        else strAssignee += ", " + user.getFirstName() + " " + user.getLastName();
                    }

                    listAssignee.add(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (strAssignee.length() > 0) {
                tvAssigneed.setText("" + strAssignee + "\n");
            } else tvAssigneed.setText("");

        }

        if (read(Constant.SHRED_PR.KEY_ASSIGNEE_CC).equals("2")) {
            //CC:
            write(Constant.SHRED_PR.KEY_ASSIGNEE_CC, "0");

            String strCC = "";
            try {
                JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
                listCC.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                    User user = new User();
                    user.setFirstName("" + jsonObject1.optString(User.FIRST_NAME));
                    user.setLastName("" + jsonObject1.optString(User.LAST_NAME));
                    user.setUser_Image("" + jsonObject1.optString(User.USER_IMAGE));
                    user.setUser_Id(jsonObject1.optInt(User.USER_ID));
                    user.setSelected(jsonObject1.optBoolean(User.SELECTED));
                    user.setDisabled(jsonObject1.optBoolean(User.DISABLED));

                    if (user.isSelected()) {
                        if (strCC.length() == 0)
                            strCC = user.getFirstName() + " " + user.getLastName();
                        else strCC += ", " + user.getFirstName() + " " + user.getLastName();
                    }

                    listCC.add(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (strCC.length() > 0) {
                tvCC.setText("" + strCC + "\n");
            } else tvCC.setText("");

        }

        if (read(Constant.SHRED_PR.KEY_TASKLIST).equals("1")) {
            write(Constant.SHRED_PR.KEY_TASKLIST, "0");

            String strDependTasks = "";
            try {
                JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
                projectWorkList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                    WorkItem workItem = new WorkItem();
                    workItem.setTaskCode(jsonObject1.optInt(WorkItem.TASK_ID));
                    workItem.setTitle("" + jsonObject1.optString(WorkItem.TASK_NAME));
                    workItem.setStartDate("" + jsonObject1.optString(WorkItem.START_DATE));
                    workItem.setEndDate("" + jsonObject1.optString(WorkItem.END_DATE));
                    workItem.setSelected(jsonObject1.optBoolean(WorkItem.SELECTED));

                    if (workItem.isSelected()) {
                        if (strDependTasks.length() == 0) strDependTasks = workItem.getTitle();
                        else strDependTasks += ", " + workItem.getTitle();
                    }

                    projectWorkList.add(workItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            tvTasksList.setText("");
            if (strDependTasks.length() > 0) {
                tvTasksList.setText("" + strDependTasks + "\n");
            }

            for (int i = 0; i < projectWorkList.size(); i++) {
                if (projectWorkList.get(i).isSelected()) {
                    Calendar tempCalendar = null, lastCalendar = null;
                    try {
                        if (tempCalendar == null) {
                            tempCalendar = Calendar.getInstance();
                            lastCalendar = Calendar.getInstance();
                            tempCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(projectWorkList.get(i).getEndDate())));
                            lastCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(projectWorkList.get(i).getEndDate())));
                        }
                        tempCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(projectWorkList.get(i).getEndDate())));
                        if (tempCalendar.after(lastCalendar)) {
                            lastCalendar = tempCalendar;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (lastCalendar != null) {
                        lastCalendar.set(Calendar.MINUTE, lastCalendar.get(Calendar.MINUTE) + 1);
                        startCalendar.setTime(lastCalendar.getTime());
                        lastCalendar.set(Calendar.DAY_OF_MONTH, lastCalendar.get(Calendar.DAY_OF_MONTH) + 1);
                        endCalendar.setTime(lastCalendar.getTime());
                        String sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
                        tvEndsBy.setText(sdate);
                        sdate = Util.ddMMMyyyy.format(startCalendar.getTime());
                        tvStartingFrom.setText(sdate);
                    }
                }
            }
        }

        if (read(Constant.SHRED_PR.KEY_DAYSLIST).equals("1")) {
            write(Constant.SHRED_PR.KEY_DAYSLIST, "0");

            String strDays = "";
            try {
                JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
                listDays.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("day", "" + jsonObject1.optString("day"));
                    hashMap.put("selected", "" + jsonObject1.optString("selected"));
                    hashMap.put("code", "" + jsonObject1.optString("code"));

                    if (hashMap.get("selected").equals("1")) {
                        if (strDays.length() == 0) strDays = hashMap.get("day");
                        else strDays += ", " + hashMap.get("day");
                    }

                    listDays.add(hashMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (strDays.length() > 0) {
                tvDays.setText("Days: " + strDays + "\n");
            }
        }

        if (read(Constant.SHRED_PR.KEY_VENDOR_LIST).equals("1")) {
            //Vendor :
            write(Constant.SHRED_PR.KEY_VENDOR_LIST, "0");

            String strVendors = "";
            try {
                JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
                vendorList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                    Customer customer = new Customer();
                    customer.setName("" + jsonObject1.optString("Name"));
                    customer.setMobileNo("" + jsonObject1.optString("MobileNo"));
                    customer.setID(jsonObject1.optInt("ID"));
                    customer.setSelected(jsonObject1.optBoolean("selected"));

                    if (customer.isSelected()) {
                        if (strVendors.length() == 0)
                            strVendors = customer.getName();
                        else strVendors += ", " + customer.getName() ;
                    }

                    vendorList.add(customer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (strVendors.length() > 0) {
                tvVendors.setText("" + strVendors + "\n");
            } else tvVendors.setText("");

            if(vendorList.get(vendorList.size()-1).isSelected()){
                llVendorOthersName.setVisibility(View.VISIBLE);
            }
            else{
                llVendorOthersName.setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Active = false;
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
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.rlDone)
    @SuppressWarnings("unused")
    public void Done(View view) {
        startSubmittingItem();
    }

    @OnClick(R.id.rlAssignee)
    @SuppressWarnings("unused")
    public void Assignee(View view) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < listAssignee.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(User.FIRST_NAME, "" + listAssignee.get(i).getFirstName());
                jsonObject.put(User.LAST_NAME, "" + listAssignee.get(i).getLastName());
                jsonObject.put(User.USER_ID, "" + listAssignee.get(i).getUser_Id());
                jsonObject.put(User.USER_IMAGE, "" + listAssignee.get(i).getUser_Image());
                jsonObject.put(User.SELECTED, "" + listAssignee.get(i).isSelected());
                jsonArray.put(jsonObject);
            }
            write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
            Intent intent = new Intent(EditWorkitem.this, SelectAssigneeCC.class);
            intent.putExtra("title", getResources().getString(R.string.assign_to));
            intent.putExtra("key_assignee_cc", "1");
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_bottom,
                    R.anim.hold_bottom);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.rlCC)
    @SuppressWarnings("unused")
    public void CC(View view) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < listCC.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(User.FIRST_NAME, "" + listCC.get(i).getFirstName());
                jsonObject.put(User.LAST_NAME, "" + listCC.get(i).getLastName());
                jsonObject.put(User.USER_ID, "" + listCC.get(i).getUser_Id());
                jsonObject.put(User.USER_IMAGE, "" + listCC.get(i).getUser_Image());
                jsonObject.put(User.SELECTED, "" + listCC.get(i).isSelected());
                jsonObject.put(User.DISABLED, "" + listCC.get(i).isDisabled());
                jsonArray.put(jsonObject);
            }
            write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
            Intent intent = new Intent(EditWorkitem.this, SelectAssigneeCC.class);
            intent.putExtra("title", getResources().getString(R.string.cc));
            intent.putExtra("key_assignee_cc", "2");
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_bottom,
                    R.anim.hold_bottom);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.rlVendors)
    @SuppressWarnings("unused")
    public void Vendors(View view) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < vendorList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Name", "" + vendorList.get(i).getName());
                jsonObject.put("MobileNo", "" + vendorList.get(i).getMobileNo());
                jsonObject.put("ID", "" + vendorList.get(i).getID());
                jsonObject.put("selected", "" + vendorList.get(i).isSelected());
                jsonArray.put(jsonObject);
            }
            write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
            Intent intent = new Intent(EditWorkitem.this, SelectVendorActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_bottom,
                    R.anim.hold_bottom);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.rlStartingFrom)
    @SuppressWarnings("unused")
    public void StartingFrom(View view) {
        if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.REGULAR])) {
            showDialog(START_TIME);
        } else
            showDialog(START_DATE);
    }

    @OnClick(R.id.rlEndsBy)
    @SuppressWarnings("unused")
    public void EndsBy(View view) {
        if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.REGULAR])) {
            showDialog(END_TIME);
        } else
            showDialog(END_DATE);
    }

    @OnClick(R.id.rlInvoiceDate)
    @SuppressWarnings("unused")
    public void rlInvoiceDate(View view) {
        showDialog(INVOICE_DATE);
    }

    @OnClick(R.id.rlDueDate)
    @SuppressWarnings("unused")
    public void rlDueDate(View view) {
        showDialog(DUE_DATE);
    }

    @OnClick(R.id.rlDependentTask)
    @SuppressWarnings("unused")
    public void DependentTask(View view) {

        if (projectWorkList.size() > 0) {
            try {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < projectWorkList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(WorkItem.TASK_ID, "" + projectWorkList.get(i).getTaskCode());
                    jsonObject.put(WorkItem.TASK_NAME, "" + projectWorkList.get(i).getTitle());
                    jsonObject.put(WorkItem.START_DATE, "" + projectWorkList.get(i).getStartDate());
                    jsonObject.put(WorkItem.END_DATE, "" + projectWorkList.get(i).getEndDate());
                    jsonObject.put(WorkItem.SELECTED, "" + projectWorkList.get(i).isSelected());
                    jsonArray.put(jsonObject);
                }
                write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
                Intent intent = new Intent(EditWorkitem.this, SelectTaskActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_bottom,
                        R.anim.hold_bottom);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_workitem_to_select), Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick(R.id.rlDaily)
    @SuppressWarnings("unused")
    public void Daily(View view) {

        strFrequency = Constant.DAILY;
        rlDaily.setBackgroundResource(R.drawable.rectangle_selected);
        rlWeekly.setBackgroundResource(R.drawable.rectangle_unselected);
        rlMonthly.setBackgroundResource(R.drawable.rectangle_unselected);
        tvDaily.setTextColor(getResources().getColor(R.color.white));
        tvWeekly.setTextColor(getResources().getColor(R.color.gray));
        tvMonthly.setTextColor(getResources().getColor(R.color.gray));

        listDays.clear();
        tvDays.setText("");

    }

    @OnClick(R.id.rlWeekly)
    @SuppressWarnings("unused")
    public void Weekly(View view) {

        boolean flag = true;
        if (strFrequency.equals(Constant.WEEKLY)) flag = false;

        if (flag) {
            strFrequency = Constant.WEEKLY;
            rlDaily.setBackgroundResource(R.drawable.rectangle_unselected);
            rlWeekly.setBackgroundResource(R.drawable.rectangle_selected);
            rlMonthly.setBackgroundResource(R.drawable.rectangle_unselected);
            tvDaily.setTextColor(getResources().getColor(R.color.gray));
            tvWeekly.setTextColor(getResources().getColor(R.color.white));
            tvMonthly.setTextColor(getResources().getColor(R.color.gray));

            listDays.clear();
            tvDays.setText("");
            for (int i = 0; i < StaticData.elementsDays.length; i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("day", "" + StaticData.elementsDays[i]);
                hashMap.put("selected", "0");
                hashMap.put("code", "" + i);
                listDays.add(hashMap);
            }
        }

        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < listDays.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("day", "" + listDays.get(i).get("day"));
                jsonObject.put("selected", "" + listDays.get(i).get("selected"));
                jsonObject.put("code", "" + listDays.get(i).get("code"));
                jsonArray.put(jsonObject);
            }
            write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
            Intent intent = new Intent(EditWorkitem.this, SelectDaysActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_bottom,
                    R.anim.hold_bottom);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.rlMonthly)
    @SuppressWarnings("unused")
    public void Monthly(View view) {

        boolean flag = true;
        if (strFrequency.equals(Constant.MONTHLY)) flag = false;

        if (flag) {
            strFrequency = Constant.MONTHLY;
            rlDaily.setBackgroundResource(R.drawable.rectangle_unselected);
            rlWeekly.setBackgroundResource(R.drawable.rectangle_unselected);
            rlMonthly.setBackgroundResource(R.drawable.rectangle_selected);
            tvDaily.setTextColor(getResources().getColor(R.color.gray));
            tvWeekly.setTextColor(getResources().getColor(R.color.gray));
            tvMonthly.setTextColor(getResources().getColor(R.color.white));

            listDays.clear();
            tvDays.setText("");
            for (int i = 0; i < StaticData.elementMonth.length; i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("day", "" + StaticData.elementMonth[i]);
                hashMap.put("selected", "0");
                hashMap.put("code", "" + StaticData.elementMonth[i]);
                listDays.add(hashMap);
            }
        }

        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < listDays.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("day", "" + listDays.get(i).get("day"));
                jsonObject.put("selected", "" + listDays.get(i).get("selected"));
                jsonObject.put("code", "" + listDays.get(i).get("code"));
                jsonArray.put(jsonObject);
            }
            write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
            Intent intent = new Intent(EditWorkitem.this, SelectDaysActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_bottom,
                    R.anim.hold_bottom);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.rlHighPriority)
    @SuppressWarnings("unused")
    public void HighPriority(View view) {

        strPriority = Constant.HIGH;
        rlHighPriority.setBackgroundResource(R.drawable.rectangle_selected);
        rlMediumPriority.setBackgroundResource(R.drawable.rectangle_unselected);
        rlLowPriority.setBackgroundResource(R.drawable.rectangle_unselected);
        tvHighPriority.setTextColor(getResources().getColor(R.color.white));
        tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
        tvLowPriority.setTextColor(getResources().getColor(R.color.gray));

    }

    @OnClick(R.id.rlMediumPriority)
    @SuppressWarnings("unused")
    public void MediumPriority(View view) {

        strPriority = Constant.MEDIUM;
        rlHighPriority.setBackgroundResource(R.drawable.rectangle_unselected);
        rlMediumPriority.setBackgroundResource(R.drawable.rectangle_selected);
        rlLowPriority.setBackgroundResource(R.drawable.rectangle_unselected);
        tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
        tvMediumPriority.setTextColor(getResources().getColor(R.color.white));
        tvLowPriority.setTextColor(getResources().getColor(R.color.gray));

    }

    @OnClick(R.id.rlLowPriority)
    @SuppressWarnings("unused")
    public void LowPriority(View view) {

        strPriority = Constant.LOW;
        rlHighPriority.setBackgroundResource(R.drawable.rectangle_unselected);
        rlMediumPriority.setBackgroundResource(R.drawable.rectangle_unselected);
        rlLowPriority.setBackgroundResource(R.drawable.rectangle_selected);
        tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
        tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
        tvLowPriority.setTextColor(getResources().getColor(R.color.white));

    }


    @OnClick(R.id.rlTaskDetails)
    @SuppressWarnings("unused")
    public void Details(View view) {
        viewTaskDetails.setVisibility(View.VISIBLE);
        viewTaskAttachments.setVisibility(View.GONE);
        tvTaskDetails.setTextColor(getResources().getColor(R.color.white));
        tvTaskAttachments.setTextColor(getResources().getColor(R.color.light_gray));

        rlDetails.setVisibility(View.VISIBLE);
        rlAttachments.setVisibility(View.GONE);
    }

    @OnClick(R.id.rlTaskAttachments)
    @SuppressWarnings("unused")
    public void Attachments(View view) {
        viewTaskDetails.setVisibility(View.GONE);
        viewTaskAttachments.setVisibility(View.VISIBLE);
        tvTaskDetails.setTextColor(getResources().getColor(R.color.light_gray));
        tvTaskAttachments.setTextColor(getResources().getColor(R.color.white));

        rlDetails.setVisibility(View.GONE);
        rlAttachments.setVisibility(View.VISIBLE);
    }


    @OnClick(R.id.rlUpload)
    @SuppressWarnings("unused")
    public void Upload(View view) {
//        Intent fileExploreIntent = new Intent(ActivityFileBrowser.INTENT_ACTION_SELECT_FILE, null, this,
//                ActivityFileBrowser.class);
//        startActivityForResult(fileExploreIntent, BROWSE_ACTIVITY);

        try{
//            String mimeType="text/plain,text/csv,text/xml,text/html,image/png,image/gif,image/jpg,image/jpeg,image/bmp,audio/mp3,audio/wav,audio/x-ogg,audio/mid,audio/midi,audio/AMR,video/mpeg,video/3gpp,application/pdf";
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType(mimeType);
//            startActivityForResult(intent, BROWSE_ACTIVITY);

            String mimeType="*/*";
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(mimeType);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, BROWSE_ACTIVITY);

        }catch (Exception e){e.printStackTrace();}

    }

    @OnClick(R.id.rlGallery)
    @SuppressWarnings("unused")
    public void Gallery(View view) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                SELECT_FILE);
    }

    public void onClick(View view) {
        switch ((view.getId())) {
            case R.id.btn_selectImage:
                selectImage();
                break;
            case R.id.btn_CancelImage:
                workItem.setTaskImagePath(null);
                workItem.setTaskImage(null);
                createURI();
                selectedImage.setImageDrawable(null);
                cancelImage.setVisibility(View.GONE);
                break;
        }
    }


    TimePickerDialog.OnTimeSetListener myToTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker timePicker, int i, int i2) {

            if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.REGULAR])) {

                Calendar calendarTemp=Calendar.getInstance();
                calendarTemp.setTime(endCalendar.getTime());
                calendarTemp.set(Calendar.HOUR_OF_DAY, i);
                calendarTemp.set(Calendar.MINUTE, i2);

                boolean flag=true;

                Log.e("calender", ">>" + startCalendar.getTime() + ":\n" + endCalendar.getTime() + ">>\n" + calendarTemp.getTime());
                if (startCalendar.after(calendarTemp)) {
                    flag=false;
                    Toast.makeText(getApplicationContext(), getString(R.string.end_time_should_greater_then_start), Toast.LENGTH_SHORT).show();
                }

                if (flag) {
                    endCalendar.set(Calendar.HOUR_OF_DAY, i);
                    endCalendar.set(Calendar.MINUTE, i2);
                    String sdate = Util.SDF.format(endCalendar.getTime());
                    tvEndsBy.setText(sdate.substring(11));
                    printDifference(startCalendar.getTime(), endCalendar.getTime());
                }
            } else {

                calendar = Calendar.getInstance();
                Calendar calendarTemp=Calendar.getInstance();
                calendarTemp.setTime(endCalendar.getTime());
                calendarTemp.set(Calendar.HOUR_OF_DAY, i);
                calendarTemp.set(Calendar.MINUTE, i2);

                boolean flag=true;

                Log.e("calender", ">>" + startCalendar.getTime() + ":\n" + endCalendar.getTime() + ">>\n" + calendarTemp.getTime());
                if (startCalendar.after(calendarTemp)) {
                    flag=false;
                    Toast.makeText(getApplicationContext(), getString(R.string.end_time_should_greater_then_start), Toast.LENGTH_SHORT).show();
                }
                else if (calendarTemp.before(calendar)) {
                    flag=false;
                    Toast.makeText(getApplicationContext(), getString(R.string.end_time_should_greater_then_current), Toast.LENGTH_SHORT).show();
                }

                if (flag) {
                    endCalendar.set(Calendar.HOUR_OF_DAY, i);
                    endCalendar.set(Calendar.MINUTE, i2);
                    String sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
                    tvEndsBy.setText(sdate);
                    printDifference(startCalendar.getTime(), endCalendar.getTime());
                }
            }
        }
    };

    TimePickerDialog.OnTimeSetListener myfromTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker timePicker, int i, int i2) {
            Log.e("Start TIme", "I=" + i + " i2=" + i2);
            startCalendar.set(Calendar.HOUR_OF_DAY, i);
            startCalendar.set(Calendar.MINUTE, i2);

            String sdate = Util.SDF.format(startCalendar.getTime());
            if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.REGULAR])) {
                tvStartingFrom.setText(sdate.substring(11));
            } else {
                String sdate1 = Util.ddMMMyyyy.format(startCalendar.getTime());
                tvStartingFrom.setText(sdate1);
            }

            endCalendar.setTime(startCalendar.getTime());
            if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.REGULAR])) {
                endCalendar.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY) + 1);
                endCalendar.set(Calendar.MINUTE, i2);
                sdate = Util.SDF.format(endCalendar.getTime());
                tvEndsBy.setText(sdate.substring(11));
            } else {
                endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH) + 1);
                endCalendar.set(Calendar.MINUTE, i2);
                sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
                tvEndsBy.setText(sdate);
            }

            printDifference(startCalendar.getTime(), endCalendar.getTime());
        }
    };

    private DatePickerDialog.OnDateSetListener myDueDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

            Calendar tempCalendar= Calendar.getInstance();
            tempCalendar.set(Calendar.YEAR, i);
            tempCalendar.set(Calendar.MONTH, i1);
            tempCalendar.set(Calendar.DAY_OF_MONTH, i2);

            if (tempCalendar.before(invoiceCalendar) && invoiceDateFlag) {
                Toast.makeText(getApplicationContext(), getString(R.string.due_time_should_greater_then_invoice), Toast.LENGTH_SHORT).show();
//                dueDateFlag = false;
//                tvDueDate.setText("");
            }
            else{
                dueDateCalendar.set(Calendar.YEAR, i);
                dueDateCalendar.set(Calendar.MONTH, i1);
                dueDateCalendar.set(Calendar.DAY_OF_MONTH, i2);
                String sdate = Util.sdf.format(dueDateCalendar.getTime());
                dueDate = Util.locatToUTC(sdate);
                sdate = Util.ddMMMyyyy1.format(dueDateCalendar.getTime());
                tvDueDate.setText(sdate);
                dueDateFlag = true;
            }

        }
    };
    private DatePickerDialog.OnDateSetListener myInvoiceDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

            Calendar tempCalendar= Calendar.getInstance();
            tempCalendar.set(Calendar.YEAR, i);
            tempCalendar.set(Calendar.MONTH, i1);
            tempCalendar.set(Calendar.DAY_OF_MONTH, i2);
            if (dueDateFlag && dueDateCalendar.before(tempCalendar)) {
                Toast.makeText(getApplicationContext(), getString(R.string.invoice_should_less), Toast.LENGTH_SHORT).show();
//                invoiceDateFlag = false;
//                tvInvoiceDate.setText("");
            }
            else{
                invoiceCalendar.set(Calendar.YEAR, i);
                invoiceCalendar.set(Calendar.MONTH, i1);
                invoiceCalendar.set(Calendar.DAY_OF_MONTH, i2);
                String sdate = Util.sdf.format(invoiceCalendar.getTime());
                invoiceDate = Util.locatToUTC(sdate);
                sdate = Util.ddMMMyyyy1.format(invoiceCalendar.getTime());
                tvInvoiceDate.setText(sdate);
                invoiceDateFlag = true;
            }

        }
    };

    private DatePickerDialog.OnDateSetListener mytoDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, month);
                endCalendar.set(Calendar.DAY_OF_MONTH, day);
                showDialog(END_TIME);

        }
    };
    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, day);
            showDialog(START_TIME);
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();

            try {
                Bundle extras=intent.getExtras();
                if(extras!=null) {
                    String response = extras.getString("response");
                    Log.e("response", ">>" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    toast(jsonObject.optString("message"));
                    if (jsonObject.optString("status").equals("Success")) {

                        String roleId = read(Constant.SHRED_PR.KEY_ROLE_ID);
                        if (Arrays.asList("1", "2").contains(roleId)) {
                            write(Constant.SHRED_PR.KEY_RELOAD,"1");
                            updateTask(jsonObject);
                        }
                        finish();
                        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private void Init() {
        InputFilter[] filterArray = new InputFilter[2];
        filterArray[0] = new InputFilterMinMax("00", "60");
        filterArray[1] = new InputFilter.LengthFilter(2);
        actualMins.setFilters(filterArray);
        progressDialog = new TransparentProgressDialog(EditWorkitem.this, R.drawable.progressdialog, false);

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        listAttachments.setDivider(null);
        listAttachments.setDividerHeight(0);
        attachmentAdapter = new AttachmentAdapter(EditWorkitem.this, attachmentData);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        invoiceCalendar = Calendar.getInstance();
        dueDateCalendar = Calendar.getInstance();
        calendar = Calendar.getInstance();

        selectImageBtn.setOnClickListener(this);
        cancelImage.setOnClickListener(this);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

//        "Project", "Regular", "One Time", "Service Call", "Sales Call", "Shopping/Purchase", "Collection"
        WorkTypeViews[0] = 0;
        WorkTypeViews[1] = rlRegularTypeTask.getId();
        WorkTypeViews[2] = 0;
        WorkTypeViews[3] = ll_sales.getId();
        WorkTypeViews[4] = ll_sales.getId();
        WorkTypeViews[5] = ll_purchase.getId();
        WorkTypeViews[6] = ll_collection.getId();


        ssCustomerContactET.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getText(ssCustomerContactET).startsWith("0")) {
                    ssCustomerContactET.setText("");
                    toast(getResources().getString(R.string.mobile_not_zero));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        collCustomerContactET.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getText(collCustomerContactET).startsWith("0")) {
                    collCustomerContactET.setText("");
                    toast(getResources().getString(R.string.mobile_not_zero));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void setViewAsPerWorkItem() {
        setLayoutasPerWorkType(workItem.getTaskType());
        workTitleET.setText(workItem.getTitle());
        workTitleET.setSelection(workTitleET.getText().length());
        workDescriptionET.setText(workItem.getDescription());
        workBudgetET.setText(workItem.getBudget());

        ////////For Task Priority Spinner

        if(workItem.getPriority().equals(Constant.HIGH)){
            strPriority=Constant.HIGH;

            rlHighPriority.setBackgroundResource(R.drawable.rectangle_selected);
            rlMediumPriority.setBackgroundResource(R.drawable.rectangle_unselected);
            rlLowPriority.setBackgroundResource(R.drawable.rectangle_unselected);
            tvHighPriority.setTextColor(getResources().getColor(R.color.white));
            tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
            tvLowPriority.setTextColor(getResources().getColor(R.color.gray));
        }
        else if(workItem.getPriority().equals(Constant.MEDIUM)){
            strPriority=Constant.MEDIUM;

            rlHighPriority.setBackgroundResource(R.drawable.rectangle_unselected);
            rlMediumPriority.setBackgroundResource(R.drawable.rectangle_selected);
            rlLowPriority.setBackgroundResource(R.drawable.rectangle_unselected);
            tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
            tvMediumPriority.setTextColor(getResources().getColor(R.color.white));
            tvLowPriority.setTextColor(getResources().getColor(R.color.gray));
        }
        else if(workItem.getPriority().equals(Constant.LOW)){
            strPriority=Constant.LOW;

            rlHighPriority.setBackgroundResource(R.drawable.rectangle_unselected);
            rlMediumPriority.setBackgroundResource(R.drawable.rectangle_unselected);
            rlLowPriority.setBackgroundResource(R.drawable.rectangle_selected);
            tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
            tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
            tvLowPriority.setTextColor(getResources().getColor(R.color.white));
        }


        SetCustomerSpinnerAndVales();
        try {
            startCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getStartDate())));
            endCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getEndDate())));
            if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.REGULAR])) {
                String sdate = Util.SDF.format(startCalendar.getTime());
                tvStartingFrom.setText(sdate.substring(11));
                sdate = Util.SDF.format(endCalendar.getTime());
                tvEndsBy.setText(sdate.substring(11));
            } else {
                String sdate1 = Util.ddMMMyyyy.format(startCalendar.getTime());
                String sdate2 = Util.ddMMMyyyy.format(endCalendar.getTime());
                tvStartingFrom.setText(sdate1);
                tvEndsBy.setText(sdate2);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            String times[] = workItem.getEstimatedWorkTime().split(",");
            if (times != null && times.length > 0) {
                if (times[0] != null && times[0].length() > 0)
                    actualHours.setText(times[0]);
                if (times[1] != null && times[1].length() > 0)
                    actualMins.setText(times[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            printDifference(startCalendar.getTime(), endCalendar.getTime());
        }

        // Regular Type

        if (workItem.getFrequency().equals(Constant.DAILY)) {
            strFrequency = Constant.DAILY;
            rlDaily.setBackgroundResource(R.drawable.rectangle_selected);
            rlWeekly.setBackgroundResource(R.drawable.rectangle_unselected);
            rlMonthly.setBackgroundResource(R.drawable.rectangle_unselected);
            tvDaily.setTextColor(getResources().getColor(R.color.white));
            tvWeekly.setTextColor(getResources().getColor(R.color.gray));
            tvMonthly.setTextColor(getResources().getColor(R.color.gray));

            listDays.clear();
            tvDays.setText("");
        } else if (workItem.getFrequency().equals(Constant.WEEKLY)) {

            strFrequency = Constant.WEEKLY;
            rlDaily.setBackgroundResource(R.drawable.rectangle_unselected);
            rlWeekly.setBackgroundResource(R.drawable.rectangle_selected);
            rlMonthly.setBackgroundResource(R.drawable.rectangle_unselected);
            tvDaily.setTextColor(getResources().getColor(R.color.gray));
            tvWeekly.setTextColor(getResources().getColor(R.color.white));
            tvMonthly.setTextColor(getResources().getColor(R.color.gray));

            listDays.clear();
            tvDays.setText("");
            for (int i = 0; i < StaticData.elementsDays.length; i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("day", "" + StaticData.elementsDays[i]);
                hashMap.put("selected", "0");
                hashMap.put("code", "" + i);
                listDays.add(hashMap);
            }

            String frq[] = workItem.getDayCodesSelected().split(",");
            if (frq != null && frq.length > 0) {
                for (int i = 0; i < listDays.size(); i++) {
                    for (int j = 0; j < frq.length; j++) {
                        if (frq[j].equals("" + listDays.get(i).get("code"))) {
                            listDays.get(i).put("selected", "1");
                        }
                    }
                }
            }

            String strDays = "";
            try {
                for (int i = 0; i < listDays.size(); i++) {
                    HashMap<String, String> hashMap = listDays.get(i);
                    if (hashMap.get("selected").equals("1")) {
                        if (strDays.length() == 0) strDays = hashMap.get("day");
                        else strDays += ", " + hashMap.get("day");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (strDays.length() > 0) {
                tvDays.setText("Days: " + strDays + "\n");
            } else tvDays.setText("");

        } else if (workItem.getFrequency().equals(Constant.MONTHLY)) {

            strFrequency = Constant.MONTHLY;
            rlDaily.setBackgroundResource(R.drawable.rectangle_unselected);
            rlWeekly.setBackgroundResource(R.drawable.rectangle_unselected);
            rlMonthly.setBackgroundResource(R.drawable.rectangle_selected);
            tvDaily.setTextColor(getResources().getColor(R.color.gray));
            tvWeekly.setTextColor(getResources().getColor(R.color.gray));
            tvMonthly.setTextColor(getResources().getColor(R.color.white));

            listDays.clear();
            tvDays.setText("");
            for (int i = 0; i < StaticData.elementMonth.length; i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("day", "" + StaticData.elementMonth[i]);
                hashMap.put("selected", "0");
                hashMap.put("code", "" + StaticData.elementMonth[i]);
                listDays.add(hashMap);
            }


            String frq[] = workItem.getDayCodesSelected().split(",");
            if (frq != null && frq.length > 0) {
                for (int i = 0; i < listDays.size(); i++) {
                    for (int j = 0; j < frq.length; j++) {
                        if (frq[j].equals("" + listDays.get(i).get("code"))) {
                            listDays.get(i).put("selected", "1");
                        }
                    }
                }
            }

            String strDays = "";
            try {
                for (int i = 0; i < listDays.size(); i++) {
                    HashMap<String, String> hashMap = listDays.get(i);
                    if (hashMap.get("selected").equals("1")) {
                        if (strDays.length() == 0) strDays = hashMap.get("day");
                        else strDays += ", " + hashMap.get("day");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (strDays.length() > 0) {
                tvDays.setText("Days: " + strDays + "\n");
            } else tvDays.setText("");
        }

        ///////////////

        Log.e("Assignee", ">>" + workItem.getTaskAssignedTo());
        String frq[] = workItem.getTaskAssignedTo().split(",");  // 35,45
        if (frq != null && frq.length > 0) {
            for (int i = 0; i < frq.length; i++) {
                for (int j = 0; j < listAssignee.size(); j++) {
                    if (frq[i].equals("" + listAssignee.get(j).getUser_Id())) {
                        listAssignee.get(j).setSelected(true);
                    }
                }
            }
        }
        String strAssignee = "";
        for (int i = 0; i < listAssignee.size(); i++) {
            User user = listAssignee.get(i);
                if (user.isSelected()) {
                    if (strAssignee.length() == 0)
                        strAssignee = user.getFirstName() + " " + user.getLastName();
                    else strAssignee += ", " + user.getFirstName() + " " + user.getLastName();
                }
        }
        Log.e("strAssignee", ">>" + strAssignee);
        if (strAssignee.length() > 0) {
            tvAssigneed.setText("" + strAssignee + "\n");
        } else tvAssigneed.setText("");

//        for (int i=0;i<listCC.size();i++) listCC.get(i).setSelected(false);

        //creation of CC to Work item dialogue
        Log.e("CCs", ">>" + workItem.getTaskCCTo());
        if (workItem.getTaskCCTo() != null && workItem.getTaskCCTo().length() > 0) {
            String frq1[] = workItem.getTaskCCTo().split(",");  // 35,45
            if (frq1 != null && frq1.length > 0) {
                for (int i = 0; i < frq1.length; i++) {
                    for (int j = 0; j < listCC.size(); j++) {
                        if (frq1[i].equals("" + listCC.get(j).getUser_Id())) {
                            listCC.get(j).setSelected(true);
                            Log.e("task type:", j + ">>" + workItem.getTaskType());
                            if(workItem.getTaskType().equals(""+Constant.WORKTYPES[Constant.PROJECT])){
                                listCC.get(j).setDisabled(true);
                            }else{
                                listCC.get(j).setDisabled(false);
                            }
                            Log.e("User Enable:",j+">>"+listCC.get(j).isDisabled());
                        }
                    }
                }
            }
        }

        String strCC = "";
        for (int i = 0; i < listCC.size(); i++) {
            User user = listCC.get(i);
                if (user.isSelected()) {
                    if (strCC.length() == 0) strCC = user.getFirstName() + " " + user.getLastName();
                    else strCC += ", " + user.getFirstName() + " " + user.getLastName();
                }
        }
        Log.e("strCC", ">>" + strCC);
        if (strCC.length() > 0) {
            tvCC.setText("" + strCC + "\n");
        } else tvCC.setText("");

        // Sales/ Service / collection

//        collCustomernameET.setText(workItem.getCustomerName());
//        collCustomerContactET.setText(workItem.getCustomerContact());
//        ssCustomerNameET.setText(workItem.getCustomerName());
//        ssCustomerContactET.setText(workItem.getCustomerContact());

        if (workItem.getCustomerType().equals(Constant.REPEAT)) {
            customerTypeGroup.check(R.id.rb_returningCustomer);
        } else {
            customerTypeGroup.check(R.id.rb_newCustomer);
        }
        pastHistoryET.setText(workItem.getPastHistory());
        // Purchase
        if (workItem.getSpVendorPreference().equals(Constant.MANDATORY))
            purchasePreferenceGroup.check(R.id.rb_purchaseMandatory);
        else
            purchasePreferenceGroup.check(R.id.rb_purchasePreffered);

        if (workItem.getSpVendorName().contains(",") || isNum(workItem.getSpVendorName())) {
            String str = getVendorNames(workItem.getSpVendorName());
            tvVendors.setText(""+ str);
            llVendorOthersName.setVisibility(View.GONE);
        } else {
            vendorList.get(vendorList.size()-1).setSelected(true);
            tvVendors.setText("" + getString(R.string.others));
            llVendorOthersName.setVisibility(View.VISIBLE);
            llVendorOthersName.setVisibility(View.VISIBLE);
            vendorNameET.setText(workItem.getSpVendorName());
        }

        advancePaidET.setText(workItem.getSpAdvancePaid());
        //Collection
        invoiceAmountET.setText(workItem.getInvoiceAmount());

        try {
            if (workItem.getInvoiceDate() != null && !workItem.getInvoiceDate().equals("null") && workItem.getInvoiceDate().length() > 0) {
                invoiceCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getInvoiceDate())));
                String sdate = Util.ddMMMyyyy1.format(invoiceCalendar.getTime());
                invoiceDate = workItem.getInvoiceDate();
                tvInvoiceDate.setText(sdate);
                invoiceDateFlag = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            if (workItem.getDueDate() != null && !workItem.getDueDate().equals("null") && workItem.getDueDate().length() > 0) {
                dueDateCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getDueDate())));
                String sdate = Util.ddMMMyyyy1.format(dueDateCalendar.getTime());
                dueDate = workItem.getDueDate();
                tvDueDate.setText(sdate);
                dueDateFlag = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        outstandingAmtET.setText(workItem.getOutStandingAmt());
        etAddress.setText(workItem.getWorkLocation());

        if ((workItem.getTaskImage() != null || !workItem.getTaskImage().equals("null")) && workItem.getTaskImage().length() > 5) {
            imageLoader.displayImage(workItem.getTaskImage(), selectedImage);
            cancelImage.setVisibility(View.VISIBLE);
        } else
            cancelImage.setVisibility(View.GONE);
//        tvHeaderTitle.setText(workItem.getTitle());

        Log.e("attachments",">>"+workItem.getAttachments());
        InitializeAttachments("" + workItem.getAttachments());
    }


    private void InitializeAttachments(String attachmentlist) {

        try{
            if(attachmentlist.length()>0){
                if(attachmentlist.endsWith(",")) attachmentlist=attachmentlist.substring(0,attachmentlist.length()-1);
            }

            Log.e("attachments",">>"+attachmentlist);

            attachmentData.clear();

            Cursor crsr = db.rawQuery("select * from " + Constant.AttachmentTable + " where " + Attachment.ATTACHMENT_ID + " IN (" + attachmentlist + ")", null);
            Log.e("Attachment:", "Count=" + crsr.getCount());
            if (crsr != null && crsr.getCount() > 0) {
                crsr.moveToFirst();
                do {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("file_path", "" + crsr.getString(crsr.getColumnIndex(Attachment.PATH)));
                    hashMap.put("uploaded", "1");
                    hashMap.put("attachmentid", ""+crsr.getString(crsr.getColumnIndex(Attachment.ATTACHMENT_ID)));
                    attachmentData.add(hashMap);

                } while (crsr.moveToNext());
            }
            if(crsr!=null) crsr.close();

            attachmentAdapter = new AttachmentAdapter(EditWorkitem.this, attachmentData);
            listAttachments.setAdapter(attachmentAdapter);
        }catch (Exception e){e.printStackTrace();}

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

                    for (int j=0;j<vendorList.size();j++){
                            if(vendorList.get(j).getID().toString().equals(cursor.getString(cursor.getColumnIndex("ID")))){
                            vendorList.get(j).setSelected(true);
                        }
                    }

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


    private void SetCustomerSpinnerAndVales() {
        //@#$ For SS Customer Name Spinner
        ArrayAdapter CustomerNameAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, customersString
        );
        CustomerNameAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        ssCustomerNameSpinner.setAdapter(CustomerNameAdapter);
        ssCustomerNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (customersString[position] != null && customersString[position].equals(getString(R.string.others))) {
                    llSSOthersName.setVisibility(View.VISIBLE);
                    if (!isFirstTime) {
                        ssCustomerNameET.setText("");
                        ssCustomerContactET.setText("");
                        ssCustomerContactET.setEnabled(true);
                    }
                } else {
                    llSSOthersName.setVisibility(View.GONE);
                    if (customerList != null && customerList.get(position) != null) {
                        ssCustomerContactET.setText(customerList.get(position).getMobileNo());
                        ssCustomerContactET.setEnabled(false);
                    }
                }
                isFirstTime = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //@#$ For Col Customer Name Spinner
        ColCustomerNameSpinner.setAdapter(CustomerNameAdapter);
        ColCustomerNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (customersString[position].equals(getString(R.string.others))) {
                    llColOthersName.setVisibility(View.VISIBLE);
                    if(!isFirstTime) {
                        collCustomernameET.setText("");
                        collCustomerContactET.setText("");
                        collCustomerContactET.setEnabled(true);
                    }
                } else {
                    llColOthersName.setVisibility(View.GONE);
                    collCustomerContactET.setText(customerList.get(position).getMobileNo());
                    collCustomerContactET.setEnabled(false);
                }
                isFirstTime=false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (isNum(workItem.getCustomerName())) {
            for (int i = 0; i < customerList.size(); i++) {

                if (Integer.parseInt(workItem.getCustomerName()) == customerList.get(i).getID()) {
                    ssCustomerNameSpinner.setSelection(i);
                    ssCustomerNameET.setText(customerList.get(i).getName());
                    ssCustomerContactET.setText(customerList.get(i).getMobileNo());
                    ColCustomerNameSpinner.setSelection(i);
                    collCustomernameET.setText(customerList.get(i).getName());
                    collCustomerContactET.setText(customerList.get(i).getMobileNo());

                }
            }
        } else {
            ssCustomerNameSpinner.setSelection(customersString.length - 1);
            ColCustomerNameSpinner.setSelection(customersString.length - 1);

            ssCustomerNameET.setText(workItem.getCustomerName());
            ssCustomerContactET.setText(workItem.getCustomerContact());
            collCustomernameET.setText(workItem.getCustomerName());
            collCustomerContactET.setText(workItem.getCustomerContact());
        }

    }

    private void setLayoutasPerWorkType(String workItemType) {
        int type = 1;
        for (int i = 0; i < Constant.WORKTYPES.length; i++) {
            if (workItemType.equals(Constant.WORKTYPES[i])) {
                type = i;
            }
        }
        for (int j = 0; j < WorkTypeViews.length; j++)
            if (WorkTypeViews[j] != 0)
                findViewById(WorkTypeViews[j]).setVisibility(View.GONE);
        if (WorkTypeViews[type] != 0) {
            findViewById(WorkTypeViews[type]).setVisibility(View.VISIBLE);
        }

        if (type == Constant.REGULAR) {
            String sdate = Util.SDF.format(startCalendar.getTime()).substring(11);
            tvStartingFrom.setText(sdate);
            sdate = Util.SDF.format(endCalendar.getTime()).substring(11);
            tvEndsBy.setText(sdate);
        } else {
            String sdate1 = Util.ddMMMyyyy.format(startCalendar.getTime());
            String sdate2 = Util.ddMMMyyyy.format(endCalendar.getTime());
            tvStartingFrom.setText(sdate1);
            tvEndsBy.setText(sdate2);
        }

        if (type == Constant.PROJECT) {
            setProjectWorkDialogue();
        }
    }

    private void setProjectWorkDialogue() {
        rlDependentTask.setVisibility(View.VISIBLE);
        projectWorkList = new ArrayList<>();
        projectWorkList.clear();
        Cursor cursor = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.PROJECT_CODE + " = " + "" + workItem.getProjectCode() + " AND " + WorkItem.STATUS + " ='Approved'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    if (cursor.getInt(cursor.getColumnIndex(WorkItem.TASK_ID)) != workItem.getTaskCode()) {
                        WorkItem work = new WorkItem();
                        work.setTaskCode(cursor.getInt(cursor.getColumnIndex(WorkItem.TASK_ID)));
                        work.setTitle(cursor.getString(cursor.getColumnIndex(WorkItem.TASK_NAME)));
                        work.setStartDate(cursor.getString(cursor.getColumnIndex(WorkItem.START_DATE)));
                        work.setEndDate(cursor.getString(cursor.getColumnIndex(WorkItem.END_DATE)));
                        work.setSelected(false);

                        projectWorkList.add(work);
                    }
                } while (cursor.moveToNext());
            }
        }
        if(cursor!=null) cursor.close();
        final int noofWork = projectWorkList.size();

        String frq[] = workItem.getTaskCodeAfter().split(",");
        if (frq != null && frq.length > 0) {
            for (int i = 0; i < frq.length; i++) {
                for (int j = 0; j < projectWorkList.size(); j++) {
                    if (frq[i].equals("" + projectWorkList.get(j).getTaskCode()))
                        projectWorkList.get(j).setSelected(true);
                }
            }
        }

        String strDependTasks = "";
        for (int i = 0; i < projectWorkList.size(); i++) {
            WorkItem workItem = projectWorkList.get(i);
            if (workItem.isSelected()) {
                if (strDependTasks.length() == 0) strDependTasks = workItem.getTitle();
                else strDependTasks += ", " + workItem.getTitle();
            }
        }

        if (strDependTasks.length() > 0) {
            tvTasksList.setText("" + strDependTasks + "\n");
        } else tvTasksList.setText("");

    }

    public void printDifference(Date fromDate, Date endDate) {
        //milliseconds
        String sTime = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_STARTTIME);
        String eTime = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ENDTIME);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date CompStartTime = null, CompEndtime = null;
        try {
            CompStartTime = simpleDateFormat.parse(sTime);
            CompEndtime = simpleDateFormat.parse(eTime);
            System.out.println("Company Time: " + simpleDateFormat.format(CompStartTime) + " TO :" + simpleDateFormat.format(CompEndtime));

        } catch (ParseException ex) {
            System.out.println("Exception " + ex);
        }

        ///////////////////////////

        if (fromDate.getHours() < CompStartTime.getHours()) {
            fromDate.setHours(CompStartTime.getHours());
//            if (fromDate.getMinutes() < CompStartTime.getMinutes())
            fromDate.setMinutes(CompStartTime.getMinutes());
        }
        if (fromDate.getHours() >= CompEndtime.getHours()) {
            fromDate.setHours(CompEndtime.getHours());
//            if (fromDate.getMinutes() > CompEndtime.getMinutes())
            fromDate.setMinutes(CompEndtime.getMinutes());
        }
        if (endDate.getHours() < CompStartTime.getHours()) {
            endDate.setHours(CompStartTime.getHours());
//            if (endDate.getMinutes() < CompStartTime.getMinutes())
            endDate.setMinutes(CompStartTime.getMinutes());
        }
        if (endDate.getHours() >= CompEndtime.getHours()) {
            endDate.setHours(CompEndtime.getHours());
//            if (endDate.getMinutes() > CompEndtime.getMinutes())
            endDate.setMinutes(CompEndtime.getMinutes());
        }

        long different = endDate.getTime() - fromDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long Days = different / daysInMilli;
        different = different % daysInMilli;

        long Hours = different / hoursInMilli;
        different = different % hoursInMilli;

        long Minutes = different / minutesInMilli;
//        Log.e("TIME", "FOUND: " + Hours + ": " + Minutes);
//        Log.e("CAL", "days:" + Days + " hrs :" + Hours + " Mins:" + Minutes);
//        different = different % minutesInMilli;
        float workingHrs = (float) Util.getWorkingHours(CompStartTime, CompEndtime);
        if (Hours > CompEndtime.getHours()) Hours = (long) (Hours - (24 - workingHrs));
        float tempHr = 0;
        if (Days > 0)
            tempHr = Days * workingHrs;

        if (!(tempHr == Math.round(tempHr))) {
            tempHr = (float) Math.floor(tempHr);
            Minutes = Minutes + 30;
            if (Minutes == 60) {
                Minutes = 0;
                Hours = Hours + 1;
            }
        }
        actualHours.setText("" + (((int) tempHr) + Hours));
        actualMins.setText("" + Minutes);

        String hh = actualHours.getText().toString();
        String mm = actualMins.getText().toString();
        hh = hh.replace("-", "");
        mm = mm.replace("-", "");
        actualHours.setText(hh);
        actualMins.setText(mm);

        if (hh.length() == 0) hh = "0";
        if (mm.length() == 0) mm = "0";
        workItem.setEstimatedWorkTime(hh + "," + mm);
    }


    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case END_DATE:
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, mytoDateListener, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DATE));
            case START_DATE:
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, myfromDateListener, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DATE));
            case END_TIME:
                return new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, myToTimeListener, endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE), false);
            case START_TIME:
                return new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, myfromTimeListener, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE), false);
            case INVOICE_DATE:
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, myInvoiceDateListener, invoiceCalendar.get(Calendar.YEAR), invoiceCalendar.get(Calendar.MONTH), invoiceCalendar.get(Calendar.DATE));
            case DUE_DATE:
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, myDueDateListener, dueDateCalendar.get(Calendar.YEAR), dueDateCalendar.get(Calendar.MONTH), dueDateCalendar.get(Calendar.DATE));
        }
        return null;
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditWorkitem.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    startCameras();
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void startCameras() {
        if (globalUri != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, globalUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            createURI();
        }
    }

    private void createURI() {
        Random rand = new Random();
        String fileName = "workItem" + rand.nextInt(Integer.MAX_VALUE) + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        globalUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case BROWSE_ACTIVITY:
//                    String newFile = data.getStringExtra(
//                            ActivityFileBrowser.returnFileParameter);
//                    AddAttachment(newFile);

                    if (null == data) return;
                    Uri uri = data.getData();
                    String file = ImageFilePath.getPath(getApplicationContext(), uri);
                    if (Arrays.asList("txt","csv","xml","html","png","gif","jpg","jpeg",
                            "bmp","mp3","wav","ogg","x-ogg","mid","midi","AMR","mpeg",
                            "3gpp","mkv","mp4","pdf").contains(Util.getFilenameType(file))) {
                        AddAttachment(file);
                    }else {
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

                case REQUEST_CAMERA:
                    Log.i("globalUri", ">>" + globalUri.getPath());
//                    doCrop();
                    String imageId = convertImageUriToFile(globalUri, CameraActivity);
                    new LoadImagesFromSDCard().execute("" + imageId);
                    break;


            }
        }
    }

    public void AddAttachment(String path) {
        if (path != null && path.length() > 4) {
            if (Util.checkFileSize(path)) {
                if (Util.isOnline(getApplicationContext())) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("file_path", "" + path);
                    hashMap.put("uploaded", "0");
                    hashMap.put("attachmentid", "0");
                    attachmentData.add(hashMap);
                    attachmentAdapter = new AttachmentAdapter(EditWorkitem.this, attachmentData);
                    listAttachments.setAdapter(attachmentAdapter);
                    new uploadFile(path).execute();
                } else {
                    toast(getResources().getString(R.string.network_error));
                }
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.image_size_exceeds_limit), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void GetUserList() {

        listUser.clear();
        listAssignee.clear();
        listCC.clear();

        Cursor cursor = db.rawQuery("select * from " + Constant.UserTable, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    User user = new User();
                    user.setUser_Id(cursor.getInt(cursor.getColumnIndex(User.USER_ID)));
                    user.setFirstName(cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)));
                    user.setLastName(cursor.getString(cursor.getColumnIndex(User.LAST_NAME)));
                    user.setUser_Image(cursor.getString(cursor.getColumnIndex(User.USER_IMAGE)));
                    user.setSelected(false);
                    //Log.e("User", "id :" + tempObj.getUser_Id() + " name:" + tempObj.getFirstName() + " " + tempObj.getLastName());
                    listUser.add(user);

                    User user1 = new User();
                    user1.setUser_Id(cursor.getInt(cursor.getColumnIndex(User.USER_ID)));
                    user1.setFirstName(cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)));
                    user1.setLastName(cursor.getString(cursor.getColumnIndex(User.LAST_NAME)));
                    user1.setUser_Image(cursor.getString(cursor.getColumnIndex(User.USER_IMAGE)));
                    user1.setSelected(false);
                    listAssignee.add(user1);

                    User user2 = new User();
                    user2.setUser_Id(cursor.getInt(cursor.getColumnIndex(User.USER_ID)));
                    user2.setFirstName(cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)));
                    user2.setLastName(cursor.getString(cursor.getColumnIndex(User.LAST_NAME)));
                    user2.setUser_Image(cursor.getString(cursor.getColumnIndex(User.USER_IMAGE)));
                    user2.setSelected(false);
                    listCC.add(user2);
                } while (cursor.moveToNext());
            }
        }
        if(cursor!=null) cursor.close();
        noofMembers = listUser.size();
    }

    private void startSubmittingItem() {
        if (checkValues()) {
            PrepareObj();
            uploadData();
        }
    }

    private Boolean checkValues() {
        if (workTitleET.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, getString(R.string.enter_title), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (workDescriptionET.getText().toString().trim().length() <= 2) {
            Toast.makeText(this, getString(R.string.description_length_should_be_minimum_of_three_chars), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endCalendar.before(startCalendar)) {
            Toast.makeText(this, getString(R.string.check_start_and_end_time), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (workItem.getTaskImagePath() != null && !workItem.getTaskImagePath().equals("null")) {
            if (!Util.checkFileSize(workItem.getTaskImagePath())) {
                Toast.makeText(this, getString(R.string.image_size_exceeds_limit), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        boolean assigned = false;
        for (int i = 0; i < listAssignee.size(); i++) {
            if (listAssignee.get(i).isSelected()) {
                assigned = true;
            }
        }
        if (!assigned) {
            Toast.makeText(this, getString(R.string.assing_work_item), Toast.LENGTH_SHORT).show();
            return false;
        }

        //////////////////
        if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.COLLECTION]))  //Because for Collection and Sales/service customer name and no are common fields in obj.
        {
            if (ColCustomerNameSpinner.getSelectedItem().equals(getString(R.string.others))) {
                if (collCustomernameET.getText().toString().length() == 0) {
                    Toast.makeText(this, getString(R.string.select_customer), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (getText(collCustomerContactET).length() != 10) {
                    toast(R.string.PleaseEnterCustomerMobile);
                    return false;
                }
            }

            if(dueDateFlag){
                if(!invoiceDateFlag){
                    toast(R.string.PleaseEnterInvoice);
                    return false;
                }
            }

        }
        if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.SERVICE_CALL]) || workItem.getTaskType().equals(Constant.WORKTYPES[Constant.SALES_CALL])) {
            if (ssCustomerNameSpinner.getSelectedItem().equals(getString(R.string.others))) {
                if (ssCustomerNameET.getText().toString().length() == 0) {
                    Toast.makeText(this, getString(R.string.select_customer), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if (getText(ssCustomerContactET).length() != 10) {
                toast(R.string.PleaseEnterCustomerMobile);
                return false;
            }

        }

        if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.SHOPPING_PURCHASE])) {
            if (vendorList.get(vendorList.size()-1).isSelected()) {
                if (vendorNameET.getText().toString().length() == 0) {
                    Toast.makeText(this, getString(R.string.enter_vendor), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                boolean flag=true;
                for (int i = 0; i < vendorList.size(); i++) {
                    if (vendorList.get(i).isSelected()) flag=false;
                }
                if(flag) {
                    Toast.makeText(this, getString(R.string.enter_vendor), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        if (!workItem.getTaskType().equals(Constant.WORKTYPES[Constant.REGULAR])) {
//            calendar = Calendar.getInstance();
//            if (endCalendar.before(calendar)) {
//                Toast.makeText(this, getString(R.string.end_time_should_greater_then_current), Toast.LENGTH_SHORT).show();
//                return false;
//            }
        } else {
            Log.e("strFrequency", ">>" + strFrequency);
            boolean flag = true;
            if (strFrequency.equals(Constant.WEEKLY) || strFrequency.equals(Constant.MONTHLY)) {
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i).get("selected").equals("1")) flag = false;
                }
            } else flag = false;

            if (flag) {
                Toast.makeText(this, getString(R.string.select_day_for_regulartask), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if(!startCalendar.before(endCalendar)){
            Toast.makeText(getApplicationContext(), getString(R.string.end_time_should_greater_then_start), Toast.LENGTH_SHORT).show();
            return  false;
        }

        for (int i = 0; i < attachmentData.size(); i++) {
            if (attachmentData.get(i).get("uploaded").equals("0")) {
                Toast.makeText(this, getString(R.string.wait_for_attachment), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void PrepareObj() {
        workItem.setTitle(workTitleET.getText().toString());
        workItem.setDescription(workDescriptionET.getText().toString());

        if (workBudgetET.getText().toString().length() > 0)
            workItem.setBudget(workBudgetET.getText().toString());
        else
            workItem.setBudget("0");

//////////////////

        String assignWork = "";
        for (int i = 0; i < listAssignee.size(); i++) {
            User user = listAssignee.get(i);
            if (user.isSelected()) {
                if (assignWork.length() == 0) assignWork = "" + user.getUser_Id();
                else assignWork += "," + user.getUser_Id();
            }
        }
        workItem.setTaskAssignedTo(assignWork);

        String ccWork = "";
        for (int i = 0; i < listCC.size(); i++) {
            User user = listCC.get(i);
            if (user.isSelected()) {
                if (ccWork.length() == 0) ccWork = "" + user.getUser_Id();
                else ccWork += "," + user.getUser_Id();
            }
        }
        workItem.setTaskCCTo(ccWork);

        ////////////////
        workItem.setPriority(strPriority);
        String sdate = Util.locatToUTC(Util.sdf.format(startCalendar.getTime()));
        workItem.setStartDate(sdate);
        String edate = Util.locatToUTC(Util.sdf.format(endCalendar.getTime()));
        workItem.setEndDate(edate);

        String hh = actualHours.getText().toString();
        String mm = actualMins.getText().toString();
        if (hh.length() == 0) hh = "0";
        if (mm.length() == 0) mm = "0";
        workItem.setEstimatedWorkTime(hh + "," + mm);

        // Regular Type
        String taskCodes = "";
        for (int i = 0; i < listDays.size(); i++) {
            if (listDays.get(i).get("selected").equals("1")) {
                if (taskCodes.length() == 0) taskCodes = "" + listDays.get(i).get("code");
                else taskCodes += "," + listDays.get(i).get("code");
            }
        }
        workItem.setFrequency(strFrequency);
        workItem.setDayCodesSelected(taskCodes);

        if (invoiceAmountET.getText().length() > 0)
            workItem.setInvoiceAmount(invoiceAmountET.getText().toString());
        else
            workItem.setInvoiceAmount("0");

        // Sales/ Service
        if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.COLLECTION]))  //Because for Collection and Sales/service customer name and no are common fields in obj.
        {
            if (ColCustomerNameSpinner.getSelectedItem().equals(getString(R.string.others))) {
                workItem.setCustomerName(collCustomernameET.getText().toString());
                workItem.setCustomerContact(collCustomerContactET.getText().toString());
            } else if (customerList.size() > 0) {
                workItem.setCustomerName("" + customerList.get(ColCustomerNameSpinner.getSelectedItemPosition()).getID());
                workItem.setCustomerContact("" + customerList.get(ColCustomerNameSpinner.getSelectedItemPosition()).getMobileNo());
            }

        } else {
            if (ssCustomerNameSpinner.getSelectedItem() != null
                    && ssCustomerNameSpinner.getSelectedItem().equals(getString(R.string.others))) {
                workItem.setCustomerName(ssCustomerNameET.getText().toString());
                workItem.setCustomerContact(ssCustomerContactET.getText().toString());
            } else if (customerList.size() > 0) {
                workItem.setCustomerName("" + customerList.get(ssCustomerNameSpinner.getSelectedItemPosition()).getID());
                workItem.setCustomerContact("" + customerList.get(ssCustomerNameSpinner.getSelectedItemPosition()).getMobileNo());
            }
        }
        if (customerTypeGroup.getCheckedRadioButtonId() == R.id.rb_returningCustomer) {
            workItem.setCustomerType(Constant.REPEAT);
        } else {
            workItem.setCustomerType(Constant.NEW);
        }
        workItem.setPastHistory(pastHistoryET.getText().toString());
        // Purchase
        if (purchasePreferenceGroup.getCheckedRadioButtonId() == R.id.rb_purchaseMandatory) {
            workItem.setSpVendorPreference(Constant.MANDATORY);
        } else
            workItem.setSpVendorPreference(Constant.PREFERRED);

//        workItem.setSpVendorName(vendorNameET.getText().toString());
        if (vendorList.get(vendorList.size()-1).isSelected()) {  // for others
            workItem.setSpVendorName(vendorNameET.getText().toString());
        } else {
            String str = "";
            for (int i = 0; i < vendorList.size(); i++) {
                {
                    if (vendorList.get(i).isSelected()) {
                        str = str + vendorList.get(i).getID() + ",";
                    }
                }
            }
            workItem.setSpVendorName(str);
        }

        if (advancePaidET.getText().length() > 0)
            workItem.setSpAdvancePaid(advancePaidET.getText().toString());
        else
            workItem.setSpAdvancePaid("0");
        //Collection
        if (invoiceDate.length() > 0 && invoiceDateFlag)
            workItem.setInvoiceDate(invoiceDate);
        else  workItem.setInvoiceDate("");

//        else
//            workItem.setInvoiceDate(sdate);
        if (dueDate.length() > 0 && dueDateFlag)
            workItem.setDueDate(dueDate);
        else  workItem.setDueDate("");

        if (outstandingAmtET.getText().length() > 0)
            workItem.setOutStandingAmt(outstandingAmtET.getText().toString());
        else
            workItem.setOutStandingAmt("0");

        workItem.setLongitude("0");
        workItem.setLatitude("0");
        workItem.setWorkLocation(etAddress.getText().toString());
        if (workItem.getTaskCodeAfter() == null || workItem.getTaskCodeAfter().equals("null")) {
            workItem.setTaskCodeAfter("");
        } else {
//            String workCodes = "";
//            for (int i = 0; i < projectWorkList.size(); i++) {
//                {
//                    if (workItemsSelected[i])
//                        workCodes = workCodes + projectWorkList.get(i).getTaskCode() + ",";
//                }
//            }
//            workItem.setTaskCodeAfter(workCodes);

            String workCodes = "";
            for (int i = 0; i < projectWorkList.size(); i++) {
                WorkItem workItem = projectWorkList.get(i);
                if (workItem.isSelected()) {
                    if (workCodes.length() == 0) workCodes = "" + workItem.getTaskCode();
                    else workCodes += "," + workItem.getTaskCode();
                }
            }

            if (workCodes.length() > 0)
                workItem.setTaskCodeAfter(workCodes);
            else workItem.setTaskCodeAfter("");

        }

        //Attachment:
        String attachment="";
        for (int i = 0; i < attachmentData.size(); i++) {
            attachment = attachment + attachmentData.get(i).get("attachmentid") + ",";
        }
        workItem.setAttachments(attachment);

        //workItem.setUserCode(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID)));
        WorkItem.PrintWorkItem(workItem);
    }

    private void uploadData() {

            //  new UploadFile(workItem.getTaskImagePath(), workItem.getTaskCode(), workItem.getUserCode(), 0, false).execute();
            HashMap<String, String> localmap = new HashMap<String, String>();
            localmap.put("workitemid", "" + workItem.getTaskCode());
            localmap.put("userid", "" + workItem.getUserCode());
            localmap.put("companyid", "" + Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ID));
            localmap.put("name", "" + workItem.getTitle());
            localmap.put("description", "" + workItem.getDescription());
            localmap.put("budget", workItem.getBudget());
            localmap.put("priority", workItem.getPriority());

            localmap.put("location", workItem.getWorkLocation());
            localmap.put("longitude", "" + 0.0);
            localmap.put("latitude", "" + 0.0);

            localmap.put("startdatetime", workItem.getStartDate());
            localmap.put("enddatetime", workItem.getEndDate());
            localmap.put("estimatedtime", workItem.getEstimatedWorkTime());
            localmap.put("tolist", workItem.getTaskAssignedTo());
            localmap.put("cclist", workItem.getTaskCCTo());
            localmap.put("worktype", workItem.getTaskType());
            localmap.put("frequency", workItem.getFrequency());
            localmap.put("daycodes", workItem.getDayCodesSelected());
            if (workItem.getInvoiceDate() != null && workItem.getInvoiceDate().length() > 6)
                localmap.put("invoicedate", workItem.getInvoiceDate());
            if (workItem.getDueDate() != null && workItem.getDueDate().length() > 6)
                localmap.put("duedate", workItem.getDueDate());
            localmap.put("invoiceamt", workItem.getInvoiceAmount());
            localmap.put("outstandingamt", workItem.getOutStandingAmt());
            localmap.put("customername", workItem.getCustomerName());
            localmap.put("customerno", workItem.getCustomerContact());
            localmap.put("customertype", workItem.getCustomerType());
            localmap.put("vendorpreferrance", workItem.getSpVendorPreference());
            localmap.put("advancepaid", workItem.getSpAdvancePaid());
            localmap.put("vendorname", workItem.getSpVendorName());
            localmap.put("vendorno", "");
            localmap.put("pasthistory", workItem.getPastHistory());
            localmap.put("projectid", "" + workItem.getProjectCode());
            localmap.put("dependenton", workItem.getTaskCodeAfter());
            localmap.put("imageURL", workItem.getTaskImage());

            HashMap<String, String> receiveMap = new HashMap<String, String>();
            receiveMap.put("id", "" + 107);
            receiveMap.put("workItemimage", workItem.getTaskImage());
            receiveMap.put("attachmentpathdata", "");

        String t = "";
        for (int i = 0; i < attachmentData.size(); i++) {
            t = t + attachmentData.get(i).get("attachmentid") + ",";
        }
        localmap.put("attachmentlist", t);

            if (workItem.getTaskImage() != null && workItem.getTaskImage().contains("http"))
                workItem.setTaskImagePath("");

//            if (Util.isOnline(getApplicationContext()))
//                app.CallApi(workItem.getTaskImagePath(), localmap, receiveMap, Constant.URL + "modifyWorkItem");
//
//            if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.PROJECT])) {
//                if (progressDialog != null) progressDialog.show();
//            } else {
//                finish();
//                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_in_left);
//            }

            if(Util.isOnline(getApplicationContext())) {
                EditTask editTask = new EditTask(workItem.getTaskImagePath(), localmap, receiveMap, Constant.URL + "modifyWorkItem", db, getApplicationContext(), 0);
                editTask.execute();
            }
            else{
                if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.PROJECT])) {
                    toast(getResources().getString(R.string.network_error));
                } else {
                    EditTask editTask = new EditTask(workItem.getTaskImagePath(), localmap, receiveMap, Constant.URL + "modifyWorkItem", db, getApplicationContext(), 0);
                    editTask.execute();
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                }
            }

    }

    public String convertImageUriToFile(Uri imageUri, Activity activity) {
        int imageID = 0;
        try {
            /*********** Which columns values want to get *******/
            String[] proj = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };
            cursor = activity.managedQuery(
                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );
            //  Get Query Data
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            int size = cursor.getCount();
            /*******  If size is 0, there are no images on the SD Card. *****/
            if (size == 0) {
            } else {

                int thumbID = 0;
                if (cursor.moveToFirst()) {
                    /**************** Captured image details ************/
                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    imageID = cursor.getInt(columnIndex);
                    thumbID = cursor.getInt(columnIndexThumb);
                    String Path = cursor.getString(file_ColumnIndex);
                    camera_pathname = Path;

                    //String orientation =  cursor.getString(orientation_ColumnIndex);
                    String CapturedImageDetails = " CapturedImageDetails : \n\n"
                            + " ImageID :" + imageID + "\n"
                            + " ThumbID :" + thumbID + "\n"
                            + " Path :" + Path + "\n";
                    // Show Captured Image detail on activity
                    //imageDetails.setText( CapturedImageDetails );
                }
            }
        } finally {
            /*if (cursor != null) {
                cursor.close();
			}*/
        }
        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )
        return "" + imageID;
    }

    private void getVendorsList() {
        vendorList.clear();
        Cursor cursor = db.rawQuery("select * from " + Constant.tableVendors, null);
        int i = 0;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Customer vendor = new Customer();
                    vendor.setName(cursor.getString(cursor.getColumnIndex("Name")));
                    vendor.setMobileNo(cursor.getString(cursor.getColumnIndex("MobileNo")));
                    vendor.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                    vendor.setSelected(false);
                    Log.e("Vendor", "id :" + vendor.getID() + " name:" + vendor.getName() + " " + vendor.getMobileNo());
                    vendorList.add(vendor);
                } while (cursor.moveToNext());
            }
        }
        if(cursor!=null) cursor.close();

        Customer vendor = new Customer();
        vendor.setName(getString(R.string.others));
        vendor.setMobileNo("");
        vendor.setID(0);
        vendor.setSelected(false);
        vendorList.add(vendor);
        llVendorOthersName.setVisibility(View.GONE);

    }

    private void getCustomerList() {
        customerList = new ArrayList<>();
        customersString = new String[1];

        Cursor cursor = db.rawQuery("select * from " + Constant.tableCustomers, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                customersString = new String[cursor.getCount() + 1];
                int i = 0;
                cursor.moveToFirst();
                do {
                    Customer customer = new Customer();
                    customer.setName(cursor.getString(cursor.getColumnIndex("Name")));
                    customer.setMobileNo(cursor.getString(cursor.getColumnIndex("MobileNo")));
                    customer.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                    Log.e("CST", customer.getName() + " " + customer.getID());

                    customersString[i] = customer.getName();
                    i++;
//                    Log.e("Customer", "id :" + customer.getID() + " name:" + customer.getName() + " Phone:" + customer.getMobileNo());
                    customerList.add(customer);
                } while (cursor.moveToNext());
                customersString[i] = getString(R.string.others);
            } else
                customersString[0] = getString(R.string.others);
        }
        if(cursor!=null) cursor.close();
    }

    private boolean isNum(String customerName) {
        try {
            int x = Integer.parseInt(customerName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //////////////////////
//    private void doCrop() {
//        Log.e("Image", "Do CRop Called");
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setType("image/*");
//        List<ResolveInfo> list = getApplicationContext().getPackageManager().queryIntentActivities(
//                intent, 0);
//        int size = list.size();
//        if (size == 0) {
//            Toast.makeText(getApplicationContext(), "Can not find image crop app",
//                    Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            intent.setData(globalUri);
//            intent.putExtra("outputX", 200);
//            intent.putExtra("outputY", 200);
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            intent.putExtra("scale", true);
//            intent.putExtra("return-data", true);
//            Intent i = new Intent(intent);
//            ResolveInfo res = list.get(0);
//            i.setComponent(new ComponentName(res.activityInfo.packageName,
//                    res.activityInfo.name));
//            startActivityForResult(i, CROP_FROM_CAMERA);
//        }
//    }

    public class LoadImagesFromSDCard extends AsyncTask<String, Void, Bitmap> {
        protected void onPreExecute() {
            /****** NOTE: You can call UI Element here. *****/
        }

        // Call after onPreExecute method
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;
            try {
                uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);
                /**************  Decode an input stream into a bitmap. *********/
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                if (bitmap != null) {
                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/
                    newBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                    bitmap.recycle();
                }
            } catch (IOException e) {
                // Error fetching image, try to recover
                /********* Cancel execution of this workItem. **********/
                cancel(true);
            }
            return newBitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            // NOTE: You can call UI Element here.
            // Close progress dialog
            if (bitmap != null) {
                workItem.setTaskImagePath(camera_pathname);
                workItem.setTaskImage("file://" + camera_pathname);
                Log.i("TAG", "After File Created  " + camera_pathname);
                selectedImage.setImageBitmap(bitmap);
            }
        }
    }

    class uploadFile extends AsyncTask<Void, String, String> {

        String filePath;

        private uploadFile(String filePath) {
            this.filePath = filePath;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            String resp = "";
            try {

                JSONObject jsonUserDetails = new JSONObject();
                jsonUserDetails.put("userid", read(Constant.SHRED_PR.KEY_USERID));

                HttpClient client = new DefaultHttpClient();
                HttpResponse response = null;
                HttpPost poster = new HttpPost(
                        Constant.URL + "addAttachment");

                FileBody fbody = null;
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                if (filePath.length() > 0) {
                    File image = new File(filePath);
                    fbody = new FileBody(image);
                    entity.addPart("file", fbody);
                }

                entity.addPart("JSON", new StringBody("" + jsonUserDetails));
                entity.addPart("TokenID", new StringBody(read(Constant.SHRED_PR.KEY_TOKEN)));
                entity.addPart("UserID", new StringBody(read(Constant.SHRED_PR.KEY_USERID)));

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
            Log.e("Resp Upload 5555", "" + resp);
            return resp;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            Log.e("result", ">>" + result);

            try {

                boolean flag = false;
                int index = 0;
                for (int i = 0; i < attachmentData.size(); i++) {
                    if (attachmentData.get(i).get("file_path").equals(filePath)) {
                        flag = true;
                        index = i;
                    }
                }

                if(flag) {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    if (status.equals("Success")) {
                        attachmentData.get(index).put("uploaded", "1");
                        attachmentData.get(index).put("file_path", "" + new JSONObject(jsonObject.optString("data")).optString("path"));
                        attachmentData.get(index).put("attachmentid", "" + new JSONObject(jsonObject.optString("data")).optString("attachmentid"));
                        attachmentAdapter.notifyDataSetChanged();
                        //insert into local
                        WorkItem_GCM.HandleAttachment(jsonObject, getApplicationContext());
                    } else {
                        toast("" + jsonObject.optString("message"));
                        attachmentAdapter.delete(index);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    class EditTask extends AsyncTask<Void, String, String> {
        String FilePath;
        HashMap<String, String> hashMap, localMap;
        String ApiName;

        SQLiteDatabase db;
        Context context;
        int offLineId = 0;

        public EditTask(String Path, HashMap<String, String> hMap, HashMap<String, String> lMap, String API, SQLiteDatabase db, Context context, int offLineid) {
            this.FilePath = Path;
            this.hashMap = hMap;
            this.localMap = lMap;
            this.context = context;
            this.db = db;
            this.offLineId = offLineid;
            ApiName = API;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(progressDialog!=null) progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resp = "";
            Log.e("File Api", "Started Background Task");

            if (Util.isOnline(context)) {
                Log.e("Calling Api", ApiName);
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = null;
                    HttpPost poster = new HttpPost(ApiName);

                    FileBody fbody = null;
                    MultipartEntity entity = new MultipartEntity(
                            HttpMultipartMode.BROWSER_COMPATIBLE);

                    Log.i("filePath", "" + FilePath);
                    if (FilePath != null && FilePath.length() > 5) {
                        File image = new File(FilePath);
                        fbody = new FileBody(image);
                        entity.addPart("file", fbody);
                    }
                    Log.e("Api Calling :", hashMap.toString());

                    entity.addPart("JSON", new StringBody(Util.prepareJsonString(hashMap)));
                    entity.addPart("TokenID", new StringBody(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_TOKEN)));
                    entity.addPart("UserID", new StringBody(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID)));
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
                    /// IF Exception occurs call will be saved to offline database
                    if (offLineId <= 0)
                        OfflineWork.StoreData(FilePath, hashMap, localMap, ApiName, db);
                    return Constant.offline;
                }
                Log.e("Resp Upload 44444", "" + resp);
                return resp;
            } else {
                if (offLineId <= 0)
                    OfflineWork.StoreData(FilePath, hashMap, localMap, ApiName, db);
                return Constant.offline;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.e("EditworkItem", "Received update :-" + response);


            if (response.equals(Constant.offline)) {
                finish();
                overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
            } else {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.optString("status");
                    if (status.equals(Constant.InvalidToken)) {
                        TeamWorkApplication.LogOutClear(EditWorkitem.this);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                        JSONObject jsonObject = new JSONObject(response);
                        toast(jsonObject.optString("message"));
                        if (jsonObject.optString("status").equals("Success")) {

                            String roleId = read(Constant.SHRED_PR.KEY_ROLE_ID);
                            if (Arrays.asList("1", "2").contains(roleId)) {
                                write(Constant.SHRED_PR.KEY_RELOAD,"1");
                                updateTask(jsonObject);
                            }
                            finish();
                            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (progressDialog != null){
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }

        }
    }

    private void updateTask(JSONObject jsonObject) {

        try {
            ContentValues taskValues = new ContentValues();
            taskValues.put(WorkItem.TASK_ID, "" + workItem.getTaskCode());
            taskValues.put(WorkItem.USER_CODE, "" + workItem.getUserCode());
            taskValues.put(WorkItem.TASK_NAME, "" + workItem.getTitle());
            taskValues.put(WorkItem.DESCRIPTION, "" + workItem.getDescription());
            taskValues.put(WorkItem.BUDGET, "" + workItem.getBudget());
            taskValues.put(WorkItem.PRIORITY, "" + workItem.getPriority());
            taskValues.put(WorkItem.ATTACHMENTS, "" + workItem.getAttachments());
            taskValues.put(WorkItem.WORK_LOCATION, "" + workItem.getWorkLocation());
            taskValues.put(WorkItem.WORK_LONGITUDE, "" + workItem.getLongitude());
            taskValues.put(WorkItem.WORK_LATITUDE, "" + workItem.getLatitude());
            taskValues.put(WorkItem.START_DATE, "" + workItem.getStartDate());
            taskValues.put(WorkItem.END_DATE, "" + workItem.getEndDate());
            taskValues.put(WorkItem.ESTIMATED_TIME, "" + workItem.getEstimatedWorkTime());
            taskValues.put(WorkItem.ASSIGNED_TO, "" + workItem.getTaskAssignedTo());
            taskValues.put(WorkItem.CC_TO, "" + workItem.getTaskCCTo());
            taskValues.put(WorkItem.TASK_TYPE, "" + workItem.getTaskType());
            taskValues.put(WorkItem.PROJECT_CODE, "" + workItem.getProjectCode());
            taskValues.put(WorkItem.TASK_AFTER, "" + workItem.getTaskCodeAfter());

            taskValues.put(WorkItem.FREQUENCY, "" + workItem.getFrequency());

            taskValues.put(WorkItem.DAYCODES_SELECTED, "" + workItem.getDayCodesSelected());
            taskValues.put(WorkItem.CUSTOMER_NAME, "" + workItem.getCustomerName());
            taskValues.put(WorkItem.CUSTOMER_CONTACT, "" + workItem.getCustomerContact());
            taskValues.put(WorkItem.CUSTOMER_TYPE, "" + workItem.getCustomerType());
            taskValues.put(WorkItem.DUE_DATE, "" +workItem.getDueDate());
            taskValues.put(WorkItem.PAST_HISTORY, "" + workItem.getPastHistory());
            taskValues.put(WorkItem.VENDOR_PREFERENCE, "" + workItem.getSpVendorPreference());
            taskValues.put(WorkItem.VENDOR_NAME, "" + workItem.getSpVendorName());
            taskValues.put(WorkItem.ADVANCE_PAID, "" + workItem.getSpAdvancePaid());
            taskValues.put(WorkItem.INVOICE_AMOUNT, "" + workItem.getInvoiceAmount());
            taskValues.put(WorkItem.INVOICE_DATE, "" + workItem.getInvoiceDate());
            taskValues.put(WorkItem.OUTSTANDING_AMT, "" + workItem.getOutStandingAmt());
            taskValues.put(WorkItem.TASK_IMAGE, "" + workItem.getTaskImage());
            String status = "";
            if(jsonObject != null && jsonObject.has("data")){
                JSONObject jsonData = jsonObject.getJSONObject("data");
                if(jsonData != null && jsonData.has("status")){
                    status = jsonData.optString("status");
                }
            }
            if(status.equalsIgnoreCase("Delegated")){
                taskValues.put(WorkItem.STATUS, "" + status);
            } else {
                taskValues.put(WorkItem.STATUS, "" + workItem.getStatus());
            }

            Cursor crsr = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + workItem.getTaskCode(), null);
            if (crsr!=null && crsr.getCount() > 0) {
                db.update(Constant.WorkItemTable, taskValues, WorkItem.TASK_ID + " = ?", new String[]{workItem.getTaskCode() + ""});
            }
            if(crsr!=null) crsr.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}


