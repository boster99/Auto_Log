/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.FuelLog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctoddcook.CamGenTools.PropertiesHelper;

import java.util.ArrayList;

/**
 * ArrayAdapter customization, for displaying a simple list of Vehicles. The NAME of each vehicle
 * is the only data displayed here. When a Model_Vehicle in the list is touched, it will open a new
 * activity displaying the details of that activity. When a vehicle is long-touched, the menu
 * will offer to retire a vehicle, or make it the default vehicle.
 * <p>
 * Created by C. Todd Cook on 5/22/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Row_VehicleName extends ArrayAdapter<Model_Vehicle> {
  private static final String TAG = "Row_VehicleName";
  private final Context mContext;
  private final ArrayList<Model_Vehicle> mList;
  private final int mDefaultVehicleID;
  private static final int IS_NEITHER = 0;
  private static final int IS_RETIRED = 1;
  private static final int IS_DEFAULT = 2;

  /**
   * Required constructor.
   * @param c the context in which this will be displayed
   * @param vList a list of Vehicles
   */
  public Row_VehicleName(Context c, ArrayList<Model_Vehicle> vList) {
    super(c, R.layout.row_vehicle_name, vList);
    mContext = c;
    mList = vList;

    mDefaultVehicleID = (int) PropertiesHelper.getInstance().getLongValue(Model_Vehicle
        .DEFAULT_VEHICLE_KEY);
  }

  /**
   * Inner class for making memory-usage more efficient and making scrolling smoother. The
   * ViewHolder retains references to TextViews. Also, when a row (which includes a ViewHolder)
   * scrolls off the screen, it is passed back to the getView() method (in the convertView
   * parameter) so it can be re-used. This way, a Model_Fueling which is coming into view can reuse
   * references to TextViews, instead of every time having to look up their details in the XML
   * file and building them from scratch.
   */
  static class ViewHolder {
    public TextView tvName;
    public ImageView ivInfoIcon;

    public void setName(String name) {
      tvName.setText(name);
    }

    /*
    An icon will be displayed if the vehicle is the default, or if it is retired.
    */
    public void showInfoIcon(int version) {
      switch (version) {
        case IS_NEITHER:
          ivInfoIcon.setVisibility(View.INVISIBLE);
          break;
        case IS_RETIRED:
          ivInfoIcon.setVisibility(View.VISIBLE);
          ivInfoIcon.setImageResource(R.drawable.ic_retirement);
          break;
        case IS_DEFAULT:
          ivInfoIcon.setVisibility(View.VISIBLE);
          ivInfoIcon.setImageResource(R.drawable.ic_blue_badge);
          break;
        default:
          throw new IllegalArgumentException("Illegal value passed to showInfoIcon(): " + version);
      }
    }
  }

  /**
   * Called by the system to get details to display and present to the user. This is called only
   * for rows that are on-screen, or just off-screen. If there are 200 Vehicles, it may be that
   * only 10 will be on-screen, so this method will be called for those 10 and maybe one or two
   * others just above or below the displayed rows (rather than calling this for all 200 rows).
   * @param pos the position in the list that is to be displayed
   * @param convertView an old, no-longer-on-screen row which can be reused, or null
   * @param parent the parent view group. (I think this is the ListView all of the rows are
   *               displayed in.
   * @return a row ready for display
   */
  @Override
  public View getView(int pos, View convertView, ViewGroup parent) {
    View rowView = convertView;
    Model_Vehicle vehicle = mList.get(pos);

    /*
    Reuse views. Only create a rowView from scratch if the call to this method did not give us
    an old (no longer visible) view we could reuse. This makes memory-use more efficient, and
    makes scrolling smoother.
     */
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) mContext
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.row_vehicle_name, parent, false);

      ViewHolder newHolder = new ViewHolder();
      newHolder.tvName = (TextView) rowView.findViewById(R.id.vehilce_name);
      newHolder.ivInfoIcon = (ImageView) rowView.findViewById(R.id.vehicle_info_icon);
      rowView.setTag(newHolder);
    }

    ViewHolder holder = (ViewHolder) rowView.getTag();

    holder.setName(vehicle.getName());

    // Show the right information icon, if any
    if (vehicle.isRetired())
      holder.showInfoIcon(IS_RETIRED);
    else if (vehicle.getID() == mDefaultVehicleID)
      holder.showInfoIcon(IS_DEFAULT);
    else
      holder.showInfoIcon(IS_NEITHER);

    return rowView;
  }
}
