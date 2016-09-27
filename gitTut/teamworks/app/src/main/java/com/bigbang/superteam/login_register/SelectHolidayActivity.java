package com.bigbang.superteam.login_register;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.util.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class SelectHolidayActivity extends BaseActivity {

    RelativeLayout relativeDone, relativeBack, rl_add, rl_add_leave, rl_cancel, rl_ok;
    ListView lst;
    EditText et_search;
    TextView tvDate;

    public static final int SELECT_DATE = 105;

    Calendar calendar;
    int curMonth, curYear, curDate;
    boolean flagAdd = true;
    int updated_row_id = 0;

    ArrayList<HashMap<String, String>> localListHolidays = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_holiday);

        init();

        try {
            JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_COMPANY_LEAVES));
            localListHolidays.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("holiday_name", "" + jsonObject1.optString("holiday_name"));
                map.put("holiday_date", "" + jsonObject1.optString("holiday_date"));
                map.put("selected", "1");
                localListHolidays.add(map);
            }
            lst.setAdapter(new CustomAdapter(localListHolidays));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        // TODO Auto-generated method stub
        relativeBack = (RelativeLayout) findViewById(R.id.relativeBack);
        relativeDone = (RelativeLayout) findViewById(R.id.relativeDone);
        rl_add = (RelativeLayout) findViewById(R.id.rl_add);
        rl_add_leave = (RelativeLayout) findViewById(R.id.rl_add_leave);
        rl_cancel = (RelativeLayout) findViewById(R.id.rl_cancel);
        rl_ok = (RelativeLayout) findViewById(R.id.rl_ok);
        lst = (ListView) findViewById(R.id.lst);
        et_search = (EditText) findViewById(R.id.et_search);
        tvDate = (TextView) findViewById(R.id.tv_date);

        calendar = Calendar.getInstance();
        curMonth = calendar.get(calendar.MONTH);
        curYear = calendar.get(calendar.YEAR);
        curDate = calendar.get(calendar.DAY_OF_MONTH);

        tvDate.setText("" + curDate + "/" + (curMonth + 1) + "/" + curYear);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    calendar = Calendar.getInstance();
                    curMonth = calendar.get(calendar.MONTH);
                    curYear = calendar.get(calendar.YEAR);
                    curDate = calendar.get(calendar.DAY_OF_MONTH);

                    Log.e("Date:", ">>" + tvDate.getText().toString());
                    String[] dates = tvDate.getText().toString().split("/");
                    calendar.set(Calendar.YEAR, Integer.parseInt(dates[2]));
                    calendar.set(Calendar.MONTH, Integer.parseInt(dates[1]) - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DatePickerDialog dialog = new DatePickerDialog(SelectHolidayActivity.this, DatePickerDialog.THEME_HOLO_LIGHT,mytoDateListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DATE));
                dialog.show();
            }
        });

        rl_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagAdd = true;
                et_search.setText("");

                calendar = Calendar.getInstance();
                curMonth = calendar.get(calendar.MONTH);
                curYear = calendar.get(calendar.YEAR);
                curDate = calendar.get(calendar.DAY_OF_MONTH);

                tvDate.setText("" + curDate + "/" + (curMonth + 1) + "/" + curYear);
                rl_add_leave.setVisibility(View.VISIBLE);
            }
        });

        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_add_leave.setVisibility(View.GONE);
            }
        });

        rl_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str_leave = et_search.getText().toString().trim().toLowerCase();
                String str_leave_date = tvDate.getText().toString().trim().toLowerCase();

                if (str_leave.length() > 0) {
                    if (flagAdd) {
                        boolean flag = true;
                        for (int i = 0; i < localListHolidays.size(); i++) {
                            if (localListHolidays.get(i).get("holiday_name").toLowerCase().equals(str_leave) || localListHolidays.get(i).get("holiday_date").toLowerCase().equals(str_leave_date)) {
                                flag = false;
                            }
                        }

                        if (flag) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("holiday_name", "" + et_search.getText().toString());
                            map.put("holiday_date", "" + tvDate.getText().toString());
                            map.put("selected", "1");
                            localListHolidays.add(map);
                            lst.setAdapter(new CustomAdapter(localListHolidays));
                            rl_add_leave.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.DuplicateHoliday), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("holiday_name", "" + et_search.getText().toString());
                        map.put("holiday_date", "" + tvDate.getText().toString());
                        map.put("selected", "1");
                        localListHolidays.remove(updated_row_id);
                        localListHolidays.add(updated_row_id, map);
                        lst.setAdapter(new CustomAdapter(localListHolidays));
                        rl_add_leave.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.PleaseEnterHoliday), Toast.LENGTH_SHORT).show();
                }

            }
        });

        relativeDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < localListHolidays.size(); i++) {
                    if (localListHolidays.get(i).get("selected").equals("1")) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("holiday_name", localListHolidays.get(i).get("holiday_name").toString());
                            jsonObject.put("holiday_date", localListHolidays.get(i).get("holiday_date").toString());
                            jsonArray.put(jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                write(Constant.SHRED_PR.KEY_COMPANY_LEAVES, jsonArray.toString());
                write(Constant.SHRED_PR.KEY_RELOAD, "1");
                finish();
            }
        });

        relativeBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }

    class CustomAdapter extends BaseAdapter {

        ArrayList<HashMap<String, String>> locallist;

        public CustomAdapter(ArrayList<HashMap<String, String>> locallist) {
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

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder holder;
            View view = convertView;

            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.select_holiday_listrow,
                        parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.relativeCheck = (RelativeLayout) view
                        .findViewById(R.id.relativeCheck);
                holder.btnCheck = (ImageButton) view.findViewById(R.id.check);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.name.setText(""
                    + locallist.get(position).get("holiday_name") + " : " + locallist.get(position).get("holiday_date"));

            if (locallist.get(position).get("selected").equals("0")) {
                holder.btnCheck.setBackgroundResource(R.drawable.unchacked);
            } else {
                holder.btnCheck.setBackgroundResource(R.drawable.chacked);
            }

            holder.relativeCheck.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (locallist.get(position).get("selected").equals("1")) {
                        holder.btnCheck
                                .setBackgroundResource(R.drawable.unchacked);
                        locallist.get(position).put("selected", "0");
                    } else {
                        holder.btnCheck
                                .setBackgroundResource(R.drawable.chacked);
                        locallist.get(position).put("selected", "1");
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        et_search.setText(locallist.get(position).get("holiday_name"));
                        tvDate.setText(locallist.get(position).get("holiday_date"));
                        flagAdd = false;
                        updated_row_id = position;
                        rl_add_leave.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            return view;
        }
    }

    static class ViewHolder {
        TextView name;
        RelativeLayout relativeCheck;
        ImageButton btnCheck;
    }

    private DatePickerDialog.OnDateSetListener mytoDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            tvDate.setText("" + day + "/" + (month + 1) + "/" + year);
        }
    };

}
