package com.kenny.snackbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.InterpolatorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
public class SnackBarItem {
    private static final int[] ATTR = new int[]{
            R.attr.snack_bar_background_color,
            R.attr.snack_bar_duration,
            R.attr.snack_bar_interpolator,
            R.attr.snack_bar_text_action_color,
            R.attr.snack_bar_text_color,
            R.attr.snack_bar_message_typeface,
            R.attr.snack_bar_action_typeface,
            R.attr.snack_bar_message_text_appearance,
            R.attr.snack_bar_action_text_appearance,
            R.attr.snack_bar_offset
    };

    private View.OnClickListener mActionClickListener;

    private View mSnackBarView;

    // The animation set used for animation. Set as an object for compatibility
    private AnimatorSet mAnimator;

    private String mMessageString;

    private String mActionMessage;

    // The color of the background
    private int mSnackBarColor = Color.TRANSPARENT;

    // The color of the message
    private int mMessageColor = Color.TRANSPARENT;

    // The default color the action item will be
    private int mActionColor = Color.TRANSPARENT;

    /* Flag for when the animation is canceled, should the item be disposed of. Will be set to false when
     the action button is selected so it removes immediately.*/
    private boolean mShouldDisposeOnCancel = true;

    private boolean mIsDisposed = false;

    private boolean mAutoDismiss = true;

    private boolean mIsGestureAccepted = false;

    private Activity mActivity;

    private SnackBarListener mSnackBarListener;

    private long mAnimationDuration = -1;

    private Interpolator mInterpolator;

    private Object mObject;

    private float mPreviousY;

    private Typeface mMessageTypeface = null;

    private Typeface mActionTypeface = null;

    @StyleRes
    private int mMessageTextAppearance = -1;

    @StyleRes
    private int mActionTextAppearance = -1;

    private float mSnackBarOffset = 0;

    private boolean mActionButtonPressed = false;

    private float mToAnimation = 0;

    private float mFromAnimation = 0;

    private SnackBarItem(Activity activty) {
        mActivity = activty;
    }

    /**
     * Shows the Snack Bar. This method is strictly for the SnackBarManager to call.
     */
    public void show() {
        if (TextUtils.isEmpty(mMessageString)) {
            throw new IllegalArgumentException("No message has been set for the Snack Bar");
        }

        FrameLayout parent = (FrameLayout) mActivity.findViewById(android.R.id.content);
        mSnackBarView = mActivity.getLayoutInflater().inflate(R.layout.snack_bar, parent, false);
        getAttributes(mActivity);

        // Setting up the background
        Drawable bg = mSnackBarView.getBackground();

        // Tablet SnackBars have a shape drawable as a background
        if (bg instanceof GradientDrawable) {
            ((GradientDrawable) bg).setColor(mSnackBarColor);
        } else {
            mSnackBarView.setBackgroundColor(mSnackBarColor);
        }

        setupGestureDetector();
        TextView messageTV = (TextView) mSnackBarView.findViewById(R.id.message);
        Button actionBtn = null;
        messageTV.setText(mMessageString);
        messageTV.setTextColor(mMessageColor);
        if (mMessageTextAppearance != -1) messageTV.setTextAppearance(mActivity, mMessageTextAppearance);
        if (mMessageTypeface != null) messageTV.setTypeface(mMessageTypeface);

        if (!TextUtils.isEmpty(mActionMessage)) {
            // Only set up the action button when an action message has been supplied
            setupActionButton(actionBtn = (Button) mSnackBarView.findViewById(R.id.action));
        }

        parent.addView(mSnackBarView);
        createShowAnimation(messageTV, actionBtn);
    }

    /**
     * Sets up the touch listener to allow the SnackBar to be swiped to dismissed
     * Code from https://github.com/MrEngineer13/SnackBar
     */
    private void setupGestureDetector() {
        mSnackBarView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (mIsDisposed || mIsGestureAccepted) return false;

                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        int[] location = new int[2];
                        view.getLocationInWindow(location);

                        if (y > mPreviousY && y - mPreviousY >= 50) {
                            mIsGestureAccepted = true;
                            mShouldDisposeOnCancel = false;
                            mAnimator.cancel();
                            createHideAnimation();
                        }
                }

