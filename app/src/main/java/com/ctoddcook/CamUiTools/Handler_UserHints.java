package com.ctoddcook.CamUiTools;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.ctoddcook.CamGenTools.PropertiesHelper;
import com.ctoddcook.FuelLog.R;

/**
 * Provides general convenience tools for the UI
 * <p/>
 * Created by C. Todd Cook on 6/9/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Handler_UserHints {

  /**
   * Displays an instructional hint to the user. Only shown the first time the user sees this
   * screen (or after HINT settings have been reset).
   */
  public static void showHint(final Context context, final String propertyKey, String hintTitle,
                               final String hintMessage) {
    PropertiesHelper ph = PropertiesHelper.getInstance();
    boolean hintAlreadyShown;
    if (hintTitle == null) hintTitle = context.getString(R.string.hint_title);

    hintAlreadyShown = ph.getBooleanValue(propertyKey, false);

    // If the hint has not been shown, build a dialog with a single OK button and show it
    if (!hintAlreadyShown) {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setTitle(hintTitle);
      builder.setMessage(hintMessage);
      builder.setNeutralButton(context.getString(R.string.hint_btn_okay_label),
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });
      builder.show();

      // Note that the hint has now been shown.
      ph.put(propertyKey, true);
    }
  }
}
