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

@file:Suppress("NOTHING_TO_INLINE", "UnstableApiUsage")

import com.android.build.api.dsl.AndroidSourceSet
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.get
import java.io.File
import java.util.*

inline fun PluginManager.withAnyPlugin(vararg plugins: String, action: Action<AppliedPlugin>) =
  plugins.forEach { withPlugin(it, action) }

inline fun <T : AndroidSourceSet> NamedDomainObjectContainer<T>.addKotlinDirectories() =
  configureEach { java.srcDir("src/$name/kotlin") }

inline fun File.loadProperties() =
  Properties().also { inputStream().use(it::load) }

inline fun Project.hasDependency(dependencyNotation: Any, inConfiguration: String = "implementation"): Boolean {

  val wanted = dependencies.create(dependencyNotation)

  return configurations[inConfiguration]
    .incoming
    .dependencies
    .matching { it == wanted }
    .isNotEmpty()
}

inline fun Project.withDependency(dependencyNotation: Any, inConfiguration: String, action: Action<Project>) {
  configurations[inConfiguration].withDependencies {
    val wanted = dependencies.create(dependencyNotation)
    if (matching { it == wanted }.isNotEmpty()) action.execute(this@withDependency)
  }
}