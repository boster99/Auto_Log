package com.ctoddcook.FuelLog;

import com.ctoddcook.CamUiTools.Handler_Hints;

/**
 * Provides the Properties keys used by the base class and by calling methods to show user hints
 * and record that they've been displayed. All functionality resides in the parent class.
 * <p/>
 * Created by C. Todd Cook on 6/14/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Handler_FuelLogHints extends Handler_Hints {
  public static final String VEHICLE_LIST_HINT_KEY = "VEHICLE_LIST_HINT_SHOWN";
  public static final String FUELING_LIST_HINT_KEY = "FUELING_LIST_HINT_SHOWN";
  public static final String VEHICLE_DETAIL_HINT_KEY = "VEHICLE_SWIPE_HINT_SHOWN";
  public static final String FUELING_DETAIL_HINT_KEY = "FUELING_SWIPE_HINT_SHOWN";
  public static final String AVERAGES_DETAIL_HINT_KEY = "AVERAGES_SWIPE_HINT_SHOWN";
  public static final String FIRST_VEHICLE_HINT_KEY = "FIRST_VEHICLE_HINT_KEY";
  public static final String FIRST_FUELING_HINT_KEY = "FIRST_FUELING_HINT_KEY";

  static {
    hintList.add(VEHICLE_LIST_HINT_KEY);
    hintList.add(FUELING_LIST_HINT_KEY);
    hintList.add(VEHICLE_DETAIL_HINT_KEY);
    hintList.add(FUELING_DETAIL_HINT_KEY);
    hintList.add(AVERAGES_DETAIL_HINT_KEY);
    hintList.add(FIRST_VEHICLE_HINT_KEY);
    hintList.add(FIRST_FUELING_HINT_KEY);
  }
}
