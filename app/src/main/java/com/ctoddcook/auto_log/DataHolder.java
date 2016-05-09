/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by C. Todd Cook on 5/9/2016.
 * ctodd@ctoddcook.com
 *
 * Provides common fields and methods for data classes.
 */
public class DataHolder {
    public static final char STATUS_NEW = 'N';
    public static final char STATUS_CURRENT = 'C';
    public static final char STATUS_UPDATED = 'U';

    private char mStatus = STATUS_NEW;
    protected Date mLastUpdated = null;

    /**
     * Updates the mLastUpdated field to the current datetime. Should be called anytime
     * a child-class instance's fields are updated. This also changes the mStatus field to
     * STATUS_UPDATED, if it is currently STATUS_CURRENT.
     */
    protected void touch() {
        this.mLastUpdated = Calendar.getInstance().getTime();
        if (mStatus == STATUS_CURRENT)
            mStatus = STATUS_UPDATED;
    }

    /**
     * Getter for mLastUpdated field
     * @return value of mLastUpdated field
     */
    public Date getLastUpdated() {
        return mLastUpdated;
    }

    /**
     * Getter for mStatus field
     * @return value of mStatus field.
     */
    public char getStatus() {
        return mStatus;
    }

    /**
     * Indicates whether the instance status indicates this is a new record.
     * @return whether mStatus equals STATUS_NEW
     */
    public boolean isNew() {
        return (mStatus == STATUS_NEW);
    }

    /**
     * Indicates whether the instance status indicates this is an udpated record.
     * @return whether mStatus equals STATUS_UPDATED
     */
    public boolean isUpdated() {
        return (mStatus == STATUS_UPDATED);
    }

    /**
     * Indicates whether the instance status indicates this is a new OR updated record.
     * @return whether mStatus equals STATUS_NEW or STATUS_UPDATED
     */
    public boolean isNewOrUpdated() {
        return (mStatus == STATUS_NEW || mStatus == STATUS_UPDATED);
    }

    /**
     * Indicates whether the instance status indicates this is a current (i.e., not new
     * and not changed) record.
     * @return whether mStatus equals STATUS_CURRENT
     */
    public boolean isCurrent() {
        return (mStatus == STATUS_CURRENT);
    }
}