                mPreviousY = y;
                return true;
            }
        });
    }

    /**
     * Sets up the action button if available
     *
     * @param action
     */
    private void setupActionButton(Button action) {
        action.setVisibility(View.VISIBLE);
        action.setText(mActionMessage.toUpperCase());
        action.setTextColor(mActionColor);
        if (mActionTextAppearance != -1) action.setTextAppearance(mActivity, mActionTextAppearance);
        if (mActionTypeface != null) action.setTypeface(mActionTypeface);

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActionButtonPressed = true;
                mShouldDisposeOnCancel = false;
                mAnimator.cancel();
                if (mActionClickListener != null) mActionClickListener.onClick(view);
                createHideAnimation();
            }
        });
    }

    /**
     * Gets the attributes to be used for the SnackBar from the context style
     *
     * @param context
     */
    private void getAttributes(Context context) {
        TypedArray a = context.obtainStyledAttributes(ATTR);
        Resources res = context.getResources();

        if (mSnackBarColor == Color.TRANSPARENT) mSnackBarColor = a.getColor(0, res.getColor(R.color.snack_bar_bg));
        if (mAnimationDuration == -1) mAnimationDuration = a.getInt(1, 3000);

        if (mInterpolator == null) {
            int id = a.getResourceId(2, android.R.interpolator.accelerate_decelerate);
            mInterpolator = AnimationUtils.loadInterpolator(mActivity, id);
        }

        if (mActionColor == Color.TRANSPARENT) mActionColor = a.getColor(3, res.getColor(R.color.snack_bar_action_default));
        if (mMessageColor == Color.TRANSPARENT) mMessageColor = a.getColor(4, Color.WHITE);

        if (mMessageTypeface == null) {
            String fontFile = a.getNonResourceString(5);
            if (!TextUtils.isEmpty(fontFile)) mMessageTypeface = Typeface.createFromAsset(mActivity.getAssets(), fontFile);
        }

        if (mActionTypeface == null) {
            String fontFile = a.getNonResourceString(6);
            if (!TextUtils.isEmpty(fontFile)) mActionTypeface = Typeface.createFromAsset(mActivity.getAssets(), fontFile);
        }

        if (mMessageTextAppearance == -1) mMessageTextAppearance = a.getResourceId(7, -1);
        if (mActionTextAppearance == -1) mActionTextAppearance = a.getResourceId(8, -1);
        if (mSnackBarOffset == 0) mSnackBarOffset = a.getDimension(9, 0);
        a.recycle();
    }

    /**
     * Cancels the Snack Bar from being displayed
     */
    public void cancel() {
        if (mAnimator != null) mAnimator.cancel();
        dispose();
    }

    /**
     * Cleans up the Snack Bar when finished
     */
    private void dispose() {
        mIsDisposed = true;

        if (mSnackBarView != null) {
            FrameLayout parent = (FrameLayout) mSnackBarView.getParent();
            if (parent != null) parent.removeView(mSnackBarView);
        }

        mAnimator = null;
        mSnackBarView = null;
        mActionClickListener = null;
        SnackBar.dispose(mActivity, this);
    }

    /**
     * Sets up and starts the show animation
     *
     * @param message The TextView of the Message
     * @param action  The Button of the action. May be null if no action is supplied
     */
    private void createShowAnimation(@NonNull TextView message, @Nullable Button action) {
        mAnimator = new AnimatorSet();
        mAnimator.setInterpolator(mInterpolator);
        List<Animator> appearAnimations = new ArrayList<>();
        appearAnimations.add(getAppearAnimation(message, action));

        // Only add this animation if the SnackBar should auto dismiss itself
        if (mAutoDismiss) {
            appearAnimations.add(ObjectAnimator.ofFloat(mSnackBarView, "alpha", 1.0f, 1.0f).setDuration(mAnimationDuration));
            appearAnimations.add(ObjectAnimator.ofFloat(mSnackBarView, "translationY", mToAnimation, mFromAnimation).setDuration(mActivity.getResources().getInteger(R.integer.snackbar_disappear_animation_length)));
        }

        mAnimator.playSequentially(appearAnimations);

        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mShouldDisposeOnCancel && mAutoDismiss) {
                    if (mSnackBarListener != null) mSnackBarListener.onSnackBarFinished(mObject, mActionButtonPressed);
                    dispose();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (mShouldDisposeOnCancel) dispose();

            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (mSnackBarListener != null) mSnackBarListener.onSnackBarStarted(mObject);
            }
        });

        mAnimator.start();
    }

    /**
     * Sets up and starts the hide animation
     */
    private void createHideAnimation() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mSnackBarView, "translationY", mToAnimation, mFromAnimation)
                .setDuration(mActivity.getResources().getInteger(R.integer.snackbar_disappear_animation_length));

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                dispose();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mSnackBarListener != null) mSnackBarListener.onSnackBarFinished(mObject, mActionButtonPressed);
                dispose();
            }
        });

        anim.start();
    }

    /**
     * Returns the animator for the appear animation
     *
     * @param message The TextView of the Message
     * @param action  The Button of the action. May be null if no action is supplied
     * @return
     */
    private Animator getAppearAnimation(@NonNull TextView message, @Nullable Button action) {
        Resources res = mActivity.getResources();
        mFromAnimation = res.getDimension(R.dimen.snack_bar_height);
        mToAnimation = res.getDimension(R.dimen.snack_bar_animation_position) - mSnackBarOffset;
        int delay = res.getInteger(R.integer.snackbar_ui_delay);

        if (hasTranslucentNavigationBar()) {
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) mToAnimation -= res.getDimensionPixelSize(resourceId);
        }

        AnimatorSet set = new AnimatorSet();
        List<Animator> animations = new ArrayList<>();
        set.setDuration(res.getInteger(R.integer.snackbar_appear_animation_length));
        animations.add(ObjectAnimator.ofFloat(mSnackBarView, "translationY", mFromAnimation, mToAnimation));

        ObjectAnimator messageAnim = ObjectAnimator.ofFloat(message, "alpha", 0.0f, 1.0f);
        messageAnim.setStartDelay(delay);
        animations.add(messageAnim);

        if (action != null) {
            ObjectAnimator actionAnim = ObjectAnimator.ofFloat(action, "alpha", 0.0f, 1.0f);
            actionAnim.setStartDelay(delay);
            animations.add(actionAnim);
        }

        set.playTogether(animations);
        return set;
    }

    /**
     * Returns if the current style supports a transparent navigation bar
     *
     * @return
     */
    private boolean hasTranslucentNavigationBar() {
        boolean isTranslucent = false;
        // Only Kit-Kit+ devices can have a translucent style
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Resources res = mActivity.getResources();
            int transparencyId = res.getIdentifier("config_enableTranslucentDecor", "bool", "android");
            int[] attrs = new int[]{android.R.attr.windowTranslucentNavigation};
            TypedArray a = mActivity.getTheme().obtainStyledAttributes(attrs);
            isTranslucent = a.getBoolean(0, false) && transparencyId > 0 && res.getBoolean(transparencyId);
            a.recycle();
        }

        return isTranslucent;
    }

    /**
     * Factory for building custom SnackBarItems
     */
    public static class Builder {
        private SnackBarItem mSnackBarItem;

        private Resources mResources;

        /**
         * Factory for creating SnackBarItems
         */
        public Builder(Activity activity) {
            mSnackBarItem = new SnackBarItem(activity);
            mResources = activity.getResources();
        }

        /**
         * Sets the message for the SnackBarItem
         *
         * @param message
         * @return
         */
        public Builder setMessage(String message) {
            mSnackBarItem.mMessageString = message;
            return this;
        }

        /**
         * Sets the message for the SnackBarItem
         *
         * @param message
         * @return
         */
        public Builder setMessageResource(@StringRes int message) {
            mSnackBarItem.mMessageString = mResources.getString(message);
            return this;
        }

        /**
         * Sets the Action Message of the SnackbarItem
         *
         * @param actionMessage
         * @return
         */
        public Builder setActionMessage(String actionMessage) {
            // guard against any null values being passed
            if (TextUtils.isEmpty(actionMessage)) return this;

            mSnackBarItem.mActionMessage = actionMessage.toUpperCase();
            return this;
        }

        /**
         * Sets the Action Message of the SnackbarItem
         *
         * @param actionMessage
         * @return
         */
        public Builder setActionMessageResource(@StringRes int actionMessage) {
            mSnackBarItem.mActionMessage = mResources.getString(actionMessage);
            return this;
        }

        /**
         * Sets the onClick listener for the action message
         *
         * @param onClickListener
         * @return
         */
        public Builder setActionClickListener(View.OnClickListener onClickListener) {
            mSnackBarItem.mActionClickListener = onClickListener;
            return this;
        }

        /**
         * Sets the default color of the action message
         *
         * @param color
         * @return
         */
        public Builder setActionMessageColor(int color) {
            mSnackBarItem.mActionColor = color;
            return this;
        }

        /**
         * Sets the default color of the action message
         *
         * @param color
         * @return
         */
        public Builder setActionMessageColorResource(@ColorRes int color) {
            mSnackBarItem.mActionColor = mResources.getColor(color);
            return this;
        }

        /**
         * Sets the background color of the SnackBar
         *
         * @param color
         * @return
         */
        public Builder setSnackBarBackgroundColor(int color) {
            mSnackBarItem.mSnackBarColor = color;
            return this;
        }

        /**
         * Sets the background color of the SnackBar
         *
         * @param color
         * @return
         */
        public Builder setSnackBarBackgroundColorResource(@ColorRes int color) {
            mSnackBarItem.mSnackBarColor = mResources.getColor(color);
            return this;
        }

        /**
         * Sets the color of the message of the SnackBar
         *
         * @param color
         * @return
         */
        public Builder setSnackBarMessageColor(int color) {
            mSnackBarItem.mMessageColor = color;
            return this;
        }

        /**
         * Sets the color of the message of the SnackBar
         *
         * @param color
         * @return
         */
        public Builder setSnackBarMessageColorResource(@ColorRes int color) {
            mSnackBarItem.mMessageColor = mResources.getColor(color);
            return this;
        }

        /**
         * Sets the duration of the SnackBar in milliseconds
         *
         * @param duration
         * @return
         */
        public Builder setDuration(long duration) {
            mSnackBarItem.mAnimationDuration = duration;
            return this;
        }

        /**
         * Sets the duration of the SnackBar in milliseconds
         *
         * @param duration
         * @return
         */
        public Builder setDurationResource(@IntegerRes int duration) {
            mSnackBarItem.mAnimationDuration = mResources.getInteger(duration);
            return this;
        }

        /**
         * Set the Interpolator of the SnackBar animation
         *
         * @param interpolator
         * @return
         */
        public Builder setInterpolator(Interpolator interpolator) {
            mSnackBarItem.mInterpolator = interpolator;
            return this;
        }

        /**
         * Set the Interpolator of the SnackBar animation
         *
         * @param interpolator
         * @return
         */
        public Builder setInterpolatorResource(@InterpolatorRes int interpolator) {
            mSnackBarItem.mInterpolator = AnimationUtils.loadInterpolator(mSnackBarItem.mActivity, interpolator);
            return this;
        }

        /**
         * Set the SnackBars object that will be returned in the SnackBarListener call backs
         *
         * @param object
         * @return
         */
        public Builder setObject(Object object) {
            mSnackBarItem.mObject = object;
            return this;
        }

        /**
         * Sets the SnackBarListener
         *
         * @param listener
         * @return
         */
        public Builder setSnackBarListener(SnackBarListener listener) {
            mSnackBarItem.mSnackBarListener = listener;
            return this;
        }

        /**
         * Sets the typeface for the SnackBar message
         *
         * @param typeFace
         * @return
         */
        public Builder setMessageTypeface(Typeface typeFace) {
            mSnackBarItem.mMessageTypeface = typeFace;
            return this;
        }

        /**
         * Sets the typeface for the SnackBar action
         *
         * @param typeFace
         * @return
         */
        public Builder setActionTypeface(Typeface typeFace) {
            mSnackBarItem.mActionTypeface = typeFace;
            return this;
        }

        /**
         * Sets whether a SnackBar should auto dismiss itself, defaulted to true.
         * If set to false,* the duration value is ignored for the SnackBar
         *
         * @param autoDismiss
         * @return
         */
        public Builder setAutoDismiss(boolean autoDismiss) {
            mSnackBarItem.mAutoDismiss = autoDismiss;
            return this;
        }

        /**
         * Sets the text appearance style for the SnackBar message
         *
         * @param textAppearance
         * @return
         */
        public Builder setMessageTextAppearance(@StyleRes int textAppearance) {
            mSnackBarItem.mMessageTextAppearance = textAppearance;
            return this;
        }

        /**
         * Sets the text appearance style for the SnackBar action
         *
         * @param textAppearance
         * @return
         */
        public Builder setActionTextAppearance(@StyleRes int textAppearance) {
            mSnackBarItem.mActionTextAppearance = textAppearance;
            return this;
        }

        /**
         * Sets the offset for the SnackBar
         *
         * @param offset
         * @return
         */
        public Builder setSnackBarOffset(float offset) {
            mSnackBarItem.mSnackBarOffset = offset;
            return this;
        }

        /**
         * Sets the offset resource for the SnackBar
         *
         * @param offset
         * @return
         */
        public Builder setSnackBarOffsetResource(@DimenRes int offset) {
            mSnackBarItem.mSnackBarOffset = mResources.getDimension(offset);
            return this;
        }

        /**
         * Shows the SnackBar
         */
        public void show() {
            SnackBar.show(mSnackBarItem.mActivity, build());
        }

        /**
         * Creates the SnackBarItem
         *
         * @return
         */
        public SnackBarItem build() {
            return mSnackBarItem;
        }
    }
}