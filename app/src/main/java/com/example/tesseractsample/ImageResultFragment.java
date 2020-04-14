package com.example.tesseractsample;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tesseractsample.tools.PathUtils;
import com.example.tesseractsample.viewmodels.ImageResultViewModel;

public class ImageResultFragment extends Fragment {

    private ImageResultViewModel mViewModel;
    private ImageView imageView;
    private View view;

    public static ImageResultFragment newInstance() {
        return new ImageResultFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.image_result_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(ImageResultViewModel.class);
        imageView = view.findViewById(R.id.image_result);
        // TODO: Use the ViewModel


        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 2;
        bitmapOptions.inMutable = true;
        Bitmap bitmap = null;

        Log.d("ImageResultFragment", "Image file path: " + mViewModel.image.getPath());
        try {
            bitmap = PathUtils.getOrientedBitmap(mViewModel.image, bitmapOptions);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);

            for (Rect rect : mViewModel.imageBoxRects) {
                Paint rectPaint = new Paint();
                rectPaint.setColor(Color.RED);
                rectPaint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(rect, rectPaint);
            }
            imageView.setImageBitmap(bitmap);

        } else {
            Log.d("ImageResultFragment", "Image bitmap is null");
        }
    }

}
