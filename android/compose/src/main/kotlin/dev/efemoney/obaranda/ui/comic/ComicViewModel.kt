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

package dev.efemoney.obaranda.ui.comic

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efemoney.obaranda.data.ComicRepo
import com.efemoney.obaranda.data.CommentRepo
import com.efemoney.obaranda.data.CommentsRequest
import com.efemoney.obaranda.data.model.Comic
import com.efemoney.obaranda.data.model.Comment
import com.efemoney.obaranda.dispatchers.Dispatchers
import dev.efemoney.obaranda.Navigator
import dev.efemoney.obaranda.plusAssign
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ComicViewModel @ViewModelInject constructor(
  private val comicRepo: ComicRepo,
  private val commentRepo: CommentRepo,
  private val dispatchers: Dispatchers,
  private val navigator: Navigator,
) : ViewModel() {

  val model = MutableStateFlow(UiModel())

  fun loadComic(page: Int) {
    viewModelScope.launch {
      runCatching { comicRepo.getComic(page) }
        .onSuccess { model += model.value.copy(comic = it) }
        .onFailure { model += model.value.copy(error = it) }
    }
  }

  fun loadComments(commentsThreadId: String) {
    viewModelScope.launch {
      model += model.value.copy(loadingComments = true)

      runCatching { commentRepo.getComments(CommentsRequest(commentsThreadId)) }
        .onSuccess {
          model += model.value.copy(
            comments = it.comments,
            loadingComments = false,
            loadingCommentsError = it.error,
          )
        }
        .onFailure {
          model += model.value.copy(
            loadingComments = false,
            loadingCommentsError = it
          )
        }
    }
  }

  data class UiModel(
    val comic: Comic? = null,
    val error: Throwable? = null,
    val comments: List<Comment>? = null,
    val loadingComments: Boolean = false,
    val loadingCommentsError: Throwable? = null,
    val loggedIn: Boolean = false
  )
}
