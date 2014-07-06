package com.syr.csrg.seclauncher.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SecureLauncherConstants {

    public static final String PROJECT_NAME = "SecureLauncherV1";
    public static final String PROJECT_PACKAGE="com.syr.csrg.seclauncher";

    public static final String SYSTEM_LINE_SEPERATOR_STRING = "line.seperator";
    public static String HOST_NAME = "";
    static {
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            String hostName = addr.getHostName();
            HOST_NAME = hostName;
        } catch (UnknownHostException e) {
            // TODO HANDLE IT
        }
    }


}
