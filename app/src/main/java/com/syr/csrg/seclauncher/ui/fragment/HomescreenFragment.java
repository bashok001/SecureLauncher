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
    // Initializing ImageViews to perform the delete, move to different container, navigate to App information.
    ImageView mTrashIcon, mContainer, mInfoIcon;
    //DD
    public static final String HOMESCREEN_POSITION = "position";
    int gridItemPosition = -1;
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
        rootView = inflater.inflate(R.layout.homescreen, container, false);

        int position = 0;

        if (getArguments() != null) {
            position = getArguments().getInt(HOMESCREEN_POSITION);
        }

        setViewPagerHeight(rootView);
        int containersCount = getHomeScreenContainersCount();
        pageAdapter = new HomescreenViewPagerAdapter(getActivity().getSupportFragmentManager(), containersCount);

        customizeViewPager(rootView);
        final ViewPager pager = (ViewPager) rootView.findViewById(R.id.viewpager);
        pager.setOnDragListener(new MyPagerDragListener());
        pager.setAdapter(pageAdapter);
        mIndicator = (CirclePageIndicator) rootView.findViewById(R.id.viewpagerindicator);
        mIndicator.setViewPager(pager);
        loadQuickAccessPanel(rootView);

        /* Variables are assigned to corresponding Ids defined in XML
        * Setting the visibility of imageview to invisible to make sure it is now shown by default
        * While Long click on icons, we need to make the imageview visible*/

        //Move to container
        mContainer = (ImageView) rootView.findViewById(R.id.container);
        mContainer.setVisibility(View.INVISIBLE);
        //Information about App
        mInfoIcon = (ImageView) rootView.findViewById(R.id.information);
        mInfoIcon.setVisibility(View.INVISIBLE);
        // Deleting the shortcut
        mTrashIcon = (ImageView) rootView.findViewById(R.id.trash);
        mTrashIcon.setVisibility(View.INVISIBLE);

        // Defining the Drag Listener for every ImageView

        /*Delete Icon Drag Listner
        * ------------------------
        * On Enter: Animation the imageview and updating the color of image icon, scaling up the delete icon
        * On Exit: clearing the colors and animation to let the users, restoring the icon to original size
        * On End: clearing the colors and animation to let the users, restoring the icon to original size
        * On Drop: assigning the imageview and textview to null */

         mTrashIcon.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                try {

                    View sourceView = (View) dragEvent.getLocalState();
                    switch (dragEvent.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            break;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            if (view.getId() == R.id.trash) {
                                Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);
                                ImageView imageView = (ImageView) gridMap_icon.get(sourceView);
                                imageView.setColorFilter(Color.RED);
                                imageView.startAnimation(shake);
                                view.animate().scaleX(1.2f);
                                view.animate().scaleY(1.2f);
                            }
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            if (view != null && view.getId() == R.id.trash) {
                                ImageView imageView = (ImageView) gridMap_icon.get(sourceView);
                                imageView.clearColorFilter();
                                imageView.clearAnimation();
                                view.animate().scaleX(1.0f);
                                view.animate().scaleY(1.0f);
                                view.invalidate();
                            }
                            break;
                        case DragEvent.ACTION_DROP:
                            try {
                                if (sourceView instanceof ViewGroup) {
                                    HomescreenViewPagerFragment.GridItemViewHolder k = (HomescreenViewPagerFragment.GridItemViewHolder) sourceView.getTag();
                                    if (k.itemType != LauncherSettings.ITEM_TYPE_FOLDER) {
                                        Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_LONG).show();
                                        ImageView imageView = (ImageView) gridMap_icon.get(sourceView);
                                        TextView textView = (TextView) gridMap_text.get(sourceView);
                                        imageView.setImageBitmap(null);
                                        textView.setText(null);
                                        view.invalidate();
                                    }
                                }
                            } catch (Exception e) {
                                Log.v("Tag", "print Exception: " + e);
                            }
                            break;
                        case DragEvent.ACTION_DRAG_LOCATION:
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            if (view != null && view.getId() == R.id.trash) {
                                ImageView imageView = (ImageView) gridMap_icon.get(sourceView);
                                imageView.clearColorFilter();
                                imageView.clearAnimation();
                                view.animate().scaleX(1.0f);
                                view.animate().scaleY(1.0f);
                            }
                            mTrashIcon.setVisibility(View.INVISIBLE);
                            break;
                    }
                } catch (Exception e) {
                    Log.v("Tag", "print Exception: " + e);
                }
                return true;
            }
        });

        /*Move to Container Drag Listner
        * ------------------------
        * On Enter: scaling up the delete icon
        * On Exit: restoring the icon to original size
        * On End: restoring the icon to original size
        * On Drop: assigning the imageview and textview to null */

        mContainer.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                View sourceView = (View) dragEvent.getLocalState();
                ViewGroup owner = (ViewGroup) sourceView.getParent().getParent();
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

                            view.invalidate();
                        }
                        break;
                    case DragEvent.ACTION_DROP:
                        //Toast.makeText(getApplicationContext(),"Deleted!",Toast.LENGTH_LONG).show();
                        if (view.getId() == R.id.container) {
                            if (owner.getId() == R.id.gridContainer) {
                                GridLayout gridLayout = (GridLayout) owner;
                                Toast.makeText(getActivity(), "Container!", Toast.LENGTH_LONG).show();
                            }
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

        /*Information Icon Drag Listner
        * ------------------------
        * On Enter: Animation the imageview and updating the color of image icon, scaling up the delete icon
        * On Exit: clearing the colors and animation to let the users, restoring the icon size
        * On End: clearing the colors and animation to let the users, restoring the icon size
        * On Drop: Getting the item information from the Shortcutinfo Class and get the package name
        *          and Intent to open Settings.ACTION_APPLICATION_DETAILS_SETTINGS */

        mInfoIcon.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                View sourceView = (View) dragEvent.getLocalState();
                ViewGroup owner = (ViewGroup) sourceView.getParent();

                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        if (view.getId() == R.id.information) {
                            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);
                            ImageView imageView = (ImageView) gridMap_icon.get(sourceView);
                            imageView.setColorFilter(Color.BLUE);
                            imageView.startAnimation(shake);
                            view.animate().scaleX(1.2f);
                            view.animate().scaleY(1.2f);
                        }
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        if (view.getId() == R.id.information) {
                            ImageView imageView = (ImageView) gridMap_icon.get(sourceView);
                            imageView.clearColorFilter();
                            imageView.clearAnimation();
                            view.animate().scaleX(1.0f);
                            view.animate().scaleY(1.0f);
                            view.invalidate();
                        }
                        break;
                    case DragEvent.ACTION_DROP:
                        try {
                            GridLayout gridLayout = (GridLayout) owner;
                            ShortcutInfo item = (ShortcutInfo) gridMap_shortcutInfo.get(sourceView);
                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            Log.v("Tag", "Icon Info: " + item.getPackageName());
                            i.setData(Uri.parse("package:" + item.getPackageName()));
                            Log.v("Tag", "Icon info before start ");
                            startActivity(i);
                        } catch (Exception e) {
                            Log.v("Tag", "print Exception: " + e);
                        }
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (view.getId() == R.id.information) {
                            ImageView imageView = (ImageView) gridMap_icon.get(sourceView);
                            imageView.clearColorFilter();
                            imageView.clearAnimation();
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

    public void onViewChange(int position) {
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        pager.setCurrentItem(position);
    }

    public void onPageChange() {
        pageAdapter.setCount(pageAdapter.getCount() + 1);
        pageAdapter.notifyDataSetChanged();
        mIndicator.notifyDataSetChanged();
    }

    private void setViewPagerHeight(View rootView) {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float containerHeight = dm.heightPixels;

        int quickAccessPanelWidthDP = 180;
        float quickAccessPanelWidthPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, quickAccessPanelWidthDP, getResources().getDisplayMetrics());

        int viewPagerHeight = (int) (containerHeight - quickAccessPanelWidthPX);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, viewPagerHeight));
    }

    private void loadQuickAccessPanel(View rootView) {
        ConfigRetrievalAgent configRetrievalAgent = new ConfigRetrievalAgent();
        ArrayList<SecLaunchSubContainer> subContainers = configRetrievalAgent.getSubContainersById(LauncherSettings.QUICK_ACCESS_PANEL_SC);

        if (subContainers.size() > 0) {
            ArrayList<ItemInfo> subContainerItems = subContainers.get(0).getItems();

            GridLayout gl = (GridLayout) rootView.findViewById(R.id.quickacesspanel);
            gl.setRowCount(1);

            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            float containerWidth = dm.widthPixels;
            float pixItemWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, getResources().getDisplayMetrics());
            int columnCount = (int) Math.round(containerWidth / pixItemWidth);
            int nearestOddColCount = (2 * Math.round(((containerWidth / pixItemWidth) + 1) / 2)) - 1;
            gl.setColumnCount(nearestOddColCount);
            final int itemWidth = (int) containerWidth / nearestOddColCount;
            int i = 0, j = 0;

            for (i = 0, j = 0; j < subContainerItems.size() && i < nearestOddColCount; i++) {
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

                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = itemWidth;
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.setGravity(Gravity.CENTER);
                    myView.setLayoutParams(params);

                    GridItemViewHolder gridItemViewHolder = new GridItemViewHolder();
                    gridItemViewHolder.itemType = LauncherSettings.ITEM_TYPE_APPDRAWER_ICON;
                    gridItemPosition++;
                    gridItemViewHolder.itemPosition = gridItemPosition;
                    myView.setTag(gridItemViewHolder);

                    gl.addView(myView);
                } else {
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



                            //DD
                            icon.setOnLongClickListener(new MyOnLongClickListener());
                            myView.setOnDragListener(new MyDragListener());
                            //DD

                            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                            params.width = itemWidth;
                            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                            params.setGravity(Gravity.CENTER);
                            myView.setLayoutParams(params);
                            GridItemViewHolder gridItemViewHolder = new GridItemViewHolder();
                            gridItemViewHolder.itemInfo = item;
                            gridItemViewHolder.itemType = LauncherSettings.ITEM_TYPE_SHORTCUT;
                            gridItemPosition++;
                            gridItemViewHolder.itemPosition = gridItemPosition;
                            myView.setTag(gridItemViewHolder);
                            gl.addView(myView);
                            //DD
                            ViewGroup view = (ViewGroup) myView;
                            gridMap_icon.put(view, icon);
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

                        GridItemViewHolder gridItemViewHolder = new GridItemViewHolder();
                        gridItemViewHolder.itemType = LauncherSettings.ITEM_TYPE_SPACER;
                        gridItemPosition++;
                        gridItemViewHolder.itemPosition = gridItemPosition;
                        myView.setTag(gridItemViewHolder);

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
            if (i < nearestOddColCount) {
                for (; i < nearestOddColCount; i++) {
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

                        GridItemViewHolder gridItemViewHolder = new GridItemViewHolder();
                        gridItemViewHolder.itemType = LauncherSettings.ITEM_TYPE_APPDRAWER_ICON;
                        gridItemPosition++;
                        gridItemViewHolder.itemPosition = gridItemPosition;
                        myView.setTag(gridItemViewHolder);

                        gl.addView(myView);
                    } else {
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

                        GridItemViewHolder gridItemViewHolder = new GridItemViewHolder();
                        gridItemViewHolder.itemType = LauncherSettings.ITEM_TYPE_SPACER;
                        gridItemPosition++;
                        gridItemViewHolder.itemPosition = gridItemPosition;
                        myView.setTag(gridItemViewHolder);

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

    private int getHomeScreenContainersCount() {
        ArrayList<SecLaunchSubContainer> subContainers = getContainerManager().getSubContainers();

        if (subContainers.size() == 0)
            return 1;
        else
            return subContainers.size();
    }

    private void customizeViewPager(View rootView) {
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.viewpager);

        pager.setPageTransformer(false, new ViewPager.PageTransformer() {
            public void transformPage(View page, float position) {
                ViewPager viewPager = (ViewPager) page.getParent();

                if (position == 0)
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
    private final class MyOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            ClipData data = ClipData.newPlainText("", "");
            if (view instanceof ImageView || view instanceof TextView) {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                ViewGroup.DragShadowBuilder shadowBuilder_viewGroup = new ViewGroup.DragShadowBuilder(viewGroup);
                viewGroup.startDrag(data, shadowBuilder_viewGroup, viewGroup, 0);
            } else {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                view.startDrag(data, shadowBuilder, view, 0);
            }
            ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);


            viewPager.animate().setDuration(500);
            viewPager.animate().scaleX(0.8F).scaleY(0.8F);

            return true;
        }

    }

    private final class MyPagerDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View view, DragEvent event) {
            try {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_EXITED:

                        Float a = event.getX();
                        Float b = event.getY();

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

                        break;

                    case DragEvent.ACTION_DROP:
                        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
                        viewPager.animate().setDuration(500);
                        viewPager.animate().scaleX(1.0F).scaleY(1.0F);
                        break;

                }
            } catch (Exception e) {
                Log.v("Tag", "MyPagerDragListener: onDrag: " + e);
            }

            return true;
        }

    }

    private final class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            float x;
            float y;

            try {
                switch (event.getAction()) {

                    case DragEvent.ACTION_DRAG_STARTED:
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        if (v instanceof ViewGroup) {
                            ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
                            viewPager.animate().setDuration(500);
                            viewPager.animate().scaleX(1.0F).scaleY(1.0F);
                        }
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        if (v instanceof ViewGroup) {
                            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);
                            v.startAnimation(shake);
                        }
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        if (v instanceof ViewGroup) {
                            v.clearAnimation();
                        }
                        break;

                    case DragEvent.ACTION_DROP: {
                        if (v instanceof ViewGroup) {
                            ViewGroup owner_target = (ViewGroup) v.getParent();
                            View view = (ViewGroup) event.getLocalState();
                            if (view instanceof ViewGroup) {

                                if (v == view) {
                                    v.setVisibility(View.VISIBLE);
                                    view.clearAnimation();
                                    v.clearAnimation();
                                    view.setVisibility(View.VISIBLE);
                                    return true;
                                }

                                if (v.getTag() == "folder" && view.getTag() != "folder") {
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
                                        ImageView imageView = (ImageView) gridMap_icon.get(view);
                                        TextView textView = (TextView) gridMap_text.get(view);
                                        imageView.setOnClickListener(null);
                                        imageView.setImageBitmap(null);
                                        textView.setText(null);
                                        gridMap_shortcutInfo.put((ViewGroup) view, new ItemInfo());
                                    }
                                } else {
                                    Log.v("Tag", "Test:v: " + v.getTag() + " View: " + view.getTag());

                                    ((ViewGroup) v).removeView(((ViewGroup) v).getChildAt(0));
                                    ((ViewGroup) view).removeView(((ViewGroup) view).getChildAt(0));

                                    ((ViewGroup) v).removeView(((ViewGroup) v).getChildAt(0));
                                    ((ViewGroup) view).removeView(((ViewGroup) view).getChildAt(0));
                                    Object lock6 = new Object();
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

                                    gridMap_icon.put((ViewGroup) v, view_fromView);
                                    gridMap_icon.put((ViewGroup) view, view_fromV);

                                    gridMap_text.put((ViewGroup) v, text_fromView);
                                    gridMap_text.put((ViewGroup) view, text_fromV);

                                    gridMap_shortcutInfo.put((ViewGroup) v, itemInfo_fromView);
                                    gridMap_shortcutInfo.put((ViewGroup) view, itemInfo_fromV);
                                    Log.v("Tag", "Test Quick Access Panel outer!");
                                    if (view.getTag() == "folder" && v.getTag() != "folder") {
                                        Log.v("Tag", "Test Quick Access Panel!");
                                        v.setTag("folder");
                                        view.setTag(null);
                                    }
                                }
                                view.clearAnimation();
                                v.clearAnimation();
                            }
                        }
                    }
                    break;
                }
            } catch (Exception e) {
                Log.v("Tag", "print Exception: " + 3);
            }
            return true;
        }
    }

    public class GridItemViewHolder {
        int itemType;
        int itemPosition;
        ItemInfo itemInfo;
    }
}