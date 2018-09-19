package org.rares.ratv.rotationaware.animation;

import android.animation.ArgbEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;

/**
 * Default implementation of {@link RotationAnimatorHost}.
 */
public class DefaultRotationAnimatorHost extends RotationAnimatorHost {

    public DefaultRotationAnimatorHost(@NonNull AnimationDTO animationData) {
        this.animationData = animationData;
    }

    @Override
    public ValueAnimator configureAnimator(boolean reverse) {
        if (animationData == null) {
            throw new IllegalStateException("Animation data is required.");
        }

        int fromRotation = reverse ? animationData.maxRotation : animationData.minRotation;
        int toRotation = reverse ? animationData.minRotation : animationData.maxRotation;

        int fromWidth = reverse ? animationData.maxWidth : animationData.minWidth;
        int toWidth = reverse ? animationData.minWidth : animationData.maxWidth;

        int fromHeight = reverse ? animationData.maxHeight : animationData.minHeight;
        int toHeight = reverse ? animationData.minHeight : animationData.maxHeight;

        int fromTextColor = reverse ? animationData.maxTextColor : animationData.minTextColor;
        int toTextColor = reverse ? animationData.minTextColor : animationData.maxTextColor;

        int fromBackgroundColor = reverse ? animationData.maxBackgroundColor : animationData.minBackgroundColor;
        int toBackgroundColor = reverse ? animationData.minBackgroundColor : animationData.maxBackgroundColor;

        int fromTextSize = reverse ? animationData.maxTextSize : animationData.minTextSize;
        int toTextSize = reverse ? animationData.minTextSize : animationData.maxTextSize;

        PropertyValuesHolder
                pvhTextColor = PropertyValuesHolder.ofObject(RotationAware.TEXT_COLOR, new ArgbEvaluator(), fromTextColor, toTextColor),
                pvhBackgroundColor = PropertyValuesHolder.ofObject(RotationAware.BACKGROUND_COLOR, new ArgbEvaluator(), fromBackgroundColor, toBackgroundColor),
                pvhRotation = PropertyValuesHolder.ofFloat(RotationAware.ROTATION, fromRotation, toRotation),
                pvhTextSize = PropertyValuesHolder.ofInt(RotationAware.TEXT_SIZE, fromTextSize, toTextSize),
                pvhWidth = PropertyValuesHolder.ofInt(RotationAware.WIDTH, fromWidth, toWidth),
                pvhHeight = PropertyValuesHolder.ofInt(RotationAware.HEIGHT, fromHeight, toHeight);

        animator = ValueAnimator
                .ofPropertyValuesHolder(
                        pvhTextColor,
                        pvhBackgroundColor,
                        pvhRotation,
                        pvhTextSize,
                        pvhWidth,
                        pvhHeight);

        animator.addUpdateListener(animationData.updateListener);
        return animator;
    }

    @Override
    public void clear() {
        if (animationData != null && animationData.updateListener != null) {
            animationData.updateListener = null;
            this.animationData = null;
        }
        this.animator = null;
    }

}