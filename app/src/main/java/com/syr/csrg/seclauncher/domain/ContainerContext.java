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
 * File: com.syr.csrg.seclauncher.domain.ContainerContext
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/6/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.domain;

public class ContainerContext {
    public static final String CLASSNAME = "ContainerContext";
    static int noOfSubs = 0;

    public static int getNoOfSubs() {
        final String METHODNAME = "getNoOfSubs";
        return noOfSubs;
    }

    public static void addToSubs() {
        final String METHODNAME = "addToSubs";
        noOfSubs++;
    }

    public static void removeFromSubs(){
        final String METHODNAME = "removeFromSubs";
        noOfSubs--;
    }
}
