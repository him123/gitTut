package com.bigbang.superteam.fragment;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.AttendanceHistoryAdminAdapter;
import com.bigbang.superteam.dataObjs.AttendanceHistoryModel;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * Created by USER 7 on 7/8/2015.
 */
public class AttendanceHistoryFragment extends Fragment {

    public static Context mContext;

    @InjectView(R.id.spinnerDay)
    Spinner spinnerDay;
    @InjectView(R.id.spinnerMonth)
    Spinner spinnerMonth;
    @InjectView(R.id.spinnerYear)
    Spinner spinnerYear;
    @InjectView(R.id.list_attendance_history)
    ListView lvAttendanceHis;
    @InjectView(R.id.tvNoHistory)
    TextView tvNoHistory;
    @InjectView(R.id.progressBar)
    ProgressBar pBar;

    AttendanceHistoryAdminAdapter atndHistoryAdapter;
    public static ArrayList<AttendanceHistoryModel> listAttendHistory;
    AttendanceHistoryModel attendanceHistoryModel;

    SQLiteHelper helper;
    String TAG = "AttendanceList Activity ";
    public static SQLiteDatabase db = null;
    boolean isAPICalling = false;
    ArrayList<String> monthArray;
    ArrayList<String> yearArray;
    ArrayList<String> dateArray;
    String[] monthName;

    private TransparentProgressDialog pd;

    public static AttendanceHistoryFragment newInstance(String string, Context ctx) {
        mContext = ctx;
        AttendanceHistoryFragment f = new AttendanceHistoryFragment();
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     //   String message = getArguments().getString(EXTRA_MESSAGE);
        View v;
        v = new View(mContext);
        v = inflater.inflate(R.layout.activity_admin_attendance_history, container, false);
        ButterKnife.inject(this, v);

        pd = new TransparentProgressDialog(getActivity(), R.drawable.progressdialog,false);
        monthArray = new ArrayList<String>();
        yearArray = new ArrayList<String>();
        dateArray = new ArrayList<String>();

        calculateSpinnerDates();
        Init();

        return v;

    }

