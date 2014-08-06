package com.syr.csrg.seclauncher.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.syr.csrg.seclauncher.R;
import com.syr.csrg.seclauncher.agent.ConfigRetrievalAgent;
import com.syr.csrg.seclauncher.packDefinitions.FolderInfo;
import com.syr.csrg.seclauncher.packDefinitions.ItemInfo;
import com.syr.csrg.seclauncher.packDefinitions.LauncherSettings;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;
import com.syr.csrg.seclauncher.packDefinitions.ShortcutInfo;
import com.syr.csrg.seclauncher.ui.activity.HomescreenActivity;

import java.util.ArrayList;

/**
 * Created by neethu on 7/23/2014.
 */
public class HomescreenViewPagerFragment extends SubContainerViewPagerFragment {
    public static final String HOMESCREEN_POSITION = "position";
    public static final String HOMESCREEN_CLICKABLE = "homescreen_clickable";

    //DD
    ImageView mContainer, mTrashIcon, mInfoIcon;
    ArrayList<ViewGroup> gridList = new ArrayList<ViewGroup>();
    Dialog dialog;
    //DD

    public static final HomescreenViewPagerFragment newInstance(int position, boolean clickable) {
        HomescreenViewPagerFragment fragment = new HomescreenViewPagerFragment();
        Bundle bdl = new Bundle(2);
        bdl.putInt(HOMESCREEN_POSITION, position);
        bdl.putBoolean(HOMESCREEN_CLICKABLE, clickable);
        fragment.setArguments(bdl);
        return fragment;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            viewChangeCallback = (onViewChangeListener) ((HomescreenActivity) activity).homescreenFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }


    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homescreen_viewpager_layout, container, false);
        final int position = getArguments().getInt(HOMESCREEN_POSITION);
        final boolean clickable = getArguments().getBoolean(HOMESCREEN_CLICKABLE);

        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        viewPager.setOnDragListener(new MyPagerDragListener());

        ArrayList<SecLaunchSubContainer> subContainers = getContainerManager().getSubContainers();

        Log.d("size?", Integer.toString(getContainerManager().getNumSubContainers()));

        //DD
        mTrashIcon = (ImageView) getActivity().findViewById(R.id.trash);
        mContainer = (ImageView) getActivity().findViewById(R.id.container);
        mInfoIcon = (ImageView) getActivity().findViewById(R.id.information);
        //DD

        if (subContainers.size() > 0) {
            final ArrayList<ItemInfo> subContainerItems = subContainers.get(Math.min(position, getContainerManager().getNumSubContainers() - 1)).getItems();

            final GridLayout gl = (GridLayout) rootView.findViewById(R.id.gridContainer);

            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            float containerWidth = dm.widthPixels;
            float pixItemWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, getResources().getDisplayMetrics());
            int columnCount = (int) Math.round(containerWidth / pixItemWidth);
            gl.setColumnCount(columnCount);
            final int itemWidth = (int) containerWidth / columnCount;

            float containerHeight = dm.heightPixels;
            int quickAccessPanelWidthDP = 180;
            float quickAccessPanelWidthPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, quickAccessPanelWidthDP, getResources().getDisplayMetrics());
            float viewPagerHeight = (containerHeight - quickAccessPanelWidthPX);
            float pixItemHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 107, getResources().getDisplayMetrics());
            int rowCount = (int) Math.round(viewPagerHeight / pixItemHeight);
            gl.setRowCount(rowCount);
            final int itemHeight = (int) viewPagerHeight / rowCount;
            final int totalGriditems = rowCount * columnCount;
            int i;
            int gridItemPosition = -1;

            if (clickable) {

                gl.setLongClickable(true);

                gl.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View arg0) {
                        View dialogMenuView = getActivity().getLayoutInflater().inflate(R.layout.homescreen_contextual_dialog, null);
                        final Dialog dialogMenu = new Dialog(getActivity(), R.style.FullHeightDialog);
                        dialogMenu.getWindow().getAttributes().windowAnimations = R.style.HomeScreenMenuAnimation;
                        dialogMenu.setContentView(dialogMenuView);
                        dialogMenu.setCancelable(true);

                        TextView setWallpaper = (TextView) dialogMenuView.findViewById(R.id.setWallpaperTxt);
                        setWallpaper.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogMenu.dismiss();

                                Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                                startActivity(Intent.createChooser(intent, "Select Wallpaper"));
                            }
                        });

                        TextView appsAndWidgets = (TextView) dialogMenuView.findViewById(R.id.appsAndWidgetsTxt);
                        appsAndWidgets.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogMenu.dismiss();
                                Intent intent = new Intent();
                                intent.setAction(".ui.activity.AppDrawerActivity");
                                startActivity(intent);
                            }
                        });

                        TextView newPage = (TextView) dialogMenuView.findViewById(R.id.newPageTxt);
                        newPage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogMenu.dismiss();

                                ConfigRetrievalAgent configRetrievalAgent = new ConfigRetrievalAgent();

                                SecLaunchSubContainer newHomeScreenSubContatiner = new SecLaunchSubContainer() {
                                };
                                newHomeScreenSubContatiner.setSubContainerID(LauncherSettings.HOME_SCREEN_SC);
                                configRetrievalAgent.getContainerByCurrentMode().addToSubContainers(newHomeScreenSubContatiner);
                                viewChangeCallback.onPageChange();
                            }
                        });

                        TextView newFolder = (TextView) dialogMenuView.findViewById(R.id.newFolderTxt);
                        newFolder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogMenu.dismiss();

                                int spaceAvailable = 1, k = 0;

                                for (int j = 0; j < subContainerItems.size(); j++) {
                                    if (subContainerItems.get(j) == null) {
                                        spaceAvailable = 1;
                                        break;
                                    } else if (subContainerItems.get(j).getItemType() == LauncherSettings.ITEM_TYPE_SHORTCUT ||
                                            subContainerItems.get(j).getItemType() == LauncherSettings.ITEM_TYPE_FOLDER) {
                                        k++;

                                        if (k == totalGriditems) {
                                            spaceAvailable = 0;
                                            break;
                                        }
                                    }
                                }

                                if (spaceAvailable == 1) {
                                    final Dialog createFolderDialog = new Dialog(getActivity(), R.style.FullHeightDialog);
                                    createFolderDialog.setContentView(R.layout.create_folder_dialog);
                                    createFolderDialog.setCancelable(true);
                                    createFolderDialog.setTitle("Create Folder");
                                    createFolderDialog.getWindow().getAttributes().windowAnimations = R.style.FolderCreateAnimation;

                                    final EditText newFolderName = (EditText) createFolderDialog.findViewById(R.id.folderName);
                                    newFolderName.requestFocus();

                                    Button ok = (Button) createFolderDialog.findViewById(R.id.okBtn);
                                    ok.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            ConfigRetrievalAgent configRetrievalAgent = new ConfigRetrievalAgent();

                                            FolderInfo in = new FolderInfo();
                                            in.setId(01);
                                            in.setContainer(01);
                                            in.setItemType(LauncherSettings.ITEM_TYPE_FOLDER);
                                            in.setFolderName(newFolderName.getText().toString());
                                            in.setFolderBackgroundColor(Color.parseColor("#669933"));
                                            int j = 0;

                                            for (j = 0; j < subContainerItems.size(); j++) {
                                                if (subContainerItems.get(j) == null)
                                                    break;
                                            }
                                            if (j == subContainerItems.size())
                                                configRetrievalAgent.getSubContainersById(LauncherSettings.HOME_SCREEN_SC).get(position).getItems().add(in);
                                            else
                                                configRetrievalAgent.getSubContainersById(LauncherSettings.HOME_SCREEN_SC).get(position).getItems().set(j, in);

                                            createFolderDialog.dismiss();
                                            viewChangeCallback.onViewChange(position);
                                        }
                                    });

                                    Button cancel = (Button) createFolderDialog.findViewById(R.id.cancelBtn);
                                    cancel.setOnClickListener(new View.OnClickListener() {

                                        public void onClick(View v) {
                                            createFolderDialog.dismiss();
                                        }
                                    });

                                    createFolderDialog.show();
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), "No space available to create new folder",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        dialogMenu.show();
                        return true;
                    }
                });
            }

            for (i = 0; i < subContainerItems.size() && gridItemPosition < totalGriditems - 1; i++) {
                GridItemViewHolder gridItemViewHolder = new GridItemViewHolder();
                if (subContainerItems.get(i) != null) {
                    if (subContainerItems.get(i).getItemType() == LauncherSettings.ITEM_TYPE_SHORTCUT) {
                        final ShortcutInfo item = (ShortcutInfo) subContainerItems.get(i);

                        LayoutInflater factory = LayoutInflater.from(getActivity());
                        View myView = factory.inflate(R.layout.homescreen_item, null);

                        //DD
                        myView.setOnLongClickListener(new MyOnLongClickListener());
                        myView.setOnDragListener(new MyDragListener());
                        //DD

                        gridItemViewHolder.itemType = LauncherSettings.ITEM_TYPE_SHORTCUT;
                        gridItemViewHolder.itemInfo = item;
                        gridItemPosition++;
                        gridItemViewHolder.itemPosition = gridItemPosition;
                        myView.setTag(gridItemViewHolder);

                        ImageView icon = (ImageView) myView.findViewById(R.id.icon);
                        icon.setImageDrawable(item.getIcon(getActivity()));

                        if (clickable) {

                            icon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(item.getIntent());
                                    startActivity(intent);
                                }
                            });

                        }

                        //DD
                        icon.setOnLongClickListener(new MyOnLongClickListener());
                        //DD

                        TextView appname = (TextView) myView.findViewById(R.id.appname);
                        appname.setText(item.getAppName());

                        //DD
                        appname.setOnLongClickListener(new MyOnLongClickListener());
                        //DD

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        if (!clickable) {
                            params.width = itemWidth / columnCount;
                            params.height = itemHeight / rowCount;
                        } else {
                            params.width = itemWidth;
                            params.height = itemHeight;
                        }
                        params.setGravity(Gravity.CENTER);
                        myView.setLayoutParams(params);
                        gl.addView(myView);
                        if (!clickable) {
                            myView.animate().scaleX(0.75f).scaleY(0.75f).start();
                        }

                        //DD
                        ViewGroup viewGroup = (ViewGroup) myView;
                        gridList.add(viewGroup);
                        Object lock = new Object();
                        synchronized (lock) {
                            HomescreenFragment.gridMap_icon.put(viewGroup, icon);
                            HomescreenFragment.gridMap_text.put(viewGroup, appname);
                            HomescreenFragment.gridMap_shortcutInfo.put(viewGroup, item);//
                        }
                        //DD

                    } else if (subContainerItems.get(i).getItemType() == LauncherSettings.ITEM_TYPE_FOLDER) {
                        LayoutInflater factory = LayoutInflater.from(getActivity());
                        View myView;

                        final FolderInfo item = (FolderInfo) subContainerItems.get(i);
                        final ArrayList<ShortcutInfo> appsInFolder = item.getAppsInFolder();

                        final int noofitems = appsInFolder.size();
                        myView = factory.inflate(R.layout.homescreen_item, null);

                        //DD
                        myView.setOnDragListener(new MyDragListener());
                        //DD

                        ImageView icon = (ImageView) myView.findViewById(R.id.icon);

                        gridItemViewHolder.itemType = LauncherSettings.ITEM_TYPE_FOLDER;
                        gridItemViewHolder.itemInfo = item;
                        gridItemPosition++;
                        gridItemViewHolder.itemPosition = gridItemPosition;
                        myView.setTag(gridItemViewHolder);

                        if (noofitems == 0) {
                            icon.setImageDrawable(getResources().getDrawable(R.drawable.foldericon));
                            icon.setBackgroundResource(R.drawable.folder_background);
                        } else {
                            int drawableSize, j = 0, k;

                            if (noofitems > 4)
                                drawableSize = 4;
                            else
                                drawableSize = noofitems;

                            Drawable[] layers = new Drawable[drawableSize];
                            for (k = drawableSize, j = 0; k > 0; k--, j++)
                                layers[j] = appsInFolder.get(k - 1).getIcon(getActivity());

                            float iconWidthPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());

                            final LayerDrawable layerDrawable = new LayerDrawable(layers);
                            if (drawableSize == 4) {
                                layerDrawable.setLayerInset(3, (int) iconWidthPX / 16, (int) iconWidthPX / 16, (int) (17 * iconWidthPX / 16), (int) (17 * iconWidthPX / 16));
                                layerDrawable.setLayerInset(2, (int) (17 * iconWidthPX / 16), (int) iconWidthPX / 16, (int) iconWidthPX / 16, (int) (17 * iconWidthPX / 16));
                                layerDrawable.setLayerInset(1, (int) iconWidthPX / 16, (int) (17 * iconWidthPX / 16), (int) (17 * iconWidthPX / 16), (int) iconWidthPX / 16);
                                layerDrawable.setLayerInset(0, (int) (17 * iconWidthPX / 16), (int) (17 * iconWidthPX / 16), (int) iconWidthPX / 16, (int) iconWidthPX / 16);
                            } else if (drawableSize == 3) {
                                layerDrawable.setLayerInset(2, (int) iconWidthPX / 16, (int) iconWidthPX / 16, (int) (17 * iconWidthPX / 16), (int) (17 * iconWidthPX / 16));
                                layerDrawable.setLayerInset(1, (int) (17 * iconWidthPX / 16), (int) iconWidthPX / 16, (int) iconWidthPX / 16, (int) (17 * iconWidthPX / 16));
                                layerDrawable.setLayerInset(0, (int) iconWidthPX / 16, (int) (17 * iconWidthPX / 16), (int) (17 * iconWidthPX / 16), (int) iconWidthPX / 16);
                            } else if (drawableSize == 2) {
                                layerDrawable.setLayerInset(1, (int) iconWidthPX / 16, (int) iconWidthPX / 16, (int) (17 * iconWidthPX / 16), (int) (17 * iconWidthPX / 16));
                                layerDrawable.setLayerInset(0, (int) (17 * iconWidthPX / 16), (int) iconWidthPX / 16, (int) iconWidthPX / 16, (int) (17 * iconWidthPX / 16));
                            } else if (drawableSize == 1)
                                layerDrawable.setLayerInset(0, (int) iconWidthPX / 16, (int) iconWidthPX / 16, (int) (17 * iconWidthPX / 16), (int) (17 * iconWidthPX / 16));

                            icon.setImageDrawable(layerDrawable);
                            icon.setBackgroundResource(R.drawable.folder_background);
                        }

                        TextView appname = (TextView) myView.findViewById(R.id.appname);
                        appname.setText(item.getFolderName());

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = itemWidth;
                        params.height = itemHeight;
                        params.setGravity(Gravity.CENTER);
                        myView.setLayoutParams(params);

                        if (clickable) {

                            icon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final View dialogView1 = getActivity().getLayoutInflater().inflate(R.layout.open_folder_dialog, null);
                                    final Dialog dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
                                    dialog.getWindow().getAttributes().windowAnimations = R.style.FolderOpenAnimation;
                                    final GridLayout gridLayoutFolder = (GridLayout) dialogView1.findViewById(R.id.folderGridContainer);
                                    final ImageView folderSettings = (ImageView) dialogView1.findViewById(R.id.folderSettings);

                                    final int folderBackgroundColor = item.getFolderBackgroundColor();
                                    gridLayoutFolder.setBackgroundColor(folderBackgroundColor);
                                    final LinearLayout.LayoutParams colorLayoutInVisibleParams = new LinearLayout.LayoutParams(0, 0);
                                    final LinearLayout.LayoutParams colorLayoutVisibleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    folderSettings.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            if (dialogView1.findViewById(R.id.colorSetting) == null) {
                                                View v1 = getColorChoserView(inflater, item, folderBackgroundColor, gridLayoutFolder);
                                                LinearLayout folderDialog = (LinearLayout) dialogView1.findViewById(R.id.folderDialog);
                                                folderDialog.addView(v1, 1);
                                            } else {
                                                LinearLayout folderDialog = (LinearLayout) dialogView1.findViewById(R.id.folderDialog);
                                                folderDialog.removeViewAt(1);
                                            }

                                        }
                                    });
                                    int dialogRowCount = 0, gheight;
                                    if (noofitems > 0)
                                        dialogRowCount = (int) Math.ceil(noofitems / 4.0);
                                    if (dialogRowCount < 2)
                                        gheight = itemHeight * dialogRowCount;
                                    else
                                        gheight = itemHeight * 2;

                                    gheight = gheight + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 67, getResources().getDisplayMetrics());

                                    dialog.setContentView(dialogView1);
                                    dialog.setCancelable(true);
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, gheight);

                                    TextView folderName = (TextView) dialogView1.findViewById(R.id.folderName);
                                    folderName.setText(item.getFolderName());
                                    LinearLayout folderHeading = (LinearLayout) dialogView1.findViewById(R.id.folderHeadingLayout);
                                    LinearLayout.LayoutParams folderHeadingParams = new LinearLayout.LayoutParams((itemWidth - 20) * 4, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    folderHeading.setLayoutParams(folderHeadingParams);
                                    //folderName.setWidth((itemWidth - 20)*4);

                                    if (noofitems > 0) {
                                        folderSettings.setVisibility(View.VISIBLE);
                                        gridLayoutFolder.setColumnCount(4);
                                        gridLayoutFolder.setRowCount(dialogRowCount);

                                        for (int i = 0; i < appsInFolder.size(); i++) {
                                            final ShortcutInfo folderItem = appsInFolder.get(i);

                                            LayoutInflater factory = LayoutInflater.from(getActivity());
                                            View folderItemView = factory.inflate(R.layout.homescreen_item, null);

                                            ImageView icon = (ImageView) folderItemView.findViewById(R.id.icon);
                                            icon.setImageDrawable(folderItem.getIcon(getActivity()));

                                            icon.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(folderItem.getIntent());
                                                    startActivity(intent);
                                                }
                                            });

                                            TextView appname = (TextView) folderItemView.findViewById(R.id.appname);
                                            appname.setText(folderItem.getAppName());

                                            GridLayout.LayoutParams folderItemParams = new GridLayout.LayoutParams();
                                            folderItemParams.width = itemWidth - 20;
                                            folderItemParams.height = itemHeight;
                                            folderItemView.setLayoutParams(folderItemParams);
                                            gridLayoutFolder.addView(folderItemView);

                                            //DD
                                            icon.setOnLongClickListener(new MyOnLongClickListenerForDialog());//
                                            icon.setOnDragListener(new MyDragListener());
                                            //DD
                                        }
                                    }

                                    dialog.show();
                                }
                            });

                        }
                        gl.addView(myView);

                        //DD
                        ViewGroup viewGroup = (ViewGroup) myView;
                        gridList.add(viewGroup);
                        //gridMap.put(viewGroup, icon);
                        icon.setOnLongClickListener(new MyOnLongClickListener());

                        //My code
                        myView.setTag("folder");
                        Object lock2 = new Object();
                        synchronized (lock2) {
                            HomescreenFragment.gridMap_icon.put(viewGroup, icon);
                            HomescreenFragment.gridMap_text.put(viewGroup, appname);
                            HomescreenFragment.gridMap_shortcutInfo.put(viewGroup, item);//
                        }
                        //DD
                    }
                } else {
                       /* Space spacer = new Space(getActivity());

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = itemWidth;
                        params.height = itemHeight;
                        params.setGravity(Gravity.CENTER);
                        spacer.setLayoutParams(params);

                        gridItemViewHolder.itemType = LauncherSettings.ITEM_TYPE_SPACER;
                        gridItemPosition++;
                        gridItemViewHolder.itemPosition = gridItemPosition;
                        spacer.setTag(gridItemViewHolder);

                        gl.addView(spacer);*/
                    LayoutInflater factory = LayoutInflater.from(getActivity());
                    View myView = factory.inflate(R.layout.homescreen_item, null);
                    ImageView icon = (ImageView) myView.findViewById(R.id.icon);
                    icon.setImageDrawable(null);
                    TextView appname = (TextView) myView.findViewById(R.id.appname);
                    appname.setText(null);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = itemWidth;
                    params.height = itemHeight;
                    params.setGravity(Gravity.CENTER);
                    myView.setLayoutParams(params);
                    gl.addView(myView);
                    //DD
                    ViewGroup viewGroup = (ViewGroup) myView;
                    gridList.add(viewGroup);
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
                }
            }
            if (i < totalGriditems) {
                for (; i < totalGriditems; i++) {
                        /*Space spacer = new Space(getActivity());

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = itemWidth;
                        params.height = itemHeight;
                        params.setGravity(Gravity.CENTER);
                        spacer.setLayoutParams(params);
                        gl.addView(spacer);*/


                    //DD
                    LayoutInflater factory = LayoutInflater.from(getActivity());
                    View myView = factory.inflate(R.layout.homescreen_item, null);
                    ImageView icon = (ImageView) myView.findViewById(R.id.icon);
                    icon.setImageDrawable(null);
                    TextView appname = (TextView) myView.findViewById(R.id.appname);
                    appname.setText(null);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = itemWidth;
                    params.height = itemHeight;
                    params.setGravity(Gravity.CENTER);
                    myView.setLayoutParams(params);
                    gl.addView(myView);
                    ViewGroup viewGroup = (ViewGroup) myView;
                    gridList.add(viewGroup);
                    Object lock4 = new Object();
                    synchronized (lock4) {
                        HomescreenFragment.gridMap_icon.put(viewGroup, icon);
                        HomescreenFragment.gridMap_text.put(viewGroup, appname);
                        HomescreenFragment.gridMap_shortcutInfo.put(viewGroup, new ItemInfo());
                    }
                    myView.setOnDragListener(new MyDragListener());
                    icon.setOnLongClickListener(new MyOnLongClickListener());
                    i++;
                    //DD
                }
            }
            if (clickable) {

                gl.setOnTouchListener(new View.OnTouchListener() {

                    int IDLE = 0;
                    int TOUCH = 1;
                    int PINCH = 2;
                    int touchState = IDLE;

                    float distStart = 0;
                    float distCurrent = 0;

                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        float distx = 0;
                        float disty = 0;

                        int action = motionEvent.getAction();
                        switch (action & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_DOWN:
                                touchState = TOUCH;
                                break;
                            case MotionEvent.ACTION_POINTER_DOWN:
                                touchState = PINCH;
                                distx = motionEvent.getX(0) - motionEvent.getX(1);
                                disty = motionEvent.getY(0) - motionEvent.getY(1);
                                distStart = FloatMath.sqrt(distx * distx + disty * disty);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (touchState == PINCH) {
                                    distx = motionEvent.getX(0) - motionEvent.getX(1);
                                    disty = motionEvent.getY(0) - motionEvent.getY(1);
                                    distCurrent = FloatMath.sqrt(distx * distx + disty * disty);
                                    final float diff = distCurrent - distStart;
                                    if (diff < -20) {
                                        getContainerManager().onZoomOut();
                                        touchState = IDLE;
                                    }
                                    return true;
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                touchState = IDLE;
                                break;
                            case MotionEvent.ACTION_POINTER_UP:
                                touchState = TOUCH;
                                break;
                        }
                        return false;
                    }

                });
            }
        }

        return rootView;
    }

    private View getColorChoserView(LayoutInflater inflater, final FolderInfo item, int folderBackgroundColor, final GridLayout gridLayoutFolder) {
        View colorChoserView = inflater.inflate(R.layout.folder_color_choser, null);

        final LinearLayout colorSetter = (LinearLayout) colorChoserView.findViewById(R.id.colorSetting);
        colorSetter.setBackgroundColor(folderBackgroundColor);

        RadioButton greenColorRB = (RadioButton) colorChoserView.findViewById(R.id.greenColorRB);
        RadioButton blueColorRB = (RadioButton) colorChoserView.findViewById(R.id.blueColorRB);
        RadioButton orangeColorRB = (RadioButton) colorChoserView.findViewById(R.id.orangeColorRB);
        RadioButton amethystColorRB = (RadioButton) colorChoserView.findViewById(R.id.amethystColorRB);
        RadioButton pomegranateColorRB = (RadioButton) colorChoserView.findViewById(R.id.pomegranateColorRB);
        RadioButton asbestosColorRB = (RadioButton) colorChoserView.findViewById(R.id.asbestosColorRB);
        RadioButton darkblueColorRB = (RadioButton) colorChoserView.findViewById(R.id.darkblueColorRB);
        RadioButton maroonColorRB = (RadioButton) colorChoserView.findViewById(R.id.maroonColorRB);
        RadioButton pistagreenColorRB = (RadioButton) colorChoserView.findViewById(R.id.pistagreenColorRB);
        RadioButton purpleColorRB = (RadioButton) colorChoserView.findViewById(R.id.purpleColorRB);
        RadioButton biscuitColorRB = (RadioButton) colorChoserView.findViewById(R.id.biscuitColorRB);
        RadioButton grayColorRB = (RadioButton) colorChoserView.findViewById(R.id.grayColorRB);
        RadioButton greenseaColorRB = (RadioButton) colorChoserView.findViewById(R.id.greenseaColorRB);
        if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_dark_green))
            greenColorRB.setChecked(true);
        else if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_belize_hole))
            blueColorRB.setChecked(true);
        else if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_orange))
            orangeColorRB.setChecked(true);
        else if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_amethyst))
            amethystColorRB.setChecked(true);
        else if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_pomegranate))
            pomegranateColorRB.setChecked(true);
        else if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_asbestos))
            asbestosColorRB.setChecked(true);
        else if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_dark_blue))
            darkblueColorRB.setChecked(true);
        else if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_maroon))
            maroonColorRB.setChecked(true);
        else if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_pista_green))
            pistagreenColorRB.setChecked(true);
        else if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_purple))
            purpleColorRB.setChecked(true);
        else if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_biscuit))
            biscuitColorRB.setChecked(true);
        else if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_gray))
            grayColorRB.setChecked(true);
        else if (folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_green_sea))
            greenseaColorRB.setChecked(true);
        else
            greenColorRB.setChecked(true);

        greenColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_dark_green);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });
        blueColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_belize_hole);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });
        orangeColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_orange);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });
        amethystColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_amethyst);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });
        pomegranateColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_pomegranate);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });
        asbestosColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_asbestos);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });
        darkblueColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_dark_blue);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });
        maroonColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_maroon);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });
        pistagreenColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_pista_green);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });
        purpleColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_purple);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });
        biscuitColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_biscuit);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });
        grayColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_gray);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });
        greenseaColorRB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int color = getResources().getColor(R.color.seclaunch_color_green_sea);
                gridLayoutFolder.setBackgroundColor(color);
                colorSetter.setBackgroundColor(color);
                item.setFolderBackgroundColor(color);

            }
        });

        return colorChoserView;
    }

    private final class MyOnLongClickListenerForDialog implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View view) {

            ClipData data = ClipData.newPlainText("", "");

            if (view instanceof ImageView || view instanceof TextView) {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                //View view_group = (View) viewGroup;
                //ClipData data1 = ClipData.newPlainText("", "");
                ViewGroup.DragShadowBuilder shadowBuilder_viewGroup = new ViewGroup.DragShadowBuilder(viewGroup);
                viewGroup.startDrag(data, shadowBuilder_viewGroup, viewGroup, 0);
                viewGroup.setVisibility(ViewGroup.INVISIBLE);
            }
            //else if(view instanceof TextView)
            else {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
            }
            ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);

            viewPager.animate().setDuration(500);
            viewPager.animate().scaleX(0.8F).scaleY(0.8F);
            dialog.hide();
            return true;
        }
    }

    private final class MyOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            try {
                if (view instanceof ImageView) {
                    if(((ImageView)view).getDrawable() != null ) {
                        mTrashIcon.setVisibility(View.VISIBLE);
                        mContainer.setVisibility(View.VISIBLE);
                        mInfoIcon.setVisibility(View.VISIBLE);
                    }
                }
                ClipData data = ClipData.newPlainText("", "");

                if (view instanceof ImageView || view instanceof TextView) {
                    if (view instanceof ImageView) {
                        if (((ImageView) view).getDrawable() == null)
                            return true;
                    }
                    if (view instanceof TextView) {
                        if (((TextView) view).getText() == null) {
                            return true;
                        }
                    }
                    final ViewGroup viewGroup = (ViewGroup) view.getParent();
                    ViewGroup.DragShadowBuilder shadowBuilder_viewGroup = new ViewGroup.DragShadowBuilder(viewGroup);
                    viewGroup.startDrag(data, shadowBuilder_viewGroup, viewGroup, 0);
                } else {

                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                }
                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
                viewPager.animate().setDuration(500);
                viewPager.animate().scaleX(0.8F).scaleY(0.8F);
            } catch (Exception e) {
                Log.v("Tag", "MyOnLongClickListener: onLongClick: " + e);
            }
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
                            ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
                            int currentPosition = viewPager.getCurrentItem();
                            viewPager.setCurrentItem(currentPosition - 1);
                        }

                        if (x > 650) {
                            ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
                            int currentPosition = viewPager.getCurrentItem();
                            viewPager.setCurrentItem(currentPosition + 1);
                        }

                        break;

                    case DragEvent.ACTION_DROP:
                        try {
                            ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
                            viewPager.animate().setDuration(500);
                            viewPager.animate().scaleX(1.0F).scaleY(1.0F);
                        } catch (Exception e) {
                            Log.v("Tag", "print Exception: " + e);
                        }
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
            final View v1 = v;

            try {
                switch (event.getAction()) {

                    case DragEvent.ACTION_DRAG_STARTED:
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        try {
                            if (v instanceof ViewGroup) {
                                View view = (ViewGroup) event.getLocalState();
                                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
                                viewPager.animate().setDuration(500);
                                viewPager.animate().scaleX(1.0F).scaleY(1.0F);
                            }
                            break;
                        } catch (Exception e) {
                            Log.v("Tag", "print Exception: " + e);
                        }

                    case DragEvent.ACTION_DRAG_ENTERED:
                        if (v instanceof ViewGroup) {
                            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);
                            v.startAnimation(shake);
                        }
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        if (v instanceof ViewGroup) {
                            try {
                                v.clearAnimation();
                            } catch (Exception e) {
                                Log.v("Tag", "print Exception: " + e);
                            }
                        }
                        break;

                    case DragEvent.ACTION_DROP: {
                        try {
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
                                        Log.v("Tag", "Test We are taking care folder!");

                                        Object lock5 = new Object();
                                        synchronized (lock5) {
                                            FolderInfo fold = (FolderInfo) HomescreenFragment.gridMap_shortcutInfo.get(v);
                                            ShortcutInfo item = (ShortcutInfo) HomescreenFragment.gridMap_shortcutInfo.get(view);
                                            fold.add(item);
                                        }
                                        //fold.add(item);
                                        view.clearAnimation();
                                        v.clearAnimation();
                                        Object lock = new Object();
                                        synchronized (lock) {
                                            ImageView imageView = (ImageView) HomescreenFragment.gridMap_icon.get(view);
                                            TextView textView = (TextView) HomescreenFragment.gridMap_text.get(view);
                                            imageView.setOnClickListener(null);
                                            imageView.setImageBitmap(null);
                                            textView.setText(null);
                                            HomescreenFragment.gridMap_shortcutInfo.put((ViewGroup) view, new ItemInfo());
                                        }
                                    } else {

                                        ((ViewGroup) v).removeView(((ViewGroup) v).getChildAt(0));
                                        ((ViewGroup) view).removeView(((ViewGroup) view).getChildAt(0));

                                        ((ViewGroup) v).removeView(((ViewGroup) v).getChildAt(0));
                                        ((ViewGroup) view).removeView(((ViewGroup) view).getChildAt(0));

                                        View view_fromV = HomescreenFragment.gridMap_icon.get(v);
                                        View view_fromView = HomescreenFragment.gridMap_icon.get(view);

                                        View text_fromV = HomescreenFragment.gridMap_text.get(v);
                                        View text_fromView = HomescreenFragment.gridMap_text.get(view);

                                        ItemInfo itemInfo_fromV = HomescreenFragment.gridMap_shortcutInfo.get(v);
                                        ItemInfo itemInfo_fromView = HomescreenFragment.gridMap_shortcutInfo.get(view);

                                        ((ViewGroup) v).addView(view_fromView, 0);
                                        ((ViewGroup) view).addView(view_fromV, 0);

                                        ((ViewGroup) v).addView(text_fromView, 1);
                                        ((ViewGroup) view).addView(text_fromV, 1);


                                        HomescreenFragment.gridMap_icon.put((ViewGroup) v, view_fromView);
                                        HomescreenFragment.gridMap_icon.put((ViewGroup) view, view_fromV);

                                        HomescreenFragment.gridMap_text.put((ViewGroup) v, text_fromView);
                                        HomescreenFragment.gridMap_text.put((ViewGroup) view, text_fromV);

                                        HomescreenFragment.gridMap_shortcutInfo.put((ViewGroup) v, itemInfo_fromView);
                                        HomescreenFragment.gridMap_shortcutInfo.put((ViewGroup) view, itemInfo_fromV);

                                        if (view.getTag() == "folder" && v.getTag() != "folder") {
                                            v.setTag("folder");
                                            view.setTag(null);
                                        }
                                    }
                                    view.clearAnimation();
                                    v.clearAnimation();
                                }
                                break;
                            }
                        } catch (Exception e) {
                            Log.v("Tag", "MyDragListener onDrag: " + e);
                        }
                    }

                }
            } catch (Exception e) {
                Log.v("Tag", "MyDragListener: onDrag Outer: " + e);
            }
            return true;
        }
    }

    public interface onViewChangeListener {
        public void onViewChange(int position);

        public void onPageChange();
    }

    onViewChangeListener viewChangeCallback;

    class GridItemViewHolder {
        int itemType;
        int itemPosition;
        ItemInfo itemInfo;
    }
}
