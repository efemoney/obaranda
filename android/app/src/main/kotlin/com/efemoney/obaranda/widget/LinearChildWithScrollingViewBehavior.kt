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
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.SCROLL_AXIS_VERTICAL
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.core.view.updatePadding
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * [BottomSheetBehavior][com.google.android.material.bottomsheet.BottomSheetBehavior] subclass that works
 * exclusively on a LinearLayout child with a 'title' [View] and a nested scrolling child.
 *
 * When the title view is at the top of the parent CoordinatorLayout, this behavior elevates it
 * and the scrolling child scrolls (or at least pretends to scroll) under it.
 *
 * This behavior also 'dodges' top window insets.
 */
open class LinearChildWithScrollingViewBehavior : BottomSheetBehavior<LinearLayout> {

  var statusBarInset = 0

  constructor() : super()

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  init {

    // dodge top insets while sliding
    addSlideCallback { sheetView, _ -> dodgeTopInsets(sheetView as LinearLayout) }
  }

  override fun onApplyWindowInsets(
    coordinatorLayout: CoordinatorLayout,
    child: LinearLayout,
    insets: WindowInsetsCompat
  ): WindowInsetsCompat {
    statusBarInset = insets.systemWindowInsetTop
    return insets
  }

  override fun onStartNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: LinearLayout,
    directTargetChild: View,
    target: View,
    axes: Int,
    type: Int
  ): Boolean {

    val shouldListen = axes and SCROLL_AXIS_VERTICAL != 0
        && child.childCount == 2
        && ViewCompat.isNestedScrollingEnabled(child[1])

    return shouldListen || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
  }

  @CallSuper
  override fun onNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: LinearLayout,
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int
  ) {
    super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
    elevateOnScroll(child)
    dodgeTopInsets(child)
  }

  override fun onLayoutChild(
    parent: CoordinatorLayout,
    child: LinearLayout,
    layoutDirection: Int
  ) = super.onLayoutChild(parent, child, layoutDirection).also {
    elevateOnScroll(child)
    dodgeTopInsets(child)
  }

  private fun elevateOnScroll(child: LinearLayout) {

    val titleView = child[0]
    val scrollingView = child[1]

    val isAtTop = child.top == 0
    val hasScrolled = scrollingView.canScrollVertically(-1)

    // trigger stateListAnimator
    titleView.isActivated = isAtTop && hasScrolled
  }

  private fun dodgeTopInsets(child: LinearLayout) {

    val topView = child[0]
    val isObscuredAtTop = child.top <= statusBarInset

    topView.updatePadding(top = if (isObscuredAtTop) statusBarInset - child.top else 0)
  }
}
