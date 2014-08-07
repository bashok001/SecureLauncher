package com.syr.csrg.seclauncher.engine;

import android.animation.Animator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syr.csrg.seclauncher.R;
import com.syr.csrg.seclauncher.packDefinitions.ItemInfo;
import com.syr.csrg.seclauncher.packDefinitions.LauncherSettings;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchContainer;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;
import com.syr.csrg.seclauncher.packDefinitions.ShortcutInfo;
import com.syr.csrg.seclauncher.ui.activity.ContainerManagementActivity;
import com.syr.csrg.seclauncher.ui.adapter.ContainerManagementViewPagerAdapter;
import com.syr.csrg.seclauncher.ui.adapter.HomescreenViewPagerAdapter;
import com.syr.csrg.seclauncher.ui.adapter.SubContainerViewPagerAdapter;
import com.syr.csrg.seclauncher.ui.fragment.ContainerManagementFragment;
import com.syr.csrg.seclauncher.ui.fragment.ContainerManagementViewPagerFragment;
import com.syr.csrg.seclauncher.ui.fragment.HomescreenFragment;
import com.syr.csrg.seclauncher.ui.fragment.SubContainerFragment;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Shane on 7/31/14.
 */

public class ContainerManager
{

    private int maxNumSubcontainers = LauncherSettings.CONTAINERS_PER_PAGE;
    private ContainerManagementActivity activity = null;
    private int fragmentId = 0;
    private int fragmentType = 0;
    public static final int HOMESCREEN = 1;
    private boolean overTrash = false;

    private ImageView trashIcon = null;
    private ImageButton addContainerButton = null;

    private ViewPager subContainerPager = null;
    private ViewPager managerPager = null;

    private SubContainerViewPagerAdapter subContainerAdapter = null;
    private ContainerManagementViewPagerAdapter managerAdapter = null;

    private View draggedView = null;
    private Drawable draggedViewBackground = null;
    private int dragStartPage = -1;

    private int addButtonTopY = 0;
    private int trashBottomY = 0;

    SecLaunchContainer container = null;
    ArrayList<SecLaunchSubContainer> subContainers = null;


    // Set in ContainerManagementActivity

    public void setMaxNumSubcontainers(int max) {
        this.maxNumSubcontainers = max;
    }

    public void setActivity(ContainerManagementActivity containerManagementActivity) {
        this.activity = containerManagementActivity;
    }

    public void setContainer(SecLaunchContainer container) {
        this.container = container;
        this.subContainers = container.getSubContainers();
    }

    public void setSubContainers(ArrayList<SecLaunchSubContainer> subContainers) {
        this.subContainers = subContainers;
    }

    public void setFragmentId(int id) {
        this.fragmentId = id;
    }

    public void setFragmentType(int type) {
        this.fragmentType = type;
    }


    // Set in SubContainerFragment

    public void setSubContainerPager(ViewPager pager) {
        this.subContainerPager = pager;
    }

    public void setSubContainerAdapter(SubContainerViewPagerAdapter adapter) {
        this.subContainerAdapter = adapter;
    }


    // Set in ContainerManagementFragment

    public void setTrashIcon(ImageView imageView) {
        this.trashIcon = imageView;
    }

    public void setAddContainerButton(ImageButton imageButton) {
        this.addContainerButton = imageButton;
    }

