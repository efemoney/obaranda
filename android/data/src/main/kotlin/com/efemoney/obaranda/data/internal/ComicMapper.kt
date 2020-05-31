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

package com.efemoney.obaranda.data.internal

import androidx.core.graphics.toColorInt
import com.efemoney.obaranda.data.internal.dao.ComicWithImages
import com.efemoney.obaranda.data.model.*
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton
import com.efemoney.obaranda.data.comics.model.Comic as ComicJson

@Singleton
internal class ComicMapper @Inject constructor() {

  fun mapJson(json: ComicJson): Comic {

    return Comic(
      url = json.url,
      page = json.page,
      title = json.title,
      pubDate = OffsetDateTime.parse(json.pubDate),
      commentsCount = json.commentsCount,
      commentsThreadId = json.commentsThreadId,
      author = Author(
        name = json.author.name,
        url = json.author.url
      ),
      post = Post(
        title = json.post.title,
        body = json.post.body,
        transcript = json.post.transcript
      ),
      images = json.images.map {
        Image(
          url = it.url,
          alt = it.alt,
          size = Size(
            width = it.size.width,
            height = it.size.height
          ),
          palette = Palette(
            muted = it.palette.muted?.toColorInt(),
            vibrant = it.palette.vibrant?.toColorInt()
          )
        )
      }
    )
  }

  fun mapLocal(comicWithImages: ComicWithImages): Comic {
    with(comicWithImages) {
      return comic.copy(images = images)
    }
  }

  fun mapJsonList(jsons: List<ComicJson>) = jsons.map(::mapJson)

  fun mapLocalList(entities: List<ComicWithImages>) = entities.map(::mapLocal)
}
