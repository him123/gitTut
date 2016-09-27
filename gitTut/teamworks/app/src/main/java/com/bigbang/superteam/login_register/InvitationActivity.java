package com.bigbang.superteam.login_register;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.InvitationAdapter;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InvitationActivity extends BaseActivity {

    @InjectView(R.id.lv_invitations)
    ListView lvInvitations;
    @InjectView(R.id.footer)
    RelativeLayout rlFooter;
    @InjectView(R.id.rlBack)
    RelativeLayout rlBack;
    @InjectView(R.id.rl_skip)
    RelativeLayout rlSkip;
    @InjectView(R.id.tvFail)
    TextView tvFail;
    @InjectView(R.id.imgEmpty)
    ImageButton imgEmpty;
    @InjectView(R.id.rlLogout)
    RelativeLayout rlLogout;
    TransparentProgressDialog progressDialog;
    int FROM;

    private static final String TAG = InvitationActivity.class.getSimpleName();

    ArrayList<HashMap<String, String>> listInvitations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        init();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FROM = Integer.parseInt(extras.getString("from"));
        }

        if (FROM == Constant.FROM_LOGIN) {
            rlFooter.setVisibility(View.VISIBLE);
            rlLogout.setVisibility(View.GONE);
        } else {
            rlFooter.setVisibility(View.GONE);
            rlLogout.setVisibility(View.GONE);
        }

        if (Util.isOnline(getApplicationContext()))
            getInvites();
        else toast(getResources().getString(R.string.network_error));
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (read(Constant.SHRED_PR.KEY_RELOAD).equals("1")) {
            write(Constant.SHRED_PR.KEY_RELOAD, "0");
            if (Util.isOnline(getApplicationContext()))
                getInvites();
            else toast(getResources().getString(R.string.network_error));
        }
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

    @OnClick(R.id.rl_skip)
    @SuppressWarnings("unused")
    public void Skip(View view) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("position", "0");
        hashMap.put("from", "" + FROM);
        startActivityWithData(CompanySetupActivity.class, hashMap);
        //finish();
        overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlLogout)
    @SuppressWarnings("unused")
    public void Logout(View view) {
        AlertDialog.Builder alert1 = new AlertDialog.Builder(InvitationActivity.this);
        alert1.setTitle("" + Constant.AppNameSuper);
        alert1.setMessage("Are you sure you want to exit?");
        alert1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @SuppressLint("InlinedApi")
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (Util.isOnline(getApplicationContext())) {
                            TeamWorkApplication.logout(InvitationActivity.this, progressDialog);
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alert1.setNegativeButton("No",
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
    }

    private void init() {
        progressDialog = new TransparentProgressDialog(InvitationActivity.this, R.drawable.progressdialog, false);
        lvInvitations.setDividerHeight(0);
        lvInvitations.setDivider(null);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);
    }

    private void getInvites() {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService().getInvites(read(Constant.SHRED_PR.KEY_TELEPHONE), read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response1) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            Log.e(TAG, "response is:- " + response);
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(InvitationActivity.this);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        listInvitations.clear();
                        tvFail.setVisibility(View.GONE);
                        imgEmpty.setVisibility(View.GONE);

                        if (response != null) {
                            try {
                                String json = Util.getString(response.getBody().in());
                                Log.e(TAG, ">>" + response);
                                JSONObject jsonObject = new JSONObject(json.toString());
                                String status = jsonObject.getString("status");
                                if (status.equals("Success")) {
                                    JSONArray jDataArray = jsonObject.getJSONArray("data");
                                    Log.e("jDataArray", ">>" + jDataArray.length());
                                    for (int i = 0; i < jDataArray.length(); i++) {

                                        JSONObject jsonObject1 = jDataArray.getJSONObject(i);
                                        HashMap<String, String> hashMap = new HashMap<String, String>();

                                        hashMap.put("ID", jsonObject1.optString("ID"));
                                        hashMap.put("EmailID", jsonObject1.optString("EmailID"));
                                        hashMap.put("Name", jsonObject1.optString("Name"));
                                        hashMap.put("MobileNo", jsonObject1.optString("MobileNo"));
                                        hashMap.put("CreatedBy", jsonObject1.optString("CreatedBy"));
                                        hashMap.put("Status", jsonObject1.optString("Status"));

                                        hashMap.put("Company_ID", new JSONObject(jsonObject1.optString("company")).optString("ID").toString());
                                        hashMap.put("Company_Name", new JSONObject(jsonObject1.optString("company")).optString("Name").toString());
                                        hashMap.put("Logo", new JSONObject(jsonObject1.optString("company")).optString("Logo").toString());

                                        hashMap.put("role_id", new JSONObject(jsonObject1.optString("roles")).optString("id").toString());
                                        hashMap.put("role_desc", new JSONObject(jsonObject1.optString("roles")).optString("desc").toString());

                                        hashMap.put("managerID", new JSONObject(jsonObject1.optString("manager")).optString("UserID").toString());

                                        listInvitations.add(hashMap);
                                    }
                                }

                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }

                        if (listInvitations.size() == 0) {
                            tvFail.setVisibility(View.VISIBLE);
                            imgEmpty.setVisibility(View.VISIBLE);
                            if (FROM == Constant.FROM_LOGIN) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("position", "0");
                                hashMap.put("from", "" + FROM);
                                startActivityWithData(CompanySetupActivity.class, hashMap);
                                finish();
                                overridePendingTransition(R.anim.enter_from_left,
                                        R.anim.hold_bottom);
                            }
                        }
                        else{
                            if (FROM == Constant.FROM_LOGIN) {
                                rlFooter.setVisibility(View.VISIBLE);
                                rlLogout.setVisibility(View.VISIBLE);
                            }
                        }

                        lvInvitations.setAdapter(new InvitationAdapter(InvitationActivity.this, listInvitations, progressDialog, FROM));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                        Log.e(TAG, "" + error);
                    }
                }

        );
    }
}
