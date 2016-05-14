/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.tools;import java.math.BigDecimal;

/**
 * Created by C. Todd Cook on 4/22/2016.
 * ctodd@ctoddcook.com
 */

/**
 * This class is to contain some general tools useful across multiple applications.
 */
public class CTools {

    /**
     * This takes a double value, and rounds it to the indicated scale, returning a new
     * double.
     * @param value The value to be rounded
     * @param places The scale for rounding (i.e., the significant digits)
     * @return A new double value rounded as indicated
     */
    public static double round(double value, int places) {
        if (value == 0) return 0;

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }


    /**
     * This takes a float value, and rounds it to the indicated scale, returning a new
     * float.
     * @param value The value to be rounded
     * @param places The scale for rounding (i.e., the significant digits)
     * @return A new float value rounded as indicated
     */
    public static float round(float value, int places) {
        if (value == 0) return 0;
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }


    /**
     * Safely converts/downcasts a long to an int.
     * @param l the long to be downcast
     * @return the same value as an int
     * @throws IllegalArgumentException if the long's value is outside the MIN..MAX range for int
     */
    public static int longToInt(long l) throws IllegalArgumentException {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE)
            throw new IllegalArgumentException(l + " cannot be cast to int without chaning its " +
                    "value.");

        return (int) l;
    }
}

