package fent.de.tum.in.sensorprocessing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import fent.de.tum.in.sensorprocessing.measurement.SensorDataBuilder;

/**
 * Implements a OnFocusChangeListener to allow easy measurement of Sensor data from text input
 */
public class PatternFocusChangeListener implements View.OnFocusChangeListener {

    private SensorDataBuilder builder;
    private final SensorManager manager;
    private final Sensor sensor;
    private final OnPatternReceivedListener callback;
    private final SensorEventListener listener = new SensorEventListener() {
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

    public PatternFocusChangeListener(Context context, int sensorType, OnPatternReceivedListener callback) {
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = (manager.getDefaultSensor(sensorType));
        this.callback = callback;
    }



    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ((EditText) v).setText(null);
            builder = null;
            manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            Log.d("Test", "EditText  got focus");
            return;
        }
        manager.unregisterListener(listener);
        callback.OnPatternReceived(builder.toSensorData());
        Log.d("Test", "EditText lost focus");

        // Hide the keyboard
        InputMethodManager manager = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

    }
}

