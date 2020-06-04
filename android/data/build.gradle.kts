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

@file:Suppress("UnstableApiUsage")

plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
}

android {

  val apiKey = providers
    .environmentVariable("DISQUS_API_KEY")
    .forUseAtConfigurationTime()
    .getOrElse("")

  defaultConfig {
    buildConfigField("String", "API_KEY", apiKey)
  }
}

kapt {
  arguments {
    arg("room.incremental", "true")
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.expandProjection", "true")
  }
}

dependencies {
  implementation(project(":android:core"))

  kapt(Deps.dagger.compiler)
  implementation(Deps.dagger)
  implementation(Deps.jsr250)

  implementation(Deps.okio)
  implementation(Deps.okHttp)
  implementation(Deps.okHttp.logging)

  implementation(Deps.retrofit)
  implementation(Deps.retrofit.converter.moshi)

  kapt(Deps.moshi.kotlinCodegen)
  implementation(Deps.moshi)
  implementation(Deps.moshi.adapters)
  implementation(Deps.moshi.lazyAdapters)

  kapt(Deps.androidx.room.compiler)
  implementation(Deps.androidx.room.ktx)
  implementation(Deps.androidx.room.runtime)
}
