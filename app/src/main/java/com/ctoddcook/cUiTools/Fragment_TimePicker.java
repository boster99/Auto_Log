/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.cUiTools;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * A factory class for generating a TimePicker dialog with the time initialized to a provided
 * value. Usage:
 * <ul>
 * <li>The calling activity must implement the <code>Fragment_DatePicker.DatePickerCaller</code> interface.
 * <li>The calling activity must use <code>Fragment_DatePicker.newInstance()</code> to create a dialog
 * instance.
 * </ul>
 *
 * <p>Created by C. Todd Cook on 5/14/2016.<br>
 * ctodd@ctoddcook.com
 */
public class Fragment_TimePicker extends DialogFragment
                            implements TimePickerDialog.OnTimeSetListener {

    private static final String KEY_TIME_TO_UPDATE = "com.ctoddcook.CUITools.TIME_TO_UPDATE";

    /**
     * A factory to create an instance of this class, sets its arguments to the initial time to
     * display, and return the instance to the caller. This must be used rather than the caller
     * simply instantiating an instance on its own.
     * @param time the time for initial display, or 0 to display the current time
     * @return the new instance all setup
     */
    public static Fragment_TimePicker newInstance(long time) {
        Fragment_TimePicker frag = new Fragment_TimePicker();
        Bundle args = new Bundle();
        args.putLong(KEY_TIME_TO_UPDATE, time);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Overridden method to conduct setup work for a brand-new or restored instance of this
     * fragment.
     * @param savedInstanceState provided by the system if the dialog needs to be restored
     * @return a new TimePickerDialog instance
     * @throws IllegalArgumentException if no argument is provided
     */
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) throws IllegalArgumentException {
        // Get the initial time to display from the arguments for the fragment
        Bundle args = this.getArguments();
        long timeInMillis;
        if (args != null)
            timeInMillis = args.getLong(KEY_TIME_TO_UPDATE);
        else
            throw new IllegalArgumentException("Caller must provide initial time to display via " +
                    "the newInstance() method");
        final Calendar c = Calendar.getInstance();

        // if provided argument is not 0, use current time
        if (timeInMillis > 0) c.setTimeInMillis(timeInMillis);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    /**
     * A calling activity/method must implement this interface so the user's selected time can be
     * passed back to the caller.
     */
    public interface TimePickerCaller {
        void setTime(int hourOfDay, int minute);
    }

    /**
     * When the user touches "OK", this method is called, and passed the user's selected time
     * back to the activity which created this dialog.
     * @param view the view which trigger this call
     * @param hourOfDay the hour of the day, in 24-hour format
     * @param minute the minute of the hour
     */
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TimePickerCaller caller = (TimePickerCaller) getActivity();
        caller.setTime(hourOfDay, minute);
    }
}
