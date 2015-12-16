package fent.de.tum.in.sensorprocessing;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MeasurementManager extends SQLiteOpenHelper {

    private static MeasurementManager instance;

    private static final int DATABASE_VERSION = 1;
    private static final String
            DATABASE_NAME = "SensorMeasurements.db",
            TABLE_USERS = "users",
            TABLE_MEASUREMENTS = "measurements",
            TABLE_DATASETS = "datasets",
            USERS_ID = "userID",
            USERS_NAME = "name",
            MEASUREMENTS_ID = "measurementID",
            MEASUREMENTS_STARTTIME = "startTime",
            MEASRUEMENTS_ENDTIME = "endTime",
            DATASETS_POINTNUMBER = "measurmentPointNumber",
            DATASETS_XAXIS = "xAxis",
            DATASETS_YAXIS = "yAxis",
            DATASETS_ZAXIS = "zAxis";

    private static final String
            CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " ( " +
            USERS_ID + " INTEGER PRIMARY KEY, " +
            USERS_NAME + " TEXT );",
            CREATE_TABLE_MEASUREMENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_MEASUREMENTS + " ( " +
                    MEASUREMENTS_ID + " INTEGER PRIMARY KEY, " +
                    USERS_ID + " INTEGER, " +
                    MEASUREMENTS_STARTTIME + " INETEGER, " +
                    MEASRUEMENTS_ENDTIME + " INTEGER, " +
                    "FOREIGN KEY (" + USERS_ID + ") REFERENCES " + TABLE_USERS + "(" + USERS_ID + ")" + " );",
            CREATE_TABLE_DATASETS = "CREATE TABLE IF NOT EXISTS " + TABLE_DATASETS + " ( " +
                    MEASUREMENTS_ID + " INTEGER, " +
                    DATASETS_POINTNUMBER + " INTEGER, " +
                    DATASETS_XAXIS + " REAL, " +
                    DATASETS_YAXIS + " REAL, " +
                    DATASETS_ZAXIS + " REAL, " +
                    "PRIMARY KEY ( " + MEASUREMENTS_ID + ", " + DATASETS_POINTNUMBER + " ) );";


    private MeasurementManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        instance = this;
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

        // start and endTime explicitly not set

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
    public void addMeasurementData(long measurementID, long startTime, long endTime, float[][] data) {
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

                db.insert(TABLE_DATASETS, null, values);
            }

            values.clear();
            //values.put(MEASUREMENTS_ID, measurementID);
            values.put(MEASUREMENTS_STARTTIME, startTime);
            values.put(MEASRUEMENTS_ENDTIME, endTime);

            db.update(TABLE_MEASUREMENTS, values, MEASUREMENTS_ID + " = ?", new String[]{Long.toString(measurementID)});
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
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // NOP: currently only version 1
    }

    public static MeasurementManager getInstance(Context context) {
        if (instance == null) {
            instance = new MeasurementManager(context);
        }
        return instance;
    }
}
