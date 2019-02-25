package com.sampleapplication.bookit.booking;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sampleapplication.bookit.model.SeatAvailable;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<SeatAvailable> availableList = new ArrayList<>();
    ArrayList<SeatAvailable> unAvailableList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fm, ArrayList<SeatAvailable> availableList, ArrayList<SeatAvailable> unAvailableList) {
        super(fm);
        this.availableList = availableList;
        this.unAvailableList = unAvailableList;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return AvailableFragment.newInstance(availableList);
        else if (position == 1)
            return UnAvailableFragment.newInstance(unAvailableList);
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "Available List";
        else if (position == 1)
            return "Un Available List";
        else
            return "";
    }

    @Override
    public int getCount() {
        return 2;
    }
}