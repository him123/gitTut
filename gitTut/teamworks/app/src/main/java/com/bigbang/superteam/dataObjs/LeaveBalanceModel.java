package com.bigbang.superteam.dataObjs;

/**
 * Created by USER 7 on 6/10/2015.
 */
public class LeaveBalanceModel {
    String leaveId;
    String leaveType;
    String numberOfLeaves;
    String availableLeaves;
    String usedLeaves;

    public String getAvailableLeaves() {
        return availableLeaves;
    }

    public void setAvailableLeaves(String availableLeaves) {
        this.availableLeaves = availableLeaves;
    }

    public String getUsedLeaves() {
        return usedLeaves;
    }

    public void setUsedLeaves(String usedLeaves) {
        this.usedLeaves = usedLeaves;
    }

    public String getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(String leaveId) {
        this.leaveId = leaveId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getNumberOfLeaves() {
        return numberOfLeaves;
    }

    public void setNumberOfLeaves(String numberOfLeaves) {
        this.numberOfLeaves = numberOfLeaves;
    }


}
