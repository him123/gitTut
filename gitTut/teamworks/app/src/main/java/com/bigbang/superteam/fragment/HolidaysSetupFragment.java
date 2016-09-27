package com.bigbang.superteam.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.login_register.AddHoliday;
import com.bigbang.superteam.login_register.CompanySetupActivity;
import com.bigbang.superteam.login_register.HolidaysDAO;
import com.bigbang.superteam.model.Holidays;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class HolidaysSetupFragment extends Fragment {

    static Activity context;
    static int FROM = 1;
    @InjectView(R.id.etSickLeave)
    EditText etSickLeave;
    @InjectView(R.id.etCasualLeave)
    EditText etCasualLeave;
    @InjectView(R.id.etPaidLeave)
    EditText etPaidLeave;
    @InjectView(R.id.etOptionalLeave)
    EditText etOptionalLeave;
    @InjectView(R.id.lv_holidays)
    ListView lvHolidays;
    @InjectView(R.id.tvHolidayList)
    TextView tvHolidayList;
    @InjectView(R.id.rl_next)
    RelativeLayout rl_next;
    @InjectView(R.id.rlAdd)
    RelativeLayout rlAdd;
    @InjectView(R.id.tvNext)
    TextView tvNext;
    @InjectView(R.id.btn_def_holidays)
    Button btn_def_holidays;
    TransparentProgressDialog progressDialog;
    ArrayList<Holidays> local_listHolidays = new ArrayList<Holidays>();
    String TAG = "LeaveSetupFragment";
    JSONArray passingJarray;

    HolidaysDAO holidaysDAO;

    public static HolidaysSetupFragment newInstance(int from) {
        FROM = from;
        HolidaysSetupFragment fragment = new HolidaysSetupFragment();
        return fragment;
    }

    private TransparentProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();

        View view = inflater.inflate(R.layout.fragment_holidays_setup, container, false);
        final Typeface mFont = Typeface.createFromAsset(context.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) view.getRootView();
        Util.setAppFont(mContainer, mFont);
        ButterKnife.inject(this, view);
        init();
        arr_holidays = holidaysDAO.getHolidays();
        setHolidays(arr_holidays);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (read(Constant.SHRED_PR.KEY_ROLE_ID).equals("3") || read(Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
            rl_next.setVisibility(View.GONE);
            rlAdd.setVisibility(View.GONE);
            btn_def_holidays.setVisibility(View.GONE);

            etSickLeave.setFocusableInTouchMode(false);
            etCasualLeave.setFocusableInTouchMode(false);
            etOptionalLeave.setFocusableInTouchMode(false);
            etPaidLeave.setFocusableInTouchMode(false);
        }

        if (FROM == Constant.FROM_DASHBOARD) {
            btn_def_holidays.setVisibility(View.GONE);
        }
    }

    private void init() {
        holidaysDAO = new HolidaysDAO(getActivity());
        lvHolidays.setDividerHeight(0);
        lvHolidays.setDivider(null);
        passingJarray = new JSONArray();
        progressDialog = new TransparentProgressDialog(context, R.drawable.progressdialog, false);
    }

    ArrayList<Holidays> arr_holidays = new ArrayList<>();
    Holidays holidays;

    private void setHolidays(ArrayList<Holidays> arr_holidays) {
        try {
            tvHolidayList.setText(getResources().getString(R.string.holiday_list) + " (" + arr_holidays.size() + ")");
            adapter = new CustomAdapter(arr_holidays);
            lvHolidays.setAdapter(adapter);
            passingJarray = new JSONArray(arr_holidays);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.rlAdd)
    @SuppressWarnings("unused")
    public void Update(View view) {
        Intent intent = new Intent(context, AddHoliday.class);
        intent.putExtra("add", 1);
        intent.putParcelableArrayListExtra("holi", arr_holidays);
        startActivityForResult(intent, 1);
        context.overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rl_next)
    @SuppressWarnings("unused")
    public void Next(View view) {

        if (Util.isOnline(getActivity())) {
            if (progressDialog != null)
                progressDialog.show();
//            arr_holidays = holidaysDAO.getHolidays();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < arr_holidays.size(); i++) {
                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("holidayName", arr_holidays.get(i).holidayName);
                    jsonObject.put("holidayDate", arr_holidays.get(i).holidayDate + " 00:00:00");
                    jsonObject.put("holidayId", arr_holidays.get(i).holidayId);

                    jsonArray.put(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Log.d("", "******** Passsing Json Array : " + jsonArray.toString());

            RestClient.getTeamWork().updateCompanyHolidays(jsonArray.toString(),
                    Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID),
                    Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_ID),
                    Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_TOKEN), new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {

                            try {
                                holidaysDAO.deleteAll();
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
                                    JSONArray holidaysJarr = jObj.getJSONArray("data");

                                    if (holidaysJarr != null) {
                                        for (int cnt = 0; cnt < holidaysJarr.length(); cnt++) {
                                            holidays = new Holidays();

                                            holidays.holidayName = holidaysJarr.getJSONObject(cnt).getString("holidayName");
                                            holidays.holidayDate = holidaysJarr.getJSONObject(cnt).getString("holidayDate");
                                            holidays.holidayId = holidaysJarr.getJSONObject(cnt).getString("holidayId");

                                            holidaysDAO.save(holidays);
                                        }
                                    }
                                    Toast.makeText(getActivity(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                    if (FROM == Constant.FROM_DASHBOARD) {
                                        context.finish();
                                        context.overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                                    } else {
                                        CompanySetupActivity.currentPosition++;
                                        CompanySetupActivity.pager.setCurrentItem(CompanySetupActivity.currentPosition);
                                    }
                                } else {
                                    Toast.makeText(getActivity(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
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
        } else {
            Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_def_holidays)
    @SuppressWarnings("unused")
    void loadDefaultHolidaysLit() {
        if (progressDialog != null)
            progressDialog.show();
        arr_holidays.clear();
        holidaysDAO.deleteAll();
        RestClient.getTeamWork().getDefaultHolidays(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {

                    Map<String, String> map = Util.readStatus(response);
                    boolean isSuccess = map.get("status").equals("1");

                    String json = Util.getString(response.getBody().in());
                    JSONObject jObj = new JSONObject(json);
                    JSONArray holidaysArr = jObj.getJSONArray("data");


                    for (int i = 0; i < holidaysArr.length(); i++) {
                        JSONObject jsonObject1 = holidaysArr.optJSONObject(i);

                        holidays = new Holidays();

                        holidays.holidayName = jsonObject1.getString("holidayName");
                        holidays.holidayDate = jsonObject1.getString("holidayDate").replaceAll("00:00:00", "").trim();
                        holidays.holidayId = jsonObject1.getString("holidayId");

                        arr_holidays.add(holidays);

                        holidaysDAO.save(holidays);
                    }
                    setHolidays(arr_holidays);

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

    protected String read(String key) {
        return Util.ReadSharePrefrence(context, key);
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(context, key, val);
    }

    protected boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    protected String getText(EditText eTxt) {
        return eTxt == null ? "" : eTxt.getText().toString().trim();
    }

    static class ViewHolder {
        @InjectView(R.id.tv_name)
        TextView tv_name;
        @InjectView(R.id.tvDate1)
        TextView tvDate1;
        @InjectView(R.id.tvDate2)
        TextView tvDate2;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(context.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

    CustomAdapter adapter;

    class CustomAdapter extends BaseAdapter {

        ArrayList<Holidays> locallist;

        public CustomAdapter(ArrayList<Holidays> locallist) {
            // TODO Auto-generated constructor stub
            this.locallist = locallist;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return locallist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        public void updateReceiptsList(ArrayList<Holidays> newlist) {
            this.locallist.clear();
            this.locallist.addAll(newlist);
            this.notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = convertView;
            ViewHolder holder;
            if (view == null) {
                view = context.getLayoutInflater().inflate(R.layout.holidays_list_row, null);
                holder = new ViewHolder(view);

                view.setTag(holder);
            } else
                holder = (ViewHolder) view.getTag();

            holder.tv_name.setText("" + locallist.get(position).holidayName);
            holder.tvDate1.setText("" + locallist.get(position).holidayDate);

            holder.tvDate1.setText("");
            holder.tvDate2.setText("");

            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("MMM");
                SimpleDateFormat sdf3 = new SimpleDateFormat("dd");

                String date = locallist.get(position).holidayDate;
                holder.tvDate1.setText("" + sdf2.format(sdf1.parse(date)).toUpperCase());
                holder.tvDate2.setText("" + sdf3.format(sdf1.parse(date)));

            } catch (Exception e) {
//                e.printStackTrace();
                Log.d("", "****** From adapter holidays: " + e);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String roleId = read(Constant.SHRED_PR.KEY_ROLE_ID);
                    if (Arrays.asList("1", "2").contains(roleId)) {
                        Intent intent = new Intent(context, AddHoliday.class);
                        intent.putExtra("add", 0);
                        intent.putExtra("pos", position);
                        intent.putParcelableArrayListExtra("holi", locallist);
                        startActivityForResult(intent, 1);
                        context.overridePendingTransition(R.anim.enter_from_left,
                                R.anim.hold_bottom);

//                        Intent intent = new Intent(context, AddHoliday.class);
//                        intent.putExtra("add", 1);
//                        intent.putParcelableArrayListExtra("holi", arr_holidays);
//                        startActivityForResult(intent, 1);
//                        context.overridePendingTransition(R.anim.enter_from_left,
//                                R.anim.hold_bottom);

                    }
                }
            });

            return view;
        }
    }

    public class CustomComparator implements Comparator<HashMap<String, String>> {
        @Override
        public int compare(HashMap<String, String> hashMap1, HashMap<String, String> hashMap2) {

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                //holidayDate
                Date d1 = sdf.parse(hashMap1.get("holidayDate"));
                Date d2 = sdf.parse(hashMap2.get("holidayDate"));
                if (d1.before(d2)) return -1;
                if (d2.before(d1)) return 1;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                String name1 = hashMap1.get("holidayName");
                String name2 = hashMap2.get("holidayName");
                return name1.compareTo(name2);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //some code
            String action = data.getStringExtra("action");
            if (action.equals("del")) {
                int row_id = data.getIntExtra("result", 0);
                arr_holidays.remove((row_id));
                adapter = new CustomAdapter(arr_holidays);
                lvHolidays.setAdapter(adapter);
            } else if (action.equals("add")) {
                Holidays holidays = data.getParcelableExtra("holiday");
                arr_holidays.add(holidays);
                adapter = new CustomAdapter(arr_holidays);
                lvHolidays.setAdapter(adapter);
            }
            if (action.equals("update")) {
                int row_id = data.getIntExtra("result", 0);
                Holidays holidays = data.getParcelableExtra("holiday");
                arr_holidays.set(row_id, holidays);
                adapter = new CustomAdapter(arr_holidays);
                lvHolidays.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tvHolidayList.setText(getResources().getString(R.string.holiday_list) + " (" + arr_holidays.size() + ")");
    }
}
