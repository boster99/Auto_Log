package com.ctoddcook.FuelLogBackup;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parses and imports from an XML file the data exported from the database. The pattern of usage
 * is a nesting of methods. The outside caller calls <code>parse()</code>. Then:
 * <ul><code>parse()</code> calls <code>readDatabase()</code></ul>
 * <ul><code>readDatabase()</code> calls <code>readTable()</code> until it finds no more tables</ul>
 * <ul><code>readTable()</code> calls <code>readRow()</code> until it finds no more rows</ul>
 * <ul><code>readRow()</code> calls <code>readColumn()</code> until it finds no more columns</ul>
 * <p/>
 * Through this nesting, all data will be read for the database, from the XML file.
 * Created by C. Todd Cook on 6/27/2016.<br>
 * ctodd@ctoddcook.com
 */
public class BackupXmlParser {
  // We don't use namespaces
  private static final String ns = null;

  /**
   * The top-level method. This is the method of interest to an outside caller, as all other
   * methods are private.
   *
   * @param input The stream providing the XML data
   * @return The complete database for the application
   * @throws XmlPullParserException
   * @throws IOException
   */
  public BackupXmlDatabase parse(InputStream input) throws XmlPullParserException, IOException {
    try {
      XmlPullParser parser = Xml.newPullParser();
      parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
      parser.setInput(input, null);
      parser.nextTag();
      return readDatabase(parser);
    } finally {
      input.close();
    }
  }

  /**
   * Looks for one or more <code>table</code> tags, and for each, calls <code>readTable()</code>
   *
   * @param parser The XmlPullParser working the input stream
   * @return The complete database for the application
   * @throws XmlPullParserException
   * @throws IOException
   */
  private BackupXmlDatabase readDatabase(XmlPullParser parser) throws XmlPullParserException,
      IOException {
    parser.require(XmlPullParser.START_TAG, ns, BackupXmlDatabase.DATABASE_TAG);

    BackupXmlDatabase database = new BackupXmlDatabase();

    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        continue;
      }
      String name = parser.getName();
      if (name.equals(BackupXmlTable.TABLE_TAG)) {
        database.add(readTable(parser));
      } else {
        skip(parser);
      }
    }

    return database;
  }

  /**
   * Reads the name and primary key for a table, then looks for one or more <code>row</code> tags
   * and for each calls <code>readRow()</code>.
   *
   * @param parser The XmlPullParser working the input stream
   * @return A complete, single table from the database
   * @throws XmlPullParserException
   * @throws IOException
   */
  private BackupXmlTable readTable(XmlPullParser parser) throws XmlPullParserException,
      IOException {
    parser.require(XmlPullParser.START_TAG, ns, BackupXmlTable.TABLE_TAG);

    String tableName = parser.getAttributeValue(ns, BackupXmlTable.NAME_ATTR);
    String primeKey = parser.getAttributeValue(ns, BackupXmlTable.PK_ATTR);

    BackupXmlTable table = new BackupXmlTable(tableName, primeKey);

    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        continue;
      }
      String name = parser.getName();
      if (name.equals(BackupXmlRow.ROW_TAG)) {
        table.add(readRow(parser));
      } else {
        skip(parser);
      }
    }

    return table;
  }

  /**
   * Looks for one or more <code>column</code> tags, and for each calls <code>readColumn()</code>
   *
   * @param parser The XmlPullParser working the input stream
   * @return A complete, single row from a table
   * @throws XmlPullParserException
   * @throws IOException
   */
  private BackupXmlRow readRow(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, ns, BackupXmlRow.ROW_TAG);

    BackupXmlRow row = new BackupXmlRow();

    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        continue;
      }
      String name = parser.getName();
      if (name.equals(BackupXmlColumn.COLUMN_TAG)) {
        row.add(readColumn(parser));
      } else {
        skip(parser);
      }
    }

    return row;
  }

  /**
   * Reads the name and datatype for a column, then gets the column's value (as text).
   *
   * @param parser The XmlPullParser working the input stream
   * @return A complete, single column from within a row
   * @throws XmlPullParserException
   * @throws IOException
   */
  private BackupXmlColumn readColumn(XmlPullParser parser) throws XmlPullParserException,
      IOException {
    parser.require(XmlPullParser.START_TAG, ns, BackupXmlColumn.COLUMN_TAG);

    String columnName = parser.getAttributeValue(ns, BackupXmlColumn.NAME_ATTR);
    String dataType = parser.getAttributeValue(ns, BackupXmlColumn.TYPE_ATTR);
    BackupXmlColumn column = new BackupXmlColumn(columnName, dataType);

    if (parser.next() == XmlPullParser.TEXT) {
      column.value = parser.getText();
      parser.nextTag();
    }
    parser.require(XmlPullParser.END_TAG, ns, BackupXmlColumn.COLUMN_TAG);

    return column;
  }

  /**
   * Skips an XML element (and all nested sub-elements)
   *
   * @param parser The XmlPullParser working the input stream
   * @throws XmlPullParserException
   * @throws IOException
   */
  private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
    if (parser.getEventType() != XmlPullParser.START_TAG) {
      throw new IllegalStateException();
    }

    int depth = 1;
    while (depth != 0) {
      switch (parser.next()) {
        case XmlPullParser.END_TAG:
          depth--;
          break;
        case XmlPullParser.START_TAG:
          depth++;
          break;
      }
    }
  }
}
