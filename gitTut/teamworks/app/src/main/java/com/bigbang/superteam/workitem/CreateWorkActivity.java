package com.bigbang.superteam.workitem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.bigbang.superteam.dataObjs.OfflineWork;
import com.bigbang.superteam.dataObjs.Project;
import com.bigbang.superteam.dataObjs.StaticData;
import com.bigbang.superteam.dataObjs.User;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.ImageFilePath;
import com.bigbang.superteam.util.InputFilterMinMax;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 4/10/2015.
 */
public class CreateWorkActivity extends BaseActivity {


    public static final int BROWSE_ACTIVITY = 101;
    //    public static final int SELECT_LOCATION = 102;
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
    String TAG = "CreateWorkActivity";

    public int WorkTypeViews[] = new int[Constant.WORKTYPES.length];
    Calendar calendar;
    Calendar startCalendar, endCalendar, invoiceCalendar, dueDateCalendar;


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
    //    @InjectView(R.id.btn_dependency)
//    Button dependencyBtn;
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
    Spinner workTypeSpinner;
    //    @InjectView(R.id.spinner_priority)
//    Spinner prioritySpinner;
    @InjectView(R.id.spinner_ListProjects)
    Spinner projectListSpinner;
    //@#$
    @InjectView(R.id.spinner_SSCustomerName)
    Spinner ssCustomerNameSpinner;
    @InjectView(R.id.spinner_ColCustomerName)
    Spinner ColCustomerNameSpinner;
    //@#$
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
    //    @InjectView(R.id.ll_taskList)
//    LinearLayout ll_taskList;
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
    //    @InjectView(R.id.rg_workCategory)
//    RadioGroup workCategoryGroup;
    @InjectView(R.id.rg_customerType)
    RadioGroup customerTypeGroup;
    @InjectView(R.id.rg_purchasePreference)

