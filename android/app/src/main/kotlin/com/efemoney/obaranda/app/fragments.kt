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
import com.efemoney.obaranda.MainFragment
import com.efemoney.obaranda.comic.ComicFragment
import com.efemoney.obaranda.favorites.FavoritesFragment
import com.efemoney.obaranda.latest.LatestFragment
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@MapKey
annotation class FragmentKey(val value: KClass<out Fragment>)

@Module
interface FragmentsModule {

  @Binds
  fun factory(factory: DaggerFragmentFactory): FragmentFactory

  @[Binds IntoMap FragmentKey(MainFragment::class)]
  fun main(fragment: MainFragment): Fragment

  @[Binds IntoMap FragmentKey(LatestFragment::class)]
  fun latest(fragment: LatestFragment): Fragment

  @[Binds IntoMap FragmentKey(ComicFragment::class)]
  fun comic(fragment: ComicFragment): Fragment

  @[Binds IntoMap FragmentKey(FavoritesFragment::class)]
  fun favorites(fragment: FavoritesFragment): Fragment
}