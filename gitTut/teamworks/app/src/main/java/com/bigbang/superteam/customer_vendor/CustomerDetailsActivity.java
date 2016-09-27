package com.bigbang.superteam.customer_vendor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.ImageButton;
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
import com.bigbang.superteam.model.Address;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.CropingOption;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.google.gson.Gson;
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

public class CustomerDetailsActivity extends BaseActivity {

    private static final String TAG = CustomerDetailsActivity
            .class.getSimpleName();

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
    @InjectView(R.id.lvAddress)
    ListView lvAddress;
    @InjectView(R.id.tvAddress)
    TextView tvAddress;
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
    @InjectView(R.id.spinnerType)
    Spinner spinnerType;
    @InjectView(R.id.icon_phnbook)
    ImageButton icon_phnbook;
    TransparentProgressDialog progressDialog;

    String CustomerVendorType = "U";
    Customer customer;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;
    Animation animation, animation1;

    private Uri mImageCaptureUri;
    private static final int CROPING_CODE = 2;
    private static final int PICK_FROM_FILE = 3;
    String camera_pathname = "";
    String strImage = "";
    private File outPutFile = null;

    int roleID;
    String CreateType = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            CustomerVendorType = extras.getString("Type");
        }

        init();
        outPutFile = new File(Constant.storageDirectory, "temp.jpg");

        roleID = Integer.parseInt(read(Constant.SHRED_PR.KEY_ROLE_ID));

        if (CustomerVendorType.equals("U")) {
            tvTitle.setText(getResources().getString(R.string.update_client));
            tvAddress.setText(getResources().getString(R.string.client_addresses));
        } else {
            tvTitle.setText(getResources().getString(R.string.update_vendor));
            tvAddress.setText(getResources().getString(R.string.vendor_addresses));
        }

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

        setData();
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

    @Override
    protected void onResume() {
        super.onResume();

        if (read(Constant.SHRED_PR.KEY_RELOAD).equals("1")) {
            write(Constant.SHRED_PR.KEY_RELOAD, "0");

            Gson gson = new Gson();
            Address address = gson.fromJson(read(Constant.SHRED_PR.KEY_TEMP_ADDRESS), Address.class);
            if (address != null) {
                Log.e("Addresses", ">>" + customer.getAddressList());
                ArrayList<Address> addresses = customer.getAddressList();

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

                customer.setAddressList(addresses);
                lvAddress.setAdapter(new AddressAdapter(CustomerDetailsActivity.this, addresses, CreateType));
                lvAddress.setSelection(addresses.size() - 1);
                Util.setListViewHeightBasedOnChildren(lvAddress);
            }

        }
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

    @OnClick(R.id.rl_add)
    @SuppressWarnings("unused")
    public void addAddress(View view) {
        Intent intent1 = new Intent(CustomerDetailsActivity.this, AddressActivity.class);
        intent1.putExtra("Create", "1");
        startActivity(intent1);
        overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
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
            strImage = "" + customer.getLogo();
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
                    hashMapCompany.put("ID", "" + customer.getID());

                    String add_Company = Util.prepareJsonString(hashMapCompany);

                    Gson gson = new Gson();
                    new updateCustomerVendor(add_Company, gson.toJson(customer.getAddressList())).execute();
//                    Toast.makeText(CustomerDetailsActivity.this, "Entered", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        Constant.network_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {

        progressDialog = new TransparentProgressDialog(CustomerDetailsActivity.this, R.drawable.progressdialog, false);

        lvAddress.setDivider(null);
        lvAddress.setDividerHeight(0);
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

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

    }

    private void setData() {

        Bundle data = getIntent().getExtras();
        customer = (Customer) data.getSerializable("customer");

        tvName.setText("" + customer.getName().toUpperCase());
        etName.setText("" + customer.getName());
        etMobile.setText("" + customer.getMobileNo());
        etLandline.setText("" + customer.getLandlineNo());
        etEmailId.setText("" + customer.getEmailID());
        etDescription.setText("" + customer.getDescription());

        spinnerType.setFocusableInTouchMode(false);
        spinnerType.setFocusable(false);
        spinnerType.setEnabled(false);
        spinnerType.setSelection(Integer.parseInt("" + customer.getCompanyTypeID()) - 1);

        strImage = "" + customer.getLogo();
        Log.e("strImage", ">>" + strImage);
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


        ArrayList<Address> addresses = customer.getAddressList();
        Log.e("Addresses", ">>" + customer.getAddressList());
        lvAddress.setAdapter(new AddressAdapter(CustomerDetailsActivity.this, addresses, CreateType));
//        lvAddress.setSelection(addresses.size() - 1);
        Util.setListViewHeightBasedOnChildren(lvAddress);

    }

    public class updateCustomerVendor extends AsyncTask<Void, String, String> {

        String add_Company, add_AddressDetails;

        private updateCustomerVendor(String add_Company, String add_AddressDetails) {
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
                        Constant.URL1 + "updateCustomerVendor");

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
                Log.e("UserID", Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID));
                Log.e("TokenID", Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_TOKEN));

                entity.addPart("CustomerDetails", new StringBody(add_Company));
                entity.addPart("AddressDetails", new StringBody(add_AddressDetails));
                entity.addPart("UserID", new StringBody(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID)));
                entity.addPart("TokenID", new StringBody(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_TOKEN)));
                entity.addPart("Application", new StringBody(Constant.AppName));
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
                    TeamWorkApplication.LogOutClear(CustomerDetailsActivity.this);
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
                        while (c.moveToNext()) {
                            String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                                while (pCur.moveToNext()) {
                                    String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                    etMobile.setText(phone.trim().substring(3));
                                    etName.setText(name);
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
                                        Constant.network_error, Toast.LENGTH_LONG).show();
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

                CropingOptionAdapter adapter = new CropingOptionAdapter(CustomerDetailsActivity.this, cropOptions);

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
        RestClient.getCommonService3().deleteLogo(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID), "" + customer.getID(), read(Constant.SHRED_PR.KEY_TOKEN), CustomerVendorType,
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(CustomerDetailsActivity.this);
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
                                write(Constant.SHRED_PR.KEY_RELOAD_CUST_VEND, "1");
                                strImage = "";
                                imageLoader.displayImage(strImage, imgProfileSmall, options);
                                customer.setLogo(strImage);
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
                entity.addPart("Type", new StringBody(CustomerVendorType));
                entity.addPart("CompanyID", new StringBody("" + customer.getID()));
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
                    TeamWorkApplication.LogOutClear(CustomerDetailsActivity.this);
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
                    write(Constant.SHRED_PR.KEY_RELOAD_CUST_VEND, "1");
                    strImage = "" + new JSONObject(jsonObject.getString("data")).getString("FilePath");
                    imageLoader.displayImage(strImage, imgProfileSmall, options);
                    customer.setLogo(strImage);
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

    public boolean isValidate() {
        if (etName.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCompanyName), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etMobile.getText().toString().trim().length() > 0 &&
                etMobile.getText().toString().trim().length() != 10) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCompanyMobile), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etLandline.getText().toString().trim().length() > 0 &&
                etLandline.getText().toString().trim().length() != 10
                && etLandline.getText().toString().trim().length() != 11) {

            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCompanyLandline), Toast.LENGTH_SHORT).show();
            return false;
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
        return true;
    }
}
