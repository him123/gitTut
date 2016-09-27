package com.bigbang.superteam.dataObjs;

import android.database.Cursor;

import com.bigbang.superteam.util.Util;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by USER 3 on 22/04/2015.
 */
public class WorkItem implements Serializable, Comparable {

    public static String TASK_ID = "Task_Id";
    public static String STATUS = "Work_Status";
    public static String USER_CODE = "User_Code";
    public static String TASK_NAME = "Task_Name";
    public static String DESCRIPTION = "Description";
    public static String BUDGET = "Budget";
    public static String PRIORITY = "Priority";
    public static String ATTACHMENTS = "Attachments";
    public static String WORK_LOCATION = "Work_Location";
    public static String WORK_LONGITUDE = "Work_Longitude";
    public static String WORK_LATITUDE = "Work_Latitude";
    public static String START_DATE = "Start_Date";
    public static String END_DATE = "End_Date";
    public static String TASK_IMAGE = "Task_Image";
    public static String ESTIMATED_TIME = "Estimated_Time";
    public static String ASSIGNED_TO = "Assigned_To";
    public static String CC_TO = "Cc_To";
    public static String TASK_TYPE = "Task_Type";
    public static String PROJECT_CODE = "Project_Code";
    //    public static String TASK_BEFORE = "Task_Before";
//    private String taskCodeBefore;
    public static String TASK_AFTER = "Task_After";
    // Regular Type
    public static String FREQUENCY = "Frequency";
    public static String DAYCODES_SELECTED = "Daycodes_Selected";
    // Sales/ Service
    public static String CUSTOMER_NAME = "Customer_Name";
    public static String CUSTOMER_CONTACT = "Customer_Contact";
    public static String CUSTOMER_TYPE = "Customer_Type";
    public static String PAST_HISTORY = "Past_History";
    ///Sales Purchase
    public static String VENDOR_PREFERENCE = "Vendor_Preference";
    public static String VENDOR_NAME = "Vendor_Name";
    public static String ADVANCE_PAID = "Advance_Paid";
    //Collection
    public static String INVOICE_AMOUNT = "Invoice_Amount";
    public static String INVOICE_DATE = "Invoice_Date";
    public static String DUE_DATE = "Due_Date";
    public static String OUTSTANDING_AMT = "Outstanding_Amt";
    private int taskCode;
    private String Status;
    private int userCode;
    private String title;
    private String description;
    private String budget;
    public boolean selected;
    public static String SELECTED = "selected";

    // Project Type
    private String Priority;
    private String attachments;
    private String workLocation;
    private String Longitude;
    private String Latitude;
    private String startDate;
    private String endDate;
    private String taskImage;
    private String taskImagePath;
    private String estimatedWorkTime;
    private String taskAssignedTo;
    private String taskCCTo;
    private String taskType;
    private int projectCode;
    private String taskCodeAfter;
    private String frequency;
    private String dayCodesSelected;
    private String customerName;
    private String customerContact;
    private String customerType;
    private String PastHistory;
    private String spVendorPreference;
    private String spVendorName;
    private String spAdvancePaid;
    private String InvoiceAmount;
    private String InvoiceDate;

    private String DueDate;
    private String OutStandingAmt;
    public int alertCounter;

