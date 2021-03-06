/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */


package com.ctoddcook.FuelLog;

import android.util.SparseArray;

import com.ctoddcook.CamGenTools.DataHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.ctoddcook.CamGenTools.CTools.round;

/**
 * This is a mModel class, used to hold all of the data pertaining to a single instance of filling
 * a gas tank. It also includes some methods for calculating numbers such as miles per gallon,
 * and price per mile.
 * <p/>
 * <strong>Note:</strong>Regarding some terminology: This class has a field called "Location"
 * (with an 'm' prefix). This is not to be confused with the Android framework of location
 * services. This is intended to hold the user's descriptive text of where he/she got gas. It
 * could read as a city and state, or a gas station, or a street address. It will be initialized
 * (in the Activity_EditFueling class) to a city and state via location services--i.e., a
 * latitude and longitude will be retrieved and used to provide the user with a city and state;
 * after that the user might change that description.
 */
class Model_Fueling extends DataHolder {
  public static final int SPAN_3_MONTHS = 0;
  public static final int SPAN_6_MONTHS = 1;
  public static final int SPAN_ONE_YEAR = 2;
  public static final int SPAN_ALL_TIME = 3;

  private static final Date DATE_THRESHOLDS[];

  private static final ArrayList<Model_Fueling> sThreeMonthSpan = new ArrayList<>();
  private static final ArrayList<Model_Fueling> sSixMonthSpan = new ArrayList<>();
  private static final ArrayList<Model_Fueling> sOneYearSpan = new ArrayList<>();
  private static final ArrayList<Model_Fueling> sLifetimeSpan = new ArrayList<>();
  private static final SparseArray<Model_Fueling> sFuelingList = new SparseArray<>(200);

  private static final String GEO_PREFIX = "geo:";
  private static final String GEO_NOT_CAPTURED = "<not captured>";
  private static final int INITIAL_ID = -1;

  static {
    DATE_THRESHOLDS = new Date[3];

    Calendar threeMonthsAgo = Calendar.getInstance();
    threeMonthsAgo.add(Calendar.MONTH, -3);             // Calculate 3 months before today
    DATE_THRESHOLDS[SPAN_3_MONTHS] = threeMonthsAgo.getTime();

    Calendar sixMonthsAgo = Calendar.getInstance();
    sixMonthsAgo.add(Calendar.MONTH, -6);              // Calculate 6 months before today
    DATE_THRESHOLDS[SPAN_6_MONTHS] = sixMonthsAgo.getTime();

    Calendar oneYearAgo = Calendar.getInstance();
    oneYearAgo.add(Calendar.YEAR, -1);                  // Calculate one mYear before today
    DATE_THRESHOLDS[SPAN_ONE_YEAR] = oneYearAgo.getTime();
  }

  private int mFuelingID = INITIAL_ID;
  private int mVehicleID = 0;
  private Date mDateOfFill = null;
  private float mDistance = 0f;
  private float mVolume = 0f;
  private float mPricePaid = 0f;
  private float mOdometer = 0f;
  private String mLocation = null;
  private float mLatitude = 0f;
  private float mLongitude = 0f;



    /*
    Constructors
    */

  /**
   * Generic constructor. New object's state will be NEW. All other fields will be null/0/empty.
   * <p/>
   * Note this object will not be added to the static SparseArray, as it may have been created
   * by the user selecting "Add", followed by the user canceling; we don't want to leave an
   * empty object in the SparseArray.
   */
  public Model_Fueling() {
  }

