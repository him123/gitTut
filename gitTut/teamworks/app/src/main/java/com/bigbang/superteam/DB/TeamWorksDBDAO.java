package com.bigbang.superteam.DB;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bigbang.superteam.util.SQLiteHelper;

public class TeamWorksDBDAO {

    protected SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private Context mContext;

    public TeamWorksDBDAO(Context context) {
        this.mContext = context;
        dbHelper = SQLiteHelper.getHelper(mContext);
        open();
    }

    public TeamWorksDBDAO() throws SQLException{
        if (dbHelper == null)
            dbHelper = SQLiteHelper.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
    }

    public void open() throws SQLException {
        if (dbHelper == null)
            dbHelper = SQLiteHelper.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
//        database.execSQL("PRAGMA foreign_keys=ON;");
    }

    public String getShopKeeperID() {

        return "";
    }

	/*public void close() {
        dbHelper.close();
		database = null;
	}*/
}
