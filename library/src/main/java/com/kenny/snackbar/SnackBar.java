package com.kenny.snackbar;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.view.View;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
public class SnackBar {
    /**
     * Shows a Snack Bar
     *
     * @param activity The activity to show the Snack Bar in
     * @param message  The Sting Resource of the message to show
     */
    public static void show(Activity activity, @StringRes int message) {
        SnackBarItem sbi = new SnackBarItem.Builder(activity)
                .setMessageResource(message)
                .build();

        SnackBarManager mngr = SnackBarManager.getInstance();
        mngr.addSnackBar(activity, sbi);

        if (!mngr.isShowingSnackBar()) {
            mngr.showSnackBars(activity);
        }
    }

    /**
     * Shows a Snack Bar
     *
     * @param activity        The activity to show the Snack Bar in
     * @param message         The Sting Resource of the message to show
     * @param actionMessage   The String Resource of the action message to show
     * @param onClickListener The onclick listener for the action button
     */
    public static void show(Activity activity, @StringRes int message, @StringRes int actionMessage, View.OnClickListener onClickListener) {
        SnackBarItem sbi = new SnackBarItem.Builder(activity)
                .setMessageResource(message)
                .setActionMessageResource(actionMessage)
                .setActionClickListener(onClickListener)
                .build();

        SnackBarManager mngr = SnackBarManager.getInstance();
        mngr.addSnackBar(activity, sbi);

        if (!mngr.isShowingSnackBar()) {
            mngr.showSnackBars(activity);
        }
    }

    /**
     * Shows a Snack Bar
     *
     * @param activity The activity to show the Snack Bar in
     * @param message  The  message to show
     */
    public static void show(Activity activity, String message) {
        SnackBarItem sbi = new SnackBarItem.Builder(activity)
                .setMessage(message)
                .build();

        SnackBarManager mngr = SnackBarManager.getInstance();
        mngr.addSnackBar(activity, sbi);

        if (!mngr.isShowingSnackBar()) {
            mngr.showSnackBars(activity);
        }
    }

    /**
     * Shows a Snack Bar
     *
     * @param activity        The activity to show the Snack Bar in
     * @param message         The message to show
     * @param actionMessage   The  action message to show
     * @param onClickListener The onclick listener for the action button
     */
    public static void show(Activity activity, String message, String actionMessage, View.OnClickListener onClickListener) {
        SnackBarItem sbi = new SnackBarItem.Builder(activity)
                .setMessage(message)
                .setActionMessage(actionMessage)
                .setActionClickListener(onClickListener)
                .build();

        SnackBarManager mngr = SnackBarManager.getInstance();
        mngr.addSnackBar(activity, sbi);

        if (!mngr.isShowingSnackBar()) {
            mngr.showSnackBars(activity);
        }
    }

    /**
     * Shows a SnackBar
     *
     * @param activity The activity to show the Snack Bar in
     * @param message  The Sting Resource of the message to show
     * @param listener The SnackBarListener to handle callbacks
     */
    public static void show(Activity activity, @StringRes int message, SnackBarListener listener) {
        SnackBarItem sbi = new SnackBarItem.Builder(activity)
                .setMessage(activity.getString(message))
                .setSnackBarListener(listener)
                .build();

        SnackBarManager mngr = SnackBarManager.getInstance();
        mngr.addSnackBar(activity, sbi);

        if (!mngr.isShowingSnackBar()) {
            mngr.showSnackBars(activity);
        }
    }

    /**
     * Shows a SnackBar
     *
     * @param activity      The activity to show the Snack Bar in
     * @param message       The Sting Resource of the message to show
     * @param actionMessage The String Resource of the action message to show
     * @param listener      The SnackBarListener to handle callbacks
     */
    public static void show(Activity activity, @StringRes int message, @StringRes int actionMessage, SnackBarListener listener) {
        SnackBarItem sbi = new SnackBarItem.Builder(activity)
                .setMessage(activity.getString(message))
                .setActionMessage(activity.getString(actionMessage))
                .setSnackBarListener(listener)
                .build();

        SnackBarManager mngr = SnackBarManager.getInstance();
        mngr.addSnackBar(activity, sbi);

        if (!mngr.isShowingSnackBar()) {
            mngr.showSnackBars(activity);
        }
    }

    /**
     * Shows a SnackBar
     *
     * @param activity      The activity to show the Snack Bar in
     * @param message       The message to show
     * @param actionMessage The action message to show
     * @param listener      The SnackBarListener to handle callbacks
     */
    public static void show(Activity activity, String message, String actionMessage, SnackBarListener listener) {
        SnackBarItem sbi = new SnackBarItem.Builder(activity)
                .setMessage(message)
                .setActionMessage(actionMessage)
                .setSnackBarListener(listener)
                .build();

        SnackBarManager mngr = SnackBarManager.getInstance();
        mngr.addSnackBar(activity, sbi);

        if (!mngr.isShowingSnackBar()) {
            mngr.showSnackBars(activity);
        }
    }

