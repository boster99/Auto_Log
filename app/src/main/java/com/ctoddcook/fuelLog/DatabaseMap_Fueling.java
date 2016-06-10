package com.ctoddcook.fuelLog;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * This provides Database/SQL information for the table which holds Model_Fueling data. Includes column
 * names, types, ordinal numbers, table mName, create table statement, select all, etc.
 * <p>
 * Created by C. Todd Cook on 4/12/2016.<br>
 * ctodd@ctoddcook.com
 */
public final class DatabaseMap_Fueling extends FuelLogDataMap implements BaseColumns {
  // To prevent someone from accidentally instantiating this class, here is
  // an empty constructor.
  private DatabaseMap_Fueling() {
  }


  public static final String TABLE_NAME;
  public static final String COLUMN_NAME_VEHICLE_ID;
  public static final String COLUMN_NAME_DATE_OF_FILL;    // date and time of fill
  public static final String COLUMN_NAME_DISTANCE;        // miles or kilometers driven
  public static final String COLUMN_NAME_VOLUME;          // volume (i.e., gallons) of fuel
  public static final String COLUMN_NAME_PRICE_PAID;
  public static final String COLUMN_NAME_ODOMETER;
  public static final String COLUMN_NAME_LOCATION;        // Name of city, state
  public static final String COLUMN_NAME_LATITUDE;
  public static final String COLUMN_NAME_LONGITUDE;


  // These column numbers must match the order in the CREATE TABLE statement
  public static final int COLUMN_NBR_FUELING_ID = 0;
  public static final int COLUMN_NBR_VEHICLE_ID = 1;
  public static final int COLUMN_NBR_DATE_OF_FILL = 2;
  public static final int COLUMN_NBR_DISTANCE = 3;
  public static final int COLUMN_NBR_VOLUME = 4;
  public static final int COLUMN_NBR_PRICE_PAID = 5;
  public static final int COLUMN_NBR_ODOMETER = 6;
  public static final int COLUMN_NBR_LOCATION = 7;
  public static final int COLUMN_NBR_LATITUDE = 8;
  public static final int COLUMN_NBR_LONGITUDE = 9;
  public static final int COLUMN_NBR_LAST_UPDATED = 10;


  static {
    TABLE_NAME = "fueling";
    COLUMN_NAME_VEHICLE_ID = "vehicle_id";
    COLUMN_NAME_DATE_OF_FILL = "date_of_fill";
    COLUMN_NAME_DISTANCE = "distance";
    COLUMN_NAME_VOLUME = "volume";
    COLUMN_NAME_PRICE_PAID = "price_paid";
    COLUMN_NAME_ODOMETER = "odometer";
    COLUMN_NAME_LOCATION = "location";
    COLUMN_NAME_LATITUDE = "latitude";
    COLUMN_NAME_LONGITUDE = "longitude";
  }

  public static final String SQL_CREATE_TABLE =
      CREATE_TABLE_PHRASE + TABLE_NAME + " (" +
          _ID + KEY_COLUMN_DEFINITION_PHRASE +
          COLUMN_NAME_VEHICLE_ID + INT_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_DATE_OF_FILL + DATETIME_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_DISTANCE + REAL_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_VOLUME + REAL_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_PRICE_PAID + REAL_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_ODOMETER + REAL_TYPE + NOT_NULL + COMMA_SEP +
          COLUMN_NAME_LOCATION + STRING_TYPE + COMMA_SEP +
          COLUMN_NAME_LATITUDE + REAL_TYPE + COMMA_SEP +
          COLUMN_NAME_LONGITUDE + REAL_TYPE + COMMA_SEP +
          COLUMN_NAME_LAST_UPDATED + DATETIME_TYPE + " )";

  public static final String SQL_DROP_TABLE =
      DROP_TABLE_PHRASE + TABLE_NAME;

  public static final String SQL_SELECT_ALL =
      SELECT_PHRASE + "*" + FROM_PHRASE +
          TABLE_NAME + ORDER_BY_PHRASE + COLUMN_NAME_DATE_OF_FILL + " DESC";

  public static final String WHERE_VEHICLE_ID = COLUMN_NAME_VEHICLE_ID + EQUAL + "?";

  public static boolean tableExists(SQLiteDatabase db) {
    return tableExists(db, TABLE_NAME);
  }

  /**
   * Returns a string containing a SELECT statement for fetching Model_Fueling records from the
   * database for a particular vehicleID (or all vehicles).
   * @param vehicleID the vehicle to filter by, or 0 to get all records with no filter
   * @return a string with a SELECT statement
   */
  public static String getSelectSQL(int vehicleID) {
    String where = "";
    if (vehicleID > 0)
      where = " WHERE " + COLUMN_NAME_VEHICLE_ID + " = " + vehicleID;

    return SELECT_PHRASE + "*" + FROM_PHRASE + TABLE_NAME +
        where + ORDER_BY_PHRASE + COLUMN_NAME_DATE_OF_FILL + " DESC";
  }
}

