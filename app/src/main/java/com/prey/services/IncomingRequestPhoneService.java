package com.prey.services;



import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;



import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.prey.Constants;
import com.prey.PreyLogger;
import com.prey.net.PreyWebServices;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Handles all incoming requests for phone data (and permissions) from wear devices.
 */
public class IncomingRequestPhoneService extends WearableListenerService {

    private static final String TAG = "PREY";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.i(TAG, "IncomingRequestPhoneService onMessageReceived(): " + messageEvent);

        String messagePath = messageEvent.getPath();

        if (messagePath.equals(Constants.MESSAGE_PATH_PHONE)) {

            DataMap dataMap = DataMap.fromByteArray(messageEvent.getData());
            int requestType = dataMap.getInt(Constants.KEY_COMM_TYPE, 0);

            if (requestType == Constants.COMM_TYPE_REQUEST_PROMPT_PERMISSION) {
                promptUserForStoragePermission(messageEvent.getSourceNodeId());

            } else if (requestType == Constants.COMM_TYPE_REQUEST_DATA) {
                respondWithStorageInformation(messageEvent.getSourceNodeId());
            } else if (requestType == Constants.COMM_TYPE_RESPONSE_LIST_DEVICES) {
                DataMap outgoingDataRequestDataMap = new DataMap();
                outgoingDataRequestDataMap.putInt(Constants.KEY_COMM_TYPE,
                        Constants.COMM_TYPE_REQUEST_DATA_LIST);


                ArrayList<DataMap> listMap= PreyWebServices.getInstance().devicesListMap(this);
                outgoingDataRequestDataMap.putDataMapArrayList(Constants.LIST_DEVICE,listMap);
                sendMessage(messageEvent.getSourceNodeId(),outgoingDataRequestDataMap);

            } else if (requestType == Constants.COMM_TYPE_RESPONSE_ACTION_DEVICE) {
                DataMap outgoingDataRequestDataMap = new DataMap();

                String action=dataMap.getString(Constants.ACTION_DEVICE);
                String deviceId=dataMap.getString(Constants.DEVICE_ID);
                PreyLogger.i("deviceId:"+deviceId+" action:"+action);
                PreyWebServices.getInstance().sendAction(this,deviceId,action);

            }
        }
    }

    private void promptUserForStoragePermission(String nodeId) {
        boolean storagePermissionApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;

        if (storagePermissionApproved) {
            DataMap dataMap = new DataMap();
            dataMap.putInt(Constants.KEY_COMM_TYPE,
                    Constants.COMM_TYPE_RESPONSE_USER_APPROVED_PERMISSION);
            sendMessage(nodeId, dataMap);
        } else {

        }
    }

    private void respondWithStorageInformation(String nodeId) {

        boolean storagePermissionApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;

        if (!storagePermissionApproved) {
            DataMap dataMap = new DataMap();
            dataMap.putInt(Constants.KEY_COMM_TYPE,
                    Constants.COMM_TYPE_RESPONSE_PERMISSION_REQUIRED);
            sendMessage(nodeId, dataMap);
        } else {
            /* To keep the sample simple, we are only displaying the top level list of directories.
             * Otherwise, it will return a message that the media wasn't available.
             */
            StringBuilder stringBuilder = new StringBuilder();

            if (isExternalStorageReadable()) {
                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                String[] fileList = externalStorageDirectory.list();

                if (fileList.length > 0) {
                    stringBuilder.append("List of directories on phone:\n");
                    for (String file : fileList) {
                        stringBuilder.append(" - " + file + "\n");
                    }
                } else {
                    stringBuilder.append("No files in external storage.");
                }
            } else {
                stringBuilder.append("No external media is available.");
            }

            // Send valid results
            DataMap dataMap = new DataMap();
            dataMap.putInt(Constants.KEY_COMM_TYPE,
                    Constants.COMM_TYPE_RESPONSE_DATA);
            dataMap.putString(Constants.KEY_PAYLOAD, stringBuilder.toString());
            sendMessage(nodeId, dataMap);

        }
    }

    private void sendMessage(String nodeId, DataMap dataMap) {
        Log.i(TAG, "sendMessage() Node: " + nodeId);

        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        client.blockingConnect(Constants.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);


        PendingResult<MessageApi.SendMessageResult> pendingMessageResult =
                Wearable.MessageApi.sendMessage(
                        client,
                        nodeId,
                        Constants.MESSAGE_PATH_WEAR,
                        dataMap.toByteArray());

        MessageApi.SendMessageResult sendMessageResult =
                pendingMessageResult.await(
                        Constants.CONNECTION_TIME_OUT_MS,
                        TimeUnit.MILLISECONDS);

        if (!sendMessageResult.getStatus().isSuccess()) {
            Log.i(TAG, "Sending message failed, status: "
                    + sendMessageResult.getStatus());
        } else {
            Log.i(TAG, "Message sent successfully");
        }
        client.disconnect();
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}