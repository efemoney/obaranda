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

@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.efemoney.obaranda.ktx

import timber.log.Timber

/*
 * Adapted from [Slimber](https://github.com/PaulWoitaschek/Slimber/blob/bea76b32563906edc8cf196ce4b6cfce8d12d6e6/slimber/src/main/kotlin/de/paul_woitaschek/slimber/Slimber.kt)
 */

/** Invokes an action if any trees are planted */
inline fun ifPlanted(tag: String? = null, action: () -> Unit) {
  if (Timber.treeCount() != 0) {
    if (tag != null) Timber.tag(tag)
    action()
  }
}

/** Delegates the provided message to [Timber.e] if any trees are planted. */
inline fun e(tag: String? = null, message: () -> String) = ifPlanted(tag) { Timber.e(message()) }

/** Delegates the provided message to [Timber.e] if any trees are planted. */
inline fun e(tag: String? = null, throwable: Throwable, message: () -> String) = ifPlanted(tag) {
  Timber.e(throwable, message())
}

/** Delegates the provided message to [Timber.e] if any trees are planted. */
inline fun e(tag: String? = null, throwable: Throwable) = ifPlanted(tag) {
  e(tag, throwable) { throwable.message!! }
}

/** Delegates the provided message to [Timber.w] if any trees are planted. */
inline fun w(tag: String? = null, message: () -> String) = ifPlanted(tag) { Timber.w(message()) }

/** Delegates the provided message to [Timber.w] if any trees are planted. */
inline fun w(tag: String? = null, throwable: Throwable, message: () -> String) = ifPlanted(tag) {
  Timber.w(throwable, message())
}

/** Delegates the provided message to [Timber.i] if any trees are planted. */
inline fun i(tag: String? = null, message: () -> String) = ifPlanted(tag) { Timber.i(message()) }

/** Delegates the provided message to [Timber.i] if any trees are planted. */
inline fun i(tag: String? = null, throwable: Throwable, message: () -> String) = ifPlanted(tag) {
  Timber.i(throwable, message())
}

/** Delegates the provided message to [Timber.d] if any trees are planted. */
inline fun d(tag: String? = null, message: () -> String) = ifPlanted(tag) { Timber.d(message()) }

/** Delegates the provided message to [Timber.d] if any trees are planted. */
inline fun d(tag: String? = null, throwable: Throwable, message: () -> String) = ifPlanted(tag) {
  Timber.d(throwable, message())
}

/** Delegates the provided message to [Timber.v] if any trees are planted. */
inline fun v(tag: String? = null, message: () -> String) = ifPlanted(tag) { Timber.v(message()) }

/** Delegates the provided message to [Timber.v] if any trees are planted. */
inline fun v(tag: String? = null, throwable: Throwable, message: () -> String) = ifPlanted(tag) {
  Timber.v(throwable, message())
}

/** Delegates the provided message to [Timber.wtf] if any trees are planted. */
inline fun wtf(tag: String? = null, message: () -> String) = ifPlanted(tag) { Timber.wtf(message()) }

/** Delegates the provided message to [Timber.wtf] if any trees are planted. */
inline fun wtf(tag: String? = null, throwable: Throwable, message: () -> String) = ifPlanted(tag) {
  Timber.wtf(throwable, message())
}

/** Delegates the provided message to [Timber.log] if any trees are planted. */
inline fun log(tag: String? = null, priority: Int, t: Throwable, message: () -> String) = ifPlanted(tag) {
  Timber.log(priority, t, message())
}

/** Delegates the provided message to [Timber.log] if any trees are planted. */
inline fun log(tag: String? = null, priority: Int, message: () -> String) = ifPlanted(tag) {
  Timber.log(priority, message())
}
