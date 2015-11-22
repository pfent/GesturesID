package fent.de.tum.in.gesturesid;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.CircularButton;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fent.de.tum.in.sensormeasurement.SensorDataBuilder;

public class MeasurementActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private CircularButton mButton;
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
        mTextView = (TextView) findViewById(R.id.text);
        mButton = (CircularButton) findViewById(R.id.button);


        manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensor = (manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    public void onButtonClick(View v) {
        // Perform action on click

        if (!isRecording) {
            isRecording = true;
            mButton.setColor(0x00FF0000);
            builder = null;
            manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            Log.d("Test", "Start sensor listening");
            return;
        }
        isRecording = false;
        mButton.setColor(0x0000FFFF);
        manager.unregisterListener(listener);
        Log.d("Test", builder.toSensorData().toCSV());
        Log.d("Test", "Stop sensor listening");
    }
}
