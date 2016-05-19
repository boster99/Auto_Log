/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.CGenTools;

import android.util.Log;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * A lightweight class for managing various properties for an application. Four data types are
 * supported: int, double, String and Date, though all of them are store as strings in the database.
 * <p>
 * The "name" of a property is immutable. You can change the value, or even the type of the
 * value, but if you want to change the name you'll need to delete the old property and create
 * a new one with the new name.
 * <p>Created by C. Todd Cook on 5/18/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Property extends DataHolder {
  private static final String TAG = "Property";

  public static final int TYPE_INT = 0;
  public static final int TYPE_DOUBLE = 1;
  public static final int TYPE_STRING = 2;
  public static final int TYPE_DATETIME = 3;

  private int mId = -1;
  private String mName;
  private int mType = -1;
  private String mStringVal;
  private int mIntVal;
  private double mDoubleVal;
  private Date mDateTimeVal;

  private static DateFormat sDateFormat;
  private static NumberFormat sNumberFormat;

  private Property() {
    // Nothing. Not allowed.
  }

  /**
   * Constructor providing a property name and String value
   * @param name the name of the property
   * @param value the value
   */
  public Property(String name, String value) {
    super();
    setName(name);
    setValue(value);
  }

  /**
   * Constructor providing a property name and an int value
   * @param name the name of the property
   * @param value the value
   */
  public Property(String name, int value) {
    super();
    setName(name);
    setValue(value);
  }

  /**
   * Constructor providing a property name and a double value
   * @param name the name of the property
   * @param value the value
   */
  public Property(String name, double value) {
    super();
    setName(name);
    setValue(value);
  }

  /**
   * Constructor providing a property name and a Date value
   * @param name the name of the property
   * @param value the value
   */
  public Property(String name, Date value) {
    super();
    setName(name);
    setValue(value);
  }

  /**
   * Constructor providing a database id, property name, data type and a String value. Based on
   * the provided data type, the String value is parsed into its native form. This constructor
   * most-likely should only be used when building a Property object from a database fetch.
   * @param id the unique database id of the property
   * @param name the name of the property
   * @param type the data type of the property
   * @param value the value of the property
   * @param lastUpdated the date the row was last updated
   * @throws IllegalArgumentException if the provided id is less than 1, or if the indicated data
   * type is not supported
   * @throws ParseException if a String representation of a Date cannot be parsed into an actual
   * Date object
   */
  public Property(int id, String name, int type, String value, Date lastUpdated) throws
      IllegalArgumentException, ParseException {
    super();
    if (id < 1)
      throw new IllegalArgumentException("mId value must be at least 1");

    setName(name);

    switch (mType) {
      case TYPE_DATETIME:
        try {
          setValue(getDateFormat().parse(value));
        } catch (ParseException ex) {
          Log.e(TAG, "Property(id,name,type,value): Provided Date value for property '" + mName +
              "' does not parse. Value provided is: " + value);
          throw ex;
        }
        break;
      case TYPE_DOUBLE:
        setValue(Double.parseDouble(value));
        break;
      case TYPE_INT:
        setValue(Integer.parseInt(value));
        break;
      case TYPE_STRING:
        setValue(value);
        break;
      default:
        throw new IllegalArgumentException("Provided type is illegal. Type is: " + type);
    }

    this.mLastUpdated = lastUpdated;

    setCurrent();
  }

  /**
   * Accessor to set the String value of this property.
   * @param value the String value
   */
  public void setValue(String value) {
    mType = TYPE_STRING;

    if (value == null || value.trim().isEmpty())
      throw new IllegalArgumentException("String value provided is " +
          (value==null?"null":"empty"));

    mStringVal = value.trim();
    touch();
  }

  /**
   * Accessor to set the int value of this property.
   * @param value the int value
   */
  public void setValue(int value) {
    mType = TYPE_INT;
    mIntVal = value;
    touch();
  }

  /**
   * Accessor to set the double value of this property.
   * @param value the double value
   */
  public void setValue(double value) {
    mType = TYPE_DOUBLE;
    mDoubleVal = value;
    touch();
  }

  /**
   * Accessor to set the Date value of this property.
   * @param value the Date value
   */
  public void setValue(Date value) {
    mType = TYPE_DATETIME;

    if (value == null)
      throw new IllegalArgumentException("Date value provided is null");

    mDateTimeVal = value;
    touch();
  }

  /**
   * Private accessor sets the name of this property, after some quick sanity checking.
   * @param name the name of this property
   * @throws IllegalArgumentException if the name is null or empty
   */
  private void setName(String name) throws IllegalArgumentException {
    if (name == null || name.length() < 1)
      throw new IllegalArgumentException("name provided must not be null and must not be empty");

    mName = name.trim().toLowerCase();
  }

  /**
   * Accessor to the database ID of the property
   * @return the database id
   */
  public int getID() {
    return mId;
  }

  /**
   * Accessor to the name of the property
   * @return the name
   */
  public String getName() {
    return mName;
  }

  /**
   * Accessor to the type of the property
   * @return the type
   */
  public int getType() {
    return mType;
  }

  /**
   * Accessor to the String value of the property
   * @return the String value
   */
  public String getStringValue() {
    return mStringVal;
  }

  /**
   * Accessor to the int value of the property
   * @return the int value
   */
  public int getIntValue() {
    return mIntVal;
  }

  /**
   * Accessor to the double value of the property
   * @return the double value
   */
  public double getDoubleValue() {
    return mDoubleVal;
  }

  /**
   * Accessor to the Date value of the property
   * @return the Date value
   */
  public Date getDateTimeValue() {
    return mDateTimeVal;
  }

  /**
   * Provides the property's value, regardless of its native type, formatted as a String.
   * @return the value as a String
   */
  public String getValueAsString() {
    switch (mType) {
      case TYPE_DATETIME:
        return getDateFormat().format(mDateTimeVal);
      case TYPE_DOUBLE:
        return Double.toString(mDoubleVal);
      case TYPE_INT:
        return Integer.toString(mIntVal);
      case TYPE_STRING:
        return mStringVal;
    }

    return null;  // should never happen, but the compiler wants a final return statement.
  }


  /**
   * Determines whether a provided name is a match for this property's name. Can be used to
   * ensure uniqueness.
   * @param other the other name to compare
   * @return true if the two are a match
   */
  public boolean nameEquals(String other) {
    return other != null && mName.equalsIgnoreCase(other.trim());
  }

  /**
   * Standard method checks for equality of all pertinent members of this and another instance.
   * Note it does not actually check <strong>all</strong> members; based on the data type
   * indicated by this property, it checks the related values and ignores the others. (For
   * example, if the data type is TYPE_INT, the two instances' int values will be compared, but
   * their double, Date and String values will be ignored.)
   * @param other the other Property to compare
   * @return true if all pertinent fields match
   */
  public boolean equals(Property other) {
    if (other == null)
      return false;

    if (this.mId != other.mId) return false;
    if (!this.mName.equalsIgnoreCase(other.mName.trim())) return false;
    if (this.mType != other.mType) return false;

    switch(this.mType) {
      case TYPE_DATETIME:
        if (!this.mDateTimeVal.equals(other.mDateTimeVal)) return false;
        break;
      case TYPE_DOUBLE:
        if (this.mDoubleVal != other.mDoubleVal) return false;
        break;
      case TYPE_INT:
        if (this.mIntVal != other.mIntVal) return false;
        break;
      case TYPE_STRING:
        if (!this.mStringVal.equals(other.mStringVal)) return false;
    }

    return true;
  }

  /**
   * Private accessor exists to ensure the static sDateFormat member has been setup, so calling
   * methods can avoid a NullPointerException.
   * @return the static DateFormat object
   */
  private DateFormat getDateFormat() {
    if (sDateFormat == null)
      sDateFormat = DateFormat.getDateTimeInstance();

    return sDateFormat;
  }

  /**
   * Upates this Property based on the values of another Property.
   * @param other the Property with the new values to use
   */
  public void update(Property other) {
    this.mType = other.mType;
    this.mStringVal = other.mStringVal;
    this.mIntVal = other.mIntVal;
    this.mDoubleVal = other.mDoubleVal;
    this.mDateTimeVal = other.mDateTimeVal;
    touch();
  }
}
