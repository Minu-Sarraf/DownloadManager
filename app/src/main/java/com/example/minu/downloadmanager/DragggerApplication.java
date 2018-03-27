package com.example.minu.downloadmanager;

import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.tonyodev.fetch.Fetch;

/**
 * Created by leapfrog on 3/27/18.
 */

public class DragggerApplication extends Application {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        setupFetch();
    }

    private void setupFetch() {
        new Fetch.Settings(getApplicationContext())
                .setAllowedNetwork(Fetch.NETWORK_ALL)
                .enableLogging(true)
                .setOnUpdateInterval(1000)
                .setConcurrentDownloadsLimit(3)
                .apply();

    }
}
