package com.syr.csrg.seclauncher.domain;

public class ContainerContext {
    static int noOfSubs = 0;

    public static int getNoOfSubs() {
        return noOfSubs;
    }

    public static void addToSubs() {
        noOfSubs++;
    }
}
