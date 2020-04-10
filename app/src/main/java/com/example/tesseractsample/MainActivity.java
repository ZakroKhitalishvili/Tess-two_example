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

import android.text.Html;
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
import java.util.Locale;
import java.util.ResourceBundle;

public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PHOTO_REQUEST_CODE = 1;
    private static final int PHOTO_SELECT_CODE = 2;
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
    private String langArray[];

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
                R.array.lang_text_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);

        langArray = getResources().getStringArray(R.array.lang_value_array_);

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lang = langArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button captureImg = (Button) findViewById(R.id.action_btn);
        Button chooseImg = (Button) findViewById(R.id.action_choosing_btn);
        if (captureImg != null) {
            captureImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startCameraActivity();
                }
            });
        }
        if (chooseImg != null) {
            chooseImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startImageSelectActivity();
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

    private void startImageSelectActivity() {
        try {
            final Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            if (choosePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(choosePictureIntent, PHOTO_SELECT_CODE);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        //making photo
        if (requestCode == PHOTO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            doOCR(imgFile, lang);
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

        if (requestCode == PHOTO_SELECT_CODE && data.getData() != null) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                final File externalImage = new File(selectedImage.getPath().replace("raw/", ""));

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {

                            doOCR(externalImage, lang);
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
    }

    private void doOCR(File image, String lang) throws Exception {
        setText(textView,"Processing, please wait ...");
        TesseractWrapper tesseractWrapper = new TesseractWrapper(this, image, lang, basePath.getPath());
        tesseractWrapper.setOnProgress(new TesseractWrapper.OnProgress() {
            @Override
            public void onProgress(int percent) {
                setProgressBar(percent);
            }
        });
        TesseractWrapper.Result result = tesseractWrapper.process();
        setText(textView,
                String.format(Locale.getDefault(), "Ellapsed Time: %.2f seconds \n\n Mean Confidence : %d/100 \n\n %s",
                        result.elapsedTime / 1000.0f, result.meanConfidence, result.htmlText));
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
                text.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
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


