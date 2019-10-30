package com.corroy.mathieu.go4lunch;

import androidx.multidex.MultiDexApplication;

import com.batch.android.Batch;
import com.batch.android.BatchActivityLifecycleHelper;
import com.batch.android.Config;
import com.corroy.mathieu.go4lunch.Utils.NotificationInterceptor;
import com.google.android.libraries.places.api.Places;

public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Places.initialize(this, "AIzaSyDXI74hOiHLi4l2vhUEs23260f055xyXvI");
        // Batch.setConfig(new Config("5D9E1D8306EC665DDE8D00D9324AE1")); // live
        Batch.setConfig(new Config("DEV5D9E1D83073B19845A52596E922")); // development
        registerActivityLifecycleCallbacks(new BatchActivityLifecycleHelper());
        Batch.Push.setNotificationInterceptor(new NotificationInterceptor());
    }
}
