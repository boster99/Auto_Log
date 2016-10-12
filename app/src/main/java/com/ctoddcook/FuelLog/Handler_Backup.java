package com.ctoddcook.FuelLog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.ctoddcook.CamGenTools.PropertyDataMap;
import com.ctoddcook.FuelLogBackup.BackupXmlEncoder;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission;


/**
 * Provides functionality for writing the database data to an XML file, and for restoring data
 * from said XML file.
 * <p>
 * Created by C. Todd Cook on 10/4/2016.<br>
 * ctodd@ctoddcook.com
 */

class Handler_Backup extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "Handler_Backup";
    private static final int PERMISSION_TO_WRITE_TO_EXT_STORAGE = 370;
    private static final int PERMISSION_TO_READ_FROM_EXT_STORAGE = 268;

    @SuppressWarnings("SpellCheckingInspection")
    private static final String BACKUP_FILE_NAME = "flbak.xml";
    private static final String BACKUP_FOLDER_NAME = "FuelLogBackup";

    private Activity mActivity;
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;


    /**
     * Constructor gets references to a Context and Activity (taken from the Context) for use in
     * displaying any needed user messages.
     * @param context The context to use for displaying messages.
     */
    Handler_Backup(Context context) {
        mContext = context;
        mActivity = (Activity)context;
        mDatabaseHelper = DatabaseHelper.getInstance(mContext);
    }

    /**
     * Primary access for starting the backup operation.
     */
    synchronized void backupToFile() {
        /* Check for accessible external storage */
        if (!isExternalStorageWritable()) {
            Toast.makeText(mContext, "Looks like there's no external storage " +
                    "available to write to. (Have you maybe removed it?) Backup has not been " +
                    "saved.", Toast.LENGTH_LONG).show();
            return;
        }

        /* Check for permission to write to external storage, and if we have it, go ahead. */
        if (isWriteToExternalAllowed()) {
            executeBackup();
        } else {
            Snackbar.make(mActivity.findViewById(R.id.Main_HistoricalsList), "If you just granted" +
                    " permission for writing to external storage, you may have to try the backup " +
                    "again.", Snackbar.LENGTH_SHORT).show();
        }
    }

    synchronized void restoreFromFile() {
        /* Check for readability of external storage */
        if (!isExternalStorageReadable()) {
            Toast.makeText(mContext, "Can't find external storage where a backup file would be " +
                    "stored.", Toast.LENGTH_LONG).show();
            return;
        }

        /* Check for permission to read from external storage, and if we have it, restore. */
        if (isReadFromExternalAllowed()) {
            /* Warn user about overwriting data */
            // TODO: Snackbar a message yes/no about overwriting data
            //executeRestore();
        } else {
            Snackbar.make(mActivity.findViewById(R.id.Main_HistoricalsList), "If you just granted" +
                    " permission for reading from external storage, you may have to try the " +
                    "restore again.", Snackbar.LENGTH_SHORT).show();
        }
    }

    /*
     * Opens (or creates) the path
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private synchronized void executeBackup() {
        File backupFolder = getFileFolder();
        if (backupFolder == null)
            return;

        /* Setup the backup encoder */
        File file = new File(backupFolder, BACKUP_FILE_NAME);
        BackupXmlEncoder d2x;

        try {
            d2x = BackupXmlEncoder.getInstance(mDatabaseHelper, file);
        } catch (IOException e) {
            Toast.makeText(mContext, "Dang! Got an IOException when trying to create a " +
                    "FileOutputStream. The backup has not been saved.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "backupToFile/Creating FileOutputStream:" + e.getMessage());
            return;
        }

        /* Add to the backup encoder the tables to be backed up */
        d2x.adddTable(PropertyDataMap.TABLE_NAME, PropertyDataMap._ID);
        d2x.adddTable(DatabaseMap_Vehicle.TABLE_NAME, DatabaseMap_Vehicle._ID);
        d2x.adddTable(DatabaseMap_Fueling.TABLE_NAME, DatabaseMap_Fueling._ID);

        /* Encode to XML */
        try {
            d2x.encodeDatabase();
        } catch (IOException e) {
            Toast.makeText(mContext, "Uhm, this is embarrassing. We got an " +
                    "IOException while trying to write to the backup file. The backup has not " +
                    "been saved.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "backupToFile:/Encoding Database: " + e.getMessage());
            return;
        }

        Snackbar.make(mActivity.findViewById(R.id.Main_HistoricalsList), "Backup has been saved " +
                "to " + backupFolder, Snackbar.LENGTH_SHORT).show();
    }

    /*
     * Checks to see if the user has granted permission to write to external storage.
     */
    private boolean isWriteToExternalAllowed() {
        /* Has the user granted permission to write to the disk? */
        if (ContextCompat.checkSelfPermission(mContext,
                permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            /* If the user hasn't granted permission, then ask for it */
            Log.d(TAG, "isWriteToExternalAllowed: WRITE_EXTERNAL_STORAGE: NOT granted");
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_TO_WRITE_TO_EXT_STORAGE);
            return false;
        }
    }

    /*
     * Checks to see if the user has granted permission to read from external storage.
     */
    private boolean isReadFromExternalAllowed() {
        /* Has the user granted permission to write to the disk? */
        if (ContextCompat.checkSelfPermission(mContext,
                permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            /* If the user hasn't granted permission, then ask for it */
            Log.d(TAG, "isReadFromExternalAllowed: READ_EXTERNAL_STORAGE: NOT granted");
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_TO_READ_FROM_EXT_STORAGE);
            return false;
        }
    }

     /*
      * Returns the folder where the backup is to be saved to or read from.
      */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private File getFileFolder() {
        /* Assemble the path to be used */
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS).toString();

        File backupFolder = new File(root
                + File.separator
                + BACKUP_FOLDER_NAME);

        /* Check whether the folder exists, and if it doesn't, create it. */
        if (!backupFolder.exists()) {
            if (!backupFolder.mkdirs()) {
                Toast.makeText(mContext, "Don't know why, but we could not create the folder to " +
                        "store the backup in.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "executeBackup: FALSE returned from folder.mkDirs()");
                return null;
            }
        }

        return backupFolder;
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    /* Checks if external storage is at least readable */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }
}
