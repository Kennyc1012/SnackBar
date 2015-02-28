package com.kenny.snackbar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.InterpolatorRes;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
public class SnackBarItem implements SnackBarAnimCompat.SnackBarAnimationListener {
    private static final int[] ATTR = new int[]
            {
                    R.attr.snack_bar_background_color,
                    R.attr.snack_bar_duration,
                    R.attr.snack_bar_interpolator,
                    R.attr.snack_bar_text_action_color,
                    R.attr.snack_bar_text_action_pressed_color,
                    R.attr.snack_bar_text_color
            };

    private View.OnClickListener mActionClickListener;

    private View mSnackBarView;

    // The animation set used for animation. Set as an object for compatibility
    private Object mAnimator;

    private String mMessageString;

    private String mActionMessage;

    // The color of the background
    private int mSnackBarColor = -1;

    private int mMessageColor = -1;

    // The default color the action item will be
    private int mDefaultActionColor = -1;

    // The pressed color of the action item
    private int mPressedActionColor = -1;

    /* Flag for when the animation is canceled, should the item be disposed of. Will be set to false when
     the action button is selected so it removes immediately.*/
    private boolean mShouldDisposeOnCancel = true;

    private boolean mIsDisposed = false;

    private Activity mActivity;

    // Callback for the SnackBarManager
    private SnackBarDisposeListner mListener;

    private SnackBarListener mSnackBarListener;

    private long mAnimationDuration = -1;

    @InterpolatorRes
    private int mInterpolatorId = -1;

    private Object mObject;

    private float mPreviousY;

    /**
     * Create a SnackBarItem
     *
     * @param message The message for the SnackBarItem
     */
    SnackBarItem(String message) {
        mMessageString = message;
    }

    /**
     * Create a SnackbarItem
     *
     * @param message         The message for the SnackBarItem
     * @param actionMessage   The action message for the SnackBarItem
     * @param onClickListener THe onClickListener for the action
     */
    SnackBarItem(String message, String actionMessage, View.OnClickListener onClickListener) {
        mMessageString = message;
        mActionMessage = actionMessage.toUpperCase();
        mActionClickListener = onClickListener;
    }

    private SnackBarItem() {
        // Empty constructor
    }

