package com.syr.csrg.seclauncher.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.syr.csrg.seclauncher.provider.config.ProviderConfig;

public class SecLauncherProvider extends ContentProvider{

    private static final String TAG = "SecLauncher.SecLauncherProvider";
    private static final boolean LOGD = false;

    private static final String DATABASE_NAME = "seclauncher.db";
    private static final int DATABASE_VERSION = 15;

    static final String AUTHORITY = ProviderConfig.AUTHORITY;

    static final Uri CONTENT_APPWIDGET_RESET_URI =
            Uri.parse("content://" + AUTHORITY + "/appWidgetReset");

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
