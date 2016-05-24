package com.ctoddcook.auto_log;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


/**
 * Populates the display for a list of vehicles
 * <p>
 * Created by C. Todd Cook on 5/24/2016.<br>
 * ctodd@ctoddcook.com
 */
public class VehicleListFrag extends ListFragment {
  private static final String TAG = "VehicleListFrag";
  private static final String POSITION = "Position";

  /**
   * Android does not want parameters passed through a constructor; they only want parameters
   * passed through a Bundle, so parameters can be provided again later if the Fragment needs to
   * be reconstituted.
   *
   * // @param position the id of the vehicle to display
   * @return a new VehicleDetailFrag
   */
  public static VehicleListFrag getInstance() {
    VehicleListFrag vhl = new VehicleListFrag();

//    Bundle args = new Bundle();
//    args.putInt(POSITION, position);
//    vhl.setArguments(args);

    return vhl;
  }

  /**
   * Called to do initial creation of a fragment.  This is called after
   * {onAttach(Activity)} and before
   * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
   * <p>
   * <p>Note that this can be called while the fragment's activity is
   * still in the process of being created.  As such, you can not rely
   * on things like the activity's content view hierarchy being initialized
   * at this point.  If you want to do work once the activity itself is
   * created, see {@link #onActivityCreated(Bundle)}.
   *
   * @param savedInstanceState If the fragment is being re-created from
   *                           a previous saved state, this is the state.
   */
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    mPosition = (getArguments() != null ? getArguments().getInt(POSITION) : 1);
  }

  /**
   * Provide default implementation to return a simple list view.  Subclasses
   * can override to replace with their own layout.  If doing so, the
   * returned view hierarchy <em>must</em> have a ListView whose id
   * is {@link android.R.id#list android.R.id.list} and can optionally
   * have a sibling view id {@link android.R.id#empty android.R.id.empty}
   * that is to be shown when the list is empty.
   * <p>
   * <p>If you are overriding this method with your own custom content,
   * consider including the standard layout {@link android.R.layout#list_content}
   * in your layout file, so that you continue to retain all of the standard
   * behavior of ListFragment.  In particular, this is currently the only
   * way to have the built-in indeterminant progress state be shown.
   *
   * @param inflater
   * @param container
   * @param savedInstanceState
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.vehicle_list_view, container, false);
  }

  /**
   * Called when the fragment's activity has been created and this
   * fragment's view hierarchy instantiated.  It can be used to do final
   * initialization once these pieces are in place, such as retrieving
   * views or restoring state.  It is also useful for fragments that use
   * {@link #setRetainInstance(boolean)} to retain their instance,
   * as this callback tells the fragment when it is fully associated with
   * the new activity instance.  This is called after {@link #onCreateView}
   * and before {@link #onViewStateRestored(Bundle)}.
   *
   * @param savedInstanceState If the fragment is being re-created from
   *                           a previous saved state, this is the state.
   */
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setListAdapter(new VehicleListArrayAdapter(getActivity(), Vehicle.getVehicleList()));
  }

  /**
   * This method will be called when an item in the list is selected.
   * Subclasses should override. Subclasses can call
   * getListView().getItemAtPosition(position) if they need to access the
   * data associated with the selected item.
   *
   * @param l        The ListView where the click happened
   * @param v        The view that was clicked within the ListView
   * @param position The position of the view in the list
   * @param id       The row id of the item that was clicked
   */
  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Toast.makeText(getActivity(), "Position is " + position, Toast.LENGTH_LONG).show();
  }
}
