/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import android.text.format.DateUtils;
import android.widget.Toast;

import com.ctoddcook.CGenTools.CLocationTools;
import com.ctoddcook.CGenTools.CLocationWaiter;
import com.ctoddcook.CUITools.DatePickerFragment;
import com.ctoddcook.CUITools.TimePickerFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Activity provides the UI and functions needed to ad a new Fueling Event or edit an
 * existing one.
 * <p>
 * Created by C. Todd Cook on 5/11/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Activity_AddEditFueling extends AppCompatActivity
    implements AdapterView.OnItemSelectedListener, DatePickerFragment.DatePickerCaller,
    TimePickerFragment.TimePickerCaller, CLocationWaiter.locationCaller {

  private static final String TAG = "Activity_AddEditFueling";
  private static DatabaseHelper sDatabaseHelper;

  public static final String KEY_ADD_EDIT_MODE = "com.ctoddcook.auto_log.ADD_EDIT_MODE";
  public static final String KEY_FUELING_ID = "com.ctoddcook.auto_log.FUELING_ID";
  public static final int MODE_ADD = 1;
  public static final int MODE_EDIT = 2;
  public static boolean dupeCheckResult;

  private int mode;
  private Fueling mFueling;
  private Vehicle mVehicle;
  private Date mDateOfFill;
  private Location mGPSLocation;
  private int mYear, mMonth, mDay, mHour, mMinute;

  /**
   * When the activity is created, apart from the standard actions, we retrieve the type of
   * user action (add or edit a mFueling) and if the mode is EDIT. If the mode is to add a new
   * fueling, we create a new one and assign it to the mFueling field; if the mode is to edit
   * an existing fueling, we retrieve that fueling and assign it to mFueling.
   *
   * @param savedInstanceState data saved from prior shut down (probably null)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_fueling);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    sDatabaseHelper = DatabaseHelper.getInstance(this);

    if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    GregorianCalendar gc = new GregorianCalendar();

    mode = getIntent().getIntExtra(KEY_ADD_EDIT_MODE, -1);

    switch (mode) {
      case MODE_ADD:
        mFueling = new Fueling();
        getInitialLocation();
        break;
      case MODE_EDIT:
        int fuelingID = getIntent().getIntExtra(KEY_FUELING_ID, -1);
        if (fuelingID < 0)
          throw new IllegalArgumentException("In edit mode, a Fueling ID must be " +
              "provided");

        mFueling = Fueling.getFueling(fuelingID);
        gc.setTimeInMillis(mFueling.getDateOfFill().getTime());
        break;
      default:
        throw new IllegalArgumentException("Calling process must specify add/edit mode");
    }

    int vehicleID = getIntent().getIntExtra(Vehicle.DEFAULT_VEHICLE_KEY, -1);
    if (vehicleID > 0) {
      mVehicle = Vehicle.getVehicle(vehicleID);
    }
    setupVehicleSpinner();

    // Get the current, local, date and time; set the seconds and milliseconds to 0
    gc.set(Calendar.SECOND, 0);
    gc.set(Calendar.MILLISECOND, 0);
    mDateOfFill = gc.getTime();
    mYear = gc.get(Calendar.YEAR);
    mMonth = gc.get(Calendar.MONTH);
    mDay = gc.get(Calendar.DATE);
    mHour = gc.get(Calendar.HOUR_OF_DAY);
    mMinute = gc.get(Calendar.MINUTE);

    // Display the date and time
    displayDateOfFill();
  }

  /**
   * Puts together a spinner with a list of all of the vehicles in the database. Each record's
   * "name" is used for the list items display.
   */
  private void setupVehicleSpinner() {
    // get a cursor providing IDs and NAMEs for each vehicle
    Cursor cursor = sDatabaseHelper.fetchSimpleVehicleListCursor(false);

    // if the cursor has no results, open the Activity_AddEditVehicle, then try again
    if (cursor.getCount() < 1) {
      Intent intent = new Intent(this, Activity_AddEditVehicle.class);
      intent.putExtra(Activity_AddEditVehicle.KEY_ADD_EDIT_MODE, Activity_AddEditVehicle
          .MODE_ADD);
      startActivity(intent);
      cursor = sDatabaseHelper.fetchSimpleVehicleListCursor(false);
    }

    // make an adapter from the cursor
    String[] from = new String[]{VehicleDBMap.COLUMN_NAME_NAME};
    int[] to = new int[]{android.R.id.text1};
    SimpleCursorAdapter sca = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
        cursor, from, to, 0);

    // set layout for activated adapter
    sca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    // get xml file spinner and set adapter
    Spinner spinner = (Spinner) this.findViewById(R.id.fueling_vehicle_spinner);
    if (spinner != null) {
      spinner.setAdapter(sca);

      // set spinner listener to display the selected item ID
      spinner.setOnItemSelectedListener(this);

      /*
      If we have a vehicle indicated (we should) then set the spinner to show that vehicle as
      selected.
       */
      if (mVehicle != null) {
        int pos = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
          SQLiteCursor row = ((SQLiteCursor)spinner.getItemAtPosition(i));
          int spinnerItemId = row.getInt(row.getColumnIndex("_id"));
          if (spinnerItemId == mVehicle.getID()) {
            pos = i;
            break;
          }
        }

        spinner.setSelection(pos);
      }
    }
  }

  private void getInitialLocation() {
    final int maxDelay = 1500;
    /*
    In ADD mode, we get the latitude and longitude and provide city and state. This will get
    the last known location, but will wait up to maxDelay milliseconds for a better location.
    Whichever comes first, it will then call setLocation() in this class to provide that location
    information.
    */
    new CLocationWaiter(this, maxDelay);
  }

  /**
   * Callback method used by CLocationWaiter to provide gps-based or network-based geo
   * coordinates. We use those coordinates to find the city and state, and provide that in the
   * "Location" field as a help to the user.
   * <p>
   * Note this should only be called when we are adding a new Fueling instance only, so we
   * don't overwrite a location description the user has already provided.
   * @param gpsLocation the location retrieved by CLocationWaiter
   */
  @Override
  public void setLocation(Location gpsLocation) {
    String cityAndState = null;
    if (gpsLocation != null) {
      cityAndState = CLocationTools.getCity(this, gpsLocation,
          CLocationTools.OPTION_INCLUDE_STATE_ABBREV);
      mGPSLocation = gpsLocation;
    }

    if (cityAndState != null) {
      EditText locET = (EditText) findViewById(R.id.fueling_edit_location);

      /*
      If the Location field is not empty, then we exit so we don't overwrite something the
      user has entered.
      */
      if (locET == null) return;
      if (locET.getText() != null && locET.getText().toString().length() > 0)
        return;

      /*
      If we've gotten here, then it's safe to initialize the Location field with city ane state.
       */
      locET.setText(cityAndState);
    }
  }

  /**
   * When a user selects a Vehicle from the spinner, update the mVehicle field with the correct
   * vehicle object.
   *
   * @param parent the parent spinner, in effect
   * @param v      the View which triggered this method call
   * @param pos    the position of the view in the adapter
   * @param id     the row ID of the item that is selected
   */
  public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
    mVehicle = Vehicle.getVehicle(id);
  }


  public void onNothingSelected(AdapterView<?> parent) {
    // Another interface callback
  }

  /**
   * When the user touches "Save", extract all of the entered details, and then--depending on
   * whether we're in ADD or EDIT mode, insert a new fueling or update an existing one in the
   * database.
   *
   * @param v the view which triggered this method call
   */
  public void saveFueling(View v) {
    if (extractDetails()) {
      switch (mode) {
        case MODE_ADD:
          sDatabaseHelper.insertFueling(mFueling);
          break;
        case MODE_EDIT:
          sDatabaseHelper.updateFueling(mFueling);
          break;
        default:
          throw new IllegalArgumentException("Member field mode does not " +
              "indicate either ADD or EDIT");
      }
    }

    Intent intent = new Intent();
    intent.putExtra(Vehicle.DEFAULT_VEHICLE_KEY, mVehicle.getID());
    setResult(RESULT_OK, intent);
    this.finish();
  }

  /**
   * Extracts data from the UI fields -- i.e., entered by the user -- and puts them into the
   * Fueling object which will be saved to the database.
   * <p>
   * <strong>Note:</strong> We do not do anything with Location (GPS) data here.
   * GPS-based (or network-based) location data is gathered when a new Fueling object is
   * created by this activity, and is used to initialize the "Location" field for the user. While
   * we keep (and save to the database) the system-provided location information, we don't give
   * the user a means to update it. The idea is that the user can updated their descriptive
   * "Location" field if they want to, but it would be troublesome for a user to enter correct
   * GPS coordinates.
   * <p>
   * Sanity Checks:
   * <ul>
   *   <li>A valid date must be provided</li>
   *   <li>The distance must be greater than 0</li>
   *   <li>The volume must be greater than 0</li>
   *   <li>The price paid doesn't have to be provided, but if it's provided it can't be
   *   negative</li>
   * </ul>
   * @return true if no problems were encountered, and no sanity checks failed
   */
  public boolean extractDetails() {
    Date date;
    float distance = 0f, volume = 0f, pricePaid = 0f, odometer = 0f;
    String location = null;

    date = mDateOfFill;

    EditText distET = (EditText) findViewById(R.id.fueling_edit_distance);
    if (distET != null && distET.getText() != null && !distET.getText().toString().isEmpty())
      distance = Float.parseFloat(distET.getText().toString());

    EditText volET = (EditText) findViewById(R.id.edit_volume);
    if (volET != null && volET.getText() != null && !volET.getText().toString().isEmpty())
      volume = Float.parseFloat(volET.getText().toString());

    EditText paidET = (EditText) findViewById(R.id.fueling_edit_total_price_paid);
    if (paidET != null && paidET.getText() != null && !paidET.getText().toString().isEmpty())
      pricePaid = Float.parseFloat(paidET.getText().toString());

    EditText locET = (EditText) findViewById(R.id.fueling_edit_location);
    if (locET != null && locET.getText() != null)
      location = locET.getText().toString().trim();

    EditText odoET = (EditText) findViewById(R.id.fueling_edit_odometer);
    if (odoET != null && odoET.getText() != null && !odoET.getText().toString().isEmpty())
      odometer = Float.parseFloat(odoET.getText().toString());

    if (date == null) {
      Toast.makeText(this, "Uh oh ... a Date and Time are needed for this to work", Toast
          .LENGTH_LONG).show();
      return false;
    }

    if (distance <= 0f) {
      Toast.makeText(this, "Dang! A valid Distance is required", Toast.LENGTH_LONG).show();
      return false;
    }

    if (volume <= 0f) {
      Toast.makeText(this, "Mmm, uhm, gonna have to have a good Volume", Toast.LENGTH_LONG)
          .show();
      return false;
    }

    if (pricePaid < 0f) {
      Toast.makeText(this, ("Well...you don't HAVE to provide a price paid, but it can't be " +
          "negative"), Toast.LENGTH_LONG).show();
      return false;
    }

    mFueling.setVehicleID(mVehicle.getID());
    mFueling.setDateOfFill(mDateOfFill);
    mFueling.setDistance(distance);
    mFueling.setVolume(volume);
    mFueling.setPricePaid(pricePaid);
    mFueling.setOdometer(odometer);
    mFueling.setLocation(location);

    if (mGPSLocation != null) {
      mFueling.setLatitude((float)mGPSLocation.getLatitude());
      mFueling.setLongitude((float)mGPSLocation.getLongitude());
    }

    return true;
  }

  /**
   * User has touched "Cancel". Just get out of here.
   *
   * @param v The View which triggered this method call
   */
  public void cancelAddFueling(View v) {
    setResult(RESULT_CANCELED);
    this.finish();
  }


  /**
   * Let the user know we've found a duplicate of the data just entered, and ask her if
   * she wants to save it anyway. Return an affirmative or negative to the calling method.
   *
   * @return the user's decision whether to save a duplicate
   */
  private boolean userWantsDuplicate() {
    AlertDialog.Builder b = new AlertDialog.Builder(this);
    b.setMessage("There is already a Fueling with the same Vehicle, Date, Distance, Volume " +
        "and Price Paid. Do you want to save this as a duplicate?");
    b.setPositiveButton("Yes, Save Duplicate", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface d, int id) {
        Activity_AddEditFueling.dupeCheckResult = true;
      }
    });
    b.setNegativeButton("No thank you", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface d, int id) {
        Activity_AddEditFueling.dupeCheckResult = false;
      }
    });

    return Activity_AddEditFueling.dupeCheckResult;
  }


  /**
   * Opens a Date Picker dialog for the user to change the date of fill
   *
   * @param v the view which called this message
   */
  public void showDatePickerDialog(View v) {
    DialogFragment datePicker = DatePickerFragment.newInstance(mDateOfFill.getTime());
    datePicker.show(getSupportFragmentManager(), "datePicker");
  }

  /**
   * Used by the Date Picker dialog to pass back the date indicated by the user
   *
   * @param year  the year selected by the user
   * @param month the month selected by the user
   * @param day   the day selected by the user
   */
  public void setDate(int year, int month, int day) {
    mYear = year;
    mMonth = month;
    mDay = day;

    mDateOfFill = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute).getTime();

    displayDateOfFill();
  }

  /**
   * Opens a Time Picker dialog for the user to change the time of fill
   *
   * @param v the View which called this method
   */
  public void showTimePickerDialog(View v) {
    DialogFragment timePicker = TimePickerFragment.newInstance(mDateOfFill.getTime());
    timePicker.show(getSupportFragmentManager(), "timePicker");
  }

  /**
   * Used by the Time Picker dialog to pass back the time indicated by the user.
   *
   * @param hour   the hour selected by the user
   * @param minute the minute selected by the user
   */
  public void setTime(int hour, int minute) {
    mHour = hour;
    mMinute = minute;

    mDateOfFill = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute).getTime();

    displayDateOfFill();
  }

  /**
   * Present the working DateOfFill (not yet stored into a Fueling object, though it may
   * have come from one if we're in EDIT mode). Splits the information into separate Date and
   * Time components, so Date Picker and Time Picker dialogs may be used to update these fields.
   */
  private void displayDateOfFill() {
    TextView dateTV = (TextView) findViewById(R.id.fueling_edit_date);
    TextView timeTV = (TextView) findViewById(R.id.fueling_edit_time);

    String date = DateUtils.formatDateTime(this, mDateOfFill.getTime(),
        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
    String time = DateUtils.formatDateTime(this, mDateOfFill.getTime(),
        DateUtils.FORMAT_SHOW_TIME);

    if (dateTV != null) dateTV.setText(date, TextView.BufferType.NORMAL);
    if (timeTV != null) timeTV.setText(time, TextView.BufferType.NORMAL);
  }
}
