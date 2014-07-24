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
 * File: com.syr.csrg.seclauncher.packDefinitions.ItemInfo
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/6/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.packDefinitions;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Represents an item in the launcher.
 */
public class ItemInfo {
    public static final String CLASSNAME = "ItemInfo";

    static final int NO_ID = -1;
    
    /**
     * The id in the settings database for this item
     */
    long id = NO_ID;
    
    /**
     * One of
     * {@link LauncherSettings.ITEM_TYPE_APPLICATION},
     * {@link LauncherSettings.ITEM_TYPE_SHORTCUT},
     * {@link LauncherSettings.ITEM_TYPE_FOLDER}, or
     * {@link LauncherSettings.ITEM_TYPE_APPWIDGET}.
     */
    int itemType;
    
    /**
     * The id of the container that holds this item.
     */
    long container = NO_ID;
    
    /**
     * Indicates the X cell span.
     */
    int spanX = 1;

    /**
     * Indicates the Y cell span.
     */
    int spanY = 1;

    /**
     * Indicates the minimum X cell span.
     */
    int minSpanX = 1;

    /**
     * Indicates the minimum Y cell span.
     */
    int minSpanY = 1;

    /**
     * Title of the item
     */
    CharSequence packageName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public long getContainer() {
        return container;
    }

    public void setContainer(long container) {
        this.container = container;
    }

    public int getSpanX() {
        return spanX;
    }

    public void setSpanX(int spanX) {
        this.spanX = spanX;
    }

    public int getSpanY() {
        return spanY;
    }

    public void setSpanY(int spanY) {
        this.spanY = spanY;
    }

    public CharSequence getPackageName() {
        return packageName;
    }

    public void setPackageName(CharSequence packageName) {
        this.packageName = packageName;
    }

    protected Intent getIntent() {
        final String METHODNAME = "getIntent";
        throw new RuntimeException("Unexpected Intent");
    }

    /**
     * Write the fields of this item to the DB
     * 
     * @param values
     */
    void onAddToDatabase(ContentValues values) {
        //TODO
    }

   static byte[] flattenBitmap(Bitmap bitmap) {
       final String METHODNAME = "flattenBitmap";
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
         //   Log.w("Favorite", "Could not write icon");
            return null;
        }
    }

    static void writeBitmap(ContentValues values, Bitmap bitmap) {
        final String METHODNAME = "writeBitmap";
        if (bitmap != null) {
            byte[] data = flattenBitmap(bitmap);
            values.put(LauncherSettings.ICON, data);
        }
    }

    /**
     * It is very important that sub-classes implement this if they contain any references
     * to the activity (anything in the view hierarchy etc.). If not, leaks can result since
     * ItemInfo objects persist across rotation and can hence leak by holding stale references
     * to the old view hierarchy / activity.
     */
    void unbind() {
    }

    @Override
    public String toString() {
        return "Item(id=" + this.id + " type=" + this.itemType + " container=" + this.container
                + " spanX=" + spanX + " spanY=" + spanY + ")";
    }
}
