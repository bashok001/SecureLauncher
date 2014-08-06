package com.syr.csrg.seclauncher.ui.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.syr.csrg.seclauncher.engine.ContainerManager;
import com.syr.csrg.seclauncher.ui.activity.ContainerManagementActivity;

/**
 * Created by Shane on 7/31/14.
 */
public abstract class SubContainerFragment extends Fragment {

    private ContainerManager cm = null;

    public ContainerManager getContainerManager() {
        return cm;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            ContainerManagementActivity containerManagementActivity = (ContainerManagementActivity) activity;
            cm = containerManagementActivity.getContainerManager();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must extend ContainerManagementActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        cm = null;
    }

}
