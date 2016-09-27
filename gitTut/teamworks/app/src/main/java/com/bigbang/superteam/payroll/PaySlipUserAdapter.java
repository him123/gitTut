package com.bigbang.superteam.payroll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by User on 6/3/2016.
 */
public class PaySlipUserAdapter extends BaseAdapter {

    private static final String TAG = PaySlipUserAdapter.class.getSimpleName();
    private static LayoutInflater inflater = null;
    private List<User> users;
    private Activity mContext;
    private TransparentProgressDialog progressDialog;
    private int FROM = 1; //1 for Generate    2 for Publish

    public PaySlipUserAdapter(Activity ctx, List<User> users, int from) {
        mContext = ctx;
        this.users = users;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        progressDialog = new TransparentProgressDialog(mContext, R.drawable.progressdialog, false);
        this.FROM = from;
    }

    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public List<User> getUserList(){
        return users;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listraw_payslip_users, null);
            holder = new ViewHolder(view);

            view.setTag(holder);

           /* ..................... 3 ...............................
           User user1 = getItem(position);
            holder.checkBoxUser.setChecked(user1.isChecked());
            holder.txtUserName.setText(user1.getFirstName() + " " + user1.getLastName());
            holder.btnViewPayslip.setText("View");
            holder.imgColor.setBackgroundResource(R.drawable.approved_color);*/

           /* .............. 1 ...................
           holder.checkBoxUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    users.get(getPosition).setChecked(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            view.setTag(holder);
            view.setTag(R.id.txtUserName, holder.txtUserName);
            view.setTag(R.id.checkBoxUser, holder.checkBoxUser);
            view.setTag(R.id.btnViewPayslip, holder.btnViewPayslip);
            view.setTag(R.id.imgColor, holder.imgColor);*/

           /* ................. 2 ..................
           CheckBox chk = (CheckBox) view.findViewById(R.id.checkBoxUser);
            TextView txt = (TextView) view.findViewById(R.id.txtUserName);
            RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rl_main);
            Button btn = (Button) view.findViewById(R.id.btnViewPayslip);
            ImageView img = (ImageView) view.findViewById(R.id.imgColor);

            holder.checkBoxUser = chk;
            holder.txtUserName = txt;
            holder.rl_main = rl;
            holder.btnViewPayslip = btn;
            holder.imgColor = img;*/



        } else {
            holder = (ViewHolder) view.getTag();
            /* ............................. 3 ..............
            holder.txtUserName.setText("");
            holder.checkBoxUser.setChecked(false);*/
        }

        if(FROM == 1){
            holder.btnViewPayslip.setVisibility(View.INVISIBLE);
            holder.imgColor.setVisibility(View.INVISIBLE);
        }else{
            holder.btnViewPayslip.setVisibility(View.VISIBLE);
            holder.imgColor.setVisibility(View.INVISIBLE);
        }


        final User user = users.get(position);

        if(FROM == 2) {
            if (user.getPublished()) {
                holder.imgColor.setVisibility(View.VISIBLE);
                holder.imgColor.setBackgroundResource(R.drawable.approved_color);
            } else {
                holder.imgColor.setVisibility(View.INVISIBLE);
            }
        }

        holder.txtUserName.setText(user.getFirstName() + " " + user.getLastName());

        holder.checkBoxUser.setChecked(user.isChecked());

        holder.checkBoxUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v ;
                users.get(position).setChecked(cb.isChecked());
            }
        });

        holder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FROM == 1) {
                    Intent intent = new Intent(mContext, UpdateUserPayrollActivity.class);
                    Bundle b = new Bundle();
                    b.putString("userID", "" + user.getUserID());
                    b.putSerializable("user",user);
                    intent.putExtras(b);
                    mContext.startActivity(intent);
                    mContext.overridePendingTransition(R.anim.enter_from_left,
                            R.anim.hold_bottom);
                }
            }
        });

        holder.btnViewPayslip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int month = 0, year = 0;
                month = Util.ReadSharePrefrenceInteger(mContext, Constant.SHRED_PR.KEY_PAYSLIP_MONTH);
                year = Util.ReadSharePrefrenceInteger(mContext, Constant.SHRED_PR.KEY_PAYSLIP_YEAR);

                if(year > 0) {
                    //servie call for payslip data
                    getPayslipData(user.getUserID(), user.getRoleId(), month, year, user);
                }else{
                    Toast.makeText(mContext, "Please select month & year", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public class ViewHolder {
        @InjectView(R.id.rl_main)
        RelativeLayout rl_main;
        @InjectView(R.id.txtUserName)
        TextView txtUserName;
        @InjectView(R.id.checkBoxUser)
        CheckBox checkBoxUser;
        @InjectView(R.id.btnViewPayslip)
        Button btnViewPayslip;
        @InjectView(R.id.imgColor)
        ImageView imgColor;

       public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

    private void getPayslipData(final int userID, final int roleID, final int month, final int year, final User user){

        if (Util.isOnline(mContext)) {

            if (progressDialog != null)
                progressDialog.show();


            Log.d("", "*************** Company ID : " + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID)
                    + " User ID " + userID + " Month " + month + " Year " + year + " ***************");

            RestClient.getTeamWork().getUserPayslip(userID+"",
                    Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID),
                    month+"",
                    year+"",
                    "false",
                    new Callback<UserPayslipDetail>() {
                        @Override
                        public void success(UserPayslipDetail data, Response response) {
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
                                    JSONObject dataJobj = jObj.getJSONObject("data");

                                    if (dataJobj != null) {
                                        Intent intent = new Intent(mContext, UserPaySLipActivity.class);
                                        intent.putExtra("userID", "" + userID);
                                        intent.putExtra("month", month);
                                        intent.putExtra("year", "" + year);
                                        intent.putExtra("role_ID", ""+ roleID );
                                        intent.putExtra("userPayslipDetail", data);
                                        intent.putExtra("user",user);
                                        mContext.startActivity(intent);
                                        mContext.overridePendingTransition(R.anim.enter_from_left,
                                                R.anim.hold_bottom);
                                    }
                                } else {
                                    Toast.makeText(mContext, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                                progressDialog.dismiss();
                            } catch (Exception e) {
                                Log.d("", "Exception : " + e);
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            progressDialog.dismiss();
                        }
                    });
        } else {
            Toast.makeText(mContext, Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }
}

