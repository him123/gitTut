package com.bigbang.superteam.payroll;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by User on 6/23/2016.
 */
public class GeneratePayslipFragment extends Fragment {

    @InjectView(R.id.tvMonthYear)
    TextView tvMonthYear;
    @InjectView(R.id.relativeLeftArrow)
    RelativeLayout relativeLeftArrow;
    @InjectView(R.id.relativeRightArrow)
    RelativeLayout relativeRightArrow;

    @InjectView(R.id.listUsers)
    ListView listUsers;

    Calendar calendar, startCalendar;
    int curMonth, curYear;
    SimpleDateFormat sdf1 = new SimpleDateFormat("MMM");

    TransparentProgressDialog progressDialog;
    PaySlipUserAdapter adapter;
    List<User> usersList = new ArrayList<User>();

    int month1 = 0, year1 = 0;

    public static GeneratePayslipFragment newInstance() {
        GeneratePayslipFragment f = new GeneratePayslipFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_generate_payslip, container, false);
        ButterKnife.inject(this, v);
        final Typeface mFont = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);
        ButterKnife.inject(this, v);

        progressDialog = new TransparentProgressDialog(getActivity(), R.drawable.progressdialog, false);

        //Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_MONTH, 0);
        //Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_YEAR, 0);

        init();

        return v;
    }

    private void init() {

        startCalendar = Calendar.getInstance();
        calendar = Calendar.getInstance();
        curMonth = calendar.get(calendar.MONTH);
        curYear = calendar.get(calendar.YEAR);

        startCalendar.set(Calendar.YEAR, curYear);
        startCalendar.set(Calendar.MONTH, curMonth);

        String sdate = sdf1.format(startCalendar.getTime());
        tvMonthYear.setText(sdate + ", " + curYear);

        relativeRightArrow.setVisibility(View.INVISIBLE);

        month1 = curMonth +1;
        year1 = curYear;

        if (Util.isOnline(getActivity())) {
            getUsers();
        }else{
            Toast.makeText(getActivity(), "" + Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void getUsers() {

        if (progressDialog != null) progressDialog.show();

        RestClient.getCommonService().getReportingMembers(Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_USERID),
                Constant.AppName,
                new Callback<List<User>>() {
                    @Override
                    public void success(List<com.bigbang.superteam.model.User> users, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(getActivity());
                                return;
                            }

                            //users -------- user list
                            //set in adapter
                            if(users != null && users.size() > 0){
                                usersList = users;
                                adapter = new PaySlipUserAdapter(getActivity(), usersList, 1);
                                listUsers.setAdapter(adapter);
                                //listUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                            }else
                                Toast.makeText(getActivity(), "No users available.", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (progressDialog != null)
                                if (progressDialog.isShowing()) progressDialog.dismiss();

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                    }
                });

    }

    @OnClick(R.id.tvMonthYear)
    void openDatePicker() {
        DatePickerDialog d = new DatePickerDialog(getActivity(),
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

        month1 = curMonth;
        year1 = curYear;

        if(startCalendar.before(calendar)){
            relativeLeftArrow.setVisibility(View.VISIBLE);
        }else{
            relativeLeftArrow.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.relativeRightArrow)
    void getNextDate() {
        startCalendar.add(startCalendar.MONTH, 1);

        curMonth = startCalendar.get(Calendar.MONTH) + 1;
        curYear = startCalendar.get(Calendar.YEAR);

        String sdate = sdf1.format(startCalendar.getTime());
        tvMonthYear.setText(sdate + ", " + curYear);

        month1 = curMonth;
        year1 = curYear;

        if(startCalendar.before(calendar)){
            relativeRightArrow.setVisibility(View.VISIBLE);
            relativeLeftArrow.setVisibility(View.VISIBLE);
        }else{
            relativeRightArrow.setVisibility(View.INVISIBLE);
        }
    }

    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_MONTH, month+1);
            Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_YEAR, year);

            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);

            curMonth = month+1;
            curYear = year;

            month1 = month+1;
            year1 = year;

            String sdate = sdf1.format(startCalendar.getTime());
            tvMonthYear.setText(sdate + ", " + year);

            if(startCalendar.before(calendar)){
                relativeRightArrow.setVisibility(View.VISIBLE);
            }else{
                relativeRightArrow.setVisibility(View.INVISIBLE);
            }
        }
    };

    /*@OnClick(R.id.relativeSelectAll)
    public void selectAll(){
        if(usersList != null && usersList.size() > 0){
            for (int  i=0; i<usersList.size(); i++){
                usersList.get(i).setChecked(true);
            }
            adapter.notifyDataSetChanged();
        }
    }*/

    @OnClick(R.id.checkBoxSelectAll)
    public void selectAllCheckBoxCLick(View v){
        CheckBox cb = (CheckBox) v ;

        if(usersList != null && usersList.size() > 0){
            for (int  i=0; i<usersList.size(); i++){
                usersList.get(i).setChecked(cb.isChecked());
            }
            adapter.notifyDataSetChanged();
        }
    }


    @OnClick(R.id.rl_save)
    public void save(View view) {
        if (Util.isOnline(getActivity())) {
            if (progressDialog != null)
                progressDialog.show();

            if(year1 > 0) {
                //Set json
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("CompanyID", Integer.parseInt(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_ID)));
                    jsonObject.put("PayslipGenerationMonth", month1);
                    jsonObject.put("PayslipGenerationYear", year1);
                    jsonObject.put("CreatedBy", Integer.parseInt(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("Payroll", "******** Passsing Json : " + jsonObject.toString());

                //set user json
                //JSONArray jsonUserArray = new JSONArray();
                StringBuilder usersArray = new StringBuilder();
                String userParameter = null;
                try {
                    if (adapter != null && adapter.getCount() > 0) {
                        List<User> userList = adapter.getUserList();
                        if (userList != null && userList.size() > 0) {
                            for (int i = 0; i < userList.size(); i++) {
                                User user = userList.get(i);
                                if (user.isChecked()) {
                                    //JSONObject jObj = new JSONObject();
                                    //jObj.put("userId", user.getUserID());
                                    //jsonUserArray.put(jObj);

                                    usersArray.append(user.getUserID() + ",");
                                }
                            }

                            userParameter = usersArray.substring(0, usersArray.length() - 1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("Payroll", "******** Passsing Json Users : " + usersArray.toString());

                if (userParameter != null && userParameter.length() > 0) {
                    RestClient.getTeamWork().addUpdateUserPayslip(jsonObject.toString(),
                            userParameter.toString(),
                            Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID),
                            Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_TOKEN), new Callback<Response>() {
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

                                    } catch (Exception e) {
                                        Log.d("", "Exception: " + e);
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    progressDialog.dismiss();
                                }
                            });
                    Toast.makeText(getActivity(), "Your request is under process. We will notify you once done", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Select User/s", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
            }else{
                Toast.makeText(getActivity(), "Please select month & year", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
        } else {
            Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

}
