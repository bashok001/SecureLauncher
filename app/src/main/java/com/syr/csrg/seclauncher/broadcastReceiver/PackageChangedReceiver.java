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
 * File: com.syr.csrg.seclauncher.broadcastReceiver.PackageChangedReceiver
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 8/5/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PackageChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        final String packageName = intent.getData().getSchemeSpecificPart();
        if (packageName == null || packageName.length() == 0) {
            return;
        }
        /*android.intent.action.PACKAGE_CHANGED
        android.intent.action.PACKAGE_REPLACED
        android.intent.action.PACKAGE_REMOVED*/
    }
}
