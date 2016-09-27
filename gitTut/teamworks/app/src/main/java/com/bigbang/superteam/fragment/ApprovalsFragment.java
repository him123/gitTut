package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.CommonGCM;
import com.bigbang.superteam.GCMIntentService;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.ApprovalListAdapter;
import com.bigbang.superteam.dataObjs.Approval;
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
 * Created by USER 8 on 02-Nov-15.
 */
public class ApprovalsFragment extends Fragment {

    @InjectView(R.id.tvError)
    TextView tvError;
    @InjectView(R.id.pull_refresh_list)
    PullToRefreshView mPullRefreshListView;
    ListView approvalList;
    @InjectView(R.id.progressBar)
    ProgressBar pBar;

    ApprovalListAdapter approvalAdapter;
    public static ArrayList<Approval> listApprovalPending = new ArrayList<>();
    Approval approvalModel;

    SQLiteHelper helper;
    String TAG = "Approvals";
    public static SQLiteDatabase db = null;

    Approval approval;
    MyReceiver myReceiver;
    Activity activity;

    public static Fragment newInstance() {
        ApprovalsFragment fragment = new ApprovalsFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_approvals, container, false);
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

        //reload();

        if (read(Constant.SHRED_PR.KEY_RELOAD).equals("1"))
            write(Constant.SHRED_PR.KEY_RELOAD, "0");

        try {
            myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(GCMIntentService.ACTIVITY_SERVICE);
            activity.registerReceiver(myReceiver, intentFilter);

            if (Util.isOnline(activity)) {
                new getApprovals().execute();
            } else {
                toast(getResources().getString(R.string.network_error));
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }


    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        try {
            activity.unregisterReceiver(myReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            try {
                Bundle extras = arg1.getExtras();
                if (extras != null) {
                    String pos = extras.getString("pos");
                    if (pos != null && pos.equals("1")) {
                        Log.e(TAG, "Inside of MyReceiver");
                        reload();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        approvalList = mPullRefreshListView.getRefreshableView();
        approvalList.setDivider(null);
        approvalList.setDividerHeight(0);

        helper = new SQLiteHelper(activity, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Util.isOnline(activity)) {
                    new getApprovals().execute();
                } else {
                    mPullRefreshListView.onRefreshComplete();
                    toast(getResources().getString(R.string.network_error));
                }
            }
        });

    }


    public void reload() {
        listApprovalPending.clear();
        Cursor cursor = db.rawQuery("select * from " + Constant.ApprovalsTable + " order by Time", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToLast();
                do {
                    try {
                        approval = new Approval();
                        approval.setId(cursor.getString(cursor.getColumnIndex(Approval.ID)));
                        approval.setUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Approval.USERID))));
                        approval.setType(cursor.getString(cursor.getColumnIndex(Approval.TYPE)));
                        approval.setTitle(cursor.getString(cursor.getColumnIndex(Approval.TITLE)));
                        approval.setDescription(cursor.getString(cursor.getColumnIndex(Approval.DESCRIPTION)));
                        approval.setImage(cursor.getString(cursor.getColumnIndex(Approval.IMAGE)));
                        approval.setDate(cursor.getString(cursor.getColumnIndex(Approval.DATE)));
                        approval.setPk(cursor.getInt(cursor.getColumnIndex("pk")));
                        approval.setData(cursor.getString(cursor.getColumnIndex("data")));
                        approval.setEndDate(cursor.getString(cursor.getColumnIndex(Approval.ENDDATE)));
                        approval.setReason(cursor.getString(cursor.getColumnIndex(Approval.REASON)));
                        approval.setStatus(cursor.getInt(cursor.getColumnIndex(Approval.STATUS)));
                        approval.setTransactionID(cursor.getString(cursor.getColumnIndex(Approval.TRANSACTIONID)));
                        approval.setTime(cursor.getString(cursor.getColumnIndex(Approval.TIME)));

                        listApprovalPending.add(approval);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } while (cursor.moveToPrevious());
            }
        }

        try {
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        approvalAdapter = new ApprovalListAdapter(activity, listApprovalPending, pBar);
        approvalList.setAdapter(approvalAdapter);
        if (listApprovalPending.size() == 0) tvError.setVisibility(View.VISIBLE);
        else tvError.setVisibility(View.GONE);
    }


    public class getApprovals extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            String NotificationType, response;

            NotificationType = Constant.APPROVAL_NEWPRJECT + "," + Constant.CREATE_NEWWORKITEM + ","
                    + Constant.EDIT_PROJECT_APPROVAL + "," + Constant.APPROVE_WORKITEMUPDATE + ","
                    + Constant.EDIT_WORKITEM_APPROVAL + "," + Constant.CHANGE_MOBILE_APPROVAL_TO_ADMIN + ","
                    + Constant.USER_RESIGN_APPROVAL_TO_ADMIN + "," + Constant.ATTENDANCE_APPROVAL_TO_ADMIN + ","
                    + Constant.LEAVE_APPROVAL_TO_ADMIN + "," + Constant.UPDATETIME_APPROVAL_TO_ADMIN + ","
                    + Constant.UPDATE_CUSTOMER_APPROVAL_TO_ADMIN + "," + Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_ADMIN
                    + "," + Constant.CHANGE_DEVICE_REQUEST + "," + Constant.EXPENSE_APPROVAL;
            params1.add(new BasicNameValuePair("Application", "" + Constant.AppName));
            params1.add(new BasicNameValuePair("Type", NotificationType));
            params1.add(new BasicNameValuePair("LastSynchTime", read(Constant.SHRED_PR.KEY_LASTSYNC_APPROVAL_TIME)));
            Log.d("params1", ">>" + params1);
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
                    JSONArray jsonArray1 = jsonObject.getJSONArray("data");
                    write(Constant.SHRED_PR.KEY_LASTSYNC_APPROVAL_TIME, "" + jsonObject.optString("lastSynchTime"));
                    Log.e("Approvals:", ">>" + jsonArray1);
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                        int status = jsonObject1.optInt("Status");
                        int type = jsonObject1.optInt("Type");
                        String userID = jsonObject1.optString("UserID");
                        String TransactionID = "" + jsonObject1.optString("TransactionID");
                        String CompanyID = "" + jsonObject1.optString("CompanyID");
                        String CurrentCompanyID = read(Constant.SHRED_PR.KEY_COMPANY_ID);
                        if (userID.equals(read(Constant.SHRED_PR.KEY_USERID)) && (CompanyID.equals(CurrentCompanyID) || CompanyID.equals("0"))) {
                            CommonGCM.InsertIntoApprovals(jsonObject1, activity);
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

}
