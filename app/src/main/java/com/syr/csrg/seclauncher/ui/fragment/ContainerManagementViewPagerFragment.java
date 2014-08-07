package com.syr.csrg.seclauncher.ui.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.content.ClipData;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import com.syr.csrg.seclauncher.R;
import com.syr.csrg.seclauncher.engine.ContainerManager;
import com.syr.csrg.seclauncher.packDefinitions.SecLaunchSubContainer;
import com.syr.csrg.seclauncher.ui.activity.ContainerManagementActivity;
import com.syr.csrg.seclauncher.ui.customLayout.CustomGridLayout;

import java.util.ArrayList;

/**
 * Created by Shane on 7/29/14.
 */
public class ContainerManagementViewPagerFragment extends Fragment {

    public static final String CONTAINER_MANAGER_POSITION = "position";

    private ContainerManager cm = null;

    int start = 0;

    public static final ContainerManagementViewPagerFragment newInstance(int position) {
        ContainerManagementViewPagerFragment fragment = new ContainerManagementViewPagerFragment();
        Bundle bdl = new Bundle(1);
        bdl.putInt(CONTAINER_MANAGER_POSITION, position);
        fragment.setArguments(bdl);
        return fragment;
    }

    private static class ViewHolder {
        int subContainerIndex;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.container_management_viewpager_layout2, container, false);
        int position = getArguments().getInt(CONTAINER_MANAGER_POSITION);

        CustomGridLayout gl = (CustomGridLayout) rootView.findViewById(R.id.gridContainer);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float containerWidth = dm.widthPixels;
        float pixItemWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, getResources().getDisplayMetrics());
        int columnCount = (int) Math.round(containerWidth / pixItemWidth);
        //gl.setColumnCount(columnCount);
        final int itemWidth = (int) containerWidth / columnCount;

//        CustomGridLayout gl = (CustomGridLayout) rootView.findViewById(R.id.gridContainer);
//        DisplayMetrics dm = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//        float containerWidth = dm.widthPixels;
//        float pixItemWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, getResources().getDisplayMetrics());
//        int columnCount = (int) Math.round(containerWidth / pixItemWidth);
//        gl.setColumnCount(columnCount);
//        final int itemWidth = (int) containerWidth / columnCount;

        float containerHeight = dm.heightPixels;
        int indicatorWidthDP = 90;
        float indicatorWidthPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorWidthDP, getResources().getDisplayMetrics());
        float viewPagerHeight = (containerHeight - indicatorWidthPX);
        float pixItemHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 107, getResources().getDisplayMetrics());
        int rowCount = (int) Math.round(viewPagerHeight / pixItemHeight);
        //gl.setRowCount(rowCount);
        final int itemHeight = (int) viewPagerHeight / rowCount;

        int itemsInContainer = rowCount * columnCount;

        ArrayList<SecLaunchSubContainer> subContainers = cm.getSubContainers();
        start = position * 7;
        for (int i = 0; i + start < subContainers.size() && i < 7; i++) {

            FrameLayout fragmentHolder = new FrameLayout(getActivity());
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = itemWidth;
            p.height = itemHeight;
            fragmentHolder.setLayoutParams(p);
            fragmentHolder.setLongClickable(true);
            fragmentHolder.setBackgroundResource(R.drawable.glass_container);
            int id = i + start + 1;
            fragmentHolder.setId(id);
            ViewHolder holder = new ViewHolder();
            holder.subContainerIndex = i + start;
            fragmentHolder.setTag(holder);
            fragmentHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewHolder viewHolder = (ViewHolder) view.getTag();
                    cm.onZoomIn(viewHolder.subContainerIndex);
                }
            });
            fragmentHolder.setOnLongClickListener(new ContainerOnLongClickListener());
            fragmentHolder.setOnDragListener(new ContainerOnDragListener());
            //gl.addView(fragmentHolder);
            gl.addView(fragmentHolder, i);

            Fragment fragment = cm.getSubContainerFragmentPage(i + start);

            if (fragment != null) {
                getChildFragmentManager().beginTransaction()
                        .replace(id, fragment)
                        .commit();
            }
        }
        return rootView;
    }

    private final class ContainerOnLongClickListener implements View.OnLongClickListener {

        public boolean onLongClick(View view) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new ContainerDragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            return true;
        }

    }

    private final class ContainerDragShadowBuilder extends View.DragShadowBuilder {

        ContainerDragShadowBuilder(View view) {
            super(view);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            super.onDrawShadow(canvas);
        }
    }

    private final class ContainerOnDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(final View targetView, DragEvent event) {


            final View sourceView = (View) event.getLocalState();

            if (targetView instanceof FrameLayout && sourceView instanceof FrameLayout) {
                cm.setDraggedView(sourceView);
                final ViewGroup owner = (ViewGroup) sourceView.getParent();

                //attempt
                final ViewGroup ownerTarget = (ViewGroup) targetView.getParent();

                if (owner instanceof CustomGridLayout && ownerTarget instanceof CustomGridLayout) {

                    final int indexFrom = owner.indexOfChild(sourceView);
                    final int indexTo = ownerTarget.indexOfChild(targetView);
                    int action = event.getAction();
                    switch (action) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            cm.onDragStarted();

                            break;
                        case DragEvent.ACTION_DRAG_ENTERED:

                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            break;
                        case DragEvent.ACTION_DROP:
                            if (targetView != sourceView) {

                                ownerTarget.removeView(targetView);
                                owner.addView(targetView, indexFrom);

                                owner.removeView(sourceView);
                                ownerTarget.addView(sourceView, indexTo);


                                if (sourceView instanceof FrameLayout) {
                                    FrameLayout frameLayout = (FrameLayout) sourceView;
                                    frameLayout.animate().setDuration(1000)
                                            .x(targetView.getLeft())
                                            .y(targetView.getTop())
                                            .rotationYBy(360)
                                            .rotationBy(-720).setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animator) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animator) {
                                            cm.onSwapSubContainers(cm.getDragStartPage() * 7 + indexFrom, cm.getMangerPage() * 7 + indexTo);
//                                            owner.removeView(sourceView);
//                                            ownerTarget.addView(sourceView, indexTo);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animator) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animator) {

                                        }
                                    });
                                }

