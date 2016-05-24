/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

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

  private ListView mVehicleListView;
  private ArrayList<Vehicle> mVehicleList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_vehicle_list);
    mVehicleList = DatabaseHelper.getInstance(this).fetchVehicleList();
    initList();
  }

  private void initList() {
    if (mVehicleListView == null) {
      mVehicleListView = (ListView) findViewById(R.id.Vehicle_ListView);
      if (mVehicleListView != null) mVehicleListView.setOnItemClickListener(this);
    }

    mVehicleListView.setAdapter(new VehicleListArrayAdapter(this, mVehicleList));
  }

  /**
   * Handler for when the user touches an item on the ListView of Vehicles.
   * @param parent the parent Adapter, see VehicleListArrayAdapter class
   * @param view the UI item that was touched
   * @param pos the position in the list that was touched
   * @param id the id of the touched item
   */
  public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
    // fixme change so this starts new activity
    if (view instanceof LinearLayout) {
      Vehicle vehicle = (Vehicle) parent.getItemAtPosition(pos);
      Toast.makeText(this, "Touch of " + vehicle.getName(), Toast.LENGTH_LONG).show();
      Toast.makeText(this, "pos is " + pos, Toast.LENGTH_SHORT).show();
      Toast.makeText(this, "id is " + id, Toast.LENGTH_SHORT).show();
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
}
