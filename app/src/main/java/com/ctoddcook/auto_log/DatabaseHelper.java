package com.ctoddcook.auto_log;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ctodd on 4/17/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, AutoLogContract.DATABASE_NAME, null, 1);
    }


    /**
     * On create, check for existence of FUELING and VEHICLE tables. If they don't exist,
     * create them.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(AutoLogContract.queryForTable(AutoLogContract.Fueling.TABLE_NAME), null);
        if (cursor.getCount() == 0) {
            db.execSQL(AutoLogContract.Fueling.SQL_CREATE_TABLE);
        }

        cursor = db.rawQuery(AutoLogContract.queryForTable(AutoLogContract.Vehicle.TABLE_NAME), null);
        if (cursor.getCount() == 0) {
            db.execSQL(AutoLogContract.Fueling.SQL_CREATE_TABLE);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        FUTURE: If the table columns are changed, we need to capture the old data, then
        adjust the tables, then write the data back into the adjusted tables.
         */
        onCreate(db);
    }
}
