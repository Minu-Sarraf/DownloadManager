package com.example.minu.downloadmanager;
public class FileUtils {


    public static String removeInvalidFileCharacters(String unformattedFilename) {
        return unformattedFilename.replace("/", "_").replace(".", "_");
    }


}
