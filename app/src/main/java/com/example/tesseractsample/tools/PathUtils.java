package com.example.tesseractsample.tools;

import java.io.File;

public class PathUtils {


    public static File combineFile(String... parts) {
        File file = new File(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            file = new File(file, parts[i]);
        }
        return file;
    }

    /**
     * Prepare directory on external storage
     *
     * @param path
     * @throws Exception
     */
    public static boolean prepareDirectory(String path) {

        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return false;
            }
        }
        return true;
    }
}
