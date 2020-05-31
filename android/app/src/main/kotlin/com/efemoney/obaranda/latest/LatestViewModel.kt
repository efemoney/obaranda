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

package com.efemoney.obaranda.latest

import com.efemoney.obaranda.*
import com.efemoney.obaranda.data.ComicRepo
import com.efemoney.obaranda.data.ComicsRequest
import com.efemoney.obaranda.data.model.Comic
import com.efemoney.obaranda.dispatchers.Dispatchers
import com.efemoney.obaranda.errors.withAction
import com.efemoney.obaranda.ktx.e
import io.reactivex.Observable
import io.reactivex.rxkotlin.merge
import io.reactivex.rxkotlin.ofType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.asObservable
import kotlinx.coroutines.rx2.rxObservable
import javax.inject.Inject

class LatestViewModel @Inject constructor(
  private val navigator: Navigator,
  private val repo: ComicRepo,
  private val dispatchers: Dispatchers
) : BaseMviViewModel<Intents, Actions, Result, State>() {

  private val _intents = Channel<Intents>(CONFLATED)
  override val intents: SendChannel<Intents> get() = _intents

  override val state = viewStateFlow(State()) {

    actionsToResults(intentsToActions(_intents.receiveAsFlow()))
      .catch { e(throwable = it) }
      .scan(State(), ::reduce)
      .collect { offer(it) }
  }

  override val intentsToActions: ViewStateProducerScope<State>.(Flow<Intents>) -> Flow<Actions> = { intents ->

    intents.asObservable().publish {

      it.ofType<Intents.ComicClicked>().subscribe(::comicClicked)

      listOf(
        it.ofType<Intents.LoadComics>()
          .map { Actions.LoadComics },
        it.ofType<Intents.LoadNextPage>()
          .filter { state.hasNextPage && !state.loadingNextPage }
          .map { Actions.LoadNextComics(state.nextPage!!) }
      ).merge()
    }.asFlow()
  }

  override val actionsToResults: ViewStateProducerScope<State>.(Flow<Actions>) -> Flow<Result> = { actions ->

    actions.asObservable().publish {

      listOf(
        it.ofType<Actions.LoadComics>().flatMap(::loadComics),
        it.ofType<Actions.LoadNextComics>().flatMap(::loadNextComics)
      ).merge()
    }.asFlow()
  }

  override suspend fun reduce(state: State, result: Result) = when (result) {

    is Result.LoadComics -> state.copy(
      comics = result.comics,
      loading = result.isLoading,
      loadingError = result.error?.withAction("Retry") { intents += Intents.LoadComics },
      nextPage = result.nextPageRequest
    )

    is Result.LoadNextComics -> state.copy(
      comics = state.comics + result.comics,
      loadingNextPage = result.isLoading,
      loadingNextPageError = result.error?.withAction("Retry") { intents += Intents.LoadNextPage },
      nextPage = result.nextPageRequest
    )
  }

  private fun comicClicked(intent: Intents.ComicClicked) = navigator.showComicDetails(intent.page)

  private fun loadComics(action: Actions.LoadComics): Observable<Result.LoadComics> {

    return rxObservable { offer(repo.getComics(ComicsRequest())) }
      .map { Result.LoadComics(comics = it.comics, error = it.error, nextPageRequest = it.nextPageRequest) }
      .onErrorReturn { Result.LoadComics(error = it) }
      .startWith(Result.LoadComics(isLoading = true))
  }

  private fun loadNextComics(action: Actions.LoadNextComics): Observable<Result.LoadNextComics> {

    return rxObservable { offer(repo.getComics(action.nextPage)) }
      .map { Result.LoadNextComics(comics = it.comics, error = it.error, nextPageRequest = it.nextPageRequest) }
      .onErrorReturn { Result.LoadNextComics(error = it, nextPageRequest = action.nextPage) }
      .startWith(Result.LoadNextComics(isLoading = true))
  }
}

sealed class Intents {

  object LoadComics : Intents()

  object LoadNextPage : Intents()

  class ComicClicked(val page: Int) : Intents()
}

sealed class Actions {

  object LoadComics : Actions()

  class LoadNextComics(val nextPage: ComicsRequest) : Actions()
}

sealed class Result {

  class LoadComics(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val comics: List<Comic> = emptyList(),
    val nextPageRequest: ComicsRequest? = null
  ) : Result()

  class LoadNextComics(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val comics: List<Comic> = emptyList(),
    val nextPageRequest: ComicsRequest? = null
  ) : Result()
}

data class State(
  val loading: Boolean = false,
  val loadingError: Throwable? = null,
  val loadingNextPage: Boolean = false,
  val loadingNextPageError: Throwable? = null,
  val comics: List<Comic> = emptyList(),
  val nextPage: ComicsRequest? = null
) {
  val hasNextPage get() = nextPage != null
}