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

import android.app.Application
import com.efemoney.obaranda.app.Scoped
import com.efemoney.obaranda.app.To
import com.efemoney.obaranda.data.DaggerDataComponent
import com.efemoney.obaranda.data.VariantModule
import dagger.Component
import dagger.Module
import okhttp3.OkHttpClient
import javax.inject.Inject

open class ObarandaApp : Application() {

  @Inject
  lateinit var component: AppComponent

  override fun onCreate() {
    super.onCreate()
    component = DaggerAppComponent.factory().create(DaggerDataComponent.factory().create(this))
  }
}

@Module
object AppModule

@Scoped(To.App)
@Component(
  modules = [AppModule::class, VariantModule::class],
  dependencies = [DataComponent::class]
)
interface AppComponent {

  fun client(): OkHttpClient

  fun activityComponentFactory(): ActivityComponent.Factory

  @Component.Factory
  interface Factory {

    fun create(dataComponent: DataComponent): AppComponent
  }
}
