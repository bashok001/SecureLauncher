package com.syr.csrg.seclauncher.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.syr.csrg.seclauncher.R;
import com.syr.csrg.seclauncher.agent.ConfigRetrievalAgent;
import com.syr.csrg.seclauncher.packDefinitions.ItemInfo;
import com.syr.csrg.seclauncher.packDefinitions.LauncherSettings;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;
import com.syr.csrg.seclauncher.packDefinitions.ShortcutInfo;

import java.util.ArrayList;

/**
 * Created by neethu on 7/24/2014.
 */
public class AppdrawerViewPagerFragment extends Fragment
{
    public static final String APPDRAWER_POSITION = "position";

    public static final AppdrawerViewPagerFragment newInstance(int position)
    {
        AppdrawerViewPagerFragment fragment = new AppdrawerViewPagerFragment();
        Bundle bdl = new Bundle(1);
        bdl.putInt(APPDRAWER_POSITION, position);
        fragment.setArguments(bdl);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.homescreen_viewpager_layout, container, false);
        int position = getArguments().getInt(APPDRAWER_POSITION);

        ConfigRetrievalAgent configRetrievalAgent = new ConfigRetrievalAgent();
        ArrayList<SecLaunchSubContainer> subContainers =  configRetrievalAgent.getSubContainersById(LauncherSettings.APP_DRAWER_SC);

        if(subContainers.size() > 0)
        {
            ArrayList<ItemInfo> subContainerItems = subContainers.get(0).getItems();

            GridLayout gl = (GridLayout) rootView.findViewById(R.id.gridContainer);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            float containerWidth = dm.widthPixels;
            float pixItemWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, getResources().getDisplayMetrics());
            int columnCount = (int) Math.round(containerWidth / pixItemWidth);
            gl.setColumnCount(columnCount);
            final int itemWidth = (int) containerWidth / columnCount;

            float containerHeight = dm.heightPixels;
            int indicatorWidthDP = 90;
            float indicatorWidthPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorWidthDP, getResources().getDisplayMetrics());
            float viewPagerHeight =  (containerHeight - indicatorWidthPX);
            float pixItemHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 107, getResources().getDisplayMetrics());
            int rowCount = (int)Math.round(viewPagerHeight/pixItemHeight);
            gl.setRowCount(rowCount);
            final int itemHeight = (int) viewPagerHeight / rowCount;

            int itemsInContainer = rowCount * columnCount;
            int j = (position * itemsInContainer);
            for (int i = j; i < j + itemsInContainer && i < subContainerItems.size(); i++)
            {
                if (subContainerItems.get(i) != null)
                {
                    if (subContainerItems.get(i).getItemType() == LauncherSettings.ITEM_TYPE_SHORTCUT) {
                        final ShortcutInfo item = (ShortcutInfo) subContainerItems.get(i);

                        LayoutInflater factory = LayoutInflater.from(getActivity());
                        View myView = factory.inflate(R.layout.homescreen_item, null);

                        ImageView icon = (ImageView) myView.findViewById(R.id.icon);
                        icon.setImageDrawable(item.getIcon(getActivity()));

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
                        params.height = itemHeight;
                        params.setGravity(Gravity.CENTER);
                        myView.setLayoutParams(params);
                        gl.addView(myView);
                    }
                }
            }
        }

        return rootView;
    }
}
