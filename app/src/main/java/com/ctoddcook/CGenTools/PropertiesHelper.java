/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.CGenTools;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ctoddcook.auto_log.FuelingDataMap;

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

  private static HashMap<String, Property> sPropertyMap = new HashMap<>(10);
  private static PropertiesHelper sPh;
  private static SQLiteDatabase sDb;


  /**
   * Returns a reference to the single PropertiesHelper instance. If the instance does not
   * already exist, it will be instantiated, and all Properties will be read from the database
   * and added to the HashMap.
   * @param db a reference to an existing SQLiteOpenHelper so we can execute SQL
   * @return a reference to the single PropertiesHelper instance
   * @throws ParseException if a downstream method throws it
   */
  public static PropertiesHelper getInstance(SQLiteOpenHelper db) throws ParseException {
    if (sPh == null) {
      sPh = new PropertiesHelper();
      sDb = db.getWritableDatabase();
      sPh.fetchProperties();
    }

    return sPh;
  }

  /**
   * Adds a Property instance to the HashMap.
   * @param pNew the Property to be added
   */
  public void put(Property pNew) {
    if (pNew == null)
      throw new IllegalArgumentException("Property instance may not be null");

    Property pExisting = sPropertyMap.get(pNew.getName());
    if (pExisting != null)
      pExisting.update(pNew);
    else
      sPropertyMap.put(pNew.getName(), pNew);
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
  public int getIntValue(String forName) {
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
    Property p = sPropertyMap.get(forName);
    if (p == null)
      throw new NoSuchElementException("No property found for name: " + forName);

    return p.getDateTimeValue();
  }

  /**
   * Retrieves all Property rows from the database. Each is added to the HashMap for quick
   * retrieval.
   * @throws ParseException if thrown by a downstream method call
   */
  private void fetchProperties() throws ParseException {
    if (!PropertyDataMap.tableExists(sDb))
      sDb.execSQL(PropertyDataMap.SQL_CREATE_TABLE);

    int id, type;
    String name, value;
    Date lastUpdated = new Date();
    Property p;

    String sql = PropertyDataMap.SQL_SELECT_ALL;

    Cursor cursor = sDb.rawQuery(sql, null);

    while (cursor.moveToNext()) {
      id = cursor.getInt(PropertyDataMap.COLUMN_NBR_ID);
      name = cursor.getString(PropertyDataMap.COLUMN_NBR_NAME);
      type = cursor.getInt(PropertyDataMap.COLUMN_NBR_TYPE);
      value = cursor.getString(PropertyDataMap.COLUMN_NBR_VALUE);
      lastUpdated.setTime(cursor.getInt(PropertyDataMap.COLUMN_NBR_LAST_UPDATED));

      p = new Property(id, name, type, value, lastUpdated);

      sPropertyMap.put(p.getName(), p);
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

    int rowsUpdated = sDb.update(PropertyDataMap.TABLE_NAME, cv, PropertyDataMap.WHERE_ID_CLAUSE,
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

    int newID = (int) sDb.insert(PropertyDataMap.TABLE_NAME, null, cv);

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

    int rowsDeleted = sDb.delete(FuelingDataMap.TABLE_NAME, FuelingDataMap.WHERE_ID_CLAUSE,
        whereArgs);

    return (rowsDeleted == 1);
  }
}
