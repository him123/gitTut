package com.bigbang.superteam.workitem;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.ExpenseAdapter;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.dataObjs.WorkTransaction;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 03/07/2015.
 */
public class WorkItemExpense extends BaseActivity {
    WorkItem workItem;
    ArrayList<WorkTransaction> workTransactionArrayList;
    ExpenseAdapter adapter;
    String code = "";
    String TAG = "WorkItemExpense";

    @InjectView(R.id.tv_itemName)
    TextView tvItemName;
    @InjectView(R.id.tv_itemBudget)
    TextView tvItemBudget;
    @InjectView(R.id.tv_itemTotal)
    TextView tvItemTotal;
    @InjectView(R.id.lv_expense)
    ListView lvExpenseLsit;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workexpence);

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        code = getIntent().getStringExtra(WorkItem.TASK_ID);
        InitializeWorkItem();
        InitializeList();

        Init();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    private void Init() {
        if (workItem == null) Log.e(TAG, "Work item null");
        tvItemName.setText(workItem.getTitle());
        tvItemBudget.setText(workItem.getBudget());
        if (workItem.getBudget() != null && workItem.getBudget().trim().length() > 0) {

            int budget = 0;
            try {
                budget = Integer.parseInt("" + (int) Float.parseFloat(workItem.getBudget().trim()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            int total = 0;
            for (int i = 0; i < workTransactionArrayList.size(); i++) {
                String amt = workTransactionArrayList.get(i).getAmount();
                if (amt != null && amt.length() > 0)
                    total = (int) (total + Float.parseFloat(amt));
            }
            tvItemTotal.setText("" + total);
        }
        adapter = new ExpenseAdapter(this, workTransactionArrayList);
        lvExpenseLsit.setAdapter(adapter);
    }

    private void InitializeList() {
        workTransactionArrayList = new ArrayList<>();
        Cursor crsr = db.rawQuery("select * from " + Constant.WorkTransaction + " where " + WorkTransaction.TASK_CODE + " = " + code + " AND " + WorkTransaction.UPDATE_TYPE + " = " + "'Expense'" + " AND " + WorkTransaction.STATUS + " ='Approved'", null);
        if (crsr != null) {
            if (crsr.getCount() > 0) {
                crsr.moveToFirst();
                do {
                    WorkTransaction transaction = new WorkTransaction();
                    transaction.setTr_code(crsr.getInt(crsr.getColumnIndex(WorkTransaction.TRANSACTION_CODE)));
                    transaction.setUser_code(crsr.getInt(crsr.getColumnIndex(WorkTransaction.USER_CODE)));
                    transaction.setUpdate_type("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.UPDATE_TYPE)));
                    transaction.setMessage("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.MESSAGE)));
                    transaction.setMessage_type("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.MESSAGE_TYPE)));
                    transaction.setDiscription("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.DISCRIPTION)));
                    transaction.setAmount("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.AMOUNT)));
                    transaction.setInvoice_date("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.INVOICE_DATE)));
                    transaction.setStatus("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.STATUS)));
                    transaction.setCreated_on("" + crsr.getString(crsr.getColumnIndex(WorkTransaction.CREATED_ON)));
                    workTransactionArrayList.add(transaction);
                } while (crsr.moveToNext());
            }
        }
        if(crsr!=null) crsr.close();
    }

    private void InitializeWorkItem() {
        workItem = new WorkItem();
        Cursor crsr = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + code, null);
        if (crsr != null) {
            crsr.moveToFirst();
            workItem.setTaskCode(crsr.getInt(crsr.getColumnIndex(WorkItem.TASK_ID)));
            workItem.setTitle(crsr.getString(crsr.getColumnIndex(WorkItem.TASK_NAME)));
            workItem.setBudget(crsr.getString(crsr.getColumnIndex(WorkItem.BUDGET)));
        }
        if(crsr!=null) crsr.close();
    }
}
