package com.ctoddcook.FuelLog;

import java.util.ArrayList;

/**
 * This centralizes constants related to hints shown throughout the app.
 * <p/>
 * Created by C. Todd Cook on 6/9/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Hints {
  public static final String VEHICLE_LIST_HINT_KEY = "FUELING_LIST_HINT_SHOWN";
  public static final String FUELING_LIST_HINT_KEY = "VEHICLE_LIST_HINT_SHOWN";
  public static final String VEHICLE_DETAIL_HINT_KEY = "VEHICLE_SWIPE_HINT_SHOWN";
  public static final String FUELING_DETAIL_HINT_KEY = "FUELING_SWIPE_HINT_SHOWN";
  public static final String AVERAGES_DETAIL_HINT_KEY = "AVERAGES_SWIPE_HINT_SHOWN";

  public static final ArrayList<String> hintList = new ArrayList<>(5);

  static {
    hintList.add(VEHICLE_LIST_HINT_KEY);
    hintList.add(FUELING_LIST_HINT_KEY);
    hintList.add(VEHICLE_DETAIL_HINT_KEY);
    hintList.add(FUELING_DETAIL_HINT_KEY);
    hintList.add(AVERAGES_DETAIL_HINT_KEY);
  }
}
