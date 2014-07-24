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
 * File: com.syr.csrg.seclauncher.packDefinitions.SecLaunchContainer
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/6/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.packDefinitions;

import com.syr.csrg.seclauncher.domain.ContainerContext;

import java.util.ArrayList;

public abstract class SecLaunchContainer {
    public static final String CLASSNAME = "SecLaunchContainer";
    private static ContainerContext containerContext;

    ArrayList<SecLaunchSubContainer> subContainers;
    private int containerID;

    public int getContainerID() {
        final String METHODNAME = "getContainerID";
        return containerID;
    }

    public SecLaunchContainer(){
        final String METHODNAME = "SecLaunchContainer";
        subContainers = new ArrayList<SecLaunchSubContainer>();
    }

    public void setContainerID(int containerID) {
        final String METHODNAME = "setContainerID";
        this.containerID = containerID;
    }

    public ContainerContext getContainerContext() {
        final String METHODNAME = "getContainerContext";
        return containerContext;
    }

    public void setContainerContext(ContainerContext containerContext) {
        final String METHODNAME = "setContainerContext";
        this.containerContext = containerContext;
    }

    public ArrayList<SecLaunchSubContainer> getSubContainers() {
        final String METHODNAME = "getSubContainers";
        return subContainers;
    }

    public void insertIntoScList(int order, SecLaunchSubContainer secLaunchSubContainer){
        final String METHODNAME = "insertIntoScList";
        if (subContainers == null)
            subContainers = new ArrayList<SecLaunchSubContainer>();
        this.subContainers.add(order, secLaunchSubContainer);
    }

    public void addToSubContainers(SecLaunchSubContainer secLaunchSubContainer){
        subContainers.add(secLaunchSubContainer);
        containerContext.addToSubs();
    }
}