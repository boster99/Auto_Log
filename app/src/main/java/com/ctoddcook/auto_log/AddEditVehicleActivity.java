/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddEditVehicleActivity extends AppCompatActivity {
  public static final String KEY_ADD_EDIT_MODE = "com.ctoddcook.auto_log.ADD_EDIT_MODE";
  public static final String KEY_VEHICLE_ID = "com.ctoddcook.auto_log.VEHICLE_ID";
  public static final int MODE_ADD = 1;
  public static final int MODE_EDIT = 2;
  public static boolean dupeCheckResult;
  private int mode;
  private VehicleData mVehicle;

  private static final String TAG = "AddEditVehicleActivity";

  /**
   * When the activity is created, apart from the standard actions, we retrieve the type of
   * user action (add or edit a mVehicle) and if the mode is EDIT. If the mode is to add a new
   * vehicle, we create a new one and assign it to the mVehicle field; if the mode is to edit
   * an existing vehicle, we retrieve that vehicle and assign it to mVehicle.
   *
   * @param savedInstanceState data saved from prior shut down (probably null)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_vehicle);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mode = getIntent().getIntExtra(KEY_ADD_EDIT_MODE, 0);

    switch (mode) {
      case MODE_ADD:
        mVehicle = new VehicleData();
        break;
      case MODE_EDIT:
        int vehicleID = getIntent().getIntExtra(KEY_VEHICLE_ID, -1);
        if (vehicleID < 0)
          throw new IllegalArgumentException("In edit mode, a Vehicle ID must be provided");

        mVehicle = VehicleData.getVehicle(vehicleID);
        break;
      default:
        throw new IllegalArgumentException("Calling process must specify add/edit mode");
    }
  }


  /**
   * When the user touches "Save", extract all of the entered details, and then--depending on
   * whether we're in ADD or EDIT mode, insert a new vehicle or update an existing one in the
   * database.
   *
   * @param v the view which triggered this method call
   */
  public void saveVehicle(View v) {
    if (extractDetails()) {
      DatabaseHelper dh = new DatabaseHelper(this);
      switch (mode) {
        case MODE_ADD:
          dh.insertVehicle(mVehicle);
          break;
        case MODE_EDIT:
          dh.updateVehicle(mVehicle);
          break;
        default:
          throw new IllegalArgumentException("Member field mode does not " +
              "indicate either ADD or EDIT");
      }
    }

    setResult(RESULT_OK);
    this.finish();
  }

  /**
   * Extract the details entered by the user and use them to fill out or update the mVehicle
   * member field. Also do a little bit of sanity checking on the data. Also, check
   *
   * @return true if all the user-provided details are usable, or false if the user screwed up
   */
  private boolean extractDetails() {
    String name = null, color = null, model = null, vin = null, licPlate = null;
    int year = 0;

    EditText nameET = (EditText) findViewById(R.id.vehicle_edit_name);
    EditText yearET = (EditText) findViewById(R.id.vehicle_edit_year);
    EditText colorET = (EditText) findViewById(R.id.vehicle_edit_color);
    EditText modelET = (EditText) findViewById(R.id.vehicle_edit_model);
    EditText vinET = (EditText) findViewById(R.id.vehicle_edit_vin);
    EditText licPlateET = (EditText) findViewById(R.id.vehicle_edit_license_plate);

    if (nameET != null) name = nameET.getText().toString().trim();
    if (colorET != null) color = colorET.getText().toString().trim();
    if (modelET != null) model = modelET.getText().toString().trim();
    if (vinET != null) vin = vinET.getText().toString().trim();
    if (licPlateET != null) licPlate = licPlateET.getText().toString().trim().toUpperCase();

    try {
      if (yearET != null) year = Integer.parseInt(yearET.getText().toString());
    } catch (NumberFormatException e) {
      Log.e(TAG, "extractDetails: mVehicle year EditText value is: "
          + yearET.getText().toString(), e);
    }

        /*
        Sanity-check the year. We accept 0 (i.e., user chooses not to provide a year)
        but otherwise require a year between 1900 and 2100 (think anyone will be using
        this in 2100?)
         */
    if (year != 0 && (year < 1900 || year > 2100)) {
      Toast.makeText(this, "Whoops! " + year + " is not a valid model year",
          Toast.LENGTH_LONG).show();
      return false;
    }

        /*
        If the user has not provided a name, make one out of the color, year and model
         */
    if (name == null || name.length() < 1) {
      if ((color == null || color.length() < 1) || (model == null || model.length() < 1) ||
          year == 0) {
        Toast.makeText(this, "Uh oh! If you don't supply a name, you must supply a " +
            "color, year and model", Toast.LENGTH_LONG).show();
        return false;
      }

      name = color + " " + year + " " + model;
    }

        /*
        Check existing VehicleData instances for same Color, Year and Model, or with the same name.
        If a similar mVehicle is found, ask the user if this duplicate should be saved.
         */
    for (VehicleData each : VehicleData.getVehicleList()) {
      if (each == mVehicle) continue;
      if (each.isDuplicate(name, color, year, model, vin, licPlate)) {
        if (!userWantsDuplicate())
          return false;
      }
    }

        /*
        If we've gotten here, then the data looks good, and either there are no duplicates or the
         user wants to save it even if it is a duplicate. Store the screen-entered details into
         the vehicle to be saved, and return an affirmative to the calling method.
         */
    mVehicle.setName(name);
    mVehicle.setYear(year);
    mVehicle.setColor(color);
    mVehicle.setModel(model);
    mVehicle.setVIN(vin);
    mVehicle.setLicensePlate(licPlate);

    return true;
  }

  /**
   * Let the user know we've found a duplicate of the data just entered, and ask her if
   * she wants to save it anyway. Return an affirmative or negative to the calling method.
   *
   * @return the user's decision whether to save a duplicate
   */
  private boolean userWantsDuplicate() {
    AlertDialog.Builder b = new AlertDialog.Builder(this);
    b.setMessage("There is already a Vehicle with the same Color, Year and Model, or with the" +
        " same name. Do you want to save this as a duplicate?");
    b.setPositiveButton("Yes, Save Duplicate", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface d, int id) {
        AddEditVehicleActivity.dupeCheckResult = true;
      }
    });
    b.setNegativeButton("No thank you", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface d, int id) {
        AddEditVehicleActivity.dupeCheckResult = false;
      }
    });

    return AddEditVehicleActivity.dupeCheckResult;
  }


  /**
   * Close the activity if the user presses "Cancel"
   *
   * @param v the view that called this method
   */
  public void cancelAddVehicle(View v) {
    setResult(RESULT_CANCELED);
    this.finish();
  }
}
