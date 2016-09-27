package com.bigbang.superteam.dataObjs;

import java.io.Serializable;

/**
 * Created by USER 3 on 19/05/2015.
 */
public class Attachment implements Serializable {

    public static String ATTACHMENT_ID = "Attachment_Id";
    public static String USER_ID = "User_Id";
    public static String WORKITEM_ID = "WorkItem_Id";
    public static String PATH = "Path";
    public static String UPLOADEDDOWNLOADED = "Uploaded_Downloaded";
    String AttachmentId;
    String UserId;
    String WorkItemId;
    String Path;
    String UploadedDownloaded;

    public String getAttachmentId() {
        return AttachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        AttachmentId = attachmentId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getWorkItemId() {
        return WorkItemId;
    }

    public void setWorkItemId(String workItemId) {
        WorkItemId = workItemId;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getUploadedDownloaded() {
        return UploadedDownloaded;
    }

    public void setUploadedDownloaded(String uploadedDownloaded) {
        UploadedDownloaded = uploadedDownloaded;
    }


}
