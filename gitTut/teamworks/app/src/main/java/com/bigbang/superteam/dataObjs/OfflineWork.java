package com.bigbang.superteam.dataObjs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bigbang.superteam.Application.FileApiTask;
import com.bigbang.superteam.Application.FileApiTaskNewModel;
import com.bigbang.superteam.util.Util;

import org.json.JSONException;

import java.util.HashMap;

/**
 * Created by USER 3 on 09/07/2015.
 */
public class OfflineWork {
    public static String OFFLINETABLE = "offLineWork";
    public static String ID = "id";
    public static String TOSEND = "toSend";
    public static String LOCAL = "local";
    public static String PATH = "path";
    public static String URL = "url";

    public int id;
    public String toSend;
    public String local;
    public String path;
    public String url;

    public static void StoreData(String Path, HashMap<String,
            String> hMap, HashMap<String, String> lMap, String API, SQLiteDatabase db) {
        Log.e("Offline Work Item", " Storing data Offline");
        ContentValues taskValues = new ContentValues();
        String sendStr = "";
        String localStr = "";

        try {
            sendStr = Util.prepareJsonString(hMap);
            localStr = Util.prepareJsonString(lMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        taskValues.put(TOSEND, sendStr);
        taskValues.put(LOCAL, localStr);
        taskValues.put(PATH, Path);
        taskValues.put(URL, API);

        db.insert(OFFLINETABLE, null, taskValues);
    }

    public static void StoreData(String Path, String sendStr, String type, String API, SQLiteDatabase db) {
        ContentValues taskValues = new ContentValues();
        taskValues.put(TOSEND, sendStr);
        taskValues.put(LOCAL, type);
        taskValues.put(PATH, Path);
        taskValues.put(URL, API);
        db.insert(OFFLINETABLE, null, taskValues);
    }

    public static void callApi(SQLiteDatabase db, Context context) {
        Cursor crsr = db.rawQuery("select * from " + OFFLINETABLE, null);
        if (crsr != null) {
            if (crsr.getCount() > 0) {
                crsr.moveToFirst();
                do {
                    String Path = crsr.getString(crsr.getColumnIndex(PATH));
                    String sendMap =crsr.getString(crsr.getColumnIndex(TOSEND));
                    String type =crsr.getString(crsr.getColumnIndex(LOCAL));
                    String ApiUrl = crsr.getString(crsr.getColumnIndex(URL));
                    int Id = crsr.getInt(crsr.getColumnIndex(ID));
                    FileApiTaskNewModel newApiCall = new FileApiTaskNewModel(Path, sendMap, type, ApiUrl, db, context, Id);
                    newApiCall.execute();
                } while (crsr.moveToNext());
            }
        }
        if(crsr!=null) crsr.close();
    }
}
