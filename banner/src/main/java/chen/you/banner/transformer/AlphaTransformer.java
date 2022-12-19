package chen.you.banner.transformer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * author: you : 2020/06/13
 */
public final class AlphaTransformer implements ViewPager2.PageTransformer {

    private static final float DEFAULT_MIN_ALPHA = 0.5f;

    private final float mMinAlpha;

    public AlphaTransformer() {
        this(DEFAULT_MIN_ALPHA);
    }

    public AlphaTransformer(float minAlpha) {
        this.mMinAlpha = minAlpha;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        page.setScaleX(0.999f);//hack
        if (position < -1) { // [-Infinity,-1)
            page.setAlpha(mMinAlpha);
        } else if (position <= 1) { // [-1,1]
            //[0，-1]
            float factor;
            if (position < 0) {
                //[1,min]
                factor = mMinAlpha + (1 - mMinAlpha) * (1 + position);
            } else {//[1，0]
                //[min,1]
                factor = mMinAlpha + (1 - mMinAlpha) * (1 - position);
            }
            page.setAlpha(factor);
        } else { // (1,+Infinity]
            page.setAlpha(mMinAlpha);
        }
    }
}
