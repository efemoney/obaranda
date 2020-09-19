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

@file:Suppress("NOTHING_TO_INLINE")

package com.efemoney.obaranda.data.internal

import com.efemoney.obaranda.data.ComicRepo
import com.efemoney.obaranda.data.ComicsRequest
import com.efemoney.obaranda.data.ComicsResult
import com.efemoney.obaranda.data.comics.ComicsApi
import com.efemoney.obaranda.data.internal.dao.ComicEntity
import com.efemoney.obaranda.data.internal.dao.ComicsDao
import com.efemoney.obaranda.data.internal.dao.ImageEntity
import com.efemoney.obaranda.data.internal.dao.ImagesDao
import com.efemoney.obaranda.data.model.Comic
import com.efemoney.obaranda.dispatchers.Dispatchers
import com.efemoney.obaranda.ktx.withEach
import kotlinx.coroutines.invoke
import java.time.Instant
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now
import javax.inject.Inject

val epochZero: Instant = Instant.ofEpochMilli(0)

internal class RealComicRepo @Inject constructor(
  private val comicsDao: ComicsDao,
  private val imagesDao: ImagesDao,
  private val api: ComicsApi,
  private val mapper: ComicMapper,
  private val dispatchers: Dispatchers
) : ComicRepo {

  private lateinit var sessionExpiresAt: OffsetDateTime

  override suspend fun getComic(page: Int) = dispatchers.disk {
    comicsDao.getComic(page).let(mapper::mapLocal)
  }

  override suspend fun getComics(request: ComicsRequest) = dispatchers.disk {

    // Strategy is to return local comics if it hasn't expired but
    // fallback to network if there are no comics locally or they have expired
    // In the case of a network error, just return whatever local data we have

    runCatching {

      val comics = getLocal(request, forceLocal = false).takeIf(::notEmpty)
        ?: getNetwork(request).also { saveToDisk(it) }

      val size = comics.size

      // Crude offset/limit calculation.
      // Ideally should be retrieved from link header for network call
      // and from another db query for local call
      ComicsResult(
        comics,
        nextPageRequest = when {
          size < request.limit -> null
          else -> ComicsRequest(offset = request.offset + size, limit = request.limit)
        }
      )
    }.recoverCatching {
      ComicsResult(
        comics = getLocal(request, forceLocal = true),
        error = it.unwrapHttpMessage()
      )
    }.getOrElse {
      ComicsResult(emptyList(), error = it)
    }
  }

  private suspend fun saveToDisk(comics: List<Comic>) = dispatchers.disk {
    if (!::sessionExpiresAt.isInitialized) sessionExpiresAt = now().plusHours(2)

    comics.withEach {
      val comicEntity = ComicEntity(page, sessionExpiresAt, this)
      val imagesEntities = images.mapIndexed { i, image -> ImageEntity(page, image, i) }

      comicsDao.putComic(comicEntity)
      imagesDao.putComicImages(imagesEntities)
    }
  }

  private suspend fun getLocal(request: ComicsRequest, forceLocal: Boolean) = dispatchers.disk {
    val expiresAfter = if (forceLocal) epochZero else Instant.now()

    comicsDao.getComics(request.limit, request.offset, expiresAfter).let(mapper::mapLocalList)
  }

  private suspend fun getNetwork(request: ComicsRequest) = dispatchers.network {
    api.getComics(request.limit, request.offset).let(mapper::mapJsonList)
  }

  private inline fun notEmpty(it: List<Comic>) = it.isNotEmpty()
}
