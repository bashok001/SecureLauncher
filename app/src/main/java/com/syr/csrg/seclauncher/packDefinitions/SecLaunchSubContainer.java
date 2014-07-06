package com.syr.csrg.seclauncher.packDefinitions;

import com.syr.csrg.seclauncher.domain.ContainerContext;
import com.syr.csrg.seclauncher.domain.SubContainerContext;

import java.util.ArrayList;

public abstract class SecLaunchSubContainer {

    private static SubContainerContext subContainerContext;

    ArrayList<ItemInfo> items;
    private String containerID;

    public String getContainerID() {
        return containerID;
    }

    public SecLaunchSubContainer(){
        items = new ArrayList<ItemInfo>();
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public SubContainerContext getSubContainerContext() {
        return subContainerContext;
    }

    public void setContainerContext(SubContainerContext containerContext) {
        this.subContainerContext = containerContext;
    }

    public ArrayList<ItemInfo> getSubContainers() {
        return items;
    }

    public void insertIntoScList(int order, ItemInfo secLaunchSubContainer){
        if (items == null)
            items = new ArrayList<ItemInfo>();
        this.items.add(order, secLaunchSubContainer);
    }
}
