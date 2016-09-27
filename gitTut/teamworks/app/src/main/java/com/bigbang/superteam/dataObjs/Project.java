package com.bigbang.superteam.dataObjs;

import java.io.Serializable;

/**
 * Created by USER 3 on 19/05/2015.
 */
public class Project implements Serializable {

    public static String PROJECT_ID = "Project_Id";
    public static String USER_CODE = "User_Code";
    public static String PROJECT_NAME = "Project_Name";
    public static String DESCRIPTION = "Description";
    public static String PRIORITY = "Priority";
    public static String START_DATE = "Start_Date";
    public static String END_DATE = "End_Date";
    public static String PROJECT_IMAGE = "Project_Image";
    public static String ESTIMATED_TIME = "Estimated_Time";
    public static String ASSIGNED_TO = "Assigned_To";
    public static String CC_TO = "Cc_To";
    public static String PROJECT_TASKS = "Project_Tasks";
    public static String STATUS = "Status";
    public static String OWNER = "Owner";

    public String ProjectTasks;
    public String Status;
    private int Project_Id;
    private int userCode;
    private String project_name;
    private String description;
    private String Priority;
    private String startDate;
    private String endDate;
    private String ProjectImage;
    private String ProjectImagePath;
    private String EstimatedWorkTime;
    private String ProjectAssignedTo;
    private String ProjectCCTo;
    private String Owner;

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public String getProjectTasks() {
        return ProjectTasks;
    }

    public void setProjectTasks(String projectTasks) {
        ProjectTasks = projectTasks;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getProject_Id() {
        return Project_Id;
    }

    public void setProject_Id(int project_Id) {
        Project_Id = project_Id;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String priority) {
        Priority = priority;
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

    public String getProjectImage() {
        return ProjectImage;
    }

    public void setProjectImage(String projectImage) {
        ProjectImage = projectImage;
    }

    public String getProjectImagePath() {
        return ProjectImagePath;
    }

    public void setProjectImagePath(String projectImagePath) {
        ProjectImagePath = projectImagePath;
    }

    public String getEstimatedWorkTime() {
        return EstimatedWorkTime;
    }

    public void setEstimatedWorkTime(String estimatedWorkTime) {
        EstimatedWorkTime = estimatedWorkTime;
    }

    public String getProjectAssignedTo() {
        return ProjectAssignedTo;
    }

    public void setProjectAssignedTo(String projectAssignedTo) {
        ProjectAssignedTo = projectAssignedTo;
    }

    public String getProjectCCTo() {
        return ProjectCCTo;
    }

    public void setProjectCCTo(String projectCCTo) {
        ProjectCCTo = projectCCTo;
    }


}
