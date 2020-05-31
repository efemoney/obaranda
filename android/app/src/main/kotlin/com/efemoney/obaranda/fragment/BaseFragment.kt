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

package com.efemoney.obaranda.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlin.LazyThreadSafetyMode.NONE

typealias Inflater = LayoutInflater

typealias Binding = ViewBinding

abstract class BaseFragment<T : Binding> : Fragment(), CoroutineScope by MainScope() {

  protected lateinit var binding: T

  protected val backPressedCallback by lazy(NONE) {
    object : OnBackPressedCallback(false) {
      override fun handleOnBackPressed() {
        handleBack()
      }
    }.also(requireActivity().onBackPressedDispatcher::addCallback)
  }

  @CallSuper
  override fun onCreateView(inflater: Inflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return createBinding(inflater, container)
      .also { binding = it }
      .also { bind() }
      .root.apply(View::requestApplyInsets)
  }

  protected abstract fun createBinding(inflater: Inflater, parent: ViewGroup?): T

  protected abstract fun bind()

  protected open fun handleBack() = false

  @CallSuper
  override fun onDestroy() {
    super.onDestroy()
    cancel()
  }
}

