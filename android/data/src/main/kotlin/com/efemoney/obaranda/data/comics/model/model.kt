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

package com.efemoney.obaranda.data.comics.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Comic(
  val url: String,
  val page: Int,
  val title: String,
  val pubDate: String,
  val commentsCount: Int,
  val commentsThreadId: String,
  val images: List<Image>,
  val post: Post,
  val author: Author
)

@JsonClass(generateAdapter = true)
data class Author(
  val name: String,
  val url: String?
)

@JsonClass(generateAdapter = true)
data class Post(
  val title: String?,
  val body: String?,
  val transcript: String?
)

@JsonClass(generateAdapter = true)
data class Image(
  val url: String,
  val alt: String?,
  val size: Size,
  val palette: Palette
)

@JsonClass(generateAdapter = true)
data class Palette(
  val muted: String?,
  val vibrant: String?
)

@JsonClass(generateAdapter = true)
data class Size(
  val width: Int,
  val height: Int
)