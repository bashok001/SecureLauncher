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
 * File: com.syr.csrg.seclauncher.util.LogUtil
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/6/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.util;

import android.util.Log;

public class LogUtil {
    private static String tag = SecureLauncherConstants.PROJECT_NAME;

    public static void logDebug(String className, String methodName, Throwable e) {
        tag = className + "||" + methodName;
        Log.d(tag, e.getLocalizedMessage(), e);
    }

    public static void logError(String className, String methodName, Throwable e) {
        tag = className + "||" + methodName;
        Log.e(tag, e.getLocalizedMessage(), e);
    }

    public static void logInfo(String className, String methodName, Throwable e) {
        tag = className + "||" + methodName;
        Log.i(tag, e.getLocalizedMessage(), e);
    }

    public static void logVerbose(String className, String methodName, Throwable e) {
        tag = className + "||" + methodName;
        Log.v(tag, e.getLocalizedMessage(), e);
    }

    public static void logWarn(String className, String methodName, Throwable e) {
        tag = className + "||" + methodName;
        Log.w(tag, e.getLocalizedMessage(), e);
    }

    public static void logWtf(String className, String methodName, Throwable e) {
        tag = className + "||" + methodName;
        Log.wtf(tag, e.getLocalizedMessage(), e);
    }

    public static void logError(String className, String methodName, String message) {
        tag = className + "||" + methodName;
        Log.e(tag, message);
    }

    public static void logInfo(String className, String methodName, String message) {
        tag = className + "||" + methodName;
        Log.i(tag, message);
    }

    public static void logVerbose(String className, String methodName, String message) {
        tag = className + "||" + methodName;
        Log.v(tag, message);
    }

    public static void logWarn(String className, String methodName, String message) {
        tag = className + "||" + methodName;
        Log.w(tag, message);
    }

    public static void logWtf(String className, String methodName, String message) {
        tag = className + "||" + methodName;
        Log.wtf(tag, message);
    }

    public static void logDebug(String className, String methodName, String message) {
        tag = className + "||" + methodName;
        Log.d(tag, message);
    }
}