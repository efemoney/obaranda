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

import com.efemoney.obaranda.errors.ApiException
import com.efemoney.obaranda.errors.name
import com.efemoney.obaranda.errors.stackTraceString
import com.squareup.moshi.JsonClass
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

@JsonClass(generateAdapter = true)
data class ErrorDto(val name: String, val message: String, val stackTrace: String? = null)

fun Application.errorHandling() {

  install(StatusPages) {
    exception<Throwable> {
      val includeStackTrace = call.request.queryParameters["stackTrace"]?.toBoolean() == true

      call.respond(
        status = (it as? ApiException)?.status ?: HttpStatusCode.InternalServerError,
        message = ErrorDto(
          name = it.name ?: "Error",
          message = it.message ?: "Error occurred while processing request.",
          stackTrace = if (includeStackTrace) it.stackTraceString else null
        )
      )
    }
  }
}