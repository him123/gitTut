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
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.admin.UpdateCompanyProfileActivity;
import com.bigbang.superteam.customer_vendor.CustomerVendorActivity;
import com.bigbang.superteam.login_register.CompanySetupActivity;
import com.bigbang.superteam.login_register.InvitationActivity;
import com.bigbang.superteam.payroll.PaySlipAdminActivity;
import com.bigbang.superteam.user.ChangeMobileActivity;
import com.bigbang.superteam.user.ChangePasswordActivity;
import com.bigbang.superteam.user.ResignUserActivity;
import com.bigbang.superteam.user.UpdateProfileActivity;
import com.bigbang.superteam.user.UsersActivity;
import com.bigbang.superteam.user.YourManagersActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 4/30/2015.
 */
public class AdminSettingsAdapter extends BaseAdapter {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    String[] list;
    Activity activty;
    String roleId;
    TransparentProgressDialog progressDialog;

    public AdminSettingsAdapter(Activity activty, TransparentProgressDialog progressDialog) {
        this.activty = activty;
        this.progressDialog = progressDialog;
        roleId = Util.ReadSharePrefrence(activty, Constant.SHRED_PR.KEY_ROLE_ID);
        if (roleId.equals("1"))
            list = activty.getResources().getStringArray(R.array.superadmin_settings_array);
        else
            list = activty.getResources().getStringArray(R.array.admin_settings_array);
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

        if (roleId.equals("1")) {
            if (position != 0 && position != 5 && position != 14) {
                view = activty.getLayoutInflater().inflate(R.layout.settings_list_item, parent,
                        false);
                holder = new ViewHolder(view);
                assert view != null;
                holder.title = (TextView) view.findViewById(R.id.title);
                view.setTag(holder);
            } else {
                view = activty.getLayoutInflater().inflate(R.layout.blank_row, parent,
                        false);
                holder = new ViewHolder(view);
                assert view != null;
                holder.title = (TextView) view.findViewById(R.id.title);
                view.setTag(holder);
            }

            holder.title.setTextColor(activty.getResources().getColor(R.color.black));
            holder.title.setText("" + list[position]);
            holder.img.setVisibility(View.GONE);
            if (list[position].toLowerCase().equals("user settings")) {
                holder.title.setTextColor(activty.getResources().getColor(R.color.green_header));
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.settings_user);
            } else if (list[position].toLowerCase().equals("company settings")) {
                holder.title.setTextColor(activty.getResources().getColor(R.color.green_header));
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.settings_company);
            } else if (list[position].toLowerCase().equals("logout")) {
                holder.title.setTextColor(activty.getResources().getColor(R.color.gray));
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (position) {
                        case 1:
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("userid", "" + Util.ReadSharePrefrence(activty, Constant.SHRED_PR.KEY_USERID));
                            startActivityWithData(UpdateProfileActivity.class, hashMap);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 2:
                            startIntent(ChangeMobileActivity.class);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 3:
                            startIntent(ChangePasswordActivity.class);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 4:
                            Intent intent = new Intent(activty, InvitationActivity.class);
                            intent.putExtra("from", "" + Constant.FROM_DASHBOARD);
                            activty.startActivity(intent);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 6:
                            startIntent(UpdateCompanyProfileActivity.class);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 7:
                            startCompanySetup(1);
                            break;
                        case 8:
                            startCompanySetup(2);
                            break;
                        case 9:
                            startCompanySetup(3);
                            break;
                        case 10:
                            startCompanySetup(4);
                            break;
                        case 11:
                            startCompanySetup(5);
                            break;
                        case 12:
                            Intent intent1 = new Intent(activty, CustomerVendorActivity.class);
                            activty.startActivity(intent1);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 13:
                            Intent intent2 = new Intent(activty, UsersActivity.class);
                            activty.startActivity(intent2);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 15:
                            AlertDialog.Builder alert1 = new AlertDialog.Builder(activty);
                            alert1.setTitle("" + Constant.AppNameSuper);
                            alert1.setMessage(activty.getResources().getString(R.string.confirm_logout));
                            alert1.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        @SuppressLint("InlinedApi")
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                            if (Util.isOnline(activty)) {
                                                TeamWorkApplication.logout(activty, progressDialog);
                                            } else {
                                                Toast.makeText(activty, activty.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
        } else {
            if (position != 0 && position != 7 && position != 16) {
                view = activty.getLayoutInflater().inflate(R.layout.settings_list_item, parent,
                        false);
                holder = new ViewHolder(view);
                assert view != null;
                holder.title = (TextView) view.findViewById(R.id.title);
                view.setTag(holder);
            } else {
                view = activty.getLayoutInflater().inflate(R.layout.blank_row, parent,
                        false);
                holder = new ViewHolder(view);
                assert view != null;
                holder.title = (TextView) view.findViewById(R.id.title);
                view.setTag(holder);
            }

            holder.title.setTextColor(activty.getResources().getColor(R.color.black));
            holder.title.setText("" + list[position]);
            holder.img.setVisibility(View.GONE);
            if (list[position].toLowerCase().equals("user settings")) {
                holder.title.setTextColor(activty.getResources().getColor(R.color.green_header));
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.settings_user);
            } else if (list[position].toLowerCase().equals("company settings")) {
                holder.title.setTextColor(activty.getResources().getColor(R.color.green_header));
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.settings_company);
            } else if (list[position].toLowerCase().equals("logout")) {
                holder.title.setTextColor(activty.getResources().getColor(R.color.gray));
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (position) {
                        case 1:
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("userid", "" + Util.ReadSharePrefrence(activty, Constant.SHRED_PR.KEY_USERID));
                            startActivityWithData(UpdateProfileActivity.class, hashMap);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 2:
                            startIntent(ChangeMobileActivity.class);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 3:
                            startIntent(ChangePasswordActivity.class);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 4:
                            startIntent(YourManagersActivity.class);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 5:
                            Intent intent = new Intent(activty, InvitationActivity.class);
                            intent.putExtra("from", "" + Constant.FROM_DASHBOARD);
                            activty.startActivity(intent);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 6:
                            startIntent(ResignUserActivity.class);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 8:
                            startIntent(UpdateCompanyProfileActivity.class);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 9:
                            startCompanySetup(1);
                            break;
                        case 10:
                            startCompanySetup(2);
                            break;
                        case 11:
                            startCompanySetup(3);
                            break;
                        case 12:
                            startCompanySetup(4);
                            break;
                        case 13:
                            startCompanySetup(5);
                            break;
                        case 14:
                            Intent intent1 = new Intent(activty, CustomerVendorActivity.class);
                            activty.startActivity(intent1);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 15:
                            Intent intent2 = new Intent(activty, UsersActivity.class);
                            activty.startActivity(intent2);
                            activty.overridePendingTransition(R.anim.enter_from_left,
                                    R.anim.hold_bottom);
                            break;
                        case 17:
                            AlertDialog.Builder alert1 = new AlertDialog.Builder(activty);
                            alert1.setTitle("" + Constant.AppNameSuper);
                            alert1.setMessage(activty.getResources().getString(R.string.confirm_logout));
                            alert1.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        @SuppressLint("InlinedApi")
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                            if (Util.isOnline(activty)) {
                                                TeamWorkApplication.logout(activty, progressDialog);
                                            } else {
                                                Toast.makeText(activty, activty.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
        }


        return view;
    }

    private void startIntent(Class clx) {
        Intent myIntent = new Intent(activty, clx);
        activty.startActivity(myIntent);
    }

    protected void startActivityWithData(Class klass, HashMap hashMap) {
        Intent intent = new Intent(activty, klass);
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            intent.putExtra((String) pair.getKey(), (String) pair.getValue());


            it.remove(); // avoids a ConcurrentModificationException
        }
        activty.startActivity(intent);
    }

    private void startCompanySetup(int position) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("position", "" + position);
        hashMap.put("from", "" + Constant.FROM_DASHBOARD);
        startActivityWithData(CompanySetupActivity.class, hashMap);
        activty.overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    public class ViewHolder {
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.img)
        ImageButton img;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(activty.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }
}
