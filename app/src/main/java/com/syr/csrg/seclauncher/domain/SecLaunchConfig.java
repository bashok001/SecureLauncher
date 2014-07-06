package com.syr.csrg.seclauncher.domain;

import com.syr.csrg.seclauncher.packDefinitions.SecLaunchContainer;

import java.util.ArrayList;

public class SecLaunchConfig {
    private static SecLaunchConfig secLaunchConfig = null;
    private ArrayList<SecLaunchContainer> slContainerArraylist;

    private SecLaunchConfig(){
    }

    public static SecLaunchConfig getInstance() {
        if (secLaunchConfig == null) {
            secLaunchConfig = new SecLaunchConfig();
            secLaunchConfig.slContainerArraylist=new ArrayList<SecLaunchContainer>();
        }
        return secLaunchConfig;
    }

    public ArrayList<SecLaunchContainer> getSlContainerArraylist() {
        return this.slContainerArraylist;
    }

    public void insertIntoSlCArraylist(int order, SecLaunchContainer secLaunchContainer){
        if(slContainerArraylist == null)
            slContainerArraylist=new ArrayList<SecLaunchContainer>();
        this.slContainerArraylist.add(order,secLaunchContainer);
    }

}