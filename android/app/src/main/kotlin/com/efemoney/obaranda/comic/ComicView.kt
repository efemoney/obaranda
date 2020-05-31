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

package com.efemoney.obaranda.comic

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextPaint
import android.text.TextUtils
import android.text.TextUtils.TruncateAt.END
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.text.util.Linkify
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView.ScaleType.CENTER_CROP
import android.widget.ImageView.ScaleType.FIT_CENTER
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TextView.BufferType.SPANNABLE
import androidx.core.text.buildSpannedString
import androidx.core.text.getSpans
import androidx.core.text.inSpans
import androidx.core.text.toSpannable
import androidx.core.text.util.LinkifyCompat
import androidx.core.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeImageTransform
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.bumptech.glide.MemoryCategory
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.efemoney.obaranda.R
import com.efemoney.obaranda.data.model.Comment
import com.efemoney.obaranda.data.model.Image
import com.efemoney.obaranda.data.model.Media
import com.efemoney.obaranda.data.model.MediaType
import com.efemoney.obaranda.databinding.CommentItemBinding
import com.efemoney.obaranda.databinding.FragmentComicBinding
import com.efemoney.obaranda.databinding.NoCommentsItemBinding
import com.efemoney.obaranda.dispatchers.Dispatchers
import com.efemoney.obaranda.fragment.BaseFragment
import com.efemoney.obaranda.fragment.Inflater
import com.efemoney.obaranda.glide.GlideApp
import com.efemoney.obaranda.ktx.*
import com.efemoney.obaranda.model.*
import com.efemoney.obaranda.plusAssign
import com.efemoney.obaranda.util.getSimpleRelativeDate
import com.efemoney.obaranda.widget.*
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class ComicFragment @Inject constructor(
  factory: ViewModelProvider.Factory,
  dispatchers: Dispatchers
) : BaseFragment<FragmentComicBinding>() {

  private val args by navArgs<ComicFragmentArgs>()
  private val viewModel by viewModels<ComicViewModel> { factory }

  private val adapter = CommentAdapter(this, dispatchers)

  private lateinit var savedMemoryCategory: MemoryCategory

  private lateinit var postSheet: BottomSheetBehavior<LinearLayout>
  private lateinit var commentSheet: BottomSheetBehavior<LinearLayout>

  override fun onAttach(context: Context) {
    super.onAttach(context)
    savedMemoryCategory = GlideApp.get(context).setMemoryCategory(MemoryCategory.HIGH) // Tell glide to use more memory
  }

  override fun onDestroy() {
    super.onDestroy()
    GlideApp.get(requireContext()).setMemoryCategory(savedMemoryCategory) // Restore previously saved memory category
  }

  override fun createBinding(inflater: Inflater, parent: ViewGroup?): FragmentComicBinding {

    return FragmentComicBinding.inflate(inflater, parent, false).apply {

      previewImage.transitionName = "image${args.page}"

      previewImage.setOnClickListener { onTaps() }

      // calling setOnApplyWindowsInsets hides default coordinator layout behavior of passing
      // insets to children Behaviors but is needed for correct working of our toolbar and imageView
      // the alternative would be to create and add specific Behaviors for toolbar and imageView
      // and that seems overkill
      ViewCompat.setOnApplyWindowInsetsListener(comicCoordinator, GiveAllChildrenWindowInsets)

      commentInputPane.setOnApplyWindowInsetsListener { view, insets ->
        view.updatePadding(bottom = insets.systemWindowInsetBottom) // dodge navigation + keyboard
        insets
      }

      val sheetInsetsListener = View.OnApplyWindowInsetsListener { view, insets ->

        // manually pass top insets to the behaviors
        val behavior = view?.behavior<LinearChildWithScrollingViewBehavior>()
        behavior?.statusBarInset = insets.systemWindowInsetTop

        view.updatePadding(bottom = insets.systemWindowInsetBottom) // dodge navigation + keyboard

        insets
      }

      postPane.setOnApplyWindowInsetsListener(sheetInsetsListener)
      commentsPane.setOnApplyWindowInsetsListener(sheetInsetsListener)

      postSheet = postPane.behavior()
      postSheet.apply {
        addSlideCallback { _, slideOffset -> comicInfo.alpha = 1 - slideOffset }
        state = BottomSheetBehavior.STATE_HIDDEN
      }

      commentSheet = commentsPane.behavior()
      commentSheet.apply {
        addStateChangedCallback { _, newState ->
          when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> viewModel.intents += Intents.ShowComments
            BottomSheetBehavior.STATE_HIDDEN -> viewModel.intents += Intents.HideComments
          }
        }
        addSlideCallback { _, slideOffset ->
          comicInfo.alpha = 1 - slideOffset

          val inputHeight = commentInputPane.height.f
          val commentVisibleHeight = commentsPane.height * slideOffset
          commentInputPane.translationY = inputHeight - minOf(commentVisibleHeight, inputHeight)
        }
        state = BottomSheetBehavior.STATE_HIDDEN
      }

      postBody.movementMethod = LinkMovementMethod.getInstance()

      commentsList.layoutManager = LinearLayoutManager(context)
      commentsList.adapter = adapter

      commentInputPane.doOnLayout { it.translationY = it.height.f }
      commentInputPane.elevation = commentsPane.elevation + requireContext().dpToPxFloat(2)

      share.setOnClickListener { onShareClicked() }
      transcript.setOnClickListener { onReadMoreClicked() } // Todo eliminate
      commentCount.setOnClickListener { onCommentClicked() }
      toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }
  }

  override fun bind() {

    viewModel.intents += Intents.LoadComic(args.page)

    viewModel.state.onEach { state ->

      with(state) {
        with(binding) {

          if (comic == null) return@onEach // probably an error, nothing to display

          updateStrip(previewImage, comic.images)

          comic.post.title?.trim()?.let {
            postTitle.text = it
            postDetailsTitle.text = it
          }

          comic.post.body?.trim()?.let {
            postBody.setText(it, SPANNABLE)
            postDetailsBody.setText(it, SPANNABLE)
          }

          postBody.addReadMoreIfNecessary()

          relativeDate.text = comic.pubDate.getSimpleRelativeDate()
          commentCount.text = comic.commentsCount.toString()
          commentsTitle.text = (comic.commentsCount).let {
            if (it == 0)
              requireContext().string(R.string.comments)
            else
              requireContext().plural(R.plurals.comments_title, it, it)
          }

          val items = mutableListOf<Item>()

          if (loadingComments) items += LoadingItem
          if (comments != null) items += flattenComments(comments)
          if (loadingCommentsError != null) items += ErrorItem(loadingCommentsError)

          // If no comments and its not loading or has no error, then comments list is empty
          if (items.isEmpty()) items += NoCommentsItem

          adapter.items += items.toList()
        }
      }
    }.launchIn(this)
  }

  override fun handleBack() = hidePostDetails() || hideComments() || super.handleBack()

  private fun flattenComments(comments: List<Comment>): List<Item> {
    val items = mutableListOf<Item>()
    addAllDepthFirst(items, 0, comments)
    return items
  }

  private fun addAllDepthFirst(out: MutableList<Item>, indentLevel: Int, comments: List<Comment>) {
    for (comment in comments) {
      out += CommentItem(comment, indentLevel)
      addAllDepthFirst(out, indentLevel + 1, comment.children)
    }
  }

  private fun updateStrip(view: PhotoView, images: List<Image>) {

    // Optimization to avoid image loads on irrelevant state changes
    if ((view.drawable as? ComicStripDrawable)?.originalData == images) return

    view.post {
      val sizer = ViewSizer(view)
      val requests = GlideApp.with(this)

      val targets = images.map { ComicStripTarget(it, requests, sizer) }
      val drawable = ComicStripDrawable(targets, sizer, images)

      view.setImageDrawable(drawable)
      setNotImmersive()
    }
  }

  private fun hideComments(): Boolean {

    return if (commentSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
      commentSheet.state = BottomSheetBehavior.STATE_HIDDEN
      true
    } else {
      false
    }
  }

  private fun hidePostDetails(): Boolean {

    return if (postSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
      postSheet.state = BottomSheetBehavior.STATE_HIDDEN
      true
    } else {
      false
    }
  }

  private fun onTaps() {
    if (!hidePostDetails() && !hideComments()) toggleImmersiveUi()
  }

  private fun onShareClicked() {}

  private fun onCommentClicked() {
    commentSheet.state = BottomSheetBehavior.STATE_EXPANDED
  }

  private fun onReadMoreClicked() {
    postSheet.state = BottomSheetBehavior.STATE_EXPANDED
  }

  private fun toggleImmersiveUi() {

    with(binding) {

      // setup transitions
      TransitionManager.beginDelayedTransition(root as ViewGroup, transitionSet {
        +Fade()
          .addTarget(toolbar)
          .addTarget(comicInfo)
          .addTarget(comicScrim)

        +ChangeImageTransform()
          .addTarget(previewImage)
      })

      val uiFlags = root.systemUiVisibility
      val wasImmersive = uiFlags and View.SYSTEM_UI_FLAG_FULLSCREEN != 0

      if (wasImmersive) setNotImmersive() else setImmersive()
    }
  }

  private fun setImmersive() {

    with(binding) {
      previewImage.doOnLayout {

        root.systemUiVisibility = root.systemUiVisibility.withImmersiveFlags()

        // Fade out other views when entering or exiting immersive mode
        toolbar.isVisible = false
        comicInfo.isVisible = false
        comicScrim.isVisible = false

        val d = previewImage.drawable

        val dWidth = d.intrinsicWidth
        val dHeight = d.intrinsicHeight

        val vWidth = previewImage.width // no padding so we use only actual view width
        val vHeight = previewImage.height

        val wScale = vWidth.f / dWidth
        val hScale = vHeight.f / dHeight

        val wScaledHeight = wScale * dHeight
        val hScaledWidth = hScale * dWidth

        previewImage.scaleType = FIT_CENTER

        if (wScaledHeight > vHeight) {

          val attacher = previewImage.attacher

          val current = Matrix()
          attacher.setDisplayMatrix(current) // reset supp matrix
          attacher.getDisplayMatrix(current) // get base matrix

          val inverse = Matrix()
          current.invert(inverse)

          attacher.setDisplayMatrix(
            Matrix().apply {
              setScale(wScale, wScale)
              preConcat(inverse)
            }
          )

          val max = maxOf(3.0f, vWidth / hScaledWidth)
          attacher.setScaleLevels(1f, 1f + (0.75f * (max - 1f)), max)
        }
      }
    }
  }

  private fun setNotImmersive() {

    with(binding) {
      previewImage.doOnLayout {

        root.systemUiVisibility = root.systemUiVisibility.withoutImmersiveFlags()

        toolbar.isVisible = true
        comicInfo.isVisible = true
        comicScrim.isVisible = true

        val d = previewImage.drawable

        val dWidth = d.intrinsicWidth
        val dHeight = d.intrinsicHeight

        val vWidth = previewImage.width // no padding so we use only actual view width
        val vHeight = previewImage.height

        val wScale = vWidth.f / dWidth
        val hScale = vHeight.f / dHeight

        val wScaledHeight = wScale * dHeight
        val hScaledWidth = hScale * dWidth

        previewImage.scaleType = CENTER_CROP

        if (wScaledHeight > vHeight) {

          previewImage.scaleType = FIT_CENTER

          val attacher = previewImage.attacher

          val current = Matrix()
          attacher.setDisplayMatrix(current) // reset supp matrix
          attacher.getDisplayMatrix(current) // get base matrix

          val inverse = Matrix()
          current.invert(inverse)

          attacher.setDisplayMatrix(
            Matrix().apply {
              setScale(wScale, wScale)
              preConcat(inverse)
            }
          )

          val max = maxOf(3.0f, vWidth / hScaledWidth)
          attacher.setScaleLevels(1f, 1f + (0.75f * (max - 1f)), max)
        }
      }
    }
  }

  private fun TextView.addReadMoreIfNecessary() {

    doOnLayout {

      val moreText = "Read More"
      val maxLines = 5

      if (lineCount <= maxLines) return@doOnLayout
      // layout is non null at this point

      val clickSpan = object : ClickableSpan() {
        override fun onClick(widget: View) = onReadMoreClicked()
        override fun updateDrawState(ds: TextPaint) = ds.setColor(ds.linkColor)
      }

      val availableWidth = layout.width.f - layout.paint.measureText(moreText)

      val start = layout.getLineStart(maxLines - 1)
      val end = layout.getLineEnd(maxLines - 1)

      val maxLinesText = layout.text[start..end]

      val ellipsizedText = TextUtils.ellipsize(maxLinesText, layout.paint, availableWidth, END)

      val newText = buildSpannedString {
        append(layout.text[0..start])
        append(ellipsizedText)
        inSpans(clickSpan) { append(moreText) }
      }

      post /* Runnable */ { setText(newText, SPANNABLE) }
    }
  }

  fun commentClicked(item: CommentItem) = Unit
}