    RadioGroup purchasePreferenceGroup;
    //    AlertDialog.Builder vendorsDialogue;
    String invoiceDate = "", dueDate = "";
    ArrayList<HashMap<String, String>> attachmentData = new ArrayList<>();
    ArrayList<User> userArrayList = new ArrayList<>();
    ArrayList<Project> projectList = new ArrayList<>();
    ArrayList<WorkItem> projectWorkList = new ArrayList<>();
    ArrayList<Customer> customerList = new ArrayList<>();
    ArrayList<Customer> vendorList = new ArrayList<>();
    ArrayList<HashMap<String, String>> listDays = new ArrayList<>();
    int noofMembers = 0;
    //    boolean assignMembersSelected[];
//    boolean ccMembersSelected[];
//    boolean workItemsSelected[];
//    boolean vendorsSelected[];
    Boolean CheckListener = true;
    //    String membersString[];
//    String workString[];
    String customersString[];
    //    String vendorsString[];
    WorkItem task = new WorkItem();
    int taskId;
    Random rand;
    Uri globalUri = null;
    CreateWorkActivity CameraActivity = null;
    Cursor cursor = null;
    String camera_pathname = "";
    ImageLoader imageLoader;
    DisplayImageOptions options;
    //    ProgressDialog progress;
    TransparentProgressDialog progressDialog;
    Boolean dueDateFlag = false, invoiceDateFlag = false, expand = false;
    Double latitude, longitude;
    GPSTracker gps;
    String strFrequency = Constant.DAILY;
    String strPriority = Constant.MEDIUM;
    int hours, minutes;
    float wrkngHrs;
    AttachmentAdapter attachmentAdapter;
    SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_assignwork);
        ButterKnife.inject(this);
        CameraActivity = this;
        rand = new Random();
        taskId = rand.nextInt(Integer.MAX_VALUE);
        Init();

        GetUserList();
        getVendorsList();
        InitSpinners();
        createURI();
    }

    @Override
    public void onResume() {
        super.onResume();
        Active = true;

        gps = new GPSTracker(this);
        if (!gps.canGetLocation()) gps.showSettingsAlert();

        if (read(Constant.SHRED_PR.KEY_RELOAD).equals("1")) {
            write(Constant.SHRED_PR.KEY_RELOAD, "0");
            if (read(Constant.SHRED_PR.KEY_TEMP_LATITUDE).length() > 0 && read(Constant.SHRED_PR.KEY_TEMP_LONGITUDE).length() > 0) {
                latitude = Double.parseDouble(read(Constant.SHRED_PR.KEY_TEMP_LATITUDE));
                longitude = Double.parseDouble(read(Constant.SHRED_PR.KEY_TEMP_LONGITUDE));
                Log.e("latitude", ">>" + latitude);
                Log.e("longitude", ">>" + longitude);

                new AsyncTask<Void, Void, List<android.location.Address>>() {

                    @Override
                    protected List<android.location.Address> doInBackground(Void... params) {
                        Geocoder geocoder = new Geocoder(CreateWorkActivity.this, Locale.getDefault());

                        try {
                            List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            return addressList;
                        } catch (IOException e) {
                            Log.e("RegisterAct", "exp==" + e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(List<android.location.Address> addressList) {
                        super.onPostExecute(addressList);

                        try {
                            if (addressList != null && addressList.size() > 0) {

                                String strAddressLine1 = addressList.get(0).getAddressLine(0);
                                String strAddressLine2 = addressList.get(0).getAddressLine(1);
                                String state = addressList.get(0).getAdminArea();
                                String city = addressList.get(0).getSubAdminArea();
                                String countryName = addressList.get(0).getCountryName();
                                String postalCode = addressList.get(0).getPostalCode();
                                if (city == null) {
                                    city = addressList.get(0).getLocality();
                                }
                                etPermAdd1.setText(strAddressLine1);
                                etPermAdd2.setText(strAddressLine2);
                                etPermCity.setText(city);
                                etPermState.setText(state);
                                etPermCountry.setText(countryName);
                                etPermPincode.setText(postalCode);
                            }
                        } catch (Exception e) {
                            Log.e("RegisterAct", "exp==" + e);
                        }
                    }
                }.execute();
            }
        }

        if (read(Constant.SHRED_PR.KEY_ASSIGNEE_CC).equals("1")) {
            //Assignee:
            write(Constant.SHRED_PR.KEY_ASSIGNEE_CC, "0");

            String strAssignee = "";
            try {
                JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
                listAssignne.clear();
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

                    listAssignne.add(user);
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
                        else strVendors += ", " + customer.getName();
                    }

                    vendorList.add(customer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (strVendors.length() > 0) {
                tvVendors.setText("" + strVendors + "\n");
            } else tvVendors.setText("");

            if (vendorList.get(vendorList.size() - 1).isSelected()) {
                llVendorOthersName.setVisibility(View.VISIBLE);
            } else {
                llVendorOthersName.setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            for (int i = 0; i < listAssignne.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(User.FIRST_NAME, "" + listAssignne.get(i).getFirstName());
                jsonObject.put(User.LAST_NAME, "" + listAssignne.get(i).getLastName());
                jsonObject.put(User.USER_ID, "" + listAssignne.get(i).getUser_Id());
                jsonObject.put(User.USER_IMAGE, "" + listAssignne.get(i).getUser_Image());
                jsonObject.put(User.SELECTED, "" + listAssignne.get(i).isSelected());
                jsonArray.put(jsonObject);
            }
            write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
            Intent intent = new Intent(CreateWorkActivity.this, SelectAssigneeCC.class);
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
                jsonArray.put(jsonObject);
            }
            write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
            Intent intent = new Intent(CreateWorkActivity.this, SelectAssigneeCC.class);
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
            Intent intent = new Intent(CreateWorkActivity.this, SelectVendorActivity.class);
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

        if (workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.REGULAR])) {
            showDialog(START_TIME);
        } else
            showDialog(START_DATE);
    }

    @OnClick(R.id.rlEndsBy)
    @SuppressWarnings("unused")
    public void EndsBy(View view) {

        if (workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.REGULAR])) {
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

    @OnClick(R.id.rlUpload)
    @SuppressWarnings("unused")
    public void Upload(View view) {
//        Intent fileExploreIntent = new Intent(ActivityFileBrowser.INTENT_ACTION_SELECT_FILE, null, this,
//                ActivityFileBrowser.class);
//        startActivityForResult(fileExploreIntent, BROWSE_ACTIVITY);

        try {
//            String mimeType="text/plain,text/csv,text/xml,text/html,image/png,image/gif,image/jpg,image/jpeg,image/bmp,audio/mp3,audio/wav,audio/x-ogg,audio/mid,audio/midi,audio/AMR,video/mpeg,video/3gpp,application/pdf";
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType(mimeType);
//            startActivityForResult(intent, BROWSE_ACTIVITY);

            String mimeType = "*/*";
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(mimeType);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, BROWSE_ACTIVITY);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                Intent intent = new Intent(CreateWorkActivity.this, SelectTaskActivity.class);
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
            Intent intent = new Intent(CreateWorkActivity.this, SelectDaysActivity.class);
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
            Intent intent = new Intent(CreateWorkActivity.this, SelectDaysActivity.class);
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

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case END_DATE:
                Log.e(TAG, "Inside end date in dialog :- " + endCalendar.getTime());
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, mytoDateListener, endCalendar.get(endCalendar.YEAR), endCalendar.get(endCalendar.MONTH), endCalendar.get(endCalendar.DATE));
            case START_DATE:
                Log.e(TAG, "Inside end date in dialog :- " + startCalendar.getTime());
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, myfromDateListener, startCalendar.get(startCalendar.YEAR), startCalendar.get(startCalendar.MONTH), startCalendar.get(startCalendar.DATE));
            case END_TIME:
                return new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, myToTimeListener, endCalendar.get(endCalendar.HOUR_OF_DAY), endCalendar.get(endCalendar.MINUTE), false);
            case START_TIME:
                return new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, myfromTimeListener, startCalendar.get(startCalendar.HOUR_OF_DAY), startCalendar.get(startCalendar.MINUTE), false);
            case INVOICE_DATE:
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, myInvoiceDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            case DUE_DATE:
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, myDueDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        }
        return null;
    }


    TimePickerDialog.OnTimeSetListener myToTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker timePicker, int i, int i2) {
            timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
            Log.i("onTimeSet", "::" + i + " " + i2);
            if (workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.REGULAR])) {

                Calendar calendarTemp = Calendar.getInstance();
                calendarTemp.setTime(endCalendar.getTime());
                calendarTemp.set(Calendar.HOUR_OF_DAY, i);
                calendarTemp.set(Calendar.MINUTE, i2);

                boolean flag = true;

                Log.e("calender", ">>>>>>*******" + startCalendar.getTime() + ":\n" + endCalendar.getTime() + ">>\n" + calendarTemp.getTime());
                if (startCalendar.after(calendarTemp)) {
                    flag = false;
                    // Toast.makeText(getApplicationContext(), getString(R.string.end_time_should_greater_then_start), Toast.LENGTH_SHORT).show();
                }
                Log.e("flag", "::" + flag);

                if (flag) {
                    endCalendar.set(Calendar.HOUR_OF_DAY, i);
                    endCalendar.set(Calendar.MINUTE, i2);
                    Log.e("calender", "Inside if part======" + endCalendar.getTime());
                    String sdate = Util.SDF.format(endCalendar.getTime());
                    tvEndsBy.setText(sdate.substring(11));
                    printDifference(startCalendar.getTime(), endCalendar.getTime());
                } else {
                    endCalendar.set(Calendar.HOUR_OF_DAY, i);
                    endCalendar.set(Calendar.MINUTE, i2);
                    String sdate = Util.SDF.format(endCalendar.getTime());
                    Log.e("calender", "Inside else part==" + endCalendar.getTime());
                    tvEndsBy.setText(sdate.substring(11));
                }

            } else {

                calendar = Calendar.getInstance();
                Calendar calendarTemp = Calendar.getInstance();
                calendarTemp.setTime(endCalendar.getTime());
                calendarTemp.set(Calendar.HOUR_OF_DAY, i);
                calendarTemp.set(Calendar.MINUTE, i2);

                boolean flag = true;

                Log.e("calender", ">>" + startCalendar.getTime() + ":\n" + endCalendar.getTime() + ">>\n" + calendarTemp.getTime());
                if (startCalendar.after(calendarTemp)) {
                    flag = false;
                    //Toast.makeText(getApplicationContext(), getString(R.string.end_time_should_greater_then_start), Toast.LENGTH_SHORT).show();
                } else if (calendarTemp.before(calendar)) {
                    flag = false;
                    //Toast.makeText(getApplicationContext(), getString(R.string.end_time_should_greater_then_current), Toast.LENGTH_SHORT).show();
                }

                if (flag) {
                    endCalendar.set(Calendar.HOUR_OF_DAY, i);
                    endCalendar.set(Calendar.MINUTE, i2);

                    String sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
                    tvEndsBy.setText(sdate);
                    printDifference(startCalendar.getTime(), endCalendar.getTime());
                } else {
                    endCalendar.set(Calendar.HOUR_OF_DAY, i);
                    endCalendar.set(Calendar.MINUTE, i2);
                    String sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
                    tvEndsBy.setText(sdate);


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
            if (workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.REGULAR])) {
                tvStartingFrom.setText(sdate.substring(11));
            } else {
                String sdate1 = Util.ddMMMyyyy.format(startCalendar.getTime());
                tvStartingFrom.setText(sdate1);
            }

            endCalendar.setTime(startCalendar.getTime());
            Log.e(TAG, "Inside satart calemdar 111111" + endCalendar.getTime());
            if (workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.REGULAR])) {
                endCalendar.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY) + 1);
                endCalendar.set(Calendar.MINUTE, i2);
                sdate = Util.SDF.format(endCalendar.getTime());
                tvEndsBy.setText(sdate.substring(11));
            } else {
                endCalendar.set(endCalendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH) + 1);
                endCalendar.set(endCalendar.MINUTE, i2);
                Log.e(TAG, "Inside satart calemdar " + endCalendar.getTime());
                sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
                tvEndsBy.setText(sdate);
            }

            printDifference(startCalendar.getTime(), endCalendar.getTime());
        }
    };
    private ArrayList<User> listAssignne = new ArrayList<>();
    private ArrayList<User> listCC = new ArrayList<>();
    private DatePickerDialog.OnDateSetListener myDueDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.set(Calendar.YEAR, i);
            tempCalendar.set(Calendar.MONTH, i1);
            tempCalendar.set(Calendar.DAY_OF_MONTH, i2);

            if (tempCalendar.before(invoiceCalendar) && invoiceDateFlag) {
                Toast.makeText(getApplicationContext(), getString(R.string.due_time_should_greater_then_invoice), Toast.LENGTH_SHORT).show();
//                dueDateFlag = false;
//                tvDueDate.setText("");
            } else {
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

            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.set(Calendar.YEAR, i);
            tempCalendar.set(Calendar.MONTH, i1);
            tempCalendar.set(Calendar.DAY_OF_MONTH, i2);
            if (dueDateFlag && dueDateCalendar.before(tempCalendar)) {
                Toast.makeText(getApplicationContext(), getString(R.string.invoice_should_less), Toast.LENGTH_SHORT).show();
//                invoiceDateFlag = false;
//                tvInvoiceDate.setText("");
            } else {
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


            endCalendar.set(endCalendar.YEAR, year);
            endCalendar.set(endCalendar.MONTH, month);
            endCalendar.set(endCalendar.DAY_OF_MONTH, day);
            Log.e(TAG, " ****** >>> mytoDateListener" + endCalendar.getTime());
            showDialog(END_TIME);

        }
    };
    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            startCalendar.set(startCalendar.YEAR, year);
            startCalendar.set(startCalendar.MONTH, month);
            startCalendar.set(startCalendar.DAY_OF_MONTH, day);
            Log.e(TAG, " ****** >>> myfromDateListener" + startCalendar.getTime());
            showDialog(START_TIME);
        }
    };


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("resultCode", ">>" + requestCode + ":" + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case BROWSE_ACTIVITY:
//                    String newFile = data.getStringExtra(
//                            ActivityFileBrowser.returnFileParameter);
//                    AddAttachment(newFile);

                    if (null == data) return;
                    Uri uri = data.getData();
                    String file = ImageFilePath.getPath(getApplicationContext(), uri);
                    if (Arrays.asList("txt", "csv", "xml", "html", "png", "gif", "jpg", "jpeg",
                            "bmp", "mp3", "wav", "ogg", "x-ogg", "mid", "midi", "AMR", "mpeg",
                            "3gpp", "mkv", "mp4", "pdf").contains(Util.getFilenameType(file))) {
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
                case REQUEST_CAMERA:
                    Log.i("globalUri", ">>" + globalUri.getPath());
                    String imageId = convertImageUriToFile(globalUri, CameraActivity);
                    new LoadImagesFromSDCard().execute("" + imageId);
                    break;

            }
        }
    }

    @OnClick(R.id.rlLocation)
    @SuppressWarnings("unused")
    public void Location(View view) {
        startActivity(new Intent(this, com.bigbang.superteam.admin.SelectLocationActivity.class));
        overridePendingTransition(R.anim.enter_from_bottom,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlTaskDetails)
    @SuppressWarnings("unused")
    public void Details(View view) {
        viewTaskDetails.setVisibility(View.VISIBLE);
        viewTaskAttachments.setVisibility(View.GONE);
        viewTaskAddress.setVisibility(View.GONE);
        tvTaskDetails.setTextColor(getResources().getColor(R.color.white));
        tvTaskAttachments.setTextColor(getResources().getColor(R.color.light_gray));
        tvTaskAddress.setTextColor(getResources().getColor(R.color.light_gray));

        rlDetails.setVisibility(View.VISIBLE);
        rlAttachments.setVisibility(View.GONE);
        rlAddress.setVisibility(View.GONE);
    }

    @OnClick(R.id.rlTaskAttachments)
    @SuppressWarnings("unused")
    public void Attachments(View view) {
        viewTaskDetails.setVisibility(View.GONE);
        viewTaskAttachments.setVisibility(View.VISIBLE);
        viewTaskAddress.setVisibility(View.GONE);
        tvTaskDetails.setTextColor(getResources().getColor(R.color.light_gray));
        tvTaskAttachments.setTextColor(getResources().getColor(R.color.white));
        tvTaskAddress.setTextColor(getResources().getColor(R.color.light_gray));

        rlDetails.setVisibility(View.GONE);
        rlAttachments.setVisibility(View.VISIBLE);
        rlAddress.setVisibility(View.GONE);
    }

    @OnClick(R.id.rlTaskAddress)
    @SuppressWarnings("unused")
    public void Address(View view) {
        viewTaskDetails.setVisibility(View.GONE);
        viewTaskAttachments.setVisibility(View.GONE);
        viewTaskAddress.setVisibility(View.VISIBLE);
        tvTaskDetails.setTextColor(getResources().getColor(R.color.light_gray));
        tvTaskAttachments.setTextColor(getResources().getColor(R.color.light_gray));
        tvTaskAddress.setTextColor(getResources().getColor(R.color.white));

        rlDetails.setVisibility(View.GONE);
        rlAttachments.setVisibility(View.GONE);
        rlAddress.setVisibility(View.VISIBLE);
    }

    private void Init() {

        progressDialog = new TransparentProgressDialog(CreateWorkActivity.this, R.drawable.progressdialog, false);

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        listAttachments.setDivider(null);
        listAttachments.setDividerHeight(0);
        attachmentAdapter = new AttachmentAdapter(CreateWorkActivity.this, attachmentData);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

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

        //Task Priority:

        if (strPriority.equals(Constant.HIGH)) {
            strPriority = Constant.HIGH;

            rlHighPriority.setBackgroundResource(R.drawable.rectangle_selected);
            rlMediumPriority.setBackgroundResource(R.drawable.rectangle_unselected);
            rlLowPriority.setBackgroundResource(R.drawable.rectangle_unselected);
            tvHighPriority.setTextColor(getResources().getColor(R.color.white));
            tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
            tvLowPriority.setTextColor(getResources().getColor(R.color.gray));
        } else if (strPriority.equals(Constant.MEDIUM)) {
            strPriority = Constant.MEDIUM;

            rlHighPriority.setBackgroundResource(R.drawable.rectangle_unselected);
            rlMediumPriority.setBackgroundResource(R.drawable.rectangle_selected);
            rlLowPriority.setBackgroundResource(R.drawable.rectangle_unselected);
            tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
            tvMediumPriority.setTextColor(getResources().getColor(R.color.white));
            tvLowPriority.setTextColor(getResources().getColor(R.color.gray));
        } else if (strPriority.equals(Constant.LOW)) {
            strPriority = Constant.LOW;

            rlHighPriority.setBackgroundResource(R.drawable.rectangle_unselected);
            rlMediumPriority.setBackgroundResource(R.drawable.rectangle_unselected);
            rlLowPriority.setBackgroundResource(R.drawable.rectangle_selected);
            tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
            tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
            tvLowPriority.setTextColor(getResources().getColor(R.color.white));
        }


        calendar = Calendar.getInstance();
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        invoiceCalendar = Calendar.getInstance();
        dueDateCalendar = Calendar.getInstance();
//////////Finds Company start and End time////////
        String sTime = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_STARTTIME);
        Log.e("Start Time", "s:" + sTime);
        final String eTime = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ENDTIME);
        Log.e("end Time", "e:" + eTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date CompStartTime = null, CompEndtime = null;
        try {
            CompStartTime = simpleDateFormat.parse(sTime);
            CompEndtime = simpleDateFormat.parse(eTime);
        } catch (ParseException ex) {
            System.out.println("Exception " + ex);
        }
        ////////////////////
        if ((startCalendar.get(Calendar.HOUR_OF_DAY) > CompEndtime.getHours())
                ||
                (startCalendar.get(Calendar.HOUR_OF_DAY) == CompEndtime.getHours() && startCalendar.get(Calendar.MINUTE) > CompEndtime.getMinutes())) {
            endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH) + 1);
            endCalendar.set(Calendar.HOUR_OF_DAY, CompEndtime.getHours());
            endCalendar.set(Calendar.MINUTE, CompEndtime.getMinutes());
        } else if (startCalendar.get(Calendar.HOUR_OF_DAY) < CompStartTime.getHours()
                ||
                (startCalendar.get(Calendar.HOUR_OF_DAY) == CompStartTime.getHours() && startCalendar.get(Calendar.MINUTE) < CompStartTime.getMinutes())) {
            endCalendar.set(Calendar.HOUR_OF_DAY, CompEndtime.getHours());
            endCalendar.set(Calendar.MINUTE, CompEndtime.getMinutes());
        } else {
            endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        printDifference(startCalendar.getTime(), endCalendar.getTime());

        Log.e("Time", "Start:" + startCalendar.getTime() + " " + "End:" + endCalendar.getTime());
        String sdate = Util.ddMMMyyyy.format(startCalendar.getTime());
        tvStartingFrom.setText(sdate);
        sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
        tvEndsBy.setText(sdate);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).considerExifParams(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        setLayoutsAsPer(Constant.ONE_TIME);

        etPermPincode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    Util.hideKeyboard(CreateWorkActivity.this);
                    if (Util.isOnline(getApplicationContext())) {
                        new searchPincode(etPermPincode.getText().toString().trim()).execute();
                    }
                    return true;
                }
                return false;
            }
        });
