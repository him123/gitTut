package com.bigbang.superteam.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SQLiteHelper extends SQLiteOpenHelper {
    private Context mContext;
    private SQLiteDatabase sqlDB;
    public static final String DB_NAME = "teamworks.sqlite";
    private static int DB_VERSION = 13;
    private String DB_PATH = "/data/data/com.bigbang.teamworks/databases/";


    //Tables
    public static final String TABLE_COMP_INFO = "company_info";
    public static final String TABLE_HOLIDAYS = "holidays";
    public static final String TABLE_COMP_LEAVES = "comp_leaves";
    public static final String TABLE_TASK_MASTER = "task_master";
    public static final String TABLE_TASK_CHAT = "task_chat";
    public static final String TABLE_EXPENSE = "Expense";
    public static final String TABLE_TASK_MEMBER = "task_member";
    public static final String TABLE_TASK_ATTACHMENT = "task_attachment";
    public static final String TABLE_TASK_TYPE = "task_type";
    public static final String TABLE_TASK_STATUS = "task_status";
    public static final String TABLE_USER = "Users";
    public static final String TABLE_CUST_VENDOR = "Customers";

    //Company Info columns
    public static final String CI_COMP_ID = "companyid";
    public static final String CI_DDCSN = "deducution";
    public static final String CI_DDSN1 = "deducutionHour1";
    public static final String CI_DDSN2 = "deducutionHour2";
    public static final String CI_DDSN3 = "deducutionHour3";
    public static final String CI_DDSN4 = "deducutionHour4";
    public static final String CI_DDSN4ON = "deducutionHour4On";
    public static final String CI_UID = "userid";
    public static final String CI_BSAL = "basicSalary";
    public static final String CI_HRA = "hra";
    public static final String CI_CONVEY = "conveyance";
    public static final String CI_MEDI = "medical";
    public static final String CI_TELE = "telephone";
    public static final String CI_ITA = "lta";
    public static final String CI_SPINCENTIVE = "specialIncentive";
    public static final String CI_OTHALLWNC = "otherAllownace";
    public static final String CI_PFEMPEE = "pfEmployee";
    public static final String CI_PFEMPER = "pfEmployer";
    public static final String CI_PROTAX = "profTax";
    public static final String CI_TDS = "tds";
    public static final String CI_OTHERDED = "otherDeduction";
    public static final String CI_NOTILEVEL = "notificationLevel";
    public static final String CI_NAME = "name";
    public static final String CI_WORKDAYS = "workingDays";
    public static final String CI_STIME = "startTime";
    public static final String CI_ETIME = "endTime";
    public static final String CI_PROLLCYCLE = "payrollCycle";
    public static final String CI_PROLLST = "payrollStart";
    public static final String CI_PROLLEND = "payrollEnd";
    public static final String CI_SALBRTYPE = "salaryBreakupType";
    public static final String CI_ISPROLLENABLED = "isPayrollEnabled";
    public static final String CI_ISREGUALLOWED = "isRegularizationAllowed";
    public static final String CI_ISLATEDDCSNON = "isLateDeductionOn";
    public static final String CI_AUTOLIVUPDATE = "autoLeaveUpdate";
//    public static final String CI_COMP_LEAVES = "companyLeaves";


    //Holidays table columns
    public static final String HO_DATE = "holidayDate";
    public static final String HO_NAME = "holidayName";
    public static final String HO_ID = "holidayId";
    public static final String HO_LOCAL_ID = "id";

    //CompanyLeaves table Columns
    public static final String CL_COMP_ID = "companyId";
    public static final String CL_LV_TYPE = "leaveType";
    public static final String CL_NOOF_LV = "noOfLeaves";
    public static final String CL_LV_UPDATE_CY = "leaveUpdateCyle";
    public static final String CL_ACTIVE = "active";
    public static final String CL_MODIFIED_BY = "modifiedBy";
    public static final String CL_LAST_MODIFIED = "lastModified";


    //TASK GENERAL COLUMNS
    public static final String TS_CREATED_BY_ID = "createdById";
    public static final String TASK_ID = "taskId";
    public static final String TS_CREATED_BY_NAME = "createdByName";
    public static final String TS_APPROVED_BY_ID = "approvedById";
    public static final String TS_APPROVED_BY_NAME = "approvedByName";
    public static final String TS_LASTMODIFIED_BY = "lastModifiedBy";


    //EXPENSE table Columns
    public static final String TS_EX_ID = "expenseId";
    public static final String TS_TASK_CHAT_ID = "taskChatID";
    public static final String TS_EX_AMOUNT = "expenseAmount";
    public static final String TS_EX_STATUS = "status";
    public static final String TS_EX_CREATED_ON = "createdOn";

    //Task table Columns
    public static final String TK_ID = "id";
    public static final String TK_TASK_ID = "taskid";
    public static final String TK_COMPANY_ID = "companyid";
    public static final String TK_NAME = "name";
    public static final String TK_DESC = "description";
    public static final String TK_TYPE = "tasktype";
    public static final String TK_LOCATION = "location";
    public static final String TK_LATITUDE = "latitude";
    public static final String TK_LONGITUDE = "longitude";
    public static final String TK_PRIORITY = "priority";
    public static final String TK_START_TIME = "starttime";
    public static final String TK_END_TIME = "endtime";
    public static final String TK_ESTIMATE_TIME = "estimatetime";
    public static final String TK_CREATED_BY = "createdby";
    public static final String TK_STATUS = "status";
    public static final String TK_ACTIVE = "active";
    public static final String TK_BUDGET = "budget";
    public static final String TK_APPROVED_ON = "approvedon";
    public static final String TK_APPROVED_BY = "approvedby";
    public static final String TK_APPROVAL_NOTE = "approvalnote";
    public static final String TK_LAST_MODIFIED_BY = "lastmodifiedby";
    public static final String TK_LAST_MODIFIED = "lastmodified";
    public static final String TK_CUST_VENDOR_ID = "custvendorid";
    public static final String TK_CUST_VENDOR_NAME = "custvendorname";
    public static final String TK_CUST_VENDOR_CONTACT = "custvendorcontact";
    public static final String TK_INVOICE_DATE = "invoicedate";
    public static final String TK_DUE_DATE = "duedate";
    public static final String TK_INVOICE_AMT = "invoiceamt";
    public static final String TK_OUTSTANDING_AMT = "outstandingamt";
    public static final String TK_PROJECT = "project";
    public static final String TK_FREQUENCY = "frequency";
    public static final String TK_DAY_CODES = "daycodes";
    public static final String TK_CUSTOMER_TYPE = "customertype";
    public static final String TK_PAST_HISTORY = "pasthistory";
    public static final String TK_ADVANCE_PAID = "advancepaid";
    public static final String TK_VENDOR_PREFERENCE = "vendorpreference";
    public static final String TK_ADDRESS_ID = "address_id";
    public static final String TK_ADDRESSSTR = "addressStr";


    //Task CHAT
    public static final String TM_C_TASKID = "taskID";
    public static final String TM_C_TASKEDIT_ID = "TaskEditID";
    public static final String TM_C_CHAT_TYPE = "chatType";
    public static final String TM_C_STATUS = "chatStatus";
    public static final String TM_C_MESSAGE = "message";
    public static final String TM_C_DTYPE = "dataType";
    public static final String TM_C_TRANSACTIONID = "transactionID";
    public static final String TM_C_CREATEDBYID = "createdById";
    public static final String TM_C_CREATEDBYNAME = "createdByName";
    public static final String TM_C_APPROVEDBYID = "approvedById";
    public static final String TM_C_APPROVEDBYNAME = "approvedByName";
    public static final String TM_C_LASTMODIFIEDBY = "lastModifiedBy";

    //Task member column
    public static final String TM_ID = "id";
    public static final String TM_USERID = "userid";
    public static final String TM_TASKID = "taskid";
    public static final String TM_MEMBER_TYPE = "membertype";
    public static final String TM_TASK_RIGHT = "taskright";
    public static final String TM_ACTIVE = "active";
    public static final String TM_USERNAME = "username";
    public static final String TM_CONTACT_NUM = "contact_num";

    //Task Attachment column
    public static final String TA_Id = "id";
    public static final String TA_ATTACHMENT_ID = "attachmentid";
    public static final String TA_TASK_ID = "taskid";
    public static final String TA_PATH = "path";
    public static final String TA_LAST_MODIFIED_BY = "lastmodifiedby";
    public static final String TA_ATTACHMENT_PATH = "attachmentPath";
    public static final String TA_LAST_MODIFIED = "lastmodified";
    public static final String TA_TASK_UPDATE_ID = "taskupdateid";

    //Task Type Column
    public static final String TT_TASK_TYPE_ID = "tasktypeid";
    public static final String TT_TASK_TYPE = "tasktype";

    //Task Status Column
    public static final String TS_TASK_STATUS_ID = "taskstatusid";
    public static final String TS_TASK_STATUS = "taskstatus";
    public static final String TS_DESCRIPTION = "description";
    public static final String TS_LASTMODIFIED = "lastmodified";

    //User columns
    public static final String USER_ID = "pk";
    public static final String USER_USER_ID = "userID";
    public static final String USER_FIRST_NAME = "firstName";
    public static final String USER_LAST_NAME = "lastName";
    public static final String USER_MOBILENO1 = "mobileNo1";
    public static final String USER_EMAIL_ID = "emailID";
    public static final String USER_PERMENANT_ADDRESS = "permanentAddress";
    public static final String USER_PICTURE = "picture";
    public static final String USER_TEMPORARY_ADDRESS = "temporaryAddress";
    public static final String USER_MOBILENO2 = "mobileNo2";
    public static final String USER_ROLE_ID = "role_id";
    public static final String USER_ROLE_DESC = "role_desc";

    //Customer Vendor column
    public static final String CV_ID = "ID";
    public static final String CV_NAME = "Name";
    public static final String CV_MOBILE_NO = "MobileNo";
    public static final String CV_LANDLINE_NO = "LandlineNo";
    public static final String CV_EMAIL_ID = "EmailID";
    public static final String CV_OWNER_ID = "OwnerID";
    public static final String CV_COMPANY_TYPE_ID = "CompanyTypeID";
    public static final String CV_CREATED_BY = "CreatedBy";
    public static final String CV_COMPANY_ID = "CompanyID";
    public static final String CV_TYPE = "Type";
    public static final String CV_DESCRIPTION = "Description";
    public static final String CV_ADDRESSLIST = "AddressList";
    public static final String CV_LOGO = "Logo";


    public SQLiteHelper(Context context, String DB_NAME) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
//		this.DB_NAME= DB_NAME;
        DB_PATH = "/data/data/"
                + mContext.getApplicationContext().getPackageName()
                + "/databases/";
        Log.v("DBPATH", DB_PATH);
    }

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void createDatabase() {
        boolean dbExist = checkDatabase();

        if (dbExist) {
            // do nothing - database already exist
        } else {

            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {
                Log.i("Error ", "" + e.toString());
                throw new Error("Error copying database");
            }
        }
    }

    private void copyDataBase() throws IOException {
        InputStream is = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream out = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }

        is.close();
        out.flush();
        out.close();
    }

    private boolean checkDatabase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    public SQLiteDatabase openDatabase() {
        // Open the database
        String myPath = DB_PATH + DB_NAME;
        sqlDB = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
        return sqlDB;
    }

    public static SQLiteHelper instance;

    public static synchronized SQLiteHelper getHelper(Context context) {
        if (instance == null)
            instance = new SQLiteHelper(context);
        return instance;
    }

    public synchronized void close() {
        if (sqlDB != null) {
            sqlDB.close();
            super.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("inside==", "onupgrade");

        try {
            copyDataBase();
        } catch (IOException e) {
            Log.i("Error ", "" + e.toString());
            throw new Error("Error copying database");
        }
    }

    public void clearTables() {
        dropTables();
    }

    private void dropTables() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + TABLE_HOLIDAYS);
        db.execSQL("delete from " + TABLE_COMP_LEAVES);
        db.execSQL("delete from " + TABLE_TASK_MASTER);
        db.execSQL("delete from " + TABLE_TASK_MEMBER);
        db.execSQL("delete from " + TABLE_TASK_ATTACHMENT);
        db.execSQL("delete from " + TABLE_TASK_TYPE);
        db.execSQL("delete from " + TABLE_TASK_STATUS);
        db.execSQL("delete from " + TABLE_USER);
        db.execSQL("delete from " + TABLE_CUST_VENDOR);
        db.execSQL("delete from " + TABLE_TASK_MASTER);
        db.execSQL("delete from " + TABLE_TASK_MEMBER);
        db.execSQL("delete from " + TABLE_TASK_CHAT);
        db.execSQL("delete from " + TABLE_TASK_ATTACHMENT);
        db.execSQL("delete from " + TABLE_EXPENSE);
        db.execSQL("delete from " + TABLE_TASK_STATUS);


        this.onCreate(db);
    }
}
