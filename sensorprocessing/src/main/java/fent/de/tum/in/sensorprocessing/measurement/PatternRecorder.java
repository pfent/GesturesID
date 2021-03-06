package fent.de.tum.in.sensorprocessing.measurement;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import fent.de.tum.in.sensorprocessing.OnPatternReceivedListener;

/**
 * Created by philipp on 16.12.15.
 */
public class PatternRecorder {

    private final SensorManager manager;
    private final Sensor sensor;
    private final OnPatternReceivedListener callback;
    private SensorDataBuilder builder;
    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (builder == null) {
                builder = new SensorDataBuilder(event.values, System.nanoTime());
                return;
            }
            builder.append(event.values, System.nanoTime());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // NOP for now
        }
    };

    /**
     * @param context    context to get the SensorManager
     * @param sensorType the sensor type from SensorManager.getSensorList(int)
     * @param callback   the callback to be called, when a new pattern has been completed
     */
    public PatternRecorder(Context context, int sensorType, OnPatternReceivedListener callback) {
        this.manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.sensor = (manager.getDefaultSensor(sensorType));
        if (sensor == null) {
            throw new RuntimeException("No default sensor or no permissions for sensor");
        }
        this.callback = callback;
    }

    public void startListening() {
        builder = null;
        manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * Stops recoding of Sensordata and triggers a callback for the OnPatternReceivedListener
     */
    public void stopListening() {
        manager.unregisterListener(listener);
        callback.OnPatternReceived(builder.toSensorData());
    }

    public void clear() {
        builder.clear();
    }

}
