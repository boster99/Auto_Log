/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.CGenTools;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.ctoddcook.auto_log.AutoLogDataMap;

/**
 * Provides SQL specifications (table name, column names and types) etc for the Property class.
 * <p>Created by C. Todd Cook on 5/18/2016.<br>
 * ctodd@ctoddcook.com
 */
public class PropertiesDataMap extends AutoLogDataMap implements BaseColumns {
  public static final String TABLE_NAME = "properties";
  public static final String COLUMN_NAME_NAME = "name";
  public static final String COLUMN_NAME_TYPE = "type";
  public static final String COLUMN_NAME_VALUE = "value";

  public static final int COLUMN_NBR_ID = 0;
  public static final int COLUMN_NBR_NAME = 1;
  public static final int COLUMN_NBR_TYPE = 2;
  public static final int COLUMN_NBR_VALUE = 3;
  public static final int COLUMN_NBR_LAST_UPDATED = 4;

  public static final String SQL_CREATE_TABLE =
      CREATE_TABLE_PHRASE + TABLE_NAME + " (" +
          _ID + KEY_COLUMN_DEFINITION_PHRASE +
          COLUMN_NAME_NAME + STRING_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_TYPE + INT_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_VALUE + STRING_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_LAST_UPDATED + DATETIME_TYPE + " )";

  public static final String SQL_DROP_TABLE =
      DROP_TABLE_PHRASE + TABLE_NAME;

  public static final String SQL_SELECT_ALL =
      SELECT_PHRASE + "*" + FROM_PHRASE +
          TABLE_NAME + ORDER_BY_PHRASE + _ID;

  public static final String SQL_SELECT_SIMPLE =
      SELECT_PHRASE + _ID + COMMA_SEP + COLUMN_NAME_NAME + FROM_PHRASE + TABLE_NAME;

  public static boolean tableExists(SQLiteDatabase db) {
    return tableExists(db, TABLE_NAME);
  }

  public static String getDeleteSQL(String name) {
    return getDeleteSQL(TABLE_NAME, COLUMN_NAME_NAME, name);
  }

  public static String getUpdateSQL(String name, String value) {
    return getUpdateSQL(TABLE_NAME, COLUMN_NAME_VALUE, value, COLUMN_NAME_NAME, name);
  }
}
