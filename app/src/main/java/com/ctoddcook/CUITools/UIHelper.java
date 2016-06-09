package com.ctoddcook.CUITools;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.ctoddcook.CGenTools.PropertiesHelper;
import com.ctoddcook.auto_log.R;

import java.util.NoSuchElementException;

/**
 * Provides general convenience tools for the UI
 * <p/>
 * Created by C. Todd Cook on 6/9/2016.<br>
 * ctodd@ctoddcook.com
 */
public class UIHelper {

  /**
   * Displays an instructional hint to the user. Only shown the first time the uesr sees this
   * screen (or after HINT settings have been reset).
   */
  private static void showHint(final Context context, final String propertyKey, String hintTitle,
                               final String hintMessage) {
    PropertiesHelper ph = PropertiesHelper.getInstance();
    boolean hintAlreadyShown;
    if (hintTitle == null) hintTitle = context.getString(R.string.hint_title);

    // Has the hint already been shown?
    try {
      hintAlreadyShown = ph.getBooleanValue(propertyKey);
    } catch (NoSuchElementException e) {
      hintAlreadyShown = false;
    }

    // If the hint has not been shown, build a dialog with a single OK button and show it
    if (!hintAlreadyShown) {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setTitle(hintTitle);
      builder.setMessage(hintMessage);
      builder.setNeutralButton(context.getString(R.string.hint_okay_button_label),
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });

      // Note that the hint has now been shown.
      ph.put(propertyKey, true);
    }
  }


}