package com.bigbang.superteam.util;

import android.os.Environment;

public class Constant {

    /*PRODUCTION : PUBLISHED ON GOOGLE PLAY STORE */
    //public static String URL1 = "http://ec2-52-88-137-105.us-west-2.compute.amazonaws.com:8080/commonServices/";
    //public static String URL = "http://ec2-52-88-183-168.us-west-2.compute.amazonaws.com:8080/TeamWorks/";

    /*TESTER TESTING && PLAY STORE TESTING*/
//    public static String URL1 = "http://ec2-52-88-137-105.us-west-2.compute.amazonaws.com:8080/commonServices8/";
//    public static String URL = "http://ec2-52-88-183-168.us-west-2.compute.amazonaws.com:8080/TeamWorks_Test/";

    /*DEV. TESTING*/
//    public static String URL1 = "http://ec2-52-88-137-105.us-west-2.compute.amazonaws.com:8080/commonServices9/";
//    public static String URL = "http://ec2-52-88-183-168.us-west-2.compute.amazonaws.com:8080/TeamWorks2/";


    /*LOCAL URLS*/
//    public static String URL1 = "http://ec2-52-88-137-105.us-west-2.compute.amazonaws.com:8080/commonServices9/";
//    public static String URL = "http://ec2-52-88-183-168.us-west-2.compute.amazonaws.com:8080/TeamWorks5/";

    /*PAYROLL AND EXPENSES URLS*/
    //public static String URL1 = "http://ec2-52-88-137-105.us-west-2.compute.amazonaws.com:8080/commonServices4/";
    //public static String URL = "http://ec2-52-88-183-168.us-west-2.compute.amazonaws.com:8080/TeamWorks4/";

    public static String GoogleGeoCodeAPI = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    public static String GoogleGeoCodeAPIKEY = "AIzaSyD6xC-i4FnBPPJAs83NiBw2Bmchvx7CVYA";
    public static String network_error = "please check your network connectivity.";
    public static String AppName = "TeamWorks";
    public static String AppNameSuper = "SuperTeam";
    public static String DatabaseName = "teamworks.sqlite";

    //Notification Status:
    public static int StatusPending = 1;

    //Store locally:
    public static String storageDirectory = Environment.getExternalStorageDirectory() + "/" + Constant.AppNameSuper + "/";
//    public static String TrackingFile = "location.txt";

    public static String APP_VERSION = "1.0.1T";

    //Company Invitation
    public static int FROM_LOGIN = 1;
    public static int FROM_DASHBOARD = 2;

    // WorkFlow & Projects
    public static String WorkTransaction = "WorkTransaction";
    public static String WorkItemTable = "WorkItem";
    public static String ProjectTable = "ProjectItem";
    public static String UserTable = "Users";
    public static int CompanyId = 1;
    public static String AttachmentTable = "AttachmentData";
    public static String ApprovalsTable = "MasterApprovals";
    public static String ApprovalAttendanceDetail = "approvals";
    public static String AlertsTable = "Alerts_Counter";
    public static String NotificationTable = "Notification_Info";
    public static String MemberTypeAssigned = "assigned";
    public static String MemberTypeCC = "cc";

    public static final int PROJECT = 0;
    public static final int REGULAR = 1;
    public static final int ONE_TIME = 2;
    public static final int SERVICE_CALL = 3;
    public static final int SALES_CALL = 4;
    public static final int SHOPPING_PURCHASE = 5;
    public static final int COLLECTION = 6;
    public static final String WORKTYPES[] = {
            "Project", "Regular", "One Time", "Service Call", "Sales Call", "Shopping/Purchase", "Collection"
    };
    public static String DAILY = "Daily";
    public static String WEEKLY = "Weekly";
    public static String MONTHLY = "Monthly";
    public static String MANDATORY = "Mandatory";
    public static String PREFERRED = "Preferred";
    public static String REPEAT = "Repeat";
    public static String NEW = "New";
    public static String offline = "Offline";

