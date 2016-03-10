package fent.de.tum.in.gesturesid.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fent.de.tum.in.gesturesid.R;

public class EvaluationFinishedFragment extends Fragment {
    private static final String ARG_COMPUTE_TIME = "computeTime";
    private static final String ARG_DB_LOCATION = "dbLocation";

    private long computeTime;
    private String userIdentificationString;


    public EvaluationFinishedFragment() {
        // Required empty public constructor
    }

    public static EvaluationFinishedFragment newInstance(long computeTime, String userIdentificationString) {
        EvaluationFinishedFragment fragment = new EvaluationFinishedFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_COMPUTE_TIME, computeTime);
        args.putString(ARG_DB_LOCATION, userIdentificationString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            computeTime = getArguments().getLong(ARG_COMPUTE_TIME);
            userIdentificationString = getArguments().getString(ARG_DB_LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_evaluation_finished, container, false);
        TextView textView = (TextView) view.findViewById(R.id.titleView);

        String text = String.format(getString(R.string.evaluationresult), computeTime, userIdentificationString);
        textView.setText(text);

        return view;
    }

}
