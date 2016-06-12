/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.cGenTools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.ctoddcook.cUiTools.SimpleDialog;
import com.ctoddcook.fuelLog.R;

/**
 * This class provides a service: getting the current location of the user/phone. The Android
 * location framework is designed around a moving phone, so getting the "current" location can be
 * a bit problematic. We get the last known location; because that may be stale, we also register
 * for changes to location, which might get us better accuracy.
 *
 * <p>However, the goal of this class and its methods are to support <i>current</i> information,
 * so the class is instantiated with a MAX WAIT TIME, provided in milliseconds. The constructor
 * starts a timer which will end after that number of milliseconds, and also registers for
 * changes in location. Whichever event occurs first (timer or location change) triggers the end
 * of this process, and makes a callback to the Activity which instantiated this to provide the
 * Location we have.
 *
 * <p>For example, the calling Activity might indicate a max wait of 1500 millis. After
 * construction, this class will get the last known location and then wait for a change in
 * location. But if no change in location is posted in 1500 milliseconds we stop waiting and
 * provide the last known location to the calling Activity. OR, if a change in location
 * <i>does</i> come before 1500 milliseconds is up, we stop the timer and provide the new location.
 * <p>Created by C. Todd Cook on 5/17/2016.<br>
 * ctodd@ctoddcook.com
 */
