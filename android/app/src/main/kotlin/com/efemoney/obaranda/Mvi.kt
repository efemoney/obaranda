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

package com.efemoney.obaranda

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow

interface MviViewModel<Intents, Actions, Result, State> {

  val state: Flow<State>

  val intents: SendChannel<Intents>

  val intentsToActions: ViewStateProducerScope<State>.(Flow<Intents>) -> Flow<Actions>

  val actionsToResults: ViewStateProducerScope<State>.(Flow<Actions>) -> Flow<Result>

  suspend fun reduce(state: State, result: Result): State
}

inline operator fun <T> SendChannel<T>.plusAssign(value: T) {
  offer(value)
}
