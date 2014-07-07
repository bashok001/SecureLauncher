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
 * File: com.syr.csrg.seclauncher.provider.SecLauncherProvider
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/6/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.syr.csrg.seclauncher.provider.config.ProviderConfig;

public class SecLauncherProvider extends ContentProvider{

    private static final String CLASSNAME = "SecLauncher.SecLauncherProvider";
    private static final boolean LOGD = false;

    private static final String DATABASE_NAME = "seclauncher.db";
    private static final int DATABASE_VERSION = 15;

    static final String AUTHORITY = ProviderConfig.AUTHORITY;

    static final Uri CONTENT_APPWIDGET_RESET_URI =
            Uri.parse("content://" + AUTHORITY + "/appWidgetReset");

    @Override
    public boolean onCreate() {
        final String METHODNAME = "onCreate";
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        final String METHODNAME = "query";
        return null;
    }

    @Override
    public String getType(Uri uri) {
        final String METHODNAME = "getType";
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final String METHODNAME = "insert";
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        final String METHODNAME = "delete";
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        final String METHODNAME = "update";
        return 0;
    }
}
