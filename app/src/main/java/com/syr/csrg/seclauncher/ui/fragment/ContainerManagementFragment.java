package com.syr.csrg.seclauncher.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.syr.csrg.seclauncher.R;
import com.syr.csrg.seclauncher.packDefinitions.LauncherSettings;
import com.syr.csrg.seclauncher.ui.activity.ContainerManagementActivity;
import com.syr.csrg.seclauncher.ui.adapter.ContainerManagementViewPagerAdapter;
import com.syr.csrg.seclauncher.engine.ContainerManager;


/**
 * Created by Shane on 7/29/14.
 */
public class ContainerManagementFragment extends Fragment {

    private ContainerManager cm = null;

    public static final String POSITION = "position";

    public static final ContainerManagementFragment newInstance(int position) {
        ContainerManagementFragment fragment = new ContainerManagementFragment();
        Bundle bdl = new Bundle(1);
        bdl.putInt(POSITION, position);
        fragment.setArguments(bdl);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.container_management, container, false);

        int position = getArguments().getInt(POSITION);

        rootView.setBackgroundColor(android.graphics.Color.argb(100, 0, 0, 0));

        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.container_manager_pager);
        int numPages = 1;
        if (cm.getNumSubContainers() > LauncherSettings.CONTAINERS_PER_PAGE) {
            numPages = 1 + (cm.getNumSubContainers() - 1) / 7;
        }

        final ContainerManagementViewPagerAdapter adapter = new ContainerManagementViewPagerAdapter(getChildFragmentManager(), numPages);
        viewPager.setAdapter(adapter);

        ImageButton addButton = (ImageButton) rootView.findViewById(R.id.plus_sign);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cm.onAddSubContainer();
            }
        });

        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Drag new container into current page
                return false;
            }
        });

        ImageView trashIcon = (ImageView) rootView.findViewById(R.id.trashed);

        trashIcon.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                View sourceView = (View) dragEvent.getLocalState();
                ViewGroup owner = (ViewGroup) sourceView.getParent();
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        if (view != null && view.getId() == R.id.trashed) {
                            cm.setIsOverTrash(true);
                            cm.colorDraggedView();
                            view.animate().scaleX(1.5f);
                            view.animate().scaleY(1.5f);
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        if (view != null && view.getId() == R.id.trashed) {
                            cm.setIsOverTrash(false);
                            cm.clearDraggedViewColor();
                            view.animate().scaleX(1.0f);
                            view.animate().scaleY(1.0f);
                            view.invalidate();
                        }
                        return true;
                    case DragEvent.ACTION_DROP:
                        if (view != null && view.getId() == R.id.trashed) {
                            if (owner != null && owner instanceof GridLayout) {
                                if (owner.getId() == R.id.gridContainer) {
                                    GridLayout gridLayout = (GridLayout) owner;
                                    int pos = gridLayout.indexOfChild(sourceView);
                                    int c = pos + cm.getDragStartPage() * 7;
                                    Log.d("deleting", Integer.toString(c));
                                    cm.onDeleteSubContainer(c);
                                }
                            }
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (view != null && view.getId() == R.id.trashed) {
                            view.animate().scaleX(1.0f);
                            view.animate().scaleY(1.0f);
                            cm.onDragEnded();
                            cm.setIsOverTrash(false);
                        }
                        return true;
                }
                return false;
            }
        });

        viewPager.setCurrentItem(position / LauncherSettings.CONTAINERS_PER_PAGE, false);

        cm.setTrashIcon(trashIcon);
        cm.setAddContainerButton(addButton);
        cm.setManagerPager(viewPager);
        cm.setManagerAdapter(adapter);

        return rootView;

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
