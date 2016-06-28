package com.ctoddcook.FuelLogBackup;

import java.util.ArrayList;

/**
 * Holds the data for a single table in the database, as imported by BackupXmlParser
 * <p/>
 * Created by C. Todd Cook on 6/27/2016.<br>
 * ctodd@ctoddcook.com
 */
public class BackupXmlTable {
  public static final String TABLE_TAG = "table";
  public static final String PK_ATTR = "prime_key";
  public static final String NAME_ATTR = "name";

  public final String name;
  public final String primeKey;
  public final ArrayList<BackupXmlRow> rows = new ArrayList<>();

  public BackupXmlTable(String name, String primeKey) {
    this.name = name;
    this.primeKey = primeKey;
  }

  public void add(BackupXmlRow row) {
    this.rows.add(row);
  }
}
