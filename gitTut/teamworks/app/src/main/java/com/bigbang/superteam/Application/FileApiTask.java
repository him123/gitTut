package com.bigbang.superteam.Application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bigbang.superteam.dataObjs.OfflineWork;
import com.bigbang.superteam.util.Constant;
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
public class FileApiTask extends AsyncTask<Void, String, String> {
    String FilePath;
    HashMap<String, String> hashMap, localMap;
    String ApiName;

    SQLiteDatabase db;
    Context context;
    int offLineId = 0;
    String TAG = "FileApiTaskNewModel";

    public FileApiTask(String Path, HashMap<String, String> hMap, HashMap<String, String> lMap, String API, SQLiteDatabase db, Context context, int offLineid) {
        this.FilePath = Path;
        this.hashMap = hMap;
        this.localMap = lMap;
        this.context = context;
        this.db = db;
        this.offLineId = offLineid;
        ApiName = API;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected String doInBackground(Void... params) {
        String resp = "";
        Log.e("File Api", "Started Background Task");

        if (Util.isOnline(context)) {
            Log.e("Calling Api", ApiName);
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
                    entity.addPart("file", fbody);
                }
                Log.e("Api Calling *************:", ""+hashMap.toString());

                entity.addPart("JSON", new StringBody(Util.prepareJsonString(hashMap)));
                entity.addPart("TokenID", new StringBody(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_TOKEN)));
                entity.addPart("UserID", new StringBody(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID)));
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
                    OfflineWork.StoreData(FilePath, hashMap, localMap, ApiName, db);
                return Constant.offline;
            }
            Log.d("Resp Upload", "" + resp);
            return resp;
        } else {
            if (offLineId <= 0)
                OfflineWork.StoreData(FilePath, hashMap, localMap, ApiName, db);
            return Constant.offline;
        }
    }

    @Override
    protected void onPostExecute(String o) {
        super.onPostExecute(o);
        Log.e("FileAPITask", "Received update :-" + o);
        if (o.equals(Constant.offline)) {
//            if (offLineId <= 0)
//                Toast.makeText(context, "Data inserted Offline", Toast.LENGTH_LONG).show();
        } else {
            try {
                JSONObject jObj = new JSONObject(o);
                String status = jObj.optString("status");
                if (status.equals(Constant.InvalidToken)) {
                    TeamWorkApplication.LogOutClear((Activity)context);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                Log.e(TAG,"Inside onPostExecute 2nd try catch");
                int Id = Integer.parseInt(localMap.get("id"));
                switch (Id) {
                    case 101:
                        Log.e(TAG,"Inside onPostExecute switch case 101");
                        HandleResponseWorkItems.HandleCreateWorkItemResponse(FilePath, ApiName, hashMap, localMap, o, db, context, offLineId);
                        break;
                    case 103:
                        Log.e(TAG,"Inside onPostExecute switch case 103");
                        HandleResponseWorkItems.HandleWorkItemUpdateResponse(FilePath, ApiName, hashMap, localMap, o, db, context, offLineId);
                        break;
                    case 106:
                        HandleResponseWorkItems.HandleCreateProject(FilePath, ApiName, hashMap, localMap, o, db, context, offLineId);
                        break;
                    case 107:

                        Intent intent = new Intent("edit_workitem");
                        intent.putExtra("response", ""+o);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

//                        JSONObject job = new JSONObject(o);
//                        if (job.getString("status").equals("Success")) {
//                            Toast.makeText(context, context.getString(R.string.work_item_modification_request_submitted), Toast.LENGTH_LONG).show();
//                            if (EditWorkitem.Active && hashMap.get("worktype").equals(Constant.WORKTYPES[Constant.PROJECT])) {
//                                Intent intent = new Intent("edit_workitem");
//                                intent.putExtra("message", "failure");
//                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                            }
//                        } else {
//                            Toast.makeText(context, job.getString("message"), Toast.LENGTH_LONG).show();
//                            if (EditWorkitem.Active && hashMap.get("worktype").equals(Constant.WORKTYPES[Constant.PROJECT])) {
//                                Intent intent = new Intent("edit_workitem");
//                                intent.putExtra("message", "failure");
//                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                            }
//                        }

                        break;
                    case 108:
                        Log.e(TAG,"Inside onPostExecute switch case 108");
                        HandleResponseWorkItems.HandleReopenWorkItem(hashMap, localMap, o, context);
                        break;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
