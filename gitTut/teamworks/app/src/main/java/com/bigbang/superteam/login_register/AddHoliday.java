package com.bigbang.superteam.login_register;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.model.Holidays;
import com.bigbang.superteam.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;

public class AddHoliday extends BaseActivity {

    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.etName)
    EditText etName;
    @InjectView(R.id.tvDate)
    TextView tvDate;
    @InjectView(R.id.rlDelete)
    RelativeLayout rlDelete;

    Calendar calendar;
    int curMonth, curYear, curDate;
    int flagAdd = 1;
    int pos = -1;
    String name = "", date = "";
    String holidayId = "0";
    String localId = "0";

    //    ArrayList<HashMap<String, String>> localListHolidays = new ArrayList<HashMap<String, String>>();
    ArrayList<Holidays> arr_holi = new ArrayList<>();
    HolidaysDAO holidaysDAO;

    private String blockCharacterSet = "123456789~#^|$%&*!()-=!@:;";

//    private InputFilter filter = new InputFilter() {
//
//        @Override
//        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//
//            if (source != null && blockCharacterSet.contains(("" + source))) {
//                return "";
//            }
//            return null;
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_holiday);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            flagAdd = extras.getInt("add");
            arr_holi = extras.getParcelableArrayList("holi");

            if (flagAdd == 0) {
                pos = extras.getInt("pos");
            }
        }
        init();

        if (flagAdd == 0) {
            tvTitle.setText(getResources().getString(R.string.update_holiday));
            rlDelete.setVisibility(View.VISIBLE);
            etName.setText("" + arr_holi.get(pos).holidayName);
            holidayId = arr_holi.get(pos).holidayId;
            localId = arr_holi.get(pos).localId;

            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM, yyyy");
//                2016/12/25 00:00:00
                tvDate.setText("" + sdf2.format(sdf1.parse(arr_holi.get(pos).holidayDate)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            tvTitle.setText(getResources().getString(R.string.add_holiday));
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


    @OnClick(R.id.rlSave)
    @SuppressWarnings("unused")
    public void Save(View view) {

        hideKeyboard();
        String str_holiday_name = etName.getText().toString().trim().toLowerCase();
        String str_holiday_date = tvDate.getText().toString().trim().toLowerCase();
        String id = holidayId;

        if (str_holiday_name.equals("")) {
            toast("Please enter holiday name");
            return;
        }

        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM, yyyy");
            str_holiday_date = sdf1.format(sdf2.parse(str_holiday_date));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (str_holiday_date.length() > 0) {
            try {
                Calendar currentCal = Calendar.getInstance();
                Calendar calendarHoliday = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("dd MMMM, yyyy");
                calendarHoliday.setTime(format.parse(tvDate.getText().toString().trim().toLowerCase()));

                if (calendarHoliday.get(Calendar.YEAR) >= currentCal.get(Calendar.YEAR)) {
                    boolean flag = true;
                    for (int i = 0; i < arr_holi.size(); i++) {
                        if (i != pos) {
                            if (arr_holi.get(i).holidayDate.toLowerCase().toString().trim().equals(str_holiday_date.trim())) {
                                flag = false;
                            }
                        }
                    }
                    if (flag) {

                        String date = "" + tvDate.getText().toString();
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM, yyyy");
                        date = sdf1.format(sdf2.parse(date));

                        HashMap<String, String> map = new HashMap<>();
                        Holidays holidays = new Holidays();
                        map.put("holiday_name", "" + etName.getText().toString());
                        map.put("holiday_date", "" + date);
                        map.put("selected", "1");

                        holidays.holidayName = etName.getText().toString();
                        holidays.holidayDate = date;
                        holidays.holidayId = id;
                        holidays.localId = localId;

                        long row_id = 0;
                        Intent returnIntent = new Intent();
                        if (flagAdd == 1) {
                            returnIntent.putExtra("holiday", holidays);
                            returnIntent.putExtra("action", "add");
                        } else {
                            returnIntent.putExtra("result", pos);
                            returnIntent.putExtra("holiday", holidays);
                            returnIntent.putExtra("action", "update");
                        }

                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();

                        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.DuplicateHoliday), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.PleaseEnterValidHoliday), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
//                e.printStackTrace();
                Log.e("", "*****ERoor in save and upate: " + e);
            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.PleaseEnterHoliday), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rlDelete)
    @SuppressWarnings("unused")
    public void Delete(View view) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to delete this holiday?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                try {
//                    holidaysDAO.delete(Integer.parseInt(localId));
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", pos);
                    returnIntent.putExtra("action", "del");
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);


                } catch (Exception e) {
                    Toast.makeText(AddHoliday.this, "Please try again", Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void init() {

//        etName.setFilters(new InputFilter[]{filter});

        holidaysDAO = new HolidaysDAO(AddHoliday.this);
        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        try {
            calendar = Calendar.getInstance();
            curMonth = calendar.get(calendar.MONTH);
            curYear = calendar.get(calendar.YEAR);
            curDate = calendar.get(calendar.DAY_OF_MONTH);

            String date = "" + curDate + "/" + (curMonth + 1) + "/" + curYear;
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM, yyyy");
            date = sdf2.format(sdf1.parse(date));
            tvDate.setText("" + date);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        localListHolidays.clear();
//        try {
//            JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_COMPANY_LEAVES));
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
//                HashMap<String, String> map = new HashMap<>();
//                map.put("holiday_name", "" + jsonObject1.optString("holiday_name"));
//                map.put("holiday_date", "" + jsonObject1.optString("holiday_date"));
//                map.put("selected", "1");
//                localListHolidays.add(map);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Collections.sort(localListHolidays, new CustomComparator());

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    calendar = Calendar.getInstance();
                    curMonth = calendar.get(calendar.MONTH);
                    curYear = calendar.get(calendar.YEAR);
                    curDate = calendar.get(calendar.DAY_OF_MONTH);

                    String date = "" + tvDate.getText().toString();
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM, yyyy");
                    date = sdf1.format(sdf2.parse(date));

                    String[] dates = date.split("/");
                    calendar.set(Calendar.YEAR, Integer.parseInt(dates[2]));
                    calendar.set(Calendar.MONTH, Integer.parseInt(dates[1]) - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DatePickerDialog dialog = new DatePickerDialog(AddHoliday.this, DatePickerDialog.THEME_HOLO_LIGHT, mytoDateListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DATE));
                //dialog.getDatePicker().setMinDate(new Date().getTime());
                dialog.show();
            }
        });
    }

    public class CustomComparator implements Comparator<HashMap<String, String>> {
        @Override
        public int compare(HashMap<String, String> hashMap1, HashMap<String, String> hashMap2) {

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                //holiday_date
                Date d1 = sdf.parse(hashMap1.get("holiday_date"));
                Date d2 = sdf.parse(hashMap2.get("holiday_date"));
                if (d1.before(d2)) return -1;
                if (d2.before(d1)) return 1;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                String name1 = hashMap1.get("holiday_name");
                String name2 = hashMap2.get("holiday_name");
                return name1.compareTo(name2);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }
    }

    private DatePickerDialog.OnDateSetListener mytoDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            try {
                String date = "" + day + "/" + (month + 1) + "/" + year;
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM, yyyy");
                tvDate.setText("" + sdf2.format(sdf1.parse(date)));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
}
