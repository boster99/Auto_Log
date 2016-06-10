package com.ctoddcook.fuelLog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// fixme Have not been able to make this work with Activity_Main.setupVehicleSpinner() and row_vehicle_name_simple.xml*
 * Array adapter for filling out spinners with vehicle lists
 * <p/>
 * Created by C. Todd Cook on 5/26/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Row_VehicleNameSimple extends ArrayAdapter<Model_Vehicle> {
  private static final String TAG = "Row_VehicleNameSimple";
  private final Context mContext;
  private final ArrayList<Model_Vehicle> mVehicles;

  public Row_VehicleNameSimple(Context c, ArrayList<Model_Vehicle> vList){
    super(c,R.layout.row_vehicle_name_simple,vList);
    mContext = c;
    mVehicles = vList;
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
  public Model_Vehicle getItem(int position) {
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
    Model_Vehicle vehicle = mVehicles.get(position);

    /*
    Reuse views. Only create a rowView from scratch if the call to this method did not give us
    an old (no longer visible) view we could reuse. This makes memory-use more efficient, and
    makes scrolling smoother.
     */
    if (row == null) {
      LayoutInflater inflater = (LayoutInflater) mContext
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      row = inflater.inflate(R.layout.row_vehicle_name_simple, parent, false);

      ViewHolder newHolder = new ViewHolder();
      newHolder.tvName = (TextView) row.findViewById(R.id.spinner_vehicle_name);
      row.setTag(newHolder);
    }

    ViewHolder holder = (ViewHolder) row.getTag();

    holder.setName(vehicle.getName());

    return row;
  }
}