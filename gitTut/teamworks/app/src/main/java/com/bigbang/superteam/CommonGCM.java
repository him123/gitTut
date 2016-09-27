package com.bigbang.superteam;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bigbang.superteam.admin.AdminDashboardNewActivity;
import com.bigbang.superteam.customer_vendor.CustomerVendorActivity;
import com.bigbang.superteam.dataObjs.CompanyLeavesDAO;
import com.bigbang.superteam.dataObjs.NotificationInfo;
import com.bigbang.superteam.login_register.HolidaysDAO;
import com.bigbang.superteam.login_register.InvitationActivity;
import com.bigbang.superteam.manager.ManagerDashboardNewActivity;
import com.bigbang.superteam.model.Company;
import com.bigbang.superteam.model.CompanyLeaves;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.model.Holidays;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.user.ResignUserActivity;
import com.bigbang.superteam.user.UserDashboardNewActivity;
import com.bigbang.superteam.user.UsersActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.LocationService;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by USER 8 on 6/17/2015.
 */
public class CommonGCM {

    private static final String TAG = CommonGCM.class.getSimpleName();

    public static void updateCompany(final Context mContext, String message, boolean showNotification) {

        Log.e("message:", ">>" + message);
        String companyID = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID);
        RestClient.getCommonService().getCompany(companyID, Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID), Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_TOKEN), new Callback<Response>() {
            @Override
            public void success(Response rc, Response response) {

                try {
                    Log.e(TAG, ">>" + Util.getString(response.getBody().in()));
                    JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                    String status = jsonObject.optString("status");
                    if (status.equals("Success")) {
                        JSONObject jData = jsonObject.optJSONObject("data");
                        Gson gson = new Gson();
                        Company company = gson.fromJson(jData.toString(), Company.class);
                        if (!company.getID().equals("0")) {
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_NAME, company.getName());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_LOGO, company.getLogo());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_MOBILE, company.getMobileNo());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_LANDLINE, company.getLandlineNo());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_EMAIL, company.getEmailID());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_Owner_ID, company.getOwnerID().toString());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_Created_By, company.getCreatedBy().toString());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_Type, "" + company.getCompanyTypeID());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_DESC, company.getDescription());
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY, new Gson().toJson(company));
                            //write(Constant.SHRED_PR.KEY_COMPANY_AddressList, new Gson().toJson(company.getAddressList()));
                            try {
                                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_AddressList, "" + jData.optJSONArray("AddressList"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, ">>" + error);
            }
        });

    }

    public static void updateCompanyInfo(final Context mContext, JSONObject jsonObject) {
        try {
            SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
            helper.createDatabase();
            SQLiteDatabase db = helper.openDatabase();

            JSONObject jData = jsonObject.optJSONObject("data");

            ContentValues values = new ContentValues();

            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);

            db.insert(Constant.NotificationTable, null, values);

            String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
            if (Arrays.asList("1", "2").contains(roleId)) {
                Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 6);
            } else if (Arrays.asList("3").contains(roleId)) {
                Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 6);
            } else if (Arrays.asList("4").contains(roleId)) {
                Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 5);
            }

            RestClient.getTeamWork().getCompanyInfo(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID),
                    Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID),
                    Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_TOKEN), new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {
                                Holidays holidays;
                                CompanyLeaves companyLeaves;
                                HolidaysDAO holidaysDAO = new HolidaysDAO(mContext);
                                CompanyLeavesDAO companyLeavesDAO = new CompanyLeavesDAO(mContext);

                                companyLeavesDAO.deleteAll();
                                holidaysDAO.deleteAll();

                                String json = Util.getString(response.getBody().in());
                                JSONObject jObjRaw = new JSONObject(json);

                                String status = jObjRaw.getString("status");
                                if (status.equals("Success"))
                                    Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA, jObjRaw.getString("companydata"));

                                JSONObject jobj = jObjRaw.getJSONObject("companydata");

                                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_STARTTIME, jobj.optString("startTime"));
                                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ENDTIME, jobj.optString("endTime"));

                                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_TRACKING_STARTTIME, jobj.getString("trackingStartTime"));
                                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_TRACKING_ENDTIME, jobj.getString("trakingEndTime"));

                                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_AUTO_LEAVE_UPDATE, jobj.optString("AutoLeaveUpdate"));

                                //Holidays update in local db
                                JSONArray holidaysJarr = jobj.getJSONArray("holidays");
                                if (holidaysJarr != null) {
                                    for (int cnt = 0; cnt < holidaysJarr.length(); cnt++) {
                                        holidays = new Holidays();

                                        holidays.holidayId = holidaysJarr.getJSONObject(cnt).getString("holidayId");
                                        holidays.holidayDate = holidaysJarr.getJSONObject(cnt).getString("holidayDate");
                                        holidays.holidayName = holidaysJarr.getJSONObject(cnt).getString("holidayName");

                                        holidaysDAO.save(holidays);
                                    }
                                }

                                //Leaves update in local db
                                JSONArray companyLeavesJarr = jobj.getJSONArray("companyLeaves");
                                if (companyLeavesJarr != null) {
                                    for (int cnt = 0; cnt < companyLeavesJarr.length(); cnt++) {
                                        companyLeaves = new CompanyLeaves();

                                        companyLeaves.companyId = companyLeavesJarr.getJSONObject(cnt).getString("companyId");
                                        companyLeaves.leaveType = companyLeavesJarr.getJSONObject(cnt).getString("leaveType");
                                        companyLeaves.noOfLeaves = companyLeavesJarr.getJSONObject(cnt).getString("noOfLeaves");
                                        companyLeaves.leaveUpdateCyle = companyLeavesJarr.getJSONObject(cnt).getString("leaveUpdateCyle");
                                        companyLeaves.active = String.valueOf(companyLeavesJarr.getJSONObject(cnt).getBoolean("active"));
                                        companyLeaves.modifiedBy = companyLeavesJarr.getJSONObject(cnt).getString("modifiedBy");
//                                        companyLeaves.lastModified = companyLeavesJarr.getJSONObject(cnt).getString("lastModified");

                                        companyLeavesDAO.save(companyLeaves);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateCustomerVendor(final Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        final SQLiteDatabase db = helper.openDatabase();

        try {
            String companyID = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID);
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            String CustomerID = jData.optString("CustomerID");
            final String Customer_Vendor_Type = jData.optString("Type");
            RestClient.getCommonService().getCustomerVendorDetails(companyID, CustomerID, new Callback<Response>() {
                @Override
                public void success(Response rc, Response response) {

                    Cursor cursor = null;
                    try {
                        Log.e(TAG, ">>" + Util.getString(response.getBody().in()));
                        JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                        String status = jsonObject.optString("status");
                        if (status.equals("Success")) {
                            JSONObject jData = jsonObject.optJSONObject("data");
                            Gson gson = new Gson();
                            Customer customer = gson.fromJson(jData.toString(), Customer.class);

                            ContentValues values = new ContentValues();
                            values.put("ID", customer.getID());
                            values.put("Name", customer.getName());
                            values.put("MobileNo", customer.getMobileNo());
                            values.put("LandlineNo", customer.getLandlineNo());
                            values.put("EmailID", customer.getEmailID());
                            values.put("OwnerID", customer.getOwnerID());
                            values.put("CompanyTypeID", customer.getCompanyTypeID());
                            values.put("CreatedBy", customer.getCreatedBy());
                            values.put("CompanyID", customer.getCompanyID());
                            values.put("Type", customer.getType());
                            values.put("Description", customer.getDescription());
                            values.put("AddressList", "" + jData.optString("AddressList"));
                            values.put("Logo", customer.getLogo());

                            String Table = "";
                            if (Customer_Vendor_Type.equals("U")) {
                                Table = Constant.tableCustomers;
                            } else {
                                Table = Constant.tableVendors;
                            }
                            boolean flag = false;
                            cursor = db.rawQuery("select * from " + Table + " where ID like \"" + customer.getID() + "\"", null);
                            if (cursor != null && cursor.getCount() > 0) flag = true;
                            if (flag) {
                                db.update(Table, values, "ID like \"" + customer.getID() + "\"", null);
                            } else {
                                db.insert(Table, null, values);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) cursor.close();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, ">>" + error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void UserResignApprovalToAdmin(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("userid", "" + jData.optString("ID"));
            values.put("title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("img", "" + jData.optString("Picture"));
            values.put("description", "" + jsonObject.optString("message"));
            values.put("data", "" + jData);
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("Status", "" + jsonObject.optString("Status"));

            String Status = jsonObject.optString("Status");
            String TransactionID = jsonObject.optString("TransactionID");
            int type = jsonObject.optInt("Type");

            Cursor cursor = null;
            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    cursor = db.rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like \"" + TransactionID + "\" AND type like \"" + type + "\"", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            if (flag) {
                db.insert(Constant.ApprovalsTable, null, values);
            } else {
                db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + TransactionID + "\"", null);
            }

            if (jsonObject.optInt("Status") != 1) showNotification = false;
            if (showNotification) {
                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 3);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 3);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 3);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void UserResignApprovalToUser(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification) {
                //generateNotification(mContext, jsonObject.optString("message"), ResignUserActivity.class);

                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 5);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 5);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 4);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void UserExpenseApprovalToAdmin(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("userid", "" + jData.optString("ID"));
            values.put("title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("img", "" + jData.optString("Picture"));
            values.put("description", "" + jsonObject.optString("message"));
            values.put("data", "" + jData);
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("Status", "" + jsonObject.optString("Status"));

            String Status = jsonObject.optString("Status");
            String TransactionID = jsonObject.optString("TransactionID");
            int type = jsonObject.optInt("Type");

            Cursor cursor = null;
            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    cursor = db.rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like \"" + TransactionID + "\" AND type like \"" + type + "\"", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            if (flag) {
                db.insert(Constant.ApprovalsTable, null, values);
            } else {
                db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + TransactionID + "\"", null);
            }

            if (jsonObject.optInt("Status") != 1) showNotification = false;
            if (showNotification) {
                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 3);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 3);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 3);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void UserExpenseNotification(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification) {
                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 8);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 8);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 6);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void changeDeviceReq(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("userid", "" + jData.optString("ID"));
            values.put("title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("img", "" + jData.optString("Picture"));
            values.put("description", "" + jsonObject.optString("message"));
            values.put("data", "" + jData);
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("Status", "" + jsonObject.optString("Status"));

            String Status = jsonObject.optString("Status");
            String TransactionID = jsonObject.optString("TransactionID");
            int type = jsonObject.optInt("Type");

            Cursor cursor = null;
            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    cursor = db.rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like \"" + TransactionID + "\" AND type like \"" + type + "\"", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            if (flag) {
                db.insert(Constant.ApprovalsTable, null, values);
            } else {
                db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + TransactionID + "\"", null);
            }

            if (jsonObject.optInt("Status") != 1) showNotification = false;
            if (showNotification) {

                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 3);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 3);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 3);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void USER_INVITATION_TO_ADMIN(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification) {
                Util.generateNotification(mContext,jsonObject.optString("message"),InvitationActivity.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void DELETE_USER(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification) {
                Util.generateNotification(mContext, jsonObject.optString("message"), UsersActivity.class);
                /*String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 6);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 6);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 5);
                }*/
            }

            String userID = "" + jData.optString("ID");
            db.delete(Constant.tableUsers, "userID like \"" + userID + "\"", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void CHANGE_DEVICE_ACK(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification) {
                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 5);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 5);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 4);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void DELETE_COMPANY(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification) {
                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 5);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 5);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 4);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void NEW_USER_ADDED(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification) {
                Util.generateNotification(mContext, jsonObject.optString("message"), UsersActivity.class);
                /*String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 6);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 6);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 5);
                }*/
            }

            if (Util.isOnline(mContext)) getUser(mContext, jData.optString("ID"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void UPDATE_USER(Context mContext, String message) {

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");

            if (Util.isOnline(mContext)) getUser(mContext, jData.optString("ID"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void getUser(final Context mContext, String UserID) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        final SQLiteDatabase db = helper.openDatabase();

        try {
            String companyID = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID);
            RestClient.getCommonService().getUser(UserID, companyID, Constant.AppName, new Callback<Response>() {
                @Override
                public void success(Response rc, Response response) {

                    Cursor cursor = null;
                    try {
                        Log.e(TAG, ">>" + Util.getString(response.getBody().in()));
                        JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                        String status = jsonObject.optString("status");
                        if (status.equals("Success")) {
                            JSONObject jData = jsonObject.optJSONObject("data");
                            JSONObject jUser = jData.optJSONObject("User");
                            Gson gson = new Gson();
                            User user = gson.fromJson(jUser.toString(), User.class);

                            ContentValues values = new ContentValues();
                            values.put("userID", user.getUserID());
                            values.put("firstName", user.getFirstName());
                            values.put("lastName", user.getLastName());
                            values.put("mobileNo1", user.getMobileNo1());
                            values.put("mobileNo2", user.getMobileNo2());
                            values.put("emailID", user.getEmailID());
                            values.put("permanentAddress", user.getPermanentAddress().toString());
                            values.put("picture", user.getPicture());
                            values.put("role_id", user.getRole().getId());
                            values.put("role_desc", user.getRole().getDesc());
                            values.put("temporaryAddress", user.getTemporaryAddress().toString());

                            String Table = Constant.tableUsers;
                            boolean flag = false;
                            cursor = db.rawQuery("select * from " + Table + " where userID like \"" + user.getUserID() + "\"", null);
                            if (cursor != null && cursor.getCount() > 0) flag = true;
                            if (flag) {
                                db.update(Table, values, "userID like \"" + user.getUserID() + "\"", null);
                            } else {
                                db.insert(Table, null, values);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) cursor.close();
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, ">>" + error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void InviteUserPayroll(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");

            String TransactionID = jsonObject.optString("TransactionID");
            Cursor cursor = null;
            boolean flag = true;

            try {
                if (TransactionID.length() > 0) {
                    cursor = db.rawQuery("select * from " + Constant.NotificationTable + " where TransactionID like \"" + TransactionID + "\" AND Type like \"" + jsonObject.optString("Type") + "\"", null);
                    if (cursor != null && cursor.getCount() > 0)
                        flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            if (flag) {
                ContentValues values = new ContentValues();
                values.put("Type", "" + jsonObject.optString("Type"));
                values.put("Time", "" + jsonObject.optString("LastModified"));
                values.put("User_Id", "" + jData.optString("ID"));
                values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
                values.put("Image_Url", "" + jData.optString("Picture"));
                values.put("Description", "" + jsonObject.optString("message"));
                values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
                values.put("Extra", "" + jData);
                db.insert(Constant.NotificationTable, null, values);
            }

            if (showNotification) {
                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 9);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 9);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 7);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void InviteUserPayroll(Context mContext, JSONObject jobj, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = jobj.getJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jobj.optString("Type"));
            values.put("Time", "" + jobj.optString("LastModified"));
            values.put("User_Id", "" + jsonObject.optString("ID"));
            values.put("Title", "" + jsonObject.optString("FirstName") + " " + jsonObject.optString("LastName"));
            values.put("Image_Url", "" + jsonObject.optString("Picture"));
            values.put("Description", "" + jobj.optString("message"));
            values.put("TransactionID", "" + jobj.optString("TransactionID"));
            values.put("Extra", "" + jsonObject);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification) {
                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jobj.optString("message"), AdminDashboardNewActivity.class, 9);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jobj.optString("message"), ManagerDashboardNewActivity.class, 9);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jobj.optString("message"), UserDashboardNewActivity.class, 7);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void InviteUser(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification) {
                Util.generateNotification(mContext, jsonObject.optString("message"), InvitationActivity.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void getNotification(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();

            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            String TransactionID = jsonObject.optString("TransactionID");
            int type = jsonObject.optInt("Type");

            Cursor cursor = null;
            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    cursor = db.rawQuery("select * from " + Constant.NotificationTable + " where TransactionID like \"" + TransactionID + "\" AND type like \"" + type + "\"", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            if (flag) {
                db.insert(Constant.NotificationTable, null, values);
            } else {
                db.update(Constant.NotificationTable, values, "TransactionID like \"" + TransactionID + "\"", null);
            }

            if (jsonObject.optInt("Status") != 1) showNotification = false;
            if (showNotification) {

                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 6);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 6);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 5);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void NEW_REPORTING_MEMBER(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification) {
                //generateNotification(mContext, jsonObject.optString("message"), null);
                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 5);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 5);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 4);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void UPDATE_ROLE_MANAGER(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification) {
                //generateNotification(mContext, jsonObject.optString("message"), null);
                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 5);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 5);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 4);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void ChangeMobileApprovalToAdmin(Context mContext, String message, boolean showNotification) {

        Log.e("message obj", ">>" + message);
        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();


        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("userid", "" + jData.optString("ID"));
            values.put("title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("img", "" + jData.optString("Picture"));
            values.put("description", "" + jsonObject.optString("message"));
            values.put("data", "" + jData);
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("Status", "" + jsonObject.optString("Status"));

            String TransactionID = jsonObject.optString("TransactionID");
            int type = jsonObject.optInt("Type");

            Cursor cursor = null;
            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    cursor = db.rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like \"" + TransactionID + "\" AND type like \"" + type + "\"", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            if (flag) {
                db.insert(Constant.ApprovalsTable, null, values);
            } else {
                db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + TransactionID + "\"", null);
            }

            if (jsonObject.optInt("Status") != 1) showNotification = false;
            if (showNotification) {

                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 3);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 3);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 3);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void ChangeMobileApprovalToUser(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification) {
                //generateNotification(mContext, jsonObject.optString("message"), null);

                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 5);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 5);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 4);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void UpdateCustomerApprovalToAdmin(Context mContext, String message, boolean showNotification) {

        Log.e("message obj", ">>" + message);
        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("userid", "" + jData.optString("ID"));
            values.put("title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("img", "" + jData.optString("Picture"));
            values.put("description", "" + jsonObject.optString("message"));
            values.put("data", "" + jData);
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("Status", "" + jsonObject.optString("Status"));

            String TransactionID = jsonObject.optString("TransactionID");
            int type = jsonObject.optInt("Type");

            Cursor cursor = null;
            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    cursor = db.rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like \"" + TransactionID + "\" AND type like \"" + type + "\"", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            if (flag) {
                db.insert(Constant.ApprovalsTable, null, values);
            } else {
                db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + TransactionID + "\"", null);
            }

            if (jsonObject.optInt("Status") != 1) showNotification = false;
            if (showNotification) {

                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 3);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 3);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 3);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void UpdateCustomerApprovalToUser(Context mContext, String message, boolean showNotification) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject jData = jsonObject.optJSONObject("data");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jsonObject.optString("Type"));
            values.put("Time", "" + jsonObject.optString("LastModified"));
            values.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            values.put("User_Id", "" + jData.optString("ID"));
            values.put("Title", "" + jData.optString("FirstName") + " " + jData.optString("LastName"));
            values.put("Image_Url", "" + jData.optString("Picture"));
            values.put("Description", "" + jsonObject.optString("message"));
            values.put("Extra", "" + jData);
            db.insert(Constant.NotificationTable, null, values);

            if (showNotification)
                Util.generateNotification(mContext, jsonObject.optString("message"), CustomerVendorActivity.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUserCurrentLoactionAPI(Context context, double latitude, double longitude, String AdminID, String uniqueKey) {
        new updateUserCurrentLocation(context, latitude, longitude, AdminID, uniqueKey).execute();
        context.startService(new Intent(context, LocationService.class));
        Util.registerUserTrackingReceiver(context);
    }

    static class updateUserCurrentLocation extends AsyncTask<Void, String, String> {

        double latitude;
        double longitude;
        Context context;
        String AdminID;
        String uniqueKey;
        GPSTracker gps;

        public updateUserCurrentLocation(Context context, double latitude, double longitude, String AdminID, String uniqueKey) {
            this.context = context;
            this.latitude = latitude;
            this.longitude = longitude;
            this.AdminID = AdminID;
            this.uniqueKey = uniqueKey;
            gps = new GPSTracker(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String date = sdf1.format(new Date());

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            String response = "";
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("latitude", "" + latitude);
                jsonObject.put("longitude", "" + longitude);
                jsonObject.put("location", "" + Util.getAddress(context, latitude, longitude));
                jsonObject.put("companyid", "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_ID));

                if (gps.canGetLocation()) {
                    jsonObject.put("isGPSOn", "true");
                } else {
                    jsonObject.put("isGPSOn", "false");
                }
                jsonObject.put("isMockLocation", "false");

                params1.add(new BasicNameValuePair("JSON", jsonObject.toString()));
                params1.add(new BasicNameValuePair("AdminID", "" + AdminID));
                params1.add(new BasicNameValuePair("uniqueKey", "" + uniqueKey));
                params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID)));

                Log.e("params1", ">>" + params1);
                response = Util.makeServiceCall(Constant.URL + "updateUserCurrentLocation", 2, params1, context);

            } catch (Exception e) {
                Log.e("", "Exception : " + e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public static void InsertIntoApprovals(JSONObject jObj, Context context) {
        try {
            Log.e("jObj", ">>" + jObj);
            int roleId = Integer.parseInt(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID));
            int type = jObj.getInt("Type");
            String TransactionID = jObj.optString("TransactionID");
            String msg = jObj.getString("message");
            JSONObject jsonObject = jObj.getJSONObject("data");
            ContentValues taskValues = new ContentValues();
            String id = "";
            boolean flag = true;
            NotificationInfo noti = new NotificationInfo();
            switch (type) {
                //workitem & projects:
                case Constant.CREATE_NEWWORKITEM:
                    WorkItem_GCM.HandleNewWorkItemApproval(jObj, context, false);
                    break;
                case Constant.EDIT_PROJECT_APPROVAL:
                    WorkItem_GCM.HandleEditProjectApproval(jObj, context, false);
                    break;
                case Constant.APPROVE_WORKITEMUPDATE:
                    if (jObj.optInt("Status") == 1) {
                        WorkItem_GCM.HandleWorkItemUpdate(jObj, context, false);
                    }
                    WorkItem_GCM.HandleWorkUpdateApproval(jObj, context, false);
                    break;
                case Constant.EDIT_WORKITEM_APPROVAL:
                    WorkItem_GCM.HandleEditWorkItemApproval(jObj, context, false);
                    break;
                case Constant.APPROVAL_NEWPRJECT:
                    WorkItem_GCM.HandleNewProjectapproval(jObj, context, false);
                    break;


                //commonservices:
                case Constant.CHANGE_MOBILE_APPROVAL_TO_ADMIN:
                    if (Arrays.asList(1, 2).contains(roleId)) {
                        ChangeMobileApprovalToAdmin(context, "" + jObj, false);
                    }
                    break;
                case Constant.USER_RESIGN_APPROVAL_TO_ADMIN:
                    if (Arrays.asList(1, 2).contains(roleId)) {
                        UserResignApprovalToAdmin(context, "" + jObj, false);
                    }
                    break;
                case Constant.UPDATE_CUSTOMER_APPROVAL_TO_ADMIN:
                    Log.e("roleId", ">>" + roleId);
                    if (Arrays.asList(1, 2).contains(roleId)) {
                        UpdateCustomerApprovalToAdmin(context, "" + jObj, false);
                    }
                    break;
                case Constant.CHANGE_DEVICE_REQUEST:
                    if (Arrays.asList(1, 2).contains(roleId)) {
                        changeDeviceReq(context, "" + jObj, false);
                    }
                    break;

                //Leave & Attendance:
                case Constant.ATTENDANCE_APPROVAL_TO_ADMIN:
                    if (!Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                        String displayMsg = jObj.optString("message");
                        Leave_Attendance_GCM.insertIntoApprovals(jObj, "" + type, context, false, displayMsg);
                    }
                    break;

                case Constant.LEAVE_APPROVAL_TO_ADMIN:
                    if (!Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                        String data = jObj.optString("data");
                        String displayMsg = jObj.optString("message");
                        Leave_Attendance_GCM.insertIntoLeaveApprovals(jObj, "" + type, context, displayMsg, false);
                    }
                    break;

                case Constant.UPDATETIME_APPROVAL_TO_ADMIN:
                    if (!Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                        String displayMsg = jObj.optString("message");
                        String data = jObj.optString("data");
                        JSONObject jsonObj = new JSONObject(data);
                        Leave_Attendance_GCM.insertUpdateTimeValue(jObj, "" + type, context, displayMsg, false);
                    }
                    break;

                case Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_ADMIN:
                    if (!Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                        String data = jObj.optString("data");
                        Leave_Attendance_GCM.insertIntoClientVendor(jObj, "" + type, context, false);
                    }
                    break;

                //Expense
                case Constant.EXPENSE_APPROVAL:
                    if (Arrays.asList(1, 2).contains(roleId)) {
                        UserExpenseApprovalToAdmin(context, "" + jObj, false);
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void InsertIntoNotifications(JSONObject jObj, Context context) {
        try {
            int roleId = Integer.parseInt(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID));
            int type = jObj.getInt("Type");
            JSONObject jsonObject;
            NotificationInfo noti;
            Log.e("NOTIFICATION RECEIVED:", "" + Util.GetDate() + ": " + jObj.toString());
            switch (type) {

                //commonservices:
                case Constant.CHANGE_MOBILE_APPROVAL_TO_USER:
                    if (Arrays.asList(3, 4).contains(roleId)) {
                        ChangeMobileApprovalToUser(context, "" + jObj, false);
                    }
                    break;
                case Constant.USER_RESIGN_APPROVAL_TO_USER:
                    if (Arrays.asList(3, 4).contains(roleId)) {
                        UserResignApprovalToUser(context, "" + jObj, false);
                    }
                    break;
                case Constant.NEW_USER_ADDED:
                    NEW_USER_ADDED(context, "" + jObj, false);
                    break;
                case Constant.NEW_REPORTING_MEMBER:
                    NEW_REPORTING_MEMBER(context, "" + jObj, false);
                    break;
                case Constant.DELETE_USER:
                    DELETE_USER(context, "" + jObj, false);
                    break;
                case Constant.DELETE_COMPANY:
                    DELETE_COMPANY(context, "" + jObj, false);
                    break;
                case Constant.USER_INVITATION_TO_ADMIN:
                    USER_INVITATION_TO_ADMIN(context, "" + jObj, false);
                    break;
                case Constant.INVITE_USER:
                    InviteUser(context, "" + jObj, false);
                    break;
                case Constant.UPDATE_ROLE_OR_MANAGER:
                    UPDATE_ROLE_MANAGER(context, "" + jObj, false);
                    break;
                case Constant.UPDATE_CUSTOMER_APPROVAL_TO_USER:
                    if (Arrays.asList(3, 4).contains(roleId)) {
                        UpdateCustomerApprovalToUser(context, "" + jObj, false);
                    }
                    break;
                case Constant.UPDATE_USER:
                    UPDATE_USER(context, "" + jObj);
                    break;
                case Constant.UPDATE_COMPANYINFO:
                    Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA, "" + jObj.optString("data"));
                    break;
                case Constant.GET_COMPANY_PRIVILAGES:
                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("1") || Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("2"))
                        Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_PRIVILAGES, "" + jObj);
                    break;
                case Constant.GET_USER_PRIVILAGES_MANAGER:
                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3")) {
                        Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_USER_PRIVILAGES, "" + jObj);
                        Privileges.Init(context);
                    }
                    break;
                case Constant.GET_USER_PRIVILAGES_TEAMMEMBER:
                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                        Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_USER_PRIVILAGES, "" + jObj);
                        Privileges.Init(context);
                    }
                    break;
                case Constant.UPDATE_COMPANY:
                    updateCompany(context, "" + jObj, false);
                    break;
                case Constant.UPDATE_CUSTOMER_VENDOR:
                    updateCustomerVendor(context, "" + jObj, false);
                    break;
                case Constant.UPDATE_USER_COMPANYINFO:
                    getNotification(context, "" + jObj, false);
                    break;
                case Constant.CHANGE_DEVICE_ACK:
                    CHANGE_DEVICE_ACK(context, "" + jObj, false);
                    break;

                //Leave & Attendance:
                case Constant.ATTENDANCE_APPROVAL_TO_USER:
                    if (Arrays.asList(3, 4).contains(roleId)) {
                        Leave_Attendance_GCM.AttendanceApprove_TO_User_Notification(context, jObj, false);
                    }
                    break;
                case Constant.LEAVE_APPROVAL_TO_USER:
                    if (Arrays.asList(3, 4).contains(roleId)) {
                        Leave_Attendance_GCM.LeaveApproval_To_User(context, jObj, false);
                    }
                    break;
                case Constant.UPDATETIME_APPROVAL_TO_USER:
                    if (Arrays.asList(3, 4).contains(roleId)) {
                        Leave_Attendance_GCM.TimeUpdate_To_User(context, jObj, false);
                    }
                    break;
                case Constant.LEAVE_WITHDRAW_TO_ADMIN:
                    if (!Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                        String data = jObj.optString("data");
                        JSONObject jsonObj = new JSONObject(data);
                        String approvalId = jsonObj.optString("uniqueKey");
                        Leave_Attendance_GCM.removeFromDataBase(approvalId, context, jObj);
                    }
                    break;
                case Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_USER:
                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3") || Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                        Leave_Attendance_GCM.AttendanceApprove_TO_User_Notification(context, jObj, false);
                    }
                    break;
                case Constant.LEAVE_UPDATE_TO_USER:
                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3") || Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                        Leave_Attendance_GCM.LeaveApproval_To_User(context, jObj, false);
                    }
                    break;


                //Workitem & Projects:
                case Constant.NEW_WORKITEM:
                    WorkItem_GCM.HandleNewWorkItem(jObj, context, false);
                    break;
                case Constant.REJECT_NEWWORKITEM:
                    WorkItem_GCM.HandleNewWorkItemRejection(jObj, context, false);
                    break;
                case Constant.UPDATE_WORKITEM:
                    WorkItem_GCM.HandleWorkItemUpdate(jObj, context, false);
                    break;
                case Constant.REJECT_WORKITEMUPDATE:
                    WorkItem_GCM.HandleWorkUpdateRejection("" + jObj, context, false);
                    break;
                case Constant.CREATE_NEWPRJECT:
                    WorkItem_GCM.HandleNewProject(jObj, context, true, false);
                    break;
                case Constant.MODIFIED_PROJECT:
                    WorkItem_GCM.HandleModifiedProject(jObj, context, true, false);
                    break;
                case Constant.REJECT_NEWPRJECT:
                    WorkItem_GCM.HandleNewProjectRejection(jObj, context, false);
                    break;
                case Constant.PROJECT_APPROVED:
                    WorkItem_GCM.HandleEditProjectApproved(jObj, context, false);
                    break;
                case Constant.PROJECT_REJECTED:
                    WorkItem_GCM.HandleEditProjectRejected(jObj, context, false);
                    break;
                case Constant.WORKITEM_MODIFIED:
                    WorkItem_GCM.HandleEditWorkItemApproved(jObj, context);
                    break;
                case Constant.ATTACHMENTS:
                    WorkItem_GCM.HandleAttachment(jObj, context);
                    break;

                //payroll
                case Constant.ADMIN_PAYROLL:
                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equalsIgnoreCase("1")
                            || Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equalsIgnoreCase("2")) {

                        InviteUserPayroll(context, jObj, false);
                    }
                    break;
                case Constant.USER_PAYSLIP:
                    if (roleId > 1) {
                        InviteUserPayroll(context, jObj, false);
                    }
                    break;

                //Expense
                case Constant.EXPENSE_NOTIFICATION:
                    if (Arrays.asList(3, 4).contains(roleId)) {
                        UserExpenseNotification(context, "" + jObj, false);
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
