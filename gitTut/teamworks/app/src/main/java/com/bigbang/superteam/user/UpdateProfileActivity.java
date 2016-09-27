package com.bigbang.superteam.user;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.DisplayFullscreenImage;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.CropingOptionAdapter;
import com.bigbang.superteam.admin.SelectLocationActivity;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.model.Address;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.CropingOption;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class  UpdateProfileActivity extends BaseActivity {

    private static final String TAG = UpdateProfileActivity.class.getSimpleName();
    private static final int CROPING_CODE = 2;
    private static final int PICK_FROM_FILE = 3;
    @InjectView(R.id.img_profile_small)
    ImageView imgProfileSmall;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.ll_options)
    LinearLayout llOptions;
    @InjectView(R.id.rl_view_photo)
    RelativeLayout rlViewPhoto;
    @InjectView(R.id.rl_delete_photo)
    RelativeLayout rlDeletePhoto;
    @InjectView(R.id.et_fname)
    EditText etFirstName;
    @InjectView(R.id.et_lname)
    EditText etLastName;
    @InjectView(R.id.et_email)
    EditText etEmail;
    @InjectView(R.id.tvMobile)
    TextView tvMobile;
    @InjectView(R.id.et_permadd1)
    EditText etPermAdd1;
    @InjectView(R.id.et_permadd2)
    EditText etPermAdd2;
    @InjectView(R.id.et_city)
    EditText etCity;
    @InjectView(R.id.et_state)
    EditText etState;
    @InjectView(R.id.et_country)
    EditText etCountry;
    @InjectView(R.id.et_pincode)
    EditText etPincode;
    TransparentProgressDialog progressDialog;
    String userID;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;
    Animation animation, animation1;
    String camera_pathname = "";
    String strImage = "";
    Double latitude, longitude;
    GPSTracker gps;
    private Uri mImageCaptureUri;
    private File outPutFile = null;

    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getString("userid");
        }

        init();

        setData();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Util.isOnline(getApplicationContext())) {
                    getUser(getApplicationContext(), userID);
                }
            }
        }, 100);

    }

    @Override
    public void onResume() {
        super.onResume();

        gps = new GPSTracker(UpdateProfileActivity.this);
        if (!gps.canGetLocation()) gps.showSettingsAlert();

        if (read(Constant.SHRED_PR.KEY_RELOAD).equals("1")) {
            write(Constant.SHRED_PR.KEY_RELOAD, "0");
            if (read(Constant.SHRED_PR.KEY_TEMP_LATITUDE).length() > 0 && read(Constant.SHRED_PR.KEY_TEMP_LONGITUDE).length() > 0) {
                latitude = Double.parseDouble(read(Constant.SHRED_PR.KEY_TEMP_LATITUDE));
                longitude = Double.parseDouble(read(Constant.SHRED_PR.KEY_TEMP_LONGITUDE));
                Log.e("latitude", ">>" + latitude);
                Log.e("longitude", ">>" + longitude);

                new AsyncTask<Void, Void, List<android.location.Address>>() {

                    @Override
                    protected List<android.location.Address> doInBackground(Void... params) {
                        Geocoder geocoder = new Geocoder(UpdateProfileActivity.this, Locale.getDefault());

                        try {
                            List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            return addressList;
                        } catch (IOException e) {
                            Log.e("RegisterAct", "exp==" + e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(List<android.location.Address> addressList) {
                        super.onPostExecute(addressList);

                        try {
                            if (addressList != null && addressList.size() > 0) {

                                String strAddressLine1 = addressList.get(0).getAddressLine(0);
                                String strAddressLine2 = addressList.get(0).getAddressLine(1);
                                String state = addressList.get(0).getAdminArea();
                                String city = addressList.get(0).getSubAdminArea();
                                String countryName = addressList.get(0).getCountryName();
                                String postalCode = addressList.get(0).getPostalCode();
                                //countryName = countryName.replace(" ", "");
                                //state = state.replace(" ", "");

                                if (city == null) {
                                    city = addressList.get(0).getLocality();
                                }

                                etPermAdd1.setText(strAddressLine1);
                                etPermAdd2.setText(strAddressLine2);
                                etCity.setText(city);
                                etState.setText(state);
                                etCountry.setText(countryName);
                                etPincode.setText(postalCode);

                            }
                        } catch (Exception e) {
                            Log.e("RegisterAct", "exp==" + e);
                        }

                    }
                }.execute();

            }
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

    @OnClick(R.id.rlLocation)
    @SuppressWarnings("unused")
    public void Location(View view) {
        startActivity(new Intent(UpdateProfileActivity.this, SelectLocationActivity.class));
        overridePendingTransition(R.anim.enter_from_bottom,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlSave)
    @SuppressWarnings("unused")
    public void Save(View view) {

        hideKeyboard();

        if (isValidate()) {
            if (Util.isOnline(getApplicationContext())) {

                String Email = getText(etEmail);
                String Fname = getText(etFirstName);
                String Lname = getText(etLastName);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Pincode", getText(etPincode));
                hashMap.put("State", getText(etState));
                hashMap.put("Country", getText(etCountry));
                hashMap.put("City", getText(etCity));
                hashMap.put("AddressLine1", getText(etPermAdd1));
                hashMap.put("AddressLine2", getText(etPermAdd2));
                String PermAdd = Util.addJsonKey("{}", hashMap);
                Log.d(TAG, PermAdd);

                updateUser(Email, Fname, Lname, PermAdd);
            } else {
                toast(getResources().getString(R.string.network_error));
            }
        }
    }

    @OnClick(R.id.rlLogo)
    @SuppressWarnings("unused")
    public void ProfileBig(View view) {
        llOptions.setVisibility(View.VISIBLE);
        llOptions.startAnimation(animation);
    }

    @OnClick(R.id.rl_cancel)
    @SuppressWarnings("unused")
    public void Cancel(View view) {
        llOptions.setVisibility(View.GONE);
        llOptions.startAnimation(animation1);
    }

    @OnClick(R.id.rl_view_photo)
    @SuppressWarnings("unused")
    public void ViewPhoto(View view) {
        llOptions.setVisibility(View.GONE);
        strImage = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_Picture);
        if (strImage.length() > 0) {
            Intent myIntent = new Intent(getApplicationContext(), DisplayFullscreenImage.class);
            myIntent.putExtra("URLURI", strImage);
            startActivity(myIntent);
            overridePendingTransition(R.anim.enter_from_bottom,
                    R.anim.hold_bottom);
        }
    }

    @OnClick(R.id.rl_upload_photo)
    @SuppressWarnings("unused")
    public void UploadPhoto(View view) {
        llOptions.setVisibility(View.GONE);
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                PICK_FROM_FILE);

    }

    @OnClick(R.id.rl_delete_photo)
    @SuppressWarnings("unused")
    public void DeletePhoto(View view) {
        llOptions.setVisibility(View.GONE);
        if (Util.isOnline(getApplicationContext())) {
            deletePicture();
        } else {
            Toast.makeText(getApplicationContext(),
                    Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("resultCode", ">>" + resultCode + ":" + data);
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
                                new addPicture().execute();
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

                CropingOptionAdapter adapter = new CropingOptionAdapter(UpdateProfileActivity.this, cropOptions);

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

    private void deletePicture() {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().deletePicture(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID), Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_ID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(UpdateProfileActivity.this);
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
                                Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_Picture, "" + strImage);
                                if (strImage.length() == 0) {
                                    tvName.setVisibility(View.VISIBLE);
                                    rlDeletePhoto.setVisibility(View.GONE);
                                    rlViewPhoto.setVisibility(View.GONE);
                                } else {
                                    tvName.setVisibility(View.GONE);
                                    rlDeletePhoto.setVisibility(View.VISIBLE);
                                    rlViewPhoto.setVisibility(View.VISIBLE);
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

    private void init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        outPutFile = new File(Constant.storageDirectory, "temp.jpg");

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

        progressDialog = new TransparentProgressDialog(UpdateProfileActivity.this, R.drawable.progressdialog, false);
        etPincode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {

                    Util.hideKeyboard(UpdateProfileActivity.this);
                    if (Util.isOnline(getApplicationContext())) {
                        new searchPincode(etPincode.getText().toString().trim()).execute();
                    }

                    return true;
                }
                return false;
            }
        });

        etFirstName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    etLastName.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private void setData() {
        tvName.setText(read(Constant.SHRED_PR.KEY_FIRSTNAME).toUpperCase());
        etFirstName.setText(read(Constant.SHRED_PR.KEY_FIRSTNAME));
        etLastName.setText(read(Constant.SHRED_PR.KEY_LASTNAME));
        etEmail.setText(read(Constant.SHRED_PR.KEY_EMAIL));
        tvMobile.setText(read(Constant.SHRED_PR.KEY_TELEPHONE));
        try {
            Address address = Address.getAddress(read(Constant.SHRED_PR.KEY_PermanentAddress));
            etPincode.setText(address.getPincode());
            etCity.setText(address.getCity());
            etState.setText(address.getState());
            etCountry.setText(address.getCountry());
            etPermAdd1.setText(address.getAddressLine1());
            etPermAdd2.setText(address.getAddressLine2());
        } catch (Exception e) {
            e.printStackTrace();
        }

        strImage = "" + read(Constant.SHRED_PR.KEY_Picture);
        imageLoader.displayImage(strImage, imgProfileSmall, options);
        if (strImage.length() == 0) {
            tvName.setVisibility(View.VISIBLE);
            rlDeletePhoto.setVisibility(View.GONE);
            rlViewPhoto.setVisibility(View.GONE);
        } else {
            tvName.setVisibility(View.GONE);
            rlDeletePhoto.setVisibility(View.VISIBLE);
            rlViewPhoto.setVisibility(View.VISIBLE);
        }

    }


    private void updateUser(final String Email, final String Fname, final String Lname, final String PermAdd) {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().updateUser(userID, read(Constant.SHRED_PR.KEY_COMPANY_ID), Email, Fname, Lname, PermAdd, read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(UpdateProfileActivity.this);
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
                                if (userID.equals(read(Constant.SHRED_PR.KEY_USERID))) {

                                    //update in shared preference:
                                    write(Constant.SHRED_PR.KEY_FIRSTNAME, Fname);
                                    write(Constant.SHRED_PR.KEY_LASTNAME, Lname);
                                    write(Constant.SHRED_PR.KEY_EMAIL, Email);
                                    write(Constant.SHRED_PR.KEY_PermanentAddress, PermAdd);

                                    //update in sqlite table

                                    ContentValues values = new ContentValues();
                                    values.put("firstName", Fname);
                                    values.put("lastName", Lname);
                                    values.put("emailID", Email);

                                    String Table = Constant.tableUsers;
                                    boolean flag = false;
                                    Cursor cursor = db.rawQuery("select * from " + Table + " where userID like \"" + userID + "\"", null);
                                    if (cursor != null && cursor.getCount() > 0) flag = true;
                                    if (flag) {
                                        db.update(Table, values, "userID like \"" + userID + "\"", null);
                                    }
                                    if(cursor!=null) cursor.close();

                                    finish();
                                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                                } else {
                                    write(Constant.SHRED_PR.KEY_RELOAD, "1");
                                    finish();
                                    UserDetailsActivity.userDetailsActivity.finish();
                                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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

    private void getUser(final Context mContext, String UserID) {

        //if (progressDialog != null) progressDialog.show();

        try {
            String companyID = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID);
            RestClient.getCommonService().getUser(UserID, companyID, Constant.AppName, new Callback<Response>() {
                @Override
                public void success(Response rc, Response response) {

                    if (progressDialog != null)
                        if (progressDialog.isShowing()) progressDialog.dismiss();

                    try {
                        Log.e(TAG, ">>" + Util.getString(response.getBody().in()));
                        JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                        String status = jsonObject.optString("status");
                        if (status.equals("Success")) {
                            JSONObject jData = jsonObject.optJSONObject("data");
                            JSONObject jUser = jData.optJSONObject("User");
                            Gson gson = new Gson();
                            User user = gson.fromJson(jUser.toString(), User.class);

                            write(Constant.SHRED_PR.KEY_FIRSTNAME, user.getFirstName());
                            write(Constant.SHRED_PR.KEY_LASTNAME, user.getLastName());
                            write(Constant.SHRED_PR.KEY_TELEPHONE, user.getMobileNo1());
                            write(Constant.SHRED_PR.KEY_Picture, user.getPicture());
                            write(Constant.SHRED_PR.KEY_PermanentAddress, new Gson().toJson(user.getPermanentAddress()));
                            write(Constant.SHRED_PR.KEY_TemporaryAddress, new Gson().toJson(user.getTemporaryAddress()));

                            setData();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isValidate() {
        if (isEmpty(getText(etFirstName))) {
            toast(getResources().getString(R.string.PleaseEnterFirstname));
            return false;
        }
        if (isEmpty(getText(etLastName))) {
            toast(getResources().getString(R.string.PleaseEnterLastname));
            return false;
        }
        if (!getText(etEmail).isEmpty()) {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(getText(etEmail)).matches()) {
                toast(R.string.PleaseEnterEmail);
                return false;
            }
        }

        if (isEmpty(getText(etPincode))) {
            toast(getResources().getString(R.string.PleaseEnterPincode));
            return false;
        }
        if (isEmpty(getText(etPermAdd1))) {
            toast(getResources().getString(R.string.PleaseEnterAdd1));
            return false;
        }
        if (isEmpty(getText(etCity))) {
            toast(getResources().getString(R.string.PleaseEnterCity));
            return false;
        }
        if (isEmpty(getText(etState))) {
            toast(getResources().getString(R.string.PleaseEnterState));
            return false;
        }
        if (isEmpty(getText(etCountry))) {
            toast(getResources().getString(R.string.PleaseEnterCountry));
            return false;
        }

        return true;
    }

    public class addPicture extends AsyncTask<Void, String, String> {


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
                        Constant.URL1 + "addPicture");

                FileBody fbody = null;
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                File image = new File(camera_pathname);
                fbody = new FileBody(image);
                entity.addPart("File", fbody);
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
                    TeamWorkApplication.LogOutClear(UpdateProfileActivity.this);
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
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_Picture, "" + strImage);
                    if (strImage.length() == 0) {
                        tvName.setVisibility(View.VISIBLE);
                        rlDeletePhoto.setVisibility(View.GONE);
                        rlViewPhoto.setVisibility(View.GONE);
                    } else {
                        tvName.setVisibility(View.GONE);
                        rlDeletePhoto.setVisibility(View.VISIBLE);
                        rlViewPhoto.setVisibility(View.VISIBLE);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class searchPincode extends AsyncTask<Void, String, String> {

        String strPincode;

        private searchPincode(String strPincode) {
            this.strPincode = strPincode;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("Pincode", "" + strPincode));

            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL1 + "searchPincode", 2, params1, getApplicationContext());

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            Log.e("result", ">>" + result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                String status = jsonObject.getString("status");
                if (status.equals("Success")) {
                    etCity.setText("" + new JSONObject(jsonObject.optString("data")).optString("city"));
                    etState.setText("" + new JSONObject(jsonObject.optString("data")).optString("state"));
                    etCountry.setText("" + new JSONObject(jsonObject.optString("data")).optString("country"));
                } else {
                    Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
