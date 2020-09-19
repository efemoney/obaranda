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

@file:Suppress("EnumEntryName")

package com.efemoney.obaranda.data.comics

import com.efemoney.obaranda.data.comics.model.Comic
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface ComicsApi {

  @GET("comics")
  suspend fun getComics(@Query("limit") limit: Int, @Query("offset") offset: Int): List<Comic>

  @GET("comics/{page}")
  suspend fun getComic(@Path("page") page: Int): Comic

  @GET("comics/latest")
  suspend fun getLatestComic(): Comic

  companion object {
    const val ENDPOINT = "https://obaranda-push.herokuapp.com/api/"
  }
}


enum class Rel { next, prev }

data class Link(val url: String, val rel: Rel)

class Links internal constructor(list: List<Link>) : List<Link> by list {

  operator fun get(rel: Rel) = find { it.rel == rel }?.url
}

internal fun parseLinkHeader(response: Response<*>) =
  response.headers()["Link"]?.let(LinkHeaderParser::parseHeader) ?: Links(emptyList())

internal object LinkHeaderParser {

  // Basic regex to parse links in the "Link" header
  private val linkRegex = Regex("<([a-zA-Z0-9\\-:/.?=%#+><&]+)>;\\s?rel=\"([a-z]{3,})\"")

  fun parseHeader(linkHeader: String) = Links(
    linkHeader
      .split(',')
      .asSequence()
      .map(String::trim)
      .map {
        linkRegex.matchEntire(it)
          ?.destructured
          ?.let { (url, rel) -> Link(url, Rel.valueOf(rel)) }
      }
      .filterNotNull()
      .toList()
  )
}
