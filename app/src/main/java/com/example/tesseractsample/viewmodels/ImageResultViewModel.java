package com.example.tesseractsample.viewmodels;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;

public class ImageResultViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    public ArrayList<Rect> imageBoxRects;
    public File image;
}
