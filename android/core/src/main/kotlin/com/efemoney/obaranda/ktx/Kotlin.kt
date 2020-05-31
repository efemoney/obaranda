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

inline fun <T : Any> T.applyIf(condition: Boolean, block: T.() -> Unit) = if (condition) apply(block) else this

inline fun <T : Any> T.alsoIf(condition: Boolean, block: (T) -> Unit) = if (condition) also(block) else this

inline fun <T : Any> T.runIf(condition: Boolean, block: T.() -> T) = if (condition) block() else this

inline fun <T : Any> Iterable<T>.withEach(action: T.() -> Unit) {
  for (element in this) element.action()
}

inline val <T : Number> T.i get() = toInt()

inline val <T : Number> T.f get() = toFloat()

inline val <T : Number> T.d get() = toDouble()
