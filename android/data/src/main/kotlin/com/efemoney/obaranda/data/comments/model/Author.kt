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
data class Author(
  val id: String,
  val username: String,
  val name: String,
  val about: String,
  val avatar: Avatar,
  val location: String,
  val joinedAt: String,
  val url: String,

  val profileUrl: String,
  val signedUrl: String,

  val isPrivate: Boolean,
  val isPrimary: Boolean,
  val isAnonymous: Boolean,
  val isPowerContributor: Boolean,
  val disable3rdPartyTrackers: Boolean
)