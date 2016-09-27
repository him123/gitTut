package com.bigbang.superteam.login_register;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.CropingOptionAdapter;
import com.bigbang.superteam.admin.SelectLocationActivity;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.CropingOption;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
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
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RegisterActivity extends BaseActivity {

    @InjectView(R.id.img_profile_pic)
    ImageView imgProfilePic;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.et_fname)
    EditText etFirstName;
    @InjectView(R.id.et_lname)
    EditText etLastName;
    @InjectView(R.id.et_email)
    EditText etEmail;
    @InjectView(R.id.et_mobile)
    EditText etMobile;
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

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private Uri mImageCaptureUri;
    private static final int CROPING_CODE = 2;
    private static final int PICK_FROM_FILE = 3;
    String camera_pathname = "";
    String strImage = "";
    private File outPutFile = null;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;

    Double latitude, longitude;
    GPSTracker gps;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        init();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //set default email address
                try {
                    Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
                    Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
                    for (Account account : accounts) {
                        if (emailPattern.matcher(account.name).matches()) {
                            String possibleEmail = account.name;
                            etEmail.setText(possibleEmail);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //set default mobile number
                try {
                    TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    String mPhoneNumber = tMgr.getLine1Number();
                    etMobile.setText(mPhoneNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //set default FirstName LastName
//                try {
//                    Cursor c = getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
//                    int count = c.getCount();
//                    String[] columnNames = c.getColumnNames();
//                    boolean b = c.moveToFirst();
//                    int position = c.getPosition();
//                    if (count == 1 && position == 0) {
//                        for (int j = 0; j < columnNames.length; j++) {
//                            String columnName = columnNames[j];
//                            String columnValue = c.getString(c.getColumnIndex(columnName));
//                            etFirstName.setText(columnName);
//                            etLastName.setText(columnValue);
//                            Log.d(TAG, "" + columnName + ":" + columnValue);
//                        }
//                    }
//                    c.close();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }
        }, 1000);

    }

    @Override
    public void onResume() {
        super.onResume();

        gps = new GPSTracker(RegisterActivity.this);
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
                        Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());

                        try {
                            List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            return addressList;
                        } catch (Exception e) {
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
        startActivity(new Intent(RegisterActivity.this, SelectLocationActivity.class));
        overridePendingTransition(R.anim.enter_from_bottom,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlLogo)
    @SuppressWarnings("unused")
    public void UploadPhoto(View view) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                PICK_FROM_FILE);
    }

    @OnClick(R.id.rlSave)
    @SuppressWarnings("unused")
    public void Next(View view) {

        if (isValidate()) {
            if (Util.isOnline(getApplicationContext())) {
                if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_GCM_ID).toString().length() == 0)
                    Util.GCM(getApplicationContext());

                String Email = "" + etEmail.getText().toString().trim();
                String Fname = "" + etFirstName.getText().toString().trim();
                String Lname = "" + etLastName.getText().toString().trim();
                String Mob1 = "" + etMobile.getText().toString().trim();
                String PermAddCountry = "" + etCountry.getText().toString().trim();
                String PermAddState = "" + etState.getText().toString().trim();
                String PermAddCity = "" + etCity.getText().toString().trim();
                String PermAddLine1 = "" + etPermAdd1.getText().toString().trim();
                String PermAddLine2 = "" + etPermAdd2.getText().toString().trim();
                String PermAddPincode = "" + etPincode.getText().toString().trim();

                String android_id = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                new createUser(Email, Fname, Lname, Mob1, PermAddCountry, PermAddState, PermAddCity, PermAddLine1, PermAddLine2, PermAddPincode, android_id).execute();
            } else {
                Toast.makeText(getApplicationContext(),
                        Constant.network_error, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void init() {

        Util.GCM(RegisterActivity.this);

        outPutFile = new File(Constant.storageDirectory, "temp.jpg");
        progressDialog = new TransparentProgressDialog(RegisterActivity.this, R.drawable.progressdialog, false);

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
        imageLoader.displayImage("", imgProfilePic, options);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);


        gps = new GPSTracker(RegisterActivity.this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            write(Constant.SHRED_PR.KEY_TEMP_LATITUDE, "" + latitude);
            write(Constant.SHRED_PR.KEY_TEMP_LONGITUDE, "" + longitude);
            write(Constant.SHRED_PR.KEY_RELOAD, "1");
        }

        etPincode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    Util.hideKeyboard(RegisterActivity.this);
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

                            imageLoader.displayImage(Uri.fromFile(new File(camera_pathname)).toString(), imgProfilePic, options);

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
            Toast.makeText(this, "Can't find image cropping app", Toast.LENGTH_SHORT).show();
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

                CropingOptionAdapter adapter = new CropingOptionAdapter(RegisterActivity.this, cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Cropping App");
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

    class searchPincode extends AsyncTask<Void, String, String> {

        String strPincode;

        private searchPincode(String strPincode) {
            this.strPincode = strPincode;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("Pincode", "" + strPincode));

            Log.d("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL1 + "searchPincode", 2, params1, getApplicationContext());

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            //progressBar.setVisibility(View.GONE);
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

    class createUser extends AsyncTask<Void, String, String> {

        String Email, Fname, Lname, Mob1, PermAddCountry, PermAddState, PermAddCity, PermAddLine1, PermAddLine2, PermAddPincode, android_id;

        private createUser(String Email, String Fname, String Lname, String Mob1, String PermAddCountry, String PermAddState, String PermAddCity, String PermAddLine1, String PermAddLine2, String PermAddPincode, String android_id) {
            this.Email = Email;
            this.Fname = Fname;
            this.Lname = Lname;
            this.Mob1 = Mob1;
            this.PermAddCountry = PermAddCountry;
            this.PermAddState = PermAddState;
            this.PermAddCity = PermAddCity;
            this.PermAddLine1 = PermAddLine1;
            this.PermAddLine2 = PermAddLine2;
            this.PermAddPincode = PermAddPincode;
            this.android_id = android_id;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if (progressDialog != null) progressDialog.show();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            String resp = "";
            try {

                JSONObject jsonUserDetails = new JSONObject();
                jsonUserDetails.put("FirstName", Fname);
                jsonUserDetails.put("LastName", Lname);
                jsonUserDetails.put("MobileNo1", Mob1);
                jsonUserDetails.put("EmailID", Email);
                // jsonUserDetails.put("DeviceID", android_id);
                if (android_id.length() <= 0) {
                    jsonUserDetails.put("DeviceID", Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID));
                } else {
                    jsonUserDetails.put("DeviceID", android_id);
                }


                JSONObject jsonPermanentAddress = new JSONObject();
                jsonPermanentAddress.put("Country", PermAddCountry);
                jsonPermanentAddress.put("State", PermAddState);
                jsonPermanentAddress.put("City", PermAddCity);
                jsonPermanentAddress.put("AddressLine1", PermAddLine1);
                jsonPermanentAddress.put("AddressLine2", PermAddLine2);
                jsonPermanentAddress.put("Pincode", PermAddPincode);
                jsonPermanentAddress.put("Lattitude", "0");
                jsonPermanentAddress.put("Longitude", "0");

                HttpClient client = new DefaultHttpClient();
                HttpResponse response = null;
                HttpPost poster = new HttpPost(
                        Constant.URL1 + "createUser");

                FileBody fbody = null;
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                if (camera_pathname.length() > 0) {
                    File image = new File(camera_pathname);
                    fbody = new FileBody(image);
                    entity.addPart("File", fbody);
                }

                Log.e("UserDetails", jsonUserDetails.toString());
                Log.e("Perm", jsonPermanentAddress.toString());

                entity.addPart("Application", new StringBody(Constant.AppName));
                entity.addPart("UserDetails", new StringBody(jsonUserDetails.toString()));
                entity.addPart("PermanentAddress", new StringBody(jsonPermanentAddress.toString()));
                poster.setEntity(entity);


                Log.d("", "********* Request: " + poster.getRequestLine().toString());

                String reqest = poster.getRequestLine().toString();

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
            return resp;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();
            Log.e("result", ">>" + result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                toast("" + jsonObject.getString("message"));

                String status = jsonObject.getString("status");
                if (status.equals("Success")) {
                    write(Constant.SHRED_PR.KEY_TELEPHONE, "" + new JSONObject(jsonObject.getString("data")).getString("MobileNo1"));
                    write(Constant.SHRED_PR.KEY_REPLACE_USERNAME, "1");
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
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
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(getText(etEmail)).matches()) {
                toast(R.string.PleaseEnterEmail);
                return false;
            }
        }
        if (etMobile.getText().toString().trim().length() != 10) {
            toast(getResources().getString(R.string.PleaseEnterMobile));
            return false;
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
}
