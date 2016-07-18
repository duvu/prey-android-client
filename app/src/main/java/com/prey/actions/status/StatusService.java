/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.actions.status;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.prey.PreyLogger;
import com.prey.PreyPhone;
import com.prey.actions.HttpDataService;
import com.prey.actions.battery.Battery;
import com.prey.net.PreyWebServices;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StatusService extends IntentService {

    public StatusService() {
        super("StatusService");
    }

    public StatusService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PreyLogger.d("Ejecuting Status Data.");

        final Context ctx = this.getApplicationContext();

        PreyPhone preyPhone=new PreyPhone(ctx);
        preyPhone.updateAll();
        try{
            Thread.sleep(1000);}
        catch (Exception e){};

        HttpDataService data= new HttpDataService("status");


        HashMap<String, String> parametersMap = null;
        ArrayList<HttpDataService> dataToBeSent = new ArrayList<HttpDataService>();


        HttpDataService dataInfo= new HttpDataService("info");
        dataInfo.setList(true);
        parametersMap = new HashMap<String, String>();
        parametersMap.put("device_model", preyPhone.getHardware().getDeviceModel());
        parametersMap.put("operation_system", preyPhone.getHardware().getOsVersion());
        dataInfo.addDataListAll(parametersMap);
        dataToBeSent.add(dataInfo);

        HttpDataService dataNetwork = new HttpDataService("network");
        dataNetwork.setList(true);
        parametersMap = new HashMap<String, String>();
        parametersMap.put("mac_address",  preyPhone.getWifi().getMacAddress());
        parametersMap.put("ip_address", preyPhone.getExternalIp());
        parametersMap.put("local_ip_address", preyPhone.getWifi().getIpAddress());
        parametersMap.put("connection",  preyPhone.getWifi().getSecurity());
        parametersMap.put("wifi_signal",  preyPhone.getWifi().getSignalStrength());
        parametersMap.put("mobile_signal", ""+preyPhone.getMobile().getMobileSignal());
        parametersMap.put("wifi_ssid", preyPhone.getWifi().getSsid());
        parametersMap.put("mobile_network", preyPhone.getMobile().getOperatorName());
        dataNetwork.addDataListAll(parametersMap);
        dataToBeSent.add(dataNetwork);


        HttpDataService dataMemory = new HttpDataService("memory");
        dataMemory.setList(true);
        parametersMap = new HashMap<String, String>();
        parametersMap.put("program_free", bytesToHuman(preyPhone.getHardware().getFreeMemory()));
        parametersMap.put("program_total", bytesToHuman(preyPhone.getHardware().getTotalMemory()));
        parametersMap.put("storage_free", bytesToHuman(preyPhone.getHardware().getFreeStorage()));
        parametersMap.put("storage_total", bytesToHuman(preyPhone.getHardware().getTotalStorage()));
        dataMemory.addDataListAll(parametersMap);
        dataToBeSent.add(dataMemory);



        boolean connected=preyPhone.getBattery().isConnected();
        String plugged="";
        if(preyPhone.getBattery().isAcCharge()){
            plugged="ac";
        }
        if(preyPhone.getBattery().isUsbCharge()){
            plugged="usb";
        }

        HttpDataService dataBattery = new HttpDataService("battery");
        dataBattery.setList(true);
        parametersMap = new HashMap<String, String>();
        parametersMap.put("main_battery", ""+preyPhone.getBattery().getPercentage());
        parametersMap.put("backup_battery", "not_present");
        parametersMap.put("power_connected", ""+connected);
        parametersMap.put("power_status", preyPhone.getBattery().isCharging()?"charging":"charged");
        parametersMap.put("power_plugged" , plugged);


        dataBattery.addDataListAll(parametersMap);
        dataToBeSent.add(dataBattery);


        PreyLogger.i("_______________________");
        Iterator<String> ite= parametersMap.keySet().iterator();
        while(ite.hasNext()){
            String key=ite.next();
            PreyLogger.i("["+key+"]"+parametersMap.get(key));

        }

        data.setList(true);
        data.addDataListAll(parametersMap);
        dataToBeSent.add(data);
        PreyWebServices.getInstance().sendPreyHttpData(ctx, dataToBeSent);

    }

    public static String bytesToHuman (long size) {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return floatForm(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " Kb";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " Mb";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " Gb";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " Tb";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " Pb";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " Eb";

        return "???";
    }

    public static String floatForm (double d) {
        return new DecimalFormat("#.##").format(d);
    }


/*
        ConnectivityManager mConnectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        PreyLogger.i("info:"+info.toString());

        // info:[type: MOBILE[LTE], state: CONNECTED/CONNECTED, reason: (unspecified), extra: (none), roaming: false, failover: false, isAvailable: true]


        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        PreyLogger.i("info:"+telephonyManager.toString());
        String carrierName = telephonyManager.getNetworkOperatorName();
        PreyLogger.i("carrierName:"+carrierName);



/*
        CustomPhoneStateListener listener=new CustomPhoneStateListener();

        try {
            Looper.prepare();
             telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            Looper.loop();








        }catch(Exception e){
            PreyLogger.e("Error:"+e.getMessage(),e);
        }


        PreyPhone preyPhone = new PreyPhone(ctx);

        HttpDataService data= new HttpDataService("status");

        parametersMap = new HashMap<String, String>();
        parametersMap.put("info][device_model", "XT1324");
        parametersMap.put("info][operation_system", "MARSHMALLOW/Android");



        WifiManager wifiMgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

        PreyLogger.i("wifiInfo:"+wifiInfo.toString());
        int ipAddress = wifiInfo.getIpAddress();
        PreyLogger.i("ipAddress:"+formatterIp(ipAddress));
        String ssid=wifiInfo.getSSID();
        PreyLogger.i("ssid:"+ssid);



        List<ScanResult> networkList = wifiMgr.getScanResults();



        int ip = wifiInfo.getIpAddress();
        String localIp = formatterIp(ip);


        WifiInfo wi = wifiMgr.getConnectionInfo();
        String currentSSID = wi.getSSID();
        currentSSID=currentSSID.replace("\"","");
        int signalStrangth= 0;
        String secure="";
        int state = wifiMgr.getWifiState();
        PreyLogger.i("state wifi:"+state);
        if(state == WifiManager.WIFI_STATE_ENABLED) {
            if (networkList != null) {
                for (ScanResult network : networkList) {
                    //PreyLogger.i("currentSSID:"+currentSSID+ " network.SSID : " + network.SSID);

                    if (currentSSID.equals(network.SSID)) {
                        //get capabilities of current connection
                        String capabilities = network.capabilities;
                        PreyLogger.i("wifi_:"+network.SSID + " capabilities : " + capabilities);
                        int level = WifiManager.calculateSignalLevel(wifiMgr.getConnectionInfo().getRssi(),
                                network.level);
                        int difference = level * 100 / network.level;
                        signalStrangth= 0;
                        if(difference >= 100)
                            signalStrangth = 4;
                        else if(difference >= 75)
                            signalStrangth = 3;
                        else if(difference >= 50)
                            signalStrangth = 2;
                        else if(difference >= 25)
                            signalStrangth = 1;
                        PreyLogger.i("wifi Difference :" + difference + " signal state:" + signalStrangth);

                        secure=getSecurity(network);


                    }
                }
            }
        }






        String externalIp="";
        try {
            externalIp = PreyWebServices.getInstance().getExternalIp(ctx);
        }catch(Exception e){}
        PreyLogger.i("externalIp:"+externalIp);




        PreyPhone preyPhone1=new PreyPhone(getApplicationContext());
        PreyLogger.i("MyMemory total:"+ (preyPhone1.getHardware().getTotalMemory()));
        PreyLogger.i("MyMemory free:"+ (preyPhone1.getHardware().getFreeMemory()));
        PreyLogger.i("MyMemory busy:"+ (preyPhone1.getHardware().getBusyMemory()));






        parametersMap.put("network][mac_address", wifiInfo.getMacAddress());
        parametersMap.put("network][ip_address", externalIp);
        parametersMap.put("network][local_ip_address", localIp);
        parametersMap.put("network][connection", secure);
        parametersMap.put("network][wifi_signal", ""+signalStrangth);
        parametersMap.put("network][mobile_signal",""+signalStrengthValue);
        parametersMap.put("network][wifi_ssid", ssid);
        parametersMap.put("network][mobile_network", carrierName);





        MyBatteryBroadcastReceiver batteryLevelReceiver = new MyBatteryBroadcastReceiver();

        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        try{Thread.sleep(2000);}catch (Exception e){}
        Battery battery=batteryLevelReceiver.getBattery();
        PreyLogger.i("MyBattery Plugged:"+battery.getPlugged());
        PreyLogger.i("MyBattery status:"+battery.getStatus());
        PreyLogger.i("MyBattery Voltage:"+battery.getVoltage());
        PreyLogger.i("MyBattery Health:"+battery.getHealth());


        parametersMap.put("battery][main_battery", "58%");
        parametersMap.put("battery][backup_battery", "not_present");
        parametersMap.put("battery][power_connected", "true");
        parametersMap.put("battery][power_status", "charging");
        parametersMap.put("battery][power_plugged", "usb");


        parametersMap.put("memory][program_free", "681.1 MB");
        parametersMap.put("memory][program_total", "1.9 GB");
        parametersMap.put("memory][storage_free", "1.5 GB");
        parametersMap.put("memory][storage_total", "11 GB");


        PreyLogger.i("BusySpaceInMB:"+bytesToHuman(busyMemory()));
        PreyLogger.i("FreeSpaceInMB:"+bytesToHuman(freeMemory()));
        PreyLogger.i("TotalSpaceInMB:"+bytesToHuman(totalMemory()));

        PreyLogger.i("_______________________");
        Iterator<String> ite= parametersMap.keySet().iterator();
        while(ite.hasNext()){
            String key=ite.next();
            PreyLogger.i("["+key+"]"+parametersMap.get(key));

        }

        data.setList(true);
        data.addDataListAll(parametersMap);
        dataToBeSent.add(data);
        PreyWebServices.getInstance().sendPreyHttpData(ctx, dataToBeSent);

    }



    private String getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return "WEP";
        } else if (result.capabilities.contains("PSK")) {
            return "PSK";
        } else if (result.capabilities.contains("EAP")) {
            return "EAP";
        }
        return "NONE";
    }

    */



}



