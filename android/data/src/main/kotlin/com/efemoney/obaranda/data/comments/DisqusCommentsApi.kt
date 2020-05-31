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

package com.efemoney.obaranda.data.comments

import com.efemoney.obaranda.data.BuildConfig
import com.efemoney.obaranda.data.comments.model.Includes
import com.efemoney.obaranda.data.comments.model.Post
import com.efemoney.obaranda.ktx.NoWildcards
import com.serjltt.moshi.adapters.Wrapped
import retrofit2.http.GET
import retrofit2.http.Query

interface DisqusCommentsApi {

  @GET("threads/listPosts.json?api_key=$API_KEY&forum=$FORUM&limit=$MAX_LIMIT")
  @Wrapped(path = ["response"])
  suspend fun listPosts(
    @Query("thread") threadId: String,
    @Query("include") includes: @NoWildcards List<Includes>,
    @Query("api_key") apiKey: String? = API_KEY
  ): List<Post>

  companion object {
    const val ENDPOINT = BuildConfig.COMMENTS_BASE_URL
    private const val FORUM = BuildConfig.FORUM
    private const val API_KEY = BuildConfig.API_KEY
    private const val MAX_LIMIT = 100
  }
}
