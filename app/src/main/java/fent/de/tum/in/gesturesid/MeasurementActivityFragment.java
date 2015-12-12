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

import fent.de.tum.in.sensorprocessing.OnPatternReceivedListener;
import fent.de.tum.in.sensorprocessing.PatternFocusChangeListener;
import fent.de.tum.in.sensorprocessing.measurement.SensorDataBuilder;

/**
 * A placeholder fragment containing a simple view.
 */
public class MeasurementActivityFragment extends Fragment {

    private OnPatternReceivedListener callback;

    public static MeasurementActivityFragment newInstance(OnPatternReceivedListener callback) {
        MeasurementActivityFragment result = new MeasurementActivityFragment();
       result.callback = callback;
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurement, container, false);
        EditText patternPassword = (EditText) view.findViewById(R.id.patternPassword);
        patternPassword.setOnFocusChangeListener(
                new PatternFocusChangeListener(getContext(), Sensor.TYPE_ACCELEROMETER, callback)
        );
        return view;
    }
}
