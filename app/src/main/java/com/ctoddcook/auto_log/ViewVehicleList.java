/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ctoddcook.CGenTools.PropertiesHelper;
import com.ctoddcook.CGenTools.Property;

import java.util.ArrayList;

/**
 * Builds presentation UI for a simeple list of Vehicles. Sets up the root ViewGroup (a LiewView)
 * and gives it an ArrayAdapter which handles display of individual rows. When a row is touched,
 * a new activity is started to view the selected vehicle with greater detail (the user might
 * then go on to edit the vehicle).
 */

public class ViewVehicleList extends AppCompatActivity implements AdapterView.OnItemClickListener {

  private static final String TAG = "ViewVehicleList";

  private static ListView sVehicleListView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_vehicle_list);
    showVehicles();
  }

  /**
   * Fetches the list of vehicles from the database and displays them as a list
   */
  private void showVehicles() {
    ArrayList<Vehicle> mVehicleList;
    mVehicleList = DatabaseHelper.getInstance(this).fetchVehicleList();

    if (sVehicleListView == null) {
      sVehicleListView = (ListView) findViewById(R.id.Vehicle_ListView);
      if (sVehicleListView != null) {
        sVehicleListView.setOnItemClickListener(this);
        registerForContextMenu(sVehicleListView);
      }
    }

    sVehicleListView.setAdapter(new VehicleListArrayAdapter(this, mVehicleList));
  }

  /**
   * Handler for when the user touches an item on the ListView of Vehicles. Opens
   * Activity_ViewDetail class and tells it to display vehicle ddetails.
   * @param parent the parent Adapter, see VehicleListArrayAdapter class
   * @param view the UI item that was touched
   * @param pos the position in the list that was touched
   * @param id the id of the touched item
   */
  public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
    if (view instanceof LinearLayout) {
      Vehicle vehicle = (Vehicle) parent.getItemAtPosition(pos);
      Intent intent = new Intent(this, Activity_ViewDetail.class);
      intent.putExtra(Activity_ViewDetail.ARG_TYPE, Activity_ViewDetail.TYPE_VEHICLE);
      intent.putExtra(Activity_ViewDetail.ARG_ITEM_ID, vehicle.getID());
      startActivity(intent);
    }
  }

  /**
   * Called when a context menu for the {@code view} is about to be shown.
   * Unlike onCreateOptionsMenu(Menu), this will be called every
   * time the context menu is about to be shown and should be populated for
   * the view (or item inside the view for {@link AdapterView} subclasses,
   * this can be found in the {@code menuInfo})).
   * <p>
   * Use {@link #onContextItemSelected(MenuItem)} to know when an
   * item has been selected.
   * <p>
   * It is not safe to hold onto the context menu after this method returns.
   *
   * @param menu The menu which is being built
   * @param v The view for which the menu is being built
   * @param menuInfo Additional information about the the item for which the context menu will be
   *                 shown. Since we only have one ListView shown, and only one context menu,
   *                 this can be ignored.
   */
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.vehicle_popup_menu, menu);
  }

  /**
   * Called when the user selects one of the options on the context menu.
   * @param item The menu item which was selected
   * @return True if we can identify which menu item was clicked and respond to it appropriately
   */
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo info =
        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

    Vehicle vehicle = (Vehicle) sVehicleListView.getItemAtPosition((int) info.id);
    int vehicleID = vehicle.getID();

    switch (item.getItemId()) {
      case R.id.vehicle_edit:
        editVehicle(vehicleID);
        return true;
      case R.id.vehicle_delete:
//        deleteVehicle(vehicleID);
        return true;
      case R.id.vehicle_make_default:
        makeDefaultVehicle(vehicleID);
        return true;
      case R.id.vehicle_retire:
//        retireVehicle(vehicleID);
        return true;
      default:
        return super.onContextItemSelected(item);
    }
  }

  /**
   * Callback method to be invoked when an item in this view has been
   * clicked and held.
   * <p>
   * Implementers can call getItemAtPosition(position) if they need to access
   * the data associated with the selected item.
   *
   * @param vehicleID The position of the view in the list
   */

  public void makeDefaultVehicle(int vehicleID) {
    PropertiesHelper ph = PropertiesHelper.getInstance();
    int defaultVehID = (int) ph.getLongValue(Vehicle.DEFAULT_VEHICLE_KEY);

    if (vehicleID != defaultVehID && Vehicle.getVehicle(vehicleID) != null) {
      ph.put(new Property(Vehicle.DEFAULT_VEHICLE_KEY, vehicleID));
    }

    showVehicles();
  }

  /**
   * Opens the activity for adding/editing a vechile, indicating ADD mode. We're
   * passing a 0 for requestCode, so we won't get a meaningful requestCode passed back to
   * onActivityResult(); that's okay because there's only one activity we start for result from
   * this activity.
   * @param v the button that was clicked
   */
  public void addVehicle(View v) {
    Intent intent = new Intent(this, Activity_AddEditVehicle.class);
    intent.putExtra(Activity_AddEditVehicle.KEY_ADD_EDIT_MODE, Activity_AddEditVehicle.MODE_ADD);
    startActivityForResult(intent, 0);
  }

  /**
   * Opens the activity for adding/editing a vechile, indicating EDIT mode. We're
   * passing a 0 for requestCode, so we won't get a meaningful requestCode passed back to
   * onActivityResult(); that's okay because there's only one activity we start for result from
   * this activity.
   * @param vehicleID The id of the vehicle to be edited
   */
  public void editVehicle(int vehicleID) {
    Intent intent = new Intent(this, Activity_AddEditVehicle.class);
    intent.putExtra(Activity_AddEditVehicle.KEY_ADD_EDIT_MODE, Activity_AddEditVehicle.MODE_EDIT);
    intent.putExtra(Activity_AddEditVehicle.KEY_VEHICLE_ID, vehicleID);
    startActivityForResult(intent, 0);
  }

  /**
   * Called when the Activity_AddEditVehicle class closes, so we can refresh our list of
   * displayed vechicles.
   * @param requestCode Would indicate which activity called this, but in this case we ignore it
   * @param resultCode whether the user clicked "save" or "cancel"; doesn't matter, we refresh
   *                   the list regardless
   * @param data any data we might want to process (there's none we want to process)
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    showVehicles();
  }
}
