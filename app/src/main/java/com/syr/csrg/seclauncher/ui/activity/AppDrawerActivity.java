package com.syr.csrg.seclauncher.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.syr.csrg.seclauncher.R;
import com.syr.csrg.seclauncher.agent.ConfigRetrievalAgent;
import com.syr.csrg.seclauncher.packDefinitions.ItemInfo;
import com.syr.csrg.seclauncher.packDefinitions.LauncherSettings;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;
import com.syr.csrg.seclauncher.ui.adapter.AppdrawerViewPagerAdapter;
import com.syr.csrg.seclauncher.ui.indicator.CirclePageIndicator;
import com.syr.csrg.seclauncher.ui.indicator.PageIndicator;

import java.util.ArrayList;

public class AppDrawerActivity extends FragmentActivity {

    private static final float MIN_SCALE = 0.75f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_drawer);

        int containersCount = getAppDrawerContainersCount(getViewPagerColumnCount() * getViewPagerRowCount());
        AppdrawerViewPagerAdapter pageAdapter = new AppdrawerViewPagerAdapter(getSupportFragmentManager(), containersCount);

        ViewPager pager = (ViewPager)findViewById(R.id.appdrawer_viewpager);
        pager.setAdapter(pageAdapter);

        PageIndicator mIndicator = (CirclePageIndicator)findViewById(R.id.appdrawer_viewpagerindicator);
        mIndicator.setViewPager(pager);

        customizeViewPager();
    }

    private void customizeViewPager()
    {
        ViewPager pager = (ViewPager)findViewById(R.id.appdrawer_viewpager);

        pager.setPageTransformer(false, new ViewPager.PageTransformer() {
            public void transformPage(View view, float position)
            {
                int pageWidth = view.getWidth();

                if (position < -1) {
                    view.setAlpha(0);

                } else if (position <= 0) {
                    view.setAlpha(1);
                    view.setTranslationX(0);
                    view.setScaleX(1);
                    view.setScaleY(1);

                } else if (position <= 1) {
                    view.setAlpha(1 - position);
                    view.setTranslationX(pageWidth * -position);

                    float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                } else {
                    view.setAlpha(0);
                }

            }
        });
    }

    private int getViewPagerColumnCount()
    {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float containerWidth = dm.widthPixels;
        float pixItemWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, getResources().getDisplayMetrics());
        int columnCount = (int)Math.round(containerWidth/pixItemWidth);

        return columnCount;
    }

    private int getViewPagerRowCount()
    {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float containerHeight = dm.heightPixels;
        int indicatorWidthDP = 90;
        float indicatorWidthPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorWidthDP, getResources().getDisplayMetrics());
        float viewPagerHeight =  (containerHeight - indicatorWidthPX);
        float pixItemHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 107, getResources().getDisplayMetrics());
        int rowCount = (int)Math.round(viewPagerHeight/pixItemHeight);

        return rowCount;
    }

    private int getAppDrawerContainersCount(int noofItems)
    {
        ConfigRetrievalAgent configRetrievalAgent = new ConfigRetrievalAgent();
        ArrayList<SecLaunchSubContainer> subContainers =  configRetrievalAgent.getSubContainersById(LauncherSettings.APP_DRAWER_SC);

        if(subContainers.size() == 0)
            return 1;
        else
        {
            int count = 0;
            ArrayList<ItemInfo> subContainerItems = subContainers.get(0).getItems();
            for (int i = 0; i < subContainerItems.size(); i++)
            {
                if (subContainerItems.get(i).getItemType() == LauncherSettings.ITEM_TYPE_SHORTCUT)
                    count++;
            }
            return (int)Math.ceil(count/(noofItems*1.0));
        }
    }
}
