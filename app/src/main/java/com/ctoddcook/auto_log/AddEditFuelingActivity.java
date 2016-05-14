/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Activity provides the UI and functions needed to ad a new Fueling Event or edit an
 * existing one.
 */
public class AddEditFuelingActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    public static final String KEY_ADD_EDIT_MODE = "com.ctoddcook.auto_log.ADD_EDIT_MODE";
    public static final String KEY_FUELING_ID = "com.ctoddcook.auto_log.FUELING_ID";
    public static final int MODE_ADD = 1;
    public static final int MODE_EDIT = 2;
    public static boolean dupeCheckResult;
    private int mode;
    private FuelingData mFueling;
    private VehicleData mVehicle;
    private Date mDateOfFill;
    private int mYear, mMonth, mDay, mHour, mMinute;

    private static final String TAG = "AddEditFuelingActivity";


    /**
     * When the activity is created, apart from the standard actions, we retrieve the type of
     * user action (add or edit a mFueling) and if the mode is EDIT. If the mode is to add a new
     * fueling, we create a new one and assign it to the mFueling field; if the mode is to edit
     * an existing fueling, we retrieve that fueling and assign it to mFueling.
     * @param savedInstanceState data saved from prior shut down (probably null)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_fueling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupVehicleSpinner();

        mode = getIntent().getIntExtra(KEY_ADD_EDIT_MODE, -1);

        switch (mode) {
            case MODE_ADD:
                mFueling = new FuelingData();
                break;
            case MODE_EDIT:
                int fuelingID = getIntent().getIntExtra(KEY_FUELING_ID, -1);
                if (fuelingID < 0)
                    throw new IllegalArgumentException("In edit mode, a Fueling ID must be " +
                            "provided");

                mFueling = FuelingData.getFueling(fuelingID);
                break;
            default:
                throw new IllegalArgumentException("Calling process must specify add/edit mode");
        }

        // Get the current, local, date and time; set the seconds and milliseconds to 0
        GregorianCalendar gc = new GregorianCalendar();
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
        DatabaseHelper dh = new DatabaseHelper(this);
        Cursor cursor = dh.fetchSimpleVehicleListCursor();

        // if the cursor has no results, open the AddEditVehicleActivity, then try again
        if (cursor.getCount() < 1) {
            Intent intent = new Intent(this, AddEditVehicleActivity.class);
            intent.putExtra(AddEditVehicleActivity.KEY_ADD_EDIT_MODE, AddEditVehicleActivity
                    .MODE_ADD);
            startActivity(intent);
            cursor = dh.fetchSimpleVehicleListCursor();
        }

        // make an adapter from the cursor
        String[] from = new String[]{VehicleDataMap.COLUMN_NAME_NAME};
        int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter sca = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                cursor, from, to, 0);

        // set layout for activated adapter
        sca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // get xml file spinner and set adapter
        Spinner spinner = (Spinner) this.findViewById(R.id.vehicle_spinner);
        if (spinner != null) {
            spinner.setAdapter(sca);

            // set spinner listener to display the selected item ID
            spinner.setOnItemSelectedListener(this);
        }
    }

    //TODO Separate Date and Time for date of fill
    //TODO setup date picker and time picker dialogs


    /**
     * When a user selects a Vehicle from the spinner, update the mVehicle field with the correct
     * vehicle object.
     * @param parent the parent spinner, in effect
     * @param v the View which triggered this method call
     * @param pos the position of the view in the adapter
     * @param id the row ID of the item that is selected
     */
    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
        mVehicle = VehicleData.getVehicle(id);
        Log.i(TAG, "onItemSelected: value of int pos is " + pos);
        Log.i(TAG, "onItemSelected: value of long id is " + id);
        //TODO I MIGHT BE MIS-USING THE ID PARAMGER ...
        // IT MIGHT BE GIVING A POSITION IN THE LIST, AND NOT THE DATABASE RECORD ID
    }


    //TODO Figure out what I'm supposed to do with onNothingSelected()
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
            DatabaseHelper dh = new DatabaseHelper(this);
            switch(mode) {
                case MODE_ADD:
//                    dh.insertFueling(mFueling);
                    break;
                case MODE_EDIT:
//                    dh.updateFueling(mFueling);
                    break;
                default:
                    throw new IllegalArgumentException("Member field mode does not " +
                            "indicate either ADD or EDIT");
            }
        }

        this.finish();
    }

    public boolean extractDetails() {
        //TODO Figure out why mVehicle is null
//        int vehicleID = mVehicle.getID();
        Date date = null;
        float distance, volume, pricePaid, odometer;
        String location = null, gpsCoordinates = null;

        date = mDateOfFill;

        EditText distET = (EditText) findViewById(R.id.edit_distance);
        if (distET != null) distance = Float.parseFloat(distET.getText().toString());

        EditText volET = (EditText) findViewById(R.id.edit_volume);
        if (volET != null) volume = Float.parseFloat(volET.getText().toString());

        EditText paidET = (EditText) findViewById(R.id.edit_total_price_paid);
        if (paidET != null) pricePaid = Float.parseFloat(paidET.getText().toString());

        EditText locET = (EditText) findViewById(R.id.edit_location);
        if (locET != null) location = locET.getText().toString().trim();

        EditText odoET = (EditText) findViewById(R.id.edit_odometer);
        if (odoET != null) odometer = Float.parseFloat(odoET.getText().toString());

        //TODO Get GPS coordinates and put them in gpsCoordinates

        return true;
    }

    /**
     * User has touched "Cancel". Just get out of here.
     * @param v The View which triggered this method call
     */
    public void cancelAddFueling(View v) {
        this.finish();
    }



    /**
     * Let the user know we've found a duplicate of the data just entered, and ask her if
     * she wants to save it anyway. Return an affirmative or negative to the calling method.
     * @return the user's decision whether to save a duplicate
     */
    private boolean userWantsDuplicate() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("There is already a Fueling with the same Vehicle, Date, Distance, Volume " +
                "and Price Paid. Do you want to save this as a duplicate?");
        b.setPositiveButton("Yes, Save Duplicate", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int id) {
                AddEditFuelingActivity.dupeCheckResult = true;
            }
        });
        b.setNegativeButton("No thank you", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int id) {
                AddEditFuelingActivity.dupeCheckResult = false;
            }
        });

        return AddEditFuelingActivity.dupeCheckResult;
    }


    /**
     * Opens a Date Pikcer dialog for the user to change the date of fill
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Used by the Date Picker dialog to pass back the date indicated by the user
     * @param year the year selected by the user
     * @param month the month selected by the user
     * @param day the day selected by the user
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
     * @param v the View which called this method
     */
    public void showTimePickerDialog(View v) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "timePicker");
    }

    /**
     * Used by the Time Picker dialog to pass back the time indicated by the user.
     * @param hour the hour selected by the user
     * @param minute the minute selected by the user
     */
    public void setTime(int hour, int minute) {
        mHour = hour;
        mMinute = minute;

        mDateOfFill = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute).getTime();

        displayDateOfFill();
    }

    /**
     * Present the working DateofFill (not yet stored into a FuelingData object, though it may
     * have come from one if we're in EDIT mode). Splits the information into separate Date and
     * Time components, so Date Picker and Time Picker dialogs may be used to update these fields.
     */
    private void displayDateOfFill() {
        TextView dateTV = (TextView) findViewById(R.id.edit_date);
        TextView timeTV = (TextView) findViewById(R.id.edit_time);

        String date = DateUtils.formatDateTime(this, mDateOfFill.getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
        String time = DateUtils.formatDateTime(this, mDateOfFill.getTime(),
                DateUtils.FORMAT_SHOW_TIME);

        if (dateTV != null) dateTV.setText(date, TextView.BufferType.NORMAL);
        if (timeTV != null) timeTV.setText(time, TextView.BufferType.NORMAL);
    }
}
