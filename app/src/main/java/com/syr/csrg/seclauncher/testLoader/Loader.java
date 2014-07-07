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

import android.content.ClipData;

import com.syr.csrg.seclauncher.domain.ContainerContext;
import com.syr.csrg.seclauncher.domain.SecLaunchContext;
import com.syr.csrg.seclauncher.packDefinitions.ItemInfo;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchContainer;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;

import java.util.ArrayList;

public class Loader {
    public void load(){
        SecLaunchContext slc = SecLaunchContext.getInstance();
        ArrayList<SecLaunchContainer> slContainerArraylist = slc.getSlContainerArraylist();
        SecLaunchContainer testContatiner = new SecLaunchContainer(){
        };

        testContatiner.setContainerID("test");
        SecLaunchSubContainer testSubContainer = new SecLaunchSubContainer() {
        };

        testSubContainer.setSubContainerID("test");

        ItemInfo in = new ItemInfo();
        in.setId(01);
        in.setContainer(01);
        in.setItemType(01);
        in.setSpanX(01);
        in.setSpanY(01);
        in.setTitle("com.android.");

        testSubContainer.insertIntoItemList(0,);
        testContatiner.insertIntoScList(0, );


        slContainerArraylist.add(0,testContatiner);
    }
}
