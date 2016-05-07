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
    public static double roundDouble(double value, int places) {
        if (value == 0)
            return 0;

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
}

