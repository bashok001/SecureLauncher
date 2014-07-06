package com.syr.csrg.seclauncher.exception;

public class SecLauncherUnrecoverableException extends SecLauncherException {

    public SecLauncherUnrecoverableException(String messageID, String messageText) {
        super(messageID, messageText);
    }

    public SecLauncherUnrecoverableException(String messageID, String messageText,
                                             Throwable th) {
        super(messageID, messageText, th);
    }

}