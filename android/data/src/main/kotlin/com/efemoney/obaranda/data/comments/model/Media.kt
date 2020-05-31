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

package com.efemoney.obaranda.data.comments.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Media(
  val id: Long,
  val title: String,
  val description: String,
  val providerName: String,
  val post/* id */: String,

  val html: String,
  val forum: String,
  val thread: String,
  val type: String, // Int
  val mediaType: String, // Int
  val metadata: Metadata,

  val url: String,
  val urlRedirect: String,
  val resolvedUrl: String,
  val resolvedUrlRedirect: String,

  val htmlWidth: Int?,
  val htmlHeight: Int?,
  val thumbnailUrl: String,
  val thumbnailURL: String,
  val thumbnailWidth: Int,
  val thumbnailHeight: Int
)