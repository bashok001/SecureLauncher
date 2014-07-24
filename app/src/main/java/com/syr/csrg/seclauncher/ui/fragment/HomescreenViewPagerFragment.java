package com.syr.csrg.seclauncher.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.syr.csrg.seclauncher.R;
import com.syr.csrg.seclauncher.agent.ConfigRetrievalAgent;
import com.syr.csrg.seclauncher.packDefinitions.FolderInfo;
import com.syr.csrg.seclauncher.packDefinitions.ItemInfo;
import com.syr.csrg.seclauncher.packDefinitions.LauncherSettings;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;
import com.syr.csrg.seclauncher.packDefinitions.ShortcutInfo;

import java.util.ArrayList;

/**
 * Created by neethu on 7/23/2014.
 */
public class HomescreenViewPagerFragment extends Fragment
{
    public static final String HOMESCREEN_POSITION = "position";

    public static final HomescreenViewPagerFragment newInstance(int position)
    {
        HomescreenViewPagerFragment fragment = new HomescreenViewPagerFragment();
        Bundle bdl = new Bundle(1);
        bdl.putInt(HOMESCREEN_POSITION, position);
        fragment.setArguments(bdl);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.homescreen_viewpager_layout, container, false);
        final int position = getArguments().getInt(HOMESCREEN_POSITION);

        ConfigRetrievalAgent configRetrievalAgent = new ConfigRetrievalAgent();
        ArrayList<SecLaunchSubContainer> subContainers =  configRetrievalAgent.getSubContainersById(LauncherSettings.HOME_SCREEN_SC);

