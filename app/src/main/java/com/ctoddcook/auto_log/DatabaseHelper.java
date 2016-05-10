package com.ctoddcook.auto_log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by C. Todd Cook on 4/17/2016.
 * ctodd@ctoddcook.com
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String COMMA = ",";

    public DatabaseHelper(Context context) {
        super(context, AutoLogContract.DATABASE_NAME, null, 1);
    }


    /**
     * On create, check for existence of FUELING and VEHICLE tables. If they don't exist,
     * create them.
     * @param db the database for this app
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(AutoLogContract.queryForTable(AutoLogContract.Fueling.TABLE_NAME), null);
        if (cursor.getCount() == 0) {
            db.execSQL(AutoLogContract.Fueling.SQL_CREATE_TABLE);
        }
        cursor.close();

        cursor = db.rawQuery(AutoLogContract.queryForTable(AutoLogContract.Vehicle.TABLE_NAME), null);
        if (cursor.getCount() == 0) {
            db.execSQL(AutoLogContract.Vehicle.SQL_CREATE_TABLE);
        }
        cursor.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        FUTURE: If the table columns are changed, we need to capture the old data, then
        adjust the tables, then write the data back into the adjusted tables.
         */
        onCreate(db);
    }

    /**
     * Retrieves the complete list of vehicles from the database.
     * @return an ArrayList of all vehicles defined
     */
    public ArrayList<VehicleData> getVehicleList() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<VehicleData> vehicleList = new ArrayList<VehicleData>();
        int id, year;
        String name, color, model, vin, licensePlate;
        Date lastUpdated = new Date();
        VehicleData vehicle;

        String query = "SELECT " +
                AutoLogContract.Vehicle.COLUMN_NAME_ID + COMMA +
                AutoLogContract.Vehicle.COLUMN_NAME_NAME + COMMA +
                AutoLogContract.Vehicle.COLUMN_NAME_YEAR + COMMA +
                AutoLogContract.Vehicle.COLUMN_NAME_COLOR + COMMA +
                AutoLogContract.Vehicle.COLUMN_NAME_MODEL + COMMA +
                AutoLogContract.Vehicle.COLUMN_NAME_VIN + COMMA +
                AutoLogContract.Vehicle.COLUMN_NAME_LICENSE_PLATE + COMMA +
                AutoLogContract.Vehicle.COLUMN_NAME_LAST_UPDATED + "FROM " +
                AutoLogContract.Vehicle.TABLE_NAME + " ORDER BY " +
                AutoLogContract.Vehicle.COLUMN_NAME_ID;

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            id = cursor.getInt(0);
            name = cursor.getString(1);
            year = cursor.getInt(2);
            color = cursor.getString(3);
            model = cursor.getString(4);
            vin = cursor.getString(5);
            licensePlate = cursor.getString(6);
            lastUpdated.setTime(cursor.getInt(7));

            vehicle = new VehicleData(id, name, year, color, model, vin, licensePlate, lastUpdated);

            vehicleList.add(vehicle);
        }

        cursor.close();

        return vehicleList;
    }

    public long insertVehicle(VehicleData vehicle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AutoLogContract.Vehicle.COLUMN_NAME_NAME, vehicle.getName());
        cv.put(AutoLogContract.Vehicle.COLUMN_NAME_YEAR, vehicle.getYear());
        cv.put(AutoLogContract.Vehicle.COLUMN_NAME_COLOR, vehicle.getColor());
        cv.put(AutoLogContract.Vehicle.COLUMN_NAME_MODEL, vehicle.getModel());
        cv.put(AutoLogContract.Vehicle.COLUMN_NAME_VIN, vehicle.getVIN());
        cv.put(AutoLogContract.Vehicle.COLUMN_NAME_LICENSE_PLATE, vehicle.getLicensePlate());
        cv.put(AutoLogContract.Vehicle.COLUMN_NAME_LAST_UPDATED, vehicle.getLastUpdated().getTime());

        long newID = db.insert(AutoLogContract.Vehicle.TABLE_NAME, null, cv);

        if (newID > 0) {
            vehicle.setVehicleID(newID);
        }

        return newID;
    }
}
