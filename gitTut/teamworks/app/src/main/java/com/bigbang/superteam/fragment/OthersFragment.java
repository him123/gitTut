package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.DisplayFullscreenImage;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.CropingOptionAdapter;
import com.bigbang.superteam.adapter.OtherMenusAdapter;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.CropingOption;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by USER 3 on 4/8/2015.
 */
public class OthersFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static Activity mContext;
    @InjectView(R.id.img_profile_big)
    ImageView imgProfileBig;
    @InjectView(R.id.img_profile_small)
    ImageView imgProfileSmall;
    @InjectView(R.id.tv_profile_name)
    TextView tvProfileName;

    @InjectView(R.id.ll_options)
    LinearLayout llOptions;
    @InjectView(R.id.rl_view_photo)
    RelativeLayout rlViewPhoto;
    @InjectView(R.id.rl_delete_photo)
    RelativeLayout rlDeletePhoto;
    @InjectView(R.id.lv_menu)
    ListView lvMenu;
    TransparentProgressDialog progressDialog;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;
    Animation animation, animation1;

    private Uri mImageCaptureUri;
    private static final int CROPING_CODE = 2;
    private static final int PICK_FROM_FILE = 3;
    String camera_pathname = "";
    String strImage = "";
    private File outPutFile = null;

    private static Map<Integer, Class> ID_CLASS = new HashMap<>();

    private static final String TAG = OthersFragment
            .class.getSimpleName();

    public static Fragment newInstance(String string, Context ctx) {
        OthersFragment f = new OthersFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, string);
        f.setArguments(bdl);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String message = getArguments().getString(EXTRA_MESSAGE);
        mContext=getActivity();
        View v;
        v = new View(mContext);
        int levelNo = Integer.parseInt(message);
        v = inflater.inflate(R.layout.others_fragment, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressDialog = new TransparentProgressDialog(mContext, R.drawable.progressdialog, false);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(R.drawable.user_icon).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.user_icon).showImageOnFail(R.drawable.user_icon).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));

        animation = AnimationUtils.loadAnimation(mContext,
                R.anim.enter_from_bottom);
        animation1 = AnimationUtils.loadAnimation(mContext,
                R.anim.enter_to_bottom);

        outPutFile = new File(Constant.storageDirectory, "temp.jpg");

        final String strName = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_FIRSTNAME) + " " + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_LASTNAME);
        tvProfileName.setText(strName);
        strImage = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_Picture);
        imageLoader.displayImage(strImage, imgProfileSmall, options);
        imageLoader.displayImage(strImage, imgProfileBig, options1);
        if (strImage.length() == 0) {
            rlDeletePhoto.setVisibility(View.GONE);
            rlViewPhoto.setVisibility(View.GONE);
        } else {
            rlDeletePhoto.setVisibility(View.VISIBLE);
            rlViewPhoto.setVisibility(View.VISIBLE);
        }


        lvMenu.setAdapter(new OtherMenusAdapter(mContext, progressDialog));
    }

    @OnClick(R.id.img_profile_big)
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
        strImage = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_Picture);
        if (strImage.length() > 0) {
            Intent myIntent = new Intent(mContext, DisplayFullscreenImage.class);
            myIntent.putExtra("URLURI", strImage);
            startActivity(myIntent);
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
        if (Util.isOnline(mContext)) {
            deletePicture();
        } else {
            Toast.makeText(mContext,
                    Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == mContext.RESULT_OK && null != data) {
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
                                Toast.makeText(mContext,
                                        "Sorry, Camera Crashed-Please Report as Crash A.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            camera_pathname = Constant.storageDirectory
                                    + "/" + camera_pathname;

                            if (Util.isOnline(mContext)) {
                                new addPicture().execute();
                            } else {
                                Toast.makeText(mContext,
                                        Constant.network_error, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(mContext, "Error while save image", Toast.LENGTH_SHORT).show();
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

        List<ResolveInfo> list = mContext.getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(mContext, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
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

                    co.title = mContext.getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = mContext.getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(mContext, cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
                            mContext.getContentResolver().delete(mImageCaptureUri, null, null);
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
                entity.addPart("UserID", new StringBody(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID)));
                entity.addPart("TokenID", new StringBody(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_TOKEN)));
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
                Toast.makeText(mContext, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                String status = jsonObject.getString("status");
                if (status.equals("Success")) {

                    strImage = "" + new JSONObject(jsonObject.getString("data")).getString("FilePath");
                    imageLoader.displayImage(strImage, imgProfileSmall, options);
                    imageLoader.displayImage(strImage, imgProfileBig, options1);
                    Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_Picture, "" + strImage);
                    if (strImage.length() == 0) {
                        rlDeletePhoto.setVisibility(View.GONE);
                        rlViewPhoto.setVisibility(View.GONE);
                    } else {
                        rlDeletePhoto.setVisibility(View.VISIBLE);
                        rlViewPhoto.setVisibility(View.VISIBLE);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deletePicture() {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().deletePicture(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID), Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID), Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(getActivity());
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            Log.e(TAG, Util.getString(response.getBody().in()));
                            Map<String, String> map = Util.readStatus(response);
                            boolean isSuccess = map.get("status").equals("Success");
                            Toast.makeText(mContext, map.get("message"), Toast.LENGTH_SHORT).show();
                            if (isSuccess) {
                                strImage = "";
                                imageLoader.displayImage(strImage, imgProfileSmall, options);
                                imageLoader.displayImage(strImage, imgProfileBig, options1);
                                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_Picture, "" + strImage);
                                if (strImage.length() == 0) {
                                    rlDeletePhoto.setVisibility(View.GONE);
                                    rlViewPhoto.setVisibility(View.GONE);
                                } else {
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


}
