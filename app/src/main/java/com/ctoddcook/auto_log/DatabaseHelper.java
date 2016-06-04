package com.ctoddcook.auto_log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

// todo implement singleton pattern with synchronized getInstance(Context) method

/**
 * Created by C. Todd Cook on 4/17/2016.
 * ctodd@ctoddcook.com
 */
public class DatabaseHelper extends SQLiteOpenHelper {

  private static final String TAG = "DatabaseHelper";
  private static DatabaseHelper sInstance;
  public static final int DATABASE_VERSION = 2;


  private DatabaseHelper(Context context) {
    super(context, AutoLogDataMap.DATABASE_NAME, null, DATABASE_VERSION);
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
    if (!FuelingDBMap.tableExists(db)) {
      db.execSQL(FuelingDBMap.SQL_CREATE_TABLE);
    }

    if (!VehicleDBMap.tableExists(db)) {
      db.execSQL(VehicleDBMap.SQL_CREATE_TABLE);
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
  public ArrayList<Vehicle> fetchVehicleList() {
    SQLiteDatabase db = this.getWritableDatabase();
    ArrayList<Vehicle> vehicleList = new ArrayList<>();
    int id, year;
    String name, color, model, vin, licensePlate, status;
    Date lastUpdated;
    Vehicle vehicle;

    Vehicle.clearAll();

    Cursor cursor = db.rawQuery(VehicleDBMap.SQL_SELECT_ALL, null);

    while (cursor.moveToNext()) {
      id = cursor.getInt(VehicleDBMap.COLUMN_NBR_ID);
      name = cursor.getString(VehicleDBMap.COLUMN_NBR_NAME);
      year = cursor.getInt(VehicleDBMap.COLUMN_NBR_YEAR);
      color = cursor.getString(VehicleDBMap.COLUMN_NBR_COLOR);
      model = cursor.getString(VehicleDBMap.COLUMN_NBR_MODEL);
      vin = cursor.getString(VehicleDBMap.COLUMN_NBR_VIN);
      licensePlate = cursor.getString(VehicleDBMap.COLUMN_NBR_LICENSE_PLATE);
      status = cursor.getString(VehicleDBMap.COLUMN_NBR_STATUS);
      lastUpdated = new Date(cursor.getInt(VehicleDBMap.COLUMN_NBR_LAST_UPDATED));

      vehicle = new Vehicle(id, name, year, color, model, vin, licensePlate, status, lastUpdated);

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

    return db.rawQuery(VehicleDBMap.SQL_SELECT_SIMPLE, null);
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
  public int insertVehicle(Vehicle vehicle) throws IllegalArgumentException {
    if (!vehicle.isNew())
      throw new IllegalArgumentException("Cannot insert a new vehicle if its state is not new");

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues cv = new ContentValues();
    cv.put(VehicleDBMap.COLUMN_NAME_NAME, vehicle.getName());
    cv.put(VehicleDBMap.COLUMN_NAME_YEAR, vehicle.getYear());
    cv.put(VehicleDBMap.COLUMN_NAME_COLOR, vehicle.getColor());
    cv.put(VehicleDBMap.COLUMN_NAME_MODEL, vehicle.getModel());
    cv.put(VehicleDBMap.COLUMN_NAME_VIN, vehicle.getVIN());
    cv.put(VehicleDBMap.COLUMN_NAME_LICENSE_PLATE, vehicle.getLicensePlate());
    cv.put(VehicleDBMap.COLUMN_NAME_STATUS, vehicle.getVehicleStatus());
    cv.put(VehicleDBMap.COLUMN_NAME_LAST_UPDATED, vehicle.getLastUpdated().getTime());

    int newID = (int) db.insert(VehicleDBMap.TABLE_NAME, null, cv);

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
  public boolean deleteVehicle(Vehicle vehicle) throws IllegalArgumentException {
    if (!vehicle.isDeleted())
      throw new IllegalArgumentException("Cannot delete a vehicle whose state is not DELETED");

    SQLiteDatabase db = this.getWritableDatabase();
    String[] whereArgs = new String[]{String.valueOf(vehicle.getID())};

    int rowsDeleted = db.delete(VehicleDBMap.TABLE_NAME, VehicleDBMap.WHERE_ID_CLAUSE, whereArgs);

    return (rowsDeleted == 1);
  }

  /**
   * Updates the database with data from the provided Vehicle instance
   *
   * @param vehicle the vehicle whose data is to be updated
   * @return true, if exactly 1 row is updated
   * @throws IllegalArgumentException if the vehicle's state is not set to UPDATED
   */
  public boolean updateVehicle(Vehicle vehicle) throws IllegalArgumentException {
    if (!vehicle.isUpdated())
      throw new IllegalArgumentException("Cannot update a vehicle record whose state is not UPDATED");

    boolean result = true;
    SQLiteDatabase db = this.getWritableDatabase();
    String[] whereArgs = new String[]{String.valueOf(vehicle.getID())};

    ContentValues cv = new ContentValues();
    cv.put(VehicleDBMap.COLUMN_NAME_NAME, vehicle.getName());
    cv.put(VehicleDBMap.COLUMN_NAME_YEAR, vehicle.getYear());
    cv.put(VehicleDBMap.COLUMN_NAME_COLOR, vehicle.getColor());
    cv.put(VehicleDBMap.COLUMN_NAME_MODEL, vehicle.getModel());
    cv.put(VehicleDBMap.COLUMN_NAME_VIN, vehicle.getVIN());
    cv.put(VehicleDBMap.COLUMN_NAME_LICENSE_PLATE, vehicle.getLicensePlate());
    cv.put(VehicleDBMap.COLUMN_NAME_STATUS, vehicle.getVehicleStatus());
    cv.put(VehicleDBMap.COLUMN_NAME_LAST_UPDATED, vehicle.getLastUpdated().getTime());

    int rowsUpdated = db.update(VehicleDBMap.TABLE_NAME, cv, VehicleDBMap.WHERE_ID_CLAUSE, whereArgs);
    if (rowsUpdated == 1)
      vehicle.setCurrent();
    else
      result = false;

    return result;
  }

  /**
   * For each Vehicle instance in the list, the object is passed through to
   * updateVehicle(Vehicle). The number of updated rows is returned; the calling
   * method can compare this to the number of items in the list as a check on total success.
   *
   * @param vList an array of Vehicle objects to be updated in the database
   * @return the number of successfully updated rows
   */
  public int updateVehicles(ArrayList<Vehicle> vList) {
    int result = 0;

    for (Vehicle each : vList) {
      if (updateVehicle(each))
        result++;
    }

    return result;
  }





    /*
    FOLLOWING ARE METHODS FOR UPDATING/INSERTING/SELECTING/DELETING FUELINGDATA
     */


  /**
   * Retrieves the complete list of Fueling records from the database, in descending
   * order by DateOfFill (i.e., the newest records are fetched first).
   *
   * @return an ArrayList of Fueling objects
   */
  public ArrayList<Fueling> fetchFuelingData(int vehicleID) {
    SQLiteDatabase db = this.getWritableDatabase();
    ArrayList<Fueling> fdList = new ArrayList<>();
    int fuelingID;
    Date dateOfFill;
    Date lastUpdated;
    float distance, volume, pricePaid, odometer, latitude, longitude;
    String location;
    Fueling fd;
    String sql = FuelingDBMap.getSelectSQL(vehicleID);

    Fueling.clearAll();

    Cursor cursor = db.rawQuery(sql, null);

    while (cursor.moveToNext()) {
      fuelingID = cursor.getInt(FuelingDBMap.COLUMN_NBR_FUELING_ID);
      vehicleID = cursor.getInt(FuelingDBMap.COLUMN_NBR_VEHICLE_ID);
      dateOfFill = new Date(cursor.getLong(FuelingDBMap.COLUMN_NBR_DATE_OF_FILL));
      distance = cursor.getFloat(FuelingDBMap.COLUMN_NBR_DISTANCE);
      volume = cursor.getFloat(FuelingDBMap.COLUMN_NBR_VOLUME);
      pricePaid = cursor.getFloat(FuelingDBMap.COLUMN_NBR_PRICE_PAID);
      odometer = cursor.getFloat(FuelingDBMap.COLUMN_NBR_ODOMETER);
      location = cursor.getString(FuelingDBMap.COLUMN_NBR_LOCATION);
      latitude = cursor.getFloat(FuelingDBMap.COLUMN_NBR_LATITUDE);
      longitude = cursor.getFloat(FuelingDBMap.COLUMN_NBR_LONGITUDE);
      lastUpdated = new Date(cursor.getLong(FuelingDBMap.COLUMN_NBR_LAST_UPDATED));

      fd = new Fueling(fuelingID, vehicleID, dateOfFill, distance, volume, pricePaid,
          odometer, location, latitude, longitude, lastUpdated);

      fdList.add(fd);
    }

    cursor.close();

    return fdList;
  }

  /**
   * Inserts a new Fueling record into the database. After a successful insert, it updates
   * the instance with the unique ID assigned to its database record, and sets the object's
   * state to CURRENT.
   *
   * @param fd the object instance to be inserted
   * @return the unique ID of the new record
   * @throws IllegalArgumentException if the object's state is not NEW
   */
  public int insertFueling(Fueling fd) throws IllegalArgumentException {
    if (!fd.isNew())
      throw new IllegalArgumentException("Cannot insert a Fueling record if its state is not NEW");

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues cv = new ContentValues();
    cv.put(FuelingDBMap.COLUMN_NAME_VEHICLE_ID, fd.getVehicleID());
    cv.put(FuelingDBMap.COLUMN_NAME_DATE_OF_FILL, fd.getDateOfFill().getTime());
    cv.put(FuelingDBMap.COLUMN_NAME_DISTANCE, fd.getDistance());
    cv.put(FuelingDBMap.COLUMN_NAME_VOLUME, fd.getVolume());
    cv.put(FuelingDBMap.COLUMN_NAME_PRICE_PAID, fd.getPricePaid());
    cv.put(FuelingDBMap.COLUMN_NAME_ODOMETER, fd.getOdometer());
    cv.put(FuelingDBMap.COLUMN_NAME_LOCATION, fd.getLocation());
    cv.put(FuelingDBMap.COLUMN_NAME_LATITUDE, fd.getLatitude());
    cv.put(FuelingDBMap.COLUMN_NAME_LONGITUDE, fd.getLongitude());
    cv.put(FuelingDBMap.COLUMN_NAME_LAST_UPDATED, fd.getLastUpdated().getTime());

    int newID = (int) db.insert(FuelingDBMap.TABLE_NAME, null, cv);

    if (newID > 0) {
      fd.setFuelingID(newID);
      fd.setCurrent();
    }

    return newID;
  }

  /**
   * Deletes the database row with the ID (prime key) of the Fueling instance.
   *
   * @param fd the instance to be deleted
   * @return true if the number of rows deleted is exactly 1
   * @throws IllegalArgumentException if the instance's state is not set to DELETED
   */
  public boolean deleteFueling(Fueling fd) throws IllegalArgumentException {
    if (!fd.isDeleted())
      throw new IllegalArgumentException("Cannot delete an object if it's state is not DELETED");

    SQLiteDatabase db = this.getWritableDatabase();
    String[] whereArgs = new String[]{String.valueOf(fd.getID())};

    int rowsDeleted = db.delete(FuelingDBMap.TABLE_NAME, FuelingDBMap.WHERE_ID_CLAUSE, whereArgs);

    return (rowsDeleted == 1);
  }

  /**
   * Updates a Fueling record in the database with new data, and resets the instance's
   * status to CURRENT. If the update fails (meaning the number of rows updated is not 1)
   * the status is not changed, and false is returned.
   *
   * @param fd the instance whose record is to be updated
   * @return true, if exactly 1 row is updated
   * @throws IllegalArgumentException if the instance's state is not set to UPDATED
   */
  public boolean updateFueling(Fueling fd) throws IllegalArgumentException {
    if (!fd.isUpdated())
      throw new IllegalArgumentException("Cannot updated a database record if the object's state is not set to UPDATED");

    boolean result = true;
    SQLiteDatabase db = this.getWritableDatabase();
    String[] whereArgs = new String[]{String.valueOf(fd.getID())};

    ContentValues cv = new ContentValues();
    cv.put(FuelingDBMap.COLUMN_NAME_VEHICLE_ID, fd.getVehicleID());
    cv.put(FuelingDBMap.COLUMN_NAME_DATE_OF_FILL, fd.getDateOfFill().getTime());
    cv.put(FuelingDBMap.COLUMN_NAME_DISTANCE, fd.getDistance());
    cv.put(FuelingDBMap.COLUMN_NAME_VOLUME, fd.getVolume());
    cv.put(FuelingDBMap.COLUMN_NAME_PRICE_PAID, fd.getPricePaid());
    cv.put(FuelingDBMap.COLUMN_NAME_ODOMETER, fd.getOdometer());
    cv.put(FuelingDBMap.COLUMN_NAME_LOCATION, fd.getLocation());
    cv.put(FuelingDBMap.COLUMN_NAME_LATITUDE, fd.getLatitude());
    cv.put(FuelingDBMap.COLUMN_NAME_LONGITUDE, fd.getLongitude());
    cv.put(FuelingDBMap.COLUMN_NAME_LAST_UPDATED, fd.getLastUpdated().getTime());

    int rowsUpdated = db.update(FuelingDBMap.TABLE_NAME, cv, FuelingDBMap.WHERE_ID_CLAUSE, whereArgs);

    if (rowsUpdated == 1)
      fd.setCurrent();
    else
      result = false;

    return result;
  }

  /**
   * For each Fueling instance in the provided list, it is passed through to
   * updateFueling(Fueling). The number of updated rows is returned; the calling
   * method can compare this to the number of items in the list as a check on total success.
   *
   * @param fdList an array of Fueling objects to be updated in the database
   * @return the number of successfully updated rows
   */
  public int updateFueling(ArrayList<Fueling> fdList) {
    int result = 0;

    for (Fueling each : fdList) {
      if (!updateFueling(each))
        result++;
    }

    return result;
  }
}
