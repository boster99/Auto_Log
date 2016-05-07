package com.ctoddcook.auto_log;

import android.provider.BaseColumns;

/**
 * Created by ctodd on 4/12/2016.
 */
public final class AutoLogContract {
    // To prevent someone from accidentally instantiating this class, here is
    // an empty constructor.
    public AutoLogContract() {}

    private static final String DATETIME_TYPE = "INTEGER";
    private static final String INT_TYPE = "INTEGER";
    private static final String REAL_TYPE = "REAL";
    private static final String STRING_TYPE = "TEXT";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ", ";

    /* Inner class that defines the table contents */
    public static abstract class Mileage implements BaseColumns {
        public static final String TABLE_NAME;
        public static final String COLUMN_NAME_VEHICLE_ID;
        public static final String COLUMN_NAME_DATE_OF_FILL;    // date and time of fill
        public static final String COLUMN_NAME_DISTANCE;    // miles or kilometers driven
        public static final String COLUMN_NAME_VOLUME;      // volume (i.e., gallons) of fuel
        public static final String COLUMN_NAME_PRICE_PAID;
        public static final String COLUMN_NAME_ODOMETER;
        public static final String COLUMN_NAME_LOCATION;    // GPS location
        public static final String COLUMN_NAME_STATUS;      // status of record
        public static final String COLUMN_NAME_DATESTAMP;   // When the record was saved

        static {
            TABLE_NAME = "mileage";
            COLUMN_NAME_VEHICLE_ID = "vehicle_id";
            COLUMN_NAME_DATE_OF_FILL = "date_of_fill";
            COLUMN_NAME_DISTANCE = "distance";
            COLUMN_NAME_VOLUME = "volume";
            COLUMN_NAME_PRICE_PAID = "price_paid";
            COLUMN_NAME_ODOMETER = "odometer";
            COLUMN_NAME_LOCATION = "location";
            COLUMN_NAME_STATUS = "status";
            COLUMN_NAME_DATESTAMP = "datestamp";
        }
    }

    private static final String SQL_CREATE_MILEAGE =
            "CREATE TABLE " + Mileage.TABLE_NAME + " (" +
                    Mileage._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Mileage.COLUMN_NAME_VEHICLE_ID + INT_TYPE + NOT_NULL + COMMA_SEP +
                    Mileage.COLUMN_NAME_DATE_OF_FILL + DATETIME_TYPE + NOT_NULL + COMMA_SEP +
                    Mileage.COLUMN_NAME_DISTANCE + REAL_TYPE + NOT_NULL + COMMA_SEP +
                    Mileage.COLUMN_NAME_VOLUME + REAL_TYPE + NOT_NULL + COMMA_SEP +
                    Mileage.COLUMN_NAME_PRICE_PAID + REAL_TYPE + NOT_NULL + COMMA_SEP +
                    Mileage.COLUMN_NAME_ODOMETER + INT_TYPE + NOT_NULL + COMMA_SEP +
                    Mileage.COLUMN_NAME_LOCATION + REAL_TYPE + COMMA_SEP +
                    Mileage.COLUMN_NAME_STATUS + STRING_TYPE + COMMA_SEP +
                    Mileage.COLUMN_NAME_DATESTAMP + DATETIME_TYPE + " )";

    // TODO: Provide a length of 1 for Column Status, or maybe change to CHAR

    private static final String SQL_DELETE_MILEAGE =
            "DROP TABLE IF EXISTS " + Mileage.TABLE_NAME;

    //public insert mileage
}

