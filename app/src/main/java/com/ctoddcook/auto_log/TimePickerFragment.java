/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by C. Todd Cook on 5/14/2016.
 * ctodd@ctoddcook.com
 */
public class TimePickerFragment extends DialogFragment
                            implements TimePickerDialog.OnTimeSetListener {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        AddEditFuelingActivity fuelingActivity = (AddEditFuelingActivity) getActivity();
        fuelingActivity.setTime(hourOfDay, minute);
    }
}
