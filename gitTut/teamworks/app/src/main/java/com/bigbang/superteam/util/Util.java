package com.bigbang.superteam.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.HeartbeatReceiver;
import com.bigbang.superteam.R;
import com.bigbang.superteam.ServerUtilities;
import com.bigbang.superteam.WorkItem_GCM;
import com.bigbang.superteam.alarm.SimpleWakefulReceiver;
import com.bigbang.superteam.dataObjs.AttendanceHistoryModel;
import com.bigbang.superteam.dataObjs.LeaveHistoryModel;
import com.bigbang.superteam.dataObjs.NotificationInfo;
import com.bigbang.superteam.dataObjs.User;
import com.bigbang.superteam.interfaces_tw.AlertDialogCallback;
import com.bigbang.superteam.task.model.Expense;
import com.bigbang.superteam.task.model.TaskChat;
import com.bigbang.superteam.task.model.TaskModel;
import com.google.android.gcm.GCMRegistrar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import au.com.bytecode.opencsv.CSVWriter;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import static com.bigbang.superteam.GCMIntentService.SENDER_ID;

@SuppressLint("SimpleDateFormat")
public class Util {


    private static final String TAG = Util.class.getSimpleName();
    public static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static SimpleDateFormat ddMMMyyyy = new SimpleDateFormat("dd MMM, yyyy hh:mm a");
    public static SimpleDateFormat ddMMMyyyy1 = new SimpleDateFormat("dd MMM, yyyy");

    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static String makeJSONStringFromTaskChatModel(TaskChat taskChat) {
        JSONObject json = new JSONObject();

        try {
            json.put("id", taskChat.id);
            json.put("taskID", taskChat.taskID);
            json.put("chatType", taskChat.chatType);
            json.put("dataType", taskChat.dataType);
            json.put("message", taskChat.message);
            json.put("startTime", taskChat.startTime);
            json.put("endTime", taskChat.endTime);
            json.put("queryTo", taskChat.queryTo);

            if (taskChat.expense != null) {
                JSONObject expJobj = new JSONObject();
                expJobj.put("taskID", taskChat.taskID);
                expJobj.put("expenseAmount", taskChat.expense.expenseAmount);
                expJobj.put("expenseDate", taskChat.expense.expenseDate);

                json.put("expense", expJobj);
            }

            if (taskChat.chatType.equals("Postponed")) {
                json.put("startTime", taskChat.startTime);
                json.put("endTime", taskChat.endTime);
            }

//            Expense expense = taskChat.expense;

//            json.put("expense", taskChat.expense);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return json.toString();
    }

    public static String makeJSONStringFromTaskModel(TaskModel taskModel) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", taskModel.id);
            json.put("taskID", taskModel.taskID);
            json.put("companyID", (Constant.SHRED_PR.KEY_COMPANY_ID));
            json.put("name", taskModel.name);
            json.put("description", taskModel.description);
            json.put("taskType", taskModel.taskType);

            JSONObject address = new JSONObject();
            if (taskModel.address != null) {
                address.put("AddressID", taskModel.address.getAddressID());
                address.put("Country", taskModel.address.getCountry());
                address.put("State", taskModel.address.getState());
                address.put("City", taskModel.address.getCity());
                address.put("AddressLine1", taskModel.address.getAddressLine1());
                address.put("AddressLine2", taskModel.address.getAddressLine1());
                address.put("Pincode", taskModel.address.getPincode());
                address.put("Lattitude", taskModel.address.getLattitude());
                address.put("Longitude", taskModel.address.getLongitude());
                json.put("address", address);
            }

            json.put("priority", taskModel.priority);

            if (taskModel.startTime != null && taskModel.startTime.length() > 0)
                json.put("startTime", taskModel.startTime);

            if (taskModel.endTime != null && taskModel.endTime.length() > 0)
                json.put("endTime", taskModel.endTime);
            json.put("estimatedTime", taskModel.estimatedTime);
            json.put("budget", taskModel.budget);

            if (taskModel.assignedToList != null && taskModel.assignedToList.size() > 0) {
                JSONArray aList = new JSONArray();
                for (int i = 0; i < taskModel.assignedToList.size(); i++) {
                    if (taskModel.assignedToList.get(i).isChecked()) {
                        JSONObject user = new JSONObject();
                        user.put("userId", taskModel.assignedToList.get(i).getUserID());
                        user.put("firstName", taskModel.assignedToList.get(i).getFirstName());
                        user.put("lastName", taskModel.assignedToList.get(i).getLastName());
                        aList.put(i, user);
                    }
                }
                json.put("assignedToList", aList);
            }

            if (taskModel.ccList != null && taskModel.ccList.size() > 0) {
                JSONArray cList = new JSONArray();
                for (int i = 0; i < taskModel.ccList.size(); i++) {
                    if (taskModel.ccList.get(i).isChecked()) {
                        JSONObject user = new JSONObject();
                        user.put("userId", taskModel.ccList.get(i).getUserID());
                        user.put("firstName", taskModel.assignedToList.get(i).getFirstName());
                        user.put("lastName", taskModel.assignedToList.get(i).getLastName());
                        cList.put(i, user);
                    }
                }
                json.put("ccList", cList);
            }

            if (taskModel.attachments != null && taskModel.attachments.size() > 0) {
                JSONArray att = new JSONArray();
                for (int i = 0; i < taskModel.attachments.size(); i++) {
                    att.put(i, taskModel.attachments.get(i));
                }
                json.put("attachments", att);
            }

            //Regular
            json.put("frequency", taskModel.frequency);
            json.put("daycodes", taskModel.daycodes);

            //sales or Service call
            json.put("customerType", taskModel.customerType);

            if (taskModel.taskCustVend != null && taskModel.taskCustVend.size() > 0) {
                JSONArray custList = new JSONArray();
                for (int i = 0; i < taskModel.taskCustVend.size(); i++) {
                    if (taskModel.taskCustVend.get(i).isSelected()) {
                        JSONObject cust = new JSONObject();

                        if (taskModel.taskCustVend.get(i).getID() > 0) {
                            cust.put("custVendorID", taskModel.taskCustVend.get(i).getID());
                            cust.put("name", taskModel.taskCustVend.get(i).getName());
                            cust.put("contact", taskModel.taskCustVend.get(i).getMobileNo());
                        } else {
                            cust.put("name", taskModel.taskCustVend.get(i).getName());
                            cust.put("contact", taskModel.taskCustVend.get(i).getMobileNo());
                        }
                        custList.put(i, cust);
                    }
                }
                json.put("taskCustVend", custList);
            }
            json.put("pastHistory", taskModel.pastHistory);

            //Collection
            if (taskModel.invoiceDate != null && taskModel.invoiceDate.length() > 0)
                json.put("invoiceDate", taskModel.invoiceDate);

            if (taskModel.dueDate != null && taskModel.dueDate.length() > 0)
                json.put("dueDate", taskModel.dueDate);

            json.put("invoiceAmount", taskModel.invoiceAmount);
            json.put("outstandingAmount", taskModel.outstandingAmount);

            //Shopping/Purchase
            json.put("advancePaid", taskModel.advancePaid);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

//    public static String postData(List<NameValuePair> nameValuePairs, String url) {
//        // TODO Auto-generated method stub
//        String responseStr = "";
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost(url);
//        Log.e("reqURL", "" + url);
//        try {
//            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//            // Execute HTTP Post Request
//            HttpResponse response = httpclient.execute(httpPost);
//            HttpEntity resEntity = response.getEntity();
//            if (resEntity != null) {
//                responseStr = EntityUtils.toString(resEntity).trim();
//                // you can add an if statement here and do other actions based
//                // on the response
//            }
//
//            Log.e("Response-->", responseStr);
//
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//            // TODO Auto-generated catch block
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return responseStr;
//    }

    public static void askForInput(Activity context, final AlertDialogCallback<String> callback, String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

//        alert.setTitle("Enter input");
        alert.setMessage(title);

        // Set an EditText view to get user input
//        final EditText input = new EditText(context);
//        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                String value = input.getText().toString();
                callback.alertDialogCallback("ok");
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                callback.alertDialogCallback("cancel");
            }
        });

        alert.show();
    }

    public static String makeServiceCall(String url, int method,
                                         List<NameValuePair> params, Context context) {

        String response = null;
        int GET = 1;
        int POST = 2;
        int PUT = 3;
        try {
            params.add(new BasicNameValuePair("CompanyID", "" + "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_ID)));
            params.add(new BasicNameValuePair("UserID", "" + "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID)));
            params.add(new BasicNameValuePair("TokenID", "" + "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_TOKEN)));

            Log.d("", "CompanyID: " + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_ID));
            Log.d("", "UserID: " + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID));
            Log.d("", "TokenID: " + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_TOKEN));


            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            Log.d("** reqURL", "" + url);
            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }


                Log.i("Url", url);
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            } else if (method == PUT) {
                HttpPut httpPut = new HttpPut(url);
                // adding post params
                if (params != null) {
                    httpPut.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPut);
            }

            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

            Util.appendLog("Response status code: " + httpResponse.getStatusLine().getStatusCode());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(TAG, "resonse is:- " + response);

        try {
            JSONObject jObj = new JSONObject(response);
            String status = jObj.optString("status");
            if (status.equals(Constant.InvalidToken)) {
                TeamWorkApplication.LogOutClear((Activity) context);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    public static void writeToFile(String data, String file_name,
                                   Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(file_name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(String file_name, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(file_name);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(
                        inputStream);
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static void WriteFile(String path, String filename, String data) {
        try {
            File direct = new File(path);

            if (!direct.exists()) {
                direct.mkdir();//directory is created;
            }
            String fpath = path + filename;
            File txtfile = new File(fpath);

            txtfile.createNewFile();
            FileOutputStream fout = new FileOutputStream(txtfile);
            OutputStreamWriter myoutwriter = new OutputStreamWriter(fout);
            myoutwriter.write(data);
            myoutwriter.close();
            fout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String ReadFile(String path, String filename) {
        File myFile = new File(path + filename);
        if (!myFile.exists()) {
            return null;
        }
        FileInputStream fIn = null;
        try {
            fIn = new FileInputStream(myFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BufferedReader myReader = new BufferedReader(
                new InputStreamReader(fIn));
        String aDataRow = "";
        String aBuffer = "";
        try {
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow + "\n";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return aBuffer;
    }

    public static void WriteSharePrefrenceForBoolean(Context context,
                                                     String key, boolean values) {

        SharedPreferences write_Data = context.getSharedPreferences(
                Constant.SHRED_PR.SHARE_PREF, context.MODE_PRIVATE);
        Editor editor = write_Data.edit();
        editor.putBoolean(key, values);
        editor.commit();
    }

    public static void WriteSharePrefrenceForInteger(Context context,
                                                     String key, int values) {

        SharedPreferences write_Data = context.getSharedPreferences(
                Constant.SHRED_PR.SHARE_PREF, context.MODE_PRIVATE);
        Editor editor = write_Data.edit();
        editor.putInt(key, values);
        editor.commit();
    }

    public static void WriteSharePrefrence(Context context, String key,
                                           String values) {
        @SuppressWarnings("static-access")
        SharedPreferences write_Data = context.getSharedPreferences(
                Constant.SHRED_PR.SHARE_PREF, context.MODE_PRIVATE);
        Editor editor = write_Data.edit();
        editor.putString(key, values);
        editor.apply();
    }


    public static String ReadSharePrefrence(Context context, String key) {
        @SuppressWarnings("static-access")
        SharedPreferences read_data = context.getSharedPreferences(
                Constant.SHRED_PR.SHARE_PREF, context.MODE_PRIVATE);

        return read_data.getString(key, "");
    }

    public static boolean ReadSharePrefrenceBoolean(Context context, String key) {
        @SuppressWarnings("static-access")
        SharedPreferences read_data = context.getSharedPreferences(
                Constant.SHRED_PR.SHARE_PREF, context.MODE_PRIVATE);

        return read_data.getBoolean(key, false);
    }

    public static int ReadSharePrefrenceInteger(Context context, String key) {
        @SuppressWarnings("static-access")
        SharedPreferences read_data = context.getSharedPreferences(
                Constant.SHRED_PR.SHARE_PREF, context.MODE_PRIVATE);

        return read_data.getInt(key, 0);
    }

    public static String getAddress(Context context, Double latitude, Double longitude) {
        // TODO Auto-generated method stub
        try {
            Log.e("latitude:longitude", ">>" + latitude + " : " + longitude);
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = "";
            String city = "";
            String country = "";
            String postcode = "";

            try {
                address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getAddressLine(1);
                country = addresses.get(0).getAddressLine(2);
                postcode = addresses.get(0).getPostalCode();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            String strAddress = "";
            if (!address.equals(""))
                strAddress = address;
            if (!city.equals(""))
                strAddress = strAddress + ", " + city;
            if (!country.equals(""))
                strAddress = strAddress + ", " + country;

            return strAddress;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }

        return "";
    }

//    public static boolean isGPSOn(Context context) {
//        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
////        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
////            return true;
////        } else {
////            return false;
////        }
//
////        String allowedLocationProviders =
////                Settings.System.getString(context.getContentResolver(),
////                        Settings.System.LOCATION_PROVIDERS_ALLOWED);
////
////        if (allowedLocationProviders == null) {
////            allowedLocationProviders = "";
////        }
////
////        return allowedLocationProviders.contains(LocationManager.GPS_PROVIDER);
//
//        String provider = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
//                Settings.Secure.LOCATION_MODE);
//        if (!provider.equals("")) {
//            //GPS Enabled
////            Toast.makeText(AndroidEnableGPS.this, "GPS Enabled: " + provider,
////                    Toast.LENGTH_LONG).show();
//            return true;
//        } else {
////            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
////            startActivity(intent);
//            return false;
//        }
//    }

    public static boolean isNetWorkLocationProviderOn(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean canToggleGPS(Context context) {
        PackageManager pacman = context.getPackageManager();
        PackageInfo pacInfo = null;

        try {
            pacInfo = pacman.getPackageInfo("com.android.settings", PackageManager.GET_RECEIVERS);
        } catch (Exception e) {
            return false; //package not found
        }

        if (pacInfo != null) {
            for (ActivityInfo actInfo : pacInfo.receivers) {
                //test if recevier is exported. if so, we can toggle GPS.
                if (actInfo.name.equals("com.android.settings.widget.SettingsAppWidgetProvider") && actInfo.exported) {
                    return true;
                }
            }
        }

        return false; //default
    }

    public static void GCM(final Context context) {
        // TODO Auto-generated method stub
        final AsyncTask<Void, Void, Void> mRegisterTask;
        final String regId;

        checkNotNull(SENDER_ID, "SENDER_ID", context);
        //GCMRegistrar.checkDevice(context);
        GCMRegistrar.checkManifest(context);

        regId = GCMRegistrar.getRegistrationId(context);
        Log.d("", "regId==" + regId);
        if (regId.equals("")) {
            GCMRegistrar.register(context, SENDER_ID);
        } else {
            if (GCMRegistrar.isRegisteredOnServer(context)) {
                // Skips registration.
            } else {
                mRegisterTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered = ServerUtilities.register(context,
                                regId);
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {

                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }

        if (regId.length() > 0)
            Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_GCM_ID, "" + regId);
    }

    private static void checkNotNull(Object reference, String name, Context context) {
        if (reference == null) {
            throw new NullPointerException(context.getString(R.string.error_config,
                    name));
        }
    }

    public static String getStringFromImage(String path) {
        Bitmap bm = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Date roundTimeQuarterHour(Date whateverDateYouWant) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(whateverDateYouWant);
        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 15;
        calendar.add(Calendar.MINUTE, mod < 8 ? -mod : (15 - mod));
        calendar.set(Calendar.MINUTE, unroundedMinutes + mod);

        return calendar.getTime();
    }

    public static Date roundTimeHalfHour(Date whateverDateYouWant) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(whateverDateYouWant);
        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 30;
        calendar.add(Calendar.MINUTE, mod < 15 ? -mod : (30 - mod));
        calendar.set(Calendar.MINUTE, unroundedMinutes + mod);

        return calendar.getTime();
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    //Create broadcast object
    BroadcastReceiver mybroadcast = new BroadcastReceiver() {
        //When Event is published, onReceive method is called
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.i("[BroadcastReceiver]", "MyReceiver");

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.i("[BroadcastReceiver]", "Screen ON");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i("[BroadcastReceiver]", "Screen OFF");
            }

        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    public static void registerUserTrackingReceiver(Context context) {
        Calendar calendar = Calendar.getInstance();
        Util.appendLog("registerUserTrackingReceiver() Called: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
        Log.e("register Tracking", "************************* registerUserTrackingReceiver() *****************************");
        try {

            if (!isMyServiceRunning(LocationService.class, context)) {
                context.startService(new Intent(context, LocationService.class));
            }

            Intent intentWakeFullBroacastReceiver = new Intent(context, SimpleWakefulReceiver.class);
            boolean alarmUp = (PendingIntent.getBroadcast(context, 1001, intentWakeFullBroacastReceiver, PendingIntent.FLAG_NO_CREATE) == null);

            if (alarmUp) {

                Util.appendLog("Tracking START: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
                PendingIntent sender = PendingIntent.getBroadcast(context, 1001, intentWakeFullBroacastReceiver, 0);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

                if (calendar.get(Calendar.MINUTE) > 0 && calendar.get(Calendar.MINUTE) <= 30) {
                    calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, 30);
                    calendar.set(Calendar.SECOND, 0);
                } else if (calendar.get(Calendar.MINUTE) >= 30) {
                    if (calendar.get(Calendar.HOUR_OF_DAY) == 23) {
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    } else {
                        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
                    }
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                }

                //MARSHMALLOW OR ABOVE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.MANUFACTURER.equals("GiONEE") && Build.MODEL.equals("S_plus")) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC,
                                calendar.getTimeInMillis(), sender);
//                        Toast.makeText(context, "This GiONEE", Toast.LENGTH_SHORT).show();
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(), sender);
                    }
                    //AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), sender);
                    //alarmManager.setAlarmClock(alarmClockInfo, sender);
                    Util.appendLog("UTIL M next track set @: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
                }
                //LOLLIPOP 21 OR ABOVE
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), sender);
                    alarmManager.setAlarmClock(alarmClockInfo, sender);
                    Util.appendLog("UTIL L next track set @: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
                }
                //KITKAT 19 OR ABOVE
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(), sender);
                    Util.appendLog("UTIL K next track set @: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
                }
                //FOR BELOW KITKAT ALL DEVICES
                else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(), sender);
                    Util.appendLog("UTIL R next track set @: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
                }
                Util.appendLog("###################");
                Log.i("", "****************************ALARM CALLED FOR LOCATION*********************************");
            }
//            context.startService(new Intent(context, UserTrackingReceiverIntentService.class));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("", "************ Exception in registerUserTrackingReceiver(): " + e);
            generateGeneralNotification(context, "Alarm missed from registerUserTrackingReceiver(): " + e);
        }
    }

    public static void appendLog(String text) {

        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();

        File logFile = new File(baseDir + "/" + Constant.AppNameSuper + "/log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void generateGeneralNotification(Context context, String message) {

        int icon = R.drawable.icon;
        int id = (int) System.currentTimeMillis();
        long[] pattern = {0, 200, 0};

        Intent notificationIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        long when = System.currentTimeMillis();

        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notification = mBuilder.setSmallIcon(R.drawable.icon_bell).setTicker(message).setWhen(id)
                .setAutoCancel(true)
                .setWhen(when)
                .setContentTitle(Constant.AppNameSuper)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(intent)
                .setVibrate(pattern)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentText(message).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);

    }

    public static boolean checkNetworkServiceProvider(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);
    }

    public static void unregisterUserTrackingReceiver(Context context) {
        try {
            Intent i = new Intent(context, SimpleWakefulReceiver.class);
            // i.setAction("com.bigbang.teamworks.CUSTOM_INTENT");
            PendingIntent sender = PendingIntent.getBroadcast(context, 1001, i, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            am.cancel(sender);
            context.stopService(new Intent(context, LocationService.class));
            sender.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerHeartbeatReceiver(Context context) {
        try {
            Intent intent = new Intent(context, HeartbeatReceiver.class);
//            intent.setAction("com.bigbang.teamworks.CUSTOM_INTENT");

            boolean alarmUp = (PendingIntent.getBroadcast(context, 1004, intent, PendingIntent.FLAG_NO_CREATE) == null);

            if (alarmUp) {
                PendingIntent sender = PendingIntent.getBroadcast(context, 1004, intent, 0);
                long firstTime = SystemClock.elapsedRealtime();
                firstTime += 5 * 60 * 1000; // 5 min
                // Schedule the alarm!
                AlarmManager am = (AlarmManager) context
                        .getSystemService(context.ALARM_SERVICE);
                am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
                        5 * 60 * 1000, sender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String prepareJsonString(HashMap<String, String> hashMap) throws JSONException {
        String retStr = null;
        JSONObject json = new JSONObject();

        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            json.put(key, value);
        }

        return json.toString();
    }

    public static String getFileNameFromUrl(URL url) {

        String urlString = url.getFile();
        return urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
    }

    public static String calculateTime(String dateAndTime) {
        String time = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a");
        try {
            Date date1 = format.parse(dateAndTime);
            time = format1.format(date1);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return time;
    }

    public static String calculatDate(String dateAndTime) {
        String date = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = format.parse(dateAndTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            int month = calendar.get(calendar.MONTH);
            int year = calendar.get(calendar.YEAR);
            int day = calendar.get(calendar.DAY_OF_MONTH);
            String strmonth = "" + (month + 1), strDate = "" + day;

            if ((month + 1) < 10) {
                strmonth = "0" + (month + 1);
            }
            if (day < 10) {
                strDate = "0" + day;
            }

            date = strDate + "/" + strmonth + "/" + year;
            //date = "" + calendar.get(calendar.DAY_OF_MONTH) + "/" + calendar.get(calendar.MONTH) + "/" + calendar.get(calendar.YEAR);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return date;
    }

    public static String GetDate() {
        Calendar calendar = Calendar.getInstance();
        String sdate = sdf.format(calendar.getTime());
        return sdate;
    }

    public static void sendNotificationDetails(Context mContext, NotificationInfo noti) {
        ContentValues notiValue = new ContentValues();
        {
            notiValue.put(NotificationInfo.ID, "" + noti.getId());
            notiValue.put(NotificationInfo.TITLE, "" + noti.getTitle());
            notiValue.put(NotificationInfo.DESCRIPTION, "" + noti.getDescription());
            notiValue.put(NotificationInfo.IMAGE_URL, "" + noti.getImage_Url());
            notiValue.put(NotificationInfo.TYPE, "" + noti.getType());
            notiValue.put(NotificationInfo.TIME, "" + noti.getTime());
            notiValue.put(NotificationInfo.USER_ID, "" + noti.getUser_Id());
            notiValue.put(NotificationInfo.EXTRA, "" + noti.getExtra());
            notiValue.put(NotificationInfo.TRANSACTION_ID, "" + noti.getTransactionID());
            Util.getDb(mContext).insert(Constant.NotificationTable, null, notiValue);

            Intent intent = new Intent("Notification_Received");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }

    public static int getMaxId(Context mContext) {
        Cursor cursor = WorkItem_GCM.getDb(mContext).rawQuery("select MAX(pk) from " + Constant.NotificationTable, null);
        int x = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            x = cursor.getInt(0);
        }
        return x + 1;
    }

    public static int getimgSrc(char c) {
        switch (c) {
            case 'A':
                return R.drawable.a_icon;
            case 'B':
                return R.drawable.b_icon;
            case 'C':
                return R.drawable.c_icon;
            case 'D':
                return R.drawable.d_icon;
            case 'E':
                return R.drawable.e_icon;
            case 'F':
                return R.drawable.f_icon;
            case 'G':
                return R.drawable.g_icon;
            case 'H':
                return R.drawable.h_icon;
            case 'I':
                return R.drawable.i_icon;
            case 'J':
                return R.drawable.j_icon;
            case 'K':
                return R.drawable.k_icon;
            case 'L':
                return R.drawable.l_icon;
            case 'M':
                return R.drawable.m_icon;
            case 'N':
                return R.drawable.n_icon;
            case 'O':
                return R.drawable.o_icon;
            case 'P':
                return R.drawable.p_icon;
            case 'Q':
                return R.drawable.q_icon;
            case 'R':
                return R.drawable.r_icon;
            case 'S':
                return R.drawable.s_icon;
            case 'T':
                return R.drawable.t_icon;
            case 'U':
                return R.drawable.u_icon;
            case 'V':
                return R.drawable.v_icon;
            case 'W':
                return R.drawable.w_icon;
            case 'X':
                return R.drawable.x_icon;
            case 'Y':
                return R.drawable.y_icon;
            case 'Z':
                return R.drawable.z_icon;
            default:
                return R.drawable.hash_icon;

        }
    }

    public static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public static Bitmap decodeFile(String path) {
        int orientation;
        try {
            if (path == null) {
                return null;
            }
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            // while (true) {
            // if (width_tmp / 2 < REQUIRED_SIZE
            // || height_tmp / 2 < REQUIRED_SIZE)
            // break;
            // width_tmp /= 2;
            // height_tmp /= 2;
            // scale++;
            // }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeFile(path, o2);
            Bitmap bitmap = bm;

            ExifInterface exif = new ExifInterface(path);

            orientation = exif
                    .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Log.e("ExifInteface .........", "rotation =" + orientation);

            // exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

            Log.e("orientation", "" + orientation);
            Matrix m = new Matrix();

            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                // m.postScale((float) bm.getWidth(), (float) bm.getHeight());
                // if(m.preRotate(90)){
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            }
            return bitmap;
        } catch (Exception e) {
            return null;
        }

    }

    public static boolean isResponseSuccess(Response res) {
        HashMap<String, String> map = readStatus(res);
        return map.containsKey("status") && map.get("status").equalsIgnoreCase("Success");
    }

    public static HashMap<String, String> readStatus(Response res) {
        HashMap<String, String> map = new HashMap<>();
        try {
            String json = getString(res.getBody().in());
            JSONObject jObj = new JSONObject(json);
            map.put("message", jObj.optString("message"));
            map.put("status", jObj.optString("status"));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            map.put("message", e.getMessage());
            map.put("status", "Fail");
        }
        return map;
    }

    public static String getString(InputStream in) {
        InputStreamReader is = new InputStreamReader(in);
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        try {
            String read = br.readLine();
            while (read != null) {
                sb.append(read);
                read = br.readLine();
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR WHILE PARSING RESPONSE TO STRING :: " + e.getMessage());
        } finally {
            try {
                if (is != null) is.close();
                if (br != null) br.close();
            } catch (Exception e) {
            }
        }
        return sb.toString();
    }

    static public void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static String addJsonKey(String json, HashMap<String, String> hashMap) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator it = hashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (isJSONArrayValid((String) pair.getValue())) {
                    String temp = (String) pair.getValue();
                    jsonObject.put((String) pair.getKey(), new JSONArray(temp));
                } else {
                    jsonObject.put((String) pair.getKey(), pair.getValue());
                }
                //jsonObject.put((String) pair.getKey(), pair.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }

            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static boolean isJSONArrayValid(String test) {
        try {
            new JSONArray(test);
        } catch (JSONException ex1) {
            return false;
        }
        return true;
    }

    public static String gmtToLocal(String dtStart) {
        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            if (dtStart != null && !dtStart.equals("null")) {
                Date localTime = format.parse(dtStart);
                newDate = sdf.format(localTime);
                return newDate;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }


    public static String gmtToLocalTime(String dtStart) {
        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date localTime = format.parse(dtStart);
            newDate = sdf.format(localTime);
            return newDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }


    public static String locatToGMT(String dtStart) {
        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date localTime = format.parse(dtStart);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date gmtTime = new Date(sdf.format(localTime));
            newDate = sdf1.format(gmtTime);
            return newDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String utcToLocalTime(String dtStart) {
        if (dtStart != null && dtStart.length() > 5) {
            String newDate = "";
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date localTime = format.parse(dtStart);
                newDate = sdf.format(localTime);
                return newDate;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newDate;
        }
        return "";
    }

//    public static String differenceWithCurrentTime(String dtStart) {
//
//        try {
//            DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//            Date dateOne = df.parse("2011-02-08 10:00:00 +0300");
//            Date dateTwo = df.parse("2011-02-08 08:00:00 +0100");
//            long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
//            System.out.println("difference:" + timeDiff);   // difference: 0
//        } catch (Exception e) {
//            Log.d("", "Exception: " + e);
//        }
//
//
//        return "";
//    }

    public static String utcToLocalTime1(String dtStart) {
        if (dtStart != null && dtStart.length() > 5) {
            String newDate = "";
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date localTime = format.parse(dtStart);
                newDate = sdf.format(localTime);
                return newDate;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newDate;
        }
        return "";
    }

    public static String utcToLocalTime2(String dtStart) {
        if (dtStart != null && dtStart.length() > 5) {
            String newDate = "";
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date localTime = format.parse(dtStart);
                newDate = sdf.format(localTime);
                return newDate;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newDate;
        }
        return "";
    }

    public static String locatToUTC(String dtStart) {
        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date localTime = format.parse(dtStart);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date gmtTime = new Date(sdf.format(localTime));
            newDate = format.format(gmtTime);
            return newDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String dateFormat(String dtStart) {

        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date localTime = format.parse(dtStart);
            Date fromGmt = new Date(localTime.getTime() + TimeZone.getDefault().getOffset(localTime.getTime()));
            String suffix = getDayNumberSuffix(fromGmt.getDate());
            SimpleDateFormat sdf = new SimpleDateFormat(" d'" + suffix + "' MMM");
            newDate = sdf.format(fromGmt);
            return newDate;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static HashMap<String, String> prepareHashmapfromJSON(String s) {
        HashMap<String, String> map = new HashMap<String, String>();
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Iterator<?> keys = jObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = null;
            if (jObject != null)
                try {
                    value = jObject.getString(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            map.put(key, value);
        }
        System.out.println("json : " + jObject);
        System.out.println("map : " + map);
        return map;
    }

    public static SQLiteDatabase getDb(Context mContext) {
        if (TeamWorkApplication.db == null) {
            SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
            helper.createDatabase();
            TeamWorkApplication.db = helper.openDatabase();
        }

        return TeamWorkApplication.db;
    }

    public static boolean checkUserPrivilage(Context context, String id) {

        String json = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USER_PRIVILAGES);
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.optJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    if (jsonObject1.optString("privilegeid").equals(id)) return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static int checkCompanyPrivilage(Context context, String id) {

        String json = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_PRIVILAGES);
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if (jsonObject1.optString("privilegeid").equals(id)) return 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public static void insertIntoOffline(String jsonData, String method, String actionName, Context context) {


        ContentValues values = new ContentValues();
        values.put("method_type", "" + method);
        values.put("action_name", "" + actionName);
        values.put("jsondata", "" + jsonData);

        getDb(context).insert(Constant.OfflineDataLeaveAttendance, null, values);
    }

    public static String locatToGMTTemp(String dtStart) {
        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date localTime = format.parse(dtStart);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date gmtTime = new Date(sdf.format(localTime));
            newDate = sdf1.format(gmtTime);
            return newDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static int createCSVFileForAttendance(List<AttendanceHistoryModel> listAttendHistory, String month, String year, Context context) {
        int result = -1;
        String timeIn, timeOut, date, isManual, timeDifference, tempTimeIn, tempTimeOut;
        Long totalTimeDifference = 0l, diff;
        File file;
        String userName = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_FIRSTNAME) + " " + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_LASTNAME);

        File exportDir = new File(Environment.getExternalStorageDirectory() + "/" + Constant.AppNameSuper, "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        if (listAttendHistory.size() > 0) {
            file = new File(exportDir, "Attendance_" + listAttendHistory.get(0).getUserName() + "_" + month + "_" + year + ".csv");
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                Date CompStartTime = null, CompEndtime = null;
                String arrStrColmns[] = {"Username", "Date", "Timein", "Timeout", "Manual Attendance", "Total hours"};
                csvWrite.writeNext(arrStrColmns);

                //code for calculating working hours from start and end time of company
                String sTime = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_STARTTIME);
                String eTime = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_ENDTIME);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                try {
                    CompStartTime = simpleDateFormat.parse(sTime);
                    CompEndtime = simpleDateFormat.parse(eTime);
                } catch (ParseException ex) {
                    System.out.println("Exception " + ex);
                }
                Log.e("StartTime ------>  " + CompStartTime, "EndTime is:- " + CompEndtime);
                double WorkingHR = Util.getWorkingHours(CompStartTime, CompEndtime);
                int workingHours = (int) WorkingHR;

                for (int i = 0; i < listAttendHistory.size(); i++) {
                    if (listAttendHistory.get(i).getNewTimeIn().length() > 0) {
                        timeIn = Util.gmtToLocalTime("" + listAttendHistory.get(i).getNewTimeIn());
                        tempTimeIn = listAttendHistory.get(i).getNewTimeIn();
                    } else {
                        timeIn = Util.gmtToLocalTime("" + listAttendHistory.get(i).getTimeIN());
                        tempTimeIn = listAttendHistory.get(i).getTimeIN();
                    }
                    if (listAttendHistory.get(i).getNewTimeOut().length() > 0) {
                        timeOut = Util.gmtToLocalTime("" + listAttendHistory.get(i).getNewTimeOut());
                        tempTimeOut = listAttendHistory.get(i).getNewTimeOut();
                    } else {
                        timeOut = Util.gmtToLocalTime("" + listAttendHistory.get(i).getTimeOut());
                        tempTimeOut = listAttendHistory.get(i).getTimeOut();
                    }

                    date = Util.gmtToLocal("" + listAttendHistory.get(i).getRequestDate());

                    if (listAttendHistory.get(i).getIsManualAttendance().equals("full")) {
                        isManual = "Yes: Full day";
                        timeDifference = "" + workingHours;
                        totalTimeDifference = totalTimeDifference + ((workingHours * 60 * 60) * 1000);
                        //Util.ReadSharePrefrence(ApplyLeaveActivity.this, Constant.SHRED_PR.KEY_USERID)
                    } else if (listAttendHistory.get(i).getIsManualAttendance().equals("half")) {
                        isManual = "Yes: Half day";
                        timeDifference = "" + (workingHours / 2);
                        totalTimeDifference = totalTimeDifference + (((workingHours / 2) * 60 * 60) * 1000);
                    } else {
                        isManual = "No";
                        diff = calculateTimeDifference(tempTimeIn, tempTimeOut);
                        totalTimeDifference = totalTimeDifference + diff;

                        long seconds = diff / 1000;
                        long minutes = seconds / 60;
                        long hours = minutes / 60;

                        if (minutes >= 60) {
                            minutes = minutes % 60;
                        }
                        timeDifference = "" + hours + " : " + minutes;
                    }
                    String arrStr[] = {listAttendHistory.get(i).getUserName(), date, timeIn, timeOut, isManual, timeDifference};
                    csvWrite.writeNext(arrStr);
                }
                long timeSec = totalTimeDifference / 1000;
                long minutes = timeSec / 60;
                long hours = minutes / 60;

                if (minutes >= 60) {
                    minutes = minutes % 60;
                }
                String totalHours[] = {"", "", "", "", "Total hours", "" + hours + " : " + minutes};
                csvWrite.writeNext(totalHours);
                csvWrite.close();
                result = 1;

            } catch (Exception sqlEx) {
                result = 0;
                sqlEx.printStackTrace();
            }
        } else {
            file = new File(exportDir, "Attendance_" + userName + "_" + month + "_" + year + ".csv");
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                String arrStrColmns[] = {"Username", "Date", "Timein", "Timeout", "Manual Attendance", "Total hours"};
                csvWrite.writeNext(arrStrColmns);
                csvWrite.close();
                result = 1;
            } catch (Exception e) {
                result = 0;
                e.printStackTrace();
            }
        }


        return result;

    }


    public static int createCSVFileForLeave(List<LeaveHistoryModel> listAttendHistory, String year, Context context) {
        int result = -1;
        String startDate, endDate;
        String userName = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_FIRSTNAME) + " " + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_LASTNAME);
        File file;
        File exportDir = new File(Environment.getExternalStorageDirectory() + "/" + Constant.AppNameSuper, "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        if (listAttendHistory.size() > 0) {
            file = new File(exportDir, "Leave_" + listAttendHistory.get(0).getUserName() + "_" + year + ".csv");
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                String arrStrColmns[] = {"Username", "StartDate", "Reason", "Leave Type", "Leave Status"};
                csvWrite.writeNext(arrStrColmns);
                for (int i = 0; i < listAttendHistory.size(); i++) {
                    startDate = Util.gmtToLocal("" + listAttendHistory.get(i).getStartDate());
                    // endDate = Util.gmtToLocal("" + listAttendHistory.get(i).getEndDate());

                    String arrStr[] = {listAttendHistory.get(i).getUserName(), startDate, listAttendHistory.get(i).getReason(), listAttendHistory.get(i).getLeaveType(), listAttendHistory.get(i).getLeaveStatus()};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                result = 1;

            } catch (Exception sqlEx) {
                result = 0;
                sqlEx.printStackTrace();
            }
        } else {
            file = new File(exportDir, "Leave_" + userName + "_" + year + ".csv");
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                String arrStrColmns[] = {"Username", "StartDate", "Reason", "Leave Type", "Leave Status"};
                csvWrite.writeNext(arrStrColmns);
                csvWrite.close();
                result = 1;
            } catch (Exception e) {
                e.printStackTrace();
                result = 0;
            }
        }
        return result;
    }

    public static Long calculateTimeDifference(String timeIn, String timeOut) {
        long diff = 0l;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date newTimeIN = format.parse(timeIn);
            Date newTimeOut = format.parse(timeOut);

            diff = newTimeOut.getTime() - newTimeIN.getTime();

            return diff;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff;
    }

    public static int calculateDateDifference(String startDate) {
        int isGreater = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date leaveDate = format.parse(startDate);
            String date = format1.format(new Date()) + " 06:30";

            Date today = format.parse(date);
            if (SDF.format(leaveDate).compareTo(SDF.format(today)) < 0) {
                isGreater = 0;
            } else if (SDF.format(leaveDate).equals(SDF.format(today))) {
                isGreater = 0;
            } else {
                isGreater = 1;
            }

            return isGreater;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return isGreater;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String arrayToString(String array[]) {
        if (array.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            sb.append(",'").append(array[i]).append("'");
        }
        return sb.substring(1);
    }

    public static String getCommaSeperatedString(String[] stringData, boolean[] selectedData) {
        ArrayList<String> localList = new ArrayList<>();
        if (stringData != null && selectedData != null && stringData.length > 0 && selectedData.length > 0) {
            for (int i = 0; i < selectedData.length; i++) {
                if (selectedData[i]) {
                    localList.add(stringData[i]);
                }
            }
            String str = "";
            for (int j = 0; j < localList.size(); j++) {
                if (j == localList.size() - 1)
                    str = str + localList.get(j);
                else str = str + localList.get(j) + ",";
            }
            str.trim();
            return str;
        }
        return "";
    }

    public static String getCommaSeperatedValues(String[] stringData, boolean[] selectedData) {
        ArrayList<String> localList = new ArrayList<>();
        if (stringData != null && selectedData != null && stringData.length > 0 && selectedData.length > 0) {
            for (int i = 0; i < selectedData.length; i++) {
                if (selectedData[i]) {
                    localList.add("" + i);
                }
            }
            String str = "";
            for (int j = 0; j < localList.size(); j++) {
                if (j == localList.size() - 1)
                    str = str + localList.get(j);
                else str = str + localList.get(j) + ",";
            }
            str.trim();
            return str;
        }
        return "";
    }


    public static String diffBetweenTwoDates(Date startDate, Date endDate) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        return "" + elapsedDays + " days " + elapsedHours + " hours " + elapsedMinutes + " minuts";
    }

    public static double getWorkingHours(Date compStartTime, Date compEndtime) {
        double sT = compStartTime.getHours();
        double eT = compEndtime.getHours();
        if (compStartTime.getMinutes() > 0) sT = sT + .5;
        if (compEndtime.getMinutes() > 0) eT = eT + .5;
        Log.e("WORKING HRS:", "" + (eT - sT));
        return eT - sT;
    }

    public static boolean checkFileSize(String filePath) {
        TypedFile file = new TypedFile("image/*", new File(filePath));
        long fileSizeInBytes = file.length();
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;
        Log.e("fileSizeInMB", "::" + fileSizeInMB);
        if (fileSizeInMB >= 1) {
            return false;
        }
        return true;
    }

    public static final void setAppFont(ViewGroup mContainer, Typeface mFont) {
        if (mContainer == null || mFont == null) return;

        final int mCount = mContainer.getChildCount();

        // Loop through all of the children.
        for (int i = 0; i < mCount; ++i) {
            final View mChild = mContainer.getChildAt(i);
            if (mChild instanceof TextView) {
                // Set the font if it is a TextView.
                ((TextView) mChild).setTypeface(mFont);
            } else if (mChild instanceof ViewGroup) {
                // Recursively attempt another ViewGroup.
                setAppFont((ViewGroup) mChild, mFont);
            } else {
                try {
                    Method mSetTypeface = mChild.getClass().getMethod("setTypeface", Typeface.class);
                    mSetTypeface.invoke(mChild, mFont);
                } catch (Exception e) { /* Do something... */ }
            }
        }
    }

    public static String dateFormatWithGMT(String dtStart) {

        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat(" d" + " MMM" + "," + " yyyy");
        try {
            if (dtStart != null && !dtStart.equals("null")) {
                Date localTime = format.parse(dtStart);
                newDate = sdf.format(localTime);
                return newDate;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String dateFormatLocal(String dtStart) {

        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        // format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date localTime = format.parse(dtStart);
            // Date fromGmt = new Date(localTime.getTime() + TimeZone.getDefault().getOffset(localTime.getTime()));
            //String suffix = getDayNumberSuffix(fromGmt.getDate());
            SimpleDateFormat sdf = new SimpleDateFormat(" d" + " MMM" + "," + " yyyy");
            newDate = sdf.format(localTime);
            return newDate;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String locatToGMTNewFormat(String dtStart) {
        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat(" d" + " MMM" + "," + " yyyy HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date localTime = format.parse(dtStart);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date gmtTime = new Date(sdf.format(localTime));
            newDate = sdf1.format(gmtTime);
            return newDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }//193.19


    public static String gmtToLocalTime12(String dtStart) {
        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date localTime = format.parse(dtStart);
            newDate = sdf.format(localTime);
            return newDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String dateFormatWithGMTWithoutYear(String dtStart) {

        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat(" d" + " MMM");
        try {
            if (dtStart != null && !dtStart.equals("null")) {
                Date localTime = format.parse(dtStart);
                newDate = sdf.format(localTime);
                return newDate;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String getUsernames(String userNos, Context context) {
        try {
            if (userNos != null && userNos.length() > 0) {
                String users[] = userNos.split(",");
                String usernames = "";
                for (int i = 0; i < users.length; i++) {
                    Cursor cursor = Util.getDb(context).rawQuery("select * from " + Constant.UserTable + " where " + User.USER_ID + " =" + users[i], null);
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        if (usernames.length() == 0) {
                            usernames = cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)) + " " + cursor.getString(cursor.getColumnIndex(User.LAST_NAME));
                        } else {
                            usernames = usernames + ", " + cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)) + " " + cursor.getString(cursor.getColumnIndex(User.LAST_NAME));
                        }
                    }
                }
                return usernames;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getFilename(String path) {
        try {
            String name[] = path.split("/");
            return name[name.length - 1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFilenameType(String path) {
        try {
            Log.e("path", ">>" + path);
            String name[] = path.split("\\.");
            return name[name.length - 1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //Notification

    //For open activity
    public static void generateNotification(Context context, String title, Class<?> cls) {
        long when = System.currentTimeMillis();
        int icon = R.drawable.icon;
        int id = (int) System.currentTimeMillis();
        long[] pattern = {0, 200, 0};

        Intent notificationIntent;
        if (cls == null) notificationIntent = new Intent();
        else notificationIntent = new Intent(context, cls);

        // set intent so it does not start a new activity
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notification = mBuilder.setSmallIcon(R.drawable.icon_bell).setTicker(title).setWhen(id)
                .setAutoCancel(true)
                .setWhen(when)
                .setContentTitle(Constant.AppNameSuper)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setContentIntent(intent)
                .setVibrate(pattern)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentText(title).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    //For open fragment
    public static void generateNotificationFragment(Context context, String title, Class<?> cls, int fragmentNo) {
        long when = System.currentTimeMillis();
        int icon = R.drawable.icon;
        int id = (int) System.currentTimeMillis();
        long[] pattern = {0, 200, 0};

        Intent notificationIntent;
        if (cls == null) notificationIntent = new Intent();
        else notificationIntent = new Intent(context, cls);

        // set intent so it does not start a new activity
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("pageno", fragmentNo);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notification = mBuilder.setSmallIcon(R.drawable.icon_bell).setTicker(title).setWhen(id)
                .setAutoCancel(true)
                .setWhen(when)
                .setContentTitle(Constant.AppNameSuper)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setContentIntent(intent)
                .setVibrate(pattern)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentText(title).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

}
