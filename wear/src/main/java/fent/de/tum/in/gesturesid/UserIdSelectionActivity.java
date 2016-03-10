package fent.de.tum.in.gesturesid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import fent.de.tum.in.sensorprocessing.MeasurementManager;

public class UserIdSelectionActivity extends Activity implements WearableListView.ClickListener {

    private static final int USERS = 42;
    private final WearableListView.Adapter adapter = new WearableListView.Adapter() {
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inf = LayoutInflater.from(UserIdSelectionActivity.this);
            return new WearableListView.ViewHolder(inf.inflate(android.R.layout.simple_list_item_1, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(android.R.id.text1);
            int userID = position + 1;
            view.setText("User " + userID);
            holder.itemView.setTag(userID);
        }

        @Override
        public int getItemCount() {
            return USERS;
        }
    };
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_id_selection);

        MeasurementManager manager = MeasurementManager.getInstance(this);
        for (int i = 1; i <= USERS; i++) {
            manager.createUser("dummy" + Integer.toString(i));
        }

        WearableListView listView = (WearableListView) findViewById(R.id.wearable_list);
        listView.setAdapter(adapter);
        listView.setClickListener(this);
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        long userID = (Integer) viewHolder.itemView.getTag();
        Intent i = new Intent(this, MeasurementActivity.class);
        i.putExtra(MeasurementActivity.USER_ID, userID);
        startActivity(i);
    }

    @Override
    public void onTopEmptyRegionClick() {
        // NOP
    }
}
