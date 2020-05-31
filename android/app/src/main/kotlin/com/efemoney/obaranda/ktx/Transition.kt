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

import androidx.transition.Transition
import androidx.transition.TransitionSet

inline fun transitionSet(builder: TransitionSetBuilder.() -> Unit): TransitionSet {
  return TransitionSetBuilder(TransitionSet())
    .apply(builder)
    .build()
}

class TransitionSetBuilder(val set: TransitionSet) {

  inline operator fun Transition.unaryPlus() = apply { set.addTransition(this) }

  inline operator fun Transition.unaryMinus() = apply { set.removeTransition(this) }

  inline fun build(): TransitionSet = set
}
