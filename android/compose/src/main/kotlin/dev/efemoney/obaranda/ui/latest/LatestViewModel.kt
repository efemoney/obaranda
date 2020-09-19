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

package dev.efemoney.obaranda.ui.latest

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efemoney.obaranda.data.ComicRepo
import com.efemoney.obaranda.data.ComicsRequest
import com.efemoney.obaranda.data.model.Comic
import com.efemoney.obaranda.dispatchers.Dispatchers
import dev.efemoney.obaranda.Navigator
import dev.efemoney.obaranda.errors.withAction
import dev.efemoney.obaranda.showComicDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LatestViewModel @ViewModelInject constructor(
  private val repo: ComicRepo,
  private val navigator: Navigator,
  private val dispatchers: Dispatchers
) : ViewModel() {

  val model = MutableStateFlow(UiModel())

  fun loadComics() {
    viewModelScope.launch {
      model.value = model.value.copy(loading = true)

      runCatching {
        repo.getComics(ComicsRequest())
      }.onSuccess {
        model.value = model.value.copy(
          comics = it.comics,
          loading = false,
          nextPage = it.nextPageRequest,
          loadingError = it.error?.withAction("Retry") { loadComics() }
        )
      }.onFailure {
        model.value = model.value.copy(loading = false, loadingError = it)
      }
    }
  }

  fun loadNextComics() = viewModelScope.launch {

  }

  fun comicClicked(page: Int) = navigator.showComicDetails(page)

  data class UiModel(
    val loading: Boolean = false,
    val loadingError: Throwable? = null,
    val loadingNextPage: Boolean = false,
    val loadingNextPageError: Throwable? = null,
    val comics: List<Comic> = emptyList(),
    val nextPage: ComicsRequest? = null
  )
}