  /**
   * Constructor with all fields provided. Most likely after being read from the database.
   * This instance's state will be CURRENT.
   *
   * @param fuelingID   the unique database ID for the instance
   * @param vehicleID   foreign key to the vehicle which received this fueling
   * @param dateOfFill  the date of the fueling
   * @param distance    the distance driven between the prior fueling and this one
   * @param volume      the volume of fuel put into the car
   * @param pricePaid   the total price paid for the gas
   * @param odometer    the car's odometer reading; will not necessarily make sense with the distance,
   *                    if a fueling is missed (not recorded)
   * @param location    the location (city and state) of the fueling, or possibly something
   *                    more descriptive from the user
   * @param latitude    the latitude of the fueling, if known when the fueling occurred
   * @param longitude   the longitude of the fueling, if known when the fueling occurred
   * @param lastUpdated the last time this record was updated
   */
  public Model_Fueling(int fuelingID, int vehicleID, Date dateOfFill, float distance, float volume,
                       float pricePaid, float odometer, String location, float latitude,
                       float longitude, Date lastUpdated) {

    mFuelingID = fuelingID;
    mVehicleID = vehicleID;
    mDateOfFill = dateOfFill;
    mDistance = distance;
    mVolume = volume;
    mPricePaid = pricePaid;
    mOdometer = odometer;
    mLocation = location;
    mLatitude = latitude;
    mLongitude = longitude;
    mLastUpdated = lastUpdated;

    setCurrent();
    adjustLists();
  }



    /*
    Following methods pertain to adding and getting an instance to/from the static SparseArray.
     */

  /**
   * Adds a Model_Fueling to the static SparseArray used to grab an instance by ID. (Does not add
   * it to the list if it's ID has not yet been set--meaning it's a new object and not yet saved
   * to the database. Also checks to see if the object is already in the list.)
   *
   * @param f the Model_Fueling object to add
   */
  private static void addToSparseArray(Model_Fueling f) {
    if (f.mFuelingID != INITIAL_ID && sFuelingList.get(f.mFuelingID) == null)
      sFuelingList.append(f.mFuelingID, f);
  }

  /**
   * Retrieves a Model_Fueling instance by its ID.
   *
   * @param id The id of the Model_Fueling object desired
   * @return The Model_Fueling object associated with the provided ID, or null if there is none.
   */
  public static Model_Fueling getFueling(int id) {
    return sFuelingList.get(id);
  }



    /*
    Following are methods which calculate average values over time spans.
     */

