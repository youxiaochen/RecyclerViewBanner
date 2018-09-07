package you.chen.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 广告栏指示器
 *
 * @author you
 */
public class BannerIndicator extends LinearLayout {

    /**
     * banner当前位置
     */
    private int selectPosition = -1;

    private int indicatorDrawableRes = 0;

    private int indicatorMargin = 15;

    public BannerIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public BannerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BannerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BannerIndicator);
            indicatorMargin = a.getDimensionPixelOffset(R.styleable.BannerIndicator_indicatorMargin, indicatorMargin);
            indicatorDrawableRes = a.getResourceId(R.styleable.BannerIndicator_indicatorDrawableRes, indicatorDrawableRes);
            a.recycle();
        }
    }

    /**
     *
     * @param count
     */
    public void setIndicatorCount(int count) {
        removeAllViews();
        selectPosition = -1;
        if (count <= 0) return;
        for (int i = 0; i < count; i++) {
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(indicatorDrawableRes);
            if (i == 0) {
                iv.setSelected(true);
                this.selectPosition = i;
            }
            this.addView(iv, params(i));
        }
    }

    protected LayoutParams params(int position) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (position > 0) {
            params.leftMargin = indicatorMargin;
        }
        return params;
    }

    /**
     * 设置当前指示器位置
     */
    public void setCurrentIndicator(int position) {
        if (position == selectPosition || position < 0 || position >= getChildCount()) return;
        getChildAt(selectPosition).setSelected(false);
        getChildAt(position).setSelected(true);
        this.selectPosition = position;
    }

}
