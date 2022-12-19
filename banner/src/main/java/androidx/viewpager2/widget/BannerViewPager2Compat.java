package androidx.viewpager2.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;

/**
 *  Created by You on 2019/3/18.
 *  ViewPager2 Compat
 */
public final class BannerViewPager2Compat {

    private BannerViewPager2Compat() {}

    public static RecyclerView getRecyclerView(ViewPager2 viewPager2) {
        return viewPager2.mRecyclerView;
    }

    public static void setPagerScrollDuration(ViewPager2 viewPager2, int fixedDuration) {
        if (fixedDuration <= 0) return;
        RecyclerView.LayoutManager lm = viewPager2.mRecyclerView.getLayoutManager();
        if (lm instanceof LinearLayoutManager) {
            LinearLayoutManager llm = (LinearLayoutManager) lm;
            ProxyLinearLayoutManager proxyLlm = new ProxyLinearLayoutManager(viewPager2, llm, fixedDuration);
            viewPager2.mRecyclerView.setLayoutManager(proxyLlm);
            proxyViewPager2LayoutManager(viewPager2, proxyLlm);
        }
    }

    private static void proxyViewPager2LayoutManager(ViewPager2 viewPager2, ProxyLinearLayoutManager proxyLlm) {
        try {
            Field lmField = ViewPager2.class.getDeclaredField("mLayoutManager");
            lmField.setAccessible(true);
            lmField.set(viewPager2, proxyLlm);

            Field seaField = ViewPager2.class.getDeclaredField("mScrollEventAdapter");
            seaField.setAccessible(true);
            Object seaObj = seaField.get(viewPager2);
            Field seaLmField = ScrollEventAdapter.class.getDeclaredField("mLayoutManager");
            seaLmField.setAccessible(true);
            seaLmField.set(seaObj, proxyLlm);

            Field ptaField = ViewPager2.class.getDeclaredField("mPageTransformerAdapter");
            ptaField.setAccessible(true);
            Object ptaObj = ptaField.get(viewPager2);
            Field ptaLmField = PageTransformerAdapter.class.getDeclaredField("mLayoutManager");
            ptaLmField.setAccessible(true);
            ptaLmField.set(ptaObj, proxyLlm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 固定时间的平滑器, 不会通过像素值计算滑动时间
     */
    static class FixedLinearSmoothScroller extends LinearSmoothScroller {
        //固定的时间
        final int fixedDuration;

        public FixedLinearSmoothScroller(Context context, int fixedDuration) {
            super(context);
            this.fixedDuration = fixedDuration;
        }

        @Override
        protected int calculateTimeForDeceleration(int dx) {
            return fixedDuration;
        }
    }

    /**
     * 代理ViewPager2 LinearLayoutManagerImpl类
     */
    static class ProxyLinearLayoutManager extends LinearLayoutManager {

        private final int fixedDuration;

        private final ViewPager2 viewPager2;

        private final LinearLayoutManager llm;

        public ProxyLinearLayoutManager(ViewPager2 viewPager2, LinearLayoutManager llm, int fixedDuration) {
            super(viewPager2.getContext(), llm.getOrientation(), llm.getReverseLayout());
            this.viewPager2 = viewPager2;
            this.llm = llm;
            this.fixedDuration = fixedDuration;
        }

        @Override
        public boolean performAccessibilityAction(@NonNull RecyclerView.Recycler recycler,
                                                  @NonNull RecyclerView.State state, int action, @Nullable Bundle args) {
            return llm.performAccessibilityAction(recycler, state, action, args);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(@NonNull RecyclerView.Recycler recycler,
                                                      @NonNull RecyclerView.State state, @NonNull AccessibilityNodeInfoCompat info) {
            llm.onInitializeAccessibilityNodeInfo(recycler, state, info);
        }

        @Override
        protected void calculateExtraLayoutSpace(@NonNull RecyclerView.State state, @NonNull int[] extraLayoutSpace) {
            int pageLimit = viewPager2.getOffscreenPageLimit();
            if (pageLimit == ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT) {
                // Only do custom prefetching of offscreen pages if requested
                super.calculateExtraLayoutSpace(state, extraLayoutSpace);
                return;
            }
            final int offscreenSpace = viewPager2.getPageSize() * pageLimit;
            extraLayoutSpace[0] = offscreenSpace;
            extraLayoutSpace[1] = offscreenSpace;
        }

        @Override
        public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child,
                                                     @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
            return llm.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView rv, RecyclerView.State state, int position) {
            LinearSmoothScroller linearSmoothScroller = new FixedLinearSmoothScroller(rv.getContext(), fixedDuration);
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }
}
