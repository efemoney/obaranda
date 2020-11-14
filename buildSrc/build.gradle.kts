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
  `kotlin-dsl`
}

sourceSets.main {
  java.setSrcDirs(setOf("src"))
  resources.setSrcDirs(setOf("resources"))
}

repositories {
  maven("https://dl.bintray.com/kotlin/kotlin-eap")
  google()
  jcenter()
  gradlePluginPortal()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.20-RC")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.20-RC")
  implementation("org.jetbrains.kotlin:kotlin-noarg:1.4.20-RC")
  implementation("com.android.tools.build:gradle:4.2.0-alpha16")
  implementation("com.google.dagger:hilt-android-gradle-plugin:2.29.1-alpha")
  implementation("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.1")
}
