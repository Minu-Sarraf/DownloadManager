package com.example.minu.downloadmanager.downloadManager;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;

import com.example.minu.downloadmanager.DownloadModel;
import com.example.minu.downloadmanager.FetchUtils.FetchUtils;
import com.example.minu.downloadmanager.interactors.DownloaderInteractor;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.listener.FetchListener;
import com.tonyodev.fetch.request.Request;
import com.tonyodev.fetch.request.RequestInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class downloads images and updates database.
 */

public class DownloadManager implements FetchListener {
    private final HashMap<Long, Integer> retryCount = new HashMap<>();
    private final ArrayList<Integer> successData = new ArrayList<>();
    private final Fetch fetch;
    private boolean updateStat = false;
    private HashMap<Long, RedownloadModel> staleRequests = new HashMap<>();
    private List<DownloadModel> oldBadgeImages = new ArrayList<>();
    private List<DownloadModel> reDownloadBadgeImages = new ArrayList<>();
    private Map<Long, DownloadModel> downloadModelRequestIdMap = new HashMap<>();
    private int totalQueueSize = 0;
    private List<DownloadModel> arrayList = new ArrayList<>();
    private WifiP2pManager.ActionListener actionListener;
    private String type = "";
    private boolean allAreOldImages;
    private int numberOfNewBadgeImages = 0;
    private boolean removeOldRequest = false;

    public DownloadManager(Context context, DownloaderInteractor downloaderInteractor) {
        Context context1 = context;
        fetch = Fetch.newInstance(context1);
        if (true)
            fetch.removeAll();
        fetch.addFetchListener(this);
        DownloaderInteractor downloaderInteractor1 = downloaderInteractor;
        updateStat = false;
        allAreOldImages = true;
    }

    private void downloadAssets(List<DownloadModel> downloadFetchesList) {
        this.arrayList = downloadFetchesList;
        this.removeOldRequest = false;
        enqueueDownloads();
    }

    private void enqueueDownloads() {
        successData.clear();
        if (arrayList.size() <= 0)
            return;

        for (DownloadModel imageDownload : arrayList) {
            type = imageDownload.getType();
            String filePath = FetchUtils.getFilePath(imageDownload.getUrl(), imageDownload.getType());
            handleRequest(imageDownload, filePath);
        }
    }

    private void handleRequest(DownloadModel imageDownload, String filePath) {
        Request request = new Request(imageDownload.getUrl(), filePath);

        long id;
        RequestInfo requestInfo = fetch.get(request);

        if (removeOldRequest && requestInfo != null) {
            id = requestInfo.getId();

            RedownloadModel redownloadModel = new RedownloadModel();
            redownloadModel.downloadModel = imageDownload;
            redownloadModel.filePath = filePath;

            staleRequests.put(id, redownloadModel);

            fetch.remove(id);
            return;
        } else {
            id = fetch.enqueue(request);
        }

        if (id != Fetch.ENQUEUE_ERROR_ID) {
            imageDownload.setId(id);
            imageDownload.setFilePath(filePath);
        }
    }

    public void deleteOldBadgeImages(List<DownloadModel> badges, boolean updateStat) {
        this.updateStat = updateStat;
        this.type = badges.get(0).getType();
        this.arrayList = badges;

        successData.clear();
        if (arrayList.size() <= 0)
            return;

        for (DownloadModel imageDownload : arrayList) {

            String filePath = FetchUtils.getFilePath(imageDownload.getUrl(), imageDownload.getType());
            Request request = new Request(imageDownload.getUrl(), filePath);
            RequestInfo requestInfo = fetch.get(request);
            if (requestInfo == null) {
                allAreOldImages = false;
                numberOfNewBadgeImages++;
                //requestInfo ==  null means this request is for new image
                long id = fetch.enqueue(request);
                if (id != Fetch.ENQUEUE_ERROR_ID) {
                    imageDownload.setId(id);
                    imageDownload.setFilePath(filePath);
                } else {
                    // Timber.e("Error in enqueue id");
                }
            } else {
                //requestinfo !=null means this is old
                // request so first delete the image then redownload
                oldBadgeImages.add(imageDownload);

            }
        }

        if (allAreOldImages) {
            deleteAndReDownloadImages(oldBadgeImages);
        }
    }

