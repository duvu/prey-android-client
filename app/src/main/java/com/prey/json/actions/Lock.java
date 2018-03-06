/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.json.actions;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.PreyPermission;
import com.prey.actions.HttpDataService;
import com.prey.actions.observer.ActionResult;
import com.prey.backwardcompatibility.FroyoSupport;
import com.prey.exceptions.PreyException;
import com.prey.json.JsonAction;
import com.prey.json.UtilJson;
import com.prey.net.PreyWebServices;
import com.prey.services.PreyLockService;

public class Lock extends JsonAction {

    public HttpDataService run(Context ctx, List<ActionResult> list, JSONObject parameters) {
        return null;
    }


    public void start(Context ctx, List<ActionResult> list, JSONObject parameters) {
        try {
            String messageId = null;
            try {
                messageId = parameters.getString(PreyConfig.MESSAGE_ID);
                PreyLogger.d("messageId:"+messageId);
            } catch (Exception e) {
            }
            String reason=null;
            String jobId =null;
            try {
                jobId = parameters.getString(PreyConfig.JOB_ID);
                PreyLogger.d("jobId:"+jobId);
                reason="{\"device_job_id\":\""+jobId+"\"}";
                PreyConfig.getPreyConfig(ctx).setJobIdLock(jobId);
            } catch (Exception e) {
            }
            String unlock = null;
            try {
                unlock = parameters.getString(PreyConfig.UNLOCK_PASS);
                PreyConfig.getPreyConfig(ctx).setUnlockPass(unlock);
            } catch (Exception e) {
            }
            lock(ctx, unlock, messageId, reason,jobId);
        } catch (Exception e) {
            PreyLogger.e("Error:" + e.getMessage() + e.getMessage(), e);
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "lock", "failed", e.getMessage()));
        }
    }

    public void stop(Context ctx, List<ActionResult> list, JSONObject parameters) {
        try {
            String messageId = null;
            try {
                messageId = parameters.getString(PreyConfig.MESSAGE_ID);
                PreyLogger.d("messageId:"+messageId);
            } catch (Exception e) {
            }
            String reason=null;
            try {
                String jobId = parameters.getString(PreyConfig.JOB_ID);
                PreyLogger.d("jobId:"+jobId);
                reason="{\"device_job_id\":\""+jobId+"\"}";

            } catch (Exception e) {
            }
            String jobIdLock=PreyConfig.getPreyConfig(ctx).getJobIdLock();
            if(jobIdLock!=null&&!"".equals(jobIdLock)){
                reason="{\"device_job_id\":\""+jobIdLock+"\"}";
                PreyConfig.getPreyConfig(ctx).setJobIdLock("");
            }
            PreyConfig.getPreyConfig(ctx).setLock(false);
            PreyConfig.getPreyConfig(ctx).deleteUnlockPass();
            if(PreyConfig.getPreyConfig(ctx).isMarshmallowOrAbove() && PreyPermission.canDrawOverlays(ctx)) {
                Thread.sleep(3000);
                PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, "processed",messageId,UtilJson.makeMapParam("start", "lock", "stopped",reason));

                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
            }else{
                PreyLogger.d("-- Unlock instruction received");
                try{
                    FroyoSupport.getInstance(ctx).changePasswordAndLock("", true);
                    WakeLock screenLock = ((PowerManager) ctx.getSystemService(Context.POWER_SERVICE)).newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, PreyConfig.TAG);
                    screenLock.acquire();
                    screenLock.release();
                    Thread.sleep(3000);
                    PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "lock", "stopped",reason));
                }catch(Exception e){
                    throw new PreyException(e);
                }
            }
        } catch (Exception e) {
            PreyLogger.e("Error:" + e.getMessage() + e.getMessage(), e);
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "lock", "failed", e.getMessage()));
        }
    }

    public void sms(Context ctx, List<ActionResult> list, JSONObject parameters) {
        try {
            String unlock = parameters.getString("parameter");
            lock(ctx, unlock, null, null,null);
        } catch (Exception e) {
            PreyLogger.i("Error:" + e.getMessage());
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "lock", "failed", e.getMessage()));
        }
    }

    public void lock(Context ctx, String unlock, String messageId, String reason,String device_job_id) {
        PreyLogger.i("lock unlock:"+unlock+" messageId:"+ messageId+" reason:"+reason);
        PreyConfig.getPreyConfig(ctx).setUnlockPass(unlock);
        PreyConfig.getPreyConfig(ctx).setLock(true);

        if(PreyConfig.getPreyConfig(ctx).isMarshmallowOrAbove() && PreyPermission.canDrawOverlays(ctx)) {
            try {
                Thread.sleep(2000);
                PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, "processed", messageId, UtilJson.makeMapParam("start", "lock", "started", reason));
            }catch(Exception e){}
            Intent intent = new Intent(ctx, PreyLockService.class);
            ctx.startService(intent);
        }else{
            if (PreyConfig.getPreyConfig(ctx).isFroyoOrAbove()) {
                try{
                    FroyoSupport.getInstance(ctx).changePasswordAndLock(unlock, true);
                }catch(Exception e){
                    PreyLogger.i("Error:" + e.getMessage());
                    PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "lock", "failed", e.getMessage()));
                }
            }
        }
    }

}

