package com.bigbang.superteam.workitem;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bigbang.superteam.Application.FileApiTask;
import com.bigbang.superteam.Application.FileApiTaskNewModel;
import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.Privileges;
import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.OfflineWork;
import com.bigbang.superteam.dataObjs.User;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.dataObjs.WorkTransaction;
import com.bigbang.superteam.task.adapter.UpdateTaskChatAdapter;
import com.bigbang.superteam.task.database.TaskChatDAO;
import com.bigbang.superteam.task.database.TaskDAO;
import com.bigbang.superteam.task.model.Expense;
import com.bigbang.superteam.task.model.TaskChat;
import com.bigbang.superteam.task.model.TaskMember;
import com.bigbang.superteam.task.model.TaskModel;
import com.bigbang.superteam.util.AudioRecorder;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 4/15/2015.
 */

public class UpdateWorkActivity extends BaseActivity implements View.OnClickListener {

    public static final int END_DATE = 105;
    public static final int START_DATE = 106;
    public static final int START_TIME = 111;
    public static final int END_TIME = 112;
    private static final int INVOICE_DATE = 107;
    private static final int SELECT_FILE = 108;
    private static final int REQUEST_CAMERA = 109;
    private static final int CROP_FROM_CAMERA = 110;
    public static Boolean Active = false;
    public static int WORK_ID = 0;
    private final long startTime = 60 * 1000;
    private final long interval = 1 * 1000;

    @InjectView(R.id.rlBack)
    RelativeLayout rlBack;
    @InjectView(R.id.rlDone)
    RelativeLayout rlDone;
    @InjectView(R.id.rlOptionMenu)
    RelativeLayout rlOptionsMenu;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.tvType)
    TextView tvType;
    @InjectView(R.id.rlSend)
    RelativeLayout rlSend;
    @InjectView(R.id.rlSound)
    RelativeLayout rlSound;
    @InjectView(R.id.rlSendImage)
    RelativeLayout rlSendImage;
    @InjectView(R.id.btnSound)
    ImageButton btnSound;
    @InjectView(R.id.tvStartingFrom)
    TextView tvStartingFrom;
    @InjectView(R.id.tvEndsBy)
    TextView tvEndsBy;
    @InjectView(R.id.tvInvoiceDate)
    TextView tvInvoiceDate;
    @InjectView(R.id.list_workItemUpdate)
    ListView updateList;
    @InjectView(R.id.btn_editWork)
    Button editWorkItemBtn;
    @InjectView(R.id.btn_viewWorkItem)
    Button viewBtn;
    @InjectView(R.id.btn_expenses)
    Button expensesBtn;
    @InjectView(R.id.et_message)
    EditText etMessage;
    @InjectView(R.id.et_amount)
    EditText etAmount;
    @InjectView(R.id.ll_Expense)
    LinearLayout llExpense;
    @InjectView(R.id.ll_Postpone)
    LinearLayout llPostpone;
    @InjectView(R.id.ll_Delegate)
    LinearLayout llDelecate;
    @InjectView(R.id.tvQuery)
    TextView tvQuery;
    @InjectView(R.id.tvUsers)
    TextView tvUsers;
    @InjectView(R.id.bottomLayout)
    LinearLayout llBottom;
    @InjectView(R.id.llTaskUpdateType)
    LinearLayout llTaskUpdateType;
    @InjectView(R.id.rlComment)
    RelativeLayout rlComment;
    @InjectView(R.id.rlQuery)
    RelativeLayout rlQuery;
    @InjectView(R.id.rlPostpone)
    RelativeLayout rlPostpone;
    @InjectView(R.id.rlDelegate)
    RelativeLayout rlDelegate;
    @InjectView(R.id.rlExpense)
    RelativeLayout rlExpense;
    @InjectView(R.id.viewComment)
    View viewComment;
    @InjectView(R.id.viewQuery)
    View viewQuery;
    @InjectView(R.id.viewDelegate)
    View viewDelegate;
    @InjectView(R.id.viewExpense)
    View viewExpense;
    @InjectView(R.id.viewPostpone)
    View viewPostpone;
    //    @InjectView(R.id.spinner_workType)
//    Spinner updateTypeSpinner;
    @InjectView(R.id.rl_parentLayout)
    RelativeLayout parentLayout;
    @InjectView(R.id.ll_menuLayout)
    LinearLayout optionsMenu;
    @InjectView(R.id.header)
    RelativeLayout topLayout;
    @InjectView(R.id.middleLayout)
    LinearLayout middleLayout;
    int taskCode = 0;
    //    int noofMembers = 0;
    String imagePath = "";
    Boolean recordFlag = false;
    String taskUpdateType = "Information";
    String status = "";
    Uri globalUri = null;
    String camera_pathname = "";
    ArrayList<WorkTransaction> listTransactions;
    ArrayList<TaskChat> listTransactionsChat;
    ArrayList<User> userArrayList = new ArrayList<>();
    UpdateTaskChatAdapter listAdapter;
    WorkItem workItem;
    Random rand;
    AudioRecorder AR;
    InputMethodManager imm;
    CountDownTimer countDownTimer;
    Calendar calendar, startCalendar, endCalendar, invoiceCalendar;
    Boolean imageFlag = false;
    //    String membersString[];
