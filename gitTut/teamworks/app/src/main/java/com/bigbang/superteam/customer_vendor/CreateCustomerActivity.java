package com.bigbang.superteam.customer_vendor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.CropingOptionAdapter;
import com.bigbang.superteam.admin.SelectLocationActivity;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.model.Customer;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.InjectView;
import butterknife.OnClick;

public class CreateCustomerActivity extends BaseActivity {

    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.etName)
    EditText etName;
    @InjectView(R.id.etMobile)
    EditText etMobile;
    @InjectView(R.id.etLandline)
    EditText etLandline;
    @InjectView(R.id.etEmailId)
    EditText etEmailId;
    @InjectView(R.id.etDescription)
    EditText etDescription;
    @InjectView(R.id.et_permadd1)
    EditText etPermAdd1;
    @InjectView(R.id.et_permadd2)
    EditText etPermAdd2;
    @InjectView(R.id.et_city)
    EditText etPermCity;
    @InjectView(R.id.et_state)
    EditText etPermState;
    @InjectView(R.id.et_country)
    EditText etPermCountry;
    @InjectView(R.id.et_pincode)
    EditText etPermPincode;
    @InjectView(R.id.tvLatLng)
    TextView tvLatLng;
    @InjectView(R.id.spinnerType)
    Spinner spinnerType;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.imgLogo)
    ImageView imgLogo;
    @InjectView(R.id.ll_company_address)
    LinearLayout llAddAddress;
    @InjectView(R.id.icon_phnbook)
    ImageButton icon_phnbook;
    TransparentProgressDialog progressDialog;

    String CustomerVendorType = "U";
    Customer customer;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;

    private Uri mImageCaptureUri;
    private static final int CROPING_CODE = 2;
    private static final int PICK_FROM_FILE = 3;
    String camera_pathname = "";
    private File outPutFile = null;

    Double latitude, longitude;
    GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            CustomerVendorType = extras.getString("Type");
        }

        gps = new GPSTracker(CreateCustomerActivity.this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            write(Constant.SHRED_PR.KEY_TEMP_LATITUDE, "" + latitude);
            write(Constant.SHRED_PR.KEY_TEMP_LONGITUDE, "" + longitude);
            write(Constant.SHRED_PR.KEY_RELOAD, "1");
        }

        init();

        if (CustomerVendorType.equals("U"))
            tvTitle.setText(getResources().getString(R.string.add_client));
        else tvTitle.setText(getResources().getString(R.string.add_vendor));

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

    static public final int PICK_CONTACT = 0;

    @OnClick(R.id.icon_phnbook)
    @SuppressWarnings("unused")
    public void readcontact() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.rlLocation)
    @SuppressWarnings("unused")
    public void Location(View view) {
        startActivity(new Intent(CreateCustomerActivity.this, SelectLocationActivity.class));
        overridePendingTransition(R.anim.enter_from_bottom,
                R.anim.hold_bottom);
    }

    @Override
    public void onResume() {
        super.onResume();

        gps = new GPSTracker(CreateCustomerActivity.this);
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
                        Geocoder geocoder = new Geocoder(CreateCustomerActivity.this, Locale.getDefault());

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
                                etPermCity.setText(city);
                                etPermState.setText(state);
                                etPermCountry.setText(countryName);
                                etPermPincode.setText(postalCode);

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

        progressDialog = new TransparentProgressDialog(CreateCustomerActivity.this, R.drawable.progressdialog, false);

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
                .createDefault(getApplicationContext()));

        imageLoader.displayImage("", imgWhite, options1);
        imageLoader.displayImage("", imgLogo, options);

        outPutFile = new File(Constant.storageDirectory, "temp.jpg");

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);


        etPermPincode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {

                    Util.hideKeyboard(CreateCustomerActivity.this);
                    if (Util.isOnline(getApplicationContext())) {
                        new searchPincode(etPermPincode.getText().toString().trim()).execute();
                    }

                    return true;
                }
                return false;
            }
        });

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

    @OnClick(R.id.rl_next)
    @SuppressWarnings("unused")
    public void Next(View view) {

        if (isValidate()) {
            if (Util.isOnline(getApplicationContext())) {
                try {
                    HashMap<String, String> hashMapCompany = new HashMap<String, String>();
                    hashMapCompany.put("Name", "" + etName.getText().toString());
                    hashMapCompany.put("MobileNo", "" + etMobile.getText().toString());
                    hashMapCompany.put("LandlineNo", "" + etLandline.getText().toString());
                    hashMapCompany.put("EmailID", "" + etEmailId.getText().toString());
                    hashMapCompany.put("OwnerID", "" + read(Constant.SHRED_PR.KEY_USERID));
                    hashMapCompany.put("CreatedBy", "" + read(Constant.SHRED_PR.KEY_USERID));
                    hashMapCompany.put("CompanyTypeID", "" + (spinnerType.getSelectedItemPosition() + 1));
                    hashMapCompany.put("CompanyID", "" + read(Constant.SHRED_PR.KEY_COMPANY_ID));
                    hashMapCompany.put("Type", CustomerVendorType);
                    hashMapCompany.put("Description", "" + etDescription.getText().toString());


                    HashMap<String, String> hashMapCompanyDetails = new HashMap<String, String>();
                    hashMapCompanyDetails.put("Country", "" + etPermCountry.getText().toString());
                    hashMapCompanyDetails.put("State", "" + etPermState.getText().toString());
                    hashMapCompanyDetails.put("City", "" + etPermCity.getText().toString());
                    hashMapCompanyDetails.put("AddressLine1", "" + etPermAdd1.getText().toString());
                    hashMapCompanyDetails.put("AddressLine2", "" + etPermAdd2.getText().toString());
                    hashMapCompanyDetails.put("Pincode", "" + etPermPincode.getText().toString());
                    hashMapCompanyDetails.put("Lattitude", "" + latitude);
                    hashMapCompanyDetails.put("Longitude", "" + longitude);
                    hashMapCompanyDetails.put("Type", "1");

                    String add_Company = Util.prepareJsonString(hashMapCompany);
                    String add_AddressDetails = Util.prepareJsonString(hashMapCompanyDetails);
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(0, new JSONObject(add_AddressDetails));

                    new addCustomerVendor(add_Company, jsonArray.toString()).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        Constant.network_error, Toast.LENGTH_SHORT).show();
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
                    etPermCity.setText("" + new JSONObject(jsonObject.optString("data")).optString("city"));
                    etPermState.setText("" + new JSONObject(jsonObject.optString("data")).optString("state"));
                    etPermCountry.setText("" + new JSONObject(jsonObject.optString("data")).optString("country"));
                } else {
                    Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public class addCustomerVendor extends AsyncTask<Void, String, String> {

        String add_Company, add_AddressDetails;

        private addCustomerVendor(String add_Company, String add_AddressDetails) {
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
                        Constant.URL1 + "addCustomerVendor");

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
                entity.addPart("CustVendorDetails", new StringBody(add_Company));
                entity.addPart("AddressDetails", new StringBody(add_AddressDetails));
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
                    write(Constant.SHRED_PR.KEY_RELOAD_CUST_VEND, "1");
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {
            switch (requestCode) {

                case (PICK_CONTACT):
                    if (resultCode == Activity.RESULT_OK) {
                        Uri contactData = data.getData();
                        Cursor c = getContentResolver().query(contactData, null, null, null, null);

                        etMobile.setText("");
                        etName.setText("");
                        etEmailId.setText("");
                        while (c.moveToNext()) {
                            String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                            retrieveContactPhoto(Long.parseLong(id));
                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                            Bitmap bitmap = Contacts.People.loadContactPhoto(CreateCustomerActivity.this, contactData, R.drawable.icon, null);
//                            imgWhite.setImageBitmap(bitmap);


                            Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);

                            while (emails.moveToNext()) {
                                String emailIdOfContact = emails.getString(emails
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                // Log.i(TAG,"...COntact Name ...."
                                // + contactName + "...contact Number..."
                                // + emailIdOfContact);
                                etEmailId.setText(emailIdOfContact);
//                                int emailType = emails.getInt(emails
//                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));


                            }
                            emails.close();

                            if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                                while (pCur.moveToNext()) {
                                    String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                                    String email = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                                    String phon_fltr = phone.replaceAll("[\\-\\+\\.\\^:,]","");

                                    etMobile.setText(phon_fltr.substring(Math.max(phon_fltr.length() - 10, 0)));
                                    etName.setText(name);
//                                    etEmailId.setText(email);
                                }
                            }
                        }
                    }
                    break;

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
                                imgLogo.setVisibility(View.VISIBLE);
                                imgLogo.setImageBitmap(photo);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                Toast.makeText(getApplicationContext(),
                                        "Sorry, Camera Crashed-Please Report as Crash A.",
                                        Toast.LENGTH_SHORT).show();
                            }

//                            camera_pathname = Constant.storageDirectory
//                                    + "/" + camera_pathname;

//                            imgLogo.setVisibility(View.VISIBLE);
//                            imageLoader.displayImage(Uri.fromFile(new File(camera_pathname)).toString(), imgLogo, options);

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

    private void retrieveContactPhoto(Long contactID) {

        Bitmap photo = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
//                ImageView imageView = (ImageView) findViewById(R.id.img_contact);
                imgLogo.setVisibility(View.VISIBLE);
                imgLogo.setImageBitmap(photo);
            } else {
                imgLogo.setVisibility(View.GONE);
            }

            assert inputStream != null;
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
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

                CropingOptionAdapter adapter = new CropingOptionAdapter(CreateCustomerActivity.this, cropOptions);

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

    public boolean isValidate() {
        if (etName.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCompanyName), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etMobile.getText().toString().trim().length() != 10) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCompanyMobile), Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (etLandline.getText().toString().trim().length() == 0) {
//            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCompanyLandline), Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (etLandline.getText().toString().trim().length() > 0) {
            String strLLN = "";
            for (int i = 0; i < etLandline.getText().toString().trim().length(); i++) {
                strLLN = strLLN + "0";
            }

            if (etLandline.getText().toString().trim().equalsIgnoreCase(strLLN) || etLandline.getText().toString().trim().length() < 10) {
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCompanyLandline), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (!getText(etEmailId).isEmpty()) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(getText(etEmailId)).matches()) {
                toast(R.string.PleaseEnterEmail);
                return false;
            }
        }

        if (etDescription.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCompanyDesc), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPermAdd1.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterAdd1), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPermCity.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCity), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPermState.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterState), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPermCountry.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCountry), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPermPincode.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterPincode), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
