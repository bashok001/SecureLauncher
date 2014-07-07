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
 * File: com.syr.csrg.seclauncher.agent.SharedPreferenceAgent
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/6/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.agent;

import android.content.Context;
import android.content.SharedPreferences;

import com.syr.csrg.seclauncher.util.SecureLauncherConstants;

public class SharedPreferenceAgent {
    public static final String CLASSNAME = "SharedPreferenceAgent";

    public static SharedPreferences getSharedPrefPrivate(Context context){
        final String METHODNAME = "getSharedPrefPrivate";
        return context.getApplicationContext().getSharedPreferences(SecureLauncherConstants.PROJECT_PACKAGE, Context.MODE_PRIVATE);
    }

}
