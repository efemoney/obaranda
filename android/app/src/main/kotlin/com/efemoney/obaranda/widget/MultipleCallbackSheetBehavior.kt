/*
 * Copyright 2020 Efeturi Money. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("NOTHING_TO_INLINE")

package com.efemoney.obaranda.widget

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

inline fun <V : View> BottomSheetBehavior<V>.addStateChangedCallback(crossinline callback: (sheetView: View, newState: Int) -> Unit) {
  addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
    override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
    override fun onStateChanged(bottomSheet: View, newState: Int) = callback(bottomSheet, newState)
  })
}

inline fun <V : View> BottomSheetBehavior<V>.addSlideCallback(crossinline callback: (sheetView: View, slideOffset: Float) -> Unit) {
  addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
    override fun onStateChanged(bottomSheet: View, newState: Int) = Unit
    override fun onSlide(bottomSheet: View, slideOffset: Float) = callback(bottomSheet, slideOffset)
  })
}