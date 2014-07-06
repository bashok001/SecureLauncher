package com.syr.csrg.seclauncher.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SecLauncherException extends Exception {

    private StringBuffer stackTrace = new StringBuffer(" ");
    private String detailedMessage;
    private String messageID;
    private String messageText;

    public SecLauncherException() {
    }

    /**
     * Constructs a SecLauncherException, with input parameters.
     *
     * @param messageID   the message id of this exception.
     * @param messageText the message text of this exception.
     */
    public SecLauncherException(String messageID, String messageText) {
        if (messageID != null)
            this.messageID = messageID;

        if (messageText != null)
            this.messageText = messageText;
        detailedMessage = messageID + ":" + messageText;
    }

    /**
     * Constructs a SecLauncherException, with input parameters. keeps the
     * stacktrace of input throwable into this exception's stack trace.
     *
     * @param messageID   the message id of this exception.
     * @param messageText the message text of this exception.
     * @param th          the actual exception that is thrown.
     */
    public SecLauncherException(String messageID, String messageText,
                                Throwable th) {
        super(messageID + ":" + messageText, th);
        stackTrace = new StringBuffer();
        if (messageID != null)
            this.messageID = messageID;

        if (messageText != null)
            this.messageText = messageText;
        detailedMessage = messageID + ":" + messageText;
        if (th != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            th.printStackTrace(pw);
            stackTrace.append(sw.getBuffer().toString());
        }

    }

    /**
     * Returns the stack trace.
     *
     * @return stack trace of this exception in string format.
     */
    public String getLocalizedMessage() {
        return stackTrace.toString();
    }

    public String getMessage() {
        return detailedMessage;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageText() {
        return messageText;
    }

    public String toString() {
        String s = getClass().getName();
        String message = getMessage();
        message = message + "\n" + stackTrace.toString();
        return (message != null || message.equals("") || message.length() == 0) ? (s + ": " + message) : s;
    }
}
