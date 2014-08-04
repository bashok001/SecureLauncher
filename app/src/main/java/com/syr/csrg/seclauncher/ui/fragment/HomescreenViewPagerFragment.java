package com.syr.csrg.seclauncher.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

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

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            viewChangeCallback = (onViewChangeListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString());
        }
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.homescreen_viewpager_layout, container, false);
        final int position = getArguments().getInt(HOMESCREEN_POSITION);

        ConfigRetrievalAgent configRetrievalAgent = new ConfigRetrievalAgent();
        ArrayList<SecLaunchSubContainer> subContainers =  configRetrievalAgent.getSubContainersById(LauncherSettings.HOME_SCREEN_SC);

            if(subContainers.size() > 0)
            {
                final ArrayList<ItemInfo> subContainerItems = subContainers.get(position).getItems();

                final GridLayout gl = (GridLayout)rootView.findViewById(R.id.gridContainer);
                gl.setLongClickable(true);

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
                final int totalGriditems = rowCount * columnCount;
                int i;
                int gridItemPosition = -1;

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

                                SecLaunchSubContainer newHomeScreenSubContatiner = new SecLaunchSubContainer() { };
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

                                for (int j = 0; j < subContainerItems.size(); j++)
                                {
                                    if (subContainerItems.get(j) == null) {
                                        spaceAvailable = 1;
                                        break;
                                    }
                                    else if(subContainerItems.get(j).getItemType() == LauncherSettings.ITEM_TYPE_SHORTCUT ||
                                            subContainerItems.get(j).getItemType() == LauncherSettings.ITEM_TYPE_FOLDER) {
                                         k++;

                                         if(k == totalGriditems) {
                                             spaceAvailable = 0;
                                             break;
                                         }
                                    }
                                }

                                if(spaceAvailable == 1) {
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

                                            for (j = 0; j < subContainerItems.size(); j++)
                                            {
                                                if (subContainerItems.get(j) == null)
                                                    break;
                                            }
                                            if(j == subContainerItems.size())
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
                                }
                                else
                                {
                                    Toast.makeText(getActivity().getApplicationContext(), "No space available to create new folder",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        dialogMenu.show();
                        return true;
                    }
                });

                for (i = 0; i < subContainerItems.size() && gridItemPosition < totalGriditems - 1; i++)
                {
                    GridItemViewHolder gridItemViewHolder = new GridItemViewHolder();
                    if (subContainerItems.get(i) != null)
                    {
                        if (subContainerItems.get(i).getItemType() == LauncherSettings.ITEM_TYPE_SHORTCUT)
                        {
                            final ShortcutInfo item = (ShortcutInfo) subContainerItems.get(i);

                            LayoutInflater factory = LayoutInflater.from(getActivity());
                            View myView = factory.inflate(R.layout.homescreen_item, null);
                            gridItemViewHolder.itemType = LauncherSettings.ITEM_TYPE_SHORTCUT;
                            gridItemViewHolder.itemInfo = item;
                            gridItemPosition++;
                            gridItemViewHolder.itemPosition = gridItemPosition;
                            myView.setTag(gridItemViewHolder);

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
                            View myView;

                            final FolderInfo item = (FolderInfo) subContainerItems.get(i);
                            final ArrayList<ShortcutInfo> appsInFolder = item.getAppsInFolder();

                            final int noofitems = appsInFolder.size();
                            myView = factory.inflate(R.layout.homescreen_item, null);
                            ImageView icon = (ImageView) myView.findViewById(R.id.icon);

                            gridItemViewHolder.itemType = LauncherSettings.ITEM_TYPE_FOLDER;
                            gridItemViewHolder.itemInfo = item;
                            gridItemPosition++;
                            gridItemViewHolder.itemPosition = gridItemPosition;
                            myView.setTag(gridItemViewHolder);

                            if(noofitems == 0) {
                                icon.setImageDrawable(getResources().getDrawable(R.drawable.foldericon));
                                icon.setBackgroundResource(R.drawable.folder_background);
                            }
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
                                    layerDrawable.setLayerInset(3, (int)iconWidthPX/16, (int)iconWidthPX/16, (int)(17 * iconWidthPX/16), (int)(17 * iconWidthPX/16));
                                    layerDrawable.setLayerInset(2, (int)(17 * iconWidthPX/16), (int)iconWidthPX/16, (int)iconWidthPX/16, (int)(17 * iconWidthPX/16));
                                    layerDrawable.setLayerInset(1, (int)iconWidthPX/16, (int)(17 * iconWidthPX/16), (int)(17 * iconWidthPX/16), (int)iconWidthPX/16);
                                    layerDrawable.setLayerInset(0, (int)(17 * iconWidthPX/16), (int)(17 * iconWidthPX/16), (int)iconWidthPX/16, (int)iconWidthPX/16);
                                }
                                else if (drawableSize == 3) {
                                    layerDrawable.setLayerInset(2, (int)iconWidthPX/16, (int)iconWidthPX/16, (int)(17 * iconWidthPX/16), (int)(17 * iconWidthPX/16));
                                    layerDrawable.setLayerInset(1, (int)(17 * iconWidthPX/16), (int)iconWidthPX/16, (int)iconWidthPX/16, (int)(17 * iconWidthPX/16));
                                    layerDrawable.setLayerInset(0, (int)iconWidthPX/16, (int)(17 * iconWidthPX/16), (int)(17 * iconWidthPX/16), (int)iconWidthPX/16);
                                }
                                else if (drawableSize == 2) {
                                    layerDrawable.setLayerInset(1, (int)iconWidthPX/16, (int)iconWidthPX/16, (int)(17 * iconWidthPX/16), (int)(17 * iconWidthPX/16));
                                    layerDrawable.setLayerInset(0, (int)(17 * iconWidthPX/16), (int)iconWidthPX/16, (int)iconWidthPX/16, (int)(17 * iconWidthPX/16));
                                }
                                else if (drawableSize == 1)
                                    layerDrawable.setLayerInset(0, (int)iconWidthPX/16, (int)iconWidthPX/16, (int)(17 * iconWidthPX/16), (int)(17 * iconWidthPX/16));

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

                            icon.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    final View dialogView1 = getActivity().getLayoutInflater().inflate(R.layout.open_folder_dialog, null);
                                    final Dialog dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
                                    dialog.getWindow().getAttributes().windowAnimations = R.style.FolderOpenAnimation;
                                    final GridLayout gridLayoutFolder = (GridLayout) dialogView1.findViewById(R.id.folderGridContainer);
                                    final ImageView folderSettings = (ImageView) dialogView1.findViewById(R.id.folderSettings);

                                    final int folderBackgroundColor = item.getFolderBackgroundColor();
                                    gridLayoutFolder.setBackgroundColor(folderBackgroundColor);
                                    final LinearLayout.LayoutParams colorLayoutInVisibleParams = new LinearLayout.LayoutParams(0,0);
                                    final LinearLayout.LayoutParams colorLayoutVisibleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    folderSettings.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            if(dialogView1.findViewById(R.id.colorSetting) == null)
                                            {
                                                View v1 = getColorChoserView(inflater, item, folderBackgroundColor, gridLayoutFolder);
                                                LinearLayout folderDialog = (LinearLayout) dialogView1.findViewById(R.id.folderDialog);
                                                folderDialog.addView(v1, 1);
                                            }
                                            else {
                                                LinearLayout folderDialog = (LinearLayout) dialogView1.findViewById(R.id.folderDialog);
                                                folderDialog.removeViewAt(1);
                                            }

                                        }
                                    });



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
                                    LinearLayout folderHeading = (LinearLayout) dialogView1.findViewById(R.id.folderHeadingLayout);
                                    LinearLayout.LayoutParams folderHeadingParams = new LinearLayout.LayoutParams((itemWidth - 20)*4, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    folderHeading.setLayoutParams(folderHeadingParams);
                                    //folderName.setWidth((itemWidth - 20)*4);

                                    if(noofitems > 0) {
                                        folderSettings.setVisibility(View.VISIBLE);
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

                        gridItemViewHolder.itemType = LauncherSettings.ITEM_TYPE_SPACER;
                        gridItemPosition++;
                        gridItemViewHolder.itemPosition = gridItemPosition;
                        spacer.setTag(gridItemViewHolder);

                        gl.addView(spacer);
                    }
                }

                if( i < totalGriditems)
                {
                    for (; i < totalGriditems; i++)
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


    private View getColorChoserView(LayoutInflater inflater, final FolderInfo item, int folderBackgroundColor, final GridLayout gridLayoutFolder)
    {
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
        if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_dark_green))
            greenColorRB.setChecked(true);
        else if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_belize_hole))
            blueColorRB.setChecked(true);
        else if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_orange))
            orangeColorRB.setChecked(true);
        else if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_amethyst))
            amethystColorRB.setChecked(true);
        else if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_pomegranate))
            pomegranateColorRB.setChecked(true);
        else if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_asbestos))
            asbestosColorRB.setChecked(true);
        else if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_dark_blue))
            darkblueColorRB.setChecked(true);
        else if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_maroon))
            maroonColorRB.setChecked(true);
        else if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_pista_green))
            pistagreenColorRB.setChecked(true);
        else if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_purple))
            purpleColorRB.setChecked(true);
        else if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_biscuit))
            biscuitColorRB.setChecked(true);
        else if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_gray))
            grayColorRB.setChecked(true);
        else if(folderBackgroundColor == getResources().getColor(R.color.seclaunch_color_green_sea))
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

    public interface onViewChangeListener {
        public void onViewChange(int position);
        public void onPageChange();
    }

    onViewChangeListener viewChangeCallback;

    class GridItemViewHolder
    {
        int itemType;
        int itemPosition;
        ItemInfo itemInfo;
    }
}