    public void setManagerPager(ViewPager pager) {
        this.managerPager = pager;
        this.managerPager.setOffscreenPageLimit(maxNumSubcontainers / LauncherSettings.CONTAINERS_PER_PAGE);
        this.managerPager.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_EXITED:
                        float x = dragEvent.getX();
                        if (x < 90) {
                            setManagerPageSmooth(getMangerPage() - 1);
                        }
                        else if (x > 630) {
                            setManagerPageSmooth(getMangerPage() + 1);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    public void setManagerAdapter(ContainerManagementViewPagerAdapter adapter) {
        this.managerAdapter = adapter;
    }


    public SubContainerFragment getNewInstanceOfFragment(int page) {
        switch (fragmentType) {
            case HOMESCREEN:
                return HomescreenFragment.newInstance(page);
            default:
                return null;
        }
    }

    public ArrayList<SecLaunchSubContainer> getSubContainers() {
        return subContainers;
    }

    public int getNumSubContainers() {
        return subContainers.size();
    }

    private void setSubContainerPage(int page) {
        this.subContainerPager.setCurrentItem(page, false);
    }

    private void setManagerPage(int page) {
        this.managerPager.setCurrentItem(page, false);
    }

    private void setManagerPageSmooth(int page) { this.managerPager.setCurrentItem(page, true); }

    public int getSubContainerPage() {
        return subContainerPager.getCurrentItem();
    }

    public int getMangerPage() {
        return managerPager.getCurrentItem();
    }

    public void setDraggedView(View view) {
        this.draggedView = view;
    }

    public void setIsOverTrash(boolean isOverTrash) {
        this.overTrash = isOverTrash;
        if (this.overTrash) {
            if (overTrashListener != null) {
                overTrashListener.isOverTrash();
            }
        }
    }

    public boolean isOverTrash() {
        return overTrash;
    }

    public void colorDraggedView() {
        if (draggedView != null) {
            Drawable[] layers = new Drawable[2];
            draggedViewBackground = draggedView.getBackground();
            layers[0] = draggedViewBackground;
            layers[1] = activity.getResources().getDrawable(R.drawable.shape_drop_in_trash);
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            draggedView.setBackground(layerDrawable);
        }
    }

    public void clearDraggedViewColor() {
        if (draggedView != null && draggedViewBackground != null) {
            draggedView.setBackground(draggedViewBackground);
        }
    }

    public boolean onBackPressed() {
        if (activity != null) {
            FragmentManager fm = activity.getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(fragmentId);
            if (fragment instanceof ContainerManagementFragment) {
                if (fragmentType == HOMESCREEN) {
                    fm.beginTransaction()
                            .replace(fragmentId, HomescreenFragment.newInstance(getSubContainerPage()))
                            .commit();
                    return true;
                }
            }
        }
        return false;
    }

    public void onSubContainersUpdated() {
        int numContainers = getNumSubContainers();
//        if (numContainers <= 0) {
//            SecLaunchSubContainer newSubCont = new SecLaunchSubContainer() {};
//            newSubCont.setSubContainerID(1);
//            subContainers.add(newSubCont);
//            numContainers = getNumSubContainers();
//        }
        int subContainerPage = getSubContainerPage();
        int managerPage = getMangerPage();
        switch (fragmentType) {
            case HOMESCREEN:
                subContainerAdapter = new HomescreenViewPagerAdapter(activity.getSupportFragmentManager(), numContainers);
                if (subContainerPager != null) {
                    subContainerPager.setAdapter(subContainerAdapter);
                    setSubContainerPage(subContainerPage);
                    subContainerPager.invalidate();
                }
                break;
            default:
                break;
        }
        int numPages = 1 + (numContainers - 1) / LauncherSettings.CONTAINERS_PER_PAGE;
        if (managerAdapter != null) {
            managerAdapter.notifyDataSetChanged();
        }
        managerAdapter = new ContainerManagementViewPagerAdapter(activity.getSupportFragmentManager(), numPages);
        if (managerPager != null) {
            managerPager.setAdapter(managerAdapter);
            setManagerPage(managerPage);
            managerPager.invalidate();
        }
    }

    public Fragment getSubContainerFragmentPage(int page) {
        if (subContainerAdapter != null) {
            return subContainerAdapter.getUnclickableItem(page);
        }
        return null;
    }

    public void saveDragStartPage() {
        this.dragStartPage = getMangerPage();
    }

    public int getDragStartPage() {
        return dragStartPage;
    }

    private final Animator.AnimatorListener changePageAtEnd = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            setManagerPageSmooth((getNumSubContainers() - 1) / LauncherSettings.CONTAINERS_PER_PAGE);
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    public void onAddSubContainer() {
        if (getNumSubContainers() < maxNumSubcontainers) {
            SecLaunchSubContainer newSubCont = new SecLaunchSubContainer() {
            };
            newSubCont.setSubContainerID(1);
            subContainers.add(newSubCont);
//        onSubContainersUpdated();
            Log.d("add new size", Integer.toString(getNumSubContainers()));


            Log.d("set count", Integer.toString(getNumSubContainers()));

            subContainerAdapter.setCount(getNumSubContainers());
            subContainerPager.invalidate();

            if (getNumSubContainers() <= LauncherSettings.CONTAINERS_PER_PAGE) {
                managerAdapter.setCount(1);
            } else {
                managerAdapter.setCount(1 + (getNumSubContainers() - 1) / LauncherSettings.CONTAINERS_PER_PAGE);
            }
            managerAdapter.notifyDataSetChanged();


//        managerPager.requestLayout();
//        managerPager.invalidate();
            setManagerPageSmooth((getNumSubContainers() - 1) / LauncherSettings.CONTAINERS_PER_PAGE);

            if (getNumSubContainers() == maxNumSubcontainers) {
                addContainerButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void onDeleteSubContainer(int index) {
        subContainers.remove(index);

        int page = getMangerPage();
        int lastPage = managerAdapter.getCount() - 1;
        if (page == lastPage && page != 0 && (getNumSubContainers()) % LauncherSettings.CONTAINERS_PER_PAGE == 0) {
            page = page - 1;
        }
        if (getNumSubContainers() == 0) {
            onAddSubContainer();
        }
        setManagerPageSmooth(page);
        Log.d("delete new size", Integer.toString(getNumSubContainers()));

        subContainerAdapter.setCount(getNumSubContainers());
        subContainerPager.invalidate();



        if (getNumSubContainers() < LauncherSettings.CONTAINERS_PER_PAGE) {
            managerAdapter.setCount(1);
        }
        else {
            managerAdapter.setCount(1 + (getNumSubContainers() - 1) / LauncherSettings.CONTAINERS_PER_PAGE);
        }

        managerAdapter.notifyDataSetChanged();


        Log.d("set count", Integer.toString(getNumSubContainers()));

        //onSubContainersUpdated();
        addContainerButton.setVisibility(View.VISIBLE);


    }

    public void onSwapSubContainers(int from, int to) {
        Collections.swap(subContainers, from, to);
        switch (fragmentType) {
            case HOMESCREEN:
//                subContainerAdapter = new HomescreenViewPagerAdapter(activity.getSupportFragmentManager(), getNumSubContainers());
//                if (subContainerAdapter != null) {
//                    subContainerAdapter = new HomescreenViewPagerAdapter(activity.getSupportFragmentManager(), getNumSubContainers());
//                }
//                if (subContainerPager != null) {
//                    subContainerPager.invalidate();
//                }
                subContainerPager.invalidate();
                break;
            default:
                break;
        }
        managerAdapter.notifyDataSetChanged();
    }

    public void onZoomOut() {
        if (subContainerPager != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(fragmentId, ContainerManagementFragment.newInstance(getSubContainerPage()))

                    .commit();
        }
    }

    public void onZoomIn(int page) {
        activity.getSupportFragmentManager().beginTransaction()
                .replace(fragmentId, getNewInstanceOfFragment(page))
                .commit();
    }

    public void onDragStarted() {
        dragStartPage = getMangerPage();
        if (managerPager != null) {
            managerPager.animate()
                    .setDuration(250)
                    .scaleX(0.75f)
                    .scaleY(0.75f)
                    .start();
        }
        if (addContainerButton != null && getNumSubContainers() < maxNumSubcontainers) {
            addButtonTopY = addContainerButton.getTop();
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            addContainerButton.animate().setDuration(650).y(dm.heightPixels + 100).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    addContainerButton.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
        if (trashIcon != null) {
            trashBottomY = trashIcon.getTop();
            trashIcon.animate().setDuration(0).y(trashBottomY - 100).start();
            trashIcon.animate().setDuration(650).y(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    trashIcon.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
    }

    public void onDragEnded() {
        clearDraggedViewColor();
        if (managerPager != null) {
            managerPager.animate()
                    .setDuration(250)
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .start();
        }
        if (trashIcon != null) {
            trashIcon.animate().setDuration(650).y(trashBottomY - 100).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    trashIcon.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
        if (addContainerButton != null && getNumSubContainers() < maxNumSubcontainers) {
            addContainerButton.animate().setDuration(650).y(addButtonTopY).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    addContainerButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
    }

    public interface IsOverTrash {
        public void isOverTrash();
    }

    IsOverTrash overTrashListener = null;

    public void setIsOverTrashListener(ContainerManagementViewPagerFragment.ContainerDragShadowBuilder s) {
        overTrashListener = (IsOverTrash) s;
    }
}
