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

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ReplacementSpan
import android.widget.TextView
import androidx.core.graphics.withSave
import androidx.core.text.getSpans
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.efemoney.obaranda.ktx.f

/**
 * A special [LinkMovementMethod] that finds [MediaSpan]s in a textView and sets their
 * [MediaSpan.drawablesCallback] to invalidate (and redraw) the textView
 */
object MediaLinkMovementMethod : LinkMovementMethod() {

  override fun initialize(widget: TextView, text: Spannable) {
    super.initialize(widget, text)

    for (span in text.getSpans<MediaSpan>()) {

      if (widget == span.drawablesCallback?.widget) continue

      span.drawablesCallback = Callback(widget)
    }
  }

  class Callback(val widget: TextView) : Drawable.Callback {

    override fun invalidateDrawable(who: Drawable) {
      widget.setText(widget.text, TextView.BufferType.SPANNABLE)
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, atWhen: Long) {
      widget.postDelayed(what, atWhen)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
      widget.removeCallbacks(what)
    }
  }
}

class MediaSpan(
  val previewUrl: String,
  private val previewWidth: Int,
  private val previewHeight: Int
) : ReplacementSpan(), Target<Drawable> {

  private val bounds = Rect(0, 0, previewWidth, previewHeight)
  private val drawable: Drawable get() = (image ?: error ?: placeholder ?: throw Exception())

  private var request: Request? = null

  private var placeholder: Drawable? = null
  private var error: Drawable? = null
  private var image: Drawable? = null

  var drawablesCallback: MediaLinkMovementMethod.Callback? = null

  override fun getSize(cb: SizeReadyCallback) = cb.onSizeReady(previewWidth, previewHeight)

  override fun removeCallback(cb: SizeReadyCallback) = Unit

  override fun onLoadStarted(placeholder: Drawable?) {
    this.placeholder = placeholder.applyBoundsAndCallback()
    invalidate()
  }

  override fun onLoadFailed(errorDrawable: Drawable?) {
    this.error = errorDrawable.applyBoundsAndCallback()
    invalidate()
  }

  override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
    this.image = resource.applyBoundsAndCallback()
    invalidate()

    (drawable as? Animatable)?.start()
  }

  override fun onLoadCleared(placeholder: Drawable?) {
    this.placeholder = placeholder.applyBoundsAndCallback()
    this.error = null
    this.image = null

    invalidate()
  }

  private fun invalidate() = drawable.invalidateSelf()

  private inline fun Drawable?.applyBoundsAndCallback() = apply {
    this?.bounds = bounds
    this?.callback = drawablesCallback
  }

  override fun setRequest(request: Request?) {
    this.request = request
  }

  override fun getRequest() = request

  override fun onStart() {
    (drawable as? Animatable)?.start()
  }

  override fun onStop() {
    (drawable as? Animatable)?.stop()
  }

  override fun onDestroy() = Unit

  override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
    val d = drawable
    val rect = d.bounds

    if (fm != null) {
      fm.ascent = -rect.bottom
      fm.descent = 0

      fm.top = fm.ascent
      fm.bottom = 0
    }

    return rect.right
  }

  private val fmi: Paint.FontMetricsInt = Paint.FontMetricsInt() // to avoid object creation

  override fun draw(
    canvas: Canvas, text: CharSequence,
    start: Int, end: Int, x: Float,
    top: Int, y: Int, bottom: Int, paint: Paint
  ) {
    val d = drawable
    paint.getFontMetricsInt(fmi)

    canvas.withSave {
      val transY = bottom - d.bounds.bottom - fmi.descent
      translate(x, transY.f)
      d.draw(canvas)
    }
  }
}
