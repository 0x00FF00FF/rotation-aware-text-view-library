package org.rares.ratv.rotationaware.animation;

import android.animation.ValueAnimator;

/**
 * Class that encapsulates animation data.
 */
public abstract class RotationAnimatorHost {
    protected ValueAnimator animator;

    AnimationDTO animationData;

    /**
     * Sets new animation data.
     * @param data animation data
     */
    public void updateAnimationData(AnimationDTO data){
        animationData = data;
    }

    /**
     * Configure the member animator.
     * @param reverse true means the animation is running backwards.
     * @return the animator in charge.
     */
    public abstract ValueAnimator configureAnimator(boolean reverse);

    /**
     * Implement this method if you have any references
     * that need to be cleared when the animator is no longer used.
     */
    public abstract void clear();
}
