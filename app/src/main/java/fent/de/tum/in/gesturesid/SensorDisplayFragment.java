package fent.de.tum.in.gesturesid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.support.v4.app.Fragment;


import com.jjoe64.graphview.GraphView;

import fent.de.tum.in.sensormeasurement.SensorData;


public class SensorDisplayFragment extends Fragment {

    SensorData data = new SensorData(new float[0][0]);
    GraphView[] graphs = new GraphView[0];
    ListView listView;
    BaseAdapter adapter;

    public static SensorDisplayFragment newInstance() {
        return new SensorDisplayFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sensor_display, container, false);
        listView = (ListView) view.findViewById(R.id.sensorDisplayLayout);
        adapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return data.getDimension();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                GraphView gView;
                if (convertView == null) {
                    gView = (GraphView) LayoutInflater.from(getActivity()).inflate(R.layout.sensor_display_view, parent, false);
                } else {
                    gView = (GraphView) convertView;
                }

                //data.displayData(gView, position);
                return gView;
            }
        };

        listView.setAdapter(adapter);

        return view;
    }

    public void displayData(SensorData data) {
        this.data = data;
        adapter.notifyDataSetChanged();
    }

}
