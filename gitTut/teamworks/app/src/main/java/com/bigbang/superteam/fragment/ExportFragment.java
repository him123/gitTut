package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
 * Created by USER 8 on 02-Nov-15.
 */
public class ExportFragment extends Fragment {

    @InjectView(R.id.spinnerReportType)
    Spinner spinnerReportType;
    @InjectView(R.id.etEmail)
    EditText etEmail;
    @InjectView(R.id.rlSave)
    RelativeLayout rlSave;
    @InjectView(R.id.rlDate)
    RelativeLayout rlDate;
    @InjectView(R.id.tvDate)
    TextView tvDate;
    @InjectView(R.id.relativeLeftArrow)
    RelativeLayout relativeLeftArrow;
    @InjectView(R.id.relativeRightArrow)
    RelativeLayout relativeRightArrow;

    String email;

    Activity activity;

    Calendar calendar, startCalendar;
    int curMonth = 0, curYear=0;
    SimpleDateFormat sdf1 = new SimpleDateFormat("MMM");

    String selected_report_type = "";

    public static Fragment newInstance() {
        ExportFragment fragment = new ExportFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_export, container, false);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

        spinnerReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_report_type = spinnerReportType.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void init() {
        etEmail.setText(read(Constant.SHRED_PR.KEY_EMAIL));

        startCalendar = Calendar.getInstance();
        calendar = Calendar.getInstance();
        curMonth = calendar.get(calendar.MONTH) + 1;
        curYear = calendar.get(calendar.YEAR);

        startCalendar.set(Calendar.YEAR, curYear);
        startCalendar.set(Calendar.MONTH, curMonth-1);

        String sdate = sdf1.format(startCalendar.getTime());
        tvDate.setText(sdate + ", " + curYear);

        relativeRightArrow.setVisibility(View.INVISIBLE);
    }

    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);

            curMonth = month + 1;
            curYear = year;

            String sdate = sdf1.format(startCalendar.getTime());
            tvDate.setText(sdate + ", " + year);

            if(startCalendar.before(calendar)){
                relativeRightArrow.setVisibility(View.VISIBLE);
            }else{
                relativeRightArrow.setVisibility(View.INVISIBLE);
            }
        }
    };

    @OnClick(R.id.tvDate)
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
        tvDate.setText(sdate + ", " + curYear);

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
        tvDate.setText(sdate + ", " + curYear);

        if(startCalendar.before(calendar)){
            relativeRightArrow.setVisibility(View.VISIBLE);
            relativeLeftArrow.setVisibility(View.VISIBLE);
        }else{
            relativeRightArrow.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.rlSave)
    @SuppressWarnings("unused")
    public void exportData(View view) {
        if (isValidate()) {
            if (Util.isOnline(activity)) {
                email = etEmail.getText().toString().trim();
                if(selected_report_type.equalsIgnoreCase("Expense Report")){
                    generateExpenseReportCall();
                }else {
                    new ExportData().execute();
                }
                toast(selected_report_type + " " + getString(R.string.err_sent_report));
            } else {
                toast(Constant.network_error);
            }
        }
    }

    public boolean isValidate() {
        if (!getText(etEmail).isEmpty()) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(getText(etEmail)).matches()) {
                toast(R.string.err_enter_valid_email);
                return false;
            }
        }

        if (isEmpty(getText(etEmail))) {
            toast(R.string.err_email);
            return false;
        }

        if (tvDate.getText().toString().equals("")) {
            toast(R.string.err_date1);
            return false;
        }
        return true;
    }

    private void generateExpenseReportCall(){
        RestClient.getCommonService().generateExpenseReport(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID),
                Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_ID),
                Constant.AppName,
                email,
                curMonth + "",
                curYear + "",
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response1) {

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
    }

    class ExportData extends AsyncTask<Void, String, String> {

        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(5);
            ArrayList<String> list = new ArrayList<String>();

            JSONArray jsArray = new JSONArray(list);

            params1.add(new BasicNameValuePair("Month", "" + curMonth));
            params1.add(new BasicNameValuePair("Year", "" + curYear));
            params1.add(new BasicNameValuePair("ReportType", "" + selected_report_type));
            params1.add(new BasicNameValuePair("Timezone", "" + ""));
            params1.add(new BasicNameValuePair("Members", ""));
            params1.add(new BasicNameValuePair("EmailId", "" + email));

            response = Util.makeServiceCall(Constant.URL + "generateReports", 1, params1, activity);
            return response;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(activity, key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(activity, key);
    }

    protected void toast(int resId) {
        toast(activity.getResources().getText(resId));
    }

    protected void toast(CharSequence text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    protected boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    protected String getText(EditText eTxt) {
        return eTxt == null ? "" : eTxt.getText().toString().trim();
    }
}
