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
    tvPrice.setText(mCurrencyForm.format(fd.getPricePerUnit()));
    this.addView(tvPrice);

    // todo change literals to localization
    TextView tvDist = new TextView(c, null, R.style.WideLightDataTextStyle);
    tvDist.setText(String.format(displayPattern, mDistanceForm.format(fd.getDistance()),
        distanceAbbrev));
    this.addView(tvDist);

    TextView tvVol = new TextView(c, null, R.style.NarrowLightDataTextStyle);
    tvVol.setText(String.format(displayPattern, mVolumeForm.format(fd.getVolume()),
        volumeAbbrev));
    this.addView(tvVol);

    TextView tvEff = new TextView(c, null, R.style.WideLightDataTextStyle);
    tvEff.setText(String.format(displayPattern, mEfficiencyForm.format(fd.getMileage()),
        efficiencyAbbrev));

    this.addView(tvEff);
  }

  /**
   * Accessor to retrieve the FuelingData object associated with this instance.
   * @return a FuelingData instance
   */
  public FuelingData getFuelingData() {
    return mFuelingData;
  }
}
