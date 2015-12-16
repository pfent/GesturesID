package fent.de.tum.in.gesturesid;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.ActionPage;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Locale;

import fent.de.tum.in.sensorprocessing.MeasurementManager;
import fent.de.tum.in.sensorprocessing.OnPatternReceivedListener;
import fent.de.tum.in.sensorprocessing.PatternRecorder;
import fent.de.tum.in.sensorprocessing.measurement.SensorData;
import fent.de.tum.in.sensorprocessing.measurement.SensorDataBuilder;

public class MeasurementActivity extends WearableActivity implements OnPatternReceivedListener {

    private ActionPage mActionPage;
    private boolean isRecording = false;

    private PatternRecorder recorder;
    private MeasurementManager manager;
    private long dummyUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        setAmbientEnabled();

        mActionPage = (ActionPage) findViewById(R.id.actionpage);
        mActionPage.setOnClickListener(onClickListener);

        recorder = new PatternRecorder(this, Sensor.TYPE_ACCELEROMETER, this);
        manager = MeasurementManager.getInstance(this);
        dummyUserID = manager.createUser("wearDummy");
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Perform action on click
            if (!isRecording) {
                isRecording = true;
                mActionPage.setColor(Color.RED);
                mActionPage.setText(getText(R.string.stop_measurement));
                recorder.startListening();
                Log.d("Test", "Start sensor listening");
                return;
            }
            isRecording = false;
            mActionPage.setColor(Color.GREEN);
            mActionPage.setText(getText(R.string.start_measurement));
            recorder.stopListening();
            Log.d("Test", "Stop sensor listening");
        }
    };

    @Override
    public void OnPatternReceived(SensorData data, long startTime, long endTime) {
        long measurementID = manager.createMeasurement(dummyUserID);
        manager.addMeasurementData(measurementID, startTime, endTime, data.data);
        manager.copyDbToSdCard();
    }
}
