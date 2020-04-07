package com.example.tesseractsample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tesseractsample.tools.PathUtils;
import com.example.tesseractsample.tools.RequestPermissionsTool;
import com.example.tesseractsample.tools.RequestPermissionsToolImpl;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PHOTO_REQUEST_CODE = 1;
    private final String APP_FOLDER_NAME = "TesseractSample";
    private final String TESS_DATA = "tessdata";
    private final String IMG_FOLDER = "imgs";
    private final String IMG_NAME = "ocr.jpg";

    private TessBaseAPI tessBaseApi;
    private TextView textView;
    private Spinner langSpinner;
    private ProgressBar progressBar;
    private String lang = "eng";
    private String result = "empty";
    private RequestPermissionsTool requestTool;
    private File basePath;
    private File imgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        basePath = PathUtils.combineFile(getExternalCacheDir().toString(), APP_FOLDER_NAME);

        langSpinner = findViewById(R.id.lang_spinner);
        progressBar = findViewById(R.id.progressBar);

        File imgDir = new File(basePath, IMG_FOLDER);
        imgFile = new File(imgDir, IMG_NAME);
        PathUtils.prepareDirectory(imgDir.getPath());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lang_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        langSpinner.setAdapter(adapter);

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lang = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button captureImg = (Button) findViewById(R.id.action_btn);
        if (captureImg != null) {
            captureImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startCameraActivity();
                }
            });
        }
        textView = (TextView) findViewById(R.id.textResult);
        textView.setMaxLines(100);
        textView.setMovementMethod(new ScrollingMovementMethod());
        requestPermissions();
    }


    /**
     * to get high resolution image from camera
     */
    private void startCameraActivity() {
        try {
            final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(imgFile.getPath()));

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        //making photo
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        doOCR();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();

        } else {
            Toast.makeText(this, "ERROR: Image was not obtained.", Toast.LENGTH_SHORT).show();
        }
    }

    private void doOCR() throws Exception {
        TesseractWrapper tesseractWrapper = new TesseractWrapper(this, imgFile, lang, basePath.getPath());
        tesseractWrapper.setOnProgress(new TesseractWrapper.OnProgress() {
            @Override
            public void onProgress(int percent) {
                setProgressBar(percent);
            }
        });
        TesseractWrapper.Result result = tesseractWrapper.process();
        setText(textView, String.format("Mean Confidence : %d/100 \n\n %s",result.meanConfidence,result.fullUTF8Text));

    }

    private void requestPermissions() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestTool = new RequestPermissionsToolImpl();
        requestTool.requestPermissions(this, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean grantedAllPermissions = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                grantedAllPermissions = false;
            }
        }

        if (grantResults.length != permissions.length || (!grantedAllPermissions)) {

            requestTool.onPermissionDenied();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void setText(final TextView text, final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    private void setProgressBar(final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress, true);
            }
        });
    }

    private Uri getFileUri(String fileName) {
        return FileProvider.getUriForFile(this,
                this.getApplicationContext().getPackageName() + ".provider",
                new File(fileName));
    }
}


