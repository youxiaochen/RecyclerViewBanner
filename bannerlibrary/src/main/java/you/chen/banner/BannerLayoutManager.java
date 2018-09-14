package you.chen.banner;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

/**
 * Created by you on 2017/2/27.
 */

public class BannerLayoutManager extends LinearLayoutManager {
    /**
     * 值越大,滑动速度越慢, 源码默认速度是25F
     */
    private static final float MILLISECONDS_PER_INCH = 75f;

    private float scrollMill = MILLISECONDS_PER_INCH;

    public BannerLayoutManager(Context context) {
        this(context, HORIZONTAL, false);
        setItemPrefetchEnabled(false);
    }

    public BannerLayoutManager(Context context, int orientation, float scrollMill) {
        this(context, orientation, false, scrollMill);
        setItemPrefetchEnabled(false);
    }

    public BannerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setItemPrefetchEnabled(false);
    }

    public BannerLayoutManager(Context context, int orientation, boolean reverseLayout, float scrollMill) {
        super(context, orientation, reverseLayout);
        if (scrollMill > 10f) {
            this.scrollMill = scrollMill;
        }
        setItemPrefetchEnabled(false);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return scrollMill / displayMetrics.densityDpi;
            }
        };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

}
