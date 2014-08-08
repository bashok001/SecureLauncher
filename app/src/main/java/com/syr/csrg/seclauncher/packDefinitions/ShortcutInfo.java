/*
 * ======================================================================*
 * Copyright (c) 2014 SecureLauncher Group
 *              All rights reserved.
 * ======================================================================*
 * NOTICE: Not for use or disclosure outside of SecureLauncher Group
 *                 without written permission.
 * ======================================================================*
 * 	@author Ashok Bommisetti
 * 	@version 1.0
 *
 * File: com.syr.csrg.seclauncher.packDefinitions.ShortcutInfo
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/7/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.packDefinitions;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;

public class ShortcutInfo extends ItemInfo {

    Intent intent;
    boolean customIcon;
    boolean usingFallbackIcon;

    /**
     * If isShortcut=true and customIcon=false, this contains a reference to the
     * shortcut icon as an application's resource.
     */
    Intent.ShortcutIconResource iconResource;

    /**
     * The application icon.
     */
    private Drawable mIcon;
   /*
   * App Name
   */
    private String appName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public ShortcutInfo() {
        itemType = LauncherSettings.ITEM_TYPE_SHORTCUT;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public boolean isCustomIcon() {
        return customIcon;
    }

    public void setCustomIcon(boolean customIcon) {
        this.customIcon = customIcon;
    }

    public boolean isUsingFallbackIcon() {
        return usingFallbackIcon;
    }

    public void setUsingFallbackIcon(boolean usingFallbackIcon) {
        this.usingFallbackIcon = usingFallbackIcon;
    }

    public Intent.ShortcutIconResource getIconResource() {
        return iconResource;
    }

    public void setIconResource(Intent.ShortcutIconResource iconResource) {
        this.iconResource = iconResource;
    }

    public static PackageInfo getPackageInfo(Context context, String packageName) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            //TODO

        }
        return pi;
    }

    public static Drawable getApplicationIcon(Context context, CharSequence packageName){
        Drawable icon = null;
        try {
            PackageManager pm = context.getPackageManager();
            icon = pm.getApplicationIcon(packageName.toString());
        } catch (NameNotFoundException e) {
            //TODO

        }
        return icon;
    }

    public void setIcon(Drawable b) {
        mIcon = b;
    }

    public Drawable getIcon(Context context) {
        if (mIcon == null) {
            updateIcon(context);
        }
        return mIcon;
    }

    public void updateIcon(Context context) {
        mIcon = getApplicationIcon(context, this.packageName);
    }

    final void setActivity(Context context, ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
        itemType = LauncherSettings.ITEM_TYPE_SHORTCUT;
    }

    @Override
    void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);

        String titleStr = packageName != null ? packageName.toString() : null;
        values.put(LauncherSettings.PACKAGE_NAME, titleStr);

        String uri = intent != null ? intent.toUri(0) : null;
        values.put(LauncherSettings.INTENT, uri);

        if (customIcon) {
            values.put(LauncherSettings.ICON_TYPE,
                    LauncherSettings.ICON_TYPE_BITMAP);
           // writeBitmap(values, mIcon);
        } else {
            if (!usingFallbackIcon) {
              //  writeBitmap(values, mIcon);
            }
            values.put(LauncherSettings.ICON_TYPE,
                    LauncherSettings.ICON_TYPE_RESOURCE);
            if (iconResource != null) {
                values.put(LauncherSettings.ICON_PACKAGE,
                        iconResource.packageName);
                values.put(LauncherSettings.ICON_RESOURCE,
                        iconResource.resourceName);
            }
        }
    }

    @Override
    public String toString() {
        return "ShortcutInfo(packageName=" + packageName.toString() + "intent=" + intent + "id=" + this.id
                + " type=" + this.itemType + " container=" + this.container + " spanX=" + spanX + " spanY=" + spanY + ")";
    }

    public static void dumpShortcutInfoList(String tag, String label,
            ArrayList<ShortcutInfo> list) {
        Log.d(tag, label + " size=" + list.size());
        for (ShortcutInfo info: list) {
            Log.d(tag, "   packageName=\"" + info.packageName + " icon=" + info.mIcon
                    + " customIcon=" + info.customIcon);
        }
    }
}

