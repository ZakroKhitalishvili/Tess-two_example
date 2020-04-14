package com.example.tesseractsample.tesseract;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import android.util.Log;

import com.example.tesseractsample.tools.PathUtils;
import com.googlecode.leptonica.android.Box;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class TesseractWrapper {

    private TessBaseAPI tessBaseAPI;
    private File image;
    private String lang;
    private OnProgress onProgress;
    private String processingDir;
    private static String TESSERACT_FOLDER = "tessdata";
    private Context context;
    private static final String TAG = "TesseractWrapper";
    private int progressPercent = 0;
    private static final int OCR_PREPARE_PART = 10;
    private static final int OCR_PART = 90;

    public TesseractWrapper(Context context, File image, String lang, String processingDir) {
        this.tessBaseAPI = new TessBaseAPI(new TessBaseAPI.ProgressNotifier() {
            @Override
            public void onProgressValues(TessBaseAPI.ProgressValues progressValues) {
                setProgress((int)(OCR_PREPARE_PART + (OCR_PART * progressValues.getPercent() / 100.0f)));
            }
        });

        this.image = image;
        this.lang = lang;
        this.context = context;
        this.processingDir = processingDir;
    }

    public Result process() throws Exception {

        doOCRPrepare();
        return doOCR();
    }

    private void doOCRPrepare() throws Exception {
        String tessDir = PathUtils.combineFile(processingDir, TESSERACT_FOLDER).getPath();
        PathUtils.prepareDirectory(tessDir);
        copyTessDataFiles(tessDir);
        setProgress(OCR_PREPARE_PART);
    }

    private Result doOCR() throws Exception {
        Result result = startOCR();
        setProgress(100);
        return result;
    }

    public void setOnProgress(OnProgress progress) {
        this.onProgress = progress;
    }

    public interface OnProgress {
        void onProgress(int percent);
    }

    public class Result
    {
        public int meanConfidence;
        public String fullUTF8Text;
        public String htmlText;
        public long elapsedTime;
        public Pixa textComponents;
        public Bitmap bitmap;
    }

    /**
     * Copy tessdata files (located on assets/tessdata) to destination directory
     *
     * @param path - name of directory with .traineddata files
     */
    private void copyTessDataFiles(String path) throws IOException {
        String fileList[] = context.getAssets().list(TESSERACT_FOLDER);
        for (String fileName : fileList) {
            // open file within the assets folder
            // if it is not already there copy it to the sdcard
            String pathToDataFile = PathUtils.combineFile(path, fileName).getPath();
            if (!(new File(pathToDataFile)).exists()) {

                InputStream in = context.getAssets().open(TESSERACT_FOLDER + "/" + fileName);

                OutputStream out = new FileOutputStream(pathToDataFile);

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

            }
        }
    }

    private Result startOCR() throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2; // 1 - means max size. 4 - means maxsize/4 size. Don't use value <4, because you need more memory in the heap to store your data.

        Bitmap bitmap = PathUtils.getOrientedBitmap(image, options);

        Result result = extractText(bitmap);
        return result;
    }


    private Result extractText(Bitmap bitmap) {

        tessBaseAPI.init(processingDir, lang);
        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
//       //EXTRA SETTINGS
//        //For example if we only want to detect numbers
//        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890");
//
//        //blackList Example
//        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
//                "YTRWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?");

//        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "@#$%^&*_+=[]}{" +
//                ":-;\\|~`/<>");

        StringBuilder extractedText = new StringBuilder();
        tessBaseAPI.setImage(bitmap);

        long start = System.currentTimeMillis();
        String html = tessBaseAPI.getHOCRText(0);
        long end = System.currentTimeMillis();
        long elapsedTime = (end-start);
        extractedText.append(tessBaseAPI.getUTF8Text());
        int meanConfidence = tessBaseAPI.meanConfidence();// triggers processing


        Result result = new Result();
        result.meanConfidence = meanConfidence;
        result.fullUTF8Text = extractedText.toString();
        result.htmlText = html ;
        result.elapsedTime = elapsedTime;
        result.textComponents = tessBaseAPI.getRegions();
        result.bitmap = bitmap;
        tessBaseAPI.end();
        return result;
    }

    private void setProgress(int percent) {
        this.progressPercent = percent;
        if (this.onProgress != null) {
            this.onProgress.onProgress(this.progressPercent);
        }
    }

}
