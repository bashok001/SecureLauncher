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
import com.syr.csrg.seclauncher.packDefinitions.FolderInfo;
import com.syr.csrg.seclauncher.packDefinitions.LauncherSettings;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchContainer;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;
import com.syr.csrg.seclauncher.packDefinitions.ShortcutInfo;

import java.util.ArrayList;
import java.util.List;

public class Loader {

    SecLaunchContainer noAccessControlContainer;
    SecLaunchSubContainer homeScreenSubContatiner1, appdrawerSubContainer, homeScreenSubContatiner2, homeScreenSubContatiner3, quickAccessPanelSubContatiner1;
    public void load(Context c){
        SecLaunchContext slc = SecLaunchContext.getInstance();
        ArrayList<SecLaunchContainer> slContainerArraylist = slc.getSlContainerArraylist();

        PackageManager pm = c.getPackageManager();
        List<ApplicationInfo> l= pm.getInstalledApplications(0);
        
        noAccessControlContainer = new SecLaunchContainer(){
        };

        noAccessControlContainer.setContainerID(LauncherSettings.NO_ACCESS_CONTROL_MODE);

        homeScreenSubContatiner1 = new SecLaunchSubContainer() {
        };

        homeScreenSubContatiner1.setSubContainerID(LauncherSettings.HOME_SCREEN_SC);

        for (int i = 0; i < 25; i++) {
            String pkg = l.get(i).packageName;
            if(c.getPackageManager().getLaunchIntentForPackage(pkg)!=null){
                ShortcutInfo in = new ShortcutInfo();
                in.setId(01);
                in.setContainer(01);
                in.setItemType(LauncherSettings.ITEM_TYPE_SHORTCUT);
                in.setSpanX(01);
                in.setSpanY(01);
                in.setPackageName(pkg);
                in.setAppName(pm.getApplicationLabel(l.get(i)).toString());
                in.setIntent(c.getPackageManager().getLaunchIntentForPackage(pkg));
                in.setIcon(c.getPackageManager().getApplicationIcon(l.get(i)));
                homeScreenSubContatiner1.getItems().add(in);
            }
            if(i == 15)
            {
                FolderInfo in = new FolderInfo();
                in.setPackageName("Unused");
                in.setItemType(02);
                in.setFolderName("Unused");

                homeScreenSubContatiner1.getItems().add(in);
            }

            if(i == 5 || i == 15)
                homeScreenSubContatiner1.getItems().add(null);
            if(i == 10)
            {
                FolderInfo in = new FolderInfo();
                in.setPackageName("Bank");
                in.setItemType(02);
                in.setFolderName("Bank");

                for (int j = 0; j < 30; j++)
                {
                    String pkg1 = l.get(j).packageName;
                    if (c.getPackageManager().getLaunchIntentForPackage(pkg1) != null) {
                        ShortcutInfo in1 = new ShortcutInfo();
                        in1.setId(01);
                        in1.setContainer(01);
                        in1.setItemType(01);
                        in1.setSpanX(01);
                        in1.setSpanY(01);
                        in1.setPackageName(pkg);
                        in1.setAppName(pm.getApplicationLabel(l.get(i)).toString());
                        in1.setIntent(c.getPackageManager().getLaunchIntentForPackage(pkg1));
                        in1.setIcon(c.getPackageManager().getApplicationIcon(l.get(j)));
                        in.add(in1);
                    }
                }

                for (int j = 5; j < 25; j++)
                {
                    String pkg1 = l.get(j).packageName;
                    if (c.getPackageManager().getLaunchIntentForPackage(pkg1) != null) {
                        ShortcutInfo in1 = new ShortcutInfo();
                        in1.setId(01);
                        in1.setContainer(01);
                        in1.setItemType(01);
                        in1.setSpanX(01);
                        in1.setSpanY(01);
                        
                        in1.setPackageName(pkg);
                        in1.setAppName(pm.getApplicationLabel(l.get(i)).toString());
                        in1.setIntent(c.getPackageManager().getLaunchIntentForPackage(pkg1));
                        in1.setIcon(c.getPackageManager().getApplicationIcon(l.get(j)));
                        in.add(in1);
                    }
                }

                homeScreenSubContatiner1.getItems().add(in);
            }

            if(i == 20)
            {
                FolderInfo in = new FolderInfo();
                in.setPackageName("Phone");
                in.setItemType(02);
                in.setFolderName("Phone");

                for (int j = 5; j < 25; j++)
                {
                    String pkg1 = l.get(j).packageName;
                    if (c.getPackageManager().getLaunchIntentForPackage(pkg1) != null) {
                        ShortcutInfo in1 = new ShortcutInfo();
                        in1.setId(01);
                        in1.setContainer(01);
                        in1.setItemType(01);
                        in1.setSpanX(01);
                        in1.setSpanY(01);
                        //in.setPackageName(pkg);
                        in1.setPackageName(pkg);
                        in1.setAppName(pm.getApplicationLabel(l.get(i)).toString());
                        in1.setIntent(c.getPackageManager().getLaunchIntentForPackage(pkg1));
                        in1.setIcon(c.getPackageManager().getApplicationIcon(l.get(j)));
                        in.add(in1);
                    }
                }
                homeScreenSubContatiner1.getItems().add(in);
            }
        }

        homeScreenSubContatiner2 = new SecLaunchSubContainer() {
        };

        homeScreenSubContatiner2.setSubContainerID(1);

        for (int i = 5; i < 25; i++) {
            String pkg = l.get(i).packageName;
            if(c.getPackageManager().getLaunchIntentForPackage(pkg)!=null){
                ShortcutInfo in = new ShortcutInfo();
                in.setId(01);
                in.setContainer(01);
                in.setItemType(01);
                if(i%5 == 0)
                    in.setSpanX(01);
                else
                    in.setSpanX(01);
                in.setSpanY(01);
                //in.setPackageName(pkg);
                in.setPackageName(l.get(i).loadLabel(pm).toString());
                in.setAppName(l.get(i).loadLabel(pm).toString());
                in.setIntent(c.getPackageManager().getLaunchIntentForPackage(pkg));
                in.setIcon(c.getPackageManager().getApplicationIcon(l.get(i)));

                homeScreenSubContatiner2.getItems().add(in);
            }
        }

        homeScreenSubContatiner3 = new SecLaunchSubContainer() {
        };

        homeScreenSubContatiner3.setSubContainerID(1);

        for (int i = 15; i < 30; i++) {
            String pkg = l.get(i).packageName;
            if(c.getPackageManager().getLaunchIntentForPackage(pkg)!=null){
                ShortcutInfo in = new ShortcutInfo();
                in.setId(01);
                in.setContainer(01);
                in.setItemType(01);
                if(i%5 == 0)
                    in.setSpanX(01);
                else
                    in.setSpanX(01);
                in.setSpanY(01);
                //in.setPackageName(pkg);
                in.setPackageName(l.get(i).loadLabel(pm).toString());
                in.setAppName(l.get(i).loadLabel(pm).toString());
                in.setIntent(c.getPackageManager().getLaunchIntentForPackage(pkg));
                in.setIcon(c.getPackageManager().getApplicationIcon(l.get(i)));
                homeScreenSubContatiner3.getItems().add(in);
            }
        }

        noAccessControlContainer.insertIntoScList(0, homeScreenSubContatiner1);
        noAccessControlContainer.insertIntoScList(1, homeScreenSubContatiner2);
        noAccessControlContainer.insertIntoScList(2, homeScreenSubContatiner3);

        quickAccessPanelSubContatiner1 = new SecLaunchSubContainer() { };
        quickAccessPanelSubContatiner1.setSubContainerID(2);

        int j =0;
        for (int i = 0; i < l.size() && j < 3; i++) {
            String pkg = l.get(i).packageName;
            if(c.getPackageManager().getLaunchIntentForPackage(pkg)!=null){
                j++;

                ShortcutInfo in = new ShortcutInfo();
                in.setId(01);
                in.setContainer(01);
                in.setItemType(01);
                if(i%5 == 0)
                    in.setSpanX(01);
                else
                    in.setSpanX(01);
                in.setSpanY(01);
                //in.setPackageName(pkg);
                in.setPackageName(l.get(i).loadLabel(pm).toString());
                in.setAppName(l.get(i).loadLabel(pm).toString());
                in.setIntent(c.getPackageManager().getLaunchIntentForPackage(pkg));
                in.setIcon(c.getPackageManager().getApplicationIcon(l.get(i)));

                quickAccessPanelSubContatiner1.getItems().add(in);
            }
        }

        for (int i = 0; i < l.size(); i++) {
            String pkg = l.get(i).packageName;
            if(c.getPackageManager().getLaunchIntentForPackage(pkg)!=null){

                ShortcutInfo in = new ShortcutInfo();
                in.setId(01);
                in.setContainer(01);
                in.setItemType(01);
                if(i%5 == 0)
                    in.setSpanX(01);
                else
                    in.setSpanX(01);
                in.setSpanY(01);
                //in.setPackageName(pkg);
                in.setPackageName(l.get(i).loadLabel(pm).toString());
                in.setAppName(l.get(i).loadLabel(pm).toString());
                in.setIntent(c.getPackageManager().getLaunchIntentForPackage(pkg));
                in.setIcon(c.getPackageManager().getApplicationIcon(l.get(i)));

                if(in.getPackageName().toString().equals("Apps"))
                    quickAccessPanelSubContatiner1.getItems().add(in);
            }
        }

        noAccessControlContainer.insertIntoScList(3,quickAccessPanelSubContatiner1);

        appdrawerSubContainer = new SecLaunchSubContainer() {
        };

        appdrawerSubContainer.setSubContainerID(3);

        for (int i = 0; i < l.size(); i++) {
            String pkg = l.get(i).packageName;
            if(c.getPackageManager().getLaunchIntentForPackage(pkg)!=null){
                ShortcutInfo in = new ShortcutInfo();
                in.setId(01);
                in.setContainer(01);
                in.setItemType(01);
                if(i%5 == 0)
                    in.setSpanX(01);
                else
                    in.setSpanX(01);
                in.setSpanY(01);
                //in.setPackageName(pkg);
                in.setPackageName(l.get(i).loadLabel(pm).toString());
                in.setAppName(l.get(i).loadLabel(pm).toString());
                in.setIntent(c.getPackageManager().getLaunchIntentForPackage(pkg));
                in.setIcon(c.getPackageManager().getApplicationIcon(l.get(i)));

                appdrawerSubContainer.getItems().add(in);
            }
        }

        noAccessControlContainer.insertIntoScList(4,appdrawerSubContainer);
        slContainerArraylist.add(0,noAccessControlContainer);
    }

    public SecLaunchContainer getContainters(){
        /*ArrayList<SecLaunchContainer> as = new ArrayList<SecLaunchContainer>();
        as.add(testContainer);
        as.add(appdrawerContainer);
        as.add(noAccessControlContainer);
        as.add(quickAccessPanelContatiner);*/
        return noAccessControlContainer;
    }

    public ArrayList<SecLaunchSubContainer> getHomeScreenSubContainters(){
        ArrayList<SecLaunchSubContainer> as = new ArrayList<SecLaunchSubContainer>();
        as.add(homeScreenSubContatiner1);
        as.add(homeScreenSubContatiner2);
        as.add(homeScreenSubContatiner3);

        return as;
    }

    public ArrayList<SecLaunchSubContainer> getQAPSubContainters(){
        ArrayList<SecLaunchSubContainer> as = new ArrayList<SecLaunchSubContainer>();
        as.add(quickAccessPanelSubContatiner1);
        return as;
    }

    public ArrayList<SecLaunchSubContainer> getAppDrawerSubContainters(){
        ArrayList<SecLaunchSubContainer> as = new ArrayList<SecLaunchSubContainer>();
        as.add(appdrawerSubContainer);
        return as;
    }
}