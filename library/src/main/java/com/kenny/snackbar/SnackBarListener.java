package com.kenny.snackbar;

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
public interface SnackBarListener {
    /**
     * Callback for when the SnackBar has started its animation
     *
     * @param object Optional object
     */
    void onSnackBarStarted(Object object);

    /**
     * Callback for when the SnackBar finishes its animation
     *
     * @param object        Optional object
     * @param actionPressed If the action button was pressed
     */
    void onSnackBarFinished(Object object, boolean actionPressed);
}