/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctoddcook.CGenTools.PropertiesHelper;

import java.util.ArrayList;

/**
 * ArrayAdapter customization, for displaying a simple list of Vehicles. The NAME of each vehicle
 * is the only data displayed here. When a Vehicle in the list is touched, it will open a new
 * activity displaying the details of that activity. When a vehicle is long-touched, the menu
 * will offer to retire a vehicle, or make it the default vehicle.
 * <p>
 * Created by C. Todd Cook on 5/22/2016.<br>
 * ctodd@ctoddcook.com
 */
public class VehicleListArrayAdapter extends ArrayAdapter<Vehicle> {
  private static final String TAG = "VehicleListArrayAdapter";
  private static final String DEFAULT_SUFFIX = " *";
  private final Context mContext;
  private final ArrayList<Vehicle> mList;
  private final int mDefaultVehicleID;

  /**
   * Required constructor.
   * @param c the context in which this will be displayed
   * @param vList a list of Vehicles
   */
  public VehicleListArrayAdapter(Context c, ArrayList<Vehicle> vList) {
    super(c, R.layout.vehicle_list_row, vList);
    mContext = c;
    mList = vList;

    mDefaultVehicleID = (int) PropertiesHelper.getInstance().getLongValue(Vehicle
        .DEFAULT_VEHICLE_KEY);
  }

  /**
   * Inner class for making memory-usage more efficient and making scrolling smoother. The
   * ViewHolder retains references to TextViews. Also, when a row (which includes a ViewHolder)
   * scrolls off the screen, it is passed back to the getView() method (in the convertView
   * parameter) so it can be re-used. This way, a Fueling which is coming into view can reuse
   * references to TextViews, instead of every time having to look up their details in the XML
   * file and building them from scratch.
   */
  static class ViewHolder {
    public TextView tvName;
    public ImageView ivBadge;

    public void setName(String name) {
      tvName.setText(name);
    }

    public void showBadge(boolean show) {
      ivBadge.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
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
    Vehicle vehicle = mList.get(pos);

        /*
    Reuse views. Only create a rowView from scratch if the call to this method did not give us
    an old (no longer visible) view we could reuse. This makes memory-use more efficient, and
    makes scrolling smoother.
     */
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) mContext
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.vehicle_list_row, parent, false);

      ViewHolder newHolder = new ViewHolder();
      newHolder.tvName = (TextView) rowView.findViewById(R.id.vehilce_name);
      newHolder.ivBadge = (ImageView) rowView.findViewById(R.id.vehicle_badge);
      rowView.setTag(newHolder);
    }

    ViewHolder holder = (ViewHolder) rowView.getTag();

    holder.setName(vehicle.getName());
    holder.showBadge(vehicle.getID() == mDefaultVehicleID);

    return rowView;
  }
}
