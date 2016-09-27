package com.bigbang.superteam.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.DisplayFullscreenImage;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.AddressAdapter;
import com.bigbang.superteam.adapter.CropingOptionAdapter;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.customer_vendor.AddressActivity;
import com.bigbang.superteam.model.Company;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.CropingOption;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UpdateCompanyProfileActivity extends BaseActivity {

    private static final String TAG = UpdateCompanyProfileActivity
            .class.getSimpleName();
    private static final int CROPING_CODE = 2;
    private static final int PICK_FROM_FILE = 3;
    @InjectView(R.id.img_profile_small)
    ImageView imgProfileSmall;
    @InjectView(R.id.tv_profile_name)
    TextView tvProfileName;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.etCompayName)
    EditText etCompayName;
    @InjectView(R.id.etCompanyMobile)
    EditText etCompanyMobile;
    @InjectView(R.id.etCompanyLandline)
    EditText etCompanyLandline;
    @InjectView(R.id.etCompanyDescription)
    EditText etCompanyDescription;
    @InjectView(R.id.etCompanyEmail)
    EditText etCompanyEmail;
    @InjectView(R.id.lvAddress)
    ListView lvAddress;
    @InjectView(R.id.spinnerCompanyType)
    Spinner spinnerCompanyType;
    @InjectView(R.id.ll_options)
    LinearLayout llOptions;
    @InjectView(R.id.rl_view_photo)
    RelativeLayout rlViewPhoto;
    @InjectView(R.id.rl_delete_photo)
    RelativeLayout rlDeletePhoto;
    @InjectView(R.id.rlDeleteCompany)
    RelativeLayout rlDeleteCompany;
    @InjectView(R.id.rlUpdateCompany)
    RelativeLayout rlUpdateCompany;
    @InjectView(R.id.rl_add)
    RelativeLayout rlAdd;
    TransparentProgressDialog progressDialog;
    Company company;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;
    Animation animation, animation1;
    String camera_pathname = "";
    String strImage = "";
    String CreateType;
    Double latitude, longitude;
    GPSTracker gps;
    String AddressID;
    int roleID;
    private Uri mImageCaptureUri;
    private File outPutFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_company_profile);

        init();

        roleID = Integer.parseInt(read(Constant.SHRED_PR.KEY_ROLE_ID));
        switch (roleID) {
            case 1:
                CreateType = "0";
                rlDeleteCompany.setVisibility(View.VISIBLE);
                rlUpdateCompany.setVisibility(View.VISIBLE);
                rlAdd.setVisibility(View.VISIBLE);
                break;
            case 2:
                CreateType = "0";
                rlDeleteCompany.setVisibility(View.GONE);
                rlUpdateCompany.setVisibility(View.VISIBLE);
                rlAdd.setVisibility(View.VISIBLE);
                break;
            default:
                CreateType = "2";
                rlAdd.setVisibility(View.GONE);
                rlDeleteCompany.setVisibility(View.GONE);
                rlUpdateCompany.setVisibility(View.GONE);

                etCompayName.setFocusableInTouchMode(false);
                etCompanyMobile.setFocusableInTouchMode(false);
                etCompanyLandline.setFocusableInTouchMode(false);
                etCompanyEmail.setFocusableInTouchMode(false);
                etCompanyDescription.setFocusableInTouchMode(false);
                spinnerCompanyType.setFocusableInTouchMode(false);
                spinnerCompanyType.setFocusable(false);
                spinnerCompanyType.setEnabled(false);

                break;
        }

        setData();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Util.isOnline(getApplicationContext())) {
                    getCompany(getApplicationContext());
                }
            }
        }, 100);

    }


    @Override
    public void onBackPressed() {

        if (llOptions.getVisibility() == View.VISIBLE) {
            llOptions.setVisibility(View.GONE);
            llOptions.startAnimation(animation1);
        } else {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
        }
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    private void init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        outPutFile = new File(Constant.storageDirectory, "temp.jpg");
        progressDialog = new TransparentProgressDialog(UpdateCompanyProfileActivity.this, R.drawable.progressdialog, false);
        company = new Company();
        lvAddress.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
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
        imageLoader.displayImage("", imgProfileSmall, options);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.enter_from_bottom);
        animation1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.enter_to_bottom);

        etCompanyMobile.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etCompanyMobile.getText().length() == 1 && getText(etCompanyMobile).equals("0")) {
                    etCompanyMobile.setText("");
                    toast(getResources().getString(R.string.mobile_not_zero));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void setData() {

        tvName.setText("" + read(Constant.SHRED_PR.KEY_COMPANY_NAME).toUpperCase());
        etCompayName.setText("" + read(Constant.SHRED_PR.KEY_COMPANY_NAME));
        etCompanyMobile.setText("" + read(Constant.SHRED_PR.KEY_COMPANY_MOBILE));
        etCompanyLandline.setText("" + read(Constant.SHRED_PR.KEY_COMPANY_LANDLINE));
        etCompanyEmail.setText("" + read(Constant.SHRED_PR.KEY_COMPANY_EMAIL));
        etCompanyDescription.setText("" + read(Constant.SHRED_PR.KEY_COMPANY_DESC));

        spinnerCompanyType.setFocusableInTouchMode(false);
        spinnerCompanyType.setFocusable(false);
        spinnerCompanyType.setEnabled(false);
        spinnerCompanyType.setSelection(Integer.parseInt(read(Constant.SHRED_PR.KEY_COMPANY_Type)) - 1);

        final String strName = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_NAME);
        tvProfileName.setText(strName);
        strImage = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_LOGO);
        imageLoader.displayImage(strImage, imgProfileSmall, options);
        if (strImage.trim().length() == 0) {
            rlDeletePhoto.setVisibility(View.GONE);
            rlViewPhoto.setVisibility(View.GONE);
            tvName.setVisibility(View.VISIBLE);
        } else {
            rlDeletePhoto.setVisibility(View.VISIBLE);
            rlViewPhoto.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.GONE);
        }

        try {
            Log.e("AddressList", ">>" + Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_AddressList));
            JSONArray jsonArray = new JSONArray(Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_AddressList));
            Gson gson = new Gson();
            java.lang.reflect.Type listOfTestObject = new TypeToken<List<com.bigbang.superteam.model.Address>>() {
            }.getType();
            ArrayList<com.bigbang.superteam.model.Address> addresses = gson.fromJson(jsonArray.toString(), listOfTestObject);
            company.setAddressList(addresses);

            lvAddress.setAdapter(new AddressAdapter(UpdateCompanyProfileActivity.this, addresses, CreateType));
            Util.setListViewHeightBasedOnChildren(lvAddress);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (read(Constant.SHRED_PR.KEY_RELOAD).equals("1")) {
            write(Constant.SHRED_PR.KEY_RELOAD, "0");

            Gson gson = new Gson();
            com.bigbang.superteam.model.Address address = gson.fromJson(read(Constant.SHRED_PR.KEY_TEMP_ADDRESS), com.bigbang.superteam.model.Address.class);
            Log.e("Addresses", ">>" + company.getAddressList());
            ArrayList<com.bigbang.superteam.model.Address> addresses = company.getAddressList();

            if (read(Constant.SHRED_PR.KEY_DELETE).equals("1")) {
                write(Constant.SHRED_PR.KEY_DELETE, "0");
                int pos = Integer.parseInt(read(Constant.SHRED_PR.KEY_TEMP_POSITION));
                addresses.remove(pos);
            } else {
                if (read(Constant.SHRED_PR.KEY_TEMP_CREATE_TYPE).equals("0")) {
                    write(Constant.SHRED_PR.KEY_TEMP_CREATE_TYPE, "1");
                    int pos = Integer.parseInt(read(Constant.SHRED_PR.KEY_TEMP_POSITION));
                    addresses.remove(pos);
                    addresses.add(pos, address);
                } else {
                    addresses.add(address);
                }
            }

            company.setAddressList(addresses);
            lvAddress.setAdapter(new AddressAdapter(UpdateCompanyProfileActivity.this, addresses, CreateType));
            lvAddress.setSelection(addresses.size() - 1);
            Util.setListViewHeightBasedOnChildren(lvAddress);

        }
    }

    @OnClick(R.id.rl_add)
    @SuppressWarnings("unused")
    public void addUser(View view) {
        Intent intent1 = new Intent(UpdateCompanyProfileActivity.this, AddressActivity.class);
        intent1.putExtra("Create", "1");
        startActivity(intent1);
        overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlUpdateCompany)
    @SuppressWarnings("unused")
    public void UpdateCompany(View view) {
        if (Arrays.asList(1, 2).contains(roleID)) {
            if (isValidate()) {
                if (Util.isOnline(getApplicationContext())) {

                    try {
                        HashMap<String, String> hashMapCompany = new HashMap<String, String>();
                        hashMapCompany.put("Name", "" + getText(etCompayName));
                        hashMapCompany.put("MobileNo", "" + getText(etCompanyMobile));
                        hashMapCompany.put("LandlineNo", "" + getText(etCompanyLandline));
                        hashMapCompany.put("EmailID", "" + getText(etCompanyEmail));
                        hashMapCompany.put("OwnerID", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID));
                        hashMapCompany.put("CreatedBy", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID));
                        hashMapCompany.put("CompanyTypeID", "" + (spinnerCompanyType.getSelectedItemPosition() + 1));
                        hashMapCompany.put("ID", "" + read(Constant.SHRED_PR.KEY_COMPANY_ID));
                        hashMapCompany.put("CompanyID", "0");
                        hashMapCompany.put("Type", "C");
                        hashMapCompany.put("Description", "" + etCompanyDescription.getText().toString());

                        String add_Company = Util.prepareJsonString(hashMapCompany);

                        Gson gson = new Gson();
                        updateCompany(add_Company, gson.toJson(company.getAddressList()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            Constant.network_error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @OnClick(R.id.rlDeleteCompany)
    @SuppressWarnings("unused")
    public void DeleteCompany(View view) {
        if (Arrays.asList(1, 2).contains(roleID)) {
            AlertDialog.Builder alert1 = new AlertDialog.Builder(UpdateCompanyProfileActivity.this);
            alert1.setTitle("" + Constant.AppNameSuper);
            alert1.setMessage(getResources().getString(R.string.company_deleted));
            alert1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @SuppressLint("InlinedApi")
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            if (Util.isOnline(getApplicationContext())) {
                                deleteCompany();
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
    }

    @OnClick(R.id.rlLogo)
    @SuppressWarnings("unused")
    public void ProfileBig(View view) {
        if (Arrays.asList(1, 2).contains(roleID)) {
            llOptions.setVisibility(View.VISIBLE);
            llOptions.startAnimation(animation);
        }
    }

    @OnClick(R.id.rl_cancel)
    @SuppressWarnings("unused")
    public void Cancel(View view) {
        if (Arrays.asList(1, 2).contains(roleID)) {
            llOptions.setVisibility(View.GONE);
            llOptions.startAnimation(animation1);
        }
    }

    @OnClick(R.id.rl_view_photo)
    @SuppressWarnings("unused")
    public void ViewPhoto(View view) {
        if (Arrays.asList(1, 2).contains(roleID)) {
            llOptions.setVisibility(View.GONE);
            strImage = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_LOGO);
            if (strImage.length() > 0) {
                Intent myIntent = new Intent(getApplicationContext(), DisplayFullscreenImage.class);
                myIntent.putExtra("URLURI", strImage);
                startActivity(myIntent);
                overridePendingTransition(R.anim.enter_from_bottom,
                        R.anim.hold_bottom);
            }
        }
    }

    @OnClick(R.id.rl_upload_photo)
    @SuppressWarnings("unused")
    public void UploadPhoto(View view) {
        if (Arrays.asList(1, 2).contains(roleID)) {
            llOptions.setVisibility(View.GONE);
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(intent, "Select File"),
                    PICK_FROM_FILE);
        }
    }

    @OnClick(R.id.rl_delete_photo)
    @SuppressWarnings("unused")
    public void DeletePhoto(View view) {
        if (Arrays.asList(1, 2).contains(roleID)) {
            llOptions.setVisibility(View.GONE);
            if (Util.isOnline(getApplicationContext())) {
                deleteLogo();
            } else {
                Toast.makeText(getApplicationContext(),
                        Constant.network_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {
            switch (requestCode) {
                case PICK_FROM_FILE:

                    try {
                        mImageCaptureUri = data.getData();
                        System.out.println("Gallery Image URI : " + mImageCaptureUri);
                        CropingIMG();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case CROPING_CODE:

                    try {
                        if (outPutFile.exists()) {
                            Bitmap photo = decodeFile(outPutFile);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();

                            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                            camera_pathname = String.valueOf(System.currentTimeMillis())
                                    + ".jpg";
                            File file = new File(Constant.storageDirectory,
                                    camera_pathname);

                            try {
                                file.createNewFile();
                                FileOutputStream fos = new FileOutputStream(file);
                                photo.compress(Bitmap.CompressFormat.PNG, 95, fos);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                Toast.makeText(getApplicationContext(),
                                        "Sorry, Camera Crashed-Please Report as Crash A.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            camera_pathname = Constant.storageDirectory
                                    + "/" + camera_pathname;

                            if (Util.isOnline(getApplicationContext())) {
                                new uploadLogo().execute();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        Constant.network_error, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

            }


        }
    }

    private void CropingIMG() {

        final ArrayList<CropingOption> cropOptions = new ArrayList<CropingOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //TODO: don't use return-data tag because it's not return large image data and crash not given any message
            //intent.putExtra("return-data", true);

            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropingOption co = new CropingOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(UpdateCompanyProfileActivity.this, cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    private void deleteLogo() {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().deleteLogo(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID), Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_ID), read(Constant.SHRED_PR.KEY_TOKEN), "C",
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(UpdateCompanyProfileActivity.this);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            Log.e(TAG, Util.getString(response.getBody().in()));
                            Map<String, String> map = Util.readStatus(response);
                            boolean isSuccess = map.get("status").equals("Success");
                            Toast.makeText(getApplicationContext(), map.get("message"), Toast.LENGTH_SHORT).show();
                            if (isSuccess) {
                                strImage = "";
                                imageLoader.displayImage(strImage, imgProfileSmall, options);
                                Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_LOGO, "" + strImage);
                                if (strImage.trim().length() == 0) {
                                    rlDeletePhoto.setVisibility(View.GONE);
                                    rlViewPhoto.setVisibility(View.GONE);
                                    tvName.setVisibility(View.VISIBLE);
                                } else {
                                    rlDeletePhoto.setVisibility(View.VISIBLE);
                                    rlViewPhoto.setVisibility(View.VISIBLE);
                                    tvName.setVisibility(View.GONE);
                                }
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

    private void deleteCompany() {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().deleteCompany(read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_COMPANY_ID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(UpdateCompanyProfileActivity.this);
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
                                TeamWorkApplication.LogOutClear(UpdateCompanyProfileActivity.this);
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

    private void updateCompany(String CompanyDetails, String AddressDetails) {
        Log.e("AddressDetails:", ">>" + AddressDetails);
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().updateCompany(CompanyDetails, AddressDetails, read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(UpdateCompanyProfileActivity.this);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                            Log.e(TAG, Util.getString(response.getBody().in()));
                            Map<String, String> map = Util.readStatus(response);
                            boolean isSuccess = map.get("status").equals("Success");
                            toast(map.get("message"));
                            if (isSuccess) {
//                                write(Constant.SHRED_PR.KEY_COMPANY_NAME, getText(etCompayName));
//                                write(Constant.SHRED_PR.KEY_COMPANY_MOBILE, getText(etCompanyMobile));
//                                write(Constant.SHRED_PR.KEY_COMPANY_LANDLINE, getText(etCompanyLandline));
//                                write(Constant.SHRED_PR.KEY_COMPANY_EMAIL, getText(etCompanyEmail));
//                                write(Constant.SHRED_PR.KEY_COMPANY_DESC, getText(etCompanyDescription));
//                                write(Constant.SHRED_PR.KEY_COMPANY_Type, "" + (spinnerCompanyType.getSelectedItemPosition() + 1));
//                                Gson gson = new Gson();
//                                write(Constant.SHRED_PR.KEY_COMPANY_AddressList, gson.toJson(company.getAddressList()));

                                Gson gson = new Gson();
                                Company company1 = gson.fromJson("" + jsonObject.optString("data"), Company.class);
                                write(Constant.SHRED_PR.KEY_COMPANY_NAME, company1.getName());
                                write(Constant.SHRED_PR.KEY_COMPANY_MOBILE, company1.getMobileNo());
                                write(Constant.SHRED_PR.KEY_COMPANY_LANDLINE, company1.getLandlineNo());
                                write(Constant.SHRED_PR.KEY_COMPANY_EMAIL, company1.getEmailID());
                                write(Constant.SHRED_PR.KEY_COMPANY_DESC, company1.getDescription());
                                write(Constant.SHRED_PR.KEY_COMPANY_AddressList, gson.toJson(company1.getAddressList()));


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

    public void getCompany(final Context mContext) {

        String companyID = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID);
        RestClient.getCommonService().getCompany(companyID, Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID), Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_TOKEN), new Callback<Response>() {
            @Override
            public void success(Response rc, Response response) {

                try {
                    Log.e(TAG, ">>" + Util.getString(response.getBody().in()));
                    JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                    String status = jsonObject.optString("status");
                    if (status.equals("Success")) {
                        JSONObject jData = jsonObject.optJSONObject("data");
                        Gson gson = new Gson();
                        Company company = gson.fromJson(jData.toString(), Company.class);
                        if (!company.getID().equals("0")) {
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_NAME, company.getName());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_LOGO, company.getLogo());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_MOBILE, company.getMobileNo());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_LANDLINE, company.getLandlineNo());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_EMAIL, company.getEmailID());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_Owner_ID, company.getOwnerID().toString());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_Created_By, company.getCreatedBy().toString());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_Type, "" + company.getCompanyTypeID());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_DESC, company.getDescription());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY, new Gson().toJson(company));
                            //write(Constant.SHRED_PR.KEY_COMPANY_AddressList, new Gson().toJson(company.getAddressList()));
                            try {
                                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_AddressList, "" + jData.optJSONArray("AddressList"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                setData();

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, ">>" + error);
            }
        });

    }

    public boolean isValidate() {
        if (isEmpty(getText(etCompayName))) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCompanyName), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (getText(etCompanyMobile).length() != 10) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCompanyMobile), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isEmpty(getText(etCompanyLandline))) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCompanyLandline), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!getText(etCompanyEmail).isEmpty()) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(getText(etCompanyEmail)).matches()) {
                toast(R.string.PleaseEnterEmail);
                return false;
            }
        }

        if (etCompanyDescription.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCompanyDesc), Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(getApplicationContext(), key);
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(getApplicationContext(), key, val);
    }

    public class uploadLogo extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resp = "";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = null;
                HttpPost poster = new HttpPost(
                        Constant.URL1 + "uploadLogo");

                FileBody fbody = null;
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                File image = new File(camera_pathname);
                fbody = new FileBody(image);
                entity.addPart("File", fbody);
                entity.addPart("Type", new StringBody("C"));
                entity.addPart("CompanyID", new StringBody(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_ID)));
                entity.addPart("UserID", new StringBody(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID)));
                entity.addPart("TokenID", new StringBody(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_TOKEN)));
                poster.setEntity(entity);

                response = client.execute(poster);
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity()
                                .getContent()));
                String line = null;
                while ((line = rd.readLine()) != null) {
                    resp += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Resp Upload", "" + resp);

            try {
                JSONObject jObj = new JSONObject(resp);
                String status = jObj.optString("status");
                Log.e(resp, "response is:- " + resp);
                if (status.equals(Constant.InvalidToken)) {
                    TeamWorkApplication.LogOutClear(UpdateCompanyProfileActivity.this);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resp;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();
            Log.e("Response", ">>" + result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                String status = jsonObject.getString("status");
                if (status.equals("Success")) {

                    strImage = "" + new JSONObject(jsonObject.getString("data")).getString("FilePath");
                    imageLoader.displayImage(strImage, imgProfileSmall, options);
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_LOGO, "" + strImage);
                    if (strImage.trim().length() == 0) {
                        rlDeletePhoto.setVisibility(View.GONE);
                        rlViewPhoto.setVisibility(View.GONE);
                        tvName.setVisibility(View.VISIBLE);
                    } else {
                        rlDeletePhoto.setVisibility(View.VISIBLE);
                        rlViewPhoto.setVisibility(View.VISIBLE);
                        tvName.setVisibility(View.GONE);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
