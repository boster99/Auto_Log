/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved. 
 */

package com.ctoddcook.CamGenTools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by C. Todd Cook on 5/10/2016.
 * ctodd@ctoddcook.com
 */
public abstract class DatabaseMap_Base {
  public static final String DATETIME_TYPE = " INTEGER";
  public static final String INT_TYPE = " INTEGER";
  public static final String REAL_TYPE = " REAL";
  public static final String STRING_TYPE = " TEXT";
  public static final String EQUAL = " = ";
  public static final String NOT_EQUAL = " != ";
  public static final String NOT_NULL = " NOT NULL";
  public static final String COMMA_SEP = ", ";
  public static final String COLUMN_NAME_LAST_UPDATED = "last_updated";
  public static final String CREATE_TABLE_PHRASE = "CREATE TABLE ";
  public static final String KEY_COLUMN_DEFINITION_PHRASE = " INTEGER PRIMARY KEY,";
  public static final String DROP_TABLE_PHRASE = "DROP TABLE IF EXISTS ";
  public static final String SELECT_PHRASE = "SELECT ";
  public static final String FROM_PHRASE = " FROM ";
  public static final String ORDER_BY_PHRASE = " ORDER BY ";
  public static final String WHERE_ID_CLAUSE = BaseColumns._ID + "=?";
  public static final String WHERE_CLAUSE = " WHERE ";
  public static final String DELETE_SQL = "DELETE FROM %1$s WHERE %2$s = %3$s";
  public static final String DELETE_ALL_SQL = "DELETE FROM %1$s";
  public static final String SELECT_SQL = "SELECT * FROM %1$s WHERE %2$s = %3$s";

  private static final String DOES_TABLE_EXIST_PHRASE = "SELECT name FROM sqlite_master " +
      "WHERE type='table' AND name='?'";

  /**
   * Checks whether a table exists in the database
   *
   * @param tableName the name of the table to query for
   * @return whether the table exists
   */
  public static boolean tableExists(SQLiteDatabase db, String tableName) {
    String query = DOES_TABLE_EXIST_PHRASE.replace("?", tableName);
    boolean result = false;

    Cursor cursor = db.rawQuery(query, null);
    if (cursor.getCount() > 0)
      result = true;
    cursor.close();

    return result;
  }

  public static String getDeleteSQL(String table, String column, String value) {
    return String.format(DELETE_SQL, table, column, value);
  }

  public static String getDeleteAllSQL(String table) {
    return String.format(DELETE_ALL_SQL, table);
  }

  public static String getSelectSQL(String table, String column, String value) {
    return String.format(SELECT_SQL, table, column, value);
  }

  public static String getUpdateSQL(String table, String columnName, String newValue, String
      whereName, String whereValue) {
    String sql = "UPDATE %1$s SET %2$s = %3$s WHERE %4$s = %5$s";
    return String.format(sql, table, columnName, newValue, whereName, whereValue);
  }
}
