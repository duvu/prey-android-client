/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.json.actions;

import java.util.List;

import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.actions.HttpDataService;
import com.prey.actions.observer.ActionResult;
import com.prey.json.JsonAction;
import com.prey.json.UtilJson;
import com.prey.net.PreyWebServices;

public class Camouflage extends JsonAction {

    public HttpDataService run(Context ctx, List<ActionResult> list, JSONObject parameters) {
        return null;
    }

    public void start(Context ctx, List<ActionResult> list, JSONObject parameters) {
        String messageId = null;
        try {
            messageId = parameters.getString(PreyConfig.MESSAGE_ID);
            PreyLogger.d("messageId:"+messageId);
        } catch (Exception e) {
        }
        String reason = null;
        try {
            String jobId = parameters.getString(PreyConfig.JOB_ID);
            PreyLogger.d("jobId:"+jobId);
            if(jobId!=null&&!"".equals(jobId)){
                reason="{\"device_job_id\":\""+jobId+"\"}";
            }
        } catch (Exception e) {
        }
        PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, "processed", messageId, UtilJson.makeMapParam("start", "camouflage", "started",reason));
        PreyConfig.getPreyConfig(ctx).setCamouflageSet(true);

        ComponentName componentToDisabled = new ComponentName("com.prey", "com.prey.activities.LoginActivity");
        PackageManager pm = ctx.getPackageManager();
        pm.setComponentEnabledSetting(componentToDisabled, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        PreyConfig.getPreyConfig(ctx).setLastEvent("camouflage_start");
    }

    public void stop(Context ctx, List<ActionResult> list, JSONObject parameters) {
        String messageId = null;
        try {
            messageId = parameters.getString(PreyConfig.MESSAGE_ID);
            PreyLogger.d("messageId:"+messageId);
        } catch (Exception e) {
        }
        String reason = null;
        try {
            String jobId = parameters.getString(PreyConfig.JOB_ID);
            PreyLogger.d("jobId:"+jobId);
            if(jobId!=null&&!"".equals(jobId)){
                reason="{\"device_job_id\":\""+jobId+"\"}";
            }
        } catch (Exception e) {
        }
        PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, "processed", messageId, UtilJson.makeMapParam("stop", "camouflage", "stopped",reason));
        PreyConfig.getPreyConfig(ctx).setCamouflageSet(false);

        ComponentName componentToEnabled = new ComponentName("com.prey", "com.prey.activities.LoginActivity");
        PackageManager pm = ctx.getApplicationContext().getPackageManager();
        pm.setComponentEnabledSetting(componentToEnabled, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        PreyConfig.getPreyConfig(ctx).setLastEvent("camouflage_stop");
    }
}
