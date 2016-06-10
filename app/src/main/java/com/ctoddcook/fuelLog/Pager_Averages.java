package com.ctoddcook.fuelLog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Presents details for one span of averages at a time
 * <p>
 * Created by C. Todd Cook on 5/24/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Pager_Averages extends FragmentStatePagerAdapter {
  private static final String TAG = "Pager_Fueling";
  private String mVehicleName;

  /**
   * Constructor passes a FragmentManager up the chain to a parent class, and stores a reference
   * to the list of Fuelings to be displayed.
   * @param fragmentManager the manager overseeing this adapter
   * @param vehicleName the mName of the vehicle represented in the averages data
   */
  public Pager_Averages(FragmentManager fragmentManager, String vehicleName) {
    super(fragmentManager);
    mVehicleName = vehicleName;
  }

  /**
   * Return the number of views available.
   */
  @Override
  public int getCount() {
    return 4;
  }

  /**
   * Return the Fragment associated with a specified position. The position is assumed to be
   * equivalent to the span needed, as indicated by the values for:
   * <ul>
   * <li>Model_Fueling.SPAN_3_MONTHS</li>
   * <li>Model_Fueling.SPAN_6_MONTHS</li>
   * <li>Model_Fueling.SPAN_ONE_YEAR</li>
   * <li>Model_Fueling.SPAN_ALL_TIME</li>
   * </ul>
   *
   * @param position the position of the row that is to be displayed
   */
  @Override
  public Fragment getItem(int position) {
    return Fragment_AveragesDetail.getInstance(position, mVehicleName);
  }
}
