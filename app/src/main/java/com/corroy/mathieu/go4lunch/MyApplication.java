package com.corroy.mathieu.go4lunch;

import androidx.multidex.MultiDexApplication;
import com.google.android.libraries.places.api.Places;

public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Places.initialize(this, "AIzaSyDXI74hOiHLi4l2vhUEs23260f055xyXvI");
    }
}
