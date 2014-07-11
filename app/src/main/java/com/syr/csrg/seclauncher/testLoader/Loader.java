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
 * File: com.syr.csrg.seclauncher.testLoader.Loader
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/7/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.testLoader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.syr.csrg.seclauncher.domain.SecLaunchContext;
import com.syr.csrg.seclauncher.packDefinitions.ItemInfo;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchContainer;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;
import com.syr.csrg.seclauncher.packDefinitions.ShortcutInfo;

import java.util.ArrayList;
import java.util.List;

public class Loader {
    public void load(Context c){
        SecLaunchContext slc = SecLaunchContext.getInstance();
        ArrayList<SecLaunchContainer> slContainerArraylist = slc.getSlContainerArraylist();
        SecLaunchContainer testContatiner = new SecLaunchContainer(){
        };

        testContatiner.setContainerID("test");
        SecLaunchSubContainer testSubContainer = new SecLaunchSubContainer() {
        };

        testSubContainer.setSubContainerID("test");

        PackageManager pm = c.getPackageManager();
        List<ApplicationInfo> l= pm.getInstalledApplications(0);
        for (int i = 0; i < l.size(); i++) {
            String pkg = l.get(i).packageName;
            if(c.getPackageManager().getLaunchIntentForPackage(pkg)!=null){
                ShortcutInfo in = new ShortcutInfo();
                in.setId(01);
                in.setContainer(01);
                in.setItemType(01);
                in.setSpanX(01);
                in.setSpanY(01);
                in.setPackageName(pkg);
                in.setIcon(c.getPackageManager().getApplicationIcon(l.get(i)));
                in.setAppName(l.get(i).name);
                testSubContainer.addToItems(in);
            }
        }
        testSubContainer.getSubContainerContext().getNoOfApps();
        testContatiner.addToSubContainers(testSubContainer);
        slc.addToContainers(testContatiner);
    }
}