    public static String HIGH = "High";
    public static String MEDIUM = "Medium";
    public static String LOW = "Low";
    public static int PRIORITY_ALL = 0;
    public static int PRIORITY_HIGH = 1;
    public static int PRIORITY_MEDIUM = 2;
    public static int PRIORITY_LOW = 3;

    // Leave & Attendance
    public static String OfflineDataLeaveAttendance = "LeaveAttendanceOffline";
    public static String LeaveTypes = "LeaveTypes";
    public static String AttendanceFile = "Attendance.txt";
    public static String LeaveFile = "LeaveHistory.txt";

    //Leave Fragments
    public static int FROM_ADMIN_DASHBOARD = 1;
    public static int FROM_USER_DASHBOARD = 2;
    public static int FROM_MANAGER_DASHBOARD = 3;
    public static int FROM_LEAVE_ADAPTER = 4;
    public static int FROM_APPROVAL_ADAPTER = 5;
    public static int FROM_LEAVE_HISTORY_ADAPTER = 6;

    //Database Tables:
    public static String tableUsers = "Users";
    public static String tableCustomers = "Customers";
    public static String tableVendors = "Vendors";

    public static final String Success = "Success";
    public static final String Fail = "Fail";
    public static final String InvalidToken = "Invalid Token";
    public static final String UnRegDevice = "Unregistered Device";

    public static class SHRED_PR {
        public static final String SHARE_PREF = AppNameSuper + "_preferences";
        public static final String KEY_IS_LOGGEDIN = "is_loggedin";
        public static final String KEY_GCM_ID = "gcm_id";
        //        public static final String KEY_IS_SERVICE_ON = "is_service_on";
        public static final String KEY_IS_COMPANY_CREATED = "is_company_created";
        public static final String KEY_IS_COMPANY_SETUP = "is_company_setup";
        public static final String KEY_RELOAD = "reload";
        public static final String KEY_RELOAD_CUST_VEND = "reload_cust_vend";
        public static final String KEY_USER_PRIVILAGES = "user_privilages";
        public static final String KEY_COMPANY_PRIVILAGES = "company_privilages";
        public static final String KEY_ALL_PRIVILAGES = "all_privilages";
        public static final String KEY_COMPANY_SETUP_DATA = "company_setup_data";
        public static final String KEY_LASTSYNC_APPROVAL_TIME = "lastsync_approval_time";
        public static final String KEY_LASTSYNC_NOTIFICATION_TIME = "lastsync_notification_time";
        public static final String KEY_LIST = "list";
        public static final String KEY_ASSIGNEE_CC = "assignee_cc";
        public static final String KEY_TASKLIST = "tasklist";
        public static final String KEY_DAYSLIST = "dayslist";
        public static final String KEY_VENDOR_LIST = "vendor_list";
        public static final String KEY_AUTO_LEAVE_UPDATE = "AutoLeaveUpdate";

        //Comapny:
        public static final String KEY_COMPANY_NAME = "company_name";
        public static final String KEY_COMPANY_LOGO = "company_logo";
        public static final String KEY_COMPANY_MOBILE = "company_mobile";
        public static final String KEY_COMPANY_LANDLINE = "company_landline";
        public static final String KEY_COMPANY_EMAIL = "company_email";
        public static final String KEY_COMPANY_ID = "company_id";
        public static final String KEY_COMPANY_DESC = "company_desc";
        public static final String KEY_COMPANY_Owner_ID = "Owner_ID";
        public static final String KEY_COMPANY_Created_By = "Created_By";
        public static final String KEY_COMPANY_Type = "Type";
        public static final String KEY_COMPANY_AddressList = "AddressList";
        public static final String KEY_COMPANY = "Company";
        public static final String KEY_COMPANY_STARTTIME = "start_time";
        public static final String KEY_COMPANY_ENDTIME = "end_time";
        public static final String KEY_COMPANY_LEAVES = "company_leaves";

