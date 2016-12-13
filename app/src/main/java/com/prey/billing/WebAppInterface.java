/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2016 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.billing;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.prey.PreyLogger;
import com.prey.activities.PlansActivity;

public class WebAppInterface {

    Context ctx;
    PlansActivity activity;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context ctx,PlansActivity activity) {
        ctx = ctx;
        this.activity=activity;
    }


    @JavascriptInterface
    public void plan(String planName) {
        PreyLogger.i("planName:"+planName);
        if("home".equals(planName)) {
            activity.onUpgradeHome();
        }else {
            activity.onUpgradePersonal();
        }
    }

    @JavascriptInterface
    public void back() {
        PreyLogger.i("back");
        activity.back();
    }

}
