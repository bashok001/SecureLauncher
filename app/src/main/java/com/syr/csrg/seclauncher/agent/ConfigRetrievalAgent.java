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
 * File: com.syr.csrg.seclauncher.agent.ConfigRetrievalAgent
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/24/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.agent;

import com.syr.csrg.seclauncher.packDefinitions.LauncherSettings;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchContainer;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;
import com.syr.csrg.seclauncher.testLoader.Loader;

import java.util.ArrayList;

public class ConfigRetrievalAgent {
    public static SecLaunchContainer getContainerByCurrentMode(){
        return Loader.getInstance().getContainters();
    }

    public static ArrayList<SecLaunchSubContainer> getSubContainersById(int subContainerID) {
        if(subContainerID == LauncherSettings.HOME_SCREEN_SC) return Loader.getInstance().getHomeScreenSubContainters();
        if(subContainerID == LauncherSettings.APP_DRAWER_SC) return Loader.getInstance().getAppDrawerSubContainters();
        if(subContainerID == LauncherSettings.QUICK_ACCESS_PANEL_SC) return Loader.getInstance().getQAPSubContainters();
        return new ArrayList<SecLaunchSubContainer>();
    }
}
