package com.syr.csrg.seclauncher.packDefinitions;

import com.syr.csrg.seclauncher.domain.ContainerContext;

import java.util.ArrayList;

public abstract class SecLaunchContainer {
    private static ContainerContext containerContext;

    ArrayList<SecLaunchSubContainer> subContainers;
    private String containerID;

    public String getContainerID() {
        return containerID;
    }

    public SecLaunchContainer(){
        subContainers = new ArrayList<SecLaunchSubContainer>();
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public ContainerContext getContainerContext() {
        return containerContext;
    }

    public void setContainerContext(ContainerContext containerContext) {
        this.containerContext = containerContext;
    }

    public ArrayList<SecLaunchSubContainer> getSubContainers() {
        return subContainers;
    }

    public void insertIntoScList(int order, SecLaunchSubContainer secLaunchSubContainer){
        if (subContainers == null)
            subContainers = new ArrayList<SecLaunchSubContainer>();
        this.subContainers.add(order, secLaunchSubContainer);
    }
}