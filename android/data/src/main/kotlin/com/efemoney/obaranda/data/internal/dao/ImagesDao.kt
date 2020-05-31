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
import androidx.room.ForeignKey.CASCADE
import com.efemoney.obaranda.data.model.Image

@Dao
abstract class ImagesDao {

  @Query("SELECT * FROM images WHERE comic_page = :page")
  abstract suspend fun findImagesByComicPage(page: Int): List<ImageEntity>?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun putComicImages(images: List<ImageEntity>)
}

@Entity(
  tableName = "images",
  foreignKeys = [
    ForeignKey(
      entity = ComicEntity::class,
      parentColumns = ["comic_page"],
      childColumns = ["comic_page"],
      onDelete = CASCADE,
      onUpdate = CASCADE
    )
  ]
)
data class ImageEntity @JvmOverloads constructor(

  @ColumnInfo(name = "comic_page", index = true)
  val comicPage: Int,

  @Embedded
  val image: Image,

  @Ignore
  val index: Int = 0,

  // Only necessary so we save only one entry for each image
  @PrimaryKey
  var id: String = "$comicPage-$index"
)