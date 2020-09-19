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

package com.efemoney.obaranda

import android.os.Bundle
import android.view.View.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigator.Extras
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.efemoney.obaranda.MainFragmentDirections.Companion.actionGotoComic
import com.efemoney.obaranda.app.FragmentsModule
import com.efemoney.obaranda.app.ViewModelsModule
import com.efemoney.obaranda.databinding.ActivityObarandaBinding
import com.efemoney.obaranda.widget.GiveAllChildrenWindowInsets
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class ObarandaActivity : AppCompatActivity() {

  private lateinit var binding: ActivityObarandaBinding
  @Inject lateinit var factory: ViewModelProvider.Factory

  private val navigator by viewModels<NavigatorViewModel> { factory }

  private val scope = MainScope()

  override fun onCreate(savedInstanceState: Bundle?) {
    (application as ObarandaApp).component
      .activityComponentFactory()
      .create(RecycledViewPool())
      .inject(this)

    super.onCreate(savedInstanceState)

    setContentView(ActivityObarandaBinding.inflate(layoutInflater).also { binding = it }.root)
    ViewCompat.setOnApplyWindowInsetsListener(binding.container, GiveAllChildrenWindowInsets)

    binding.container.systemUiVisibility =
      SYSTEM_UI_FLAG_LAYOUT_STABLE or // stable so screen doesn't jump
          SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or // status bar
          SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // navigation bar

    navigator.channel.receiveAsFlow().onEach {
      if (it is DirectionsWithExtras && it.extras != null) {
        findNavController(R.id.container).navigate(it.directions, it.extras)
      } else {
        findNavController(R.id.container).navigate(it)
      }
    }.launchIn(scope)
  }

  override fun onDestroy() {
    super.onDestroy()
    scope.cancel()
  }

  @Inject
  fun injectFragmentManagerFactory(factory: FragmentFactory) {
    supportFragmentManager.fragmentFactory = factory
  }
}

@Subcomponent(modules = [FragmentsModule::class, ViewModelsModule::class])
interface ActivityComponent {

  fun inject(activity: ObarandaActivity)

  @Subcomponent.Factory
  interface Factory {

    fun create(@BindsInstance recycledViewPool: RecycledViewPool): ActivityComponent
  }
}

interface Navigator {

  fun navigate(directions: NavDirections)
}

fun Navigator.showComicDetails(page: Int) {
  navigate(actionGotoComic(page))
}

class NavigatorViewModel @Inject constructor() : Navigator, ViewModel() {
  val channel = Channel<NavDirections>()
  override fun navigate(directions: NavDirections) {
    channel.offer(directions)
  }
}

class DirectionsWithExtras(
  val directions: NavDirections,
  val extras: Extras? = null
) : NavDirections by directions