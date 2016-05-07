/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */


package com.ctoddcook.auto_log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.ctoddcook.tools.CTools.roundDouble;

/**
 * This is a model class, used to hold all of the data pertaining to a single instance of filling
 * a gas tank. It also includes some
 */
class FillData {
    public static final int SPAN_60_DAYS = 0;
    public static final int SPAN_120_DAYS = 1;
    public static final int SPAN_ONE_YEAR = 2;
    public static final int SPAN_ALL_TIME = 3;
    public static final String STATUS_NEW = "N";
    public static final String STATUS_CURRENT = "C";
    public static final String STATUS_UPDATED = "U";

    private static final Date DATE_THRESHOLDS[];

    private static final ArrayList<FillData> sFillsInLast60 = new ArrayList<FillData>();
    private static final ArrayList<FillData> sFillsInLast120 = new ArrayList<FillData>();
    private static final ArrayList<FillData> sFillsInLastYear = new ArrayList<FillData>();
    private static final ArrayList<FillData> sAllFills = new ArrayList<FillData>();

    private long mRowID = 0L;
    private long mVehicleID = 0L;
    private Date mDateOfFill = null;
    private double mDistance = 0d;
    private double mVolume = 0d;
    private double mPricePaid = 0d;
    private double mOdometer = 0d;
    private double mLocation = 0d;
    private String mStatus = STATUS_NEW;

    static {
        DATE_THRESHOLDS = new Date[3];

        Calendar sixtyDaysAgo = Calendar.getInstance();
        sixtyDaysAgo.add(Calendar.DATE, -60);               // Calculate 60 days before today
        DATE_THRESHOLDS[SPAN_60_DAYS] = sixtyDaysAgo.getTime();

        Calendar oneHundredTwentyDaysAgo = Calendar.getInstance();
        oneHundredTwentyDaysAgo.add(Calendar.DATE, -120);   // Calculate 120 days before today
        DATE_THRESHOLDS[SPAN_120_DAYS] = oneHundredTwentyDaysAgo.getTime();

        Calendar oneYearAgo = Calendar.getInstance();
        oneYearAgo.add(Calendar.YEAR, -1);                  // Calculate one year before today
        DATE_THRESHOLDS[SPAN_ONE_YEAR] = oneYearAgo.getTime();
    }


    /*
    Following are methods which calculate average values over time spans.
     */

    /**
     * Returns the appropriate ArrayList of FillData instances for the indicated time span
     * @param span FillData.SPAN_60_DAYS, FillData.SPAN_120_DAYS, FillData.SPAN_ONE_YEAR or
     *             FillData.SPAN_ALL_TIME
     * @return an ArrayList of FillData instances
     * @throws IllegalArgumentException if the argument provided does not match one of the required
     * constants.
     */
    private static ArrayList<FillData> getListForSpan(int span) throws IllegalArgumentException {
        ArrayList<FillData> listForSpan;

        switch (span) {
            case SPAN_60_DAYS:
                listForSpan = sFillsInLast60;
                break;
            case SPAN_120_DAYS:
                listForSpan = sFillsInLast120;
                break;
            case SPAN_ONE_YEAR:
                listForSpan = sFillsInLastYear;
                break;
            case SPAN_ALL_TIME:
                listForSpan = sAllFills;
                break;
            default:
                throw new IllegalArgumentException("span argument must be " + SPAN_60_DAYS + ", "
                        + SPAN_120_DAYS + ", " + SPAN_ONE_YEAR + " or " + SPAN_ALL_TIME + ".");
        }

        return listForSpan;
    }

    /**
     * Calculates and returns the average (mean) distance for each fill in the indicated time span.
     * Rounded to 1 decimal place.
     * @param span FillData.SPAN_60_DAYS, FillData.SPAN_120_DAYS, FillData.SPAN_ONE_YEAR or
     *             FillData.SPAN_ALL_TIME
     * @return the average (mean) distance for the fills in the indicated time span.
     */
    public static double getAvgDistanceOverSpan(int span) {
        ArrayList<FillData> fillsOverSpan;

        fillsOverSpan = getListForSpan(span);

        if (fillsOverSpan.isEmpty())
            return 0d;

        double totalDistance = 0d;

        for (FillData each: fillsOverSpan)
            totalDistance += each.mDistance;

        return roundDouble(totalDistance / fillsOverSpan.size(), 1);
    }

