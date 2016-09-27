package com.bigbang.superteam.login_register;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.CommonGCM;
import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.GCMIntentService;
import com.bigbang.superteam.Privileges;
import com.bigbang.superteam.R;
import com.bigbang.superteam.WorkItem_GCM;
import com.bigbang.superteam.admin.AdminDashboardNewActivity;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.Attachment;
import com.bigbang.superteam.dataObjs.User_Location;
import com.bigbang.superteam.manager.ManagerDashboardNewActivity;
import com.bigbang.superteam.model.Address;
import com.bigbang.superteam.model.Company_info;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.model.Holidays;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.task.database.ExpenceDAO;
import com.bigbang.superteam.task.database.TaskAttachmentDAO;
import com.bigbang.superteam.task.database.TaskChatDAO;
import com.bigbang.superteam.task.database.TaskDAO;
import com.bigbang.superteam.task.database.TaskMemberDAO;
import com.bigbang.superteam.task.model.Expense;
import com.bigbang.superteam.task.model.TaskAttachment;
import com.bigbang.superteam.task.model.TaskChat;
import com.bigbang.superteam.task.model.TaskMember;
import com.bigbang.superteam.task.model.TaskModel;
import com.bigbang.superteam.user.UserDashboardNewActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @InjectView(R.id.etMobile)
    EditText etMobile;
    @InjectView(R.id.etPassword)
    EditText etPassword;
    TransparentProgressDialog progressDialog;
    TeamWorksDBDAO teamWorksDBDAO;
    MyReceiver myReceiver;
    Activity activity;
    Holidays holidays;
    HolidaysDAO holidaysDAO;

    Company_info company_info;
    CompanyInfoDAO companyInfoDAO;
    String defaultCompanySetup = "{\"companyid\":1, \"deducution\":20, \"deducutionHour1\":15, " +
            "\"deducutionHour2\":30, \"deducutionHour3\":50, \"deducutionHour4\":60, \"deducutionHour4On\":80,\n" +
            "\"basicSalary\":40, \"hra\":10, \"conveyance\":1000, \"medical\":1000, \"telephone\":1000, " +
            "\"lta\":1000, \"specialIncentive\":0, \"otherAllownace\":0, \"pfEmployee\":1000, \"pfEmployer\":1000, \n" +
            "\"profTax\":200, \"tds\":0, \"otherDeduction\":0, \"notificationLevel\":2, \"slBal\":10, \"plBal\":10, " +
            "\"olBal\":10, \"clBal\":10, \"name\":\"test\", \"workingDays\":\"Mon,Tue,Wed,Thu,Fri\", \"startTime\":" +
            "\"09:00:00\", \n" +
            "\"endTime\":\"18:00:00\", \"payrollCycle\":\"Monthly\", \"payrollStart\":1, \"payrollEnd\":30, " +
            "\"salaryBreakupType\":\"Flat\", \"isPayrollEnabled\":true, \"isRegularizationAllowed\":true," +
            "\"isLateDeductionOn\":true}";


    public static final int GET_TASKS = 0;
    public static final int GET_PROJECTS = 1;
    public static final int GET_PRIVILAGES = 2;
    public static final int GET_COMPANY_PRIVILAGES = 3;
    public static final int GET_USER_PRIVILAGES = 4;
    public static final int GET_APPROVALS = 5;
    public static final int GET_NOTIFICATIONS = 6;
    public static final int GET_DEFAULTSETTINGS = 7;
    public static final int GET_USER_COMPANYINFO = 8;
    public static final int GET_COMPANYINFO = 9;
    public static final int UPDATE_TRACKING = 10;
    public static final String APIS[] = {
            "getActiveTask",
            "getProjectByUser",
            "getPrivileges",
            "getCompanyPrivileges",
            "getUserPrivileges",
            "getNotifications",
            "getNotifications",
            "getDefaultSettings",
            "getUserCompanyInfo",
            "getCompanyInfo",
            "updateUserLocation"
    };
    //public static final String APIS[] = {"getWorkItemByUser", "getWorkItemUpdateByUser", "getWorkItemAttachmentByUser", "getProjectByUser", "getPrivileges", "getCompanyPrivileges", "getUserPrivileges", "getDefaultSettings", "getUserCompanyInfo", "getCompanyInfo"};
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (read(Constant.SHRED_PR.KEY_REPLACE_USERNAME).equals("1")) {
            etMobile.setText(read(Constant.SHRED_PR.KEY_TELEPHONE));
            write(Constant.SHRED_PR.KEY_REPLACE_USERNAME, "0");
        }

        try {
            myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(GCMIntentService.ACTIVITY_SERVICE);
            registerReceiver(myReceiver, intentFilter);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        try {
            unregisterReceiver(myReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (progressDialog != null) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        }
        super.onDestroy();
    }

    private void init() {

        SQLiteHelper helper = new SQLiteHelper(LoginActivity.this, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        activity = this;
        teamWorksDBDAO = new CompanyInfoDAO(LoginActivity.this);
        companyInfoDAO = new CompanyInfoDAO();
        holidaysDAO = new HolidaysDAO();

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        progressDialog = new TransparentProgressDialog(LoginActivity.this, R.drawable.progressdialog, false);

        //set default:
        write(Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA, defaultCompanySetup);
        write(Constant.SHRED_PR.KEY_FILTER_PRIORITY, "0");
        write(Constant.SHRED_PR.KEY_FILTER_ASSIGNED, "");
        write(Constant.SHRED_PR.KEY_FILTER_TASK_TYPE, "");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Util.GCM(LoginActivity.this);
            }
        }, 100);

        try {
            TeamWorkApplication.LogInClear(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        etMobile.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etMobile.getText().length() == 1 && getText(etMobile).equals("0")) {
                    etMobile.setText("");
                    toast(getResources().getString(R.string.mobile_not_zero));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            Bundle extras = arg1.getExtras();
            if (extras != null) {
                String OTP = extras.getString("OTP");
                etPassword.setText(OTP);
                //btnLogin.performClick();
            }
        }
    }

    @OnClick(R.id.rlForgotPassword)
    @SuppressWarnings("unused")
    public void ForgotPassword(View view) {
        startActivity(ForgotPasswordActivity.class);
        overridePendingTransition(R.anim.enter_from_bottom,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlRegister)
    @SuppressWarnings("unused")
    public void registerUser(View view) {
        startActivity(RegisterActivity.class);
        overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlLogin)
    @SuppressWarnings("unused")
    public void login(View view) {
        hideKeyboard();
        if (isValidate()) {
            if (Util.isOnline(getBaseContext())) {
                if (isEmpty(read(Constant.SHRED_PR.KEY_GCM_ID))) Util.GCM(LoginActivity.this);
                login(getText(etMobile), getText(etPassword));
            } else {
                toast(Constant.network_error);
            }
        }
    }

    public boolean isValidate() {
        if (getText(etMobile).length() != 10) {
            toast(R.string.PleaseEnterMobile);
            return false;
        }

        if (isEmpty(getText(etPassword))) {
            toast(R.string.PleaseEnterPassword);
            return false;
        }
        return true;
    }

    private void login(String username, final String password) {
        teamWorksDBDAO.open();
        try {
            if (progressDialog != null) progressDialog.show();
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        }

        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        RestClient.getCommonService().login(username, password, android_id, Constant.AppName, "F", read(Constant.SHRED_PR.KEY_GCM_ID),
                new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        try {
                            if (progressDialog != null)
                                if (progressDialog.isShowing()) progressDialog.dismiss();
                        } catch (final IllegalArgumentException e) {
                            // Handle or log or ignore
                        } catch (final Exception e) {
                            // Handle or log or ignore
                        }

                        try {
                            Log.i(TAG, "USER DETAIL :: " + user);
                            Log.i(TAG, Util.getString(response.getBody().in()));

                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.UnRegDevice)) {

                                AlertDialog.Builder alert1 = new AlertDialog.Builder(activity);
                                alert1.setTitle("" + Constant.AppNameSuper);
                                alert1.setMessage(jObj.optString("message"));
                                alert1.setPositiveButton("Continue",
                                        new DialogInterface.OnClickListener() {
                                            @SuppressLint("InlinedApi")
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {

                                                if (Util.isOnline(activity)) {
                                                    changeRegDevice(getText(etMobile));
                                                } else {
                                                    Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                alert1.setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                // TODO Auto-generated method stub
                                                dialog.cancel();
                                            }
                                        });
                                alert1.create();
                                alert1.show();

                                return;
                            }

                            Map<String, String> map = Util.readStatus(response);
                            boolean isSuccess = map.get("status").equals("Success");
                            if (isSuccess && user != null) {
                                try {
                                    TeamWorkApplication.LogInClear(getApplicationContext());
//                                    if (!user.getUserID().toString().equals(read(Constant.SHRED_PR.KEY_USERID))) {
//                                        TeamWorkApplication.LogInClear();
//                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, "ERROR WHILE CLEARING LOGIN :: " + e.getMessage());
                                }

                                //User Info:
                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                                write(Constant.SHRED_PR.KEY_TOKEN, "" + new JSONObject(jsonObject.optString("data")).optString("SecurityToken"));

                                //write(Constant.SHRED_PR.KEY_IS_LOGGEDIN, "true");
                                write(Constant.SHRED_PR.KEY_PASSWORD, password);
                                write(Constant.SHRED_PR.KEY_USERID, user.getUserID() + "");
                                write(Constant.SHRED_PR.KEY_FIRSTNAME, user.getFirstName());
                                write(Constant.SHRED_PR.KEY_LASTNAME, user.getLastName());
                                write(Constant.SHRED_PR.KEY_TELEPHONE, user.getMobileNo1());
                                write(Constant.SHRED_PR.KEY_ROLE, user.getRole().getDesc());
                                write(Constant.SHRED_PR.KEY_ROLE_ID, user.getRole().getId() + "");
                                write(Constant.SHRED_PR.KEY_EMAIL, user.getEmailID());
                                write(Constant.SHRED_PR.KEY_Picture, user.getPicture());
                                write(Constant.SHRED_PR.KEY_PermanentAddress, new Gson().toJson(user.getPermanentAddress()));
                                write(Constant.SHRED_PR.KEY_TemporaryAddress, new Gson().toJson(user.getTemporaryAddress()));

                                //Company Info:
                                String id = user.getCompany() != null ? user.getCompany().getID() + "" : "0";
                                write(Constant.SHRED_PR.KEY_COMPANY_ID, id);

                                if (!id.equals("0")) {
                                    write(Constant.SHRED_PR.KEY_COMPANY_NAME, user.getCompany().getName());
                                    write(Constant.SHRED_PR.KEY_COMPANY_LOGO, user.getCompany().getLogo());
                                    write(Constant.SHRED_PR.KEY_COMPANY_MOBILE, user.getCompany().getMobileNo());
                                    write(Constant.SHRED_PR.KEY_COMPANY_LANDLINE, user.getCompany().getLandlineNo());
                                    write(Constant.SHRED_PR.KEY_COMPANY_EMAIL, user.getCompany().getEmailID());
                                    write(Constant.SHRED_PR.KEY_COMPANY_Owner_ID, user.getCompany().getOwnerID().toString());
                                    write(Constant.SHRED_PR.KEY_COMPANY_Created_By, user.getCompany().getCreatedBy().toString());
                                    write(Constant.SHRED_PR.KEY_COMPANY_Type, "" + user.getCompany().getCompanyTypeID());
                                    write(Constant.SHRED_PR.KEY_COMPANY_DESC, user.getCompany().getDescription());
                                    write(Constant.SHRED_PR.KEY_COMPANY, new Gson().toJson(user.getCompany()));
                                    //write(Constant.SHRED_PR.KEY_COMPANY_AddressList, new Gson().toJson(user.getCompany().getAddressList()));
                                    try {
                                        write(Constant.SHRED_PR.KEY_COMPANY_AddressList, "" + new JSONArray(new JSONObject(new JSONObject(jsonObject.optString("data")).optString("company")).optString("AddressList")));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                String roleId = read(Constant.SHRED_PR.KEY_ROLE_ID);
                                if (roleId.equals("1") || roleId.equals("2")) {
                                    if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_ID).equals("0")) {
                                        write(Constant.SHRED_PR.KEY_IS_LOGGEDIN, "true");
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("from", "" + Constant.FROM_LOGIN);
                                        startActivityWithData(InvitationActivity.class, hashMap);
                                        finish();
                                    } else {
                                        write(Constant.SHRED_PR.KEY_IS_COMPANY_CREATED, "true");
                                        if (Util.isOnline(getApplicationContext())) {
                                            new getCompanyInfo(roleId).execute();
                                        } else {
                                            toast(Constant.network_error);
                                        }
                                    }
                                } else {
                                    if (Util.isOnline(getApplicationContext())) {
                                        new Synchronise("" + read(Constant.SHRED_PR.KEY_USERID), 0).execute();
                                    } else {
                                        toast(Constant.network_error);
                                    }
                                }
                            } else {
                                toast(map.get("message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        try {
                            if (progressDialog != null)
                                if (progressDialog.isShowing()) progressDialog.dismiss();
                        } catch (final IllegalArgumentException e) {
                            // Handle or log or ignore
                        } catch (final Exception e) {
                            // Handle or log or ignore
                        }

                        Log.d(TAG, "login::failure ==>" + error, error);
                        toast(getResources().getString(R.string.some_error));
                        Util.GCM(LoginActivity.this);
                    }
                });

    }

    public class getCompanyInfo extends AsyncTask<Void, String, String> {

        String roleID;

        private getCompanyInfo(String roleID) {
            this.roleID = roleID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (!progressDialog.isShowing()) {
                    if (progressDialog != null) progressDialog.show();
                }
            } catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            } catch (final Exception e) {
                // Handle or log or ignore
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String resp = "";

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("CompanyID", Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_ID)));

            Log.d("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + "getCompanyInfo", 1, params1, getApplicationContext());

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Response", ">>" + result);

            try {
                if (progressDialog != null)
                    if (progressDialog.isShowing()) progressDialog.dismiss();
            } catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            } catch (final Exception e) {
                // Handle or log or ignore
            }

            try {
                JSONObject jsonObject = new JSONObject(result);

                String status = jsonObject.getString("status");
                if (status.equals("Success")) {
                    write(Constant.SHRED_PR.KEY_IS_COMPANY_SETUP, "true");

                    Log.d("", "********** Test: " + jsonObject.getString("companydata"));

                    JSONObject j = jsonObject.getJSONObject("companydata");

                    write(Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA, jsonObject.getString("companydata"));

                    //set payroll data in preferences
                    setPayrollDataInPreferences(j);

                    new Synchronise("" + read(Constant.SHRED_PR.KEY_USERID), 0).execute();
                } else {
                    if (roleID.equals("1")) {
                        write(Constant.SHRED_PR.KEY_IS_LOGGEDIN, "true");
                        write(Constant.SHRED_PR.KEY_IS_COMPANY_SETUP, "false");
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("position", "1");
                        startActivityWithData(CompanySetupActivity.class, hashMap);
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class Synchronise extends AsyncTask<Void, String, String> {
        String UserID;
        int Code;

        public Synchronise(String Userid, int code) {
            UserID = Userid;
            Code = code;
            Log.d(TAG, "In Synchronise Cunstructor");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (!progressDialog.isShowing()) {
                    if (progressDialog != null) progressDialog.show();
                }
            } catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            } catch (final Exception e) {
                // Handle or log or ignore
            }

            Log.d(TAG, "In Synchronise Pre");
        }

        @Override
        protected String doInBackground(Void... params) {
            String resp = "";
            Log.d(TAG, "============== In Synchronise background Task ======================");
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            String NotificationType, response;
            switch (Code) {
                case GET_APPROVALS:
                    //GET API
                    NotificationType = Constant.APPROVAL_NEWPRJECT + "," + Constant.CREATE_NEWWORKITEM + ","
                            + Constant.EDIT_PROJECT_APPROVAL + "," + Constant.APPROVE_WORKITEMUPDATE + ","
                            + Constant.EDIT_WORKITEM_APPROVAL + "," + Constant.CHANGE_MOBILE_APPROVAL_TO_ADMIN + ","
                            + Constant.USER_RESIGN_APPROVAL_TO_ADMIN + "," + Constant.ATTENDANCE_APPROVAL_TO_ADMIN + ","
                            + Constant.LEAVE_APPROVAL_TO_ADMIN + "," + Constant.UPDATETIME_APPROVAL_TO_ADMIN + ","
                            + Constant.UPDATE_CUSTOMER_APPROVAL_TO_ADMIN + "," + Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_ADMIN
                            + "," + Constant.CHANGE_DEVICE_REQUEST + "," + Constant.EXPENSE_APPROVAL;
                    params1.add(new BasicNameValuePair("Application", "" + Constant.AppName));
                    params1.add(new BasicNameValuePair("Type", NotificationType));
                    Log.d("params1", ">>" + params1);
//                    params1.add(new BasicNameValuePair("PageNo", "1"));
//                    params1.add(new BasicNameValuePair("PageSize", "10"));
                    response = Util.makeServiceCall(Constant.URL1 + APIS[Code], 1, params1, getApplicationContext());
                    return response;
                case GET_NOTIFICATIONS:
                    //GET API
                    NotificationType = "" + Constant.WORKITEM_MODIFIED + "," + Constant.CHANGE_MOBILE_APPROVAL_TO_USER
                            + "," + Constant.USER_RESIGN_APPROVAL_TO_USER + "," + Constant.NEW_USER_ADDED + ","
                            + Constant.NEW_REPORTING_MEMBER + "," + Constant.DELETE_USER + ","
                            + Constant.USER_INVITATION_TO_ADMIN + "," + Constant.INVITE_USER + ","
                            + Constant.UPDATE_ROLE_OR_MANAGER + "," + Constant.ATTENDANCE_APPROVAL_TO_USER + ","
                            + Constant.LEAVE_APPROVAL_TO_USER + "," + Constant.UPDATETIME_APPROVAL_TO_USER + ","
                            + Constant.LEAVE_WITHDRAW_TO_ADMIN + "," + Constant.LEAVE_UPDATE_TO_USER + ","
                            + Constant.UPDATE_CUSTOMER_APPROVAL_TO_USER + "," + Constant.NEW_WORKITEM + ","
                            + Constant.REJECT_NEWWORKITEM + "," + Constant.UPDATE_WORKITEM + ","
                            + Constant.REJECT_WORKITEMUPDATE + "," + Constant.CREATE_NEWPRJECT + ","
                            + Constant.MODIFIED_PROJECT + "," + Constant.REJECT_NEWPRJECT + ","
                            + Constant.PROJECT_APPROVED + "," + Constant.PROJECT_REJECTED + ","
                            + Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_USER + ","
                            + Constant.DELETE_COMPANY + "," + Constant.ATTACHMENTS + ","
                            + Constant.CHANGE_DEVICE_ACK + "," + Constant.UPDATE_USER_COMPANYINFO + ","
                            + Constant.ADMIN_PAYROLL + "," + Constant.USER_PAYSLIP
                            + "," + Constant.EXPENSE_NOTIFICATION;
                    params1.add(new BasicNameValuePair("Application", "" + Constant.AppName));
                    params1.add(new BasicNameValuePair("Type", NotificationType));
//                    params1.add(new BasicNameValuePair("PageNo", "1"));
//                    params1.add(new BasicNameValuePair("PageSize", "10"));

                    Log.d("params1", ">>" + params1);
                    response = Util.makeServiceCall(Constant.URL1 + APIS[Code], 1, params1, getApplicationContext());
                    return response;
                case UPDATE_TRACKING:
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String DateTime = sdf.format(date);
                    DateTime = Util.locatToUTC(DateTime);
                    directCallAPIWithoutAsync(
                            Util.ReadSharePrefrence(LoginActivity.this, "track_lat"),
                            Util.ReadSharePrefrence(LoginActivity.this, "track_lng"),
                            DateTime);
                default:
                    params1.add(new BasicNameValuePair("CompanyID", "" + read(Constant.SHRED_PR.KEY_COMPANY_ID)));
                    params1.add(new BasicNameValuePair("RoleID", "" + read(Constant.SHRED_PR.KEY_ROLE_ID)));
                    params1.add(new BasicNameValuePair("Type", "default"));
                    params1.add(new BasicNameValuePair("MemberID", "" + read(Constant.SHRED_PR.KEY_USERID)));

                    Log.d("params1", ">>" + params1);
                    Log.d("params1", "this is default case inside the login : " + params1);
                    response = Util.makeServiceCall(Constant.URL + APIS[Code], 1, params1, getApplicationContext());
                    return response;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Response for :", APIS[Code] + ":: " + s);

            boolean flag = true;
            if (s == null) flag = false;
            try {
                JSONObject jObj = new JSONObject(s);
                String status = jObj.getString("status");
                Log.d(TAG, "response is:- " + s);
                if (status.equals(Constant.InvalidToken)) {
                    flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("flag", ">>" + flag + ":" + APIS[Code]);
            if (flag) {
                HandleResponse(s, Code);

                if (Code < APIS.length - 1) {
                    Code++;
                    new Synchronise(UserID, Code).execute();
                } else {
                    getUsers();
                }
            } else {
                //logout:
                TeamWorkApplication.LogOutClear(LoginActivity.this);
            }
        }
    }

    List<String> arrAttachment = new ArrayList<>();

    private void HandleResponse(String s, int code) {
        if (s != null) {
            int roleId = Integer.parseInt(read(Constant.SHRED_PR.KEY_ROLE_ID));
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonObject != null) {
                try {
                    if (jsonObject.getString("status").equals("Success")) {
                        switch (code) {
                            case GET_TASKS:
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
//                                if (jsonArray.length() >= 0) return;

                                TaskDAO taskDAO = new TaskDAO(LoginActivity.this);
                                TaskMemberDAO taskMemberDAO = new TaskMemberDAO(LoginActivity.this);
                                TaskAttachmentDAO taskMemberDAO1 = new TaskAttachmentDAO(LoginActivity.this);
                                TaskChatDAO taskChatDAO = new TaskChatDAO(LoginActivity.this);
                                ExpenceDAO expenceDAO = new ExpenceDAO(LoginActivity.this);

                                TaskModel taskModel = null;

                                Log.d(TAG, "Inside GET_WORKITEM switch case ***");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Log.d(TAG, "Inside for loop i is:- " + i);
//                                    WorkItem_GCM.InsertIntoWorkItem((JSONObject) jsonArray.get(i), LoginActivity.this);

                                    taskModel = new TaskModel();

                                    taskModel.taskID = jsonArray.getJSONObject(i).optInt("taskID");
                                    taskModel.companyID = jsonArray.getJSONObject(i).optInt("companyID");
                                    taskModel.name = jsonArray.getJSONObject(i).optString("name");
                                    taskModel.description = jsonArray.getJSONObject(i).optString("description");
                                    taskModel.taskType = jsonArray.getJSONObject(i).optString("taskType");
                                    taskModel.address_id = jsonArray.getJSONObject(i).getJSONObject("address").optInt("AddressID");
                                    taskModel.addressStr = jsonArray.getJSONObject(i).getJSONObject("address").optString("AddressLine1") + ", " +
                                            jsonArray.getJSONObject(i).getJSONObject("address").optString("AddressLine2") + ", " +
                                            jsonArray.getJSONObject(i).getJSONObject("address").optString("City") + ", " +
                                            jsonArray.getJSONObject(i).getJSONObject("address").optString("State") + ", " +
                                            jsonArray.getJSONObject(i).getJSONObject("address").optString("Country");

                                    taskModel.invoiceAmount = jsonArray.getJSONObject(i).optDouble("invoiceAmount");
                                    taskModel.invoiceDate = jsonArray.getJSONObject(i).optString("invoiceDate");
                                    taskModel.outstandingAmount = jsonArray.getJSONObject(i).optDouble("outstandingAmount");
                                    taskModel.pastHistory = jsonArray.getJSONObject(i).optString("pastHistory");
                                    taskModel.customerType = jsonArray.getJSONObject(i).optString("customerType");
                                    taskModel.advancePaid = jsonArray.getJSONObject(i).optDouble("advancePaid");
                                    taskModel.vendorPreference = jsonArray.getJSONObject(i).optString("vendorPreference");
                                    taskModel.daycodes = jsonArray.getJSONObject(i).optString("daycodes");
                                    taskModel.frequency = jsonArray.getJSONObject(i).optString("frequency");

                                    //Address
                                    Address address = new Address();
                                    address.setAddressID(jsonArray.getJSONObject(i).getJSONObject("address").optInt("AddressID"));
                                    address.setCountry(jsonArray.getJSONObject(i).getJSONObject("address").optString("Country"));
                                    address.setState(jsonArray.getJSONObject(i).getJSONObject("address").optString("State"));
                                    address.setCity(jsonArray.getJSONObject(i).getJSONObject("address").optString("City"));
                                    address.setAddressLine1(jsonArray.getJSONObject(i).getJSONObject("address").optString("AddressLine1"));
                                    address.setAddressLine2(jsonArray.getJSONObject(i).getJSONObject("address").optString("AddressLine1"));
                                    address.setPincode(jsonArray.getJSONObject(i).getJSONObject("address").optString("Pincode"));
                                    address.setLattitude(jsonArray.getJSONObject(i).getJSONObject("address").optString("Lattitude"));
                                    address.setLongitude(jsonArray.getJSONObject(i).getJSONObject("address").optString("Longitude"));

                                    taskModel.address = address;
                                    taskModel.priority = jsonArray.getJSONObject(i).optString("priority");
                                    taskModel.startTime = jsonArray.getJSONObject(i).optString("startTime");
                                    taskModel.endTime = jsonArray.getJSONObject(i).optString("endTime");
                                    taskModel.estimatedTime = jsonArray.getJSONObject(i).optString("estimatedTime");

                                    //Task Member Json Object
                                    taskModel.taskRight = jsonArray.getJSONObject(i).optString("taskRight");

                                    //Created_by JSON object
                                    taskModel.createdByName = jsonArray.getJSONObject(i).getJSONObject("createdBy").optString("firstName") + " " +
                                            jsonArray.getJSONObject(i).getJSONObject("createdBy").optString("lastName");

                                    taskModel.createdById = jsonArray.getJSONObject(i).getJSONObject("createdBy").optInt("userId");

                                    taskModel.status = jsonArray.getJSONObject(i).optString("status");
                                    taskModel.active = jsonArray.getJSONObject(i).getBoolean("active");
                                    taskModel.budget = jsonArray.getJSONObject(i).optDouble("budget");


                                    //Approved_by JSON object
                                    User user2 = new User();
                                    user2.setUserID(jsonArray.getJSONObject(i).getJSONObject("approvedBy").optInt("userId"));
                                    taskModel.approvedBy = user2;

                                    TaskMember taskMember;
                                    //Assigned_to list should be in task member table
                                    JSONArray arrAssignedUsers = jsonArray.getJSONObject(i).getJSONArray("assignedToList");
                                    if (arrAssignedUsers.length() != 0)
                                        for (int j = 0; j < arrAssignedUsers.length(); j++) {
                                            taskMember = new TaskMember();

                                            taskMember.TaskID = jsonArray.getJSONObject(i).optInt("taskID");
                                            taskMember.userID = arrAssignedUsers.getJSONObject(j).optInt("userId");
                                            taskMember.memberType = Constant.MemberTypeAssigned;
                                            taskMember.userName = arrAssignedUsers.getJSONObject(j).optString("firstName");

                                            taskMemberDAO.save(taskMember);
                                        }

                                    //CC to list should be in task member table
                                    JSONArray arrCCUsers = jsonArray.getJSONObject(i).getJSONArray("ccList");
                                    if (arrCCUsers.length() != 0)
                                        for (int j = 0; j < arrCCUsers.length(); j++) {
                                            taskMember = new TaskMember();

                                            taskMember.TaskID = jsonArray.getJSONObject(i).optInt("taskID");
                                            taskMember.userID = arrCCUsers.getJSONObject(j).optInt("userId");
                                            taskMember.memberType = Constant.MemberTypeCC;
                                            taskMember.userName = arrCCUsers.getJSONObject(j).optString("firstName") + " " + arrCCUsers.getJSONObject(j).optString("lastName");

                                            taskMemberDAO.save(taskMember);
                                        }

                                    //Attachment
//                                    JSONArray jArray = jsonArray.getJSONObject(i).getJSONArray("attachments");
//                                    if (jArray.length() != 0) {
//                                        for (int k = 0; k < jArray.length(); k++) {
////                                            arrAttachment.add(jArray.get(k).toString());
//
//                                            Attachment attachment= new Attachment();
//                                            attachment.
//                                        }
//                                    }

//                                    taskModel.attachments = arrAttachment;

                                    TaskChat taskChat;

                                    //CHAT list should be in task member table
                                    JSONArray arrChat = jsonArray.getJSONObject(i).getJSONArray("taskChats");
                                    if (arrChat.length() != 0)
                                        for (int j = 0; j < arrChat.length(); j++) {
                                            taskChat = new TaskChat();

                                            taskChat.taskID = arrChat.getJSONObject(j).optInt("taskID");
                                            taskChat.TaskEditID = arrChat.getJSONObject(j).optInt("TaskEditID");
                                            taskChat.chatType = arrChat.getJSONObject(j).optString("chatType");
                                            taskChat.chatStatus = arrChat.getJSONObject(j).optString("chatStatus");

                                            taskChat.dataType = arrChat.getJSONObject(j).optString("dataType");
                                            taskChat.transactionID = arrChat.getJSONObject(j).optString("transactionID");
                                            taskChat.createdOn = arrChat.getJSONObject(j).optString("createdOn");
                                            taskChat.createdById = arrChat.getJSONObject(j).getJSONObject("createdBy").optInt("userId");
                                            taskChat.createdByName = "";//jsonArray.getJSONObject(i).optString("createdByName");
                                            taskChat.approvedById = arrChat.getJSONObject(j).getJSONObject("approvedBy").optInt("userId");
                                            taskChat.approvedByName = "";// jsonArray.getJSONObject(i).optInt("approvedByName");
                                            taskChat.lastModifiedBy = arrChat.getJSONObject(j).optInt("lastModifiedBy");

                                            if (taskChat.dataType.equals("Text")) {
                                                taskChat.message = arrChat.getJSONObject(j).optString("message");
                                                taskChat.attachmentPath = "";
                                            } else {
                                                taskChat.message = "";
                                                taskChat.attachmentPath = arrChat.getJSONObject(j).optString("attachmentPath");
                                            }


                                            Expense expense = new Expense();

                                            JSONObject chatExpJobj = arrChat.getJSONObject(j).getJSONObject("expense");
                                            expense.expenseId = chatExpJobj.optInt("expenseId");
                                            expense.taskID = chatExpJobj.optInt("taskID");
                                            expense.taskChatID = chatExpJobj.optInt("taskChatID");
                                            expense.expenseAmount = chatExpJobj.optDouble("expenseAmount");
                                            expense.lastModifiedBy = chatExpJobj.optInt("lastModifiedBy");

//                                            taskChat.expense = expense;

                                            expenceDAO.save(expense);

                                            taskChatDAO.save(taskChat);
                                        }


                                    //Customer/Vendor list should be in task member table
                                    JSONArray arrCustVend = jsonArray.getJSONObject(i).getJSONArray("taskCustVend");
                                    if (arrCustVend.length() != 0)
                                        for (int j = 0; j < arrCustVend.length(); j++) {
                                            taskMember = new TaskMember();

                                            taskMember.TaskID = arrCustVend.getJSONObject(j).optInt("taskID");
                                            taskMember.userID = arrCustVend.getJSONObject(j).optInt("custVendorID");
                                            taskMember.userName = arrCustVend.getJSONObject(j).optString("name");
                                            taskMember.memberType = arrCustVend.getJSONObject(j).optString("custVendType");
                                            taskMember.active = arrCustVend.getJSONObject(j).optBoolean("active");
                                            taskMember.contact_num = arrCustVend.getJSONObject(j).optString("contact");

                                            taskMemberDAO.save(taskMember);
                                        }

                                    taskDAO.save(taskModel);
                                }
                                break;
//                            case GET_UPDATES_WORKITEM:
//                                jsonArray = jsonObject.getJSONArray("data");
//                                for (int i = 0; i < jsonArray.length(); i++)
//                                    WorkItem_GCM.InsertIntoWorkItemUpdate((JSONObject) jsonArray.get(i), LoginActivity.this);
//                                break;
                            case GET_PROJECTS:
                                jsonArray = jsonObject.optJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++)
                                    WorkItem_GCM.InsertIntoProjects(jsonArray.optJSONObject(i), LoginActivity.this);
                                break;
//                            case GET_ATTACHMENTS:
//                                jsonArray = jsonObject.getJSONArray("data");
//                                for (int i = 0; i < jsonArray.length(); i++)
//                                    WorkItem_GCM.InsertIntoAttachments((JSONObject) jsonArray.get(i), LoginActivity.this);
//                                break;
                            case GET_PRIVILAGES:
                                Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_ALL_PRIVILAGES, "" + s);
                                break;
                            case GET_COMPANY_PRIVILAGES:
                                if (roleId == 1 || roleId == 2) {
                                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_PRIVILAGES, "" + s);
                                    Privileges.Init(getApplicationContext());
                                }
                                break;
                            case GET_USER_PRIVILAGES:
                                if (roleId == 3 || roleId == 4) {
                                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USER_PRIVILAGES, "" + s);
                                    Privileges.Init(getApplicationContext());
                                }
                                break;
                            case GET_APPROVALS:
                                JSONArray jsonArray1 = jsonObject.getJSONArray("data");
                                write(Constant.SHRED_PR.KEY_LASTSYNC_APPROVAL_TIME, "" + jsonObject.optString("lastSynchTime"));
                                Log.d("Approvals:", ">>" + jsonArray1);
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                    int status = jsonObject1.optInt("Status");
                                    String userID = jsonObject1.optString("UserID");
                                    if (userID.equals(read(Constant.SHRED_PR.KEY_USERID)))
                                        CommonGCM.InsertIntoApprovals(jsonObject1, LoginActivity.this);
                                }
                                break;
                            case GET_NOTIFICATIONS:
                                JSONArray jsonArray2 = jsonObject.getJSONArray("data");
                                write(Constant.SHRED_PR.KEY_LASTSYNC_NOTIFICATION_TIME, "" + jsonObject.optString("lastSynchTime"));
                                Log.d("Notifications:", ">>" + jsonArray2);
                                for (int i = jsonArray2.length() - 1; i >= 0; i--) {
                                    JSONObject jsonObject1 = jsonArray2.getJSONObject(i);
                                    String userID = jsonObject1.optString("UserID");
                                    if (userID.equals(read(Constant.SHRED_PR.KEY_USERID))) {
                                        CommonGCM.InsertIntoNotifications(jsonObject1, LoginActivity.this);
                                    }
                                }
                                break;
                            case GET_DEFAULTSETTINGS:
                                if (roleId == 3 || roleId == 4) {
                                    JSONArray jsonArray3 = jsonObject.optJSONArray("data");
                                    if (jsonArray3.length() > 0)
                                        write(Constant.SHRED_PR.KEY_USER_LOCATION_DETAILS, "" + jsonArray3.getJSONObject(0));
                                }
                                break;
                            case GET_USER_COMPANYINFO:
                                JSONObject jsondata = jsonObject.optJSONObject("data");
                                write(Constant.SHRED_PR.KEY_COMPANY_STARTTIME, jsondata.optString("starttime"));
                                write(Constant.SHRED_PR.KEY_COMPANY_ENDTIME, jsondata.optString("endtime"));

                                break;
                            case GET_COMPANYINFO:
                                String status = jsonObject.optString("status");
                                if (status.equals("Success"))
                                    write(Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA, jsonObject.optString("companydata"));
                                try {
                                    JSONObject jobj = jsonObject.getJSONObject("companydata");

                                    write(Constant.SHRED_PR.KEY_TRACKING_STARTTIME, jobj.optString("trackingStartTime"));
                                    write(Constant.SHRED_PR.KEY_TRACKING_ENDTIME, jobj.optString("trakingEndTime"));

                                    JSONArray holidaysJarr = jobj.getJSONArray("holidays");
                                    if (holidaysJarr != null) {
                                        for (int cnt = 0; cnt < holidaysJarr.length(); cnt++) {
                                            holidays = new Holidays();

                                            holidays.holidayId = holidaysJarr.getJSONObject(cnt).optString("holidayId");
                                            holidays.holidayDate = holidaysJarr.getJSONObject(cnt).optString("holidayDate");
                                            holidays.holidayName = holidaysJarr.getJSONObject(cnt).optString("holidayName");

                                            holidaysDAO.save(holidays);
                                        }
                                    }

                                    company_info = new Company_info();
                                    company_info.companyid = jobj.optString("companyid");
                                    company_info.deducution = jobj.optString("deducution");
                                    company_info.deducutionHour1 = jobj.optString("deducutionHour1");
                                    company_info.deducutionHour2 = jobj.optString("deducutionHour2");
                                    company_info.deducutionHour3 = jobj.optString("deducutionHour3");
                                    company_info.deducutionHour4 = jobj.optString("deducutionHour4");
                                    company_info.deducutionHour4On = jobj.optString("deducutionHour4On");
                                    company_info.userid = jobj.optString("userid");
                                    company_info.basicSalary = jobj.optString("basicSalary");
                                    company_info.hra = jobj.optString("hra");
                                    company_info.conveyance = jobj.optString("conveyance");
                                    company_info.medical = jobj.optString("medical");
                                    company_info.telephone = jobj.optString("telephone");
                                    company_info.lta = jobj.optString("lta");
                                    company_info.specialIncentive = jobj.optString("specialIncentive");
                                    company_info.otherAllownace = jobj.optString("otherAllownace");
                                    company_info.pfEmployee = jobj.optString("pfEmployee");
                                    company_info.pfEmployer = jobj.optString("pfEmployer");
                                    company_info.profTax = jobj.optString("profTax");
                                    company_info.tds = jobj.optString("tds");
                                    company_info.otherDeduction = jobj.optString("otherDeduction");
                                    company_info.notificationLevel = jobj.optString("notificationLevel");
                                    company_info.name = jobj.optString("name");
                                    company_info.workingDays = jobj.optString("workingDays");
                                    company_info.startTime = jobj.optString("startTime");
                                    company_info.endTime = jobj.optString("endTime");
                                    company_info.payrollCycle = jobj.optString("payrollCycle");
                                    company_info.payrollStart = jobj.optString("payrollStart");
                                    company_info.payrollEnd = jobj.optString("payrollEnd");
                                    company_info.salaryBreakupType = jobj.optString("salaryBreakupType");
                                    company_info.isPayrollEnabled = jobj.optString("isPayrollEnabled");
                                    company_info.isPayrollEnabled = jobj.optString("isPayrollEnabled");
                                    company_info.isRegularizationAllowed = jobj.optString("isRegularizationAllowed");
                                    company_info.isLateDeductionOn = jobj.optString("isLateDeductionOn");
                                    company_info.autoLeaveUpdate = String.valueOf(jobj.getBoolean("AutoLeaveUpdate"));

                                    companyInfoDAO.save(company_info);

                                } catch (Exception e) {
                                    Log.d("", "Exception: " + e);
                                }
//
                                break;
                            default:
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //Fail Syncing:
            try {
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                }
            } catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            } catch (final Exception e) {
                // Handle or log or ignore
            }
        }
    }

    public void exportDatabse(String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + getPackageName() + "//databases//" + databaseName + "";
                String backupDBPath = "backupname.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }

    private void getUsers() {
        String companyID = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_ID);
        RestClient.getCommonService().getCompanyUsersList(companyID, Constant.AppName, Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID), Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_TOKEN), new Callback<List<com.bigbang.superteam.model.User>>() {
            @Override
            public void success(List<com.bigbang.superteam.model.User> users, Response response) {

                try {
                    Log.d(TAG, ">>" + Util.getString(response.getBody().in()));

                    SQLiteHelper helper = new SQLiteHelper(getBaseContext(), Constant.DatabaseName);
                    SQLiteDatabase db = helper.openDatabase();
                    db.delete(Constant.tableUsers, null, null);

                    for (com.bigbang.superteam.model.User user : users) {
                        ContentValues values = new ContentValues();
                        values.put("userID", user.getUserID());
                        values.put("firstName", user.getFirstName());
                        values.put("lastName", user.getLastName());
                        values.put("mobileNo1", user.getMobileNo1());
                        values.put("mobileNo2", user.getMobileNo2());
                        values.put("emailID", user.getEmailID());
                        values.put("permanentAddress", user.getPermanentAddress().toString());
                        values.put("picture", user.getPicture());
                        values.put("role_id", user.getRole().getId());
                        values.put("role_desc", user.getRole().getDesc());
                        values.put("temporaryAddress", user.getTemporaryAddress().toString());
                        db.insert(Constant.tableUsers, null, values);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                getCustomers();

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, ">>" + error);
            }
        });
    }

    private void getCustomers() {
        String companyID = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_ID);
        RestClient.getCommonService().getCustomers(companyID,
                Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID),
                Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_TOKEN),
                new Callback<ArrayList<Customer>>() {
                    @Override
                    public void success(ArrayList<Customer> customers, Response response) {

                        try {
                            Log.d(TAG, ">>" + Util.getString(response.getBody().in()));
                            JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                            JSONArray jsonArray = jsonObject.optJSONArray("data");

                            SQLiteHelper helper = new SQLiteHelper(getBaseContext(), Constant.DatabaseName);
                            SQLiteDatabase db = helper.openDatabase();
                            db.delete(Constant.tableCustomers, null, null);

                            for (int i = 0; i < customers.size(); i++) {
                                Customer customer = customers.get(i);
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                                ContentValues values = new ContentValues();
                                values.put("ID", customer.getID());
                                values.put("Name", customer.getName());
                                values.put("MobileNo", customer.getMobileNo());
                                values.put("LandlineNo", customer.getLandlineNo());
                                values.put("EmailID", customer.getEmailID());
                                values.put("OwnerID", customer.getOwnerID());
                                values.put("CompanyTypeID", customer.getCompanyTypeID());
                                values.put("CreatedBy", customer.getCreatedBy());
                                values.put("CompanyID", customer.getCompanyID());
                                values.put("Type", customer.getType());
                                values.put("Description", customer.getDescription());
                                values.put("AddressList", "" + jsonObject1.optString("AddressList"));
                                values.put("Logo", customer.getLogo());
                                db.insert(Constant.tableCustomers, null, values);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getVendors();

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, ">>" + error);
                    }
                });
    }

    private void getVendors() {
        String companyID = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_ID);
        RestClient.getCommonService().getVendors(companyID, Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID), Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_TOKEN), new Callback<ArrayList<Customer>>() {
            @Override
            public void success(ArrayList<Customer> customers, Response response) {

                try {
                    Log.d(TAG, ">>" + Util.getString(response.getBody().in()));
                    JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                    JSONArray jsonArray = jsonObject.optJSONArray("data");

                    SQLiteHelper helper = new SQLiteHelper(getBaseContext(), Constant.DatabaseName);
                    SQLiteDatabase db = helper.openDatabase();
                    db.delete(Constant.tableVendors, null, null);

                    for (int i = 0; i < customers.size(); i++) {
                        Customer customer = customers.get(i);
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                        ContentValues values = new ContentValues();
                        values.put("ID", customer.getID());
                        values.put("Name", customer.getName());
                        values.put("MobileNo", customer.getMobileNo());
                        values.put("LandlineNo", customer.getLandlineNo());
                        values.put("EmailID", customer.getEmailID());
                        values.put("OwnerID", customer.getOwnerID());
                        values.put("CompanyTypeID", customer.getCompanyTypeID());
                        values.put("CreatedBy", customer.getCreatedBy());
                        values.put("CompanyID", customer.getCompanyID());
                        values.put("Type", customer.getType());
                        values.put("Description", customer.getDescription());
                        values.put("AddressList", "" + jsonObject1.getString("AddressList"));
                        values.put("Logo", customer.getLogo());
                        db.insert(Constant.tableVendors, null, values);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Done Syncing:
                try {
                    if (progressDialog != null) {
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                }

                write(Constant.SHRED_PR.KEY_IS_LOGGEDIN, "true");
                String roleId = read(Constant.SHRED_PR.KEY_ROLE_ID);
                if (roleId.equals("2") || roleId.equals("1")) {
                    startActivity((AdminDashboardNewActivity.class));
                    finish();
                } else if (roleId.equals("3")) {
//                    Util.registerUserTrackingReceiver(LoginActivity.this);
                    startActivity((ManagerDashboardNewActivity.class));
                    finish();
                } else {
//                    Util.registerUserTrackingReceiver(LoginActivity.this);
                    startActivity((UserDashboardNewActivity.class));
                    finish();
                }

                Calendar calendar = Calendar.getInstance();
                Util.appendLog("###################");
                Util.appendLog("Login at: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
                Util.appendLog("App Version: " + Constant.APP_VERSION);
                Util.appendLog("###################");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, ">>" + error);
            }
        });
    }

    private void changeRegDevice(String mobile) {
        if (progressDialog != null) progressDialog.show();

        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String deviceName = android.os.Build.MODEL + " " + android.os.Build.MANUFACTURER;

        RestClient.getCommonService3().changeRegDevice(mobile, Constant.AppName, deviceName, android_id, new Callback<Response>() {
            @Override
            public void success(Response rc, Response response) {
                if (progressDialog != null)
                    if (progressDialog.isShowing()) progressDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                    String status = jObj.optString("status");
                    if (!status.equals(Constant.InvalidToken)) {
                        Log.d(TAG, Util.getString(response.getBody().in()));
                        Map<String, String> map = Util.readStatus(response);
                        toast(map.get("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null)
                    if (progressDialog.isShowing()) progressDialog.dismiss();
            }
        });
    }

    private void setPayrollDataInPreferences(JSONObject jsonObjectMain) {
        try {

            if (jsonObjectMain != null) {
                boolean isPayrollActive = jsonObjectMain.optBoolean("payrollEnabled");
                Util.WriteSharePrefrenceForBoolean(activity, Constant.SHRED_PR.KEY_PAYROLL_ACTIVE, isPayrollActive);

                if (isPayrollActive) {
                    JSONObject jsonObj = jsonObjectMain.optJSONObject("companyPayroll");
                    if (jsonObj != null) {
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_BASIC_SALARY, jsonObj.optString("BasicSalary"));
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_HRA, jsonObj.optString("HRA"));
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_CONVEYANCE, jsonObj.optString("Conveyance"));
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_MEDICAL, jsonObj.optString("MedicalAllowances"));
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_VARIABLE_AMOUNT, jsonObj.optString("VariablePayAmount"));
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_EMPLOYEE_CONTRIBUTION, jsonObj.optString("EmployeeContribution"));
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_EMPLOYER_CONTRIBUTION, jsonObj.optString("EmployerContribution"));
                        Util.WriteSharePrefrenceForBoolean(activity, Constant.SHRED_PR.KEY_METRO_CITY, jsonObj.getBoolean("metroCityHRA"));
                        Util.WriteSharePrefrenceForInteger(activity, Constant.SHRED_PR.KEY_WORKING_DAYS_POLICY, jsonObj.optInt("CompanyWorkingDaysID"));

                        //Payroll advance settings
                        JSONObject dataObj = jsonObj.optJSONObject("companyWorkingPolicy");
                        Util.WriteSharePrefrenceForInteger(activity, Constant.SHRED_PR.KEY_WORKING_POLICY_TYPE_ID, dataObj.optInt("WorkingPolicyTypeID"));
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_ONE, dataObj.optString("LateEarly1hrsDeduction"));
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_TWO, dataObj.optString("LateEarly2hrsDeduction"));
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_THREE, dataObj.optString("LateEarly3hrsDeduction"));
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_FOUR, dataObj.optString("LateEarly4hrsDeduction"));
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_FIVE, dataObj.optString("LateEarly5hrsDeduction"));
                        Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_LATE_ALLOWED_TIME, dataObj.optString("LateAllowedTimes"));
                        Util.WriteSharePrefrenceForInteger(activity, Constant.SHRED_PR.KEY_HRS_CALCULATION_TYPE, dataObj.optInt("HrsCalculationType"));

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void directCallAPIWithoutAsync(String latitude, String longitude, String date) {
        Log.e("", " &&&&&&&& directCallAPIWithoutAsync() called from login");

        String response = "";
        try {
            Util.appendLog("API Called");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", "" + date);
            jsonObject.put("latitude", "" + latitude);
            jsonObject.put("longitude", "" + longitude);
            jsonObject.put("location", "");
            if (checkNetWorkProviders()) {
                jsonObject.put(User_Location.isGPSOn, "true");
            } else {
                jsonObject.put(User_Location.isGPSOn, "false");
            }
            jsonObject.put(User_Location.isMockLocation, "false");

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("JSON", jsonObject.toString()));

            Util.appendLog("Req passed to server: " + jsonObject.toString());

            Log.e("params1", ">>" + params1);

            response = Util.makeServiceCall(Constant.URL + "updateUserLocation", 2, params1, LoginActivity.this);

            Util.appendLog(response);
            Log.e("response", ">>" + response);


        } catch (Exception e) {
            Util.appendLog("Exception directCallAPIWithoutAsync() : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    LocationManager locationManager;

    public boolean checkNetWorkProviders() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);// Internet

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);//SIM car service provider

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
            return false;
        } else {
            return true;
        }
    }
}