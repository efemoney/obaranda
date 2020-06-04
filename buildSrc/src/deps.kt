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

@file:Suppress("ClassName", "UnstableApiUsage", "unused")

import org.gradle.api.internal.artifacts.dsl.ParsedModuleStringNotation

object Versions {
  const val agp = "4.1.0-alpha10"

  const val kotlin = "1.3.72"
  const val ktor = "1.3.2"
  const val kotlinpoet = "1.5.0"
  const val coroutines = "1.3.7"
  const val dagger = "2.28"
  const val hilt = "2.28-alpha"
  const val moshi = "1.9.2"
  const val retrofit = "2.9.0"
  const val okio = "2.6.0"
  const val okhttp = "4.7.2"
  const val store = "4.0.0-alpha04"
  const val glide = "4.11.0"
  const val material = "1.3.0-alpha01"
  const val timber = "4.7.1"
  const val flowBinding = "0.12.0"

  object androidx {
    const val core = "1.4.0-alpha01"
    const val activity = "1.2.0-alpha03"
    const val appcompat = "1.3.0-alpha01"
    const val fragment = "1.3.0-alpha05"
    const val coordinatorLayout = "1.1.0"
    const val constraintLayout = "2.0.0-beta6"
    const val recyclerview = "1.2.0-alpha02"
    const val recyclerviewSelection = "1.1.0-alpha06"
    const val lifecycle = "2.3.0-alpha03"
    const val navigation = "2.3.0-beta01"
    const val room = "2.2.5"
    const val preference = "1.1.1"
  }

  object snapshots {
    const val dagger = "HEAD-SNAPSHOT"
    const val timber = "4.8.0-SNAPSHOT"
  }
}

object Deps {
  const val okio = "com.squareup.okio:okio:${Versions.okio}"
  const val photoView = "com.github.chrisbanes:PhotoView:2.3.0"
  const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
  const val jsr250 = "javax.annotation:jsr250-api:1.0"
  const val kotlinpoet = "com.squareup:kotlinpoet:${Versions.kotlinpoet}"
  const val autoService = "com.google.auto.service:auto-service:+"

  object kotlin {
    const val bom = "org.jetbrains.kotlin:kotlin-bom:${Versions.kotlin}"
    const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"

    object stdlib : Dep("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}") {
      const val common = "org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.kotlin}"
      const val js = "org.jetbrains.kotlin:kotlin-stdlib-js:${Versions.kotlin}"
      const val jdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
      const val jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    }

    object scripting {
      const val jvm = "org.jetbrains.kotlin:kotlin-scripting-jvm:${Versions.kotlin}"
      const val jvmHost = "org.jetbrains.kotlin:kotlin-scripting-jvm-host-embeddable:${Versions.kotlin}"
    }

    object gradle {
      const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
      const val pluginApi = "org.jetbrains.kotlin:kotlin-gradle-plugin-api:${Versions.kotlin}"
    }
  }

  object ktor {
    const val bom = "io.ktor:ktor-bom:${Versions.ktor}"
    const val auth = "io.ktor:ktor-auth:${Versions.ktor}"

    object content {
      const val moshi = "com.ryanharter.ktor:ktor-moshi:1.0.1"
    }

    object server : Dep("io.ktor:ktor-server-core:${Versions.ktor}") {
      const val jvm = "io.ktor:ktor-server-jvm:${Versions.ktor}"
      const val netty = "io.ktor:ktor-server-netty:${Versions.ktor}"
    }

    object client : Dep("io.ktor:ktor-client-core:${Versions.ktor}") {
      const val jvm = "io.ktor:ktor-client-jvm:${Versions.ktor}"
      const val json = "io.ktor:ktor-client-json-jvm:${Versions.ktor}"
      const val okhttp = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
    }
  }

