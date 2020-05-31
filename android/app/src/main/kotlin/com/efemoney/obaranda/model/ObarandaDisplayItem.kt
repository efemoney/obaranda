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

@file:Suppress("UNCHECKED_CAST")

package com.efemoney.obaranda.model

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.RequestBuilder
import com.efemoney.obaranda.R
import com.efemoney.obaranda.databinding.ErrorItemBinding
import com.efemoney.obaranda.databinding.LoadingItemBinding
import com.efemoney.obaranda.dispatchers.Dispatchers
import com.efemoney.obaranda.errors.ViewErrorTarget
import com.efemoney.obaranda.model.ItemsAdapter.ViewTypes.ERROR
import com.efemoney.obaranda.model.ItemsAdapter.ViewTypes.LOADING
import com.efemoney.obaranda.widget.ViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

typealias Item = ObarandaDisplayItem

typealias Items = List<ObarandaDisplayItem>

interface ObarandaDisplayItem {
  val id: Long
  val viewType: Int
}

object LoadingItem : Item {
  override val viewType = LOADING
  override val id = -1L
}

data class ErrorItem(val error: Throwable) : Item, SupportsChange {
  override val viewType = ERROR
  override val id = -2L
  override fun getChanges(n: Item): Changes? {
    return buildChanges { if (n is ErrorItem && error != n.error) +"error" }
  }
}


class ErrorHolder(binding: ErrorItemBinding) : ViewHolder<ErrorItem>(binding) {
  private val target = ViewErrorTarget(binding.root)

  override fun bind(item: ErrorItem) {
    target
      .visible(true)
      .error(item.error)
      .message(item.error.message!!)
  }
}

class LoadingHolder(binding: LoadingItemBinding) : ViewHolder<LoadingItem>(binding) {
  override fun bind(item: LoadingItem) = Unit
}

class AdapterSpanLookup(
  private val adapter: RecyclerView.Adapter<*>,
  private val spanCount: Int
) : GridLayoutManager.SpanSizeLookup() {

  override fun getSpanSize(position: Int): Int {

    return (adapter as? ItemSpanLookup)?.getItemSpanSize(position, spanCount) ?: spanCount
  }
}

interface ItemSpanLookup {

  fun getItemSpanSize(position: Int, spanCount: Int): Int
}

class ItemDiffCallback(private val old: Items, private val new: Items) : Callback() {

  override fun getOldListSize(): Int = old.size

  override fun getNewListSize(): Int = new.size

  override fun areItemsTheSame(oldPos: Int, newPos: Int) = old[oldPos].id == new[newPos].id

  override fun areContentsTheSame(oldPos: Int, newPos: Int) = old[oldPos] == new[newPos]

  override fun getChangePayload(oldPos: Int, newPos: Int): Any? {
    val o = old[oldPos]
    val n = new[newPos]

    return (o as? SupportsChange)?.getChanges(n)
  }
}

abstract class ItemsAdapter constructor(
  context: Context,
  dispatchers: Dispatchers,
  scope: CoroutineScope
) : ItemSpanLookup, PreloadModelProvider<Item>, RecyclerView.Adapter<ViewHolder<Item>>() {

  protected val inflater: LayoutInflater = LayoutInflater.from(context)

  val items: Channel<Items> = Channel()
  private var _items: Items = emptyList()

  init {
    setHasStableIds(true)
    scope.launch {
      val initialPair = emptyList<Item>() to (null as DiffResult?)
      items.receiveAsFlow()
        .scan(initialPair) { (oldList, _), newList ->
          newList to calculateDiff(ItemDiffCallback(oldList, newList))
        }
        .flowOn(dispatchers.computation)
        .collect {
          _items = it.first
          it.second?.dispatchUpdatesTo(this@ItemsAdapter)
        }
    }
  }

  override fun getItemCount() = _items.size

  override fun getItemId(position: Int) = _items[position].id

  override fun getItemViewType(position: Int) = _items[position].viewType

  override fun getItemSpanSize(position: Int, spanCount: Int): Int {

    return _items[position].run {
      when (viewType) {
        ERROR, LOADING -> spanCount
        else -> spanCountFor(this, spanCount)
      }
    }
  }

  override fun getPreloadItems(position: Int): Items {

    return _items.run {
      val wanted = minOf(lastIndex, position + preloadCount())
      this[position..wanted]
    }
  }

  override fun getPreloadRequestBuilder(item: Item): RequestBuilder<*>? {

    return when (item.viewType) {
      ERROR, LOADING -> null
      else -> preloadRequestBuilderFor(item)
    }
  }

  open fun spanCountFor(item: Item, spanCount: Int) = spanCount

  open fun preloadCount(): Int = 4

  open fun preloadRequestBuilderFor(item: Item): RequestBuilder<*>? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Item> {

    @Suppress("UNCHECKED_CAST")
    return when (viewType) {
      ERROR -> ErrorHolder(ErrorItemBinding.inflate(inflater, parent, false))
      LOADING -> LoadingHolder(LoadingItemBinding.inflate(inflater, parent, false))
      else -> createItemViewHolder(parent, viewType)
    } as ViewHolder<Item>
  }

  override fun onBindViewHolder(holder: ViewHolder<Item>, position: Int, payloads: List<Any>) {
    when (payloads.size) {
      0 -> onBindViewHolder(holder, position)
      else -> onBindViewHolderChanges(holder, position, payloads as List<Changes>)
    }
  }

  override fun onBindViewHolder(holder: ViewHolder<Item>, position: Int) {
    holder.bind(_items[position])
  }

  protected fun onBindViewHolderChanges(holder: ViewHolder<Item>, position: Int, changesList: List<Changes>) {
    changesList.forEach {
      holder.bindChanges(_items[position], it)
    }
  }

  override fun onViewRecycled(holder: ViewHolder<Item>) = holder.onRecycled()

  abstract fun createItemViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Item>

  companion object ViewTypes {
    const val LOADING = R.layout.loading_item
    const val ERROR = R.layout.error_item
  }
}


operator fun <E : Any> List<E>.get(range: IntRange) = subList(range.first, range.last)


