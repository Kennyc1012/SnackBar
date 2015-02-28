package com.kenny.snackbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.InterpolatorRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/*
 * Copyright (C) 2014 Kenny Campagna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class SnackBarAnimCompat {

    /**
     * Shows the SnackBar
     *
     * @param context        App context
     * @param view           The SnackBar view to animate
     * @param listener       The listener to register callbacks to
     * @param interpolatorId The id of the interpolator to use
     * @param duration       The duration of the snack bar
     * @return
     */
    public static AnimatorSet show(@NonNull Context context, @NonNull View view, @NonNull final SnackBarAnimationListener listener,
                                   @InterpolatorRes int interpolatorId, long duration) {
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(AnimationUtils.loadInterpolator(context, interpolatorId));
        Animator appear = getAppearAnimation(context, view);
        appear.setTarget(view);

        set.playSequentially(
                appear,
                ObjectAnimator.ofFloat(view, "alpha", 1.0f, 1.0f).setDuration(duration),
                ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f).setDuration(context.getResources().getInteger(R.integer.snackbar_disappear_animation_length))
        );

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                listener.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                listener.onAnimationCanceled();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                listener.onAnimationStart();
            }
        });
        set.start();
        return set;
    }

    /**
     * Hides the SnackBar immediately
     *
     * @param view     The SnackBar view to animate
     * @param listener The listener to register callbacks to
     */
    public static void hide(@NonNull View view, final SnackBarAnimationListener listener) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f)
                .setDuration(view.getResources().getInteger(R.integer.snackbar_disappear_animation_length));

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                listener.onActionAnimationCanceled();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                listener.onActionAnimationEnd();
            }
        });

        anim.start();
    }

    /**
     * Cancels the animator set from the SnackBarItem object
     *
     * @param animatorSet The object to be cast as an animatorset to cancel
     */
    public static void cancelAnimationSet(Object animatorSet) {
        if (animatorSet instanceof AnimatorSet) {
            ((AnimatorSet) animatorSet).cancel();
        }
    }

    /**
     * Returns the animator for the appear animation
     *
     * @param context
     * @return
     */
    private static Animator getAppearAnimation(@NonNull Context context, @NonNull View view) {
        Resources res = context.getResources();
        AnimatorSet set = null;

        // Only Kit-Kit+ devices can have a translucent style
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int[] attrs = new int[]{android.R.attr.windowTranslucentNavigation};
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs);
            boolean isTranslucent = a.getBoolean(0, false);
            a.recycle();

            if (isTranslucent) {
                boolean isLandscape = res.getBoolean(R.bool.sb_isLandscape);
                boolean isTablet = res.getBoolean(R.bool.sb_isTablet);

                // Translucent nav bars will appear on anything that isn't landscape, as well as tablets in landscape
                if (!isLandscape || isTablet) {
                    int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
                    float animationFrom = res.getDimension(R.dimen.snack_bar_height);
                    float animationTo = res.getDimension(R.dimen.snack_bar_animation_position);
                    if (resourceId > 0) animationTo -= res.getDimensionPixelSize(resourceId);

                    set = new AnimatorSet();
                    set.playTogether(
                            ObjectAnimator.ofFloat(view, "translationY", animationFrom, animationTo),
                            ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f));
                }
            }
        }

        if (set == null) {
            set = new AnimatorSet().setDuration(res.getInteger(R.integer.snackbar_appear_animation_length));
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f),
                    ObjectAnimator.ofFloat(view, "translationY", res.getDimension(R.dimen.snack_bar_height),
                            res.getDimension(R.dimen.snack_bar_animation_position))
            );
        }

        return set;
    }

    /**
     * Callback interface for the SnackBar and the AnimationCompat class
     */
    public static interface SnackBarAnimationListener {
        public void onAnimationStart();

        public void onAnimationCanceled();

        public void onAnimationEnd();

        public void onActionAnimationCanceled();

        public void onActionAnimationEnd();
    }
}
