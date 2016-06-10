package com.ctoddcook.fuelLog;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



/**
 * This populates the display with the details from a particular fueling.
 * <p>
 * Created by C. Todd Cook on 5/24/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Fragment_FuelingDetail extends Fragment {
  private static final String TAG = "Fragment_FuelingDetail";
  private static final String FUELING_ID = "FuelingID";
  private Model_Fueling mFueling;

  /**
   * Android does not want parameters passed through a constructor; they only want parameters
   * passed through a Bundle, so parameters can be provided again later if the Fragment needs to
   * be reconstituted.
   *
   * @param fuelingID the id of the fueling to display
   * @return a new Fragment_FuelingDetail
   */
  public static Fragment_FuelingDetail getInstance(int fuelingID) {
    Fragment_FuelingDetail fdf = new Fragment_FuelingDetail();
    Bundle args = new Bundle();
    args.putInt(FUELING_ID, fuelingID);
    fdf.setArguments(args);
    return fdf;
  }

  /**
   * This simply gets (from arguments) the ID of the fueling to be displayed, then retrieves that
   * fueling.
   *
   * @param savedInstanceState If the fragment is being re-created from
   *                           a previous saved state, this is the state.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    int fuelingID = (getArguments() != null ? getArguments().getInt(FUELING_ID) : -1);
    mFueling = Model_Fueling.getFueling(fuelingID);
  }

  /**
   * Puts the details from the fueling in question into the TextViews which are displayed on screen.
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
    View layoutView = inflater.inflate(R.layout.fueling_details_layout, container, false);

    TextView tvVehicle = (TextView) layoutView.findViewById(R.id.fueling_detail_vehicle);
    tvVehicle.setText(Vehicle.getVehicle(mFueling.getVehicleID()).getName());

    TextView tvDate = (TextView) layoutView.findViewById(R.id.fueling_detail_date);
    tvDate.setText(FormatHandler.formatLongDateShortTime(mFueling.getDateOfFill()));

    TextView tvDist = (TextView) layoutView.findViewById(R.id.fueling_detail_distance);
    tvDist.setText(FormatHandler.formatDistance(mFueling.getDistance()));

    TextView tvVol = (TextView) layoutView.findViewById(R.id.fueling_detail_volume);
    tvVol.setText(FormatHandler.formatVolumeLong(mFueling.getVolume()));

    TextView tvPrice = (TextView) layoutView.findViewById(R.id.fueling_detail_total_price_paid);
    tvPrice.setText(FormatHandler.formatPrice(mFueling.getPricePaid()));

    TextView tvLocation = (TextView) layoutView.findViewById(R.id.fueling_detail_location);
    tvLocation.setText(mFueling.getLocation());

    TextView tvOdometer = (TextView) layoutView.findViewById(R.id.fueling_detail_odometer);
    tvOdometer.setText(Float.toString(mFueling.getOdometer()));

    TextView tvMPG = (TextView) layoutView.findViewById(R.id.fueling_detail_efficiency);
    tvMPG.setText(FormatHandler.formatEfficiency(mFueling.getEfficiency()));

    TextView tvPPG = (TextView) layoutView.findViewById(R.id.fueling_detail_price_per_unit);
    tvPPG.setText(FormatHandler.formatPriceLong(mFueling.getPricePerUnit()));

    TextView tvPPM = (TextView) layoutView.findViewById(R.id.fueling_detail_price_per_distance);
    tvPPM.setText(FormatHandler.formatPriceLong(mFueling.getPricePerDistance()));

    TextView tvGPS = (TextView) layoutView.findViewById(R.id.fueling_detail_coordinates);
    tvGPS.setText(mFueling.getGeoURI());


    return layoutView;
  }
}
