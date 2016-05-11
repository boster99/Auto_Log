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


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DatabaseHelper dh = new DatabaseHelper(this);
//        dh.getSomething();
    }

    public void addFueling(View v) {
        Intent intent = new Intent(this, AddFuelingActivity.class);
        startActivity(intent);
    }

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
