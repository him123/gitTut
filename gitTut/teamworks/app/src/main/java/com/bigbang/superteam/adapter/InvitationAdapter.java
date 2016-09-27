package com.bigbang.superteam.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.CommonGCM;
import com.bigbang.superteam.Privileges;
import com.bigbang.superteam.R;
import com.bigbang.superteam.WorkItem_GCM;
import com.bigbang.superteam.admin.AdminDashboardNewActivity;
import com.bigbang.superteam.login_register.CompanySetupActivity;
import com.bigbang.superteam.manager.ManagerDashboardNewActivity;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.user.ResignUserActivity;
import com.bigbang.superteam.user.UserDashboardNewActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by USER 8 on 7/13/2015.
 */
public class InvitationAdapter extends BaseAdapter {

    private static final String TAG = InvitationAdapter.class.getSimpleName();
    private static LayoutInflater inflater = null;
    ArrayList<HashMap<String, String>> locallist;
    static Activity activity;
    TransparentProgressDialog progressDialog;
    int FROM;
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();

    //Login:
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
    public static final int GET_COMPANYINFO = 11;
    public static final String APIS[] = {"getWorkItemByUser", "getWorkItemUpdateByUser", "getWorkItemAttachmentByUser", "getProjectByUser", "getPrivileges", "getCompanyPrivileges", "getUserPrivileges", "getNotifications", "getNotifications", "getDefaultSettings", "getUserCompanyInfo", "getCompanyInfo"};


    public InvitationAdapter(Activity activity, ArrayList<HashMap<String, String>> locallist, TransparentProgressDialog progressDialog, int FROM) {
        this.activity = activity;
        this.locallist = locallist;
        this.progressDialog = progressDialog;
        this.FROM = FROM;
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        imageLoader.init(ImageLoaderConfiguration
                .createDefault(activity));
    }

    private void delete(int index) {
        Log.e(TAG,"Inside delete");
        locallist.remove(index);
        Log.e(TAG,"Inside delete after remove listview");
        notifyDataSetChanged();
        Log.e(TAG,"Inside delete after notifyDataSetChanged");
    }

    private void changeStatus(int index) {
        locallist.get(index).put("Status", "Pending");
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return locallist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.invitation_listraw, null);
            holder = new ViewHolder(view);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        holder.tvTitle.setText("" + locallist.get(position).get("Company_Name"));
        holder.tvName.setText("" + locallist.get(position).get("Company_Name").toUpperCase());
        holder.tvDesc.setText("" + locallist.get(position).get("role_desc"));
        holder.tvStatus.setText("" + locallist.get(position).get("Status"));

        String logo = "" + locallist.get(position).get("Logo");
        imageLoader.displayImage("" + logo, holder.img, options);

        if (logo.length() > 0) {
            holder.tvName.setVisibility(View.GONE);
        } else {
            holder.tvName.setVisibility(View.VISIBLE);
        }

        holder.rlApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = true;
                for (int i = 0; i < locallist.size(); i++) {
                    if (locallist.get(i).get("Status").toString().toLowerCase().equals("pending"))
                        flag = false;
                }

