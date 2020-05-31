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

@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.efemoney.obaranda

import com.efemoney.obaranda.ktx.d
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach

inline fun <ViewState : Any> viewStateFlow(
  initialState: ViewState,
  crossinline block: suspend ViewStateProducerScope<ViewState>.() -> Unit
): Flow<ViewState> = channelFlow {
  var state = initialState

  ViewStateProducerScope(
    get = { state },
    set = { state = it; offer(it) },
    outerScope = this
  ).block()

  awaitClose()

}.distinctUntilChanged().onEach { d("StateLog", it::toString) }

class ViewStateProducerScope<ViewState> constructor(
  private inline val get: () -> ViewState,
  private inline val set: (ViewState) -> Unit,
  private val outerScope: ProducerScope<ViewState>
) : ProducerScope<ViewState> by outerScope {
  var state: ViewState
    get() = get()
    set(value) = set(value)

  @Suppress("NOTHING_TO_INLINE")
  inline operator fun ViewState.unaryPlus() {
    state = this
  }
}