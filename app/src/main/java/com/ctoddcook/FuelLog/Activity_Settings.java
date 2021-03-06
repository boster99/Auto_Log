package com.ctoddcook.FuelLog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ctoddcook.CamGenTools.PropertiesHelper;

import java.util.NoSuchElementException;

/**
 * Provides a UI for adjust a couple of settings.
 * <p/>
 * Created by C. Todd Cook on 6/9/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Activity_Settings extends AppCompatActivity {
  private static final PropertiesHelper sPropertiesHelper = PropertiesHelper.getInstance();


  /**
   * In the setup of this activity, we set the GPS switch according to the saved state in the
   * database--or, if it hasn't been saved in the database, we set it to true and save it. We
   * also setup a listener that responds to the user changing the switch, and that listener
   * stores the new value in the database.
   * @param savedInstanceState Provided in case the activity is resumed after a pause or stop
   */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    Switch gpsSwitch;

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
    if (toolbar != null) {
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      toolbar.setLogo(R.drawable.ic_settings);
    }

    // Get the current setting for whether gps is allowed, and set the switch accordingly
    gpsSwitch = (Switch) findViewById(R.id.gps_allowed);
    boolean gpsAllowed;

    try {
      gpsAllowed = sPropertiesHelper.getBooleanValue(Activity_EditFueling.KEY_USER_ALLOWS_GPS);
    } catch (NoSuchElementException e) {
      gpsAllowed = true;
      sPropertiesHelper.put(Activity_EditFueling.KEY_USER_ALLOWS_GPS, gpsAllowed);
    }

    gpsSwitch.setChecked(gpsAllowed);
    gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sPropertiesHelper.put(Activity_EditFueling.KEY_USER_ALLOWS_GPS, isChecked);
      }
    });
  }


  /**
   * Resets the properties for each one-time hint so they will be shown again.
   * @param v The view which called this method
   */
  public void resetHints(View v) {
    Handler_FuelLogHints.resetHints();
  }
}
