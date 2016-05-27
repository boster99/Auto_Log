package com.ctoddcook.CGenTools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.*;


/**
 * Tables to XML file exporter. Modifications made by
 * <a href="mailto:ctodd@ctoddcook.com">C. Todd Cook</a>
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DatabaseToXmlExporter extends XmlBase {
  public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
  private final BufferedWriter w;
  private static SQLiteOpenHelper sDbOpener;

  private DatabaseToXmlExporter(final File outFile) throws IOException { //I hate checked exceptions
    this(new FileOutputStream(outFile));
  }

  public DatabaseToXmlExporter(final OutputStream os) throws IOException {
    w = new BufferedWriter(new OutputStreamWriter(os, "UTF8"));
    w.append(HEADER);
    w.newLine();
  }

  public void close() throws IOException {
    closeTag(DATABASE_TAG);
    w.close();
  }

  public void writeTable(final SQLiteDatabase db, final String tableName, final String pkColumn) throws IOException {
    openTag(TABLE_TAG, NAME_ATTR, tableName, PK_ATTR, pkColumn);
    w.newLine();
    final Cursor c = db.rawQuery("select * from " + tableName, null);
    if (c.moveToFirst()) {
      final int cols = c.getColumnCount();
      do {
        openTag(ROW_TAG);
        w.newLine();
        for (int i = 0; i < cols; i++) {
          openTag(COLUMN_TAG, NAME_ATTR, c.getColumnName(i));
          final String s = escape(c.getString(i));
          w.append(s);
          closeTag(COLUMN_TAG);
          w.newLine();
        }
        closeTag(ROW_TAG);
        w.newLine();
      } while (c.moveToNext());
    }
    c.close();
    closeTag(TABLE_TAG);
    w.newLine();
  }

  public synchronized static DatabaseToXmlExporter getInstance(SQLiteOpenHelper dbOpener, File outFile)
      throws IOException {
    if (sDbOpener == null) sDbOpener = dbOpener;

    return new DatabaseToXmlExporter(outFile);
  }

  private void exportData(final SQLiteDatabase database, final File f, String[] tables,
                          String[] primaryKeys) throws IOException {
    if (tables == null) throw new IllegalArgumentException("Parameter tables is null");
    if (primaryKeys == null) throw new IllegalArgumentException("Parameter primaryKeys is null");
    if (tables.length != primaryKeys.length)
      throw new IllegalArgumentException("Parameters tables and primaryKeys are not the same " +
          "length");

    final DatabaseToXmlExporter x = new DatabaseToXmlExporter(f);
    for (int i = 0; i < tables.length; i++) {
      x.writeTable(database, tables[i], primaryKeys[i]);
    }
    x.close();
  }

  private void exportData(final SQLiteDatabase database, final OutputStream os, String[] tables,
                          String[] primaryKeys) throws IOException {

    if (tables == null) throw new IllegalArgumentException("Parameter tables is null");
    if (primaryKeys == null) throw new IllegalArgumentException("Parameter primaryKeys is null");
    if (tables.length != primaryKeys.length)
      throw new IllegalArgumentException("Parameters tables and primaryKeys are not the same " +
          "length");

    final DatabaseToXmlExporter x = new DatabaseToXmlExporter(os);
    for (int i = 0; i < tables.length; i++) {
      x.writeTable(database, tables[i], primaryKeys[i]);
    }
    x.close();
  }

  protected Writer openTag(final String tag, final String... attrs) throws IOException {
    w.append('<').append(tag);
    if (attrs.length == 0) {
      w.append('>');
      return w;
    }
    CTools.assertTrue(attrs.length % 2 == 0);
    for (int i = 0; i < attrs.length; i++) {
      w.append(' ').append(attrs[i++]).append("=\"").append(attrs[i]).append('"');
    }
    w.append('>');
    return w;
  }

  protected Writer closeTag(final String tag) throws IOException {
    w.append("</").append(tag).append('>');
    return w;
  }
}
