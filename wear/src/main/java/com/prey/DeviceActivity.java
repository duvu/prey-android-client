package com.prey;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;

import com.prey.fragments.ActionFragment;
import com.prey.fragments.DeviceModelFragment;
import com.prey.fragments.MapsFragment;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by oso on 21-09-16.
 */

public class DeviceActivity extends Activity {

    private GridViewPager mPager;
    private DeviceModelFragment deviceModelFragment;
    private ActionFragment actionsFragment;
    private MapsFragment mapFragment;
    private int idDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        idDevice = b.getInt("id");
        Log.i("PREY","id:"+idDevice);

        setContentView(R.layout.activity_device);
        setupViews();
    }


    private void setupViews() {
        mPager = (GridViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageCount(2);
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setDotSpacing((int) getResources().getDimension(R.dimen.dots_spacing));
        dotsPageIndicator.setPager(mPager);
        deviceModelFragment = new DeviceModelFragment();
        deviceModelFragment.idDevice=idDevice;
        actionsFragment = new ActionFragment();
        actionsFragment.idDevice=idDevice;
        mapFragment = new MapsFragment();
        List<Fragment> pages = new ArrayList<>();
        pages.add(deviceModelFragment);
        pages.add(actionsFragment);
        pages.add(mapFragment);
        final MyPagerAdapter adapter = new MyPagerAdapter(getFragmentManager(), pages);
        mPager.setAdapter(adapter);

    }


    private class MyPagerAdapter extends FragmentGridPagerAdapter {

        private List<Fragment> mFragments;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount(int row) {
            return mFragments == null ? 0 : mFragments.size();
        }

        @Override
        public Fragment getFragment(int row, int column) {
            return mFragments.get(column);
        }

    }
}
