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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updatePadding
import com.efemoney.obaranda.R.id.comment_input_pane
import com.efemoney.obaranda.ktx.dpToPx

class CommentsPaneBehavior : LinearChildWithScrollingViewBehavior {

  constructor() : super()

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  override fun layoutDependsOn(
    parent: CoordinatorLayout,
    child: LinearLayout,
    dependency: View
  ): Boolean {

    return dependency.id == comment_input_pane
        || super.layoutDependsOn(parent, child, dependency)
  }

  override fun onDependentViewChanged(
    parent: CoordinatorLayout,
    child: LinearLayout,
    dependency: View
  ): Boolean {

    if (dependency.id == comment_input_pane) {
      child.updatePadding(bottom = dependency.height + dependency.context.dpToPx(16))
      return true
    }

    return super.onDependentViewChanged(parent, child, dependency)
  }
}