  object kotlinx {
    object coroutines {
      const val bom = "org.jetbrains.kotlinx:kotlinx-coroutines-bom:${Versions.coroutines}"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
      const val coreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.coroutines}"
      const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
      const val rx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${Versions.coroutines}"
    }
  }

  object android {
    const val gradlePlugin = "com.android.tools.build:gradle:${Versions.agp}"
  }

  object androidx {
    const val appcompat = "androidx.appcompat:appcompat:${Versions.androidx.appcompat}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.androidx.core}"
    const val activityKtx = "androidx.activity:activity-ktx:${Versions.androidx.activity}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.androidx.fragment}"
    const val fragmentTesting = "androidx.fragment:fragment-testing:${Versions.androidx.fragment}"
    const val preferenceKtx = "androidx.preference:preference-ktx:${Versions.androidx.preference}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.androidx.constraintLayout}"
    const val coordinatorLayout = "androidx.coordinatorlayout:coordinatorlayout:${Versions.androidx.coordinatorLayout}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.androidx.recyclerview}"
    const val recyclerViewSelection =
      "androidx.recyclerview:recyclerview-selection:${Versions.androidx.recyclerviewSelection}"

    object navigation {
      const val common = "androidx.navigation:navigation-common:${Versions.androidx.navigation}"
      const val commonKtx = "androidx.navigation:navigation-common-ktx:${Versions.androidx.navigation}"
      const val fragment = "androidx.navigation:navigation-fragment:${Versions.androidx.navigation}"
      const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.androidx.navigation}"
      const val runtime = "androidx.navigation:navigation-runtime:${Versions.androidx.navigation}"
      const val runtimeKtx = "androidx.navigation:navigation-runtime-ktx:${Versions.androidx.navigation}"
      const val safeArgsGenerator = "androidx.navigation:navigation-safe-args-generator:${Versions.androidx.navigation}"
      const val safeArgsGradlePlugin =
        "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.androidx.navigation}"
      const val ui = "androidx.navigation:navigation-ui:${Versions.androidx.navigation}"
      const val uiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.androidx.navigation}"
    }

    object lifecycle {
      const val common = "androidx.lifecycle:lifecycle-common:${Versions.androidx.lifecycle}"
      const val commonJava8 = "androidx.lifecycle:lifecycle-common-java8:${Versions.androidx.lifecycle}"
      const val compiler = "androidx.lifecycle:lifecycle-compiler:${Versions.androidx.lifecycle}"
      const val extensions = "androidx.lifecycle:lifecycle-extensions:${Versions.androidx.lifecycle}"
      const val livedata = "androidx.lifecycle:lifecycle-livedata:${Versions.androidx.lifecycle}"
      const val livedataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.androidx.lifecycle}"
      const val livedataCore = "androidx.lifecycle:lifecycle-livedata-core:${Versions.androidx.lifecycle}"
      const val livedataCoreKtx = "androidx.lifecycle:lifecycle-livedata-core-ktx:${Versions.androidx.lifecycle}"
      const val process = "androidx.lifecycle:lifecycle-process:${Versions.androidx.lifecycle}"
      const val runtime = "androidx.lifecycle:lifecycle-runtime:${Versions.androidx.lifecycle}"
      const val service = "androidx.lifecycle:lifecycle-service:${Versions.androidx.lifecycle}"
      const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel:${Versions.androidx.lifecycle}"
      const val viewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidx.lifecycle}"
      const val viewmodelSavedstate = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.androidx.lifecycle}"
    }

    object room {
      const val ktx = "androidx.room:room-ktx:${Versions.androidx.room}"
      const val runtime = "androidx.room:room-runtime:${Versions.androidx.room}"
      const val compiler = "androidx.room:room-compiler:${Versions.androidx.room}"
    }
  }

  object flowBinding {
    const val android = "io.github.reactivecircus.flowbinding:flowbinding-android:${Versions.flowBinding}"
    const val material = "io.github.reactivecircus.flowbinding:flowbinding-material:${Versions.flowBinding}"
    const val recyclerview = "io.github.reactivecircus.flowbinding:flowbinding-recyclerview:${Versions.flowBinding}"
  }

  object glide : Dep("com.github.bumptech.glide:glide:${Versions.glide}") {
    const val compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
    const val okHttp = "com.github.bumptech.glide:okhttp3-integration:${Versions.glide}"
    const val recyclerView = "com.github.bumptech.glide:recyclerview-integration:${Versions.glide}"
  }

  object dagger : Dep("com.google.dagger:dagger:${Versions.dagger}") {
    const val compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"

    object android : Dep("com.google.dagger:dagger-android:${Versions.dagger}") {
      const val support = "com.google.dagger:dagger-android-support:${Versions.dagger}"
      const val processor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    }

    object hilt : Dep("com.google.dagger:hilt-android:${Versions.hilt}") {
      const val compiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
      const val gradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
    }
  }

  object okHttp : Dep("com.squareup.okhttp3:okhttp:${Versions.okhttp}") {
    const val bom = "com.squareup.okhttp3:okhttp-bom:${Versions.okhttp}"
    const val logging = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
  }

  object material {
    const val android = "com.google.android.material:material:${Versions.material}"
  }

  object moshi : Dep("com.squareup.moshi:moshi:${Versions.moshi}") {
    const val adapters = "com.squareup.moshi:moshi-adapters:${Versions.moshi}"
    const val lazyAdapters = "com.serjltt.moshi:moshi-lazy-adapters:2.2"
    const val kotlinCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
  }

  object retrofit : Dep("com.squareup.retrofit2:retrofit:${Versions.retrofit}") {
    object converter {
      const val moshi = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    }
  }

  object store : Dep("com.dropbox.mobile.store:store4:${Versions.store}") {

  }
}

object Plugins {
  const val agp = Deps.android.gradlePlugin
  const val hilt = Deps.dagger.hilt.gradlePlugin
  const val kotlin = Deps.kotlin.gradle.plugin
  const val navigationSafeArgs = Deps.androidx.navigation.safeArgsGradlePlugin
}

object Repos {
  const val ktor = "https://kotlin.bintray.com/ktor/"
  const val jitpack = "https://jitpack.io"
  const val kotlinx = "https://kotlin.bintray.com/kotlinx"
  const val kotlinEap = "https://dl.bintray.com/kotlin/kotlin-eap"
  const val sonatype = "https://oss.sonatype.org/content/repositories/snapshots"
}

open class Dep(module: String) : Map<String, Any> by parseDependency(module)

// uses internal Gradle APIs
internal fun parseDependency(module: String): Map<String, Any> {

  val artifact = module.substringAfterLast('@', "")
  val strippedModule = module.substringBeforeLast('@')

  require(!strippedModule.contains('@'))

  return with(ParsedModuleStringNotation(strippedModule, artifact)) {
    mapOf(
      "group" to group,
      "name" to name,
      "version" to version,
      "classifier" to classifier,
      "ext" to artifactType
    ).filterValues { !it.isNullOrEmpty() }
  }
}
