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

import com.efemoney.obaranda.errors.InvalidUserException
import com.google.firebase.auth.UserRecord
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.request.header
import io.ktor.util.AttributeKey

val SaveUserToken: RouteHandler = {
  TODO()
}

val SaveUserPage: RouteHandler = {
  TODO()
}

val AuthenticateUser: RouteHandler = {

  call.userRecord = call.request.header("X-Auth-Uid")
    ?.let { call.component.user().userByUid(it) }
    ?: throw InvalidUserException()

  proceed()
}

var ApplicationCall.userRecord
  get() = attributes[UserRecordKey]
  private set(value) = attributes.put(UserRecordKey, value)

private val UserRecordKey = AttributeKey<UserRecord>("UserRecordKey")