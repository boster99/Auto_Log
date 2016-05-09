/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

/**
 * Created by C. Todd Cook on 5/9/2016.
 * ctodd@ctoddcook.com
 *
 * A fairly simple, straightforward data class. No calculations. Just store and present
 * data for different vehicles.
 */
public class VehicleData extends DataHolder {
    private long mVehicleID;
    private String mName;
    private int mYear;
    private String mColor;
    private String mModel;
    private String mVIN;
    private String mLicensePlate;

    /**
     * Getter for mVehicleID field.
     * @return mVehicleID field value
     */
    public long getVehicleID() {
        return mVehicleID;
    }

    /**
     * Setter for mVehicleID field. Does not allow a change to an existing (non-zero) ID, and
     * does not allow setting the ID to a number less than 1.
     * @param vehicleID New ID for the record
     * @throws UnsupportedOperationException If there is already an ID in place for this instance.
     * @throws IllegalArgumentException If the incoming ID is less than 1
     */
    public void setVehicleID(long vehicleID) throws UnsupportedOperationException, IllegalArgumentException {
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
}