package com.syr.csrg.seclauncher.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.syr.csrg.seclauncher.engine.ContainerManager;

/**
 * Created by Shane on 7/31/14.
 */
public class ContainerManagementActivity extends FragmentActivity {

    private ContainerManager cm = new ContainerManager();

    public ContainerManager getContainerManager() {
        return cm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
