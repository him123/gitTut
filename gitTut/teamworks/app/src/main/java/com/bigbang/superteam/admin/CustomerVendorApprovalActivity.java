package com.bigbang.superteam.admin;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.AddressApprovalAdapter;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.Approval;
import com.bigbang.superteam.model.Address;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CustomerVendorApprovalActivity extends BaseActivity {

    @InjectView(R.id.img_profile_small)
    ImageView imgLogo;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.tvName1)
    TextView tvName1;
    @InjectView(R.id.tvProfileName)
    TextView tvProfileName;
    @InjectView(R.id.title)
    TextView tvTitle;
    @InjectView(R.id.rlName)
    RelativeLayout rlName;
    @InjectView(R.id.tvOldName)
    TextView tvOldName;
    @InjectView(R.id.tvNewName)
    TextView tvNewName;
    @InjectView(R.id.rlMobile)
    RelativeLayout rlMobile;
    @InjectView(R.id.tvOldMobile)
    TextView tvOldMobile;
    @InjectView(R.id.tvNewMobile)
    TextView tvNewMobile;
    @InjectView(R.id.rlLandline)
    RelativeLayout rlLandline;
    @InjectView(R.id.tvOldLandline)
    TextView tvOldLandline;
    @InjectView(R.id.tvNewLandline)
    TextView tvNewLandline;
    @InjectView(R.id.rlEmail)
    RelativeLayout rlEmail;
    @InjectView(R.id.tvOldEmail)
    TextView tvOldEmail;
    @InjectView(R.id.tvNewEmail)
    TextView tvNewEmail;
    @InjectView(R.id.rlDesc)
    RelativeLayout rlDesc;
    @InjectView(R.id.tvOldDesc)
    TextView tvOldDesc;
    @InjectView(R.id.tvNewDesc)
    TextView tvNewDesc;
    @InjectView(R.id.rlUpdatedAddress)
    RelativeLayout rlUpdatedAddress;
    @InjectView(R.id.lvUpdatedAddress)
    ListView lvUpdatedAddress;
    @InjectView(R.id.rlNewAddress)
    RelativeLayout rlNewAddress;
    @InjectView(R.id.lvNewAddress)
    ListView lvNewAddress;
    @InjectView(R.id.rlDeletedAddress)
    RelativeLayout rlDeletedAddress;
    @InjectView(R.id.lvDeletedAddress)
    ListView lvDeletedAddress;

    Approval approval;
    String MemberID, TransactionID;
    TransparentProgressDialog progressDialog;
    private static final String TAG = CustomerVendorApprovalActivity.class.getSimpleName();

    ArrayList<HashMap<String, String>> listNewAddresses = new ArrayList<>();
    ArrayList<HashMap<String, String>> listDeletedAddresses = new ArrayList<>();
    ArrayList<HashMap<String, String>> listUpdatedOldAddresses = new ArrayList<>();
    ArrayList<HashMap<String, String>> listUpdatedNewAddresses = new ArrayList<>();

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;
    Context mContext = this;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_vendor_approval);

        init();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            approval = (Approval) getIntent().getSerializableExtra("Approval");
            MemberID = "" + approval.getUserId();
            TransactionID = "" + approval.getTransactionID();

            if (approval.getDescription().toLowerCase().contains(getResources().getString(R.string.update_client).toLowerCase())) {
                tvTitle.setText(getResources().getString(R.string.update_client));
            } else {
                tvTitle.setText(getResources().getString(R.string.update_vendor));
            }

            tvProfileName.setText("" + approval.getTitle());
            tvName1.setText("" + approval.getTitle().toUpperCase());

            String logo = approval.getImage();
            imageLoader.displayImage(logo, imgLogo, options);

            if (logo.length() > 0) tvName1.setVisibility(View.GONE);
            else tvName1.setVisibility(View.VISIBLE);
        }

        if (Util.isOnline(getApplicationContext())) {
            viewCustomerUpdates();
        } else toast(getResources().getString(R.string.network_error));

    }

    private void init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);
        progressDialog = new TransparentProgressDialog(CustomerVendorApprovalActivity.this, R.drawable.progressdialog, false);

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
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

        lvUpdatedAddress.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        lvNewAddress.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        lvDeletedAddress.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

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

    @OnClick(R.id.rlAccept)
    @SuppressWarnings("unused")
    public void Accept(View view) {
        new acceptUpdateCustomer("true",mContext).execute();
    }

    @OnClick(R.id.rlReject)
    @SuppressWarnings("unused")
    public void Reject(View view) {
        new acceptUpdateCustomer("false",mContext).execute();
    }

    private void viewCustomerUpdates() {

        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService().viewCustomerUpdates(TransactionID, MemberID, read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {
                if (progressDialog != null)
                    if (progressDialog.isShowing()) progressDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                    String status = jObj.optString("status");
                    Log.e(TAG, "response is:- " + response);

                    if (status.equals("Success")) {
                        JSONObject jData = jObj.optJSONObject("data");

                        String name = "" + jData.optString("Name");
                        if (name.length() > 0) {
                            rlName.setVisibility(View.VISIBLE);
                            tvOldName.setText("" + jData.optJSONObject("Name").optString("Old"));
                            tvNewName.setText("" + jData.optJSONObject("Name").optString("New"));
                        } else rlName.setVisibility(View.GONE);

                        String mobile = "" + jData.optString("MobileNo");
                        if (mobile.length() > 0) {
                            rlMobile.setVisibility(View.VISIBLE);
                            tvOldMobile.setText("" + jData.optJSONObject("MobileNo").optString("Old"));
                            tvNewMobile.setText("" + jData.optJSONObject("MobileNo").optString("New"));
                        } else rlMobile.setVisibility(View.GONE);

                        String Landline = "" + jData.optString("Landline");
                        if (Landline.length() > 0) {
                            rlLandline.setVisibility(View.VISIBLE);
                            tvOldLandline.setText("" + jData.optJSONObject("Landline").optString("Old"));
                            tvNewLandline.setText("" + jData.optJSONObject("Landline").optString("New"));
                        } else rlLandline.setVisibility(View.GONE);

                        String Email = "" + jData.optString("EmailID");
                        if (Email.length() > 0) {
                            rlEmail.setVisibility(View.VISIBLE);
                            tvOldEmail.setText("" + jData.optJSONObject("EmailID").optString("Old"));
                            tvNewEmail.setText("" + jData.optJSONObject("EmailID").optString("New"));
                        } else rlEmail.setVisibility(View.GONE);

                        String Description = "" + jData.optString("Description");
                        if (Description.length() > 0) {
                            rlDesc.setVisibility(View.VISIBLE);
                            tvOldDesc.setText("" + jData.optJSONObject("Description").optString("Old"));
                            tvNewDesc.setText("" + jData.optJSONObject("Description").optString("New"));
                        } else rlDesc.setVisibility(View.GONE);

                        Gson gson = new Gson();
                        java.lang.reflect.Type listOfTestObject = new TypeToken<List<Address>>() {
                        }.getType();

                        String NewAddress = "" + jData.optString("NewAddress");
                        if (NewAddress.length() > 0) {
                            rlNewAddress.setVisibility(View.VISIBLE);

                            JSONArray jsonArray = jData.optJSONArray("NewAddress");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                HashMap<String, String> hashMap = new HashMap<String, String>();

                                String address = "" + jsonObject.optString("AddressLine1") + " " + jsonObject.optString("AddressLine2") + "\n" + jsonObject.optString("City") + "-" + jsonObject.optString("Pincode") + " " + jsonObject.optString("State") + " " + jsonObject.optString("Country");
                                String type = "";

                                String[] addressType = getResources().getStringArray(R.array.address_type_array);
                                if (addressType.length > 0) {
                                    type = "Permanent Address";
                                    for (int j = 0; j < addressType.length; j++) {
                                        if (Integer.parseInt(jsonObject.optString("Type")) == (j + 2)) {
                                            type = "" + addressType[j] + " Address";
                                        }
                                    }
                                }

                                hashMap.put("Address", "" + address);
                                hashMap.put("Type", "" + type);
                                listNewAddresses.add(hashMap);
                            }
                            lvNewAddress.setAdapter(new AddressApprovalAdapter(CustomerVendorApprovalActivity.this, listNewAddresses, null));
                            Util.setListViewHeightBasedOnChildren(lvNewAddress);

                        } else rlNewAddress.setVisibility(View.GONE);


                        String DeletedAddress = "" + jData.optString("DeletedAddress");
                        if (DeletedAddress.length() > 0) {
                            rlDeletedAddress.setVisibility(View.VISIBLE);

                            JSONArray jsonArray = jData.optJSONArray("DeletedAddress");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                HashMap<String, String> hashMap = new HashMap<String, String>();

                                String address = "" + jsonObject.optString("AddressLine1") + " " + jsonObject.optString("AddressLine2") + "\n" + jsonObject.optString("City") + "-" + jsonObject.optString("Pincode") + " " + jsonObject.optString("State") + " " + jsonObject.optString("Country");
                                String type = "";

                                String[] addressType = getResources().getStringArray(R.array.address_type_array);
                                if (addressType.length > 0) {
                                    type = "Permanent Address";
                                    for (int j = 0; j < addressType.length; j++) {
                                        if (Integer.parseInt(jsonObject.optString("Type")) == (j + 2)) {
                                            type = "" + addressType[j] + " Address";
                                        }
                                    }
                                }

                                hashMap.put("Address", "" + address);
                                hashMap.put("Type", "" + type);
                                listDeletedAddresses.add(hashMap);
                            }
                            lvDeletedAddress.setAdapter(new AddressApprovalAdapter(CustomerVendorApprovalActivity.this, listDeletedAddresses, null));
                            Util.setListViewHeightBasedOnChildren(lvDeletedAddress);

                        } else rlDeletedAddress.setVisibility(View.GONE);

                        String UpdatedOldAddress = "" + jData.optString("UpdatedOldAddress");
                        if (UpdatedOldAddress.length() > 0) {
                            rlUpdatedAddress.setVisibility(View.VISIBLE);

                            JSONArray jsonArray = jData.optJSONArray("UpdatedOldAddress");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                HashMap<String, String> hashMap = new HashMap<String, String>();

                                String address = "" + jsonObject.optString("AddressLine1") + " " + jsonObject.optString("AddressLine2") + "\n" + jsonObject.optString("City") + "-" + jsonObject.optString("Pincode") + " " + jsonObject.optString("State") + " " + jsonObject.optString("Country");
                                String type = "";

                                String[] addressType = getResources().getStringArray(R.array.address_type_array);
                                if (addressType.length > 0) {
                                    type = "Permanent Address";
                                    for (int j = 0; j < addressType.length; j++) {
                                        if (Integer.parseInt(jsonObject.optString("Type")) == (j + 2)) {
                                            type = "" + addressType[j] + " Address";
                                        }
                                    }
                                }

                                hashMap.put("Address", "" + address);
                                hashMap.put("Type", "" + type);
                                listUpdatedOldAddresses.add(hashMap);
                            }

                            JSONArray jsonArray1 = jData.optJSONArray("UpdateNewdAddress");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject = jsonArray1.optJSONObject(i);
                                HashMap<String, String> hashMap = new HashMap<String, String>();

                                String address = "" + jsonObject.optString("AddressLine1") + " " + jsonObject.optString("AddressLine2") + "\n" + jsonObject.optString("City") + "-" + jsonObject.optString("Pincode") + " " + jsonObject.optString("State") + " " + jsonObject.optString("Country");
                                String type = "";

                                String[] addressType = getResources().getStringArray(R.array.address_type_array);
                                if (addressType.length > 0) {
                                    type = "Permanent Address";
                                    for (int j = 0; j < addressType.length; j++) {
                                        if (Integer.parseInt(jsonObject.optString("Type")) == (j + 2)) {
                                            type = "" + addressType[j]+" Address";
                                        }
                                    }
                                }

                                hashMap.put("Address", "" + address);
                                hashMap.put("Type", "" + type);
                                listUpdatedNewAddresses.add(hashMap);
                            }

                            lvUpdatedAddress.setAdapter(new AddressApprovalAdapter(CustomerVendorApprovalActivity.this, listUpdatedOldAddresses, listUpdatedNewAddresses));
                            Util.setListViewHeightBasedOnChildren(lvUpdatedAddress);

                        } else rlUpdatedAddress.setVisibility(View.GONE);


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, ">>" + error);
                if (progressDialog != null)
                    if (progressDialog.isShowing()) progressDialog.dismiss();
            }
        });

    }


    class acceptUpdateCustomer extends AsyncTask<Void, String, String> {

        String Approved;
        String response;
        Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) {
                progressDialog.show();
            }
        }

        public acceptUpdateCustomer(String Approved, Context context) {
            this.Approved = Approved;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject(approval.getData());

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("MemberID", "" + approval.getUserId()));
                params1.add(new BasicNameValuePair("TransactionID", "" + approval.getTransactionID()));
                params1.add(new BasicNameValuePair("Approved", Approved));
                params1.add(new BasicNameValuePair("Application", Constant.AppName));
                response = Util.makeServiceCall(Constant.URL1 + "acceptUpdateCustomer", 2, params1, context);
                Log.d("params1", ">>" + params1);

                Log.v(TAG, "** response is:- " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progressDialog != null) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }

            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(context, "" + jObj.getString("message"), Toast.LENGTH_SHORT).show();
                if (status.equals("Success")) {
                    int approvalStatus=1;
                    if(Approved.equals("true")){
                        approvalStatus=2;
                    }
                    else{
                        approvalStatus=3;
                    }
                    ContentValues values = new ContentValues();
                    values.put("Status", "" + approvalStatus);

                    db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + approval.getTransactionID() + "\"", null);
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
