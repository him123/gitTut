package com.bigbang.superteam.task;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.task.database.CustomerVendorDAO;
import com.bigbang.superteam.task.database.TaskAttachmentDAO;
import com.bigbang.superteam.task.database.TaskDAO;
import com.bigbang.superteam.task.database.TaskMemberDAO;
import com.bigbang.superteam.task.database.TaskStatusDAO;
import com.bigbang.superteam.task.database.TaskTypeDAO;
import com.bigbang.superteam.task.database.UserDAO;
import com.bigbang.superteam.task.model.TaskModel;
import com.bigbang.superteam.task.model.TaskType;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.workitem.SelectAssigneeCC;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddTaskActivity extends BaseActivity {

    @InjectView(R.id.viewTaskDetails)
    View viewTaskDetails;
    @InjectView(R.id.viewTaskAttachments)
    View viewTaskAttachments;
    @InjectView(R.id.viewTaskAddress)
    View viewTaskAddress;
    @InjectView(R.id.tvTaskDetails)
    TextView tvTaskDetails;
    @InjectView(R.id.tvTaskAttachments)
    TextView tvTaskAttachments;
    @InjectView(R.id.tvTaskAddress)
    TextView tvTaskAddress;
    @InjectView(R.id.rlDetails)
    RelativeLayout rlDetails;
    @InjectView(R.id.rlAttachments)
    RelativeLayout rlAttachments;
    @InjectView(R.id.rlAddress)
    RelativeLayout rlAddress;
    @InjectView(R.id.tvProjectStartingFrom)
    TextView tvProjectStartingFrom;
    @InjectView(R.id.tvProjectEndsBy)
    TextView tvProjectEndsBy;
    @InjectView(R.id.tvStartingFrom)
    TextView tvStartingFrom;
    @InjectView(R.id.tvEndsBy)
    TextView tvEndsBy;

    @InjectView(R.id.tvInvoiceDate)
    TextView tvInvoiceDate;
    @InjectView(R.id.tvDueDate)
    TextView tvDueDate;
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

    @InjectView(R.id.et_permadd1)
    EditText etPermAdd1;
    @InjectView(R.id.et_permadd2)
    EditText etPermAdd2;
    @InjectView(R.id.et_city)
    EditText etPermCity;
    @InjectView(R.id.et_state)
    EditText etPermState;
    @InjectView(R.id.et_country)
    EditText etPermCountry;
    @InjectView(R.id.et_pincode)
    EditText etPermPincode;

    @InjectView(R.id.spinner_workType)
    Spinner taskTypeSpinner;
    @InjectView(R.id.spinner_ListProjects)
    Spinner projectListSpinner;
    @InjectView(R.id.spinner_SSCustomerName)
    Spinner ssCustomerNameSpinner;
    @InjectView(R.id.spinner_ColCustomerName)
    Spinner ColCustomerNameSpinner;
    @InjectView(R.id.iv_selectedImage)
    ImageView selectedImage;
    @InjectView(R.id.ll_Projects)
    LinearLayout ll_project;
    @InjectView(R.id.ll_sales_service)
    LinearLayout ll_sales;
    @InjectView(R.id.ll_collection_service)
    LinearLayout ll_collection;
    @InjectView(R.id.ll_purchase)
    LinearLayout ll_purchase;
    @InjectView(R.id.tv_taskList)
    TextView tvTasksList;
    @InjectView(R.id.tv_assignTo)
    TextView tvAssigneed;
    @InjectView(R.id.tv_ccWorkitem)
    TextView tvCC;
    @InjectView(R.id.tv_selectedDays)
    TextView tvDays;

    @InjectView(R.id.ll_SSOthersName)
    RelativeLayout llSSOthersName;
    @InjectView(R.id.ll_ColOthersName)
    RelativeLayout llColOthersName;
    @InjectView(R.id.ll_otherVendorName)
    RelativeLayout llVendorOthersName;
    @InjectView(R.id.ll_optionalLayout)
    LinearLayout llOptional;
    @InjectView(R.id.tvVendors)
    TextView tvVendors;

    @InjectView(R.id.listAttachments)
    ListView listAttachments;
    @InjectView(R.id.rg_customerType)
    RadioGroup customerTypeGroup;
    @InjectView(R.id.rg_purchasePreference)
    RadioGroup purchasePreferenceGroup;

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

    public static boolean Active = false;
    String TAG = "AddTaskActivity";

    public int WorkTypeViews[] = new int[Constant.WORKTYPES.length];
    Calendar calendar;
    Calendar startCalendar, endCalendar, invoiceCalendar, dueDateCalendar;

    TransparentProgressDialog progressDialog;
    TaskDAO taskDAO;
    TaskMemberDAO taskMemberDAO;
    TaskAttachmentDAO taskAttachmentDAO;
    TaskTypeDAO taskTypeDAO;
    TaskStatusDAO taskStatusDAO;
    UserDAO userDAO;
    CustomerVendorDAO customerVendorDAO;

    ArrayList<TaskType> taskTypeList;
    ArrayList<User> assigneeList;
    ArrayList<User> ccList;
    ArrayList<Customer> vendorList;
    ArrayList<Customer> customerList;

    TaskModel taskModel;

    Boolean CheckListener = true;
    int hours, minutes;
    float wrkngHrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        init();
    }

    private void init() {
        ButterKnife.inject(this);

        //Set Font
        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        //Initialize Variables
        progressDialog = new TransparentProgressDialog(this, R.drawable.progressdialog, false);
        taskModel = new TaskModel();

        //Initialize DAO classes
        taskDAO = new TaskDAO(this);
        taskAttachmentDAO = new TaskAttachmentDAO(this);
        taskMemberDAO = new TaskMemberDAO(this);
        taskTypeDAO = new TaskTypeDAO(this);
        taskMemberDAO = new TaskMemberDAO(this);
        userDAO = new UserDAO(this);
        customerVendorDAO = new CustomerVendorDAO(this);

        //Set Spinner for TASK TYPE
        taskTypeList = new ArrayList<TaskType>();
        setTaskTypeSpinnerData();

        //get Assignee & CC Data
        assigneeList = new ArrayList<User>();
        assigneeList = userDAO.getUserList();

        ccList = new ArrayList<User>();
        ccList = userDAO.getUserList();

        //Get & Set Vendor Data
        vendorList = new ArrayList<Customer>();
        vendorList = customerVendorDAO.getCustomerVendorList("V");
        Customer vendor = new Customer();
        vendor.setName(getString(R.string.others));
        vendor.setMobileNo("");
        vendor.setID(0);
        vendor.setSelected(true);
        vendorList.add(vendor);
        llVendorOthersName.setVisibility(View.VISIBLE);
        tvVendors.setText(getString(R.string.others));

        //Get & Set Customer Data
        customerList = new ArrayList<Customer>();
        customerList = customerVendorDAO.getCustomerVendorList("U");
        Customer customer = new Customer();
        customer.setName(getString(R.string.others));
        customer.setMobileNo("");
        customer.setID(0);
        customer.setSelected(true);
        customerList.add(vendor);

        //Set Layout As per TASK TYPE
        WorkTypeViews[0] = ll_project.getId();
        WorkTypeViews[1] = rlRegularTypeTask.getId();
        WorkTypeViews[2] = 0;
        WorkTypeViews[3] = ll_sales.getId();
        WorkTypeViews[4] = ll_sales.getId();
        WorkTypeViews[5] = ll_purchase.getId();
        WorkTypeViews[6] = ll_collection.getId();

        //Get & Set Address Data
        setAddressData();
    }

    private void setTaskTypeSpinnerData() {
        taskTypeList = taskTypeDAO.getTaskTypeData();
        ArrayAdapter workTypeAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                taskTypeList);
        workTypeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        taskTypeSpinner.setAdapter(workTypeAdapter);
        taskTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setLayoutAsPerTaskType(i);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        taskTypeSpinner.setSelection(2);
    }

    private void setLayoutAsPerTaskType(int taskType) {
        reset();

        for (int j = 0; j < WorkTypeViews.length; j++) {
            if (WorkTypeViews[j] != 0)
                findViewById(WorkTypeViews[j]).setVisibility(View.GONE);
        }

        if (WorkTypeViews[taskType] != 0) {
            findViewById(WorkTypeViews[taskType]).setVisibility(View.VISIBLE);
        }

        if (taskType == Constant.PROJECT) {
            setProjectSpinnerData();
        } else {
            for (int i = 0; i < assigneeList.size(); i++) {
                assigneeList.get(i).setChecked(false);
            }

            for (int i = 0; i < ccList.size(); i++) {
                assigneeList.get(i).setChecked(false);
            }

            tvAssigneed.setText("");
            tvCC.setText("");
        }

        if (taskType == Constant.REGULAR) {
            endCalendar.setTime(startCalendar.getTime());
            endCalendar.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY) + 1);

            String sdate = Util.SDF.format(startCalendar.getTime()).substring(11);
            tvStartingFrom.setText(sdate);
            sdate = Util.SDF.format(endCalendar.getTime()).substring(11);
            tvEndsBy.setText(sdate);
            printDifference(startCalendar.getTime(), endCalendar.getTime());
        } else {
            String sdate = Util.ddMMMyyyy.format(startCalendar.getTime());
            tvStartingFrom.setText(sdate);
            sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
            tvEndsBy.setText(sdate);
            tvDays.setText("");
        }
    }

    private void setAddressData() {
        try {
            JSONArray jsonArray = new JSONArray(Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_AddressList));
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                etPermPincode.setText("" + jsonObject.optString("Pincode"));
                etPermAdd1.setText("" + jsonObject.optString("AddressLine1"));
                etPermAdd2.setText("" + jsonObject.optString("AddressLine2"));
                etPermCity.setText("" + jsonObject.optString("City"));
                etPermState.setText("" + jsonObject.optString("State"));
                etPermCountry.setText("" + jsonObject.optString("Country"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reset() {

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

    @OnClick(R.id.rlStartingFrom)
    @SuppressWarnings("unused")
    public void StartingFrom(View view) {
        if (taskTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.REGULAR])) {
            showDialog(START_TIME);
        } else
            showDialog(START_DATE);
    }

    @OnClick(R.id.rlEndsBy)
    @SuppressWarnings("unused")
    public void EndsBy(View view) {
        if (taskTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.REGULAR])) {
            showDialog(END_TIME);
        } else
            showDialog(END_DATE);
    }

    @OnClick(R.id.rlAssignee)
    @SuppressWarnings("unused")
    public void Assignee(View view) {
        write(Constant.SHRED_PR.KEY_LIST, "" + assigneeList);
        Intent intent = new Intent(this, AssignCCActivity.class);
        intent.putExtra("title", getResources().getString(R.string.assign_to));
        intent.putExtra("key_assignee_cc", "1");
        intent.putExtra("list", assigneeList);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_bottom);
    }

    @OnClick(R.id.rlCC)
    @SuppressWarnings("unused")
    public void CC(View view) {
        write(Constant.SHRED_PR.KEY_LIST, "" + ccList);
        Intent intent = new Intent(this, AssignCCActivity.class);
        intent.putExtra("title", getResources().getString(R.string.cc));
        intent.putExtra("key_assignee_cc", "2");
        intent.putExtra("list", ccList);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_bottom);
    }

    @OnClick(R.id.rlVendors)
    @SuppressWarnings("unused")
    public void Vendors(View view) {
        write(Constant.SHRED_PR.KEY_LIST, "" + vendorList);
        Intent intent = new Intent(this, CustomerVendorActivity.class);
        intent.putExtra("list", vendorList);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_bottom);
    }


    private void setProjectSpinnerData() {

    }

    private void printDifference(Date fromDate, Date endDate) {
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

        if (fromDate.getHours() < CompStartTime.getHours()) {
            fromDate.setHours(CompStartTime.getHours());
            fromDate.setMinutes(CompStartTime.getMinutes());
        }
        if (fromDate.getHours() >= CompEndtime.getHours()) {
            fromDate.setHours(CompEndtime.getHours());
            fromDate.setMinutes(CompEndtime.getMinutes());
        }
        if (endDate.getHours() < CompStartTime.getHours()) {
            endDate.setHours(CompStartTime.getHours());
            endDate.setMinutes(CompStartTime.getMinutes());
        }
        if (endDate.getHours() >= CompEndtime.getHours()) {
            endDate.setHours(CompEndtime.getHours());
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

        CheckListener = false;
        actualHours.setText("" + (((int) tempHr) + Hours));
        actualMins.setText("" + Minutes);

        String hh = actualHours.getText().toString();
        String mm = actualMins.getText().toString();
        hh = hh.replace("-", "");
        mm = mm.replace("-", "");
        actualHours.setText(hh);
        actualMins.setText(mm);
        Log.d("ActualTime", "::" + hh + " " + mm);

        if (hh.length() == 0) hh = "0";
        if (mm.length() == 0) mm = "0";
        taskModel.estimatedTime = hh + "," + mm;

        hours = (int) (((int) tempHr) + Hours);
        minutes = (int) Minutes;
        wrkngHrs = workingHrs;

        CheckListener = true;
    }

}
