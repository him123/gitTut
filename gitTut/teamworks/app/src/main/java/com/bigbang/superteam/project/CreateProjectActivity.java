package com.bigbang.superteam.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.OfflineWork;
import com.bigbang.superteam.dataObjs.Project;
import com.bigbang.superteam.dataObjs.User;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.workitem.HandleResponseWorkItems;
import com.bigbang.superteam.workitem.SelectAssigneeCC;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 4/16/2015.
 */
public class CreateProjectActivity extends BaseActivity {

    public static final int END_DATE = 101;
    public static final int START_DATE = 102;
    public static final int START_TIME = 103;
    public static final int END_TIME = 104;
    private static final int SELECT_FILE = 105;
    private static final int REQUEST_CAMERA = 106;
    private static final int CROP_FROM_CAMERA = 107;
    public static int NEW = 1;
    public static int EDIT = 2;
    public static int MODE = NEW;
    public static Boolean Active = false;
    SQLiteDatabase db = null;

    @InjectView(R.id.et_projectName)
    EditText et_projectName;
    @InjectView(R.id.et_projectDescription)
    EditText et_projectDescription;
//    @InjectView(R.id.spinner_priority)
//    Spinner spinner_priority;
    @InjectView(R.id.spinner_owner)
    Spinner spinner_owner;
    @InjectView(R.id.iv_selectedImage)
    ImageView ivSelectedImage;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;

    @InjectView(R.id.rlHighPriority)
    RelativeLayout rlHighPriority;
    @InjectView(R.id.rlMediumPriority)
    RelativeLayout rlMediumPriority;
    @InjectView(R.id.rlLowPriority)
    RelativeLayout rlLowPriority;
    @InjectView(R.id.tvHighPriority)
    TextView tvHighPriority;
    @InjectView(R.id.tvMediumPriority)
    TextView tvMediumPriority;
    @InjectView(R.id.tvLowPriority)
    TextView tvLowPriority;

    @InjectView(R.id.tvStartingFrom) TextView tvStartingFrom;
    @InjectView(R.id.tvEndsBy) TextView tvEndsBy;

    Calendar calendar;
    Calendar startCalendar, endCalendar;
//    boolean assignMembersSelected[];
//    boolean ccMembersSelected[];
    String membersString[];
    Random random;
    Uri globalUri = null;
    String camera_pathname = "";
    String imagePath = "";
    Boolean imageFlag = false;
    Cursor cursor = null;
    int noofMembers = 0;
    Project project;
    ArrayList<User> userArrayList = new ArrayList<>();
    private ArrayList<User> listAssignne = new ArrayList<>();
    private ArrayList<User> listCC = new ArrayList<>();
    @InjectView(R.id.tv_assignTo)
    TextView tvAssigneed;
    @InjectView(R.id.tv_ccWorkitem)
    TextView tvCC;
    TransparentProgressDialog progressDialog;
    boolean flagST,flagET;
    String strPriority = Constant.MEDIUM;

