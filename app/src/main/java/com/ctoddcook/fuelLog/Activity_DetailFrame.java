package com.ctoddcook.FuelLog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ctoddcook.CamUiTools.Handler_Hints;

/**
 * This activity is reusable, and will show one of three fragments to display vehicle details,
 * fueling details, or averages-over-span details.
 * <p/>
 * Created by C. Todd Cook on 5/24/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Activity_DetailFrame extends AppCompatActivity {
  public static final String ARG_TYPE = "ArgType";
  public static final int TYPE_VEHICLE = 101;
  public static final int TYPE_FUELING = 102;
  public static final int TYPE_AVERAGE = 103;
  public static final String ARG_POSITION = "Position";
  public static final String ARG_VEHICLE = "Model_Vehicle";

  private static ViewPager mPager;
  private Toolbar mToolbar;


  /**
   * Setup work when the activity is created. Gathers incoming details about the record type and
   * list position to be displayed, and passes them to the method which handles the display.
   * @param savedInstanceState Information about the state of the activity if it was stopped
   */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_detail_frame);
    mToolbar = (Toolbar) findViewById(R.id.toolbar_detail_frame);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mPager = (ViewPager) findViewById(R.id.Details_Pager);
    int type = getIntent().getIntExtra(ARG_TYPE, -1);

    if (type != TYPE_AVERAGE && type != TYPE_FUELING && type != TYPE_VEHICLE)
      throw new IllegalArgumentException("Invalid argument value supplied for ARG_TYPE: " + type);

    int position = getIntent().getIntExtra(ARG_POSITION, -1);

    showFragment(type, position);
  }

  /**
   * Displays the appropriate fragment for viewing details of a record.
   * @param type The type of record to be displayed
   * @param position The position in the list of records to display first
   */
  public void showFragment(int type, int position) {
    switch (type) {
      case TYPE_VEHICLE:
        Pager_Vehicle pagerVehicle = new Pager_Vehicle(getSupportFragmentManager(),
            Model_Vehicle.getVehicleList());
        mPager.setAdapter(pagerVehicle);
        mPager.setCurrentItem(position);
        mToolbar.setLogo(R.drawable.ic_car);
        setTitle(R.string.vehicle_details_title);
        showVehicleSwipeHint();
        break;

      case TYPE_FUELING:
        Pager_Fueling pagerFueling = new Pager_Fueling(getSupportFragmentManager(),
            Model_Fueling.getFuelingList());
        mPager.setAdapter(pagerFueling);
        mPager.setCurrentItem(position);
        mToolbar.setLogo(R.drawable.ic_gas_pump);
        setTitle(R.string.fueling_details_title);
        showFuelingSwipeHint();
        break;

      case TYPE_AVERAGE:
        String vehicleName = getIntent().getStringExtra(ARG_VEHICLE);
        Pager_Averages pagerAverages = new Pager_Averages(getSupportFragmentManager(),
            vehicleName);
        mPager.setAdapter(pagerAverages);
        mPager.setCurrentItem(position);
        mToolbar.setLogo(R.drawable.ic_bar_chart);
        setTitle(R.string.averages_detail_title);
        showAveragesSwipeHint();
        break;
    }
  }

  /**
   * Displays a one-time hint for how to swipe from record to record.
   */
  private void showVehicleSwipeHint() {
    Handler_Hints.showHint(this, Handler_FuelLogHints.VEHICLE_DETAIL_HINT_KEY,
        getString(R.string.pager_vehicle_hint_title),
        getString(R.string.pager_vehicle_hint));
  }

  /**
   * Displays a one-time hint for how to swipe from record to record.
   */
  private void showFuelingSwipeHint() {
    Handler_Hints.showHint(this, Handler_FuelLogHints.FUELING_DETAIL_HINT_KEY,
        getString(R.string.pager_fueling_hint_title),
        getString(R.string.pager_fueling_hint));
  }

  /**
   * Displays a one-time hint for how to swipe from record to record.
   */
  private void showAveragesSwipeHint() {
    Handler_Hints.showHint(this, Handler_FuelLogHints.AVERAGES_DETAIL_HINT_KEY,
        getString(R.string.pager_averages_hint_title),
        getString(R.string.pager_averages_hint));
  }


  /**
   * Show a map (Google Maps) of the location indicated by GPS coordinates.
   * @param v the TextView which was touched to generate a call to this method
   */
  public void showMap(View v) {
    TextView tvGPS = (TextView) v;
    String uri;

    if (tvGPS.toString() == null || tvGPS.toString().isEmpty()) return;

    uri = tvGPS.getText().toString().trim();
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    startActivity(intent);
  }
}
