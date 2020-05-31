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

package com.efemoney.obaranda.latest

import android.graphics.Color
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.efemoney.obaranda.R
import com.efemoney.obaranda.data.model.Comic
import com.efemoney.obaranda.databinding.ControllerLatestBinding
import com.efemoney.obaranda.databinding.GridItemBinding
import com.efemoney.obaranda.dispatchers.Dispatchers
import com.efemoney.obaranda.errors.SnackBarErrorTarget
import com.efemoney.obaranda.errors.ViewErrorTarget
import com.efemoney.obaranda.fragment.BaseFragment
import com.efemoney.obaranda.fragment.Inflater
import com.efemoney.obaranda.glide.GlideApp
import com.efemoney.obaranda.glide.GlideRequests
import com.efemoney.obaranda.ktx.*
import com.efemoney.obaranda.latest.Intents.*
import com.efemoney.obaranda.model.*
import com.efemoney.obaranda.plusAssign
import com.efemoney.obaranda.util.UiUtil.createRipple
import com.efemoney.obaranda.util.getSimpleRelativeDate
import com.efemoney.obaranda.widget.GridItemViewOutline
import com.efemoney.obaranda.widget.GridSpaceDecoration
import com.efemoney.obaranda.widget.ViewHolder
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar.make
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.recyclerview.scrollEvents
import javax.inject.Inject
import kotlin.time.milliseconds

class LatestFragment @Inject constructor(
  factory: Factory,
  private val dispatchers: Dispatchers,
  private val recycledViewPool: RecycledViewPool
) : BaseFragment<ControllerLatestBinding>() {

  private val viewModel: LatestViewModel by viewModels { factory }

  private lateinit var adapter: LatestAdapter
  private lateinit var snackTarget: SnackBarErrorTarget
  private lateinit var viewTarget: ViewErrorTarget

  override fun createBinding(inflater: Inflater, parent: ViewGroup?): ControllerLatestBinding {

    return ControllerLatestBinding.inflate(inflater, parent, false).apply {

      appBar.addOnOffsetChangedListener(
        AppBarLayout.OnOffsetChangedListener { _, offset ->

          // center of parent frame has moved offset / 2. We translate the title textview
          title.translationY = -offset.f / 2
        }
      )

      val glideRequests = GlideApp.with(this@LatestFragment)
      adapter = LatestAdapter(this@LatestFragment, dispatchers, glideRequests)
      val spanCount = requireContext().int(R.integer.grid_span_count)
      val preloader = RecyclerViewPreloader(glideRequests, adapter, ViewPreloadSizeProvider(), 4)

      grid.addOnScrollListener(preloader)
      grid.setRecycledViewPool(recycledViewPool)
      grid.addItemDecoration(GridSpaceDecoration(requireContext().dpToPx(16)))
      grid.layoutManager = GridLayoutManager(requireContext(), spanCount).apply {
        spanSizeLookup = AdapterSpanLookup(adapter, spanCount)
      }
      grid.adapter = adapter

      snackTarget = SnackBarErrorTarget(make(homeCoordinator, "", LENGTH_INDEFINITE))
      viewTarget = ViewErrorTarget(errorContainer)
    }
  }

  override fun bind() {

    viewModel.intents += LoadComics

    binding.grid.scrollEvents()
      .debounce(100.milliseconds)
      .filter { it.dy > 0 } // scrolling down
      .filter {
        with(it.view.layoutManager as GridLayoutManager) {
          findLastVisibleItemPosition() >= itemCount - (spanCount * 2 /* rows */)
        }
      }
      .onEach { viewModel.intents += LoadNextPage }
      .launchIn(this)

    viewModel.state.onEach { state ->
      with(state) {
        with(binding) {

          progress.isVisible = loading

          val errorTarget = if (comics.isEmpty()) viewTarget else snackTarget

          when (loadingError) {
            null -> errorTarget.visible(false)
            else -> {
              errorTarget.visible(true)
                .error(loadingError)
                .message(loadingError.message!!)
            }
          }

          adapter.items += buildList {
            comics.mapTo(this, ::ComicItem)
            if (loadingNextPageError != null) add(ErrorItem(loadingNextPageError))
            if (loadingNextPage) add(LoadingItem)
          }
        }
      }
    }.launchIn(this)
  }

  fun comicClicked(item: ComicItem) {
    viewModel.intents += ComicClicked(item.page)
  }

  fun comicCommentClicked(item: ComicItem) {
    d { "Comment clicked: $item" }
  }
}

class LatestAdapter constructor(
  private val controller: LatestFragment,
  dispatchers: Dispatchers,
  private val glideRequests: GlideRequests = GlideApp.with(controller),
  coroutineScope: CoroutineScope = controller
) : ItemsAdapter(controller.requireContext(), dispatchers, coroutineScope) {

  companion object ViewTypes {
    const val COMIC = R.layout.home_grid_item
  }

  override fun createItemViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Item> {

    if (viewType != COMIC) throw IllegalArgumentException("Unknown view type")

    @Suppress("UNCHECKED_CAST") return ComicHolder(
      GridItemBinding.inflate(inflater, parent, false),
      glideRequests,
      controller::comicClicked,
      controller::comicCommentClicked
    ) as ViewHolder<Item>
  }

  override fun preloadRequestBuilderFor(item: Item): RequestBuilder<*>? {
    return if (item !is ComicItem) null
    else glideRequests.asDrawable().load(item.previewImage)
  }

  override fun spanCountFor(item: Item, spanCount: Int) = if (item is ComicItem) 1 else spanCount
}

class ComicHolder(
  private val binding: GridItemBinding,
  private val glideRequests: GlideRequests,
  private val clickHandler: (ComicItem) -> Unit,
  private val commentClickHandler: (ComicItem) -> Unit
) : ViewHolder<ComicItem>(binding) {

  init {
    with(binding) {
      root.clipToOutline = true
      root.outlineProvider = GridItemViewOutline
      root.setOnClickListener { clickHandler(item) }
      commentCount.setOnClickListener { commentClickHandler(item) }
    }
  }

  private lateinit var item: ComicItem

  override fun bind(item: ComicItem) {
    this.item = item

    with(binding) {

      root.compatForeground = item.clickRipple

      image.transitionName = "image${item.page}"

      title.text = item.title
      commentCount.text = item.commentsCount.toString()
      relativeDate.text = item.date.getSimpleRelativeDate()

      glideRequests
        .asDrawable()
        .placeholder(item.placeholder)
        .load(item.previewImage)
        .into(image)
    }
  }

  override fun onRecycled() {
    glideRequests.clear(binding.image)
  }
}

data class ComicItem(private val comic: Comic) : Item, SupportsChange {
  override val id = comic.page.toLong()
  override val viewType = LatestAdapter.COMIC

  val page = comic.page
  val date = comic.pubDate
  val title = comic.title
  val previewImage = comic.images[0]
  val commentsCount = comic.commentsCount

  val placeholder = previewImage.palette.run {
    (muted ?: vibrant ?: Color.TRANSPARENT).toDrawable()
  }

  val clickRipple = previewImage.palette.run {
    (vibrant ?: muted)?.let { createRipple(it, 0.25f, false) }
  }

  override fun getChanges(n: Item) = buildChanges {

    if (n is ComicItem) when {
      date != n.date -> +"date";
      title != n.title -> +"title"
      previewImage != n.previewImage -> +"image"
      commentsCount != n.commentsCount -> +"comments count"
    }
  }
}
