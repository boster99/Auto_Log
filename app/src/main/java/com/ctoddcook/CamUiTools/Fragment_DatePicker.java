/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.CamUiTools;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * A factory class for generating a DatePicker dialog with the date initialized to a provided
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
public class Fragment_DatePicker extends DialogFragment
                            implements DatePickerDialog.OnDateSetListener {

    private static final String KEY_INITIAL_DATE = "com.ctoddcook.CUITools.DATE_TO_UPDATE";

    /**
     * A factory to create an instance of this class, sets its arguments to the initial date to
     * display, and return the instance to the caller. This must be used rather than the caller
     * simply instantiating an instance on its own.
     * @param date the date for initial display, or 0 to display the current time
     * @return the new instance all setup
     */
    public static Fragment_DatePicker newInstance(long date) {
        Fragment_DatePicker frag = new Fragment_DatePicker();
        Bundle args = new Bundle();
        args.putLong(KEY_INITIAL_DATE, date);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Overridden method to conduct setup work for a brand-new or restored instance of this
     * fragment.
     * @param savedInstanceState provided by the system if the dialog needs to be restored
     * @return a new DatePickerDialog instance
     * @throws IllegalArgumentException if no argument is provided
     */
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) throws IllegalArgumentException {
        super.onCreateDialog(savedInstanceState);

        // Get the initial time to display from the arguments for the fragment
        long initialDate;
        Bundle args = this.getArguments();
        if (args != null)
            initialDate = args.getLong(KEY_INITIAL_DATE);
        else
        throw new IllegalArgumentException("Caller must provide initial date to display via " +
                "the newInstance() method");
        final Calendar c = Calendar.getInstance();

        // if provided argument is not 0, use current date
        if (initialDate != 0) c.setTimeInMillis(initialDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * A calling activity/method must implement this interface so the user's selected date can be
     * passed back to the caller.
     */
    public interface DatePickerCaller {
        void setDate(int year, int month, int day);
    }

    /**
     * When the user touches "OK", this method is called, and passed the user's selected date
     * back to the activity which created this dialog.
     * @param view the view which trigger this call
     * @param year the year
     * @param month the month in that year
     * @param day the day of the month
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        DatePickerCaller caller = (DatePickerCaller) getActivity();
        caller.setDate(year, month, day);
    }
}
