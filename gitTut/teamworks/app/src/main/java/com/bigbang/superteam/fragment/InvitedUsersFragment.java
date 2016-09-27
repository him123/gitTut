package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.InvitedUserListAdapter;
import com.bigbang.superteam.model.InvitedUser;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONObject;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by USER 8 on 8/6/2015.
 */
public class InvitedUsersFragment extends Fragment {

    private static final String TAG = InvitedUsersFragment.class.getSimpleName();
    @InjectView(R.id.lv_users)
    ListView lvUsers;
    @InjectView(R.id.tvFail)
    TextView tvFail;
    TransparentProgressDialog progressDialog;
    Activity activity;
    InvitedUserListAdapter adapter;
    View view;

    public static InvitedUsersFragment newInstance() {
        InvitedUsersFragment fragment = new InvitedUsersFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        view = inflater.inflate(R.layout.activity_userlist, container, false);
        ButterKnife.inject(this, view);
        doSearch();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        reload(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (read(Constant.SHRED_PR.KEY_RELOAD).equals("2")) {
            write(Constant.SHRED_PR.KEY_RELOAD, "0");
            reload(true);
        }
    }

    private void init() {
        progressDialog = new TransparentProgressDialog(activity, R.drawable.progressdialog, false);
        lvUsers.setDivider(null);
        lvUsers.setDividerHeight(0);
    }

    private void reload(boolean flag) {
        if (Util.isOnline(activity)) {
            getUsers(flag);
        } else {
            Toast.makeText(activity, "" + Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }


    private void getUsers(boolean flag) {
        if (flag) {
            if (progressDialog != null) progressDialog.show();
        }
        String companyID = Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID);
        RestClient.getCommonService().invitedMembers(companyID, read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN), Constant.AppName, new Callback<List<InvitedUser>>() {
            @Override
            public void success(List<InvitedUser> users, Response response) {
                if (progressDialog != null)
                    if (progressDialog.isShowing()) progressDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                    String status = jObj.optString("status");
                    Log.e(TAG, "response is:- " + response);
                    if (status.equals(Constant.InvalidToken)) {
                        TeamWorkApplication.LogOutClear(getActivity());
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Log.e(TAG, ">>" + Util.getString(response.getBody().in()));
                    adapter = new InvitedUserListAdapter(activity, users, progressDialog);
                    lvUsers.setAdapter(adapter);

                    Intent intent1 = new Intent();
                    // intent.setAction(MY_ACTION);
                    intent1.setAction(activity.ACTIVITY_SERVICE);
                    intent1.putExtra("type", "invited");
                    intent1.putExtra("count", ""
                            + users.size());
                    activity.sendBroadcast(intent1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (users.size() == 0) tvFail.setVisibility(View.VISIBLE);
                else tvFail.setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, ">>" + error);
                if (progressDialog != null)
                    if (progressDialog.isShowing()) progressDialog.dismiss();
            }
        });
    }

    private void doSearch() {
        final EditText et = (EditText) view.findViewById(R.id.edt_search);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    protected void toast(int resId) {
        toast(activity.getResources().getText(resId));
    }

    protected void toast(CharSequence text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(activity, key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(activity, key);
    }

    protected void startActivity(Class klass) {
        startActivity(new Intent(activity, klass));
    }

}
