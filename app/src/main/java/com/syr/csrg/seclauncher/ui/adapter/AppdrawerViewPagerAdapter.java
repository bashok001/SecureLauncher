package com.syr.csrg.seclauncher.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.syr.csrg.seclauncher.ui.fragment.AppdrawerViewPagerFragment;

/**
 * Created by neethu on 7/24/2014.
 */
public class AppdrawerViewPagerAdapter extends FragmentStatePagerAdapter
{
    int count;

    public AppdrawerViewPagerAdapter(FragmentManager fm, int size)
    {
        super(fm);
        count = size;
    }

    public Fragment getItem(int position)
    {
        return AppdrawerViewPagerFragment.newInstance(position);
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int c)
    {
        count = c;
    }

}