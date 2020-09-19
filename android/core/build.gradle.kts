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
}

dependencies {
  api(Deps.kotlin.stdlib.jdk8)
  api(Deps.androidx.coreKtx)
  api(Deps.androidx.appcompat)
  api(Deps.androidx.coordinatorLayout)
  api(Deps.kotlinx.coroutines.core)
  api(Deps.kotlinx.coroutines.android)
  api(Deps.kotlinx.coroutines.rx2)
  api(Deps.timber)

  implementation(Deps.dagger)
  implementation(Deps.jsr250)
}
