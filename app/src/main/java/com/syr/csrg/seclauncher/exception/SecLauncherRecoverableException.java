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
 * File: com.syr.csrg.seclauncher.exception.SecLauncherRecoverableException
 *
 * Summary of Changes:
 * "Date of Change"	"Developer's Name"	"Changes"
 * 7/6/2014			Ashok Bommisetti	Initial Version
 */

package com.syr.csrg.seclauncher.exception;

public class SecLauncherRecoverableException extends SecLauncherException {

    public SecLauncherRecoverableException(String messageID, String messageText) {
        super(messageID, messageText);
    }

    public SecLauncherRecoverableException(String messageID, String messageText,
                                           Throwable th) {
        super(messageID, messageText, th);
    }
}
