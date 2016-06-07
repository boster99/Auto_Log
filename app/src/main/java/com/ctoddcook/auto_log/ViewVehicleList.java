/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ctoddcook.CGenTools.PropertiesHelper;
import com.ctoddcook.CGenTools.Property;

import java.util.ArrayList;

/**
 * Builds presentation UI for a simple list of Vehicles. Sets up the root ViewGroup (a ListView)
 * and gives it an ArrayAdapter which handles display of individual rows. When a row is touched,
 * a new activity is started to view the selected vehicle with greater detail (the user might
 * then go on to edit the vehicle).
 */

public class ViewVehicleList extends AppCompatActivity implements AdapterView.OnItemClickListener {

  private static final String TAG = "ViewVehicleList";

  private static ListView sVehicleListView;
  private DatabaseHelper sDB;
  private Vehicle mVehicleToDelete = null;
  private Vehicle mVehicleToRetire = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_vehicle_list);
    sDB = DatabaseHelper.getInstance(this);
    showVehicles();
  }

  /**
   * Fetches the list of vehicles from the database and displays them as a list
   */
  private void showVehicles() {
    ArrayList<Vehicle> mVehicleList;
    mVehicleList = sDB.fetchVehicleList();

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
   * Activity_ViewDetail class and tells it to display vehicle details.
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
    int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
    Vehicle vehicle = (Vehicle) sVehicleListView.getItemAtPosition(position);

    /*
    Always display the base menu for vehicles (EDIT and DELETE). Add additional menu items based
    on whether the vehicle is ACTIVE or RETIRED.
     */
    inflater.inflate(R.menu.vehicle_popup_base_menu, menu);
    if (vehicle.isActive())
      inflater.inflate(R.menu.vehicle_popup_active_menu, menu);
    else
      inflater.inflate(R.menu.vehicle_popup_retired_menu, menu);
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
        deleteVehicle(vehicleID);
        return true;
      case R.id.vehicle_make_default:
        makeDefaultVehicle(vehicleID);
        return true;
      case R.id.vehicle_retire:
        retireVehicle(vehicleID);
        return true;
      case R.id.vehicle_unretire:
        unretireVehicle(vehicleID);
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

  private void makeDefaultVehicle(int vehicleID) {
    PropertiesHelper ph = PropertiesHelper.getInstance();
    int defaultVehID = (int) ph.getLongValue(Vehicle.DEFAULT_VEHICLE_KEY);

    if (vehicleID != defaultVehID && Vehicle.getVehicle(vehicleID) != null) {
      ph.put(new Property(Vehicle.DEFAULT_VEHICLE_KEY, vehicleID));
    }

    showVehicles();
  }

  /**
   * Opens the activity for adding/editing a vehicle, indicating ADD mode. We're
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
   * Opens the activity for adding/editing a vehicle, indicating EDIT mode. We're
   * passing a 0 for requestCode, so we won't get a meaningful requestCode passed back to
   * onActivityResult(); that's okay because there's only one activity we start for result from
   * this activity.
   * @param vehicleID The id of the vehicle to be edited
   */
  private void editVehicle(int vehicleID) {
    Intent intent = new Intent(this, Activity_AddEditVehicle.class);
    intent.putExtra(Activity_AddEditVehicle.KEY_ADD_EDIT_MODE, Activity_AddEditVehicle.MODE_EDIT);
    intent.putExtra(Activity_AddEditVehicle.KEY_VEHICLE_ID, vehicleID);
    startActivityForResult(intent, 0);
  }

  /**
   * Gets confirmation from the user that s/he really wants to retire the vehicle, and if the
   * user's response is affirmative, we go ahead and retire it.
   * @param vehicleID The id of the vehicle to retire
   */
  private void retireVehicle(int vehicleID) {
    // Note the vehicle which is to be retired
    mVehicleToRetire = Vehicle.getVehicle(vehicleID);
    if (mVehicleToRetire == null || mVehicleToRetire.isRetired()) return;

    // Setup the listeners which will respond to the user's response to the dialog
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which){
          // If the user clicks "YES" then we retire the vehicle
          case DialogInterface.BUTTON_POSITIVE:
            mVehicleToRetire.setRetired();
            sDB.updateVehicle(mVehicleToRetire);
            mVehicleToRetire = null;
            showVehicles();
            break;

          // If the user clicks "NO" then we clean up and get out of here
          case DialogInterface.BUTTON_NEGATIVE:
            mVehicleToRetire = null;
            break;
        }
      }
    };

    // Set up and display the dialog to get the user's confirmation that the vehicle should be
    // retired.
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Retire " + mVehicleToRetire.getName());
    builder.setMessage("Retiring a vehicle means you can still view its fueling data, but you can" +
        " no longer add new fuelings for that vehicle. Retiring a vehicle can be reversed" +
        ".\n\nWould you like to do this?")
        .setPositiveButton("Yes, retire it", dialogClickListener)
        .setNegativeButton("No, keep it active", dialogClickListener).show();
  }

  /**
   * Changes a vehicle's status to ACTIVE and updates the database.
   * @param vehicleID The id of the vehicle to unretire
   */
  private void unretireVehicle(int vehicleID) {
    Vehicle vehicle = Vehicle.getVehicle(vehicleID);
    if (vehicle.isRetired()) {
      vehicle.setActive();
      sDB.updateVehicle(vehicle);
      showVehicles();
    }
  }

  /**
   * Gets confirmation from the user that s/he really wants to delete the vehicle, and if the
   * user responds affirmative we go ahead and delete the vehicle.
   * @param vehicleID The id of the vehicle to delete
   */
  private void deleteVehicle(int vehicleID) {
    // Note the vehicle which is to be deleted
    mVehicleToDelete = Vehicle.getVehicle(vehicleID);
    if (mVehicleToDelete == null) return;

    // Setup the listeners which will respond to the user's response to the dialog
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which){
          // If the user clicks "YES" then we delete the vehicle
          case DialogInterface.BUTTON_POSITIVE:
            mVehicleToDelete.setDeleted();
            sDB.deleteVehicle(mVehicleToDelete);
            mVehicleToDelete = null;
            break;

          // If the user clicks "NO" then we clean up and get out of here
          case DialogInterface.BUTTON_NEGATIVE:
            mVehicleToDelete = null;
            break;
        }
      }
    };

    // Set up and display the dialog to get the user's confirmation that the vehicle should be
    // deleted.
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Delete " + mVehicleToDelete.getName());
    builder.setMessage("Deleting a vehicle cannot be undone. Are you sure you want to do this?")
        .setPositiveButton("Yes, delete it", dialogClickListener)
        .setNegativeButton("No, never mind", dialogClickListener).show();
  }

  /**
   * Called when the Activity_AddEditVehicle class closes, so we can refresh our list of
   * displayed vehicles.
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
