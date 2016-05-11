package com.ctoddcook.auto_log;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 *  Created by ctodd on 4/12/2016.
 */
public final class FuelingDataMap extends AutoLogDataMap implements BaseColumns {
    // To prevent someone from accidentally instantiating this class, here is
    // an empty constructor.
    public FuelingDataMap() {}


    public static final String TABLE_NAME;
    public static final String COLUMN_NAME_VEHICLE_ID;
    public static final String COLUMN_NAME_DATE_OF_FILL;    // date and time of fill
    public static final String COLUMN_NAME_DISTANCE;        // miles or kilometers driven
    public static final String COLUMN_NAME_VOLUME;          // volume (i.e., gallons) of fuel
    public static final String COLUMN_NAME_PRICE_PAID;
    public static final String COLUMN_NAME_ODOMETER;
    public static final String COLUMN_NAME_LOCATION;        // Name of city, state
    public static final String COLUMN_NAME_GPS_COORDS;


    // These column numbers must match the order in the CREATE TABLE statement
    public static final int COLUMN_NBR_FUELING_ID = 0;
    public static final int COLUMN_NBR_VEHICLE_ID = 1;
    public static final int COLUMN_NBR_DATE_OF_FILL = 2;
    public static final int COLUMN_NBR_DISTANCE = 3;
    public static final int COLUMN_NBR_VOLUME = 4;
    public static final int COLUMN_NBR_PRICE_PAID = 5;
    public static final int COLUMN_NBR_ODOMETER = 6;
    public static final int COLUMN_NBR_LOCATION = 7;
    public static final int COLUMN_NBR_GPS_COORDS = 8;
    public static final int COLUMN_NBR_LAST_UPDATED = 9;


    static {
        TABLE_NAME = "fueling";
        COLUMN_NAME_VEHICLE_ID = "vehicle_id";
        COLUMN_NAME_DATE_OF_FILL = "date_of_fill";
        COLUMN_NAME_DISTANCE = "distance";
        COLUMN_NAME_VOLUME = "volume";
        COLUMN_NAME_PRICE_PAID = "price_paid";
        COLUMN_NAME_ODOMETER = "odometer";
        COLUMN_NAME_LOCATION = "location";
        COLUMN_NAME_GPS_COORDS = "gps_coords";
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
                    COLUMN_NAME_GPS_COORDS + STRING_TYPE + COMMA_SEP +
                    COLUMN_NAME_LAST_UPDATED + DATETIME_TYPE + " )";

    public static final String SQL_DROP_TABLE =
            DROP_TABLE_PHRASE + TABLE_NAME;

    public static final String SQL_SELECT_ALL =
            SELECT_PHRASE + "*" + FROM_PHRASE +
                    TABLE_NAME + ORDER_BY_PHRASE + COLUMN_NAME_DATE_OF_FILL + " DESC";

    public static boolean tableExists(SQLiteDatabase db) {
        return tableExists(db, TABLE_NAME);
    }
}

