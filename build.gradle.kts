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

import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.gradle.api.JavaVersion.VERSION_1_8 as java8

plugins { id("com.github.ben-manes.versions") version "0.36.0" }

allprojects {
  repositories {
    maven(Repos.kotlinEap)
    maven(Repos.jitpack)
    google()
    jcenter()
  }
}

subprojects {

  group = "com.efemoney.obaranda"
  version = "1.0.0"

  pluginManager.withAnyPlugin("java", "kotlin", "android", "android-library") {
    dependencies {
      "implementation"(platform(Deps.ktor.bom))
      "implementation"(platform(Deps.kotlin.bom))
      "implementation"(platform(Deps.kotlinx.coroutines.bom))
    }
  }

  pluginManager.withAnyPlugin("java", "kotlin") {
    configure<JavaPluginExtension> {
      sourceCompatibility = java8
      targetCompatibility = java8
    }
  }

  pluginManager.withPlugin("kotlin-kapt") {
    configure<KaptExtension> {
      correctErrorTypes = true
      showProcessorTimings = true
      mapDiagnosticLocations = true
      arguments {
        if (hasDependency(Deps.dagger.compiler, inConfiguration = "kapt")) {
          arg("dagger.fastInit", "enabled")
          arg("dagger.experimentalDaggerErrorMessages", "enabled")
        }
        if (hasDependency(Deps.dagger.hilt.compiler, inConfiguration = "kapt")) {
          arg("dagger.hilt.disableModulesHaveInstallInCheck", "true")
        }
      }
    }
  }

  pluginManager.withAnyPlugin("android", "android-library") {

    dependencies {
      "implementation"(platform(Deps.okHttp.bom))
    }

    configure<BaseExtension> {
      compileSdkVersion(30)

      defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true
      }

      sourceSets.addKotlinDirectories()

      compileOptions {
        // https://developer.android.com/studio/preview/features#j8-desugar
        isCoreLibraryDesugaringEnabled = true

        project.dependencies {
          "coreLibraryDesugaring"("com.android.tools:desugar_jdk_libs:1.1.0")
        }

        sourceCompatibility = java8
        targetCompatibility = java8
      }

      buildFeatures.run {
        buildConfig = false
        viewBinding = false
        aidl = false
        prefab = false
        compose = false
        shaders = false
        resValues = false
        renderScript = false
      }
    }
  }

  afterEvaluate {
    tasks.withType<KotlinCompile<*>>().configureEach {
      kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
          "-Xnew-inference",
          "-Xjvm-default=all",
          "-Xopt-in=kotlin.RequiresOptIn",
          "-Xopt-in=kotlin.ExperimentalStdlibApi",
          "-Xopt-in=kotlin.time.ExperimentalTime",
          "-Xopt-in=io.ktor.util.KtorExperimentalAPI",
          "-Xopt-in=kotlinx.coroutines.FlowPreview",
          "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
      }
      if (this is KotlinJvmCompile) kotlinOptions.jvmTarget = "1.8"
    }
  }
}
