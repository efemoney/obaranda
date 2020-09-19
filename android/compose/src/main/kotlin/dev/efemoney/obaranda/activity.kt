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

package dev.efemoney.obaranda

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.efemoney.obaranda.internal.NavigatorViewModel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ObarandaActivity : AppCompatActivity() {

  private val navigator by viewModels<NavigatorViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(FragmentContainerView(this).apply {
      id = R.id.main_container
    })

    supportFragmentManager.commit {
      val navHost = NavHostFragment.create(R.navigation.app)
      replace(R.id.main_container, navHost)
      setPrimaryNavigationFragment(navHost)
    }

    navigator.channel.consumeAsFlow().onEach {
      val controller = findNavController(R.id.main_container)
      if (it is DirectionsWithExtras && it.extras != null) {
        controller.navigate(it.directions, it.extras)
      } else {
        controller.navigate(it)
      }
    }.launchIn(lifecycleScope)
  }
}