package com.example.minu.downloadmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.minu.downloadmanager.downloadManager.DownloadManager;
import com.example.minu.downloadmanager.interactors.DownloaderInteractor;

public class MainActivity extends AppCompatActivity {
    private DownloaderInteractor downloaderInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DownloadManager(this, downloaderInteractor);
    }
}