//    boolean assignMembersSelected[], addUsersSelected[];
    TransparentProgressDialog progressDialog;
    boolean flagST, flagET, flagID;
    SQLiteDatabase db;

    TaskChatDAO taskChatDAO;

    String TAG = "UpdateWorkActivity";
    TaskDAO taskDAO;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_work);

        ButterKnife.inject(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            taskCode = extras.getInt("task_id");
            status = extras.getString("Status");
            WORK_ID = taskCode;
        }

        Init();
        InitializeWorkItem();

        rand = new Random();
        createURI();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);

        GetUserList();
        listTransactions = new ArrayList<>();
        try {
            fetchingChatData(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_WORKUPDATE_NO, "" + 0);
        Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_WORKUPDATE_STRING, "");
    }

    String strAssigneeIds[];
    ArrayList<Integer> arrAssigneeIds = new ArrayList<>();

    private BroadcastReceiver mReceiver;
    String strAssignee = "";

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(
                "android.intent.action.MAIN");

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent


                String msg_for_me = intent.getStringExtra("some_msg");
                InitializeWorkItem();
                fetchingChatData(false);
                //log our message value
                Log.i("InchooTutorial", msg_for_me);

            }
        };

        this.registerReceiver(mReceiver, intentFilter);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("message_aaya"));

        Active = true;

        if (!imageFlag) {
            try {
//                fetchAndLoadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            imageFlag = false;
        }

        if (read(Constant.SHRED_PR.KEY_RELOAD).equals("1")) {
            write(Constant.SHRED_PR.KEY_RELOAD, "0");
            try {
                InitializeWorkItem();
//                fetchAndLoadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (read(Constant.SHRED_PR.KEY_ASSIGNEE_CC).equals("3")) {
            //Assignee:
            write(Constant.SHRED_PR.KEY_ASSIGNEE_CC, "0");

            try {
                JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
                userArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                    User user = new User();
                    user.setFirstName("" + jsonObject1.optString(User.FIRST_NAME));
                    user.setLastName("" + jsonObject1.optString(User.LAST_NAME));
                    user.setUser_Image("" + jsonObject1.optString(User.USER_IMAGE));
                    user.setUser_Id(jsonObject1.optInt(User.USER_ID));
                    user.setSelected(jsonObject1.optBoolean(User.SELECTED));

                    if (user.isSelected()) {
                        if (strAssignee.length() == 0) {
                            strAssignee = user.getFirstName() + " " + user.getLastName();
//                            strAssigneeIds = String.valueOf(user.getUser_Id());
                            arrAssigneeIds.add(user.getUser_Id());
                        } else {
                            strAssignee += ", " + user.getFirstName() + " " + user.getLastName();
                            arrAssigneeIds.add(user.getUser_Id());
                        }
                    }

                    userArrayList.add(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (strAssignee.length() > 0) {
                tvUsers.setText("" + strAssignee + "\n");
            } else tvUsers.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Active = false;
        SQLiteHelper helper = new SQLiteHelper(UpdateWorkActivity.this, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();
        db.delete(Constant.AlertsTable, "Id=" + taskCode + " AND Type like " + "\'" + "Work Update" + "\'", null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        this.unregisterReceiver(this.mReceiver);
        Active = false;
//        listAdapter.PauseCalled();
    }

    @Override
    public void onBackPressed() {
        if (optionsMenu.getVisibility() == View.VISIBLE) {
            optionsMenu.setVisibility(View.GONE);
        } else if (record)
            Toast.makeText(this, getString(R.string.stop_recording_before_exit), Toast.LENGTH_SHORT).show();
        else {
            finish();
            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
        }
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        if (optionsMenu.getVisibility() == View.VISIBLE) {
            optionsMenu.setVisibility(View.GONE);
        } else if (record)
            Toast.makeText(this, getString(R.string.stop_recording_before_exit), Toast.LENGTH_SHORT).show();
        else {
            finish();
            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
        }

    }

    @OnClick(R.id.rlOptionMenu)
    @SuppressWarnings("unused")
    public void OptionsMenu1(View view) {

        if (optionsMenu.getVisibility() == View.VISIBLE) {
            optionsMenu.setVisibility(View.GONE);
        } else {
            optionsMenu.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.rlStartingFrom)
    @SuppressWarnings("unused")
    public void StartingFrom(View view) {
        if (taskModel.taskType.equals(Constant.WORKTYPES[Constant.REGULAR])) {
            showDialog(START_TIME);
        } else
            showDialog(START_DATE);
    }

    @OnClick(R.id.rlEndsBy)
    @SuppressWarnings("unused")
    public void EndsBy(View view) {
        if (taskModel.taskType.equals(Constant.WORKTYPES[Constant.REGULAR])) {
            showDialog(END_TIME);
        } else
            showDialog(END_DATE);
    }

    @OnClick(R.id.rlInvoiceDate)
    @SuppressWarnings("unused")
    public void InvoiceDate(View view) {
        showDialog(INVOICE_DATE);
    }

    @OnClick(R.id.rlSend)
    @SuppressWarnings("unused")
    public void Send(View view) {

        hideKeyboard();
        sendTextMessage();
    }

    @OnClick(R.id.rlUsers)
    @SuppressWarnings("unused")
    public void Users(View view) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < userArrayList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(User.FIRST_NAME, "" + userArrayList.get(i).getFirstName());
                jsonObject.put(User.LAST_NAME, "" + userArrayList.get(i).getLastName());
                jsonObject.put(User.USER_ID, "" + userArrayList.get(i).getUser_Id());
                jsonObject.put(User.USER_IMAGE, "" + userArrayList.get(i).getUser_Image());
                jsonObject.put(User.SELECTED, "" + userArrayList.get(i).isSelected());
                jsonArray.put(jsonObject);
            }
            write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
            Intent intent = new Intent(UpdateWorkActivity.this, SelectAssigneeCC.class);
            intent.putExtra("title", tvQuery.getText().toString());
            intent.putExtra("key_assignee_cc", "3");
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_bottom,
                    R.anim.hold_bottom);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.rlComment)
    @SuppressWarnings("unused")
    public void Comment(View view) {

        viewComment.setBackgroundColor(getResources().getColor(R.color.green_header));
        viewQuery.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewPostpone.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewDelegate.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewExpense.setBackgroundColor(getResources().getColor(R.color.green_box_bg));

        taskUpdateType = "Information";

        rlSend.setVisibility(View.GONE);
        rlSound.setVisibility(View.VISIBLE);
        rlSendImage.setVisibility(View.VISIBLE);

        llPostpone.setVisibility(View.GONE);
        llDelecate.setVisibility(View.GONE);
        llExpense.setVisibility(View.GONE);
    }

    @OnClick(R.id.rlQuery)
    @SuppressWarnings("unused")
    public void Query(View view) {

        viewComment.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewQuery.setBackgroundColor(getResources().getColor(R.color.green_header));
        viewPostpone.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewDelegate.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewExpense.setBackgroundColor(getResources().getColor(R.color.green_box_bg));

        taskUpdateType = "Query";
        tvQuery.setText(getResources().getString(R.string.query_to));

        rlSend.setVisibility(View.VISIBLE);
        rlSound.setVisibility(View.GONE);
        rlSendImage.setVisibility(View.GONE);

        llPostpone.setVisibility(View.GONE);
        llDelecate.setVisibility(View.VISIBLE);
        llExpense.setVisibility(View.GONE);

        for (int i = 0; i < userArrayList.size(); i++) userArrayList.get(i).setSelected(false);

//        String frq[] = workItem.getTaskAssignedTo().split(",");  // 35,45
//        if (frq != null && frq.length > 0) {
//            for (int i = 0; i < frq.length; i++)
//                for (int j = 0; j < userArrayList.size(); j++)
//                    try {
//                        if (frq[i].equals("" + userArrayList.get(j).getUser_Id()))
//                            userArrayList.get(j).setSelected(true);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//        }
//
//        String strAssignee = "";
//        for (int i = 0; i < userArrayList.size(); i++) {
//            User user = userArrayList.get(i);
//            if (user.isSelected()) {
//                if (strAssignee.length() == 0)
//                    strAssignee = user.getFirstName() + " " + user.getLastName();
//                else strAssignee += ", " + user.getFirstName() + " " + user.getLastName();
//            }
//        }
//
//        if (strAssignee.length() > 0) {
//            tvUsers.setText(strAssignee + "\n");
//        } else tvUsers.setText("");

    }

    @OnClick(R.id.rlPostpone)
    @SuppressWarnings("unused")
    public void Postpone(View view) {

        viewComment.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewQuery.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewPostpone.setBackgroundColor(getResources().getColor(R.color.green_header));
        viewDelegate.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewExpense.setBackgroundColor(getResources().getColor(R.color.green_box_bg));

        taskUpdateType = "Postponed";

        rlSend.setVisibility(View.VISIBLE);
        rlSound.setVisibility(View.GONE);
        rlSendImage.setVisibility(View.GONE);

        llPostpone.setVisibility(View.VISIBLE);
        llDelecate.setVisibility(View.GONE);
        llExpense.setVisibility(View.GONE);

        flagST = false;
        flagET = false;
        tvStartingFrom.setText("");
        tvEndsBy.setText("");
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

    }

    @OnClick(R.id.rlDelegate)
    @SuppressWarnings("unused")
    public void Delegate(View view) {

        viewComment.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewQuery.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewPostpone.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewDelegate.setBackgroundColor(getResources().getColor(R.color.green_header));
        viewExpense.setBackgroundColor(getResources().getColor(R.color.green_box_bg));

        taskUpdateType = "Delegate";
        tvQuery.setText(getResources().getString(R.string.delegate_to));

        rlSend.setVisibility(View.VISIBLE);
        rlSound.setVisibility(View.GONE);
        rlSendImage.setVisibility(View.GONE);

        llPostpone.setVisibility(View.GONE);
        llDelecate.setVisibility(View.VISIBLE);
        llExpense.setVisibility(View.GONE);

        for (int i = 0; i < userArrayList.size(); i++) userArrayList.get(i).setSelected(false);

//        String frq[] = workItem.getTaskAssignedTo().split(",");  // 35,45
//        if (frq != null && frq.length > 0) {
//            for (int i = 0; i < frq.length; i++)
//                for (int j = 0; j < userArrayList.size(); j++)
//                    try {
//                        if (frq[i].equals("" + userArrayList.get(j).getUser_Id()))
//                            userArrayList.get(j).setSelected(true);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//        }
//
//        String strAssignee = "";
//        for (int i = 0; i < userArrayList.size(); i++) {
//            User user = userArrayList.get(i);
//            if (user.isSelected()) {
//                if (strAssignee.length() == 0)
//                    strAssignee = user.getFirstName() + " " + user.getLastName();
//                else strAssignee += ", " + user.getFirstName() + " " + user.getLastName();
//            }
//        }

//        if (strAssignee.length() > 0) {
//            tvUsers.setText(strAssignee + "\n");
//        } else tvUsers.setText("");

    }

    @OnClick(R.id.rlExpense)
    @SuppressWarnings("unused")
    public void Expense(View view) {

        viewComment.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewQuery.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewPostpone.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewDelegate.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        viewExpense.setBackgroundColor(getResources().getColor(R.color.green_header));

        taskUpdateType = "Expense";

        rlSend.setVisibility(View.VISIBLE);
        rlSound.setVisibility(View.GONE);
        rlSendImage.setVisibility(View.GONE);

        llPostpone.setVisibility(View.GONE);
        llDelecate.setVisibility(View.GONE);
        llExpense.setVisibility(View.VISIBLE);

        etAmount.setText("");
        invoiceCalendar = Calendar.getInstance();
        flagID = false;
        tvInvoiceDate.setText("");
    }

    @OnClick(R.id.rlDone)
    @SuppressWarnings("unused")
    public void Done(View view) {
        taskUpdateType = "Done";
        sendTextMessage();
    }

    @OnClick(R.id.rlSound)
    @SuppressWarnings("unused")
    public void Sound(View view) {

        if (recordFlag) {
            stopRecording();
            rlSend.setEnabled(true);
            rlSendImage.setEnabled(true);
            rlOptionsMenu.setEnabled(true);
            rlBack.setEnabled(true);
            etMessage.setEnabled(true);

            rlComment.setClickable(true);
            rlQuery.setClickable(true);
            rlPostpone.setClickable(true);
            rlDelegate.setClickable(true);
            rlExpense.setClickable(true);
            rlDone.setClickable(true);

            record = false;
        } else {
            record = true;
            startRecording();
            rlSend.setEnabled(false);
            rlSendImage.setEnabled(false);
            rlOptionsMenu.setEnabled(false);
            rlBack.setEnabled(false);
            etMessage.setEnabled(false);

            rlComment.setClickable(false);
            rlQuery.setClickable(false);
            rlPostpone.setClickable(false);
            rlDelegate.setClickable(false);
            rlExpense.setClickable(false);
            rlDone.setClickable(false);

        }

    }

    @OnClick(R.id.rlSendImage)
    @SuppressWarnings("unused")
    public void SendImage(View view) {
        selectImage();
    }

    @Override
    public void onClick(View view) {
        if (optionsMenu.getVisibility() == View.VISIBLE)
            optionsMenu.setVisibility(View.GONE);
        switch (view.getId()) {

            case R.id.btn_viewWorkItem:
                optionsMenu.setVisibility(View.GONE);
                Intent myIntent = new Intent(this, ViewWorkItemActivity.class);
                myIntent.putExtra(WorkItem.TASK_ID, taskCode);
                startActivity(myIntent);
                overridePendingTransition(R.anim.enter_from_left,
                        R.anim.hold_bottom);
                break;

            case R.id.btn_expenses:
                Intent expenseIntent = new Intent(UpdateWorkActivity.this, WorkItemExpense.class);
                expenseIntent.putExtra(WorkItem.TASK_ID, "" + workItem.getTaskCode());
                startActivity(expenseIntent);
                overridePendingTransition(R.anim.enter_from_left,
                        R.anim.hold_bottom);

                break;
            case R.id.btn_editWork:
                optionsMenu.setVisibility(View.GONE);
                if (workItem.getStatus() != null && workItem.getStatus().equals("Done")) {
                    // Call Service for Reopen this Work Item

                    if (Util.isOnline(getApplicationContext())) {

                        HashMap<String, String> localmap = new HashMap<String, String>();
                        HashMap<String, String> receivemap = new HashMap<String, String>();
                        localmap.put("workitemid", "" + workItem.getTaskCode());
                        receivemap.put("id", "" + 108);
                        Log.e("Reopen", "Call:" + localmap.toString());

                        ReopenTask reopenTask = new ReopenTask(imagePath, localmap, receivemap, Constant.URL + "reopenWorkItem", db, getApplicationContext(), 0);
                        reopenTask.execute();
                    } else {
                        toast(getResources().getString(R.string.network_error));
                    }

                } else {
                    if (workItem.getStatus() != null && workItem.getStatus().equals("Approved")) {
                        Log.e("Edit :", Util.ReadSharePrefrence(UpdateWorkActivity.this, Constant.SHRED_PR.KEY_USERID) + "  in workitem :" + (workItem.getUserCode()));
                        if (Privileges.MODIFY_WORKITEM) {
                            Intent editIntent = new Intent(UpdateWorkActivity.this, EditWorkitem.class);
                            editIntent.putExtra("WorkItem", workItem);
                            startActivity(editIntent);
                            overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                        } else
                            Toast.makeText(UpdateWorkActivity.this, getString(R.string.contact_adming_to_edit_this_work), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdateWorkActivity.this, getString(R.string.work_item_needs_activation), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void GetUserList() {
        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
        userArrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + Constant.UserTable, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    User tempObj = new User();
                    tempObj.setUser_Id(cursor.getInt(cursor.getColumnIndex(User.USER_ID)));
                    tempObj.setFirstName(cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)));
                    tempObj.setLastName(cursor.getString(cursor.getColumnIndex(User.LAST_NAME)));
                    tempObj.setUser_Image(cursor.getString(cursor.getColumnIndex(User.USER_IMAGE)));
                    tempObj.setSelected(false);
                    userArrayList.add(tempObj);
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) cursor.close();
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case END_DATE:
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, mytoDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            case START_DATE:
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, myfromDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            case INVOICE_DATE:
                return new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, myInvoiceDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            case END_TIME:
                return new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, myToTimeListener, endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE), false);
            case START_TIME:
                return new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, myfromTimeListener, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE), false);
        }
        return null;
    }

    TaskModel taskModel;

    private void InitializeWorkItem() {
        try {

            taskModel = taskDAO.getSingleTaskFromID(taskCode);

            if (taskModel.status != null && taskModel.status.equals("Done"))
                status = "Done";

            Log.e("workItem status", ">>" + taskModel.status);
            if (taskModel.status != null && taskModel.status.equals("Approved")) {
                llBottom.setVisibility(View.VISIBLE);
                rlDone.setVisibility(View.VISIBLE);
            } else {
                llBottom.setVisibility(View.VISIBLE);
                rlDone.setVisibility(View.VISIBLE);
            }

            tvName.setText(taskModel.name);
            tvType.setText(taskModel.taskType.toString().toUpperCase() + " TASK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<TaskChat> taskChats;

    private void fetchingChatData(boolean addNull) {
//        taskChats.clear();
        listTransactionsChat.clear();
        TaskChat taskChat = new TaskChat();
        taskChats = taskChatDAO.getTaskChatData(WORK_ID);
//        Collections.reverse(taskChats);
        if (addNull)
            listTransactionsChat.add(0, taskChat);

        for (int i = 0; i < taskChats.size(); i++) {
            taskChat = new TaskChat();

            taskChat.taskID = taskChats.get(i).taskID;
            taskChat.TaskEditID = taskChats.get(i).TaskEditID;
            taskChat.chatType = taskChats.get(i).chatType;
            taskChat.chatStatus = taskChats.get(i).chatStatus;
            taskChat.message = taskChats.get(i).message;
            taskChat.dataType = taskChats.get(i).dataType;
            taskChat.attachmentPath = taskChats.get(i).attachmentPath;
            taskChat.transactionID = taskChats.get(i).transactionID;
            if (taskChat.dataType.equals(WorkTransaction.SOUND)) {
                if (taskChat.attachmentPath != null)
                    if (taskChat.attachmentPath.startsWith("http")) {
                        taskChat.mp = null;
                        if (Util.isOnline(this))

                            new DownloadFile(getApplicationContext(), taskChat.attachmentPath, taskChat.transactionID).execute();
                    } else {
                        File myFile = new File(taskChat.attachmentPath);

                        if(myFile.exists()){
                                taskChat.mp = MediaPlayer.create(this, Uri.parse(taskChat.attachmentPath));
                        }else{
                            Log.e("","===Fle does not exist");
                        }
                    }
            }
            taskChat.transactionID = taskChats.get(i).transactionID;
            taskChat.createdOn = taskChats.get(i).createdOn;
            taskChat.createdById = taskChats.get(i).createdById;
            taskChat.createdByName = taskChats.get(i).createdByName;
            taskChat.approvedById = taskChats.get(i).approvedById;
            taskChat.approvedByName = taskChats.get(i).approvedByName;
            taskChat.lastModifiedBy = taskChats.get(i).lastModifiedBy;
            taskChat.attachmentPath = taskChats.get(i).attachmentPath;

            listTransactionsChat.add(taskChat);
        }

        listAdapter = new UpdateTaskChatAdapter(UpdateWorkActivity.this, listTransactionsChat, taskModel);
        updateList.setAdapter(listAdapter);

        updateList.setSelection(updateList.getCount() - 1);
    }

    private void Init() {
//        taskChats = new ArrayList<>();

        progressDialog = new TransparentProgressDialog(UpdateWorkActivity.this, R.drawable.progressdialog, false);
        listTransactionsChat = new ArrayList<>();
        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

//        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
//        helper.createDatabase();
//        db = helper.openDatabase();

        updateList.setDivider(null);

        viewBtn.setOnClickListener(this);
        editWorkItemBtn.setOnClickListener(this);
        parentLayout.setOnClickListener(this);
        expensesBtn.setOnClickListener(this);
        expensesBtn.setVisibility(View.VISIBLE);

        parentLayout.setOnTouchListener(menuHideListener);
        updateList.setOnTouchListener(menuHideListener);
        topLayout.setOnTouchListener(menuHideListener);
        middleLayout.setOnTouchListener(menuHideListener);
        llBottom.setOnTouchListener(menuHideListener);

        calendar = Calendar.getInstance();
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        invoiceCalendar = Calendar.getInstance();
        String roleId = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_ROLE_ID);
        // If status of workitem is done then no need to display parent and bottom layouts
        Log.e("Privi", "Privilage edit:" + Privileges.MODIFY_WORKITEM);
        if (status != null && status.equals("Done")) {
            if (llBottom != null) {
                llBottom.setVisibility(View.GONE);
            }
            rlDone.setVisibility(View.GONE);

            if (Privileges.REOPEN_WORKITEM) {
                editWorkItemBtn.setText(getString(R.string.reopen));
                editWorkItemBtn.setVisibility(View.VISIBLE);
            } else {
                editWorkItemBtn.setVisibility(View.GONE);
            }
        } else {
            if (Privileges.MODIFY_WORKITEM)
                editWorkItemBtn.setVisibility(View.VISIBLE);
            else editWorkItemBtn.setVisibility(View.GONE);
        }

//        {"Done", "Postponed", "Query", "Delegate", "Information", "Expense"};
//        ArrayAdapter updateAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
//                StaticData.updateTypes);
//        updateAdapter.setDropDownViewResource(
//                android.R.layout.simple_spinner_dropdown_item);
//        updateTypeSpinner.setAdapter(updateAdapter);
//        updateTypeSpinner.setSelection(4);
        Boolean LimitedRights = false;

        if (!Privileges.CLOSE_WORKITEM) {
//            updateAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
//                    StaticData.updateTypes1);
//            updateAdapter.setDropDownViewResource(
//                    android.R.layout.simple_spinner_dropdown_item);
//            updateTypeSpinner.setAdapter(updateAdapter);
//            updateTypeSpinner.setSelection(3);
            LimitedRights = true;
            rlDone.setVisibility(View.GONE);
        }

        countDownTimer = new CountDownTimer(startTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (recordFlag)
                    stopRecording();
            }
        };

        etMessage.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (taskUpdateType.equals("Information")) {
                    if (etMessage.getText().length() > 0) {
                        rlSend.setVisibility(View.VISIBLE);
                        rlSound.setVisibility(View.GONE);
                        rlSendImage.setVisibility(View.GONE);
                    } else {
                        rlSend.setVisibility(View.GONE);
                        rlSound.setVisibility(View.VISIBLE);
                        rlSendImage.setVisibility(View.VISIBLE);
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        taskDAO = new TaskDAO(UpdateWorkActivity.this);
        taskChatDAO = new TaskChatDAO(UpdateWorkActivity.this);

        updateList.setStackFromBottom(true);
    }

    private void sendTextMessage() {
        TaskChat taskChat = new TaskChat();
        boolean error = false;
        /*Postponed Task*/
        ArrayList<Integer> querytoAssignyStr;
        if (taskUpdateType.equals("Postponed")) {

            if (tvStartingFrom.getText().toString() != null && !tvStartingFrom.getText().toString().equals("")) {
//                transaction.setStart_date(Util.locatToUTC(Util.sdf.format(startCalendar.getTime())));
                taskChat.startTime = Util.locatToUTC(Util.sdf.format(startCalendar.getTime()));
            } else {
                Toast.makeText(this, getString(R.string.enter_start_date), Toast.LENGTH_SHORT).show();
                error = true;
                return;
            }
            if (tvEndsBy.getText().toString() != null && !tvEndsBy.getText().toString().equals("")) {
//                transaction.setEnd_date(Util.locatToUTC(Util.sdf.format(endCalendar.getTime())));
                taskChat.endTime = Util.locatToUTC(Util.sdf.format(endCalendar.getTime()));
            } else {
                Toast.makeText(this, getString(R.string.enter_end_date), Toast.LENGTH_SHORT).show();
                error = true;
                return;
            }
            if (startCalendar.getTime().after(endCalendar.getTime())) {
                Toast.makeText(this, getString(R.string.end_time_should_greater_then_start), Toast.LENGTH_SHORT).show();
                error = true;
                return;
            }
            Calendar calendar1 = Calendar.getInstance();

            if (endCalendar.before(calendar1)) {
                Toast.makeText(this, getString(R.string.end_time_should_greater_then_current), Toast.LENGTH_SHORT).show();
                error = true;
                return;
            }

            try {
                Log.e("Task Time:", ">>" + workItem.getStartDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getStartDate())));
                if (calendar.getTime().after(startCalendar.getTime())) {
                    Toast.makeText(this, getString(R.string.postpone_start_date), Toast.LENGTH_SHORT).show();
                    error = true;
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            taskChat.chatStatus = "Pending";

//            taskChat.startTime = Util.locatToUTC(Util.sdf.format(startCalendar.getTime()));
//            taskChat.endTime = Util.locatToUTC(Util.sdf.format(endCalendar.getTime()));

          /*Delegate Task*/
        } else if (taskUpdateType.equals("Delegate")) {
            String assignWork = "";
            for (int i = 0; i < userArrayList.size(); i++) {
                if (userArrayList.get(i).isSelected()) {
                    assignWork = assignWork + userArrayList.get(i).getUser_Id() + ",";
                }
            }
            if (assignWork.length() > 0) {
//                transaction.setDelegate_to(assignWork);

                TaskMember taskMember = new TaskMember();
                taskMember.userID = 0;

                querytoAssignyStr = arrAssigneeIds;
                taskChat.queryTo = querytoAssignyStr;

                taskChat.chatStatus = "Pending";
            } else {
                Toast.makeText(this, getString(R.string.select_assignees), Toast.LENGTH_SHORT).show();
                error = true;
                return;
            }
        } else if (taskUpdateType.equals("Query")) {

            TaskMember taskMember = new TaskMember();
            taskMember.userID = 0;

            querytoAssignyStr = arrAssigneeIds;
            taskChat.queryTo = querytoAssignyStr;
//            taskChat.
//            String assignWork = "";
//            int count = 0;
//            for (int i = 0; i < userArrayList.size(); i++) {
//                if (userArrayList.get(i).isSelected()) {
//                    assignWork = assignWork + userArrayList.get(i).getUser_Id() + ",";
//                    count++;
//                }
//            }
//            if (count > 0) {
//                transaction.setDelegate_to(assignWork);
//            } else
//                transaction.setDelegate_to("" + workItem.getUserCode());
            taskChat.chatStatus = "Approved";
        }
          /*Expense Task*/
        else if (taskUpdateType.equals("Expense")) {
            Expense expense = new Expense();
            if (etAmount.getText().length() > 0) {
                expense.expenseAmount = Double.parseDouble(etAmount.getText().toString());
            } else {
                Toast.makeText(this, getString(R.string.select_amount), Toast.LENGTH_SHORT).show();
                error = true;
                return;
            }
            if (etMessage.getText().length() > 0) {
                taskChat.message = etMessage.getText().toString();
            } else {
                Toast.makeText(this, getString(R.string.select_expense_description), Toast.LENGTH_SHORT).show();
                error = true;
                return;
            }
            if (flagID) {
                expense.expenseDate = Util.locatToUTC(Util.sdf.format(invoiceCalendar.getTime()));
            } else {
                Toast.makeText(this, getString(R.string.select_invoice_date), Toast.LENGTH_SHORT).show();
                error = true;
                return;
            }
            expense.taskID = WORK_ID;
            taskChat.chatStatus = "Pending";
            taskChat.expense = expense;
        } else if (taskUpdateType.equals("Information")) {
            taskChat.chatStatus = "Approved";
        }

        taskChat.taskID = WORK_ID;
        taskChat.chatType = taskUpdateType;
        taskChat.dataType = "Text";
        taskChat.createdById = Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID));
        if (!etMessage.getText().toString().isEmpty() || !etMessage.getText().toString().equals("")) {
            taskChat.message = etMessage.getText().toString();
        }

        if (!error) {
            etMessage.setText("");
            updateTransaction(taskChat);
            updateList.setSelection(updateList.getCount());
        }
    }

    private void updateTransaction(TaskChat taskChat) {

        int id = (int) taskChatDAO.save(taskChat);
//        taskChat.id = id;
//        taskChats.add(taskChat);

//        listAdapter.notifyDataSetChanged();
//        updateList.setStackFromBottom(true);

//        taskChatDAO.save(taskChat);

        String reqString = Util.makeJSONStringFromTaskChatModel(taskChat);
        String reqStringFormated;
        String regex = "(\"queryTo\":)\"([^\"]*)\"";
        if (taskUpdateType.equals("Query")) {
            reqStringFormated = reqString.replaceAll(regex, "\"queryTo\":" + arrAssigneeIds.toString());
        } else if (taskUpdateType.equals("Delegate")) {
            reqStringFormated = reqString.replaceAll(regex, "\"delegateTo\":" + arrAssigneeIds.toString());
        } else if (taskUpdateType.equals("Expense")) {
            reqStringFormated = reqString;
        } else {
            reqStringFormated = reqString;
        }

        FileApiTaskNewModel fileApiTaskNewModel = new FileApiTaskNewModel(imagePath, reqStringFormated, "103", Constant.URL + "insertTaskUpdate", db, UpdateWorkActivity.this, id);
        fileApiTaskNewModel.execute();

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
            startCameras();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_form_library),
                getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    startCameras();
                } else if (items[item].equals(getString(R.string.choose_form_library))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    imageFlag = true;
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
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
                            Log.e("imagePath", imagePath);
                            if (imagePath != null && imagePath.length() > 4) {
                                if (Util.checkFileSize(imagePath)) {
                                    addImage("file://" + imagePath);
                                } else {
                                    Toast.makeText(this, getString(R.string.image_size_exceeds_limit), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_CAMERA:
                    imagePath = Util.getRealPathFromURI(getApplicationContext(), globalUri);
//                    taskChats.
                    Log.e("imagePath", imagePath);
                    if (imagePath != null && imagePath.length() > 4) {
                        if (Util.checkFileSize(imagePath)) {
                            addImage("content://media" + globalUri.getPath());
                        } else {
                            Toast.makeText(this, getString(R.string.image_size_exceeds_limit), Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
            }
        }
    }

    private void addImage(String s) {
        Log.d("Add Image", "::::::::" + s);

        WorkTransaction transaction = new WorkTransaction();
        TaskChat taskChat = new TaskChat();
        int tr_code = rand.nextInt(rand.nextInt(Integer.MAX_VALUE));
        if (s.length() > 0) {
            transaction.setCreated_on(Util.locatToUTC(Util.GetDate()));
//            transaction.setMessage("");
            taskChat.message = "";
//            transaction.setLink(s);
//            transaction.setMessage_type(WorkTransaction.IMAGE);
            taskChat.dataType = WorkTransaction.IMAGE;
//            transaction.setUpdate_type(taskUpdateType);
            taskChat.chatStatus = getStatus(taskUpdateType);
            taskChat.chatType = "Information";
//            transaction.setStatus(getStatus(taskUpdateType));
//            transaction.setTask_code(taskCode);
            taskChat.taskID = WORK_ID;
//            transaction.setTr_code(tr_code);
//            transaction.setUser_code(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID).toString()));
            taskChat.createdById = Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID).toString());
            taskChat.attachmentPath = imagePath;

        }
        listTransactions.add(transaction);
//        listAdapter.notifyDataSetChanged();
        updateList.smoothScrollToPosition(listTransactions.size() - 1);
//        UpdateTransaction(transaction);
        updateTransaction(taskChat);
    }

    private String getStatus(String workType) {
        if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_ROLE_ID).equals("1") || Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_ROLE_ID).equals("2") || Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_ROLE_ID).equals("3")) {
            if (workType.equals("Postponed") || workType.equals("Delegate") || workType.equals("Expense") || workType.equals("Done")) {
                return getString(R.string.approved);
            }
        }
        Log.e("user codes ", "context :" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID).trim() + " in wrok :" + taskModel.createdById);
        if (Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID).trim()) == taskModel.createdById) {
            Log.e("return", "approved");
            return "Approved";
        }
        if (workType.equals("Postponed") || workType.equals("Delegate") || workType.equals("Expense") || workType.equals("Done")) {
            return getString(R.string.pending);
        }
        return "Approved";
    }

    private void stopRecording() {
//        WorkTransaction transaction = new WorkTransaction();
        TaskChat taskChat = new TaskChat();
        recordFlag = false;
        countDownTimer.cancel();
        String path = "";
        try {
            path = AR.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int tr_code = rand.nextInt();
//        transaction.setLink(path);
//        transaction.setLinkPath(path);
//        transaction.setUpdate_type(taskUpdateType);
//        transaction.setStatus(getStatus(taskUpdateType));
        taskChat.chatStatus = getStatus(taskUpdateType);
        imagePath = path;
        taskChat.attachmentPath = path;
//        transaction.mp = MediaPlayer.create(UpdateWorkActivity.this, Uri.parse(path));
        taskChat.mp = MediaPlayer.create(UpdateWorkActivity.this, Uri.parse(path));
//        transaction.setMessage_type(WorkTransaction.SOUND);
        taskChat.dataType = WorkTransaction.SOUND;
        taskChat.chatType = "Information";
//        transaction.setTask_code(taskCode);
        taskChat.taskID = WORK_ID;
//        transaction.setTr_code(tr_code);
//        transaction.setUser_code(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID).toString()));
        taskChat.createdById = Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID).toString());