    /**
     * Calculates and returns the average (mean) volume for each fill in the indicated time span.
     * Rounded to 1 decimal place.
     * @param span FillData.SPAN_60_DAYS, FillData.SPAN_120_DAYS, FillData.SPAN_ONE_YEAR or
     *             FillData.SPAN_ALL_TIME
     * @return the average (mean) distance for the fills in the indicated time span.
     */
    public static double getAvgVolumeOverSpan(int span) {
        ArrayList<FillData> fillsOverSpan;

        fillsOverSpan = getListForSpan(span);

        if (fillsOverSpan.isEmpty())
            return 0d;

        double totalVolume = 0d;

        for (FillData each: fillsOverSpan)
            totalVolume += each.mVolume;

        return roundDouble(totalVolume / fillsOverSpan.size(), 1);
    }

    /**
     * Calculates and returns the average (mean) Price Paid for each fill in the indicated time
     * span. Rounded to 2 decimal places.
     * @param span FillData.SPAN_60_DAYS, FillData.SPAN_120_DAYS, FillData.SPAN_ONE_YEAR or
     *             FillData.SPAN_ALL_TIME
     * @return the average (mean) price paid for the fills in the indicated time span.
     */
    public static double getAvgPricePaidOverSpan(int span) throws IllegalArgumentException {
        ArrayList<FillData> fillsOverSpan;

        fillsOverSpan = getListForSpan(span);

        if (fillsOverSpan.isEmpty())
            return 0d;

        double totalPricePaid = 0d;

        for (FillData each: fillsOverSpan)
            totalPricePaid += each.mPricePaid;

        return roundDouble(totalPricePaid / fillsOverSpan.size(), 2);
    }

    /**
     * Calculates and returns the average (mean) Price Paid per Unit (gallon) over the indicated
     * time span. Rounded to 3 decimal places.
     * @param span FillData.SPAN_60_DAYS, FillData.SPAN_120_DAYS, FillData.SPAN_ONE_YEAR or
     *             FillData.SPAN_ALL_TIME
     * @return the average (mean) price per unit over indicated time span.
     */
    public static double getAvgPricePerUnitOverSpan(int span) throws IllegalArgumentException {
        ArrayList<FillData> fillsOverSpan;

        fillsOverSpan = getListForSpan(span);

        if (fillsOverSpan.isEmpty())
            return 0d;

        double totalPricePaid = 0d;
        double totalVolume = 0d;

        for (FillData each: fillsOverSpan) {
            totalPricePaid += each.mPricePaid;
            totalVolume += each.mVolume;
        }

        return roundDouble(totalPricePaid / totalVolume, 3);
    }

    /**
     * Calculates and returns the average (mean) Price Paid per Distance Unit (mile or kilometer)
     * over the indicated time span. Rounded to 3 decimal places.
     * @param span FillData.SPAN_60_DAYS, FillData.SPAN_120_DAYS, FillData.SPAN_ONE_YEAR or
     *             FillData.SPAN_ALL_TIME
     * @return the average (mean) price per distance unit over indicated time span.
     */
    public static double getAvgPricePerDistanceOverSpan(int span) throws IllegalArgumentException {
        ArrayList<FillData> fillsOverSpan;

        fillsOverSpan = getListForSpan(span);

        if (fillsOverSpan.isEmpty())
            return 0d;

        double totalPricePaid = 0d;
        double totalDistance = 0d;

        for (FillData each: fillsOverSpan) {
            totalPricePaid += each.mPricePaid;
            totalDistance += each.mDistance;
        }

        return roundDouble(totalPricePaid / totalDistance, 3);
    }