        public static final String KEY_TRACKING_STARTTIME = "trackingStartTime";
        public static final String KEY_TRACKING_ENDTIME = "trakingEndTime";

        //User:
        public static final String KEY_TOKEN = "token";
        public static final String KEY_ROLE = "role";
        public static final String KEY_ROLE_ID = "role_id";
        public static final String KEY_USERID = "user_id";
        public static final String KEY_FIRSTNAME = "fname";
        public static final String KEY_LASTNAME = "lname";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_TELEPHONE = "telephone";
        public static final String KEY_Picture = "picture";
        public static final String KEY_PermanentAddress = "permanentAddress";
        public static final String KEY_TemporaryAddress = "temporaryAddress";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_REPLACE_USERNAME = "replace_username";
        public static final String KEY_TEMP_LATITUDE = "temp_latitude";
        public static final String KEY_TEMP_LONGITUDE = "temp_longitude";
        public static final String KEY_USER_LOCATION_DETAILS = "location_details";
        public static final String KEY_TEMP_ADDRESS = "address";
        public static final String KEY_TEMP_POSITION = "position";
        public static final String KEY_TEMP_CREATE_TYPE = "create_type";
        public static final String KEY_DELETE = "delete";

        //Tracking:
        public static final String KEY_TIME_PERIOD_TRACKING = "time_period_tracking";
        public static final String KEY_WORKUPDATE_NO = "work_update_id";
        public static final String KEY_WORKUPDATE_STRING = "work_update_string";

        //Task Filter:
        public static final String KEY_FILTER_PRIORITY = "filter_priority";
        public static final String KEY_FILTER_TASK_TYPE = "filter_task_type";
        public static final String KEY_FILTER_ASSIGNED = "filter_assigned";

        //Payroll
        public static final String KEY_PAYROLL_ACTIVE = "payroll_active";
        public static final String KEY_METRO_CITY = "metro_city";
        public static final String KEY_BASIC_SALARY = "basic_salary";
        public static final String KEY_HRA = "hra";
        public static final String KEY_CONVEYANCE = "conveyance";
        public static final String KEY_MEDICAL = "medical";
        public static final String KEY_VARIABLE_AMOUNT = "variable_amount";
        public static final String KEY_EMPLOYEE_CONTRIBUTION = "employee_contribution";
        public static final String KEY_EMPLOYER_CONTRIBUTION = "employer_contribution";
        public static final String KEY_WORKING_DAYS_POLICY = "working_day_policy";
        public static final String KEY_SPECIAL_ALLOWANCES = "special_allowances";
        public static final String KEY_LATEEARLY_DEDUCTION_ONE = "lateeary_1_hrs_deduction";
        public static final String KEY_LATEEARLY_DEDUCTION_TWO = "lateeary_2_hrs_deduction";
        public static final String KEY_LATEEARLY_DEDUCTION_THREE = "lateeary_3_hrs_deduction";
        public static final String KEY_LATEEARLY_DEDUCTION_FOUR = "lateeary_4_hrs_deduction";
        public static final String KEY_LATEEARLY_DEDUCTION_FIVE = "lateeary_5_hrs_deduction";
        public static final String KEY_WORKING_POLICY_TYPE_ID = "working_policy_type_id";
        public static final String KEY_LATE_ALLOWED_TIME = "late_alllowed_time";
        public static final String KEY_HRS_CALCULATION_TYPE = "hrs_cal_type";
        public static final String KEY_PAYSLIP_MONTH = "payslip_month";
        public static final String KEY_PAYSLIP_YEAR = "payslip_year";

    }

