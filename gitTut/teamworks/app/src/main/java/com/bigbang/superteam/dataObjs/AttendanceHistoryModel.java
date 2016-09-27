package com.bigbang.superteam.dataObjs;

/**
 * Created by USER 7 on 5/20/2015.
 */
public class AttendanceHistoryModel {

    String userId;
    String requestTime;
    String requestDate;
    String requestStatus;
    String attendanceType;
    String reason;
    String imageUrl;
    String locationType;
    String attendanceID;
    String isManualAttendance;
    String userName;
    String timeIN;
    String timeOut;
    String newTimeIn;
    String newTimeOut;
    String updateTimeIn;
    String updateTimeOut;

    Boolean isPresent;
    Boolean checkInApprovalState;
    Boolean checkOutApprovalState;
    Boolean ManualApprovalState;
    String jsonObj;

    public Boolean getManualApprovalState() {
        return ManualApprovalState;
    }

    public void setManualApprovalState(Boolean manualApprovalState) {
        ManualApprovalState = manualApprovalState;
    }

    public Boolean getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(Boolean isPresent) {
        this.isPresent = isPresent;
    }

    public Boolean getCheckInApprovalState() {
        return checkInApprovalState;
    }

    public void setCheckInApprovalState(Boolean checkInApprovalState) {
        this.checkInApprovalState = checkInApprovalState;
    }

    public Boolean getCheckOutApprovalState() {
        return checkOutApprovalState;
    }

    public void setCheckOutApprovalState(Boolean checkOutApprovalState) {
        this.checkOutApprovalState = checkOutApprovalState;
    }

    public String getJsonObj() {
        return jsonObj;
    }

    public void setJsonObj(String jsonObj) {
        this.jsonObj = jsonObj;
    }

    public String getUpdateTimeIn() {
        return updateTimeIn;
    }

    public void setUpdateTimeIn(String updateTimeIn) {
        this.updateTimeIn = updateTimeIn;
    }

    public String getUpdateTimeOut() {
        return updateTimeOut;
    }

    public void setUpdateTimeOut(String updateTimeOut) {
        this.updateTimeOut = updateTimeOut;
    }

    public String getNewTimeIn() {
        return newTimeIn;
    }

    public void setNewTimeIn(String newTimeIn) {
        this.newTimeIn = newTimeIn;
    }

    public String getNewTimeOut() {
        return newTimeOut;
    }

    public void setNewTimeOut(String newTimeOut) {
        this.newTimeOut = newTimeOut;
    }

    public String getTimeIN() {
        return timeIN;
    }

    public void setTimeIN(String timeIN) {
        this.timeIN = timeIN;
    }

       public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIsManualAttendance() {
        return isManualAttendance;
    }

    public void setIsManualAttendance(String isManualAttendance) {
        this.isManualAttendance = isManualAttendance;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getAttendanceID() {
        return attendanceID;
    }

    public void setAttendanceID(String attendanceID) {
        this.attendanceID = attendanceID;
    }
}
