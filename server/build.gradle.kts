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
  application
  kotlin
  `kotlin-kapt`
  `kotlin-noarg`
}

application.mainClassName = "io.ktor.server.netty.EngineMain"

noArg.annotation("com.efemoney.obaranda.model.NoArgConstructor")

sourceSets {
  main {
    java.setSrcDirs(setOf("src"))
    resources.setSrcDirs(setOf("resources"))
  }
}

tasks.register("stage") {
  dependsOn("installDist")
}

dependencies {
  implementation(Deps.kotlin.stdlib.jdk8)

  implementation(Deps.moshi)
  kapt(Deps.moshi.kotlinCodegen)

  implementation(Deps.kotlinx.coroutines.core)

  implementation(Deps.ktor.server)
  implementation(Deps.ktor.server.netty)
  implementation(Deps.ktor.auth)
  implementation(Deps.ktor.content.moshi)

  implementation(Deps.dagger)
  kapt(Deps.dagger.compiler)

  implementation("ch.qos.logback:logback-classic:1.2.3")
  implementation("com.google.firebase:firebase-admin:6.13.0")
}
