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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.efemoney.obaranda.ktx.NoWildcards
import javax.inject.Inject
import javax.inject.Provider

class DaggerViewModelFactory @Inject constructor(
  private val providers: Map<Class<out ViewModel>, @NoWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>): T {

    val found = providers[modelClass]
      ?: providers.entries.find { modelClass.isAssignableFrom(it.key) }?.value
      ?: throw IllegalArgumentException("Unknown view model class $modelClass")

    return try {
      @Suppress("UNCHECKED_CAST")
      found.get() as T
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }
}