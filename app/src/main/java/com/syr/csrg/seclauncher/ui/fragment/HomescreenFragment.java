package com.syr.csrg.seclauncher.ui.fragment;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.syr.csrg.seclauncher.R;
import com.syr.csrg.seclauncher.agent.ConfigRetrievalAgent;
import com.syr.csrg.seclauncher.engine.ContainerManager;
import com.syr.csrg.seclauncher.packDefinitions.FolderInfo;
import com.syr.csrg.seclauncher.packDefinitions.ItemInfo;
import com.syr.csrg.seclauncher.packDefinitions.LauncherSettings;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;
import com.syr.csrg.seclauncher.packDefinitions.ShortcutInfo;
import com.syr.csrg.seclauncher.ui.adapter.HomescreenViewPagerAdapter;
import com.syr.csrg.seclauncher.ui.indicator.CirclePageIndicator;
import com.syr.csrg.seclauncher.ui.indicator.PageIndicator;


import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class HomescreenFragment extends SubContainerFragment implements HomescreenViewPagerFragment.onViewChangeListener {
    //DD
    public static ConcurrentHashMap<ViewGroup, View> gridMap_icon = new ConcurrentHashMap<ViewGroup, View>();
    public static ConcurrentHashMap<ViewGroup, View> gridMap_text = new ConcurrentHashMap<ViewGroup, View>();
    public static ConcurrentHashMap<ViewGroup, ItemInfo> gridMap_shortcutInfo = new ConcurrentHashMap<ViewGroup, ItemInfo>();
    ImageView mTrashIcon, mContainer, mInfoIcon;
    //DD
    public static final String HOMESCREEN_POSITION = "position";
	
    HomescreenViewPagerAdapter pageAdapter = null;
    PageIndicator mIndicator = null;
    View rootView;
    public static final HomescreenFragment newInstance(int position) {
        HomescreenFragment fragment = new HomescreenFragment();
        Bundle bdl = new Bundle(1);
        bdl.putInt(HOMESCREEN_POSITION, position);
        fragment.setArguments(bdl);
        return fragment;
    }

    public HomescreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.homescreen, container, false);

        int position = 0;

        if (getArguments() != null) {
            position = getArguments().getInt(HOMESCREEN_POSITION);
        }

        setViewPagerHeight(rootView);
        int containersCount = getHomeScreenContainersCount();
        pageAdapter = new HomescreenViewPagerAdapter(getActivity().getSupportFragmentManager(), containersCount);

        customizeViewPager(rootView);
        final ViewPager pager = (ViewPager) rootView.findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        mIndicator = (CirclePageIndicator)rootView.findViewById(R.id.viewpagerindicator);
        mIndicator.setViewPager(pager);

        loadQuickAccessPanel(rootView);
            mContainer = (ImageView) rootView.findViewById(R.id.container);
            mContainer.setVisibility(View.INVISIBLE);

            mInfoIcon = (ImageView) rootView.findViewById(R.id.information);
            mInfoIcon.setVisibility(View.INVISIBLE);

            mTrashIcon = (ImageView) rootView.findViewById(R.id.trash);
            mTrashIcon.setVisibility(View.INVISIBLE);

            mTrashIcon.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    try {

                        View sourceView = (View) dragEvent.getLocalState();
                        int startPage = pager.getCurrentItem();
                        switch (dragEvent.getAction()) {
                            case DragEvent.ACTION_DRAG_STARTED:
                                break;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                if (view.getId() == R.id.trash) {
                                    view.animate().scaleX(1.2f);
                                    view.animate().scaleY(1.2f);
                                }
                                break;
                            case DragEvent.ACTION_DRAG_EXITED:
                                if (view != null && view.getId() == R.id.trash) {
                                    view.animate().scaleX(1.0f);
                                    view.animate().scaleY(1.0f);
                                    Toast.makeText(getActivity(), "Exit!", Toast.LENGTH_SHORT).show();
                                    view.invalidate();
                                }
                                break;
                            case DragEvent.ACTION_DROP:
                                try {
                                    if (sourceView instanceof ViewGroup)
                                         Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_LONG).show();

                                         ImageView imageView = (ImageView) gridMap_icon.get(sourceView);
                                         TextView textView = (TextView) gridMap_text.get(sourceView);

                                         imageView.setImageBitmap(null);
                                         textView.setText(null);
                                         view.invalidate();

                                }catch(Exception e){
                                    Log.v("Tag", "print Exception: "+e);
                                }
                                break;
                            case DragEvent.ACTION_DRAG_LOCATION:
                                break;
                            case DragEvent.ACTION_DRAG_ENDED:
                                if (view != null && view.getId() == R.id.trash) {
                                    view.animate().scaleX(1.0f);
                                    view.animate().scaleY(1.0f);
                                }
                                mTrashIcon.setVisibility(View.INVISIBLE);
                                break;
                        }
                    }catch(Exception e){
                        Log.v("Tag", "print Exception: "+e);
                    }
                    return true;
                }
            });

            mContainer.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    View sourceView = (View) dragEvent.getLocalState();
                    ViewGroup owner = (ViewGroup) sourceView.getParent().getParent();
                    int startPage = pager.getCurrentItem();
                    switch (dragEvent.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            break;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            if (view.getId() == R.id.container) {
                                view.animate().scaleX(1.2f);
                                view.animate().scaleY(1.2f);
                            }
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            if (view.getId() == R.id.container) {
                                view.animate().scaleX(1.0f);
                                view.animate().scaleY(1.0f);
                                Toast.makeText(getActivity(), "Exit!", Toast.LENGTH_LONG).show();
                                view.invalidate();
                            }
                            break;
                        case DragEvent.ACTION_DROP:
                            //Toast.makeText(getApplicationContext(),"Deleted!",Toast.LENGTH_LONG).show();
                            if (view.getId() == R.id.container) {

                                //if (owner != null && owner instanceof GridLayout) {
                                if (owner.getId() == R.id.gridContainer) {
                                    GridLayout gridLayout = (GridLayout) owner;
                                    int pos = gridLayout.indexOfChild(sourceView);
                                    //int c = pos + startPage * 5;
                                    Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_LONG).show();
                                    //SecLaunchContext.removeContainer(c);
                                    //onUpdateIcons();
                                }
                                //}
                            }
                            break;
                        case DragEvent.ACTION_DRAG_LOCATION:
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            if (view.getId() == R.id.container) {
                                view.animate().scaleX(1.0f);
                                view.animate().scaleY(1.0f);
                            }
                            mContainer.setVisibility(View.INVISIBLE);
                            break;
                    }
                    return true;
                }
            });

            mInfoIcon.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    View sourceView = (View) dragEvent.getLocalState();
                    //View sourceView1 = (View) sourceView.getParent();
                    //System.out.println("SourceView: " + sourceView);
                    ViewGroup owner = (ViewGroup) sourceView.getParent();
                    // ViewGroup owner = (ViewGroup) sourceView.getParent().getParent();

                    int startPage = pager.getCurrentItem();
                    switch (dragEvent.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            break;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            if (view.getId() == R.id.information) {
                                view.animate().scaleX(1.2f);
                                view.animate().scaleY(1.2f);
                            }
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            if (view.getId() == R.id.information) {
                                view.animate().scaleX(1.0f);
                                view.animate().scaleY(1.0f);
                                Toast.makeText(getActivity(), "Exit!", Toast.LENGTH_LONG).show();
                                view.invalidate();
                            }
                            break;
                        case DragEvent.ACTION_DROP:
                            try {
                                //if (view != null && view.getId() == R.id.information) {
//                            ConfigRetrievalAgent configRetrievalAgent = new ConfigRetrievalAgent();
//                            ArrayList<SecLaunchSubContainer> subContainers =  configRetrievalAgent.getSubContainersById(LauncherSettings.ITEM_TYPE_SHORTCUT);
//                            ArrayList<ItemInfo> subContainerItems = subContainers.get(0).getItems();

                                    //if (owner != null && owner instanceof GridLayout) {
                                        //if (owner.getId() == R.id.gridContainer) {
                                            GridLayout gridLayout = (GridLayout) owner;
                                            int j = gridLayout.indexOfChild(sourceView/*sourceView.findViewById(R.id.icon)*/);
                                            int test = j + startPage * 16;
                                            System.out.println("Test : " + test);
                                            ShortcutInfo item = (ShortcutInfo) gridMap_shortcutInfo.get(sourceView);
                                            System.out.println("App1: " + j + " " + item);
                                            //Toast.makeText(getApplicationContext(),"Deleted!",Toast.LENGTH_LONG).show();
//                                    if (subContainerItems.get(test) != null ) {
//                                        //for(int test=0; test<32; test++) {
//                                        ShortcutInfo item = (ShortcutInfo) subContainerItems.get(test);
//                                        System.out.println("App: " + test + " " + item);
                                            //}
                                //final int apiLevel = Build.VERSION.SDK_INT;
                                //final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");
                                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            i.addCategory(Intent.CATEGORY_DEFAULT);
                                    Log.v("Tag", "Icon Info: "+item.getPackageName());
                                            i.setData(Uri.parse("package:" + item.getPackageName()));
                                            //i.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                                            //i.putExtra(appPkgName, item.getPackageName());
                                Log.v("Tag", "Icon info before start ");
                                startActivity(i);
                                            //}
                                        //}
                                    //}
                                //}
                            }catch(Exception e){
                                Log.v("Tag", "print Exception: " + e);
                            }
                            break;
                        case DragEvent.ACTION_DRAG_LOCATION:
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            if (view.getId() == R.id.information) {
                                view.animate().scaleX(1.0f);
                                view.animate().scaleY(1.0f);
                            }
                            mInfoIcon.setVisibility(View.INVISIBLE);
                            break;
                    }
                    return true;
                }
            });



        pager.setCurrentItem(position, false);
  		
		int glowDrawableId = getResources().getIdentifier("overscroll_glow", "drawable", "android");
        Drawable androidGlow = getResources().getDrawable(glowDrawableId);
        androidGlow.setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);

        ContainerManager cm = getContainerManager();
        cm.setSubContainerPager(pager);
        cm.setSubContainerAdapter(pageAdapter);

        return rootView;
    }
