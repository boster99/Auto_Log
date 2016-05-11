/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 *
 */
public class AddFuelingActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fueling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupVehicleSpinner() {
        //TODO: If vehicle list is empty, go to Add Vehicle activity (doesn't yet exist)
        // get a cursor providing IDs and NAMEs for each vehicle
        DatabaseHelper dh = new DatabaseHelper(this);
        Cursor cursor = dh.fetchSimpleVehicleListCursor();

        // if the cursor has no results, open the AddVehicleActivity, then try again
        if (cursor.getCount() < 1) {
            Intent intent = new Intent(this, AddVehicleActivity.class);
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
        spinner.setAdapter(sca);

        // set spinner listener to display the selected item ID
        spinner.setOnItemSelectedListener(this);
    }




    public void onItemSelected(AdapterView<?> parent, View v,int pos, long id) {
        Toast.makeText(this, "ID is " + id, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "toString() is " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void saveNewFueling(View v) {
        // TODO: check sanity of data, create an object, and save it to database
    }

    public void cancelAddFueling(View v) {
        this.finish();
    }
}
