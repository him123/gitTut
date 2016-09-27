package com.bigbang.superteam.payroll;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by User on 6/22/2016.
 */
public class UserPayslipFragment extends Fragment {

    @InjectView(R.id.header)
    RelativeLayout header;
    @InjectView(R.id.rl_save)
    RelativeLayout rl_save;
    @InjectView(R.id.tvNext)
    TextView tvNext;

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

    Calendar calendar, startCalendar;
    int curMonth, curYear;
    SimpleDateFormat sdf1 = new SimpleDateFormat("MMM");
    int year1=0, month1=0;

    public static Activity activity;

    public static UserPayslipFragment newInstance() {
        UserPayslipFragment f = new UserPayslipFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.activity_user_pay_slip, container, false);
        ButterKnife.inject(this, v);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);
        ButterKnife.inject(this, v);

        header.setVisibility(View.GONE);
        tvNext.setText("DOWNLOAD");
        //disable download button
        disableDownloadButton();
        rl_save.setClickable(true);

        progressDialog = new TransparentProgressDialog(activity, R.drawable.progressdialog, false);
        return v;
    }

    private void disableDownloadButton(){
        rl_save.setBackgroundResource(R.drawable.rectanle_disable);
        rl_save.setClickable(false);
    }

    private void enableDownloadButton(){
        rl_save.setBackgroundResource(R.drawable.rectangle_blue);
        rl_save.setClickable(true);
    }

    private void refreshAllFields(){
        etTotalWorkingDays.setText("");
        etTotalPresentDays.setText("");
        etTotalLeavesTaken.setText("");

        etBasicSalary.setText("");
        etHRA.setText("");
        etSpecialAllowances.setText("");
        etConveyance.setText("");
        etMedical.setText("");
        etVariableEarnings.setText("");

        etGrossEarnings.setText("");
        etPF.setText("");
        //etLeaveDeduction.setText("");
        etTDS.setText("");
        etProfessionalTax.setText("");
        etShortDeduction.setText("");

        etGrossDeduction.setText("");

        etNetSalary.setText("");
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        startCalendar = Calendar.getInstance();
        calendar = Calendar.getInstance();
        curMonth = calendar.get(calendar.MONTH);
        curYear = calendar.get(calendar.YEAR);

        startCalendar.set(Calendar.YEAR, curYear);
        startCalendar.set(Calendar.MONTH, curMonth);

        String sdate = sdf1.format(startCalendar.getTime());
        tvMonthYear.setText(sdate + ", " + curYear);

        relativeRightArrow.setVisibility(View.INVISIBLE);

        month1 = curMonth+1;
        year1 = curYear;

        etTotalPresentDays.setBackgroundColor(getResources().getColor(R.color.transparent));
        etTotalPresentDays.setEnabled(false);
        etTotalPresentDays.setFocusable(false);
        etTotalPresentDays.setTextColor(getResources().getColor(R.color.black));
        etTotalPresentDays.setPadding(0,0,0,0);

        etVariableEarnings.setBackgroundColor(getResources().getColor(R.color.transparent));
        etVariableEarnings.setEnabled(false);
        etVariableEarnings.setFocusable(false);
        etVariableEarnings.setTextColor(getResources().getColor(R.color.black));
        etVariableEarnings.setPadding(0,0,0,0);

        getUserPayslipData();
    }

    private void getUserPayslipData() {

        if (Util.isOnline(activity)) {

            if (progressDialog != null)
                progressDialog.show();


            RestClient.getTeamWork().getUserPayslip(Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID),
                    Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID),
                    month1 +"",
                    year1 +"",
                    "true",
                    new Callback<UserPayslipDetail>() {
                        @Override
                        public void success(UserPayslipDetail data, Response response) {
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
                                        //etLeaveDeduction.setText(data.LeavesDeduction + "");
                                        etTDS.setText(data.TDS + "");
                                        etProfessionalTax.setText(data.ProfessionalTax + "");
                                        etShortDeduction.setText(data.ShortDeduction + "");

                                        etGrossDeduction.setText(data.GrossDeduction + "");

                                        etNetSalary.setText(data.NetSalary + "");

                                        //enable download button
                                        enableDownloadButton();
                                    }else
                                        disableDownloadButton();
                                } else {
                                    Toast.makeText(activity, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                    disableDownloadButton();
                                    //refresh all fields
                                    refreshAllFields();
                                }
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                Log.d("", "Exception : " + e);
                                progressDialog.dismiss();
                                disableDownloadButton();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            progressDialog.dismiss();
                            disableDownloadButton();
                            //refresh all fields
                        }
                    });
        } else {
            Toast.makeText(activity, Constant.network_error +"", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rl_save)
    void downloadPayslip(){
        //int year = Util.ReadSharePrefrenceInteger(activity, Constant.SHRED_PR.KEY_PAYSLIP_YEAR);
        //int month = Util.ReadSharePrefrenceInteger(activity, Constant.SHRED_PR.KEY_PAYSLIP_MONTH);
        if(year1 > 0){
            //Open dialog for EMAIL ID
            Intent intent = new Intent(activity, EmailDialogActivity.class);
            intent.putExtra("userID", Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID));
            intent.putExtra("month", month1+"");
            intent.putExtra("year", year1+"");
            intent.putExtra("email_ID", Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_EMAIL));
            activity.startActivityForResult(intent, 1);
            //startActivityForResult(new Intent(AddFlatDisocountOffer.this, SelectItemDailogActivity.class).putExtra("flag", true).putExtra("ids", customer_ids).putExtra("ids_live", customer_ids), 1);

        }else{
            Toast.makeText(activity, "Please select Month and Year", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.tvMonthYear)
    void openDatePicker() {
        DatePickerDialog d = new DatePickerDialog(activity,
                DatePickerDialog.THEME_HOLO_LIGHT, myfromDateListener, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DATE));
        ((ViewGroup) d.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        d.getDatePicker().setMaxDate(new Date().getTime());
        d.setTitle("");
        d.show();
    }

    @OnClick(R.id.relativeLeftArrow)
    void getPreviousDate() {
        relativeRightArrow.setVisibility(View.VISIBLE);
        startCalendar.add(startCalendar.MONTH, -1);

        curMonth = startCalendar.get(Calendar.MONTH) + 1;
        curYear = startCalendar.get(Calendar.YEAR);

        String sdate = sdf1.format(startCalendar.getTime());
        tvMonthYear.setText(sdate + ", " + curYear);

        year1 = curYear;
        month1 = curMonth;

        if(startCalendar.before(calendar)){
            relativeLeftArrow.setVisibility(View.VISIBLE);
        }else{
            relativeLeftArrow.setVisibility(View.INVISIBLE);
        }

        getUserPayslipData();
    }

    @OnClick(R.id.relativeRightArrow)
    void getNextDate() {
        startCalendar.add(startCalendar.MONTH, 1);

        curMonth = startCalendar.get(Calendar.MONTH) + 1;
        curYear = startCalendar.get(Calendar.YEAR);

        String sdate = sdf1.format(startCalendar.getTime());
        tvMonthYear.setText(sdate + ", " + curYear);

        year1 = curYear;
        month1 = curMonth;

        if(startCalendar.before(calendar)){
            relativeRightArrow.setVisibility(View.VISIBLE);
            relativeLeftArrow.setVisibility(View.VISIBLE);
        }else{
            relativeRightArrow.setVisibility(View.INVISIBLE);
        }

        getUserPayslipData();
    }

    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            Util.WriteSharePrefrenceForInteger(activity, Constant.SHRED_PR.KEY_PAYSLIP_MONTH, month+1);
            Util.WriteSharePrefrenceForInteger(activity, Constant.SHRED_PR.KEY_PAYSLIP_YEAR, year);

            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);

            curMonth = month+1;
            curYear = year;

            String sdate = sdf1.format(startCalendar.getTime());
            tvMonthYear.setText(sdate + ", " + year);

            year1 = curYear;
            month1 = curMonth;

            if(startCalendar.before(calendar)){
                relativeRightArrow.setVisibility(View.VISIBLE);
            }else{
                relativeRightArrow.setVisibility(View.INVISIBLE);
            }


            getUserPayslipData();
        }
    };

}
