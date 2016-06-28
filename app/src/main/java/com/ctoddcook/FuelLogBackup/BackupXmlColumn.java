package com.ctoddcook.FuelLogBackup;

/**
 * Holds the data imported by the BackupXmlParser for a single column in a single row
 * <p/>
 * Created by C. Todd Cook on 6/27/2016.<br>
 * ctodd@ctoddcook.com
 */
public class BackupXmlColumn {
  public static final String COLUMN_TAG = "column";
  public static final String NAME_ATTR = "name";
  public static final String TYPE_ATTR = "type";

  public final String name;
  public final int type;
  public String value;

  public BackupXmlColumn(String name, String type) {
    this.name = name;
    this.type = Integer.parseInt(type);
  }
}
