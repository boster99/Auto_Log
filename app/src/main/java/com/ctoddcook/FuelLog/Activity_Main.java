/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.FuelLog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
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
import com.ctoddcook.CUITools.UIHelper;

import java.util.ArrayList;


// TODO Reorganize this mess

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
    View.OnClickListener, AdapterView.OnItemSelectedListener,
    DataUpdateController.DataUpdateListener {
  private static final String TAG = "Activity_Main";
  private static final int PROGRESS = 0x1;
  private static PropertiesHelper sPH;
  private static DatabaseHelper sDatabaseHelper;
  private int mCurrentVehicleID = 0;
  private ListView mHistoricalsList;
  private DrawerLayout mDrawerLayout;
  private Toolbar mToolbar;
  private ListView mDrawerList;
  private Fueling mFuelingToDelete;

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

    DataUpdateController.getInstance().setOnDataUpdatedListener(this);
    setupDrawer();

    populateScreen();
    showHint();

//    ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
//    try {
//      DatabaseToXmlExporter d2x = DatabaseToXmlExporter.getInstance(sDatabaseHelper, baos);
//      d2x.writeTable(PropertyDataMap.TABLE_NAME, PropertyDataMap._ID);
//      String p = baos.toString();
//      d2x.writeTable(FuelingDBMap.TABLE_NAME, FuelingDBMap._ID);
//      String f = baos.toString();
//      d2x.writeTable(VehicleDBMap.TABLE_NAME, VehicleDBMap._ID);
//      String v = baos.toString();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
  }

  /**
   * Displays an instructional hint to the user. Only shown the first time the uesr sees this
   * screen (or after HINT settings have been reset).
   */
  private void showHint() {
    UIHelper.showHint(this, Hints.FUELING_LIST_HINT_KEY, null, getString(R.string.fueling_list_hint));
  }

  // todo make the spinner work with a custom adapter
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
    // get a cursor providing IDs and NAMEs for each vehicle (include retired vehicles)
    Cursor cursor = sDatabaseHelper.fetchSimpleVehicleListCursor(true);

    // if the cursor has no results, open the Activity_AddEditVehicle, then try again
    if (cursor.getCount() < 1) {
      Intent intent = new Intent(this, Activity_AddEditVehicle.class);
      intent.putExtra(Activity_AddEditVehicle.KEY_ADD_EDIT_MODE, Activity_AddEditVehicle
          .MODE_ADD);
      startActivity(intent);
      cursor = sDatabaseHelper.fetchSimpleVehicleListCursor(true);
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
        int pos = -1;

        for (int i = 0; i < spinner.getCount(); i++) {
          SQLiteCursor row = ((SQLiteCursor)spinner.getItemAtPosition(i));
          int spinnerItemId = row.getInt(row.getColumnIndex("_id"));
          if (spinnerItemId == mCurrentVehicleID) {
            pos = i;
            break;
          }
        }

        /*
        If we did not find mCurrentVehicleID in the spinner, then it most likely was deleted. In
        that case, set the spinner and mCurrentVehicleID to the first one in the list.
         */
        if (pos < 0) {
          SQLiteCursor row = ((SQLiteCursor)spinner.getItemAtPosition(0));
          mCurrentVehicleID = row.getInt(row.getColumnIndex("_id"));
          pos = 0;
        }
        spinner.setSelection(pos);
      }
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

    if (v.getId() != R.id.Main_HistoricalsList) return;  // Context menu ONLY for fuelings

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.fueling_popup_menu, menu);
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

    Fueling fueling = (Fueling) mHistoricalsList.getItemAtPosition(info.position);
    int fuelingID = fueling.getID();

    switch (item.getItemId()) {
      case R.id.fueling_edit:
        editFueling(fuelingID);
        return true;
      case R.id.fueling_delete:
        deleteFueling(fuelingID);
        return true;
      default:
        return super.onContextItemSelected(item);
    }
  }



  /**
   * <p>Callback method to be invoked when an item in this view has been
   * selected. This callback is invoked only when the newly selected
   * position is different from the previously selected position or if
   * there was no selected item.</p>
   * <p/>
   * Implementers can call getItemAtPosition(position) if they need to access the
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
      loadFuelings(mCurrentVehicleID);
    }
  }

  /**
   * If the list of vehicles or fuelings gets changed, refresh the display as needed.
   * @param event The type of data updated
   * @param data Extra information if needed
   */
  public void onDataUpdated(DataUpdateController.DataUpdateEvent event, Intent data) {
    switch (event) {
      case VEHICLE_LIST_UPDATED:
        loadVehicles();

        break;
      case FUELING_LIST_UPDATED:
        int id = (data != null ? data.getIntExtra(Vehicle.DEFAULT_VEHICLE_KEY, 0) :
            mCurrentVehicleID);
        if (id > 0 && id != mCurrentVehicleID) {
          mCurrentVehicleID = id;
        }
        loadFuelings(mCurrentVehicleID);
        break;
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
      mCurrentVehicleID = row.getInt(row.getColumnIndex("_id"));
      loadVehicles();
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
     the navigation drawer.
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
    loadVehicles();
    if (mCurrentVehicleID > 0)
      loadFuelings(mCurrentVehicleID);
  }

  /**
   * Gets the default Vehicle, then fetches all the Vehicles from the database, and sets up the
   * vehicle spinner.
   */
  private void loadVehicles() {
    ArrayList<Vehicle> vehicles;

    // Get the default Vehicle
    if (mCurrentVehicleID == 0 && sPH.doesNameExist(Vehicle.DEFAULT_VEHICLE_KEY))
      mCurrentVehicleID = (int) sPH.getLongValue(Vehicle.DEFAULT_VEHICLE_KEY);

    // Fetch the list of vehicles into memory
    vehicles = sDatabaseHelper.fetchVehicleList();

    /*
    If no vehicles were found in the database, we open the activity to add at least one vehicle.
    Then we fetch the list again. If the list is not empty (expected) we use the newly-added
    vehicle as the Default, and add that default to properties.
     */

    if (vehicles.isEmpty()) {
      Intent intent = new Intent(this, Activity_AddEditVehicle.class);
      intent.putExtra(Activity_AddEditVehicle.KEY_ADD_EDIT_MODE, Activity_AddEditVehicle.MODE_ADD);
      startActivity(intent);
    } else {
      if (mCurrentVehicleID == 0) {
        mCurrentVehicleID = vehicles.get(0).getID();
        Property p = new Property(Vehicle.DEFAULT_VEHICLE_KEY, mCurrentVehicleID);
        sPH.put(p);
      }
    }

    setupVehicleSpinner();
  }

  /**
   * Starts the activity for viewing details of a fueling.
   * @param pos The position in the list of fuelings which holds the fueling to view
   */
  private void viewFueling(int pos) {
    Intent intent = new Intent(this, Activity_ViewDetail.class);
    intent.putExtra(Activity_ViewDetail.ARG_TYPE, Activity_ViewDetail.TYPE_FUELING);
    intent.putExtra(Activity_ViewDetail.ARG_POSITION, pos);
    startActivity(intent);
  }

  /**
   * Creates an Intent of type Activity_AddEditFueling, tells it we're in ADD mode (rather than
   * EDIT mode) and tells it the default vehicle ID, and opens the Intent. When the intent is
   * closed, we re-fetch the list of fuelings and redisplay averages and historicals.
   * @param v The View (a button) which called this method (required by the framework, since this
   *          method is referenced in the menu xml onClick entry, but not used here)
   */
  public void addFueling(@Nullable View v) {
    if (mCurrentVehicleID < 1) {
      Toast.makeText(this, "Oops! Please select a vehicle before adding a fueling to it.",
          Toast.LENGTH_LONG).show();
      return;
    }

    if (Vehicle.getVehicle(mCurrentVehicleID).isRetired()) {
      Toast.makeText(this, "Uhm, this vehicle is retired. Please choose an" +
          " active vehicle, and then try again", Toast.LENGTH_LONG).show();
      return;
    }

    Intent intent = new Intent(this, Activity_AddEditFueling.class);

    // Tell the new activity whether we're in ADD mode
    intent.putExtra(Activity_AddEditFueling.KEY_ADD_EDIT_MODE, Activity_AddEditFueling.MODE_ADD);

    // Tell the new activity what the default vehicle is
    intent.putExtra(Vehicle.DEFAULT_VEHICLE_KEY, mCurrentVehicleID);
    startActivity(intent);
  }

  /**
   * Opens the Add/Edit Fueling activity, put it in EDIT mode, and give it the fueling id to edit.
   * @param fuelingID The ID of the fueling to edit
   */
  private void editFueling(int fuelingID) {
    Intent intent = new Intent(this, Activity_AddEditFueling.class);

    // Tell the new activity whether we're in EDIT mode, and which fueling is to be edited
    intent.putExtra(Activity_AddEditFueling.KEY_ADD_EDIT_MODE, Activity_AddEditFueling.MODE_EDIT);
    intent.putExtra(Activity_AddEditFueling.KEY_FUELING_ID, fuelingID);
    intent.putExtra(Vehicle.DEFAULT_VEHICLE_KEY, mCurrentVehicleID);

    startActivity(intent);
  }

  /**
   * Get confirmation from the user that s/he really wants to delete the selected fueling, then
   * delete it (or cancel).
   * @param fuelingID The id of the fueling to be deleted.
   */
  private void deleteFueling(int fuelingID) {
    // Note the vehicle which is to be deleted
    mFuelingToDelete = Fueling.getFueling(fuelingID);
    if (mFuelingToDelete == null) return;

    // Setup the listeners which will respond to the user's response to the dialog
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which){
          // If the user clicks "YES" then we delete the vehicle
          case DialogInterface.BUTTON_POSITIVE:
            mFuelingToDelete.setDeleted();
            sDatabaseHelper.deleteFueling(mFuelingToDelete);
            mFuelingToDelete = null;
            DataUpdateController.getInstance().dispatchDataUpdateEvent(
                DataUpdateController.DataUpdateEvent.FUELING_LIST_UPDATED, null);
            break;

          // If the user clicks "NO" then we clean up and get out of here
          case DialogInterface.BUTTON_NEGATIVE:
            mFuelingToDelete = null;
            break;
        }
      }
    };

    // Set up and display the dialog to get the user's confirmation that the fueling should be
    // deleted.
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Delete Fueling");
    builder.setMessage("Deleting a fueling cannot be undone. Are you sure you want to do this?")
        .setPositiveButton("Yes, delete it", dialogClickListener)
        .setNegativeButton("No, never mind", dialogClickListener).show();
  }

  /**
   * Handler for loading Vehicle data for a specified id.
   * @param id the id for the desired Vehicle.
   */
  private void loadFuelings(int id) {
    ArrayList<Fueling> fList = sDatabaseHelper.fetchFuelingData(id);

    if (fList.isEmpty()) {
      addFueling(null);
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
      if (mHistoricalsList != null) {
        mHistoricalsList.setOnItemClickListener(this);
        registerForContextMenu(mHistoricalsList);
      }
    }

    mHistoricalsList.setAdapter(new FuelingArrayAdapter(this, fList));
  }

  /**
   * Retrieves averages calculations and writes them into the pre-defined TextViews in the
   * Averages portion of the main screen. This should only be called after a fetch of all of the
   * rows for a particular vehicle.
   *
   * @see #loadFuelings(int id)
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
      tv.setText(FormatHandler.formatVolumeShort(Fueling.getAvgVolumeOverSpan(
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
      tv.setText(FormatHandler.formatVolumeShort(Fueling.getAvgVolumeOverSpan(
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
      tv.setText(FormatHandler.formatVolumeShort(Fueling.getAvgVolumeOverSpan(
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
      tv.setText(FormatHandler.formatVolumeShort(Fueling.getAvgVolumeOverSpan(
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

    switch (v.getId()) {
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
      viewFueling(pos);
    }
  }
}