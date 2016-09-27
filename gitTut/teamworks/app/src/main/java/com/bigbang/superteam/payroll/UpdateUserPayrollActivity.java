package com.bigbang.superteam.payroll;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONObject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UpdateUserPayrollActivity extends BaseActivity {

    @InjectView(R.id.tvUserName)
    TextView tvUserName;

    @InjectView(R.id.edEmployeeCode)
    EditText edEmployeeCode;
    @InjectView(R.id.edDesignation)
    EditText edDesignation;
    @InjectView(R.id.edPANNo)
    EditText edPANNo;
    @InjectView(R.id.edPFAccountNo)
    EditText edPFAccountNo;
    @InjectView(R.id.edBankName)
    EditText edBankName;
    @InjectView(R.id.edBankActNumber)
    EditText edBankActNumber;

    @InjectView(R.id.edCTC)
    EditText edCTC;
    @InjectView(R.id.edBasicSalary)
    EditText edBasicSalary;
    @InjectView(R.id.edHRA)
    EditText edHRA;
    @InjectView(R.id.edMedicalAllow)
    EditText edMedicalAllow;
    @InjectView(R.id.edConveyance)
    EditText edConveyance;
    @InjectView(R.id.edVariablePayAmt)
    EditText edVariablePayAmt;

    @InjectView(R.id.edEmployeeCon)
    EditText edEmployeeCon;
    @InjectView(R.id.edEmployerCon)
    EditText edEmployerCon;

    @InjectView(R.id.edActualRentPaid)
    EditText edActualRentPaid;
    @InjectView(R.id.edInvestment80C)
    EditText edInvestment80C;
    @InjectView(R.id.edInvestment80D)
    EditText edInvestment80D;

    String basicSalary, hra, medical, conveyance, variablePayAmt, employeeCont, employerCont;

    private TransparentProgressDialog progressDialog;
    Intent i;

    private String userID;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_payroll);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        ButterKnife.inject(this);

        progressDialog = new TransparentProgressDialog(UpdateUserPayrollActivity.this, R.drawable.progressdialog, false);
        i = getIntent();
        Bundle b = getIntent().getExtras();

        userID = b.getString("userID");
        user = (User)b.getSerializable("user");

        edCTC.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});
        edBasicSalary.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
        edHRA.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
        edMedicalAllow.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});
        edConveyance.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});
        edVariablePayAmt.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
        edEmployeeCon.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
        edEmployerCon.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
        edActualRentPaid.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});
        edInvestment80C.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});
        edInvestment80D.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});

        tvUserName.setText(user.getFirstName() + " " + user.getLastName());

        init();
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


    private void init() {

        if (progressDialog != null)
            progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("UserID",userID);
            jsonObject.put("CompanyID", Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ID));
        }catch(Exception e){
            e.printStackTrace();
        }

        RestClient.getTeamWork().getUserPayrollDetails(jsonObject.toString(),
                new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {

                    Map<String, String> map = Util.readStatus(response);
                    boolean isSuccess = map.get("status").equals("Success");

                    String json = Util.getString(response.getBody().in());
                    JSONObject jObj = new JSONObject(json);

                    String status = jObj.optString("status");
                    if (status.equals(Constant.InvalidToken)) {
                        TeamWorkApplication.LogOutClear(UpdateUserPayrollActivity.this);
                        return;
                    }

                    if(isSuccess) {
                        JSONObject dataJobj = jObj.getJSONObject("data");

                        if (dataJobj != null) {
                            //set value in INPUT BOX
                            edEmployeeCode.setText(dataJobj.getString("EmployeeCode"));
                            edDesignation.setText(dataJobj.getString("Designation"));
                            edPANNo.setText(dataJobj.getString("PANNumber"));
                            edPFAccountNo.setText(dataJobj.getString("PFAccountNumber"));
                            edBankName.setText(dataJobj.getString("BankName"));
                            edBankActNumber.setText(dataJobj.getString("BankAccountNumber"));

                            edCTC.setText(dataJobj.getString("CTC"));
                            edBasicSalary.setText(dataJobj.getString("BasicSalary"));
                            edHRA.setText(dataJobj.getString("HRA"));
                            edConveyance.setText(dataJobj.getString("Conveyance"));
                            edMedicalAllow.setText(dataJobj.getString("MedicalAllowances"));
                            edVariablePayAmt.setText(dataJobj.getString("VariablePayAmount"));

                            edEmployeeCon.setText(dataJobj.getString("EmployeeContribution"));
                            edEmployerCon.setText(dataJobj.getString("EmployerContribution"));

                            edActualRentPaid.setText(dataJobj.getString("ActualRentPaid"));
                            edInvestment80C.setText(dataJobj.getString("InvestmentUnder80C"));
                            edInvestment80D.setText(dataJobj.getString("InvestmentUnder80D"));
                        }
                    }else {
                        boolean isNoData = map.get("status").equals("No Data Found");
                        if(isNoData)
                            setDefaultValuesFromPreferences();
                        else
                            Toast.makeText(UpdateUserPayrollActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
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

    private void setDefaultValuesFromPreferences() {
        basicSalary = read(Constant.SHRED_PR.KEY_BASIC_SALARY);
        hra = read(Constant.SHRED_PR.KEY_HRA);
        medical = read(Constant.SHRED_PR.KEY_MEDICAL);
        conveyance = read(Constant.SHRED_PR.KEY_CONVEYANCE);
        variablePayAmt = read(Constant.SHRED_PR.KEY_VARIABLE_AMOUNT);
        employeeCont = read(Constant.SHRED_PR.KEY_EMPLOYEE_CONTRIBUTION);
        employerCont = read(Constant.SHRED_PR.KEY_EMPLOYER_CONTRIBUTION);

        edBasicSalary.setText(basicSalary + "");
        edHRA.setText(hra + "");
        edMedicalAllow.setText(medical + "");
        edConveyance.setText(conveyance + "");
        edVariablePayAmt.setText(variablePayAmt + "");
        edEmployeeCon.setText(employeeCont + "");
        edEmployerCon.setText(employerCont + "");
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(this, key);
    }

    @OnClick(R.id.rl_next)
    @SuppressWarnings("unused")
    public void Next(View view) {

        if (Util.isOnline(this)) {

            setBlank();
            if (isValidate()) {
                if (progressDialog != null)
                    progressDialog.show();

                //setBlank();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("UserID",userID);
                    jsonObject.put("ActualRentPaid", getTextInt(edActualRentPaid));
                    jsonObject.put("InvestmentUnder80C", getTextInt(edInvestment80C));
                    jsonObject.put("InvestmentUnder80D", getTextInt(edInvestment80D));
                    jsonObject.put("CompanyID", Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ID));
                    jsonObject.put("EmployeeCode", getText(edEmployeeCode));
                    jsonObject.put("PFAccountNumber", getText(edPFAccountNo));
                    jsonObject.put("BankName", getText(edBankName));
                    jsonObject.put("PANNumber", getText(edPANNo));
                    jsonObject.put("Designation", getText(edDesignation));
                    jsonObject.put("BankAccountNumber", getText(edBankActNumber));
                    jsonObject.put("CTC", getTextInt(edCTC));
                    jsonObject.put("BasicSalary", getTextInt(edBasicSalary));
                    jsonObject.put("HRA", getTextInt(edHRA));
                    jsonObject.put("EmployeeContribution", getTextInt(edEmployeeCon));
                    jsonObject.put("EmployerContribution", getTextInt(edEmployerCon));
                    jsonObject.put("MedicalAllowances", getTextInt(edMedicalAllow));
                    jsonObject.put("Conveyance", getTextInt(edConveyance));
                    jsonObject.put("VariablePayAmount", getTextInt(edVariablePayAmt));
                    jsonObject.put("CreatedBy", Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERID));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("Payroll", "******** Passsing Json : " + jsonObject.toString());

                RestClient.getTeamWork().addUpdateUserPayroll(jsonObject.toString(),
                        Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERID),
                        Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_TOKEN), new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                try {
                                    Map<String, String> map = Util.readStatus(response);
                                    boolean isSuccess = map.get("status").equals("Success");
                                    String json = Util.getString(response.getBody().in());
                                    JSONObject jObj = new JSONObject(json);

                                    String status = jObj.optString("status");
                                    if (status.equals(Constant.InvalidToken)) {
                                        TeamWorkApplication.LogOutClear(UpdateUserPayrollActivity.this);
                                        return;
                                    }

                                    if (isSuccess) {
                                        Toast.makeText(UpdateUserPayrollActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();

                                        UpdateUserPayrollActivity.this.finish();
                                        UpdateUserPayrollActivity.this.overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);

                                    } else {
                                        Toast.makeText(UpdateUserPayrollActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(UpdateUserPayrollActivity.this, Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setBlank(){
        if (edCTC.getText().toString().trim().equals("") || edCTC.getText().toString().trim().equals(".")) {
            edCTC.setText("0.0");
        }

        if (edBasicSalary.getText().toString().trim().equals("") || edBasicSalary.getText().toString().trim().equals(".")) {
            edBasicSalary.setText("0.0");
        }

        if (edHRA.getText().toString().trim().equals("") || edHRA.getText().toString().trim().equals(".")) {
            edHRA.setText("0.0");
        }

        if (edMedicalAllow.getText().toString().trim().equals("") || edMedicalAllow.getText().toString().trim().equals(".")) {
            edMedicalAllow.setText("0.0");
        }

        if (edConveyance.getText().toString().trim().equals("") || edConveyance.getText().toString().trim().equals(".")) {
            edConveyance.setText("0.0");
        }

        if (edVariablePayAmt.getText().toString().trim().equals("") || edVariablePayAmt.getText().toString().trim().equals(".")) {
            edVariablePayAmt.setText("0.0");
        }

        if (edEmployeeCon.getText().toString().trim().equals("") || edEmployeeCon.getText().toString().trim().equals(".")) {
            edEmployeeCon.setText("0.0");
        }

        if (edEmployerCon.getText().toString().trim().equals("") || edEmployerCon.getText().toString().trim().equals(".")) {
            edEmployerCon.setText("0.0");
        }

        if (edActualRentPaid.getText().toString().trim().equals("") || edActualRentPaid.getText().toString().trim().equals(".")) {
            edActualRentPaid.setText("0.0");
        }

        if (edInvestment80C.getText().toString().trim().equals("") || edInvestment80C.getText().toString().trim().equals(".")) {
            edInvestment80C.setText("0.0");
        }

        if (edInvestment80D.getText().toString().trim().equals("") || edInvestment80D.getText().toString().trim().equals(".")) {
            edInvestment80D.setText("0.0");
        }
    }

    private boolean isValidate() {

        if (isEmpty(getText(edCTC))) {
            toast(getResources().getString(R.string.enter_CTC));
            return false;
        }

        if (isEmpty(getText(edBasicSalary))) {
            toast(getResources().getString(R.string.enter_basicsalary));
            return false;
        }

        if(Double.parseDouble(getText(edBasicSalary)) > 100){
            toast("Basic salary % must be less than 100");
            return false;
        }

        /*if (isEmpty(getText(edHRA))) {
            toast(getResources().getString(R.string.enter_hra));
            return false;
        }*/

        if(Double.parseDouble(getText(edHRA)) > 100){
            toast("HRA % must be less than 100");
            return false;
        }

        /*if (isEmpty(getText(edConveyance))) {
            toast(getResources().getString(R.string.enter_conveyance));
            return false;
        }*/

        /*if (isEmpty(getText(edMedicalAllow))) {
            toast(getResources().getString(R.string.enter_medical));
            return false;
        }*/

        /*if (isEmpty(getText(edVariablePayAmt))) {
            toast(getResources().getString(R.string.enter_variable_amt));
            return false;
        }*/

        if(Double.parseDouble(getText(edVariablePayAmt)) > 100){
            toast("Variable pay amount % must be less than 100");
            return false;
        }

        if (isEmpty(getText(edEmployeeCon))) {
            toast(getResources().getString(R.string.enter_employee_contribution));
            return false;
        }

        if(Double.parseDouble(getText(edEmployeeCon)) > 100){
            toast("Employee contribution % must be less than 100");
            return false;
        }

        if (isEmpty(getText(edEmployerCon))) {
            toast(getResources().getString(R.string.enter_employer_contribution));
            return false;
        }

        if(Double.parseDouble(getText(edEmployerCon)) > 100){
            toast("Employer contribution % must be less than 100");
            return false;
        }

        return true;
    }

    protected String getText(EditText eTxt) {
        return eTxt == null ? "" : eTxt.getText().toString().trim();
    }

    protected Double getTextInt(EditText eTxt) {
        if (eTxt.getText() != null && eTxt.getText().length() > 0) {
            if(eTxt.getText().toString().equalsIgnoreCase("."))
                return 0.0;
            else
                return Double.parseDouble(eTxt.getText().toString().trim());
        } else {
            return 0.0;
        }
    }

    @OnClick(R.id.rlBack)
    void backPressed121() {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

}
