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
 * File: com.syr.csrg.seclauncher.domain.SecLaunchConfig
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/6/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.domain;

import com.syr.csrg.seclauncher.packDefinitions.SecLaunchContainer;

import java.util.ArrayList;

public class SecLaunchContext {
    public static final String CLASSNAME = "SecLaunchContext";

    private static SecLaunchContext secLaunchContext = null;
    private ArrayList<SecLaunchContainer> slContainerArraylist;

    static int noOfContainers;

    public static int getNoOfContainers() {
        final String METHODNAME = "getNoOfContainers";
        return noOfContainers;
    }

    public static void addToContainers() {
        final String METHODNAME = "addToContainers";
        noOfContainers++;
    }

    public static void removeFromContainers() {
        final String METHODNAME = "removeFromContainers";
        noOfContainers--;
    }

    private SecLaunchContext(){
    }

    public static SecLaunchContext getInstance() {
        final String METHODNAME = "getInstance";
        if (secLaunchContext == null) {
            secLaunchContext = new SecLaunchContext();
            secLaunchContext.slContainerArraylist=new ArrayList<SecLaunchContainer>();
        }
        return secLaunchContext;
    }

    public ArrayList<SecLaunchContainer> getSlContainerArraylist() {
        final String METHODNAME = "getSlContainerArraylist";
        return this.slContainerArraylist;
    }

    public void insertIntoSlCArraylist(int order, SecLaunchContainer secLaunchContainer){
        final String METHODNAME = "insertIntoSlCArraylist";
        if(slContainerArraylist == null)
            slContainerArraylist=new ArrayList<SecLaunchContainer>();

        if(order > slContainerArraylist.size())
            while(slContainerArraylist.size() != order)

        this.slContainerArraylist.add(order,secLaunchContainer);
    }

    public void addToContainers(SecLaunchContainer secLaunchContainer){
        slContainerArraylist.add(secLaunchContainer);
        noOfContainers++;
    }

}