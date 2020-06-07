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

import com.google.firebase.auth.UserRecord
import io.ktor.application.Application
import io.ktor.auth.Credential
import io.ktor.auth.Principal
import io.ktor.auth.authentication
import io.ktor.auth.basic

fun Application.auth() = authentication {
  basic("cron") {
    realm = "cron-job"
    validate { (name, password) ->
      val requiredName = environment.config.property("cron.username").getString()
      val requiredPassword = environment.config.property("cron.password").getString()
      if (name == requiredName && password == requiredPassword) Check else null
    }
  }
}

private val Check = object : Principal {}

data class Uid(val value: String) : Credential

data class User(val record: UserRecord) : Principal