    /**
     * Calculates and returns the average (mean) distance per volume (mpg) over the indicated
     * time span. Rounded to 1 decimal place.
     * @param span FillData.SPAN_60_DAYS, FillData.SPAN_120_DAYS or FillData.SPAN_ONE_YEAR
     * @return the average (mean) distance per volume of fuel (mpg) over indicated time span.
     */
    public static double getAvgMileageOverSpan(int span) throws IllegalArgumentException {
        ArrayList<FillData> fillsOverSpan;

        fillsOverSpan = getListForSpan(span);

        if (fillsOverSpan.isEmpty())
            return 0d;

        double totalDistance = 0d;
        double totalVolume = 0d;

        for (FillData each: fillsOverSpan) {
            totalDistance += each.mDistance;
            totalVolume += each.mVolume;
        }

        return roundDouble(totalDistance / totalVolume, 1);
    }





    /*
    Following are methods which get row counts from the arrays containing FillData instances
    in certain time spans.
     */

    /**
     * Provides the count of fills in the "last 60 days" list
     * @return the size of the sFillsInLast60 array
     */
    public static int get60DaysRowCount() {
        return sFillsInLast60.size();
    }

    /**
     * Provides the count of fills in the "last 120 days" list
     * @return the size of the sFillsInLast120 array
     */
    public static int get120DaysRowCount() {
        return sFillsInLast120.size();
    }

    /**
     * Provides the count of fills in the "last year" list
     * @return the size of the sFillsInLastYear array
     */
    public static int getOneYearRowCount() {
       return sFillsInLastYear.size();
    }

    /**
     * Returns the count of all fills in memory.
     * @return the size of the sAllFills array
     */
    public static int getAllRowCount() {
        return sAllFills.size();
    }





    /*
    The next few methods make adjustments to the arrays containing FillData instances
    in certain time spans.
     */

    /**
     * This will add (or remove) the instance to (from) the static lists of fill records
     * based on date. If the fill date (mDateOfFill) is within the last 60 or 120 days or
     * last year, this instance will get added to at least one of those lists.
     *
     * Care is taken not to add the instance to a list if it is already there. Likewise, if the
     * instance should NOT be in a list, it is removed from that list if it is found to be present.
     */
    private void adjustLists() throws IllegalStateException {
        if (mDateOfFill == null)
            throw new IllegalStateException("Field mDateOfFill must not be null when this method is called");

        if (mDateOfFill.after(DATE_THRESHOLDS[SPAN_60_DAYS])) {      // Should be in 60-day list?
            if (!sFillsInLast60.contains(this))     //   Are we already there?
                sFillsInLast60.add(this);           //     If not, add to the list
        } else {                                    // Otherwise, we should NOT be in the list
            if (sFillsInLast60.contains(this))      //   But are we there?
                sFillsInLast60.remove(this);        //     If so, remove from the list
        }

        if (mDateOfFill.after(DATE_THRESHOLDS[SPAN_120_DAYS])) {     // Should be in 120-day list?
            if (!sFillsInLast120.contains(this))    //   Are we already there?
                sFillsInLast120.add(this);          //     If not, add to the list
        } else {                                    // Otherwise, we should NOT be in the list
            if (sFillsInLast120.contains(this))     //   But are we there?
                sFillsInLast120.remove(this);       //     If so, remove from the list
        }

        if (mDateOfFill.after(DATE_THRESHOLDS[SPAN_ONE_YEAR])) {     // Should be in one-year list?
            if (!sFillsInLastYear.contains(this))   //   Are we already there?
                sFillsInLastYear.add(this);         //     If not, add to the list
        } else {                                    // Otherwise, we should NOT be in the list
            if (sFillsInLastYear.contains(this))    //   But are we there?
                sFillsInLastYear.remove(this);      //     If so, remove from the list
        }

        // Finally, regardless of date, add this to the list of all FillData instances
        if (!sAllFills.contains(this))
            sAllFills.add(this);
    }

