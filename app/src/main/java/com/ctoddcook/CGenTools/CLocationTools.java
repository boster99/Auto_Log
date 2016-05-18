/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.CGenTools;

import android.content.ContentResolver;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Provides some utility methods related to location services.
 * <p>Created by C. Todd Cook on 5/17/2016.<br>
 * ctodd@ctoddcook.com
 */
public class CLocationTools {
  private static final String TAG = "CLocationTools";
  private static final int TWO_MINUTES = 1000 * 60 * 2;



  /**
   * Determines whether location services are available. There are multiple modes of enabled
   * settings; we look for anything that doesn't boil down to NOTHING.
   *
   * @param c An activity/context from which this is being called
   * @return true if some level of location services is available
   */
  public static boolean isGpsEnabled(Context c) {
    ContentResolver cr = c.getContentResolver();
    int locationMode;

    try {
      locationMode = Settings.Secure.getInt(cr, Settings.Secure.LOCATION_MODE);
    } catch (Settings.SettingNotFoundException ex) {
      Log.e(TAG, "isGpsEnabled: ", ex);
      Toast.makeText(c, "Unable to determine whether location services are enabled", Toast
          .LENGTH_LONG).show();
      return false;
    }

    return (locationMode != Settings.Secure.LOCATION_MODE_OFF);
  }

  /**
   * Returns the name of the city (optionally with the state included) for a given Location object.
   * @param c the context/activity calling this method
   * @param loc the source Location
   * @param includeState whether to append the state to the city
   * @return the name of the city alone, or city and state separated by a comma
   */
  public static String getCity(Context c, Location loc, boolean includeState) {
    Geocoder gcd = new Geocoder(c);
    List<Address> addresses;

    try {
      addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
    } catch (IOException ex) {
      Toast.makeText(c, "AHH! We got an IOException, darn it!", Toast.LENGTH_LONG).show();
      Log.e(TAG, "getCity: Trying to get city from location", ex);
      return null;
    }

    String city;
    String state;
    String result = "";
    if (addresses.size() > 0) {
      city = addresses.get(0).getLocality();
      result = city;
      if (includeState) {
        state = addresses.get(0).getAdminArea();
        result = result + ", " + state;
      }
    }

    return result;
  }

  /**
   * Returns the longitude and latitude from a given Location object, in the form
   * <code>[latitude].[longitude]</code>. For example, "-122.2,294.8".
   * @param loc the source location object
   * @return the longitude and latitude, or "---" if the source is null
   */
  public static String getLatitudeAndLongitude(Location loc) {
    String longLat;

    if (loc != null) {
      longLat = loc.getLatitude() + "," + loc.getLongitude();
    }
    else
      longLat = "---";

    return longLat;
  }

  /** Determines whether one Location reading is better than the current Location fix.
   * <p>
   * Taken directly from Android API Guides (https://developer.android
   * .com/guide/topics/location/strategies.html)
   * @param location  The new Location that you want to evaluate
   * @param currentBestLocation  The current Location fix, to which you want to compare the new one
   */
  protected boolean isBetterLocation(Location location, Location currentBestLocation) {
    if (currentBestLocation == null) {
      // A new location is always better than no location
      return true;
    }

    // Check whether the new location fix is newer or older
    long timeDelta = location.getTime() - currentBestLocation.getTime();
    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
    boolean isNewer = timeDelta > 0;

    // If it's been more than two minutes since the current location, use the new location
    // because the user has likely moved
    if (isSignificantlyNewer) {
      return true;
      // If the new location is more than two minutes older, it must be worse
    } else if (isSignificantlyOlder) {
      return false;
    }

    // Check whether the new location fix is more or less accurate
    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
    boolean isLessAccurate = accuracyDelta > 0;
    boolean isMoreAccurate = accuracyDelta < 0;
    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

    // Check if the old and new location are from the same provider
    boolean isFromSameProvider = isSameProvider(location.getProvider(),
        currentBestLocation.getProvider());

    // Determine location quality using a combination of timeliness and accuracy
    if (isMoreAccurate) {
      return true;
    } else if (isNewer && !isLessAccurate) {
      return true;
    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
      return true;
    }
    return false;
  }

  /**
   * Checks whether two providers are the same
   * <p>
   * Taken directly from Android API Guides (https://developer.android
   * .com/guide/topics/location/strategies.html)
   * @param provider1 first provider
   * @param provider2 second provider
   * @return true if they are the same, including if they are both <code>null</code>
   */
  private boolean isSameProvider(String provider1, String provider2) {
    if (provider1 == null) {
      return provider2 == null;
    }
    return provider1.equals(provider2);
  }

}
