/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved. 
 */

package com.ctoddcook.auto_log;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.ctoddcook.CGenTools.BaseDataMap;

/**
 * Created by C. Todd Cook on 5/10/2016.
 * ctodd@ctoddcook.com
 */
public abstract class AutoLogDataMap extends BaseDataMap {
  public static final String DATABASE_NAME = "autolog.db";
}
