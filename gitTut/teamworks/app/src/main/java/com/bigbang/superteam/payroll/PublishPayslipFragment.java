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

import org.json.JSONArray;
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
 * Created by User on 6/30/2016.
 */
public class PublishPayslipFragment extends Fragment {

    @InjectView(R.id.tvMonthYear)
    TextView tvMonthYear;
    @InjectView(R.id.relativeLeftArrow)
    RelativeLayout relativeLeftArrow;
    @InjectView(R.id.relativeRightArrow)
    RelativeLayout relativeRightArrow;

    @InjectView(R.id.listUsers)
    ListView listUsers;
    @InjectView(R.id.rl_save)
    RelativeLayout rl_save;
    @InjectView(R.id.relativeSelectAll)
    RelativeLayout relativeSelectAll;
    @InjectView(R.id.tvNext)
    TextView tvNext;

    Calendar calendar, startCalendar;
    int curMonth, curYear;
    SimpleDateFormat sdf1 = new SimpleDateFormat("MMM");

    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;

    TransparentProgressDialog progressDialog;
    PaySlipUserAdapter adapter;
    List<User> usersList = new ArrayList<User>();

    public static PublishPayslipFragment newInstance() {
        PublishPayslipFragment f = new PublishPayslipFragment();
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

        tvNext.setText("PUBLISH");

        if (!fragmentResume && fragmentVisible) {   //only when first time fragment is created
            updateMyUI();
        }

        init();

        return v;
    }

