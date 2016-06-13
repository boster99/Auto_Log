/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.FuelLog;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


/**
 * Created by C. Todd Cook on 5/10/2016.
 * ctodd@ctoddcook.com
 */
public class DatabaseMap_Vehicle extends DatabaseMap_FuelLog implements BaseColumns {
  public static final String TABLE_NAME = "vehicle";
  public static final String COLUMN_NAME_NAME = "vehicle_name";
  public static final String COLUMN_NAME_YEAR = "vehicle_year";
  public static final String COLUMN_NAME_COLOR = "vehicle_color";
  public static final String COLUMN_NAME_MODEL = "model";
  public static final String COLUMN_NAME_VIN = "vin";
  public static final String COLUMN_NAME_LICENSE_PLATE = "license_plate";
  public static final String COLUMN_NAME_STATUS = "status";
  public static final String COLUMN_NAME_LAST_UPDATED = "last_updated";

  // These column numbers must match the order in the CREATE TABLE and SELECT statements
  public static final int COLUMN_NBR_ID = 0;
  public static final int COLUMN_NBR_NAME = 1;
  public static final int COLUMN_NBR_YEAR = 2;
  public static final int COLUMN_NBR_COLOR = 3;
  public static final int COLUMN_NBR_MODEL = 4;
  public static final int COLUMN_NBR_VIN = 5;
  public static final int COLUMN_NBR_LICENSE_PLATE = 6;
  public static final int COLUMN_NBR_STATUS = 7;
  public static final int COLUMN_NBR_LAST_UPDATED = 8;

  public static final String SQL_CREATE_TABLE =
      CREATE_TABLE_PHRASE + TABLE_NAME + " (" +
          _ID + KEY_COLUMN_DEFINITION_PHRASE +       // AKA Model_Vehicle ID
          COLUMN_NAME_NAME + STRING_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_YEAR + INT_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_COLOR + STRING_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_MODEL + STRING_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_VIN + STRING_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_LICENSE_PLATE + STRING_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_STATUS + STRING_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_LAST_UPDATED + DATETIME_TYPE + " )";

  public static final String SQL_DROP_TABLE =
      DROP_TABLE_PHRASE + TABLE_NAME;

  public static final String SQL_SELECT_ALL =
      SELECT_PHRASE +
          _ID + COMMA_SEP +
          COLUMN_NAME_NAME + COMMA_SEP +
          COLUMN_NAME_YEAR + COMMA_SEP +
          COLUMN_NAME_COLOR + COMMA_SEP +
          COLUMN_NAME_MODEL + COMMA_SEP +
          COLUMN_NAME_VIN + COMMA_SEP +
          COLUMN_NAME_LICENSE_PLATE + COMMA_SEP +
          COLUMN_NAME_STATUS + COMMA_SEP +
          COLUMN_NAME_LAST_UPDATED +
          FROM_PHRASE + TABLE_NAME +
          ORDER_BY_PHRASE + COLUMN_NAME_STATUS + COMMA_SEP + _ID;

  public static final String SQL_SELECT_SIMPLE =
      SELECT_PHRASE + _ID + COMMA_SEP + COLUMN_NAME_NAME + FROM_PHRASE + TABLE_NAME;

  public static final String SQL_SELECT_SIMPLE_EXCLUDE_RETIRED =
      SELECT_PHRASE + _ID + COMMA_SEP + COLUMN_NAME_NAME + FROM_PHRASE + TABLE_NAME +
          WHERE_CLAUSE + COLUMN_NAME_STATUS + NOT_EQUAL + "'" + Model_Vehicle.STATUS_RETIRED + "'";

  public static boolean tableExists(SQLiteDatabase db) {
    return tableExists(db, TABLE_NAME);
  }
}