//        transaction.setCreated_on(Util.locatToUTC(Util.GetDate()));
        taskChat.createdOn = Util.locatToUTC(Util.GetDate());
//        listTransactions.add(transaction);
//        listAdapter.notifyDataSetChanged();
//        updateList.smoothScrollToPosition(listTransactions.size() - 1);
//        UpdateTransaction(transaction);
        updateTransaction(taskChat);
        btnSound.setBackgroundResource(R.drawable.record_audio);

        rlSend.setEnabled(true);
        rlSendImage.setEnabled(true);
        rlOptionsMenu.setEnabled(true);
        rlBack.setEnabled(true);
        etMessage.setEnabled(true);

        rlComment.setClickable(true);
        rlQuery.setClickable(true);
        rlPostpone.setClickable(true);
        rlDelegate.setClickable(true);
        rlExpense.setClickable(true);
        rlDone.setClickable(true);

        record = false;
    }

    private void startRecording() {
        int temp = rand.nextInt(Integer.MAX_VALUE);
        String fileName = Constant.storageDirectory + "task" + temp + ".mp3";
        AR = new AudioRecorder(fileName);
        try {
            AR.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recordFlag = true;
        countDownTimer.start();
        btnSound.setBackgroundResource(R.drawable.pause);
        Toast.makeText(this, getString(R.string.voice_record_limit_1_min), Toast.LENGTH_SHORT).show();
    }

    private void UpdateTransaction(WorkTransaction transaction) {

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
        transaction.setCreated_on("" + Util.locatToUTC(Util.GetDate()));


        //If Task is pending, or rejected than need to activate task first
        if (workItem.getStatus() != null && workItem.getStatus().equals("Pending") || workItem.getStatus().equals("Rejected")) {
            Toast.makeText(this, getString(R.string.work_item_needs_activation), Toast.LENGTH_SHORT).show();
            return;
        }

        //If Task status is not done
        if (workItem.getStatus() == null || !workItem.getStatus().equals("Done")) {
            HashMap<String, String> receiveMap = new HashMap<String, String>();
            receiveMap.put("id", "" + 103);
            receiveMap.put("filelink", transaction.getLink());

            transaction.saveInDb(db);
            FileApiTask apiTask = new FileApiTask(imagePath, getHashmapFromTransaction(transaction), receiveMap, Constant.URL + "updateWorkItem", db, getApplicationContext(), 0);
            apiTask.execute();

            // Set TaskUpdate Information Type
            viewComment.setBackgroundColor(getResources().getColor(R.color.green_header));
            viewQuery.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
            viewPostpone.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
            viewDelegate.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
            viewExpense.setBackgroundColor(getResources().getColor(R.color.green_box_bg));

            taskUpdateType = "Information";
            llPostpone.setVisibility(View.GONE);
            llDelecate.setVisibility(View.GONE);
            llExpense.setVisibility(View.GONE);

            // If admin sends done request then
            String RoleID = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_ROLE_ID);
            String userID = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID);
            if ((transaction.getUpdate_type().equals("Done"))) {
                Toast.makeText(this, "" + getString(R.string.work_item_done_request_on_progress), Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
            } else
                createURI();

        } else {
            Toast.makeText(this, getString(R.string.work_item_already_done), Toast.LENGTH_SHORT).show();
            listTransactions.remove(listTransactions.size() - 1);
            listAdapter.notifyDataSetChanged();
            return;
        }

    }

    private HashMap<String, String> getHashmapFromTransaction(WorkTransaction transaction) {
        HashMap<String, String> localmap = new HashMap<String, String>();
        localmap.put("updateid", "" + transaction.getTr_code());
        localmap.put("workitemid", "" + transaction.getTask_code());
        localmap.put("userid", "" + transaction.getUser_code());
        localmap.put("description", "" + transaction.getMessage());
        localmap.put("updatetype", "" + transaction.getUpdate_type());
        localmap.put("type", "" + transaction.getMessage_type());
        localmap.put("status", "" + transaction.getStatus());
        localmap.put("startdate", transaction.getStart_date());
        localmap.put("enddate", transaction.getEnd_date());
        localmap.put("delegateto", transaction.getDelegate_to());
        localmap.put("expenseamt", transaction.getAmount());
        localmap.put("description", transaction.getDiscription());
        localmap.put("expensedate", transaction.getInvoice_date());
        localmap.put("reason", transaction.getReason());
        localmap.put("queryto", transaction.getDelegate_to());
        return localmap;
    }

    private void createURI() {
        String fileName = "task" + rand.nextInt(Integer.MAX_VALUE) + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        globalUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public class DownloadFile extends AsyncTask<String, Void, String> {
        Context context;
        String urlDownload;
        String transactionId;

        public DownloadFile(Context context, String url, String transactionId) {
            this.context = context;
            this.urlDownload = url;
            this.transactionId = transactionId;
        }

        protected void onPreExecute() {
            Log.v("DOWNLOAD", "Wait for downloading url : " + urlDownload);
        }

        protected String doInBackground(String... params) {
            URL url = null;
            try {
                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("UserID", "" + "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID)));
                params1.add(new BasicNameValuePair("TokenID", "" + "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_TOKEN)));

                if (params1 != null) {
                    String paramString = URLEncodedUtils
                            .format(params1, "utf-8");
                    urlDownload += "?" + paramString;
                }
                url = new URL(urlDownload);
                Log.w("DOWNLOAD", "URL TO CALL : " + url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //set up some things on the connection
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                File folder = new File(Constant.storageDirectory);

                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }

                File file = new File(folder, Util.getFileNameFromUrl(new URL(urlDownload)));

                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = urlConnection.getInputStream();

                //this is the total size of the file
                int totalSize = urlConnection.getContentLength();
                //variable to store total downloaded bytes
                int downloadedSize = 0;

                //create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0; //used to store a temporary size of the buffer

                //now, read through the input buffer and write the contents to the file
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    //add the data in the buffer to the file in the file output stream (the file on the sd card
                    fileOutput.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    //this is where you would do something to report the prgress, like this maybe
                    //updateProgress(downloadedSize, totalSize);
                    Log.w("DOWNLOAD", "progress " + downloadedSize + " / " + totalSize);
                }
                //close the output stream when done
                fileOutput.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException p) {
                p.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //catch some possible errors...
            return "done";
        }

        private void publishProgress(int i) {
            Log.v("DOWNLOAD", "PROGRESS ... " + i);
        }

        protected void onPostExecute(String result) {

            Log.e("", "=================== onPostExecute =======================");

            if (result.equals("done")) {
                SQLiteHelper helper = new SQLiteHelper(context, Constant.DatabaseName);
                helper.createDatabase();
                SQLiteDatabase db = helper.openDatabase();

                ContentValues userValues = new ContentValues();
                try {

                    Log.e("", "======== File name: " + Util.getFileNameFromUrl(new URL(urlDownload)));

                    userValues.put(WorkTransaction.MESSAGE_LINK, Constant.storageDirectory + Util.getFileNameFromUrl(new URL(urlDownload)));

                    taskChatDAO.updateAttachment(transactionId, Constant.storageDirectory + Util.getFileNameFromUrl(new URL(urlDownload)));

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
//                db.update(Constant.WorkTransaction, userValues, WorkTransaction.TRANSACTION_CODE + " = ?", new String[]{transactionCode + ""});
//                taskChatDAO.updateAttachment()
                try {
//                    fetchAndLoadData();
                    fetchingChatData(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ReopenTask extends AsyncTask<Void, String, String> {
        String FilePath;
        HashMap<String, String> hashMap, localMap;
        String ApiName;

        SQLiteDatabase db;
        Context context;
        int offLineId = 0;

        public ReopenTask(String Path, HashMap<String, String> hMap, HashMap<String, String> lMap, String API, SQLiteDatabase db, Context context, int offLineid) {
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
            if (progressDialog != null) progressDialog.show();
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
                } catch (Exception e) {
                    e.printStackTrace();
                    /// IF Exception occurs call will be saved to offline database
                    if (offLineId <= 0)
                        OfflineWork.StoreData(FilePath, hashMap, localMap, ApiName, db);
                    return Constant.offline;
                }
                Log.e("Resp Upload 33333", "" + resp);
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
            Log.e("UpdateWorkActivity", "Received update :-" + response);


            if (response.equals(Constant.offline)) {
                toast(getResources().getString(R.string.network_error));
            } else {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.optString("status");
                    if (status.equals(Constant.InvalidToken)) {
                        TeamWorkApplication.LogOutClear(UpdateWorkActivity.this);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    toast(jsonObject.optString("message"));
                    if (jsonObject.optString("status").equals("Success")) {

                        ContentValues taskValues = new ContentValues();
                        taskValues.put(WorkItem.STATUS, "Approved");
                        db.update(Constant.WorkItemTable, taskValues, WorkItem.TASK_ID + " = ?", new String[]{jsonObject.getString("workitemid") + ""});

                        finish();
                        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (progressDialog != null) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }

        }
    }

    public void removeCountsforWorkItem(int id) {
        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int pk = 0;
        String query = "select * from " + Constant.AlertsTable;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                try {
                    pk = Integer.parseInt(cursor.getString(cursor.getColumnIndex("pk")));
                    Log.e("Id for cancellation:", " pk :=" + pk);
                    try {
                        notificationManager.cancel(pk);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.delete(Constant.AlertsTable, "Id=" + id, null);
    }

    View.OnTouchListener menuHideListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            optionsMenu.setVisibility(View.GONE);
            return false;
        }
    };
    Boolean record = false;

    TimePickerDialog.OnTimeSetListener myToTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker timePicker, int i, int i2) {

            boolean flag = false;

            if (flagST) {

                Log.e(TAG, "myToTimeListener 111111");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endCalendar.getTime());
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i2);

                if (startCalendar.before(calendar)) {
                    flag = true;
                    Log.e(TAG, "myToTimeListener 222222");
                } else {
                    Log.e(TAG, "myToTimeListener 3333333");

                    //Toast.makeText(getApplicationContext(),""+getResources().getString(R.string.end_time_greater),Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "myToTimeListener 444444");
                flag = true;
            }

            if (flag) {
                Log.e(TAG, "myToTimeListener 55555555");
                flagET = true;
                endCalendar.set(Calendar.HOUR_OF_DAY, i);
                endCalendar.set(Calendar.MINUTE, i2);
                String sdate = Util.SDF.format(endCalendar.getTime());
                if (taskModel.taskType.equals(Constant.WORKTYPES[Constant.REGULAR])) {
                    tvEndsBy.setText(sdate.substring(11));
                } else {
                    String sdate1 = Util.ddMMMyyyy.format(endCalendar.getTime());
                    tvEndsBy.setText(sdate1);
                }

            } else {
                endCalendar.set(Calendar.HOUR_OF_DAY, i);
                endCalendar.set(Calendar.MINUTE, i2);
                String sdate = Util.SDF.format(endCalendar.getTime());
                if (workItem.getTaskType().equals(Constant.WORKTYPES[Constant.REGULAR])) {
                    tvEndsBy.setText(sdate.substring(11));
                } else {
                    String sdate1 = Util.ddMMMyyyy.format(endCalendar.getTime());
                    tvEndsBy.setText(sdate1);
                }
            }

        }
    };

    TimePickerDialog.OnTimeSetListener myfromTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker timePicker, int i, int i2) {
            Log.e("Start TIme", "I=" + i + " i2=" + i2);

            boolean flag = false;

            if (flagET) {

                Log.e(TAG, "myToTimeListener 6666666666");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startCalendar.getTime());
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i2);

                if (calendar.before(endCalendar)) {
                    flag = true;
                    Log.e(TAG, "myToTimeListener 777777777777");
                } else {
                    Log.e(TAG, "myToTimeListener 888888888888");
                    //  Toast.makeText(getApplicationContext(),""+getResources().getString(R.string.start_time_less),Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "myToTimeListener 99999999999");
                flag = true;
            }


            if (flag) {
                Log.e(TAG, "myToTimeListener 1010101010");
                flagST = true;
                startCalendar.set(Calendar.HOUR_OF_DAY, i);
                startCalendar.set(Calendar.MINUTE, i2);

                String sdate = Util.SDF.format(startCalendar.getTime());
                if (taskModel.taskType.equals(Constant.WORKTYPES[Constant.REGULAR])) {
                    tvStartingFrom.setText(sdate.substring(11));
                } else {
                    String sdate1 = Util
                            .ddMMMyyyy.format(startCalendar.getTime());
                    tvStartingFrom.setText(sdate1);
                }
            } else {
                startCalendar.set(Calendar.HOUR_OF_DAY, i);
                startCalendar.set(Calendar.MINUTE, i2);
                String sdate = Util.SDF.format(startCalendar.getTime());
                if (taskModel.taskType.equals(Constant.WORKTYPES[Constant.REGULAR])) {
                    tvStartingFrom.setText(sdate.substring(11));
                } else {
                    String sdate1 = Util.ddMMMyyyy.format(startCalendar.getTime());
                    tvStartingFrom.setText(sdate1);
                }
            }

        }
    };

    private DatePickerDialog.OnDateSetListener mytoDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            endCalendar = Calendar.getInstance();
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, month);
            endCalendar.set(Calendar.DAY_OF_MONTH, day);
            showDialog(END_TIME);
            Log.e(TAG, "myToTimeListener @@@@@@@@@@ 6666666666666");

        }
    };

    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            boolean flag = false;

            if (flagET) {

                Log.e(TAG, "myToTimeListener @@@@@@@@@@ 1111");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                if (calendar.before(endCalendar)) {
                    flag = true;
                    Log.e(TAG, "myToTimeListener @@@@@@@@@@ 222222");
                } else {
                    //Toast.makeText(getApplicationContext(),""+getResources().getString(R.string.start_date_less),Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "myToTimeListener @@@@@@@@@@ 33333333");
                }
            } else {
                flag = true;
                Log.e(TAG, "myToTimeListener @@@@@@@@@@ 44444444");
            }

            if (flag) {
                Log.e(TAG, "myToTimeListener @@@@@@@@@@ 5555555555555");
                startCalendar = Calendar.getInstance();
                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, month);
                startCalendar.set(Calendar.DAY_OF_MONTH, day);
                showDialog(START_TIME);
            } else {
                startCalendar = Calendar.getInstance();
                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, month);
                startCalendar.set(Calendar.DAY_OF_MONTH, day);
                showDialog(START_TIME);
            }
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
//                InitializeWorkItem();
//                fetchAndLoadData();

                Log.e("", "What you got gfom :");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private DatePickerDialog.OnDateSetListener myInvoiceDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            invoiceCalendar.set(Calendar.YEAR, i);
            invoiceCalendar.set(Calendar.MONTH, i1);
            invoiceCalendar.set(Calendar.DAY_OF_MONTH, i2);

            String sdate1 = Util.ddMMMyyyy1.format(invoiceCalendar.getTime());
            tvInvoiceDate.setText(sdate1);
            flagID = true;

        }
    };

}



