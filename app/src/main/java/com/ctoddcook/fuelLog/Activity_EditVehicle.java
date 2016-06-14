/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.FuelLog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ctoddcook.CamUiTools.Handler_Hints;

import java.util.Locale;

/**
 * This activity is used for adding a new, or editing an existing Model_Vehicle.
 */
public class Activity_EditVehicle extends AppCompatActivity {
  public static final String KEY_ADD_EDIT_MODE = "com.ctoddcook.FuelLog.ADD_EDIT_MODE";
  public static final String KEY_VEHICLE_ID = "com.ctoddcook.FuelLog.VEHICLE_ID";
  public static final int MODE_ADD = 1;
  public static final int MODE_EDIT = 2;
  private static final String TAG = "Activity_EditVehicle";
  private static DatabaseHelper sDatabaseHelper;
  EditText mNameET, mColorET, mYearET, mModelET, mLicensePlateET, mVinET;
  String mName, mColor, mModel, mVin, mLicensePlate;
  int mYear = 0;
  private int mMode;
  private Model_Vehicle mVehicle;

  /**
   * When the activity is created, apart from the standard actions, we retrieve the type of
   * user action (add or edit a mVehicle) and if the mMode is EDIT. If the mMode is to add a new
   * vehicle, we create a new one and assign it to the mVehicle field; if the mMode is to edit
   * an existing vehicle, we retrieve that vehicle and assign it to mVehicle.
   *
   * @param savedInstanceState data saved from prior shut down (probably null)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_vehicle);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit_vehicle);
    if (toolbar != null) {
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      toolbar.setLogo(R.drawable.ic_car);
    }

    sDatabaseHelper = DatabaseHelper.getInstance(this);

    mNameET = (EditText) findViewById(R.id.vehicle_edit_name);
    mColorET = (EditText) findViewById(R.id.vehicle_edit_color);
    mYearET = (EditText) findViewById(R.id.vehicle_edit_year);
    mModelET = (EditText) findViewById(R.id.vehicle_edit_model);
    mLicensePlateET = (EditText) findViewById(R.id.vehicle_edit_license_plate);
    mVinET = (EditText) findViewById(R.id.vehicle_edit_vin);

    mMode = getIntent().getIntExtra(KEY_ADD_EDIT_MODE, 0);

    switch (mMode) {
      case MODE_ADD:
        mVehicle = new Model_Vehicle();
        break;
      case MODE_EDIT:
        int vehicleID = getIntent().getIntExtra(KEY_VEHICLE_ID, -1);
        if (vehicleID < 0)
          throw new IllegalArgumentException("In edit mMode, a Model_Vehicle ID must be provided");

        mVehicle = Model_Vehicle.getVehicle(vehicleID);
        populateFromVehicle();
        break;
      default:
        throw new IllegalArgumentException("Calling process must specify add/edit mMode");
    }


    Handler_Hints.showHint(this, Handler_FuelLogHints.FIRST_VEHICLE_HINT_KEY,
        getString(R.string.first_vehicle_hint_title),
        getString(R.string.first_vehicle_hint));

  }

  /**
   * Populates the display with details from the current vehicle.
   */
  private void populateFromVehicle() {
    mNameET.setText(mVehicle.getName());
    mColorET.setText(mVehicle.getColor());
    mYearET.setText(String.format(Locale.getDefault(), "%d", mVehicle.getYear()));
    mModelET.setText(mVehicle.getModel());
    mLicensePlateET.setText(mVehicle.getLicensePlate());
    mVinET.setText(mVehicle.getVIN());
  }

  /**
   * Manages the extraction of data from the text fields, sanity checks, and saving the record to
   * the database.
   * @param v The view (a button) that called this method
   */
  public void processEdits(View v) {
    extractDetails();
    if (sanityChecksPass() && duplicateChecksPass())
      saveVehicle();
  }

  /**
   * Extract the details entered by the user and use them to fill out or update the mVehicle
   * member field.
   */
  private void extractDetails() {
    mName = mNameET.getText().toString().trim();
    mColor = mColorET.getText().toString().trim();
    mModel = mModelET.getText().toString().trim();
    mVin = mVinET.getText().toString().trim();
    mLicensePlate = mLicensePlateET.getText().toString().trim().toUpperCase();

    try {
      mYear = Integer.parseInt(mYearET.getText().toString());
    } catch (NumberFormatException e) {
      Log.e(TAG, "extractDetails: Vehicle model Year EditText value is: "
          + mYearET.getText().toString(), e);
    }

    // If the user has not provided a mName, make one out of the mColor, mYear and mModel
    if (mName.length() < 1) {
      mName = mColor + " " + mYear + " " + mModel;
    }
  }

