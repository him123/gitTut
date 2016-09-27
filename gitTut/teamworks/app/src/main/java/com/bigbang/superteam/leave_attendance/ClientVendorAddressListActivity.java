package com.bigbang.superteam.leave_attendance;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.ClientVendorAddressAdapter;
import com.bigbang.superteam.dataObjs.ClientVendorAddressModel;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 7 on 8/22/2015.
 */
public class ClientVendorAddressListActivity extends Activity {


    @InjectView(R.id.listVwLocation)
    ListView lvLocation;
    @InjectView(R.id.tvNoHistory)
    TextView tvNoHistory;


    ClientVendorAddressAdapter clientVendorAddressAdapter;
    public static ArrayList<ClientVendorAddressModel> listClientVendorAddress;
    ClientVendorAddressModel clientVendorAddressModel;

    SQLiteHelper helper;
    String TAG = "ClientVendorAddressListActivity Activity ";
    public static SQLiteDatabase db = null;

    String userId;

    private TransparentProgressDialog pd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_vendor_address);
        ButterKnife.inject(this);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        pd = new TransparentProgressDialog(ClientVendorAddressListActivity.this, R.drawable.progressdialog, false);
        Intent i1 = getIntent();

        userId = i1.getStringExtra("userId");
        Init();

        if(Util.isOnline(ClientVendorAddressListActivity.this)){
            new getSavedLocation().execute();
        }else{
            Toast.makeText(ClientVendorAddressListActivity.this, ClientVendorAddressListActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

        }
    }


    private void Init() {


        listClientVendorAddress = new ArrayList<ClientVendorAddressModel>();
    }



    class getSavedLocation extends AsyncTask<Void, String, String> {


        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);

            params1.add(new BasicNameValuePair("Type", "P"));
            params1.add(new BasicNameValuePair("MemberID", ""+userId));
            params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(ClientVendorAddressListActivity.this, Constant.SHRED_PR.KEY_COMPANY_ID)));
            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL
                    + "getDefaultSettings", 1, params1, ClientVendorAddressListActivity.this);
            Log.e("response", "<<" + response);

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            /*String date = (String) spinnerYear.getSelectedItem() + "_" + (spinnerMonth.getSelectedItemPosition() + 1);
            Util.WriteFile(getCacheDir() + "/AttendanceHistory", userId + "_" + date + ".txt", result);
            reload(result);*/
            listClientVendorAddress.clear();

            try {

                tvNoHistory.setVisibility(View.GONE);
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                if (pd != null) {
                    pd.dismiss();
                }

                if (status.equals("Success")) {
                    JSONArray jArry = jObj.getJSONArray("data");
                    for (int i = 0; i < jArry.length(); i++) {
                        final JSONObject jsonObj = jArry.getJSONObject(i);
                        clientVendorAddressModel = new ClientVendorAddressModel();
                        clientVendorAddressModel.setAddresId("" + jsonObj.optInt("AddressID"));
                        clientVendorAddressModel.setClientVendorId(jsonObj.optInt("ClientVendorID"));
                        clientVendorAddressModel.setLocationType("" + jsonObj.optString("LocationType"));
                        clientVendorAddressModel.setCompanyID(""+jsonObj.optString("CompanyID"));
                        clientVendorAddressModel.setUserId(""+jsonObj.optString("UserID"));
                        clientVendorAddressModel.setClientName(""+jsonObj.optString("ClientName"));
                        clientVendorAddressModel.setAddressData(""+jsonObj.optString("ClientAddress"));

                        listClientVendorAddress.add(clientVendorAddressModel);
                        // insertUpdateTimeValue(jsonObj,AttendanceDetailsActivity.this);
                    }

                } else {
                    Toast.makeText(ClientVendorAddressListActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (pd != null) {
                    pd.dismiss();
                }
            }
            if (listClientVendorAddress.size() <= 0) {
                tvNoHistory.setVisibility(View.VISIBLE);
            } else {
                tvNoHistory.setVisibility(View.GONE);
            }
            clientVendorAddressAdapter = new ClientVendorAddressAdapter(ClientVendorAddressListActivity.this, listClientVendorAddress);
             lvLocation.setAdapter(clientVendorAddressAdapter);

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.rlBack)
    void backPressed() {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

}

