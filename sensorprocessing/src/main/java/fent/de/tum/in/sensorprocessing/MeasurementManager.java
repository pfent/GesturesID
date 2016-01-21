package fent.de.tum.in.sensorprocessing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import fent.de.tum.in.sensorprocessing.measurement.SensorData;
import fent.de.tum.in.sensorprocessing.measurement.SensorDataBuilder;

public class MeasurementManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String
            DATABASE_NAME = "SensorMeasurements.db",
            TABLE_USERS = "users",
            TABLE_MEASUREMENTS = "measurements",
            TABLE_DATASETS = "datasets",
            TABLE_KEYSTROKES = "keystrokes",
            USERS_ID = "userID",
            USERS_NAME = "name",
            MEASUREMENTS_ID = "measurementID",
            MEASUREMENTS_TIME = "sensorTime",
            DATASETS_POINTNUMBER = "measurmentPointNumber",
            DATASETS_XAXIS = "xAxis",
            DATASETS_YAXIS = "yAxis",
            DATASETS_ZAXIS = "zAxis",
            KEYSTROKES_POINTNUMBER = "keystrokeNumber",
            KEYSTROKES_TIME = "keystrokeTime",
            KEYSTROKES_CHARACTER = "keyStroked";
    private static final String
            CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " ( " +
            USERS_ID + " INTEGER PRIMARY KEY, " +
            USERS_NAME + " TEXT );",
            CREATE_TABLE_MEASUREMENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_MEASUREMENTS + " ( " +
                    MEASUREMENTS_ID + " INTEGER PRIMARY KEY, " +
                    USERS_ID + " INTEGER, " +
                    "FOREIGN KEY (" + USERS_ID + ") REFERENCES " + TABLE_USERS + "(" + USERS_ID + ")" + " );",
            CREATE_TABLE_DATASETS = "CREATE TABLE IF NOT EXISTS " + TABLE_DATASETS + " ( " +
                    MEASUREMENTS_ID + " INTEGER, " +
                    DATASETS_POINTNUMBER + " INTEGER, " +
                    MEASUREMENTS_TIME + " INTEGER, " +
                    DATASETS_XAXIS + " REAL, " +
                    DATASETS_YAXIS + " REAL, " +
                    DATASETS_ZAXIS + " REAL, " +
                    "PRIMARY KEY ( " + MEASUREMENTS_ID + ", " + DATASETS_POINTNUMBER + " ) );",
            CREATE_TABLE_KEYSTROKES = "CREATE TABLE IF NOT EXISTS " + TABLE_KEYSTROKES + " ( " +
                    MEASUREMENTS_ID + " INTEGER, " +
                    KEYSTROKES_POINTNUMBER + " INTEGER, " +
                    KEYSTROKES_TIME + " INTEGER, " +
                    KEYSTROKES_CHARACTER + " TEXT, " +
                    "PRIMARY KEY ( " + MEASUREMENTS_ID + ", " + KEYSTROKES_POINTNUMBER + " ) );";
    private static MeasurementManager instance;
    private Context context;

    private MeasurementManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        instance = this;
        this.context = context;
    }

    public static MeasurementManager getInstance(Context context) {
        if (instance == null) {
            instance = new MeasurementManager(context);
        }
        return instance;
    }

    /**
     * Create a new profile and user
     *
     * @param name The name of the user
     * @return the ID of the new user
     */
    public long createUser(String name) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USERS_NAME, name);

        return db.insert(TABLE_USERS, null, values);
    }

    /**
     * Initialize a new measurement for given user, without writing any concrete data yet
     *
     * @param userID the userID of the user that initiatzed the measurement
     * @return the ID of the new measurement
     */
    public long createMeasurement(long userID) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USERS_ID, userID);

        return db.insert(TABLE_MEASUREMENTS, null, values);
    }

    /**
     * Add data to a specific measurement
     *
     * @param measurementID where the data belongs to
     * @param startTime     when the measurement starten
     * @param endTime       when the measurment ended
     * @param data          the measurement data (X-, Y-, Z-Axis values)
     */
    public void addMeasurementData(long measurementID, float[][] data, long[] measurementTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            db.beginTransaction();

            for (int i = 0; i < data[0].length; i++) {
                values.clear();
                values.put(MEASUREMENTS_ID, measurementID);
                values.put(DATASETS_POINTNUMBER, i);
                values.put(DATASETS_XAXIS, data[0][i]);
                values.put(DATASETS_YAXIS, data[1][i]);
                values.put(DATASETS_ZAXIS, data[2][i]);
                values.put(MEASUREMENTS_TIME, measurementTime[i]);

                db.insert(TABLE_DATASETS, null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void addKeyStrokes(long measurementID, char[] keystrokes, long[] keystrokeTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            db.beginTransaction();

            for (int i = 0; i < keystrokes.length; i++) {
                values.clear();
                values.put(MEASUREMENTS_ID, measurementID);
                values.put(KEYSTROKES_POINTNUMBER, i);
                values.put(KEYSTROKES_TIME, keystrokeTime[i]);
                values.put(KEYSTROKES_CHARACTER, Character.toString(keystrokes[i]));

                db.insert(TABLE_KEYSTROKES, null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(CREATE_TABLE_USERS);
            db.execSQL(CREATE_TABLE_DATASETS);
            db.execSQL(CREATE_TABLE_MEASUREMENTS);
            db.execSQL(CREATE_TABLE_KEYSTROKES);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // NOP: currently only version 1
    }

    public void copyDbToSdCard() {
        String path = context.getApplicationInfo().dataDir;
        String databasePath = path + "/databases/" + DATABASE_NAME;

        String external = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String externalDatabasePath = external + "/" + DATABASE_NAME;

        File src = new File(databasePath);
        File dst = new File(externalDatabasePath);

        try {

            if (dst.exists()) {
                dst.delete();
            }
            dst.createNewFile();

            FileInputStream inStream = new FileInputStream(src);
            FileOutputStream outStream = new FileOutputStream(dst);

            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();

            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Long> getAllUsers() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(true, TABLE_USERS, new String[]{USERS_ID}, null, null, null, null, null, null);

        List<Long> result = new ArrayList<>(c.getCount());
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result.add(c.getLong(0));
        }
        c.close();
        return result;
    }

    public List<Long> getMeasurementsForUser(long userID) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(true, TABLE_MEASUREMENTS, new String[]{MEASUREMENTS_ID, USERS_ID},
                USERS_ID + " = ?", new String[]{Long.toString(userID)}, null, null, null, null);

        List<Long> result = new ArrayList<>(c.getCount());
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result.add(c.getLong(0));
        }
        c.close();
        return result;
    }

    public SensorData getSensorData(long measurementID) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(true, TABLE_DATASETS,
                new String[]{MEASUREMENTS_ID, DATASETS_POINTNUMBER, MEASUREMENTS_TIME,
                        DATASETS_XAXIS, DATASETS_YAXIS, DATASETS_ZAXIS},
                MEASUREMENTS_ID + " = ?", new String[]{Long.toString(measurementID)}, null, null, null, null);
        c.moveToFirst();
        final int timePos = 2,
                xAxisPos = 3,
                yAxisPos = 4,
                zAxisPos = 5;

        SensorDataBuilder result = new SensorDataBuilder(
                new float[]{c.getFloat(xAxisPos), c.getFloat(yAxisPos), c.getFloat(zAxisPos)},
                c.getLong(timePos)
        );
        for (; !c.isAfterLast(); c.moveToNext()) {
            result.append(
                    new float[]{c.getFloat(xAxisPos), c.getFloat(yAxisPos), c.getFloat(zAxisPos)},
                    c.getLong(timePos)
            );
        }
        c.close();
        return result.toSensorData();
    }

}
