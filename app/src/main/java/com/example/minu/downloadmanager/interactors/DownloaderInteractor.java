package com.example.minu.downloadmanager.interactors;


public interface DownloaderInteractor {

    boolean getAdditionalDataDownloadStatus();

    void setAdditionalDataDownloadStatus(boolean complete);
    void setDownloadLevel(int level);
    int getDownloadLevel();

    void updateSavedGraphicsPath(String imageUrl, String imageSavedPath);

    void updateSilhouetteGraphicsPath(String silhouetteUrl, String imageSavedPath);

    void setAppInstallationTime(long appInstalltionTime);

    long getAppInstallationTime();

}

