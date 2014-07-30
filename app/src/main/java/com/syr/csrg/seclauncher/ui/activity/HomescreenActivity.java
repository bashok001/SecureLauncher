package com.syr.csrg.seclauncher.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.syr.csrg.seclauncher.R;
import com.syr.csrg.seclauncher.agent.ConfigRetrievalAgent;
import com.syr.csrg.seclauncher.packDefinitions.ItemInfo;
import com.syr.csrg.seclauncher.packDefinitions.LauncherSettings;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;
import com.syr.csrg.seclauncher.packDefinitions.ShortcutInfo;
import com.syr.csrg.seclauncher.testLoader.Loader;
import com.syr.csrg.seclauncher.ui.adapter.HomescreenViewPagerAdapter;
import com.syr.csrg.seclauncher.ui.indicator.CirclePageIndicator;
import com.syr.csrg.seclauncher.ui.indicator.PageIndicator;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HomescreenActivity extends FragmentActivity
{
    HomescreenViewPagerAdapter pageAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        Loader.getInstance().load(this);

        setViewPagerHeight();
        int containersCount = getHomeScreenContainersCount();
        pageAdapter = new HomescreenViewPagerAdapter(getSupportFragmentManager(), containersCount);

        customizeViewPager();
        ViewPager pager = (ViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        PageIndicator mIndicator = (CirclePageIndicator)findViewById(R.id.viewpagerindicator);
        mIndicator.setViewPager(pager);

        loadQuickAccessPanel();
    }

    private void setViewPagerHeight()
    {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float containerHeight = dm.heightPixels;

        int quickAccessPanelWidthDP = 180;
        float quickAccessPanelWidthPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, quickAccessPanelWidthDP, getResources().getDisplayMetrics());

        int viewPagerHeight = (int) (containerHeight - quickAccessPanelWidthPX);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, viewPagerHeight));
    }

    private void loadQuickAccessPanel()
    {
        ConfigRetrievalAgent configRetrievalAgent = new ConfigRetrievalAgent();
        ArrayList<SecLaunchSubContainer> subContainers =  configRetrievalAgent.getSubContainersById(LauncherSettings.QUICK_ACCESS_PANEL_SC);
        if(subContainers.size() > 0)
        {
            ArrayList<ItemInfo> subContainerItems = subContainers.get(0).getItems();

            GridLayout gl = (GridLayout)findViewById(R.id.quickacesspanel);
            gl.setRowCount(1);

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            float containerWidth = dm.widthPixels;
            float pixItemWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, getResources().getDisplayMetrics());
            int columnCount = (int)Math.round(containerWidth/pixItemWidth);
            gl.setColumnCount(columnCount);
            final int itemWidth = (int) containerWidth/columnCount;

            for (int i = 0; i < subContainerItems.size() && i < columnCount; i++)
            {
                LayoutInflater factory = LayoutInflater.from(this);
                View myView = factory.inflate(R.layout.homescreen_item, null);
                ArrayList<View> allViews = new ArrayList<View>();
                allViews.add(myView);
                if (subContainerItems.get(i) != null)
                {

                    if (subContainerItems.get(i).getItemType() == LauncherSettings.ITEM_TYPE_SHORTCUT)
                    {
                        final ShortcutInfo item = (ShortcutInfo) subContainerItems.get(i);

                       /* LayoutInflater factory = LayoutInflater.from(this);
                        View myView = factory.inflate(R.layout.homescreen_item, null);*/

                        ImageView icon = (ImageView) myView.findViewById(R.id.icon);
                        icon.setImageDrawable(item.getIcon(this));

                        icon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(item.getIntent());
                                startActivity(intent);
                            }
                        });

                        TextView appname = (TextView) myView.findViewById(R.id.appname);
                        appname.setText(item.getAppName());

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = itemWidth;
                        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                        params.setGravity(Gravity.CENTER);
                        myView.setLayoutParams(params);
                        gl.addView(myView);
                    }
                }
                else
                {
                    Space spacer = new Space(this);

                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();

                    params.width = itemWidth;
                    params.height =  GridLayout.LayoutParams.WRAP_CONTENT;
                    params.setGravity(Gravity.CENTER);

                    spacer.setLayoutParams(params);
                   /*spacer.addTouchables(allViews);*/
                    gl.addView(spacer);
                }
            }
        }
    }

    private int getHomeScreenContainersCount()
    {
        ConfigRetrievalAgent configRetrievalAgent = new ConfigRetrievalAgent();
        ArrayList<SecLaunchSubContainer> subContainers =  configRetrievalAgent.getSubContainersById(LauncherSettings.HOME_SCREEN_SC);

        if(subContainers.size() == 0)
            return 1;
        else
            return subContainers.size();
    }

    private void customizeViewPager()
    {
        ViewPager pager = (ViewPager)findViewById(R.id.viewpager);

        pager.setPageTransformer(false, new ViewPager.PageTransformer() {
            public void transformPage(View page, float position)
            {
                ViewPager viewPager = (ViewPager) page.getParent();

                if(position == 0)
                    page.setBackground(null);
                else
                    page.setBackground(getResources().getDrawable(R.drawable.homescreen_viewpager_border));

                page.setRotationY(position * -20);
                page.setScaleY(1 - Math.abs(position / 3));

                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setAlpha(normalizedposition);
            }
        });
    }
}
