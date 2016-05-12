/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddEditVehicleActivity extends AppCompatActivity {
    private static final String TAG = "AddEditVehicleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_vehicle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    /**
     * When the user touches "Save", extract all of the entered details, do a little
     * sanity checking, create a new Vehicle object and insert it into the database.
     *
     * @param v the view which triggered this method call
     */
    public void saveNewVehicle(View v) {
        VehicleData vd = extractDetails(null);
        if (vd != null) {
            DatabaseHelper dh = new DatabaseHelper(this);
            dh.insertVehicle(vd);
        }

        this.finish();
    }


    /**
     * Extracts the details entered by the user and uses them to create a new VehicleData
     * object, which is returned to the calling method. If the details entered violate some
     * important rule, the user is notified, and null is returned.
     *
     * @param vd an existing VehicleData instance to update, or null to have a new one created
     * @return a new/updated VehicleData instance, or null if the user screwed up
     */
    private VehicleData extractDetails(VehicleData vd) {
        String name = "";
        String color = "";
        String model = "";
        String vin = "";
        String licPlate = "";
        int year = 0;

        EditText nameET = (EditText) findViewById(R.id.edit_veh_name);
        EditText yearET = (EditText) findViewById(R.id.edit_veh_year);
        EditText colorET = (EditText) findViewById(R.id.edit_veh_color);
        EditText modelET = (EditText) findViewById(R.id.edit_veh_model);
        EditText vinET = (EditText) findViewById(R.id.edit_veh_vin);
        EditText licPlateET = (EditText) findViewById(R.id.edit_veh_license_plate);

        if (nameET != null) name = nameET.getText().toString().trim();
        if (colorET != null) color = colorET.getText().toString().trim();
        if (modelET != null) model = modelET.getText().toString().trim();
        if (vinET != null) vin = vinET.getText().toString().trim();
        if (licPlateET != null) licPlate = licPlateET.getText().toString().trim();

        try {
            if (yearET != null) year = Integer.parseInt(yearET.getText().toString());
        } catch (NumberFormatException e) {
            Log.e(TAG, "extractDetails: vehicle year EditText value is: "
                    + yearET.getText().toString(), e);
        }

        if (year < 1900 || year > 2100) {
            Toast.makeText(this, "Whoops! " + year + " is not a valid model year",
                    Toast.LENGTH_LONG).show();
            return null;
        }

        if (name.length() < 1) {
            if (color.length() < 1 || model.length() < 1) {
                Toast.makeText(this, "Uh oh! If you don't supply a name, you must supply a " +
                        "color and model", Toast.LENGTH_LONG).show();
                return null;
            }

            name = color + " " + year + " " + model;
        }

        if (vd == null)
            vd = new VehicleData();

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
     *
     * @param v the view that called this method
     */
    public void cancelAddVehicle(View v) {
        this.finish();
    }
}
