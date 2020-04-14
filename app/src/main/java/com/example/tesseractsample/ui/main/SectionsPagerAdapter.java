package com.example.tesseractsample.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tesseractsample.R;

import java.util.ArrayList;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    private ArrayList<Fragment> arrayList = new ArrayList<>();

    public SectionsPagerAdapter(Context context, FragmentManager fm, Lifecycle lifecycle) {
        super(fm, lifecycle);
        mContext = context;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return arrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void addFragment(Fragment fragment)
    {
        arrayList.add(fragment);
    }

     public int getTabTitleId(int position)
     {
         return TAB_TITLES[position];
     }




}