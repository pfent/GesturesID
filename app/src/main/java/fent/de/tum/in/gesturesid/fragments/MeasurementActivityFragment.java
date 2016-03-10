package fent.de.tum.in.gesturesid.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import fent.de.tum.in.gesturesid.R;
import fent.de.tum.in.sensorprocessing.OnPatternReceivedListener;
import fent.de.tum.in.sensorprocessing.measurement.PatternFocusChangeListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class MeasurementActivityFragment extends Fragment implements View.OnTouchListener {

    private OnPatternReceivedListener callback;
    private EditText patternPassword;
    private Context context;
    private PatternFocusChangeListener patternListener;

    public static MeasurementActivityFragment newInstance() {
        return new MeasurementActivityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurement, container, false);
        patternPassword = (EditText) view.findViewById(R.id.patternPassword);
        patternListener = new PatternFocusChangeListener(getContext(), Sensor.TYPE_ACCELEROMETER, callback);
        patternPassword.setOnFocusChangeListener(patternListener);
        patternPassword.setOnTouchListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnPatternReceivedListener) {
            callback = (OnPatternReceivedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNameInputListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (context instanceof TextWatcher) {
            patternPassword.addTextChangedListener((TextWatcher) context);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TextWatcher");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Drawable clearBtn = patternPassword.getCompoundDrawables()[2];
        int leftBoundary = patternPassword.getWidth() - clearBtn.getIntrinsicWidth();
        int rightBoundary = patternPassword.getWidth();
        int tappedX = (int) event.getX();
        int tappedY = (int) event.getY();

        if (tappedX > leftBoundary && tappedX < rightBoundary && event.getAction() == MotionEvent.ACTION_UP) {
            patternPassword.setText("");
            patternListener.clear();
        }

        return false;
    }
}
