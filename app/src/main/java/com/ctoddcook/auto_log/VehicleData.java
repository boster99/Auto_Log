/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Date;

import static com.ctoddcook.CGenTools.CTools.longToInt;

/**
 * Created by C. Todd Cook on 5/9/2016.
 * ctodd@ctoddcook.com
 *
 * A fairly simple, straightforward data class. No calculations. Just store and present
 * data for different vehicles.
 */
public class VehicleData extends DataHolder {
    private static final SparseArray<VehicleData> sVehicleList = new SparseArray<>(12);
    private int mVehicleID;
    private String mName;
    private int mYear;
    private String mColor;
    private String mModel;
    private String mVIN;
    private String mLicensePlate;

    /**
     * Constructor. This version should be used when creating a new instance from the user
     * interface. It does not setup any fields directly, though DataHolder.mStatus defaults
     * to NEW.
     *
     * Note: Creating a new, empty instance does not cause it to be added to the sVehicleList
     * SparseArray, as the user might cancel the operation, leaving an empty instance in the list.
     */
    public VehicleData() {}

    /**
     * This constructor should be used when retrieving records from the database.
     * @param id the vehicle id, generated automatically by the database (prime key)
     * @param name the user-provided name of the record
     * @param year the vehicle year
     * @param color the vehicle color
     * @param model the vehicle model
     * @param vin the vehicle VIN
     * @param licensePlate the vehicle license plate
     * @param lastUpdated the last time the record was updated
     */
    public VehicleData(int id, String name, int year, String color, String model, String vin, String licensePlate, Date lastUpdated) {
        mVehicleID = id;
        mName = name;
        mYear = year;
        mColor = color;
        mModel = model;
        mVIN = vin;
        mLicensePlate = licensePlate;
        mLastUpdated = lastUpdated;
        setCurrent();
        addVehicle(this);
    }

    /**
     * Returns the vehicle for the given ID. If no such vehicle is found in the static SparseArray
     * null is returned.
     * @param id the ID of the desired VehicleData instance
     * @return the associated VehicleData instance, or null if there is none
     */
    public static VehicleData getVehicle(int id) {
        return sVehicleList.get(id);
    }

    /**
     * Returns the vehicle for the given ID. If no such vehicle is found in the static SparseArray
     * null is returned.
     * @param id the ID of the desired VehicleData instance
     * @return the associated VehicleData instance, or null if there is none
     */
    public static VehicleData getVehicle(long id) {
        return getVehicle(longToInt(id));
    }

    /**
     * Adds an instance to the static list of Vehicles.
     * @param v The vehicle to be added to the list
     */
    public static void addVehicle(VehicleData v) {
        sVehicleList.put(v.getID(), v);
    }

    /**
     * Returns an iterable list of Vehicles.
     * @return ArrayList of all of the VehicleData instances
     */
    public static ArrayList<VehicleData> getVehicleList() {
        ArrayList<VehicleData> list = new ArrayList<>();
        VehicleData v;
        for (int i=0; i < sVehicleList.size(); i++) {
            v = sVehicleList.valueAt(i);
            list.add(v);
        }

        return list;
    }

    /**
     * Getter for mVehicleID field.
     * @return mVehicleID field value
     */
    public int getVehicleID() {
        return mVehicleID;
    }

    /**
     * Getter for record ID
     * @return The database ID for the record
     */
    @Override
    public int getID() {
        return getVehicleID();
    }

    /**
     * Setter for mVehicleID field. Does not allow a change to an existing (non-zero) ID, and
     * does not allow setting the ID to a number less than 1.
     * @param vehicleID New ID for the record
     * @throws UnsupportedOperationException If there is already an ID in place for this instance.
     * @throws IllegalArgumentException If the incoming ID is less than 1
     */
    public void setVehicleID(int vehicleID) throws UnsupportedOperationException, IllegalArgumentException {
        if (mVehicleID != 0)
            throw new UnsupportedOperationException("The Row ID can only be set once, and cannot be updated");
        if (vehicleID < 1)
            throw new IllegalArgumentException("The Row ID cannot be less than 1");

        mVehicleID = vehicleID;
    }

    /**
     * Getter for mName field.
     * @return value of mName field.
     */
    public String getName() {
        return mName;
    }

    /**
     * Setter for mName field.
     * @param name The new value for the mName field
     */
    public void setName(String name) {
        mName = name;

        touch();
    }

    /**
     * Getter for mYear field
     * @return value of mYear field
     */
    public int getYear() {
        return mYear;
    }

