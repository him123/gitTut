package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.expenses.ExpenseType;
import com.bigbang.superteam.expenses.UpdateExpenseActivity;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.admin.ApprovalDetail_Activity;
import com.bigbang.superteam.admin.CustomerVendorApprovalActivity;
import com.bigbang.superteam.admin.LeaveApprovalDetails;
import com.bigbang.superteam.admin.LocationApprovalDetailActivity;
import com.bigbang.superteam.admin.ResignationApprovalActivity;
import com.bigbang.superteam.admin.UpdateAttendanceDetails;
import com.bigbang.superteam.dataObjs.Approval;
import com.bigbang.superteam.dataObjs.Project;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.dataObjs.WorkTransaction;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.workitem.WorkApprovalDetail_Activity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by USER 3 on 4/15/2015.
 */
public class ApprovalListAdapter extends BaseAdapter {
    private ArrayList<Approval> data;
    private static Activity mContext;
    private static LayoutInflater inflater = null;

    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    ProgressBar progresBar;
    String TAG = "ApprovalListAdapter";
    private TransparentProgressDialog pd;

    SQLiteDatabase db;

    public ApprovalListAdapter(Activity ctx, ArrayList<Approval> d, ProgressBar pBar) {
        mContext = ctx;
        data = d;
        progresBar = pBar;
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(ctx));
        pd = new TransparentProgressDialog(ctx, R.drawable.progressdialog, false);

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
    }

    private void update(int index, int status) {
        data.get(index).setStatus(status);
        notifyDataSetChanged();
    }

    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int index, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listraw_approval, null);
            holder = new ViewHolder(view);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        holder.tvName.setText("" + data.get(index).getTitle().toUpperCase());
        holder.itemTitleTv.setText("" + data.get(index).getTitle());

        if (data.get(index).getDescription().equals("Manual Attendance Half")) {
            holder.itemDescriptionTv.setText("Manual Attendance");
        } else {
            holder.itemDescriptionTv.setText("" + data.get(index).getDescription());
        }

        String logo = data.get(index).getImage();
        imageLoader.displayImage("", holder.imgUser, options);
        if (data.get(index).getImage() != null)
            imageLoader.displayImage(logo, holder.imgUser, options);

        if (logo.length() > 4) {
            holder.tvName.setVisibility(View.GONE);
        } else {
            holder.tvName.setVisibility(View.VISIBLE);
        }

        int status = data.get(index).getStatus();
        Log.e("status", "index:" + index + ":" + status);
        if (status == 1) {
            //pending
            holder.rlApprove.setVisibility(View.VISIBLE);
            holder.rlReject.setVisibility(View.VISIBLE);
            holder.imgColor.setVisibility(View.GONE);
        } else if (status == 2) {
            //approved
            holder.rlApprove.setVisibility(View.GONE);
            holder.rlReject.setVisibility(View.GONE);
            holder.imgColor.setVisibility(View.VISIBLE);
            holder.imgColor.setBackgroundResource(R.drawable.approved_color);
        } else if (status == 3) {
            //rejected
            holder.rlApprove.setVisibility(View.GONE);
            holder.rlReject.setVisibility(View.GONE);
            holder.imgColor.setVisibility(View.VISIBLE);
            holder.imgColor.setBackgroundResource(R.drawable.rejected_color);
        } else if (status == 4) {
            //cancelled or withdrawn:
            holder.rlApprove.setVisibility(View.GONE);
            holder.rlReject.setVisibility(View.GONE);
            holder.imgColor.setVisibility(View.VISIBLE);
            holder.imgColor.setBackgroundResource(R.drawable.offline_color);
        } else {
            holder.rlApprove.setVisibility(View.GONE);
            holder.rlReject.setVisibility(View.GONE);
            holder.imgColor.setVisibility(View.GONE);
        }

        int type = Integer.parseInt(data.get(index).getType());
        if (type == Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_ADMIN && status == 1) {
            holder.relativeAlwaysAllow.setVisibility(View.VISIBLE);
        } else {
            holder.relativeAlwaysAllow.setVisibility(View.GONE);
        }

        holder.tvDate.setText("");

        try {
            String currentstrDate = Util.sdf.format(new Date());
            currentstrDate = Util.locatToUTC(currentstrDate);
            Date currentDate = Util.sdf.parse(currentstrDate);
            Date date = Util.sdf.parse("" + data.get(index).getTime());

            //milliseconds
            long different = currentDate.getTime() - date.getTime();

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

            if (elapsedDays > 0) holder.tvDate.setText(elapsedDays + "d");
            else if (elapsedHours > 0) holder.tvDate.setText(elapsedHours + "h");
            else if (elapsedMinutes > 0) holder.tvDate.setText(elapsedMinutes + "m");
            else if (elapsedSeconds > 0) holder.tvDate.setText(elapsedSeconds + "s");
            else if (elapsedSeconds == 0) holder.tvDate.setText("1s");
            else holder.tvDate.setText("");


        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.rlApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Util.isOnline(mContext)) {
                    int Type = Integer.parseInt(data.get(index).getType());

                    switch (Type) {

                        //leave & attendance:
                        case Constant.ATTENDANCE_APPROVAL_TO_ADMIN:
                           /* if (data.get(index).getDescription().equals("Manual Attendance") || data.get(index).getDescription().equals("CheckOut Request") || data.get(index).getDescription().equals("Manual Attendance Half")) {
                                new attendanceApproveOrReject(index, "CheckOut", "acceptCheckOutAttendanceApproval").execute();
                            } else {
                                new attendanceApproveOrReject(index, "CheckIn", "acceptCheckInAttendanceApproval").execute();
                            }*/
                            new ApproveAttendance(index, "true", false).execute();
                            break;
                        case Constant.LEAVE_APPROVAL_TO_ADMIN:
                            new ApproveLeaves(index, "true").execute();
                            break;
                        case Constant.UPDATETIME_APPROVAL_TO_ADMIN:
                            // new UpdateTimeAccept(index, "acceptUpdateCheckInOutTimeApproval").execute();

                            new ApproveAttendance(index, "true", false).execute();
                            break;
                        case Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_ADMIN:
                            if (holder.checkBox.isChecked() == true) {
                                new ApproveAttendance(index, "true", true).execute();
                            } else {
                                new ApproveAttendance(index, "true", false).execute();
                            }
                            break;


                        //commonservices:
                        case Constant.CHANGE_MOBILE_APPROVAL_TO_ADMIN:
                            new ChangeMobileApproval(index, "true", mContext).execute();
                            break;
                        case Constant.USER_RESIGN_APPROVAL_TO_ADMIN:
                            new UserResignApproval(index, "true", mContext).execute();
                            break;
                        case Constant.UPDATE_CUSTOMER_APPROVAL_TO_ADMIN:
                            new acceptUpdateCustomer(index, "true", mContext).execute();
                            break;
                        case Constant.CHANGE_DEVICE_REQUEST:
                            new approvechangeRegMobile(index, "true", mContext).execute();
                            break;

                        //workitem & projects:
                        case Constant.APPROVAL_NEWPRJECT:
                            StartRejectOrApproval(data.get(index), index, true);
                            break;
                        case Constant.CREATE_NEWWORKITEM:
                            StartRejectOrApproval(data.get(index), index, true);
                            break;
                        case Constant.APPROVE_WORKITEMUPDATE:
                            StartRejectOrApproval(data.get(index), index, true);
                            break;
                        case Constant.EDIT_WORKITEM_APPROVAL:
                            StartRejectOrApproval(data.get(index), index, true);
                            break;
                        case Constant.EDIT_PROJECT_APPROVAL:
                            StartRejectOrApproval(data.get(index), index, true);
                            break;
                        case Constant.EXPENSE_APPROVAL:
                            new UserExpensesApproval(index, "true", mContext).execute();
                            break;
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }

            }
        });
        holder.rlReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Util.isOnline(mContext)) {

                    int Type = Integer.parseInt(data.get(index).getType());

                    switch (Type) {

                        //leave & attendance:
                        case Constant.ATTENDANCE_APPROVAL_TO_ADMIN:
                            new ApproveAttendance(index, "false", false).execute();
                            break;
                        case Constant.LEAVE_APPROVAL_TO_ADMIN:
                            new ApproveLeaves(index, "false").execute();
                            break;
                        case Constant.UPDATETIME_APPROVAL_TO_ADMIN:
                            // new UpdateTimeReject(index, "rejectUpdateCheckInOutApproval").execute();
                            new ApproveAttendance(index, "false", false).execute();
                            break;
                        case Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_ADMIN:
                            if (holder.checkBox.isChecked() == true) {
                                new ApproveAttendance(index, "false", true).execute();
                            } else {
                                new ApproveAttendance(index, "false", false).execute();
                            }
                            break;


                        //commonservices:
                        case Constant.CHANGE_MOBILE_APPROVAL_TO_ADMIN:
                            new ChangeMobileApproval(index, "false", mContext).execute();
                            break;
                        case Constant.USER_RESIGN_APPROVAL_TO_ADMIN:
                            new UserResignApproval(index, "false", mContext).execute();
                            break;
                        case Constant.UPDATE_CUSTOMER_APPROVAL_TO_ADMIN:
                            new acceptUpdateCustomer(index, "false", mContext).execute();
                            break;
                        case Constant.CHANGE_DEVICE_REQUEST:
                            new approvechangeRegMobile(index, "false", mContext).execute();
                            break;

                        //workitem & projects:
                        case Constant.APPROVAL_NEWPRJECT:
                            StartRejectOrApproval(data.get(index), index, false);
                            break;
                        case Constant.CREATE_NEWWORKITEM:
                            StartRejectOrApproval(data.get(index), index, false);
                            break;
                        case Constant.APPROVE_WORKITEMUPDATE:
                            StartRejectOrApproval(data.get(index), index, false);
                            break;
                        case Constant.EDIT_WORKITEM_APPROVAL:
                            StartRejectOrApproval(data.get(index), index, false);
                            break;
                        case Constant.EDIT_PROJECT_APPROVAL:
                            StartRejectOrApproval(data.get(index), index, false);
                            break;
                        case Constant.EXPENSE_APPROVAL:
                            new UserExpensesApproval(index, "false", mContext).execute();
                            break;

                        default:
                            break;
                    }

                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }

        });

        view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        int status = data.get(index).getStatus();
                                        if (status == 1) {
                                            int Type = Integer.parseInt(data.get(index).getType());

                                            switch (Type) {

                                                //leave & attendance:
                                                case Constant.ATTENDANCE_APPROVAL_TO_ADMIN:
                                                    Intent approvalDetailIntent = new Intent(mContext, ApprovalDetail_Activity.class);
                                                    approvalDetailIntent.putExtra("TransactionId", "" + data.get(index).getTransactionID());
                                                    approvalDetailIntent.putExtra("data", "" + data.get(index).getData());
                                                    mContext.startActivity(approvalDetailIntent);
                                                    ((Activity) mContext).overridePendingTransition(R.anim.enter_from_left,
                                                            R.anim.hold_bottom);

                                                    break;
                                                case Constant.LEAVE_APPROVAL_TO_ADMIN:
                                                    Intent iLeave = new Intent(mContext, LeaveApprovalDetails.class);
                                                    iLeave.putExtra("startDate", "" + data.get(index).getDate());
                                                    iLeave.putExtra("endDate", "" + data.get(index).getEndDate());
                                                    iLeave.putExtra("reason", "" + data.get(index).getReason());
                                                    iLeave.putExtra("leaveType", "" + data.get(index).getDescription());
                                                    iLeave.putExtra("leaveStatus", "Pending");
                                                    iLeave.putExtra("leaveId", "" + data.get(index).getId());
                                                    iLeave.putExtra("userID", "" + data.get(index).getUserId());
                                                    iLeave.putExtra("TransactionID", "" + data.get(index).getTransactionID());
                                                    iLeave.putExtra("data", "" + data.get(index).getData());
                                                    iLeave.putExtra("imgUrl", "" + data.get(index).getImage());
                                                    iLeave.putExtra("adminId", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID));
                                                    //iLeave.putExtra("activityName", "ApprovalListAdapter");
                                                    iLeave.putExtra("fromActivity", Constant.FROM_APPROVAL_ADAPTER);
                                                    mContext.startActivity(iLeave);
                                                    ((Activity) mContext).overridePendingTransition(R.anim.enter_from_left,
                                                            R.anim.hold_bottom);

                                                    break;
                                                case Constant.UPDATETIME_APPROVAL_TO_ADMIN:
                                                    Intent iLeave1 = new Intent(mContext, UpdateAttendanceDetails.class);
                                                    iLeave1.putExtra("data", "" + data.get(index).getData());
                                                    iLeave1.putExtra("TransactionId", "" + data.get(index).getTransactionID());
                                                    mContext.startActivity(iLeave1);
                                                    ((Activity) mContext).overridePendingTransition(R.anim.enter_from_left,
                                                            R.anim.hold_bottom);
                                                    break;
                                                case Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_ADMIN:
                                                    Intent iAttendance = new Intent(mContext, LocationApprovalDetailActivity.class);
                                                    iAttendance.putExtra("data", "" + data.get(index).getData());
                                                    iAttendance.putExtra("TransactionId", "" + data.get(index).getTransactionID());
                                                    mContext.startActivity(iAttendance);
                                                    ((Activity) mContext).overridePendingTransition(R.anim.enter_from_left,
                                                            R.anim.hold_bottom);
                                                    break;


                                                //workitem & projects:
                                                case Constant.APPROVAL_NEWPRJECT:
                                                    startWorkApprovalDetails(data.get(index));
                                                    break;
                                                case Constant.CREATE_NEWWORKITEM:
                                                    startWorkApprovalDetails(data.get(index));
                                                    break;
                                                case Constant.APPROVE_WORKITEMUPDATE:
                                                    startWorkApprovalDetails(data.get(index));
                                                    break;
                                                case Constant.EDIT_WORKITEM_APPROVAL:
                                                    startWorkApprovalDetails(data.get(index));
                                                    break;
                                                case Constant.EDIT_PROJECT_APPROVAL:
                                                    startWorkApprovalDetails(data.get(index));
                                                    break;

                                                //Commonservices:
                                                case Constant.USER_RESIGN_APPROVAL_TO_ADMIN:
                                                    Intent intent = new Intent(mContext, ResignationApprovalActivity.class);
                                                    intent.putExtra("Approval", data.get(index));
                                                    mContext.startActivity(intent);
                                                    mContext.overridePendingTransition(R.anim.enter_from_left,
                                                            R.anim.hold_bottom);
                                                    break;
                                                case Constant.UPDATE_CUSTOMER_APPROVAL_TO_ADMIN:
                                                    Intent intent1 = new Intent(mContext, CustomerVendorApprovalActivity.class);
                                                    intent1.putExtra("Approval", data.get(index));
                                                    mContext.startActivity(intent1);
                                                    mContext.overridePendingTransition(R.anim.enter_from_left,
                                                            R.anim.hold_bottom);
                                                    break;
                                                case Constant.EXPENSE_APPROVAL:
                                                    /*Intent intent2 = new Intent(mContext, ApproveRejectExpenseActivity.class);
                                                    intent2.putExtra("Approval", data.get(index).getTransactionID());
                                                    mContext.startActivity(intent2);
                                                    mContext.overridePendingTransition(R.anim.enter_from_left,
                                                            R.anim.hold_bottom);*/
                                                    getExpenseForTransaction(data.get(index).getTransactionID());
                                                    break;

                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                }

        );
        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.title)
        TextView itemTitleTv;
        @InjectView(R.id.desc)
        TextView itemDescriptionTv;
        @InjectView(R.id.tvDate)
        TextView tvDate;
        @InjectView(R.id.imgvwUser)
        ImageView imgUser;
        @InjectView(R.id.imgColor)
        ImageView imgColor;
        @InjectView(R.id.rlApprove)
        RelativeLayout rlApprove;
        @InjectView(R.id.rlReject)
        RelativeLayout rlReject;
        @InjectView(R.id.rlAlwaysAllow)
        RelativeLayout relativeAlwaysAllow;
        @InjectView(R.id.checkbox)
        CheckBox checkBox;
        @InjectView(R.id.tvName)
        TextView tvName;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

    private void getExpenseForTransaction(String transactionID){
        if (Util.isOnline(mContext)) {

            if (pd != null)
                pd.show();

            RestClient.getCommonService().getExpenseForTransaction(Constant.AppName,
                    transactionID,
                    new Callback<ExpenseType>() {
                @Override
                public void success(ExpenseType type, Response response) {
                    try {
                        Map<String, String> map = Util.readStatus(response);
                        boolean isSuccess = map.get("status").equals("Success");

                        String json = Util.getString(response.getBody().in());
                        JSONObject jObj = new JSONObject(json);

                        if (isSuccess) {
                            Intent intent = new Intent(mContext, UpdateExpenseActivity.class);
                            Bundle b = new Bundle();
                            b.putParcelable("expense",type);
                            b.putBoolean("isMember", true);
                            intent.putExtras(b);
                            mContext.startActivity(intent);
                        } else {
                            Toast.makeText(mContext, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();
                    } catch (Exception e) {
                        Log.d("", "Exception: " + e);
                        pd.dismiss();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pd.dismiss();
                }
            });

        } else {
            Toast.makeText(mContext, Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    //Leave & Attendance:
    class ApproveAttendance extends AsyncTask<Void, String, String> {

        int index;
        String actionName;
        String response;
        boolean isChecked;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progresBar.setVisibility(View.VISIBLE);
            if (pd != null) {
                pd.show();
            }
        }

        public ApproveAttendance(int pos, String action, boolean isChecked) {
            this.index = pos;
            this.actionName = action;
            this.isChecked = isChecked;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                JSONObject jData = new JSONObject(data.get(index).getData());
              /*  String strAddress = jData.optString("addressObject");
                JSONObject jAddress = new JSONObject(strAddress);*/
                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("MemberID", "" + data.get(index).getUserId()));
                params1.add(new BasicNameValuePair("AttendanceID", "" + data.get(index).getId()));
                params1.add(new BasicNameValuePair("AdminID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID)));
                params1.add(new BasicNameValuePair("TransactionID", "" + data.get(index).getTransactionID()));
                // params1.add(new BasicNameValuePair("AddressMasterID", "" + jData.optString("addressId")));
                // params1.add(new BasicNameValuePair("AddressMasterID", "" + jAddress.optString("AddressID")));
                params1.add(new BasicNameValuePair("Approve", "" + actionName));
                if (isChecked) {
                    params1.add(new BasicNameValuePair("IsPermanentLocation", "true"));
                } else {
                    params1.add(new BasicNameValuePair("IsPermanentLocation", "false"));
                }
                params1.add(new BasicNameValuePair("ClientID", "" + jData.optString("clientVendorID")));
                // params1.add(new BasicNameValuePair("CompanyID", "" + jData.optString("companyId")));
                //params1.add(new BasicNameValuePair("AttendanceType", "" + jData.optString("attendanceType")));

                Log.e("params1", ">>" + params1);
                response = Util.makeServiceCall(Constant.URL + "approveAttendance", 2, params1, mContext);
                Log.e(TAG, "** response is:- " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                //progresBar.setVisibility(View.GONE);
                Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                if (status.equals("Success")) {
                    int approvalStatus = 1;
                    if (actionName.equals("true")) approvalStatus = 2;
                    else if (actionName.equals("false")) approvalStatus = 3;
                    updateApprovalsDB(index, approvalStatus);
                }

            } catch (Exception e) {
                e.printStackTrace();
                //progresBar.setVisibility(View.GONE);
            }
        }
    }

    class ApproveLeaves extends AsyncTask<Void, String, String> {

        int index;
        String actionName;
        String response;
        boolean isChecked;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progresBar.setVisibility(View.VISIBLE);
            if (pd != null) {
                pd.show();
            }
        }

        public ApproveLeaves(int pos, String action) {
            this.index = pos;
            this.actionName = action;

        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("MemberID", "" + data.get(index).getUserId()));
                params1.add(new BasicNameValuePair("TransactionID", "" + data.get(index).getTransactionID()));
                params1.add(new BasicNameValuePair("LeaveDates", ""));
                params1.add(new BasicNameValuePair("Approve", "" + actionName));

                Log.e("params1", ">>" + params1);
                response = Util.makeServiceCall(Constant.URL + "approveLeaves", 2, params1, mContext);
                Log.e(TAG, "** response is:- " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                //progresBar.setVisibility(View.GONE);
                Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                if (status.equals("Success")) {
                    int approvalStatus = 1;
                    if (actionName.equals("true")) approvalStatus = 2;
                    else if (actionName.equals("false")) approvalStatus = 3;
                    updateApprovalsDB(index, approvalStatus);
                }

            } catch (Exception e) {
                e.printStackTrace();
                //progresBar.setVisibility(View.GONE);
            }
        }
    }

    private void updateApprovalsDB(int index, int approvalStatus) {
        ContentValues values = new ContentValues();
        values.put("Status", "" + approvalStatus);
        db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + data.get(index).getTransactionID() + "\"", null);

        update(index, approvalStatus);
    }

    //Work Item & Projects:
    public void startWorkApprovalDetails(Approval approval) {
        Intent myIntent = new Intent(mContext, WorkApprovalDetail_Activity.class);
        myIntent.putExtra("Object", approval);
        mContext.startActivity(myIntent);
        mContext.overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    private void StartRejectOrApproval(Approval approval, int index1, boolean flag) {
        if (Integer.parseInt(approval.getType()) == Constant.APPROVE_WORKITEMUPDATE) {
            Cursor crsr = db.rawQuery("select * from " + Constant.WorkTransaction + " where " + WorkTransaction.TRANSACTION_CODE + " = " + approval.getId(), null);
            if (crsr != null) {
                crsr.moveToFirst();
                String type = crsr.getString(crsr.getColumnIndex(WorkTransaction.UPDATE_TYPE));
                String amt = crsr.getString(crsr.getColumnIndex(WorkTransaction.AMOUNT));
                new Upload(index1, approval, type, amt, Boolean.toString(flag)).execute();
            }
            if (crsr != null) crsr.close();
        } else {
            new Upload(index1, approval, null, "", Boolean.toString(flag)).execute();
        }
    }

    private void setDataStatusProject(String answer, String id) {
        Cursor crsr = db.rawQuery("select * from " + Constant.ProjectTable + " where " + Project.PROJECT_ID + " = " + id, null);
        ContentValues objValues = new ContentValues();
        Log.e("Project", " Project Approval done updating");
        if (answer.equals("true"))
            objValues.put(Project.STATUS, "Approved");
        else
            objValues.put(Project.STATUS, "Rejected");
        if (crsr != null && crsr.getCount() > 0) {
            crsr.moveToFirst();
            db.update(Constant.ProjectTable, objValues, Project.PROJECT_ID + " = ?", new String[]{id + ""});
        }
        if (crsr != null) crsr.close();
    }

    private void setDataStatusCreate(String answer, String id) {
        Cursor crsr = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + id, null);
        ContentValues objValues = new ContentValues();
        if (answer.equals("true"))
            objValues.put("Work_Status", "Approved");
        else
            objValues.put("Work_Status", "Rejected");
        if (crsr != null && crsr.getCount() > 0) {
            crsr.moveToFirst();
            db.update(Constant.WorkItemTable, objValues, WorkItem.TASK_ID + " = ?", new String[]{id + ""});
        }
        if (crsr != null) crsr.close();
    }

    private void setDataStatusUpdate(String answer, String id) {
        Log.e("Update Status", "Transaction updated: " + id);
        Cursor crsr = db.rawQuery("select * from " + Constant.WorkTransaction + " where " + WorkTransaction.TRANSACTION_CODE + " = " + id, null);
        ContentValues objValues = new ContentValues();
        if (answer.equals("true"))
            objValues.put("status", "Approved");
        else
            objValues.put("status", "Rejected");

        if (crsr != null && crsr.getCount() > 0) {
            crsr.moveToFirst();
            db.update(Constant.WorkTransaction, objValues, WorkTransaction.TRANSACTION_CODE + " = ?", new String[]{id + ""});
        }
        if (crsr != null) crsr.close();
    }

    class Upload extends AsyncTask<Void, String, String> {
        Approval approval;
        String type, amt, answer;
        int indexNo;

        public Upload(int index1, Approval ap, String t, String a, String ans) {
            approval = ap;
            type = t;
            answer = ans;
            indexNo = index1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("Pre Execute", " Started Pre Execute");
            if (pd != null) {
                pd.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String resp = "";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = null;
                HttpPost poster = null;

                HashMap<String, String> localmap = new HashMap<String, String>();
                localmap.put("transactionid", "" + approval.getTransactionID());
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                if (Integer.parseInt(approval.getType()) == Constant.APPROVE_WORKITEMUPDATE) {
                    Log.e("Call approval", " Gen localmap");
                    localmap.put("workitemupdateid", "" + approval.getId());
                    localmap.put("userid", "" + approval.getUserId());
                    localmap.put("type", "" + approval.getTitle());
                    localmap.put("note", "" + approval.getDescription());

                    if (amt != null && amt.length() > 0)
                        localmap.put("expenseamt", amt);
                    else
                        localmap.put("expenseamt", "0");
                    localmap.put("type", type);
                    poster = new HttpPost(
                            Constant.URL + "approveWorkItemUpdate");
                } else if (Integer.parseInt(approval.getType()) == Constant.APPROVAL_NEWPRJECT || Integer.parseInt(approval.getType()) == Constant.EDIT_PROJECT_APPROVAL) {
                    Log.e("Project", " Project Approval");
                    localmap.put("projectid", "" + approval.getId());
                    localmap.put("userid", "" + approval.getUserId());
                    localmap.put("note", "" + approval.getDescription());
                    poster = new HttpPost(
                            Constant.URL + "approveProject");
                } else if (Integer.parseInt(approval.getType()) == Constant.CREATE_NEWWORKITEM || Integer.parseInt(approval.getType()) == Constant.EDIT_WORKITEM_APPROVAL) {
                    poster = new HttpPost(Constant.URL + "approveWorkItem");
                    localmap.put("workitemid", "" + approval.getId());
                    localmap.put("userid", "" + approval.getUserId());
                    localmap.put("note", "" + approval.getDescription());
                }
                if (poster != null) {
                    localmap.put("approved", answer);
                    Log.e("Calling Service:", localmap.toString());
                    entity.addPart("JSON", new StringBody(Util.prepareJsonString(localmap)));
                    entity.addPart("TokenID", new StringBody(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_TOKEN)));
                    entity.addPart("UserID", new StringBody(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID)));
                    poster.setEntity(entity);
                    response = client.execute(poster);
                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(response.getEntity()
                                    .getContent()));
                    String line = null;
                    while ((line = rd.readLine()) != null) {
                        resp += line;
                    }
                } else {
                    Toast.makeText(mContext, "No Approval Type Matched", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Resp Upload", "" + resp);
            return resp;
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            Log.e("Response", "Received :-" + o);
            JSONObject json = null;  //your response
            try {
                json = new JSONObject(o);
                Toast.makeText(mContext, json.optString("message"), Toast.LENGTH_SHORT).show();
                if (json.getString("status").equals("Success")) {
                    Log.e("getTransactionID", ">>" + approval.getTransactionID() + "::" + indexNo);

                    Log.e("resonse handle", " id;" + approval.getId() + " type:" + approval.getType());
                    if (Integer.parseInt(approval.getType()) == Constant.APPROVE_WORKITEMUPDATE)
                        setDataStatusUpdate(answer, approval.getId());
                    else if (Integer.parseInt(approval.getType()) == Constant.APPROVAL_NEWPRJECT)
                        setDataStatusProject(answer, approval.getId());
                    else if (Integer.parseInt(approval.getType()) == Constant.CREATE_NEWWORKITEM)
                        setDataStatusCreate(answer, approval.getId());
                    else if (Integer.parseInt(approval.getType()) == Constant.EDIT_PROJECT_APPROVAL)
                        setDataStatusUpdate(answer, approval.getId());
                    else if (Integer.parseInt(approval.getType()) == Constant.EDIT_WORKITEM_APPROVAL)
                        setDataStatusUpdate(answer, approval.getId());
                    else
                        setDataStatusCreate(answer, approval.getId());

                    int approvalStatus = 1;
                    if (answer.equals("true")) {
                        approvalStatus = 2;
                    } else {
                        approvalStatus = 3;
                    }

                    updateApprovalsDB(indexNo, approvalStatus);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //progress.hide();
            //progresBar.setVisibility(View.GONE);
            if (pd != null) {
                if (pd.isShowing()) pd.dismiss();
            }
        }
    }

    // Common Services:
    class ChangeMobileApproval extends AsyncTask<Void, String, String> {

        int index;
        String Approved;
        String response;
        Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }
        }

        public ChangeMobileApproval(int pos, String Approved, Context context) {
            this.index = pos;
            this.Approved = Approved;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject(data.get(index).getData());

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID)));
                params1.add(new BasicNameValuePair("MobileNo", "" + jData.optString("NewMobileNo")));
                params1.add(new BasicNameValuePair("MemberID", "" + data.get(index).getUserId()));
                params1.add(new BasicNameValuePair("TransactionID", "" + data.get(index).getTransactionID()));
                params1.add(new BasicNameValuePair("Approved", Approved));
                response = Util.makeServiceCall(Constant.URL1 + "approveChangeMobile", 2, params1, context);
                Log.d("params1", ">>" + params1);

                Log.v(TAG, "** response is:- " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                if (pd.isShowing()) pd.dismiss();
            }

            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_SHORT).show();
                if (status.equals("Success")) {

                    int approvalStatus = 1;
                    if (Approved.equals("true")) {
                        approvalStatus = 2;
                    } else {
                        approvalStatus = 3;
                    }

                    updateApprovalsDB(index, approvalStatus);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class UserResignApproval extends AsyncTask<Void, String, String> {

        int index;
        String Approved;
        String response;
        Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }
        }

        public UserResignApproval(int pos, String Approved, Context context) {
            this.index = pos;
            this.Approved = Approved;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject(data.get(index).getData());

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("OwnerID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID)));
                params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID)));
                params1.add(new BasicNameValuePair("MemberID", "" + data.get(index).getUserId()));
                params1.add(new BasicNameValuePair("TransactionID", "" + data.get(index).getTransactionID()));
                params1.add(new BasicNameValuePair("Accept", Approved));
                params1.add(new BasicNameValuePair("Application", Constant.AppName));
                response = Util.makeServiceCall(Constant.URL1 + "acceptRejectResignation", 2, params1, context);
                Log.d("params1", ">>" + params1);

                Log.v(TAG, "** response is:- " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                if (pd.isShowing()) pd.dismiss();
            }

            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_SHORT).show();
                if (status.equals("Success")) {
                    int approvalStatus = 1;
                    if (Approved.equals("true")) {
                        approvalStatus = 2;
                    } else {
                        approvalStatus = 3;
                    }
                    updateApprovalsDB(index, approvalStatus);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class UserExpensesApproval extends AsyncTask<Void, String, String> {

        int index;
        String Approved;
        String response;
        Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }
        }

        public UserExpensesApproval(int pos, String Approved, Context context) {
            this.index = pos;
            this.Approved = Approved;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("Application", Constant.AppName));
                params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID)));
                params1.add(new BasicNameValuePair("TokenID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_TOKEN)));
                params1.add(new BasicNameValuePair("TransactionID", "" + data.get(index).getTransactionID()));
                params1.add(new BasicNameValuePair("isApproved", Approved));

                response = Util.makeServiceCall(Constant.URL1 + "approveRejectExpense", 2, params1, context);
                Log.d("params1", ">>" + params1);

                Log.v(TAG, "** response is:- " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd != null) {
                if (pd.isShowing()) pd.dismiss();
            }

            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_SHORT).show();
                if (status.equals("Success")) {
                    int approvalStatus = 1;
                    if (Approved.equals("true")) {
                        approvalStatus = 2;
                    } else {
                        approvalStatus = 3;
                    }
                    updateApprovalsDB(index, approvalStatus);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class acceptUpdateCustomer extends AsyncTask<Void, String, String> {

        int index;
        String Approved;
        String response;
        Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }
        }

        public acceptUpdateCustomer(int pos, String Approved, Context context) {
            this.index = pos;
            this.Approved = Approved;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                JSONObject jData = new JSONObject(data.get(index).getData());

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("MemberID", "" + data.get(index).getUserId()));
                params1.add(new BasicNameValuePair("TransactionID", "" + data.get(index).getTransactionID()));
                params1.add(new BasicNameValuePair("Approved", Approved));
                params1.add(new BasicNameValuePair("Application", Constant.AppName));
                response = Util.makeServiceCall(Constant.URL1 + "acceptUpdateCustomer", 2, params1, context);
                Log.d("params1", ">>" + params1);

                Log.v(TAG, "** response is:- " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                if (pd.isShowing()) pd.dismiss();
            }

            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_SHORT).show();
                if (status.equals("Success")) {
                    int approvalStatus = 1;
                    if (Approved.equals("true")) {
                        approvalStatus = 2;
                    } else {
                        approvalStatus = 3;
                    }
                    updateApprovalsDB(index, approvalStatus);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class approvechangeRegMobile extends AsyncTask<Void, String, String> {

        int index;
        String Approved;
        String response;
        Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }
        }

        public approvechangeRegMobile(int pos, String Approved, Context context) {
            this.index = pos;
            this.Approved = Approved;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject(data.get(index).getData());

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID)));
                params1.add(new BasicNameValuePair("TransactionID", "" + data.get(index).getTransactionID()));
                params1.add(new BasicNameValuePair("Approved", Approved));
                response = Util.makeServiceCall(Constant.URL1 + "approvechangeRegDevice", 2, params1, context);
                Log.d("params1", ">>" + params1);

                Log.v(TAG, "** response is:- " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                if (pd.isShowing()) pd.dismiss();
            }

            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_SHORT).show();
                if (status.equals("Success")) {

                    int approvalStatus = 1;
                    if (Approved.equals("true")) {
                        approvalStatus = 2;
                    } else {
                        approvalStatus = 3;
                    }

                    updateApprovalsDB(index, approvalStatus);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
