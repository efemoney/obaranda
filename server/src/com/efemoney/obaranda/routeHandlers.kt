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
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor


typealias RouteHandler = PipelineInterceptor<Unit, ApplicationCall>

typealias RouteHandlerContext = PipelineContext<Unit, ApplicationCall>


internal val RouteHandlerContext.moshi get() = application.component.moshi()

internal val RouteHandlerContext.config get() = application.component.config()

internal val RouteHandlerContext.disqusApi get() = application.component.disqus()

internal val RouteHandlerContext.streamingApi get() = application.component.streaming()

internal val RouteHandlerContext.comics get() = call.component.comics()

internal val RouteHandlerContext.settings get() = call.component.settings()