public class CLocationWaiter implements LocationListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

  private static final String TAG = "CLocationWaiter";
  private static final int LOCATION_PERMISSION_REQUEST = 323;
  private Activity mActivity;
  private locationCaller mCaller;
  private Location mLocation;
  private LocationManager mLocationManager;
  private Handler mTimerHandler;
  private Runnable mRunnable;
  private int mMaxDelayMillis;


  /**
   * This interface is required of the calling class, so we can post to it the location
   * information we collect.
   */
  public interface locationCaller {
    void setLocation(Location location);
  }

  /**
   * Constructor. Takes two parameters to 1) have a means of communicating with the application
   * context and 2) knowing the maximum time to wait for a location-changed event before giving
   * up and using the information we already have.
   * @param a the Activity which instantiated this object
   * @param maxDelayMillis the maximum time to wait before giving old location information
   */
  public CLocationWaiter(Activity a, int maxDelayMillis) {
    mActivity = a;
    mMaxDelayMillis = maxDelayMillis;

    if (gpsIsAllowed())
      setLocation();
  }

  /**
   * Check to see whether we have system permission to access location services. If we don't,
   * then we'll ask the system whether we should explain to the user why we want location
   * access, and provide that explanation if so. Finally, we request the permission if we don't
   * have it.
   * @return True if we already have permission
   */
  private boolean gpsIsAllowed() {

    // Do we already have permission?
    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED)
      return true;

    // Should we show an explanation?
    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
        Manifest.permission.ACCESS_FINE_LOCATION)) {

      // Provide an explanation. When the user clicks "Okay", we go on and request permission.
      AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
      builder.setTitle(R.string.location_request_explanation_title);
      builder.setMessage(R.string.location_request_explanation_message);

      builder.setNeutralButton(R.string.all_btn_okay,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              requestPermissions();
            }
          });

      builder.show();

    // If we aren't displaying an explanation, we just go forward with requesting permission.
    } else {
      requestPermissions();
    }

    /*
    If we did not already have permission (at the top of this method) we return false, even
    though the user might go on to give us permission. Because the permission request managed by
    Android is asynchronous, we can't just wait for the user's response before moving on.

    Responding FALSE here will lead to the constructor (above) NOT calling setLocation().

    But, if the user does give us permission, there will be an asynchronous call to
    onRequestPermissionsResult(), which will then go on to call setLocation().
     */
    return false;
  }

  /**
   * Make the request for permission to access location services. The answer to the request will
   * be provided through the call-back method onRequestPermissionsResult() (below).
   */
  private void requestPermissions() {
    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission
        .ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
  }

  /**
   * This should be called only after we've established that we have permissions to access
   * location services.
   */
  private void setLocation() {
    mCaller = (locationCaller) mActivity;
    boolean gpsAllowed = false, networkAllowed = false;

    mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);

    /*
    Try to get fine-grained location information updates (gps-based), or, if that's not
    available, try to get course-grained location information updates. When permissions are not
    granted by the user we get a SecurityException thrown in our face.
    */

    try {
      mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
      mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      gpsAllowed = true;
    } catch(SecurityException ex) {
      Log.e(TAG, "CLocationWaiter: ", ex);
      /*
      Do nothing. This means we aren't allowed to get gps-provided location data. We might be
      able to get network-provided location data, so we'll try that next.
       */
    }

    if (!gpsAllowed) {
      try {
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        networkAllowed = true;
      } catch(SecurityException ex) {
        Toast.makeText(mActivity, R.string.service_not_allowed_1, Toast.LENGTH_LONG).show();
        Toast.makeText(mActivity, R.string.service_not_allowed_2, Toast.LENGTH_LONG).show();
      }
    }

    /*
    If we can't get any kind of location data, we send a null Location back to the calling
    Activity and get out of here.
     */
    if (!gpsAllowed && !networkAllowed) {
      mCaller.setLocation(null);
      return;
    }

    startTimer(mMaxDelayMillis);
  }

  /**
   * Callback for the result from requesting permissions. This method
   * is invoked for every call on requestPermissions(Activity, String[], int)}.
   * <p>
   * <strong>Note:</strong> It is possible that the permissions request interaction
   * with the user is interrupted. In this case you will receive empty permissions
   * and results arrays which should be treated as a cancellation.
   * </p>
   * NOTE: As of June 11 2015 this is never being called, as it seems it should be.
   *
   * @param requestCode  The request code passed in requestPermissions(Activity, String[], int)}.
   * @param permissions  The requested permissions. Never null.
   * @param grantResults The grant results for the corresponding permissions
   *                     which is either {@link PackageManager#PERMISSION_GRANTED}
   *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode != LOCATION_PERMISSION_REQUEST)
      return;

    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
      setLocation();
  }

  /**
   * Called when the location has changed.
   * <p/>
   * <p> There are no restrictions on the use of the supplied Location object.
   *
   * @param location The new location, as a Location object.
   */
  @Override
  public void onLocationChanged(Location location) {
    if (CLocationTools.isBetterLocation(location, mLocation))
      mLocation = location;
    wrapUp();
  }

  /**
   * Creates a one-use timer which will, after the indicated number of milliseconds, call the
   * {@code wrapUp()} method and end.
   * @param millis the amount of delay before the timer exits
   */
  private void startTimer(int millis) {
    mTimerHandler = new Handler();
    mTimerHandler.postDelayed(mRunnable, millis);

    mRunnable = new Runnable() {
      @Override
      public void run() {
        wrapUp();
      }
    };
  }

  /**
   * This is called after one of two events occurs:
   * <ol>
   *   <li>We get an updated location from the LocationManager calls
   *   {@code onLocationChanged()}. In this case, we should have received an updated
   *   (and presumably more accurate) lcoation</li>
   *   <li>At the expiration of the timer created in {@code startTimer()}</li>
   * </ol>
   * When one of these events calls this method, we cancel the timer operation, then pass the
   * location back to the calling class.
   */
  private void wrapUp() {
    // Stop the timer
    if (mTimerHandler != null)
      mTimerHandler.removeCallbacks(mRunnable);

    // Stop waiting for a change in location
    try {
      mLocationManager.removeUpdates(this);
    } catch (SecurityException ex) {
      Toast.makeText(mActivity, "Error ending location updates", Toast.LENGTH_LONG).show();
    }

    // Callback to give the location to the calling Activity
    mCaller.setLocation(mLocation);
  }





  /**
   * Required by LocationListener, but not being used here.
   */
  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) { }

  /**
   * Required by LocationListener, but not being used here.
   */
  @Override
  public void onProviderEnabled(String provider) { }

  /**
   * Required by LocationListener, but not being used here.
   */
  @Override
  public void onProviderDisabled(String provider) { }
}
