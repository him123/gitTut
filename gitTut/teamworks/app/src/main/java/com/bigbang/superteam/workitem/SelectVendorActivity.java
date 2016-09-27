package com.bigbang.superteam.workitem;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SelectVendorActivity extends BaseActivity {

    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.lvVendors)
    ListView lvVendors;

    ArrayList<Customer> listVendors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_vendor);

        init();

        tvTitle.setText(getResources().getString(R.string.select_vendors));

        try {
            JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
            listVendors.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                Customer customer = new Customer();
                customer.setName("" + jsonObject1.optString("Name"));
                customer.setMobileNo("" + jsonObject1.optString("MobileNo"));
                customer.setID(jsonObject1.optInt("ID"));
                customer.setSelected(jsonObject1.optBoolean("selected"));
                listVendors.add(customer);

            }
            lvVendors.setAdapter(new CustomAdapter(listVendors));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }


    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {

        hideKeyboard();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

    @OnClick(R.id.rlSave)
    @SuppressWarnings("unused")
    public void Save(View view) {

        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < listVendors.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Name", "" + listVendors.get(i).getName());
                jsonObject.put("MobileNo", "" + listVendors.get(i).getMobileNo());
                jsonObject.put("ID", "" + listVendors.get(i).getID());
                jsonObject.put("selected", "" + listVendors.get(i).isSelected());
                jsonArray.put(jsonObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
        write(Constant.SHRED_PR.KEY_VENDOR_LIST, "1");
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);

    }

    private void init() {
        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);
    }

    class ViewHolder {
        TextView name;
        RelativeLayout relativeCheck;
        ImageButton btnCheck;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();

            Util.setAppFont(mContainer, mFont);
        }
    }

    class CustomAdapter extends BaseAdapter {

        ArrayList<Customer> locallist;

        public CustomAdapter(ArrayList<Customer> locallist) {
            // TODO Auto-generated constructor stub
            this.locallist = locallist;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return locallist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder holder;
            View view = convertView;

            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.listrow_assignee_cc,
                        parent, false);
                holder = new ViewHolder(view);
                assert view != null;
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.relativeCheck = (RelativeLayout) view
                        .findViewById(R.id.relativeCheck);
                holder.btnCheck = (ImageButton) view.findViewById(R.id.check);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final Customer customer = locallist.get(position);
            holder.name.setText("" + customer.getName());

            if (customer.isSelected()) {
                holder.btnCheck.setBackgroundResource(R.drawable.chacked);
            } else {
                holder.btnCheck.setBackgroundResource(R.drawable.unchacked);
            }

            holder.relativeCheck.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (customer.isSelected()) {
                        holder.btnCheck
                                .setBackgroundResource(R.drawable.unchacked);
                        customer.setSelected(false);
                        locallist.get(position).setSelected(false);
                    } else {
                        if (position == getCount() - 1) {
                            for (int i = 0; i < locallist.size(); i++) {
                                updateView(i);
                            }
                        } else {
                            updateView(locallist.size()-1);
                        }
                        holder.btnCheck
                                .setBackgroundResource(R.drawable.chacked);
                        locallist.get(position).setSelected(true);
                    }

                }
            });


            return view;
        }

        private void updateView(int index) {
            View view = lvVendors.getChildAt(index -
                    lvVendors.getFirstVisiblePosition());

            if (view == null)
                return;

            ImageButton btnCheck = (ImageButton) view.findViewById(R.id.check);
            btnCheck.setBackgroundResource(R.drawable.unchacked);
            listVendors.get(index).setSelected(false);
        }

    }


}
