/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by C. Todd Cook on 4/19/2016.
 * ctodd@ctoddcook.com
 */
public class FillDataTest {
    private static FillData fd5;
    private static FillData fd25;
    private static FillData fd55;
    private static FillData fd75;
    private static FillData fd105;
    private static FillData fd119;
    private static FillData fd121;
    private static FillData fd260;
    private static FillData fd350;
    private static FillData fd400;


    /**
     * This sets up 10 FillData objects, named "fd###" where ### is a number representing
     * how many days in the past the fill occurred. For example, fd25 has an mDateOfFill
     * for 25 days ago.
     *
     * Of the 10 FillData objects:
     * -- 3 of them are in the past 60 days, so they should be in all three arrays sFillsInLast60,
     *    sFillsInLast120 and sFillsInLastYear.
     * -- 3 more objects are in the past 120 days but not in the past 60, so they should be in both
     *    sFillsInLast60 and sFillsInLastYear.
     * -- 3 objects are in the last year, but not in the last 120 days, so they should be in only
     *    the sFillsInLastYear array.
     * -- 1 final object which is setup to be 400 days old, so it should be in none of the arrays.
     *
     * These objects can then be used for testing that the arrays are setup and updated correctly,
     * and that averages are being calculated correctly.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        Calendar cal;

        /*
        These first three FillData objects should all end up in the sFillsInLast60, sFillsInLast120
        and sFillsInLastYear arrays.
         */
        if (fd5 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -5);
            fd5 = new FillData();
            fd5.setDateOfFill(cal.getTime());
            fd5.setDistance(350.9);
            fd5.setPricePaid(38.45);
            fd5.setVolume(18.987);
        }

        if (fd25 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -25);
            fd25 = new FillData();
            fd25.setDateOfFill(cal.getTime());
            fd25.setDistance(297.6);
            fd25.setPricePaid(35.70);
            fd25.setVolume(17.702);
        }

        if (fd55 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -55);
            fd55 = new FillData();
            fd55.setDateOfFill(cal.getTime());
            fd55.setDistance(425.7);
            fd55.setPricePaid(43.40);
            fd55.setVolume(16.443);
        }

        /*
        These next three objects should end up in the sFillsInLast120 and sFillsInLastYear arrays.
        */
        if (fd75 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -75);
            fd75 = new FillData();
            fd75.setDateOfFill(cal.getTime());
            fd75.setDistance(350.9);
            fd75.setPricePaid(43.15);
            fd75.setVolume(17.499);
        }

        if (fd105 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -105);
            fd105 = new FillData();
            fd105.setDateOfFill(cal.getTime());
            fd105.setDistance(416.4);
            fd105.setPricePaid(27.27);
            fd105.setVolume(17.982);
        }

        if (fd119 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -119);
            fd119 = new FillData();
            fd119.setDateOfFill(cal.getTime());
            fd119.setDistance(524.1);
            fd119.setPricePaid(46.17);
            fd119.setVolume(19.243);
        }

        /*
        These next three objects should end up in the sFillsInLastYear only.
         */
        if (fd121 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -121);
            fd121 = new FillData();
            fd121.setDateOfFill(cal.getTime());
            fd121.setDistance(423.7);
            fd121.setPricePaid(36.23);
            fd121.setVolume(18.241);
        }

        if (fd260 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -260);
            fd260 = new FillData();
            fd260.setDateOfFill(cal.getTime());
            fd260.setDistance(360.1);
            fd260.setPricePaid(40.18);
            fd260.setVolume(19.032);
        }

        if (fd350 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -350);
            fd350 = new FillData();
            fd350.setDateOfFill(cal.getTime());
            fd350.setDistance(382.8);
            fd350.setPricePaid(39.04);
            fd350.setVolume(17.045);
        }

        /*
        This last object should not be found in any of the arrays.
         */
        if (fd400 == null ) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -400);
            fd400 = new FillData();
            fd400.setDateOfFill(cal.getTime());
            fd400.setDistance(402.8);
            fd400.setPricePaid(37.63);
            fd400.setVolume(18.543);
        }
    }

    /**
     * Used to reset the FillData instances and subsequently set them up again
     * @throws Exception
     */
    private void reset() throws Exception {
        fd5 = null;
        fd25 = null;
        fd55 = null;
        fd75 = null;
        fd105 = null;
        fd119 = null;
        fd121 = null;
        fd260 = null;
        fd350 = null;
        fd400 = null;

        this.setUp();
    }




    /*
    Test methods which manage the ArrayLists
     */

    /**
     * Check the size of the 4 ArrayLists of FillData objects containing the FillData instances
     * instantiated inthe setUp() method. These tests are very dependent on the number of
     * instances created and the mDataOfFill data set into them.
     * @throws Exception
     */
    @Test
    public void testCountsInArrays() throws Exception {
        Assert.assertEquals(3, FillData.get60DaysRowCount());
        Assert.assertEquals(6, FillData.get120DaysRowCount());
        Assert.assertEquals(9, FillData.getOneYearRowCount());
        Assert.assertEquals(10, FillData.getAllRowCount());
    }

    /**
     * Check the clearSpans() and clearAll() methods. These tests are very dependent on the number
     * of instances created and the mDataOfFill data set into them, in the setUp() method.
     * @throws Exception
     */
    @Test
    public void testArrayClears() throws Exception {
        FillData.clearSpans();
        Assert.assertEquals(0, FillData.get60DaysRowCount());   // Should be empty
        Assert.assertEquals(0, FillData.get120DaysRowCount());  // Should be empty
        Assert.assertEquals(0, FillData.getOneYearRowCount());  // Should be empty
        Assert.assertEquals(10, FillData.getAllRowCount());     // Should NOT be empty

        FillData.clearAll();
        Assert.assertEquals(0, FillData.getAllRowCount());      // Now, should be empty

        this.reset();       // Return arrays to "setup" state
    }

    /**
     * Test that a FillData object is properly included and later removed from ArrayLists.
     * We can't do this directly, since the ArrayLists are private. So we create a FillData and
     * set it's date, then check ArrayList counts, then call remove() and re-check the
     * ArrayList counts.
     * @throws Exception
     */
    @Test
    public void testFillDataInclusionAndRemoval() throws Exception {
        FillData fd = new FillData();
        Calendar cal = Calendar.getInstance();
        long count60 = FillData.get60DaysRowCount();
        long count120 = FillData.get120DaysRowCount();
        long countYear = FillData.getOneYearRowCount();
        long countAll = FillData.getAllRowCount();

        // By setting the date to 130 days ago, we should find that getOneYearRowCount() and
        // getAllRowCount() now return values which have increased by 1. The other two counts
        // should not have changed.
        cal.add(Calendar.DATE, -130);
        fd.setDateOfFill(cal.getTime());

        Assert.assertEquals(count60, FillData.get60DaysRowCount());         // s/b unchanged
        Assert.assertEquals(count120, FillData.get120DaysRowCount());       // s/b unchanged
        Assert.assertEquals(countYear+1, FillData.getOneYearRowCount());    // s/b one greater
        Assert.assertEquals(countAll+1, FillData.getAllRowCount());         // s/b one greater

        // Now we call remove, and re-check counts. All counts should then match
        // their original values.

        FillData.remove(fd);
        Assert.assertEquals(count60, FillData.get60DaysRowCount());         // s/b unchanged
        Assert.assertEquals(count120, FillData.get120DaysRowCount());       // s/b unchanged
        Assert.assertEquals(countYear, FillData.getOneYearRowCount());      // s/b back to original
        Assert.assertEquals(countAll, FillData.getAllRowCount());           // s/b back to original
    }




    /*
    Test methods which calculate data. (There are a couple of exceptions here.)
     */

    /**
     * Tests that the appropriate span constants are accepted. All of the getXxxOverSpan methods
     * call the private FillData.getListForSpan(), which checks the argument and throws
     * an exception if the argument is not acceptable.
     * @throws Exception
     */
    @Test
    public void testGetAvgDistanceShouldPassWithCorrectArgument() throws Exception {
        double result = 0d;

        result = FillData.getAvgDistanceOverSpan(FillData.SPAN_60_DAYS);       // expect no error
        result = FillData.getAvgDistanceOverSpan(FillData.SPAN_120_DAYS);      // expect no error
        result = FillData.getAvgDistanceOverSpan(FillData.SPAN_ONE_YEAR);      // expect no error
        result = FillData.getAvgDistanceOverSpan(FillData.SPAN_ALL_TIME);      // expect no error
    }

    /**
     * Pass a "bad" argument through to the private FillData.getListForSpan() to be sure
     * it throws an exception.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetAvgDistanceShouldFailOnWrongArgument() throws Exception {
        double result = 0d;
        result = FillData.getAvgDistanceOverSpan(55);                          // Should fail
    }

    /**
     * Test the calculation of miles per gallon (or kilometers per litre).
     * @throws Exception
     */
    @Test
    public void testSingleMPG() throws Exception {
        FillData fd = new FillData();
        fd.setVolume(18.987);
        fd.setDistance(350.9);
        Assert.assertEquals(18.5, fd5.getMileage());
    }

    /**
     * Test the "Span" arrays to be sure they return the correct average
     * mileage.
     * @throws Exception
     */
    @Test
    public void testMPGOverSpans() throws Exception {
        /*
        Set the distance and volume parameters here. They may have been set elsewhere, but we
        need to control the values here so we know what the results should be. IT'S IMPORTANT
        THAT THESE VALUES NOT BE CHANGED WITHOUT UPDATING THE EXPECTED VALUES IN THE ASSERT()
        STATEMENTS.
         */
        fd5.setDistance(350.9);
        fd5.setVolume(18.987);

        fd25.setDistance(297.6);
        fd25.setVolume(17.702);

        fd55.setDistance(425.7);
        fd55.setVolume(16.443);

        fd75.setDistance(350.9);
        fd75.setVolume(17.499);

        fd105.setDistance(416.4);
        fd105.setVolume(17.982);

        fd119.setDistance(524.1);
        fd119.setVolume(19.243);

        fd121.setDistance(423.7);
        fd121.setVolume(18.241);

        fd260.setDistance(360.1);
        fd260.setVolume(19.032);

        fd350.setDistance(382.8);
        fd350.setVolume(17.045);

        fd400.setDistance(443.7);
        fd400.setVolume(18.173);

        Assert.assertEquals(20.2, FillData.getAvgMileageOverSpan(FillData.SPAN_60_DAYS));
        Assert.assertEquals(21.9, FillData.getAvgMileageOverSpan(FillData.SPAN_120_DAYS));
        Assert.assertEquals(21.8, FillData.getAvgMileageOverSpan(FillData.SPAN_ONE_YEAR));
        Assert.assertEquals(22.0, FillData.getAvgMileageOverSpan(FillData.SPAN_ALL_TIME));
        }

    /**
     * Test the calculation of price per gallon (or litre).
     * @throws Exception
     */
    @Test
    public void testPricePerUnit() throws Exception {
        FillData fd = new FillData();

        fd.setVolume(18.987);
        fd.setPricePaid(37.39);

        Assert.assertEquals(1.969, fd.getPricePerUnit());
    }

    /**
     * Providing price paid and volume filled values, test the average price per gallon (or litre)
     * calculations over spans.
     * @throws Exception
     */
    @Test
    public void testPricePerUnitOverSpan() throws Exception {
        /*
        Set the price paid and volume parameters here. They may have been set elsewhere, but we
        need to control the values here so we know what the results should be. IT'S IMPORTANT
        THAT THESE VALUES NOT BE CHANGED WITHOUT UPDATING THE EXPECTED VALUES IN THE ASSERT()
        STATEMENTS.
         */
        fd5.setPricePaid(37.39);
        fd5.setVolume(18.987);

        fd25.setPricePaid(35.7);
        fd25.setVolume(17.702);

        fd55.setPricePaid(43.4);
        fd55.setVolume(16.443);

        fd75.setPricePaid(43.15);
        fd75.setVolume(17.499);

        fd105.setPricePaid(27.27);
        fd105.setVolume(17.982);

        fd119.setPricePaid(46.17);
        fd119.setVolume(19.243);

        fd121.setPricePaid(36.23);
        fd121.setVolume(18.241);

        fd260.setPricePaid(40.18);
        fd260.setVolume(19.032);

        fd350.setPricePaid(39.04);
        fd350.setVolume(17.045);

        fd400.setPricePaid(36.75);
        fd400.setVolume(18.173);

        Assert.assertEquals(2.192, FillData.getAvgPricePerUnitOverSpan(FillData.SPAN_60_DAYS));
        Assert.assertEquals(2.161, FillData.getAvgPricePerUnitOverSpan(FillData.SPAN_120_DAYS));
        Assert.assertEquals(2.149, FillData.getAvgPricePerUnitOverSpan(FillData.SPAN_ONE_YEAR));
        Assert.assertEquals(2.136, FillData.getAvgPricePerUnitOverSpan(FillData.SPAN_ALL_TIME));
    }

    /**
     * Tests the calculation of average total price paid per fill over each of the "span"
     * arrays.
     * @throws Exception
     */
    @Test
    public void testPricePaidOverSpan() throws Exception {
        /*
        Set the price paid parameters here. They may have been set elsewhere, but we
        need to control the values here so we know what the results should be. IT'S IMPORTANT
        THAT THESE VALUES NOT BE CHANGED WITHOUT UPDATING THE EXPECTED VALUES IN THE ASSERT()
        STATEMENTS.
         */
        fd5.setPricePaid(37.39);
        fd25.setPricePaid(35.7);
        fd55.setPricePaid(43.4);
        fd75.setPricePaid(43.15);
        fd105.setPricePaid(27.27);
        fd119.setPricePaid(46.17);
        fd121.setPricePaid(36.23);
        fd260.setPricePaid(40.18);
        fd350.setPricePaid(39.04);
        fd400.setPricePaid(36.75);

        Assert.assertEquals(38.83, FillData.getAvgPricePaidOverSpan(FillData.SPAN_60_DAYS));
        Assert.assertEquals(38.85, FillData.getAvgPricePaidOverSpan(FillData.SPAN_120_DAYS));
        Assert.assertEquals(38.73, FillData.getAvgPricePaidOverSpan(FillData.SPAN_ONE_YEAR));
        Assert.assertEquals(38.53, FillData.getAvgPricePaidOverSpan(FillData.SPAN_ALL_TIME));
    }

    @Test
    public void testVolumeOverSpan() throws Exception {
                /*
        Set the volume parameters here. They may have been set elsewhere, but we
        need to control the values here so we know what the results should be. IT'S IMPORTANT
        THAT THESE VALUES NOT BE CHANGED WITHOUT UPDATING THE EXPECTED VALUES IN THE ASSERT()
        STATEMENTS.
         */
        fd5.setVolume(18.987);
        fd25.setVolume(17.702);
        fd55.setVolume(16.443);
        fd75.setVolume(17.499);
        fd105.setVolume(17.982);
        fd119.setVolume(19.243);
        fd121.setVolume(18.241);
        fd260.setVolume(19.032);
        fd350.setVolume(17.045);
        fd400.setVolume(18.173);

        Assert.assertEquals(17.7, FillData.getAvgVolumeOverSpan(FillData.SPAN_60_DAYS));
        Assert.assertEquals(18.0, FillData.getAvgVolumeOverSpan(FillData.SPAN_120_DAYS));
        Assert.assertEquals(18.0, FillData.getAvgVolumeOverSpan(FillData.SPAN_ONE_YEAR));
        Assert.assertEquals(18.0, FillData.getAvgVolumeOverSpan(FillData.SPAN_ALL_TIME));
    }

    @Test
    public void testDistanceOverSpan() throws Exception {
                /*
        Set the distance parameters here. They may have been set elsewhere, but we
        need to control the values here so we know what the results should be. IT'S IMPORTANT
        THAT THESE VALUES NOT BE CHANGED WITHOUT UPDATING THE EXPECTED VALUES IN THE ASSERT()
        STATEMENTS.
         */
        fd5.setDistance(350.9);
        fd25.setDistance(297.6);
        fd55.setDistance(425.7);
        fd75.setDistance(350.9);
        fd105.setDistance(416.4);
        fd119.setDistance(524.1);
        fd121.setDistance(423.7);
        fd260.setDistance(360.1);
        fd350.setDistance(382.8);
        fd400.setDistance(443.7);

        Assert.assertEquals(358.1, FillData.getAvgDistanceOverSpan(FillData.SPAN_60_DAYS));
        Assert.assertEquals(394.3, FillData.getAvgDistanceOverSpan(FillData.SPAN_120_DAYS));
        Assert.assertEquals(392.5, FillData.getAvgDistanceOverSpan(FillData.SPAN_ONE_YEAR));
        Assert.assertEquals(397.6, FillData.getAvgDistanceOverSpan(FillData.SPAN_ALL_TIME));
    }

    /**
     * Test the "span" arrays to be sure they return the correct calculation for average
     * price/distance
     * @throws Exception
     */
    @Test
    public void testPricePerDistanceOverSpan() throws Exception {
        /*
        Set the distance and volume parameters here. They may have been set elsewhere, but we
        need to control the values here so we know what the results should be. IT'S IMPORTANT
        THAT THESE VALUES NOT BE CHANGED WITHOUT UPDATING THE EXPECTED VALUES IN THE ASSERT()
        STATEMENTS.
         */

        fd5.setDistance(350.9);
        fd5.setPricePaid(37.39);

        fd25.setDistance(297.6);
        fd25.setPricePaid(35.7);

        fd55.setDistance(425.7);
        fd55.setPricePaid(43.4);

        fd75.setDistance(350.9);
        fd75.setPricePaid(43.15);

        fd105.setDistance(416.4);
        fd105.setPricePaid(27.27);

        fd119.setDistance(524.1);
        fd119.setPricePaid(46.17);

        fd121.setDistance(423.7);
        fd121.setPricePaid(36.23);

        fd260.setDistance(360.1);
        fd260.setPricePaid(40.18);

        fd350.setDistance(382.8);
        fd350.setPricePaid(39.04);

        fd400.setDistance(443.7);
        fd400.setPricePaid(36.75);

        Assert.assertEquals(0.108, FillData.getAvgPricePerDistanceOverSpan(FillData.SPAN_60_DAYS));
        Assert.assertEquals(0.099, FillData.getAvgPricePerDistanceOverSpan(FillData.SPAN_120_DAYS));
        Assert.assertEquals(0.099, FillData.getAvgPricePerDistanceOverSpan(FillData.SPAN_ONE_YEAR));
        Assert.assertEquals(0.097, FillData.getAvgPricePerDistanceOverSpan(FillData.SPAN_ALL_TIME));
    }

    /**
     * Test the calculation of price per mile (or kilometer)
     * @throws Exception
     */
    @Test
    public void testPricePerDistance() throws Exception {
        FillData fd = new FillData();

        fd.setDistance(382.8);
        fd.setPricePaid(37.39);

        Assert.assertEquals(0.098, fd.getPricePerDistance());
    }





    /*
    Test accessor methods
    */

    /**
     * Test that an exception is thrown if a value less than 1 is provided as a row ID.
     * @throws Exception
     */
    @Test (expected = IllegalArgumentException.class)
    public void testRowIDAccessorsIllegalArgument() throws Exception {
        FillData fd = new FillData();

        fd.setRowID(0);
    }

    /**
     * Test that an exception is thrown if an attempt is made to update an existing row ID.
     * @throws Exception
     */
    @Test (expected = UnsupportedOperationException.class)
    public void testRowIDAccessorsUnsupportedOp() throws Exception {
        FillData fd = new FillData();

        fd.setRowID(4);     // This one should work fine
        fd.setRowID(5);     // This one should throw an exception
    }

    /**
     * Test the accessors for mRowID
     * @throws Exception
     */
    @Test
    public void testRowIDAccessors() throws Exception {
        FillData fd = new FillData();

        fd.setRowID(5);
        Assert.assertEquals(5, fd.getRowID());
    }

    /**
     * Test the accessors for mVehicleID
     * @throws Exception
     */
    @Test
    public void testVehicleIDAccessors() throws Exception {
        FillData fd = new FillData();

        fd.setVehicleID(17);
        Assert.assertEquals(17, fd.getVehicleID());
    }

    /**
     * Test the mDateOfFill accessors. The method setDateOfFill() has the downstream affect of
     * adding (or removing) a FillData instance to (from) span ArrayLists; because of that, we don't
     * create a new FillData object (because many tests in this suite of tests depend on the right
     * number of FillData instances in those ArrayLists) AND we reset the mDateOfFill field back
     * to its original value on the existing FillData object we use for this test.
     * @throws Exception
     */
    @Test
    public void testDateOfFillAccessors() throws Exception {
        // We use an EXISTING FillData object rather than creating a new one, so we don't
        // add any more FillData objects to the span ArrayLists.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -10);
        Date newDate = cal.getTime();
        Date oldDate = fd5.getDateOfFill();    // save the original date of fill

        fd5.setDateOfFill(newDate);
        Assert.assertEquals(newDate, fd5.getDateOfFill());

        fd5.setDateOfFill(oldDate);            // set fd5 back to the way it was
    }

    /**
     * Test the mDistance field accessor methods.
     * @throws Exception
     */
    @Test
    public void testDistanceAccessors() throws Exception {
        FillData fd = new FillData();
        double dist = 512.6;

        fd.setDistance(dist);
        Assert.assertEquals(dist, fd.getDistance());
    }

    /**
     * Test the mVolume field accessor methods
     * @throws Exception
     */
    @Test
    public void testVolumeAccessors() throws Exception {
        FillData fd = new FillData();
        double vol = 19.7;

        fd.setVolume(vol);
        Assert.assertEquals(vol, fd.getVolume());
    }

    @Test
    public void testPricePaidAccessors() throws Exception {
        FillData fd = new FillData();
        double paid = 32.57;

        fd.setPricePaid(paid);
        Assert.assertEquals(paid, fd.getPricePaid());
    }

    /**
     * Test the accessors for mOdometer
     * @throws Exception
     */
    @Test
    public void testOdometerAccessors() throws Exception {
        FillData fd = new FillData();

        fd.setOdometer(54);
        Assert.assertEquals(54.0, fd.getOdometer());
    }

    /**
     * Test the accessors for mLocation
     * @throws Exception
     */
    @Test
    public void testLocationAccessors() throws Exception {
        FillData fd = new FillData();

        fd.setLocation(1818);
        Assert.assertEquals(1818.0, fd.getLocation());
    }

    /**
     * Test the accessors for mStatus
     * @throws Exception
     */
    @Test
    public void testStatusAccessors() throws Exception {
        FillData fd = new FillData();

        fd.setStatus(FillData.STATUS_NEW);
        Assert.assertEquals(FillData.STATUS_NEW, fd.getStatus());

        fd.setStatus(FillData.STATUS_CURRENT);
        Assert.assertEquals(FillData.STATUS_CURRENT, fd.getStatus());

        fd.setStatus(FillData.STATUS_UPDATED);
        Assert.assertEquals(FillData.STATUS_UPDATED, fd.getStatus());
    }

    /**
     * Test that the mStatus accessor throws an exception if a bad argument is passed
     * @throws Exception
     */
    @Test (expected = IllegalArgumentException.class)
    public void testStatusAccessorIllegalArg() throws Exception {
        FillData fd = new FillData();

        fd.setStatus("F");
    }

}