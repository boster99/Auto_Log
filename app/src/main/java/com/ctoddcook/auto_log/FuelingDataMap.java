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
    public static final String COLUMN_NAME_DISTANCE;    // miles or kilometers driven
    public static final String COLUMN_NAME_VOLUME;      // volume (i.e., gallons) of fuel
    public static final String COLUMN_NAME_PRICE_PAID;
    public static final String COLUMN_NAME_ODOMETER;
    public static final String COLUMN_NAME_LOCATION;    // GPS location

    static {
        TABLE_NAME = "fueling";
        COLUMN_NAME_VEHICLE_ID = "vehicle_id";
        COLUMN_NAME_DATE_OF_FILL = "date_of_fill";
        COLUMN_NAME_DISTANCE = "distance";
        COLUMN_NAME_VOLUME = "volume";
        COLUMN_NAME_PRICE_PAID = "price_paid";
        COLUMN_NAME_ODOMETER = "odometer";
        COLUMN_NAME_LOCATION = "location";
    }

    public static final String SQL_CREATE_TABLE =
            CREATE_TABLE_PHRASE + TABLE_NAME + " (" +
                    _ID + KEY_COLUMN_DEFINITION_PHRASE +
                    COLUMN_NAME_VEHICLE_ID + INT_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_NAME_DATE_OF_FILL + DATETIME_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_NAME_DISTANCE + REAL_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_NAME_VOLUME + REAL_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_NAME_PRICE_PAID + REAL_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_NAME_ODOMETER + INT_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_NAME_LOCATION + REAL_TYPE + COMMA_SEP +
                    COLUMN_NAME_LAST_UPDATED + DATETIME_TYPE + " )";

    public static final String SQL_DROP_TABLE =
            DROP_TABLE_PHRASE + TABLE_NAME;

    public static boolean tableExists(SQLiteDatabase db) {
        return tableExists(db, TABLE_NAME);
    }
}