                if (flag) {

                    if (FROM == Constant.FROM_LOGIN) {
                        if (Util.isOnline(activity)) {
                            acceptCompanyInvite(progressDialog, position);
                        } else
                            Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    } else {
                        String roleID = Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_ROLE_ID);
                        if (roleID.equals("1")) {
                            AlertDialog.Builder alert1 = new AlertDialog.Builder(activity);
                            alert1.setTitle("" + Constant.AppNameSuper);
                            alert1.setMessage(activity.getResources().getString(R.string.company_deleted));
                            alert1.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        @SuppressLint("InlinedApi")
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            if (Util.isOnline(activity)) {
                                                acceptCompanyInvite(progressDialog, position);
                                            } else
                                                Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                            );
                            alert1.setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // TODO Auto-generated method stub
                                            dialog.cancel();
                                        }
                                    }

                            );
                            alert1.create();
                            alert1.show();
                        } else {
                            Intent intent = new Intent(activity, ResignUserActivity.class);
                            intent.putExtra("map", locallist.get(position));
                            activity.startActivity(intent);
                        }
                    }

                } else
                    Toast.makeText(activity, activity.getResources().getString(R.string.invitation_pending), Toast.LENGTH_SHORT).show();
            }
        });

        holder.rlReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Util.isOnline(activity)) {
                    declineCompanyInvite(progressDialog, position);
                } else
                    Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.title)
        TextView tvTitle;
        @InjectView(R.id.desc)
        TextView tvDesc;
        @InjectView(R.id.tvStatus)
        TextView tvStatus;
        @InjectView(R.id.img)
        ImageView img;
        @InjectView(R.id.tvName)
        TextView tvName;
        @InjectView(R.id.rlApprove)
        RelativeLayout rlApprove;
        @InjectView(R.id.rlReject)
        RelativeLayout rlReject;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

    protected void startActivityWithData(Class klass, HashMap hashMap) {
        Intent intent = new Intent(activity, klass);
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            intent.putExtra((String) pair.getKey(), (String) pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        activity.startActivity(intent);
    }

    private void acceptCompanyInvite(final TransparentProgressDialog progressDialog, final int position) {
        if (progressDialog != null) progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", locallist.get(position).get("ID"));
            jsonObject.put("EmailID", locallist.get(position).get("EmailID"));
            jsonObject.put("Name", locallist.get(position).get("Name"));
            jsonObject.put("MobileNo", locallist.get(position).get("MobileNo"));
            jsonObject.put("CreatedBy", locallist.get(position).get("CreatedBy"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String companyID = locallist.get(position).get("Company_ID");
        String roleID = locallist.get(position).get("role_id");
        String managerID = locallist.get(position).get("managerID");

        RestClient.getCommonService().acceptCompanyInvite("", jsonObject.toString(), Constant.AppName, companyID, roleID, managerID, read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response1) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            Log.e(TAG, "resonse is:- " + response);
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(activity);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (response != null) {
                            try {
                                String json = Util.getString(response.getBody().in());
                                Log.e(TAG, ">>" + response);
                                JSONObject jsonObject = new JSONObject(json.toString());
                                String status = jsonObject.getString("status");
                                Toast.makeText(activity, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                if (status.equals("Success")) {
                                    Log.e(TAG,"Inside company Invite Accept ");
                                    if (FROM == Constant.FROM_LOGIN) {
                                        Log.e(TAG,"Inside company Invite Accept FROM == Constant.FROM_LOGIN 111");
                                        delete(position);
                                        Log.e(TAG,"Inside company Invite Accept after delete 2222");
                                        if (Util.isOnline(activity)) {
                                            Log.e(TAG,"Inside company Invite Accept  if (Util.isOnline(activity)) 111111111");
                                            login(read(Constant.SHRED_PR.KEY_TELEPHONE), read(Constant.SHRED_PR.KEY_PASSWORD));
                                            Log.e(TAG,"Inside company Invite Accept  if (Util.isOnline(activity))");
                                        } else
                                            Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e(TAG,"Inside company Invite Accept 33333");
                                        changeStatus(position);
                                        Log.e(TAG,"Inside company Invite Accept 44444");
                                        String roleID = Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_ROLE_ID);
                                        if (roleID.equals("1")) {
                                            toast(activity.getResources().getString(R.string.re_login));
                                            TeamWorkApplication.LogOutClear(activity);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                        Log.e(TAG, "" + error);
                        toast(activity.getResources().getString(R.string.some_error));
                    }
                }

        );
    }

    private void declineCompanyInvite(final TransparentProgressDialog progressDialog, final int position) {
        if (progressDialog != null) progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", locallist.get(position).get("ID"));
            jsonObject.put("EmailID", locallist.get(position).get("EmailID"));
            jsonObject.put("Name", locallist.get(position).get("Name"));
            jsonObject.put("MobileNo", locallist.get(position).get("MobileNo"));
            jsonObject.put("CreatedBy", locallist.get(position).get("CreatedBy"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String companyID = locallist.get(position).get("Company_ID");
        String roleID = locallist.get(position).get("role_id");

        RestClient.getCommonService().declineCompanyInvite(jsonObject.toString(), Constant.AppName, companyID, roleID, read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response1) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            Log.e(TAG, "resonse is:- " + response);
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(activity);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (response != null) {
                            try {
                                String json = Util.getString(response.getBody().in());
                                Log.e(TAG, ">>" + response);
                                JSONObject jsonObject = new JSONObject(json.toString());
                                String status = jsonObject.getString("status");
                                Toast.makeText(activity, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                if (status.equals("Success")) {
                                    delete(position);

                                    if (FROM == Constant.FROM_LOGIN) {
                                        if (locallist.size() == 0) {
                                            HashMap<String, String> hashMap = new HashMap<>();
                                            hashMap.put("position", "0");
                                            hashMap.put("from", "" + FROM);
                                            startActivityWithData(CompanySetupActivity.class, hashMap);
                                            activity.finish();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                        Log.e(TAG, "" + error);
                        toast(activity.getResources().getString(R.string.some_error));
                    }
                }

        );
    }

    private void login(String username, final String password) {

        if (progressDialog != null) progressDialog.show();

        String android_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        RestClient.getCommonService().login(username, password, android_id, Constant.AppName, "F", read(Constant.SHRED_PR.KEY_GCM_ID),
                new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            Log.i(TAG, "USER DETAIL :: " + user);
                            Log.i(TAG, Util.getString(response.getBody().in()));
                            Map<String, String> map = Util.readStatus(response);
                            boolean isSuccess = map.get("status").equals("Success");
                            if (isSuccess && user != null) {
                                try {
                                    TeamWorkApplication.LogInClear(activity);
//                                    if (!user.getUserID().toString().equals(read(Constant.SHRED_PR.KEY_USERID))) {
//                                        TeamWorkApplication.LogInClear();
//                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "ERROR WHILE CLEARING LOGIN :: " + e.getMessage());
                                }

                                Log.d("KEY_TOKEN", "" + read(Constant.SHRED_PR.KEY_TOKEN));
                                //User Info:
                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                                write(Constant.SHRED_PR.KEY_TOKEN, "" + new JSONObject(jsonObject.optString("data")).optString("SecurityToken"));
                                Log.d("KEY_TOKEN", "" + read(Constant.SHRED_PR.KEY_TOKEN));

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

                                if (Util.isOnline(activity)) {
                                    new Synchronise("" + read(Constant.SHRED_PR.KEY_USERID), 0).execute();
                                } else {
                                    toast(Constant.network_error);
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
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                        Log.e(TAG, "login::failure ==>" + error, error);
                        toast(activity.getResources().getString(R.string.some_error));
                    }
                });

    }


    // ***************** LOGIN ****************************************//


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
                            + ","+Constant.CHANGE_DEVICE_REQUEST + "," + Constant.EXPENSE_APPROVAL;
                    params1.add(new BasicNameValuePair("Application", "" + Constant.AppName));
                    params1.add(new BasicNameValuePair("Type", NotificationType));
                    Log.e("params1", ">>" + params1);
                    response = Util.makeServiceCall(Constant.URL1 + APIS[Code], 1, params1, activity);
                    return response;
                case GET_NOTIFICATIONS:
                    //GET API
                    NotificationType = "" + Constant.WORKITEM_MODIFIED + "," + Constant.CHANGE_MOBILE_APPROVAL_TO_USER
                            + "," + Constant.USER_RESIGN_APPROVAL_TO_USER + "," + Constant.NEW_USER_ADDED + ","
                            +Constant.NEW_REPORTING_MEMBER+","+ Constant.DELETE_USER + ","
                            + Constant.USER_INVITATION_TO_ADMIN + "," + Constant.INVITE_USER + ","
                            + Constant.UPDATE_ROLE_OR_MANAGER + "," + Constant.ATTENDANCE_APPROVAL_TO_USER + ","
                            + Constant.LEAVE_APPROVAL_TO_USER + "," + Constant.UPDATETIME_APPROVAL_TO_USER + ","
                            + Constant.LEAVE_WITHDRAW_TO_ADMIN + "," + Constant.UPDATE_CUSTOMER_APPROVAL_TO_USER + ","
                            + Constant.NEW_WORKITEM + "," + Constant.REJECT_NEWWORKITEM + ","
                            + Constant.UPDATE_WORKITEM + "," + Constant.REJECT_WORKITEMUPDATE + ","
                            + Constant.CREATE_NEWPRJECT + "," + Constant.MODIFIED_PROJECT+ ","
                            + Constant.REJECT_NEWPRJECT + "," + Constant.PROJECT_APPROVED + ","
                            + Constant.PROJECT_REJECTED + "," + Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_USER + ","
                            + Constant.LEAVE_UPDATE_TO_USER+"," + Constant.DELETE_COMPANY+","+Constant.ATTACHMENTS+ ","
                            + Constant.CHANGE_DEVICE_ACK + "," + Constant.ADMIN_PAYROLL + ","
                            + Constant.USER_PAYSLIP + "," + Constant.EXPENSE_NOTIFICATION;
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

                                    //write(Constant.SHRED_PR.KEY_USER_LOCATION_DETAILS, "" + jsonObject.optJSONArray("data").toString());
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
        RestClient.getCommonService().getCompanyUsersList(companyID, Constant.AppName, Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID), Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_TOKEN), new Callback<List<com.bigbang.superteam.model.User>>() {
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
                getCustomers();

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, ">>" + error);
            }
        });
    }


    private void getCustomers() {
        String companyID = Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID);
        RestClient.getCommonService().getCustomers(companyID, Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID), Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_TOKEN), new Callback<ArrayList<Customer>>() {
            @Override
            public void success(ArrayList<Customer> customers, Response response) {

                try {
                    Log.e(TAG, ">>" + Util.getString(response.getBody().in()));
                    JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                    JSONArray jsonArray = jsonObject.optJSONArray("data");

                    SQLiteHelper helper = new SQLiteHelper(activity, Constant.DatabaseName);
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
                Log.e(TAG, ">>" + error);
            }
        });
    }

    private void getVendors() {
        String companyID = Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID);
        RestClient.getCommonService().getVendors(companyID, Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID), Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_TOKEN), new Callback<ArrayList<Customer>>() {
            @Override
            public void success(ArrayList<Customer> customers, Response response) {

                try {
                    Log.e(TAG, ">>" + Util.getString(response.getBody().in()));
                    JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                    JSONArray jsonArray = jsonObject.optJSONArray("data");

                    SQLiteHelper helper = new SQLiteHelper(activity, Constant.DatabaseName);
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
                        values.put("AddressList", "" + jsonObject1.optString("AddressList"));
                        values.put("Logo", customer.getLogo());
                        db.insert(Constant.tableVendors, null, values);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Done Syncing:
                if (progressDialog != null)
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                write(Constant.SHRED_PR.KEY_IS_LOGGEDIN, "true");
                String roleId = read(Constant.SHRED_PR.KEY_ROLE_ID);
                if (roleId.equals("2") || roleId.equals("1")) {
                    startActivity((AdminDashboardNewActivity.class));
                    activity.finish();
                } else if (roleId.equals("3")) {
                    startActivity((ManagerDashboardNewActivity.class));
                    activity.finish();
                } else {
                    startActivity((UserDashboardNewActivity.class));
                    activity.finish();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, ">>" + error);
            }
        });
    }


    // *************************************************************** //

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(activity, key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(activity, key);
    }

    protected void startActivity(Class klass) {
        activity.startActivity(new Intent(activity, klass));
    }

    protected void toast(CharSequence text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

}
