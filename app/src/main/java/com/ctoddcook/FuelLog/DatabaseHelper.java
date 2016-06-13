package com.ctoddcook.FuelLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ctoddcook.CamGenTools.DatabaseMap_Base;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by C. Todd Cook on 4/17/2016.
 * ctodd@ctoddcook.com
 */
public class DatabaseHelper extends SQLiteOpenHelper {

  public static final int DATABASE_VERSION = 3;
  private static DatabaseHelper sInstance;


  private DatabaseHelper(Context context) {
    super(context, DatabaseMap_FuelLog.DATABASE_NAME, null, DATABASE_VERSION);
  }


  /**
   * Returns reference to singleton instance for global use.
   * @param c the context in which this is being used
   * @return a reference to the global DatabaseHelper instance
   */
  public static synchronized DatabaseHelper getInstance(Context c) {
    if (sInstance == null)
      sInstance = new DatabaseHelper(c);

    return sInstance;
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
    if (!DatabaseMap_Fueling.tableExists(db)) {
      db.execSQL(DatabaseMap_Fueling.SQL_CREATE_TABLE);
    }

    if (!DatabaseMap_Vehicle.tableExists(db)) {
      db.execSQL(DatabaseMap_Vehicle.SQL_CREATE_TABLE);
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

    switch (oldVersion) {
      case 1:   // version 1 was never put into production
      case 2:
        upgradeToVersion3(db);
        break;
      default:
        throw new IllegalStateException("onUpgrade() with unknown oldVersion" + oldVersion);
    }
  }


  /**
   * Closes the open database. Should be called before the app exits.
   */
  public void close() {
    sInstance.close();
  }




    /*
    FOLLOWING ARE METHODS FOR SELECTING/INSERTING/DELETING/UPDATING THE VEHICLE TABLE
     */

  /**
   * Retrieves the complete list of vehicles from the database.
   *
   * @return an ArrayList of all vehicles defined
   */
  public ArrayList<Model_Vehicle> fetchVehicleList() {
    SQLiteDatabase db = this.getWritableDatabase();
    ArrayList<Model_Vehicle> vehicleList = new ArrayList<>();
    int id, year;
    String name, color, model, vin, licensePlate, status;
    Date lastUpdated;
    Model_Vehicle vehicle;

    Model_Vehicle.clearAll();

    Cursor cursor = db.rawQuery(DatabaseMap_Vehicle.SQL_SELECT_ALL, null);

    while (cursor.moveToNext()) {
      id = cursor.getInt(DatabaseMap_Vehicle.COLUMN_NBR_ID);
      name = cursor.getString(DatabaseMap_Vehicle.COLUMN_NBR_NAME);
      year = cursor.getInt(DatabaseMap_Vehicle.COLUMN_NBR_YEAR);
      color = cursor.getString(DatabaseMap_Vehicle.COLUMN_NBR_COLOR);
      model = cursor.getString(DatabaseMap_Vehicle.COLUMN_NBR_MODEL);
      vin = cursor.getString(DatabaseMap_Vehicle.COLUMN_NBR_VIN);
      licensePlate = cursor.getString(DatabaseMap_Vehicle.COLUMN_NBR_LICENSE_PLATE);
      status = cursor.getString(DatabaseMap_Vehicle.COLUMN_NBR_STATUS);
      lastUpdated = new Date(cursor.getInt(DatabaseMap_Vehicle.COLUMN_NBR_LAST_UPDATED));

      vehicle = new Model_Vehicle(id, name, year, color, model, vin, licensePlate, status, lastUpdated);

      vehicleList.add(vehicle);
    }

    cursor.close();

    return vehicleList;
  }

  /**
   * Returns a cursor pointer to all of the rows in the Model_Vehicle table, with just the _ID
   * and NAME columns provided.
   *
   * @return a cursor pointing to the Model_Vehicle table
   */
  public Cursor fetchSimpleVehicleListCursor(boolean includeRetired) {
    SQLiteDatabase db = this.getWritableDatabase();

    String sql = (includeRetired ? DatabaseMap_Vehicle.SQL_SELECT_SIMPLE : DatabaseMap_Vehicle
        .SQL_SELECT_SIMPLE_EXCLUDE_RETIRED);

    return db.rawQuery(sql, null);
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
  public int insertVehicle(Model_Vehicle vehicle) throws IllegalArgumentException {
    if (!vehicle.isNew())
      throw new IllegalArgumentException("Cannot insert a new vehicle if its state is not new");

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues cv = new ContentValues();
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_NAME, vehicle.getName());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_YEAR, vehicle.getYear());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_COLOR, vehicle.getColor());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_MODEL, vehicle.getModel());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_VIN, vehicle.getVIN());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_LICENSE_PLATE, vehicle.getLicensePlate());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_STATUS, vehicle.getVehicleStatus());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_LAST_UPDATED, vehicle.getLastUpdated().getTime());

    int newID = (int) db.insert(DatabaseMap_Vehicle.TABLE_NAME, null, cv);

    if (newID > 0) {
      vehicle.setVehicleID(newID);
      vehicle.setCurrent();
    }

    return newID;
  }

  /**
   * Deletes the database row with the ID (prime key) of the Model_Vehicle instance, after deleting any
   * rows from the fueling table which point to that Model_Vehicle ID.
   *
   * @param vehicle the instance to be deleted
   * @return true if the number of rows deleted is exactly 1
   * @throws IllegalArgumentException if vehicle's state is not DELETED
   */
  public boolean deleteVehicle(Model_Vehicle vehicle) throws IllegalArgumentException {
    if (!vehicle.isDeleted())
      throw new IllegalArgumentException("Cannot delete a vehicle whose state is not DELETED");

    SQLiteDatabase db = this.getWritableDatabase();
    String[] whereArgs = new String[]{String.valueOf(vehicle.getID())};

    int rowsDeleted = 0;

    db.beginTransaction();
    try {
      rowsDeleted = db.delete(DatabaseMap_Fueling.TABLE_NAME, DatabaseMap_Fueling.WHERE_VEHICLE_ID, whereArgs);
      rowsDeleted += db.delete(DatabaseMap_Vehicle.TABLE_NAME, DatabaseMap_Vehicle.WHERE_ID_CLAUSE, whereArgs);
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }

    return (rowsDeleted > 1);
  }

  /**
   * Updates the database with data from the provided Model_Vehicle instance
   *
   * @param vehicle the vehicle whose data is to be updated
   * @return true, if exactly 1 row is updated
   * @throws IllegalArgumentException if the vehicle's state is not set to UPDATED
   */
  public boolean updateVehicle(Model_Vehicle vehicle) throws IllegalArgumentException {
    if (!vehicle.isUpdated())
      throw new IllegalArgumentException("Cannot update a vehicle record whose state is not UPDATED");

    boolean result = true;
    SQLiteDatabase db = this.getWritableDatabase();
    String[] whereArgs = new String[]{String.valueOf(vehicle.getID())};

    ContentValues cv = new ContentValues();
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_NAME, vehicle.getName());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_YEAR, vehicle.getYear());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_COLOR, vehicle.getColor());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_MODEL, vehicle.getModel());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_VIN, vehicle.getVIN());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_LICENSE_PLATE, vehicle.getLicensePlate());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_STATUS, vehicle.getVehicleStatus());
    cv.put(DatabaseMap_Vehicle.COLUMN_NAME_LAST_UPDATED, vehicle.getLastUpdated().getTime());

    int rowsUpdated = db.update(DatabaseMap_Vehicle.TABLE_NAME, cv, DatabaseMap_Vehicle.WHERE_ID_CLAUSE, whereArgs);
    if (rowsUpdated == 1)
      vehicle.setCurrent();
    else
      result = false;

    return result;
  }

  /**
   * For each Model_Vehicle instance in the list, the object is passed through to
   * updateVehicle(Model_Vehicle). The number of updated rows is returned; the calling
   * method can compare this to the number of items in the list as a check on total success.
   *
   * @param vList an array of Model_Vehicle objects to be updated in the database
   * @return the number of successfully updated rows
   */
  public int updateVehicles(ArrayList<Model_Vehicle> vList) {
    int result = 0;

    for (Model_Vehicle each : vList) {
      if (updateVehicle(each))
        result++;
    }

    return result;
  }





    /*
    FOLLOWING ARE METHODS FOR UPDATING/INSERTING/SELECTING/DELETING FUELINGDATA
     */


  /**
   * Retrieves the complete list of Model_Fueling records from the database, in descending
   * order by DateOfFill (i.e., the newest records are fetched first).
   *
   * @return an ArrayList of Model_Fueling objects
   */
  public ArrayList<Model_Fueling> fetchFuelingData(int vehicleID) {
    SQLiteDatabase db = this.getWritableDatabase();
    ArrayList<Model_Fueling> fdList = new ArrayList<>();
    int fuelingID;
    Date dateOfFill;
    Date lastUpdated;
    float distance, volume, pricePaid, odometer, latitude, longitude;
    String location;
    Model_Fueling fd;
    String sql = DatabaseMap_Fueling.getSelectSQL(vehicleID);

    Model_Fueling.clearAll();

    Cursor cursor = db.rawQuery(sql, null);

    while (cursor.moveToNext()) {
      fuelingID = cursor.getInt(DatabaseMap_Fueling.COLUMN_NBR_FUELING_ID);
      vehicleID = cursor.getInt(DatabaseMap_Fueling.COLUMN_NBR_VEHICLE_ID);
      dateOfFill = new Date(cursor.getLong(DatabaseMap_Fueling.COLUMN_NBR_DATE_OF_FILL));
      distance = cursor.getFloat(DatabaseMap_Fueling.COLUMN_NBR_DISTANCE);
      volume = cursor.getFloat(DatabaseMap_Fueling.COLUMN_NBR_VOLUME);
      pricePaid = cursor.getFloat(DatabaseMap_Fueling.COLUMN_NBR_PRICE_PAID);
      odometer = cursor.getFloat(DatabaseMap_Fueling.COLUMN_NBR_ODOMETER);
      location = cursor.getString(DatabaseMap_Fueling.COLUMN_NBR_LOCATION);
      latitude = cursor.getFloat(DatabaseMap_Fueling.COLUMN_NBR_LATITUDE);
      longitude = cursor.getFloat(DatabaseMap_Fueling.COLUMN_NBR_LONGITUDE);
      lastUpdated = new Date(cursor.getLong(DatabaseMap_Fueling.COLUMN_NBR_LAST_UPDATED));

      fd = new Model_Fueling(fuelingID, vehicleID, dateOfFill, distance, volume, pricePaid,
          odometer, location, latitude, longitude, lastUpdated);

      fdList.add(fd);
    }

    cursor.close();

    return fdList;
  }

  /**
   * Inserts a new Model_Fueling record into the database. After a successful insert, it updates
   * the instance with the unique ID assigned to its database record, and sets the object's
   * state to CURRENT.
   *
   * @param fd the object instance to be inserted
   * @return the unique ID of the new record
   * @throws IllegalArgumentException if the object's state is not NEW
   */
  public int insertFueling(Model_Fueling fd) throws IllegalArgumentException {
    if (!fd.isNew())
      throw new IllegalArgumentException("Cannot insert a Model_Fueling record if its state is not NEW");

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues cv = new ContentValues();
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_VEHICLE_ID, fd.getVehicleID());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_DATE_OF_FILL, fd.getDateOfFill().getTime());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_DISTANCE, fd.getDistance());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_VOLUME, fd.getVolume());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_PRICE_PAID, fd.getPricePaid());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_ODOMETER, fd.getOdometer());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_LOCATION, fd.getLocation());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_LATITUDE, fd.getLatitude());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_LONGITUDE, fd.getLongitude());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_LAST_UPDATED, fd.getLastUpdated().getTime());

    int newID = (int) db.insert(DatabaseMap_Fueling.TABLE_NAME, null, cv);

    if (newID > 0) {
      fd.setFuelingID(newID);
      fd.setCurrent();
    }

    return newID;
  }

  /**
   * Deletes the database row with the ID (prime key) of the Model_Fueling instance.
   *
   * @param fd the instance to be deleted
   * @return true if the number of rows deleted is exactly 1
   * @throws IllegalArgumentException if the instance's state is not set to DELETED
   */
  public boolean deleteFueling(Model_Fueling fd) throws IllegalArgumentException {
    if (!fd.isDeleted())
      throw new IllegalArgumentException("Cannot delete an object if it's state is not DELETED");

    SQLiteDatabase db = this.getWritableDatabase();
    String[] whereArgs = new String[]{String.valueOf(fd.getID())};

    int rowsDeleted = db.delete(DatabaseMap_Fueling.TABLE_NAME, DatabaseMap_Fueling.WHERE_ID_CLAUSE, whereArgs);

    return (rowsDeleted == 1);
  }

  /**
   * Updates a Model_Fueling record in the database with new data, and resets the instance's
   * status to CURRENT. If the update fails (meaning the number of rows updated is not 1)
   * the status is not changed, and false is returned.
   *
   * @param fd the instance whose record is to be updated
   * @return true, if exactly 1 row is updated
   * @throws IllegalArgumentException if the instance's state is not set to UPDATED
   */
  public boolean updateFueling(Model_Fueling fd) throws IllegalArgumentException {
    if (!fd.isUpdated())
      throw new IllegalArgumentException("Cannot updated a database record if the object's state is not set to UPDATED");

    boolean result = true;
    SQLiteDatabase db = this.getWritableDatabase();
    String[] whereArgs = new String[]{String.valueOf(fd.getID())};

    ContentValues cv = new ContentValues();
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_VEHICLE_ID, fd.getVehicleID());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_DATE_OF_FILL, fd.getDateOfFill().getTime());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_DISTANCE, fd.getDistance());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_VOLUME, fd.getVolume());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_PRICE_PAID, fd.getPricePaid());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_ODOMETER, fd.getOdometer());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_LOCATION, fd.getLocation());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_LATITUDE, fd.getLatitude());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_LONGITUDE, fd.getLongitude());
    cv.put(DatabaseMap_Fueling.COLUMN_NAME_LAST_UPDATED, fd.getLastUpdated().getTime());

    int rowsUpdated = db.update(DatabaseMap_Fueling.TABLE_NAME, cv, DatabaseMap_Fueling.WHERE_ID_CLAUSE, whereArgs);

    if (rowsUpdated == 1)
      fd.setCurrent();
    else
      result = false;

    return result;
  }

  /**
   * For each Model_Fueling instance in the provided list, it is passed through to
   * updateFueling(Model_Fueling). The number of updated rows is returned; the calling
   * method can compare this to the number of items in the list as a check on total success.
   *
   * @param fdList an array of Model_Fueling objects to be updated in the database
   * @return the number of successfully updated rows
   */
  public int updateFueling(ArrayList<Model_Fueling> fdList) {
    int result = 0;

    for (Model_Fueling each : fdList) {
      if (!updateFueling(each))
        result++;
    }

    return result;
  }




  /*
  **
  ** Following are methods for upgrading database schemas
  **
   */


  /**
   * To get from 2 to 3, we add the column "status" to the table "vehicle". Fill the column with
   * "A" (meaning "active") for all existing rows.
   */
  private void upgradeToVersion3(SQLiteDatabase db) {
    String sqlAlter = "ALTER TABLE " + DatabaseMap_Vehicle.TABLE_NAME + " ADD COLUMN " +
        DatabaseMap_Vehicle.COLUMN_NAME_STATUS + " " + DatabaseMap_Base.STRING_TYPE;
    String sqlUpdate = "UPDATE " + DatabaseMap_Vehicle.TABLE_NAME + " SET " + DatabaseMap_Vehicle
        .COLUMN_NAME_STATUS + " = '" + Model_Vehicle.STATUS_ACTIVE + "'";

    db.beginTransaction();
    try {
      db.execSQL(sqlAlter);
      db.execSQL(sqlUpdate);
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
  }
}