    /**
     * Removes all Fill Data from the arrays (sFillsInLast60, sFillInLast120, sFillsInLastYear)
     * which are used for calculating averages over time.
     */
    public static void clearSpans() {
        sFillsInLast60.clear();
        sFillsInLast120.clear();
        sFillsInLastYear.clear();
    }

    /**
     * Removes all Fill Data from all the arrays, including those used for calculating averages
     * over time (sFillsInLast60, sFillsInLast120, sFillsInLastYear) and the one used to hold
     * all existing Fill Data objects (sAllFills).
     */
    public static void clearAll() {
        clearSpans();
        sAllFills.clear();
    }

    /**
     * Removes the provided FillData instance from all ArrayLists
     * @param fd The FillData instance to remove
     */
    public static void remove(FillData fd) {
        if (sFillsInLast60.contains(fd))
            sFillsInLast60.remove(fd);

        if (sFillsInLast120.contains(fd))
            sFillsInLast120.remove(fd);

        if (sFillsInLastYear.contains(fd))
            sFillsInLastYear.remove(fd);

        if (sAllFills.contains(fd))
            sAllFills.remove(fd);
    }





    /*
    The next few methods calculate values for a specific instance of FillData
     */

    /**
     * Returns the price per unit (price per gallon or litre) for the instance, rounded to
     * 3 decimal places
     * @return the price per unit
     */
    public double getPricePerUnit() {
        if (mVolume == 0)
            return 0;

        return roundDouble(mPricePaid/mVolume, 3);
    }

    /**
     * Calculates the mileage (mpg or lpk) for the instance, rounded to 1 decimal place
     * @return Miles per gallon or litres per kilometer
     */
    public double getMileage() {
        if (mVolume == 0)
            return 0;

        return roundDouble(mDistance/mVolume, 1);
    }

    /**
     * Returns the price per distance unit traveled, rounded to 3 decimal places
     * @return the price per distance unit
     */
    public double getPricePerDistance() {
        if (mDistance == 0)
            return 0;

        return roundDouble(mPricePaid/mDistance, 3);
    }





    /*
    Next are the accessor methods
     */

    /**
     * Getter for mRowID, the database prime key value of the row
     * @return the database prime key value of the row
     */
    public long getRowID() {
        return mRowID;
    }

    /**
     * Setter for mRowID, the database prime key value of the row. This should only be called
     * when filling in the data retrieved from the database; i.e., this should not be used to
     * "update" the value but only to get the already existing value from the database.
     * @param rowID the database prime key value of the row
     * @throws UnsupportedOperationException if mRowID is not 0 when this method is called
     * @throws IllegalArgumentException if the parameter provided is less than 1
     */
    public void setRowID(long rowID) throws UnsupportedOperationException, IllegalArgumentException {
        if (mRowID != 0)
            throw new UnsupportedOperationException("The Row ID can only be set once, and cannot be updated");
        if (rowID < 1)
            throw new IllegalArgumentException("The Row ID cannot be less than 1");

        mRowID = rowID;
    }

    /**
     * Getter for mVehicleID, the unique identifier for the vehicle represented by the data
     * @return the unique identifier for the vehicle represented by the data
     */
    public long getVehicleID() {
        return mVehicleID;
    }

    /**
     * Setter for mVehicleID, the unique identifier for the vehicle represented by the data
     * @param vehicleID A unique identifier for the vehicle represented by the data
     */
    public void setVehicleID(long vehicleID) {
        mVehicleID = vehicleID;
    }

    /**
     * Getter for mDateOfFill, a Date value indicating when the vehicle was filled with gas
     * @return a Date value indicating when the vehicle was filled with gas
     */
    public Date getDateOfFill() {
        return mDateOfFill;
    }

    /**
     * Setter for mDateOfFill, a Date value indicating when the vehicle was filled with gas. After
     * setting the new value, adjustLists() is called to add this instance to (or remove it from)
     * appropriate arrays of FillData objects.
     * @param dateOfFill a Date value indicating when the vehicle was filled with gas
     */
    public void setDateOfFill(Date dateOfFill) {
        mDateOfFill = dateOfFill;
        adjustLists();
    }

