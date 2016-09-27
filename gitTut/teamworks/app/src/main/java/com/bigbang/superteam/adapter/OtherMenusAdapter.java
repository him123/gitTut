package com.bigbang.superteam.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.admin.UpdateCompanyProfileActivity;
import com.bigbang.superteam.customer_vendor.CustomerVendorActivity;
import com.bigbang.superteam.login_register.CompanySetupActivity;
import com.bigbang.superteam.login_register.InvitationActivity;
import com.bigbang.superteam.tracking.UserDailyReportActivity;
import com.bigbang.superteam.user.ChangeMobileActivity;
import com.bigbang.superteam.user.ChangePasswordActivity;
import com.bigbang.superteam.user.DefaultWorkLocation;
import com.bigbang.superteam.user.ResignUserActivity;
import com.bigbang.superteam.user.UpdateProfileActivity;
import com.bigbang.superteam.user.YourManagersActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 4/30/2015.
 */
public class OtherMenusAdapter extends BaseAdapter {

    String[] list;
    Activity activity;
    TransparentProgressDialog progressDialog;

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    public OtherMenusAdapter(Activity activity, TransparentProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
        this.activity = activity;
        list = activity.getResources().getStringArray(R.array.other_menu_array);
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        View view = convertView;

        if (position != 0 && position != 9 && position != 14) {
            view = activity.getLayoutInflater().inflate(R.layout.settings_list_item, parent,
                    false);
            holder = new ViewHolder(view);
            assert view != null;
            holder.title = (TextView) view.findViewById(R.id.title);
            view.setTag(holder);

        } else {
            view = activity.getLayoutInflater().inflate(R.layout.blank_row, parent,
                    false);
            holder = new ViewHolder(view);
            assert view != null;
            holder.title = (TextView) view.findViewById(R.id.title);
            view.setTag(holder);

        }

        holder.title.setTextColor(activity.getResources().getColor(R.color.black));
        holder.title.setText("" + list[position]);
        holder.img.setVisibility(View.GONE);
        if (list[position].toLowerCase().equals("user settings")) {
            holder.title.setTextColor(activity.getResources().getColor(R.color.green_header));
            holder.img.setVisibility(View.VISIBLE);
            holder.img.setBackgroundResource(R.drawable.settings_user);
        } else if (list[position].toLowerCase().equals("company settings")) {
            holder.title.setTextColor(activity.getResources().getColor(R.color.green_header));
            holder.img.setVisibility(View.VISIBLE);
            holder.img.setBackgroundResource(R.drawable.settings_company);
        } else if (list[position].toLowerCase().equals("logout")) {
            holder.title.setTextColor(activity.getResources().getColor(R.color.gray));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (position) {

                    case 1:
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("userid", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID));
                        startActivityWithData(UpdateProfileActivity.class, hashMap);
                        activity.overridePendingTransition(R.anim.enter_from_left,
                                R.anim.hold_bottom);
                        break;
                    case 2:
                        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                        int curMonth = calendar.get(calendar.MONTH) + 1;
                        int curYear = calendar.get(calendar.YEAR);
                        int curDate = calendar.get(calendar.DAY_OF_MONTH);

                        Intent intent = new Intent(activity, UserDailyReportActivity.class);
                        intent.putExtra("UserName", Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_FIRSTNAME) + " " + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_LASTNAME));
                        intent.putExtra("UserID", Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID));
                        intent.putExtra("day", "" + curDate);
                        intent.putExtra("month", "" + curMonth);
                        intent.putExtra("year", "" + curYear);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter_from_left,
                                R.anim.hold_bottom);
                        break;
                    case 3:
                        startIntent(DefaultWorkLocation.class);
                        activity.overridePendingTransition(R.anim.enter_from_left,
                                R.anim.hold_bottom);
                        break;
                    case 4:
                        startIntent(ChangeMobileActivity.class);
                        activity.overridePendingTransition(R.anim.enter_from_left,
                                R.anim.hold_bottom);
                        break;
                    case 5:
                        startIntent(ChangePasswordActivity.class);
                        activity.overridePendingTransition(R.anim.enter_from_left,
                                R.anim.hold_bottom);
                        break;
                    case 6:
                        startIntent(YourManagersActivity.class);
                        activity.overridePendingTransition(R.anim.enter_from_left,
                                R.anim.hold_bottom);
                        break;
                    case 7:
                        Intent intent1 = new Intent(activity, InvitationActivity.class);
                        intent1.putExtra("from", "" + Constant.FROM_DASHBOARD);
                        activity.startActivity(intent1);
                        activity.overridePendingTransition(R.anim.enter_from_left,
                                R.anim.hold_bottom);
                        break;
                    case 8:
                        startIntent(ResignUserActivity.class);
                        activity.overridePendingTransition(R.anim.enter_from_left,
                                R.anim.hold_bottom);
                        break;

                    case 10:
                        startIntent(UpdateCompanyProfileActivity.class);
                        activity.overridePendingTransition(R.anim.enter_from_left,
                                R.anim.hold_bottom);
                        break;
                    case 11:
                        startCompanySetup(1);
                        break;
                    case 12:
                        startCompanySetup(2);
                        break;
                    case 13:
                        startCompanySetup(3);
                        break;
                    case 14:
                        Intent intent2 = new Intent(activity, CustomerVendorActivity.class);
                        activity.startActivity(intent2);
                        activity.overridePendingTransition(R.anim.enter_from_left,
                                R.anim.hold_bottom);
                        break;
                    case 16:
                        AlertDialog.Builder alert1 = new AlertDialog.Builder(activity);
                        alert1.setTitle("" + Constant.AppNameSuper);
                        alert1.setMessage(activity.getResources().getString(R.string.confirm_logout));
                        alert1.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @SuppressLint("InlinedApi")
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        if (Util.isOnline(activity)) {
                                            TeamWorkApplication.logout(activity, progressDialog);
                                        } else {
                                            Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        alert1.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        dialog.cancel();
                                    }
                                });
                        alert1.create();
                        alert1.show();
                        break;

                    default:
                        break;
                }
            }
        });

        return view;
    }

    public class ViewHolder {
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.img)
        ImageButton img;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

    private void startIntent(Class clx) {
        Intent myIntent = new Intent(activity, clx);
        activity.startActivity(myIntent);
    }

    protected void startActivityWithData(Class klass, HashMap hashMap) {
        Intent intent = new Intent(activity, klass);
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            intent.putExtra((String) pair.getKey(), (String) pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        activity.startActivity(intent);
    }

    private void startCompanySetup(int position) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("position", "" + position);
        hashMap.put("from", "" + Constant.FROM_DASHBOARD);
        startActivityWithData(CompanySetupActivity.class, hashMap);
        activity.overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

}
