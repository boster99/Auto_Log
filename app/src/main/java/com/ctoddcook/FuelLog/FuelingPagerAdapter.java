package com.ctoddcook.FuelLog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Presents details for one feuling at a time
 * <p>
 * Created by C. Todd Cook on 5/24/2016.<br>
 * ctodd@ctoddcook.com
 */
public class FuelingPagerAdapter extends FragmentStatePagerAdapter {
  private static final String TAG = "FuelingPagerAdapter";
  private final ArrayList<Fueling> mFuelings;

  /**
   * Constructor passes a FragmentManager up the chain to a parent class, and stores a reference
   * to the list of Fuelings to be displayed.
   * @param fragmentManager the manager overseeing this adapter
   * @param fuelings a list of fuelings, from which details will be retrieved for display
   */
  public FuelingPagerAdapter(FragmentManager fragmentManager, ArrayList<Fueling> fuelings) {
    super(fragmentManager);
    mFuelings = fuelings;
  }

  /**
   * Return the number of views available.
   */
  @Override
  public int getCount() {
    return mFuelings.size();
  }

  /**
   * Return the Fragment associated with a specified position.
   *
   * @param position the position of the row that is to be displayed
   */
  @Override
  public Fragment getItem(int position) {
    if (position >= 0 && position < mFuelings.size())
      return FuelingDetailFrag.getInstance(mFuelings.get(position).getID());

    return null;
  }
}
