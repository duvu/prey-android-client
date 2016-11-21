package com.prey.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.google.android.gms.wearable.DataMap;

import com.prey.Constants;
import com.prey.DeviceActivity;
import com.prey.IncomingRequestWearService;
import com.prey.R;

import java.util.ArrayList;


/**
 * Created by oso on 22-09-16.
 */

public class ActionFragment extends Fragment  {



    public int idDevice;

    public static final String TAG = "PREY";
    public DeviceActivity deviceActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.actions_fragment, container, false);


        ImageView sound=(ImageView)view.findViewById(R.id.sound);

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DataMap> listMap= IncomingRequestWearService.listMap;
                String deviceId=listMap.get(idDevice).get("key");
                new ActionDeviceRemoteTask().execute(deviceId,"sound");
            }
        });

        ImageView alert=(ImageView)view.findViewById(R.id.alert);

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DataMap> listMap= IncomingRequestWearService.listMap;
                String deviceId=listMap.get(idDevice).get("key");
                new ActionDeviceRemoteTask().execute(deviceId,"alert");
            }
        });


        ImageView lock=(ImageView)view.findViewById(R.id.lock);

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DataMap> listMap= IncomingRequestWearService.listMap;
                String deviceId=listMap.get(idDevice).get("key");
                new ActionDeviceRemoteTask().execute(deviceId,"lock");
            }
        });

        ImageView trash=(ImageView)view.findViewById(R.id.trash);

        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DataMap> listMap= IncomingRequestWearService.listMap;
                String deviceId=listMap.get(idDevice).get("key");
                new ActionDeviceRemoteTask().execute(deviceId,"trash");
            }
        });





        return view;
    }




    private class ActionDeviceRemoteTask extends AsyncTask<String, Void, Void> {

        ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute ActionDeviceRemoteTask");
        }

        @Override
        protected Void doInBackground(String... data) {
            Log.i(TAG, "doInBackground ActionDeviceRemoteTask:"+data[0]);
            DataMap dataMap = new DataMap();
            dataMap.putInt(Constants.KEY_COMM_TYPE,
                    Constants.COMM_TYPE_RESPONSE_ACTION_DEVICE);
            dataMap.putString(Constants.ACTION_DEVICE,data[1]);
            dataMap.putString(Constants.DEVICE_ID,data[0]);
            deviceActivity.sendMessage(dataMap);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.i(TAG, "onPostExecute ActionDeviceRemoteTask");
        }

    }


}
