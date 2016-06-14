/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.FuelLog;

import android.util.SparseArray;

import com.ctoddcook.CamGenTools.DataHolder;

import java.util.ArrayList;
import java.util.Date;

import static com.ctoddcook.CamGenTools.CTools.longToInt;

/**
 * Created by C. Todd Cook on 5/9/2016.
 * ctodd@ctoddcook.com
 * <p>
 * A fairly simple, straightforward data class. No calculations. Just store and present
 * data for different vehicles.
 */
public class Model_Vehicle extends DataHolder {
  public static final String DEFAULT_VEHICLE_KEY = "com.ctoddcook.FuelLog.DefaultVehicleID";
  public static final String STATUS_ACTIVE="A";
  public static final String STATUS_RETIRED="R";

  private static final SparseArray<Model_Vehicle> sVehicleList = new SparseArray<>(40);

  private int mVehicleID;
  private String mName;
  private int mYear;
  private String mColor;
  private String mModel;
  private String mVIN;
  private String mLicensePlate;
  private String mStatus;   // either active (A) or retired (R)

  /**
   * Constructor. This version should be used when creating a new instance from the user
   * interface. It does not setup any fields directly, though DataHolder.mStatus defaults
   * to NEW. We default the status to Active.
   * <p>
   * Note: Creating a new, empty instance does not cause it to be added to the sVehicleList
   * SparseArray, as the user might cancel the operation, leaving an empty instance in the list.
   */
  public Model_Vehicle() {
    mStatus = STATUS_ACTIVE;
  }

  /**
   * This constructor should be used when retrieving records from the database.
   *
   * @param id           the vehicle id, generated automatically by the database (prime key)
   * @param name         the user-provided mName of the record
   * @param year         the vehicle mYear
   * @param color        the vehicle mColor
   * @param model        the vehicle mModel
   * @param vin          the vehicle VIN
   * @param licensePlate the vehicle license plate
   * @param status       the status of the vehicle, either A or R
   * @param lastUpdated  the last time the record was updated
   */
  public Model_Vehicle(int id, String name, int year, String color, String model, String vin,
                       String licensePlate, String status, Date lastUpdated) {
    mVehicleID = id;
    mName = name;
    mYear = year;
    mColor = color;
    mModel = model;
    mVIN = vin;
    mLicensePlate = licensePlate;

    if (status.equals(STATUS_RETIRED))   // If status is anything but 'R' we default to Active
      setRetired();
    else
      setActive();

    mLastUpdated = lastUpdated;
    setCurrent();
    addVehicle(this);
  }

  /**
   * Returns the vehicle for the given ID. If no such vehicle is found in the static SparseArray
   * null is returned.
   *
   * @param id the ID of the desired Model_Vehicle instance
   * @return the associated Model_Vehicle instance, or null if there is none
   */
  public static Model_Vehicle getVehicle(int id) {
    return sVehicleList.get(id);
  }

  /**
   * Returns the vehicle for the given ID. If no such vehicle is found in the static SparseArray
   * null is returned.
   *
   * @param id the ID of the desired Model_Vehicle instance
   * @return the associated Model_Vehicle instance, or null if there is none
   */
  public static Model_Vehicle getVehicle(long id) {
    return getVehicle(longToInt(id));
  }

  /**
   * Adds an instance to the static list of Vehicles.
   *
   * @param v The vehicle to be added to the list
   */
  public static void addVehicle(Model_Vehicle v) {
    sVehicleList.put(v.getID(), v);
  }

  /**
   * Returns an iterable list of Vehicles.
   *
   * @return ArrayList of all of the Model_Vehicle instances
   */
  public static ArrayList<Model_Vehicle> getVehicleList() {
    ArrayList<Model_Vehicle> list = new ArrayList<>();
    Model_Vehicle v;
    for (int i = 0; i < sVehicleList.size(); i++) {
      v = sVehicleList.valueAt(i);
      list.add(v);
    }

    return list;
  }

