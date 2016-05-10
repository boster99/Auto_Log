/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved. 
 */

package com.ctoddcook.auto_log;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by C. Todd Cook on 5/10/2016.
 * ctodd@ctoddcook.com
 */
public class AutoLogDataMap {
    public static final String DATABASE_NAME = "autolog.db";
    public static final String DATETIME_TYPE = "INTEGER";
    public static final String INT_TYPE = "INTEGER";
    public static final String REAL_TYPE = "REAL";
    public static final String STRING_TYPE = "TEXT";
    public static final String NOT_NULL = " NOT NULL";
    public static final String COMMA_SEP = ", ";
    public static final String COLUMN_NAME_LAST_UPDATED = "last_updated";
    public static final String CREATE_TABLE_PHRASE = "CREATE TABLE ";
    public static final String KEY_COLUMN_DEFINITION_PHRASE = " INTEGER PRIMARY KEY AUTOINCREMENT,";
    public static final String DROP_TABLE_PHRASE = "DROP TABLE IF EXISTS ";
    public static final String SELECT_PHRASE = "SELECT ";
    public static final String FROM_PHRASE = " FROM ";
    public static final String ORDER_BY_PHRASE = " ORDER BY ";

    private static final String DOES_TABLE_EXIST_PHRASE = "SELECT name FROM sqlite_master " +
            "WHERE type='table' AND name='?'";

    /**
     * Checks whether a table exists in the database
     * @param tableName the name of the table to query for
     * @return whether the table exists
     */
    public static boolean tableExists(SQLiteDatabase db, String tableName) {
        String query =  DOES_TABLE_EXIST_PHRASE.replace("?", tableName);
        boolean result = false;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 1)
            result = true;
        cursor.close();

        return result;
    }
}
