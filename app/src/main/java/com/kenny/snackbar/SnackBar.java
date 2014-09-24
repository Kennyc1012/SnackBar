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
        SnackBarItem sbi = new SnackBarItem(activity.getString(message));
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
        SnackBarItem sbi = new SnackBarItem(activity.getString(message), activity.getString(actionMessage), onClickListener);
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
        SnackBarItem sbi = new SnackBarItem(message);
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
        SnackBarItem sbi = new SnackBarItem(message, actionMessage, onClickListener);
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
     * Cancels all SnackBars for the given activity
     *
     * @param activity
     */
    public static void cancelSnackBars(Activity activity) {
        SnackBarManager.getInstance().cancelSnackBars(activity);
    }

    private static class SnackBarManager implements SnackBarItem.SnackBarDisposeListner {
        private final ConcurrentHashMap<Activity, ConcurrentLinkedQueue<SnackBarItem>> mQueue =
                new ConcurrentHashMap<Activity, ConcurrentLinkedQueue<SnackBarItem>>();

        private static SnackBarManager mManager;

        private boolean mIsShowingSnackBar = false;

        public static SnackBarManager getInstance() {
            if (mManager == null) {
                mManager = new SnackBarManager();
            }

            return mManager;
        }

        /**
         * Cancels all Snack Bar messages for an activity
         *
         * @param activity
         */
        public void cancelSnackBars(Activity activity) {
            ConcurrentLinkedQueue<SnackBarItem> list = mQueue.get(activity);

            if (list != null) {
                for (SnackBarItem items : list) {
                    items.cancel();
                }

                list.clear();
                mQueue.remove(activity);
            }
        }

        /**
         * Adds a Snack Bar to The queue to be displayed
         *
         * @param activity
         * @param item
         */
        public void addSnackBar(Activity activity, SnackBarItem item) {
            ConcurrentLinkedQueue<SnackBarItem> list = mQueue.get(activity);

            if (list == null) {
                list = new ConcurrentLinkedQueue<SnackBarItem>();
                mQueue.put(activity, list);
            }

            list.add(item);
        }

        /**
         * Shows the next SnackBar for the current activity
         *
         * @param activity
         */
        public void showSnackBars(Activity activity) {
            ConcurrentLinkedQueue<SnackBarItem> list = mQueue.get(activity);

            if (list != null && !list.isEmpty()) {
                mIsShowingSnackBar = true;
                list.peek().show(activity, this);
            }
        }

        @Override
        public void onDispose(Activity activity, SnackBarItem snackBar) {
            ConcurrentLinkedQueue<SnackBarItem> list = mQueue.get(activity);

            if (list != null) {
                list.remove(snackBar);

                if (list.peek() == null) {
                    mQueue.remove(activity);
                    mIsShowingSnackBar = false;
                } else {
                    mIsShowingSnackBar = true;
                    list.peek().show(activity, this);
                }
            }
        }

        public boolean isShowingSnackBar() {
            return mIsShowingSnackBar;
        }
    }
}
