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
import com.prey.PreyConfig; 
import com.prey.PreyLogger;
 import com.prey.actions.observer.ActionResult; 
import com.prey.json.UtilJson; 
import com.prey.net.PreyWebServices;  

public class Ping  {  
    public static final String DATA_ID = "ping";    

    public void get(Context ctx, List<ActionResult> list, JSONObject parameters){ 
        String messageId = null; 
        try { 
            messageId = parameters.getString(PreyConfig.MESSAGE_ID); 
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, "processed",messageId, UtilJson.makeMapParam("start", "ping", "started",null)); 
            PreyLogger.d("messageId:"+messageId); 
        } catch (Exception e) {         }  
    } 
}
