package com.ctoddcook.auto_log;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Presents details for one vehicle at a time, or a list of vehicles
 * <p>
 * Created by C. Todd Cook on 5/24/2016.<br>
 * ctodd@ctoddcook.com
 */
public class VehicleAdapter extends FragmentStatePagerAdapter {
  private static final String TAG = "VehicleAdapter";
  private final ArrayList<Vehicle> mVehicles;

  /**
   * Constructor passes a FragmentManager up the chain to a parent class, and stores a reference
   * to the list of Vehicles to be displayed.
   * @param fragmentManager the manager overseeing this adapter
   * @param vehicles a list of vehicles, from which details will be retrieved for display
   */
  public VehicleAdapter(FragmentManager fragmentManager, ArrayList<Vehicle> vehicles) {
    super(fragmentManager);
    mVehicles = vehicles;
  }

  /**
   * Return the number of views available.
   */
  @Override
  public int getCount() {
    return mVehicles.size();
  }

  /**
   * Return the Fragment associated with a specified position.
   *
   * @param position the position of the row that is to be displayed
   */
  @Override
  public Fragment getItem(int position) {
    if (position >= 0 && position < mVehicles.size())
      return VehicleDetailFrag.getInstance(mVehicles.get(position).getID());

    return null; // remove this when fragments above are taken care of
  }
}
