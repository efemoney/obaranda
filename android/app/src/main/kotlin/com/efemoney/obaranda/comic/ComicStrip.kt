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

package com.efemoney.obaranda.comic

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import android.view.WindowManager
import androidx.core.content.getSystemService
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.toRect
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.efemoney.obaranda.data.model.Image
import com.efemoney.obaranda.glide.GlideRequests
import com.efemoney.obaranda.ktx.f
import java.lang.ref.WeakReference
import java.util.*

/**
 * Copy of [com.bumptech.glide.request.target.CustomViewTarget.SizeDeterminer]
 *
 * This class aims to load comic images scaled to a lower dimension when the View width is
 * less than the comic width but at a maximum of comic width when the View width is larger
 */
class ViewSizer(private val view: View) {

  private val cbs = ArrayList<SizeReadyCallback>()

  private var layoutListener: SizeDeterminerPreDrawListener? = null

  private val targetWidth: Int
    get() {
      val padding = view.paddingLeft + view.paddingRight
      val layoutParamSize = view.layoutParams?.width ?: PENDING_SIZE

      return getTargetDimen(view.width, layoutParamSize, padding)
    }

  private val targetHeight: Int
    get() {
      val padding = view.paddingTop + view.paddingBottom
      val layoutParamSize = view.layoutParams?.height ?: PENDING_SIZE

      return getTargetDimen(view.height, layoutParamSize, padding)
    }

  private fun notifyCbs(width: Int, height: Int) {
    // One or more callbacks may trigger the removal of one or more additional callbacks, so we
    // need a copy of the list to avoid a concurrent modification exception. One place this
    // happens is when a full request completes from the in memory cache while its thumbnail is
    // still being loaded asynchronously. See #2237.
    for (cb in ArrayList(cbs)) {
      cb.onSizeReady(width, height)
    }
  }

  fun checkCurrentDimens() {

    if (cbs.isEmpty()) return

    val currentWidth = targetWidth
    val currentHeight = targetHeight

    if (!isViewStateAndSizeValid(currentWidth, currentHeight)) return

    notifyCbs(currentWidth, currentHeight)

    clearCallbacksAndListener()
  }

  fun getSize(cb: SizeReadyCallback) {
    val currentWidth = targetWidth
    val currentHeight = targetHeight

    if (isViewStateAndSizeValid(currentWidth, currentHeight)) {
      cb.onSizeReady(currentWidth, currentHeight)
      return
    }

    // We want to notify callbacks in the order they were added and we only expect one or two
    // callbacks to be added a time, so a List is a reasonable choice.
    if (!cbs.contains(cb)) cbs.add(cb)

    if (layoutListener == null) {
      layoutListener = SizeDeterminerPreDrawListener(this)
      view.viewTreeObserver.addOnPreDrawListener(layoutListener)
    }
  }

  fun removeCallback(cb: SizeReadyCallback) {
    cbs.remove(cb)
  }

  fun clearCallbacksAndListener() {

    with(view.viewTreeObserver) { if (isAlive) removeOnPreDrawListener(layoutListener) }

    layoutListener = null

    cbs.clear()
  }

  private fun getTargetDimen(viewSize: Int, paramSize: Int, paddingSize: Int): Int {

    /*
    We consider the View state as valid if the View has non-null layout params and a non-zero
    layout params width and height. This is imperfect. We're making an assumption that View
    parents will obey their child's layout parameters, which isn't always the case.
    */
    val adjustedParamSize = paramSize - paddingSize
    if (adjustedParamSize > 0) return adjustedParamSize

    /*
    Since we always prefer layout parameters with fixed sizes, even if waitForLayout is true,
    we might as well ignore it and just return the layout parameters above if we have them.
    Otherwise we should wait for a layout pass before checking the View's dimensions.
    */
    if (view.isLayoutRequested) return PENDING_SIZE

    /*
    We also consider the View state valid if the View has a non-zero width and height. This
    means that the View has gone through at least one layout pass. It does not mean the Views
    width and height are from the current layout pass. For example, if a View is re-used in
    RecyclerView or ListView, this width/height may be from an old position. In some cases
    the dimensions of the View at the old position may be different than the dimensions of the
    View in the new position because the LayoutManager/ViewParent can arbitrarily decide to
    change them. Nevertheless, in most cases this should be a reasonable choice.
    */
    val adjustedViewSize = viewSize - paddingSize
    if (adjustedViewSize > 0) return adjustedViewSize

    /*
    Finally we consider the view valid if the layout parameter size is set to wrap_content.
    It's difficult for Glide to figure out what to do here. Although Target.SIZE_ORIGINAL is a
    coherent choice, it's extremely dangerous because original images may be much too large to
    fit in memory or so large that only a couple can fit in memory, causing OOMs. If users want
    the original image, they can always use .override(Target.SIZE_ORIGINAL). Since wrap_content
    may never resolve to a real size unless we load something, we aim for a square whose length
    is the largest screen size. That way we're loading something and that something has some
    hope of being downsampled to a size that the device can support. We also log a warning that
    tries to explain what Glide is doing and why some alternatives are preferable.
    Since WRAP_CONTENT is sometimes used as a default layout parameter, we always wait for
    layout to complete before using this fallback parameter (ConstraintLayout among others).
    */
    if (!view.isLayoutRequested && paramSize == ViewGroup.LayoutParams.WRAP_CONTENT)
      return getMaxDisplayLength(view.context)

    /*
    If the layout parameters are < padding, the view size is < padding, or the layout
    parameters are set to match_parent or wrap_content and no layout has occurred, we should
    wait for layout and repeat.
    */
    return PENDING_SIZE
  }