  /**
   * Sanity-check the data provided by the user. If something is not acceptable, we let the user
   * know about it.
   * @return Yes or no, did the data pass sanity checks
   */
  private boolean sanityChecksPass() {
    // The user must provide either a name, or color and model and year.
    if (mName.length() < 1) {
      if (mColor.length() < 1 || mModel.length() < 1 || mYear == 0) {
        Toast.makeText(this, "Uh oh! If you don't supply a mName, you must supply a " +
            "mColor, mYear and mModel", Toast.LENGTH_LONG).show();
        return false;
      }
    }

    /*
    Sanity-check the model year. We accept 0 (i.e., user chooses not to provide a year)
    but otherwise require a value between 1900 and 2100 (think anyone will be using
    this in 2100?)
     */
    if (mYear != 0 && (mYear < 1970 || mYear > 2100)) {
      Toast.makeText(this, "Whoops! " + mYear + " is not a valid mModel mYear",
          Toast.LENGTH_LONG).show();
      return false;
    }

    return true;
  }

  /**
   * Compares the user-provided details to see if they appear to duplicate those of another
   * vehicle. If they do look like a duplicate, we ask the user if s/he wants to save the edits
   * anyway.
   * @return True or false, does the data duplicate that of an existing vehicle
   */
  private boolean duplicateChecksPass() {
    /*
    Check existing Model_Vehicle instances for same Color, Year and Model, or with the same mName.
    If a similar mVehicle is found, ask the user if this duplicate should be saved.
     */
    for (Model_Vehicle each : Model_Vehicle.getVehicleList()) {
      if (each == mVehicle) continue;
      if (each.isDuplicate(mName, mColor, mYear, mModel, mVin, mLicensePlate)) {
        promptUserForDuplicate();
        return false;
      }
    }

    return true;
  }

  /**
   * Place the user-provided details into the vehicle instance and save it to the database.
   */
  public void saveVehicle() {
    mVehicle.setName(mName);
    mVehicle.setYear(mYear);
    mVehicle.setColor(mColor);
    mVehicle.setModel(mModel);
    mVehicle.setVIN(mVin);
    mVehicle.setLicensePlate(mLicensePlate);

    switch (mMode) {
      case MODE_ADD:
        sDatabaseHelper.insertVehicle(mVehicle);
        break;
      case MODE_EDIT:
        sDatabaseHelper.updateVehicle(mVehicle);
        break;
      default:
        throw new IllegalArgumentException("Member field mMode does not " +
            "indicate either ADD or EDIT");
    }

    Handler_DataEvents.getInstance().dispatchDataUpdateEvent(
        Handler_DataEvents.DataUpdateEvent.VEHICLE_LIST_UPDATED, null);
    this.finish();
  }

  /**
   * Let the user know we've found a duplicate of the data just entered, and ask if s/he
   * wants to save it anyway. If yse, we call the method to save the vehicle to the database.
   */
  private void promptUserForDuplicate() {
    // Setup the listener which will respond to the user's response to the dialog
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which){
          // If the user clicks "YES" then save the duplicate vehicle
          case DialogInterface.BUTTON_POSITIVE:
            saveVehicle();
            break;

          // If the user clicks "NO" then we return to the edit screen
          case DialogInterface.BUTTON_NEGATIVE:
            break;
        }
      }
    };

    // Set up and display the dialog to get the user's confirmation that the vehicle should be
    // saved despite the duplication of data.
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Duplicate?");
    builder.setMessage(getString(R.string.edit_vehicle_duplicate_warning));
    builder.setPositiveButton("Yes, Save Duplicate", dialogClickListener);
    builder.setNegativeButton("No thank you", dialogClickListener);
    builder.show();
  }


  /**
   * Close the activity if the user presses "Cancel"
   *
   * @param v the view that called this method
   */
  public void cancelAddVehicle(View v) {
    this.finish();
  }
}
