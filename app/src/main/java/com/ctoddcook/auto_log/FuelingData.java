/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */


package com.ctoddcook.auto_log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.ctoddcook.tools.CTools.roundDouble;

/**
 * This is a model class, used to hold all of the data pertaining to a single instance of filling
 * a gas tank. It also includes some
 */
class FuelingData extends DataHolder {
    public static final int SPAN_3_MONTHS = 0;
    public static final int SPAN_6_MONTHS = 1;
    public static final int SPAN_ONE_YEAR = 2;
    public static final int SPAN_ALL_TIME = 3;

    private static final Date DATE_THRESHOLDS[];

    private static final ArrayList<FuelingData> sThreeMonthSpan = new ArrayList<>();
    private static final ArrayList<FuelingData> sSixMonthSpan = new ArrayList<>();
    private static final ArrayList<FuelingData> sOneYearSpan = new ArrayList<>();
    private static final ArrayList<FuelingData> sLifetimeSpan = new ArrayList<>();

    private int mFuelingID = 0;
    private int mVehicleID = 0;
    private Date mDateOfFill = null;
    private double mDistance = 0d;
    private double mVolume = 0d;
    private double mPricePaid = 0d;
    private double mOdometer = 0d;
    private String mLocation = null;
    private String mGPSCoords = null;

    static {
        DATE_THRESHOLDS = new Date[3];

        Calendar threeMonthsAgo = Calendar.getInstance();
        threeMonthsAgo.add(Calendar.MONTH, -3);             // Calculate 3 months before today
        DATE_THRESHOLDS[SPAN_3_MONTHS] = threeMonthsAgo.getTime();

        Calendar sixMonthsAgo = Calendar.getInstance();
        sixMonthsAgo.add(Calendar.MONTH, -6);              // Calculate 6 months before today
        DATE_THRESHOLDS[SPAN_6_MONTHS] = sixMonthsAgo.getTime();

        Calendar oneYearAgo = Calendar.getInstance();
        oneYearAgo.add(Calendar.YEAR, -1);                  // Calculate one year before today
        DATE_THRESHOLDS[SPAN_ONE_YEAR] = oneYearAgo.getTime();
    }



    /*
    Constructors
    */

    /**
     * Generic constructor. New object's state will be NEW. All other fields will be null/0/empty.
     */
    public FuelingData() {}

    /**
     * Constructor with all fields provided. Most likely after being read from the database.
     * This instance's state will be CURRENT.
     * @param fuelingID the unique database ID for the instance
     * @param vehicleID foreign key to the vehicle which received this fueling
     * @param dateOfFill the date of the fueling
     * @param distance the distance driven between the prior fueling and this one
     * @param volume the volume of fuel put into the car
     * @param pricePaid the total price paid for the gas
     * @param odometer the car's odometer reading; will not necessarily make sense with the distance,
     *                 if a fueling is missed (not recorded)
     * @param location the location (city and state) of the fueling, or possibly something
     *                 more descriptive from the user
     * @param gpsCoords the gps coordinates of the fueling, if they were known when the fueling
     *                  occurred
     * @param lastUpdated the last time this record was updated
     */
    public FuelingData(int fuelingID, int vehicleID, Date dateOfFill, double distance, double volume,
                       double pricePaid, double odometer, String location, String gpsCoords,
                       Date lastUpdated) {
        mFuelingID = fuelingID;
        mVehicleID = vehicleID;
        mDateOfFill = dateOfFill;
        mDistance = distance;
        mVolume = volume;
        mPricePaid = pricePaid;
        mOdometer = odometer;
        mLocation = location;
        mGPSCoords = gpsCoords;
        mLastUpdated = lastUpdated;

        setCurrent();
    }




    /*
    Following are methods which calculate average values over time spans.
     */

    /**
     * Returns the appropriate ArrayList of FuelingData instances for the indicated time span
     * @param span FuelingData.SPAN_3_MONTHS, FuelingData.SPAN_6_MONTHS, FuelingData.SPAN_ONE_YEAR or
     *             FuelingData.SPAN_ALL_TIME
     * @return an ArrayList of FuelingData instances
     * @throws IllegalArgumentException if the argument provided does not match one of the required
     * constants.
     */
    private static ArrayList<FuelingData> getListForSpan(int span) throws IllegalArgumentException {
        ArrayList<FuelingData> listForSpan;

        switch (span) {
            case SPAN_3_MONTHS:
                listForSpan = sThreeMonthSpan;
                break;
            case SPAN_6_MONTHS:
                listForSpan = sSixMonthSpan;
                break;
            case SPAN_ONE_YEAR:
                listForSpan = sOneYearSpan;
                break;
            case SPAN_ALL_TIME:
                listForSpan = sLifetimeSpan;
                break;
            default:
                throw new IllegalArgumentException("span argument must be " + SPAN_3_MONTHS + ", "
                        + SPAN_6_MONTHS + ", " + SPAN_ONE_YEAR + " or " + SPAN_ALL_TIME + ".");
        }

        return listForSpan;
    }

