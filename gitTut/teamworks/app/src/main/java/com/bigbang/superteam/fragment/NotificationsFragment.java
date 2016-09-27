package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import com.bigbang.superteam.dataObjs.NotificationInfo;
import com.bigbang.superteam.pulltorefresh.PullToRefreshBase;
import com.bigbang.superteam.pulltorefresh.PullToRefreshView;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 3 on 4/8/2015.
 */
public class NotificationsFragment extends Fragment {

    @InjectView(R.id.tvError)
    TextView tvError;
    @InjectView(R.id.pull_refresh_list)
    PullToRefreshView mPullRefreshListView;
    ListView notificationList;
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static Activity mContext;

    ArrayList<NotificationInfo> list = new ArrayList<>();
    NotificationListAdapter notificationAdapter;

    SQLiteHelper helper;
    public static SQLiteDatabase db = null;

    public static Fragment newInstance(String string, Context ctx) {
        NotificationsFragment f = new NotificationsFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, string);
        f.setArguments(bdl);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String message = getArguments().getString(EXTRA_MESSAGE);
        mContext=getActivity();
        View v;
        v = new View(mContext);
        int levelNo = Integer.parseInt(message);
        v = inflater.inflate(R.layout.activity_notificationlist, container, false);
        ButterKnife.inject(this, v);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        reload();
    }

    private void init() {
        notificationList = mPullRefreshListView.getRefreshableView();

        helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Util.isOnline(mContext)) {
                    new getNotifications().execute();
                } else {
                    mPullRefreshListView.onRefreshComplete();
                    toast(getResources().getString(R.string.network_error));
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                new IntentFilter("Notification_Received"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reload();
        }
    };

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

                    list.add(notification);

                } while (cursor.moveToPrevious());
            }
        }

        try{
            if(cursor!=null) cursor.close();
        }catch (Exception e){e.printStackTrace();}

        notificationAdapter = new NotificationListAdapter(mContext, list);
        notificationList.setAdapter(notificationAdapter);
        if (list.size() == 0) tvError.setVisibility(View.VISIBLE);
        else tvError.setVisibility(View.GONE);

    }


    public class getNotifications extends AsyncTask<Void, String, String> {


        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            String NotificationType, response;

            NotificationType = "" + Constant.CHANGE_MOBILE_APPROVAL_TO_USER + "," + Constant.USER_RESIGN_APPROVAL_TO_USER
                    + "," + Constant.NEW_USER_ADDED + "," +Constant.NEW_REPORTING_MEMBER+","
                    + Constant.DELETE_USER + "," + Constant.USER_INVITATION_TO_ADMIN + "," + Constant.INVITE_USER
                    + "," + Constant.UPDATE_ROLE_OR_MANAGER + "," + Constant.ATTENDANCE_APPROVAL_TO_USER + ","
                    + Constant.LEAVE_APPROVAL_TO_USER + "," + Constant.UPDATETIME_APPROVAL_TO_USER + ","
                    + Constant.LEAVE_WITHDRAW_TO_ADMIN + "," + Constant.UPDATE_CUSTOMER_APPROVAL_TO_USER + ","
                    + Constant.NEW_WORKITEM + "," + Constant.REJECT_NEWWORKITEM + "," + Constant.UPDATE_WORKITEM
                    + "," + Constant.REJECT_WORKITEMUPDATE + "," + Constant.CREATE_NEWPRJECT + ","
                    + Constant.REJECT_NEWPRJECT + "," + Constant.PROJECT_APPROVED + "," + Constant.PROJECT_REJECTED + ","
                    + Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_USER + "," + Constant.WORKITEM_MODIFIED + ","
                    + Constant.ATTACHMENTS + "," + Constant.UPDATE_USER + "," + Constant.DELETE_COMPANY+ ","
                    + Constant.CHANGE_DEVICE_ACK + "," + Constant.ADMIN_PAYROLL + "," + Constant.USER_PAYSLIP
                    + "," + Constant.EXPENSE_NOTIFICATION;
            params1.add(new BasicNameValuePair("Application", "" + Constant.AppName));
            params1.add(new BasicNameValuePair("Type", NotificationType));
            params1.add(new BasicNameValuePair("LastSynchTime", read(Constant.SHRED_PR.KEY_LASTSYNC_NOTIFICATION_TIME)));
            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL1 + "getNotifications", 1, params1, mContext);
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
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject jsonObject1 = jsonArray2.getJSONObject(i);
                        int type = jsonObject1.optInt("Type");
                        String userID = jsonObject1.optString("UserID");
                        String TransactionID = "" + jsonObject1.optString("TransactionID");
                        String CompanyID = "" + jsonObject1.optString("CompanyID");
                        String CurrentCompanyID = read(Constant.SHRED_PR.KEY_COMPANY_ID);
                        if (userID.equals(read(Constant.SHRED_PR.KEY_USERID)) && (CompanyID.equals(CurrentCompanyID) || CompanyID.equals("0"))) {

                            Cursor cursor=null;
                            boolean flag = true;
                            try {
                                if (TransactionID.length() > 0) {
                                    cursor = db.rawQuery("select * from " + Constant.NotificationTable + " where TransactionID like \"" + TransactionID + "\" AND Type like \"" + type + "\"", null);
                                    if (cursor != null && cursor.getCount() > 0) flag = false;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            finally {
                                if(cursor!=null) cursor.close();
                            }

                            if (flag)
                                CommonGCM.InsertIntoNotifications(jsonObject1, mContext);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            reload();

        }
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(mContext, key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(mContext, key);
    }

    protected void toast(int resId) {
        toast(mContext.getResources().getText(resId));
    }

    protected void toast(CharSequence text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

}
