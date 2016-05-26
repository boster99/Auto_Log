/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.CGenTools;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ctoddcook.auto_log.FuelingDBMap;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This is a singleton class which maintains a list of name/value properties which are saved to
 * and read from the database.
 * <p>Created by C. Todd Cook on 5/18/2016.<br>
 * ctodd@ctoddcook.com
 */
public class PropertiesHelper {

  private static final String TAG = "PropertiesHelper";

  private static HashMap<String, Property> sPropertyMap = new HashMap<>(10);
  private static PropertiesHelper sInstance;
  private static SQLiteDatabase sDB;


  /**
   * Returns a reference to the single PropertiesHelper instance. If the instance does not
   * already exist, it will be instantiated, and all Properties will be read from the database
   * and added to the HashMap.
   * @return a reference to the single PropertiesHelper instance
   * @throws UnsupportedOperationException if setDatabaseHelper() has not already been called
   * @see #setDatabaseHelper(SQLiteOpenHelper)
   */
  public static synchronized PropertiesHelper getInstance() {
    // Make sure a database helper has already been provided
    if (sDB == null)
      throw new UnsupportedOperationException("Whoa. setDatabaseHelper() must be called before " +
          "this method is called.");

    if (sInstance == null) {
      sInstance = new PropertiesHelper();
      sInstance.fetchProperties();
    }

    return sInstance;
  }

  /**
   * Sets the database helper this object will use to issue SQL commands. Must be called before
   * getInstance().
   * @param db the database helper to use
   * @see #getInstance()
   */
  public static synchronized void setDatabaseHelper(SQLiteOpenHelper db) {
    if (db == null)
      throw new IllegalArgumentException("Provided SQLiteOpenHelper argument is null. Bad.");

    sDB = db.getWritableDatabase();
  }

  /**
   * Adds a Property instance to the HashMap.
   * @param pNew the Property to be added
   */
  public void put(Property pNew) {
    if (pNew == null)
      throw new IllegalArgumentException("Property instance may not be null");

    Property pExisting = sPropertyMap.get(pNew.getName());
    if (pExisting == null) {
      sPropertyMap.put(pNew.getName(), pNew);
      insertProperty(pNew);
    } else if (!pExisting.equals(pNew)) {
      pExisting.update(pNew);
      updateProperty(pExisting);
    }
  }

  /**
   * Returns all of the Properties held in memory.
   * @return all Properties
   */
  public ArrayList<Property> getListOfAll() {
    Iterator iterator = sPropertyMap.keySet().iterator();
    ArrayList<Property> list = new ArrayList<>(sPropertyMap.size());
    String key;

    while(iterator.hasNext()) {
      key = (String) iterator.next();
      list.add(sPropertyMap.get(key));
    }

    return list;
  }

  /**
   * Returns the String value for a property
   * @param forName the name of the desired property
   * @return the String value
   */
  public String getStringValue(String forName) {
    forName = forName.trim().toLowerCase();
    Property p = sPropertyMap.get(forName);
    if (p == null)
      throw new NoSuchElementException("No property found for name: " + forName);

    return p.getStringValue();
  }

  /**
   * Returns the double value for a property
   * @param forName the name of the desired property
   * @return the double value
   */
  public double getDoubleValue(String forName) {
    forName = forName.trim().toLowerCase();
    Property p = sPropertyMap.get(forName);
    if (p == null)
      throw new NoSuchElementException("No property found for name: " + forName);

    return p.getDoubleValue();
  }

  /**
   * Returns the int value for a property
   * @param forName the name of the desired property
   * @return the int value
   */
  public long getLongValue(String forName) {
    forName = forName.trim().toLowerCase();
    Property p = sPropertyMap.get(forName);
    if (p == null)
      throw new NoSuchElementException("No property found for name: " + forName);

    return p.getIntValue();
  }

  /**
   * Returns the Date value of a property
   * @param forName the name of the desired property
   * @return the date value
   */
  public Date getDateValue(String forName) {
    forName = forName.trim().toLowerCase();
    Property p = sPropertyMap.get(forName);
    if (p == null)
      throw new NoSuchElementException("No property found for name: " + forName);

    return p.getDateTimeValue();
  }

  /**
   * Simply checks for the existence of a Property with the provied name.
   * @param name The name to check
   * @return true if the Property exists, false otherwise
   */
  public boolean doesNameExist(String name) {
    name = name.trim().toLowerCase();
    Property p = sPropertyMap.get(name);
    return p != null;
  }

