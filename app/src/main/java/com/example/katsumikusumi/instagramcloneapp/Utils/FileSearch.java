package com.example.katsumikusumi.instagramcloneapp.Utils;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class FileSearch {

    /**
     * Search a directory and return a list of all **directories** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory) {
        Log.d(TAG, "started getDirectoryPaths");
        Log.d(TAG, "getDirectoryPaths: passed directory is " + directory);
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for (int i = 0; i < listfiles.length; i++) {
            if (listfiles[i].isDirectory()) {
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }

        for(int i = 0; i < pathArray.size(); i++) {
            Log.d(TAG, "getDirectoryPaths: pathArray: " + pathArray.get(i));
        }

        Log.d(TAG, "end getDirectoryPaths");
        return pathArray;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for (int i = 0; i < listfiles.length; i++) {
            if (listfiles[i].isFile()) {
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
}
