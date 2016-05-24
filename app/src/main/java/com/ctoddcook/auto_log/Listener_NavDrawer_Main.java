package com.ctoddcook.auto_log;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.Toast;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Customized listener to listen for touches on the Main Navigation Drawer
 * <p>
 * Created by C. Todd Cook on 5/23/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Listener_NavDrawer_Main implements NavigationView.OnNavigationItemSelectedListener {
  private DrawerLayout mDrawerLayout;
  private Activity mActivity;

  public Listener_NavDrawer_Main(Activity a) {
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

    switch (item.getItemId()) {
      case R.id.action_vehicles:
        Intent intent = new Intent(mActivity, ViewVehicleList.class);
        intent.putExtra(AddEditVehicleActivity.KEY_ADD_EDIT_MODE, AddEditVehicleActivity.MODE_ADD);
        startActivity(mActivity, intent, null);
        break;
      case R.id.action_backup:
        Toast.makeText(mActivity, "Sorry ... backup is not yet implemented", Toast.LENGTH_LONG).show();
    }

    return true;
  }
}
