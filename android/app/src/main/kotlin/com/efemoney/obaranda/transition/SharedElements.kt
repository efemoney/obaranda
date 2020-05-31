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

package com.efemoney.obaranda.transition

import android.view.View
import java.lang.ref.WeakReference

open class SharedElements private constructor(
  private val list: List<SharedElement>
) : Iterable<SharedElement> {

  fun names() = map(SharedElement::transitionName)

  override operator fun iterator() = Iterator(this)

  companion object {

    private val EMPTY = SharedElements(emptyList())

    @JvmStatic
    fun of(vararg pairs: Pair<String, View>): SharedElements {

      return when {
        pairs.isEmpty() -> EMPTY
        else -> SharedElements(pairs.map(::SharedElement))
      }
    }

    @JvmStatic
    fun of(vararg views: View): SharedElements {

      return when {
        views.isEmpty() -> EMPTY
        views.any { it.transitionName == null } -> throw Exception("View with null transition name")
        else -> SharedElements(views.map(::SharedElement))
      }
    }
  }

  class Iterator(private val s: SharedElements) : kotlin.collections.Iterator<SharedElement> {
    private var cursor = 0
    override operator fun hasNext() = cursor != s.list.size
    override operator fun next() = s.list[cursor++]
  }
}

data class SharedElement(
  val transitionName: String,
  val viewRef: WeakReference<View>
) {
  constructor(view: View) : this(view.transitionName, view)
  constructor(pair: Pair<String, View>) : this(pair.first, pair.second)
  constructor(transitionName: String, view: View) : this(transitionName, WeakReference(view))
}