    // GCM MESSAGE TYPES
    public static final int PROVIDE_CURRENT_LOCATION = 1;
    public static final int UPDATE_CURRENT_LOCATION = 2;
    public static final int NEW_WORKITEM = 3;
    public static final int UPDATE_WORKITEM = 4;
    public static final int ATTENDANCE_APPROVAL_TO_ADMIN = 5;
    public static final int ATTENDANCE_APPROVAL_TO_USER = 6;
    public static final int AUTO_CHECKIN = 7;
    public static final int AUTO_CHECKOUT = 8;
    public static final int ATTACHMENTS = 10;
    public static final int APPROVE_WORKITEMUPDATE = 11;
    public static final int REJECT_WORKITEMUPDATE = 12;
    public static final int LEAVE_APPROVAL_TO_ADMIN = 13;
    public static final int LEAVE_APPROVAL_TO_USER = 14;
    public static final int AUTO_LOGOUT = 15;
    public static final int CREATE_NEWWORKITEM = 16;
    public static final int REJECT_NEWWORKITEM = 17;
    public static final int LEAVE_WITHDRAW_TO_ADMIN = 18;
    public static final int NEW_USER_ADDED = 19;
    public static final int LEAVE_CANCELLED_TO_ADMIN = 20;
    public static final int CREATE_NEWPRJECT = 21;
    public static final int APPROVAL_NEWPRJECT = 22;
    public static final int REJECT_NEWPRJECT = 23;
    public static final int CHANGE_MOBILE_APPROVAL_TO_USER = 24;
    public static final int CHANGE_MOBILE_APPROVAL_TO_ADMIN = 25;
    public static final int EDIT_WORKITEM_APPROVAL = 26;
    public static final int USER_INVITATION_TO_ADMIN = 27;
    public static final int USER_RESIGN_APPROVAL_TO_ADMIN = 28;
    public static final int USER_RESIGN_APPROVAL_TO_USER = 29;
    public static final int INVITE_USER = 30;
    public static final int DELETE_USER = 31;
    public static final int DELETE_COMPANY = 32;
    public static final int UPDATETIME_APPROVAL_TO_ADMIN = 33;
    public static final int UPDATETIME_APPROVAL_TO_USER = 34;

    public static final int UPDATE_COMPANYINFO = 35;
    public static final int GET_COMPANY_PRIVILAGES = 36;
    public static final int GET_USER_PRIVILAGES_MANAGER = 37;
    public static final int GET_USER_PRIVILAGES_TEAMMEMBER = 38;
    public static final int EDIT_PROJECT_APPROVAL = 39; // - Project Approval
    public static final int PROJECT_REJECTED = 40;// - Rejected
    public static final int PROJECT_APPROVED = 41;// - Approved
    public static final int UPDATE_CUSTOMER_APPROVAL_TO_ADMIN = 42;
    public static final int UPDATE_CUSTOMER_APPROVAL_TO_USER = 43;
    public static final int UPDATE_ROLE_OR_MANAGER = 44;
    public static final int ATTENDANCE_CUSTOMER_VENDOR_TO_ADMIN = 45;
    public static final int ATTENDANCE_CUSTOMER_VENDOR_TO_USER = 46;
    public static final int UPDATE_COMPANY = 47;
    public static final int UPDATE_CUSTOMER_VENDOR = 48;
    public static final int UPDATE_USER_COMPANYINFO = 49;
    public static final int WORKITEM_MODIFIED = 52;
    public static final int UPDATE_USER = 53;
    public static final int MODIFIED_PROJECT = 54;
    public static final int LEAVE_UPDATE_TO_USER = 55;
    public static final int NEW_REPORTING_MEMBER = 56;
    public static final int CHANGE_DEVICE_REQUEST = 57;
    public static final int CHANGE_DEVICE_ACK = 58;
    public static final int USER_PAYSLIP = 59;
    public static final int ADMIN_PAYROLL = 60;
    public static final int EXPENSE_APPROVAL = 61;
    public static final int EXPENSE_NOTIFICATION = 62;

    /*OFFICE USE*/
    public static String URL1 = "http://ec2-52-88-137-105.us-west-2.compute.amazonaws.com:8080/commonServices9/";
    public static String URL = "http://ec2-52-88-183-168.us-west-2.compute.amazonaws.com:8080/TeamWorks5/";
}