    public static WorkItem getWorkItemfromCursor(Cursor crsr, int index) {
        WorkItem item = new WorkItem();
        crsr.moveToPosition(index);
        item.setTaskCode(crsr.getInt(crsr.getColumnIndex(WorkItem.TASK_ID)));
        item.setUserCode(crsr.getInt(crsr.getColumnIndex(WorkItem.USER_CODE)));
        item.setTaskImage(crsr.getString(crsr.getColumnIndex(WorkItem.TASK_IMAGE)));
        item.setTaskImagePath(crsr.getString(crsr.getColumnIndex(WorkItem.TASK_IMAGE)));

        item.setTitle(crsr.getString(crsr.getColumnIndex(WorkItem.TASK_NAME)));
        item.setDescription(crsr.getString(crsr.getColumnIndex(WorkItem.DESCRIPTION)));
        item.setBudget(crsr.getString(crsr.getColumnIndex(WorkItem.BUDGET)));
        item.setPriority(crsr.getString(crsr.getColumnIndex(WorkItem.PRIORITY)));
        item.setWorkLocation(crsr.getString(crsr.getColumnIndex(WorkItem.WORK_LOCATION)));
        item.setLatitude(crsr.getString(crsr.getColumnIndex(WorkItem.WORK_LATITUDE)));
        item.setLongitude(crsr.getString(crsr.getColumnIndex(WorkItem.WORK_LONGITUDE)));
        item.setStartDate(crsr.getString(crsr.getColumnIndex(WorkItem.START_DATE)));
        item.setEndDate(crsr.getString(crsr.getColumnIndex(WorkItem.END_DATE)));
        item.setEstimatedWorkTime(crsr.getString(crsr.getColumnIndex(WorkItem.ESTIMATED_TIME)));
        item.setTaskAssignedTo(crsr.getString(crsr.getColumnIndex(WorkItem.ASSIGNED_TO)));
        item.setTaskCCTo(crsr.getString(crsr.getColumnIndex(WorkItem.CC_TO)));
        item.setTaskType(crsr.getString(crsr.getColumnIndex(WorkItem.TASK_TYPE)));

        item.setProjectCode(crsr.getInt(crsr.getColumnIndex(WorkItem.PROJECT_CODE)));
        //   workItem.setTaskCodeBefore(crsr.getString(crsr.getColumnIndex(WorkItem.TASK_BEFORE)));
        item.setTaskCodeAfter(crsr.getString(crsr.getColumnIndex(WorkItem.TASK_AFTER)));

        item.setFrequency(crsr.getString(crsr.getColumnIndex(WorkItem.FREQUENCY)));
        item.setDayCodesSelected(crsr.getString(crsr.getColumnIndex(WorkItem.DAYCODES_SELECTED)));


        item.setCustomerName(crsr.getString(crsr.getColumnIndex(WorkItem.CUSTOMER_NAME)));
        item.setCustomerContact(crsr.getString(crsr.getColumnIndex(WorkItem.CUSTOMER_CONTACT)));
        item.setCustomerType(crsr.getString(crsr.getColumnIndex(WorkItem.CUSTOMER_TYPE)));
        item.setInvoiceAmount(crsr.getString(crsr.getColumnIndex(WorkItem.INVOICE_AMOUNT)));
        item.setInvoiceDate(crsr.getString(crsr.getColumnIndex(WorkItem.INVOICE_DATE)));
        item.setDueDate(crsr.getString(crsr.getColumnIndex(WorkItem.DUE_DATE)));
        item.setOutStandingAmt(crsr.getString(crsr.getColumnIndex(WorkItem.OUTSTANDING_AMT)));

        item.setPastHistory(crsr.getString(crsr.getColumnIndex(WorkItem.PAST_HISTORY)));
        item.setSpVendorName(crsr.getString(crsr.getColumnIndex(WorkItem.VENDOR_NAME)));
        item.setSpVendorPreference(crsr.getString(crsr.getColumnIndex(WorkItem.VENDOR_PREFERENCE)));
        item.setSpAdvancePaid(crsr.getString(crsr.getColumnIndex(WorkItem.ADVANCE_PAID)));
        item.setStatus(crsr.getString(crsr.getColumnIndex(WorkItem.STATUS)));
        item.setAttachments(crsr.getString(crsr.getColumnIndex(WorkItem.ATTACHMENTS)));
        return item;
    }

