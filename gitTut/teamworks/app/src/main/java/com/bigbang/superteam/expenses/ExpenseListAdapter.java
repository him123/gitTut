package com.bigbang.superteam.expenses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by User on 7/30/2016.
 */
public class ExpenseListAdapter extends BaseAdapter {

    ArrayList<ExpenseType> data;
    private Activity mContext;
    private static LayoutInflater inflater = null;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;

    TransparentProgressDialog progressDialog;

    private boolean isMember;

    SQLiteDatabase db;

    public ExpenseListAdapter(Activity ctx, ArrayList<ExpenseType> d, boolean isMember) {

        mContext = ctx;
        data = d;
        this.isMember = isMember;

        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_profilepic).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.empty_profilepic).showImageOnFail(R.drawable.empty_profilepic).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        imageLoader.init(ImageLoaderConfiguration
                .createDefault(ctx));

        progressDialog = new TransparentProgressDialog(mContext, R.drawable.progressdialog, false);

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

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
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.list_expense_raw, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        if (isMember) {
            holder.btnWithdraw.setVisibility(View.INVISIBLE);
        }
        else {
            holder.btnWithdraw.setVisibility(View.VISIBLE);
        }


        final ExpenseType eType = data.get(position);
        if (eType != null) {
            holder.tvUserName.setText(eType.user.getFirstName() + " " + eType.user.getLastName());
            holder.txtStartDate.setText(dateFormat(eType.startDate));
            holder.txtEndDate.setText(dateFormat(eType.endDate));

            if (eType.status.equalsIgnoreCase("Pending")) {
                holder.imgStatus.setBackgroundResource(R.drawable.pending_color);
                holder.txtApprovedAmt.setText(String.format("%.2f", eType.totalExpenseRequested));
                if(isMember){
                    holder.imgStatus.setVisibility(View.INVISIBLE);

                    holder.rlApprove.setVisibility(View.VISIBLE);
                    holder.rlReject.setVisibility(View.VISIBLE);
                }else {
                    holder.rlApprove.setVisibility(View.INVISIBLE);
                    holder.rlReject.setVisibility(View.INVISIBLE);
                }
            } else if (eType.status.equalsIgnoreCase("Accept")) {
                holder.imgStatus.setVisibility(View.VISIBLE);
                holder.btnWithdraw.setVisibility(View.INVISIBLE);
                holder.imgStatus.setBackgroundResource(R.drawable.approved_color);
                holder.txtApprovedAmt.setText(String.format("%.2f", eType.totalExpenseApproved));

                holder.rlApprove.setVisibility(View.INVISIBLE);
                holder.rlReject.setVisibility(View.INVISIBLE);
            } else if (eType.status.equalsIgnoreCase("Reject")) {
                holder.imgStatus.setVisibility(View.VISIBLE);
                holder.btnWithdraw.setVisibility(View.INVISIBLE);
                holder.imgStatus.setBackgroundResource(R.drawable.rejected_color);
                holder.txtApprovedAmt.setText(String.format("%.2f", eType.totalExpenseRequested));

                holder.rlApprove.setVisibility(View.INVISIBLE);
                holder.rlReject.setVisibility(View.INVISIBLE);
            } else if (eType.status.equalsIgnoreCase("Withdraw")) {
                holder.imgStatus.setVisibility(View.VISIBLE);
                holder.btnWithdraw.setVisibility(View.INVISIBLE);
                holder.imgStatus.setBackgroundResource(R.drawable.offline_color);
                holder.txtApprovedAmt.setText(String.format("%.2f", eType.totalExpenseRequested));

                holder.rlApprove.setVisibility(View.INVISIBLE);
                holder.rlReject.setVisibility(View.INVISIBLE);
            }

            holder.tvContactImg.setText("" + eType.user.getFirstName().toUpperCase());
            imageLoader.displayImage(eType.user.getPicture(), holder.imgUserImage, options);

            if ((eType.user.getPicture().length())>5){
                holder.tvContactImg.setVisibility(View.GONE);
            }else{
                holder.tvContactImg.setVisibility(View.VISIBLE);
            }
        }

        holder.btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert1 = new AlertDialog.Builder(mContext);
                alert1.setTitle("" + Constant.AppNameSuper);
                alert1.setMessage(mContext.getResources().getString(R.string.confirm_withdraw));
                alert1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @SuppressLint("InlinedApi")
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                withdrawExpenses(eType.expenseId, position);
                            }
                        });
                alert1.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                alert1.create();
                alert1.show();

            }
        });

        holder.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateExpenseActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("expense",eType);
                b.putBoolean("isMember", isMember);
                intent.putExtras(b);
                mContext.startActivity(intent);
            }
        });

        holder.rlReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isOnline(mContext)) {
                    new UserExpensesApproval(position, "false", mContext).execute();
                }else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.rlApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isOnline(mContext)) {
                    new UserExpensesApproval(position, "true", mContext).execute();
                }else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    public class ViewHolder {

        @InjectView(R.id.tvUserName)
        TextView tvUserName;
        @InjectView(R.id.txtStartDate)
        TextView txtStartDate;
        @InjectView(R.id.txtEndDate)
        TextView txtEndDate;
        /*@InjectView(R.id.txtRequestedAmt)
        TextView txtRequestedAmt;*/
        @InjectView(R.id.txtApprovedAmt)
        TextView txtApprovedAmt;
        @InjectView(R.id.imgUserImage)
        ImageView imgUserImage;
        @InjectView(R.id.imgStatus)
        ImageView imgStatus;
        @InjectView(R.id.btnWithdraw)
        Button btnWithdraw;
        @InjectView(R.id.rlMain)
        RelativeLayout rlMain;
        @InjectView(R.id.tvContactImg)
        TextView tvContactImg;
        @InjectView(R.id.rlApprove)
        RelativeLayout rlApprove;
        @InjectView(R.id.rlReject)
        RelativeLayout rlReject;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

    public static String dateFormat(String dtStart) {

        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = format.parse(dtStart);
            SimpleDateFormat sdf = new SimpleDateFormat(" d" + " MMM");
            newDate = sdf.format(convertedDate);
            return newDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    protected void updateStatus(int pos, String status, double amt) {
        data.get(pos).status = status;
        data.get(pos).totalExpenseApproved = amt;
        notifyDataSetChanged();
    }

    private void withdrawExpenses(int expenseId, final int position) {

        if (Util.isOnline(mContext)) {

            if (progressDialog != null)
                progressDialog.show();

            RestClient.getCommonService().withdrawExpense(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID),
                    expenseId + "",
                    Constant.AppName,
                    Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_TOKEN),
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {
                                Map<String, String> map = Util.readStatus(response);
                                boolean isSuccess = map.get("status").equals("Success");
                                String json = Util.getString(response.getBody().in());
                                JSONObject jObj = new JSONObject(json);

                                String status = jObj.optString("status");
                                if (status.equals(Constant.InvalidToken)) {
                                    TeamWorkApplication.LogOutClear(mContext);
                                    return;
                                }

                                if (isSuccess) {
                                    Toast.makeText(mContext, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                    updateStatus(position,"Withdraw",0);
                                } else {
                                    Toast.makeText(mContext, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                                if (progressDialog != null)
                                    if (progressDialog.isShowing()) progressDialog.dismiss();

                            } catch (Exception e) {
                                e.printStackTrace();
                                if (progressDialog != null)
                                    if (progressDialog.isShowing()) progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (progressDialog != null)
                                if (progressDialog.isShowing()) progressDialog.dismiss();
                        }
                    });
        } else {
            Toast.makeText(mContext, "" + Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    class UserExpensesApproval extends AsyncTask<Void, String, String> {

        int index;
        String Approved;
        String response;
        Context context;
        double amt;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) {
                progressDialog.show();
            }
        }

        public UserExpensesApproval(int pos, String Approved, Context context) {
            this.index = pos;
            this.Approved = Approved;
            this.context = context;
            this.amt = 0;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("Application", Constant.AppName));
                params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID)));
                params1.add(new BasicNameValuePair("TokenID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_TOKEN)));
                params1.add(new BasicNameValuePair("TransactionID", "" + data.get(index).transactionID));
                params1.add(new BasicNameValuePair("isApproved", Approved));

                response = Util.makeServiceCall(Constant.URL1 + "approveRejectExpense", 2, params1, context);
                Log.d("params1", ">>" + params1);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog != null) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }

            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_SHORT).show();
                if (status.equals("Success")) {
                    String setStatus = "Pending";
                    int approvalStatus = 1;
                    if (Approved.equals("true")) {
                        approvalStatus = 2;
                        setStatus = "Accept";
                        amt = data.get(index).totalExpenseRequested;
                    } else {
                        approvalStatus = 3;
                        setStatus = "Reject";
                    }
                    updateApprovalsDB(index, approvalStatus, setStatus, amt);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateApprovalsDB(int index, int approvalStatus, String status, double amt) {
        ContentValues values = new ContentValues();
        values.put("Status", "" + approvalStatus);
        db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + data.get(index).transactionID + "\"", null);

        updateStatus(index, status, amt);
    }

}
