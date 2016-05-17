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
   * <code>[longitude].[latitude]</code>. For example, "-122.2,294.8".
   * @param loc the source location object
   * @return the longitude and latitude, or "---" if the source is null
   */
  public static String getLongitudeAndLatitude(Location loc) {
    String longLat;

    if (loc != null) {
      longLat = loc.getLongitude() + "," + loc.getLatitude();
    }
    else
      longLat = "---";

    return longLat;
  }


}