public void onViewChange(int position)
    {
        ViewPager pager = (ViewPager)rootView.findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        pager.setCurrentItem(position);
    }

    public void onPageChange()
    {
        pageAdapter.setCount(pageAdapter.getCount() + 1);
        pageAdapter.notifyDataSetChanged();
        mIndicator.notifyDataSetChanged();
    }

    private void setViewPagerHeight(View rootView)
    {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float containerHeight = dm.heightPixels;

        int quickAccessPanelWidthDP = 180;
        float quickAccessPanelWidthPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, quickAccessPanelWidthDP, getResources().getDisplayMetrics());

        int viewPagerHeight = (int) (containerHeight - quickAccessPanelWidthPX);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, viewPagerHeight));
    }

    private void loadQuickAccessPanel(View rootView)
    {
        ConfigRetrievalAgent configRetrievalAgent = new ConfigRetrievalAgent();
        ArrayList<SecLaunchSubContainer> subContainers =  configRetrievalAgent.getSubContainersById(LauncherSettings.QUICK_ACCESS_PANEL_SC);

        if(subContainers.size() > 0)
        {
            ArrayList<ItemInfo> subContainerItems = subContainers.get(0).getItems();

            GridLayout gl = (GridLayout) rootView.findViewById(R.id.quickacesspanel);
            gl.setRowCount(1);

            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            float containerWidth = dm.widthPixels;
            float pixItemWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, getResources().getDisplayMetrics());
            int columnCount = (int)Math.round(containerWidth/pixItemWidth);
            int nearestOddColCount = (2 * Math.round(((containerWidth/pixItemWidth) + 1) /2)) - 1;
            gl.setColumnCount(nearestOddColCount);
            final int itemWidth = (int) containerWidth/nearestOddColCount;
            int i = 0, j = 0;

            for (i = 0, j = 0; j < subContainerItems.size() && i < nearestOddColCount; i++)
            {
                //adding app drawer in the middle
                if(i + 1 == Math.ceil(nearestOddColCount/2.0))
                {
                    LayoutInflater factory = LayoutInflater.from(getActivity());
                    View myView = factory.inflate(R.layout.homescreen_item, null);

                    ImageView icon = (ImageView) myView.findViewById(R.id.icon);
                    icon.setImageResource(R.drawable.appdrawer);

                    icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(".ui.activity.AppDrawerActivity");
                            startActivity(intent);
                        }
                    });

                    TextView appname = (TextView) myView.findViewById(R.id.appname);
                    appname.setText("Apps");

                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = itemWidth;
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.setGravity(Gravity.CENTER);
                    myView.setLayoutParams(params);
                    gl.addView(myView);


                }
                else {
                    if (subContainerItems.get(j) != null) {
                        if (subContainerItems.get(j).getItemType() == LauncherSettings.ITEM_TYPE_SHORTCUT) {
                            final ShortcutInfo item = (ShortcutInfo) subContainerItems.get(j);

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

                            //DD
                            appname.setOnLongClickListener(new MyOnLongClickListener());
                            icon.setOnLongClickListener(new MyOnLongClickListener());
                            myView.setOnDragListener(new MyDragListener());
                            //DD

                            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                            params.width = itemWidth;
                            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                            params.setGravity(Gravity.CENTER);
                            myView.setLayoutParams(params);
                            gl.addView(myView);

                            //DD
                            ViewGroup view = (ViewGroup) myView;
                            gridMap_icon.put(view, icon);
                            gridMap_text.put(view, appname);
                            gridMap_shortcutInfo.put(view, item);
                            //DD
                        }
                    } else {
                        //DD
                        /*Space spacer = new Space(getActivity());

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = itemWidth;
                        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                        params.setGravity(Gravity.CENTER);
                        spacer.setLayoutParams(params);*/
                        /*gl.addView(spacer);*/

                        //DD
                        LayoutInflater factory = LayoutInflater.from(getActivity());
                        View myView = factory.inflate(R.layout.homescreen_item, null);
                        ImageView icon = (ImageView) myView.findViewById(R.id.icon);
                        icon.setImageDrawable(null);
                        TextView appname = (TextView) myView.findViewById(R.id.appname);
                        appname.setText(null);
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = itemWidth;
                        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                        params.setGravity(Gravity.CENTER);
                        myView.setLayoutParams(params);
                        gl.addView(myView);
                        ViewGroup viewGroup = (ViewGroup) myView;
                        Object lock3 = new Object();
                        synchronized (lock3) {
                            HomescreenFragment.gridMap_icon.put(viewGroup, icon);
                            HomescreenFragment.gridMap_text.put(viewGroup, appname);
                            HomescreenFragment.gridMap_shortcutInfo.put(viewGroup, new ItemInfo());
                        }
                        myView.setOnDragListener(new MyDragListener());
                        icon.setOnLongClickListener(new MyOnLongClickListener());
                        icon.setOnLongClickListener(new MyOnLongClickListener());
                        //DD

                        gl.addView(myView);
                    }
                    j++;
                }
            }

            //filling up empty extra spaces before adding app drawer
            if(i < nearestOddColCount)
            {
                for(; i < nearestOddColCount; i++)
                {
                    //adding app drawer in the middle
                    if (i + 1 == Math.ceil(nearestOddColCount / 2.0)) {
                        LayoutInflater factory = LayoutInflater.from(getActivity());
                        View myView = factory.inflate(R.layout.homescreen_item, null);

                        ImageView icon = (ImageView) myView.findViewById(R.id.icon);
                        icon.setImageResource(R.drawable.appdrawer);

                        icon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(".ui.activity.AppDrawerActivity");
                                startActivity(intent);
                            }
                        });

                        TextView appname = (TextView) myView.findViewById(R.id.appname);
                        appname.setText("Apps");

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = itemWidth;
                        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                        params.setGravity(Gravity.CENTER);
                        myView.setLayoutParams(params);
                        gl.addView(myView);
                    }
                    else
                    {
                        //DD
                        /*Space spacer = new Space(getActivity());

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = itemWidth;
                        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                        params.setGravity(Gravity.CENTER);
                        spacer.setLayoutParams(params);
                        gl.addView(spacer);*/
                        LayoutInflater factory = LayoutInflater.from(getActivity());
                        View myView = factory.inflate(R.layout.homescreen_item, null);
                        ImageView icon = (ImageView) myView.findViewById(R.id.icon);
                        icon.setImageDrawable(null);
                        TextView appname = (TextView) myView.findViewById(R.id.appname);
                        appname.setText(null);
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = itemWidth;
                        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                        params.setGravity(Gravity.CENTER);
                        myView.setLayoutParams(params);
                        gl.addView(myView);
                        ViewGroup viewGroup = (ViewGroup) myView;
                        Object lock3 = new Object();
                        synchronized (lock3) {
                            HomescreenFragment.gridMap_icon.put(viewGroup, icon);
                            HomescreenFragment.gridMap_text.put(viewGroup, appname);
                            HomescreenFragment.gridMap_shortcutInfo.put(viewGroup, new ItemInfo());
                        }
                        myView.setOnDragListener(new MyDragListener());
                        icon.setOnLongClickListener(new MyOnLongClickListener());
                        icon.setOnLongClickListener(new MyOnLongClickListener());
                        //DD

                       // gl.addView(myView);

                        //DD
                    }
                }
            }
        }
    }

    private int getHomeScreenContainersCount()
    {
        ArrayList<SecLaunchSubContainer> subContainers =  getContainerManager().getSubContainers();

        if(subContainers.size() == 0)
            return 1;
        else
            return subContainers.size();
    }

    private void customizeViewPager(View rootView)
    {
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.viewpager);

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
//DD
    private final class MyOnLongClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view){
            //Log.v("Tag", "Inner onLongClick Listener!");
            //changesizeViewPager();
            //ActionBar actionBar = getActivity().getActionBar();
            //actionBar.setLogo(R.drawable.ic_launcher);
            ClipData data = ClipData.newPlainText("", "");
            if(view instanceof ImageView || view instanceof TextView){
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                //View view_group = (View) viewGroup;
                //ClipData data1 = ClipData.newPlainText("", "");
                ViewGroup.DragShadowBuilder shadowBuilder_viewGroup = new ViewGroup.DragShadowBuilder(viewGroup);
                viewGroup.startDrag(data, shadowBuilder_viewGroup, viewGroup, 0);
                //Modify here
                //viewGroup.setVisibility(ViewGroup.INVISIBLE);
            }
            //else if(view instanceof TextView)
            else {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                view.startDrag(data, shadowBuilder, view, 0);
                //modify here
                //view.setVisibility(View.INVISIBLE);
            }
            ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);


            viewPager.animate().setDuration(500);
            //viewPager.animate().rotationYBy(720);
            viewPager.animate().scaleX(0.8F).scaleY(0.8F);

            return true;
        }

    }
