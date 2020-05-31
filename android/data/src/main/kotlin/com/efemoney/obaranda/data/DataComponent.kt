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

package com.efemoney.obaranda.data

import android.content.Context
import com.efemoney.obaranda.data.internal.DataModule
import com.efemoney.obaranda.dispatchers.Dispatchers
import com.efemoney.obaranda.inject.qualifier.AppContext
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class])
interface DataComponent {

  fun client(): OkHttpClient // Glide needs it

  fun dispatchers(): Dispatchers

  fun comics(): ComicRepo

  fun comments(): CommentRepo

  @Component.Factory
  interface Factory {

    fun create(@BindsInstance @AppContext context: Context): DataComponent
  }
}
