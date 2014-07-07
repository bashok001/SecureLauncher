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
 * File: com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/6/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.packDefinitions;

import com.syr.csrg.seclauncher.domain.ContainerContext;
import com.syr.csrg.seclauncher.domain.SubContainerContext;

import java.util.ArrayList;

public abstract class SecLaunchSubContainer {
    public static final String CLASSNAME = "SecLaunchSubContainer";
    private static SubContainerContext subContainerContext;

    ArrayList<ItemInfo> items;
    private String subContainerID;

    public SecLaunchSubContainer(){
        final String METHODNAME = "SecLaunchSubContainer";
        items = new ArrayList<ItemInfo>();
    }

    public String getSubContainerID() {
        final String METHODNAME = "getSubContainerID";
        return subContainerID;
    }

    public void setSubContainerID(String subContainerID) {
        final String METHODNAME = "setSubContainerID";
        this.subContainerID = subContainerID;
    }

    public SubContainerContext getSubContainerContext() {
        final String METHODNAME = "getSubContainerContext";
        return subContainerContext;
    }

    public void setContainerContext(SubContainerContext containerContext) {
        final String METHODNAME = "setContainerContext";
        this.subContainerContext = containerContext;
    }

    public ArrayList<ItemInfo> getItems() {
        final String METHODNAME = "getItems";
        return items;
    }

    public void insertIntoItemList(int order, ItemInfo itemInfo){
        final String METHODNAME = "insertIntoItemList";
        if (items == null)
            items = new ArrayList<ItemInfo>();
        this.items.add(order, itemInfo);
    }
}