/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.fuelLog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * ArrayAdapter customization, for displaying Fueling in a ListView.
 * <p/>
 * Created by C. Todd Cook on 5/20/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Row_FuelingDetails extends ArrayAdapter<Fueling> {
  private static final String TAG = "Row_FuelingDetails";
  private final Context context;
  private final ArrayList<Fueling> fuelingsList;

  /**
   * Required constructor.
   * @param c Context instantiating this adapter
   * @param fList a list of Fuelings to be displayed
   */
  public Row_FuelingDetails(Context c, ArrayList<Fueling> fList) {
    super(c, R.layout.fueling_row_layout, fList);
    context = c;
    fuelingsList = fList;
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
    public TextView tvDate;
    public TextView tvPrice;
    public TextView tvDist;
    public TextView tvVol;
    public TextView tvEff;

    public void setDate(Date date) {
      tvDate.setText(FormatHandler.formatShortDate(date));
    }

    public void setPrice(float price) {
      tvPrice.setText(FormatHandler.formatPrice(price));
    }

    public void setDistance(float dist) {
      tvDist.setText(FormatHandler.formatDistance(dist));
    }

    public void setVolume(float vol) {
      tvVol.setText(FormatHandler.formatVolumeShort(vol));
    }

    public void setEfficiency(float eff) {
      tvEff.setText(FormatHandler.formatEfficiency(eff));
    }
  }

  /**
   * Called by the system to get details to display and present to the user. This is called only
   * for rows that are on-screen, or just off-screen. If there are 200 Fuelings, it may be that
   * only 10 will be on-screen, so this method will be called for those 10 and maybe one or two
   * others just above or below the displayed rows (rather than calling this for all 200 rows).
   * @param pos the position in the list that is to be displayed
   * @param convertView an old, no-longer-on-screen row, or null
   * @param parent the parent view group. (I think this is the ListView all of the rows are
   *               displayed in.
   * @return a row ready for display
   */
  @Override
  public View getView(int pos, View convertView, ViewGroup parent) {

    View row = convertView;
    Fueling fd = fuelingsList.get(pos);

    /*
    Reuse views. Only create a row from scratch if the call to this method did not give us
    an old (no longer visible) view we could reuse. This makes memory-use more efficient, and
    makes scrolling smoother.
     */
    if (row == null) {
      LayoutInflater inflater = (LayoutInflater) context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      row = inflater.inflate(R.layout.fueling_row_layout, parent, false);

      ViewHolder newHolder = new ViewHolder();
      newHolder.tvDate = (TextView) row.findViewById(R.id.fueling_row_date);
      newHolder.tvPrice = (TextView) row.findViewById(R.id.fueling_row_price);
      newHolder.tvDist = (TextView) row.findViewById(R.id.fueling_row_dist);
      newHolder.tvVol = (TextView) row.findViewById(R.id.fueling_row_vol);
      newHolder.tvEff = (TextView) row.findViewById(R.id.fueling_row_efficiency);
      row.setTag(newHolder);
    }

    ViewHolder holder = (ViewHolder) row.getTag();

    holder.setDate(fd.getDateOfFill());
    holder.setPrice(fd.getPricePerUnit());
    holder.setDistance(fd.getDistance());
    holder.setVolume(fd.getVolume());
    holder.setEfficiency(fd.getEfficiency());

    return row;
  }
}
