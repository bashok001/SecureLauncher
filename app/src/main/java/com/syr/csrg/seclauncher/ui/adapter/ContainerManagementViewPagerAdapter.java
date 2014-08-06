package com.syr.csrg.seclauncher.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.syr.csrg.seclauncher.ui.fragment.ContainerManagementViewPagerFragment;

/**
 * Created by Shane on 7/29/14.
 */
public class ContainerManagementViewPagerAdapter extends FragmentStatePagerAdapter
{
    int count;
    public ContainerManagementViewPagerAdapter(FragmentManager fm, int size)
    {
        super(fm);
        count = size;
    }

    public Fragment getItem(int position)
    {
        return ContainerManagementViewPagerFragment.newInstance(position);
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
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
