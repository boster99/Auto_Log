package com.ctoddcook.FuelLogBackup;

import java.util.ArrayList;

/**
 * Holds a list of BackupXmlColumn columns for a single row of data
 * <p/>
 * Created by C. Todd Cook on 6/27/2016.<br>
 * ctodd@ctoddcook.com
 */
public class BackupXmlRow {
  public static final String ROW_TAG = "row";

  public final ArrayList<BackupXmlColumn> columns = new ArrayList<>();

  public void add(BackupXmlColumn column) {
    this.columns.add(column);
  }
}
