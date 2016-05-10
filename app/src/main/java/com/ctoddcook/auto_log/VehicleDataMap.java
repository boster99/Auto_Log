/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


/**
 * Created by C. Todd Cook on 5/10/2016.
 * ctodd@ctoddcook.com
 */
public class VehicleDataMap extends AutoLogDataMap implements BaseColumns {
    public static final String TABLE_NAME;
    public static final String COLUMN_NAME_NAME;
    public static final String COLUMN_NAME_YEAR;
    public static final String COLUMN_NAME_COLOR;
    public static final String COLUMN_NAME_MODEL;
    public static final String COLUMN_NAME_VIN;
    public static final String COLUMN_NAME_LICENSE_PLATE;
    public static final String COLUMN_NAME_LAST_UPDATED;

    // These column numbers must match the order in the CREATE TABLE and SELECT statements
    public static final int COLUMN_NBR_ID = 0;
    public static final int COLUMN_NBR_NAME = 1;
    public static final int COLUMN_NBR_YEAR = 2;
    public static final int COLUMN_NBR_COLOR = 3;
    public static final int COLUMN_NBR_MODEL = 4;
    public static final int COLUMN_NBR_VIN = 5;
    public static final int COLUMN_NBR_LICENSE_PLATE = 6;
    public static final int COLUMN_NBR_LAST_UPDATED = 7;

    static {
        TABLE_NAME = "vehicle";
        COLUMN_NAME_NAME = "vehicle_name";
        COLUMN_NAME_YEAR = "vehicle_year";
        COLUMN_NAME_COLOR = "vehicle_color";
        COLUMN_NAME_MODEL = "model";
        COLUMN_NAME_VIN = "vin";
        COLUMN_NAME_LICENSE_PLATE = "license_plate";
        COLUMN_NAME_LAST_UPDATED = "last_updated";
    }

    public static final String SQL_CREATE_TABLE =
            CREATE_TABLE_PHRASE + TABLE_NAME + " (" +
                    _ID + KEY_COLUMN_DEFINITION_PHRASE +       // AKA Vehicle ID
                    COLUMN_NAME_NAME + STRING_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_NAME_YEAR + INT_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_NAME_COLOR + STRING_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_NAME_MODEL + STRING_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_NAME_VIN + STRING_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_NAME_LICENSE_PLATE + STRING_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_NAME_LAST_UPDATED + DATETIME_TYPE + " )";

    public static final String SQL_DROP_TABLE =
            DROP_TABLE_PHRASE + TABLE_NAME;

    public static final String SQL_SELECT_ALL =
            SELECT_PHRASE + "*" + FROM_PHRASE +
                    TABLE_NAME + ORDER_BY_PHRASE + _ID;
    
    public static boolean tableExists(SQLiteDatabase db) {
        return tableExists(db, TABLE_NAME);
    }
}
