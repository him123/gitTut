package com.bigbang.superteam.payroll;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONObject;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by User on 6/22/2016.
 */
public class UserPayrollFragment extends Fragment {

    @InjectView(R.id.header)
    RelativeLayout header;
    @InjectView(R.id.rl_next_back)
    RelativeLayout rl_next_back;

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

    public static Activity activity;
    private TransparentProgressDialog progressDialog;

    String basicSalary, hra, medical, conveyance, variablePayAmt, employeeCont, employerCont;

    public static UserPayrollFragment newInstance() {
        UserPayrollFragment f = new UserPayrollFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.activity_update_user_payroll, container, false);
        ButterKnife.inject(this, v);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);
        ButterKnife.inject(this, v);

        header.setVisibility(View.GONE);
        rl_next_back.setVisibility(View.GONE);
        progressDialog = new TransparentProgressDialog(activity, R.drawable.progressdialog, false);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        //callUserPayrollData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        disableAllEditText();

        callUserPayrollData();
    }

    private void disableAllEditText() {
        edEmployeeCode.setEnabled(false);
        edEmployeeCode.setFocusable(false);

        edDesignation.setEnabled(false);
        edDesignation.setFocusable(false);

        edPANNo.setEnabled(false);
        edPANNo.setFocusable(false);

        edPFAccountNo.setEnabled(false);
        edPFAccountNo.setFocusable(false);

        edBankName.setEnabled(false);
        edBankName.setFocusable(false);

        edBankActNumber.setEnabled(false);
        edBankActNumber.setFocusable(false);

        edCTC.setEnabled(false);
        edCTC.setFocusable(false);

        edBasicSalary.setEnabled(false);
        edBasicSalary.setFocusable(false);

        edHRA.setEnabled(false);
        edHRA.setFocusable(false);

        edMedicalAllow.setEnabled(false);
        edMedicalAllow.setFocusable(false);

        edConveyance.setEnabled(false);
        edConveyance.setFocusable(false);

        edVariablePayAmt.setEnabled(false);
        edVariablePayAmt.setFocusable(false);

        edEmployeeCon.setEnabled(false);
        edEmployeeCon.setFocusable(false);

        edEmployerCon.setEnabled(false);
        edEmployerCon.setFocusable(false);

        edActualRentPaid.setEnabled(false);
        edActualRentPaid.setFocusable(false);

        edInvestment80C.setEnabled(false);
        edInvestment80C.setFocusable(false);

        edInvestment80D.setEnabled(false);
        edInvestment80D.setFocusable(false);

    }

    private void callUserPayrollData() {

        if (Util.isOnline(activity)) {
            if (progressDialog != null)
                progressDialog.show();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("UserID", Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID));
                jsonObject.put("CompanyID", Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID));
            } catch (Exception e) {
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
                                    TeamWorkApplication.LogOutClear(getActivity());
                                    return;
                                }

                                if (isSuccess) {
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
                                } else {
                                    boolean isNoData = map.get("status").equals("No Data Found");
                                    if (isNoData)
                                        setDefaultValuesFromPreferences();
                                    else
                                        Toast.makeText(activity, jObj.getString("message"), Toast.LENGTH_SHORT).show();
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
        } else {
            Toast.makeText(activity, Constant.network_error, Toast.LENGTH_SHORT).show();
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

        edBasicSalary.setText(basicSalary + "");
        edHRA.setText(hra + "");
        edMedicalAllow.setText(medical + "");
        edConveyance.setText(conveyance + "");
        edVariablePayAmt.setText(variablePayAmt + "");
        edEmployeeCon.setText(employeeCont + "");
        edEmployerCon.setText(employerCont + "");
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(activity, key);
    }

}