  /**
   * Returns the appropriate ArrayList of Model_Fueling instances for the indicated time span
   *
   * @param span Model_Fueling.SPAN_3_MONTHS, Model_Fueling.SPAN_6_MONTHS, Model_Fueling.SPAN_ONE_YEAR or
   *             Model_Fueling.SPAN_ALL_TIME
   * @return an ArrayList of Model_Fueling instances
   * @throws IllegalArgumentException if the argument provided does not match one of the required
   *                                  constants.
   */
  private static ArrayList<Model_Fueling> getListForSpan(int span) throws IllegalArgumentException {
    ArrayList<Model_Fueling> listForSpan;

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
   *
   * @param span Model_Fueling.SPAN_3_MONTHS, Model_Fueling.SPAN_6_MONTHS, Model_Fueling.SPAN_ONE_YEAR or
   *             Model_Fueling.SPAN_ALL_TIME
   * @return the average (mean) distance for the fills in the indicated time span.
   */
  public static float getAvgDistanceOverSpan(int span) {
    ArrayList<Model_Fueling> fillsOverSpan;

    fillsOverSpan = getListForSpan(span);

    if (fillsOverSpan.isEmpty())
      return 0.0f;

    float totalDistance = 0.0f;

    for (Model_Fueling each : fillsOverSpan)
      totalDistance += each.mDistance;

    return round(totalDistance / fillsOverSpan.size(), 1);
  }

  /**
   * Calculates and returns the average (mean) volume for each fill in the indicated time span.
   * Rounded to 1 decimal place.
   *
   * @param span Model_Fueling.SPAN_3_MONTHS, Model_Fueling.SPAN_6_MONTHS, Model_Fueling.SPAN_ONE_YEAR or
   *             Model_Fueling.SPAN_ALL_TIME
   * @return the average (mean) distance for the fills in the indicated time span.
   */
  public static float getAvgVolumeOverSpan(int span) {
    ArrayList<Model_Fueling> fillsOverSpan;

    fillsOverSpan = getListForSpan(span);

    if (fillsOverSpan.isEmpty())
      return 0.0f;

    float totalVolume = 0.0f;

    for (Model_Fueling each : fillsOverSpan)
      totalVolume += each.mVolume;

    return round(totalVolume / fillsOverSpan.size(), 3);
  }

  /**
   * Calculates and returns the average (mean) Price Paid for each fill in the indicated time
   * span. Rounded to 2 decimal places.
   *
   * @param span Model_Fueling.SPAN_3_MONTHS, Model_Fueling.SPAN_6_MONTHS, Model_Fueling.SPAN_ONE_YEAR or
   *             Model_Fueling.SPAN_ALL_TIME
   * @return the average (mean) price paid for the fills in the indicated time span.
   */
  public static float getAvgPricePaidOverSpan(int span) throws IllegalArgumentException {
    ArrayList<Model_Fueling> fillsOverSpan;
    int countOfNonZeroEntries = 0;

    fillsOverSpan = getListForSpan(span);

    if (fillsOverSpan.isEmpty())
      return 0.0f;

    float totalPricePaid = 0.0f;

    for (Model_Fueling each : fillsOverSpan) {
      if (each.mPricePaid > 0.0f) {
        totalPricePaid += each.mPricePaid;
        countOfNonZeroEntries++;
      }
    }

    if (countOfNonZeroEntries == 0)
      return 0.0f;

    return round(totalPricePaid / countOfNonZeroEntries, 2);
  }

  /**
   * Calculates and returns the average (mean) Price Paid per Unit (gallon) over the indicated
   * time span. Rounded to 3 decimal places.
   *
   * @param span Model_Fueling.SPAN_3_MONTHS, Model_Fueling.SPAN_6_MONTHS, Model_Fueling.SPAN_ONE_YEAR or
   *             Model_Fueling.SPAN_ALL_TIME
   * @return the average (mean) price per unit over indicated time span.
   */
  public static float getAvgPricePerUnitOverSpan(int span) throws IllegalArgumentException {
    ArrayList<Model_Fueling> fillsOverSpan;

    fillsOverSpan = getListForSpan(span);

    if (fillsOverSpan.isEmpty())
      return 0.0f;

    float totalPricePaid = 0.0f;
    float totalVolume = 0.0f;

    for (Model_Fueling each : fillsOverSpan) {
      if (each.mPricePaid > 0.0f && each.mVolume > 0.0f) {
        totalPricePaid += each.mPricePaid;
        totalVolume += each.mVolume;
      }
    }

    if (totalVolume == 0.0f)
      return 0.0f;

    return round(totalPricePaid / totalVolume, 3);
  }

  /**
   * Calculates and returns the average (mean) Price Paid per Distance Unit (mile or kilometer)
   * over the indicated time span. Rounded to 3 decimal places.
   *
   * @param span Model_Fueling.SPAN_3_MONTHS, Model_Fueling.SPAN_6_MONTHS, Model_Fueling.SPAN_ONE_YEAR or
   *             Model_Fueling.SPAN_ALL_TIME
   * @return the average (mean) price per distance unit over indicated time span.
   */
  public static float getAvgPricePerDistanceOverSpan(int span) throws IllegalArgumentException {
    ArrayList<Model_Fueling> fillsOverSpan;

    fillsOverSpan = getListForSpan(span);

    if (fillsOverSpan.isEmpty())
      return 0.0f;

    float totalPricePaid = 0.0f;
    float totalDistance = 0.0f;

    for (Model_Fueling each : fillsOverSpan) {
      if (each.mPricePaid > 0.0f && each.mDistance > 0.0f) {
        totalPricePaid += each.mPricePaid;
        totalDistance += each.mDistance;
      }
    }

    if (totalDistance == 0.0f)
      return 0.0f;

    return round(totalPricePaid / totalDistance, 3);
  }

  /**
   * Calculates and returns the average (mean) distance per volume (mpg) over the indicated
   * time span. Rounded to 1 decimal place.
   *
   * @param span Model_Fueling.SPAN_3_MONTHS, Model_Fueling.SPAN_6_MONTHS or Model_Fueling.SPAN_ONE_YEAR
   * @return the average (mean) distance per volume of fuel (mpg) over indicated time span.
   */
  public static float getAvgEfficiencyOverSpan(int span) throws IllegalArgumentException {
    ArrayList<Model_Fueling> fillsOverSpan;

    fillsOverSpan = getListForSpan(span);

    if (fillsOverSpan.isEmpty())
      return 0.0f;

    float totalDistance = 0.0f;
    float totalVolume = 0.0f;

    for (Model_Fueling each : fillsOverSpan) {
      totalDistance += each.mDistance;
      totalVolume += each.mVolume;
    }

    if (totalVolume == 0.0f)
      return 0.0f;

    return round(totalDistance / totalVolume, 1);
  }





    /*
    Following are methods which get row counts from the arrays containing Model_Fueling instances
    in certain time spans.
     */

  /**
   * Provides the count of fills in the "last 3 months" list
   *
   * @return the size of the sThreeMonthSpan array
   */
  public static int getThreeMonthsRowCount() {
    return sThreeMonthSpan.size();
  }

  /**
   * Provides the count of fills in the "last 6 months" list
   *
   * @return the size of the sSixMonthSpan array
   */
  public static int getSixMonthsRowCount() {
    return sSixMonthSpan.size();
  }

  /**
   * Provides the count of fills in the "last mYear" list
   *
   * @return the size of the sOneYearSpan array
   */
  public static int getOneYearRowCount() {
    return sOneYearSpan.size();
  }

  /**
   * Returns the count of all fills in memory.
   *
   * @return the size of the sLifetimeSpan array
   */
  public static int getLifetimeRowCount() {
    return sLifetimeSpan.size();
  }





    /*
    The next few methods make adjustments to the arrays containing Model_Fueling instances
    in certain time spans.
     */

  /**
   * Removes all Fill Data from the arrays (sThreeMonthSpan, sSixMonthSpan, sOneYearSpan)
   * which are used for calculating averages over time.
   */
  public static void clearSpans() {
    sThreeMonthSpan.clear();
    sSixMonthSpan.clear();
    sOneYearSpan.clear();
    sLifetimeSpan.clear();
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
   * Removes the provided Model_Fueling instance from all ArrayLists
   *
   * @param fd The Model_Fueling instance to remove
   */
  public static void remove(Model_Fueling fd) {
    if (sThreeMonthSpan.contains(fd))
      sThreeMonthSpan.remove(fd);

    if (sSixMonthSpan.contains(fd))
      sSixMonthSpan.remove(fd);

    if (sOneYearSpan.contains(fd))
      sOneYearSpan.remove(fd);

    if (sLifetimeSpan.contains(fd))
      sLifetimeSpan.remove(fd);
  }

  /**
   * Returns a String representation of the indicated span.
   * @param span the desired span. The value passed must be one of:
   *             <ul>
   *             <li>Model_Fueling.SPAN_3_MONTHS</li>
   *             <li>Model_Fueling.SPAN_6_MONTHS</li>
   *             <li>Model_Fueling.SPAN_ONE_YEAR</li>
   *             <li>Model_Fueling.SPAN_ALL_TIME</li>
   *             </ul>
   * @return a String representing the provided span.
   */
  public static String getSpanPeriod(int span) {
    switch (span) {
      case SPAN_3_MONTHS:
        return "3 Months";
      case SPAN_6_MONTHS:
        return "6 months";
      case SPAN_ONE_YEAR:
        return "One Year";
      case SPAN_ALL_TIME:
        return "Lifetime";
      default:
        throw new IllegalArgumentException("Argument passed: " + span + " is not a valid span");
    }
  }





    /*
    The next few methods calculate values for a specific instance of Model_Fueling
     */

  /**
   * Accessor to entire list of Fuelings
   * @return list of all retrieved Fuelings
   */
  public static ArrayList<Model_Fueling> getFuelingList() {
    return sLifetimeSpan;
  }

  /**
   * Provides the number of Fuelings presently in memory (i.e., for the selected vehicle).
   * @return The nubmer of fuelings in memory.
   */
  public static int getCount() {
    return sLifetimeSpan.size();
  }

  /**
   * This will add (or remove) the instance to (from) the static lists of fill records
   * based on date. If the fill date (mDateOfFill) is within the last 3 or 6 months or
   * last mYear, this instance will get added to at least one of those lists.
   * <p/>
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

    if (mDateOfFill.after(DATE_THRESHOLDS[SPAN_ONE_YEAR])) {     // Should be in one-mYear list?
      if (!sOneYearSpan.contains(this))   //   Are we already there?
        sOneYearSpan.add(this);         //     If not, add to the list
    } else {                                    // Otherwise, we should NOT be in the list
      if (sOneYearSpan.contains(this))    //   But are we there?
        sOneYearSpan.remove(this);      //     If so, remove from the list
    }

    // Finally, regardless of date, add this to the list of all Model_Fueling instances as well as
    // to the SparseArray list.
    if (!sLifetimeSpan.contains(this))
      sLifetimeSpan.add(this);

    addToSparseArray(this);
  }

  /**
   * Returns the price per unit (price per gallon or litre) for the instance, rounded to
   * 3 decimal places
   *
   * @return the price per unit
   */
  public float getPricePerUnit() {
    if (mVolume == 0)
      return 0;

    return round(mPricePaid / mVolume, 3);
  }

  /**
   * Calculates the efficiency (mpg or lpk) for the instance, rounded to 1 decimal place
   *
   * @return Miles per gallon or litres per kilometer
   */
  public float getEfficiency() {
    if (mVolume == 0)
      return 0;

    return round(mDistance / mVolume, 1);
  }



    /*
    Next are the accessor methods
     */

  /**
   * Returns the price per distance unit traveled, rounded to 3 decimal places
   *
   * @return the price per distance unit
   */
  public float getPricePerDistance() {
    if (mDistance == 0)
      return 0;

    return round(mPricePaid / mDistance, 3);
  }

  /**
   * Getter for mFuelingID, the database prime key value of the row
   *
   * @return the database prime key value of the row
   */
  public int getFuelingID() {
    return mFuelingID;
  }

  /**
   * Setter for mFuelingID, the database prime key value of the row. This should only be called
   * when filling in the data retrieved from the database; i.e., this should not be used to
   * "update" the value but only to get the already existing value from the database.
   * <p/>
   * We add the instance to the SparseArray, because at the point the object is given an ID is
   * after it is written to the database, so it is "complete". (This method should not be
   * called for an object created from data already existing in the database.)
   *
   * @param fuelingID the database prime key value of the row
   * @throws UnsupportedOperationException if mFuelingID is not 0 when this method is called
   * @throws IllegalArgumentException      if the parameter provided is less than 1
   */
  public void setFuelingID(int fuelingID) throws UnsupportedOperationException, IllegalArgumentException {
    if (mFuelingID != INITIAL_ID)
      throw new UnsupportedOperationException("The Row ID can only be set once, and cannot be updated");
    if (fuelingID < 1)
      throw new IllegalArgumentException("The Row ID cannot be less than 1");

    mFuelingID = fuelingID;
    addToSparseArray(this);

    touch();
  }

  /**
   * Getter for record ID
   *
   * @return The database ID for the record
   */
  @Override
  public int getID() {
    return getFuelingID();
  }

  /**
   * Getter for mVehicleID, the unique identifier for the vehicle represented by the data
   *
   * @return the unique identifier for the vehicle represented by the data
   */
  public int getVehicleID() {
    return mVehicleID;
  }

  /**
   * Setter for mVehicleID, the unique identifier for the vehicle represented by the data
   *
   * @param vehicleID A unique identifier for the vehicle represented by the data
   */
  public void setVehicleID(int vehicleID) {
    mVehicleID = vehicleID;

    touch();
  }

  /**
   * Getter for mDateOfFill, a Date value indicating when the vehicle was filled with gas
   *
   * @return a Date value indicating when the vehicle was filled with gas
   */
  public Date getDateOfFill() {
    return mDateOfFill;
  }

  /**
   * Setter for mDateOfFill, a Date value indicating when the vehicle was filled with gas
   *
   * @param dateOfFill a Long value indicating the millis representation of the date
   */
  @SuppressWarnings("unused")
  public void setDateOfFill(long dateOfFill) {
    Date newDate = new Date(dateOfFill);
    setDateOfFill(newDate);
  }

  /**
   * Setter for mDateOfFill, a Date value indicating when the vehicle was filled with gas. After
   * setting the new value, adjustLists() is called to add this instance to (or remove it from)
   * appropriate arrays of Model_Fueling objects.
   *
   * @param dateOfFill a Date value indicating when the vehicle was filled with gas
   */
  public void setDateOfFill(Date dateOfFill) {
    mDateOfFill = dateOfFill;
    adjustLists();

    touch();
  }

  /**
   * Getter for mDistance, the distance traveled on the tank of gas
   *
   * @return the distance traveled on the tank of gas
   */
  public float getDistance() {
    return mDistance;
  }

  /**
   * Setter for mDistance, the distance traveled on the tank of gas
   *
   * @param distance the distance traveled on the tank of gas
   * @throws IllegalArgumentException if the volume argument is negative
   */
  public void setDistance(float distance) throws IllegalArgumentException {
    if (distance < 0)
      throw new IllegalArgumentException("distance argument cannot be negative");
    mDistance = distance;

    touch();
  }

  /**
   * Getter for mVolume, the volume of gas (in gallons or liters) on this fill
   *
   * @return the volume of gas (in gallons or liters) on this fill
   */
  public float getVolume() {
    return mVolume;
  }

  /**
   * Setter for mVolume, the volume of gas (in gallons or liters) on this fill
   *
   * @param volume the newVolume of gas (in gallons or liters) on this fill
   * @throws IllegalArgumentException if the volume argument is negative
   */
  public void setVolume(float volume) throws IllegalArgumentException {
    if (volume < 0)
      throw new IllegalArgumentException("volume argument cannot be negative");
    mVolume = volume;

    touch();
  }

  /**
   * Getter for mPricePaid, the total amount paid for the tank of gas
   *
   * @return the total amount paid for the tank of gas
   */
  public float getPricePaid() {
    return mPricePaid;
  }

  /**
   * Setter for mPricePaid, the total amount paid for the tank of gas
   *
   * @param pricePaid the total amount paid for the tank of gas
   * @throws IllegalArgumentException if the pricePaid argument is negative
   */
  public void setPricePaid(float pricePaid) throws IllegalArgumentException {
    if (pricePaid < 0)
      throw new IllegalArgumentException("pricePaid argument cannot be negative");
    mPricePaid = pricePaid;

    touch();
  }

  /**
   * Getter for mOdometer, the odometer reading when the tank was filled
   *
   * @return the odometer reading when the tank was filled
   */
  public float getOdometer() {
    return mOdometer;
  }

  /**
   * Setter for mOdometer, the odometer reading when the tank was filled
   *
   * @param odometer the odometer reading when the tank was filled
   * @throws IllegalArgumentException if the odometer value provided is negative
   */
  public void setOdometer(float odometer) throws IllegalArgumentException {
    if (odometer < 0)
      throw new IllegalArgumentException("odometer argument cannot be negative");
    mOdometer = odometer;

    touch();
  }

  /**
   * Getter for mLocation, the GPS location when the tank was filled
   *
   * @return the GPS location when the tank was filled
   */
  public String getLocation() {
    return mLocation;
  }

  /**
   * Setter for mLocation, the GPS location when the tank was filled
   *
   * @param location the GPS location when the tank was filled
   */
  public void setLocation(String location) {
    mLocation = location;

    touch();
  }

  /**
   * Getter for the mLatitude field
   *
   * @return the latitude, if it has been captured
   */
  public float getLatitude() {
    return mLatitude;
  }

  /**
   * Setter for the mLatitude field
   *
   * @param latitude the GPS coordinates
   */
  public void setLatitude(float latitude) {
    mLatitude = latitude;
  }

  /**
   * Getter for the mLongitude field
   *
   * @return the longitude, if it has been captured
   */
  public float getLongitude() {
    return mLongitude;
  }

  /**
   * Setter for the mLongitude field
   *
   * @param longitude the GPS coordinates
   */
  public void setLongitude(float longitude) {
    mLongitude = longitude;
  }

  /**
   * Returns a URI string suitable for passing to a map application or intent
   * @return a String in the form 'geo:[latitude],[longitude]'
   */
  public String getGeoURI() {
    return (isGeoURIValid() ? GEO_PREFIX + mLatitude + "," + mLongitude : GEO_NOT_CAPTURED);
  }

  /**
   * Indicates whether the latitude and longitude are valid.
   * @return true, if latitude + longitude != 0
   */
  public boolean isGeoURIValid() {
    return (mLatitude+mLongitude != 0f);
  }

  /**
   * Determines whether another, provided Model_Fueling instance is the same as this one.
   * All fields are checked, except for ID and LastUpdated. Strings are compared ignoring case.
   *
   * @param other the Model_Fueling instance this one is compared to
   * @return true if their data is the same
   */
  @SuppressWarnings("unused")
  public boolean equals(Model_Fueling other) {
    if (this == other) return true;
    if (this.getStatus() != other.getStatus()) return false;
    if (this.mVehicleID != other.mVehicleID) return false;
    if (!this.mDateOfFill.equals(other.mDateOfFill)) return false;
    if (this.mDistance != other.mDistance) return false;
    if (this.mVolume != other.mVolume) return false;
    if (this.mPricePaid != other.mPricePaid) return false;
    if (this.mOdometer != other.mOdometer) return false;
    if (!this.mLocation.equals(other.mLocation)) return false;
    if (this.mLatitude != other.mLatitude) return false;
    if (this.mLongitude != other.mLongitude) return false;
    //noinspection RedundantIfStatement
    if (!this.getLastUpdated().equals(other.getLastUpdated())) return false;

    return true;
  }

  /**
   * Determines whether another, provided Model_Fueling instance has the same key fields as
   * this one. Fields checked are mVehicleID, mDateOfFill, mDistance, mVolume, and mPricePaid.
   *
   * @param vehID      Model_Vehicle ID to be compared against
   * @param dateOfFill Date of Fill to be compared against
   * @param distance   Distance to be compared against
   * @param volume     Volume to be compared against
   * @param pricePaid  Price Paid to be compared against
   * @return true if all of the fields match
   */
  @SuppressWarnings("unused")
  public boolean isDuplicate(int vehID, Date dateOfFill, float distance, float volume, float
      pricePaid) {
    if (this.mVehicleID != vehID) return false;
    if (!this.mDateOfFill.equals(dateOfFill)) return false;
    if (this.mDistance != distance) return false;
    if (this.mVolume != volume) return false;
    //noinspection RedundantIfStatement
    if (this.mPricePaid != pricePaid) return false;

    return true;
  }

}
