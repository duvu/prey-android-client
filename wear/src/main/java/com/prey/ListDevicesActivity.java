package com.prey;




import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;


public class ListDevicesActivity extends Activity   implements WearableListView.ClickListener



{

    private TextView mTextView;
    private WearableListView mWearableListView;


    public static final String TAG = "PREY";

    public static int NUMBER_OF_TIMES;

    private ListViewItem[] listDevicesItems  ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.devices_list);

        ArrayList<DataMap> listMap=IncomingRequestWearService.listMap;
        NUMBER_OF_TIMES=listMap.size();
        listDevicesItems = new ListViewItem[NUMBER_OF_TIMES];

        for (int i=0;listMap!=null&i<listMap.size();i++){
            DataMap map=listMap.get(i);
            String deviceName= map.getString("name");
            listDevicesItems[i] = new ListViewItem(deviceName,
                    (i + 1) * 60 * 1000);

        }


        mWearableListView = (WearableListView) findViewById(R.id.times_list_view);
        mWearableListView.setAdapter(new DeviceItemWearableListViewAdapter(this));
        mWearableListView.setClickListener(this);





    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause()");
        super.onPause();

    }












    @Override
    public void onClick(WearableListView.ViewHolder holder) {
        long duration = listDevicesItems[holder.getPosition()].duration;
        Log.i(TAG, "posicion: " + holder.getPosition());

        Intent startIntent = new Intent(this, DeviceActivity.class);
        startIntent.putExtra("id", holder.getPosition());
        startActivity(startIntent);


    }
    @Override
    public void onTopEmptyRegionClick() {
    }

    /** Model class for the listview. */
    private static class ListViewItem {

        // Duration in milliseconds.
        long duration;
        // Label to display.
        private String label;

        public ListViewItem(String label, long duration) {
            this.label = label;
            this.duration = duration;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final class DeviceItemWearableListViewAdapter extends WearableListView.Adapter {
        private final Context mContext;
        private final LayoutInflater mInflater;

        private DeviceItemWearableListViewAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);

        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            WearableListView.ViewHolder holders= new WearableListView.ViewHolder(
                    mInflater.inflate(R.layout.device_item, null));



            return holders;
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.time_text);
            view.setText(listDevicesItems[position].label);
            holder.itemView.setTag(position);

            ImageView img=(ImageView) holder.itemView.findViewById(R.id.circle);

            ArrayList<DataMap> listMap=IncomingRequestWearService.listMap;
            String icon=listMap.get(position).get("icon");



            if (icon.equals("mac-laptop")){
                    img.setImageResource(R.drawable.windows_laptop2);
                }else{
                    if(icon.equals("android-phone")){
                        img.setImageResource(R.drawable.android_generic2);
                    }else{
                        img.setImageResource(R.drawable.windows_laptop2);
                    }
            }





        }

        @Override
        public int getItemCount() {
            return NUMBER_OF_TIMES;
        }



    }






}
