package com.syr.csrg.seclauncher.ui.activity;


import android.os.Bundle;

import com.syr.csrg.seclauncher.R;
import com.syr.csrg.seclauncher.agent.ConfigRetrievalAgent;
import com.syr.csrg.seclauncher.engine.ContainerManager;
import com.syr.csrg.seclauncher.packDefinitions.LauncherSettings;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;
import com.syr.csrg.seclauncher.testLoader.Loader;
import com.syr.csrg.seclauncher.ui.fragment.HomescreenFragment;

import java.util.ArrayList;

public class HomescreenActivity extends ContainerManagementActivity {

    private static ArrayList<SecLaunchSubContainer> homescreenSubContainers = null;
    public HomescreenFragment homescreenFragment = null;

    public static ArrayList<SecLaunchSubContainer> getHomescreenSubContainers() {
        if (homescreenSubContainers == null) {
            ConfigRetrievalAgent configRetrievalAgent = new ConfigRetrievalAgent();
            homescreenSubContainers =  configRetrievalAgent.getSubContainersById(LauncherSettings.HOME_SCREEN_SC);

        }
        return homescreenSubContainers;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        homescreenFragment = new HomescreenFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.homescreen_activity, homescreenFragment)
                    .commit();
        }

        Loader.getInstance().load(this);

        ContainerManager cm = getContainerManager();
        cm.setMaxNumSubcontainers(LauncherSettings.CONTAINERS_PER_PAGE * 5);
        cm.setActivity(this);
        cm.setSubContainers(getHomescreenSubContainers());
        cm.setFragmentId(R.id.homescreen_activity);
        cm.setFragmentType(ContainerManager.HOMESCREEN);
    }

    @Override
    public void onBackPressed() {
        if (!getContainerManager().onBackPressed()) {
            super.onBackPressed();
        }
    }
}