    /**
     * Setter for mDateOfFill, a Date value indicating when the vehicle was filled with gas
     * @param dateOfFill a Long value indicating the millis representation of the date
     */
    public void setDateOfFill(long dateOfFill) {
        Date newDate = new Date();
        newDate.setTime(dateOfFill);
        setDateOfFill(newDate);
    }

    /**
     * Getter for mDistance, the distance traveled on the tank of gas
     * @return the distance traveled on the tank of gas
     */
    public double getDistance() {
        return mDistance;
    }

    /**
     * Setter for mDistance, the distance traveled on the tank of gas
     * @param distance the distance traveled on the tank of gas
     * @throws IllegalArgumentException if the volume argument is negative
     */
    public void setDistance(double distance) throws IllegalArgumentException {
        if (distance < 0)
            throw new IllegalArgumentException("distance argument cannot be negative");
        mDistance = distance;
    }

    /**
     * Getter for mVolume, the volume of gas (in gallons or liters) on this fill
     * @return the volume of gas (in gallons or liters) on this fill
     */
    public double getVolume() {
        return mVolume;
    }

    /**
     * Setter for mVolume, the volume of gas (in gallons or liters) on this fill
     * @param volume the newVolume of gas (in gallons or liters) on this fill
     * @throws IllegalArgumentException if the volume argument is negative
     */
    public void setVolume(double volume) throws IllegalArgumentException {
        if (volume < 0)
            throw new IllegalArgumentException("volume argument cannot be negative");
        mVolume = volume;
    }

    /**
     * Getter for mPricePaid, the total amount paid for the tank of gas
     * @return the total amount paid for the tank of gas
     */
    public double getPricePaid() {
        return mPricePaid;
    }

    /**
     * Setter for mPricePaid, the total amount paid for the tank of gas
     * @param pricePaid the total amount paid for the tank of gas
     * @throws IllegalArgumentException if the pricePaid argument is negative
     */
    public void setPricePaid(double pricePaid) throws IllegalArgumentException {
        if (pricePaid < 0)
            throw new IllegalArgumentException("pricePaid argument cannot be negative");
        mPricePaid = pricePaid;
    }

    /**
     * Getter for mOdometer, the odometer reading when the tank was filled
     * @return the odometer reading when the tank was filled
     */
    public double getOdometer() {
        return mOdometer;
    }

    /**
     * Setter for mOdometer, the odometer reading when the tank was filled
     * @param odometer the odometer reading when the tank was filled
     * @throws IllegalArgumentException if the odometer value provided is negative
     */
    public void setOdometer(double odometer) throws IllegalArgumentException {
        if (odometer < 0)
            throw new IllegalArgumentException("odometer argument cannot be negative");
        mOdometer = odometer;
    }

    /**
     * Getter for mLocation, the GPS location when the tank was filled
     * @return the GPS location when the tank was filled
     */
    public double getLocation() {
        return mLocation;
    }

    /**
     * Setter for mLocation, the GPS location when the tank was filled
     * @param location the GPS location when the tank was filled
     */
    public void setLocation(double location) {
        mLocation = location;
    }

    /**
     * Getter for mStatus, the status of the row. Possible values are <code>N</code> (New row)
     * <code>C</code> (Current row) or <code>U</code> (Updated row).
     * @return A length-one String containing N, C or U
     */
    public String getStatus() {
        return mStatus;
    }

    /**
     * Setter for mStatus, the status of the row
     * @param status An 'N', 'C', or 'U' indicating the row is a new one, is current, or updated
     * @throws IllegalArgumentException if the argument is not an N, C or U
     */
    public void setStatus(String status) throws IllegalArgumentException {
        if (!status.equals(STATUS_NEW) && !status.equals(STATUS_CURRENT) && !status.equals(STATUS_UPDATED))
            throw new IllegalArgumentException("The provided status must be '" + STATUS_NEW +
                    "' OR '" + STATUS_CURRENT + "' OR '" + STATUS_UPDATED + "'.");

        mStatus = status;
    }
}
