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

package com.efemoney.obaranda.data.internal.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.efemoney.obaranda.data.internal.dao.ComicEntity
import com.efemoney.obaranda.data.internal.dao.ComicsDao
import com.efemoney.obaranda.data.internal.dao.ImageEntity
import com.efemoney.obaranda.data.internal.dao.ImagesDao
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ISO_INSTANT
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

@TypeConverters(Converters::class)
@Database(
  entities = [ComicEntity::class, ImageEntity::class],
  version = 1
)
abstract class Database : RoomDatabase() {
  abstract fun comicsDao(): ComicsDao
  abstract fun imagesDao(): ImagesDao
}

object Converters {

  @[JvmStatic TypeConverter]
  fun fromInstant(instant: Instant?) = ISO_INSTANT.format(instant)

  @[JvmStatic TypeConverter]
  fun fromOffsetDateTime(date: OffsetDateTime?) = ISO_OFFSET_DATE_TIME.format(date)

  @[JvmStatic TypeConverter]
  fun toOffsetDateTime(value: String?) = ISO_OFFSET_DATE_TIME.parse(value, OffsetDateTime::from)
}
