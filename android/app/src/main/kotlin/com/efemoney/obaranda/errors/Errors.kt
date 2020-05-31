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

package com.efemoney.obaranda.errors

data class ErrorAction(
  val label: CharSequence,
  val block: () -> Unit
)

data class ThrowablePlusMeta(
  val actualError: Throwable,
  val title: CharSequence? = null,
  val action: ErrorAction? = null
) : Throwable(actualError.message, actualError.cause)

val Throwable.actualError: Throwable get() = if (this is ThrowablePlusMeta) actualError else this

fun Throwable.withTitle(title: CharSequence): Throwable {

  return when (this) {
    is ThrowablePlusMeta -> copy(title = title)
    else -> ThrowablePlusMeta(actualError = this, title = title)
  }
}

fun Throwable.withAction(label: CharSequence, action: () -> Unit): Throwable {

  return when (this) {
    is ThrowablePlusMeta -> copy(action = ErrorAction(label, action))
    else -> ThrowablePlusMeta(this, action = ErrorAction(label, action))
  }
}

inline fun Throwable.hasAction() = this is ThrowablePlusMeta && action != null

inline fun Throwable.hasTitle() = this is ThrowablePlusMeta && title != null
