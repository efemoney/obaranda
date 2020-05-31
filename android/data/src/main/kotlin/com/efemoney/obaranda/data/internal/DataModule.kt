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

import com.efemoney.obaranda.data.ComicRepo
import com.efemoney.obaranda.data.CommentRepo
import com.efemoney.obaranda.data.comics.ComicsDataModule
import com.efemoney.obaranda.data.comments.CommentsDataModule
import com.efemoney.obaranda.data.networking.NetworkModule
import com.efemoney.obaranda.inject.DispatchersModule
import dagger.Binds
import dagger.Module

@Module(
  includes = [
    DispatchersModule::class,
    DbModule::class,
    NetworkModule::class,
    ComicsDataModule::class,
    CommentsDataModule::class
  ]
)
internal abstract class DataModule {

  @Binds
  abstract fun comics(realComicRepo: RealComicRepo): ComicRepo

  @Binds
  abstract fun comments(realCommentRepo: RealCommentRepo): CommentRepo
}