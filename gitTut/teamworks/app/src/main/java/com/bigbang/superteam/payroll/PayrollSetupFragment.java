package com.bigbang.superteam.payroll;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.login_register.CompanySetupActivity;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class PayrollSetupFragment extends Fragment {

    String TAG = "PayrollSetupFragment";

    @InjectView(R.id.svMain)
    ScrollView svMain;

    @InjectView(R.id.et_basic_salary)
    EditText etBasicSalary;
    @InjectView(R.id.et_hra)
    EditText etHRA;
    @InjectView(R.id.et_conveyance)
    EditText etConveyance;
    @InjectView(R.id.et_medical)
    EditText etMedical;
    @InjectView(R.id.et_variable_amt)
    EditText etVariableAmt;
    @InjectView(R.id.et_employee_contribution)
    EditText etEmployeeContribution;
    @InjectView(R.id.et_employer_contribution)
    EditText etEmployerContribution;

    @InjectView(R.id.bt_payroll_enabled)
    ToggleButton btPayrollEnabled;
    @InjectView(R.id.bt_metrocity)
    ToggleButton btMetroCity;

    @InjectView(R.id.spnWorkingDayPolicy)
    Spinner spnWorkingDayPolicy;

    @InjectView(R.id.txt_advanceSetting)
    TextView txtAdvanceSetting;

    @InjectView(R.id.tvNext)
    TextView tvNext;
    TransparentProgressDialog progressDialog;
    Activity activity;

    static int FROM = 1;

    String basicSalary, hra, medical, conveyance, variablePayAmt, employeeCont, employerCont;
    int workingDayPolicy = 0;
    boolean metroCity = false;

    public static PayrollSetupFragment newInstance(int from) {
        FROM = from;
        PayrollSetupFragment fragment = new PayrollSetupFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_payroll_setup, container, false);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) view.getRootView();
        Util.setAppFont(mContainer, mFont);
        ButterKnife.inject(this, view);

        txtAdvanceSetting.setText(Html.fromHtml("<b> <u>Advance Settings</u> </b>"));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressDialog = new TransparentProgressDialog(activity, R.drawable.progressdialog, false);

        svMain.setVisibility(View.GONE);
        txtAdvanceSetting.setVisibility(View.GONE);

        etBasicSalary.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
        etHRA.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
        etVariableAmt.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
        etEmployeeContribution.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
        etEmployerContribution.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
        etConveyance.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});
        etMedical.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10 ,2)});

        boolean payrollEnabled = Util.ReadSharePrefrenceBoolean(activity, Constant.SHRED_PR.KEY_PAYROLL_ACTIVE);

        if (payrollEnabled) {
            svMain.setVisibility(View.VISIBLE);
            txtAdvanceSetting.setVisibility(View.VISIBLE);
            btPayrollEnabled.setChecked(true);
            setDefaultValuesFromPreferences();
        }

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FROM == Constant.FROM_DASHBOARD) {
                    if (Util.isOnline(activity)) {
                        try {
                            new getCompanyInfo().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, 100);*/
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }

    private void setDefaultValuesFromPreferences() {
        basicSalary = read(Constant.SHRED_PR.KEY_BASIC_SALARY);
        hra = read(Constant.SHRED_PR.KEY_HRA);
        medical = read(Constant.SHRED_PR.KEY_MEDICAL);
        conveyance = read(Constant.SHRED_PR.KEY_CONVEYANCE);
        variablePayAmt = read(Constant.SHRED_PR.KEY_VARIABLE_AMOUNT);
        employeeCont = read(Constant.SHRED_PR.KEY_EMPLOYEE_CONTRIBUTION);
        employerCont = read(Constant.SHRED_PR.KEY_EMPLOYER_CONTRIBUTION);
        workingDayPolicy = Util.ReadSharePrefrenceInteger(activity, Constant.SHRED_PR.KEY_WORKING_DAYS_POLICY);
        metroCity = Util.ReadSharePrefrenceBoolean(activity,Constant.SHRED_PR.KEY_METRO_CITY);

        etBasicSalary.setText(basicSalary + "");
        etHRA.setText(hra + "");
        etMedical.setText(medical + "");
        etConveyance.setText(conveyance + "");
        etVariableAmt.setText(variablePayAmt + "");
        etEmployeeContribution.setText(employeeCont + "");
        etEmployerContribution.setText(employerCont + "");
        if (workingDayPolicy > 0)
            spnWorkingDayPolicy.setSelection(workingDayPolicy - 1);
        btMetroCity.setChecked(metroCity);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (btPayrollEnabled.isChecked()) {
            svMain.setVisibility(View.VISIBLE);
            txtAdvanceSetting.setVisibility(View.VISIBLE);
        } else {
            svMain.setVisibility(View.GONE);
            txtAdvanceSetting.setVisibility(View.GONE);
        }
    }

    private void setDefaultData() {

        if (progressDialog != null)
            progressDialog.show();

        setBlank();

        RestClient.getTeamWork().getCompanyPayrolls(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {

                    Map<String, String> map = Util.readStatus(response);
                    boolean isSuccess = map.get("status").equals("Success");

                    String json = Util.getString(response.getBody().in());
                    JSONObject jObj = new JSONObject(json);

                    String status = jObj.optString("status");
                    if (status.equals(Constant.InvalidToken)) {
                        TeamWorkApplication.LogOutClear(getActivity());
                        return;
                    }

                    if(isSuccess) {

                        JSONObject dataJobj = jObj.getJSONObject("data");

                        //set data in prefernces & set in INPUT BOX
                        etBasicSalary.setText(dataJobj.getString("Basic_Salary"));
                        etHRA.setText(dataJobj.getString("HRA"));
                        etConveyance.setText(dataJobj.getString("Conveyance"));
                        etMedical.setText(dataJobj.getString("Medical_Allowance"));
                        etVariableAmt.setText(dataJobj.getString("Variable_Amount"));
                        etEmployeeContribution.setText(dataJobj.getString("Employee_Contribution"));
                        etEmployerContribution.setText(dataJobj.getString("Employer_Contribution"));
                        btMetroCity.setChecked(dataJobj.getBoolean("Metro_City"));

                    }else {
                        Toast.makeText(getActivity(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (Exception e) {
                    Log.d("", "Exception : " + e);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.bt_payroll_enabled)
    @SuppressWarnings("unused")
    public void Payroll(View view) {
        if (btPayrollEnabled.isChecked()) {
            svMain.setVisibility(View.VISIBLE);
            txtAdvanceSetting.setVisibility(View.VISIBLE);
            setDefaultData();
        } else {
            svMain.setVisibility(View.GONE);
            txtAdvanceSetting.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.txt_advanceSetting)
    @SuppressWarnings("unused")
    public void AdvanceSettings(View view) {
        Intent intent = new Intent(activity, PayrollSetupAdvanceActivity.class);
        intent.putExtra("from", "" + Constant.FROM_DASHBOARD);
        startActivity(intent);
        activity.overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rl_next)
    @SuppressWarnings("unused")
    public void Next(View view) {

        if (Util.isOnline(getActivity())) {

            setBlank();
            if (isValidate()) {
                if (progressDialog != null)
                    progressDialog.show();

                //setBlank();

                JSONObject jsonObject = new JSONObject();
                try {
                    if (btPayrollEnabled.isChecked() == true) {
                        jsonObject.put("CompanyID", Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID));
                        jsonObject.put("PayrollEnabled", true);
                        jsonObject.put("CompanyWorkingDaysID", spnWorkingDayPolicy.getSelectedItemPosition() + 1);
                        jsonObject.put("BasicSalary", getTextInt(etBasicSalary));
                        jsonObject.put("HRA", getTextInt(etHRA));
                        jsonObject.put("EmployeeContribution", getTextInt(etEmployeeContribution));
                        jsonObject.put("EmployerContribution", getTextInt(etEmployerContribution));
                        jsonObject.put("MedicalAllowances", getTextInt(etMedical));
                        jsonObject.put("Conveyance", getTextInt(etConveyance));
                        jsonObject.put("VariablePayAmount", getTextInt(etVariableAmt));
                        jsonObject.put("CreatedBy", Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID));
                        jsonObject.put("metroCityHRA", btMetroCity.isChecked());
                    } else {
                        jsonObject.put("CompanyID", Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID));
                        jsonObject.put("PayrollEnabled", "false");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("Payroll", "******** Passsing Json : " + jsonObject.toString());

                RestClient.getTeamWork().addUpdateCompanyPayroll(jsonObject.toString(),
                        Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID),
                        Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_TOKEN), new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                try {
                                    Map<String, String> map = Util.readStatus(response);
                                    boolean isSuccess = map.get("status").equals("Success");
                                    String json = Util.getString(response.getBody().in());
                                    JSONObject jObj = new JSONObject(json);

                                    String status = jObj.optString("status");
                                    if (status.equals(Constant.InvalidToken)) {
                                        TeamWorkApplication.LogOutClear(getActivity());
                                        return;
                                    }

                                    if (isSuccess) {
                                        Toast.makeText(getActivity(), jObj.getString("message"), Toast.LENGTH_SHORT).show();

                                        //value save in preferences
                                        JSONObject dataObj = jObj.getJSONObject("data");

                                        Util.WriteSharePrefrenceForBoolean(activity, Constant.SHRED_PR.KEY_PAYROLL_ACTIVE, btPayrollEnabled.isChecked());
                                        write(Constant.SHRED_PR.KEY_BASIC_SALARY, dataObj.getString("BasicSalary"));
                                        write(Constant.SHRED_PR.KEY_HRA, dataObj.getString("HRA"));
                                        write(Constant.SHRED_PR.KEY_CONVEYANCE, dataObj.getString("Conveyance"));
                                        write(Constant.SHRED_PR.KEY_MEDICAL, dataObj.getString("MedicalAllowances"));
                                        write(Constant.SHRED_PR.KEY_VARIABLE_AMOUNT, dataObj.getString("VariablePayAmount"));
                                        Util.WriteSharePrefrenceForBoolean(activity, Constant.SHRED_PR.KEY_METRO_CITY, dataObj.getBoolean("metroCityHRA"));
                                        write(Constant.SHRED_PR.KEY_EMPLOYEE_CONTRIBUTION, dataObj.getString("EmployeeContribution"));
                                        write(Constant.SHRED_PR.KEY_EMPLOYER_CONTRIBUTION, dataObj.getString("EmployerContribution"));
                                        Util.WriteSharePrefrenceForInteger(activity, Constant.SHRED_PR.KEY_WORKING_DAYS_POLICY, dataObj.getInt("CompanyWorkingDaysID"));

                                        /*if(btPayrollEnabled.isChecked())
                                            txtAdvanceSetting.setVisibility(View.VISIBLE);*/

                                        if (FROM == Constant.FROM_DASHBOARD) {
                                            activity.finish();
                                            activity.overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                                        } else {
                                            if (btPayrollEnabled.isChecked()) {
                                                CompanySetupActivity.currentPosition++;
                                                CompanySetupActivity.pager.setCurrentItem(CompanySetupActivity.currentPosition);
                                            } else {
                                                CompanySetupActivity.currentPosition = CompanySetupActivity.currentPosition + 2;
                                                CompanySetupActivity.pager.setCurrentItem(CompanySetupActivity.currentPosition);
                                            }
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Log.d("", "Exception: " + e);
                                    progressDialog.dismiss();
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                progressDialog.dismiss();
                            }
                        });
            }
        } else {
            Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setBlank(){
        if (etBasicSalary.getText().toString().trim().equals("") || etBasicSalary.getText().toString().trim().equals(".")) {
            etBasicSalary.setText("0.0");
        }

        if (etHRA.getText().toString().trim().equals("") || etHRA.getText().toString().trim().equals(".")) {
            etHRA.setText("0.0");
        }

        if (etConveyance.getText().toString().trim().equals("") || etConveyance.getText().toString().trim().equals(".")) {
            etConveyance.setText("0.0");
        }

        if (etMedical.getText().toString().trim().equals("") || etMedical.getText().toString().trim().equals(".")) {
            etMedical.setText("0.0");
        }

        if (etVariableAmt.getText().toString().trim().equals("") || etVariableAmt.getText().toString().trim().equals(".")) {
            etVariableAmt.setText("0.0");
        }

        if (etEmployeeContribution.getText().toString().trim().equals("") || etEmployeeContribution.getText().toString().trim().equals(".")) {
            etEmployeeContribution.setText("0.0");
        }

        if (etEmployerContribution.getText().toString().trim().equals("") || etEmployerContribution.getText().toString().trim().equals(".")) {
            etEmployerContribution.setText("0.0");
        }
    }

    public class getCompanyInfo extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //if (progressDialog != null) progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resp = "";

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            boolean companyID = params1.add(new BasicNameValuePair("CompanyID", "" + read(Constant.SHRED_PR.KEY_COMPANY_ID)));
            params1.add(new BasicNameValuePair("RoleID", "" + read(Constant.SHRED_PR.KEY_ROLE_ID)));
            params1.add(new BasicNameValuePair("MemberID", "" + read(Constant.SHRED_PR.KEY_USERID)));

            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + "getCompanyInfo", 1, params1, activity);

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("Response", ">>" + result);
            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.optString("status");
                if (status.equals("Success")) {
                    write(Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA, jsonObject.optString("companydata"));
                    //setupDefault();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isValidate() {

        HashMap<String, String> hashMap = new HashMap<String, String>();
        if (btPayrollEnabled.isChecked() == false) {
            hashMap.put("isPayrollEnabled", "false");
        } else {
            hashMap.put("isPayrollEnabled", "true");
            hashMap.put("salaryBreakupType", "breakup");

            if (isEmpty(getText(etBasicSalary))) {
                toast(getResources().getString(R.string.enter_basicsalary));
                return false;
            }

            /*if(Double.parseDouble(getText(etBasicSalary)) <= 0){
                toast("Basic salary must be greater than 0");
                return false;
            }*/

            if(Double.parseDouble(getText(etBasicSalary)) > 100){
                toast("Basic salary % must be less than 100");
                return false;
            }else hashMap.put("basicSalary", getText(etBasicSalary));

            /*if (isEmpty(getText(etHRA))) {
                toast(getResources().getString(R.string.enter_hra));
                return false;
            } */

            if(Double.parseDouble(getText(etHRA)) > 100){
                toast("HRA % must be less than 100");
                return false;
            }else hashMap.put("hra", getText(etHRA));

            /*if (isEmpty(getText(etConveyance))) {
                toast(getResources().getString(R.string.enter_conveyance));
                return false;
            } else hashMap.put("conveyance", getText(etConveyance));*/

            if(!isEmpty(getText(etConveyance)))
                hashMap.put("conveyance", getText(etConveyance));

            /*if (isEmpty(getText(etMedical))) {
                toast(getResources().getString(R.string.enter_medical));
                return false;
            } else hashMap.put("medical", getText(etMedical));*/

            if(!isEmpty(getText(etMedical)))
                hashMap.put("medical", getText(etMedical));


            /*if (isEmpty(getText(etVariableAmt))) {
                toast(getResources().getString(R.string.enter_variable_amt));
                return false;
            } */

            if(Double.parseDouble(getText(etVariableAmt)) > 100){
                toast("Variable amount % must be less than 100");
                return false;
            }else hashMap.put("variable_amt", getText(etVariableAmt));


            if (isEmpty(getText(etEmployeeContribution))) {
                toast(getResources().getString(R.string.enter_employee_contribution));
                return false;
            }

            if(Double.parseDouble(getText(etEmployeeContribution)) > 100){
                toast("Employee contribution % must be less than 100");
                return false;
            }else hashMap.put("pfEmployee", getText(etEmployeeContribution));


            if (isEmpty(getText(etEmployerContribution))) {
                toast(getResources().getString(R.string.enter_employer_contribution));
                return false;
            }

            if(Double.parseDouble(getText(etEmployerContribution)) > 100){
                toast("Employer contribution % must be less than 100");
                return false;
            }else hashMap.put("pfEmployer", getText(etEmployerContribution));
        }

        hashMap.put("companyid", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID));

        write(Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA, Util.addJsonKey(read(Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA), hashMap));
        Log.e(TAG, read(Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA));

        return true;
    }

    protected boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    protected String getText(EditText eTxt) {
        return eTxt == null ? "" : eTxt.getText().toString().trim();
    }

    protected Double getTextInt(EditText eTxt) {
        if (eTxt.getText() != null && eTxt.getText().length() > 0) {
            return Double.parseDouble(eTxt.getText().toString().trim());
        } else {
            return 0.0;
        }
    }

    protected void toast(int resId) {
        toast(activity.getResources().getText(resId));
    }

    protected void toast(CharSequence text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(activity, key);
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(activity, key, val);
    }

}
