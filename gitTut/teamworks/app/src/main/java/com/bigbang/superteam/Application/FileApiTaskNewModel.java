package com.bigbang.superteam.Application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bigbang.superteam.dataObjs.OfflineWork;
import com.bigbang.superteam.task.HandleResponse;
import com.bigbang.superteam.task.database.TaskDAO;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.workitem.HandleResponseWorkItems;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by USER 3 on 28/05/2015.
 */
public class FileApiTaskNewModel extends AsyncTask<Void, String, String> {
    String FilePath;
    String taskData, type;
    String ApiName;

    SQLiteDatabase db;
    Context context;
    int offLineId = 0;
    String TAG = "FileApiTaskNewModel";

    public FileApiTaskNewModel(String Path,
                               String taskData,
                               String type,
                               String API,
                               SQLiteDatabase db,
                               Context context, int offLineid) {
        this.FilePath = Path;
        this.taskData = taskData;
        this.type = type;
        this.context = context;
        this.db = db;
        this.offLineId = offLineid;
        ApiName = API;
    }

    @Override
    protected String doInBackground(Void... params) {
        String resp = "";
        if (Util.isOnline(context)) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = null;
                HttpPost poster = new HttpPost(ApiName);

                FileBody fbody = null;
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                Log.i("filePath", "" + FilePath);
                if (FilePath != null && FilePath.length() > 5) {
                    File image = new File(FilePath);
                    fbody = new FileBody(image);
                    entity.addPart("File", fbody);
                }
                entity.addPart("JSON", new StringBody(taskData));
                entity.addPart("TokenID", new StringBody(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_TOKEN)));
                entity.addPart("UserID", new StringBody(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID)));
                entity.addPart("CompanyID", new StringBody(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_ID)));
                poster.setEntity(entity);

                response = client.execute(poster);

                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity()
                                .getContent()));
                String line = null;
                while ((line = rd.readLine()) != null) {
                    resp += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
                /// IF Exception occurs call will be saved to offline database
                if (offLineId <= 0)
                    OfflineWork.StoreData(FilePath, taskData, type, ApiName, db);
                return Constant.offline;
            }
            Log.d("Resp Upload", "" + resp);
            return resp;
        } else {
            if (offLineId <= 0)
                OfflineWork.StoreData(FilePath, taskData, type, ApiName, db);
            return Constant.offline;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (response.equals(Constant.offline)) {
            Intent i = new Intent("android.intent.action.MAIN").putExtra("some_msg", "I will be sent!");
            context.sendBroadcast(i);
        } else {
            try {
                JSONObject jObj = new JSONObject(response);
                String status = jObj.optString("status");
                if (status.equals(Constant.InvalidToken)) {
                    TeamWorkApplication.LogOutClear((Activity) context);
                    return;
                }

                if (status.equals("Success")) {
                    handleResponse(response);

                    //delete from offline
                    deleteFromOffline(db, offLineId);
                } else {
                    //delete from task model
                    if (offLineId == 0)
                        db.delete(SQLiteHelper.TABLE_TASK_MASTER, SQLiteHelper.TK_ID + " = ?", new String[]{offLineId + ""});
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (offLineId <= 0)
                    OfflineWork.StoreData("", taskData, type, Constant.URL + "addTask", db);
            }
        }
    }

    private static void deleteFromOffline(SQLiteDatabase db, int offlineId) {
        if (offlineId != 0) {
            db.delete(OfflineWork.OFFLINETABLE, OfflineWork.ID + " = ?", new String[]{offlineId + ""});
            Log.d("Removed", " Removed Entry from Offline Table");
        }
    }

    private void handleResponse(String response) {
        try {
            int Id = Integer.parseInt(type);
            switch (Id) {
                case 101:
                    HandleResponse.handleAddTaskResponse(response, context, new JSONObject(taskData).getString("id"));
                    break;

                case 103:
                    HandleResponse.handleChatAddResponse(response, context, String.valueOf(offLineId));
                    break;
                    /*case 106:
                        HandleResponseWorkItems.HandleCreateProject(FilePath, ApiName, hashMap, localMap, o, db, context, offLineId);
                        break;
                    case 107:
                        Intent intent = new Intent("edit_workitem");
                        intent.putExtra("response", ""+o);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        break;
                    case 108:
                        HandleResponseWorkItems.HandleReopenWorkItem(hashMap, localMap, o, context);
                        break;*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
