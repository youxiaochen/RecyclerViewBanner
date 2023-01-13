package chen.you.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.BannerViewPager2Compat;
import androidx.viewpager2.widget.ViewPager2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * author: you : 2020/06/13
 */
public final class BannerPager extends ViewGroup {
    /**
     * 垂直与水平布局两种状态
     */
    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 0;
    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
    }

    //默认轮播间隔
    private static final int DEF_INTERVAL = 5000;
    /**
     * 默认延时翻页的时间间隔超过不执行,在banner翻页动画时间较长,当离开窗体时需要停止滑动{@link #stopScroll()}并归正,
     * 会重新触发checkCurrentItem, 用此时间来防止重复执行
     */
    private static final int DEF_POST_INTERVAL = 300;
    //默认至少2个item才自动滑动
    static final int DEF_LOOP_COUNT = 2;
    //代理时在原有的数量左右加上此数量,来达到无限循环 totalItemCount = itemCount + this
    //为何不直接 +2呢？, 当快速滑动(滑动页还未停的时候又滑动翻页或者多个手指不停滑动时(没有触发onPageSelected来轿正)
    //边缘数量不够就会滑不动了, 因此边缘加的数量可大一点, 左边添加10, 右边添加20
    static final int PROXY_ADDITION = 30;
    //代理滑动的边缘, 当前位置小于此, 或 此 + 前位置 > totalItemCount时需要重新轿正
    static final int PROXY_EDGE = PROXY_ADDITION / 3;

    private ViewPager2 mViewPager2;
    private ProxyPagerAdapter mPagerAdapter;
    private Adapter<?> mAdapter;
    //回调监听
    private BannerViewObserver mBannerObserver;
    private ProxyPageChangeCallback pageChangeCallback;

    //布局方向
    private @Orientation int orientation = HORIZONTAL;
    //切换的间隔时间
    private int intervalTime = DEF_INTERVAL;
    //自动轮播动画时间, 为0时按ViewPager2原始滑动效果
    private int bannerDuration = 0;
    //自动切换
    private boolean autoRunning = true;

    //轮播开关
    private boolean runningSwitch = false;
    //是否附着到window
    private boolean hasAttachedToWindow;
    //如果有添加生命周期机制时,onStop时即停止, 如果没有添加不触发即相当不考虑此参数
    private boolean hasLifecycleStopped = false;
    //上一次postDelayed时间, 防止短时间内的多次延时提交
    private long lastPostDelayedTime;

    //自动轮播Runnable
    private final PageTurnRunnable autoRunnable = new PageTurnRunnable(this);
    //Callback
    private final List<OnBannerChangeListener> mBannerChangeListeners = new ArrayList<>();

    //用于自动生命周期管理start/stop
    private LifecycleOwner mLifecycleOwner;
    private OnStartListener mOnStartListener;

    //当前ViewGroup测量的矩阵与子控件要显示的矩阵
    private final Rect mContainerRect = new Rect();
    private final Rect mChildRect = new Rect();

    public BannerPager(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public BannerPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BannerPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BannerPager);
            orientation = a.getInt(R.styleable.BannerPager_bannerOrientation, orientation);
            intervalTime = a.getInt(R.styleable.BannerPager_bannerInterval, intervalTime);
            bannerDuration = a.getInt(R.styleable.BannerPager_bannerDuration, bannerDuration);
            autoRunning = a.getBoolean(R.styleable.BannerPager_bannerAuto, autoRunning);
            a.recycle();
        }
        if (intervalTime <= 0) intervalTime = DEF_INTERVAL;

        mViewPager2 = new ViewPager2(context);
        if (bannerDuration > 0) BannerViewPager2Compat.setPagerScrollDuration(mViewPager2, bannerDuration);
        mViewPager2.setOffscreenPageLimit(1);
        mViewPager2.setOrientation(orientation == HORIZONTAL ? ViewPager2.ORIENTATION_HORIZONTAL : ViewPager2.ORIENTATION_VERTICAL);
        BannerViewPager2Compat.getRecyclerView(mViewPager2).setItemViewCacheSize(1);
        BannerViewPager2Compat.getRecyclerView(mViewPager2).setHasFixedSize(true);

        mPagerAdapter = new ProxyPagerAdapter();
        mViewPager2.setAdapter(mPagerAdapter);

        pageChangeCallback = new ProxyPageChangeCallback();
        super.addView(mViewPager2, -1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        throw new UnsupportedOperationException("addView(View...) is not supported in BannerPager");
    }

    @Override
    public void removeView(View child) {
        throw new UnsupportedOperationException("removeView(View) is not supported in BannerPager");
    }

    @Override
    public void removeViewAt(int index) {
        throw new UnsupportedOperationException("removeViewAt(int) is not supported in BannerPager");
    }

    @Override
    public void removeAllViews() {
        throw new UnsupportedOperationException("removeAllViews() is not supported in BannerPager");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingLeftRight = getPaddingLeft() + getPaddingRight();
        int paddingTopBottom = getPaddingTop() + getPaddingBottom();
        int childState = mViewPager2.getMeasuredState();
        measureChild(mViewPager2, widthMeasureSpec, heightMeasureSpec);
        int width = mViewPager2.getMeasuredWidth() + paddingLeftRight;
        int height = mViewPager2.getMeasuredHeight() + paddingTopBottom;
        setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, childState),
                resolveSizeAndState(height, heightMeasureSpec, childState));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = mViewPager2.getMeasuredWidth();
        int height = mViewPager2.getMeasuredHeight();
        mContainerRect.set(getPaddingLeft(), getPaddingTop(), r - l - getPaddingRight(), b - t - getPaddingBottom());
        Gravity.apply(Gravity.TOP | Gravity.START, width, height, mContainerRect, mChildRect);
        mViewPager2.layout(mChildRect.left, mChildRect.top, mChildRect.right, mChildRect.bottom);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        hasAttachedToWindow = true;
        mViewPager2.registerOnPageChangeCallback(pageChangeCallback);
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hasAttachedToWindow = false;
        mViewPager2.unregisterOnPageChangeCallback(pageChangeCallback);
        stop();
    }

    /**
     * 翻到下一个item
     */
    private void turnPager() {
//        Log.d("BannerPager", "after turnPage... ");
        if (autoRunning && runningSwitch && hasAttachedToWindow && !hasLifecycleStopped && !pageChangeCallback.isPageTurning) {
            int scrollTurnPage = mPagerAdapter.getTurnIndex(mViewPager2.getCurrentItem());
            if (scrollTurnPage > 0) {
                mViewPager2.setCurrentItem(scrollTurnPage);
            }
        }
    }

    /**
     * 校正当前位置,并延时滑动下一次
     */
    private void checkCurrentItem() {
//        Log.d("BannerPager", "checkCurrentItem...");
        if (autoRunning) {
            int correctIndex = mPagerAdapter.getCorrectIndex(mViewPager2.getCurrentItem());
            if (correctIndex > 0) {
//                Log.d("BannerPager", "checkCurrent " + correctIndex);
                mViewPager2.setCurrentItem(correctIndex, false);
            }
            if (runningSwitch && hasAttachedToWindow && !hasLifecycleStopped && mPagerAdapter.needProxy()) {
                delayTurnPager();
            }
        }
    }

    private void delayTurnPager() {
//        Log.d("BannerPager", "delayTurnPager ...... ");
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPostDelayedTime >= DEF_POST_INTERVAL) {
            lastPostDelayedTime = currentTime;
            postDelayed(autoRunnable, intervalTime);
        }
    }

    /**
     * 滑动中停止, 如果正在滑动停止会触发{@link #checkCurrentItem()}
     * @return true时会自动校正并下次翻页
     */
    private boolean stopScroll() {
        if (mViewPager2.getScrollState() != ViewPager2.SCROLL_STATE_IDLE) {
//            Log.d("BannerPager","stopScroll currentPage " + mViewPager2.getCurrentItem());
            BannerViewPager2Compat.getRecyclerView(mViewPager2).scrollToPosition(mViewPager2.getCurrentItem());
            return true;
        }
        return false;
    }

    /**
     * 此方法只有在{@link #setLifecycleOwner(LifecycleOwner) 设置生命周期管理时才有影响}
     */
    private void onResume() {
        hasLifecycleStopped = false;
        start();
    }

    private void onStop() {
        hasLifecycleStopped = true;
        stop();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onDataSetChanged() {
        boolean stopScroll = stopScroll();
        mPagerAdapter.refreshCount();
        mPagerAdapter.notifyDataSetChanged();
        if (!stopScroll) start();
        for (OnBannerChangeListener listener : mBannerChangeListeners) {
            listener.onPageDataSetChanged(mAdapter == null ? 0 : mAdapter.getItemCount());
        }
        if (mPagerAdapter.needProxy()) {
            mViewPager2.setCurrentItem(PROXY_EDGE, false);
        }
    }

    public void start() {
//        Log.d("BannerPager", "start...");
        runningSwitch = true;
        if (autoRunning && hasAttachedToWindow && !hasLifecycleStopped &&
                !pageChangeCallback.isPageTurning && mPagerAdapter.needProxy()) {
            removeCallbacks(autoRunnable);
            delayTurnPager();
        }
    }

    public void stop() {
//        Log.d("BannerPager", "stop...");
        removeCallbacks(autoRunnable);
        stopScroll();
        runningSwitch = false;
    }

    public void setAdapter(Adapter<?> adapter) {
        if (mAdapter != null) {
            mAdapter.setBannerObserver(null);
        }
        mAdapter = adapter;
        if (adapter != null) {
            if (mBannerObserver == null) {
                mBannerObserver = new BannerViewObserver();
            }
            mAdapter.setBannerObserver(mBannerObserver);
        }
        mPagerAdapter.adapter = adapter;
        onDataSetChanged();
    }

    //添加生命周期管理
    public void setLifecycleOwner(@Nullable LifecycleOwner lifecycleOwner) {
        if (mLifecycleOwner == lifecycleOwner) {
            return;
        }
        if (mLifecycleOwner != null && mOnStartListener != null) {
            mLifecycleOwner.getLifecycle().removeObserver(mOnStartListener);
        }
        mLifecycleOwner = lifecycleOwner;
        if (lifecycleOwner != null) {
            if (mOnStartListener == null) {
                mOnStartListener = new OnStartListener(this);
            }
            lifecycleOwner.getLifecycle().addObserver(mOnStartListener);
        }
    }

    public void setPageTransformer(@Nullable ViewPager2.PageTransformer transformer) {
        mViewPager2.setPageTransformer(transformer);
    }

    public void addOnBannerChangeListener(OnBannerChangeListener listener) {
        this.mBannerChangeListeners.add(listener);
    }

    public void removeOnBannerChangeListener(OnBannerChangeListener listener) {
        this.mBannerChangeListeners.remove(listener);
    }

    public Adapter<?> getAdapter() {
        return mAdapter;
    }

    public void setCurrentItem(int item) {
        mViewPager2.setCurrentItem(PROXY_EDGE + item, false);
    }

    public int getCurrentItem() {
        return mViewPager2.getCurrentItem();
    }

    public int getCurrentBannerItem() {
        return mPagerAdapter.getProxyIndex(mViewPager2.getCurrentItem());
    }

    public void setOrientation(@Orientation int orientation) {
        if (this.orientation == orientation) return;
        this.orientation = orientation;
        mViewPager2.setOrientation(orientation == HORIZONTAL ? ViewPager2.ORIENTATION_HORIZONTAL : ViewPager2.ORIENTATION_VERTICAL);
    }

    public @Orientation int getOrientation() {
        return orientation;
    }

    public void setIntervalTime(int intervalTime) {
        if (intervalTime < DEF_POST_INTERVAL || this.intervalTime == intervalTime) return;
        this.intervalTime = intervalTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setBannerDuration(int bannerDuration) {
        if (this.bannerDuration == bannerDuration) return;
        this.bannerDuration = bannerDuration;
        if (bannerDuration > 0) {
            stopScroll();
            BannerViewPager2Compat.setPagerScrollDuration(mViewPager2, bannerDuration);
        }
    }

    public int getBannerDuration() {
        return bannerDuration;
    }

    public void setAutoRunning(boolean autoRunning) {
        if (this.autoRunning == autoRunning) return;
        this.autoRunning = autoRunning;
        if (autoRunning) {
            start();
        }
    }

    public boolean isAutoRunning() {
        return autoRunning;
    }

    /**
     * 代理回调类
     */
    private class ProxyPageChangeCallback extends ViewPager2.OnPageChangeCallback {
        //上一次select index
        int mLastSelectPosition = RecyclerView.NO_POSITION;
        //正在翻页
        boolean isPageTurning = false;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            for (OnBannerChangeListener listener : mBannerChangeListeners) {
                //有监听类时才计算代理位置
                listener.onPageScrolled(position, mPagerAdapter.getProxyIndex(position), positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            //adapter中的数据为空时还是会触发onPageSelected
            if (mPagerAdapter.isEmpty()) {
                mLastSelectPosition = -1;
            } else {
                int selectPosition = mPagerAdapter.getProxyIndex(position);
                if (mLastSelectPosition == selectPosition) return;
                mLastSelectPosition = selectPosition;
                for (OnBannerChangeListener listener : mBannerChangeListeners) {
                    listener.onPageSelected(mLastSelectPosition);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case ViewPager2.SCROLL_STATE_IDLE:
                    checkCurrentItem();
                    isPageTurning = false;
                    break;
                case ViewPager2.SCROLL_STATE_DRAGGING:
                    isPageTurning = false;
                    removeCallbacks(autoRunnable);
                    break;
                case ViewPager2.SCROLL_STATE_SETTLING:
                    isPageTurning = false;
                    break;

            }
            for (OnBannerChangeListener listener : mBannerChangeListeners) {
                listener.onPageScrollStateChanged(state);
            }
        }
    }

    /**
     * Adapter观察者
     */
    private class BannerViewObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            onDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            onDataSetChanged();
        }
    }

    /*---------------------------  Inner classes   ---------------------------*/

    /**
     * Adapter ViewHolder
     */
    public static abstract class ViewHolder {
        @NonNull
        public final View itemView;
        int mPosition = RecyclerView.NO_POSITION;
        int mItemViewType = RecyclerView.INVALID_TYPE;

        public ViewHolder(@NonNull View itemView) {
            this.itemView = itemView;
        }

        public final int getPosition() {
            return mPosition;
        }

        public final int getItemViewType() {
            return mItemViewType;
        }
    }

    /**
     * banner change listener
     */
    public static abstract class OnBannerChangeListener {

        //Page Adapter DataSetChanged..
        public void onPageDataSetChanged(int adapterCount) {}

        /**
         * 滑动偏移
         * @param position 实际页面位置
         * @param index 代理后的索引位置
         */
        public void onPageScrolled(int position, int index, float indexOffset, int indexOffsetPixels) {}

        public void onPageSelected(int index) {}

        public void onPageScrollStateChanged(int state) {}
    }

    /**
     * 适配器
     * @param <VH>
     */
    public static abstract class Adapter<VH extends ViewHolder> {

        private DataSetObserver bannerObserver;

        void setBannerObserver(DataSetObserver observer) {
            synchronized (this) {
                bannerObserver = observer;
            }
        }

        /**
         * 可以根据实际需求
         */
        public final void notifyDataSetChanged() {
            synchronized (this) {
                if (bannerObserver != null) {
                    bannerObserver.onChanged();
                }
            }
        }

        public abstract int getItemCount();

        public abstract void onBindViewHolder(@NonNull VH vh, int position);

        public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

        public int getItemViewType(int position) {
            return 0;
        }

        public void onViewRecycled(@NonNull VH holder) {
        }

        public void onViewAttachedToWindow(@NonNull VH holder) {
        }

        public void onViewDetachedFromWindow(@NonNull VH holder) {
        }
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {

        final ViewHolder viewHolder;

        public BannerViewHolder(@NonNull ViewHolder viewHolder) {
            super(viewHolder.itemView);
            this.viewHolder = viewHolder;
        }
    }

    /**
     * 翻页Runnable
     */
    static class PageTurnRunnable implements Runnable {

        final WeakReference<BannerPager> bannerPagerWR;

        PageTurnRunnable(BannerPager bannerPager) {
            this.bannerPagerWR = new WeakReference<>(bannerPager);
        }

        @Override
        public void run() {
            BannerPager bannerPager = bannerPagerWR.get();
            if (bannerPager != null) {
                bannerPager.turnPager();
            }
        }
    }

    /**
     * 生命周期管理滑动类
     */
    static class OnStartListener implements LifecycleEventObserver {

        final WeakReference<BannerPager> bannerPagerWR;

        OnStartListener(BannerPager bannerPager) {
            this.bannerPagerWR = new WeakReference<>(bannerPager);
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner owner, @NonNull Lifecycle.Event event) {
            BannerPager bannerPager = bannerPagerWR.get();
            switch (event) {
                case ON_RESUME:
                    if (bannerPager != null) bannerPager.onResume();
                    break;
                case ON_PAUSE:
                    if (bannerPager != null) bannerPager.onStop();
                    break;
                case ON_DESTROY:
                    owner.getLifecycle().removeObserver(this);
                    if (bannerPager != null) {
                        bannerPager.mLifecycleOwner = null;
                        bannerPager.setupWithIndicator(null);
                        bannerPager.mBannerChangeListeners.clear();
                    }
                    break;
            }
        }
    }

    /**
     * 核心代理适配器类
     */
    static class ProxyPagerAdapter extends RecyclerView.Adapter<BannerViewHolder> {
        //BannerPager Adapter
        Adapter adapter;
        //代理计算的索引分割位置
        int subCount = 0;
        //代理的总数量
        Integer mItemCounts = null;

        ProxyPagerAdapter() {
            setHasStableIds(true);
        }

        void refreshCount() {
            mItemCounts = null;
            subCount = 0;
        }

        @Override
        public int getItemCount() {
            if (mItemCounts == null) {
                if (adapter == null) {
                    mItemCounts = 0;
                } else {
                    int itemCount = adapter.getItemCount();
                    if (itemCount >= DEF_LOOP_COUNT) {
                        subCount = PROXY_EDGE % itemCount;
                        itemCount += PROXY_ADDITION;
                    }
                    mItemCounts = itemCount;
                }
            }
            return mItemCounts;
        }

        @Override
        public int getItemViewType(int position) {
            return adapter.getItemViewType(getProxyIndex(position));
        }

        @NonNull
        @Override
        public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewHolder vh = adapter.onCreateViewHolder(parent, viewType);
            vh.mItemViewType = viewType;
//            Log.d("BannerPager", "onCreateProxyViewHolder " + viewType);
            return new BannerViewHolder(vh);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
            int proxyPosition = getProxyIndex(position);
            holder.viewHolder.mPosition = proxyPosition;
//            Log.d("BannerPager", "onBindViewHolder " + proxyPosition + "  " + position);
            ViewHolder vh = holder.viewHolder;
            adapter.onBindViewHolder(holder.viewHolder, proxyPosition);
        }

        @Override
        public void onViewRecycled(@NonNull BannerViewHolder holder) {
            if (adapter != null) adapter.onViewRecycled(holder.viewHolder);
        }

        @Override
        public void onViewAttachedToWindow(@NonNull BannerViewHolder holder) {
            if (adapter != null) adapter.onViewAttachedToWindow(holder.viewHolder);
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull BannerViewHolder holder) {
            if (adapter != null) adapter.onViewDetachedFromWindow(holder.viewHolder);
        }

        @Override
        public long getItemId(int position) {
            return getProxyIndex(position);
        }

        boolean needProxy() {
            return adapter != null && adapter.getItemCount() >= DEF_LOOP_COUNT;
        }

        int getProxyIndex(int position) {
            if (needProxy()) return countProxyIndex(position);
            return position;
        }

        int getCorrectIndex(int position) {
            if (needProxy() && (position < PROXY_EDGE || position >= getItemCount() - PROXY_EDGE)) {
                return countProxyIndex(position) + PROXY_EDGE;
            }
            return -1;
        }

        int getTurnIndex(int position) {
            if (needProxy()) return position + 1;
            return -1;
        }

        //计算代理的索引
        private int countProxyIndex(int position) {
            int adapterCount = adapter.getItemCount();
            if (position < subCount) {
                return adapterCount - subCount + position;
            } else {
                return (position - subCount) % adapterCount;
            }
        }

        boolean isEmpty() {
            return adapter == null || adapter.getItemCount() <= 0;
        }
    }

    //指示监听
    private BannerIndicatorChangeListener indicatorChangeListener;

    /**
     * 绑定指示器
     */
    public void setupWithIndicator(IndicatorView indicatorView) {
        if (indicatorChangeListener != null) {
            this.mBannerChangeListeners.remove(indicatorChangeListener);
        }
        if (indicatorView != null) {
            indicatorView.setIndicatorCount(mAdapter == null ? 0 : mAdapter.getItemCount());
            indicatorView.setOrientation(orientation);
            indicatorView.setSelectIndex(getCurrentBannerItem());
            indicatorChangeListener = new BannerIndicatorChangeListener(indicatorView);
            this.mBannerChangeListeners.add(indicatorChangeListener);
        }
    }

    /**
     * 指示器监听类
     */
    static class BannerIndicatorChangeListener extends OnBannerChangeListener {

        final IndicatorView indicatorView;

        BannerIndicatorChangeListener(IndicatorView indicatorView) {
            this.indicatorView = indicatorView;
        }

        @Override
        public void onPageDataSetChanged(int adapterCount) {
            indicatorView.setIndicatorCount(adapterCount);
        }

        @Override
        public void onPageScrolled(int position, int index, float indexOffset, int indexOffsetPixels) {
            indicatorView.setSelectIndexOffset(index, indexOffset);
        }
    }
}