    public static void PrintWorkItem(WorkItem workItem) {
        System.out.println("Code :" + workItem.taskCode +
                "\n status :" + workItem.Status +
                "\n user code: " + workItem.userCode +
                "\n Title: " + workItem.title +
                "\n Desc : " + workItem.description +
                "\n Budget : " + workItem.budget +
                "\n Priority : " + workItem.Priority +
                "\n Attachments : " + workItem.attachments +
                "\n Location : " + workItem.workLocation +
                "\n Longi : " + workItem.Longitude +
                "\n Lati : " + workItem.Latitude +
                "\n Start date : " + workItem.startDate +
                "\n End date : " + workItem.endDate +
                "\n task Image : " + workItem.taskImage +
                "\n image path : " + workItem.taskImagePath +
                "\n estimated : " + workItem.estimatedWorkTime +
                "\n assign to : " + workItem.taskAssignedTo +
                "\n cc to : " + workItem.taskCCTo +
                "\n type : " + workItem.taskType +
                "\n project code : " + workItem.projectCode +
                "\n tasks after : " + workItem.taskCodeAfter +
                "\n frequency : " + workItem.frequency +
                "\n daycodes : " + workItem.dayCodesSelected +
                "\n cust name : " + workItem.customerName +
                "\n cust conta : " + workItem.customerContact +
                "\n cust type : " + workItem.customerType +
                "\n history : " + workItem.PastHistory +
                "\n sp vend pref. : " + workItem.spVendorPreference +
                "\n sp vend name : " + workItem.spVendorName +
                "\n sp adv paid : " + workItem.spAdvancePaid +
                "\n invoice amt : " + workItem.InvoiceAmount +
                "\n invoice date : " + workItem.InvoiceDate +
                "\n Due date : " + workItem.DueDate +
                "\n outstanding amt : " + workItem.OutStandingAmt);
    }

    public int getAlertCounter() {
        return alertCounter;
    }

    public void setAlertCounter(int alertCounter) {
        this.alertCounter = alertCounter;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTaskCodeAfter() {
        return taskCodeAfter;
    }

    public void setTaskCodeAfter(String taskCodeAfter) {
        this.taskCodeAfter = taskCodeAfter;
    }

    public int getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(int taskCode) {
        this.taskCode = taskCode;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTaskImage() {
        return taskImage;
    }

    public void setTaskImage(String taskImage) {
        this.taskImage = taskImage;
    }

    public String getTaskImagePath() {
        return taskImagePath;
    }

    public void setTaskImagePath(String taskImagePath) {
        this.taskImagePath = taskImagePath;
    }

    public String getEstimatedWorkTime() {
        return estimatedWorkTime;
    }

    public void setEstimatedWorkTime(String estimatedWorkTime) {
        this.estimatedWorkTime = estimatedWorkTime;
    }

    public String getTaskAssignedTo() {
        return taskAssignedTo;
    }

    public void setTaskAssignedTo(String taskAssignedTo) {
        this.taskAssignedTo = taskAssignedTo;
    }

    public String getTaskCCTo() {
        return taskCCTo;
    }

    public void setTaskCCTo(String taskCCTo) {
        this.taskCCTo = taskCCTo;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public int getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(int projectCode) {
        this.projectCode = projectCode;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDayCodesSelected() {
        return dayCodesSelected;
    }

    public void setDayCodesSelected(String dayCodesSelected) {
        this.dayCodesSelected = dayCodesSelected;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getPastHistory() {
        return PastHistory;
    }

    public void setPastHistory(String pastHistory) {
        PastHistory = pastHistory;
    }

    public String getSpVendorPreference() {
        return spVendorPreference;
    }

    public void setSpVendorPreference(String spVendorPreference) {
        this.spVendorPreference = spVendorPreference;
    }

    public String getSpVendorName() {
        return spVendorName;
    }

    public void setSpVendorName(String spVendorName) {
        this.spVendorName = spVendorName;
    }

    public String getSpAdvancePaid() {
        return spAdvancePaid;
    }

    public void setSpAdvancePaid(String spAdvancePaid) {
        this.spAdvancePaid = spAdvancePaid;
    }

    public String getInvoiceAmount() {
        return InvoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        InvoiceAmount = invoiceAmount;
    }

    public String getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        InvoiceDate = invoiceDate;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public String getOutStandingAmt() {
        return OutStandingAmt;
    }

    public void setOutStandingAmt(String outStandingAmt) {
        OutStandingAmt = outStandingAmt;
    }


    @Override
    public int compareTo(Object o) {
        int alert = ((WorkItem) o).getAlertCounter();
        if (alert > 0 || this.getAlertCounter() > 0) {
            return alert - this.getAlertCounter();
        }
        try {
            Date d1 = Util.sdf.parse(((WorkItem) o).getEndDate());
            Date d2 = Util.sdf.parse(this.getEndDate());
            if (d1.before(d2)) return 1;
            if (d2.before(d1)) return -1;
            return 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return alert - this.getAlertCounter();
        }
    }

}
