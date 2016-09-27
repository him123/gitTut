package com.bigbang.superteam.user;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.YourManagersAdapter;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class YourManagersActivity extends BaseActivity {

    @InjectView(R.id.lvManagers)
    ListView lvManagers;
    TransparentProgressDialog progressDialog;
    private static final String TAG = YourManagersActivity.class.getSimpleName();

    ArrayList<HashMap<String, String>> listManagers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_managers);

        init();

        if (Util.isOnline(getApplicationContext()))
            getInvites();
        else toast(getResources().getString(R.string.network_error));
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

    private void init() {

        progressDialog = new TransparentProgressDialog(YourManagersActivity.this, R.drawable.progressdialog, false);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        lvManagers.setDivider(null);
        lvManagers.setDividerHeight(0);

    }


    private void getInvites() {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService().getManagerHierarchy(Constant.AppName, read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
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
                                TeamWorkApplication.LogOutClear(YourManagersActivity.this);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        listManagers.clear();

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

                                        hashMap.put("UserID", jsonObject1.optString("UserID"));
                                        hashMap.put("EmailID", jsonObject1.optString("EmailID"));
                                        hashMap.put("FirstName", jsonObject1.optString("FirstName"));
                                        hashMap.put("LastName", jsonObject1.optString("LastName"));
                                        hashMap.put("MobileNo1", jsonObject1.optString("MobileNo1"));
                                        hashMap.put("picture", jsonObject1.optString("picture"));

                                        hashMap.put("role_id", new JSONObject(jsonObject1.optString("role")).optString("id").toString());
                                        hashMap.put("role_desc", new JSONObject(jsonObject1.optString("role")).optString("desc").toString());

                                        listManagers.add(hashMap);
                                    }
                                }

                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }

                        Collections.reverse(listManagers);
                        lvManagers.setAdapter(new YourManagersAdapter(YourManagersActivity.this, listManagers));
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
