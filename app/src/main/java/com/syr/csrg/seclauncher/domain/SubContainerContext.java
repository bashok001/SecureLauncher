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
 * File: com.syr.csrg.seclauncher.domain.SubContainerContext
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/6/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.domain;

public class SubContainerContext {
    public static final String CLASSNAME = "SubContainerContext";
    static int noOfApps = 0;

    public static int getNoOfApps() {
        final String METHODNAME = "getNoOfApps";
        return noOfApps;
    }

    public static void addToNoOfApps(){
        final String METHODNAME = "addToNoOfApps";
        noOfApps++;
    }

    public static void removeFromNoOfApps(){
        final String METHODNAME = "removeFromNoOfApps";
        noOfApps--;
    }
}