    /**
     * Calculates and returns the average (mean) distance for each fill in the indicated time span.
     * Rounded to 1 decimal place.
     * @param span FuelingData.SPAN_3_MONTHS, FuelingData.SPAN_6_MONTHS, FuelingData.SPAN_ONE_YEAR or
     *             FuelingData.SPAN_ALL_TIME
     * @return the average (mean) distance for the fills in the indicated time span.
     */
    public static double getAvgDistanceOverSpan(int span) {
        ArrayList<FuelingData> fillsOverSpan;

        fillsOverSpan = getListForSpan(span);

        if (fillsOverSpan.isEmpty())
            return 0d;

        double totalDistance = 0d;

        for (FuelingData each: fillsOverSpan)
            totalDistance += each.mDistance;

        return roundDouble(totalDistance / fillsOverSpan.size(), 1);
    }

    /**
     * Calculates and returns the average (mean) volume for each fill in the indicated time span.
     * Rounded to 1 decimal place.
     * @param span FuelingData.SPAN_3_MONTHS, FuelingData.SPAN_6_MONTHS, FuelingData.SPAN_ONE_YEAR or
     *             FuelingData.SPAN_ALL_TIME
     * @return the average (mean) distance for the fills in the indicated time span.
     */
    public static double getAvgVolumeOverSpan(int span) {
        ArrayList<FuelingData> fillsOverSpan;

        fillsOverSpan = getListForSpan(span);

        if (fillsOverSpan.isEmpty())
            return 0d;

        double totalVolume = 0d;

        for (FuelingData each: fillsOverSpan)
            totalVolume += each.mVolume;

        return roundDouble(totalVolume / fillsOverSpan.size(), 1);
    }

    /**
     * Calculates and returns the average (mean) Price Paid for each fill in the indicated time
     * span. Rounded to 2 decimal places.
     * @param span FuelingData.SPAN_3_MONTHS, FuelingData.SPAN_6_MONTHS, FuelingData.SPAN_ONE_YEAR or
     *             FuelingData.SPAN_ALL_TIME
     * @return the average (mean) price paid for the fills in the indicated time span.
     */
    public static double getAvgPricePaidOverSpan(int span) throws IllegalArgumentException {
        ArrayList<FuelingData> fillsOverSpan;

        fillsOverSpan = getListForSpan(span);

        if (fillsOverSpan.isEmpty())
            return 0d;

        double totalPricePaid = 0d;

        for (FuelingData each: fillsOverSpan)
            totalPricePaid += each.mPricePaid;

        return roundDouble(totalPricePaid / fillsOverSpan.size(), 2);
    }

    /**
     * Calculates and returns the average (mean) Price Paid per Unit (gallon) over the indicated
     * time span. Rounded to 3 decimal places.
     * @param span FuelingData.SPAN_3_MONTHS, FuelingData.SPAN_6_MONTHS, FuelingData.SPAN_ONE_YEAR or
     *             FuelingData.SPAN_ALL_TIME
     * @return the average (mean) price per unit over indicated time span.
     */
    public static double getAvgPricePerUnitOverSpan(int span) throws IllegalArgumentException {
        ArrayList<FuelingData> fillsOverSpan;

        fillsOverSpan = getListForSpan(span);

        if (fillsOverSpan.isEmpty())
            return 0d;

        double totalPricePaid = 0d;
        double totalVolume = 0d;

        for (FuelingData each: fillsOverSpan) {
            totalPricePaid += each.mPricePaid;
            totalVolume += each.mVolume;
        }

        return roundDouble(totalPricePaid / totalVolume, 3);
    }

