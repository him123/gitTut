package com.bigbang.superteam.task;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.model.Customer;

import java.util.ArrayList;

import butterknife.InjectView;

public class CustomerVendorActivity extends Activity {

    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.lvVendors)
    ListView lvVendors;

    ArrayList<Customer> listVendors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_vendor);
    }

}
