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

package com.efemoney.obaranda.comic

import com.efemoney.obaranda.BaseMviViewModel
import com.efemoney.obaranda.ViewStateProducerScope
import com.efemoney.obaranda.data.ComicRepo
import com.efemoney.obaranda.data.CommentRepo
import com.efemoney.obaranda.data.CommentsRequest
import com.efemoney.obaranda.data.model.Comic
import com.efemoney.obaranda.data.model.Comment
import com.efemoney.obaranda.dispatchers.Dispatchers
import com.efemoney.obaranda.errors.withAction
import com.efemoney.obaranda.ktx.e
import com.efemoney.obaranda.plusAssign
import com.efemoney.obaranda.viewStateFlow
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

class ComicViewModel @Inject constructor(
  private val comicRepo: ComicRepo,
  private val commentRepo: CommentRepo,
  private val dispatchers: Dispatchers
) : BaseMviViewModel<Intents, Actions, Result, State>() {

  private val _intents = Channel<Intents>(CONFLATED)
  override val intents: SendChannel<Intents> get() = _intents

  override val state: Flow<State> = viewStateFlow(State(loadingComments = true)) {

    actionsToResults(intentsToActions(_intents.receiveAsFlow()))
      .catch { e(throwable = it) }
      .scan(State(loadingComments = true), ::reduce)
      .collect { offer(it) }
  }

  override val intentsToActions: ViewStateProducerScope<State>.(Flow<Intents>) -> Flow<Actions> = { intents ->

    intents.asObservable().publish {

      val cancelCommentLoad = it.ofType<Intents.HideComments>()

      listOf(

        it.ofType<Intents.LoadComic>().map { Actions.GetComic(it.page) },

        // ShowComments triggers a load only when comments have never been loaded
        // RefreshComments always triggers a load
        listOf(
          it.ofType<Intents.ShowComments>().filter { state.comic != null && state.comments == null },
          it.ofType<Intents.RefreshComments>()
        ).merge().map { Actions.LoadComments(state.threadId, cancelCommentLoad) }
      ).merge()
    }.asFlow()
  }

  override val actionsToResults: ViewStateProducerScope<State>.(Flow<Actions>) -> Flow<Result> = { actions ->

    actions.asObservable().publish {
      listOf(
        it.ofType<Actions.GetComic>().switchMap(::getComic),
        it.ofType<Actions.LoadComments>().switchMap(::loadComments)
      ).merge()
    }.asFlow()
  }

  override suspend fun reduce(state: State, result: Result) = when (result) {

    is Result.GetComic -> state.copy(comic = result.comic, error = result.error)

    is Result.LoadComments -> state.copy(
      loadingComments = result.loading,
      loadingCommentsError = result.error,
      comments = result.comments
    )
  }

  private fun getComic(action: Actions.GetComic): Observable<Result.GetComic> {

    return rxObservable { offer(comicRepo.getComic(action.page)) }
      .map { Result.GetComic(comic = it) }
      .onErrorReturn { Result.GetComic(error = it) }
  }

  private fun loadComments(action: Actions.LoadComments): Observable<Result.LoadComments> {

    return rxObservable { offer(commentRepo.getComments(CommentsRequest(action.threadId))) }
      .takeUntil(action.cancelLoading)
      .map { Result.LoadComments(it.comments, it.error) }
      .onErrorReturn {
        Result.LoadComments(error = it.withAction("Reload") { intents += Intents.RefreshComments })
      }
      .startWith(Result.LoadComments(loading = true))
  }
}

sealed class Intents {

  class LoadComic(val page: Int) : Intents()

  object ComicClicked : Intents()

  object RefreshComments : Intents()

  object ShowComments : Intents()

  object HideComments : Intents()
}

sealed class Actions {

  class GetComic(val page: Int) : Actions()

  class LoadComments(val threadId: String, val cancelLoading: Observable<*>) : Actions()
}

sealed class Result {

  class GetComic(
    val comic: Comic? = null,
    val error: Throwable? = null
  ) : Result()

  class LoadComments(
    val comments: List<Comment> = emptyList(),
    val error: Throwable? = null,
    val loading: Boolean = false
  ) : Result()
}

data class State(
  val comic: Comic? = null,
  val error: Throwable? = null,
  val comments: List<Comment>? = null,
  val loadingComments: Boolean = false,
  val loadingCommentsError: Throwable? = null,
  val loggedIn: Boolean = false
) {
  val threadId get() = comic?.commentsThreadId!!
}
