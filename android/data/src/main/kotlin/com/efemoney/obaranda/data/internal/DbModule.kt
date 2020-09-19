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

import android.content.Context
import androidx.room.Room
import com.efemoney.obaranda.data.internal.db.Database
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
internal object DbModule {

  @[Provides Singleton]
  fun db(@ApplicationContext context: Context) =
    Room.databaseBuilder(context, Database::class.java, "obaranda.db").build()

  @[Provides Reusable]
  fun comicsDao(database: Database) = database.comicsDao()

  @[Provides Reusable]
  fun imagesDao(database: Database) = database.imagesDao()
}
