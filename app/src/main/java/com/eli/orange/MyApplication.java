package com.eli.orange;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import androidx.multidex.MultiDex;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}