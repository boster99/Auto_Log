/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.Context;
import android.content.res.Resources;
import android.widget.TextView;

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
 * So the app needs to call the static method FormatHandler.init() early on, before any other
 * methods are called, and pass to init() the Context.
 * <p>
 * Created by C. Todd Cook on 5/18/2016.<br>
 * ctodd@ctoddcook.com
 */
public class FormatHandler {
  private static DecimalFormat mCurrencyForm = null;
  private static DecimalFormat mDistanceForm = null;
  private static DecimalFormat mVolumeForm = null;
  private static DecimalFormat mEfficiencyForm = null;
  private static final DateFormat mDateForm = SimpleDateFormat.getDateInstance();
  private static boolean hasBeenSetup = false;
  private static String displayPattern = null;
  private static String distanceAbbrev = null;
  private static String volumeAbbrev = null;
  private static String efficiencyAbbrev = null;

  private Fueling mFueling;

  private FormatHandler() {
    // Nothing. Not allowed.
  }

  /**
   * Constructor takes the context from which this instance has been instantiated and a style
   * tag, and uses them to call the super constructor. Then sets up shared/static class members
   * (if that has not already been done) and then fills this LinearLayout with TextViews
   * presenting data from the provided Fueling instance.
   * @param c the context which instantiated this object
   *
   * @see #setupStaticMembers(Context)
   */
  private FormatHandler(Context c) {
    setupStaticMembers(c);
  }

  public static void init(Context c) {
    FormatHandler f = new FormatHandler(c);
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
   * Returns proper, localized formatting for the date of fill. Used by populateSelf() and
   * available to outside methods.
   * @param date the date to be formatted
   * @return a String with the formatted date
   */
  public static String formatDate(Date date) {
    if (!hasBeenSetup)
      throw new UnsupportedOperationException("FormatHandler has not been initialized");

    return mDateForm.format(date);
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
