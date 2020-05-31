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

package com.efemoney.obaranda.ktx

import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.efemoney.obaranda.glide.GlideRequest
import com.efemoney.obaranda.glide.GlideRequests

inline fun <reified T> GlideBuilder.setDefaultTransitionOptions(options: TransitionOptions<*, T>) =
  setDefaultTransitionOptions(T::class.java, options)

inline fun <reified T> GlideRequests.asResourceOf() = `as`(T::class.java)

inline fun <reified T> GlideRequest<*>.decodeTypeOf() = decode(T::class.java)


class LoadFailedData<T>(
  val e: GlideException?,
  val model: Any?,
  val target: Target<T>,
  val isFirstResource: Boolean
)

class ResourceReadyData<T>(
  val resource: T,
  val model: Any?,
  val target: Target<T>,
  val dataSource: DataSource,
  val isFirstResource: Boolean
)

fun <T> RequestBuilder<T>.onResource(
  failed: ((LoadFailedData<T>) -> Unit)? = null,
  loaded: ((ResourceReadyData<T>) -> Unit)? = null
): RequestBuilder<T> {

  if (failed == null && loaded == null) return this

  return listener(object : RequestListener<T> {

    override fun onLoadFailed(
      e: GlideException?, model: Any?, target: Target<T>, isFirstResource: Boolean
    ): Boolean {
      failed?.invoke(LoadFailedData(e, model, target, isFirstResource))
      return false
    }

    override fun onResourceReady(
      resource: T, model: Any?, target: Target<T>, dataSource: DataSource, isFirstResource: Boolean
    ): Boolean {
      loaded?.invoke(ResourceReadyData(resource, model, target, dataSource, isFirstResource))
      return false
    }
  })
}


inline fun <reified M, reified D> MultiModelLoaderFactory.build() =
  build(M::class.java, D::class.java)

inline fun <reified M, reified D> Registry.replace(factory: ModelLoaderFactory<M, D>) =
  replace(M::class.java, D::class.java, factory)

inline fun <reified M, reified D> Registry.prepend(factory: ModelLoaderFactory<M, D>) =
  prepend(M::class.java, D::class.java, factory)

