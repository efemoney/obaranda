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

@file:Suppress("NOTHING_TO_INLINE")

package com.efemoney.obaranda

import com.serjltt.moshi.adapters.FirstElement
import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.*
import dagger.Module
import dagger.Provides
import dagger.Reusable
import java.io.IOException
import java.lang.reflect.Type
import java.time.Instant

/**
 * Added to allow serialization of Concrete collection types (ArrayList, HashSet etc) as Moshi doesn't support
 * serializing those types out of the box (only the base classes List, Set etc)
 *
 * Needed because of firebase admin for some mundane reason I do not remember
 */
private abstract class MutableCollectionJsonAdapter<C : MutableCollection<T>, T> private constructor(
  private val elementAdapter: JsonAdapter<T>
) : JsonAdapter<C>() {

  @Throws(IOException::class)
  override fun fromJson(reader: JsonReader) = newCollection().apply {
    reader.beginArray()
    while (reader.hasNext()) add(elementAdapter.fromJson(reader)!!)
    reader.endArray()
  }

  @Throws(IOException::class)
  override fun toJson(writer: JsonWriter, value: C?) {
    writer.beginArray()
    for (element in value!!) elementAdapter.toJson(writer, element)
    writer.endArray()
  }

  override fun toString() = "$elementAdapter.collection()"

  abstract fun newCollection(): C

  companion object FACTORY : Factory {

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
      val rawType = Types.getRawType(type)

      return when {
        annotations.isNotEmpty() -> null
        rawType == ArrayList::class.java -> newArrayListAdapter<Any>(type, moshi).nullSafe()
        else -> null
      }
    }

    private fun <T> newArrayListAdapter(type: Type, moshi: Moshi): JsonAdapter<MutableCollection<T>> {
      val elementType = Types.collectionElementType(type, MutableCollection::class.java)
      val elementAdapter: JsonAdapter<T> = moshi.adapter(elementType)
      return object : MutableCollectionJsonAdapter<MutableCollection<T>, T>(elementAdapter) {
        override fun newCollection(): MutableCollection<T> = ArrayList()
      }
    }
  }
}

class InstantJsonAdapter : JsonAdapter<Instant>() {

  @Throws(IOException::class)
  override fun fromJson(reader: JsonReader): Instant = reader.nextString().parse()

  @Throws(IOException::class)
  override fun toJson(writer: JsonWriter, instant: Instant?) { writer.value(instant?.toString()) }
}

@Module
interface MoshiModule {

  companion object {

    @Provides
    @Reusable
    fun moshi(): Moshi = Moshi.Builder()
      .add(Wrapped.ADAPTER_FACTORY)
      .add(FirstElement.ADAPTER_FACTORY)
      .add(MutableCollectionJsonAdapter.FACTORY)
      .add(Instant::class.java, InstantJsonAdapter())
      .build()
  }
}