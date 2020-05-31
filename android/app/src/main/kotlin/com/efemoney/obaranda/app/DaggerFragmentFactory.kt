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

package com.efemoney.obaranda.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.efemoney.obaranda.ktx.NoWildcards
import javax.inject.Inject
import javax.inject.Provider

class DaggerFragmentFactory @Inject constructor(
  private val providers: Map<Class<out Fragment>, @NoWildcards Provider<Fragment>>
) : FragmentFactory() {

  override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
    val cls = loadFragmentClass(classLoader, className)
    val provider = providers[cls] ?: providers.entries.find { cls.isAssignableFrom(it.key) }?.value

    return provider?.get() ?: super.instantiate(classLoader, className)
  }
}