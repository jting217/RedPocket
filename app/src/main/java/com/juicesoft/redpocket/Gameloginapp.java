package com.juicesoft.redpocket;
import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Daniel Alvarez on 29/6/16.
 * Copyright © 2016 Alvarez.tech. All rights reserved.
 */
public class Gameloginapp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
