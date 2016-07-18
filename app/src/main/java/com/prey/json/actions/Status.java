/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.json.actions;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.prey.PreyLogger;
import com.prey.PreyPhone;
import com.prey.actions.HttpDataService;
import com.prey.actions.location.LocationUtil;
import com.prey.actions.observer.ActionResult;
import com.prey.actions.report.ReportService;
import com.prey.actions.status.StatusService;
import com.prey.json.JsonAction;
import com.prey.managers.PreyConnectivityManager;
import com.prey.net.PreyWebServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Status   {


    public  List<HttpDataService> get(final Context ctx, List<ActionResult> list, JSONObject parameters) {
        ArrayList<HttpDataService> dataToBeSent = new ArrayList<HttpDataService>();


        Intent intent = new Intent(ctx, StatusService.class);
        PreyLogger.d("________startService StatusService");

        ctx.startService(intent);


        return dataToBeSent;
    }



}
