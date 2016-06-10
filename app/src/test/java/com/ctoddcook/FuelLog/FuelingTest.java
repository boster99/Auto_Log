/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.FuelLog;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by C. Todd Cook on 4/19/2016.
 * ctodd@ctoddcook.com
 */
public class FuelingTest {
    private static Fueling fd5;
    private static Fueling fd25;
    private static Fueling fd55;

    private static Fueling fd95;
    private static Fueling fd105;
    private static Fueling fd169;

    private static Fueling fd191;
    private static Fueling fd260;
    private static Fueling fd350;

    private static Fueling fd400;


    /**
     * This sets up 10 Fueling objects, named "fd###" where ### is a number representing
     * how many days in the past the fill occurred. For example, fd25 has an mDateOfFill
     * for 25 days ago.
     *
     * Of the 10 Fueling objects:
     * -- 3 of them are in the past 60 days, so they should be in all three arrays sFillsInLast60,
     *    sFillsInLast120 and sFillsInLastYear.
     * -- 3 more objects are in the past 120 days but not in the past 60, so they should be in both
     *    sFillsInLast60 and sFillsInLastYear.
     * -- 3 objects are in the last mYear, but not in the last 120 days, so they should be in only
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
        These first three Fueling objects should all end up in the sFillsInLast60, sFillsInLast120
        and sFillsInLastYear arrays.
         */
        if (fd5 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -5);
            fd5 = new Fueling();
            fd5.setDateOfFill(cal.getTime());
            fd5.setDistance(350.9f);
            fd5.setPricePaid(38.45f);
            fd5.setVolume(18.987f);
        }

        if (fd25 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -25);
            fd25 = new Fueling();
            fd25.setDateOfFill(cal.getTime());
            fd25.setDistance(297.6f);
            fd25.setPricePaid(35.70f);
            fd25.setVolume(17.702f);
        }

        if (fd55 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -55);
            fd55 = new Fueling();
            fd55.setDateOfFill(cal.getTime());
            fd55.setDistance(425.7f);
            fd55.setPricePaid(43.40f);
            fd55.setVolume(16.443f);
        }

        /*
        These next three objects should end up in the sFillsInLast120 and sFillsInLastYear arrays.
        */
        if (fd95 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -95);
            fd95 = new Fueling();
            fd95.setDateOfFill(cal.getTime());
            fd95.setDistance(350.9f);
            fd95.setPricePaid(43.15f);
            fd95.setVolume(17.499f);
        }

        if (fd105 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -105);
            fd105 = new Fueling();
            fd105.setDateOfFill(cal.getTime());
            fd105.setDistance(416.4f);
            fd105.setPricePaid(27.27f);
            fd105.setVolume(17.982f);
        }

        if (fd169 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -169);
            fd169 = new Fueling();
            fd169.setDateOfFill(cal.getTime());
            fd169.setDistance(524.1f);
            fd169.setPricePaid(46.17f);
            fd169.setVolume(19.243f);
        }

        /*
        These next three objects should end up in the sFillsInLastYear only.
         */
        if (fd191 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -191);
            fd191 = new Fueling();
            fd191.setDateOfFill(cal.getTime());
            fd191.setDistance(423.7f);
            fd191.setPricePaid(36.23f);
            fd191.setVolume(18.241f);
        }

        if (fd260 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -260);
            fd260 = new Fueling();
            fd260.setDateOfFill(cal.getTime());
            fd260.setDistance(360.1f);
            fd260.setPricePaid(40.18f);
            fd260.setVolume(19.032f);
        }

        if (fd350 == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -350);
            fd350 = new Fueling();
            fd350.setDateOfFill(cal.getTime());
            fd350.setDistance(382.8f);
            fd350.setPricePaid(39.04f);
            fd350.setVolume(17.045f);
        }

        /*
        This last object should not be found in any of the arrays.
         */
        if (fd400 == null ) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -400);
            fd400 = new Fueling();
            fd400.setDateOfFill(cal.getTime());
            fd400.setDistance(402.8f);
            fd400.setPricePaid(37.63f);
            fd400.setVolume(18.543f);
        }
    }

    /**
     * Used to reset the Fueling instances and subsequently set them up again
     * @throws Exception
     */
    private void reset() throws Exception {
        fd5 = null;
        fd25 = null;
        fd55 = null;
        fd95 = null;
        fd105 = null;
        fd169 = null;
        fd191 = null;
        fd260 = null;
        fd350 = null;
        fd400 = null;

        this.setUp();
    }




    /*
    Test methods which manage the ArrayLists
     */

    /**
     * Check the size of the 4 ArrayLists of Fueling objects containing the Fueling instances
     * instantiated in the setUp() method. These tests are very dependent on the number of
     * instances created and the mDataOfFill data set into them.
     * @throws Exception
     */
    @Test
    public void testCountsInArrays() throws Exception {
        Assert.assertEquals(3, Fueling.getThreeMonthsRowCount());
        Assert.assertEquals(6, Fueling.getSixMonthsRowCount());
        Assert.assertEquals(9, Fueling.getOneYearRowCount());
        Assert.assertEquals(10, Fueling.getLifetimeRowCount());
    }

    /**
     * Check the clearSpans() and clearAll() methods. These tests are very dependent on the number
     * of instances created and the mDataOfFill data set into them, in the setUp() method.
     * @throws Exception
     */
    @Test
    public void testArrayClears() throws Exception {
        Fueling.clearSpans();
        Assert.assertEquals(0, Fueling.getThreeMonthsRowCount());   // Should be empty
        Assert.assertEquals(0, Fueling.getSixMonthsRowCount());  // Should be empty
        Assert.assertEquals(0, Fueling.getOneYearRowCount());  // Should be empty
        Assert.assertEquals(10, Fueling.getLifetimeRowCount());     // Should NOT be empty

        Fueling.clearAll();
        Assert.assertEquals(0, Fueling.getLifetimeRowCount());      // Now, should be empty

        this.reset();       // Return arrays to "setup" state
    }

    /**
     * Test that a Fueling object is properly included and later removed from ArrayLists.
     * We can't do this directly, since the ArrayLists are private. So we create a Fueling and
     * set it's date, then check ArrayList counts, then call remove() and re-check the
     * ArrayList counts.
     * @throws Exception
     */
    @Test
    public void testFillDataInclusionAndRemoval() throws Exception {
        Fueling fd = new Fueling();
        Calendar cal = Calendar.getInstance();
        long count60 = Fueling.getThreeMonthsRowCount();
        long count120 = Fueling.getSixMonthsRowCount();
        long countYear = Fueling.getOneYearRowCount();
        long countAll = Fueling.getLifetimeRowCount();

        // By setting the date to 230 days ago, we should find that getOneYearRowCount() and
        // getLifetimeRowCount() now return values which have increased by 1. The other two counts
        // should not have changed.
        cal.add(Calendar.DATE, -230);
        fd.setDateOfFill(cal.getTime());

        Assert.assertEquals(count60, Fueling.getThreeMonthsRowCount());         // s/b unchanged
        Assert.assertEquals(count120, Fueling.getSixMonthsRowCount());       // s/b unchanged
        Assert.assertEquals(countYear+1, Fueling.getOneYearRowCount());    // s/b one greater
        Assert.assertEquals(countAll+1, Fueling.getLifetimeRowCount());         // s/b one greater

        // Now we call remove, and re-check counts. All counts should then match
        // their original values.

        Fueling.remove(fd);
        Assert.assertEquals(count60, Fueling.getThreeMonthsRowCount());         // s/b unchanged
        Assert.assertEquals(count120, Fueling.getSixMonthsRowCount());       // s/b unchanged
        Assert.assertEquals(countYear, Fueling.getOneYearRowCount());      // s/b back to original
        Assert.assertEquals(countAll, Fueling.getLifetimeRowCount());           // s/b back to original
    }




    /*
    Test methods which calculate data. (There are a couple of exceptions here.)
     */

    /**
     * Tests that the appropriate span constants are accepted. All of the getXxxOverSpan methods
     * call the private Fueling.getListForSpan(), which checks the argument and throws
     * an exception if the argument is not acceptable.
     * @throws Exception
     */
    @Test
    public void testGetAvgDistanceShouldPassWithCorrectArgument() throws Exception {
        Fueling.getAvgDistanceOverSpan(Fueling.SPAN_3_MONTHS);      // expect no error
        Fueling.getAvgDistanceOverSpan(Fueling.SPAN_6_MONTHS);      // expect no error
        Fueling.getAvgDistanceOverSpan(Fueling.SPAN_ONE_YEAR);      // expect no error
        Fueling.getAvgDistanceOverSpan(Fueling.SPAN_ALL_TIME);      // expect no error
    }

    /**
     * Pass a "bad" argument through to the private Fueling.getListForSpan() to be sure
     * it throws an exception.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetAvgDistanceShouldFailOnWrongArgument() throws Exception {
        Fueling.getAvgDistanceOverSpan(55);                          // Should fail
    }

    /**
     * Test the calculation of miles per gallon (or kilometers per litre).
     * @throws Exception
     */
    @Test
    public void testSingleMPG() throws Exception {
        Fueling fd = new Fueling();
        fd.setVolume(18.987f);
        fd.setDistance(350.9f);
        Assert.assertEquals(18.5f, fd5.getEfficiency());
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
        fd5.setDistance(350.9f);
        fd5.setVolume(18.987f);

        fd25.setDistance(297.6f);
        fd25.setVolume(17.702f);

        fd55.setDistance(425.7f);
        fd55.setVolume(16.443f);

        fd95.setDistance(350.9f);
        fd95.setVolume(17.499f);

        fd105.setDistance(416.4f);
        fd105.setVolume(17.982f);

        fd169.setDistance(524.1f);
        fd169.setVolume(19.243f);

        fd191.setDistance(423.7f);
        fd191.setVolume(18.241f);

        fd260.setDistance(360.1f);
        fd260.setVolume(19.032f);

        fd350.setDistance(382.8f);
        fd350.setVolume(17.045f);

        fd400.setDistance(443.7f);
        fd400.setVolume(18.173f);

        Assert.assertEquals(20.2f, Fueling.getAvgEfficiencyOverSpan(Fueling.SPAN_3_MONTHS));
        Assert.assertEquals(21.9f, Fueling.getAvgEfficiencyOverSpan(Fueling.SPAN_6_MONTHS));
        Assert.assertEquals(21.8f, Fueling.getAvgEfficiencyOverSpan(Fueling.SPAN_ONE_YEAR));
        Assert.assertEquals(22.0f, Fueling.getAvgEfficiencyOverSpan(Fueling.SPAN_ALL_TIME));
        }

    /**
     * Test the calculation of price per gallon (or litre).
     * @throws Exception
     */
    @Test
    public void testPricePerUnit() throws Exception {
        Fueling fd = new Fueling();

        fd.setVolume(18.987f);
        fd.setPricePaid(37.39f);

        Assert.assertEquals(1.969f, fd.getPricePerUnit());
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
        fd5.setPricePaid(37.39f);
        fd5.setVolume(18.987f);

        fd25.setPricePaid(35.7f);
        fd25.setVolume(17.702f);

        fd55.setPricePaid(43.4f);
        fd55.setVolume(16.443f);

        fd95.setPricePaid(43.15f);
        fd95.setVolume(17.499f);

        fd105.setPricePaid(27.27f);
        fd105.setVolume(17.982f);

        fd169.setPricePaid(46.17f);
        fd169.setVolume(19.243f);

        fd191.setPricePaid(36.23f);
        fd191.setVolume(18.241f);

        fd260.setPricePaid(40.18f);
        fd260.setVolume(19.032f);

        fd350.setPricePaid(39.04f);
        fd350.setVolume(17.045f);

        fd400.setPricePaid(36.75f);
        fd400.setVolume(18.173f);

        Assert.assertEquals(2.192f, Fueling.getAvgPricePerUnitOverSpan(Fueling
                .SPAN_3_MONTHS));
        Assert.assertEquals(2.161f, Fueling.getAvgPricePerUnitOverSpan(Fueling
                .SPAN_6_MONTHS));
        Assert.assertEquals(2.149f, Fueling.getAvgPricePerUnitOverSpan(Fueling
                .SPAN_ONE_YEAR));
        Assert.assertEquals(2.136f, Fueling.getAvgPricePerUnitOverSpan(Fueling
                .SPAN_ALL_TIME));
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
        fd5.setPricePaid(37.39f);
        fd25.setPricePaid(35.7f);
        fd55.setPricePaid(43.4f);
        fd95.setPricePaid(43.15f);
        fd105.setPricePaid(27.27f);
        fd169.setPricePaid(46.17f);
        fd191.setPricePaid(36.23f);
        fd260.setPricePaid(40.18f);
        fd350.setPricePaid(39.04f);
        fd400.setPricePaid(36.75f);

        Assert.assertEquals(38.83f, Fueling.getAvgPricePaidOverSpan(Fueling.SPAN_3_MONTHS));
        Assert.assertEquals(38.85f, Fueling.getAvgPricePaidOverSpan(Fueling.SPAN_6_MONTHS));
        Assert.assertEquals(38.73f, Fueling.getAvgPricePaidOverSpan(Fueling.SPAN_ONE_YEAR));
        Assert.assertEquals(38.53f, Fueling.getAvgPricePaidOverSpan(Fueling.SPAN_ALL_TIME));
    }

    @Test
    public void testVolumeOverSpan() throws Exception {
                /*
        Set the volume parameters here. They may have been set elsewhere, but we
        need to control the values here so we know what the results should be. IT'S IMPORTANT
        THAT THESE VALUES NOT BE CHANGED WITHOUT UPDATING THE EXPECTED VALUES IN THE ASSERT()
        STATEMENTS.
         */
        fd5.setVolume(18.987f);
        fd25.setVolume(17.702f);
        fd55.setVolume(16.443f);
        fd95.setVolume(17.499f);
        fd105.setVolume(17.982f);
        fd169.setVolume(19.243f);
        fd191.setVolume(18.241f);
        fd260.setVolume(19.032f);
        fd350.setVolume(17.045f);
        fd400.setVolume(18.173f);

        Assert.assertEquals(17.7f, Fueling.getAvgVolumeOverSpan(Fueling.SPAN_3_MONTHS));
        Assert.assertEquals(18.0f, Fueling.getAvgVolumeOverSpan(Fueling.SPAN_6_MONTHS));
        Assert.assertEquals(18.0f, Fueling.getAvgVolumeOverSpan(Fueling.SPAN_ONE_YEAR));
        Assert.assertEquals(18.0f, Fueling.getAvgVolumeOverSpan(Fueling.SPAN_ALL_TIME));
    }

    @Test
    public void testDistanceOverSpan() throws Exception {
                /*
        Set the distance parameters here. They may have been set elsewhere, but we
        need to control the values here so we know what the results should be. IT'S IMPORTANT
        THAT THESE VALUES NOT BE CHANGED WITHOUT UPDATING THE EXPECTED VALUES IN THE ASSERT()
        STATEMENTS.
         */
        fd5.setDistance(350.9f);
        fd25.setDistance(297.6f);
        fd55.setDistance(425.7f);
        fd95.setDistance(350.9f);
        fd105.setDistance(416.4f);
        fd169.setDistance(524.1f);
        fd191.setDistance(423.7f);
        fd260.setDistance(360.1f);
        fd350.setDistance(382.8f);
        fd400.setDistance(443.7f);

        Assert.assertEquals(358.1f, Fueling.getAvgDistanceOverSpan(Fueling.SPAN_3_MONTHS));
        Assert.assertEquals(394.3f, Fueling.getAvgDistanceOverSpan(Fueling.SPAN_6_MONTHS));
        Assert.assertEquals(392.5f, Fueling.getAvgDistanceOverSpan(Fueling.SPAN_ONE_YEAR));
        Assert.assertEquals(397.6f, Fueling.getAvgDistanceOverSpan(Fueling.SPAN_ALL_TIME));
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

        fd5.setDistance(350.9f);
        fd5.setPricePaid(37.39f);

        fd25.setDistance(297.6f);
        fd25.setPricePaid(35.7f);

        fd55.setDistance(425.7f);
        fd55.setPricePaid(43.4f);

        fd95.setDistance(350.9f);
        fd95.setPricePaid(43.15f);

        fd105.setDistance(416.4f);
        fd105.setPricePaid(27.27f);

        fd169.setDistance(524.1f);
        fd169.setPricePaid(46.17f);

        fd191.setDistance(423.7f);
        fd191.setPricePaid(36.23f);

        fd260.setDistance(360.1f);
        fd260.setPricePaid(40.18f);

        fd350.setDistance(382.8f);
        fd350.setPricePaid(39.04f);

        fd400.setDistance(443.7f);
        fd400.setPricePaid(36.75f);

        Assert.assertEquals(0.108f, Fueling.getAvgPricePerDistanceOverSpan(Fueling
                .SPAN_3_MONTHS));
        Assert.assertEquals(0.099f, Fueling.getAvgPricePerDistanceOverSpan(Fueling
                .SPAN_6_MONTHS));
        Assert.assertEquals(0.099f, Fueling.getAvgPricePerDistanceOverSpan(Fueling
                .SPAN_ONE_YEAR));
        Assert.assertEquals(0.097f, Fueling.getAvgPricePerDistanceOverSpan(Fueling
                .SPAN_ALL_TIME));
    }

    /**
     * Test the calculation of price per mile (or kilometer)
     * @throws Exception
     */
    @Test
    public void testPricePerDistance() throws Exception {
        Fueling fd = new Fueling();

        fd.setDistance(382.8f);
        fd.setPricePaid(37.39f);

        Assert.assertEquals(0.098f, fd.getPricePerDistance());
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
        Fueling fd = new Fueling();

        fd.setFuelingID(0);
    }

    /**
     * Test that an exception is thrown if an attempt is made to update an existing row ID.
     * @throws Exception
     */
    @Test (expected = UnsupportedOperationException.class)
    public void testRowIDAccessorsUnsupportedOp() throws Exception {
        Fueling fd = new Fueling();

        fd.setFuelingID(4);     // This one should work fine
        fd.setFuelingID(5);     // This one should throw an exception
    }

    /**
     * Test the accessors for mRowID
     * @throws Exception
     */
    @Test
    public void testRowIDAccessors() throws Exception {
        Fueling fd = new Fueling();

        fd.setFuelingID(5);
        Assert.assertEquals(5, fd.getFuelingID());
    }

    /**
     * Test the accessors for mVehicleID
     * @throws Exception
     */
    @Test
    public void testVehicleIDAccessors() throws Exception {
        Fueling fd = new Fueling();

        fd.setVehicleID(17);
        Assert.assertEquals(17, fd.getVehicleID());
    }

    /**
     * Test the mDateOfFill accessors. The method setDateOfFill() has the downstream affect of
     * adding (or removing) a Fueling instance to (from) span ArrayLists; because of that, we don't
     * create a new Fueling object (because many tests in this suite of tests depend on the right
     * number of Fueling instances in those ArrayLists) AND we reset the mDateOfFill field back
     * to its original value on the existing Fueling object we use for this test.
     * @throws Exception
     */
    @Test
    public void testDateOfFillAccessors() throws Exception {
        // We use an EXISTING Fueling object rather than creating a new one, so we don't
        // add any more Fueling objects to the span ArrayLists.
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
        Fueling fd = new Fueling();
        float dist = 512.6f;

        fd.setDistance(dist);
        Assert.assertEquals(dist, fd.getDistance());
    }

    /**
     * Test the mVolume field accessor methods
     * @throws Exception
     */
    @Test
    public void testVolumeAccessors() throws Exception {
        Fueling fd = new Fueling();
        float vol = 19.7f;

        fd.setVolume(vol);
        Assert.assertEquals(vol, fd.getVolume());
    }

    @Test
    public void testPricePaidAccessors() throws Exception {
        Fueling fd = new Fueling();
        float paid = 32.57f;

        fd.setPricePaid(paid);
        Assert.assertEquals(paid, fd.getPricePaid());
    }

    /**
     * Test the accessors for mOdometer
     * @throws Exception
     */
    @Test
    public void testOdometerAccessors() throws Exception {
        Fueling fd = new Fueling();

        fd.setOdometer(54);
        Assert.assertEquals(54.0f, fd.getOdometer());
    }

    /**
     * Test the accessors for mLocation
     * @throws Exception
     */
    @Test
    public void testLocationAccessors() throws Exception {
        Fueling fd = new Fueling();
        String loc = "Omaha, NE";

        fd.setLocation(loc);
        Assert.assertEquals(loc, fd.getLocation());
    }

    /**
     * Test the accessors for mLatitude
     * @throws Exception
     */
    @Test
    public void testLatitudeAccessors() throws Exception {
        Fueling fd = new Fueling();
        float lat = -199.5f;

        fd.setLatitude(lat);
        Assert.assertEquals(lat, fd.getLatitude());
    }

    /**
     * Test the accessors for mGPSCoords
     * @throws Exception
     */
    @Test
    public void testLongitudeAccessors() throws Exception {
        Fueling fd = new Fueling();
        float lng = 400.3f;

        fd.setLatitude(lng);
        Assert.assertEquals(lng, fd.getLatitude());
    }

    /**
     * Test the accessors for mStatus
     * @throws Exception
     */
    @Test
    public void testStatusAccessors() throws Exception {
        Fueling fd = new Fueling();

        fd.setCurrent();
        Assert.assertEquals(Fueling.STATUS_CURRENT, fd.getStatus());

        fd.setDeleted();
        Assert.assertEquals(Fueling.STATUS_DELETED, fd.getStatus());
    }
}