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

import fent.de.tum.in.sensorprocessing.measurement.SensorDataBuilder;

public class MeasurementActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private ActionPage mActionPage;
    private boolean isRecording = false;

    SensorDataBuilder builder;
    SensorManager manager;
    Sensor sensor;
    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (builder == null) {
                builder = new SensorDataBuilder(event.values);
                return;
            }
            builder.append(event.values);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // NOP for now
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mActionPage = (ActionPage) findViewById(R.id.actionpage);
        mActionPage.setOnClickListener(onClickListener);

        manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensor = (manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Perform action on click

            if (!isRecording) {
                isRecording = true;
                mActionPage.setColor(Color.RED);
                mActionPage.setText(getText(R.string.stop_measurement));
                builder = null;
                manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
                Log.d("Test", "Start sensor listening");
                return;
            }
            isRecording = false;
            mActionPage.setColor(Color.GREEN);
            mActionPage.setText(getText(R.string.start_measurement));
            manager.unregisterListener(listener);
            Log.d("Test", builder.toSensorData().toCSV());
            Log.d("Test", "Stop sensor listening");
        }
    };

}
