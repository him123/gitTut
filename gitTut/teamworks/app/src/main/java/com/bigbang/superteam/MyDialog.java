package com.bigbang.superteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER 7 on 5/30/2015.
 */
public class MyDialog extends Activity {

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;
    String time, date;
    double curLat, curLng;

    private TransparentProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        pd = new TransparentProgressDialog(MyDialog.this, R.drawable.progressdialog, false);
        GPSTracker gps = new GPSTracker(MyDialog.this);
        curLat = gps.getLatitude();
        curLng = gps.getLongitude();

        alertDialog.setTitle("" + Constant.AppNameSuper);
        alertDialog.setMessage("Your company time is over. Do you want to Check out?");
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @SuppressLint("InlinedApi")
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();
                        android.os.Handler hn = new android.os.Handler();
                        hn.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (Util.isOnline(getApplicationContext())) {
                                    new checkOutAsync().execute();
                                }
                            }
                        }, 500);
                    }
                });
        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();
                        android.os.Handler hn = new android.os.Handler();
                        hn.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 500);
                    }
                });
        alertDialog.create();
        alertDialog.show();
    }

    class checkOutAsync extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pBar.setVisibility(View.VISIBLE);
           if(pd!=null)
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {


            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(MyDialog.this, Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(MyDialog.this, Constant.SHRED_PR.KEY_COMPANY_ID)));
            params1.add(new BasicNameValuePair("Latitude", "" + curLat));
            params1.add(new BasicNameValuePair("Longitude", "" + curLng));
            //params1.add(new BasicNameValuePair("Type", "Office"));
            params1.add(new BasicNameValuePair("ClientVendorID", "0"));
            params1.add(new BasicNameValuePair("AddressMasterID", "0"));


            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + "checkOut", 2, params1, getApplicationContext());

            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if(pd!=null)
                pd.dismiss();
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                //pBar.setVisibility(View.GONE);
                Toast.makeText(MyDialog.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
                //pBar.setVisibility(View.GONE);
            }

            finish();
        }
    }
}