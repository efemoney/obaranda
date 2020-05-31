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

package com.efemoney.obaranda.data.internal.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.efemoney.obaranda.data.model.Comic
import com.efemoney.obaranda.data.model.Image
import java.time.Instant
import java.time.OffsetDateTime

@Dao
interface ComicsDao {

  @Transaction
  @Query(
    """
      SELECT page,url,title,pubDate,commentsCount,commentsThreadId,post_body,post_title,post_transcript,author_name,author_url
      FROM comics
      WHERE datetime(expires_at) >= datetime(:expiresAfter)
      ORDER BY comic_page DESC
      LIMIT :limit OFFSET :offset
    """
  )
  suspend fun getComics(limit: Int, offset: Int, expiresAfter: Instant): List<ComicWithImages>

  @Transaction
  @Query(
    """
      SELECT page,url,title,pubDate,commentsCount,commentsThreadId,post_body,post_title,post_transcript,author_name,author_url
      FROM comics
      WHERE comic_page = :page
    """
  )
  suspend fun getComic(page: Int): ComicWithImages

  @Insert(onConflict = REPLACE)
  suspend fun putComic(comic: ComicEntity)

  @Insert(onConflict = REPLACE)
  suspend fun putComics(comics: List<ComicEntity>)
}

@Entity(tableName = "comics")
data class ComicEntity(

  // Primary key doesn't work from within embedded fields so we duplicate it here
  @PrimaryKey
  @ColumnInfo(name = "comic_page")
  val page: Int,

  @ColumnInfo(name = "expires_at")
  val expiresAt: OffsetDateTime,

  @Embedded
  val comic: Comic
)

data class ComicWithImages @JvmOverloads constructor(

  @Embedded
  val comic: Comic,

  @Relation(
    entity = ImageEntity::class,
    projection = ["url", "alt", "width", "height", "color_muted", "color_vibrant"],
    parentColumn = "page",
    entityColumn = "comic_page"
  )
  var images: List<Image> = emptyList()
)