package com.example.tesseractsample;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tesseractsample.viewmodels.TextResultViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class TextResultFragment extends Fragment {

    private TextResultViewModel mViewModel;
    private TextView output;
    private View view;

    public static TextResultFragment newInstance() {
        return new TextResultFragment();
    }

    public TextResultFragment(@NotNull TextResultViewModel model)
    {
        Log.d("OCRResultFragment", "Model confidence:"+model.confidence);
        mViewModel = model;

    }

    public TextResultFragment()
    {
        mViewModel = new TextResultViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.text_result_fragment, container, false);
         return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(getActivity()).get(TextResultViewModel.class);
        // TODO: Use the ViewModel

        output = view.findViewById(R.id.ocr_output);

        Log.d("OCRResultFragment", "Model confidence:"+mViewModel.confidence);
        output.setText(  String.format(Locale.getDefault(), "Ellapsed Time: %.2f seconds \n\n Mean Confidence : %d/100 \n\n %s",
                mViewModel.millisecondsElapsed / 1000.0f, mViewModel.confidence, mViewModel.text));
           }

}
