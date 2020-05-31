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

import com.efemoney.obaranda.await
import com.efemoney.obaranda.data
import com.efemoney.obaranda.errors.ComicNotFoundException
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query.Direction.DESCENDING
import com.squareup.moshi.JsonClass
import javax.inject.Inject

internal class Comics @Inject constructor(private val firestore: Firestore) {

  suspend fun getAll(limit: Int, offset: Int): List<Comic> {
    return firestore
      .collection("comics")
      .orderBy("page", DESCENDING)
      .limit(limit)
      .offset(offset)
      .await()
      .documents
      .map { it.data() }
  }

  suspend fun getTotalCount(): Long {
    return firestore
      .collection("internal")
      .document("comics-meta")
      .await()
      .let { it.takeIf { it.exists() }?.getLong("total-count") ?: 0 }
  }

  suspend fun getByPage(page: Int): Comic {
    return firestore
      .collection("comics")
      .whereEqualTo("page", page)
      .await()
      .documents
      .run { if (size == 1) get(0).data() else throw ComicNotFoundException("page $page") }
  }

  suspend fun getByLatest(): Comic {
    return firestore
      .collection("comics")
      .orderBy("page", DESCENDING)
      .limit(1)
      .await()
      .documents
      .run { if (size == 1) get(0).data() else throw ComicNotFoundException("latest") }
  }
}


@[NoArgConstructor JsonClass(generateAdapter = true)]
data class Comic(
  val page: Int,
  val url: String,
  val title: String,
  val commentsCount: Int,
  val commentsThreadId: String,
  val pubDate: String,
  val images: List<Image>,
  val post: Post,
  val author: Author
)

@[NoArgConstructor JsonClass(generateAdapter = true)]
data class Author(
  val name: String,
  val url: String?
)

@[NoArgConstructor JsonClass(generateAdapter = true)]
data class Post(
  val title: String?,
  val body: String?,
  val transcript: String?
)

@[NoArgConstructor JsonClass(generateAdapter = true)]
data class Size(
  val width: Int,
  val height: Int
)

@[NoArgConstructor JsonClass(generateAdapter = true)]
data class Palette(
  val muted: String?,
  val vibrant: String?
)

@[NoArgConstructor JsonClass(generateAdapter = true)]
data class Image(
  val url: String,
  val alt: String?,
  val size: Size?,
  val palette: Palette?
)