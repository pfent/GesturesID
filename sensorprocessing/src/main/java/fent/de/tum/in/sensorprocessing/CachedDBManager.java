package fent.de.tum.in.sensorprocessing;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * This is a class to generate a database in Android's cache.
 * <p/>
 * This is used to export the data via USB and visualize the algorithms
 */
public class CachedDBManager extends SQLiteOpenHelper {

    private static CachedDBManager instance;
    private final String dataBaseLocation;
    private Context context;

    private CachedDBManager(Context context) {
        super(context, new File(context.getCacheDir(), "CacheFeatures.db").getAbsolutePath(), null, 1);
        dataBaseLocation = new File(context.getCacheDir(), "CacheFeatures.db").getAbsolutePath();
        this.context = context;
    }

    public static CachedDBManager getInstance(Context context) {
        if (instance == null) {
            instance = new CachedDBManager(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS peaks ( measurementID INTEGER, peakNumber INTEGER " +
                "value INTEGER, PRIMARY KEY (measurementID, peakNumber) );");
        db.execSQL("CREATE TABLE IF NOT EXISTS normalizedData ( measurementID INTEGER, measurmentPointNumber INTEGER " +
                "zAxis REAL, PRIMARY KEY (measurementID, measurmentPointNumber) );");
        db.execSQL("CREATE TABLE IF NOT EXISTS smoothedData ( measurementID INTEGER, measurmentPointNumber INTEGER " +
                "zAxis REAL, PRIMARY KEY (measurementID, measurmentPointNumber) );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // NOP
    }

    public void insertPeaks(long measurementID, int[] peaks) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            db.beginTransaction();

            for (int i = 0; i < peaks.length; i++) {
                values.clear();
                values.put("measurementID", measurementID);
                values.put("peakNumber", i);
                values.put("value", peaks[i]);

                db.insert("peaks", null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void addNormalizedData(long measurementID, float[] data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            db.beginTransaction();

            for (int i = 0; i < data.length; i++) {
                values.clear();
                values.put("measurementID", measurementID);
                values.put("measurmentPointNumber", i);
                values.put("zAxis", data[i]);

                db.insert("normalizedData", null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void addSmoothedData(long measurementID, float[] data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            db.beginTransaction();

            for (int i = 0; i < data.length; i++) {
                values.clear();
                values.put("measurementID", measurementID);
                values.put("measurmentPointNumber", i);
                values.put("zAxis", data[i]);

                db.insert("smoothedData", null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void clear() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS peaks;");
        db.execSQL("DROP TABLE IF EXISTS normalizedData;");
        db.execSQL("DROP TABLE IF EXISTS smoothedData;");
    }
}
