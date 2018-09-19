package org.rares.ratv.rotationaware.animation;

import android.animation.ValueAnimator;
import android.view.ViewGroup;

import org.rares.ratv.rotationaware.RotationAwareTextView;

import java.lang.ref.WeakReference;

/**
 * Default update listener. It holds a {@link WeakReference} to the {@link RotationAwareTextView} that this updates.
 */
public class RotationAwareUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    private WeakReference<RotationAwareTextView> viewReference;

    public RotationAwareUpdateListener(RotationAwareTextView animatedView) {
        viewReference = new WeakReference<>(animatedView);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        RotationAwareTextView animatedView = viewReference.get();
        float rotation = (float) animation.getAnimatedValue(RotationAware.ROTATION);
        int width = (int) animation.getAnimatedValue(RotationAware.WIDTH);
        int height = (int) animation.getAnimatedValue(RotationAware.HEIGHT);
        int bgc = (int) animation.getAnimatedValue(RotationAware.BACKGROUND_COLOR);
        int tc = (int) animation.getAnimatedValue(RotationAware.TEXT_COLOR);
        int ts = (int) animation.getAnimatedValue(RotationAware.TEXT_SIZE);

        animatedView.setRotation(rotation);
        ViewGroup.LayoutParams layoutParams = animatedView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        animatedView.setTextColor(tc);
        animatedView.setBackgroundColor(bgc);
        animatedView.setTextSize(ts);
        animatedView.setLayoutParams(layoutParams);
    }

    public void clear() {
        viewReference.clear();
        viewReference = null;
    }
}