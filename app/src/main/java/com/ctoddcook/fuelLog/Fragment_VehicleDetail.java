package com.ctoddcook.FuelLog;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * This populates the display with the details from a particular vehicle.
 * <p>
 * Created by C. Todd Cook on 5/24/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Fragment_VehicleDetail extends Fragment {
  private static final String TAG = "Fragment_VehicleDetail";
  private static final String VEHICLE_ID = "VehicleID";
  private Model_Vehicle mVehicle;

  /**
   * Android does not want parameters passed through a constructor; they only want parameters
   * passed through a Bundle, so parameters can be provided again later if the Fragment needs to
   * be reconstituted.
   *
   * @param vehicleID the id of the vehicle to display
   * @return a new Fragment_VehicleDetail
   */
  public static Fragment_VehicleDetail getInstance(int vehicleID) {
    Fragment_VehicleDetail vdf = new Fragment_VehicleDetail();
    Bundle args = new Bundle();
    args.putInt(VEHICLE_ID, vehicleID);
    vdf.setArguments(args);
    return vdf;
  }

  /**
   * This simply gets (from arguments) the ID of the vehicle to be displayed, then retrieves that
   * vehicle.
   *
   * @param savedInstanceState If the fragment is being re-created from
   *                           a previous saved state, this is the state.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    int vehID = (getArguments() != null ? getArguments().getInt(VEHICLE_ID) : -1);
    mVehicle = Model_Vehicle.getVehicle(vehID);
  }

  /**
   * Puts the details from the vehicle in question into the TextViews which are displayed on screen.
   *
   * @param inflater           The LayoutInflater object that can be used to inflate
   *                           any views in the fragment,
   * @param container          If non-null, this is the parent view that the fragment's
   *                           UI should be attached to.  The fragment should not add the view itself,
   *                           but this can be used to generate the LayoutParams of the view.
   * @param savedInstanceState If non-null, this fragment is being re-constructed
   *                           from a previous saved state as given here.
   * @return Return the View for the fragment's UI, or null.
   */
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layoutView = inflater.inflate(R.layout.fragment_vehicle_detail, container, false);
    TextView tvName = (TextView) layoutView.findViewById(R.id.vehicle_detail_name);
    tvName.setText(mVehicle.getName());

    TextView tvColor = (TextView) layoutView.findViewById(R.id.vehicle_detail_color);
    tvColor.setText(mVehicle.getColor());

    TextView tvYear = (TextView) layoutView.findViewById(R.id.vehicle_detail_year);
    tvYear.setText(Integer.toString(mVehicle.getYear()));

    TextView tvModel = (TextView) layoutView.findViewById(R.id.vehicle_detail_model);
    tvModel.setText(mVehicle.getModel());

    TextView tvPlate = (TextView) layoutView.findViewById(R.id.vehicle_detail_license_plate);
    tvPlate.setText(mVehicle.getLicensePlate());

    TextView tvVIN = (TextView) layoutView.findViewById(R.id.vehicle_detail_vin);
    tvVIN.setText(mVehicle.getVIN());

    return layoutView;
  }
}
