package com.bigbang.superteam.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.customer_vendor.AddressActivity;
import com.bigbang.superteam.model.Address;
import com.bigbang.superteam.util.Constant;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 8/12/2015.
 */
public class AddressAdapter extends BaseAdapter {

    private static final String TAG = UserListAdapter.class.getSimpleName();
    private static LayoutInflater inflater = null;
    private List<Address> addresses;
    private Activity mContext;
    String CreateType;
    ListView lvAddress;

    public AddressAdapter(Activity ctx, List<Address> addresses, String CreateType) {
        mContext = ctx;
        this.addresses = addresses;
        this.CreateType = CreateType;
        this.lvAddress = lvAddress;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void delete(int position) {
        addresses.remove(position);
        notifyDataSetChanged();
        Util.setListViewHeightBasedOnChildren(lvAddress);
    }


    @Override
    public int getCount() {
        return addresses.size();
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
    public View getView(final int position, View view, ViewGroup parent) {

        lvAddress = (ListView) parent;
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.address_row2, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Address address = addresses.get(position);

        if (Integer.parseInt(address.getType()) == 1) {
            holder.rlDeleteAddress.setVisibility(View.INVISIBLE);
        } else {
            holder.rlDeleteAddress.setVisibility(View.VISIBLE);
        }

        String strAddress = "" + address.getAddressLine1();
        if (address.getAddressLine2().length() > 0) {
            strAddress = strAddress + ", " + address.getAddressLine2();
        }
        strAddress = strAddress + ", " + address.getCity() + " " + address.getPincode() + ", " + address.getState() + ", " + address.getCountry();

        holder.tvTitle.setText(strAddress);

        String[] addressType = mContext.getResources().getStringArray(R.array.address_type_array);
        if (addressType.length > 0) {
            holder.tvDesc.setText("Permanent Address");
            for (int i = 0; i < addressType.length; i++) {
                if (Integer.parseInt(address.getType()) == (i + 2)) {
                    holder.tvDesc.setText("" + addressType[i] + " Address");
                    holder.txt_invi.setText("" + addressType[i] + " Address");
                }
            }
        }

        holder.rlDeleteAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert1 = new AlertDialog.Builder(mContext);
                alert1.setTitle("" + Constant.AppNameSuper);
                alert1.setMessage("Are you sure you want to delete this Address?");
                alert1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @SuppressLint("InlinedApi")
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                delete(position);

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
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, AddressActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("address", address);
                intent.putExtras(bundle);
                intent.putExtra("Create", CreateType);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
                mContext.overridePendingTransition(R.anim.enter_from_left,
                        R.anim.hold_bottom);
            }
        });

        return view;

    }

    public class ViewHolder {
        @InjectView(R.id.title)
        TextView tvTitle;
        @InjectView(R.id.desc)
        TextView tvDesc;
        @InjectView(R.id.txt_invi)
        TextView txt_invi;

        @InjectView(R.id.rlDeleteAddress)
        RelativeLayout rlDeleteAddress;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }
}
