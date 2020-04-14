package com.example.tesseractsample.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

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

    public static Bitmap getOrientedBitmap(File image, BitmapFactory.Options options) throws Exception {
        Bitmap bitmap = BitmapFactory.decodeFile(image.getPath(), options);

        if (bitmap == null) {
            throw new Exception("Could not get a bitmap from image file");
        }

        //exploring image orientation
        ExifInterface exif = new ExifInterface(image.getPath());
        int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        if (rotate != 0) {

            // Getting width & height of the given image.
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.postRotate(rotate);

            // Rotating Bitmap
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
        }

        return bitmap;
    }
}
