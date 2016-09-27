package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.CropingOptionAdapter;
import com.bigbang.superteam.admin.SelectLocationActivity;
import com.bigbang.superteam.login_register.CompanySetupActivity;
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
import org.json.JSONArray;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CompanySetupFragment extends Fragment {

    private static final int CROPING_CODE = 2;
    private static final int PICK_FROM_FILE = 3;
    @InjectView(R.id.tvLatLng)
    TextView tvLatLng;
    @InjectView(R.id.etCompayName)
    EditText etCompanyName;
    @InjectView(R.id.etCompanyMobile)
    EditText etMobile;
    @InjectView(R.id.etCompanyLandline)
    EditText etLandline;
    @InjectView(R.id.etCompanyEmail)
    EditText etEmailId;
    @InjectView(R.id.etCompanyDescription)
    EditText etCompanyDescription;
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
    @InjectView(R.id.spinnerCompanyType)
    Spinner spinnerCompanyType;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.img_logo)
    ImageView imgLogo;
    TransparentProgressDialog progressDialog;
    String camera_pathname = "";
    String strImage = "";
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;
    Double latitude, longitude;
    GPSTracker gps;
    private Uri mImageCaptureUri;
    private File outPutFile = null;
    Activity activity;

    // TODO: Rename and change types and number of parameters
    public static CompanySetupFragment newInstance() {
        CompanySetupFragment fragment = new CompanySetupFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity=getActivity();
        View view = inflater.inflate(R.layout.fragment_company_setup, container, false);
        ButterKnife.inject(this, view);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) view.getRootView();
        Util.setAppFont(mContainer, mFont);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gps = new GPSTracker(activity);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            write(Constant.SHRED_PR.KEY_TEMP_LATITUDE, "" + latitude);
            write(Constant.SHRED_PR.KEY_TEMP_LONGITUDE, "" + longitude);
            write(Constant.SHRED_PR.KEY_RELOAD, "1");
        }

        init();
    }

    @Override
    public void onResume() {
        super.onResume();

        gps = new GPSTracker(activity);
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
                        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());

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

                                tvLatLng.setText("Latitude: " + String.format("%.4f", latitude) + "\nLongitude: " + String.format("%.4f", longitude));
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


    private void init() {

        progressDialog = new TransparentProgressDialog(activity, R.drawable.progressdialog, false);
        outPutFile = new File(Constant.storageDirectory, "temp.jpg");

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_profilepic).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.empty_profilepic).showImageOnFail(R.drawable.empty_profilepic).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        imageLoader.init(ImageLoaderConfiguration
                .createDefault(activity));

        imageLoader.displayImage("", imgWhite, options1);
        imageLoader.displayImage("", imgLogo, options);

        etPincode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {

                    Util.hideKeyboard(activity);
                    if (Util.isOnline(activity)) {
                        new searchPincode(etPincode.getText().toString().trim()).execute();
                    }

                    return true;
                }
                return false;
            }
        });

        etMobile.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etMobile.getText().length() == 1 && etMobile.getText().toString().equals("0")) {
                    etMobile.setText("");
                    Toast.makeText(activity, activity.getResources().getString(R.string.mobile_not_zero), Toast.LENGTH_SHORT).show();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

    }

    @OnClick(R.id.rlLocation)
    @SuppressWarnings("unused")
    public void Location(View view) {
        startActivity(new Intent(activity, SelectLocationActivity.class));
        activity.overridePendingTransition(R.anim.enter_from_bottom,
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


    @OnClick(R.id.rlUpdateCompany)
    @SuppressWarnings("unused")
    public void Next(View view) {

        if (isValidate()) {
            if (Util.isOnline(activity)) {

                try {
                    HashMap<String, String> hashMapCompany = new HashMap<String, String>();
                    hashMapCompany.put("Name", "" + etCompanyName.getText().toString());
                    hashMapCompany.put("MobileNo", "" + etMobile.getText().toString());
                    hashMapCompany.put("LandlineNo", "" + etLandline.getText().toString());
                    hashMapCompany.put("EmailID", "" + etEmailId.getText().toString());
                    hashMapCompany.put("OwnerID", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID));
                    hashMapCompany.put("CreatedBy", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID));
                    hashMapCompany.put("CompanyTypeID", "" + (spinnerCompanyType.getSelectedItemPosition() + 1));
                    hashMapCompany.put("CompanyID", "0");
                    hashMapCompany.put("Type", "C");
                    hashMapCompany.put("Description", "" + etCompanyDescription.getText().toString());

                    String add_Company = Util.prepareJsonString(hashMapCompany);

                    HashMap<String, String> hashMapCompanyDetails = new HashMap<String, String>();
                    hashMapCompanyDetails.put("Country", "" + etCountry.getText().toString());
                    hashMapCompanyDetails.put("State", "" + etState.getText().toString());
                    hashMapCompanyDetails.put("City", "" + etCity.getText().toString());
                    hashMapCompanyDetails.put("AddressLine1", "" + etPermAdd1.getText().toString());
                    hashMapCompanyDetails.put("AddressLine2", "" + etPermAdd2.getText().toString());
                    hashMapCompanyDetails.put("Pincode", "" + etPincode.getText().toString());
                    hashMapCompanyDetails.put("Lattitude", "" + latitude);
                    hashMapCompanyDetails.put("Longitude", "" + longitude);
                    hashMapCompanyDetails.put("Type", "1");

                    String add_AddressDetails = Util.prepareJsonString(hashMapCompanyDetails);
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(0, new JSONObject(add_AddressDetails));

                    new addCompany(add_Company, jsonArray.toString()).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(activity,
                        Constant.network_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == activity.RESULT_OK && null != data) {
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
                            //imgLogo.setImageBitmap(photo);

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
                                Toast.makeText(activity,
                                        "Sorry, Camera Crashed-Please Report as Crash A.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            camera_pathname = Constant.storageDirectory
                                    + "/" + camera_pathname;

                            imgLogo.setVisibility(View.VISIBLE);
                            imageLoader.displayImage(Uri.fromFile(new File(camera_pathname)).toString(), imgLogo, options);

                        } else {
                            Toast.makeText(activity, "Error while save image", Toast.LENGTH_SHORT).show();
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

        List<ResolveInfo> list = activity.getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(activity, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
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

                    co.title = activity.getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = activity.getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(activity, cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
                            activity.getContentResolver().delete(mImageCaptureUri, null, null);
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

    public boolean isValidate() {
        if (etCompanyName.getText().toString().trim().length() == 0) {
            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterCompanyName), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etMobile.getText().toString().trim().length() != 10) {
            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterCompanyMobile), Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (etLandline.getText().toString().trim().length() == 0) {
//            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterCompanyLandline), Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (etEmailId.getText().toString().trim().length() != 0) {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmailId.getText().toString()).matches()) {
                Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterEmail), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (etCompanyDescription.getText().toString().trim().length() == 0) {
            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterCompanyDesc), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPermAdd1.getText().toString().trim().length() == 0) {
            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterAdd1), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCity.getText().toString().trim().length() == 0) {
            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterCity), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etState.getText().toString().trim().length() == 0) {
            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterState), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCountry.getText().toString().trim().length() == 0) {
            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterCountry), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPincode.getText().toString().trim().length() == 0) {
            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterPincode), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (latitude == 0.0 && longitude == 0.0) {
            Toast.makeText(activity, activity.getResources().getString(R.string.err_lat_lng), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(activity, key);
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(activity, key, val);
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

            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL1 + "searchPincode", 2, params1, activity);

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
                    Toast.makeText(activity, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public class addCompany extends AsyncTask<Void, String, String> {

        String add_Company, add_AddressDetails;

        private addCompany(String add_Company, String add_AddressDetails) {
            this.add_Company = add_Company;
            this.add_AddressDetails = add_AddressDetails;
        }

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
                        Constant.URL1 + "addCompany");

                FileBody fbody = null;
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                if (camera_pathname.length() > 0) {
                    File image = new File(camera_pathname);
                    fbody = new FileBody(image);
                    entity.addPart("File", fbody);
                }

                Log.e("CompanyDetails", add_Company);
                Log.e("AddressDetails", add_AddressDetails);
                entity.addPart("CompanyDetails", new StringBody(add_Company));
                entity.addPart("AddressDetails", new StringBody(add_AddressDetails));
                entity.addPart("UserID", new StringBody(Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID)));
                entity.addPart("TokenID", new StringBody(Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_TOKEN)));
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
                    TeamWorkApplication.LogOutClear(getActivity());
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
                Toast.makeText(activity, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                String status = jsonObject.getString("status");
                if (status.equals("Success")) {

                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");

                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID, "" + jsonObject1.optInt("ID"));
                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_NAME, "" + jsonObject1.getString("Name"));
                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_LOGO, "" + jsonObject1.getString("Logo"));
                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_MOBILE, "" + jsonObject1.getString("MobileNo"));
                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_LANDLINE, "" + jsonObject1.getString("LandlineNo"));
                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_EMAIL, "" + jsonObject1.getString("EmailID"));
                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_DESC, "" + jsonObject1.getString("Description"));
                    write(Constant.SHRED_PR.KEY_COMPANY_AddressList, "" + jsonObject1.getString("AddressList"));
                    write(Constant.SHRED_PR.KEY_COMPANY_Owner_ID, "" + jsonObject1.getString("OwnerID"));
                    write(Constant.SHRED_PR.KEY_COMPANY_Created_By, "" + jsonObject1.getString("CreatedBy"));
                    write(Constant.SHRED_PR.KEY_COMPANY_Type, "" + jsonObject1.getString("CompanyTypeID"));

                    if (!Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID).equals("0")) {
                        write(Constant.SHRED_PR.KEY_IS_COMPANY_CREATED, "true");
                        CompanySetupActivity.currentPosition++;
                        CompanySetupActivity.pager.setCurrentItem(CompanySetupActivity.currentPosition);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}