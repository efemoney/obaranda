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

@file:Suppress("USELESS_CAST")

package com.efemoney.obaranda.ktx

import android.graphics.drawable.Drawable
import android.os.Build.VERSION_CODES.M
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.efemoney.obaranda.android.R

var View.compatForeground: Drawable?
  get() {
    return when {

      isAtLeastApi(M) -> foreground

      this is FrameLayout -> (this as FrameLayout).foreground

      this is ViewGroup -> {

        var dummyChild = getTag(R.id.dummy_foreground_child) as View?

        if (dummyChild == null) {
          dummyChild = View(context)
          addView(dummyChild, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
          setTag(R.id.dummy_foreground_child, dummyChild)
        }

        dummyChild.background
      }

      else -> null
    }
  }
  set(value) {
    when {

      isAtLeastApi(M) -> foreground = value

      this is FrameLayout -> (this as FrameLayout).foreground = value

      this is ViewGroup -> {

        var dummyChild = getTag(R.id.dummy_foreground_child) as View?

        if (dummyChild == null) {
          dummyChild = View(context)
          addView(dummyChild, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
          setTag(R.id.dummy_foreground_child, dummyChild)
        }

        dummyChild?.background = value
      }
    }
  }

inline fun <reified T : CoordinatorLayout.Behavior<*>> View.behavior(): T {

  val params = layoutParams as? CoordinatorLayout.LayoutParams
    ?: throw IllegalArgumentException("The view is not a child of CoordinatorLayout")

  return params.behavior as? T
    ?: throw IllegalArgumentException("The view is not associated with ${T::class.java.simpleName}")
}