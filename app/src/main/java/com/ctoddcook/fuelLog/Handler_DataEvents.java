package com.ctoddcook.FuelLog;

import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.EventListener;

/**
 * An event controller to notify listeners when certain data updates are made. Implemented in a
 * singleton pattern. Interested listeners must:
 * <ul>
 *   <li>Use <code>getInstance()</code> to get a reference to the controller</li>
 *   <li>Implement the <code>Handler_DataEvents.DataUpdateListener</code> interface</li>
 *   <li>Call <code>setOnDataUpdatedListener()</code> before listening</li>
 *   <li>Call <code>removeOnDataUpdateListener()</code> when done listening</li>
 * </ul>
 * <p/>
 * Created by C. Todd Cook on 6/7/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Handler_DataEvents {
  private static Handler_DataEvents sSingleton;
  private ArrayList<DataUpdateListener> listOfListeners;

  /**
   * Class cannot be instantiated directly.
   */
  private Handler_DataEvents() {
    listOfListeners = new ArrayList<>();
  }

  /**
   * Use this method to get a reference to the one instance of this class.
   * @return The single instance of this class.
   */
  public synchronized static Handler_DataEvents getInstance() {
    if (sSingleton == null) {
      sSingleton = new Handler_DataEvents();
    }
    return sSingleton;
  }

  /**
   * Interested listeners must implement this interface.
   */
  public interface DataUpdateListener extends EventListener {
    /**
     * A listener uses this method to receive event notifications
     * @param event Indicates the type of data updated
     * @param data Extra information, if needed
     */
    void onDataUpdated(DataUpdateEvent event, Intent data);
  }

  /**
   * These indicate the type of data updated when an event is posted.
   */
  public enum DataUpdateEvent {
    VEHICLE_LIST_UPDATED,
    FUELING_LIST_UPDATED
  }

  /**
   * Used by interested listener to register for event postings.
   * @param listener The class that wants to listen for events.
   */
  public void setOnDataUpdatedListener(DataUpdateListener listener) {
    if (!listOfListeners.contains(listener))
      listOfListeners.add(listener);
  }

  /**
   * Used by listeners to de-register from event postings.
   * @param listener The class which is no longer interested.
   */
  public void removeOnDataUpdateListener(DataUpdateListener listener) {
    listOfListeners.remove(listener);
  }

  /**
   * Used by a class/method to post an event indicating data has been changed.
   * @param event Indicates of the type of data updated
   * @param data Extra information, if needed
   */
  public void dispatchDataUpdateEvent(DataUpdateEvent event, @Nullable Intent data) {
    for (DataUpdateListener each : listOfListeners) {
      each.onDataUpdated(event, data);
    }
  }
}
