package com.ctoddcook.auto_log;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// fixme Have not been able to make this work with Activity_Main.setupVehicleSpinner() and vehicle_name_array_adapter.xml
/**
 * Array adapter for filling out spinners with vehicle lists
 * <p/>
 * Created by C. Todd Cook on 5/26/2016.<br>
 * ctodd@ctoddcook.com
 */
public class VehicleNameArrayAdapter extends ArrayAdapter<Vehicle> {
  private static final String TAG = "VehicleNameArrayAdapter";
  private final Context mContext;
  private final ArrayList<Vehicle> mVehicles;

  public VehicleNameArrayAdapter(Context c, ArrayList<Vehicle> vList){
    super(c,R.layout.vehicle_name_array_adapter,vList);
    mContext = c;
    mVehicles = vList;
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

    public void setName(String name) {
      tvName.setText(name);
    }
  }


  /**
   * {@inheritDoc}
   *
   * @param position
   */
  @Override
  public Vehicle getItem(int position) {
    Log.d(TAG, "getItem: got called");
    return mVehicles.get(position);
  }

  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    Log.d(TAG, "getDropDownView: got called");
    return getView(position, convertView, parent);
  }

  /**
   * {@inheritDoc}
   *
   * @param position
   * @param convertView
   * @param parent
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    View row = convertView;
    Vehicle vehicle = mVehicles.get(position);

    /*
    Reuse views. Only create a rowView from scratch if the call to this method did not give us
    an old (no longer visible) view we could reuse. This makes memory-use more efficient, and
    makes scrolling smoother.
     */
    if (row == null) {
      LayoutInflater inflater = (LayoutInflater) mContext
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      row = inflater.inflate(R.layout.vehicle_name_array_adapter, parent, false);

      ViewHolder newHolder = new ViewHolder();
      newHolder.tvName = (TextView) row.findViewById(R.id.spinner_vehicle_name);
      row.setTag(newHolder);
    }

    ViewHolder holder = (ViewHolder) row.getTag();

    holder.setName(vehicle.getName());

    return row;
  }
}