    /**
     * Calculates and returns the average (mean) Price Paid per Distance Unit (mile or kilometer)
     * over the indicated time span. Rounded to 3 decimal places.
     * @param span FuelingData.SPAN_3_MONTHS, FuelingData.SPAN_6_MONTHS, FuelingData.SPAN_ONE_YEAR or
     *             FuelingData.SPAN_ALL_TIME
     * @return the average (mean) price per distance unit over indicated time span.
     */
    public static double getAvgPricePerDistanceOverSpan(int span) throws IllegalArgumentException {
        ArrayList<FuelingData> fillsOverSpan;

        fillsOverSpan = getListForSpan(span);

        if (fillsOverSpan.isEmpty())
            return 0d;

        double totalPricePaid = 0d;
        double totalDistance = 0d;

        for (FuelingData each: fillsOverSpan) {
            totalPricePaid += each.mPricePaid;
            totalDistance += each.mDistance;
        }

        return roundDouble(totalPricePaid / totalDistance, 3);
    }

    /**
     * Calculates and returns the average (mean) distance per volume (mpg) over the indicated
     * time span. Rounded to 1 decimal place.
     * @param span FuelingData.SPAN_3_MONTHS, FuelingData.SPAN_6_MONTHS or FuelingData.SPAN_ONE_YEAR
     * @return the average (mean) distance per volume of fuel (mpg) over indicated time span.
     */
    public static double getAvgMileageOverSpan(int span) throws IllegalArgumentException {
        ArrayList<FuelingData> fillsOverSpan;

        fillsOverSpan = getListForSpan(span);

        if (fillsOverSpan.isEmpty())
            return 0d;

        double totalDistance = 0d;
        double totalVolume = 0d;

        for (FuelingData each: fillsOverSpan) {
            totalDistance += each.mDistance;
            totalVolume += each.mVolume;
        }

        return roundDouble(totalDistance / totalVolume, 1);
    }





    /*
    Following are methods which get row counts from the arrays containing FuelingData instances
    in certain time spans.
     */

    /**
     * Provides the count of fills in the "last 3 months" list
     * @return the size of the sThreeMonthSpan array
     */
    public static int getThreeMonthsRowCount() {
        return sThreeMonthSpan.size();
    }

    /**
     * Provides the count of fills in the "last 6 months" list
     * @return the size of the sSixMonthSpan array
     */
    public static int getSixMonthsRowCount() {
        return sSixMonthSpan.size();
    }

    /**
     * Provides the count of fills in the "last year" list
     * @return the size of the sOneYearSpan array
     */
    public static int getOneYearRowCount() {
       return sOneYearSpan.size();
    }

    /**
     * Returns the count of all fills in memory.
     * @return the size of the sLifetimeSpan array
     */
    public static int getLifetimeRowCount() {
        return sLifetimeSpan.size();
    }





    /*
    The next few methods make adjustments to the arrays containing FuelingData instances
    in certain time spans.
     */

    /**
     * This will add (or remove) the instance to (from) the static lists of fill records
     * based on date. If the fill date (mDateOfFill) is within the last 3 or 6 months or
     * last year, this instance will get added to at least one of those lists.
     *
     * Care is taken not to add the instance to a list if it is already there. Likewise, if the
     * instance should NOT be in a list, it is removed from that list if it is found to be present.
     */
    private void adjustLists() throws IllegalStateException {
        if (mDateOfFill == null)
            throw new IllegalStateException("Field mDateOfFill must not be null when this method is called");

        if (mDateOfFill.after(DATE_THRESHOLDS[SPAN_3_MONTHS])) {      // Should be in 3-month list?
            if (!sThreeMonthSpan.contains(this))     //   Are we already there?
                sThreeMonthSpan.add(this);           //     If not, add to the list
        } else {                                    // Otherwise, we should NOT be in the list
            if (sThreeMonthSpan.contains(this))      //   But are we there?
                sThreeMonthSpan.remove(this);        //     If so, remove from the list
        }

        if (mDateOfFill.after(DATE_THRESHOLDS[SPAN_6_MONTHS])) {     // Should be in 6-month list?
            if (!sSixMonthSpan.contains(this))    //   Are we already there?
                sSixMonthSpan.add(this);          //     If not, add to the list
        } else {                                    // Otherwise, we should NOT be in the list
            if (sSixMonthSpan.contains(this))     //   But are we there?
                sSixMonthSpan.remove(this);       //     If so, remove from the list
        }

        if (mDateOfFill.after(DATE_THRESHOLDS[SPAN_ONE_YEAR])) {     // Should be in one-year list?
            if (!sOneYearSpan.contains(this))   //   Are we already there?
                sOneYearSpan.add(this);         //     If not, add to the list
        } else {                                    // Otherwise, we should NOT be in the list
            if (sOneYearSpan.contains(this))    //   But are we there?
                sOneYearSpan.remove(this);      //     If so, remove from the list
        }

        // Finally, regardless of date, add this to the list of all FuelingData instances
        if (!sLifetimeSpan.contains(this))
            sLifetimeSpan.add(this);
    }

