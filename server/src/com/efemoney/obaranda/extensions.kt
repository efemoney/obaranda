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

import io.ktor.application.ApplicationCall
import io.ktor.util.pipeline.Pipeline
import io.ktor.util.pipeline.PipelineInterceptor
import io.ktor.util.pipeline.PipelinePhase

typealias RouteHandler = PipelineInterceptor<Unit, ApplicationCall>

/**
 * Compose multiple pipeline [interceptors] into one. This executes [interceptors] in a new pipeline running in this
 * interceptors context on this interceptors subject
 *
 * @return A [PipelineInterceptor] that executes other [interceptors] in a pipeline within its own context on its own subject
 * @param interceptors [PipelineInterceptor]s to execute **serially**
 */
fun <TSubject : Any, TContext : Any> compose(
  vararg interceptors: PipelineInterceptor<TSubject, TContext>
): PipelineInterceptor<TSubject, TContext> = {
  val pipeline = Pipeline(PipelinePhase("Compose"), interceptors.asList())
  pipeline.execute(context, subject)
}