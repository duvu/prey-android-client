package com.prey;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;

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
import com.prey.fragments.ActionFragment;
import com.prey.fragments.DeviceModelFragment;
import com.prey.fragments.MapsFragment;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by oso on 21-09-16.
 */

public class DeviceActivity extends Activity implements   GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        CapabilityApi.CapabilityListener,
        MessageApi.MessageListener{

    public static final String TAG = "PREY";

    private GridViewPager mPager;
    private DeviceModelFragment deviceModelFragment;
    private ActionFragment actionsFragment;
    private MapsFragment mapFragment;
    private int idDevice;
    private GoogleApiClient mGoogleApiClient;
    private String mPhoneNodeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        idDevice = b.getInt("id");
        Log.i("PREY","id:"+idDevice);

        setContentView(R.layout.activity_device);
        setupViews();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    private void setupViews() {
        mPager = (GridViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageCount(2);
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setDotSpacing((int) getResources().getDimension(R.dimen.dots_spacing));
        dotsPageIndicator.setPager(mPager);
        deviceModelFragment = new DeviceModelFragment();
        deviceModelFragment.idDevice=idDevice;
        actionsFragment = new ActionFragment();
        actionsFragment.idDevice=idDevice;
        actionsFragment.deviceActivity=this;
        mapFragment = new MapsFragment();
        List<Fragment> pages = new ArrayList<>();
        pages.add(deviceModelFragment);
        pages.add(actionsFragment);
        pages.add(mapFragment);
        final MyPagerAdapter adapter = new MyPagerAdapter(getFragmentManager(), pages);
        mPager.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();




        }
    }


    @Override
    protected void onPause() {
        Log.i(TAG, "onPause()");
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





    private class MyPagerAdapter extends FragmentGridPagerAdapter {

        private List<Fragment> mFragments;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount(int row) {
            return mFragments == null ? 0 : mFragments.size();
        }

        @Override
        public Fragment getFragment(int row, int column) {
            return mFragments.get(column);
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
                    Log.i(TAG, "mPhoneNodeId:"+mPhoneNodeId);

                } else {
                    Log.i(TAG, "Failed CapabilityApi result: "
                            + getCapabilityResult.getStatus());
                }
            }
        });
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

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended(): connection to location client suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed(): connection to location client failed");
    }

    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        Log.i(TAG, "onCapabilityChanged(): " + capabilityInfo);

        mPhoneNodeId = pickBestNodeId(capabilityInfo.getNodes());
    }

    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(TAG, "onMessageReceived(): " + messageEvent);

        String messagePath = messageEvent.getPath();

        if (messagePath.equals(Constants.MESSAGE_PATH_WEAR)) {

            DataMap dataMap = DataMap.fromByteArray(messageEvent.getData());
            int commType = dataMap.getInt(Constants.KEY_COMM_TYPE, 0);


        }
    }

    public void sendMessage(DataMap dataMap) {

        Log.i(TAG, "sendMessage(): " + dataMap);
        try {



            PendingResult<CapabilityApi.GetCapabilityResult> pendingCapabilityResult =
                    Wearable.CapabilityApi.getCapability(
                            mGoogleApiClient,
                            Constants.CAPABILITY_PHONE_APP,
                            CapabilityApi.FILTER_REACHABLE);

            CapabilityApi.GetCapabilityResult getCapabilityResult =
                    pendingCapabilityResult.await(
                            Constants.CONNECTION_TIME_OUT_MS,
                            TimeUnit.MILLISECONDS);

            if (!getCapabilityResult.getStatus().isSuccess()) {
                Log.i(TAG, "CapabilityApi failed to return any results.");
                mGoogleApiClient.disconnect();
                return;
            }

            CapabilityInfo capabilityInfo = getCapabilityResult.getCapability();
            String phoneNodeId = pickBestNodeId(capabilityInfo.getNodes());

            PendingResult<MessageApi.SendMessageResult> pendingMessageResult =
                    Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            phoneNodeId,
                            Constants.MESSAGE_PATH_PHONE,
                            dataMap.toByteArray());

            MessageApi.SendMessageResult sendMessageResult =
                    pendingMessageResult.await(Constants.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);

            if (!sendMessageResult.getStatus().isSuccess()) {
                Log.i(TAG, "Sending message failed, onResult: " + sendMessageResult.getStatus());
            } else {
                Log.i(TAG, "Message sent successfully");
            }


        }catch(Exception e){

        }
    }

}
