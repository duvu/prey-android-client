package com.prey;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.google.android.gms.wearable.Wearable;



import com.google.android.gms.wearable.Node;

import java.util.Set;
import java.util.concurrent.TimeUnit;



/**
 * Created by oso on 14-11-16.
 */

public class LoaderActivity extends Activity implements   GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        CapabilityApi.CapabilityListener,
        MessageApi.MessageListener{

    public static final String TAG = "PREY";
    private GoogleApiClient mGoogleApiClient;
    private String mPhoneNodeId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    @Override
    protected void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();

            new ListDevicesRemoteTask().execute();

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

    private void sendMessage(DataMap dataMap) {

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

    private class ListDevicesRemoteTask extends AsyncTask<String, Void, Void> {

        ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute ListDevicesRemoteTask");
        }

        @Override
        protected Void doInBackground(String... data) {
            Log.i(TAG, "doInBackground ListDevicesRemoteTask");
            DataMap dataMap = new DataMap();
            dataMap.putInt(Constants.KEY_COMM_TYPE,
                    Constants.COMM_TYPE_RESPONSE_LIST_DEVICES);
            sendMessage(dataMap);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.i(TAG, "onPostExecute ListDevicesRemoteTask");
        }

    }
}
