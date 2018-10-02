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
        int ml = (int) animation.getAnimatedValue(RotationAware.MARGIN_LEFT);
        int mt = (int) animation.getAnimatedValue(RotationAware.MARGIN_TOP);
        int mr = (int) animation.getAnimatedValue(RotationAware.MARGIN_RIGHT);
        int mb = (int) animation.getAnimatedValue(RotationAware.MARGIN_BOTTOM);

        animatedView.setRotation(rotation);
        ViewGroup.MarginLayoutParams mlp = null;
        ViewGroup.LayoutParams layoutParams = animatedView.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            mlp = (ViewGroup.MarginLayoutParams) layoutParams;
            mlp.leftMargin = ml;
            mlp.topMargin = mt;
            mlp.rightMargin = mr;
            mlp.bottomMargin = mb;

            mlp.width = width;
            mlp.height = height;
        }

        layoutParams.width = width;
        layoutParams.height = height;

        animatedView.setTextColor(tc);
        animatedView.setBackgroundColor(bgc);
        animatedView.setTextSize(ts);

        if (mlp != null) {
            animatedView.setLayoutParams(mlp);
        } else {
            animatedView.setLayoutParams(layoutParams);
        }
    }

    public void clear() {
        viewReference.clear();
        viewReference = null;
    }
}