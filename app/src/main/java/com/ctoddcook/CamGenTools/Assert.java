package com.ctoddcook.CamGenTools;

import java.util.AbstractCollection;
import java.util.Map;

/**
 * Utility class for checking arguments and conditions.
 * <p/>
 * Created by C. Todd Cook on 6/25/2016.<br>
 * ctodd@ctoddcook.com
 */
@SuppressWarnings("unused")
public class Assert {
  /**
   * Tests that the object provided is not null
   *
   * @param o       The object to test for null value
   * @param message A message to be included in an IllegalArgumentException
   * @throws IllegalArgumentException If the object is null
   */
  public static void notNull(Object o, String message) throws IllegalArgumentException {
    if (o == null) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Tests that the condition provided evaluates to true
   *
   * @param condition The condition to be tested
   * @param message   A message to be included in an IllegalArgumentException
   * @throws IllegalArgumentException If the condition evaluates to fales
   */
  public static void isTrue(boolean condition, String message) throws IllegalArgumentException {
    if (!condition) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Tests whether the provided map is empty
   *
   * @param map     The map to be evaluated
   * @param message A message to be included in an IllegalArgumentException
   * @throws IllegalArgumentException If the map is null or empty
   */
  public static void notEmpty(Map map, String message) throws IllegalArgumentException {
    if (map == null || map.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Tests whether the provided array is empty
   *
   * @param array   The array to be evaluated
   * @param message A message to be included in an IllegalArgumentException
   * @throws IllegalArgumentException If the array is null or empty
   */
  public static void notEmpty(Object[] array, String message) throws IllegalArgumentException {
    if (array == null || array.length == 0) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Tests whether the provided list is empty
   *
   * @param list    The array to be evaluated
   * @param message A message to be included in an IllegalArgumentException
   * @throws IllegalArgumentException If the list is null or empty
   */
  public static void notEmpty(AbstractCollection list, String message) throws
      IllegalArgumentException {
    if (list == null || list.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Tests whether the provided object is null
   *
   * @param o       The object to be evaluated
   * @param message A message to be included in an IllegalArgumentException
   * @throws IllegalArgumentException If the object is null
   */
  public static void isNull(Object o, String message) throws IllegalArgumentException {
    if (o != null) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Tests whether the provided object is an instance of the indicated class
   *
   * @param o       The object to test
   * @param c       The class the object is expected to be an instance of
   * @param message A message to be included in an IllegalArgumentException
   * @throws IllegalArgumentException If the object is not an instance of the expected class
   */
  public static void isInstanceOf(Object o, Class c, String message) throws
      IllegalArgumentException {
    if (o.getClass() != c) {
      throw new IllegalArgumentException(message);
    }
  }
}
