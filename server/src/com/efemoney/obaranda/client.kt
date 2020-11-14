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

package com.efemoney.obaranda

import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.ktor.config.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

internal interface AllApi : DisqusApi, StreamingApi

@Module
internal interface ApiModule {

  @Binds
  fun AllApi.disqusApi(): DisqusApi

  @Binds
  fun AllApi.streamingApi(): StreamingApi

  companion object {

    @Provides
    @Reusable
    fun allApi(moshi: Moshi, okhttp: OkHttpClient): AllApi = Retrofit.Builder()
      .baseUrl("http://localhost/") // placeholder url, every api MUST specify its full url
      .validateEagerly(true)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .client(okhttp)
      .build()
      .create()

    @Provides
    @ApplicationScope
    fun okHttp(logger: org.slf4j.Logger, config: ApplicationConfig): OkHttpClient = OkHttpClient.Builder()
      .addInterceptor(
        HttpLoggingInterceptor(logger::info).setLevel(Level.valueOf(config.property("okHttp.logLevel").getString()))
      ).build()
  }
}

