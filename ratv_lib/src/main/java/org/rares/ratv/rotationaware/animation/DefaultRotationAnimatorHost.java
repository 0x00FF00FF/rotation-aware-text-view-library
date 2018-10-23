package org.rares.ratv.rotationaware.animation;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;

import java.util.ArrayList;

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

        int fromMarginLeft = reverse ? animationData.maxMarginLeft : animationData.minMarginLeft;
        int toMarginLeft = reverse ? animationData.minMarginLeft : animationData.maxMarginLeft;

        int fromMarginTop = reverse ? animationData.maxMarginTop : animationData.minMarginTop;
        int toMarginTop = reverse ? animationData.minMarginTop : animationData.maxMarginTop;

        int fromMarginRight = reverse ? animationData.maxMarginRight : animationData.minMarginRight;
        int toMarginRight = reverse ? animationData.minMarginRight : animationData.maxMarginRight;

        int fromMarginBottom = reverse ? animationData.maxMarginBottom : animationData.minMarginBottom;
        int toMarginBottom = reverse ? animationData.minMarginBottom : animationData.maxMarginBottom;

        int fromShadowColor = reverse ? animationData.maxShadowColor : animationData.minShadowColor;
        int toShadowColor = reverse ? animationData.minShadowColor : animationData.maxShadowColor;

        int fromShadowRadius = reverse ? animationData.maxShadowRadius : animationData.minShadowRadius;
        int toShadowRadius = reverse ? animationData.minShadowRadius : animationData.maxShadowRadius;

        PropertyValuesHolder
                pvhTextColor = PropertyValuesHolder.ofObject(RotationAware.TEXT_COLOR, new ArgbEvaluator(), fromTextColor, toTextColor),
                pvhShadowColor = PropertyValuesHolder.ofObject(RotationAware.SHADOW_COLOR, new ArgbEvaluator(), fromShadowColor, toShadowColor),
                pvhBackgroundColor = PropertyValuesHolder.ofObject(RotationAware.BACKGROUND_COLOR, new ArgbEvaluator(), fromBackgroundColor, toBackgroundColor),
                pvhRotation = PropertyValuesHolder.ofFloat(RotationAware.ROTATION, fromRotation, toRotation),
                pvhShadowRadius = PropertyValuesHolder.ofInt(RotationAware.SHADOW_RADIUS, fromShadowRadius, toShadowRadius),
                pvhTextSize = PropertyValuesHolder.ofInt(RotationAware.TEXT_SIZE, fromTextSize, toTextSize),
                pvhMarginLeft = PropertyValuesHolder.ofInt(RotationAware.MARGIN_LEFT, fromMarginLeft, toMarginLeft),
                pvhMarginTop = PropertyValuesHolder.ofInt(RotationAware.MARGIN_TOP, fromMarginTop, toMarginTop),
                pvhMarginRight = PropertyValuesHolder.ofInt(RotationAware.MARGIN_RIGHT, fromMarginRight, toMarginRight),
                pvhMarginBottom = PropertyValuesHolder.ofInt(RotationAware.MARGIN_BOTTOM, fromMarginBottom, toMarginBottom),
                pvhWidth = PropertyValuesHolder.ofInt(RotationAware.WIDTH, fromWidth, toWidth),
                pvhHeight = PropertyValuesHolder.ofInt(RotationAware.HEIGHT, fromHeight, toHeight);

        clearListeners();

        animator = ValueAnimator
                .ofPropertyValuesHolder(
                        pvhTextColor,
                        pvhShadowColor,
                        pvhBackgroundColor,
                        pvhRotation,
                        pvhShadowRadius,
                        pvhTextSize,
                        pvhMarginLeft,
                        pvhMarginTop,
                        pvhMarginRight,
                        pvhMarginBottom,
                        pvhWidth,
                        pvhHeight);

        return animator;
    }

    @Override
    public void clearListeners() {
        if (animator == null) {
            return;
        }
        animator.end();
        ArrayList<Animator.AnimatorListener> listenersList = animator.getListeners();
        if (listenersList != null && listenersList.size() > 0) {
            for (Animator.AnimatorListener listener : listenersList) {
                if (listener instanceof RotationAwareUpdateListener) {
                    ((RotationAwareUpdateListener) listener).clear();
                }
            }
        }
        animator.removeAllListeners();
    }

    @Override
    public void clear() {
        this.animationData = null;
        if (animator != null) {
            clearListeners();
            animator = null;
        }
    }

}