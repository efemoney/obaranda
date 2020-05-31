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

package com.efemoney.obaranda.model

typealias Change = String

typealias Changes = String // comma separated string of changes

interface SupportsChange {

  fun getChanges(n: Item): Changes?
}

fun buildChanges(builder: ChangesBuilder.() -> Unit): Changes? {

  return ChangesBuilder().apply(builder).getChanges()
}

class ChangesBuilder {

  private val builder = StringBuilder()

  internal fun getChanges(): Changes? {
    return if (builder.isEmpty()) null else builder.deleteCharAt(builder.lastIndex).toString()
  }

  operator fun Change.unaryPlus() {
    builder.append(this).append(',')
  }
}
