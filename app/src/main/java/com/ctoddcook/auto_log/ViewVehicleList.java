/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Builds presentation UI for a simeple list of Vehicles. Sets up the root ViewGroup (a LiewView)
 * and gives it an ArrayAdapter which handles display of individual rows. When a row is touched,
 * a new activity is started to view the selected vehicle with greater detail (the user might
 * then go on to edit the vehicle).
 */

public class ViewVehicleList extends AppCompatActivity implements AdapterView
    .OnItemClickListener, AdapterView.OnItemLongClickListener {

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
      if (sVehicleListView != null) sVehicleListView.setOnItemClickListener(this);
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
   * Callback method to be invoked when an item in this view has been
   * clicked and held.
   * <p>
   * Implementers can call getItemAtPosition(position) if they need to access
   * the data associated with the selected item.
   *
   * @param parent   The AbsListView where the click happened
   * @param view     The view within the AbsListView that was clicked
   * @param pos      The position of the view in the list
   * @param id       The row id of the item that was clicked
   * @return true if the callback consumed the long click, false otherwise
   */
  @Override
  public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
    // fixme change this so it opens menu (for setting default or retiring)
    if (view instanceof LinearLayout) {
      Vehicle vehicle = (Vehicle) view.getTag();
      Toast.makeText(this, "Long touch of " + vehicle.getName(), Toast.LENGTH_LONG).show();
      Toast.makeText(this, "pos is " + pos, Toast.LENGTH_SHORT).show();
      Toast.makeText(this, "id is " + id, Toast.LENGTH_SHORT).show();
    }

    return false;
  }

  /**
   * Called by the floating action button. Opens the activity for adding a new vechile. We're
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
   * Called when the Activity_AddEditVehicle class closes, so we can refresh our list of
   * displayed vechicles.
   * @param requestCode Would indicate which activity called this, except that we pass a 0 above
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