    private void updateMyUI() {

        init();
        /*usersList = null;
        usersList = new ArrayList<User>();

        tvMonthYear.setText("Select Month & Year");
        listUsers.setVisibility(View.INVISIBLE);
        relativeSelectAll.setVisibility(View.INVISIBLE);
        rl_save.setVisibility(View.INVISIBLE);

        Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_MONTH, 0);
        Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_YEAR, 0);*/

    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
            updateMyUI();
        } else if (visible) {        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        } else if (!visible && fragmentOnCreated) {// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
        }
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

        Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_MONTH, curMonth+1);
        Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_YEAR, curYear);

        getUsers();
    }

    @OnClick(R.id.tvMonthYear)
    void openDatePicker() {
        //updateMyUI();
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

        Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_MONTH, curMonth);
        Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_YEAR, curYear);

        if(startCalendar.before(calendar)){
            relativeLeftArrow.setVisibility(View.VISIBLE);
        }else{
            relativeLeftArrow.setVisibility(View.INVISIBLE);
        }

        getUsers();
    }

    @OnClick(R.id.relativeRightArrow)
    void getNextDate() {
        startCalendar.add(startCalendar.MONTH, 1);

        curMonth = startCalendar.get(Calendar.MONTH) + 1;
        curYear = startCalendar.get(Calendar.YEAR);

        String sdate = sdf1.format(startCalendar.getTime());
        tvMonthYear.setText(sdate + ", " + curYear);

        Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_MONTH, curMonth);
        Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_YEAR, curYear);

        if(startCalendar.before(calendar)){
            relativeRightArrow.setVisibility(View.VISIBLE);
            relativeLeftArrow.setVisibility(View.VISIBLE);
        }else{
            relativeRightArrow.setVisibility(View.INVISIBLE);
        }

        getUsers();
    }


    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_MONTH, month + 1);
            Util.WriteSharePrefrenceForInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_YEAR, year);

            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);

            curMonth = month + 1;
            curYear = year;

            String sdate = sdf1.format(startCalendar.getTime());
            tvMonthYear.setText(sdate + ", " + year);

            if(startCalendar.before(calendar)){
                relativeRightArrow.setVisibility(View.VISIBLE);
            }else{
                relativeRightArrow.setVisibility(View.INVISIBLE);
            }

            getUsers();
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
    public void selectAllCheckBoxCLick(View v) {
        CheckBox cb = (CheckBox) v;

        if (usersList != null && usersList.size() > 0) {
            for (int i = 0; i < usersList.size(); i++) {
                usersList.get(i).setChecked(cb.isChecked());
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void getUsers() {
        if (Util.isOnline(getActivity())) {

            int month = Util.ReadSharePrefrenceInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_MONTH);
            int year = Util.ReadSharePrefrenceInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_YEAR);

            if (year > 0) {
                if (progressDialog != null)
                    progressDialog.show();

                RestClient.getTeamWork().getPayslipGeneratedUsers(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID),
                        Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_ID),
                        month + "",
                        year + "",
                        new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {

                                try {
                                    JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                                    String status = jObj.optString("status");
                                    if (status.equals(Constant.InvalidToken)) {
                                        TeamWorkApplication.LogOutClear(getActivity());
                                        return;
                                    }

                                    Map<String, String> map = Util.readStatus(response);
                                    boolean isSuccess = map.get("status").equals("Success");

                                    if (isSuccess) {

                                        JSONArray jsonArray = jObj.optJSONArray("data");
                                        if (jsonArray != null && jsonArray.length() > 0) {
                                            //get Users
                                            JSONArray jsonUserList = jsonArray.optJSONArray(0);

                                            usersList = null;
                                            usersList = new ArrayList<User>();

                                            if (jsonUserList != null && jsonUserList.length() > 0) {

                                                for (int i = 0; i < jsonUserList.length(); i++) {
                                                    JSONObject jsonUser = jsonUserList.getJSONObject(i);
                                                    User user = new User();
                                                    user.setUserID(jsonUser.optInt("userId"));
                                                    user.setRoleId(jsonUser.optInt("roleId"));
                                                    user.setFirstName(jsonUser.optString("firstName"));
                                                    user.setLastName(jsonUser.optString("lastName"));
                                                    user.setDeviceID(jsonUser.optString("deviceid"));
                                                    user.setPicture(jsonUser.optString("picture"));
                                                    user.setEmailID(jsonUser.optString("emailID"));

                                                    usersList.add(user);
                                                }

                                            }

                                            //get PUBLISHED string
                                            JSONObject jsonPublished = jsonArray.optJSONObject(1);
                                            if (jsonPublished != null) {
                                                String published = jsonPublished.optString("Published");
                                                if (published != null && published.trim().length() > 0) {
                                                    if (published.contains(",")) {
                                                        String[] arr = published.split(",");
                                                        for (int i = 0; i < usersList.size(); i++) {
                                                            for (int j = 0; j < arr.length; j++) {
                                                                if (usersList.get(i).getUserID() == Integer.parseInt(arr[j])) {
                                                                    usersList.get(i).setPublished(true);
                                                                    break;
                                                                }
                                                            }
                                                        }

                                                    } else {
                                                        for (int i = 0; i < usersList.size(); i++) {
                                                            if (usersList.get(i).getUserID() == Integer.parseInt(published)) {
                                                                usersList.get(i).setPublished(true);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            //set in adapter
                                            if (usersList != null && usersList.size() > 0) {
                                                listUsers.setVisibility(View.VISIBLE);
                                                rl_save.setVisibility(View.VISIBLE);
                                                relativeSelectAll.setVisibility(View.VISIBLE);

                                                adapter = new PaySlipUserAdapter(getActivity(), usersList, 2);
                                                listUsers.setAdapter(adapter);
                                            } else {
                                                listUsers.setVisibility(View.GONE);
                                                relativeSelectAll.setVisibility(View.GONE);
                                                Toast.makeText(getActivity(), "No users available.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {
                                        listUsers.setVisibility(View.GONE);
                                        relativeSelectAll.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                    if (progressDialog != null)
                                        if (progressDialog.isShowing()) progressDialog.dismiss();

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

            } else {
                Toast.makeText(getActivity(), "Please select month & year", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "" + Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rl_save)
    public void save(View view) {
        if (Util.isOnline(getActivity())) {

            int month = Util.ReadSharePrefrenceInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_MONTH);
            int year = Util.ReadSharePrefrenceInteger(getActivity(), Constant.SHRED_PR.KEY_PAYSLIP_YEAR);

            if(year > 0) {

                if (progressDialog != null)
                    progressDialog.show();

                //set user json
                StringBuilder usersArray = new StringBuilder();
                String userParameter = null;
                try {
                    if (adapter != null && adapter.getCount() > 0) {
                        List<User> userList = adapter.getUserList();
                        if (userList != null && userList.size() > 0) {
                            for (int i = 0; i < userList.size(); i++) {
                                User user = userList.get(i);
                                if (user.isChecked()) {
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
                    RestClient.getTeamWork().publishPayslip(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID),
                            Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_ID),
                            userParameter.toString(),
                            month +"",
                            year + "",
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
                    Toast.makeText(getActivity(), "User payslip will be send to users via email shortly", Toast.LENGTH_SHORT).show();
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
