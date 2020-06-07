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

package com.efemoney.obaranda

import com.serjltt.moshi.adapters.FirstElement
import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.Instant

val DisqusSuccessCallback: RouteHandler = {

}

interface DisqusApi {

  @[GET("$endPoint/posts/list.json?forum=obaranda-com&limit=100") Wrapped(path = ["response"])]
  suspend fun listPosts(
    @Query("api_secret") apiSecret: String,
    @Query("start") startAt: Instant
  ): List<DisqusPost>

  @[GET("$endPoint/threads/set.json?forum=obaranda-com") Wrapped(path = ["response"])]
  suspend fun listThreads(
    @Query("api_secret") apiSecret: String,
    @Query("thread") threadIds: List<String>
  ): List<DisqusThread>

  @[GET("$endPoint/threads/set.json?forum=obaranda-com") Wrapped(path = ["response"]) FirstElement]
  suspend fun threadByUrl(
    @Query("api_secret") apiSecret: String,
    @Query("thread:link") urls: List<String>
  ): DisqusThread

  companion object {
    const val endPoint = "https://disqus.com/api/3.0"
  }
}

@JsonClass(generateAdapter = true)
data class DisqusPost(val thread: String)

@JsonClass(generateAdapter = true)
data class DisqusThread(val id: String, val posts: Int)
