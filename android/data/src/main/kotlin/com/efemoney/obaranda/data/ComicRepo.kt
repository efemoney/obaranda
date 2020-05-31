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

package com.efemoney.obaranda.data

import com.efemoney.obaranda.data.model.Comic

interface ComicRepo {

  suspend fun getComic(page: Int): Comic

  suspend fun getComics(request: ComicsRequest): ComicsResult
}

data class ComicsRequest(
  val offset: Int = 0,
  val limit: Int = 10
)

data class ComicsResult(
  val comics: List<Comic>,
  val error: Throwable? = null,
  val nextPageRequest: ComicsRequest? = null
)
