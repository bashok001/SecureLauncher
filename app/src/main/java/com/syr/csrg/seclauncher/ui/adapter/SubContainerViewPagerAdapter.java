package com.syr.csrg.seclauncher.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.syr.csrg.seclauncher.ui.fragment.HomescreenViewPagerFragment;

/**
 * Created by Shane on 7/31/14.
 */
public abstract class SubContainerViewPagerAdapter extends FragmentStatePagerAdapter {

    int count;

    public SubContainerViewPagerAdapter(FragmentManager fm, int size)
    {
        super(fm);
        count = size;
    }

    public abstract Fragment getItem(int position);

    public abstract Fragment getUnclickableItem(int position);

    public int getCount()
    {
        return count;
    }

    public void setCount(int c)
    {
        count = c;
    }

}
