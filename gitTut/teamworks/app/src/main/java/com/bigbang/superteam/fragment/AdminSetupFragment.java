package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AdminSetupFragment extends Fragment {

    @InjectView(R.id.rl_next)
    RelativeLayout rlNext;
    @InjectView(R.id.et_fname)
    EditText etFirstName;
    @InjectView(R.id.et_lname)
    EditText etLastName;
    @InjectView(R.id.et_mobile)
    EditText etMobile;
    @InjectView(R.id.et_email)
    EditText etEmail;
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

    Activity activity;

    // TODO: Rename and change types and number of parameters
    public static AdminSetupFragment newInstance() {
        AdminSetupFragment fragment = new AdminSetupFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity=getActivity();
        View view = inflater.inflate(R.layout.fragment_admin_setup, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    private void init() {

        progressDialog = new TransparentProgressDialog(activity, R.drawable.progressdialog,false);

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

        rlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValidate()) {
                    if (Util.isOnline(activity)) {
                        if (Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_GCM_ID).toString().length() == 0)
                            Util.GCM(activity);
                        new createUser().execute();
                    } else {
                        Toast.makeText(activity,
                                Constant.network_error, Toast.LENGTH_SHORT).show();
                    }

                }

//                RegisterActivity.currentPosition++;
//                RegisterActivity.pager.setCurrentItem(RegisterActivity.currentPosition);
            }
        });
    }

    class createUser extends AsyncTask<Void, String, String> {

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

            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("Application", "TeamWorks"));
            params1.add(new BasicNameValuePair("Email", "" + etEmail.getText().toString().trim()));
            params1.add(new BasicNameValuePair("Fname", "" + etFirstName.getText().toString().trim()));
            params1.add(new BasicNameValuePair("Lname", "" + etLastName.getText().toString().trim()));
            params1.add(new BasicNameValuePair("Mob1", "" + etMobile.getText().toString().trim()));
            params1.add(new BasicNameValuePair("PermAddCountry", "" + etCountry.getText().toString().trim()));
            params1.add(new BasicNameValuePair("PermAddState", "" + etState.getText().toString().trim()));
            params1.add(new BasicNameValuePair("PermAddCity", "" + etCity.getText().toString().trim()));
            params1.add(new BasicNameValuePair("PermAddLine1", "" + etPermAdd1.getText().toString().trim()));
            params1.add(new BasicNameValuePair("PermAddLine2", "" + etPermAdd2.getText().toString().trim()));
            params1.add(new BasicNameValuePair("PermAddPincode", "" + etPincode.getText().toString().trim()));
            params1.add(new BasicNameValuePair("TempAdd", "0"));
            params1.add(new BasicNameValuePair("DeviceID", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_GCM_ID)));

            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL1 + "createUser", 2, params1, activity);

            return response;
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

                Toast.makeText(activity, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                String status = jsonObject.getString("status");
                if (status.equals("Success")) {
                    JSONObject jData = jsonObject.getJSONObject("data");

                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID, "" + jData.getString("userID"));
                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_FIRSTNAME, "" + jData.getString("firstName"));
                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_LASTNAME, "" + jData.getString("lastName"));
                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_TELEPHONE, "" + jData.getString("mobileNo1"));
                    Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_EMAIL, "" + jData.getString("emailID"));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public boolean isValidate() {
        if (etFirstName.getText().toString().trim().length() == 0) {
            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterFirstname), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etLastName.getText().toString().trim().length() == 0) {
            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterLastname), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etMobile.getText().toString().trim().length() != 10) {
            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterMobile), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            Toast.makeText(activity, activity.getResources().getString(R.string.PleaseEnterEmail), Toast.LENGTH_SHORT).show();
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

        return true;
    }
}