    /**
     * Shows the Snack Bar
     *
     * @param activity
     * @param listener
     */
    public void show(Activity activity, SnackBarDisposeListner listener) {
        if (TextUtils.isEmpty(mMessageString)) {
            throw new IllegalArgumentException("No message has been set for the Snack Bar");
        }

        mActivity = activity;
        mListener = listener;
        FrameLayout parent = (FrameLayout) activity.findViewById(android.R.id.content);
        mSnackBarView = activity.getLayoutInflater().inflate(R.layout.snack_bar, parent, false);
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
        messageTV.setText(mMessageString);
        messageTV.setTextColor(mMessageColor);
        messageTV.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), "RobotoCondensed-Regular.ttf"));

        if (!TextUtils.isEmpty(mActionMessage)) {
            // Only set up the action button when an action message ahs been supplied
            setupActionButton((ImageView) mSnackBarView.findViewById(R.id.action));
        }

        if (mAnimationDuration <= 0) mAnimationDuration = activity.getResources().getInteger(R.integer.snackbar_duration_length);
        if (mInterpolatorId == -1) mInterpolatorId = android.R.anim.accelerate_decelerate_interpolator;
        parent.addView(mSnackBarView);
        mAnimator = SnackBarAnimCompat.show(mActivity, mSnackBarView, this, mInterpolatorId, mAnimationDuration);
    }

    /**
     * Sets up the touch listener to allow the SnackBar to be swiped to dismissed
     * Code from https://github.com/MrEngineer13/SnackBar
     */
    private void setupGestureDetector() {
        mSnackBarView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (mIsDisposed) return false;

                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        int[] location = new int[2];
                        view.getLocationInWindow(location);

                        if (y > mPreviousY && y - mPreviousY >= 50) {
                            mShouldDisposeOnCancel = false;
                            SnackBarAnimCompat.cancelAnimationSet(mAnimator);
                            SnackBarAnimCompat.hide(mSnackBarView, SnackBarItem.this);
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
    private void setupActionButton(ImageView action) {
        action.setVisibility(View.VISIBLE);
        action.setImageDrawable(createActionButton(action.getResources()));

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShouldDisposeOnCancel = false;
                mShouldDisposeOnCancel = false;
                SnackBarAnimCompat.cancelAnimationSet(mAnimator);
                SnackBarAnimCompat.hide(mSnackBarView, SnackBarItem.this);

                if (mActionClickListener != null) {
                    mActionClickListener.onClick(view);
                }

                if (mSnackBarListener != null) {
                    mSnackBarListener.onSnackBarAction(mObject);
                }
            }
        });
    }

    /**
     * Create the actionButton using TextDrawables and StateListDrawables
     *
     * @param resources
     * @return
     */
    private Drawable createActionButton(Resources resources) {
        TextDrawable regular = new TextDrawable(mActionMessage, resources.getDimensionPixelSize(R.dimen.snack_bar_action_text_size),
                mDefaultActionColor, Typeface.createFromAsset(resources.getAssets(), "Roboto-Medium.ttf"));

        TextDrawable pressed = new TextDrawable(mActionMessage, resources.getDimensionPixelSize(R.dimen.snack_bar_action_text_size),
                mPressedActionColor, Typeface.createFromAsset(resources.getAssets(), "Roboto-Medium.ttf"));

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
        stateListDrawable.addState(new int[]{}, regular);
        return stateListDrawable;
    }

    /**
     * Gets the attributes to be used for the SnackBar from the context style
     *
     * @param context
     */
    private void getAttributes(Context context) {
        TypedArray a = context.obtainStyledAttributes(ATTR);
        Resources res = context.getResources();

        if (mSnackBarColor == -1) mSnackBarColor = a.getColor(0, res.getColor(R.color.snack_bar_bg));
        if (mAnimationDuration == -1) mAnimationDuration = a.getInt(1, 3000);
        if (mInterpolatorId == -1) mInterpolatorId = a.getResourceId(2, android.R.anim.accelerate_decelerate_interpolator);
        if (mDefaultActionColor == -1) mDefaultActionColor = a.getColor(3, res.getColor(R.color.snack_bar_action_default));
        if (mPressedActionColor == -1) mPressedActionColor = a.getColor(4, Color.WHITE);
        if (mMessageColor == -1) mMessageColor = a.getColor(5, Color.WHITE);
        a.recycle();
    }

    /**
     * Cancels the Snack Bar from being displayed
     */
    public void cancel() {
        SnackBarAnimCompat.cancelAnimationSet(mAnimator);
        dispose();
    }

    /**
     * Cleans up the Snack Bar when finished
     */
    private void dispose() {
        mIsDisposed = true;

        if (mSnackBarView != null) {
            FrameLayout parent = (FrameLayout) mSnackBarView.getParent();

            if (parent != null) {
                parent.removeView(mSnackBarView);
            }
        }

        if (mAnimator != null) {
            mAnimator = null;
        }

        mSnackBarView = null;
        mActionClickListener = null;

        if (mListener != null) {
            mListener.onDispose(mActivity, this);
        }
    }

    /**
     * Factory for building custom SnackBarItems
     */
    public static class Builder {
        private SnackBarItem mSnackBarItem;

        /**
         * Factory for creating SnackBarItems
         */
        public Builder() {
            mSnackBarItem = new SnackBarItem();
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
        public Builder setActionMessageColor(@ColorRes int color) {
            mSnackBarItem.mDefaultActionColor = color;
            return this;
        }

        /**
         * Sets the color of the action message when pressed
         *
         * @param color
         * @return
         */
        public Builder setActionMessagePressedColor(@ColorRes int color) {
            mSnackBarItem.mPressedActionColor = color;
            return this;
        }

        public Builder setSnackBarBackgroundColor(@ColorRes int color) {
            mSnackBarItem.mSnackBarColor = color;
            return this;
        }

        public Builder setSnackBarMessageColor(@ColorRes int color) {
            mSnackBarItem.mMessageColor = color;
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
         * Set the Interpolator of the SnackBar animation
         *
         * @param interpolator
         * @return
         */
        public Builder setInterpolator(@IdRes int interpolator) {
            mSnackBarItem.mInterpolatorId = interpolator;
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
         * Creates the SnackBarItem
         *
         * @return
         */
        public SnackBarItem build() {
            return mSnackBarItem;
        }
    }

    @Override
    public void onAnimationStart() {
        if (mSnackBarListener != null) {
            mSnackBarListener.onSnackBarStarted(mObject);
        }
    }

    @Override
    public void onAnimationCanceled() {
        if (mShouldDisposeOnCancel) dispose();
    }

    @Override
    public void onAnimationEnd() {
        if (mShouldDisposeOnCancel) {
            if (mSnackBarListener != null) {
                mSnackBarListener.onSnackBarFinished(mObject);
            }

            dispose();
        }
    }

    @Override
    public void onActionAnimationCanceled() {
        dispose();
    }

    @Override
    public void onActionAnimationEnd() {
        if (mSnackBarListener != null) {
            mSnackBarListener.onSnackBarFinished(mObject);
        }

        dispose();
    }

    public static interface SnackBarDisposeListner {
        /**
         * Called when the SnackBar has finished
         *
         * @param activity The activity tied to the SnackBar
         * @param snackBar The SnackBarItem that has finished
         */
        void onDispose(Activity activity, SnackBarItem snackBar);
    }

}