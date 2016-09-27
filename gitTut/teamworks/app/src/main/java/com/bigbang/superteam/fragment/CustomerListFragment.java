package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.CustomerListAdapter;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CustomerListFragment extends Fragment {

    private static final String TAG = CustomerListFragment.class.getSimpleName();
    @InjectView(R.id.lvClients)
    ListView lvClients;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.tvFail)
    TextView tvFail;
    @InjectView(R.id.tvDesc)
    TextView tvDesc;
    @InjectView(R.id.imgEmpty)
    ImageButton imgEmpty;
    TransparentProgressDialog progressDialog;
    String CustomerVendorType = "U";
    Activity activity;
    CustomerListAdapter adapter;

    public static CustomerListFragment newInstance() {
        CustomerListFragment fragment = new CustomerListFragment();
        return fragment;
    }

    ViewGroup mContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        View view = inflater.inflate(R.layout.activity_customer_list, container, false);
        ButterKnife.inject(this, view);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        mContainer = (ViewGroup) view.getRootView();
        Util.setAppFont(mContainer, mFont);
        doSearch();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        reload();

    }

    private void init() {
        progressDialog = new TransparentProgressDialog(activity, R.drawable.progressdialog, true);

        lvClients.setDividerHeight(0);
        lvClients.setDivider(null);
    }


    private void reload() {
        if (Util.isOnline(activity)) {
            getCustomers();
        } else {
            Toast.makeText(activity, "" + Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }


    private void getCustomers() {
        if (progressDialog != null) progressDialog.show();
        String companyID = Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID);
        RestClient.getCommonService().getCustomers(companyID, read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN), new Callback<ArrayList<Customer>>() {
            @Override
            public void success(ArrayList<Customer> customers, Response response) {
                if (progressDialog != null)
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                try {
                    JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                    String status = jObj.optString("status");
                    Log.e(TAG, "response is:- " + response);
                    if (status.equals(Constant.InvalidToken)) {
                        TeamWorkApplication.LogOutClear(getActivity());
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Log.e(TAG, ">>" + Util.getString(response.getBody().in()));
                    JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                    JSONArray jsonArray = jsonObject.optJSONArray("data");

                    SQLiteHelper helper = new SQLiteHelper(activity, Constant.DatabaseName);
                    SQLiteDatabase db = helper.openDatabase();
                    db.delete(Constant.tableCustomers, null, null);

                    for (int i = 0; i < customers.size(); i++) {
                        Customer customer = customers.get(i);
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                        customer.setAddressList(jsonObject1.optString("AddressList"));
                        Gson gson = new Gson();
                        Log.e("Addresses", ">>" + customer.getAddressList() + ">>" + gson.toJson(customer.getAddressList()));

                        ContentValues values = new ContentValues();
                        values.put("ID", customer.getID());
                        values.put("Name", customer.getName());
                        values.put("MobileNo", customer.getMobileNo());
                        values.put("LandlineNo", customer.getLandlineNo());
                        values.put("EmailID", customer.getEmailID());
                        values.put("OwnerID", customer.getOwnerID());
                        values.put("CompanyTypeID", customer.getCompanyTypeID());
                        values.put("CreatedBy", customer.getCreatedBy());
                        values.put("CompanyID", customer.getCompanyID());
                        values.put("Type", customer.getType());
                        values.put("Description", customer.getDescription());
                        values.put("AddressList", "" + jsonObject1.optString("AddressList"));
                        values.put("Logo", customer.getLogo());
                        db.insert(Constant.tableCustomers, null, values);
                    }

                    adapter = new CustomerListAdapter(progressDialog, activity, CustomerVendorType, customers);

                    lvClients.setAdapter(adapter);
                    if (customers.size() == 0) {
                        tvFail.setVisibility(View.VISIBLE);
                        tvFail.setText(getResources().getString(R.string.no_client));
                        tvDesc.setVisibility(View.VISIBLE);
                        tvDesc.setText(getResources().getString(R.string.client_description));
                        imgEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvFail.setVisibility(View.GONE);
                        tvDesc.setVisibility(View.GONE);
                        imgEmpty.setVisibility(View.GONE);
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
    }


    private void doSearch() {
        final EditText et = (EditText) mContainer.findViewById(R.id.edt_search);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    protected void write(String key, String val) {
        Util.WriteSharePrefrence(activity, key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(activity, key);
    }

}
