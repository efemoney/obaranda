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

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("androidx.navigation.safeargs.kotlin")
  id("dagger.hilt.android.plugin")
}

android {
  defaultConfig.applicationId = "dev.efemoney.obaranda"
  buildFeatures.compose = true
  kotlinOptions {
    useIR = true
    freeCompilerArgs = freeCompilerArgs + listOf(
      "-Xallow-jvm-ir-dependencies",
      "-Xskip-prerelease-check"
    )
  }
  composeOptions {
    kotlinCompilerVersion = Versions.kotlin
    kotlinCompilerExtensionVersion = Versions.androidx.compose
  }
}

dependencies {
  implementation(project(":android:core"))
  implementation(project(":android:data"))

  implementation(Deps.androidx.coreKtx)
  implementation(Deps.androidx.appcompat)
  implementation(Deps.androidx.ui.tooling)
  implementation(Deps.androidx.compose.ui)
  implementation(Deps.androidx.compose.material)
  implementation("io.coil-kt:coil:1.0.0-rc3")
  implementation("dev.chrisbanes.accompanist:accompanist-coil:0.2.2")
  implementation(Deps.androidx.lifecycle.runtime)
  implementation(Deps.androidx.lifecycle.viewmodelKtx)
  implementation(Deps.androidx.navigation.runtimeKtx)
  implementation(Deps.androidx.navigation.fragmentKtx)

  implementation(Deps.jsr250)
  implementation(Deps.dagger)
  implementation(Deps.dagger.hilt)
  implementation(Deps.androidx.hilt.viewModel)
  kapt(Deps.dagger.compiler)
  kapt(Deps.dagger.hilt.compiler)
  kapt(Deps.androidx.hilt.compiler)

  implementation(Deps.okio)
  implementation(Deps.okHttp)
}
