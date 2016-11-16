package com.prey.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;
import com.prey.IncomingRequestWearService;
import com.prey.R;

import java.util.ArrayList;

/**
 * Created by oso on 22-09-16.
 */

public class DeviceModelFragment extends Fragment {

    // private DataItemAdapter mDataItemListAdapter;
    private TextView mIntroText;
    private boolean mInitialized;
    public int idDevice;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_model_fragment, container, false);

        ImageView imageDevice=(ImageView) view.findViewById(R.id.image_device);


        ArrayList<DataMap> listMap= IncomingRequestWearService.listMap;
        String icon=listMap.get(idDevice).get("icon");
        String name=listMap.get(idDevice).get("name");
        String description=listMap.get(idDevice).get("description");
        String client_version=listMap.get(idDevice).get("client_version");
        String state=listMap.get(idDevice).get("state");
        if (icon.equals("mac-laptop")){
            imageDevice.setImageResource(R.drawable.windows_laptop2);
        }else{
            if(icon.equals("android-phone")){
                imageDevice.setImageResource(R.drawable.android_generic2);
            }else{
                imageDevice.setImageResource(R.drawable.windows_laptop2);
            }
        }


        TextView textDeviceName=(TextView)view.findViewById(R.id.device_name);
        textDeviceName.setText(name);

        TextView textDeviceOs=(TextView)view.findViewById(R.id.device_os);
        textDeviceOs.setText(description);

        TextView textDeviceVersion=(TextView)view.findViewById(R.id.device_version);
        textDeviceVersion.setText(client_version);
        TextView textDeviceState=(TextView)view.findViewById(R.id.device_estado);
        textDeviceState.setText(client_version);

        /*
        View view = inflater.inflate(R.layout.data_fragment, container, false);
        ListView dataItemList = (ListView) view.findViewById(R.id.dataItem_list);
        mIntroText = (TextView) view.findViewById(R.id.intro);
        mDataItemListAdapter = new DataItemAdapter(getActivity(),
                android.R.layout.simple_list_item_1);
        dataItemList.setAdapter(mDataItemListAdapter);
        mInitialized = true;
        */
        return view;
    }
/*
    public void appendItem(String title, String text) {
        if (!mInitialized) {
            return;
        }
        mIntroText.setVisibility(View.INVISIBLE);
        mDataItemListAdapter.add(new Event(title, text));
    }

    private static class DataItemAdapter extends ArrayAdapter<Event> {

        private final Context mContext;

        public DataItemAdapter(Context context, int unusedResource) {
            super(context, unusedResource);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(android.R.layout.two_line_list_item, null);
                convertView.setTag(holder);
                holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Event event = getItem(position);
            holder.text1.setText(event.title);
            holder.text2.setText(event.text);
            return convertView;
        }

        private class ViewHolder {

            TextView text1;
            TextView text2;
        }
    }

    private class Event {

        String title;
        String text;

        public Event(String title, String text) {
            this.title = title;
            this.text = text;
        }
    }*/
}
