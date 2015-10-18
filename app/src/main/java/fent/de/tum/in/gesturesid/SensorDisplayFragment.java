package fent.de.tum.in.gesturesid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v4.app.Fragment;


import com.jjoe64.graphview.GraphView;


public class SensorDisplayFragment extends Fragment {

    GraphView[] graphs;
    ListView listView;

    public static SensorDisplayFragment newInstance() {
        return new SensorDisplayFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sensor_display, container, false);
        listView = (ListView) view.findViewById(R.id.sensorDisplayLayout);
        graphs = new GraphView[3];
        for (int i = 1; i < 3; i++) {
            graphs[i] = new GraphView(getActivity());
            listView.addView(graphs[i]);
        }
        return view;
    }

    public void displayData(SensorData data) {
        data.displayData(graphs);
    }

}
