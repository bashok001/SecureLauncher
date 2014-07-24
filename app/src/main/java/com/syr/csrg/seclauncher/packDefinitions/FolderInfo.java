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
 * File: com.syr.csrg.seclauncher.packDefinitions.FolderInfo
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/10/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.packDefinitions;

import com.syr.csrg.seclauncher.R;

import java.util.ArrayList;

public class FolderInfo extends ItemInfo {
    ArrayList<ShortcutInfo> appsInFolder = new ArrayList<ShortcutInfo>();
    boolean opened;
    String folderName;
    int folderBackgroundColor;

    public int getFolderBackgroundColor() {
        return folderBackgroundColor;
    }

    public void setFolderBackgroundColor(int folderBackgroundColor) {
        this.folderBackgroundColor = folderBackgroundColor;
    }



    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void add(ShortcutInfo item){
        appsInFolder.add(item);
    }

    public void remove(ShortcutInfo item){
        appsInFolder.remove(item);
    }

    public ArrayList<ShortcutInfo> getAppsInFolder() {
        return appsInFolder;
    }

    public FolderInfo() {
        itemType = LauncherSettings.ITEM_TYPE_FOLDER;
    }

    @Override
    void unbind() {
        super.unbind();
    }
}
