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

package dev.efemoney.obaranda.internal

import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.NavDirections
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.efemoney.obaranda.Navigator
import kotlinx.coroutines.channels.Channel

class NavigatorViewModel @ViewModelInject constructor() : ViewModel(), Navigator {
  val channel = Channel<NavDirections>()
  override fun navigate(directions: NavDirections) {
    channel.offer(directions)
  }
}

@Module
@InstallIn(ActivityComponent::class)
object NavigatorModule {

  @Provides
  fun navigator(activity: FragmentActivity): Navigator = ViewModelProvider(activity).get<NavigatorViewModel>()
}
