package chen.you.banner.transformer;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

/**
 * author: you : 2020/06/13
 */
public final class ZoomOutTransformer implements ViewPager2.PageTransformer {

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    private final float minScale;

    private final float minAlpha;

    public ZoomOutTransformer() {
        this(MIN_SCALE, MIN_ALPHA);
    }

    public ZoomOutTransformer(float minScale, float minAlpha) {
        this.minScale = minScale;
        this.minAlpha = minAlpha;
    }

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0f);
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(minScale, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }
            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            // Fade the page relative to its size.
            view.setAlpha(minAlpha + (scaleFactor - minScale) / (1 - minScale) * (1 - minAlpha));
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0f);
        }
    }
}