    private void Init() {
        helper = new SQLiteHelper(getActivity(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        listAttendHistory = new ArrayList<AttendanceHistoryModel>();
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);

        monthName = getResources().getStringArray(R.array.month_array);
        ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, dateArray);
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapterDate);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, monthArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, yearArray);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapterYear);

        spinnerMonth.setSelection(calendar.get(calendar.MONTH));
        int curDate = calendar.get(calendar.DAY_OF_MONTH);
        Log.d(TAG, "Date of month is:- " + curDate);

        spinnerDay.setSelection(calendar.get(Calendar.DATE) - 1);
        String[] years = getResources().getStringArray(R.array.year_array);
        for (int i = 0; i < years.length; i++) {
            if (years[i].equals("" + currentYear)) {
                spinnerYear.setSelection(i);
            }
        }

    }
    @OnItemSelected(R.id.spinnerDay)
    void onItemDaySelected(int position) {
        // TODO ...
        if (Util.isOnline(getActivity().getApplicationContext())) {
            if (!isAPICalling)
                new getAllUserRecords().execute();
        } else {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            String date = (String) spinnerYear.getSelectedItem()+ "_" + (spinnerMonth.getSelectedItemPosition() + 1) + "_" +spinnerDay.getSelectedItem();
            reload(Util.ReadFile(getActivity().getCacheDir()+"/AttendanceHistory", Constant.AttendanceFile+"_"+date+".txt"));

        }
    }

    @OnItemSelected(R.id.spinnerMonth)
    void onItemSelectedMonth(int position) {
        // TODO ...
        if (!isAPICalling) {
            setSpinnerDay();
            if (Util.isOnline(getActivity().getApplicationContext())) {

            } else {
                //Toast.makeText(getActivity().getApplicationContext(), Constant.network_error, Toast.LENGTH_SHORT).show();
                String date = (String) spinnerYear.getSelectedItem()+ "_" + (spinnerMonth.getSelectedItemPosition() + 1) + "_" +spinnerDay.getSelectedItem();
                reload(Util.ReadFile(getActivity().getCacheDir()+"/AttendanceHistory", Constant.AttendanceFile+"_"+date+".txt"));
            }
        }
    }

    @OnItemSelected(R.id.spinnerYear)
    void onItemSelectedYear(int position) {
        // TODO ...
        if (!isAPICalling) {
            setSpinnerMonth();
            if (Util.isOnline(getActivity().getApplicationContext())) {

                new getAllUserRecords().execute();
            } else {
               // Toast.makeText(getActivity().getApplicationContext(), Constant.network_error, Toast.LENGTH_SHORT).show();
                String date = (String) spinnerYear.getSelectedItem()+ "_" + (spinnerMonth.getSelectedItemPosition() + 1) + "_" +spinnerDay.getSelectedItem();
                reload(Util.ReadFile(getActivity().getCacheDir()+"/AttendanceHistory", Constant.AttendanceFile+"_"+date+".txt"));
            }
        }
    }

  /*  @OnClick(R.id.btnExportExcel)
    void createCSV() {
        int result = Util.createCSVFileForAttendance(listAttendHistory);
        if(result==1)
        {
            Toast.makeText(getActivity(),"Your file has been created successfully",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(),"Some error occurred while creating file",Toast.LENGTH_SHORT).show();

        }
    }*/

    @OnClick(R.id.btnExportPdf)
    void createPdf(){
//        int result =Util.convertToPDF(listAttendHistory);
//        if(result==1)
//        {
//            Toast.makeText(getActivity(),"Your file has been created successfully",Toast.LENGTH_LONG).show();
//        }else{
//            Toast.makeText(getActivity(),"Some error occurred while creating file",Toast.LENGTH_LONG).show();
//
//        }
    }

    private void calculateSpinnerDates() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int curMonth = calendar.get(calendar.MONTH);
        int curYear = calendar.get(calendar.YEAR);
        int curDate = calendar.get(calendar.DAY_OF_MONTH);
        int[] years1 = getResources().getIntArray(R.array.year_array_int);
        monthName = getResources().getStringArray(R.array.month_array);

        for (int i = 0; i < 12; i++) {
            if ((curMonth) >= i) {

                // monthArray.add("" + i);
                monthArray.add("" + monthName[i]);

            } else {
                break;
            }
        }
        for (int j = 1; j <= years1.length; j++) {
            if (curYear >= (years1[j])) {
                yearArray.add("" + years1[j]);
            } else {
                break;
            }
        }
        for (int k = 1; k <= 31; k++) {
            if (curDate >= k) {
                dateArray.add("" + k);
            } else {
                break;
            }
        }
    }

    private void setSpinnerMonth() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int curYear = calendar.get(calendar.YEAR);
        int curMonth = calendar.get(calendar.MONTH);

        if (curYear > Integer.parseInt("" + spinnerYear.getSelectedItem())) {
            monthArray.clear();
            for (int i = 0; i < 12; i++) {
                // monthArray.add("" + i);
                monthArray.add("" + monthName[i]);
            }

        } else if (curYear == Integer.parseInt("" + spinnerYear.getSelectedItem())) {
            monthArray.clear();
            for (int i = 0; i < 12; i++) {
                if ((curMonth) >= i) {
                    //monthArray.add("" + i);
                    monthArray.add("" + monthName[i]);
                } else {
                    break;
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, monthArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);
        spinnerMonth.setSelection(calendar.get(calendar.MONTH));

        setSpinnerDay();
    }

    private void setSpinnerDay() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int curYear = calendar.get(calendar.YEAR);
        int curMonth = calendar.get(calendar.MONTH);
        int curDate = calendar.get(calendar.DAY_OF_MONTH);
        int selectedMonth = Integer.parseInt("" + spinnerMonth.getSelectedItemId());

        if (curYear > Integer.parseInt("" + spinnerYear.getSelectedItem())) {
            monthArray.clear();
            for (int i = 0; i < 12; i++) {
                // monthArray.add("" + i);
                monthArray.add("" + monthName[i]);
            }
            setDates(selectedMonth + 1);
        } else {
            if ((curMonth) > selectedMonth) {
                setDates(selectedMonth + 1);
            } else {
                dateArray.clear();
                for (int k = 1; k <= 31; k++) {
                    if (curDate >= k) {
                        dateArray.add("" + k);
                    } else {
                        break;
                    }
                }
            }
        }

        ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, dateArray);
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapterDate);
        spinnerDay.setSelection(calendar.get(Calendar.DATE) - 1);

    }

    private void setDates(int selectedMonth) {
        if (selectedMonth == 1 || selectedMonth == 3 || selectedMonth == 5 || selectedMonth == 7 || selectedMonth == 8 || selectedMonth == 10 || selectedMonth == 12) {
            dateArray.clear();
            for (int i = 1; i <= 31; i++) {
                dateArray.add("" + i);
            }
        } else if (selectedMonth == 2) {
            dateArray.clear();
            for (int i = 1; i <= 28; i++) {
                dateArray.add("" + i);
            }
        } else {
            dateArray.clear();
            for (int i = 1; i <= 30; i++) {
                dateArray.add("" + i);
            }
        }
    }

    class getAllUserRecords extends AsyncTask<Void, String, String> {

        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(pd!=null){
                pd.show();
            }
            isAPICalling = true;
        }


        @Override
        protected String doInBackground(Void... params) {

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            String date = (String) spinnerYear.getSelectedItem()+ "/" + (spinnerMonth.getSelectedItemPosition() + 1) + "/" +spinnerDay.getSelectedItem();
            params1.add(new BasicNameValuePair("Date", "" + date));
            params1.add(new BasicNameValuePair("AdminID", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID)));

            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL
                    + "getAllUserAttendanceHistory", 1, params1,getActivity());

            Log.e("result:- ", ">>" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            String date = (String) spinnerYear.getSelectedItem()+ "_" + (spinnerMonth.getSelectedItemPosition() + 1) + "_" +spinnerDay.getSelectedItem();
            Util.WriteFile(getActivity().getCacheDir()+"/AttendanceHistory", Constant.AttendanceFile+"_"+date+".txt", result);
            reload(result);
        }
    }
    private void reload(String result1) {

        isAPICalling = false;
        listAttendHistory.clear();

        try {
            JSONObject jObj = new JSONObject(result1);
            String status = jObj.optString("status");
            if(pd!=null){
                pd.dismiss();
            }

            if (status.equals("Success")) {
                JSONArray jArry = jObj.getJSONArray("data");
                for (int i = 0; i < jArry.length(); i++) {
                    JSONObject jsonObj = jArry.getJSONObject(i);
                    attendanceHistoryModel = new AttendanceHistoryModel();

                    attendanceHistoryModel.setUserId("" + jsonObj.optInt("userId"));
                   attendanceHistoryModel.setNewTimeIn(""+jsonObj.optString("newTimeIn"));
                    attendanceHistoryModel.setNewTimeOut(""+jsonObj.optString("newTimeOut"));
                    attendanceHistoryModel.setTimeIN("" + jsonObj.optString("timeIn"));
                   // attendanceHistoryModel.setApprovalState(""+jsonObj.optString("approved"));
                    attendanceHistoryModel.setCheckInApprovalState(jsonObj.optBoolean("checkInApproved"));
                    attendanceHistoryModel.setCheckOutApprovalState(jsonObj.optBoolean("checkOutApproved"));
                    attendanceHistoryModel.setTimeOut("" + jsonObj.optString("timeOut"));
                    attendanceHistoryModel.setImageUrl("" + jsonObj.optString("picture"));
                    attendanceHistoryModel.setIsPresent(jsonObj.optBoolean("present"));
                    attendanceHistoryModel.setIsManualAttendance("" + jsonObj.optString("manualAttendance"));
                    attendanceHistoryModel.setRequestDate("" + jsonObj.optString("strDate"));
                    attendanceHistoryModel.setUserName("" + jsonObj.optString("firstName")+" "+jsonObj.optString("lastName"));
                   attendanceHistoryModel.setAttendanceID(""+jsonObj.optString("attendanceId"));

                    listAttendHistory.add(attendanceHistoryModel);
                }

            } else {
                Toast.makeText(getActivity(), "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            if(pd!=null){
                pd.dismiss();
            }
        }
        if (listAttendHistory.size() <= 0) {
            tvNoHistory.setVisibility(View.VISIBLE);
        } else {
            tvNoHistory.setVisibility(View.GONE);
        }
        atndHistoryAdapter = new AttendanceHistoryAdminAdapter(getActivity(), listAttendHistory);
        lvAttendanceHis.setAdapter(atndHistoryAdapter);
    }

}
