package com.example.minu.downloadmanager.FetchUtils;

import android.net.Uri;
import android.os.Environment;

import com.example.minu.downloadmanager.FileUtils;
import com.tonyodev.fetch.request.Request;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by minu.In this class variables and filepath are defined.
 */

public final class FetchUtils {

    public static final String CLASSIFICATION = "Classifications";
    public static final String CLASSIFICATIONGRAPHICS = "Graphics";
    public static final String CLASSIFICATIONSIL = "Silhouettes";
    public static final String CHARACTER = "Characters";
    private static final String USERPHOTO = "USERPHOTO";
    public static final String BADGE = "Badges";
    public static final String SERVER_ERROR = "Server_error";

    private FetchUtils() {

    }

    static List<Request> getFetchRequests(ArrayList<String> sampleUrls, String type) {
        List<Request> requests = new ArrayList<>();
        for (String sampleUrl : sampleUrls) {
            Request request = new Request(sampleUrl, getFilePath(sampleUrl, type));
            requests.add(request);
        }
        return requests;
    }

    public static String getFilePath(String url, String type) {
        Uri uri = Uri.parse(url);
        String filePath;
        String title = uri.getLastPathSegment();
        if (type.contentEquals(USERPHOTO)) {
            filePath = Environment.getExternalStorageDirectory() +
                    "/" + "Android/data/edu.uw.naturecollections/files/" + Environment.DIRECTORY_PICTURES + "/" +
                    FileUtils.removeInvalidFileCharacters(title) + ".jpg";
        } else {
            filePath = Environment.getExternalStorageDirectory() +
                    "/" + "Android/data/edu.uw.naturecollections/files/" + Environment.DIRECTORY_PICTURES + "/" +
                    type + "/" + FileUtils.removeInvalidFileCharacters(title) + ".jpg";
        }

        return filePath;
    }

}
