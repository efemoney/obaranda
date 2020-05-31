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

package com.efemoney.obaranda.errors

import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import java.io.PrintWriter
import java.io.StringWriter

open class ApiException(
  override val message: String? = null,
  val status: HttpStatusCode = InternalServerError,
  override val cause: Throwable? = null
) : Throwable()

val Throwable.name get() = this::class.simpleName

val Throwable.stackTraceString get() = StringWriter().apply { printStackTrace(PrintWriter(this)) }.toString()

class InvalidUserException :
  ApiException("Invalid or No User. Please login.", Unauthorized)

class AuthenticationException :
  ApiException("You cannot access this resource.", Forbidden)

class ComicNotFoundException(query: String?) :
  ApiException("Could not find comic${if (query != null) "($query)" else ""}", NotFound)