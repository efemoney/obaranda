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

package com.efemoney.obaranda.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build.VERSION_CODES.LOLLIPOP
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.drawable.toDrawable
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.efemoney.obaranda.ktx.isAtLeastApi

object UiUtil {

  val fastOutSlowInInterpolator = FastOutSlowInInterpolator()

  /**
   * Creates a selector drawable that is API-aware. This will create a ripple for Lollipop+ and
   * supports masks. If this is pre-lollipop and no mask is provided, it will fall back to a simple
   * [StateListDrawable] with the color as its pressed and focused states.
   *
   * @param color Selector color
   * @param mask Mask drawable for ripples to be bound to
   * @return The drawable if successful, or null if not valid for this case (masked on pre-lollipop)
   */
  inline fun createColorSelector(@ColorInt color: Int, mask: Drawable?): Drawable? {

    return when {

      isAtLeastApi(LOLLIPOP) -> RippleDrawable(ColorStateList.valueOf(color), null, mask)

      mask == null -> StateListDrawable().apply {
        val colorDrawable = ColorDrawable(color)
        setEnterFadeDuration(200)
        setExitFadeDuration(200)
        addState(intArrayOf(android.R.attr.state_pressed), colorDrawable)
        addState(intArrayOf(android.R.attr.state_focused), colorDrawable)
        addState(intArrayOf(), null)
      }

      // We don't do it on pre-lollipop because normally selectors can't abide by a mask
      else -> null
    }
  }

  @TargetApi(LOLLIPOP)
  inline fun createRipple(@ColorInt color: Int, bounded: Boolean): RippleDrawable {
    return RippleDrawable(
      ColorStateList.valueOf(color), null,
      if (bounded) ColorDrawable(Color.WHITE) else null
    )
  }

  @SuppressLint("Range")
  @TargetApi(LOLLIPOP)
  inline fun createRipple(
    @ColorInt inputColor: Int,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
    bounded: Boolean
  ): RippleDrawable {
    return inputColor
      .let { ColorUtil.modifyAlpha(it, alpha) }
      .let { RippleDrawable(ColorStateList.valueOf(it), null, if (bounded) Color.WHITE.toDrawable() else null) }
  }

  /*@SuppressLint("Range")
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  inline fun createRipple(palette: Palette,
      @FloatRange(from = 0.0, to = 1.0) darkAlpha: Float,
      @FloatRange(from = 0.0, to = 1.0) lightAlpha: Float,
      @ColorInt fallbackColor: Int,
      bounded: Boolean): RippleDrawable {
    // try the named swatches in preference order
    val rippleColor = palette.orderedSwatches(darkAlpha, lightAlpha)
        .firstOrNull()
        ?.let { (swatch, alpha) ->
          return@let ColorUtil.modifyAlpha(swatch.rgb, alpha)
        } ?: fallbackColor
    return RippleDrawable(ColorStateList.valueOf(rippleColor), null,
        if (bounded) ColorDrawable(Color.WHITE) else null)
  }*/
}