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
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.efemoney.obaranda.R

class RoundedFrameLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  init {
    context.withStyledAttributes(attrs, R.styleable.RoundedFrameLayout, 0, R.style.RoundedFrameLayoutDefault) {

      val all = getDimension(R.styleable.RoundedFrameLayout_android_radius, 0f)
      val topLeft = getDimension(R.styleable.RoundedFrameLayout_android_topLeftRadius, all)
      val topRight = getDimension(R.styleable.RoundedFrameLayout_android_topRightRadius, all)
      val bottomRight = getDimension(R.styleable.RoundedFrameLayout_android_bottomRightRadius, all)
      val bottomLeft = getDimension(R.styleable.RoundedFrameLayout_android_bottomLeftRadius, all)

      background = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadii =
          floatArrayOf(topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft)
      }

      outlineProvider = ViewOutlineProvider.BACKGROUND

      clipToOutline = true
    }
  }
}