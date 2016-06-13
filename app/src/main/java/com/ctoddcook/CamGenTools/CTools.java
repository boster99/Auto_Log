/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.CamGenTools;

import java.math.BigDecimal;

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
   *
   * @param value  The value to be rounded
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
   *
   * @param value  The value to be rounded
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
   * Safely converts/down-casts a long to an int.
   *
   * @param l the long to be downcast
   * @return the same value as an int
   * @throws IllegalArgumentException if the long's value is outside the MIN..MAX range for int
   */
  public static int longToInt(long l) throws IllegalArgumentException {
    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE)
      throw new IllegalArgumentException(l + " cannot be cast to int without changing its " +
          "value.");

    return (int) l;
  }


  /**
   * Used to test the truth of an assumption. If it is true, nothing happens. If the assumption
   * fails, a RuntimeException is thrown.
   *
   * @param c the expression assumed to be true
   */
  public static void assertTrue(final boolean c) {
    if (!c) throw new RuntimeException("assertion failed");
  }


  /**
   * Tests where two Strings are equal. If they are, nothing happens. If they are different a
   * RuntimeException is thrown.
   *
   * @param expected value 1 to compare
   * @param found    value 2 to compare
   */
  public static void assertEquals(final String expected, final String found) {
    if (!expected.equals(found))
      throw new RuntimeException("assertion failed: expected '" + expected + "', found '" + found
          + "'");
  }


  /**
   * Tests where two int values are equal. If they are, nothing happens. If they are different a
   * RuntimeException is thrown.
   *
   * @param expected value 1 to compare
   * @param found    value 2 to compare
   */
  public static void assertEquals(final int expected, final int found) {
    if (expected != found)
      throw new RuntimeException("assertion failed: expected '" + expected + "', found '" + found
          + "'");
  }
}

