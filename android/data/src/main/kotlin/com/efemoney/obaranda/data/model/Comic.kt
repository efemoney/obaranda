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

package com.efemoney.obaranda.data.model

import androidx.room.Embedded
import androidx.room.Ignore
import java.time.OffsetDateTime

// Ugly constructor because of Room
data class Comic @JvmOverloads constructor(
  val page: Int,
  val url: String,
  val title: String,
  val pubDate: OffsetDateTime,
  val commentsCount: Int,
  val commentsThreadId: String,
  @Ignore val images: List<Image> = listOf(),
  @Embedded(prefix = "post_") val post: Post,
  @Embedded(prefix = "author_") val author: Author
)
