package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.customer_vendor.CustomerDetailsActivity;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 3 on 4/15/2015.
 */
public class CustomerListAdapter extends BaseAdapter {
    private static final String TAG = UserListAdapter.class.getSimpleName();
    private static LayoutInflater inflater = null;
    private ArrayList<Customer> customers;
    private ArrayList<Customer> customers_temp;
    TransparentProgressDialog progressDialog;
    private DisplayImageOptions options;
    private Activity mContext;
    private ImageLoader imageLoader;
    String CustomerVendorType = "U";

    public CustomerListAdapter(TransparentProgressDialog progressDialog, Activity ctx, String CustomerVendorType, ArrayList<Customer> customers) {
        mContext = ctx;
        this.progressDialog = progressDialog;
        this.CustomerVendorType = CustomerVendorType;
        this.customers = customers;
        customers_temp = new ArrayList<>();
        customers_temp.addAll(customers);

        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));
        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public void delete(int pos) {
        customers.remove(pos);
        notifyDataSetChanged();
    }

    public int getCount() {
        return customers.size();
    }

    @Override
    public Object getItem(int i) {
        return customers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listraw_customer, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Customer customer = customers.get(position);
        holder.tvUserName.setText(customer.getName());
        holder.tvName.setText(customer.getName().toUpperCase());
        imageLoader.displayImage(customer.getLogo(), holder.ivUserImage, options);
        holder.tvUserType.setText("");

        String[] customerType = mContext.getResources().getStringArray(R.array.company_type_array);
        if (customerType.length > 0) {
            for (int i = 0; i < customerType.length; i++) {
                if (customer.getCompanyTypeID() == (i + 1)) {
                    holder.tvUserType.setText("Type: " + customerType[i]);
                }
            }
        }

        String logo = customer.getLogo();
        if (logo.length() > 0) {
            holder.tvName.setVisibility(View.GONE);
        } else {
            holder.tvName.setVisibility(View.VISIBLE);
        }

        holder.rlCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCustomerVendor(customer.getMobileNo());
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CustomerDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("customer", customer);
                intent.putExtras(bundle);
                intent.putExtra("Type", CustomerVendorType);
                intent.putExtra("Create", "0");
                mContext.startActivity(intent);
                mContext.overridePendingTransition(R.anim.enter_from_left,
                        R.anim.hold_bottom);
            }
        });

        return view;
    }

    public class ViewHolder {
        @InjectView(R.id.title)
        TextView tvUserName;
        @InjectView(R.id.desc)
        TextView tvUserType;
        @InjectView(R.id.img)
        ImageView ivUserImage;
        @InjectView(R.id.tvName)
        TextView tvName;
        @InjectView(R.id.rlCall)
        RelativeLayout rlCall;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

    private void callCustomerVendor(String mob) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mob));
        mContext.startActivity(intent);
    }

    protected void toast(CharSequence text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(mContext, key);
    }

    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        customers.clear();
        if (charText.length() == 0) {
            customers.addAll(customers_temp);
        } else {
            for (Customer wp : customers_temp) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    customers.add(wp);
                }
                if (wp.getMobileNo().contains(charText)) {
                    customers.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