    /**
     * Removes all Fill Data from the arrays (sThreeMonthSpan, sSixMonthSpan, sOneYearSpan)
     * which are used for calculating averages over time.
     */
    public static void clearSpans() {
        sThreeMonthSpan.clear();
        sSixMonthSpan.clear();
        sOneYearSpan.clear();
    }

    /**
     * Removes all Fill Data from all the arrays, including those used for calculating averages
     * over time (sThreeMonthSpan, sSixMonthSpan, sOneYearSpan) and the one used to hold
     * all existing Fill Data objects (sLifetimeSpan).
     */
    public static void clearAll() {
        clearSpans();
        sLifetimeSpan.clear();
    }

    /**
     * Removes the provided FuelingData instance from all ArrayLists
     * @param fd The FuelingData instance to remove
     */
    public static void remove(FuelingData fd) {
        if (sThreeMonthSpan.contains(fd))
            sThreeMonthSpan.remove(fd);

        if (sSixMonthSpan.contains(fd))
            sSixMonthSpan.remove(fd);

        if (sOneYearSpan.contains(fd))
            sOneYearSpan.remove(fd);

        if (sLifetimeSpan.contains(fd))
            sLifetimeSpan.remove(fd);
    }





    /*
    The next few methods calculate values for a specific instance of FuelingData
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
     * Getter for mFuelingID, the database prime key value of the row
     * @return the database prime key value of the row
     */
    public int getFuelingID() {
        return mFuelingID;
    }

    /**
     * Getter for record ID
     * @return The database ID for the record
     */
    @Override
    public int getID() {
        return getFuelingID();
    }

    /**
     * Setter for mFuelingID, the database prime key value of the row. This should only be called
     * when filling in the data retrieved from the database; i.e., this should not be used to
     * "update" the value but only to get the already existing value from the database.
     * @param fuelingID the database prime key value of the row
     * @throws UnsupportedOperationException if mFuelingID is not 0 when this method is called
     * @throws IllegalArgumentException if the parameter provided is less than 1
     */
    public void setFuelingID(int fuelingID) throws UnsupportedOperationException, IllegalArgumentException {
        if (mFuelingID != 0)
            throw new UnsupportedOperationException("The Row ID can only be set once, and cannot be updated");
        if (fuelingID < 1)
            throw new IllegalArgumentException("The Row ID cannot be less than 1");

        mFuelingID = fuelingID;

        touch();
    }

    /**
     * Getter for mVehicleID, the unique identifier for the vehicle represented by the data
     * @return the unique identifier for the vehicle represented by the data
     */
    public int getVehicleID() {
        return mVehicleID;
    }

    /**
     * Setter for mVehicleID, the unique identifier for the vehicle represented by the data
     * @param vehicleID A unique identifier for the vehicle represented by the data
     */
    public void setVehicleID(int vehicleID) {
        mVehicleID = vehicleID;

        touch();
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
     * appropriate arrays of FuelingData objects.
     * @param dateOfFill a Date value indicating when the vehicle was filled with gas
     */
    public void setDateOfFill(Date dateOfFill) {
        mDateOfFill = dateOfFill;
        adjustLists();

        touch();
    }

    /**
     * Setter for mDateOfFill, a Date value indicating when the vehicle was filled with gas
     * @param dateOfFill a Long value indicating the millis representation of the date
     */
    public void setDateOfFill(long dateOfFill) {
        Date newDate = new Date();
        newDate.setTime(dateOfFill);
        setDateOfFill(newDate);

        touch();
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

        touch();
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

        touch();
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

        touch();
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

        touch();
    }

    /**
     * Getter for mLocation, the GPS location when the tank was filled
     * @return the GPS location when the tank was filled
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     * Setter for mLocation, the GPS location when the tank was filled
     * @param location the GPS location when the tank was filled
     */
    public void setLocation(String location) {
        mLocation = location;

        touch();
    }

    /**
     * Getter for GPSCoords field
     * @return the GPS coordinates, if they are captured
     */
    public String getGPSCoords() {
        return mGPSCoords;
    }

    /**
     * Setter for the GPS coordinates
     * @param GPSCoords the GPS coordinates
     */
    public void setGPSCoords(String GPSCoords) {
        mGPSCoords = GPSCoords;
    }
}
