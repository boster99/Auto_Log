/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddVehicleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    /**
     * When the user touches "Save", extract all of the entered details, do a little
     * sanity checking, create a new Vehicle object and insert it into the database.
     * @param v
     */
    public void saveNewVehicle(View v) {
        VehicleData vd = extractDetails();
        DatabaseHelper dh = new DatabaseHelper(this);
        dh.insertVehicle(vd);
    }


    /**
     * Extracts the details entered by the user and uses them to create a new VehicleData
     * object, which is returned to the calling method. If the details entered violate some
     * important rule, the user is notified, and null is returned.
     * @return a new VehicleData object, or null if the user screwed up
     */
    private VehicleData extractDetails() {
        EditText nameET = (EditText) findViewById(R.id.edit_veh_name);
        EditText yearET = (EditText) findViewById(R.id.edit_veh_year);
        EditText colorET = (EditText) findViewById(R.id.edit_veh_color);
        EditText modelET = (EditText) findViewById(R.id.edit_veh_model);
        EditText vinET = (EditText) findViewById(R.id.edit_veh_vin);
        EditText licPlateET = (EditText) findViewById(R.id.edit_veh_license_plate);

        String name = nameET.getText().toString();
        int year = Integer.parseInt(yearET.getText().toString());
        String color = colorET.getText().toString();
        String model = modelET.getText().toString();
        String vin = vinET.getText().toString();
        String licPlate = licPlateET.getText().toString();

        if (year < 1900 || year > 2100) {
            Toast.makeText(this, "Whoops! You must provide a year between 1900 and 2100", Toast.LENGTH_LONG).show();
            return null;
        }

        if (name.length() < 1) {
            if (color.length() < 1 || model.length() < 1) {
                Toast.makeText(this, "Uh oh! If you don't supply a name, you must supply a color and model", Toast.LENGTH_LONG).show();
                return null;
            }

            name = color + " " + year + " " + model;
        }

        VehicleData vd = new VehicleData();
        vd.setName(name);
        vd.setYear(year);
        vd.setColor(color);
        vd.setModel(model);
        vd.setVIN(vin);
        vd.setLicensePlate(licPlate);

        return vd;
    }


    /**
     * Close the activity if the user presses "Cancel"
     * @param v
     */
    public void cancelAddVehicle(View v) {
        this.finish();
    }
}
