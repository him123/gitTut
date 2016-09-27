package com.bigbang.superteam.fragment;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.CommonGCM;
import com.bigbang.superteam.Privileges;
import com.bigbang.superteam.R;
import com.bigbang.superteam.WorkItem_GCM;
import com.bigbang.superteam.adapter.FragmentAdapter;
import com.bigbang.superteam.admin.AdminDashboardNewActivity;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class RolesRightsSetupFragment extends Fragment {

    private static final String TAG = RolesRightsSetupFragment.class.getSimpleName();

    @InjectView(R.id.tvNext)
    TextView tvNext;
    @InjectView(R.id.viewM)
    View viewM;
    @InjectView(R.id.viewTM)
    View viewTM;
    @InjectView(R.id.tvM)
    TextView tvM;
    @InjectView(R.id.tvTM)
    TextView tvTM;

    TransparentProgressDialog progressDialog;
    Activity activity;

    static int FROM = Constant.FROM_LOGIN;
    FragmentAdapter pageAdapter;
    View v;
    public static ViewPager pager;
    public int currentPosition = 0;
    public static FragmentManager fragmentManager;

    public static ArrayList<HashMap<String, String>> listPrivilegesTM = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> listPrivilegesM = new ArrayList<>();

    public static final int GET_WORKITEM = 0;
    public static final int GET_UPDATES_WORKITEM = 1;
    public static final int GET_ATTACHMENTS = 2;
    public static final int GET_PROJECTS = 3;
    public static final int GET_PRIVILAGES = 4;
    public static final int GET_COMPANY_PRIVILAGES = 5;
    public static final int GET_USER_PRIVILAGES = 6;
    public static final int GET_APPROVALS = 7;
    public static final int GET_NOTIFICATIONS = 8;
    public static final int GET_DEFAULTSETTINGS = 9;
    public static final int GET_USER_COMPANYINFO = 10;
    public static final String APIS[] = {"getWorkItemByUser", "getWorkItemUpdateByUser", "getWorkItemAttachmentByUser", "getProjectByUser", "getPrivileges", "getCompanyPrivileges", "getUserPrivileges", "getNotifications", "getNotifications", "getDefaultSettings", "getUserCompanyInfo"};


    public static RolesRightsSetupFragment newInstance(int from) {
        FROM = from;
        RolesRightsSetupFragment fragment = new RolesRightsSetupFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity=getActivity();
        View view = inflater.inflate(R.layout.fragment_roles_rights_setup, container, false);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) view.getRootView();
        Util.setAppFont(mContainer, mFont);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

        if (FROM == Constant.FROM_DASHBOARD) {
            if (Util.isOnline(activity)) {
                new getCompanyPrivileges().execute();
            } else {
                Toast.makeText(activity, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                reload(Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_ALL_PRIVILAGES));
            }
        } else {
            if (Util.isOnline(activity)) {
                new getPrivileges().execute();
            } else {
                Toast.makeText(activity, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        reload(Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_ALL_PRIVILAGES));
    }

    @OnClick(R.id.rlManager)
    @SuppressWarnings("unused")
    public void Manager(View view) {
        pager.setCurrentItem(0);
    }

    @OnClick(R.id.rlTeamMember)
    @SuppressWarnings("unused")
    public void TeamMember(View view) {
        pager.setCurrentItem(1);
    }

    @OnClick(R.id.rl_next)
    @SuppressWarnings("unused")
    public void Next(View view) {
        if (Util.isOnline(activity)) {
            try {
                new addRolePrivilege().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity,
                    Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {

        progressDialog = new TransparentProgressDialog(activity, R.drawable.progressdialog, false);

        fragmentManager = activity.getFragmentManager();
        pager = (ViewPager) getView().findViewById(R.id.viewpager);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pageNo) {
                currentPosition = pageNo;
                switch (currentPosition) {
                    case 0:
                        currentPosition = 0;
                        viewM.setVisibility(View.VISIBLE);
                        viewTM.setVisibility(View.GONE);
                        tvM.setTextColor(getResources().getColor(R.color.white));
                        tvTM.setTextColor(getResources().getColor(R.color.light_gray));
                        break;
                    case 1:
                        currentPosition = 1;
                        viewM.setVisibility(View.GONE);
                        viewTM.setVisibility(View.VISIBLE);
                        tvM.setTextColor(getResources().getColor(R.color.light_gray));
                        tvTM.setTextColor(getResources().getColor(R.color.white));
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void reload() {
        final List<Fragment> fragmentList = getFragments();
        pageAdapter = new FragmentAdapter(getChildFragmentManager(), fragmentList);
        pager.setAdapter(pageAdapter);
        pager.setCurrentItem(currentPosition);
    }


    private List<Fragment> getFragments() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(ManagerRightsFragment.newInstance());
        fragmentList.add(TeamMemberRightsFragment.newInstance());

        return fragmentList;
    }

    public class addRolePrivilege extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < listPrivilegesM.size(); i++) {
                    if (listPrivilegesM.get(i).get("checked").equals("1")) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("roleid", "" + listPrivilegesM.get(i).get("roleid"));
                        jsonObject.put("privilegeid", "" + listPrivilegesM.get(i).get("privilegeid"));
                        jsonArray.put(jsonObject);
                    } else {

                    }
                }

                for (int i = 0; i < listPrivilegesTM.size(); i++) {
                    if (listPrivilegesTM.get(i).get("checked").equals("1")) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("roleid", "" + listPrivilegesTM.get(i).get("roleid"));
                        jsonObject.put("privilegeid", "" + listPrivilegesTM.get(i).get("privilegeid"));
                        jsonArray.put(jsonObject);
                    } else {

                    }
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("companyid", Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID));
                jsonObject.put("data", jsonArray);

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("JSON", "" + jsonObject));

                Log.e("params1", ">>" + params1);
                String response = Util.makeServiceCall(Constant.URL + "addRolePrivilege", 2, params1, activity);

                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("Response", ">>" + result);
            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if (status.equals("Success")) {
                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_PRIVILAGES, "" + result);
                    if (FROM == Constant.FROM_DASHBOARD) {
                        activity.finish();
                        activity.overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                    } else {
                        new Synchronise("" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID), 0).execute();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class getPrivileges extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resp = "";

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);

            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + "getPrivileges", 1, params1, activity);

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("Response", ">>" + result);
            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();

            Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_ALL_PRIVILAGES, "" + result);
            reload(result);

        }

    }

    public class getCompanyPrivileges extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resp = "";

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);

            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + "getCompanyPrivileges", 1, params1, activity);

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("Response", ">>" + result);
            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();

            Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_PRIVILAGES, "" + result);
            Privileges.Init(activity);
            reload(Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_ALL_PRIVILAGES));

        }

    }


    private void reload(String result) {

        RolesRightsSetupFragment.listPrivilegesTM.clear();
        RolesRightsSetupFragment.listPrivilegesM.clear();
        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString("status");
            if (status.equals("Success")) {
                JSONArray jData = jsonObject.getJSONArray("data");
                for (int i = 0; i < jData.length(); i++) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    JSONObject jsonObject1 = jData.getJSONObject(i);
                    hashMap.put("privilegeid", "" + jsonObject1.optString("privilegeid"));
                    hashMap.put("roleid", "" + jsonObject1.optString("roleid"));
                    hashMap.put("privilege", "" + jsonObject1.optString("privilege"));
                    hashMap.put("checked", "1");
                    if (FROM == Constant.FROM_DASHBOARD)
                        hashMap.put("checked", "" + Util.checkCompanyPrivilage(activity, hashMap.get("privilegeid")));

                    if (hashMap.get("roleid").equals("3")) {
                        RolesRightsSetupFragment.listPrivilegesM.add(hashMap);
                    }
                    if (hashMap.get("roleid").equals("4")) {
                        RolesRightsSetupFragment.listPrivilegesTM.add(hashMap);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(RolesRightsSetupFragment.listPrivilegesM, byRoleID);
        Collections.sort(RolesRightsSetupFragment.listPrivilegesTM, byRoleID);
        reload();
    }


    static final Comparator<HashMap<String, String>> byRoleID = new Comparator<HashMap<String, String>>() {

        public int compare(HashMap<String, String> ord1, HashMap<String, String> ord2) {

            int role1 = Integer.parseInt(ord1.get("roleid"));
            int role2 = Integer.parseInt(ord2.get("roleid"));

            try {
                if (role1 == role2) return 0;
                else return ((role1 > role2 ? 1 : -1));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }
    };

    public class Synchronise extends AsyncTask<Void, String, String> {
        String UserID;
        int Code;

        public Synchronise(String Userid, int code) {
            UserID = Userid;
            Code = code;
            Log.e(TAG, "In Synchronise Cunstructor");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!progressDialog.isShowing()) {
                if (progressDialog != null) progressDialog.show();
            }
            Log.e(TAG, "In Synchronise Pre");
        }

        @Override
        protected String doInBackground(Void... params) {
            String resp = "";
            Log.e(TAG, "In Synchronise background Task");
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
                            +","+Constant.CHANGE_DEVICE_REQUEST+","+Constant.EXPENSE_APPROVAL;
                    params1.add(new BasicNameValuePair("Application", "" + Constant.AppName));
                    params1.add(new BasicNameValuePair("Type", NotificationType));
                    Log.e("params1", ">>" + params1);
                    response = Util.makeServiceCall(Constant.URL1 + APIS[Code], 1, params1, activity);
                    return response;
                case GET_NOTIFICATIONS:
                    //GET API
                    NotificationType = "" + Constant.WORKITEM_MODIFIED + "," + Constant.CHANGE_MOBILE_APPROVAL_TO_USER + ","
                            + Constant.USER_RESIGN_APPROVAL_TO_USER + "," + Constant.NEW_USER_ADDED + ","
                            +Constant.NEW_REPORTING_MEMBER+","+ Constant.DELETE_USER + "," + Constant.USER_INVITATION_TO_ADMIN
                            + "," + Constant.INVITE_USER + "," + Constant.UPDATE_ROLE_OR_MANAGER + ","
                            + Constant.ATTENDANCE_APPROVAL_TO_USER + "," + Constant.LEAVE_APPROVAL_TO_USER + ","
                            + Constant.UPDATETIME_APPROVAL_TO_USER + "," + Constant.LEAVE_WITHDRAW_TO_ADMIN + ","
                            + Constant.UPDATE_CUSTOMER_APPROVAL_TO_USER + "," + Constant.LEAVE_UPDATE_TO_USER+ ","
                            + Constant.NEW_WORKITEM + "," + Constant.REJECT_NEWWORKITEM + "," + Constant.UPDATE_WORKITEM
                            + "," + Constant.REJECT_WORKITEMUPDATE + "," + Constant.CREATE_NEWPRJECT + ","
                            + Constant.MODIFIED_PROJECT+ "," + Constant.REJECT_NEWPRJECT + "," + Constant.PROJECT_APPROVED
                            + "," + Constant.PROJECT_REJECTED + "," + Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_USER + ","
                            + Constant.DELETE_COMPANY+","+Constant.ATTACHMENTS+ "," + Constant.CHANGE_DEVICE_ACK
                            + "," + Constant.ADMIN_PAYROLL + "," + Constant.USER_PAYSLIP
                            + "," + Constant.EXPENSE_NOTIFICATION;
                    params1.add(new BasicNameValuePair("Application", "" + Constant.AppName));
                    params1.add(new BasicNameValuePair("Type", NotificationType));
                    Log.e("params1", ">>" + params1);
                    response = Util.makeServiceCall(Constant.URL1 + APIS[Code], 1, params1, activity);
                    return response;
                default:

                    params1.add(new BasicNameValuePair("CompanyID", "" + read(Constant.SHRED_PR.KEY_COMPANY_ID)));
                    params1.add(new BasicNameValuePair("RoleID", "" + read(Constant.SHRED_PR.KEY_ROLE_ID)));
                    params1.add(new BasicNameValuePair("Type", "default"));
                    params1.add(new BasicNameValuePair("MemberID", "" + read(Constant.SHRED_PR.KEY_USERID)));

                    Log.e("params1", ">>" + params1);
                    response = Util.makeServiceCall(Constant.URL + APIS[Code], 1, params1, activity);
                    return response;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Response for :", APIS[Code] + ":: " + s);
            HandleResponse(s, Code);

            if (Code < APIS.length - 1) {
                Code++;
                new Synchronise(UserID, Code).execute();
            } else {
                getUsers();
            }
        }
    }

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
                            case GET_WORKITEM:
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++)
                                    WorkItem_GCM.InsertIntoWorkItem((JSONObject) jsonArray.get(i), activity);
                                break;
                            case GET_UPDATES_WORKITEM:
                                jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++)
                                    WorkItem_GCM.InsertIntoWorkItemUpdate((JSONObject) jsonArray.get(i), activity);
                                break;
                            case GET_PROJECTS:
                                jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++)
                                    WorkItem_GCM.InsertIntoProjects(jsonArray.optJSONObject(i), activity);
                                break;
                            case GET_ATTACHMENTS:
                                jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++)
                                    WorkItem_GCM.InsertIntoAttachments((JSONObject) jsonArray.get(i), activity);
                                break;
                            case GET_PRIVILAGES:
                                Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_ALL_PRIVILAGES, "" + s);
                                break;
                            case GET_COMPANY_PRIVILAGES:
                                if (roleId == 1 || roleId == 2) {
                                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_PRIVILAGES, "" + s);
                                    Privileges.Init(activity);
                                }
                                break;
                            case GET_USER_PRIVILAGES:
                                if (roleId == 3 || roleId == 4) {
                                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_USER_PRIVILAGES, "" + s);
                                    Privileges.Init(activity);
                                }
                                break;
                            case GET_APPROVALS:

                                JSONArray jsonArray1 = jsonObject.getJSONArray("data");
                                write(Constant.SHRED_PR.KEY_LASTSYNC_APPROVAL_TIME, "" + jsonObject.optString("lastSynchTime"));
                                Log.e("Approvals:", ">>" + jsonArray1);
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                    int status = jsonObject1.optInt("Status");
                                    String userID = jsonObject1.optString("UserID");
                                    if (userID.equals(read(Constant.SHRED_PR.KEY_USERID)))
                                        CommonGCM.InsertIntoApprovals(jsonObject1, activity);
                                }
                                break;
                            case GET_NOTIFICATIONS:

                                JSONArray jsonArray2 = jsonObject.getJSONArray("data");
                                write(Constant.SHRED_PR.KEY_LASTSYNC_NOTIFICATION_TIME, "" + jsonObject.optString("lastSynchTime"));
                                Log.e("Notifications:", ">>" + jsonArray2);
                                for (int i = 0; i < jsonArray2.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray2.getJSONObject(i);
                                    String userID = jsonObject1.optString("UserID");
                                    if (userID.equals(read(Constant.SHRED_PR.KEY_USERID))) {
                                        CommonGCM.InsertIntoNotifications(jsonObject1, activity);
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
                            default:
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void getUsers() {
        String companyID = Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID);
        RestClient.getCommonService().getCompanyUsersList(companyID, Constant.AppName, Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID), Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_TOKEN), new Callback<List<User>>() {
            @Override
            public void success(List<com.bigbang.superteam.model.User> users, Response response) {

                try {
                    Log.e(TAG, ">>" + Util.getString(response.getBody().in()));

                    SQLiteHelper helper = new SQLiteHelper(activity, Constant.DatabaseName);
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

                //Done Syncing:
                if (progressDialog != null)
                    if (progressDialog.isShowing()) progressDialog.dismiss();

                Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_IS_COMPANY_SETUP, "true");
                Intent intent = new Intent(activity, AdminDashboardNewActivity.class);
                activity.startActivity(intent);
                activity.finish();

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, ">>" + error);
            }
        });
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(activity, key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(activity, key);
    }


}