  private fun isViewStateAndSizeValid(width: Int, height: Int) =
    width.isValidSize() && height.isValidSize()

  private fun Int.isValidSize() = this > 0 || this == Target.SIZE_ORIGINAL

  private class SizeDeterminerPreDrawListener(sizer: ViewSizer) : OnPreDrawListener {

    private val sizeDeterminerRef = WeakReference(sizer)

    override fun onPreDraw(): Boolean {
      sizeDeterminerRef.get()?.checkCurrentDimens()
      return true
    }
  }

  companion object {

    // Some negative sizes (Target.SIZE_ORIGINAL) are valid, 0 is never valid.
    private const val PENDING_SIZE = 0

    private var maxDisplayLength: Int = -1

    // Use the maximum to avoid depending on the device's current orientation.
    private fun getMaxDisplayLength(context: Context): Int {

      if (maxDisplayLength == -1) {
        val (x, y) = Point().apply {
          context.getSystemService<WindowManager>()!!.defaultDisplay.getSize(this)
        }
        maxDisplayLength = maxOf(x, y)
      }

      return maxDisplayLength
    }
  }
}

class ComicStripTarget(image: Image, glideRequests: GlideRequests, sizer: ViewSizer) {

  private val aspectRatio = with(image.size) { width.f / height.f }
  private val placeholder = ColorDrawable(image.palette.muted ?: 0)

  private var bounds: Rect? = null
  private var drawable: Drawable? = null
  private var callback: Callback? = null

  init {

    val glideTarget = object : Target<Drawable> {

      private var request: Request? = null
      private var animatable: Animatable? = null

      override fun getSize(cb: SizeReadyCallback) {
        sizer.getSize(cb)
      }

      override fun removeCallback(cb: SizeReadyCallback) {
        sizer.removeCallback(cb)
      }

      override fun onLoadCleared(placeholder: Drawable?) {
        sizer.clearCallbacksAndListener()
      }

      override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        onResourceLoaded(resource)

        animatable = resource as? Animatable
        animatable?.start()
      }

      override fun onStart() {
        animatable?.start()
      }

      override fun onStop() {
        animatable?.stop()
      }

      override fun onLoadStarted(placeholder: Drawable?) = Unit
      override fun onLoadFailed(errorDrawable: Drawable?) = Unit
      override fun onDestroy() = Unit
      override fun getRequest(): Request? = request
      override fun setRequest(request: Request?) {
        this.request = request
      }
    }

    glideRequests
      .asDrawable()
      .load(image)
      .into(glideTarget)
  }

  fun setBounds(bounds: Rect) {
    this.bounds = bounds
    getDrawable().bounds = bounds
  }

  fun setCallback(callback: Callback) {
    this.callback = callback
    getDrawable().callback = callback
  }

  fun getDrawable() = drawable ?: placeholder

  fun getDrawableAspectRatio() = aspectRatio

  private fun onResourceLoaded(resource: Drawable) {

    bounds?.let { resource.bounds = it }

    callback?.let { resource.callback = it }

    drawable = resource

    callback?.invalidateDrawable(getDrawable()) // invalidate
  }
}

class ComicStripDrawable(
  private val targets: List<ComicStripTarget>,
  private val sizer: ViewSizer,
  val originalData: List<Image>
) : Drawable(), SizeReadyCallback {

  private val callback = object : Callback {
    override fun invalidateDrawable(d: Drawable) = invalidateSelf()
    override fun scheduleDrawable(d: Drawable, what: Runnable, at: Long) = scheduleSelf(what, at)
    override fun unscheduleDrawable(d: Drawable, what: Runnable) = unscheduleSelf(what)
  }

  /* Calculated width and height */
  private var width: Int = -1
  private var height: Int = -1

  init {
    sizer.getSize(this)
  }

  override fun onSizeReady(width: Int, height: Int) {
    calculateDimen(width)
    invalidateSelf()
    sizer.removeCallback(this)
  }

  private fun calculateDimen(workingWidth: Int) {

    // Assume we have no intrinsic dimensions first
    var w = 0
    var h = 0

    for (t in targets) {
      val d = t.getDrawable()
      val ar = t.getDrawableAspectRatio()

      var dw = d.intrinsicWidth
      var dh = d.intrinsicHeight

      if (dw == -1) {
        dw = workingWidth

        if (dh != -1 && ar != 1f) throw Exception("drawable height and width must both be -1")
        dh = (workingWidth / ar).toInt()
      }

      w = maxOf(w, dw)
      h += dh

      t.setCallback(callback)
    }

    width = w
    height = h
  }

  override fun draw(canvas: Canvas) {
    for (i in 0 until targets.size) {
      targets[i].getDrawable().draw(canvas)
    }
  }

  override fun getIntrinsicHeight() = height

  override fun getIntrinsicWidth() = width

  override fun getOpacity(): Int = PixelFormat.OPAQUE

  override fun setAlpha(alpha: Int) = Unit

  override fun setColorFilter(colorFilter: ColorFilter?) = Unit

  override fun onBoundsChange(bounds: Rect) {

    var dy = 0f
    val tempRectF = RectF()

    for (target in targets) {

      val boundedWidth = bounds.width().f
      val boundedHeight = boundedWidth / target.getDrawableAspectRatio()

      tempRectF.set(0f, dy, boundedWidth, boundedHeight + dy)

      target.setBounds(tempRectF.toRect())

      dy += boundedHeight
    }
  }
}