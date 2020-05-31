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

package com.efemoney.obaranda.ktx

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

fun Context.dpToPx(dp: Int) = (dpToPxFloat(dp) + 0.5f).toInt()

fun Context.dpToPxFloat(dp: Int) = dp * resources.displayMetrics.density

fun Context.spToPx(sp: Int) = (spToPxFloat(sp) + 0.5f).toInt()

fun Context.spToPxFloat(sp: Int) = sp * resources.displayMetrics.scaledDensity


inline fun Context.int(@IntegerRes id: Int): Int = resources.getInteger(id)

inline fun Context.font(@FontRes id: Int): Typeface? = runCatching { ResourcesCompat.getFont(this, id) }.getOrNull()

inline fun Context.dimen(@DimenRes id: Int): Float = resources.getDimension(id)

inline fun Context.dimenPx(@DimenRes id: Int): Int = resources.getDimensionPixelSize(id)

inline fun Context.string(@StringRes id: Int): String = getString(id)

inline fun Context.string(@StringRes id: Int, vararg args: Any): String = getString(id, *args)

inline fun Context.plural(@PluralsRes id: Int, quantity: Int): String = resources.getQuantityString(id, quantity)

inline fun Context.plural(@PluralsRes id: Int, quantity: Int, vararg args: Any): String {

  return resources.getQuantityString(id, quantity, *args)
}

inline fun Context.color(@ColorRes id: Int) = ContextCompat.getColor(this, id)

inline fun Context.drawable(@DrawableRes id: Int) = AppCompatResources.getDrawable(this, id)