  /**
   * Retrieves all Property rows from the database. Each is added to the HashMap for quick
   * retrieval.
   */
  private void fetchProperties() {
    if (!PropertyDataMap.tableExists(sDB))
      sDB.execSQL(PropertyDataMap.SQL_CREATE_TABLE);

    int id, type;
    String name, value;
    Date lastUpdated = new Date();
    Property p;

    String sql = PropertyDataMap.SQL_SELECT_ALL;

    Cursor cursor = sDB.rawQuery(sql, null);

    while (cursor.moveToNext()) {
      id = cursor.getInt(PropertyDataMap.COLUMN_NBR_ID);
      name = cursor.getString(PropertyDataMap.COLUMN_NBR_NAME);
      type = cursor.getInt(PropertyDataMap.COLUMN_NBR_TYPE);
      value = cursor.getString(PropertyDataMap.COLUMN_NBR_VALUE);
      lastUpdated.setTime(cursor.getInt(PropertyDataMap.COLUMN_NBR_LAST_UPDATED));

      try {
        p = new Property(id, name, type, value, lastUpdated);
        sPropertyMap.put(p.getName(), p);
      } catch (ParseException ex) {
        Log.e(TAG, "fetchProperties: Failed to fetch properties due to data error", ex);
      }
    }

    cursor.close();
  }

  /**
   * Iterates through all Properties. For each that is new, it is inserted into the database and
   * for each that is changed, it is updated in the database.
   */
  private void updateProperties() {
    Iterator iterator = sPropertyMap.keySet().iterator();
    String key;
    Property p;

    while(iterator.hasNext()) {
      key = (String) iterator.next();
      p = sPropertyMap.get(key);

      if (p.isNew())
        insertProperty(p);
      else if (p.isUpdated())
        updateProperty(p);
    }
  }

  /**
   * Updates the database with changes for the provided Property.
   * @param p the Property to be updated in the database
   * @return true if the update succeeds (meaning it updates exactly 1 row)
   */
  private boolean updateProperty(Property p) {
    if (!p.isUpdated())
      throw new IllegalArgumentException("Cannot insert Property if its state is not UPDATED");

    boolean result = true;
    String[] whereArgs = new String[]{String.valueOf(p.getID())};

    ContentValues cv = new ContentValues();
    cv.put(PropertyDataMap.COLUMN_NAME_NAME, p.getName());
    cv.put(PropertyDataMap.COLUMN_NAME_TYPE, p.getType());
    cv.put(PropertyDataMap.COLUMN_NAME_VALUE, p.getValueAsString());
    cv.put(PropertyDataMap.COLUMN_NAME_LAST_UPDATED, p.getLastUpdated().getTime());

    int rowsUpdated = sDB.update(PropertyDataMap.TABLE_NAME, cv, PropertyDataMap.WHERE_ID_CLAUSE,
        whereArgs);

    if (rowsUpdated == 1)
      p.setCurrent();
    else
      result = false;

    return result;
  }

  /**
   * Inserts a new Property into the database.
   * @param p the Property to be inserted
   * @throws IllegalArgumentException if the object's state is not NEW
   */
  private void insertProperty(Property p) throws IllegalArgumentException {
    if (!p.isNew())
      throw new IllegalArgumentException("Cannot insert Property if its state is not NEW");

    ContentValues cv = new ContentValues();

    cv.put(PropertyDataMap.COLUMN_NAME_NAME, p.getName());
    cv.put(PropertyDataMap.COLUMN_NAME_TYPE, p.getType());
    cv.put(PropertyDataMap.COLUMN_NAME_VALUE, p.getValueAsString());
    cv.put(PropertyDataMap.COLUMN_NAME_LAST_UPDATED, p.getLastUpdated().getTime());

    int newID = (int) sDB.insert(PropertyDataMap.TABLE_NAME, null, cv);

    if (newID > 0) {
      p.setID(newID);
      p.setCurrent();
    }
  }

  /**
   * Deletes the database row with the ID (prime key) of the Property instance.
   *
   * @param p the instance to be deleted
   * @return true if the number of rows deleted is exactly 1
   * @throws IllegalArgumentException if the instance's state is not set to DELETED
   */
  public boolean deleteProperty(Property p) throws IllegalArgumentException {
    if (!p.isDeleted())
      throw new IllegalArgumentException("Cannot delete an object if it's state is not DELETED");

    String[] whereArgs = new String[]{String.valueOf(p.getID())};

    int rowsDeleted = sDB.delete(FuelingDBMap.TABLE_NAME, FuelingDBMap.WHERE_ID_CLAUSE,
        whereArgs);

    return (rowsDeleted == 1);
  }
}