    @Override
    public void onUpdate(long id, int status, int progress, long downloadedBytes, long fileSize, int error) {

        if (status == Fetch.STATUS_REMOVED && removeOldRequest) {
            RedownloadModel redownloadModel = staleRequests.get(id);
            if (redownloadModel != null) {
                totalQueueSize = totalQueueSize + 1;
                handleRequest(redownloadModel.downloadModel, redownloadModel.filePath);
            }
            staleRequests.remove(id);
        }

        if (updateStat && status == Fetch.STATUS_REMOVED) {
            if (downloadModelRequestIdMap.size() > 0 && downloadModelRequestIdMap.get(id) != null) {
                reDownloadBadgeImages.add(downloadModelRequestIdMap.get(id));
                downloadModelRequestIdMap.remove(id);
            }
            startDownloadingNewBadgeImages();
        }

        if (status != Fetch.STATUS_REMOVED) {
            DownloadModel download = getDownload(id);
            if (download != null) {
                download.setStatus(status);
                download.setProgress(progress);
                download.setError(error);
                checkStatus(id, status, download, error);
            }
        } else {
            // Timber.e("update: %s status%d%d Status Removed", null, status, id);
        }
    }

    private void startDownloadingNewBadgeImages() {
        if (downloadModelRequestIdMap.size() == 0) {
//          downloadModelRequestIdMap.size() == 0 means all old images has been deleted
            updateStat = false;
            downloadAssets(reDownloadBadgeImages);
        }
    }

    private void checkStatus(long id, int status, DownloadModel download, int error) {
        switch (status) {
            case Fetch.STATUS_DONE: {
                successData.add((int) id);
            }
            break;
            case Fetch.STATUS_ERROR: {
                if (error == Fetch.ERROR_NO_STORAGE_SPACE) {
                    retry(id);
                }
            }
            break;
        }
        if (!updateStat && arrayList.size() == successData.size()) {
            if (type.equalsIgnoreCase(FetchUtils.BADGE)) {
                fetch.release();
            }
        }
        if (updateStat && numberOfNewBadgeImages == successData.size()) {
            deleteAndReDownloadImages(oldBadgeImages);
        }
    }

    private void deleteAndReDownloadImages(List<DownloadModel> oldBadgeImages) {
        if (oldBadgeImages.size() > 0) {
            for (DownloadModel imageDownload : oldBadgeImages) {
                String filePath = FetchUtils.getFilePath(imageDownload.getUrl(), imageDownload.getType());
                Request request = new Request(imageDownload.getUrl(), filePath);
                RequestInfo requestInfo = fetch.get(request);
                downloadModelRequestIdMap.put(requestInfo.getId(), imageDownload);
                fetch.remove(requestInfo.getId());

            }
        } else {
            fetch.release();
        }
    }

    private void retry(long id) {
        final int retryCounter = 2;
        int noOfRetry = 0;
        if (retryCount.get(id) == null) {
            retryCount.put(id, 0);
        } else {
            noOfRetry = retryCount.get(id) + 1;
            retryCount.put(id, noOfRetry);
        }

        if (noOfRetry >= retryCounter) {
            // actionListener.onDownloadFailed((String.format("Downloading %s Failed", type)));
            fetch.release();
        } else {
            fetch.retry(id);
        }
    }

    private DownloadModel getDownload(long id) {
        for (DownloadModel download : arrayList) {
            if (download.getId() == id) {
                return download;
            }
        }
        return null;
    }


   /* private void saveToDatabase(final DownloadModel download) {
        type = download.getType();
        switch (type) {
            case FetchUtils.CHARACTER:
                downloaderInteractor.updateCharacter(download);
                break;
            case FetchUtils.CLASSIFICATION:
                downloaderInteractor.updateClassification(download);
                break;
            case FetchUtils.CLASSIFICATIONSIL:
                downloaderInteractor.updateSil(download);
                break;
            case FetchUtils.CLASSIFICATIONGRAPHICS:
                downloaderInteractor.updateGraphic(download);
                break;
            case FetchUtils.BADGE:
                downloaderInteractor.updateBadge(download);
                break;
            case FetchUtils.USERPHOTO:
                downloaderInteractor.updateUserPhotos(download);
                break;
        }*/

    public class RedownloadModel {
        public DownloadModel downloadModel;
        public String filePath;
    }

}

