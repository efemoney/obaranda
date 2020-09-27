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

import com.ryanharter.ktor.moshi.moshi
import com.sun.tools.javac.file.Locations
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.routing.*
import org.slf4j.event.Level.INFO

fun Application.features() {
  install(CORS)
  install(AutoHeadResponse)
  install(Compression) { gzip() }
  install(ContentNegotiation) { moshi(component.moshi()) }
  install(CallLogging) { level = INFO }

  install(Locations)
}

fun Application.routes() = routing {

  route("/api") {
    get<> {}
    get("/comics", GetAllComics)
    get("/comics/{page}", GetComicPage)
    get("/comics/latest", GetComicLatest)
    put("/users/token", compose(AuthenticateUser, SaveUserToken))
    put("/users/page", compose(AuthenticateUser, SaveUserPage))
  }

  authenticate("cron") {
    post("/poll", PollSignal)
  }

  get("/external/disqus/oauth/success", DisqusSuccessCallback)
}
