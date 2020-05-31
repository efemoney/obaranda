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

import com.efemoney.obaranda.model.Comics
import com.efemoney.obaranda.model.User
import com.squareup.moshi.Moshi
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationEnvironment
import io.ktor.config.ApplicationConfig
import io.ktor.util.AttributeKey
import javax.inject.Scope

@Scope
internal annotation class ApplicationScope

@Scope
internal annotation class CallScope


@ApplicationScope
@Component(
  modules = [FirebaseAdminModule::class, MoshiModule::class],
  dependencies = [ApplicationEnvironment::class]
)
internal interface ApplicationComponent {

  fun newCallComponentFactory(): CallComponent.Factory

  fun config(): ApplicationConfig

  fun moshi(): Moshi

  @Component.Factory
  interface Factory {

    fun create(@BindsInstance application: Application, environment: ApplicationEnvironment): ApplicationComponent
  }
}

@CallScope
@Subcomponent
internal interface CallComponent {

  fun comics(): Comics

  fun user(): User

  @Subcomponent.Factory
  interface Factory {

    fun create(@BindsInstance call: ApplicationCall): CallComponent
  }
}


internal val Application.component
  get() = attributes.computeIfAbsent(ApplicationComponentKey) {
    DaggerApplicationComponent.factory().create(this, environment)
  }

internal val ApplicationCall.component
  get() = attributes.computeIfAbsent(CallComponentKey) {
    application.component.newCallComponentFactory().create(this)
  }


private val ApplicationComponentKey = AttributeKey<ApplicationComponent>("ApplicationComponentKey")

private val CallComponentKey = AttributeKey<CallComponent>("CallComponentKey")
