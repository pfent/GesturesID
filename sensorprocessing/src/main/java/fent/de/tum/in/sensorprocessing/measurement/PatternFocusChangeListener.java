package fent.de.tum.in.sensorprocessing.measurement;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import fent.de.tum.in.sensorprocessing.OnPatternReceivedListener;

/**
 * Implements a OnFocusChangeListener to allow easy measurement of Sensor data from text input
 */
public class PatternFocusChangeListener extends PatternRecorder implements View.OnFocusChangeListener {

    /**
     * Contstruct the listener
     *
     * @param context    context to get the SensorManager
     * @param sensorType the sensor type from SensorManager.getSensorList(int)
     * @param callback   the callback to be called, when a new pattern has been completed
     */
    public PatternFocusChangeListener(Context context, int sensorType, OnPatternReceivedListener callback) {
        super(context, sensorType, callback);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ((EditText) v).setText(null);
            this.startListening();
            return;
        }

        this.stopListening();
        // Hide the keyboard
        InputMethodManager manager = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

    }
}

