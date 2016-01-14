package fent.de.tum.in.gesturesid.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fent.de.tum.in.gesturesid.R;
import fent.de.tum.in.sensorprocessing.OnPatternReceivedListener;
import fent.de.tum.in.sensorprocessing.measurement.PatternFocusChangeListener;
import fent.de.tum.in.sensorprocessing.measurement.SensorData;

public class NameInputFragment extends Fragment {

    private OnNameInputListener nameListener;
    private OnPatternReceivedListener patternListener;
    private TextView nameField;

    public static NameInputFragment newInstance() {
        return new NameInputFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_name_input, container, false);

        nameField = (TextView) view.findViewById(R.id.name);
        nameField.setOnFocusChangeListener(
                new PatternFocusChangeListener(getContext(), Sensor.TYPE_ACCELEROMETER, wrapper)
        );
        return view;
    }

    public void onButtonPressed(String name) {
        if (nameListener != null) {
            nameListener.onNameInput(name);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNameInputListener) {
            nameListener = (OnNameInputListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNameInputListener");
        }

        if (context instanceof OnPatternReceivedListener) {
            patternListener = (OnPatternReceivedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPatternReceivedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        nameListener = null;
        patternListener = null;
    }

    public interface OnNameInputListener {
        void onNameInput(String name);
    }

    private OnPatternReceivedListener wrapper = new OnPatternReceivedListener() {
        @Override
        public void OnPatternReceived(SensorData data) {
            nameListener.onNameInput(nameField.getText().toString());
            patternListener.OnPatternReceived(data);
        }
    };
}
