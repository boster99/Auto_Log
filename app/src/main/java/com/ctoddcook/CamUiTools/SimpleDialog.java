package com.ctoddcook.CamUiTools;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;


/**
 * Helper for displaying simple asynchronous messages. There are no callbacks out of this component.
 * <p/>
 * Created by C. Todd Cook on 6/11/2016.<br>
 * ctodd@ctoddcook.com
 */
public class SimpleDialog {

  private static final String DEFAULT_NEUTRAL_LABEL = "Okay";


  /**
   * Builds a one-button AlertDialog with the provided message. There is no callback; when the
   * user touches the one button, the dialog dismisses itself and that's it.
   *
   * @param c           The context within which the dialog will be displayed
   * @param title       The title for the message dialog (use <code>null</code> for no title)
   * @param message     The message to display
   * @param buttonLabel The label for the button (use <code>null</code> to accept the default)
   */
  public static void showSimpleMessage(Context c, @Nullable String title, String message,
                                       @Nullable String buttonLabel) {
    AlertDialog.Builder builder = new AlertDialog.Builder(c);

    if (title != null) builder.setTitle(title);

    builder.setMessage(message);

    builder.setNeutralButton((buttonLabel == null ? DEFAULT_NEUTRAL_LABEL : buttonLabel),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

    builder.show();
  }
}
