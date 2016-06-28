package com.ctoddcook.FuelLogBackup;

import java.util.ArrayList;

/**
 * Holds the data from the data, as imported by BackupXmlParser
 * <p/>
 * Created by C. Todd Cook on 6/27/2016.<br>
 * ctodd@ctoddcook.com
 */
public class BackupXmlDatabase {
  public static final String DATABASE_TAG = "database";

  public final ArrayList<BackupXmlTable> tables = new ArrayList<>();

  public void add(BackupXmlTable table) {
    this.tables.add(table);
  }
}
