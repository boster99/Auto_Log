package com.ctoddcook.auto_log;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * This activity is reusable, and will show one of three fragments to display vehicle details,
 * fueling details, or averages-over-span details.
 * <p/>
 * Created by C. Todd Cook on 5/24/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Activity_ViewDetail extends AppCompatActivity {
  private static final String TAG = "Activity_ViewDetail";
  public static final String ARG_TYPE = "ArgType";
  public static final int TYPE_VEHICLE = 101;
  public static final int TYPE_FUELING = 102;
  public static final int TYPE_AVERAGE = 103;
  public static final String ARG_POSITION = "Position";
  public static final String ARG_ITEM_ID = "ItemID";
  public static final String ARG_VEHICLE = "Vehicle";

  private static ViewPager mPager;
  private Fragment mCurrentFragment;
  private int mArgType;
  private int mMode;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.frag_view_detail);
    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));// ACTION BAR

    mPager = (ViewPager) findViewById(R.id.Details_Pager);
    int mode = getIntent().getIntExtra(ARG_TYPE, -1);

    if (mode != TYPE_AVERAGE && mode != TYPE_FUELING && mode != TYPE_VEHICLE)
      throw new IllegalArgumentException("Invalid argument value supplied for ARG_TYPE");

    int position = getIntent().getIntExtra(ARG_POSITION, -1);

    showFragment(mode, position);
  }

  public void showFragment(int mode, int position) {
    mMode = mode;

    switch (mode) {
      case TYPE_VEHICLE:
        VehiclePagerAdapter vehiclePagerAdapter = new VehiclePagerAdapter(getSupportFragmentManager(),
            Vehicle.getVehicleList());
        mPager.setAdapter(vehiclePagerAdapter);
        mPager.setCurrentItem(position);
        break;

      case TYPE_FUELING:
        FuelingPagerAdapter fuelingPagerAdapter = new FuelingPagerAdapter(getSupportFragmentManager(),
            Fueling.getFuelingList());
        mPager.setAdapter(fuelingPagerAdapter);
        mPager.setCurrentItem(position);
        break;

      case TYPE_AVERAGE:
        String vehicleName = getIntent().getStringExtra(ARG_VEHICLE);
        SpanPagerAdapter spanPagerAdapter = new SpanPagerAdapter(getSupportFragmentManager(),
            vehicleName);
        mPager.setAdapter(spanPagerAdapter);
        mPager.setCurrentItem(position);
        break;
    }
  }

  // fixme Cannot get buttons to work
  private void toggleButtonsForMode() {
    Button btn;

    switch (mMode) {
      case TYPE_VEHICLE:
      case TYPE_FUELING:
        btn = (Button) findViewById(R.id.delete_item);
        if (btn != null) btn.setVisibility(Button.VISIBLE);
        btn = (Button) findViewById(R.id.edit_item);
        if (btn != null) btn.setVisibility(Button.VISIBLE);
        break;

      case TYPE_AVERAGE:
        btn = (Button) findViewById(R.id.delete_item);
        if (btn != null) btn.setVisibility(Button.INVISIBLE);
        btn = (Button) findViewById(R.id.edit_item);
        if (btn != null) btn.setVisibility(Button.INVISIBLE);
        break;
    }
  }

  public void deleteDetail(View v) {
    switch (mMode) {
      case TYPE_VEHICLE:
        Toast.makeText(this, "Delete VEHICLE", Toast.LENGTH_LONG).show();
        break;

      case TYPE_FUELING:
        Toast.makeText(this, "Delete FUELING", Toast.LENGTH_LONG).show();
        break;

      case TYPE_AVERAGE:
        Toast.makeText(this, "Delete ... uh oh", Toast.LENGTH_LONG).show();
        break;
    }
  }

  public void editDetail(View v) {
    switch (mMode) {
      case TYPE_VEHICLE:
        Toast.makeText(this, "Edit VEHICLE", Toast.LENGTH_LONG).show();
        break;

      case TYPE_FUELING:
        Toast.makeText(this, "Edit FUELING", Toast.LENGTH_LONG).show();
        break;

      case TYPE_AVERAGE:
        Toast.makeText(this, "Edit ... uh oh", Toast.LENGTH_LONG).show();
        break;
    }
  }
}
