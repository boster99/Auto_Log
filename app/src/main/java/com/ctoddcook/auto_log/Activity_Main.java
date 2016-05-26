/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ctoddcook.CGenTools.PropertiesHelper;
import com.ctoddcook.CGenTools.Property;

import java.util.ArrayList;


/**
 * The Main activity for this app. This displays a split screen:
 * <ul>
 *   <li>The top of the screen displays averages over some time spans</li>
 *   <li>The bottom of the screen displays historical records</li>
 * </ul>
 * <p/>
 * Created by C. Todd Cook on 5/01/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Activity_Main extends AppCompatActivity implements AdapterView.OnItemClickListener,
    View.OnClickListener, AdapterView.OnItemSelectedListener {
  private static final String TAG = "Activity_Main";
  private static final int PROGRESS = 0x1;
  private static final int ADD_FIRST_VEHICLE = 755;
  private static final int ADD_FUELING = 442;
  private static PropertiesHelper sPH;
  private static DatabaseHelper sDatabaseHelper;
  private int mCurrentVehicleID = 0;
  private ListView mHistoricalsList;
  private DrawerLayout mDrawerLayout;
  private Toolbar mToolbar;
  private ListView mDrawerList;
  private ArrayList<Vehicle> mVehicles;

  /**
   * Called by the system when the UI elements of the activity are created.
   * @param savedInstanceState state information if this is rebuilt
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_nav_drawer);
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    FormatHandler.init(this);

    sDatabaseHelper = DatabaseHelper.getInstance(this);
    PropertiesHelper.setDatabaseHelper(sDatabaseHelper);
    sPH = PropertiesHelper.getInstance();

    setupDrawer();

    populateScreen();
  }

  private void setupVehicleSpinner() {
//    ArrayAdapter adapter = new ArrayAdapter(this, R.layout.vehicle_name_array_adapter,
//        R.id.spinner_vehicle_name, mVehicles);
//    Spinner spinner = (Spinner) findViewById(R.id.main_vehicle_spinner);
//    if (spinner != null)
//      spinner.setAdapter(adapter);
//    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//    /*
//      If we have a vehicle indicated (we should) then set the spinner to show that vehicle as
//      selected.
//    */
//    if (mCurrentVehicleID != 0) {
//      int pos = 0;
//
//      for (int i = 0; i < spinner.getCount(); i++) {
//        int spinnerItemID = ((Vehicle)spinner.getItemAtPosition(i)).getID();
//        if (spinnerItemID == mCurrentVehicleID) {
//          pos = i;
//          break;
//        }
//      }
//
//      spinner.setSelection(pos);
//    }
//  }
//
    // get a cursor providing IDs and NAMEs for each vehicle
    Cursor cursor = sDatabaseHelper.fetchSimpleVehicleListCursor();

    // if the cursor has no results, open the Activity_AddEditVehicle, then try again
    if (cursor.getCount() < 1) {
      Intent intent = new Intent(this, Activity_AddEditVehicle.class);
      intent.putExtra(Activity_AddEditVehicle.KEY_ADD_EDIT_MODE, Activity_AddEditVehicle
          .MODE_ADD);
      startActivity(intent);
      cursor = sDatabaseHelper.fetchSimpleVehicleListCursor();
    }

    // make an adapter from the cursor
    String[] from = new String[]{VehicleDBMap.COLUMN_NAME_NAME};
    int[] to = new int[]{android.R.id.text1};
    SimpleCursorAdapter sca = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
        cursor, from, to, 0);

    // set layout for activated adapter
    sca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    // get xml file spinner and set adapter
    Spinner spinner = (Spinner) this.findViewById(R.id.main_vehicle_spinner);
    if (spinner != null) {
      spinner.setAdapter(sca);

      // set spinner listener to display the selected item ID
      spinner.setOnItemSelectedListener(this);

      /*
      If we have a vehicle indicated (we should) then set the spinner to show that vehicle as
      selected.
       */
      if (mCurrentVehicleID != 0) {
        int pos = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
          SQLiteCursor row = ((SQLiteCursor)spinner.getItemAtPosition(i));
          int spinnerItemId = row.getInt(row.getColumnIndex("_id"));
          if (spinnerItemId == mCurrentVehicleID) {
            pos = i;
            break;
          }
        }

        spinner.setSelection(pos);
      }
    }

  }

  /**
   * <p>Callback method to be invoked when an item in this view has been
   * selected. This callback is invoked only when the newly selected
   * position is different from the previously selected position or if
   * there was no selected item.</p>
   * <p/>
   * Impelmenters can call getItemAtPosition(position) if they need to access the
   * data associated with the selected item.
   *
   * @param parent   The AdapterView where the selection happened
   * @param view     The view within the AdapterView that was clicked
   * @param position The position of the view in the adapter
   * @param id       The row id of the item that is selected
   */
  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    if (mCurrentVehicleID != id && Vehicle.getVehicle(id) != null) {
      mCurrentVehicleID = (int) id;
      loadFuelingsForVehicle(mCurrentVehicleID);
    }
  }

  /**
   * Callback method to be invoked when the selection disappears from this
   * view. The selection can disappear for instance when touch is activated
   * or when the adapter becomes empty.
   *
   * @param parent The AdapterView that now contains no selected item.
   */
  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    Spinner spinner = (Spinner) parent;
    if (spinner.getCount() > 0) {
      spinner.setSelection(0);
      SQLiteCursor row = ((SQLiteCursor)spinner.getItemAtPosition(0));
      int spinnerItemId = row.getInt(row.getColumnIndex("_id"));

      mCurrentVehicleID = spinnerItemId;
      prepareVehicles();
    }
  }

  /**
   * Initialize the sliding navigation drawer, and initialize the hamburger which will open the
   * drawer. This largely follows the demo found here:
   *   http://www.android4devs.com/2015/06/navigation-view-material-design-support.html
   */
  private void setupDrawer() {
    NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
    if (navView == null) return;

    navView.setNavigationItemSelectedListener(new Listener_NavDrawer_Main(this));

    /*
    The ActionBarDrawerToggle puts the menu (aka, "hamburger") icon on the action bar for opening
     the navication drawer.
     */
    ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
        mToolbar, R.string.main_drawer_open, R.string.main_drawer_close) {

      @Override
      public void onDrawerClosed(View drawerView) {
        // I don't care to do anything special in response to the drawer opening
        super.onDrawerClosed(drawerView);
      }

      @Override
      public void onDrawerOpened(View drawerView) {
        // I don't care to do anything special in response to the drawer closing
        super.onDrawerOpened(drawerView);
      }
    };

    // Connecting the actionbarToggle to the drawer
    mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

    // Calling syncState() is required or else your hamburger icon wont show up
    actionBarDrawerToggle.syncState();
  }


  /**
   * Handler for calling other methods to gather vehicle information, then fueling information
   * for that vehicle.
   */
  private void populateScreen() {
    prepareVehicles();
    setupVehicleSpinner();
    if (mCurrentVehicleID > 0)
      loadFuelingsForVehicle(mCurrentVehicleID);
  }

  /**
   * Gets the default Vehicle, then fetches all the Vehicles from the database
   */
  private void prepareVehicles() {
    // Get the default Vehicle
    if (mCurrentVehicleID == 0 && sPH.doesNameExist(Vehicle.DEFAULT_VEHICLE_KEY))
      mCurrentVehicleID = (int) sPH.getLongValue(Vehicle.DEFAULT_VEHICLE_KEY);

    // Fetch the list of vehicles into memory
    mVehicles = sDatabaseHelper.fetchVehicleList();

    /*
    If no vehicles were found in the database, we open the activity to add at least one vehicle.
    Then we fetch the list again. If the list is not empty (expected) we use the newly-added
    vehicle as the Default, and add that default to properties.
     */

    if (mVehicles.isEmpty()) {
      Intent intent = new Intent(this, Activity_AddEditVehicle.class);
      intent.putExtra(Activity_AddEditVehicle.KEY_ADD_EDIT_MODE, Activity_AddEditVehicle.MODE_ADD);
      startActivityForResult(intent, ADD_FIRST_VEHICLE);
    } else {
      if (mCurrentVehicleID == 0) {
        mCurrentVehicleID = mVehicles.get(0).getID();
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
            loadFuelingsForVehicle(mCurrentVehicleID);
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
   * Creates an Intent of type Activity_AddEditFueling, tells it we're in ADD mode (rather than
   * EDIT mode) and tells it the default vehicle ID, and opens the Intent. When the intent is
   * closed, we re-fetch the list of fuelings and redisplay averages and historicals.
   */
  private void addFueling() {
    Intent intent = new Intent(this, Activity_AddEditFueling.class);

    // Tell the new activity whether we're in ADD mode or EDIT mode
    intent.putExtra(Activity_AddEditFueling.KEY_ADD_EDIT_MODE, Activity_AddEditFueling.MODE_ADD);

    // Tell the new activity what the default vehicle is
    intent.putExtra(Vehicle.DEFAULT_VEHICLE_KEY, mCurrentVehicleID);
    startActivityForResult(intent, ADD_FUELING);
  }

  /**
   * Handler for loading Vehicle data for a specified id.
   * @param id the id for the desired Vehicle.
   */
  private void loadFuelingsForVehicle(int id) {
    //todo add a spinning-circle progress bar
    ArrayList<Fueling> fList = sDatabaseHelper.fetchFuelingData(id);

    if (fList.isEmpty()) {
      addFueling();
    } else {
      loadAverages();
      loadHistoricalFuelingsList(fList);
    }
  }

  /**
   * Handler for loading historical Fueling data into the scrolling ListView.
   * @param fList the complete list of Fuelings for the current Vehicle.
   */
  private void loadHistoricalFuelingsList(ArrayList<Fueling> fList) {
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
   * @see #loadFuelingsForVehicle(int id)
   */
  private void loadAverages() {
    TextView tv;
    LinearLayout ll;

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

    ll = (LinearLayout) findViewById(R.id.averages_first_row);
    if (ll != null) ll.setOnClickListener(this);



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

    ll = (LinearLayout) findViewById(R.id.averages_second_row);
    if (ll != null) ll.setOnClickListener(this);



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

    ll = (LinearLayout) findViewById(R.id.averages_third_row);
    if (ll != null) ll.setOnClickListener(this);



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

    ll = (LinearLayout) findViewById(R.id.averages_fourth_row);
    if (ll != null) ll.setOnClickListener(this);
  }


  /**
   * Handler for when the user touches a row in the list of averages.
   * @param v the LinearLayout which generated this call
   */
  public void onClick(View v) {
    if (!(v instanceof LinearLayout)) {
      Toast.makeText(this, "Uh oh ... onClick came form some other type of View",
          Toast.LENGTH_LONG).show();
      return;
    }

    int pos;

    switch (((LinearLayout)v).getId()) {
      case R.id.averages_first_row:
        pos = 0;
        break;
      case R.id.averages_second_row:
        pos = 1;
        break;
      case R.id.averages_third_row:
        pos = 2;
        break;
      case R.id.averages_fourth_row:
        pos = 3;
        break;
      default:
        throw new IllegalArgumentException("Cannot identify the View which generated call to " +
            "onClick(). toString(): " + v.toString());
    }

    Intent intent = new Intent(this, Activity_ViewDetail.class);
    intent.putExtra(Activity_ViewDetail.ARG_TYPE, Activity_ViewDetail.TYPE_AVERAGE);
    intent.putExtra(Activity_ViewDetail.ARG_POSITION, pos);
    intent.putExtra(Activity_ViewDetail.ARG_VEHICLE, Vehicle.getVehicle(mCurrentVehicleID)
        .getName());
    startActivity(intent);
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
      Intent intent = new Intent(this, Activity_ViewDetail.class);
      intent.putExtra(Activity_ViewDetail.ARG_TYPE, Activity_ViewDetail.TYPE_FUELING);
      intent.putExtra(Activity_ViewDetail.ARG_POSITION, pos);
      startActivity(intent);
    }
  }







  /*
  ** FOLLOWING ARE METHODS I *MIGHT* HAVE TO DEAL WITH. RESEARCH IS NEEDED.
   */
  // todo DO I need an appbar menu on main screen?

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main_appbar_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    // This responds to touches on items on the appbar menu
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
