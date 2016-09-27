package com.bigbang.superteam.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.DisplayFullscreenImage;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.SpinnerAdapter;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.leave_attendance.UpdateLeaveBalanceActivity;
import com.bigbang.superteam.model.Address;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.payroll.UpdateUserPayrollActivity;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserDetailsActivity extends BaseActivity {

    private static final String TAG = UserDetailsActivity.class.getSimpleName();
    @InjectView(R.id.img_profile_small)
    ImageView imgLogo;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.tvProfileName)
    TextView tvProfileName;
    @InjectView(R.id.tv_mobile)
    TextView tvMobile;
    @InjectView(R.id.tv_email)
    TextView tvEmail;
    @InjectView(R.id.tv_perm_address)
    TextView tvPermAddress;
    @InjectView(R.id.spinnerRole)
    Spinner spinnerRole;
    @InjectView(R.id.spinnerManager)
    Spinner spinnerManager;
    @InjectView(R.id.rlUpdateLeaves)
    RelativeLayout rlUpdateLeaves;
    TransparentProgressDialog progressDialog;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;
    private String pictureURL;
    User user;
    SQLiteDatabase db;

    public static UserDetailsActivity userDetailsActivity;
    ArrayList<HashMap<String, String>> usersList = new ArrayList<>();
    ArrayList<HashMap<String, String>> managers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        userDetailsActivity = this;
        init();
        Bundle data = getIntent().getExtras();
        user = (User) data.getSerializable("user");
        getUsers();

        pictureURL = user.getPicture();
        imageLoader.displayImage(pictureURL, imgLogo, options);

        if (pictureURL.length() > 0) tvName.setVisibility(View.GONE);
        else tvName.setVisibility(View.VISIBLE);

        tvName.setText(user.getFirstName().toUpperCase());
        tvProfileName.setText(user.getFirstName() + " " + user.getLastName());
        tvMobile.setText("" + user.getMobileNo1());
        tvEmail.setText("" + user.getEmailID());
        tvPermAddress.setText(buildAddress(user.getPermanentAddress()));

        spinnerRole.setSelection(user.getRole().getId() - 2);

        if(user.getRole().getId()==1 || user.getRole().getId()==2){
            rlUpdateLeaves.setVisibility(View.GONE);
        }else{
            rlUpdateLeaves.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.rlCall)
    @SuppressWarnings("unused")
    public void Call(View view) {
        try {
            String uri = "tel:" + user.getMobileNo1();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.rlLogo)
    @SuppressWarnings("unused")
    public void showImage(View view) {
        if (pictureURL != null && !pictureURL.isEmpty()) {
            Intent myIntent = new Intent(this, DisplayFullscreenImage.class);
            myIntent.putExtra("URLURI", pictureURL);
            startActivity(myIntent);
            overridePendingTransition(R.anim.enter_from_bottom,
                    R.anim.hold_bottom);
        }
    }

    @OnClick(R.id.rlUpdateLeaves)
    @SuppressWarnings("unused")
    public void UpdateLeaves(View view) {
        Intent intent = new Intent(UserDetailsActivity.this, UpdateLeaveBalanceActivity.class);
        intent.putExtra("userID", "" + user.getUserID());
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlUserPayroll)
    @SuppressWarnings("unused")
    public void UpdatePayroll(View view) {
        Intent intent = new Intent(UserDetailsActivity.this, UpdateUserPayrollActivity.class);
        Bundle b = new Bundle();
        b.putString("userID", "" + user.getUserID());
        b.putSerializable("user",user);
        intent.putExtras(b);
        //intent.putExtra("userID", "" + user.getUserID());
        //intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlResetPassword)
    @SuppressWarnings("unused")
    public void ResetPassword(View view) {
        Intent intent = new Intent(UserDetailsActivity.this, ResetPasswordForMember.class);
        intent.putExtra("userid", "" + user.getUserID());
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlDeleteUser)
    @SuppressWarnings("unused")
    public void DeleteUser(View view) {
        AlertDialog.Builder alert1 = new AlertDialog.Builder(UserDetailsActivity.this);
        alert1.setTitle("" + Constant.AppNameSuper);
        alert1.setMessage("If you delete this user than user will also deleted from SuperSales.\n" +
                "\n" +
                "Are you sure you want to delete this user?");
        alert1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @SuppressLint("InlinedApi")
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (Util.isOnline(getApplicationContext())) {
                            removeUser();
                        } else {
                            toast(getResources().getString(R.string.network_error));
                        }

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
    }

    @OnClick(R.id.rlUpdateRole)
    @SuppressWarnings("unused")
    public void UpdateRole(View view) {
        int newRoleID = spinnerRole.getSelectedItemPosition() + 2;
        String ManagerID = "" + managers.get(spinnerManager.getSelectedItemPosition()).get("userID");

        boolean isRoleChanged = (user.getRole().getId() != newRoleID ? true : false);
        boolean isManagerChanged = (user.getManager().getUserID() != Integer.parseInt(ManagerID) ? true : false);

        if (isManagerChanged || isRoleChanged) {
            if (Util.isOnline(getApplicationContext())) {
                updateUserRole(newRoleID, ManagerID);
            } else {
                toast(getResources().getString(R.string.network_error));
            }
        } else {
            toast(getResources().getString(R.string.please_update_role));
        }
    }

    private void init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        progressDialog = new TransparentProgressDialog(UserDetailsActivity.this, R.drawable.progressdialog, false);

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_profilepic).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.empty_profilepic).showImageOnFail(R.drawable.empty_profilepic).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        imageLoader.init(ImageLoaderConfiguration
                .createDefault(getApplicationContext()));

        imageLoader.displayImage("", imgWhite, options1);
        imageLoader.displayImage("", imgLogo, options);

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

    private void setManagers(int roleID) {
        managers.clear();

        for (int i = 0; i < usersList.size(); i++) {
            HashMap<String, String> hashMap = usersList.get(i);
            if (!user.getUserID().toString().equals(hashMap.get("userID"))) {
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

        }

        Log.e("Size", ">>" + managers.size());
        spinnerManager.setAdapter(new SpinnerAdapter(UserDetailsActivity.this, R.layout.listrow_spinner, managers));
        for (int i = 0; i < managers.size(); i++) {
            if (managers.get(i).get("userID").equals("" + user.getManager().getUserID())) {
                spinnerManager.setSelection(i);
            }
        }

    }

    @NonNull
    private String buildAddress(Address address) {
        StringBuffer sb = new StringBuffer();
        if (address != null) {
            if (!isEmpty(address.getAddressLine1()))
                sb.append(address.getAddressLine1()).append(", ");
            if (!isEmpty(address.getAddressLine2()))
                sb.append(address.getAddressLine2()).append(", ").append("\n");
            if (!isEmpty(address.getCity())) sb.append(address.getCity()).append(" ");
            if (!isEmpty(address.getPincode()))
                sb.append(address.getPincode()).append(", ");
            if (!isEmpty(address.getState()))
                sb.append(address.getState()).append(", ");
            if (!isEmpty(address.getCountry())) sb.append(address.getCountry());
        }
        return sb.toString();
    }


    private void removeUser() {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().removeUser(read(Constant.SHRED_PR.KEY_USERID), "" + user.getUserID(), read(Constant.SHRED_PR.KEY_COMPANY_ID), Constant.AppName, read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(UserDetailsActivity.this);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            Log.e(TAG, Util.getString(response.getBody().in()));
                            Map<String, String> map = Util.readStatus(response);
                            boolean isSuccess = map.get("status").equals("Success");
                            toast(map.get("message"));
                            if (isSuccess) {
                                write(Constant.SHRED_PR.KEY_RELOAD, "1");
                                finish();
                                overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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

    private void updateUserRole(int newRoleID, String ManagerID) {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().updateUserRole(read(Constant.SHRED_PR.KEY_USERID), "" + user.getUserID(), read(Constant.SHRED_PR.KEY_COMPANY_ID), Constant.AppName, ManagerID, "" + user.getRole().getId(), "" + newRoleID, read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(UserDetailsActivity.this);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            Log.e(TAG, Util.getString(response.getBody().in()));
                            Map<String, String> map = Util.readStatus(response);
                            boolean isSuccess = map.get("status").equals("Success");
                            toast(map.get("message"));
                            if (isSuccess) {
                                write(Constant.SHRED_PR.KEY_RELOAD, "1");
                                finish();
                                overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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

}
