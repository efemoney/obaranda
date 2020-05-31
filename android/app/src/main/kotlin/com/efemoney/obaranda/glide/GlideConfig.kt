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

@file:Suppress("RemoveExplicitTypeArguments")

package com.efemoney.obaranda.glide

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import androidx.core.content.getSystemService
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888
import com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565
import com.bumptech.glide.load.engine.DiskCacheStrategy.ALL
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy.CENTER_INSIDE
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.efemoney.obaranda.BuildConfig
import com.efemoney.obaranda.ObarandaApp
import com.efemoney.obaranda.ktx.prepend
import com.efemoney.obaranda.ktx.replace
import com.efemoney.obaranda.ktx.setDefaultTransitionOptions

@GlideModule
@Excludes(OkHttpLibraryGlideModule::class)
class GlideConfig : AppGlideModule() {

  override fun isManifestParsingEnabled() = false

  override fun applyOptions(context: Context, builder: GlideBuilder) {
    val logLevel = if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR
    builder.setLogLevel(logLevel)

    val am = context.getSystemService<ActivityManager>()!!
    builder.setDefaultRequestOptions(
      RequestOptions()
        .format(if (am.isLowRamDevice) PREFER_RGB_565 else PREFER_ARGB_8888)
        .downsample(CENTER_INSIDE)
        .diskCacheStrategy(ALL)
        .disallowHardwareConfig()
    )

    builder.setDefaultTransitionOptions(withCrossFade())
  }

  override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
    val client = (context.applicationContext as ObarandaApp).component.client()

    registry.replace(OkHttpUrlLoader.Factory(client))
    registry.prepend(ImageModelLoader.Factory)
  }
}
