package com.bigbang.superteam.task.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.task.model.TaskAttachment;
import com.bigbang.superteam.util.SQLiteHelper;

/**
 * Created by User on 9/3/2016.
 */
public class TaskAttachmentDAO extends TeamWorksDBDAO {

    public TaskAttachmentDAO(Context contxt) {
        super(contxt);
    }

    public TaskAttachmentDAO() {
        super();
    }

    public long save(TaskAttachment taskAttachment) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.TA_Id, taskAttachment.id);
        values.put(SQLiteHelper.TA_ATTACHMENT_ID, taskAttachment.attachmentid);
        values.put(SQLiteHelper.TA_TASK_ID, taskAttachment.taskid);
        values.put(SQLiteHelper.TA_PATH, taskAttachment.path);
        values.put(SQLiteHelper.TA_LAST_MODIFIED_BY, taskAttachment.lastmodifiedby);
        values.put(SQLiteHelper.TA_LAST_MODIFIED, taskAttachment.lastmodified);
        values.put(SQLiteHelper.TA_TASK_UPDATE_ID, taskAttachment.taskupdateid);

        status = database.insert(SQLiteHelper.TABLE_TASK_ATTACHMENT, null, values);
        if (status != -1)
            Log.d("Insert Task Attachment", "*********************************************successfull");
        else
            Log.d("Insert Task Attachment", "*********************************************unsuccessfull");

        return status;
    }

    public TaskAttachment getTaskAttachmentData() {
        TaskAttachment taskAttachment = new TaskAttachment();

        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK_ATTACHMENT,
                new String[]{
                        SQLiteHelper.TA_Id,//0
                        SQLiteHelper.TA_ATTACHMENT_ID,//1
                        SQLiteHelper.TA_TASK_ID,//2
                        SQLiteHelper.TA_PATH,//3
                        SQLiteHelper.TA_LAST_MODIFIED_BY,//4
                        SQLiteHelper.TA_LAST_MODIFIED,//5
                        SQLiteHelper.TA_TASK_UPDATE_ID,//6
                }, null, null, null,
                null, null);

        while (cursor.moveToNext()) {
            taskAttachment.id = cursor.getInt(0);
            taskAttachment.attachmentid = cursor.getInt(1);
            taskAttachment.taskid = cursor.getInt(2);
            taskAttachment.path = cursor.getString(3);
            taskAttachment.lastmodifiedby = cursor.getString(4);
            taskAttachment.lastmodified = cursor.getString(5);
            taskAttachment.taskupdateid = cursor.getString(6);
        }
        return taskAttachment;
    }

    public int delete(int local_id) {
        return database.delete(SQLiteHelper.TABLE_TASK_ATTACHMENT, "id =?",
                new String[]{local_id + ""});
    }
}
