/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.CamGenTools;

import android.content.ContentResolver;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides some utility methods related to location services.
 * <p>Created by C. Todd Cook on 5/17/2016.<br>
 * ctodd@ctoddcook.com
 */
public class CLocationTools {
  private static final String TAG = "CLocationTools";
  private static final int TWO_MINUTES = 1000 * 60 * 2;
  private static final HashMap<String, String> sStateCodes = new HashMap<>(71);
  public static final int OPTION_INCLUDE_STATE_NAME = 101;
  public static final int OPTION_INCLUDE_STATE_ABBREV = 102;

  static {
    sStateCodes.put("Alabama","AL");
    sStateCodes.put("Alaska","AK");
    sStateCodes.put("Alberta","AB");
    sStateCodes.put("American Samoa","AS");
    sStateCodes.put("Arizona","AZ");
    sStateCodes.put("Arkansas","AR");
    sStateCodes.put("Armed Forces (AE)","AE");
    sStateCodes.put("Armed Forces Americas","AA");
    sStateCodes.put("Armed Forces Pacific","AP");
    sStateCodes.put("British Columbia","BC");
    sStateCodes.put("California","CA");
    sStateCodes.put("Colorado","CO");
    sStateCodes.put("Connecticut","CT");
    sStateCodes.put("Delaware","DE");
    sStateCodes.put("District Of Columbia","DC");
    sStateCodes.put("Florida","FL");
    sStateCodes.put("Georgia","GA");
    sStateCodes.put("Guam","GU");
    sStateCodes.put("Hawaii","HI");
    sStateCodes.put("Idaho","ID");
    sStateCodes.put("Illinois","IL");
    sStateCodes.put("Indiana","IN");
    sStateCodes.put("Iowa","IA");
    sStateCodes.put("Kansas","KS");
    sStateCodes.put("Kentucky","KY");
    sStateCodes.put("Louisiana","LA");
    sStateCodes.put("Maine","ME");
    sStateCodes.put("Manitoba","MB");
    sStateCodes.put("Maryland","MD");
    sStateCodes.put("Massachusetts","MA");
    sStateCodes.put("Michigan","MI");
    sStateCodes.put("Minnesota","MN");
    sStateCodes.put("Mississippi","MS");
    sStateCodes.put("Missouri","MO");
    sStateCodes.put("Montana","MT");
    sStateCodes.put("Nebraska","NE");
    sStateCodes.put("Nevada","NV");
    sStateCodes.put("New Brunswick","NB");
    sStateCodes.put("New Hampshire","NH");
    sStateCodes.put("New Jersey","NJ");
    sStateCodes.put("New Mexico","NM");
    sStateCodes.put("New York","NY");
    sStateCodes.put("Newfoundland","NF");
    sStateCodes.put("North Carolina","NC");
    sStateCodes.put("North Dakota","ND");
    sStateCodes.put("Northwest Territories","NT");
    sStateCodes.put("Nova Scotia","NS");
    sStateCodes.put("Nunavut","NU");
    sStateCodes.put("Ohio","OH");
    sStateCodes.put("Oklahoma","OK");
    sStateCodes.put("Ontario","ON");
    sStateCodes.put("Oregon","OR");
    sStateCodes.put("Pennsylvania","PA");
    sStateCodes.put("Prince Edward Island","PE");
    sStateCodes.put("Puerto Rico","PR");
    sStateCodes.put("Quebec","PQ");
    sStateCodes.put("Rhode Island","RI");
    sStateCodes.put("Saskatchewan","SK");
    sStateCodes.put("South Carolina","SC");
    sStateCodes.put("South Dakota","SD");
    sStateCodes.put("Tennessee","TN");
    sStateCodes.put("Texas","TX");
    sStateCodes.put("Utah","UT");
    sStateCodes.put("Vermont","VT");
    sStateCodes.put("Virgin Islands","VI");
    sStateCodes.put("Virginia","VA");
    sStateCodes.put("Washington","WA");
    sStateCodes.put("West Virginia","WV");
    sStateCodes.put("Wisconsin","WI");
    sStateCodes.put("Wyoming","WY");
    sStateCodes.put("Yukon Territory","YT");
  }


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
   * @param option option to include state name or abbreviation
   * @return the name of the city alone, or city and state separated by a comma
   */
  public static String getCity(Context c, Location loc, int option) {
    Geocoder gcd = new Geocoder(c);
    List<Address> addressList;
    Address address;

    try {
      addressList = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
    } catch (IOException ex) {
      Log.d(TAG, "getCity: Trying to get addressList from latitude and longitude", ex);
      return null;
    }

    String city = null;
    String state = null;
    if (addressList.size() > 0) {
      address = addressList.get(0);
      city = address.getLocality();

      switch (option) {
        case OPTION_INCLUDE_STATE_NAME:
          state = address.getAdminArea();
          break;
        case OPTION_INCLUDE_STATE_ABBREV:
          state = sStateCodes.get(address.getAdminArea());
      }

      if (option > 0)
        if (state != null) city += ", " + state;
      else
        Log.d(TAG, "getCity: Could not get state from address. Address is: " + address.toString());
    }

    return city;
  }