//////////////
//        "Project", "Regular", "One Time", "Service Call", "Sales Call", "Shopping/Purchase", "Collection"
        WorkTypeViews[0] = ll_project.getId();
        WorkTypeViews[1] = rlRegularTypeTask.getId();
        WorkTypeViews[2] = 0;
        WorkTypeViews[3] = ll_sales.getId();
        WorkTypeViews[4] = ll_sales.getId();
        WorkTypeViews[5] = ll_purchase.getId();
        WorkTypeViews[6] = ll_collection.getId();

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
        actualHours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Boolean flag = false;
                    if (CheckListener) {
                        if (s.length() >= 0 && s.length() < 5) {
                            int hrs = Integer.parseInt(s.toString());
                            if (hrs > hours && hrs > 0) {
                                if (workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.REGULAR])) {
                                    Log.e("wrkgn", "" + wrkngHrs + "");
                                    if (hrs > wrkngHrs) {
                                        Toast.makeText(CreateWorkActivity.this, getString(R.string.hours_must_be_les_than_working_hours), Toast.LENGTH_SHORT).show();
                                        printDifference(startCalendar.getTime(), endCalendar.getTime());
                                        flag = true;
                                    }
                                }
                                if (!flag) {

                                    if (workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.REGULAR])) {
                                        int addDays = (int) (hrs / wrkngHrs);
                                        int addHours = (int) (hrs % wrkngHrs);
                                        Log.e("ADD", "Days:" + addDays + " Hrs:" + addHours);
                                        if (addDays > 0) {
                                            final String eTime = Util.ReadSharePrefrence(CreateWorkActivity.this, Constant.SHRED_PR.KEY_COMPANY_ENDTIME);
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                                            Date CompEndtime = null;
                                            try {
                                                CompEndtime = simpleDateFormat.parse(eTime);
                                                endCalendar.set(Calendar.HOUR_OF_DAY, CompEndtime.getHours());
                                                endCalendar.set(Calendar.MINUTE, CompEndtime.getMinutes());
                                            } catch (ParseException ex) {
                                                System.out.println("Exception " + ex);
                                            }
                                        } else
                                            endCalendar.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY) + addHours);

                                        String sdate = Util.SDF.format(endCalendar.getTime());
                                        sdate = Util.SDF.format(endCalendar.getTime());
                                        tvEndsBy.setText(sdate.substring(11));
                                    } else {
                                        int addDays = (int) (hrs / wrkngHrs);
                                        int addHours = (int) (hrs % wrkngHrs);
                                        Log.e("ADD", "Days:" + addDays + " Hrs:" + addHours);
                                        endCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.get(Calendar.DAY_OF_MONTH) + addDays);
                                        endCalendar.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY) + addHours);
                                        String sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
                                        tvEndsBy.setText(sdate);
                                    }
                                }
                            }
                        } else {
                            printDifference(startCalendar.getTime(), endCalendar.getTime());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        InputFilter[] filterArray = new InputFilter[2];
        filterArray[0] = new InputFilterMinMax("00", "60");
        filterArray[1] = new InputFilter.LengthFilter(2);
        actualMins.setFilters(filterArray);
        actualMins.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length() > 0) {
                        int mins = Integer.parseInt(s.toString());
                        if (mins > minutes && mins > 0) {
                            if (mins > 60) {
                                Toast.makeText(CreateWorkActivity.this, getString(R.string.max_minute_size_60), Toast.LENGTH_SHORT).show();
                                //actualMins.setText("0");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        if (cursor != null) cursor.close();

        Customer vendor = new Customer();
        vendor.setName(getString(R.string.others));
        vendor.setMobileNo("");
        vendor.setID(0);
        vendor.setSelected(true);
        vendorList.add(vendor);
        llVendorOthersName.setVisibility(View.VISIBLE);
        tvVendors.setText(getString(R.string.others));
    }

    private void GetUserList() {

        userArrayList.clear();
        listAssignne.clear();
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
                    userArrayList.add(user);

                    User user1 = new User();
                    user1.setUser_Id(cursor.getInt(cursor.getColumnIndex(User.USER_ID)));
                    user1.setFirstName(cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)));
                    user1.setLastName(cursor.getString(cursor.getColumnIndex(User.LAST_NAME)));
                    user1.setUser_Image(cursor.getString(cursor.getColumnIndex(User.USER_IMAGE)));
                    user1.setSelected(false);
                    listAssignne.add(user1);

                    User user2 = new User();
                    user2.setUser_Id(cursor.getInt(cursor.getColumnIndex(User.USER_ID)));
                    user2.setFirstName(cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)));
                    user2.setLastName(cursor.getString(cursor.getColumnIndex(User.LAST_NAME)));
                    user2.setUser_Image(cursor.getString(cursor.getColumnIndex(User.USER_IMAGE)));
                    user2.setSelected(false);
                    listCC.add(user2);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        noofMembers = userArrayList.size();
    }

    private void InitSpinners() {
        ////For Work Types Spinner
        ArrayAdapter workTypeAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                StaticData.workTypes);
        workTypeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        workTypeSpinner.setAdapter(workTypeAdapter);
        workTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setLayoutsAsPer(i);

            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        workTypeSpinner.setSelection(2);

        //@#$ For SS Customer Name Spinner
        getCustomerList();

        ArrayAdapter CustomerNameAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, customersString
        );
        CustomerNameAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        ssCustomerNameSpinner.setAdapter(CustomerNameAdapter);

        ssCustomerNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ssCustomerContactET.setText("");
                if (customersString[position].equals(getString(R.string.others))) {
                    llSSOthersName.setVisibility(View.VISIBLE);
                    ssCustomerNameET.setText("");
                    ssCustomerContactET.setText("");
                } else {
                    llSSOthersName.setVisibility(View.GONE);
                    ssCustomerContactET.setText(customerList.get(position).getMobileNo());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ssCustomerNameSpinner.setSelection(customersString.length - 1);
        // For Col Customer Name Spinner
        ColCustomerNameSpinner.setAdapter(CustomerNameAdapter);
        ColCustomerNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                collCustomerContactET.setText("");
                if (customersString[position].equals(getString(R.string.others))) {
                    llColOthersName.setVisibility(View.VISIBLE);
                    collCustomernameET.setText("");
                    collCustomerContactET.setText("");
                } else {
                    llColOthersName.setVisibility(View.GONE);
                    collCustomerContactET.setText(customerList.get(position).getMobileNo());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ColCustomerNameSpinner.setSelection(customersString.length - 1);
    }

    private void getCustomerList() {
        customerList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + Constant.tableCustomers, null);
        customersString = new String[1];

        int i = 0;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                customersString = new String[cursor.getCount() + 1];
                cursor.moveToFirst();
                do {
                    Customer customer = new Customer();
                    customer.setName(cursor.getString(cursor.getColumnIndex("Name")));
                    customer.setMobileNo(cursor.getString(cursor.getColumnIndex("MobileNo")));
                    customer.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                    customersString[i++] = customer.getName();
                    Log.e("Customer", "id :" + customer.getID() + " name:" + customer.getName() + " Phone:" + customer.getMobileNo());
                    customerList.add(customer);
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) cursor.close();
        customersString[i] = getString(R.string.others);
    }

    private void setProjectSpinner() {
        Cursor cursor = db.rawQuery("select * from " + Constant.ProjectTable + " where " + Project.STATUS + " ='Approved'", null);
        String projectString[] = new String[cursor.getCount()];
        projectList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            projectString = new String[cursor.getCount()];
            cursor.moveToFirst();
            do {
                Project tempProject = new Project();
                String name = cursor.getString(cursor.getColumnIndex(Project.PROJECT_NAME));
                tempProject.setProject_name(name);
                tempProject.setProject_Id(cursor.getInt(cursor.getColumnIndex(Project.PROJECT_ID)));
                tempProject.setProjectAssignedTo(cursor.getString(cursor.getColumnIndex(Project.ASSIGNED_TO)));
                tempProject.setProjectCCTo(cursor.getString(cursor.getColumnIndex(Project.CC_TO)));
                tempProject.setStartDate(cursor.getString(cursor.getColumnIndex(Project.START_DATE)));
                tempProject.setEndDate(cursor.getString(cursor.getColumnIndex(Project.END_DATE)));
                projectString[cursor.getPosition()] = name;
                projectList.add(tempProject);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(this, getString(R.string.no_projects_available_to_select), Toast.LENGTH_SHORT).show();
            workTypeSpinner.setSelection(2);
        }
        if (projectString != null && projectString.length > 0) {
            ArrayAdapter projectAdapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_item,
                    projectString);
            projectAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            projectListSpinner.setAdapter(projectAdapter);
            projectListSpinner.setSelection(0);
            //setProjectWorkDialogue();
            projectListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    setProjectWorkDialogue();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
    }

    private void setProjectWorkDialogue() {

        projectWorkList.clear();
        Cursor cursor = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.PROJECT_CODE + " = " + "" + projectList.get(projectListSpinner.getSelectedItemPosition()).getProject_Id() + " AND " + WorkItem.STATUS + " ='Approved'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    WorkItem work = new WorkItem();
                    work.setTaskCode(cursor.getInt(cursor.getColumnIndex(WorkItem.TASK_ID)));
                    work.setTitle(cursor.getString(cursor.getColumnIndex(WorkItem.TASK_NAME)));
                    work.setStartDate(cursor.getString(cursor.getColumnIndex(WorkItem.START_DATE)));
                    work.setEndDate(cursor.getString(cursor.getColumnIndex(WorkItem.END_DATE)));
                    work.setSelected(false);
                    projectWorkList.add(work);
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) cursor.close();
        tvTasksList.setText("");

        String strDependTasks = "";
        for (int i = 0; i < projectWorkList.size(); i++) {
            WorkItem workItem = projectWorkList.get(i);
            if (workItem.isSelected()) {
                if (strDependTasks.length() == 0) strDependTasks = workItem.getTitle();
                else strDependTasks += ", " + workItem.getTitle();
            }
        }

        tvTasksList.setText("");
        if (strDependTasks.length() > 0) {
            tvTasksList.setText("" + strDependTasks + "\n");
        }


        ///////Setting assigned member to be selected

        for (int i = 0; i < listAssignne.size(); i++) listAssignne.get(i).setSelected(false);
        for (int i = 0; i < listCC.size(); i++) listCC.get(i).setSelected(false);

        String Assignees = projectList.get(projectListSpinner.getSelectedItemPosition()).getProjectAssignedTo();
        if (Assignees != null && Assignees.length() > 0) {
            String asgns[] = Assignees.split(",");  // 35,45
            if (asgns != null && asgns.length > 0) {
                for (int i = 0; i < asgns.length; i++) {
                    for (int j = 0; j < listAssignne.size(); j++) {
                        if (asgns[i].equals("" + listAssignne.get(j).getUser_Id())) {
                            listAssignne.get(j).setSelected(true);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < listCC.size(); i++) listCC.get(i).setSelected(false);
        String Ccs = projectList.get(projectListSpinner.getSelectedItemPosition()).getProjectCCTo();
        if (Ccs != null && Ccs.length() > 0) {
            String CCss[] = Ccs.split(",");  // 35,45
            if (CCss != null && CCss.length > 0) {
                for (int i = 0; i < CCss.length; i++) {
                    for (int j = 0; j < listCC.size(); j++) {
                        if (CCss[i].equals("" + listCC.get(j).getUser_Id())) {
                            listCC.get(j).setSelected(true);
                        }
                    }
                }
            }
        }

        Log.e("Assignee CC", ">>" + Assignees + ":" + Ccs);
        String strAssignee = "";
        for (int i = 0; i < listAssignne.size(); i++) {
            User user = listAssignne.get(i);
            Log.e("Assignee user", ">>" + user.isSelected() + ":" + i);
            if (user.isSelected()) {
                if (strAssignee.length() == 0) {
                    strAssignee = user.getFirstName() + " " + user.getLastName();
                } else {
                    strAssignee += ", " + user.getFirstName() + " " + user.getLastName();
                }
            }
        }

        if (strAssignee.length() > 0) {
            tvAssigneed.setText("" + strAssignee + "\n");
        } else tvAssigneed.setText("");

        String strCC = "";
        for (int i = 0; i < listCC.size(); i++) {
            User user = listCC.get(i);
            Log.e("CC user", ">>" + user.isSelected() + ":" + i);
            if (user.isSelected()) {
                if (strCC.length() == 0) {
                    strCC = user.getFirstName() + " " + user.getLastName();
                } else {
                    strCC += ", " + user.getFirstName() + " " + user.getLastName();
                }
            }
        }

        Log.e("strAssignee CC", ">>" + strAssignee + ":" + strCC);
        if (strCC.length() > 0) {
            tvCC.setText("" + strCC + "\n");
        } else tvCC.setText("");

        try {
            Date sdate = Util.sdf.parse(Util.utcToLocalTime(projectList.get(projectListSpinner.getSelectedItemPosition()).getStartDate()));
            tvProjectStartingFrom.setText(Util.ddMMMyyyy.format(sdate));
            Date edate = Util.sdf.parse(Util.utcToLocalTime(projectList.get(projectListSpinner.getSelectedItemPosition()).getEndDate()));
            tvProjectEndsBy.setText(Util.ddMMMyyyy.format(edate));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setLayoutsAsPer(int i) {
        reset();
        for (int j = 0; j < WorkTypeViews.length; j++)
            if (WorkTypeViews[j] != 0)
                findViewById(WorkTypeViews[j]).setVisibility(View.GONE);
        if (WorkTypeViews[i] != 0) {
            findViewById(WorkTypeViews[i]).setVisibility(View.VISIBLE);
        }
        if (i == Constant.PROJECT) {
            setProjectSpinner();

        } else {
            for (int i1 = 0; i1 < listAssignne.size(); i1++)
                listAssignne.get(i1).setSelected(false);
            for (int i1 = 0; i1 < listCC.size(); i1++) listCC.get(i1).setSelected(false);
            tvAssigneed.setText("");
            tvCC.setText("");
        }
        if (i == Constant.REGULAR) {
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

    public void reset() {

        calendar = Calendar.getInstance();
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        invoiceCalendar = Calendar.getInstance();
        dueDateCalendar = Calendar.getInstance();
//////////////////
        String sTime = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_STARTTIME);
        Log.e("Start Time", "s:" + sTime);
        final String eTime = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ENDTIME);
        Log.e("end Time", "e:" + eTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date CompStartTime = null, CompEndtime = null;
        try {
            CompStartTime = simpleDateFormat.parse(sTime);
            CompEndtime = simpleDateFormat.parse(eTime);
        } catch (ParseException ex) {
            System.out.println("Exception " + ex);
        }
        ////////////////////
        if ((startCalendar.get(Calendar.HOUR_OF_DAY) > CompEndtime.getHours())
                ||
                (startCalendar.get(Calendar.HOUR_OF_DAY) == CompEndtime.getHours() && startCalendar.get(Calendar.MINUTE) > CompEndtime.getMinutes())) {
            endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH) + 1);
            endCalendar.set(Calendar.HOUR_OF_DAY, CompEndtime.getHours());
            endCalendar.set(Calendar.MINUTE, CompEndtime.getMinutes());
        } else if (startCalendar.get(Calendar.HOUR_OF_DAY) < CompStartTime.getHours()
                ||
                (startCalendar.get(Calendar.HOUR_OF_DAY) == CompStartTime.getHours() && startCalendar.get(Calendar.MINUTE) < CompStartTime.getMinutes())) {
            endCalendar.set(Calendar.HOUR_OF_DAY, CompEndtime.getHours());
            endCalendar.set(Calendar.MINUTE, CompEndtime.getMinutes());
        } else {
            endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        printDifference(startCalendar.getTime(), endCalendar.getTime());

        Log.e("Time", "Start:" + startCalendar.getTime() + " " + "End:" + endCalendar.getTime());
        String sdate = Util.ddMMMyyyy.format(startCalendar.getTime());
        tvStartingFrom.setText(sdate);
        sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
        tvEndsBy.setText(sdate);

        if (customersString != null) {
            ColCustomerNameSpinner.setSelection(customersString.length - 1);
            ssCustomerNameSpinner.setSelection(customersString.length - 1);
        }

        //workBudgetET.setText("");
        ssCustomerNameET.setText("");
        ssCustomerContactET.setText("");
        pastHistoryET.setText("");
        collCustomernameET.setText("");
        collCustomerContactET.setText("");
        invoiceAmountET.setText("");
        outstandingAmtET.setText("");
        vendorNameET.setText("");
        advancePaidET.setText("");

        tvDays.setText("");

        tvTasksList.setText("");

    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_form_library),
                getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateWorkActivity.this);
        builder.setTitle(getString(R.string.add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    startCameras();
                } else if (items[item].equals(getString(R.string.choose_form_library))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals(getString(R.string.cancel))) {
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

    private void startSubmittingItem() {
        if (checkValues()) {

            PrepareObj();
            uploadData();
        }
    }

    private Boolean checkValues() {
        if (workTitleET.getText().toString().trim().length() <= 2) {
            Toast.makeText(this, getString(R.string.title_length_should_be_minimum_of_three_chars), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (workDescriptionET.getText().toString().trim().length() <= 2) {
            Toast.makeText(this, getString(R.string.description_length_should_be_minimum_of_three_chars), Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean assigned = false;
        for (int i = 0; i < listAssignne.size(); i++) {
            if (listAssignne.get(i).isSelected()) {
                assigned = true;
            }
        }
        if (!assigned) {
            Toast.makeText(this, getString(R.string.assing_work_item), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endCalendar.before(startCalendar)) {
            Toast.makeText(this, getString(R.string.end_time_should_greater_then_start), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.REGULAR])) {
            calendar = Calendar.getInstance();
            if (endCalendar.before(calendar)) {
                Toast.makeText(this, getString(R.string.end_time_should_greater_then_current), Toast.LENGTH_SHORT).show();
                return false;
            }
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

        if (task.getTaskImagePath() != null && !task.getTaskImagePath().equals("null")) {
            if (!Util.checkFileSize(task.getTaskImagePath())) {
                Toast.makeText(this, getString(R.string.image_size_exceeds_limit), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //////////////////
        if (workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.COLLECTION]))  //Because for Collection and Sales/service customer name and no are common fields in obj.
        {
            if (ColCustomerNameSpinner.getSelectedItem().equals(getString(R.string.others))) {
                if (collCustomernameET.getText().toString().trim().length() == 0) {
                    Toast.makeText(this, getString(R.string.select_customer), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (getText(collCustomerContactET).length() != 10) {
                    toast(R.string.PleaseEnterCustomerMobile);
                    return false;
                }
            }

            if (dueDateFlag) {
                if (!invoiceDateFlag) {
                    toast(R.string.PleaseEnterInvoice);
                    return false;
                }
            }

        }

        if (workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.SERVICE_CALL]) || workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.SALES_CALL])) {
            if (ssCustomerNameSpinner.getSelectedItem().equals(getString(R.string.others))) {
                if (ssCustomerNameET.getText().toString().trim().length() == 0) {
                    Toast.makeText(this, getString(R.string.select_customer), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (getText(ssCustomerContactET).length() != 10) {
                    toast(R.string.PleaseEnterCustomerMobile);
                    return false;
                }
            }
        }

        if (workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.SHOPPING_PURCHASE])) {
            if (vendorList.get(vendorList.size() - 1).isSelected()) {
                if (vendorNameET.getText().toString().length() == 0) {
                    Toast.makeText(this, getString(R.string.enter_vendor), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                boolean flag = true;
                for (int i = 0; i < vendorList.size(); i++) {
                    if (vendorList.get(i).isSelected()) flag = false;
                }
                if (flag) {
                    Toast.makeText(this, getString(R.string.enter_vendor), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        if (workTypeSpinner.getSelectedItem().toString().equals(Constant.WORKTYPES[Constant.PROJECT])) {

            try {
                Calendar projectStart = Calendar.getInstance();
                Calendar projectEnd = Calendar.getInstance();
                projectStart.setTime(Util.sdf.parse(Util.utcToLocalTime(projectList.get(projectListSpinner.getSelectedItemPosition()).getStartDate())));
                projectEnd.setTime(Util.sdf.parse(Util.utcToLocalTime(projectList.get(projectListSpinner.getSelectedItemPosition()).getEndDate())));

                if (startCalendar.before(projectStart)) {
                    Toast.makeText(this, getString(R.string.check_proj_start_date), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (endCalendar.after(projectEnd)) {
                    Toast.makeText(this, getString(R.string.check_proj_end_date), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (!startCalendar.before(endCalendar)) {
            Toast.makeText(getApplicationContext(), getString(R.string.end_time_should_greater_then_start), Toast.LENGTH_SHORT).show();
            return false;
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
        task.setUserCode(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID)));
        task.setTitle(workTitleET.getText().toString());
        task.setDescription(workDescriptionET.getText().toString());
        if (workBudgetET.getText().toString().length() > 0)
            task.setBudget(workBudgetET.getText().toString());
        else
            task.setBudget("0");

        task.setPriority(strPriority);
        String sdate = Util.locatToUTC(Util.sdf.format(startCalendar.getTime()));
        task.setStartDate(sdate);
        String edate = Util.locatToUTC(Util.sdf.format(endCalendar.getTime()));
        task.setEndDate(edate);

        String hh = actualHours.getText().toString();
        String mm = actualMins.getText().toString();
        if (hh.length() == 0) hh = "0";
        if (mm.length() == 0) mm = "0";
        task.setEstimatedWorkTime(hh + "," + mm);

        String assignWork = "";
        for (int i = 0; i < listAssignne.size(); i++) {
            User user = listAssignne.get(i);
            if (user.isSelected()) {
                if (assignWork.length() == 0) assignWork = "" + user.getUser_Id();
                else assignWork += "," + user.getUser_Id();
            }
        }
        task.setTaskAssignedTo(assignWork);

        String ccWork = "";
        for (int i = 0; i < listCC.size(); i++) {
            User user = listCC.get(i);
            if (user.isSelected()) {
                if (ccWork.length() == 0) ccWork = "" + user.getUser_Id();
                else ccWork += "," + user.getUser_Id();
            }
        }
        task.setTaskCCTo(ccWork);
        task.setTaskType(workTypeSpinner.getSelectedItem().toString());

        // Project Type
        if (workTypeSpinner.getSelectedItemPosition() == 0) {
            task.setProjectCode(projectList.get(projectListSpinner.getSelectedItemPosition()).getProject_Id());

            String workCodes = "";
            for (int i = 0; i < projectWorkList.size(); i++) {
                WorkItem workItem = projectWorkList.get(i);
                if (workItem.isSelected()) {
                    if (workCodes.length() == 0) workCodes = "" + workItem.getTaskCode();
                    else workCodes += "," + workItem.getTaskCode();
                }
            }

            if (workCodes.length() > 0)
                task.setTaskCodeAfter(workCodes);
            else task.setTaskCodeAfter("");
        } else {
            task.setProjectCode(0);
        }

        // Regular Type
        String taskCodes = "";
        for (int i = 0; i < listDays.size(); i++) {
            if (listDays.get(i).get("selected").equals("1")) {
                if (taskCodes.length() == 0) taskCodes = "" + listDays.get(i).get("code");
                else taskCodes += "," + listDays.get(i).get("code");
            }
        }
        task.setFrequency(strFrequency);
        task.setDayCodesSelected(taskCodes);

        // Sales/ Service
        if (task.getTaskType().equals(Constant.WORKTYPES[Constant.COLLECTION]))  //Because for Collection and Sales/service customer name and no are common fields in obj.
        {
            if (ColCustomerNameSpinner.getSelectedItem().equals(getString(R.string.others))) {
                task.setCustomerName(collCustomernameET.getText().toString());
                task.setCustomerContact(collCustomerContactET.getText().toString());
            } else {
                task.setCustomerName("" + customerList.get(ColCustomerNameSpinner.getSelectedItemPosition()).getID());
                task.setCustomerContact("" + customerList.get(ColCustomerNameSpinner.getSelectedItemPosition()).getMobileNo());
            }

        } else {
            if (ssCustomerNameSpinner.getSelectedItem().equals(getString(R.string.others))) {
                task.setCustomerName(ssCustomerNameET.getText().toString());
                task.setCustomerContact(ssCustomerContactET.getText().toString());
            } else {
                task.setCustomerName("" + customerList.get(ssCustomerNameSpinner.getSelectedItemPosition()).getID());
                task.setCustomerContact("" + customerList.get(ssCustomerNameSpinner.getSelectedItemPosition()).getMobileNo());
            }
        }
        if (customerTypeGroup.getCheckedRadioButtonId() == R.id.rb_returningCustomer) {
            task.setCustomerType(Constant.REPEAT);
        } else {
            task.setCustomerType(Constant.NEW);
        }
        task.setPastHistory(pastHistoryET.getText().toString());

        // Purchase
        if (purchasePreferenceGroup.getCheckedRadioButtonId() == R.id.rb_purchaseMandatory) {
            task.setSpVendorPreference(Constant.MANDATORY);
        } else
            task.setSpVendorPreference(Constant.PREFERRED);
        /////////
        if (vendorList.get(vendorList.size() - 1).isSelected()) {
            task.setSpVendorName(vendorNameET.getText().toString());
            task.setSpVendorName(vendorNameET.getText().toString());
        } else {
            String str = "";
            for (int i = 0; i < vendorList.size(); i++) {
                if (vendorList.get(i).isSelected())
                    str = str + vendorList.get(i).getID() + ",";
            }
            task.setSpVendorName(str);
        }
        if (advancePaidET.getText().length() > 0)
            task.setSpAdvancePaid(advancePaidET.getText().toString());
        else
            task.setSpAdvancePaid("0");
        //Collection
        if (invoiceAmountET.getText().length() > 0)
            task.setInvoiceAmount(invoiceAmountET.getText().toString());
        else
            task.setInvoiceAmount("0");

        if (invoiceDate.length() > 0 && invoiceDateFlag)
            task.setInvoiceDate(invoiceDate);
        else task.setInvoiceDate("");

        if (dueDateFlag && dueDate.length() > 0)
            task.setDueDate(dueDate);
        else task.setDueDate("");

        if (outstandingAmtET.getText().length() > 0)
            task.setOutStandingAmt(outstandingAmtET.getText().toString());
        else
            task.setOutStandingAmt("0");
        task.setLongitude("0");
        task.setLatitude("0");

        String address = "";
        if (etPermAdd1.getText().toString().trim().length() > 0)
            address = address + " " + etPermAdd1.getText().toString();
        if (etPermAdd2.getText().toString().trim().length() > 0)
            address = address + ", " + etPermAdd2.getText().toString();
        if (etPermCity.getText().toString().trim().length() > 0)
            address = address + ", " + etPermCity.getText().toString();
        if (etPermState.getText().toString().trim().length() > 0)
            address = address + ", " + etPermState.getText().toString();
        if (etPermCountry.getText().toString().trim().length() > 0)
            address = address + ", " + etPermCountry.getText().toString();
        if (etPermPincode.getText().toString().trim().length() > 0)
            address = address + " - " + etPermPincode.getText().toString();
        task.setWorkLocation(address);
    }

    private void uploadData() {
        HashMap<String, String> localmap = new HashMap<String, String>();
        localmap.put("workitemid", "" + taskId);
        localmap.put("userid", "" + task.getUserCode());
        localmap.put("companyid", "" + Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ID));
        localmap.put("name", "" + task.getTitle());
        localmap.put("description", "" + task.getDescription());
        localmap.put("budget", task.getBudget());
        localmap.put("priority", task.getPriority());
        localmap.put("location", task.getWorkLocation());
        localmap.put("longitude", "" + 0.0);
        localmap.put("latitude", "" + 0.0);
        localmap.put("startdatetime", task.getStartDate());
        localmap.put("enddatetime", task.getEndDate());
        localmap.put("estimatedtime", task.getEstimatedWorkTime());
        localmap.put("tolist", task.getTaskAssignedTo());
        localmap.put("cclist", task.getTaskCCTo());
        localmap.put("worktype", task.getTaskType());
        localmap.put("frequency", task.getFrequency());
        localmap.put("daycodes", task.getDayCodesSelected());
        if (task.getInvoiceDate() != null && task.getInvoiceDate().length() > 3)
            localmap.put("invoicedate", task.getInvoiceDate());
        localmap.put("invoiceamt", task.getInvoiceAmount());
        localmap.put("outstandingamt", task.getOutStandingAmt());
        localmap.put("customername", task.getCustomerName());
        localmap.put("customerno", task.getCustomerContact());
        localmap.put("customertype", task.getCustomerType());
        if (task.getDueDate() != null && task.getDueDate().length() > 3)
            localmap.put("duedate", task.getDueDate());
        localmap.put("vendorpreferrance", task.getSpVendorPreference());
        localmap.put("advancepaid", task.getSpAdvancePaid());
        localmap.put("vendorname", task.getSpVendorName());
        localmap.put("vendorno", "");
        if (task.getPastHistory() != null && task.getPastHistory().length() > 0)
            localmap.put("pasthistory", "" + task.getPastHistory());
        else
            localmap.put("pasthistory", "");

        localmap.put("projectid", "" + task.getProjectCode());
        localmap.put("dependenton", task.getTaskCodeAfter());
        Log.e("hashmap", localmap.toString());

        String t = "";
        for (int i = 0; i < attachmentData.size(); i++) {
            t = t + attachmentData.get(i).get("attachmentid") + ",";
        }
        localmap.put("attachmentlist", t);

        HashMap<String, String> receiveMap = new HashMap<String, String>();
        receiveMap.put("id", "" + 101);
        receiveMap.put("taskimage", task.getTaskImage());
        String p = "";
        for (int i = 0; i < attachmentData.size(); i++) {
            p = p + attachmentData.get(i).get("attachmentid") + ",";
        }
        receiveMap.put("attachmentpathdata", p);

//        for (int i = 0; i < attachmentData.size(); i++) {
//            if (attachmentData.get(i) != null) {
//                ContentValues taskValues = new ContentValues();
//                int id = rand.nextInt(Integer.MAX_VALUE);
//                taskValues.put("Attachment_Id", attachmentData.get(i).get("attachmentid"));
//                taskValues.put("User_Id", task.getUserCode());
//                taskValues.put("WorkItem_Id", task.getTaskCode());
//                taskValues.put("Path", attachmentData.get(i).get("file_path"));
//                taskValues.put("Uploaded_Downloaded", "false");
//                db.insert(Constant.AttachmentTable, null, taskValues);
//            }
//        }


        if (task.getTaskType().equals(Constant.WORKTYPES[Constant.PROJECT])) {
            if (Util.isOnline(getApplicationContext())) {
                CreateTask createTask = new CreateTask(task.getTaskImagePath(), localmap, receiveMap, Constant.URL + "createWorkItem", db, getApplicationContext(), 0);
                createTask.execute();
                saveIntoDatabase(localmap);
            } else {
                toast(getResources().getString(R.string.network_error));
            }
        } else {
            CreateTask createTask = new CreateTask(task.getTaskImagePath(), localmap, receiveMap, Constant.URL + "createWorkItem", db, getApplicationContext(), 0);
            createTask.execute();
            saveIntoDatabase(localmap);
        }

    }

    public void saveIntoDatabase(HashMap toSendHashMap) {
        ContentValues taskValues = new ContentValues();
        taskValues.put(WorkItem.TASK_ID, "" + toSendHashMap.get("workitemid"));
        taskValues.put(WorkItem.USER_CODE, "" + toSendHashMap.get("userid"));
        taskValues.put(WorkItem.TASK_NAME, "" + toSendHashMap.get("name"));
        taskValues.put(WorkItem.DESCRIPTION, "" + toSendHashMap.get("description"));
        taskValues.put(WorkItem.BUDGET, "" + toSendHashMap.get("budget"));
        taskValues.put(WorkItem.PRIORITY, "" + toSendHashMap.get("priority"));
        taskValues.put(WorkItem.ATTACHMENTS, "" + toSendHashMap.get("attachmentlist"));
        taskValues.put(WorkItem.WORK_LOCATION, "" + toSendHashMap.get("location"));
        taskValues.put(WorkItem.WORK_LONGITUDE, "" + toSendHashMap.get("longitude"));
        taskValues.put(WorkItem.WORK_LATITUDE, "" + toSendHashMap.get("latitude"));
        taskValues.put(WorkItem.START_DATE, "" + toSendHashMap.get("startdatetime"));
        taskValues.put(WorkItem.END_DATE, "" + toSendHashMap.get("enddatetime"));
        taskValues.put(WorkItem.ESTIMATED_TIME, "" + toSendHashMap.get("estimatedtime"));
        taskValues.put(WorkItem.ASSIGNED_TO, "" + toSendHashMap.get("tolist"));
        taskValues.put(WorkItem.CC_TO, "" + toSendHashMap.get("cclist"));
        taskValues.put(WorkItem.TASK_TYPE, "" + toSendHashMap.get("worktype"));
        taskValues.put(WorkItem.PROJECT_CODE, "" + toSendHashMap.get("projectid"));
        taskValues.put(WorkItem.TASK_AFTER, "" + toSendHashMap.get("dependenton"));
        taskValues.put(WorkItem.FREQUENCY, "" + toSendHashMap.get("frequency"));
        taskValues.put(WorkItem.DAYCODES_SELECTED, "" + toSendHashMap.get("daycodes"));
        taskValues.put(WorkItem.CUSTOMER_NAME, "" + toSendHashMap.get("customername"));
        taskValues.put(WorkItem.CUSTOMER_CONTACT, "" + toSendHashMap.get("customerno"));
        taskValues.put(WorkItem.CUSTOMER_TYPE, "" + toSendHashMap.get("customertype"));

        if (task.getDueDate() != null && task.getDueDate().length() > 3)
            taskValues.put(WorkItem.DUE_DATE, "" + toSendHashMap.get("duedate"));
        taskValues.put(WorkItem.PAST_HISTORY, "" + toSendHashMap.get("pasthistory"));
        taskValues.put(WorkItem.VENDOR_PREFERENCE, "" + toSendHashMap.get("vendorpreferrance"));
        taskValues.put(WorkItem.VENDOR_NAME, "" + toSendHashMap.get("vendorname"));
        taskValues.put(WorkItem.ADVANCE_PAID, "" + toSendHashMap.get("advancepaid"));
        taskValues.put(WorkItem.INVOICE_AMOUNT, "" + toSendHashMap.get("invoiceamt"));
        if (task.getInvoiceDate() != null && task.getInvoiceDate().length() > 3)
            taskValues.put(WorkItem.INVOICE_DATE, "" + toSendHashMap.get("invoicedate"));
        taskValues.put(WorkItem.OUTSTANDING_AMT, "" + toSendHashMap.get("outstandingamt"));
        taskValues.put(WorkItem.TASK_IMAGE, "" + task.getTaskImage());
        taskValues.put(WorkItem.STATUS, Constant.offline);
        db.insert(Constant.WorkItemTable, null, taskValues);
    }

    String getFilename(String path) {
        String name[] = path.split("/");
        return name[name.length - 1];
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
                    attachmentAdapter = new AttachmentAdapter(CreateWorkActivity.this, attachmentData);
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
        task.setEstimatedWorkTime(hh + "," + mm);
        hours = (int) (((int) tempHr) + Hours);
        minutes = (int) Minutes;
        wrkngHrs = workingHrs;

        CheckListener = true;
    }

    private void createURI() {
        String fileName = "task" + rand.nextInt(Integer.MAX_VALUE) + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        globalUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * ********* Convert Image Uri path to physical path *************
     */

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

                }
            }
        } finally {

        }
        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )
        return "" + imageID;
    }

    /**
     * Async task for loading the images from the SD card.
     *
     * @author Android Example
     */
// Class with extends AsyncTask class
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
                /********* Cancel execution of this task. **********/
                cancel(true);
            }
            return newBitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            // NOTE: You can call UI Element here.
            // Close progress dialog
            if (bitmap != null) {
                task.setTaskImagePath(camera_pathname);
                task.setTaskImage("file://" + camera_pathname);
                Log.i("TAG", "After File Created  " + camera_pathname);
                selectedImage.setImageBitmap(bitmap);
            }
        }
    }

    class searchPincode extends AsyncTask<Void, String, String> {
        String strPincode;

        private searchPincode(String strPincode) {
            this.strPincode = strPincode;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("Pincode", "" + strPincode));
            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL1
                    + "searchPincode", 2, params1, getApplicationContext());
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("result", ">>" + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if (status.equals("Success")) {
                    etPermCity.setText("" + new JSONObject(jsonObject.optString("data")).optString("city"));
                    etPermState.setText("" + new JSONObject(jsonObject.optString("data")).optString("state"));
                    etPermCountry.setText("" + new JSONObject(jsonObject.optString("data")).optString("country"));
                } else {
                    Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            Log.e("Resp Upload 222", "" + resp);
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

                if (flag) {
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


    class CreateTask extends AsyncTask<Void, String, String> {
        String FilePath;
        HashMap<String, String> hashMap, localMap;
        String ApiName;

        SQLiteDatabase db;
        Context context;
        int offLineId = 0;

        public CreateTask(String Path, HashMap<String, String> hMap, HashMap<String, String> lMap, String API, SQLiteDatabase db, Context context, int offLineid) {
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
            if (progressDialog != null) progressDialog.show();
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

                    try {
                        JSONObject jObj = new JSONObject(resp);
                        String status = jObj.optString("status");
                        if (!status.equals(Constant.InvalidToken)) {
                            HandleResponseWorkItems.HandleCreateWorkItemResponse(FilePath, ApiName, hashMap, localMap, resp, db, context, offLineId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    /// IF Exception occurs call will be saved to offline database
                    if (offLineId <= 0) {
                        OfflineWork.StoreData(FilePath, hashMap, localMap, ApiName, db);
                        Toast.makeText(CameraActivity, getString(R.string.assing_work_item), Toast.LENGTH_SHORT).show();
                    }
                    return Constant.offline;
                }
                Log.e("Resp Upload 1111", "" + resp);
                return resp;
            } else {
                if (offLineId <= 0) {
                    OfflineWork.StoreData(FilePath, hashMap, localMap, ApiName, db);
                    Toast.makeText(CameraActivity, getString(R.string.assing_work_item), Toast.LENGTH_SHORT).show();
                }
                return Constant.offline;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.e("CreateWorkActivity", "Received update :-" + response);

            if (response.equals(Constant.offline)) {
                finish();
                overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
            } else {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.optString("status");
                    if (status.equals(Constant.InvalidToken)) {
                        TeamWorkApplication.LogOutClear(CreateWorkActivity.this);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    toast(jsonObject.optString("message"));
                    if (jsonObject.optString("status").equals("Success")) {
                        finish();
                        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (progressDialog != null) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }

        }
    }

}
