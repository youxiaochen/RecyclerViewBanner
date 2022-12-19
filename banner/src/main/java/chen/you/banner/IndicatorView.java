package chen.you.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * author: you : 2020/06/16
 * Banner指示器也可用于其他功能的指示器, 如引导页面的ViewPager, ViewPager2
 */
public final class IndicatorView extends View {
    //默认宽度, drawable未指定高度时的
    private static final int DEF_SIZE = 30;
    private static final int DEF_SELECTED_SIZE = 40;
    //水平与垂直布局
    @BannerPager.Orientation
    private int orientation = BannerPager.HORIZONTAL;
    //默认间隙
    private int indicatorMargin = 50;
    //选中与未选中时的图标
    private Drawable drawable, selectDrawable;
    //标记的数量
    private int indicatorCount;
    //当前位置
    private int selectIndex;
    //当前位置偏移
    private float indexOffset;
    //图标宽高与select时的宽高
    private int drawableWidth, drawableHeight;
    private int selectDrawableWidth, selectDrawableHeight;
    //单元的宽度与长度
    private int unitWidth, unitHeight;
    //图标画中心时的偏移
    private int centerXOff, centerYOff;
    private int centerSelectXOff, centerSelectYOff;

    public IndicatorView(Context context) {
        super(context);
        init(context, null);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        Drawable drawable = null;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndicatorView);
            orientation = a.getInt(R.styleable.IndicatorView_indicatorOrientation, orientation);
            indicatorMargin = a.getDimensionPixelOffset(R.styleable.IndicatorView_indicatorMargin, indicatorMargin);
            drawable = a.getDrawable(R.styleable.IndicatorView_indicatorDrawable);
            a.recycle();
        }
        initDrawable(drawable);
    }

    private void initDrawable(Drawable drawable) {
        if (drawable instanceof StateListDrawable) {
            StateListDrawable listDrawable = (StateListDrawable) drawable;
            this.drawable = listDrawable.getCurrent();
            listDrawable.setState(new int[]{android.R.attr.state_selected});
            selectDrawable = listDrawable.getCurrent();
        } else {
            selectDrawable = null;
            this.drawable = null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (drawable == null || selectDrawable == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        selectDrawableWidth = selectDrawable.getIntrinsicWidth();
        if (selectDrawableWidth <= 0) selectDrawableWidth = DEF_SELECTED_SIZE;
        selectDrawableHeight = selectDrawable.getIntrinsicHeight();
        if (selectDrawableHeight <= 0) selectDrawableHeight = DEF_SELECTED_SIZE;
        drawableWidth = drawable.getIntrinsicWidth();
        if (drawableWidth <= 0) drawableWidth = DEF_SIZE;
        drawableHeight = drawable.getIntrinsicHeight();
        if (drawableHeight <= 0) drawableHeight = DEF_SIZE;

        unitWidth = Math.max(selectDrawableWidth, drawableWidth);
        unitHeight = Math.max(selectDrawableHeight, drawableHeight);
        int paddingLeftRight = getPaddingLeft() + getPaddingRight();
        int paddingTopBottom = getPaddingTop() + getPaddingBottom();
        if (indicatorCount <= 0) {
            setMeasuredDimension(unitWidth + paddingLeftRight, unitHeight + paddingTopBottom);
            return;
        }

        centerXOff = (unitWidth - drawableWidth) >> 1;
        centerYOff = (unitHeight - drawableHeight) >> 1;
        centerSelectXOff = (unitWidth - selectDrawableWidth) >> 1;
        centerSelectYOff = (unitHeight - selectDrawableHeight) >> 1;

        int width, height;
        if (orientation == BannerPager.HORIZONTAL) {
            width = paddingLeftRight + unitWidth * indicatorCount + (indicatorCount - 1) * indicatorMargin;
            height = paddingTopBottom + unitHeight;
        } else {
            width = paddingLeftRight + unitWidth;
            height = paddingTopBottom + unitHeight * indicatorCount + (indicatorCount - 1) * indicatorMargin;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (indicatorCount <= 0 || drawable == null || selectDrawable == null) return;
        int left = getPaddingLeft() + centerXOff;
        int top = getPaddingTop() + centerYOff;
        int selectLeft = getPaddingLeft() + centerSelectXOff;
        int selectTop = getPaddingTop() + centerSelectYOff;
        int unitOffset;
        if (orientation == BannerPager.HORIZONTAL) {//画水平时的
            unitOffset = unitWidth + indicatorMargin;
            for (int i = 0; i < indicatorCount; i++) {
                if (i != selectIndex || indexOffset != 0) { //非select项或者有偏移时才画
                    drawable.setBounds(left, top, left + drawableWidth, top + drawableHeight);
                    drawable.draw(canvas);
                }
                left += unitOffset;
            }
            selectLeft += selectIndex * unitOffset  + (int) (indexOffset * unitOffset);
        } else {
            unitOffset = unitHeight + indicatorMargin;
            for (int i = 0; i < indicatorCount; i++) {
                if (i != selectIndex || indexOffset != 0) { //非select项或者有偏移时才画
                    drawable.setBounds(left, top, left + drawableWidth, top + drawableHeight);
                    drawable.draw(canvas);
                }
                top += unitOffset;
            }
            selectTop += selectIndex * unitOffset  + (int) (indexOffset * unitOffset);
        }
        selectDrawable.setBounds(selectLeft, selectTop, selectLeft + selectDrawableWidth, selectTop + selectDrawableHeight);
        selectDrawable.draw(canvas);
    }

    public void setOrientation(@BannerPager.Orientation int orientation) {
        if (this.orientation == orientation) return;
        this.orientation = orientation;
        requestLayout();
        postInvalidate();
    }

    public void setIndicatorCount(int indicatorCount) {
        if (indicatorCount < 0 || this.indicatorCount == indicatorCount) return;
        this.indexOffset = 0;
        this.indicatorCount = indicatorCount;
        requestLayout();
        invalidate();
    }

    public void setSelectIndex(int selectIndex) {
        setSelectIndexOffset(selectIndex, 0);
    }

    public void setSelectIndexOffset(int index, float offset) {
        if (index < 0 || index >= indicatorCount) return;
        if (index == indicatorCount - 1 && offset > 0) {
            offset = 0;
        }
        if (this.selectIndex == index && this.indexOffset == offset) return;
        this.selectIndex = index;
        this.indexOffset = offset;
        postInvalidate();
    }

    public void setImageDrawable(Drawable drawable) {
        initDrawable(drawable);
        requestLayout();
        postInvalidate();
    }

    public void setImageResource(@DrawableRes int resId) {
        setImageDrawable(getContext().getResources().getDrawable(resId));
    }
}