  /**
   * Provides the number of vehicles stored.
   *
   * @return The count of vehicles
   */
  public static int getCount() {
    return sVehicleList.size();
  }

  /**
   * Clears the static in-memory list of vehicles. Should be called before re-fetching vehicles
   * from the database.
   */
  public static void clearAll() {
    if (sVehicleList != null)
      sVehicleList.clear();
  }

  /**
   * Getter for mVehicleID field.
   *
   * @return mVehicleID field value
   */
  public int getVehicleID() {
    return mVehicleID;
  }

  /**
   * Setter for mVehicleID field. Does not allow a change to an existing (non-zero) ID, and
   * does not allow setting the ID to a number less than 1.
   *
   * @param vehicleID New ID for the record
   * @throws UnsupportedOperationException If there is already an ID in place for this instance.
   * @throws IllegalArgumentException      If the incoming ID is less than 1
   */
  public void setVehicleID(int vehicleID) throws UnsupportedOperationException, IllegalArgumentException {
    if (mVehicleID != 0)
      throw new UnsupportedOperationException("The Row ID can only be set once, and cannot be updated");
    if (vehicleID < 1)
      throw new IllegalArgumentException("The Row ID cannot be less than 1");

    mVehicleID = vehicleID;
  }

  /**
   * Getter for record ID
   *
   * @return The database ID for the record
   */
  @Override
  public int getID() {
    return getVehicleID();
  }

  /**
   * Getter for mName field.
   *
   * @return value of mName field.
   */
  public String getName() {
    return mName;
  }

  /**
   * Setter for mName field.
   *
   * @param name The new value for the mName field
   */
  public void setName(String name) {
    mName = name;

    touch();
  }

  /**
   * Getter for mYear field
   *
   * @return value of mYear field
   */
  public int getYear() {
    return mYear;
  }

  /**
   * Setter for mYear field
   *
   * @param year new value for Year
   * @throws IllegalArgumentException if the mYear is outside 1900-2100
   */
  public void setYear(int year) throws IllegalArgumentException {
    if (year < 1900 || year > 2100)
      throw new IllegalArgumentException("The mYear must be between 1900 and 2100");
    mYear = year;
    touch();
  }

  /**
   * Getter for mColor
   *
   * @return the mColor of the car
   */
  public String getColor() {
    return mColor;
  }

  /**
   * Setter for mColor
   *
   * @param color of the car
   */
  public void setColor(String color) {
    mColor = color;

    touch();
  }

  /**
   * Getter for the car's mModel
   *
   * @return the car's mModel
   */
  public String getModel() {
    return mModel;
  }

  /**
   * Setter for the car's mModel
   *
   * @param model the car's mModel
   */
  public void setModel(String model) {
    mModel = model;

    touch();
  }

  /**
   * Getter for the car's VIN
   *
   * @return the car's VIN
   */
  public String getVIN() {
    return mVIN;
  }

  /**
   * Setter for the car's VIN
   *
   * @param VIN the car's VIN
   */
  public void setVIN(String VIN) {
    mVIN = VIN;

    touch();
  }

  /**
   * Getter for the car's license plate
   *
   * @return the car's license plate
   */
  public String getLicensePlate() {
    return mLicensePlate;
  }

  /**
   * Setter for license plate
   *
   * @param licensePlate the new license plate value
   */
  public void setLicensePlate(String licensePlate) {
    mLicensePlate = licensePlate;

    touch();
  }

  /**
   * Getter for the car's status.
   * @return A if the vehicle is Active, R if it is Retired.
   */
  public String getVehicleStatus() {
    return mStatus;
  }

  /**
   * Setter for mStatus. Sets status to Active, if it isn't already Active.
   */
  public void setActive() {
    if (mStatus == null || !mStatus.equals(STATUS_ACTIVE)) {
      mStatus = STATUS_ACTIVE;
      touch();
    }
  }

