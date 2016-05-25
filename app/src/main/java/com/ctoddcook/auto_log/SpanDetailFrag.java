package com.ctoddcook.auto_log;

import android.content.res.Resources;
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
public class SpanDetailFrag extends Fragment {
  private static final String TAG = "SpanDetailFrag";
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
   * @return a new SpanDetailFrag
   */
  public static SpanDetailFrag getInstance(int span, String vehicleName) {
    SpanDetailFrag sdf = new SpanDetailFrag();
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
    tvPeriod.setText(Fueling.getSpanPeriod(mSpan));

    TextView tvDist = (TextView) view.findViewById(R.id.span_detail_distance);
    tvDist.setText(FormatHandler.formatDistance(Fueling.getAvgDistanceOverSpan(mSpan)));

    TextView tvVol = (TextView) view.findViewById(R.id.span_detail_volume);
    tvVol.setText(FormatHandler.formatVolume(Fueling.getAvgVolumeOverSpan(mSpan)));

    TextView tvPrice = (TextView) view.findViewById(R.id.span_detail_total_price_paid);
    tvPrice.setText(FormatHandler.formatPrice(Fueling.getAvgPricePaidOverSpan(mSpan)));

    TextView tvMPG = (TextView) view.findViewById(R.id.span_detail_efficiency);
    tvMPG.setText(FormatHandler.formatEfficiency(Fueling.getAvgEfficiencyOverSpan(mSpan)));

    TextView tvPPG = (TextView) view.findViewById(R.id.span_detail_price_per_unit);
    tvPPG.setText(FormatHandler.formatPrice(Fueling.getAvgPricePerUnitOverSpan(mSpan)));

    TextView tvPPM = (TextView) view.findViewById(R.id.span_detail_price_per_distance);
    tvPPM.setText(FormatHandler.formatPrice(Fueling.getAvgPricePerDistanceOverSpan(mSpan)));

    return view;
  }
}
