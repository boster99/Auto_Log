package com.ctoddcook.auto_log;

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
public class VehicleDetailFrag extends Fragment {
  private static final String TAG = "VehicleDetailFrag";
  private static final String VEH_ID = "vehicleID";
  private Vehicle mVehicle;

  /**
   * Android does not want parameters passed through a constructor; they only want parameters
   * passed through a Bundle, so parameters can be provided again later if the Fragment needs to
   * be reconstituted.
   *
   * @param vehID the id of the vehicle to display
   * @return a new VehicleDetailFrag
   */
  public static VehicleDetailFrag getInstance(int vehID) {
    VehicleDetailFrag vdf = new VehicleDetailFrag();
    Bundle args = new Bundle();
    args.putInt(VEH_ID, vehID);
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
    int vehID = (getArguments() != null ? getArguments().getInt(VEH_ID) : 1);
    mVehicle = Vehicle.getVehicle(vehID);
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
    View layoutView = inflater.inflate(R.layout.vehicle_details_layout, container, false);
    TextView tvName = (TextView) layoutView.findViewById(R.id.vehicle_detail_name);
    tvName.setText(mVehicle.getName());

    TextView tvColor = (TextView) layoutView.findViewById(R.id.vehicle_detail_color);
    tvColor.setText(mVehicle.getColor());

    TextView tvYear = (TextView) layoutView.findViewById(R.id.vehicle_detail_year);
    tvYear.setText(mVehicle.getYear());

    TextView tvModel = (TextView) layoutView.findViewById(R.id.vehicle_detail_model);
    tvModel.setText(mVehicle.getModel());

    TextView tvPlate = (TextView) layoutView.findViewById(R.id.vehicle_detail_license_plate);
    tvPlate.setText(mVehicle.getLicensePlate());

    TextView tvVIN = (TextView) layoutView.findViewById(R.id.vehicle_detail_vin);
    tvVIN.setText(mVehicle.getVIN());

    return layoutView;
  }
}
