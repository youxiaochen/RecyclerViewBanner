package chen.you.banner.transformer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * author: you : 2022/12/16
 */
public final class AlphaTransformer implements ViewPager2.PageTransformer {

    private float mMinAlpha = 0.5f;

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
