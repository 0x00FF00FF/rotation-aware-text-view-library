package org.rares.ratv.rotationaware;

import android.view.View;

/**
 * Default click listener that starts the animation on click.
 */
public class RotationAwareClickListener implements View.OnClickListener {

    private boolean reverse = false;

    @Override
    public void onClick(View v) {
        if (v instanceof RotationAwareTextView) {
            RotationAwareTextView view = (RotationAwareTextView) v;

            if (view.isDefaultAnimatorEnabled() &&
                    view.isDefaultClickListenerEnabled() &&
                    view.rotationAnimatorHost != null) {
                view.rotationAnimatorHost
                        .configureAnimator(reverse)
                        .setDuration(reverse ? 250 : 200)
                        .start();
            }

            reverse = !reverse;
        }
    }
}