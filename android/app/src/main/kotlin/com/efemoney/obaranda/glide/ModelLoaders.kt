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

package com.efemoney.obaranda.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.request.target.Target
import com.efemoney.obaranda.data.model.Image
import com.efemoney.obaranda.ktx.build
import java.io.InputStream

class ImageModelLoader(
  private val delegate: ModelLoader<GlideUrl, InputStream>
) : ModelLoader<Image, InputStream> {

  override fun handles(model: Image) = true

  override fun buildLoadData(model: Image, width: Int, height: Int, options: Options) =
    // Load image with width requirement but no height requirement
    delegate.buildLoadData(GlideUrl(model.url), width, Target.SIZE_ORIGINAL, options)

  companion object Factory : ModelLoaderFactory<Image, InputStream> {
    override fun build(factory: MultiModelLoaderFactory) = ImageModelLoader(factory.build())
    override fun teardown() = Unit
  }
}