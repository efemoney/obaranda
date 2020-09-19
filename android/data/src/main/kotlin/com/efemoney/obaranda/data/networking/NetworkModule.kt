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

package com.efemoney.obaranda.data.networking

import com.efemoney.obaranda.data.BuildConfig.DEBUG
import com.efemoney.obaranda.ktx.i
import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import okhttp3.logging.HttpLoggingInterceptor.Logger
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object NetworkModule {

  @[Provides Reusable]
  fun logger() = object : Logger {
    override fun log(message: String) {
      i("OkHttp") { message }
    }
  }

  @[Provides Singleton]
  fun okHttpClient(logger: Logger, connectionInterceptor: ConnectivityInterceptor): OkHttpClient {

    return OkHttpClient.Builder()
      .connectTimeout(60, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)
      .writeTimeout(60, TimeUnit.SECONDS)
      .addInterceptor(connectionInterceptor)
      .addInterceptor(HttpLoggingInterceptor(logger).setLevel(if (DEBUG) BODY else NONE))
      .build()
  }

  @[Provides Singleton]
  fun moshi(): Moshi {
    return Moshi.Builder()
      .add(Wrapped.ADAPTER_FACTORY)
      .build()
  }

  @[Provides Singleton]
  fun retrofitBuilder(client: OkHttpClient, moshi: Moshi): Retrofit.Builder {

    // Preconfigure a builder
    return Retrofit.Builder()
      .client(client)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
  }
}