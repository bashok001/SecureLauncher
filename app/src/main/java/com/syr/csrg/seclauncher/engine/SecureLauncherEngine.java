package com.syr.csrg.seclauncher.engine;

import com.syr.csrg.seclauncher.domain.ContainerContext;
import com.syr.csrg.seclauncher.domain.SecLaunchConfig;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchContainer;

import java.util.ArrayList;

public class SecureLauncherEngine {
    static ContainerContext containerContext;

    SecLaunchConfig slConfig = SecLaunchConfig.getInstance();
    ArrayList<SecLaunchContainer> slContainerArraylist = slConfig.getSlContainerArraylist();


}