    TimePickerDialog.OnTimeSetListener myToTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker timePicker, int i, int i2) {

            boolean flag=false;

                calendar = Calendar.getInstance();
                Calendar calendarTemp=Calendar.getInstance();
                calendarTemp.setTime(endCalendar.getTime());
                calendarTemp.set(Calendar.HOUR_OF_DAY, i);
                calendarTemp.set(Calendar.MINUTE, i2);

                if (startCalendar.after(calendarTemp))
                    flag = true;
                   // Toast.makeText(getApplicationContext(), getString(R.string.end_time_should_greater_then_start), Toast.LENGTH_SHORT).show();
                else if (calendar.after(calendarTemp))
                    //Toast.makeText(getApplicationContext(), getString(R.string.end_time_should_greater_then_current), Toast.LENGTH_SHORT).show();
                    flag = true;
            else flag=true;

            if(flag) {
                endCalendar.set(Calendar.HOUR_OF_DAY, i);
                endCalendar.set(Calendar.MINUTE, i2);
                String sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
                tvEndsBy.setText(sdate);
            }
        }
    };
    TimePickerDialog.OnTimeSetListener myfromTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker timePicker, int i, int i2) {

            startCalendar.set(Calendar.HOUR_OF_DAY, i);
            startCalendar.set(Calendar.MINUTE, i2);
            String sdate = Util.ddMMMyyyy.format(startCalendar.getTime());
            tvStartingFrom.setText(sdate);

                    endCalendar.setTime(startCalendar.getTime());
                    endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH) + 1);
                    endCalendar.set(Calendar.MINUTE, i2);
                    sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
                    tvEndsBy.setText(sdate);
        }
    };
    ImageLoader imageLoader;
    int adMinCode = 0;
    private ProgressDialog progress;
    private DatePickerDialog.OnDateSetListener mytoDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, month);
                endCalendar.set(Calendar.DAY_OF_MONTH, day);
                showDialog(END_TIME);

        }
    };
    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, day);
            showDialog(START_TIME);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);
        ButterKnife.inject(this);
        CheckForEditorNew();
        Init();
        random = new Random();
        createURI();
        InitDialoguesAndSpinners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Active = true;

        if (read(Constant.SHRED_PR.KEY_ASSIGNEE_CC).equals("1")) {
            //Assignee:
            write(Constant.SHRED_PR.KEY_ASSIGNEE_CC,"0");

            String strAssignee = "";
            try {
                JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
                listAssignne.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                    User user = new User();
                    user.setFirstName("" + jsonObject1.optString(User.FIRST_NAME));
                    user.setLastName("" + jsonObject1.optString(User.LAST_NAME));
                    user.setUser_Image("" + jsonObject1.optString(User.USER_IMAGE));
                    user.setUser_Id(jsonObject1.optInt(User.USER_ID));
                    user.setSelected(jsonObject1.optBoolean(User.SELECTED));

                    if(user.isSelected()) {
                        if (strAssignee.length() == 0) strAssignee = user.getFirstName()+" "+user.getLastName();
                        else strAssignee += ", " + user.getFirstName()+" "+user.getLastName();
                    }

                    listAssignne.add(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (strAssignee.length() > 0) {
                tvAssigneed.setText("" + strAssignee + "\n");
            } else tvAssigneed.setText("");

        }

        if (read(Constant.SHRED_PR.KEY_ASSIGNEE_CC).equals("2")) {
            //CC:
            write(Constant.SHRED_PR.KEY_ASSIGNEE_CC,"0");

            String strCC = "";
            try {
                JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
                listCC.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                    User user = new User();
                    user.setFirstName("" + jsonObject1.optString(User.FIRST_NAME));
                    user.setLastName("" + jsonObject1.optString(User.LAST_NAME));
                    user.setUser_Image("" + jsonObject1.optString(User.USER_IMAGE));
                    user.setUser_Id(jsonObject1.optInt(User.USER_ID));
                    user.setSelected(jsonObject1.optBoolean(User.SELECTED));

                    if(user.isSelected()) {
                        if (strCC.length() == 0) strCC = user.getFirstName()+" "+user.getLastName();
                        else strCC += ", " + user.getFirstName()+" "+user.getLastName();
                    }

                    listCC.add(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (strCC.length() > 0) {
                tvCC.setText("" + strCC+"\n");
            } else tvCC.setText("");

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Active = false;
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

    @OnClick(R.id.rlDone)
    @SuppressWarnings("unused")
    public void Done(View view) {
        startCreatingProject();
    }

    @OnClick(R.id.rlAssignee)
    @SuppressWarnings("unused")
    public void Assignee(View view) {
        try {
            JSONArray jsonArray=new JSONArray();
            for (int i=0;i<listAssignne.size();i++){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put(User.FIRST_NAME,""+listAssignne.get(i).getFirstName());
                jsonObject.put(User.LAST_NAME,""+listAssignne.get(i).getLastName());
                jsonObject.put(User.USER_ID,""+listAssignne.get(i).getUser_Id());
                jsonObject.put(User.USER_IMAGE,""+listAssignne.get(i).getUser_Image());
                jsonObject.put(User.SELECTED,""+listAssignne.get(i).isSelected());
                jsonArray.put(jsonObject);
            }
            write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
            Intent intent = new Intent(CreateProjectActivity.this, SelectAssigneeCC.class);
            intent.putExtra("title", getResources().getString(R.string.assign_to));
            intent.putExtra("key_assignee_cc", "1");
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_bottom,
                    R.anim.hold_bottom);
        }catch (Exception e){e.printStackTrace();}

    }

    @OnClick(R.id.rlCC)
    @SuppressWarnings("unused")
    public void CC(View view) {
        try {
            JSONArray jsonArray=new JSONArray();
            for (int i=0;i<listCC.size();i++){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put(User.FIRST_NAME,""+listCC.get(i).getFirstName());
                jsonObject.put(User.LAST_NAME,""+listCC.get(i).getLastName());
                jsonObject.put(User.USER_ID,""+listCC.get(i).getUser_Id());
                jsonObject.put(User.USER_IMAGE,""+listCC.get(i).getUser_Image());
                jsonObject.put(User.SELECTED,""+listCC.get(i).isSelected());
                jsonArray.put(jsonObject);
            }
            write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
            Intent intent = new Intent(CreateProjectActivity.this, SelectAssigneeCC.class);
            intent.putExtra("title", getResources().getString(R.string.cc));
            intent.putExtra("key_assignee_cc", "2");
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_bottom,
                    R.anim.hold_bottom);
        }catch (Exception e){e.printStackTrace();}
    }

    @OnClick(R.id.rlStartingFrom)
    @SuppressWarnings("unused")
    public void StartingFrom(View view) {
        showDialog(START_DATE);
    }

    @OnClick(R.id.rlEndsBy)
    @SuppressWarnings("unused")
    public void EndsBy(View view) {
        showDialog(END_DATE);
    }

    @OnClick(R.id.rlHighPriority)
    @SuppressWarnings("unused")
    public void HighPriority(View view) {

        strPriority = Constant.HIGH;
        rlHighPriority.setBackgroundResource(R.drawable.rectangle_selected);
        rlMediumPriority.setBackgroundResource(R.drawable.rectangle_unselected);
        rlLowPriority.setBackgroundResource(R.drawable.rectangle_unselected);
        tvHighPriority.setTextColor(getResources().getColor(R.color.white));
        tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
        tvLowPriority.setTextColor(getResources().getColor(R.color.gray));

    }

    @OnClick(R.id.rlMediumPriority)
    @SuppressWarnings("unused")
    public void MediumPriority(View view) {

        strPriority = Constant.MEDIUM;
        rlHighPriority.setBackgroundResource(R.drawable.rectangle_unselected);
        rlMediumPriority.setBackgroundResource(R.drawable.rectangle_selected);
        rlLowPriority.setBackgroundResource(R.drawable.rectangle_unselected);
        tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
        tvMediumPriority.setTextColor(getResources().getColor(R.color.white));
        tvLowPriority.setTextColor(getResources().getColor(R.color.gray));

    }

    @OnClick(R.id.rlLowPriority)
    @SuppressWarnings("unused")
    public void LowPriority(View view) {

        strPriority = Constant.LOW;
        rlHighPriority.setBackgroundResource(R.drawable.rectangle_unselected);
        rlMediumPriority.setBackgroundResource(R.drawable.rectangle_unselected);
        rlLowPriority.setBackgroundResource(R.drawable.rectangle_selected);
        tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
        tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
        tvLowPriority.setTextColor(getResources().getColor(R.color.white));

    }


    private void CheckForEditorNew() {
        project = new Project();
        project = (Project) getIntent().getSerializableExtra("ProjectItem");
        if (project != null && project.getProject_Id() > 0) {
            MODE = EDIT;
            tvTitle.setText(getString(R.string.edit_project));
            Log.d("Edit Project", "id:" + project.getProject_Id() + " name :" + project.getProject_name());
        } else {
            project = new Project();
            MODE = NEW;
        }
    }

    private void Init() {

        progressDialog = new TransparentProgressDialog(CreateProjectActivity.this, R.drawable.progressdialog, false);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        calendar = Calendar.getInstance();
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        if (MODE == NEW) {

            String sTime = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_STARTTIME);
            Log.e("Start Time", "s:" + sTime);
            String eTime = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ENDTIME);
            Log.e("end Time", "e:" + eTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            Date CompStartTime = null, CompEndtime = null;
            try {
                CompStartTime = simpleDateFormat.parse(sTime);
                CompEndtime = simpleDateFormat.parse(eTime);
            } catch (ParseException ex) {
                System.out.println("Exception " + ex);
            }
            ////////////////////
            if ((startCalendar.get(Calendar.HOUR_OF_DAY) > CompEndtime.getHours())
                    ||
                    (startCalendar.get(Calendar.HOUR_OF_DAY) == CompEndtime.getHours() && startCalendar.get(Calendar.MINUTE) > CompEndtime.getMinutes())) {
                endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH) + 1);
                endCalendar.set(Calendar.HOUR_OF_DAY, CompEndtime.getHours());
                endCalendar.set(Calendar.MINUTE, CompEndtime.getMinutes());
            } else if (startCalendar.get(Calendar.HOUR_OF_DAY) < CompStartTime.getHours()
                    ||
                    (startCalendar.get(Calendar.HOUR_OF_DAY) == CompStartTime.getHours() && startCalendar.get(Calendar.MINUTE) < CompStartTime.getMinutes())) {
                endCalendar.set(Calendar.HOUR_OF_DAY, CompEndtime.getHours());
                endCalendar.set(Calendar.MINUTE, CompEndtime.getMinutes());
            } else {
                endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH) + 1);
            }

        } else {
            try {
                startCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(project.getStartDate())));
                endCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(project.getEndDate())));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        ///Setting Default Current Dates in to and from dates

        String sdate = Util.ddMMMyyyy.format(startCalendar.getTime());
        tvStartingFrom.setText(sdate);
        sdate = Util.ddMMMyyyy.format(endCalendar.getTime());
        tvEndsBy.setText(sdate);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        if (MODE == EDIT) {
            if (project.getProjectImage() != null && project.getProjectImage() != "null" && project.getProjectImage().length() > 4) {
                imageLoader.displayImage(project.getProjectImage(), ivSelectedImage);
            }
            et_projectName.setText(project.getProject_name());
            et_projectName.setSelection(et_projectName.getText().length());
            et_projectDescription.setText(project.getDescription());
        }
    }

    private void InitDialoguesAndSpinners() {
        GetUserList();
        Log.e("Create Project", "User SIze :" + noofMembers);

        ArrayAdapter ownerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, membersString);
        ownerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_owner.setAdapter(ownerAdapter);
        spinner_owner.setSelection(adMinCode);

        if (MODE == NEW) {

            //Priority:

            if(strPriority.equals(Constant.HIGH)){
                strPriority=Constant.HIGH;

                rlHighPriority.setBackgroundResource(R.drawable.rectangle_selected);
                rlMediumPriority.setBackgroundResource(R.drawable.rectangle_unselected);
                rlLowPriority.setBackgroundResource(R.drawable.rectangle_unselected);
                tvHighPriority.setTextColor(getResources().getColor(R.color.white));
                tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
                tvLowPriority.setTextColor(getResources().getColor(R.color.gray));
            }
            else if(strPriority.equals(Constant.MEDIUM)){
                strPriority=Constant.MEDIUM;

                rlHighPriority.setBackgroundResource(R.drawable.rectangle_unselected);
                rlMediumPriority.setBackgroundResource(R.drawable.rectangle_selected);
                rlLowPriority.setBackgroundResource(R.drawable.rectangle_unselected);
                tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
                tvMediumPriority.setTextColor(getResources().getColor(R.color.white));
                tvLowPriority.setTextColor(getResources().getColor(R.color.gray));
            }
            else if(strPriority.equals(Constant.LOW)){
                strPriority=Constant.LOW;

                rlHighPriority.setBackgroundResource(R.drawable.rectangle_unselected);
                rlMediumPriority.setBackgroundResource(R.drawable.rectangle_unselected);
                rlLowPriority.setBackgroundResource(R.drawable.rectangle_selected);
                tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
                tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
                tvLowPriority.setTextColor(getResources().getColor(R.color.white));
            }

        } else {
            ////Set Assigned users

            Log.e("Assignee", ">>" + project.getProjectAssignedTo());
            String frq[] = project.getProjectAssignedTo().split(",");  // 35,45
            if (frq != null && frq.length > 0) {
                for (int i = 0; i < frq.length; i++) {
                    for (int j = 0; j < listAssignne.size(); j++) {
                        if (frq[i].equals("" + listAssignne.get(j).getUser_Id())) {
                            listAssignne.get(j).setSelected(true);
                        }
                    }
                }
            }
            String strAssignee = "";
            for (int i=0;i<listAssignne.size();i++){
                User user=listAssignne.get(i);
                if(user.isSelected()) {
                    if (strAssignee.length() == 0) strAssignee = user.getFirstName()+" "+user.getLastName();
                    else strAssignee += ", " + user.getFirstName()+" "+user.getLastName();
                }
            }
            Log.e("strAssignee", ">>" + strAssignee);
            if (strAssignee.length() > 0) {
                tvAssigneed.setText("" + strAssignee + "\n");
            } else tvAssigneed.setText("");


            for (int i=0;i<listCC.size();i++) listCC.get(i).setSelected(false);
            //creation of CC to Work item dialogue
            if (project.getProjectCCTo() != null && project.getProjectCCTo().length() > 0) {
                Log.e("CCs", ">>" + project.getProjectCCTo());
                String frq1[] = project.getProjectCCTo().split(",");  // 35,45
                if (frq1 != null && frq1.length > 0) {
                    for (int i = 0; i < frq1.length; i++) {
                        for (int j = 0; j < listCC.size(); j++) {
                            if (frq1[i].equals(""+listCC.get(j).getUser_Id())) {
                                listCC.get(j).setSelected(true);
                            }
                        }
                    }
                }
            }
            String strCC = "";
            for (int i=0;i<listCC.size();i++){
                User user=listCC.get(i);
                if(user.isSelected()) {
                    if (strCC.length() == 0) strCC = user.getFirstName()+" "+user.getLastName();
                    else strCC += ", " + user.getFirstName()+" "+user.getLastName();
                }
            }
            Log.e("strCC", ">>" + strCC);
            if (strCC.length() > 0) {
                tvCC.setText("" + strCC+"\n");
            } else tvCC.setText("");


            /// Set Priority


            if(project.getPriority().equals(Constant.HIGH)){
                strPriority=Constant.HIGH;

                rlHighPriority.setBackgroundResource(R.drawable.rectangle_selected);
                rlMediumPriority.setBackgroundResource(R.drawable.rectangle_unselected);
                rlLowPriority.setBackgroundResource(R.drawable.rectangle_unselected);
                tvHighPriority.setTextColor(getResources().getColor(R.color.white));
                tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
                tvLowPriority.setTextColor(getResources().getColor(R.color.gray));
            }
            else if(project.getPriority().equals(Constant.MEDIUM)){
                strPriority=Constant.MEDIUM;

                rlHighPriority.setBackgroundResource(R.drawable.rectangle_unselected);
                rlMediumPriority.setBackgroundResource(R.drawable.rectangle_selected);
                rlLowPriority.setBackgroundResource(R.drawable.rectangle_unselected);
                tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
                tvMediumPriority.setTextColor(getResources().getColor(R.color.white));
                tvLowPriority.setTextColor(getResources().getColor(R.color.gray));
            }
            else if(project.getPriority().equals(Constant.LOW)){
                strPriority=Constant.LOW;

                rlHighPriority.setBackgroundResource(R.drawable.rectangle_unselected);
                rlMediumPriority.setBackgroundResource(R.drawable.rectangle_unselected);
                rlLowPriority.setBackgroundResource(R.drawable.rectangle_selected);
                tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
                tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
                tvLowPriority.setTextColor(getResources().getColor(R.color.white));
            }

            /// Set Owners
            for (int i = 0; i < userArrayList.size(); i++) {
                try {
                    if (userArrayList.get(i).getUser_Id() == Integer.parseInt(project.getOwner()))
                        spinner_owner.setSelection(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void GetUserList() {
        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        userArrayList.clear();
        listAssignne.clear();
        listCC.clear();

        Cursor cursor = db.rawQuery("select * from " + Constant.UserTable, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {

                    User user = new User();
                    user.setUser_Id(cursor.getInt(cursor.getColumnIndex(User.USER_ID)));
                    user.setFirstName(cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)));
                    user.setLastName(cursor.getString(cursor.getColumnIndex(User.LAST_NAME)));
                    user.setUser_Image(cursor.getString(cursor.getColumnIndex(User.USER_IMAGE)));
                    user.setSelected(false);
                    //Log.e("User", "id :" + tempObj.getUser_Id() + " name:" + tempObj.getFirstName() + " " + tempObj.getLastName());
                    if (cursor.getString(cursor.getColumnIndex("role_id")).equals("1")) {
                        adMinCode = userArrayList.size();
                    }
                    userArrayList.add(user);

                    User user1 = new User();
                    user1.setUser_Id(cursor.getInt(cursor.getColumnIndex(User.USER_ID)));
                    user1.setFirstName(cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)));
                    user1.setLastName(cursor.getString(cursor.getColumnIndex(User.LAST_NAME)));
                    user1.setUser_Image(cursor.getString(cursor.getColumnIndex(User.USER_IMAGE)));
                    user1.setSelected(false);
                    listAssignne.add(user1);

                    User user2 = new User();
                    user2.setUser_Id(cursor.getInt(cursor.getColumnIndex(User.USER_ID)));
                    user2.setFirstName(cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)));
                    user2.setLastName(cursor.getString(cursor.getColumnIndex(User.LAST_NAME)));
                    user2.setUser_Image(cursor.getString(cursor.getColumnIndex(User.USER_IMAGE)));
                    user2.setSelected(false);
                    listCC.add(user2);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if(cursor!=null) cursor.close();

        noofMembers = userArrayList.size();

        membersString = new String[noofMembers];
        for (int i = 0; i < noofMembers; i++) {
            membersString[i] = userArrayList.get(i).getName();
        }
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case END_DATE:
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT,mytoDateListener, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DATE));
            case START_DATE:
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT,myfromDateListener, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DATE));
            case END_TIME:
                return new TimePickerDialog(this,TimePickerDialog.THEME_HOLO_LIGHT, myToTimeListener, endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE), false);
            case START_TIME:
                return new TimePickerDialog(this,TimePickerDialog.THEME_HOLO_LIGHT, myfromTimeListener, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE), false);
        }
        return null;
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    startCameras();
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void startCameras() {
        if (globalUri != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, globalUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(intent, REQUEST_CAMERA);
            imageFlag = true;
        } else {
            createURI();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_FILE:
                    try {
                        // When an Image is picked
                        if (data != null) {
                            // Get the Image from data
                            Uri selectedImageUri = data.getData();
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imagePath = camera_pathname = cursor.getString(columnIndex);
                            cursor.close();
                            Log.d("img path", camera_pathname);
                            // Set the Image in ImageView after decoding the String
                            ivSelectedImage.setImageBitmap(BitmapFactory.decodeFile(camera_pathname));
                            project.setProjectImage("file://" + imagePath);
                            project.setProjectImagePath(imagePath);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case REQUEST_CAMERA:
                    Log.i("globalUri", ">>" + globalUri.getPath());
                    String imageId = convertImageUriToFile(globalUri, this);
                    new LoadImagesFromSDCard().execute("" + imageId);
                    break;
            }
        }
    }

    private void startCreatingProject() {
        if (checkValues()) {
            PrepareObj();
            uploadData();
        }
    }

    private boolean checkValues() {
        if (!(et_projectName.getText().toString().trim().length() > 0)) {
            Toast.makeText(this, "Enter Project Title", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(et_projectDescription.getText().toString().trim().length() > 0)) {
            Toast.makeText(this, "Enter Project Description", Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean assigned = false;
        for (int i = 0; i < listAssignne.size(); i++) {
            if (listAssignne.get(i).isSelected()) {
                assigned = true;
            }
        }
        if (!assigned) {
            Toast.makeText(this, "Select Assignees", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (endCalendar.before(startCalendar)) {
            Toast.makeText(this,  getResources().getString(R.string.end_time_should_greater_then_start), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (MODE == NEW) {
            calendar = Calendar.getInstance();
            if (endCalendar.before(calendar)) {
                Toast.makeText(this, getResources().getString(R.string.end_time_should_greater_then_current), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (project.getProjectImagePath() != null && project.getProjectImagePath().length() > 5) {
            if (!Util.checkFileSize(project.getProjectImagePath())) {
                Toast.makeText(this, getResources().getString(R.string.image_size_exceeds_limit), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void PrepareObj() {
        project.setProject_name(et_projectName.getText().toString());
        project.setDescription(et_projectDescription.getText().toString());
        project.setPriority(strPriority);

        String sdate = Util.sdf.format(startCalendar.getTime());
        project.setStartDate(Util.locatToUTC(sdate));
        String edate = Util.sdf.format(endCalendar.getTime());
        project.setEndDate(Util.locatToUTC(edate));

        String assignWork = "";
        for (int i = 0; i < listAssignne.size(); i++) {
            User user=listAssignne.get(i);
            if(user.isSelected()) {
                if (assignWork.length() == 0) assignWork = ""+user.getUser_Id();
                else assignWork += "," + user.getUser_Id();
            }
        }
        project.setProjectAssignedTo(assignWork);

        String ccWork = "";
        for (int i = 0; i < listCC.size(); i++) {
            User user=listCC.get(i);
            if(user.isSelected()) {
                if (ccWork.length() == 0) ccWork = ""+user.getUser_Id();
                else ccWork += "," + user.getUser_Id();
            }
        }
        project.setProjectCCTo(ccWork);


/// Set Owner
        if (spinner_owner.getSelectedItemPosition() >= 0)
            project.setOwner("" + userArrayList.get(spinner_owner.getSelectedItemPosition()).getUser_Id());
        else
            project.setOwner(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID));

        if (MODE == NEW)
            try {
                project.setUserCode(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID)));
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    private void uploadData() {
        HashMap<String, String> localmap = getHashMapToSend();
        Random rand = new Random();

        localmap.put("name", project.getProject_name());
        localmap.put("description", project.getDescription());
        localmap.put("priority", project.getPriority());
        localmap.put("tolist", project.getProjectAssignedTo());
        localmap.put("cclist", project.getProjectCCTo());
        localmap.put("startdatetime", project.getStartDate());
        localmap.put("enddatetime", project.getEndDate());
        localmap.put("estimateddatetime", project.getEstimatedWorkTime());
        localmap.put("ownerid", project.getOwner());
        localmap.put("creatorid", "" + project.getUserCode());
        localmap.put("companyid", "" + Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ID));
        localmap.put("status", "" + Constant.offline);
        localmap.put("imageURL", project.getProjectImage());
        HashMap<String, String> receiveMap = new HashMap<String, String>();

            if (MODE == NEW) {
                localmap.put("projectid", "" + rand.nextInt(Integer.MAX_VALUE));
                saveIntoDatabase(localmap);
                receiveMap.put("id", "" + 106);
                receiveMap.put("projectImage", project.getProjectImage());
                new ProjectAPI(project.getProjectImagePath(), localmap, receiveMap, Constant.URL + "createProject", db, getApplicationContext(), 0).execute();
            } else {
                localmap.put("projectid", "" + project.getProject_Id());
                receiveMap.put("id", "" + 109);
                receiveMap.put("projectImage", project.getProjectImage());
                receiveMap.put("status", project.getStatus());
                new ProjectAPI(project.getProjectImagePath(), localmap, receiveMap, Constant.URL + "modifyProject", db, getApplicationContext(), 0).execute();
            }

    }

    public void saveIntoDatabase(HashMap toSendHashMap) {

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        ContentValues projectValues = new ContentValues();
        projectValues.put(Project.USER_CODE, "" + toSendHashMap.get("userid"));
        projectValues.put(Project.PROJECT_NAME, "" + toSendHashMap.get("name"));
        projectValues.put(Project.DESCRIPTION, "" + toSendHashMap.get("description"));
        projectValues.put(Project.PRIORITY, "" + toSendHashMap.get("priority"));
        projectValues.put(Project.PROJECT_ID, "" + toSendHashMap.get("projectid"));
        Log.d("CreateProject", " local Id:" + "" + toSendHashMap.get("projectid"));
        projectValues.put(Project.START_DATE, "" + toSendHashMap.get("startdatetime"));
        projectValues.put(Project.END_DATE, "" + toSendHashMap.get("enddatetime"));
        projectValues.put(Project.ESTIMATED_TIME, "" + toSendHashMap.get("estimatedtime"));
        projectValues.put(Project.ASSIGNED_TO, "" + toSendHashMap.get("tolist"));
        projectValues.put(Project.CC_TO, "" + toSendHashMap.get("cclist"));
        projectValues.put(Project.PROJECT_IMAGE, "" + project.getProjectImage());
        projectValues.put(Project.OWNER, "" + toSendHashMap.get("ownerid"));
        projectValues.put(Project.STATUS, Constant.offline);
        db.insert(Constant.ProjectTable, null, projectValues);
    }

    private HashMap<String, String> getHashMapToSend() {
        HashMap<String, String> localmap = new HashMap<String, String>();
        localmap.put("userid", "" + project.getUserCode());
        localmap.put("companyid", "" + Constant.CompanyId);
        localmap.put("name", "" + project.getProject_name());
        localmap.put("description", "" + project.getDescription());
        localmap.put("priority", project.getPriority());
        localmap.put("startdatetime", project.getStartDate());
        localmap.put("enddatetime", project.getEndDate());
        localmap.put("tolist", project.getProjectAssignedTo());
        localmap.put("cclist", project.getProjectCCTo());
        return localmap;
    }

    private void createURI() {
        String fileName = "task" + random.nextInt(Integer.MAX_VALUE) + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        globalUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public String convertImageUriToFile(Uri imageUri, Activity activity) {
        int imageID = 0;
        try {
            String[] proj = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };
            cursor = activity.managedQuery(

                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );
            //  Get Query Data
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            int size = cursor.getCount();
            if (size == 0) {

            } else {

                int thumbID = 0;
                if (cursor.moveToFirst()) {
                    imageID = cursor.getInt(columnIndex);
                    thumbID = cursor.getInt(columnIndexThumb);
                    String Path = cursor.getString(file_ColumnIndex);
                    camera_pathname = Path;
                    String CapturedImageDetails = " CapturedImageDetails : \n\n"
                            + " ImageID :" + imageID + "\n"
                            + " ThumbID :" + thumbID + "\n"
                            + " Path :" + Path + "\n";
                }
            }
        } finally {
        }
        return "" + imageID;
    }

    public class LoadImagesFromSDCard extends AsyncTask<String, Void, Bitmap> {
        protected void onPreExecute() {
        }

        protected Bitmap doInBackground(String... urls) {

            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;
            try {
                uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                if (bitmap != null) {
                    newBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                    bitmap.recycle();
                }
            } catch (IOException e) {
                cancel(true);
            }
            return newBitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                project.setProjectImagePath(camera_pathname);
                project.setProjectImage("file://" + camera_pathname);
                Log.i("TAG", "After File Created  " + camera_pathname);
                ivSelectedImage.setImageBitmap(bitmap);
            }
        }
    }

    class ProjectAPI extends AsyncTask<Void, String, String> {
        String FilePath;
        HashMap<String, String> hashMap, localMap;
        String ApiName;

        SQLiteDatabase db;
        Context context;
        int offLineId = 0;

        public ProjectAPI(String Path, HashMap<String, String> hMap, HashMap<String, String> lMap, String API, SQLiteDatabase db, Context context, int offLineid) {
            this.FilePath = Path;
            this.hashMap = hMap;
            this.localMap = lMap;
            this.context = context;
            this.db = db;
            this.offLineId = offLineid;
            ApiName = API;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(progressDialog!=null) progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resp = "";
            Log.e("File Api", "Started Background Task");

            if (Util.isOnline(context)) {
                Log.e("Calling Api", ApiName);
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = null;
                    HttpPost poster = new HttpPost(ApiName);

                    FileBody fbody = null;
                    MultipartEntity entity = new MultipartEntity(
                            HttpMultipartMode.BROWSER_COMPATIBLE);

                    Log.i("filePath", "" + FilePath);
                    if (FilePath != null && FilePath.length() > 5) {
                        File image = new File(FilePath);
                        fbody = new FileBody(image);
                        entity.addPart("file", fbody);
                    }
                    Log.e("Api Calling :", hashMap.toString());

                    entity.addPart("JSON", new StringBody(Util.prepareJsonString(hashMap)));
                    entity.addPart("TokenID", new StringBody(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_TOKEN)));
                    entity.addPart("UserID", new StringBody(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID)));
                    poster.setEntity(entity);

                    response = client.execute(poster);

                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(response.getEntity()
                                    .getContent()));
                    String line = null;
                    while ((line = rd.readLine()) != null) {
                        resp += line;
                    }

                    try {
                        JSONObject jObj = new JSONObject(resp);
                        String status = jObj.optString("status");
                        if (!status.equals(Constant.InvalidToken)) {
                            if(MODE==NEW) {
                                HandleResponseWorkItems.HandleCreateProject(FilePath, ApiName, hashMap, localMap, resp, db, context, offLineId);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    /// IF Exception occurs call will be saved to offline database
                    if (offLineId <= 0)
                        OfflineWork.StoreData(FilePath, hashMap, localMap, ApiName, db);
                    return Constant.offline;
                }
                Log.d("Resp Upload", "" + resp);
                return resp;
            } else {
                if (offLineId <= 0)
                    OfflineWork.StoreData(FilePath, hashMap, localMap, ApiName, db);
                return Constant.offline;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.e("CreateProjectActivity", "Received update :-" + response);

            if (response.equals(Constant.offline)) {
                finish();
                overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
            } else {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.optString("status");
                    if (status.equals(Constant.InvalidToken)) {
                        TeamWorkApplication.LogOutClear(CreateProjectActivity.this);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                        JSONObject jsonObject = new JSONObject(response);
                        toast(jsonObject.optString("message"));

                        if (jsonObject.optString("status").equals("Success")) {
                            String roleId = read(Constant.SHRED_PR.KEY_ROLE_ID);
                            if (Arrays.asList("1", "2").contains(roleId)) {
                                write(Constant.SHRED_PR.KEY_RELOAD,"1");
                                updateProject();
                            }
                            finish();
                            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (progressDialog != null){
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }

        }
    }

    private void updateProject() {
        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
        try {
            ContentValues projectValues = new ContentValues();
            projectValues.put(Project.USER_CODE, project.getUserCode());
            projectValues.put(Project.PROJECT_NAME, project.getProject_name());
            projectValues.put(Project.DESCRIPTION, project.getDescription());
            projectValues.put(Project.PRIORITY, project.getPriority());
            projectValues.put(Project.START_DATE, project.getStartDate());
            projectValues.put(Project.END_DATE, project.getEndDate());
            projectValues.put(Project.ASSIGNED_TO, project.getProjectAssignedTo());
            projectValues.put(Project.CC_TO, project.getProjectCCTo());
            projectValues.put(Project.STATUS, project.getStatus());
            projectValues.put(Project.OWNER, project.getOwner());

            Cursor crsr = db.rawQuery("select * from " + Constant.ProjectTable + " where " + Project.PROJECT_ID + " = " + project.getProject_Id(), null);
            if (crsr != null && crsr.getCount() > 0) {
                crsr.moveToFirst();
                db.update(Constant.ProjectTable, projectValues, Project.PROJECT_ID + " = ?", new String[]{project.getProject_Id() + ""});
            }
            if(crsr!=null) crsr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
