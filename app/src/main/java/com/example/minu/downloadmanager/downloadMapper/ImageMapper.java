package com.example.minu.downloadmanager.downloadMapper;

import com.example.minu.downloadmanager.DownloadModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Minu on 12/22/17.This class maps api data to download model which is passed to
 * download manager to download images.
 */

class ImageMapper  {
    private final String TAG = "ClassificationInfoHelper";
    private List<DownloadModel> fetchArrayList = new ArrayList<>();
    private List<ArrayList> classificationInfos = new ArrayList<>();


    public List<DownloadModel> getCassification() {
        classificationInfos.clear();
        fetchArrayList.clear();
        //classificationInfos = dataServices.getClassification();
        String test = "/images/classification/test.png";
      /*  for (final ClassificationInfo classificationInfo : classificationInfos) {
            if (!TextUtils.isEmpty(classificationInfo.getImageUrl())) {
                if (!classificationInfo.getImageUrl().equalsIgnoreCase(test)) {
                    File file = new File(classificationInfo.getImageUrl());
                    //  if (!file.exists()) {
                    DownloadModel downloadModel = new DownloadModel();
                    downloadModel.setType(FetchUtils.CLASSIFICATION);
                    downloadModel.setUrl(NetworkModule.IMAGE_URL + classificationInfo.getImageUrl());
                    downloadModel.setData(classificationInfo);
                    fetchArrayList.add(downloadModel);
                }
            }*/

        return fetchArrayList;
    }



}
