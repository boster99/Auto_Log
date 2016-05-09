package com.ctoddcook.auto_log;

import android.provider.BaseColumns;

/**
 *  Created by ctodd on 4/12/2016.
 */
public final class AutoLogContract {
    // To prevent someone from accidentally instantiating this class, here is
    // an empty constructor.
    public AutoLogContract() {}

    public static final String DATABASE_NAME = "autolog.db";
    private static final String DATETIME_TYPE = "INTEGER";
    private static final String INT_TYPE = "INTEGER";
    private static final String REAL_TYPE = "REAL";
    private static final String STRING_TYPE = "TEXT";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ", ";
    private static final String COLUMN_NAME_STATUS = "status";
    private static final String COLUMN_NAME_DATESTAMP = "datestamp";
    private static final String CREATE_TABLE_PHRASE = "CREATE TABLE ";
    private static final String KEY_COLUMN_DEFINITION_PHRASE = " INTEGER PRIMARY KEY AUTOINCREMENT,";
    private static final String DROP_TABLE_PHRASE = "DROP TABLE IF EXISTS ";



    public static String queryForTable(String tableName) {
        return "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
    }



    /* Inner class that defines the FUELING table contents */
    public static abstract class Fueling implements BaseColumns {
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
                CREATE_TABLE_PHRASE + Fueling.TABLE_NAME + " (" +
                        Fueling._ID + KEY_COLUMN_DEFINITION_PHRASE +
                        Fueling.COLUMN_NAME_VEHICLE_ID + INT_TYPE + NOT_NULL + COMMA_SEP +
                        Fueling.COLUMN_NAME_DATE_OF_FILL + DATETIME_TYPE + NOT_NULL + COMMA_SEP +
                        Fueling.COLUMN_NAME_DISTANCE + REAL_TYPE + NOT_NULL + COMMA_SEP +
                        Fueling.COLUMN_NAME_VOLUME + REAL_TYPE + NOT_NULL + COMMA_SEP +
                        Fueling.COLUMN_NAME_PRICE_PAID + REAL_TYPE + NOT_NULL + COMMA_SEP +
                        Fueling.COLUMN_NAME_ODOMETER + INT_TYPE + NOT_NULL + COMMA_SEP +
                        Fueling.COLUMN_NAME_LOCATION + REAL_TYPE + COMMA_SEP +
                        COLUMN_NAME_STATUS + STRING_TYPE + COMMA_SEP +
                        COLUMN_NAME_DATESTAMP + DATETIME_TYPE + " )";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + Fueling.TABLE_NAME;
    }




    /* Inner class that defines the VEHICLE table contents */
    public static abstract class Vehicle implements BaseColumns {
        public static final String TABLE_NAME;
        public static final String COLUMN_NAME_ID;
        public static final String COLUMN_NAME_NAME;
        public static final String COLUMN_NAME_YEAR;
        public static final String COLUMN_NAME_COLOR;
        public static final String COLUMN_NAME_MODEL;
        public static final String COLUMN_NAME_VIN;
        public static final String COLUMN_NAME_LICENSE_PLATE;
        public static final String COLUMN_NAME_STATUS;      // status of record
        public static final String COLUMN_NAME_DATESTAMP;   // When the record was saved


        static {
            TABLE_NAME = "vehicle";
            COLUMN_NAME_ID = "vehicle_id";
            COLUMN_NAME_NAME = "vehicle_name";
            COLUMN_NAME_YEAR = "vehicle_year";
            COLUMN_NAME_COLOR = "vehicle_color";
            COLUMN_NAME_MODEL = "model";
            COLUMN_NAME_VIN = "vin";
            COLUMN_NAME_LICENSE_PLATE = "license_plate";
            COLUMN_NAME_STATUS = "status";
            COLUMN_NAME_DATESTAMP = "datestamp";
        }

        public static final String SQL_CREATE_VEHICLE =
                CREATE_TABLE_PHRASE + Vehicle.TABLE_NAME + " (" +
                        Vehicle._ID + KEY_COLUMN_DEFINITION_PHRASE +       // AKA Vehicle ID
                        Vehicle.COLUMN_NAME_NAME + STRING_TYPE + NOT_NULL + COMMA_SEP +
                        Vehicle.COLUMN_NAME_YEAR + INT_TYPE + NOT_NULL + COMMA_SEP +
                        Vehicle.COLUMN_NAME_COLOR + STRING_TYPE + NOT_NULL + COMMA_SEP +
                        Vehicle.COLUMN_NAME_MODEL + STRING_TYPE + NOT_NULL + COMMA_SEP +
                        Vehicle.COLUMN_NAME_VIN + STRING_TYPE + NOT_NULL + COMMA_SEP +
                        Vehicle.COLUMN_NAME_LICENSE_PLATE + STRING_TYPE + NOT_NULL + COMMA_SEP +
                        Vehicle.COLUMN_NAME_STATUS + STRING_TYPE + COMMA_SEP +
                        Vehicle.COLUMN_NAME_DATESTAMP + DATETIME_TYPE + " )";

        public static final String SQL_DROP_VEHICLE =
                DROP_TABLE_PHRASE + Vehicle.TABLE_NAME;

    }










}