            if(subContainers.size() > 0)
            {
                ArrayList<ItemInfo> subContainerItems = subContainers.get(position).getItems();

                GridLayout gl = (GridLayout)rootView.findViewById(R.id.gridContainer);
                gl.setLongClickable(true);
                gl.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View arg0) {
                        View dialogMenuView = getActivity().getLayoutInflater().inflate(R.layout.homescreen_contextual_dialog, null);
                        final Dialog dialogMenu = new Dialog(getActivity(), R.style.FullHeightDialog);
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

                                //Intent intent = new Intent(getActivity().getPackageManager().getLaunchIntentForPackage("com.syr.csrg.seclauncher.ui.activity.appdrawer.app"));
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

                                SecLaunchSubContainer newHomeScreenSubContatiner = new SecLaunchSubContainer() { };
                                newHomeScreenSubContatiner.setSubContainerID(LauncherSettings.HOME_SCREEN_SC);
                                configRetrievalAgent.getContainerByCurrentMode().addToSubContainers(newHomeScreenSubContatiner);
                            }
                        });

                        TextView newFolder = (TextView) dialogMenuView.findViewById(R.id.newFolderTxt);
                        newFolder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogMenu.dismiss();

                                final Dialog createFolderDialog = new Dialog(getActivity(), R.style.FullHeightDialog);
                                createFolderDialog.setContentView(R.layout.create_folder_dialog);
                                createFolderDialog.setCancelable(true);
                                createFolderDialog.setTitle("Create Folder");

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
                                        configRetrievalAgent.getSubContainersById(LauncherSettings.HOME_SCREEN_SC).get(position).getItems().add(in);

                                        createFolderDialog.dismiss();
                                    }
                                });

                                Button cancel = (Button) createFolderDialog.findViewById(R.id.cancelBtn);
                                cancel.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View v) {
                                        createFolderDialog.dismiss();
                                    }
                                });

                                createFolderDialog.show();
                            }
                        });

                        dialogMenu.show();
                        return true;
                    }
                });

                DisplayMetrics dm = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                float containerWidth = dm.widthPixels;
                float pixItemWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, getResources().getDisplayMetrics());
                int columnCount = (int)Math.round(containerWidth/pixItemWidth);
                gl.setColumnCount(columnCount);
                final int itemWidth = (int) containerWidth/columnCount;

                float containerHeight = dm.heightPixels;
                int quickAccessPanelWidthDP = 180;
                float quickAccessPanelWidthPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, quickAccessPanelWidthDP, getResources().getDisplayMetrics());
                float viewPagerHeight =  (containerHeight - quickAccessPanelWidthPX);
                float pixItemHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 107, getResources().getDisplayMetrics());
                int rowCount = (int)Math.round(viewPagerHeight/pixItemHeight);
                gl.setRowCount(rowCount);
                final int itemHeight = (int) viewPagerHeight/rowCount;

                for (int i = 0; i < subContainerItems.size(); i++)
                {
                    if (subContainerItems.get(i) != null)
                    {
                        if (subContainerItems.get(i).getItemType() == LauncherSettings.ITEM_TYPE_SHORTCUT)
                        {
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

                        else if (subContainerItems.get(i).getItemType() == LauncherSettings.ITEM_TYPE_FOLDER)
                        {
                            LayoutInflater factory = LayoutInflater.from(getActivity());
                            View myView = factory.inflate(R.layout.homescreen_item, null);
                            ImageView icon = (ImageView) myView.findViewById(R.id.icon);

                            final FolderInfo item = (FolderInfo) subContainerItems.get(i);
                            final ArrayList<ShortcutInfo> appsInFolder = item.getAppsInFolder();

                            final int noofitems = appsInFolder.size();

                            if(noofitems == 0)
                                icon.setImageDrawable(getResources().getDrawable(R.drawable.ficon));
                            else
                            {
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
                                    layerDrawable.setLayerInset(0, (int)iconWidthPX/12, -(int)iconWidthPX/12, (int)iconWidthPX/12, (int)iconWidthPX/6);
                                    layerDrawable.setLayerInset(1, (int)iconWidthPX/6, (int)iconWidthPX/12, -(int)iconWidthPX/12, (int)iconWidthPX/12);
                                    layerDrawable.setLayerInset(2, -(int)iconWidthPX/12, (int)iconWidthPX/12, (int)iconWidthPX/6, (int)iconWidthPX/12);
                                    layerDrawable.setLayerInset(3, (int)iconWidthPX/12, (int)iconWidthPX/6, (int)iconWidthPX/12, -(int)iconWidthPX/12);
                                }
                                else if (drawableSize == 3) {
                                    layerDrawable.setLayerInset(0, (int)iconWidthPX/6, (int)iconWidthPX/12, -(int)iconWidthPX/12, (int)iconWidthPX/12);
                                    layerDrawable.setLayerInset(1, -(int)iconWidthPX/12, (int)iconWidthPX/12, (int)iconWidthPX/6, (int)iconWidthPX/12);
                                    layerDrawable.setLayerInset(2, (int)iconWidthPX/12, (int)iconWidthPX/6, (int)iconWidthPX/12, -(int)iconWidthPX/12);
                                }
                                else if (drawableSize == 2) {
                                    layerDrawable.setLayerInset(0, -(int)iconWidthPX/12, (int)iconWidthPX/12, (int)iconWidthPX/6, (int)iconWidthPX/12);
                                    layerDrawable.setLayerInset(1, (int)iconWidthPX/12, (int)iconWidthPX/6, (int)iconWidthPX/12, -(int)iconWidthPX/12);
                                }
                                else if (drawableSize == 1)
                                    layerDrawable.setLayerInset(0, (int)iconWidthPX/12, (int)iconWidthPX/6, (int)iconWidthPX/12, -(int)iconWidthPX/12);

                                icon.setImageDrawable(layerDrawable);
                            }

                            TextView appname = (TextView) myView.findViewById(R.id.appname);
                            appname.setText(item.getFolderName());

                            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                            params.width = itemWidth;
                            params.height = itemHeight;
                            params.setGravity(Gravity.CENTER);
                            myView.setLayoutParams(params);

                            myView.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    View dialogView1 = getActivity().getLayoutInflater().inflate(R.layout.open_folder_dialog, null);
                                    final Dialog dialog = new Dialog(getActivity(), R.style.FullHeightDialog);

                                    int dialogRowCount = 0, gheight;
                                    if(noofitems > 0)
                                        dialogRowCount = (int) Math.ceil(noofitems/ 4.0);
                                    if (dialogRowCount < 2)
                                        gheight = itemHeight * dialogRowCount;
                                    else
                                        gheight = itemHeight * 2;

                                    gheight = gheight + (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 67, getResources().getDisplayMetrics());

                                    dialog.setContentView(dialogView1);
                                    dialog.setCancelable(true);
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, gheight);

                                    TextView folderName = (TextView) dialogView1.findViewById(R.id.folderName);
                                    folderName.setText(item.getFolderName());
                                    folderName.setWidth((itemWidth - 20)*4);

                                    if(noofitems > 0) {
                                        GridLayout gridLayoutFolder = (GridLayout) dialogView1.findViewById(R.id.folderGridContainer);
                                        gridLayoutFolder.setColumnCount(4);
                                        gridLayoutFolder.setRowCount(dialogRowCount);

                                        for (int i = 0; i < appsInFolder.size(); i++)
                                        {
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
                                        }
                                    }

                                    dialog.show();
                                }
                            });

                            gl.addView(myView);
                        }
                    }
                    else
                    {
                        Space spacer = new Space(getActivity());

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = itemWidth;
                        params.height = itemHeight;
                        params.setGravity(Gravity.CENTER);
                        spacer.setLayoutParams(params);
                        gl.addView(spacer);
                    }
                }
            }

        return rootView;
    }
}
