package com.bigbang.superteam.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.SpinnerAdapter;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by USER 3 on 4/16/2015.
 */
public class InviteUserActivity extends BaseActivity {

    @InjectView(R.id.et_Name)
    EditText etName;
    @InjectView(R.id.et_mobile)
    EditText etMobile;
    @InjectView(R.id.et_mailID)
    EditText etEmail;
    @InjectView(R.id.spinnerRole)
    Spinner spinnerRole;
    @InjectView(R.id.spinnerManager)
    Spinner spinnerManager;
    @InjectView(R.id.rlManager)
    RelativeLayout rlManager;
    TransparentProgressDialog progressDialog;
    private static final String TAG = InviteUserActivity.class.getSimpleName();

    ArrayList<HashMap<String, String>> usersList = new ArrayList<>();
    ArrayList<HashMap<String, String>> managers = new ArrayList<>();
    SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adduser);

        init();
        getUsers();
        spinnerRole.setSelection(2);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }


    private void getUsers() {
        usersList.clear();
        Cursor cursor = db.rawQuery("select * from " + Constant.UserTable, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("userID", "" + cursor.getString(cursor.getColumnIndex("userID")));
                    hashMap.put("firstName", "" + cursor.getString(cursor.getColumnIndex("firstName")));
                    hashMap.put("lastName", "" + cursor.getString(cursor.getColumnIndex("lastName")));
                    hashMap.put("role_id", "" + cursor.getString(cursor.getColumnIndex("role_id")));
                    hashMap.put("role_desc", "" + cursor.getString(cursor.getColumnIndex("role_desc")));
                    usersList.add(hashMap);
                } while (cursor.moveToNext());
            }
        }
        if(cursor!=null) cursor.close();
    }

    private void init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        progressDialog = new TransparentProgressDialog(InviteUserActivity.this, R.drawable.progressdialog, false);

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

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

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setManagers(position + 2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setManagers(int roleID) {
        managers.clear();

        for (int i = 0; i < usersList.size(); i++) {
            HashMap<String, String> hashMap = usersList.get(i);
            int role_id = Integer.parseInt(hashMap.get("role_id"));
            switch (roleID) {
                case 2:
                    if (roleID > role_id)
                        managers.add(hashMap);
                    break;
                case 3:
                    if (roleID >= role_id)
                        managers.add(hashMap);
                    break;
                case 4:
                    if (roleID > role_id)
                        managers.add(hashMap);
                    break;
                default:
                    break;
            }
        }


        Log.e("Size", ">>" + managers.size());
        spinnerManager.setAdapter(new SpinnerAdapter(InviteUserActivity.this, R.layout.listrow_spinner, managers));
        if (managers.size() == 0) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("userID", "" + read(Constant.SHRED_PR.KEY_USERID));
            hashMap.put("firstName", "" + read(Constant.SHRED_PR.KEY_FIRSTNAME));
            hashMap.put("lastName", "" + read(Constant.SHRED_PR.KEY_LASTNAME));
            hashMap.put("role_id", "" + read(Constant.SHRED_PR.KEY_ROLE_ID));
            hashMap.put("role_desc", "" + read(Constant.SHRED_PR.KEY_ROLE));
            managers.add(hashMap);
            spinnerManager.setAdapter(new SpinnerAdapter(InviteUserActivity.this, R.layout.listrow_spinner, managers));
        }

    }

    @OnClick(R.id.rl_add)
    @SuppressWarnings("unused")
    public void addUser(View view) {

        hideKeyboard();
        if (isValidate())
            if (Util.isOnline(getApplicationContext())) {
                int newRoleID = spinnerRole.getSelectedItemPosition() + 2;
                String ManagerID = read(Constant.SHRED_PR.KEY_USERID);
                if (managers.size() > 0)
                    ManagerID = "" + managers.get(spinnerManager.getSelectedItemPosition()).get("userID");
                addUser(newRoleID, ManagerID);
            } else toast(getResources().getString(R.string.network_error));
    }

    public boolean isValidate() {
        if (getText(etName).isEmpty()) {
            toast(R.string.PleaseEnterName);
            return false;
        }
        if (getText(etMobile).isEmpty()) {
            toast(R.string.PleaseEnterMobile);
            return false;
        }
        if (!getText(etEmail).isEmpty()) {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(getText(etEmail)).matches()) {
                toast(R.string.PleaseEnterEmail);
                return false;
            }
        }

        return true;
    }


    private void addUser(int newRoleID, String ManagerID) {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService().addUser("TeamWorks",
                getText(etEmail),
                getText(etMobile),
                getText(etName),
                newRoleID, read(Constant.SHRED_PR.KEY_COMPANY_ID),
                read(Constant.SHRED_PR.KEY_USERID), ManagerID, "0", read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
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
                                TeamWorkApplication.LogOutClear(InviteUserActivity.this);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (response != null) {
                            try {
                                Map<String, String> map = Util.readStatus(response);
                                if (map.containsKey("message")) {
                                    toast(map.get("message"));
                                }
                                if (map.containsKey("status") && map.get("status").equals("Success")) {
                                    write(Constant.SHRED_PR.KEY_RELOAD, "2");
                                    finish();
                                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
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
                    }
                });
    }

}
