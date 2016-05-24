/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.Context;
import android.content.res.Resources;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This provides standard formatting functions for this app. This is designed to have these
 * methods called in a static fashion, and in fact, other classes cannot instantiate a
 * FormatHandler object.
 * <p>
 * However, it does need initialization before it is used, and that initialization requires a
 * Context object, so it can get appropriate screen dimensions, localization, etc., so when it
 * reads resource files for strings (like currency format) it can get appropriate versions.
 * <p>
 * So the app needs to call the static method FormatHandler.getInstance() early on, before any other
 * methods are called, and pass to getInstance() the Context.
 * <p>
 * Created by C. Todd Cook on 5/18/2016.<br>
 * ctodd@ctoddcook.com
 */
public class FormatHandler {
  private static DecimalFormat mCurrencyForm = null;
  private static DecimalFormat mDistanceForm = null;
  private static DecimalFormat mVolumeForm = null;
  private static DecimalFormat mEfficiencyForm = null;

  private static final DateFormat mShortDateForm =
      SimpleDateFormat.getDateInstance(DateFormat.SHORT);
  private static final DateFormat mMediumDateForm =
      SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
  private static final DateFormat mLongDateForm =
      SimpleDateFormat.getDateInstance(DateFormat.LONG);

  private static final DateFormat mShortDateTimeForm =
      SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
  private static final DateFormat mMediumDateTimeForm =
      SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
  private static final DateFormat mLongDateTimeForm =
      SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);

  private static final DateFormat mShortTimeForm =
      SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
  private static final DateFormat mMediumTimeForm =
      SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM);
  private static final DateFormat mLongTimeForm =
      SimpleDateFormat.getTimeInstance(DateFormat.LONG);

  private static boolean hasBeenSetup = false;
  private static String displayPattern = null;
  private static String distanceAbbrev = null;
  private static String volumeAbbrev = null;
  private static String efficiencyAbbrev = null;

  private Fueling mFueling;

  private FormatHandler() {
    // Nothing. Not allowed.
  }

  public static void init(Context c) {
    FormatHandler f = new FormatHandler();
    f.setupStaticMembers(c);
  }
  /**
   * One-time setup of shared, static members of the class.
   */
  private void setupStaticMembers(Context context) {
    if (hasBeenSetup) return;

    // Read some @string resources
    Resources res = context.getResources();
    displayPattern = res.getString(R.string.Fueling_DataPattern);
    distanceAbbrev = res.getString(R.string.Fueling_DistanceAbbrev);
    volumeAbbrev = res.getString(R.string.Fueling_VolumeAbbrev);
    efficiencyAbbrev = res.getString(R.string.Fueling_EfficiencyAbbrev);

    String currency = res.getString(R.string.Fueling_CurrencyForm);
    String singleDec = res.getString(R.string.Fueling_SingleDecimalPointForm);

    // Create shared/static formatting objects which will be used over and over
    mCurrencyForm = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
    mCurrencyForm.applyLocalizedPattern(currency);

    mDistanceForm = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
    mDistanceForm.applyLocalizedPattern(singleDec);

    mVolumeForm = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
    mVolumeForm.applyLocalizedPattern(singleDec);

    mEfficiencyForm = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
    mEfficiencyForm.applyLocalizedPattern(singleDec);

    // note that setup does not have to be performed again
    hasBeenSetup = true;
  }

  /**
   * Returns proper, localized formatting for a date. This utilizes the SHORT form,
   * which, in the U.S. looks like 5/19/16 (for May 19, 2016).
   * @param date the date to be formatted in SHORT form
   * @return a String with the formatted date
   */
  public static String formatShortDate(Date date) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mShortDateForm.format(date);
  }

  /**
   * Returns proper, localized formatting for a date. This utilizes the MEDIUM form,
   * which, in the U.S. looks like 5/19/16 (for May 19, 2016).
   * @param date the date to be formatted in MEDIUM form
   * @return a String with the formatted date
   */
  public static String formatMediumDate(Date date) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mMediumDateForm.format(date);
  }

  /**
   * Returns proper, localized formatting for a date. This utilizes the Long form, which,
   * in the U.S. looks like December 19, 2016.
   * @param date the date to be formatted in LONG form
   * @return a String with the formatted date
   */
  public static String formatLongDate(Date date) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mLongDateForm.format(date);
  }

  /**
   * Returns proper, localized formatting for a date and time.
   * @param date the date to be formatted in SHORT form
   * @return a String with the formatted date
   */
  public static String formatShortDateTime(Date date) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mShortDateTimeForm.format(date);
  }

  /**
   * Returns proper, localized formatting for a date and time.
   * @param date the date to be formatted in MEDIUM form
   * @return a String with the formatted date
   */
  public static String formatMediumDateTime(Date date) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mMediumDateTimeForm.format(date);
  }

  /**
   * Returns proper, localized formatting for a date and time.
   * @param date the date to be formatted in LONG form
   * @return a String with the formatted date
   */
  public static String formatLongDateTime(Date date) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mLongDateTimeForm.format(date);
  }

  /**
   * Returns proper, localized formatting for a date and time. This version utilizes a LONG date
   * and a SHORT time.
   * @param date the date to be formatted in LONG form
   * @return a String with the formatted date
   */
  public static String formatLongDateShortTime(Date date) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mLongDateForm.format(date) + " " + mShortTimeForm.format(date);
  }


  /**
   * Returns proper, localized formatting for a date and time. This version utilizes a MEDIUM date
   * and a SHORT time.
   * @param date the date to be formatted in LONG form
   * @return a String with the formatted date
   */
  public static String formatMediumDateShortTime(Date date) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mMediumDateForm.format(date) + " " + mShortTimeForm.format(date);
  }

  /**
   * Returns proper, localized formatting for a time.
   * @param date the date to be formatted in SHORT form
   * @return a String with the formatted date
   */
  public static String formatShortTime(Date date) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mShortTimeForm.format(date);
  }

  /**
   * Returns proper, localized formatting for a time.
   * @param date the date to be formatted in MEDIUM form
   * @return a String with the formatted date
   */
  public static String formatMediumTime(Date date) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mMediumTimeForm.format(date);
  }

  /**
   * Returns proper, localized formatting for a time.
   * @param date the date to be formatted in LONG form
   * @return a String with the formatted date
   */
  public static String formatLongTime(Date date) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mLongTimeForm.format(date);
  }

  /**
   * Returns proper, localized formatting for the price paid per unit (i.e., price per gallon or
   * per litre). Used by populateSelf() and available to other methods.
   * @param price the price paid per unit
   * @return a localized currency value, such as $ 1.899.
   */
  public static String formatPrice(float price) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mCurrencyForm.format(price);
  }

  /**
   * Returns a formatted String version of the provided distance. Called by populateSelf() and
   * available to other methods, too.
   * @param dist the distance covered in a tank of fuel
   * @return a formatted String
   */
  public static String formatDistance(float dist) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return String.format(displayPattern, mDistanceForm.format(dist), distanceAbbrev);
  }

  /**
   * Returns a formatted String version of the provided volume. Used by populateSelf(), but
   * available to outside methods as well.
   * @param vol the volume to convert to formatted String
   * @return a formatted String
   */
  public static String formatVolume(float vol) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return String.format(displayPattern, mVolumeForm.format(vol), volumeAbbrev);
  }

  /**
   * Returns a formatted String version of the provided efficiency value. Used by populateSelf(),
   * but available to outside methods as well.
   * @param eff the efficiency to be converted to String
   * @return the formatted String
   */
  public static String formatEfficiency(float eff) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return String.format(displayPattern, mEfficiencyForm.format(eff), efficiencyAbbrev);
  }
}
