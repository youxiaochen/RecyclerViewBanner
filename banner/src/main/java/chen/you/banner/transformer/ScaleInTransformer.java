package chen.you.banner.transformer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * author: you : 2020/06/13
 */
public final class ScaleInTransformer implements ViewPager2.PageTransformer {
    public static final float DEFAULT_CENTER = 0.5f;
    private static final float DEFAULT_MIN_SCALE = 0.85f;

    private final float mMinScale;

    public ScaleInTransformer() {
        this(DEFAULT_MIN_SCALE);
    }

    public ScaleInTransformer(float minScale) {
        this.mMinScale = minScale;
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        view.setPivotY(pageHeight / 2.f);
        view.setPivotX(pageWidth / 2.f);
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
            view.setPivotX(pageWidth);
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            if (position < 0) { //1-2:1[0,-1] ;2-1:1[-1,0]
                float scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setPivotX(pageWidth * (DEFAULT_CENTER + (DEFAULT_CENTER * -position)));
            } else  {//1-2:2[1,0] ;2-1:2[0,1]
                float scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setPivotX(pageWidth * ((1 - position) * DEFAULT_CENTER));
            }
        } else { // (1,+Infinity]
            view.setPivotX(0);
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
        }
    }
}