//                                if (owner == ownerTarget) {
//                                ownerTarget.removeView(targetView);
//                                owner.addView(targetView, indexFrom);
//                                }
                                if (targetView instanceof FrameLayout) {
                                    FrameLayout frameLayout = (FrameLayout) targetView;
                                    frameLayout.animate().setDuration(1000)
                                            .x(sourceView.getLeft())
                                            .y(sourceView.getTop())
                                            .rotationXBy(360)
                                            .rotationBy(720).setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animator) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animator) {
//                                            ownerTarget.removeView(targetView);
//                                            owner.addView(targetView, indexFrom);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animator) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animator) {

                                        }
                                    });
                                }
                                Log.d("swap", Integer.toString(cm.getDragStartPage() * 7 + indexFrom) + "-" + Integer.toString(cm.getMangerPage() * 7 + indexTo));
                                Object tempTargetTag = targetView.getTag();
                                targetView.setTag(sourceView.getTag());
                                sourceView.setTag(tempTargetTag);
                            }
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            cm.onDragEnded();

                            break;
                        default:
                            break;
                    }
                }
            }

            return true;
        }
    }


    private final class OnDragAndInsertListener implements View.OnDragListener {
        @Override
        public boolean onDrag(final View targetView, DragEvent event) {

            final View sourceView = (View) event.getLocalState();
            cm.setDraggedView(sourceView);
            ViewGroup owner = (ViewGroup) sourceView.getParent();

            //attempt
            ViewGroup ownerTarget = (ViewGroup) targetView.getParent();

            final int indexFrom = owner.indexOfChild(sourceView);
            final int indexTo = ownerTarget.indexOfChild(targetView);
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    cm.onDragStarted();

                    break;
                case DragEvent.ACTION_DRAG_ENTERED:

                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    if (targetView != sourceView) {
                        final CustomGridLayout gls = (CustomGridLayout) sourceView.findViewById(R.id.gridContainer);

                        if (sourceView instanceof FrameLayout) {
                            FrameLayout frameLayout = (FrameLayout) sourceView;
                            frameLayout.animate().setDuration(1000)
                                    .x(targetView.getLeft())
                                    .y(targetView.getTop())
                                    .rotationYBy(360)
                                    .rotationBy(-720).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    ((FrameLayout) gls.getParent()).removeView(gls);
                                    ((FrameLayout) targetView).addView(gls);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });
                        }

                        final CustomGridLayout glt = (CustomGridLayout) targetView.findViewById(R.id.gridContainer);

                        if (targetView instanceof FrameLayout) {
                            FrameLayout frameLayout = (FrameLayout) targetView;
                            frameLayout.animate().setDuration(1000)
                                    .x(sourceView.getLeft())
                                    .y(sourceView.getTop())
                                    .rotationXBy(360)
                                    .rotationBy(720).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    ((FrameLayout) glt.getParent()).removeView(glt);
                                    ((FrameLayout) sourceView).addView(glt);
                                    cm.onSwapSubContainers(cm.getDragStartPage() * 7 + indexFrom, cm.getMangerPage() * 7 + indexTo);

                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });
                        }
                        Log.d("swap", Integer.toString(cm.getDragStartPage() * 7 + indexFrom) + "-" + Integer.toString(cm.getMangerPage() * 7 + indexTo));
                        Object tempTargetTag = targetView.getTag();
                        targetView.setTag(sourceView.getTag());
                        sourceView.setTag(tempTargetTag);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    cm.onDragEnded();

                    break;
                default:
                    break;
            }


            return true;

        }


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
        //cm = null;
    }

}
