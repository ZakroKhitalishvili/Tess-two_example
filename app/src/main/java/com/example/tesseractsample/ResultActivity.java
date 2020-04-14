package com.example.tesseractsample;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import com.example.tesseractsample.viewmodels.ImageResultViewModel;
import com.example.tesseractsample.viewmodels.TextResultViewModel;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tesseractsample.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), getLifecycle());
        Intent intent = getIntent();

        TextResultViewModel model = new ViewModelProvider(this).get(TextResultViewModel.class);
        model.confidence = intent.getIntExtra("confidence", 0);
        model.text = intent.getStringExtra("text");
        model.millisecondsElapsed = intent.getLongExtra("elapsedTime", 0);

        ImageResultViewModel imageModel = new ViewModelProvider(this).get(ImageResultViewModel.class);
        imageModel.imageBoxRects = (ArrayList<Rect>) intent.getSerializableExtra("imageBoxRects");
        imageModel.image = (File) intent.getSerializableExtra("image");

        sectionsPagerAdapter.addFragment(new TextResultFragment());
        sectionsPagerAdapter.addFragment(new ImageResultFragment());

        final ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);

        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        String title = getResources().getString(sectionsPagerAdapter.getTabTitleId(position));
                        tab.setText(title);
                    }
                }).attach();

    }
}