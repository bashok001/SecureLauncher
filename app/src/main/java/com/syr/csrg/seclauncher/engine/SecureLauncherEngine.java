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
 * File: com.syr.csrg.seclauncher.engine.SecureLauncherEngine
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/6/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.engine;

import com.syr.csrg.seclauncher.domain.ContainerContext;
import com.syr.csrg.seclauncher.domain.SecLaunchContext;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchContainer;

import java.util.ArrayList;

public class SecureLauncherEngine {
    static ContainerContext containerContext;
    public static final String CLASSNAME = "SecureLauncherEngine";

    SecLaunchContext slContext = SecLaunchContext.getInstance();
    ArrayList<SecLaunchContainer> slContainerArraylist = slContext.getSlContainerArraylist();


}
