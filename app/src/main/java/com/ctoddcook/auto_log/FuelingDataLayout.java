/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * This child of LinearLayout adds a FuelingData object association, so that when the user
 * touches a LinaerLayout, the host activity can retrieve the FuelingData details of interest to
 * the user and display them (or delete them, or edit them, or whatever).
 * <p>Created by C. Todd Cook on 5/18/2016.<br>
 * ctodd@ctoddcook.com
 */
public class FuelingDataLayout extends LinearLayout {
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

  private FuelingData mFuelingData;

  /**
   * Constructor takes the context from which this instance has been instantiated and a style
   * tag, and uses them to call the super constructor. Then sets up shared/static class members
   * (if that has not already been done) and then fills this LinearLayout with TextViews
   * presenting data from the provided FuelingData instance.
   * @param c the context which instantiated this object
   * @param style the style to apply to the LinearLayout
   * @param fd the FuelingData instance to associate with this LinearLayout
   *
   * @see #setupStaticMembers()
   * @see #populateSelf(Context, FuelingData)
   */
  public FuelingDataLayout(Context c, int style, FuelingData fd) {
    super(c, null, style);
    setupStaticMembers();
    populateSelf(c, fd);
  }

  private FuelingDataLayout(Context c) {
    super(c);
  }

  private FuelingDataLayout(Context c, AttributeSet a, int style) {
    super(c, a, style);
  }

  private FuelingDataLayout(Context c, AttributeSet a) {
    super(c, a);
  }

  /**
   * One-time setup of shared, static members of the class.
   */
  private void setupStaticMembers() {
    if (hasBeenSetup) return;

    // Read some @string resources
    Resources res = getResources();
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
   * Populates this LinearLayout with TextViews displaying the perinent details for the provided
   * FuelingData object.
   * @param c the context from which this instance was instantiated
   * @param fd the FuelingData instance associated with this
   */
  private void populateSelf(Context c, FuelingData fd) {
    mFuelingData = fd;

    TextView tvLabel = new TextView(c, null, R.style.WideLightDataTextStyle);
    tvLabel.setText(mDateForm.format(fd.getDateOfFill()));
    this.addView(tvLabel);

    TextView tvPrice = new TextView(c, null, R.style.NarrowLightDataTextStyle);
    tvPrice.setText(formatPrice(fd.getPricePerUnit()));
    this.addView(tvPrice);

    // todo change literals to localization
    TextView tvDist = new TextView(c, null, R.style.WideLightDataTextStyle);
    tvDist.setText(formatDistance(fd.getDistance()));
    this.addView(tvDist);

    TextView tvVol = new TextView(c, null, R.style.NarrowLightDataTextStyle);
    tvVol.setText(formatVolume(fd.getVolume()));
    this.addView(tvVol);

    TextView tvEff = new TextView(c, null, R.style.WideLightDataTextStyle);
    tvEff.setText(formatEfficiency(fd.getEfficiency()));
    this.addView(tvEff);
  }

  /**
   * Accessor to retrieve the FuelingData object associated with this instance.
   * @return a FuelingData instance
   */
  public FuelingData getFuelingData() {
    return mFuelingData;
  }

  /**
   * Returns proper, localized formaatting for the price paid per unit (i.e., price per gallon or
   * per litre). Used by populateSelf() and available to other methods.
   * @param price the price paid per unit
   * @return a localized currency value, such as $ 1.899.
   * @see #populateSelf(Context, FuelingData)
   */
  public static String formatPrice(float price) {
    return mCurrencyForm.format(price);
  }

  /**
   * Returns a formatted String version of the provided distance. Called by populateSelf() and
   * available to other methods, too.
   * @param dist the distance covered in a tank of fuel
   * @return a formatted String
   * @see #populateSelf(Context, FuelingData)
   */
  public static String formatDistance(float dist) {
    return String.format(displayPattern, mDistanceForm.format(dist), distanceAbbrev);
  }

  /**
   * Returns a formatted String version of the provided volume. Used by populateSelf(), but
   * available to outside methods as well.
   * @param vol the volume to convert to formatted String
   * @return a formatted String
   * @see #populateSelf(Context, FuelingData)
   */
  public static String formatVolume(float vol) {
    return String.format(displayPattern, mVolumeForm.format(vol), volumeAbbrev);
  }

  /**
   * Returns a formatted String version of the provided efficiency value. Used by populateSelf(),
   * but available to outside methods as well.
   * @param eff the efficiency to be converted to String
   * @return the formatted String
   */
  public static String formatEfficiency(float eff) {
    return String.format(displayPattern, mEfficiencyForm.format(eff), efficiencyAbbrev);
  }
}
