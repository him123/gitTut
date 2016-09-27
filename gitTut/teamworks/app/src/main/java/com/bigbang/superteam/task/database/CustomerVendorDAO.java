package com.bigbang.superteam.task.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.util.SQLiteHelper;

import java.util.ArrayList;

/**
 * Created by User on 9/5/2016.
 */
public class CustomerVendorDAO extends TeamWorksDBDAO{
    public CustomerVendorDAO(Context contxt) {
        super(contxt);
    }

    public CustomerVendorDAO() {
        super();
    }

    public long save(Customer customer) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.CV_ID, customer.getID());
        values.put(SQLiteHelper.CV_NAME, customer.getName());
        values.put(SQLiteHelper.CV_MOBILE_NO, customer.getMobileNo());
        values.put(SQLiteHelper.CV_LANDLINE_NO, customer.getLandlineNo());
        values.put(SQLiteHelper.CV_EMAIL_ID, customer.getEmailID());
        values.put(SQLiteHelper.CV_OWNER_ID, customer.getOwnerID());
        values.put(SQLiteHelper.CV_COMPANY_TYPE_ID, customer.getCompanyTypeID());
        values.put(SQLiteHelper.CV_CREATED_BY, customer.getCreatedBy());
        values.put(SQLiteHelper.CV_COMPANY_ID, customer.getCompanyID());
        values.put(SQLiteHelper.CV_TYPE, customer.getType());
        values.put(SQLiteHelper.CV_DESCRIPTION, customer.getDescription());
        //values.put(SQLiteHelper.CV_ADDRESSLIST, customer.getAddressList());
        values.put(SQLiteHelper.CV_LOGO, customer.getLogo());

        status = database.insert(SQLiteHelper.TABLE_CUST_VENDOR, null, values);
        if (status != -1)
            Log.d("Insert Customer Vendor", "*********************************************successfull");
        else
            Log.d("Insert Customer Vendor", "*********************************************unsuccessfull");

        return status;
    }

    public ArrayList<Customer> getCustomerVendorList(String type) {
        ArrayList<Customer> customerList = new ArrayList<Customer>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_CUST_VENDOR,
                new String[]{
                        SQLiteHelper.CV_ID,//0
                        SQLiteHelper.CV_NAME,//1
                        SQLiteHelper.CV_MOBILE_NO, //2
                        SQLiteHelper.CV_LANDLINE_NO, //3
                        SQLiteHelper.CV_EMAIL_ID, //4
                        SQLiteHelper.CV_OWNER_ID, //5
                        SQLiteHelper.CV_COMPANY_TYPE_ID, //6
                        SQLiteHelper.CV_CREATED_BY, //7
                        SQLiteHelper.CV_COMPANY_ID, //8
                        SQLiteHelper.CV_TYPE, //9
                        SQLiteHelper.CV_DESCRIPTION, //10
                        SQLiteHelper.CV_ADDRESSLIST, //11
                        SQLiteHelper.CV_LOGO, //12
                }, SQLiteHelper.CV_TYPE + "=?", new String[]{type}, null, null, null, null);

        while (cursor.moveToNext()) {
            Customer customer = new Customer();

            customer.setID(cursor.getInt(0));
            customer.setName(cursor.getString(1));
            customer.setMobileNo(cursor.getString(2));
            customer.setLandlineNo(cursor.getString(3));
            customer.setEmailID(cursor.getString(4));
            customer.setOwnerID(cursor.getInt(5));
            customer.setCompanyTypeID(cursor.getInt(6));
            customer.setCreatedBy(cursor.getInt(7));
            customer.setCompanyID(cursor.getInt(8));
            customer.setType(cursor.getString(9));
            customer.setDescription(cursor.getString(10));
            customer.setAddressList(cursor.getString(11));
            customer.setLogo(cursor.getString(12));

            customerList.add(customer);
        }
        return customerList;
    }
}
