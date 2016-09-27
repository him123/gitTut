package com.bigbang.superteam.payroll;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
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

/**
 * Created by User on 5/25/2016.
 */
public class PayrollSetupAdvanceActivity extends BaseActivity {

    @InjectView(R.id.spnWorkingDayPolicy)
    Spinner spnWorkingDayPolicy;
    @InjectView(R.id.etOneHrsDeduction)
    EditText etOneHrsDeduction;
    @InjectView(R.id.etTwoHrsDeduction)
    EditText etTwoHrsDeduction;
    @InjectView(R.id.etThreeHrsDeduction)
    EditText etThreeHrsDeduction;
    @InjectView(R.id.etFourHrsDeduction)
    EditText etFourHrsDeduction;
    @InjectView(R.id.etFiveHrsDeduction)
    EditText etFiveHrsDeduction;
    @InjectView(R.id.etLateAllowedTime)
    EditText etLateAllowedTime;
    @InjectView(R.id.spnHoursCalculationType)
    Spinner spnHoursCalculationType;

    @InjectView(R.id.rl_one_hrs_deduction)
    RelativeLayout rl_one_hrs_deduction;
    @InjectView(R.id.rl_two_hrs_deduction)
    RelativeLayout rl_two_hrs_deduction;
    @InjectView(R.id.rl_three_hrs_deduction)
    RelativeLayout rl_three_hrs_deduction;
    @InjectView(R.id.rl_four_hrs_deduction)
    RelativeLayout rl_four_hrs_deduction;
    @InjectView(R.id.rl_five_hrs_deduction)
    RelativeLayout rl_five_hrs_deduction;
    @InjectView(R.id.rl_late_allowed_time)
    RelativeLayout rl_late_allowed_time;
    @InjectView(R.id.rl_note_text)
    RelativeLayout rl_note_text;

    @InjectView(R.id.rl_hrs_cal_type)
    RelativeLayout rl_hrs_cal_type;

    TransparentProgressDialog progressDialog;

