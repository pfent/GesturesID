package fent.de.tum.in.gesturesid;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import fent.de.tum.in.sensormeasurement.SensorDataBuilder;

/**
 * A placeholder fragment containing a simple view.
 */
public class MeasurementActivityFragment extends Fragment {

    OnPatternReceivedListener callback;


    public static MeasurementActivityFragment newInstance() {
        return new MeasurementActivityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurement, container, false);
        EditText patternPassword = (EditText) view.findViewById(R.id.patternPassword);
        patternPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            SensorDataBuilder builder;
            SensorManager manager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = (manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
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
        });
        return view;
    }

    public void setOnPatternReceivedListener(OnPatternReceivedListener listener) {
        this.callback = listener;
    }
}
