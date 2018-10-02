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
                    view.getRotationAnimatorHost() != null) {
                view.getRotationAnimatorHost()
                        .configureAnimator(reverse)
                        .setDuration(reverse ? 1250 : 1200)
                        .start();
            }

            reverse = !reverse;
        }
    }
}