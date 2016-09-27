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
import android.widget.RelativeLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserPaySLipActivity extends BaseActivity {

    @InjectView(R.id.tvUserName)
    TextView tvUserName;

    @InjectView(R.id.tvMonthYear)
    TextView tvMonthYear;
    @InjectView(R.id.relativeLeftArrow)
    RelativeLayout relativeLeftArrow;
    @InjectView(R.id.relativeRightArrow)
    RelativeLayout relativeRightArrow;

    @InjectView(R.id.et_total_working_days)
    EditText etTotalWorkingDays;
    @InjectView(R.id.et_total_Present_days)
    EditText etTotalPresentDays;
    @InjectView(R.id.et_total_leave_taken)
    EditText etTotalLeavesTaken;

    @InjectView(R.id.et_basic_salary)
    EditText etBasicSalary;
    @InjectView(R.id.et_hra)
    EditText etHRA;
    @InjectView(R.id.et_special_allow)
    EditText etSpecialAllowances;
    @InjectView(R.id.et_conveyance)
    EditText etConveyance;
    @InjectView(R.id.et_medical)
    EditText etMedical;
    @InjectView(R.id.et_variable_earning)
    EditText etVariableEarnings;

    @InjectView(R.id.et_gross)
    EditText etGrossEarnings;
    @InjectView(R.id.et_pf)
    EditText etPF;
    /*@InjectView(R.id.et_leave_deduction)
    EditText etLeaveDeduction;*/
    @InjectView(R.id.et_tds)
    EditText etTDS;
    @InjectView(R.id.et_professional_tax)
    EditText etProfessionalTax;
    @InjectView(R.id.et_short_deduction)
    EditText etShortDeduction;

    @InjectView(R.id.et_gross_deduction)
    EditText etGrossDeduction;

    @InjectView(R.id.et_net_salary)
    EditText etNetSalary;


    private TransparentProgressDialog progressDialog;
    Intent i;

    private String userID;
    private String month;
    private String year;
    private String roleID;

    UserPayslipDetail userPayslipDetail = null;
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pay_slip);

        ButterKnife.inject(this);

        init();
    }

    private void init() {
        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        progressDialog = new TransparentProgressDialog(UserPaySLipActivity.this, R.drawable.progressdialog, false);
        i = getIntent();
        userID = i.getStringExtra("userID");

        relativeRightArrow.setVisibility(View.INVISIBLE);
        relativeLeftArrow.setVisibility(View.INVISIBLE);

        month = i.getStringExtra("month");//month_int + "";
        year = i.getStringExtra("year");//year_int + "";
        roleID = i.getStringExtra("role_ID");
        userPayslipDetail = i.getParcelableExtra("userPayslipDetail");
        user = (User) i.getSerializableExtra("user");

        if (roleID.equalsIgnoreCase("2")) {
            etTotalPresentDays.setEnabled(true);
            etTotalPresentDays.setFocusable(true);
        } else {
            etTotalPresentDays.setBackgroundColor(getResources().getColor(R.color.transparent));
            etTotalPresentDays.setEnabled(false);
            etTotalPresentDays.setFocusable(false);
            etTotalPresentDays.setTextColor(getResources().getColor(R.color.black));
            etTotalPresentDays.setPadding(0, 0, 0, 0);
        }

        tvUserName.setText(user.getFirstName() + " " + user.getLastName());

        etVariableEarnings.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});
        etTotalPresentDays.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});

        setUserPayslipData();
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }

   /* public class DecimalDigitsInputFilterDays implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilterDays(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }*/

    private void setUserPayslipData() {
        if (userPayslipDetail != null) {
            //set value in INPUT BOX
            Calendar startCalendar = Calendar.getInstance();
            SimpleDateFormat sdf1 = new SimpleDateFormat("MMM");
            startCalendar.set(Calendar.MONTH, userPayslipDetail.PayslipGenerationMonth - 1);
            startCalendar.set(Calendar.YEAR, userPayslipDetail.PayslipGenerationYear);

            String sdate = sdf1.format(startCalendar.getTime());
            tvMonthYear.setText(sdate + ", " + userPayslipDetail.PayslipGenerationYear);

            etTotalWorkingDays.setText(userPayslipDetail.TotalWorkingDays + "");
            etTotalPresentDays.setText(userPayslipDetail.TotalPaidDays + "");
            etTotalLeavesTaken.setText(userPayslipDetail.TotalLeavesTaken + "");

            etBasicSalary.setText(userPayslipDetail.BasicSalary + "");
            etHRA.setText(userPayslipDetail.HRA + "");
            etSpecialAllowances.setText(userPayslipDetail.SpecialAllowance + "");
            etConveyance.setText(userPayslipDetail.ConveyanceAllowance + "");
            etMedical.setText(userPayslipDetail.MedicalExpenses + "");
            etVariableEarnings.setText(userPayslipDetail.VariableEarnings + "");

            etGrossEarnings.setText(userPayslipDetail.GrossEarnings + "");
            etPF.setText(userPayslipDetail.PFEmployeeContribution + "");
            //etLeaveDeduction.setText(userPayslipDetail.LeavesDeduction + "");
            etTDS.setText(userPayslipDetail.TDS + "");
            etProfessionalTax.setText(userPayslipDetail.ProfessionalTax + "");
            etShortDeduction.setText(userPayslipDetail.ShortDeduction + "");

            etGrossDeduction.setText(userPayslipDetail.GrossDeduction + "");

            etNetSalary.setText(userPayslipDetail.NetSalary + "");
        }
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

    @OnClick(R.id.rl_save)
    @SuppressWarnings("unused")
    public void saveUpdate(View view) {

        if (Util.isOnline(this)) {

            setBlank();
            if (isValidate()) {
                if (progressDialog != null)
                    progressDialog.show();

                //setBlank();

                JSONObject jsonObject = new JSONObject();
                try {
                    if (userPayslipDetail != null) {
                        jsonObject.put("ID", userPayslipDetail.ID);
                        jsonObject.put("UserID", userPayslipDetail.UserID);
                        jsonObject.put("CompanyID", userPayslipDetail.CompanyID);
                        jsonObject.put("PayslipGenerationMonth", userPayslipDetail.PayslipGenerationMonth);
                        jsonObject.put("PayslipGenerationYear", userPayslipDetail.PayslipGenerationYear);
                        jsonObject.put("PayslipGenerationDate", userPayslipDetail.PayslipGenerationDate);
                        jsonObject.put("TotalWorkingDays", userPayslipDetail.TotalWorkingDays);
                        jsonObject.put("TotalPaidDays", getTextDouble(etTotalPresentDays));
                        jsonObject.put("TotalLeavesTaken", userPayslipDetail.TotalLeavesTaken);
                        jsonObject.put("BasicSalary", userPayslipDetail.BasicSalary);
                        jsonObject.put("HRA", userPayslipDetail.HRA);
                        jsonObject.put("SpecialAllowance", userPayslipDetail.SpecialAllowance);
                        jsonObject.put("ConveyanceAllowance", userPayslipDetail.ConveyanceAllowance);
                        jsonObject.put("MedicalExpenses", userPayslipDetail.MedicalExpenses);
                        jsonObject.put("VariableEarnings", getTextDouble(etVariableEarnings));
                        jsonObject.put("GrossEarnings", userPayslipDetail.GrossEarnings);
                        jsonObject.put("PFEmployeeContribution", userPayslipDetail.PFEmployeeContribution);
                        jsonObject.put("PFEmployerContribution", userPayslipDetail.PFEmployerContribution);
                        //jsonObject.put("LeavesDeduction", userPayslipDetail.LeavesDeduction);
                        jsonObject.put("TDS", userPayslipDetail.TDS);
                        jsonObject.put("ProfessionalTax", userPayslipDetail.ProfessionalTax);
                        jsonObject.put("ShortDeduction", userPayslipDetail.ShortDeduction);
                        jsonObject.put("GrossDeduction", userPayslipDetail.GrossDeduction);
                        jsonObject.put("NetSalary", userPayslipDetail.NetSalary);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("Update PaySlip", "******** Passsing Json : " + jsonObject.toString());

                RestClient.getTeamWork().updateUserPayslip(jsonObject.toString(),
                        Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERID),
                        Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_TOKEN), new Callback<UserPayslipDetail>() {
                            @Override
                            public void success(UserPayslipDetail data, Response response) {
                                try {
                                    Map<String, String> map = Util.readStatus(response);
                                    boolean isSuccess = map.get("status").equals("Success");
                                    String json = Util.getString(response.getBody().in());
                                    JSONObject jObj = new JSONObject(json);

                                    String status = jObj.optString("status");
                                    if (status.equals(Constant.InvalidToken)) {
                                        TeamWorkApplication.LogOutClear(UserPaySLipActivity.this);
                                        return;
                                    }

                                    if (isSuccess) {
                                        Toast.makeText(UserPaySLipActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                        //value save in preferences
                                        JSONObject dataObj = jObj.getJSONObject("data");
                                        if (dataObj != null) {
                                            userPayslipDetail = data;

                                            //set value in INPUT BOX
                                            etTotalWorkingDays.setText(data.TotalWorkingDays + "");
                                            etTotalLeavesTaken.setText(data.TotalLeavesTaken + "");

                                            etBasicSalary.setText(data.BasicSalary + "");
                                            etHRA.setText(data.HRA + "");
                                            etSpecialAllowances.setText(data.SpecialAllowance + "");
                                            etConveyance.setText(data.ConveyanceAllowance + "");
                                            etMedical.setText(data.MedicalExpenses + "");
                                            etVariableEarnings.setText(data.VariableEarnings + "");

                                            etGrossEarnings.setText(data.GrossEarnings + "");
                                            etPF.setText(data.PFEmployeeContribution + "");
                                            //etLeaveDeduction.setText(data.LeavesDeduction + "");
                                            etTDS.setText(data.TDS + "");
                                            etProfessionalTax.setText(data.ProfessionalTax + "");
                                            etShortDeduction.setText(data.ShortDeduction + "");

                                            etGrossDeduction.setText(data.GrossDeduction + "");

                                            etNetSalary.setText(data.NetSalary + "");
                                        }
                                    } else {
                                        Toast.makeText(UserPaySLipActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setBlank() {
        if (etVariableEarnings.getText().toString().trim().equals("") || etVariableEarnings.getText().toString().trim().equals(".")) {
            etVariableEarnings.setText("0.0");
        }

        if (etTotalPresentDays.getText().toString().trim().equals("") || etTotalPresentDays.getText().toString().trim().equals(".")) {
            etTotalPresentDays.setText("0.0");
        }
    }

    private boolean isValidate() {

        String totalPresentDays = getText(etTotalPresentDays);
        if(totalPresentDays.contains(".")){
            if(((Integer.parseInt(totalPresentDays.substring(totalPresentDays.indexOf(".")+1,totalPresentDays.length()))) != 0)
                    && ((Integer.parseInt(totalPresentDays.substring(totalPresentDays.indexOf(".")+1,totalPresentDays.length()))) != 5)){
                toast("Only 0 and 5 allowed in decimal for total present days");
                return false;
            }
        }

        if(getTextDouble(etTotalPresentDays) > getTextDouble(etTotalWorkingDays)){
            toast("Total paid days should be less than total working days");
            return false;
        }
        return true;
    }

    protected Double getTextDouble(EditText eTxt) {
        if (eTxt.getText() != null && eTxt.getText().length() > 0) {
            return Double.parseDouble(eTxt.getText().toString().trim());
        } else {
            return 0.0;
        }
    }

     /*private void getUserPayslipData() {

        if (Util.isOnline(this)) {

            if (progressDialog != null)
                progressDialog.show();

            Log.d("", "*************** Company ID : " + Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ID)
                    + " User ID " + userID + " Month " + month + " Year " + year + " ***************");

            RestClient.getTeamWork().getUserPayslip(userID,
                    Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ID),
                    month,
                    year,
                    new Callback<UserPayslipDetail>() {
                        @Override
                        public void success(UserPayslipDetail data, Response response) {
                            try {

                                Map<String, String> map = Util.readStatus(response);
                                boolean isSuccess = map.get("status").equals("Success");

                                String json = Util.getString(response.getBody().in());
                                JSONObject jObj = new JSONObject(json);
                                if (isSuccess) {
                                    JSONObject dataJobj = jObj.getJSONObject("data");

                                    if (dataJobj != null) {
                                        userPayslipDetail = data;
                                        //set value in INPUT BOX
                                        Calendar startCalendar = Calendar.getInstance();
                                        SimpleDateFormat sdf1 = new SimpleDateFormat("MMM");
                                        startCalendar.set(Calendar.MONTH, data.PayslipGenerationMonth-1);
                                        startCalendar.set(Calendar.YEAR, data.PayslipGenerationYear);

                                        String sdate = sdf1.format(startCalendar.getTime());
                                        tvMonthYear.setText(sdate + ", " + data.PayslipGenerationYear);

                                        etTotalWorkingDays.setText(data.TotalWorkingDays + "");
                                        etTotalPresentDays.setText(data.TotalPaidDays + "");
                                        etTotalLeavesTaken.setText(data.TotalLeavesTaken + "");

                                        etBasicSalary.setText(data.BasicSalary + "");
                                        etHRA.setText(data.HRA + "");
                                        etSpecialAllowances.setText(data.SpecialAllowance + "");
                                        etConveyance.setText(data.ConveyanceAllowance + "");
                                        etMedical.setText(data.MedicalExpenses + "");
                                        etVariableEarnings.setText(data.VariableEarnings + "");

                                        etGrossEarnings.setText(data.GrossEarnings + "");
                                        etPF.setText(data.PFEmployeeContribution + "");
                                        etLeaveDeduction.setText(data.LeavesDeduction + "");
                                        etTDS.setText(data.TDS + "");
                                        etProfessionalTax.setText(data.ProfessionalTax + "");
                                        etShortDeduction.setText(data.ShortDeduction + "");

                                        etGrossDeduction.setText(data.GrossDeduction + "");

                                        etNetSalary.setText(data.NetSalary + "");
                                    }
                                } else {
                                    Toast.makeText(UserPaySLipActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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
            Toast.makeText(this, Constant.network_error, Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
        }
    }*/

}
