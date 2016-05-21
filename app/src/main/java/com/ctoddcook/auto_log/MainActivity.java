/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctoddcook.CGenTools.PropertiesHelper;
import com.ctoddcook.CGenTools.Property;

import java.util.ArrayList;


/**
 * The Main activity for this app. This diplays a split screen:
 * <ul>
 *   <li>The top of the screen displays averages over some time spans</li>
 *   <li>The bottom of the screen displays historical records</li>
 * </ul>
 * <p/>
 * Created by C. Todd Cook on 5/01/2016.<br>
 * ctodd@ctoddcook.com
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
  private static final String TAG = "MainActivity";
  private static final int PROGRESS = 0x1;
  private static final int ADD_FIRST_VEHICLE = 755;
  private static final int ADD_FUELING = 442;
  private static PropertiesHelper sPH;
  private static DatabaseHelper sDatabaseHelper;
  private int mCurrentVehicleID = 0;
  private ListView mHistoricalsList;

  /**
   * Called by the system when the UI elements of the activity are created.
   * @param savedInstanceState state information if this is rebuilt
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FormatHandler.init(this);

    sDatabaseHelper = DatabaseHelper.getInstance(this);
    PropertiesHelper.setDatabaseHelper(sDatabaseHelper);
    sPH = PropertiesHelper.getInstance();

    populateScreen();
  }

  /**
   * Handler for calling other methods to gather vehicle information, then fueling information
   * for that vehicle.
   */
  private void populateScreen() {
    prepareVehicles();
    if (mCurrentVehicleID > 0)
      loadForVehicle(mCurrentVehicleID);
  }

  /**
   * Gets the default Vehicle, then fetches all the Vehicles from the database
   */
  private void prepareVehicles() {
    // Get the default Vehicle
    if (sPH.doesNameExist(Vehicle.DEFAULT_VEHICLE_KEY))
      mCurrentVehicleID = (int) sPH.getLongValue(Vehicle.DEFAULT_VEHICLE_KEY);

    // Fetch the list of vehicles into memory
    ArrayList<Vehicle> vList = sDatabaseHelper.fetchVehicleList();

    /*
    If no vehicles were found in the database, we open the activity to add at least one vehicle.
    Then we fetch the list again. If the list is not empty (expected) we use the newly-added
    vehicle as the Default, and add that default to properties.
     */

    if (vList.isEmpty()) {
      Intent intent = new Intent(this, AddEditVehicleActivity.class);
      intent.putExtra(AddEditVehicleActivity.KEY_ADD_EDIT_MODE, AddEditVehicleActivity.MODE_ADD);
      startActivityForResult(intent, ADD_FIRST_VEHICLE);
    } else {
      if (mCurrentVehicleID == 0) {
        mCurrentVehicleID = vList.get(0).getID();
        Property p = new Property(Vehicle.DEFAULT_VEHICLE_KEY, mCurrentVehicleID);
        sPH.put(p);
      }
    }
  }

  /**
   * Dispatch incoming result to the correct fragment.
   *
   * @param requestCode the code passed by this activity when starting another one
   * @param resultCode the result (Okay or Cancelled)
   * @param data data returned by the other activity/fragment
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case ADD_FIRST_VEHICLE:
        if (resultCode == RESULT_OK)
          prepareVehicles();
        break;
      case ADD_FUELING:
        if (resultCode == RESULT_OK) {
          int id = data.getIntExtra(Vehicle.DEFAULT_VEHICLE_KEY, 0);
          if (id > 0) {
            mCurrentVehicleID = id;
            loadForVehicle(mCurrentVehicleID);
          }
        }
        break;
    }
  }

  /**
   * Used by UI to respond to a button touch
   * @param v The View (a button) which called this method
   */
  public void addFueling(View v) {
    addFueling();
  }

  /**
   * Creates an Intent of type AddEditFuelingActivity, tells it we're in ADD mode (rather than
   * EDIT mode) and tells it the default vehicle ID, and opens the Intent. When the intent is
   * closed, we re-fetch the list of fuelings and redisplay averages and historicals.
   */
  private void addFueling() {
    Intent intent = new Intent(this, AddEditFuelingActivity.class);

    // Tell the new activity whether we're in ADD mode or EDIT mode
    intent.putExtra(AddEditFuelingActivity.KEY_ADD_EDIT_MODE, AddEditFuelingActivity.MODE_ADD);

    // Tell the new activity what the default vehicle is
    intent.putExtra(Vehicle.DEFAULT_VEHICLE_KEY, mCurrentVehicleID);
    startActivityForResult(intent, ADD_FUELING);
  }

  /**
   * Handler for loading Vehicle data for a specified id.
   * @param id the id for the desired Vehicle.
   */
  private void loadForVehicle(int id) {
    //todo add a spinning-circle progress bar
    ArrayList<Fueling> fList = sDatabaseHelper.fetchFuelingData(id);

    if (fList.isEmpty()) {
      addFueling();
    } else {
      loadAverages();
      loadHistoricals(fList);
    }
  }

  /**
   * Handler for loading historical Fueling data into the scrolling ListView.
   * @param fList the complete list of Fuelings for the current Vehicle.
   */
  private void loadHistoricals(ArrayList<Fueling> fList) {
    if (mHistoricalsList == null) {
      mHistoricalsList = (ListView) findViewById(R.id.Main_HistoricalsList);
      if (mHistoricalsList != null) mHistoricalsList.setOnItemClickListener(this);
    }

    mHistoricalsList.setAdapter(new FuelingArrayAdapter(this, fList));
  }

  /**
   * Retrieves averages calculations and writes them into the pre-defined TextViews in the
   * Averages portion of the main screen. This should only be called after a fetch of all of the
   * rows for a particular vehicle.
   *
   * @see #loadForVehicle(int id)
   */
  private void loadAverages() {
    TextView tv;

    /*
    Values for the first row of averages, spanning 3 months
     */
    tv = (TextView) findViewById(R.id.first_average_row_price);
    if (tv != null)
      tv.setText(FormatHandler.formatPrice(Fueling.getAvgPricePerUnitOverSpan(
          Fueling.SPAN_3_MONTHS)));

    tv = (TextView) findViewById(R.id.first_average_row_dist);
    if (tv != null)
      tv.setText(FormatHandler.formatDistance(Fueling.getAvgDistanceOverSpan(
          Fueling.SPAN_3_MONTHS)));

    tv = (TextView) findViewById(R.id.first_average_row_vol);
    if (tv != null)
      tv.setText(FormatHandler.formatVolume(Fueling.getAvgVolumeOverSpan(
          Fueling.SPAN_3_MONTHS)));

    tv = (TextView) findViewById(R.id.first_average_row_efficiency);
    if (tv != null)
      tv.setText(FormatHandler.formatEfficiency(Fueling.getAvgEfficiencyOverSpan(
          Fueling.SPAN_3_MONTHS)));


    /*
    Values for the second row of averages, spanning 6 months
     */
    tv = (TextView) findViewById(R.id.second_average_row_price);
    if (tv != null)
      tv.setText(FormatHandler.formatPrice(Fueling.getAvgPricePerUnitOverSpan(
          Fueling.SPAN_6_MONTHS)));

    tv = (TextView) findViewById(R.id.second_average_row_dist);
    if (tv != null)
      tv.setText(FormatHandler.formatDistance(Fueling.getAvgDistanceOverSpan(
          Fueling.SPAN_6_MONTHS)));

    tv = (TextView) findViewById(R.id.second_average_row_vol);
    if (tv != null)
      tv.setText(FormatHandler.formatVolume(Fueling.getAvgVolumeOverSpan(
          Fueling.SPAN_6_MONTHS)));

    tv = (TextView) findViewById(R.id.second_average_row_efficiency);
    if (tv != null)
      tv.setText(FormatHandler.formatEfficiency(Fueling.getAvgEfficiencyOverSpan(
          Fueling.SPAN_6_MONTHS)));



    /*
    Values for the third row of averages, spanning 12 months
     */
    tv = (TextView) findViewById(R.id.third_average_row_price);
    if (tv != null)
      tv.setText(FormatHandler.formatPrice(Fueling.getAvgPricePerUnitOverSpan(
          Fueling.SPAN_ONE_YEAR)));

    tv = (TextView) findViewById(R.id.third_average_row_dist);
    if (tv != null)
      tv.setText(FormatHandler.formatDistance(Fueling.getAvgDistanceOverSpan(
          Fueling.SPAN_ONE_YEAR)));

    tv = (TextView) findViewById(R.id.third_average_row_vol);
    if (tv != null)
      tv.setText(FormatHandler.formatVolume(Fueling.getAvgVolumeOverSpan(
          Fueling.SPAN_ONE_YEAR)));

    tv = (TextView) findViewById(R.id.third_average_row_efficiency);
    if (tv != null)
      tv.setText(FormatHandler.formatEfficiency(Fueling.getAvgEfficiencyOverSpan(
          Fueling.SPAN_ONE_YEAR)));



    /*
    Values for the fourth row of averages, spanning the lifetime of the vehicle
     */
    tv = (TextView) findViewById(R.id.fourth_average_row_price);
    if (tv != null)
      tv.setText(FormatHandler.formatPrice(Fueling.getAvgPricePerUnitOverSpan(
          Fueling.SPAN_ALL_TIME)));

    tv = (TextView) findViewById(R.id.fourth_average_row_dist);
    if (tv != null)
      tv.setText(FormatHandler.formatDistance(Fueling.getAvgDistanceOverSpan(
          Fueling.SPAN_ALL_TIME)));

    tv = (TextView) findViewById(R.id.fourth_average_row_vol);
    if (tv != null)
      tv.setText(FormatHandler.formatVolume(Fueling.getAvgVolumeOverSpan(
          Fueling.SPAN_ALL_TIME)));

    tv = (TextView) findViewById(R.id.fourth_average_row_efficiency);
    if (tv != null)
      tv.setText(FormatHandler.formatEfficiency(Fueling.getAvgEfficiencyOverSpan(
          Fueling.SPAN_ALL_TIME)));
  }

  /**
   * Handler for when the user touches an item on the ListView of historical Fuelings.
   * @param parent the parent Adapter, see FuelingArrayAdapter class
   * @param v the UI item that was touched
   * @param pos the position in the list that was touched
   * @param id the id of the touched item
   */
  public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
    if (v instanceof LinearLayout) {
      Fueling fd = (Fueling) parent.getItemAtPosition(pos);
      Toast.makeText(this, "Price per mile was " + Float.toString(fd.getPricePerDistance()),
          Toast.LENGTH_LONG).show();
    }
  }







  /*
  ** FOLLOWING ARE METHODS I *MIGHT* HAVE TO DEAL WITH. RESEARCH IS NEEDED.
   */

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onPause() {
    super.onPause();

    // TODO: Temporary save of entered/edited data.
    // Use onPause() for quick, light operations. Use onStop() for heavier, more-permanent
    // shut-down procedures.
  }

  @Override
  protected void onResume() {
    super.onResume();

    // TODO: Retrieve data from temporary storage
  }

  @Override
  protected void onStop() {
    super.onStop();

    // TODO: Save data.
    // Maybe don't need this; maybe onPause() is enough.
  }

  @Override
  protected void onStart() {
    super.onStart();

    // TODO: Retrieve temp data to recover entered-but-not-saved data
    // Maybe don't need this. Maybe onResume() is enough.
    // Use onPause() for quick, light operations. Use onStop() for heavier, more-permanent
    // shut-down procedures.
  }

}