class CommentAdapter constructor(
  private val controller: ComicFragment,
  dispatchers: Dispatchers,
  coroutineScope: CoroutineScope = controller
) : ItemsAdapter(controller.requireContext(), dispatchers, coroutineScope) {

  override fun createItemViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Item> {

    @Suppress("UNCHECKED_CAST") return when (viewType) {
      NO_COMMENT -> NoCommentsHolder(NoCommentsItemBinding.inflate(inflater, parent, false))
      COMMENT -> CommentHolder(CommentItemBinding.inflate(inflater, parent, false), controller::commentClicked)
      else -> throw IllegalArgumentException("Unknown view type")
    } as ViewHolder<Item>
  }

  companion object ViewTypes {
    const val COMMENT = R.layout.comment_item
    const val NO_COMMENT = R.layout.no_comments_item
  }
}


class CommentHolder(
  private val binding: CommentItemBinding,
  private val clickHandler: (CommentItem) -> Unit
) : ViewHolder<CommentItem>(binding) {

  init {
    binding.root.setOnClickListener { clickHandler(item) }
    binding.message.movementMethod = MediaLinkMovementMethod
  }

  private lateinit var item: CommentItem

  private val placeholder: Drawable by lazy(NONE) {
    val context = binding.root.context

    GradientDrawable().apply {
      shape = GradientDrawable.RECTANGLE
      color = ColorStateList.valueOf(context.color(R.color.pale_grey))
      cornerRadius = context.dpToPxFloat(2)
      setStroke(context.dpToPx(1), context.color(R.color.silver))
    }
  }

  override fun bind(item: CommentItem) {
    this.item = item

    with(binding) {

      val context = root.context
      val glideRequests = GlideApp.with(binding.root.context)

      item.avatarUrl?.let {
        glideRequests
          .load(it)
          .circleCrop()
          .into(avatar)
      }

      upvote.text = item.up.toString()
      downvote.text = item.down.toString()

      message.text = item.text
      displayName.text = item.displayName

      childCommentIndent.updateLayoutParams {
        width = item.indentMultiplier * context.dimenPx(R.dimen.child_comment_indent_space)
      }

      for (loadTarget in item.loadTargets) {
        glideRequests
          .load(loadTarget.previewUrl)
          .placeholder(placeholder)
          .transform(RoundedCorners(context.dpToPx(2)))
          .into(loadTarget)
      }
    }
  }

  override fun onRecycled() {
    val glideRequests = GlideApp.with(binding.root.context)

    glideRequests.clear(binding.avatar)
    for (loadTarget in item.loadTargets) glideRequests.clear(loadTarget)
  }
}