private final class MyPagerDragListener implements View.OnDragListener{
        @Override
        public boolean onDrag(View view, DragEvent event){
            try {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_EXITED:
                        //if(view instanceof GridLayout) {

                        Float a = event.getX();
                        //Log.v("Tag LayoutListener X ", a.toString());
                        Float b = event.getY();
                        //Log.v("Tag LayoutListener Y ", b.toString());

                        float x = event.getX();
                        if (x < 80) {

                            ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

                            int currentPosition = viewPager.getCurrentItem();
                            viewPager.setCurrentItem(currentPosition - 1);

                        }

                        if (x > 650) {
                            ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

                            int currentPosition = viewPager.getCurrentItem();
                            viewPager.setCurrentItem(currentPosition + 1);
                        }

                        // }
                        break;

                    case DragEvent.ACTION_DROP:
                        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
                        viewPager.animate().setDuration(500);
                        //viewPager.animate().rotationYBy(-720);
                        viewPager.animate().scaleX(1.0F).scaleY(1.0F);
                        break;

                }
            }catch(Exception e){
                Log.v("Tag", "print: Exception: "+e);
            }

            return true;
        }

    }

    private final class MyDragListener implements View.OnDragListener{
        @Override
        public boolean onDrag(View v, DragEvent event){
            int action = event.getAction();
            float x;
            float y;

            try {
                switch (event.getAction()) {

                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.v("Tag", "I am in DRAG_started!!!");
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        //Log.v("Tag", "event: DRAG_ENDED");
                        if (v instanceof ViewGroup) {
                            //View view3 = (View) event.getLocalState();
                            //ImageView view4 = (ImageView) view3;

                            ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
                            viewPager.animate().setDuration(500);
                            //viewPager.animate().rotationYBy(-720);
                            viewPager.animate().scaleX(1.0F).scaleY(1.0F);
                            //Modify here
                            //v.setVisibility(View.VISIBLE);
                            //v.setVisibility(View.VISIBLE);
                        }
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        //Log.v("Tag", "*********I am in DRAG_ENTERED!!!");
                        if (v instanceof ViewGroup) {
                            //ImageView imageView = (ImageView) view;
                            //ImageView get = (ImageView)v;
                            //Context context = Activity;
                            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);
                            v.startAnimation(shake);
                            //Log.v("Tag", "*********I am in DRAG_ENTERED!!!");
                            //Log.v("Tag", "Address: "+(int[])v.getTag());
                            //int[] po;
                            //po = (int[])v.getTag();
                            //int row = po[0];
                            //int colum = po[1];

                            //Log.v("Tag", "Entered row: "+row+"#####colum: "+colum);

                            //Drawable temp;
                            //ImageView tmpImage = null;
//                        boolean check = false;
//                        if(check == false){
//                            for(int i = row; i <= 4; i++){
//                                if(check == true)
//                                    break;
//                                for(int j = colum; j<=4; j++){
//                                    //Log.v("Tag", "GridMap: ########"+row+","+colum);
//                                    if(gridMap.get(i+","+j).getDrawable() == null){
//                                        tmpImage = gridMap.get(i+","+j);
//                                        Log.v("Tag", "find a null one!");
//                                        //temp = gridMap.get(row+","+colum).getDrawable();
//                                        check = true;
//                                        break;
//                                    }
//                                }
//                            }
//                        }


                            //View viewtotal = (View) event.getLocalState();
                            //ViewGroup ownertotal = (ViewGroup) viewtotal.getParent();
                            //int indexFrom = ownertotal.indexOfChild(tmpImage);
                            //int indexTo = ownertotal.indexOfChild(v);

                            //float x_v = v.getX();
                            //float y_v = v.getY();
                            //x = tmpImage.getX();
                            //y = tmpImage.getY();
                            //ownertotal.removeView(v);
                            //v.animate().setDuration(1000);
                            //v.animate().x(x).y(y);
                            //ownertotal.addView(v, indexTo);

                            //TranslateAnimation ta = new TranslateAnimation(x_v, y_v, x, y);
                            //ta.setDuration(1000);
                            //v.startAnimation(ta);
                            //ownertotal.removeView(tmpImage);
                            //tmpImage.animate().setDuration(1000);
                            //tmpImage.animate().x(x_v).y(y_v);
                            //ownertotal.addView(tmpImage, indexFrom);
                            //tmpImage.setImageDrawable(((ImageView) v).getDrawable());
                            //((ImageView) v).setImageBitmap(null);
                            //v.animate().rotationYBy(720);
                            //v.animate().x(x_v).y(y_v);

//                        TreeMap<Double, String> map = new TreeMap<Double, String>(new Comparator<Double>() {
//                            @Override
//                            public int compare(Double aDouble, Double aDouble2) {
//                                if(aDouble > aDouble2)
//                                    return 1;
//                                else
//                                    return -1;
//                            }
//                        });
//                        for(int i = 1; i <= 4; i++){
//                            for(int j = 1; j <= 4; j++){
//                                double distance = Math.sqrt((i - row)*(i - row));
//                                Log.v("Tag", "Distance: "+distance);
//                                map.put(distance, i+","+j);
//                            }
//                        }

//                        TreeMap.Entry<Double, String> entry = map.firstEntry();
//                        String a = entry.getValue();
//                        Log.v("Tag", "This is the most nearest one: " + a);
                            //imageView.setImageResource(R.drawable.alice);
                        }
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        if (v instanceof ViewGroup) {
                            //Log.v("Tag", "Exit!!!!");
                            v.clearAnimation();
                            //ImageView imageView = (ImageView) view;
                            //x = v.getX();
                            //y = v.getY();
                            //v.animate().x(x).y(y);
                            //imageView.setImageResource(R.drawable.alice);
                            //Modify here
                            //v.setVisibility(View.VISIBLE);
                        }
                        break;

                    case DragEvent.ACTION_DROP: {

                        Log.v("Tag", "Action drop!!!!");
                        if (v instanceof ViewGroup) {
                            //View view = (View) event.getLocalState();
                            //ViewGroup location_viewGroup = (ViewGroup) view.getParent();
                            //final ViewGroup v1 = (ViewGroup)v;
                            ViewGroup owner_target = (ViewGroup) v.getParent();
                            View view = (ViewGroup) event.getLocalState();
                            if (view instanceof ViewGroup) {

                                if (v == view) {
                                    v.setVisibility(View.VISIBLE);
                                    view.clearAnimation();
                                    v.clearAnimation();
                                    view.setVisibility(View.VISIBLE);
                                    Log.v("Tag", "inner loop Action drop!!!!");
                                    return true;
                                }
//                           x= subContainerItems.get(index8);
//                           y=subContainerItems.get(index2);
//                           x.getType == LauncherSettings.ITEM_TYPE_FOLDER
//                                   (FolderInfo)x.add((ShotcutInfo)y);

                                if (v.getTag() == "folder" && view.getTag() != "folder") {
                                    Log.v("Tag", "Test We are taking care folder!");

                                    Object lock5 = new Object();
                                    synchronized (lock5) {
                                        FolderInfo fold = (FolderInfo) gridMap_shortcutInfo.get(v);
                                        ShortcutInfo item = (ShortcutInfo) gridMap_shortcutInfo.get(view);
                                        fold.add(item);
                                    }
                                    //fold.add(item);
                                    view.clearAnimation();
                                    v.clearAnimation();
                                    Object lock = new Object();
                                    synchronized (lock) {
                                        //ImageView imageView = (ImageView) ((ViewGroup) view).getChildAt(0);
                                        //TextView textView = (TextView) ((ViewGroup) view).getChildAt(1);
                                        ImageView imageView = (ImageView) gridMap_icon.get(view);
                                        TextView textView = (TextView) gridMap_text.get(view);
                                        imageView.setOnClickListener(null);
                                        imageView.setImageBitmap(null);
                                        textView.setText(null);
                                        gridMap_shortcutInfo.put((ViewGroup) view, new ItemInfo());
                                    }
                                    //return true;
                                }
                                //ViewGroup owner = (ViewGroup) view.getParent();
                                //int index_origin = owner.indexOfChild(view);
                                //int index_target = owner_target.indexOfChild(v);
                            /*Solution One~~~~~~just swap the ImageView and TextView*/
                                //ImageView imageView_origin = (ImageView) ((ViewGroup) view).getChildAt(0);
                                //ImageView imageView_origin = gridMap.get(view);
                                //TextView textView_origin = (TextView) ((ViewGroup) view).getChildAt(1);

                                //ImageView imageView_target = (ImageView) ((ViewGroup) v).getChildAt(0);
                                //ImageView imageView_target = gridMap.get(v);
                                //TextView textView_target = (TextView) ((ViewGroup) v).getChildAt(1);

                                //Drawable temp_icon = imageView_origin.getDrawable();
                                //String temp_text = textView_origin.getText().toString();

                                //imageView_origin.setImageDrawable(imageView_target.getDrawable());

                                //textView_origin.setText(textView_target.getText());

                                //imageView_target.setImageDrawable(temp_icon);
                                //textView_target.setText(temp_text);
                                else {

                                    Log.v("Tag", "Test:v: " + v.getTag() + " View: " + view.getTag());

                                    ((ViewGroup) v).removeView(((ViewGroup) v).getChildAt(0));
                                    ((ViewGroup) view).removeView(((ViewGroup) view).getChildAt(0));

                                    ((ViewGroup) v).removeView(((ViewGroup) v).getChildAt(0));
                                    ((ViewGroup) view).removeView(((ViewGroup) view).getChildAt(0));
                                    //TextView textView_origin = (TextView) ((ViewGroup) view).getChildAt(1);
                                    //TextView textView_target = (TextView) ((ViewGroup) v).getChildAt(1);
                                    //String temp_text = textView_origin.getText().toString();
                                    //textView_origin.setText(textView_target.getText());
                                    //textView_target.setText(temp_text);

                                    //((ViewGroup) v).addView(HomescreenActivity.gridMap_text.get(view), 1);
                                    //((ViewGroup) view).addView(HomescreenActivity.gridMap_text.get(v), 1);
                                    //((ViewGroup) v).addView(HomescreenActivity.gridMap_icon.get(view), 0);
                                    //((ViewGroup) view).addView(HomescreenActivity.gridMap_icon.get(v), 0);

                                    Object lock6 = new Object();
                                    //synchronized (lock6) {
                                    View view_fromV = gridMap_icon.get(v);
                                    View view_fromView = gridMap_icon.get(view);

                                    View text_fromV = gridMap_text.get(v);
                                    View text_fromView = gridMap_text.get(view);

                                    ItemInfo itemInfo_fromV = gridMap_shortcutInfo.get(v);
                                    ItemInfo itemInfo_fromView = gridMap_shortcutInfo.get(view);

                                    ((ViewGroup) v).addView(view_fromView, 0);
                                    ((ViewGroup) view).addView(view_fromV, 0);

                                    ((ViewGroup) v).addView(text_fromView, 1);
                                    ((ViewGroup) view).addView(text_fromV, 1);


                                    //View temp = view_fromV;
                                    //view_fromV = view_fromView;
                                    //view_fromView = temp;
                                    gridMap_icon.put((ViewGroup) v, view_fromView);
                                    gridMap_icon.put((ViewGroup) view, view_fromV);

                                    gridMap_text.put((ViewGroup) v, text_fromView);
                                    gridMap_text.put((ViewGroup) view, text_fromV);

                                    gridMap_shortcutInfo.put((ViewGroup) v, itemInfo_fromView);
                                    gridMap_shortcutInfo.put((ViewGroup) view, itemInfo_fromV);
                                    // }

                                    Log.v("Tag", "Test Quick Access Panel outer!");
                                    if (view.getTag() == "folder" && v.getTag() != "folder") {
                                        Log.v("Tag", "Test Quick Access Panel!");
                                        v.setTag("folder");
                                        view.setTag(null);
                                    }
                                }
                                //Modify here
                                //view.setVisibility(View.VISIBLE);
                                view.clearAnimation();
                                v.clearAnimation();
                                //Modify here
                                //view.setVisibility(View.VISIBLE);
                                //v.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    break;
                }
            }catch(Exception e){
                Log.v("Tag", "print Exception: "+3);
            }
            return true;
        }
    }
}