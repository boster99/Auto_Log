package com.ctoddcook.fuelLog;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Customized listener to listen for touches on the Main Navigation Drawer
 * <p>
 * Created by C. Todd Cook on 5/23/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Listener_NavDrawer implements NavigationView.OnNavigationItemSelectedListener {
  private DrawerLayout mDrawerLayout;
  private AppCompatActivity mActivity;

  public Listener_NavDrawer(AppCompatActivity a) {
    mActivity = a;
    mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
  }

  /**
   * Called when an item in the navigation menu is selected.
   *
   * @param item The selected item
   * @return true to display the item as the selected item
   */
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    mDrawerLayout.closeDrawers();
    Intent intent;

    switch (item.getItemId()) {
      case R.id.action_vehicles:
        intent = new Intent(mActivity, Activity_VehicleList.class);
        mActivity.startActivity(intent);
        break;
      case R.id.action_backup:
        Toast.makeText(mActivity, "Sorry ... backup is not yet implemented", Toast.LENGTH_LONG).show();
        break;
      case R.id.action_settings:
        intent = new Intent(mActivity, Activity_Settings.class);
        mActivity.startActivity(intent);
        break;
    }

    return true;
  }
}
