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
import com.efemoney.obaranda.Navigator
import com.efemoney.obaranda.NavigatorViewModel
import com.efemoney.obaranda.comic.ComicViewModel
import com.efemoney.obaranda.latest.LatestViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
interface ViewModelsModule {

  @Binds
  fun factory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

  @[Binds IntoMap ViewModelKey(LatestViewModel::class)]
  fun latest(viewModel: LatestViewModel): ViewModel

  @[Binds IntoMap ViewModelKey(ComicViewModel::class)]
  fun comic(viewModel: ComicViewModel): ViewModel

  @Binds
  fun navigator(navigator: NavigatorViewModel): Navigator

  @[Binds IntoMap ViewModelKey(NavigatorViewModel::class)]
  fun navigatorImpl(viewModel: NavigatorViewModel): ViewModel
}