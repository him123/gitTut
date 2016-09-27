package com.bigbang.superteam.expenses;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.pulltorefresh.PullToRefreshBase;
import com.bigbang.superteam.pulltorefresh.PullToRefreshView;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
 * Created by User on 8/2/2016.
 */
public class ExpenseAdminOwnFragment extends Fragment {

    @InjectView(R.id.tvMonthYear)
    TextView tvMonthYear;
    @InjectView(R.id.lvExpenses)
    PullToRefreshView mPullRefreshListView;
    ListView lvExpenses;
    @InjectView(R.id.tvNoHistory)
    TextView tvNoHistory;
    @InjectView(R.id.relativeLeftArrow)
    RelativeLayout relativeLeftArrow;
    @InjectView(R.id.relativeRightArrow)
    RelativeLayout relativeRightArrow;

    Calendar calendar, startCalendar;
    int curMonth = 0, curYear=0;
    SimpleDateFormat sdf1 = new SimpleDateFormat("MMM");

    Activity activity;

    ExpenseListAdapter adapter;

    TransparentProgressDialog progressDialog;

    public static Fragment newInstance() {
        ExpenseAdminOwnFragment fragment = new ExpenseAdminOwnFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity=getActivity();
        View v = inflater.inflate(R.layout.fragment_expenses, container, false);
        ButterKnife.inject(this, v);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);

        init();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void init(){
        lvExpenses = mPullRefreshListView.getRefreshableView();

        progressDialog = new TransparentProgressDialog(getActivity(), R.drawable.progressdialog, false);

        startCalendar = Calendar.getInstance();
        calendar = Calendar.getInstance();
        curMonth = calendar.get(calendar.MONTH) + 1;
        curYear = calendar.get(calendar.YEAR);

        startCalendar.set(Calendar.YEAR, curYear);
        startCalendar.set(Calendar.MONTH, curMonth-1);

        String sdate = sdf1.format(startCalendar.getTime());
        tvMonthYear.setText(sdate + ", " + curYear);

        relativeRightArrow.setVisibility(View.INVISIBLE);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserExpenses();
            }
        });

        getUserExpenses();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(curMonth > 0 && curYear > 0)
            getUserExpenses();
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

        if(startCalendar.before(calendar)){
            relativeLeftArrow.setVisibility(View.VISIBLE);
        }else{
            relativeLeftArrow.setVisibility(View.INVISIBLE);
        }

        getUserExpenses();
    }

    @OnClick(R.id.relativeRightArrow)
    void getNextDate() {
        startCalendar.add(startCalendar.MONTH, 1);

        curMonth = startCalendar.get(Calendar.MONTH) + 1;
        curYear = startCalendar.get(Calendar.YEAR);

        String sdate = sdf1.format(startCalendar.getTime());
        tvMonthYear.setText(sdate + ", " + curYear);

        if(startCalendar.before(calendar)){
            relativeRightArrow.setVisibility(View.VISIBLE);
            relativeLeftArrow.setVisibility(View.VISIBLE);
        }else{
            relativeRightArrow.setVisibility(View.INVISIBLE);
        }

        getUserExpenses();
    }


    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

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

            getUserExpenses();
        }
    };

    private void getUserExpenses(){

        if (Util.isOnline(getActivity())) {

            if (progressDialog != null)
                progressDialog.show();

            RestClient.getCommonService().getUserExpenses(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID),
                    Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_ID),
                    Constant.AppName,
                    curMonth + "",
                    curYear + "",
                    new Callback<ArrayList<ExpenseType>>() {
                        @Override
                        public void success(ArrayList<ExpenseType> list, Response response) {
                            try {
                                Map<String, String> map = Util.readStatus(response);
                                boolean isSuccess = map.get("status").equals("Success");
                                String json = Util.getString(response.getBody().in());
                                JSONObject jObj = new JSONObject(json);

                                if (isSuccess) {
                                    if (list!= null && list.size() > 0) {
                                        lvExpenses.setVisibility(View.VISIBLE);
                                        tvNoHistory.setVisibility(View.GONE);

                                        //set in adapter
                                        adapter = new ExpenseListAdapter(getActivity(), list, false);
                                        lvExpenses.setAdapter(adapter);
                                    }else {
                                        lvExpenses.setVisibility(View.GONE);
                                        tvNoHistory.setVisibility(View.VISIBLE);
                                        tvNoHistory.setText(jObj.getString("message") +"");
                                        //Toast.makeText(getActivity(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(),jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                mPullRefreshListView.onRefreshComplete();

                                if (progressDialog != null)
                                    if (progressDialog.isShowing()) progressDialog.dismiss();

                            } catch (Exception e) {
                                e.printStackTrace();
                                mPullRefreshListView.onRefreshComplete();
                                if (progressDialog != null)
                                    if (progressDialog.isShowing()) progressDialog.dismiss();

                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            error.printStackTrace();
                            mPullRefreshListView.onRefreshComplete();
                            if (progressDialog != null)
                                if (progressDialog.isShowing()) progressDialog.dismiss();
                        }
                    });

        }else {
            mPullRefreshListView.onRefreshComplete();
            Toast.makeText(getActivity(), "" + Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rlCreate)
    @SuppressWarnings("unused")
    public void Create(View view) {
        startActivity(new Intent(activity, AddExpensesActivity.class));
        activity.overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

}
