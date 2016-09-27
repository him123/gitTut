package com.bigbang.superteam.dataObjs;

/**
 * Created by USER 7 on 6/6/2015.
 */
public class LeaveHistoryModel {

    String userId;
    String approvalId;
    String startDate;
    String endDate1;
    String imageUrl;
    String reason;
    String leaveStatus;
   // String activityName;
    String TransactionId;
    int fromActivity;

    public String getLeaveDay() {
        return leaveDay;
    }

    public void setLeaveDay(String leaveDay) {
        this.leaveDay = leaveDay;
    }

    String leaveDay;

    public int getFromActivity() {
        return fromActivity;
    }

    public void setFromActivity(int fromActivity) {
        this.fromActivity = fromActivity;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

   /* public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }*/

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(String approvalId) {
        this.approvalId = approvalId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

  /*  public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }*/

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLeaveStatus() {
        return leaveStatus;
    }

    public void setLeaveStatus(String leaveStatus) {
        this.leaveStatus = leaveStatus;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    String leaveType;
    String userName;


}