  /**
   * Attempt to retrieve a 2-letter, uppercase state or province abbreviation from an Address
   * object. Note, the pattern match expects 2 uppercase letters (not alphanumerics) bounded at
   * front and back by non-alphanumerics (a space, a comma, etc).
   * <p>
   * Credit to user @OldSchool4664 at stackoverflow.
   * @param address a full address object
   * @return null if no code found, or a 2-letter abbreviation
   */
  static private String parseStateCodeFromFullAddress(Address address) {
    if ((address == null) || address.getMaxAddressLineIndex() < 0)
      return null;

    String fullAddress = "";
    for(int j = 0; j <= address.getMaxAddressLineIndex(); j++) {
      if (address.getAddressLine(j) != null)
        fullAddress += " " + address.getAddressLine(j);
    }

    Log.d(TAG, "Full address: " + fullAddress);

    /*
    See https://developer.android.com/reference/java/util/regex/Pattern.html for detailed
    discussion of patterns.

    The phrase ?<! means IT'S NOT A MATCH IF IMMEDIATELY PRECEDING THERE IS A ... in this case
    any character in the ranges A-Z, a-z, and 0-9. So if the text 9NE is found, the '9' prevents
    the "NE" from being recognized as a match, and if ONE is found, the O keeps NE from being a
    match. The ?< is called "look behind", which means immediately before, and the ! means
    "negative", meaning it's not a match if you find this. Complete phrase: (?<![A-Za-z0-9])

    The phrase ([A-Z]{2}) means LOOK FOR AT LEAST 2 CHARACTERS IN A ROW THAT ARE IN THE RANGE A-Z
     ... which means no numbers, no lowercase, no spaces, no punctuation, etc. "NE" is a match, but
     "Ne" is not. "NEB" would be a positive match, because it's at least 2 in a row.

    Then the phrase ?! means IT'S NOT A MATCH IF IMMEDIATELY FOLLOWING THERE IS A ... again any
    character in the ranges A-Z, a-z, and 0-9. So if the text NE3 is found, the 3 keeps the NE
    from being a match. And if NEB is found, the B keeps the NE from being a match. '?' (without
    the '<') is called "look ahead", which means immediately following a possible match, and
    again '!' means negative. Complete phrase: (?![A-Za-z0-9])

    These 3 phrases together mean this: Find exactly 2 uppercase alpha characters in a row (no
    numbers), surrounded on both sides by spaces, punctuation, or other non-alphanumeric characters.
     */
    Pattern pattern = Pattern.compile("(?<![A-Za-z0-9])([A-Z]{2})(?![A-Za-z0-9])");
    Matcher matcher = pattern.matcher(fullAddress);


    /*
    A little tricky here. We actually look for the LAST occurrence of the pattern. Sometimes
    there are 2-character modifiers on street names, as in "2513 SW Bender Ave", and we don't want
    those. So each time we find a match, it replaces any we found before it. That's why we do
    "while (matcher.find())" rather than "if (matcher.find())".
     */
    String stateCode = null;
    while (matcher.find()) {
      stateCode = matcher.group().trim();
    }

    Log.d(TAG, "Parsed state code: " + stateCode);

    return stateCode;
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
  protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
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
  private static boolean isSameProvider(String provider1, String provider2) {
    if (provider1 == null) {
      return provider2 == null;
    }
    return provider1.equals(provider2);
  }
}
