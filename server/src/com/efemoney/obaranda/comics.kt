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

import io.ktor.application.call
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.LinkHeader
import io.ktor.http.link
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.util.getValue
import io.ktor.util.url
import kotlin.math.max

val GetAllComics: RouteHandler = {
  val limit: Int by call.request.queryParameters
  val offset: Int by call.request.queryParameters

  val comicsClient = context.component.comics()
  val comics = comicsClient.getAll(limit, offset)
  val totalCount = comicsClient.getTotalCount()

  if (offset > 0) {
    call.response.link(LinkHeader(uri = call.url {
      parameters["limit"] = "$limit"
      parameters["offset"] = "${max(offset - limit, 0)}"
    }, rel = "prev"))
  }

  if (offset + comics.size <= totalCount) {
    call.response.link(LinkHeader(uri = call.url {
      parameters["limit"] = "$limit"
      parameters["offset"] = "${offset + comics.size}"
    }, rel = "next"))
  }

  call.response.header(HttpHeaders.XTotalCount, totalCount)
  call.respond(HttpStatusCode.OK, comics)
}

val GetComicPage: RouteHandler = {
  val page: Int by call.parameters

  val comic = context.component.comics().getByPage(page)
  call.respond(HttpStatusCode.OK, comic)
}

val GetComicLatest: RouteHandler = {
  val comic = context.component.comics().getByLatest()
  call.respond(HttpStatusCode.OK, comic)
}
