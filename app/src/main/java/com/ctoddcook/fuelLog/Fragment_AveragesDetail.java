package com.ctoddcook.fuelLog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * This populates the display with the details from a particular time span.
 * <p>
 * Created by C. Todd Cook on 5/24/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Fragment_AveragesDetail extends Fragment {
  private static final String TAG = "Fragment_AveragesDetail";
  private static final String SPAN = "Span";
  private static final String VEHICLE_NAME = "VehicleName";
  private int mSpan;
  private String mVehicleName;

  /**
   * Android does not want parameters passed through a constructor; they only want parameters
   * passed through a Bundle, so parameters can be provided again later if the Fragment needs to
   * be reconstituted.
   *
   * @param span indicates which span's averages are to be displayed
   * @return a new Fragment_AveragesDetail
   */
  public static Fragment_AveragesDetail getInstance(int span, String vehicleName) {
    Fragment_AveragesDetail sdf = new Fragment_AveragesDetail();
    Bundle args = new Bundle();
    args.putInt(SPAN, span);
    args.putString(VEHICLE_NAME, vehicleName);
    sdf.setArguments(args);
    return sdf;
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
    mSpan = (getArguments() != null ? getArguments().getInt(SPAN) : -1);
    mVehicleName = (getArguments() != null ? getArguments().getString(VEHICLE_NAME) : "");
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
    View view = inflater.inflate(R.layout.span_details_layout, container, false);

    TextView tvVehicle = (TextView) view.findViewById(R.id.span_detail_vehicle);
    tvVehicle.setText(mVehicleName);

    TextView tvPeriod = (TextView) view.findViewById(R.id.span_detail_period);
    tvPeriod.setText(Model_Fueling.getSpanPeriod(mSpan));

    TextView tvDist = (TextView) view.findViewById(R.id.span_detail_distance);
    tvDist.setText(Handler_Format.formatDistance(Model_Fueling.getAvgDistanceOverSpan(mSpan)));

    TextView tvVol = (TextView) view.findViewById(R.id.span_detail_volume);
    tvVol.setText(Handler_Format.formatVolumeShort(Model_Fueling.getAvgVolumeOverSpan(mSpan)));

    TextView tvPrice = (TextView) view.findViewById(R.id.span_detail_total_price_paid);
    tvPrice.setText(Handler_Format.formatPrice(Model_Fueling.getAvgPricePaidOverSpan(mSpan)));

    TextView tvMPG = (TextView) view.findViewById(R.id.span_detail_efficiency);
    tvMPG.setText(Handler_Format.formatEfficiency(Model_Fueling.getAvgEfficiencyOverSpan(mSpan)));

    TextView tvPPG = (TextView) view.findViewById(R.id.span_detail_price_per_unit);
    tvPPG.setText(Handler_Format.formatPrice(Model_Fueling.getAvgPricePerUnitOverSpan(mSpan)));

    TextView tvPPM = (TextView) view.findViewById(R.id.span_detail_price_per_distance);
    tvPPM.setText(Handler_Format.formatPrice(Model_Fueling.getAvgPricePerDistanceOverSpan(mSpan)));

    return view;
  }
}
