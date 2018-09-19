package org.rares.ratv.rotationaware.animation;

import android.animation.ValueAnimator;

/**
 * Data transfer object for sending animation values around.
 */
public class AnimationDTO {
    public int minRotation;
    public int maxRotation;
    public int minWidth;
    public int maxWidth;
    public int minHeight;
    public int maxHeight;
    public int minTextSize;
    public int maxTextSize;
    public int minTextColor;
    public int maxTextColor;
    public int minBackgroundColor;
    public int maxBackgroundColor;

    public ValueAnimator.AnimatorUpdateListener updateListener;
}