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
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query.Direction.DESCENDING
import com.google.cloud.firestore.SetOptions
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
      .takeIf(DocumentSnapshot::exists)
      ?.getLong("total-count") ?: 0
  }

  suspend fun getByPage(page: Int): Comic {
    return firestore
      .collection("comics")
      .whereEqualTo("page", page)
      .await()
      .documents
      .singleOrNull()
      ?.data() ?: throw ComicNotFoundException("page $page")
  }

  suspend fun getByLatest(): Comic {
    return firestore
      .collection("comics")
      .orderBy("page", DESCENDING)
      .limit(1)
      .await()
      .documents
      .singleOrNull()
      ?.data() ?: throw ComicNotFoundException("latest")
  }

  suspend fun getPageByThreadId(threadId: String): Int? {
    return firestore.collection("comics")
      .whereEqualTo("commentsThreadId", threadId)
      .await()
      .singleOrNull()
      ?.id
      ?.toInt()
  }

  suspend fun putCommentsCount(page: Int, postCount: Int) {
    firestore.collection("comics")
      .document("$page")
      .set(mapOf("commentsCount" to postCount), SetOptions.merge())
      .await()
  }

  suspend fun deleteComicByPage(page: Int) {
    firestore.collection("comics")
      .document("$page")
      .delete()
      .await()
  }

  suspend fun putComic(comic: Comic) {
    firestore.collection("comics")
      .document("${comic.page}")
      .set(comic)
      .await()
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