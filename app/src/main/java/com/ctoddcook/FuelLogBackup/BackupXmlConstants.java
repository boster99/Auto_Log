package com.ctoddcook.FuelLogBackup;

/**
 * Holds the data imported by the BackupXmlParser for a single column in a single row
 * <p/>
 * Created by C. Todd Cook on 6/27/2016.<br>
 * ctodd@ctoddcook.com
 */
class BackupXmlConstants {
    static final String TABLE_TAG = "table";
    static final String TABLE_PK_ATTR = "prime_key";
    static final String TABLE_NAME_ATTR = "name";

    static final String DATABASE_TAG = "database";

    static final String ROW_TAG = "row";

    static final String COLUMN_TAG = "column";
    static final String COLUMN_NAME_ATTR = "name";
    static final String COLUMN_TYPE_ATTR = "type";
}
