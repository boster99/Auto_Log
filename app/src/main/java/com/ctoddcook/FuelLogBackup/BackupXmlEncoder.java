package com.ctoddcook.FuelLogBackup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ctoddcook.CamGenTools.Assert;
import com.ctoddcook.CamGenTools.XmlBase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


/**
 * Tables to XML file exporter. Modifications made by
 * <a href="mailto:ctodd@ctoddcook.com">C. Todd Cook</a>
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class BackupXmlEncoder extends XmlBase {
    private static final String TABLE_INDENT = INDENT;
    private static final String ROW_INDENT = TABLE_INDENT + INDENT;
    private static final String COLUMN_INDENT = ROW_INDENT + INDENT;
    private static SQLiteDatabase mDB;
    private final BufferedWriter w;
    private ArrayList<Table> mTableList = new ArrayList<>(10);

    /* Convenience constructor for use with a file. Calls the other constructor. */
    private BackupXmlEncoder(final File outFile) throws IOException {
        this(new FileOutputStream(outFile));
    }

    /* Constructor for use with an output stream */
    private BackupXmlEncoder(final OutputStream os) throws IOException {
        w = new BufferedWriter(new OutputStreamWriter(os, "UTF8"));
    }

    /**
     * Returns a constructed instance using the provided File to open an output stream
     * @param dbOpener The Database helper which will provide data
     * @param outFile The File to which the XML data is to be written
     * @return An instance of this class, ready to have tables added and then to encode
     * @throws IOException If there is a problem opening the provided File
     */
    public static BackupXmlEncoder getInstance(SQLiteOpenHelper dbOpener, File outFile)
            throws IOException {
        setDb(dbOpener);

        return new BackupXmlEncoder(outFile);
    }

    /**
     * Returns a constructed instance using the output stream. For example, the output stream
     * might be a ByteArrayOutputStream, so the XML data can be retrieved from that stream's
     * <code>toString()</code> method.
     * @param dbOpener The Database helper which will provide data
     * @param os The stream to which the XML data is to be written
     * @return An instance of this class, ready to have tables added and then to encode
     * @throws IOException If there is a problem accessing the output stream
     */
    public static BackupXmlEncoder getInstance(SQLiteOpenHelper dbOpener, OutputStream os)
            throws IOException {
        setDb(dbOpener);

        return new BackupXmlEncoder(os);
    }

    /* Stores a reference to the database helper (the source of the data) */
    private synchronized static void setDb(SQLiteOpenHelper dbOpener) {
        if (mDB == null) mDB = dbOpener.getReadableDatabase();
    }

    /**
     * Adds a <code>table name/prime key</code> pair to the list of tables to be exported to XML.
     * This must be called for ALL tables to be included, before calling encodeDatabase().
     *
     * @param name     The name of the table
     * @param primeKey The table's primary key
     */
    public void adddTable(final String name, final String primeKey) {
        Assert.notNull(name != null, "The table name must not be empty or null");
        Assert.notNull(primeKey != null, "The prime key must not be empty or null");

        Table t = new Table(name, primeKey);
        mTableList.add(t);
    }

    /**
     * Encodes all of the added tables (via <code>addTable()</code>) and their data to XML.
     *
     * @throws IOException
     */
    public void encodeDatabase() throws IOException {
        Assert.notEmpty(mTableList, "The mTableList field is null or empty");

        w.append(HEADER);
        w.newLine();

        w.append(openTag(BackupXmlDatabase.DATABASE_TAG));
        w.newLine();

        for (Table t : mTableList) {
            encodeTable(t.mName, t.mPrimeKey);
        }

        w.append(closeTag(BackupXmlDatabase.DATABASE_TAG));
        w.newLine();
        w.close();
    }

    /**
     * Encodes a single table and its rows of data to XML
     *
     * @param tableName       The table to be encoded
     * @param primeKeyColumn  The table's primary key
     * @throws IOException
     */
    private void encodeTable(final String tableName, final String primeKeyColumn) throws
            IOException {
        w.append(TABLE_INDENT);
        w.append(openTag(BackupXmlTable.TABLE_TAG,
                BackupXmlTable.NAME_ATTR, tableName,
                BackupXmlTable.PK_ATTR, primeKeyColumn));
        w.newLine();

        final Cursor c = mDB.rawQuery("select * from " + tableName, null);

        if (c.moveToFirst()) {
            final int cols = c.getColumnCount();
            do {
                encodeRow(c, cols);
            } while (c.moveToNext());
        }

        c.close();

        w.append(TABLE_INDENT);
        w.append(closeTag(BackupXmlTable.TABLE_TAG));
        w.newLine();
    }

    /**
     * Encodes a single row of data to XML, column by column
     *
     * @param c          A cursor used to fetch data from the database
     * @param nbrColumns The number of columns in each row
     * @throws IOException
     */
    private void encodeRow(final Cursor c, final int nbrColumns) throws IOException {
        w.append(ROW_INDENT);
        w.append(openTag(BackupXmlRow.ROW_TAG));
        w.newLine();

        for (int i = 0; i < nbrColumns; i++) {
            encodeColumn(c, i);
        }

        w.append(ROW_INDENT);
        w.append(closeTag(BackupXmlRow.ROW_TAG));
        w.newLine();
    }

    /**
     * Encodes a single column of data, from a single row, to XML.
     *
     * @param c         A cursor used to fetch data from the database
     * @param colNumber The column number to encode to XML
     * @throws IOException
     */
    private void encodeColumn(final Cursor c, final int colNumber) throws IOException {
        w.append(COLUMN_INDENT);
        w.append(openTag(BackupXmlColumn.COLUMN_TAG,
                BackupXmlColumn.NAME_ATTR, c.getColumnName(colNumber),
                BackupXmlColumn.TYPE_ATTR, Integer.toString(c.getType(colNumber))));
        w.append(encodeValue(c.getString(colNumber)));
        w.append(closeTag(BackupXmlColumn.COLUMN_TAG));
        w.newLine();
    }

    private class Table {
        String mName, mPrimeKey;

        Table(String name, String primeKey) {
            mName = name;
            mPrimeKey = primeKey;
        }
    }
}
