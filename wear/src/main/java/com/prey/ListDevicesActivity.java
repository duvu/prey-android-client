package com.prey;




import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;


public class ListDevicesActivity extends Activity   implements WearableListView.ClickListener,


                GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        CapabilityApi.CapabilityListener,
        MessageApi.MessageListener
{

    private TextView mTextView;
    private WearableListView mWearableListView;
    private GoogleApiClient mGoogleApiClient;
    private String mPhoneNodeId;
    public static final String TAG = "PREY";


    public static final int NUMBER_OF_TIMES = 4;

    private ListViewItem[] listDevicesItems = new ListViewItem[NUMBER_OF_TIMES];


    private String deviceNames[]=new String[]{
            "Motorola XT1563",
            "Samsung SM-G900M",
            "Asus Nexus 7","Oso MacBook"};

    private int deviceIds[]=new int[]{
            R.drawable.android_generic2,
            R.drawable.android_phone2,
            R.drawable.android_tablet2,
            R.drawable.windows_laptop2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.devices_list);


        for (int i = 0; i < NUMBER_OF_TIMES; i++) {
            listDevicesItems[i] = new ListViewItem(deviceNames[i],
                    (i + 1) * 60 * 1000);
        }

        mWearableListView = (WearableListView) findViewById(R.id.times_list_view);
        mWearableListView.setAdapter(new DeviceItemWearableListViewAdapter(this));
        mWearableListView.setClickListener(this);

        /*
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });*/

/*
        int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d("TEST", "GPS GRANTED");
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},
                    0);


        }*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        if ((mGoogleApiClient != null) && mGoogleApiClient.isConnected()) {
            Wearable.CapabilityApi.removeCapabilityListener(
                    mGoogleApiClient,
                    this,
                    Constants.CAPABILITY_PHONE_APP);
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected()...");

        // Set up listeners for capability and message changes.
        Wearable.CapabilityApi.addCapabilityListener(
                mGoogleApiClient,
                this,
                Constants.CAPABILITY_PHONE_APP);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Log.i(TAG, "addListener()");
        // Initial check of capabilities to find the phone.
        PendingResult<CapabilityApi.GetCapabilityResult> pendingResult =
                Wearable.CapabilityApi.getCapability(
                        mGoogleApiClient,
                        Constants.CAPABILITY_PHONE_APP,
                        CapabilityApi.FILTER_REACHABLE);

        pendingResult.setResultCallback(new ResultCallback<CapabilityApi.GetCapabilityResult>() {
            @Override
            public void onResult(CapabilityApi.GetCapabilityResult getCapabilityResult) {
                Log.i(TAG, "setResultCallback()");
                if (getCapabilityResult.getStatus().isSuccess()) {
                    CapabilityInfo capabilityInfo = getCapabilityResult.getCapability();
                    Log.i(TAG, "capabilityInfo.getNodes():"+capabilityInfo.getNodes());
                    mPhoneNodeId = pickBestNodeId(capabilityInfo.getNodes());

                } else {
                    Log.d(TAG, "Failed CapabilityApi result: "
                            + getCapabilityResult.getStatus());
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended(): connection to location client suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed(): connection to location client failed");
    }

    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        Log.d(TAG, "onCapabilityChanged(): " + capabilityInfo);

        mPhoneNodeId = pickBestNodeId(capabilityInfo.getNodes());
    }

    private String pickBestNodeId(Set<Node> nodes) {

        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily.
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }


    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived(): " + messageEvent);

        String messagePath = messageEvent.getPath();

        if (messagePath.equals(Constants.MESSAGE_PATH_WEAR)) {

            DataMap dataMap = DataMap.fromByteArray(messageEvent.getData());
            int commType = dataMap.getInt(Constants.KEY_COMM_TYPE, 0);


        }
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
            img.setImageResource(deviceIds[position]);
        }

        @Override
        public int getItemCount() {
            return NUMBER_OF_TIMES;
        }



    }
}
