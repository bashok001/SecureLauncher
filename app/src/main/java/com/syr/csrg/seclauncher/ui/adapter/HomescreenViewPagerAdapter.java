package com.syr.csrg.seclauncher.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.syr.csrg.seclauncher.ui.fragment.HomescreenViewPagerFragment;

/**
 * Created by neethu on 7/9/2014.
 */
public class HomescreenViewPagerAdapter extends SubContainerViewPagerAdapter
{
    int count;

    public HomescreenViewPagerAdapter(FragmentManager fm, int size)
    {
        super(fm, size);
        count = size;
    }

    public Fragment getItem(int position)
    {
        return HomescreenViewPagerFragment.newInstance(position, true);
    }

    public Fragment getUnclickableItem(int position)
    {
        return HomescreenViewPagerFragment.newInstance(position, false);
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
