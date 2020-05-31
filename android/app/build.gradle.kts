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
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-kapt")
  id("androidx.navigation.safeargs.kotlin")
}

android {
  defaultConfig.applicationId = "com.efemoney.obaranda"
  buildFeatures.viewBinding = true
}

dependencies {
  implementation(project(":android:data"))
  implementation(project(":android:core"))

  implementation(Deps.androidx.appcompat)
  implementation(Deps.androidx.fragmentKtx)
  implementation(Deps.androidx.constraintLayout)
  implementation(Deps.androidx.preferenceKtx)

  implementation(Deps.androidx.navigation.uiKtx)
  implementation(Deps.androidx.navigation.runtimeKtx)
  implementation(Deps.androidx.navigation.fragmentKtx)

  implementation(Deps.material.android)
  implementation(Deps.photoView)

  implementation(Deps.flowBinding.android)
  implementation(Deps.flowBinding.material)
  implementation(Deps.flowBinding.recyclerview)

  kapt(Deps.dagger.compiler)
  implementation(Deps.dagger)
  implementation(Deps.jsr250)

  implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

  implementation(Deps.okio)
  implementation(Deps.okHttp)

  kapt(Deps.glide.compiler)
  implementation(Deps.glide)
  implementation(Deps.glide.okHttp)
  implementation(Deps.glide.recyclerView)

  implementation(Deps.androidx.lifecycle.runtime)
  implementation(Deps.androidx.lifecycle.livedataKtx)
  implementation(Deps.androidx.lifecycle.viewmodelKtx)
}