    /**
     * Setter for mYear field
     * @param year new value for Year
     * @throws IllegalArgumentException if the year is outside 1900-2100
     */
    public void setYear(int year) throws IllegalArgumentException {
        if (year < 1900 || year > 2100)
            throw new IllegalArgumentException("The year must be between 1900 and 2100");
        mYear = year;
        touch();
    }

    /**
     * Getter for color
     * @return the color of the car
     */
    public String getColor() {
        return mColor;
    }

    /**
     * Setter for color
     * @param color of the car
     */
    public void setColor(String color) {
        mColor = color;

        touch();
    }

    /**
     * Getter for the car's model
     * @return the car's model
     */
    public String getModel() {
        return mModel;
    }

    /**
     * Setter for the car's model
     * @param model the car's model
     */
    public void setModel(String model) {
        mModel = model;

        touch();
    }

    /**
     * Getter for the car's VIN
     * @return the car's VIN
     */
    public String getVIN() {
        return mVIN;
    }

    /**
     * Setter for the car's VIN
     * @param VIN the car's VIN
     */
    public void setVIN(String VIN) {
        mVIN = VIN;

        touch();
    }

    /**
     * Getter for the car's license plate
     * @return the car's license plate
     */
    public String getLicensePlate() {
        return mLicensePlate;
    }

    /**
     * Setter for license plate
     * @param licensePlate the new license plate value
     */
    public void setLicensePlate(String licensePlate) {
        mLicensePlate = licensePlate;

        touch();
    }

    /**
     * Determines whether this and another (provided) instance are equal. Excludes the ID and
     * LastUpdated fields. Case is ignored, so "gray" and "GRAY" are equal.
     * @param other the VehicleData instance to compare against
     * @return true if all member fields (except ID and LastUpdated) are the same
     */
    public boolean equals(VehicleData other) {
        if (this == other) return true;
        if (getStatus() != other.getStatus()) return false;
        if (!this.mName.equalsIgnoreCase(other.mName)) return false;
        if (this.mYear != other.mYear) return false;
        if (!this.mColor.equalsIgnoreCase(other.mColor)) return false;
        if (!this.mModel.equalsIgnoreCase(other.mModel)) return false;
        if (!this.mVIN.equalsIgnoreCase(other.mVIN)) return false;
        if (!this.mLicensePlate.equalsIgnoreCase(other.mLicensePlate)) return false;
        if (!getLastUpdated().equals(other.getLastUpdated())) return false;

        return true;
    }

    /**
     * Determines whether the Color, Model and Year of another instance of VehicleData are
     * the same as this instance's. Case is ignored, so "gray" is the same as "GRAY".
     * @param color the color of the other vehicle
     * @param year the year of the other vehicle
     * @param model the model of the other vehicle
     * @return true if the Color, Year and Model are the same
     */
    public boolean isSimilar(String color, int year, String model) {
        if (this.mYear != year) return false;
        if (!this.mColor.equalsIgnoreCase(color)) return false;
        if (!this.mModel.equalsIgnoreCase(model)) return false;

        return true;
    }

    /**
     * Check for possible indications of duplication. Tests are:
     * -- name
     * -- color, year and model together
     * -- vin
     * -- license plate
     * Empty values (or 0 in the case of YEAR) are not considered valid for consideration
     * of duplicates. So, if this instance has an empty name, and the incoming name is empty,
     * that is not considered a duplicate.
     * @param name name of the other vehicle
     * @param color color of the other vehicle
     * @param year year of the other vehicle
     * @param model of the other vehicle
     * @param vin of the other vehicle
     * @param licensePlate of the other vehicle
     * @return true if any duplicates are found, false otherwise
     */
    public boolean isDuplicate(String name, String color, int year, String model, String vin,
                               String licensePlate) {

        if (mName.length() > 0 && mName.equalsIgnoreCase(name))
            return true;

        // Check for color, year and model as a set. If all 3 are duplicates, return true
        if ((mColor.length() > 0 && mColor.equalsIgnoreCase(color)) &&
                (mYear != 0 && mYear == year) &&
                (mModel.length() > 0 && mModel.equalsIgnoreCase(model)))
            return true;

        if (mVIN.length() > 0 && mVIN.equalsIgnoreCase(vin))
            return true;

        if (mLicensePlate.length() > 0 && mLicensePlate.equalsIgnoreCase(licensePlate))
            return true;

        return false;
    }
}
