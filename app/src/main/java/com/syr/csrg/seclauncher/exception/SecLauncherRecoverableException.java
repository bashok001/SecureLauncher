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