class NoCommentsHolder(binding: NoCommentsItemBinding) : ViewHolder<NoCommentsItem>(binding) {
  override fun bind(item: NoCommentsItem) = Unit
}


data class CommentItem(
  private val comment: Comment,
  private val indentLevel: Int
) : Item, SupportsChange {
  override val id = comment.id
  override val viewType = CommentAdapter.COMMENT

  val loadTargets = mutableListOf<MediaSpan>()
  val displayName = comment.displayName
  val avatarUrl = comment.avatar
  val up = comment.upvoteCount
  val down = comment.downvoteCount
  val text = parseText(comment.rawText, comment.media)
  val indentMultiplier = indentLevel

  private fun parseText(rawText: String, media: List<Media>): CharSequence {

    val s = rawText.toSpannable()

    if (!LinkifyCompat.addLinks(s, Linkify.WEB_URLS)) return rawText

    val mediaMap = media.associateBy { it.url }

    for (urlSpan in s.getSpans<URLSpan>()) {
      val spanMedia = mediaMap[urlSpan.url] ?: continue

      with(spanMedia) {
        if (previewUrl.isNotEmpty() && previewWidth > 0 && previewHeight > 0) {
          // comment has image preview, replace the span

          val start = s.getSpanStart(urlSpan)
          val end = s.getSpanEnd(urlSpan)
          val flags = s.getSpanFlags(urlSpan)

          val mediaSpan = MediaSpan(previewUrl, previewWidth, previewHeight)
          loadTargets += mediaSpan

          s.setSpan(mediaSpan, start, end, flags)

          // Only videos need to retain their click behavior, images and gifs will display inline
          if (spanMedia.type != MediaType.video) s.removeSpan(urlSpan)
        }
      }
    }

    return s
  }

  override fun getChanges(n: ObarandaDisplayItem): Changes? {

    return buildChanges {
      if (n !is CommentItem) return@buildChanges
    }
  }
}

object NoCommentsItem : Item {
  override val id = R.layout.no_comments_item.toLong()
  override val viewType = CommentAdapter.NO_COMMENT
}


private inline fun Int.withImmersiveFlags(): Int {
  return this or
      View.SYSTEM_UI_FLAG_FULLSCREEN or
      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
      View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
}

private inline fun Int.withoutImmersiveFlags(): Int {
  return this and
      View.SYSTEM_UI_FLAG_FULLSCREEN.inv() and
      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv() and
      View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
}
