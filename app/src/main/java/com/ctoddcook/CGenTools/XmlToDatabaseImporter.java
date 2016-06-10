package com.ctoddcook.cGenTools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Tables to XML importer.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class XmlToDatabaseImporter extends XmlBase {
  private static final String TAG = "XmlToDatabaseImporter";
  private static SQLiteDatabase mDB;


  public static void importData(final Context ctx, final File f, final boolean overwrite)
      throws IOException {
    final FileInputStream fis = new FileInputStream(f);
    try {
      importData(ctx, fis, overwrite);
    } finally {
      fis.close();
    }
  }

  public static void importData(final Context ctx, final InputStream is, final boolean overwrite)
      throws IOException {
    final InputStreamReader isr = new InputStreamReader(is, "UTF8");
    try {
      final XmlPullParser p = Xml.newPullParser();
      p.setInput(isr);
      readTag(p, DATABASE_TAG, false);

      // Order of tables matters for foreign keys. If a table has a foreign key pointing to
      // another table, it must come after the table it points to

      // I've commented out the next 2 lines so I can get the app to compile, but otherwise I
      // have not changed them.
//      final LongSparseArray<Long> tagIDRemapping = importTable(
//          mDB,
//          p,
//          DBHelper.TAG_TABLE_NAME,
//          overwrite,
//          null,
//          null,
//          DBHelper.TAG_TAG);
//      importTable(
//          mDB,
//          p,
//          DBHelper.TODO_TABLE_NAME,
//          overwrite,
//          DBHelper.TODO_TAG_ID,
//          tagIDRemapping,
//          DBHelper.TODO_TAG_ID,
//          DBHelper.TODO_SUMMARY);
    } catch (XmlPullParserException e) {
      Log.e(TAG, "error parsing backup file", e);
    } finally {
      mDB.close();
      isr.close();
    }
  }


  /*
  I have not been able to figure out what he's doing with the mergeByColumn arguments. He called
  this method with two tables. For the first one, he passed nothing for mergeByColumn; for the
  second one, he passed TODO_TAG_ID (which appears to be a foreign key pointing to the TAG_ID
  column of the TAG table), and passed TODO_SUMMARY.

  fkColumn is when the table being passed here has a foreign key pointing to another table.
   */
  // todo document this method thoroughly
  private static LongSparseArray<Long> importTable(
      final SQLiteDatabase db,
      final XmlPullParser xmlParser,
      final String tableName,
      final boolean overwrite,
      @Nullable final String fkColumn,  // points to the prime key of another table
      @Nullable final LongSparseArray<Long> fkRemapping,
      final String... mergeByColumn) throws IOException, XmlPullParserException {
    readTag(xmlParser, TABLE_TAG, false);
    CTools.assertEquals(tableName, xmlParser.getAttributeValue(null, NAME_ATTR));

    final String pkColumn = xmlParser.getAttributeValue(null, PK_ATTR);
    final LongSparseArray<Long> remapping = overwrite ? null : new LongSparseArray<Long>();
    final HashSet<String> mb = new HashSet<>(mergeByColumn.length);
    mb.addAll(Arrays.asList(mergeByColumn));

    while (true) {
      final int et = xmlParser.nextTag();
      if (et == XmlPullParser.END_TAG) {
        CTools.assertEquals(TABLE_TAG, xmlParser.getName());
        return remapping;
      } else {
        CTools.assertEquals(XmlPullParser.START_TAG, et);
        CTools.assertEquals(ROW_TAG, xmlParser.getName());
        importRow(db, xmlParser, tableName, pkColumn, remapping, fkColumn, fkRemapping, mb);
      }
    }
  }

  // todo document this method thoroughly
  private static void importRow(final SQLiteDatabase db,
                                final XmlPullParser xmlParser,
                                final String tableName,
                                final String pkColumn,
                                final LongSparseArray<Long> pkRemapping, //we fill pkRemapping
                                @Nullable final String fkColumn,
                                @Nullable final LongSparseArray<Long> fkRemapping, //we use
                                final Set<String> mergeByColumn)
        throws IOException, XmlPullParserException {
    final ContentValues cv = new ContentValues();
    int mergeCnt = 0;
    final StringBuilder mergeCond = new StringBuilder();
    final String[] mergeCols = new String[mergeByColumn.size()];
    final String[] mergeArgs = new String[mergeByColumn.size()];

    Long oldPK = null;
    while (true) {
      final int et = xmlParser.nextTag();
      if (et == XmlPullParser.END_TAG) {
        if (ROW_TAG.equals(xmlParser.getName()))
          break;
        CTools.assertEquals(COLUMN_TAG, xmlParser.getName());
      } else {
        CTools.assertEquals(XmlPullParser.START_TAG, et);
        CTools.assertEquals(COLUMN_TAG, xmlParser.getName());
        final String columnName = xmlParser.getAttributeValue(null, NAME_ATTR);
        final String columnValue = unescape(xmlParser.nextText());

        if (pkRemapping == null || !columnName.equals(pkColumn))
          cv.put(columnName, columnValue);
        if (fkRemapping != null && columnName.equals(fkColumn)) {
          final Long oldFK = Long.parseLong(columnValue);
          final Long newFK = fkRemapping.get(oldFK);
          if (newFK == null) {
            Log.w(TAG, "can't find remapping for " + oldFK + " for table " + tableName);
          } else if (!newFK.equals(oldFK)) {
//						Log.i(TAG, "remapped " + tableName + '.' + fkColumn + ": " + oldFK + " -> " + newFK);
            cv.put(columnName, newFK);
          } // else same FK
        }
        if (columnName.equals(pkColumn))
          oldPK = Long.parseLong(columnValue);

        if (mergeByColumn.contains(columnName)) {
          if (mergeCnt > 0)
            mergeCond.append(" AND ");
          mergeCond.append(columnName).append("=?");
          mergeCols[mergeCnt] = columnName;
          mergeArgs[mergeCnt++] = columnValue;
        }
      }
    }
    if (fkRemapping != null)
      for (int i = 0; i < mergeCnt; i++) {
        if (mergeCols[i].equals(fkColumn)) {
          final Long oldFK = Long.parseLong(mergeArgs[i]);
          final Long newFK = fkRemapping.get(oldFK);
          if (newFK != null && !newFK.equals(oldFK))
            mergeArgs[i] = newFK.toString();
        }
      }
    if (mergeCnt > 0) {
      final String mergeCondStr = mergeCond.toString();
//			Log.i(TAG, "merge: " + mergeCondStr + ", " + Arrays.toString(mergeArgs));
      final int updated = db.update(tableName, cv, mergeCondStr, mergeArgs);
      if (updated > 1) {
        if (pkRemapping != null)
          Log.w(TAG, "Can't do PK remapping, updated more than 1 row");
        return;
      }
      if (updated == 1) {
        if (pkRemapping != null) {
          final Cursor cursor = db.query(tableName, new String[]{pkColumn}, mergeCondStr,
              mergeArgs, null, null, null);
          final int cnt = cursor.getCount();
          if (cnt != 1)
            Log.w(TAG, "Can't do PK remapping, error finding updated row; cnt=" + cnt);
          else if (oldPK == null)
            Log.w(TAG, "Can't do PK remapping, oldPK is null; cnt=" + cnt);
          else {
            cursor.moveToFirst();
            pkRemapping.put(oldPK, cursor.getLong(0));
            cursor.close();
          }
        }
//				Log.i(TAG, "Merged!");
        return;
      }
      /*
      ????? He has no insert statement???? This next line was commented out by him
       */
      //updated 0 = do an insert
    }
    final long pk = db.replace(tableName, null, cv);
    if (oldPK != null && pkRemapping != null)
      pkRemapping.put(oldPK, pk);
  }

  private static void readTag(final XmlPullParser p, final String expectedName,
                              final boolean closing) throws IOException, XmlPullParserException {
    p.nextTag();
    p.require(closing ? XmlPullParser.END_TAG : XmlPullParser.START_TAG, null, expectedName);
  }
}
