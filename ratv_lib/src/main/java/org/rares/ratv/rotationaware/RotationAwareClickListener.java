package org.rares.ratv.rotationaware;

import android.animation.ValueAnimator;
import android.view.View;

import org.rares.ratv.rotationaware.animation.DefaultRotationAnimatorHost;
import org.rares.ratv.rotationaware.animation.RotationAnimatorHost;
import org.rares.ratv.rotationaware.animation.RotationAwareUpdateListener;

/**
 * Default click listener that starts the animation on click.
 * This should only be used for demonstration purposes, as it
 * references the rotationAwareTextView.
 */
public class RotationAwareClickListener implements View.OnClickListener {

    private boolean reverse = false;
    private RotationAnimatorHost animatorHost = null;

    @Override
    public void onClick(View v) {
        if (v instanceof RotationAwareTextView) {
            RotationAwareTextView view = (RotationAwareTextView) v;

            if (animatorHost == null) {
                animatorHost = new DefaultRotationAnimatorHost(view.gatherAnimationData());
            }
            ValueAnimator animator = animatorHost
                    .configureAnimator(reverse)
                    .setDuration(reverse ? 250 : 200);
            animator.addUpdateListener(new RotationAwareUpdateListener(view));
            animator.start();

            reverse = !reverse;
        }
    }
}