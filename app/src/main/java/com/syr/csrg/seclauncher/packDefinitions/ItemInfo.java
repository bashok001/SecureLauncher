package com.syr.csrg.seclauncher.packDefinitions;

public class ItemInfo {
    String itemName;
    String icon;

    public String getIcon() {
        return icon;
    }

    public String getItemName() {
        return itemName;
    }

    int order;

    public void setOrder(int order) {
        this.order = order;
    }

}
