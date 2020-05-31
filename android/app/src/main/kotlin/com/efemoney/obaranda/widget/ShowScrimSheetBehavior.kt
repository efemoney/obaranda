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

package com.efemoney.obaranda.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * [BottomSheetBehavior] that shows a user configurable scrim
 */
open class ShowScrimSheetBehavior<V : View> : BottomSheetBehavior<V> {

  private var scrimColor = Color.BLACK
  private var scrimOpacity = 0f

  constructor() : super()

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  init {
    // default scrim covers content when bottom sheet slides
    addSlideCallback { _, slideOffset -> scrimOpacity = -slideOffset }
  }

  override fun getScrimColor(parent: CoordinatorLayout, child: V) = scrimColor

  override fun getScrimOpacity(parent: CoordinatorLayout, child: V) = scrimOpacity
}