  /**
   * Setter for mStatus. Sets status to Retired, if it isn't already Retired.
   */
  public void setRetired() {
    if (mStatus == null || !mStatus.equals(STATUS_RETIRED)) {
      mStatus = STATUS_RETIRED;
      touch();
    }
  }

  /**
   * Returns true or false indicating whether mStatus is set to Active
   * @return True, if the vehicle is Active
   */
  public boolean isActive() {
    return mStatus.equals(STATUS_ACTIVE);
  }

  /**
   * Returns true or false indicating whether mStatus is set to Retired
   * @return True, if the vehicle is Retired
   */
  public boolean isRetired() {
    return mStatus.equals(STATUS_RETIRED);
  }








  /**
   * Determines whether this and another (provided) instance are equal. Excludes the ID and
   * LastUpdated fields. Case is ignored, so "gray" and "GRAY" are equal.
   *
   * @param other the Model_Vehicle instance to compare against
   * @return true if all member fields (except ID and LastUpdated) are the same
   */
  @SuppressWarnings("unused")
  public boolean equals(Model_Vehicle other) {
    if (this == other) return true;
    if (getStatus() != other.getStatus()) return false;
    if (!this.mName.equalsIgnoreCase(other.mName)) return false;
    if (this.mYear != other.mYear) return false;
    if (!this.mColor.equalsIgnoreCase(other.mColor)) return false;
    if (!this.mModel.equalsIgnoreCase(other.mModel)) return false;
    if (!this.mVIN.equalsIgnoreCase(other.mVIN)) return false;
    if (!this.mLicensePlate.equalsIgnoreCase(other.mLicensePlate)) return false;
    if (!this.mStatus.equals(other.mStatus)) return false;
    //noinspection RedundantIfStatement
    if (!getLastUpdated().equals(other.getLastUpdated())) return false;

    return true;
  }

  /**
   * Determines whether the Color, Model and Year of another instance of Model_Vehicle are
   * the same as this instance's. Case is ignored, so "gray" is the same as "GRAY".
   *
   * @param color the mColor of the other vehicle
   * @param year  the mYear of the other vehicle
   * @param model the mModel of the other vehicle
   * @return true if the Color, Year and Model are the same
   */
  @SuppressWarnings("unused")
  public boolean isSimilar(String color, int year, String model) {
    if (this.mYear != year) return false;
    if (!this.mColor.equalsIgnoreCase(color)) return false;
    //noinspection RedundantIfStatement
    if (!this.mModel.equalsIgnoreCase(model)) return false;

    return true;
  }

  /**
   * Check for possible indications of duplication. Tests are:
   * -- mName
   * -- mColor, mYear and mModel together
   * -- mVin
   * -- license plate
   * Empty values (or 0 in the case of YEAR) are not considered valid for consideration
   * of duplicates. So, if this instance has an empty mName, and the incoming mName is empty,
   * that is not considered a duplicate.
   *
   * @param name         mName of the other vehicle
   * @param color        mColor of the other vehicle
   * @param year         mYear of the other vehicle
   * @param model        of the other vehicle
   * @param vin          of the other vehicle
   * @param licensePlate of the other vehicle
   * @return true if any duplicates are found, false otherwise
   */
  public boolean isDuplicate(String name, String color, int year, String model, String vin,
                             String licensePlate) {

    if (mName.length() > 0 && mName.equalsIgnoreCase(name))
      return true;

    // Check for mColor, mYear and mModel as a set. If all 3 are duplicates, return true
    if ((mColor.length() > 0 && mColor.equalsIgnoreCase(color)) &&
        (mYear != 0 && mYear == year) &&
        (mModel.length() > 0 && mModel.equalsIgnoreCase(model)))
      return true;

    if (mVIN.length() > 0 && mVIN.equalsIgnoreCase(vin))
      return true;

    //noinspection RedundantIfStatement
    if (mLicensePlate.length() > 0 && mLicensePlate.equalsIgnoreCase(licensePlate))
      return true;

    return false;
  }

  public String toString() {
    return getName();
  }
}
