/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

/**
 * Created by C. Todd Cook on 5/9/2016.
 * ctodd@ctoddcook.com
 *
 * Provides common fields and methods for data classes.
 */
public class DataHolder {
    public static final String STATUS_NEW = "N";
    public static final String STATUS_CURRENT = "C";
    public static final String STATUS_UPDATED = "U";

    protected String mStatus = STATUS_NEW;
}
