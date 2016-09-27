package com.bigbang.superteam.dataObjs;

/**
 * Created by USER 7 on 5/14/2015.
 */
public class ApprovalModel {

    String approvalText;
    String approvalMsg;
    String approvalTime;

    public String getApprovalID() {
        return approvalID;
    }

    public void setApprovalID(String approvalID) {
        this.approvalID = approvalID;
    }

    String approvalID;
    int userId;
    String isManualAttendance;
    int isLateComing;
    int isEarlyGoing;
    int lateByMins;
    int earlyByMins;

    String userName;
    String imageURL;
    String date;
    String timeIn;
    String timeOut;
    String reason;
    String locationType;

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    String attendanceType;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getIsManualAttendance() {
        return isManualAttendance;
    }

    public void setIsManualAttendance(String isManualAttendance) {
        this.isManualAttendance = isManualAttendance;
    }

    public int getIsLateComing() {
        return isLateComing;
    }

    public void setIsLateComing(int isLateComing) {
        this.isLateComing = isLateComing;
    }

    public int getIsEarlyGoing() {
        return isEarlyGoing;
    }

    public void setIsEarlyGoing(int isEarlyGoing) {
        this.isEarlyGoing = isEarlyGoing;
    }

    public int getLateByMins() {
        return lateByMins;
    }

    public void setLateByMins(int lateByMins) {
        this.lateByMins = lateByMins;
    }

    public int getEarlyByMins() {
        return earlyByMins;
    }

    public void setEarlyByMins(int earlyByMins) {
        this.earlyByMins = earlyByMins;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }



    public String getApprovalText() {
        return approvalText;
    }

    public void setApprovalText(String approvalText) {
        this.approvalText = approvalText;
    }

    public String getApprovalMsg() {
        return approvalMsg;
    }

    public void setApprovalMsg(String approvalMsg) {
        this.approvalMsg = approvalMsg;
    }

    public String getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(String approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
