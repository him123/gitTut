package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.CommonGCM;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.NotificationListAdapter;
import com.bigbang.superteam.dataObjs.CompanyLeavesDAO;
import com.bigbang.superteam.dataObjs.NotificationInfo;
import com.bigbang.superteam.login_register.HolidaysDAO;
import com.bigbang.superteam.model.CompanyLeaves;
import com.bigbang.superteam.model.Holidays;
import com.bigbang.superteam.pulltorefresh.PullToRefreshBase;
import com.bigbang.superteam.pulltorefresh.PullToRefreshView;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by USER 8 on 02-Nov-15.
 */
public class NotificationFragment extends Fragment {

    @InjectView(R.id.tvError)
    TextView tvError;
    @InjectView(R.id.pull_refresh_list)
    PullToRefreshView mPullRefreshListView;
    ListView notificationList;
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    ArrayList<NotificationInfo> list = new ArrayList<>();
    NotificationListAdapter notificationAdapter;

    SQLiteHelper helper;
    public static SQLiteDatabase db = null;
    Activity activity;

    public static Fragment newInstance() {
        NotificationFragment f = new NotificationFragment();
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_notification, container, false);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        reload();
    }

    @Override
    public void onResume() {
        super.onResume();

        reload();

        if (read(Constant.SHRED_PR.KEY_RELOAD).equals("1"))
            write(Constant.SHRED_PR.KEY_RELOAD, "0");

    }

    private void init() {
        notificationList = mPullRefreshListView.getRefreshableView();
        notificationList.setDividerHeight(0);
        notificationList.setDivider(null);

        helper = new SQLiteHelper(activity, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Util.isOnline(activity)) {
                    new getNotifications().execute();
                } else {
                    mPullRefreshListView.onRefreshComplete();
                    toast(getResources().getString(R.string.network_error));
                }
            }
        });

    }


    private void reload() {
        list.clear();
        Cursor cursor = db.rawQuery("select * from " + Constant.NotificationTable + " order by Time", null);
        Log.e("Count Notifications", "No :" + cursor.getCount());
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToLast();
                do {
                    NotificationInfo notification = new NotificationInfo();
                    notification.setTitle(cursor.getString(cursor.getColumnIndex(NotificationInfo.TITLE)));
                    notification.setDescription(cursor.getString(cursor.getColumnIndex(NotificationInfo.DESCRIPTION)));
                    notification.setTime(cursor.getString(cursor.getColumnIndex(NotificationInfo.TIME)));
                    notification.setImage_Url(cursor.getString(cursor.getColumnIndex(NotificationInfo.IMAGE_URL)));
                    notification.setType(cursor.getString(cursor.getColumnIndex(NotificationInfo.TYPE)));

                    if (!notification.getType().equals("" + Constant.WORKITEM_MODIFIED))
                        list.add(notification);

                } while (cursor.moveToPrevious());
            }
        }

        try {
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        notificationAdapter = new NotificationListAdapter(activity, list);
        notificationList.setAdapter(notificationAdapter);
        if (list.size() == 0) tvError.setVisibility(View.VISIBLE);
        else tvError.setVisibility(View.GONE);

    }


    public class getNotifications extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            String NotificationType, response;

            NotificationType = "" + Constant.CHANGE_MOBILE_APPROVAL_TO_USER + "," + Constant.USER_RESIGN_APPROVAL_TO_USER + ","
                    + Constant.NEW_USER_ADDED + "," + Constant.NEW_REPORTING_MEMBER + "," + Constant.DELETE_USER + ","
                    + Constant.USER_INVITATION_TO_ADMIN + "," + Constant.INVITE_USER + "," + Constant.UPDATE_ROLE_OR_MANAGER
                    + "," + Constant.ATTENDANCE_APPROVAL_TO_USER + "," + Constant.LEAVE_APPROVAL_TO_USER + ","
                    + Constant.UPDATETIME_APPROVAL_TO_USER + "," + Constant.LEAVE_WITHDRAW_TO_ADMIN + ","
                    + Constant.UPDATE_CUSTOMER_APPROVAL_TO_USER + "," + Constant.NEW_WORKITEM + ","
                    + Constant.REJECT_NEWWORKITEM + "," + Constant.UPDATE_WORKITEM + "," + Constant.REJECT_WORKITEMUPDATE + ","
                    + Constant.CREATE_NEWPRJECT + "," + Constant.MODIFIED_PROJECT + "," + Constant.REJECT_NEWPRJECT + ","
                    + Constant.PROJECT_APPROVED + "," + Constant.PROJECT_REJECTED + ","
                    + Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_USER + "," + Constant.WORKITEM_MODIFIED + ","
                    + Constant.ATTACHMENTS + "," + Constant.UPDATE_USER + "," + Constant.DELETE_COMPANY + ","
                    + Constant.LEAVE_CANCELLED_TO_ADMIN + "," + Constant.UPDATE_COMPANYINFO + ","
                    + Constant.GET_COMPANY_PRIVILAGES + "," + Constant.GET_USER_PRIVILAGES_MANAGER + ","
                    + Constant.GET_USER_PRIVILAGES_TEAMMEMBER + "," + Constant.UPDATE_COMPANY + ","
                    + Constant.UPDATE_CUSTOMER_VENDOR + "," + Constant.UPDATE_USER_COMPANYINFO + ","
                    + Constant.CHANGE_DEVICE_ACK + "," + Constant.USER_PAYSLIP + "," + Constant.ADMIN_PAYROLL
                    + "," + Constant.EXPENSE_NOTIFICATION;

            Log.d("","*********** NotificationType: "+NotificationType);

            params1.add(new BasicNameValuePair("Application", "" + Constant.AppName));
            params1.add(new BasicNameValuePair("Type", NotificationType));
            params1.add(new BasicNameValuePair("LastSynchTime", read(Constant.SHRED_PR.KEY_LASTSYNC_NOTIFICATION_TIME)));
            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL1 + "getNotifications", 1, params1, activity);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            mPullRefreshListView.onRefreshComplete();

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray2 = jsonObject.getJSONArray("data");
                    write(Constant.SHRED_PR.KEY_LASTSYNC_NOTIFICATION_TIME, "" + jsonObject.optString("lastSynchTime"));
                    Log.e("Notifications:", ">>" + jsonArray2);
                    for (int i = jsonArray2.length() - 1; i >= 0; i--) {
                        JSONObject jsonObject1 = jsonArray2.getJSONObject(i);
                        int type = jsonObject1.optInt("Type");
                        String userID = jsonObject1.optString("UserID");
                        String TransactionID = "" + jsonObject1.optString("TransactionID");
                        String CompanyID = "" + jsonObject1.optString("CompanyID");
                        String CurrentCompanyID = read(Constant.SHRED_PR.KEY_COMPANY_ID);
                        if (userID.equals(read(Constant.SHRED_PR.KEY_USERID)) && (CompanyID.equals(CurrentCompanyID) || CompanyID.equals("0"))) {

                            Cursor cursor = null;
                            boolean flag = true;
                            try {
                                if (TransactionID.length() > 0) {
                                    cursor = db.rawQuery("select * from " + Constant.NotificationTable + " where TransactionID like \"" + TransactionID + "\" AND Type like \"" + type + "\"", null);
                                    if (cursor != null && cursor.getCount() > 0) flag = false;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (cursor != null) cursor.close();
                            }

                            if (flag)
                                CommonGCM.InsertIntoNotifications(jsonObject1, activity);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            reload();

            updateCompanyInfo();
        }
    }


    protected void write(String key, String val) {
        Util.WriteSharePrefrence(activity, key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(activity, key);
    }

    protected void toast(int resId) {
        toast(activity.getResources().getText(resId));
    }

    protected void toast(CharSequence text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }


    private void updateCompanyInfo() {
        RestClient.getTeamWork().getCompanyInfo(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID),
                Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_ID),
                Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_TOKEN), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {

                        try {

                            Holidays holidays;
                            CompanyLeaves companyLeaves;
                            HolidaysDAO holidaysDAO = new HolidaysDAO(getActivity());
                            CompanyLeavesDAO companyLeavesDAO = new CompanyLeavesDAO(getActivity());

                            companyLeavesDAO.deleteAll();
                            holidaysDAO.deleteAll();

                            String json = Util.getString(response.getBody().in());
                            JSONObject jObjRaw = new JSONObject(json);

                            String status = jObjRaw.getString("status");
                            if (status.equals("Success"))
                                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA, jObjRaw.getString("companydata"));

                            JSONObject jobj = jObjRaw.getJSONObject("companydata");

                            Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_AUTO_LEAVE_UPDATE, jobj.getString("AutoLeaveUpdate"));

                            //Holidays update in local db
                            JSONArray holidaysJarr = jobj.getJSONArray("holidays");
                            if (holidaysJarr != null) {
                                for (int cnt = 0; cnt < holidaysJarr.length(); cnt++) {
                                    holidays = new Holidays();

                                    holidays.holidayId = holidaysJarr.getJSONObject(cnt).getString("holidayId");
                                    holidays.holidayDate = holidaysJarr.getJSONObject(cnt).getString("holidayDate");
                                    holidays.holidayName = holidaysJarr.getJSONObject(cnt).getString("holidayName");

                                    holidaysDAO.save(holidays);
                                }
                            }

                            //Leaves update in local db
                            JSONArray companyLeavesJarr = jobj.getJSONArray("companyLeaves");
                            if (companyLeavesJarr != null) {
                                for (int cnt = 0; cnt < companyLeavesJarr.length(); cnt++) {
                                    companyLeaves = new CompanyLeaves();

                                    companyLeaves.companyId = companyLeavesJarr.getJSONObject(cnt).getString("companyId");
                                    companyLeaves.leaveType = companyLeavesJarr.getJSONObject(cnt).getString("leaveType");
                                    companyLeaves.noOfLeaves = companyLeavesJarr.getJSONObject(cnt).getString("noOfLeaves");
                                    companyLeaves.leaveUpdateCyle = companyLeavesJarr.getJSONObject(cnt).getString("leaveUpdateCyle");
                                    companyLeaves.active = String.valueOf(companyLeavesJarr.getJSONObject(cnt).getBoolean("active"));
                                    companyLeaves.modifiedBy = companyLeavesJarr.getJSONObject(cnt).getString("modifiedBy");
//                                    companyLeaves.lastModified = companyLeavesJarr.getJSONObject(cnt).getString("lastModified");

                                    companyLeavesDAO.save(companyLeaves);
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

}
