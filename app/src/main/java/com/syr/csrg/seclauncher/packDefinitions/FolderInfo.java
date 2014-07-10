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

import java.util.ArrayList;

public class FolderInfo extends ItemInfo {
    ArrayList<ShortcutInfo> appsInFolder = new ArrayList<ShortcutInfo>();
    boolean opened;

    public void add(ShortcutInfo item){
        appsInFolder.add(item);
    }

    public void remove(ShortcutInfo item){
        appsInFolder.remove(item);
    }

    FolderInfo() {
        itemType = LauncherSettings.ITEM_TYPE_FOLDER;
    }
    public void setTitle(CharSequence title) {
        this.packageName = title;
    }

    @Override
    void unbind() {
        super.unbind();
    }
}
