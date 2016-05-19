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

  public static final int DATABASE_VERSION = 2;

  public DatabaseHelper(Context context) {
    super(context, AutoLogDataMap.DATABASE_NAME, null, DATABASE_VERSION);
  }


  /**
   * On create, check for existence of FUELING and VEHICLE tables. If they don't exist,
   * create them. (They should not exist, since onCreate() is called when the database
   * is created.)
   *
   * @param db the database for this app
   */
  @Override
  public void onCreate(SQLiteDatabase db) {
    if (!FuelingDataMap.tableExists(db)) {
      db.execSQL(FuelingDataMap.SQL_CREATE_TABLE);
    }

    if (!VehicleDataMap.tableExists(db)) {
      db.execSQL(VehicleDataMap.SQL_CREATE_TABLE);
    }
  }


  /**
   * Upgrade table(s) when a schema changes from one production release to another.
   *
   * @param db         The database
   * @param oldVersion The user's old (current) version, to be upgraded from
   * @param newVersion The app's new version, to be upgraded to
   */
  @Override
  @SuppressWarnings("SpellCheckingInspection")  // because URLs generate typo warnings
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*

        This is a good discussion of when onUpgrade() is called.
        http://stackoverflow.com/questions/21881992/when-is-sqliteopenhelper-oncreate-onupgrade-run

        Examples
        https://thebhwgroup.com/blog/how-android-sqlite-onupgrade

        */
  }




    /*
    FOLLOWING ARE METHODS FOR SELECTING/INSERTING/DELETING/UPDATING THE VEHICLE TABLE
     */

  /**
   * Retrieves the complete list of vehicles from the database.
   *
   * @return an ArrayList of all vehicles defined
   */
  public ArrayList<VehicleData> fetchVehicleList() {
    SQLiteDatabase db = this.getWritableDatabase();
    ArrayList<VehicleData> vehicleList = new ArrayList<>();
    int id, year;
    String name, color, model, vin, licensePlate;
    Date lastUpdated = new Date();
    VehicleData vehicle;

    Cursor cursor = db.rawQuery(VehicleDataMap.SQL_SELECT_ALL, null);

    while (cursor.moveToNext()) {
      id = cursor.getInt(VehicleDataMap.COLUMN_NBR_ID);
      name = cursor.getString(VehicleDataMap.COLUMN_NBR_NAME);
      year = cursor.getInt(VehicleDataMap.COLUMN_NBR_YEAR);
      color = cursor.getString(VehicleDataMap.COLUMN_NBR_COLOR);
      model = cursor.getString(VehicleDataMap.COLUMN_NBR_MODEL);
      vin = cursor.getString(VehicleDataMap.COLUMN_NBR_VIN);
      licensePlate = cursor.getString(VehicleDataMap.COLUMN_NBR_LICENSE_PLATE);
      lastUpdated.setTime(cursor.getInt(VehicleDataMap.COLUMN_NBR_LAST_UPDATED));

      vehicle = new VehicleData(id, name, year, color, model, vin, licensePlate, lastUpdated);

      vehicleList.add(vehicle);
    }

    cursor.close();

    return vehicleList;
  }

  /**
   * Returns a cursor pointer to all of the rows in the Vehicle table, with just the _ID
   * and NAME columns provided.
   *
   * @return a cursor pointing to the Vehicle table
   */
  public Cursor fetchSimpleVehicleListCursor() {
    SQLiteDatabase db = this.getWritableDatabase();

    return db.rawQuery(VehicleDataMap.SQL_SELECT_SIMPLE, null);
  }

  /**
   * Inserts a new vehicle row into the database. After a successful insert, it updates
   * the instance with the unique ID assigned to its database record, and sets the object's
   * state to CURRENT.
   *
   * @param vehicle the vehicle to be added to the database
   * @return the unique ID of the new record
   * @throws IllegalArgumentException if the instance's state is not NEW
   */
  public int insertVehicle(VehicleData vehicle) throws IllegalArgumentException {
    if (!vehicle.isNew())
      throw new IllegalArgumentException("Cannot insert a new vehicle if its state is not new");

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues cv = new ContentValues();
    cv.put(VehicleDataMap.COLUMN_NAME_NAME, vehicle.getName());
    cv.put(VehicleDataMap.COLUMN_NAME_YEAR, vehicle.getYear());
    cv.put(VehicleDataMap.COLUMN_NAME_COLOR, vehicle.getColor());
    cv.put(VehicleDataMap.COLUMN_NAME_MODEL, vehicle.getModel());
    cv.put(VehicleDataMap.COLUMN_NAME_VIN, vehicle.getVIN());
    cv.put(VehicleDataMap.COLUMN_NAME_LICENSE_PLATE, vehicle.getLicensePlate());
    cv.put(VehicleDataMap.COLUMN_NAME_LAST_UPDATED, vehicle.getLastUpdated().getTime());

    int newID = (int) db.insert(VehicleDataMap.TABLE_NAME, null, cv);

    if (newID > 0) {
      vehicle.setVehicleID(newID);
      vehicle.setCurrent();
    }

    return newID;
  }

  /**
   * Deletes the database row with the ID (prime key) of the Vehicle instance.
   *
   * @param vehicle the instance to be deleted
   * @return true if the number of rows deleted is exactly 1
   * @throws IllegalArgumentException if vehicle's state is not DELETED
   */
  public boolean deleteVehicle(VehicleData vehicle) throws IllegalArgumentException {
    if (!vehicle.isDeleted())
      throw new IllegalArgumentException("Cannot delete a vehicle whose state is not DELETED");

    SQLiteDatabase db = this.getWritableDatabase();
    String[] whereArgs = new String[]{String.valueOf(vehicle.getID())};

    int rowsDeleted = db.delete(VehicleDataMap.TABLE_NAME, VehicleDataMap.WHERE_ID_CLAUSE, whereArgs);

    return (rowsDeleted == 1);
  }

  /**
   * Updates the database with data from the provided Vehicle instance
   *
   * @param vehicle the vehicle whose data is to be updated
   * @return true, if exactly 1 row is updated
   * @throws IllegalArgumentException if the vehicle's state is not set to UPDATED
   */
  public boolean updateVehicle(VehicleData vehicle) throws IllegalArgumentException {
    if (!vehicle.isUpdated())
      throw new IllegalArgumentException("Cannot update a vehicle record whose state is not UPDATED");

    boolean result = true;
    SQLiteDatabase db = this.getWritableDatabase();
    String[] whereArgs = new String[]{String.valueOf(vehicle.getID())};

    ContentValues cv = new ContentValues();
    cv.put(VehicleDataMap.COLUMN_NAME_NAME, vehicle.getName());
    cv.put(VehicleDataMap.COLUMN_NAME_YEAR, vehicle.getYear());
    cv.put(VehicleDataMap.COLUMN_NAME_COLOR, vehicle.getColor());
    cv.put(VehicleDataMap.COLUMN_NAME_MODEL, vehicle.getModel());
    cv.put(VehicleDataMap.COLUMN_NAME_VIN, vehicle.getVIN());
    cv.put(VehicleDataMap.COLUMN_NAME_LICENSE_PLATE, vehicle.getLicensePlate());
    cv.put(VehicleDataMap.COLUMN_NAME_LAST_UPDATED, vehicle.getLastUpdated().getTime());

    int rowsUpdated = db.update(VehicleDataMap.TABLE_NAME, cv, VehicleDataMap.WHERE_ID_CLAUSE, whereArgs);
    if (rowsUpdated == 1)
      vehicle.setCurrent();
    else
      result = false;

    return result;
  }

  /**
   * For each VehicleData instance in the list, the object is passed through to
   * updateVehicle(VehicleData). The number of updated rows is returned; the calling
   * method can compare this to the number of items in the list as a check on total success.
   *
   * @param vList an array of VehicleData objects to be updated in the database
   * @return the number of successfully updated rows
   */
  public int updateVehicles(ArrayList<VehicleData> vList) {
    int result = 0;

    for (VehicleData each : vList) {
      if (updateVehicle(each))
        result++;
    }

    return result;
  }





    /*
    FOLLOWING ARE METHODS FOR UPDATING/INSERTING/SELECTING/DELETING FUELINGDATA
     */


  /**
   * Retrieves the complete list of FuelingData records from the database, in descending
   * order by DateOfFill (i.e., the newest records are fetched first).
   *
   * @return an ArrayList of FuelingData objects
   */
  public ArrayList<FuelingData> fetchFuelingData(int vehicleID) {
    SQLiteDatabase db = this.getWritableDatabase();
    ArrayList<FuelingData> fdList = new ArrayList<>();
    int fuelingID;
    Date dateOfFill = new Date();
    Date lastUpdated = new Date();
    float distance, volume, pricePaid, odometer, latitude, longitude;
    String location;
    FuelingData fd;
    String sql = FuelingDataMap.getSelectSQL(vehicleID);

    Cursor cursor = db.rawQuery(sql, null);

    while (cursor.moveToNext()) {
      fuelingID = cursor.getInt(FuelingDataMap.COLUMN_NBR_FUELING_ID);
      vehicleID = cursor.getInt(FuelingDataMap.COLUMN_NBR_VEHICLE_ID);
      dateOfFill.setTime(cursor.getInt(FuelingDataMap.COLUMN_NBR_DATE_OF_FILL));
      distance = cursor.getFloat(FuelingDataMap.COLUMN_NBR_DISTANCE);
      volume = cursor.getFloat(FuelingDataMap.COLUMN_NBR_VOLUME);
      pricePaid = cursor.getFloat(FuelingDataMap.COLUMN_NBR_PRICE_PAID);
      odometer = cursor.getFloat(FuelingDataMap.COLUMN_NBR_ODOMETER);
      location = cursor.getString(FuelingDataMap.COLUMN_NBR_LOCATION);
      latitude = cursor.getFloat(FuelingDataMap.COLUMN_NBR_LATITUDE);
      longitude = cursor.getFloat(FuelingDataMap.COLUMN_NBR_LONGITUDE);
      lastUpdated.setTime(cursor.getInt(FuelingDataMap.COLUMN_NBR_LAST_UPDATED));

      fd = new FuelingData(fuelingID, vehicleID, dateOfFill, distance, volume, pricePaid,
          odometer, location, latitude, longitude, lastUpdated);

      fdList.add(fd);
    }

    cursor.close();

    return fdList;
  }

  /**
   * Inserts a new FuelingData record into the database. After a successful insert, it updates
   * the instance with the unique ID assigned to its database record, and sets the object's
   * state to CURRENT.
   *
   * @param fd the object instance to be inserted
   * @return the unique ID of the new record
   * @throws IllegalArgumentException if the object's state is not NEW
   */
  public int insertFueling(FuelingData fd) throws IllegalArgumentException {
    if (!fd.isNew())
      throw new IllegalArgumentException("Cannot insert a FuelingData record if its state is not NEW");

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues cv = new ContentValues();
    cv.put(FuelingDataMap.COLUMN_NAME_VEHICLE_ID, fd.getVehicleID());
    cv.put(FuelingDataMap.COLUMN_NAME_DATE_OF_FILL, fd.getDateOfFill().getTime());
    cv.put(FuelingDataMap.COLUMN_NAME_DISTANCE, fd.getDistance());
    cv.put(FuelingDataMap.COLUMN_NAME_VOLUME, fd.getVolume());
    cv.put(FuelingDataMap.COLUMN_NAME_PRICE_PAID, fd.getPricePaid());
    cv.put(FuelingDataMap.COLUMN_NAME_ODOMETER, fd.getOdometer());
    cv.put(FuelingDataMap.COLUMN_NAME_LOCATION, fd.getLocation());
    cv.put(FuelingDataMap.COLUMN_NAME_LATITUDE, fd.getLatitude());
    cv.put(FuelingDataMap.COLUMN_NAME_LONGITUDE, fd.getLongitude());
    cv.put(FuelingDataMap.COLUMN_NAME_LAST_UPDATED, fd.getLastUpdated().getTime());

    int newID = (int) db.insert(FuelingDataMap.TABLE_NAME, null, cv);

    if (newID > 0) {
      fd.setFuelingID(newID);
      fd.setCurrent();
    }

    return newID;
  }

  /**
   * Deletes the database row with the ID (prime key) of the FuelingData instance.
   *
   * @param fd the instance to be deleted
   * @return true if the number of rows deleted is exactly 1
   * @throws IllegalArgumentException if the instance's state is not set to DELETED
   */
  public boolean deleteFueling(FuelingData fd) throws IllegalArgumentException {
    if (!fd.isDeleted())
      throw new IllegalArgumentException("Cannot delete an object if it's state is not DELETED");

    SQLiteDatabase db = this.getWritableDatabase();
    String[] whereArgs = new String[]{String.valueOf(fd.getID())};

    int rowsDeleted = db.delete(FuelingDataMap.TABLE_NAME, FuelingDataMap.WHERE_ID_CLAUSE, whereArgs);

    return (rowsDeleted == 1);
  }

  /**
   * Updates a FuelingData record in the database with new data, and resets the instance's
   * status to CURRENT. If the update fails (meaning the number of rows updated is not 1)
   * the status is not changed, and false is returned.
   *
   * @param fd the instance whose record is to be updated
   * @return true, if exactly 1 row is updated
   * @throws IllegalArgumentException if the instance's state is not set to UPDATED
   */
  public boolean updateFueling(FuelingData fd) throws IllegalArgumentException {
    if (!fd.isUpdated())
      throw new IllegalArgumentException("Cannot updated a database record if the object's state is not set to UPDATED");

    boolean result = true;
    SQLiteDatabase db = this.getWritableDatabase();
    String[] whereArgs = new String[]{String.valueOf(fd.getID())};

    ContentValues cv = new ContentValues();
    cv.put(FuelingDataMap.COLUMN_NAME_VEHICLE_ID, fd.getVehicleID());
    cv.put(FuelingDataMap.COLUMN_NAME_DATE_OF_FILL, fd.getDateOfFill().getTime());
    cv.put(FuelingDataMap.COLUMN_NAME_DISTANCE, fd.getDistance());
    cv.put(FuelingDataMap.COLUMN_NAME_VOLUME, fd.getVolume());
    cv.put(FuelingDataMap.COLUMN_NAME_PRICE_PAID, fd.getPricePaid());
    cv.put(FuelingDataMap.COLUMN_NAME_ODOMETER, fd.getOdometer());
    cv.put(FuelingDataMap.COLUMN_NAME_LOCATION, fd.getLocation());
    cv.put(FuelingDataMap.COLUMN_NAME_LATITUDE, fd.getLatitude());
    cv.put(FuelingDataMap.COLUMN_NAME_LONGITUDE, fd.getLongitude());
    cv.put(FuelingDataMap.COLUMN_NAME_LAST_UPDATED, fd.getLastUpdated().getTime());

    int rowsUpdated = db.update(FuelingDataMap.TABLE_NAME, cv, FuelingDataMap.WHERE_ID_CLAUSE, whereArgs);

    if (rowsUpdated == 1)
      fd.setCurrent();
    else
      result = false;

    return result;
  }

  /**
   * For each FuelingData instance in the provided list, it is passed through to
   * updateFueling(FuelingData). The number of updated rows is returned; the calling
   * method can compare this to the number of items in the list as a check on total success.
   *
   * @param fdList an array of FuelingData objects to be updated in the database
   * @return the number of successfully updated rows
   */
  public int updateFueling(ArrayList<FuelingData> fdList) {
    int result = 0;

    for (FuelingData each : fdList) {
      if (!updateFueling(each))
        result++;
    }

    return result;
  }

}