    /**
     * Shows a SnackBar
     *
     * @param activity The activity to show the Snack Bar in
     * @param message  The message to show
     * @param listener The SnackBarListener to handle callbacks
     */
    public static void show(Activity activity, String message, SnackBarListener listener) {
        SnackBarItem sbi = new SnackBarItem.Builder(activity)
                .setMessage(message)
                .setSnackBarListener(listener)
                .build();

        SnackBarManager mngr = SnackBarManager.getInstance();
        mngr.addSnackBar(activity, sbi);

        if (!mngr.isShowingSnackBar()) {
            mngr.showSnackBars(activity);
        }
    }

    /**
     * Shows a Snack Bar
     *
     * @param activity     The activity to show the Snack Bar in
     * @param snackBarItem The SnackBarItem to Show
     */
    public static void show(Activity activity, SnackBarItem snackBarItem) {
        if (snackBarItem == null) {
            throw new NullPointerException("SnackBarItem can not be null");
        }

        SnackBarManager mngr = SnackBarManager.getInstance();
        mngr.addSnackBar(activity, snackBarItem);

        if (!mngr.isShowingSnackBar()) {
            mngr.showSnackBars(activity);
        }
    }

    /**
     * Cleans up the {@link SnackBarItem} and the {@link Activity} it is tied to.
     * Used by the {@link com.kenny.snackbar.SnackBar.SnackBarManager} for SnackBar cleanup
     *
     * @param activity     The {@link Activity} tied to the {@link SnackBarItem}
     * @param snackBarItem The {@link SnackBarItem} to clean up
     */
    public static void dispose(Activity activity, SnackBarItem snackBarItem) {
        SnackBarManager.getInstance().disposeSnackBar(activity, snackBarItem);
    }

    /**
     * Cancels all SnackBars for the given activity
     *
     * @param activity
     */
    public static void cancelSnackBars(Activity activity) {
        SnackBarManager.getInstance().cancelSnackBars(activity);
    }

    private static class SnackBarManager {
        private final ConcurrentHashMap<Activity, ConcurrentLinkedQueue<SnackBarItem>> mQueue = new ConcurrentHashMap<>();

        private static SnackBarManager mManager;

        private boolean mIsShowingSnackBar = false;

        private boolean mIsCanceling = false;

        public static SnackBarManager getInstance() {
            if (mManager == null) mManager = new SnackBarManager();
            return mManager;
        }

        /**
         * Cancels all SnackBar messages for an activity
         *
         * @param activity
         */
        public void cancelSnackBars(Activity activity) {
            ConcurrentLinkedQueue<SnackBarItem> list = mQueue.get(activity);

            if (list != null) {
                mIsCanceling = true;

                for (SnackBarItem items : list) {
                    items.cancel();
                }

                mIsCanceling = false;
                list.clear();
                mQueue.remove(activity);
            }
        }

        /**
         * Adds a SnackBar to The queue to be displayed
         *
         * @param activity
         * @param item
         */
        public void addSnackBar(Activity activity, SnackBarItem item) {
            ConcurrentLinkedQueue<SnackBarItem> list = mQueue.get(activity);

            if (list == null) {
                list = new ConcurrentLinkedQueue<>();
                mQueue.put(activity, list);
            }

            list.add(item);
        }

        /**
         * Shows the nextSnackBar for the current activity
         *
         * @param activity
         */
        public void showSnackBars(Activity activity) {
            ConcurrentLinkedQueue<SnackBarItem> list = mQueue.get(activity);

            if (list != null && !list.isEmpty()) {
                mIsShowingSnackBar = true;
                list.peek().show(activity);
            }
        }

        /**
         * Cleans up the {@link SnackBarItem} and the {@link Activity} it is tied to
         *
         * @param activity     The {@link Activity} tied to the {@link SnackBarItem}
         * @param snackBarItem The {@link SnackBarItem} to clean up
         */
        public void disposeSnackBar(Activity activity, SnackBarItem snackBarItem) {
            ConcurrentLinkedQueue<SnackBarItem> list = mQueue.get(activity);

            if (list != null) {
                list.remove(snackBarItem);

                if (list.peek() == null) {
                    mQueue.remove(activity);
                    mIsShowingSnackBar = false;
                } else if (!mIsCanceling) {
                    mIsShowingSnackBar = true;
                    list.peek().show(activity);
                }
            }
        }

        public boolean isShowingSnackBar() {
            return mIsShowingSnackBar;
        }
    }
}
