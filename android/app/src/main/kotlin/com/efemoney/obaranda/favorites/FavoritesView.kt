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

package com.efemoney.obaranda.favorites

import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.efemoney.obaranda.R
import com.efemoney.obaranda.databinding.FragmentFavoritesBinding
import com.efemoney.obaranda.fragment.BaseFragment
import com.efemoney.obaranda.fragment.Inflater
import com.efemoney.obaranda.ktx.string
import javax.inject.Inject

class FavoritesFragment @Inject constructor(
  factory: ViewModelProvider.Factory
) : BaseFragment<FragmentFavoritesBinding>() {

  private val viewModel: FavoritesViewModel by viewModels { factory }

  override fun bind() {
    binding.toolbar.title = requireContext().string(R.string.favorites_title)
  }

  override fun createBinding(inflater: Inflater, parent: ViewGroup?) =
    FragmentFavoritesBinding.inflate(inflater, parent, false)
}