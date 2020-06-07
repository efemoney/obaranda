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

tasks {

  val generateServiceAccount by registering {
    val serviceAccountContents = providers.environmentVariable("GOOGLE_APPLICATION_CREDENTIALS_CONTENTS")
    val serviceAccountDir = file("$buildDir/generated-service-account")

    inputs.property("contents", Callable { serviceAccountContents.orNull })
    outputs.dir(serviceAccountDir)

    mustRunAfter(clean)

    doLast {
      serviceAccountContents.orNull?.let { contents ->
        logger.warn("Found service account contents: $contents")
        serviceAccountDir.mkdirs()
        File(serviceAccountDir, "service-account.json").also {
          logger.warn("Writing service account contents to: $it")
        }.writeText(contents)
      }
    }
  }

  register("stage") {
    dependsOn(installDist)
    mustRunAfter(clean, generateServiceAccount)
  }
}

dependencies {
  implementation(Deps.kotlin.stdlib.jdk8)

  implementation(Deps.okHttp)
  implementation(Deps.okHttp.logging)

  implementation(Deps.moshi)
  implementation(Deps.moshi.lazyAdapters)
  kapt(Deps.moshi.kotlinCodegen)

  implementation(Deps.retrofit)
  implementation(Deps.retrofit.converter.moshi)

  implementation(Deps.kotlinx.coroutines.core)

  implementation(Deps.ktor.server)
  implementation(Deps.ktor.server.netty)
  implementation(Deps.ktor.auth)
  implementation(Deps.ktor.content.moshi)

  implementation(Deps.dagger)
  kapt(Deps.dagger.compiler)

  implementation("com.github.trickl:palette:0.1.1")
  implementation("ch.qos.logback:logback-classic:1.2.3")
  implementation("com.google.firebase:firebase-admin:6.13.0")
}