    String one, two, three, four, five, lateAllowedTime;
    int workingPolicy = 0, hrs_cal_type = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_payroll_setup_advance);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        ButterKnife.inject(this);

        progressDialog = new TransparentProgressDialog(this, R.drawable.progressdialog, false);

        spnWorkingDayPolicy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    lateComingEarlyGoingPolicy();
                } else if (position == 1) {
                    minimumHoursPolicy();
                } else if (position == 2) {
                    none();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etOneHrsDeduction.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        etTwoHrsDeduction.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        etThreeHrsDeduction.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        etFourHrsDeduction.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        etFiveHrsDeduction.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});

        spnWorkingDayPolicy.setSelection(2);

        boolean payrollEnabled = Util.ReadSharePrefrenceBoolean(this, Constant.SHRED_PR.KEY_PAYROLL_ACTIVE);

        if (payrollEnabled) {
            setDefaultValuesFromPreferences();
        }
    }

    private void setDefaultValuesFromPreferences() {
        one = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_ONE);
        two = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_TWO);
        three = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_THREE);
        four = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_FOUR);
        five = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_FIVE);
        lateAllowedTime = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_LATE_ALLOWED_TIME);
        workingPolicy = Util.ReadSharePrefrenceInteger(this, Constant.SHRED_PR.KEY_WORKING_POLICY_TYPE_ID);
        hrs_cal_type = Util.ReadSharePrefrenceInteger(this, Constant.SHRED_PR.KEY_HRS_CALCULATION_TYPE);

        if (workingPolicy > 0)
            spnWorkingDayPolicy.setSelection(workingPolicy - 1);

        if (hrs_cal_type > 0)
            spnHoursCalculationType.setSelection(hrs_cal_type - 1);

        etOneHrsDeduction.setText(one + "");
        etTwoHrsDeduction.setText(two + "");
        etThreeHrsDeduction.setText(three + "");
        etFourHrsDeduction.setText(four + "");
        etFiveHrsDeduction.setText(five + "");
        etLateAllowedTime.setText(lateAllowedTime + "");
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

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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
                    jsonObject.put("WorkingPolicyTypeID", spnWorkingDayPolicy.getSelectedItemPosition() + 1);
                    jsonObject.put("CompanyID", Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ID));
                    if (spnWorkingDayPolicy.getSelectedItemPosition() == 0) {
                        jsonObject.put("LateEarly1hrsDeduction", getTextInt(etOneHrsDeduction));
                        jsonObject.put("LateEarly2hrsDeduction", getTextInt(etTwoHrsDeduction));
                        jsonObject.put("LateEarly3hrsDeduction", getTextInt(etThreeHrsDeduction));
                        jsonObject.put("LateEarly4hrsDeduction", getTextInt(etFourHrsDeduction));
                        jsonObject.put("LateEarly5hrsDeduction", getTextInt(etFiveHrsDeduction));
                        jsonObject.put("LateAllowedTimes", getTextInt(etLateAllowedTime));

                        jsonObject.put("HrsCalculationType", 0);
                    }
                    if (spnWorkingDayPolicy.getSelectedItemPosition() == 1) {
                        jsonObject.put("LateEarly1hrsDeduction", 0);
                        jsonObject.put("LateEarly2hrsDeduction", 0);
                        jsonObject.put("LateEarly3hrsDeduction", 0);
                        jsonObject.put("LateEarly4hrsDeduction", 0);
                        jsonObject.put("LateEarly5hrsDeduction", 0);
                        jsonObject.put("LateAllowedTimes", 0);

                        jsonObject.put("HrsCalculationType", spnHoursCalculationType.getSelectedItemPosition() + 1);
                    }
                    if (spnWorkingDayPolicy.getSelectedItemPosition() == 2) {
                        jsonObject.put("LateEarly1hrsDeduction", 0);
                        jsonObject.put("LateEarly2hrsDeduction", 0);
                        jsonObject.put("LateEarly3hrsDeduction", 0);
                        jsonObject.put("LateEarly4hrsDeduction", 0);
                        jsonObject.put("LateEarly5hrsDeduction", 0);
                        jsonObject.put("LateAllowedTimes", 0);

                        jsonObject.put("HrsCalculationType", 0);
                    }
                    jsonObject.put("CreatedBy", Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERID));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("Payroll Advance", "******** Passsing Json : " + jsonObject.toString());

                RestClient.getTeamWork().addUpdateCompanyWorkingPolicy(jsonObject.toString(),
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
                                        TeamWorkApplication.LogOutClear(PayrollSetupAdvanceActivity.this);
                                        return;
                                    }

                                    if (isSuccess) {
                                        Toast.makeText(PayrollSetupAdvanceActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();

                                        //value save in preferences
                                        JSONObject dataObj = jObj.getJSONObject("data");

                                        Util.WriteSharePrefrenceForInteger(PayrollSetupAdvanceActivity.this, Constant.SHRED_PR.KEY_WORKING_POLICY_TYPE_ID, dataObj.getInt("WorkingPolicyTypeID"));
                                        Util.WriteSharePrefrence(PayrollSetupAdvanceActivity.this, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_ONE, dataObj.getString("LateEarly1hrsDeduction"));
                                        Util.WriteSharePrefrence(PayrollSetupAdvanceActivity.this, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_TWO, dataObj.getString("LateEarly2hrsDeduction"));
                                        Util.WriteSharePrefrence(PayrollSetupAdvanceActivity.this, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_THREE, dataObj.getString("LateEarly3hrsDeduction"));
                                        Util.WriteSharePrefrence(PayrollSetupAdvanceActivity.this, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_FOUR, dataObj.getString("LateEarly4hrsDeduction"));
                                        Util.WriteSharePrefrence(PayrollSetupAdvanceActivity.this, Constant.SHRED_PR.KEY_LATEEARLY_DEDUCTION_FIVE, dataObj.getString("LateEarly5hrsDeduction"));
                                        Util.WriteSharePrefrence(PayrollSetupAdvanceActivity.this, Constant.SHRED_PR.KEY_LATE_ALLOWED_TIME, dataObj.getString("LateAllowedTimes"));
                                        Util.WriteSharePrefrenceForInteger(PayrollSetupAdvanceActivity.this, Constant.SHRED_PR.KEY_HRS_CALCULATION_TYPE, dataObj.getInt("HrsCalculationType"));

                                        finish();
                                        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);

                                    } else {
                                        Toast.makeText(PayrollSetupAdvanceActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(PayrollSetupAdvanceActivity.this, Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setBlank() {
        if (etOneHrsDeduction.getText().toString().trim().equals("") || etOneHrsDeduction.getText().toString().trim().equals(".")) {
            etOneHrsDeduction.setText("0.0");
        }

        if (etTwoHrsDeduction.getText().toString().trim().equals("") || etTwoHrsDeduction.getText().toString().trim().equals(".")) {
            etTwoHrsDeduction.setText("0.0");
        }

        if (etThreeHrsDeduction.getText().toString().trim().equals("") || etThreeHrsDeduction.getText().toString().trim().equals(".")) {
            etThreeHrsDeduction.setText("0.0");
        }

        if (etFourHrsDeduction.getText().toString().trim().equals("") || etFourHrsDeduction.getText().toString().trim().equals(".")) {
            etFourHrsDeduction.setText("0.0");
        }

        if (etFiveHrsDeduction.getText().toString().trim().equals("") || etFiveHrsDeduction.getText().toString().trim().equals(".")) {
            etFiveHrsDeduction.setText("0.0");
        }

        if (etLateAllowedTime.getText().toString().trim().equals("")) {
            etLateAllowedTime.setText("00");
        }
    }

    private void lateComingEarlyGoingPolicy() {
        rl_one_hrs_deduction.setVisibility(View.VISIBLE);
        rl_two_hrs_deduction.setVisibility(View.VISIBLE);
        rl_three_hrs_deduction.setVisibility(View.VISIBLE);
        rl_four_hrs_deduction.setVisibility(View.VISIBLE);
        rl_five_hrs_deduction.setVisibility(View.VISIBLE);
        rl_late_allowed_time.setVisibility(View.VISIBLE);
        rl_note_text.setVisibility(View.VISIBLE);

        rl_hrs_cal_type.setVisibility(View.GONE);
    }

    private void minimumHoursPolicy() {
        rl_one_hrs_deduction.setVisibility(View.GONE);
        rl_two_hrs_deduction.setVisibility(View.GONE);
        rl_three_hrs_deduction.setVisibility(View.GONE);
        rl_four_hrs_deduction.setVisibility(View.GONE);
        rl_five_hrs_deduction.setVisibility(View.GONE);
        rl_late_allowed_time.setVisibility(View.GONE);
        rl_note_text.setVisibility(View.GONE);

        rl_hrs_cal_type.setVisibility(View.VISIBLE);
    }

    private void none() {
        rl_one_hrs_deduction.setVisibility(View.GONE);
        rl_two_hrs_deduction.setVisibility(View.GONE);
        rl_three_hrs_deduction.setVisibility(View.GONE);
        rl_four_hrs_deduction.setVisibility(View.GONE);
        rl_five_hrs_deduction.setVisibility(View.GONE);
        rl_late_allowed_time.setVisibility(View.GONE);
        rl_note_text.setVisibility(View.GONE);

        rl_hrs_cal_type.setVisibility(View.GONE);
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
        toast(this.getResources().getText(resId));
    }

    protected void toast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(this, key);
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(this, key, val);
    }

    private boolean isValidate() {
        if (Double.parseDouble(getText(etOneHrsDeduction)) > 100) {
            toast("Late/Early 1 hour deduction % must be less than 100");
            return false;
        }

        if (Double.parseDouble(getText(etTwoHrsDeduction)) > 100) {
            toast("Late/Early 2 hours deduction % must be less than 100");
            return false;
        }

        if (Double.parseDouble(getText(etThreeHrsDeduction)) > 100) {
            toast("Late/Early 3 hours deduction % must be less than 100");
            return false;
        }

        if (Double.parseDouble(getText(etFourHrsDeduction)) > 100) {
            toast("Late/Early 4 hours deduction % must be less than 100");
            return false;
        }

        if (Double.parseDouble(getText(etFiveHrsDeduction)) > 100) {
            toast("Late/Early 5 hours deduction % must be less than 100");
            return false;
        }

        return true;
    }
}
