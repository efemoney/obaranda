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

package com.efemoney.obaranda.transition

import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat.SRC_IN
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withSave
import com.efemoney.obaranda.ktx.f
import kotlin.math.roundToInt

/**
 * Adapted from https://github.com/googlesamples/android-unsplash/blob/master/app/src/main/java/com/example/android/unsplash/transition/TextResize.java
 *
 * Transitions a TextView from one font size to another. This does not
 * do any animation of TextView content and if the text changes, this
 * transition will not run.
 *
 * The animation works by capturing a bitmap of the text at the start
 * and end states. It then scales the start bitmap until it reaches
 * a threshold and switches to the scaled end bitmap for the remainder
 * of the animation. This keeps the jump in bitmaps in the middle of
 * the animation, where it is less noticeable than at the beginning
 * or end of the animation. This transition does not work well with
 * cropped text. TextResize also does not work with changes in
 * TextView gravity.
 */
class TextResize : Transition {

  constructor() : super() {
    addTarget(TextView::class.java)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    addTarget(TextView::class.java)
  }

  override fun getTransitionProperties(): Array<String> = PROPERTIES

  override fun captureStartValues(values: TransitionValues) = captureValues(values)

  override fun captureEndValues(values: TransitionValues) = captureValues(values)

  private fun captureValues(transitionValues: TransitionValues) {

    if (transitionValues.view !is TextView) return

    val view = transitionValues.view as TextView

    transitionValues.values[DATA] = TextResizeData(view)
    transitionValues.values[FONT_SIZE] = view.textSize
  }

  override fun createAnimator(
    sceneRoot: ViewGroup,
    startValues: TransitionValues?,
    endValues: TransitionValues?
  ): Animator? {

    if (startValues == null || endValues == null) return null

    val startData = startValues.values[DATA] as TextResizeData
    val endData = endValues.values[DATA] as TextResizeData

    if (startData.gravity != endData.gravity) return null // Can't deal with changes in gravity

    val textView = endValues.view as TextView

    var startFontSize = startValues.values[FONT_SIZE] as Float
    setTextViewData(textView, startData, startFontSize) // we need to set the start values first

    val startWidth = textView.paint.measureText(textView.text.toString())
    val startBitmap = captureTextBitmap(textView) // Capture the start bitmap
    if (startBitmap == null) startFontSize = 0f

    var endFontSize = endValues.values[FONT_SIZE] as Float
    setTextViewData(textView, endData, endFontSize) // Set the end values

    val endWidth = textView.paint.measureText(textView.text.toString())
    val endBitmap = captureTextBitmap(textView) // Capture the end bitmap
    if (endBitmap == null) endFontSize = 0f

    if (startFontSize == 0f || endFontSize == 0f) return null // Can't animate null bitmaps

    // Set the colors of the TextView so that nothing is drawn.
    // Only draw the bitmaps in the overlay.
    val textColors = textView.textColors
    val hintColors = textView.hintTextColors
    val linkColors = textView.linkTextColors
    val highlightColor = textView.highlightColor

    textView.setTextColor(Color.TRANSPARENT)
    textView.setHintTextColor(Color.TRANSPARENT)
    textView.setLinkTextColor(Color.TRANSPARENT)
    textView.highlightColor = Color.TRANSPARENT

    // Create the drawable that will be animated in the TextView's overlay.
    // Ensure that it is showing the start state now.
    val drawable = SwitchBitmapDrawable(
      textView,
      startData.gravity,
      startBitmap!!,
      startFontSize,
      startWidth,
      endBitmap!!,
      endFontSize,
      endWidth
    )

    textView.overlay.add(drawable)

    // Properties: left, top, font size, text color
    val leftProp = PropertyValuesHolder.ofFloat("left", startData.paddingLeft.f, endData.paddingLeft.f)

    val topProp = PropertyValuesHolder.ofFloat("top", startData.paddingTop.f, endData.paddingTop.f)

    val rightProp = PropertyValuesHolder.ofFloat(
      "right",
      startData.width.f - startData.paddingRight,
      endData.width.f - endData.paddingRight
    )

    val bottomProp = PropertyValuesHolder.ofFloat(
      "bottom",
      startData.height.f - startData.paddingBottom,
      endData.height.f - endData.paddingBottom
    )

    val fontSizeProp = PropertyValuesHolder.ofFloat("fontSize", startFontSize, endFontSize)

    val animator: ObjectAnimator = if (startData.textColor != endData.textColor) {

      val textColorProp =
        PropertyValuesHolder.ofObject("textColor", ArgbEvaluator(), startData.textColor, endData.textColor)

      ObjectAnimator.ofPropertyValuesHolder(
        drawable, leftProp, topProp, rightProp, bottomProp, fontSizeProp, textColorProp
      )

    } else {

      ObjectAnimator.ofPropertyValuesHolder(drawable, leftProp, topProp, rightProp, bottomProp, fontSizeProp)
    }

    val finalFontSize = endFontSize

    val listener = object : AnimatorListenerAdapter() {

      override fun onAnimationPause(animation: Animator) {
        val fraction = animator.animatedFraction

        val paddingLeft = drawable.left.roundToInt()
        val paddingTop = drawable.top.roundToInt()
        val paddingRight = interpolate(startData.paddingRight.f, endData.paddingRight.f, fraction).roundToInt()
        val paddingBottom = interpolate(startData.paddingBottom.f, endData.paddingBottom.f, fraction).roundToInt()

        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, drawable.fontSize)
        textView.setTextColor(drawable.textColor)
      }

      override fun onAnimationResume(animation: Animator) {
        textView.setPadding(endData.paddingLeft, endData.paddingTop, endData.paddingRight, endData.paddingBottom)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, finalFontSize)
        textView.setTextColor(endData.textColor)
      }

      override fun onAnimationEnd(animation: Animator) {
        textView.overlay.remove(drawable)

        textView.setTextColor(textColors)
        textView.setHintTextColor(hintColors)
        textView.setLinkTextColor(linkColors)
        textView.highlightColor = highlightColor
      }
    }
    animator.addListener(listener)
    animator.addPauseListener(listener)

    return animator
  }

  /**
   * This Drawable is used to scale the start and end bitmaps and switch between them
   * at the appropriate progress.
   */
  private class SwitchBitmapDrawable(
    private val view: TextView,
    gravity: Int,
    private val startBitmap: Bitmap,
    private val startFontSize: Float,
    private val startWidth: Float,
    private val endBitmap: Bitmap,
    private val endFontSize: Float,
    private val endWidth: Float
  ) : Drawable() {

    private val horizontalGravity = gravity and Gravity.HORIZONTAL_GRAVITY_MASK
    private val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK

    private val paint = Paint()

    var fontSize: Float = 0f
      /**
       * @return The font size of the text in the displayed bitmap.
       */
      get
      /**
       * Sets the font size that the text should be displayed at.
       *
       * @param fontSize The font size in pixels of the scaled bitmap text.
       */
      set(fontSize) {
        field = fontSize
        invalidateSelf()
      }

    var left: Float = 0f
      /**
       * @return The left side of the text.
       */
      get
      /**
       * Sets the left side of the text. This should be the same as the left padding.
       *
       * @param left The left side of the text in pixels.
       */
      set(left) {
        field = left
        invalidateSelf()
      }

    var top: Float = 0f
      /**
       * @return The top of the text.
       */
      get
      /**
       * Sets the top of the text. This should be the same as the top padding.
       *
       * @param top The top of the text in pixels.
       */
      set(top) {
        field = top
        invalidateSelf()
      }

    var right: Float = 0f
      /**
       * @return The right side of the text.
       */
      get
      /**
       * Sets the right of the drawable.
       *
       * @param right The right pixel of the drawn area.
       */
      set(right) {
        field = right
        invalidateSelf()
      }

    var bottom: Float = 0f
      /**
       * @return The bottom of the text.
       */
      get
      /**
       * Sets the bottom of the drawable.
       *
       * @param bottom The bottom pixel of the drawn area.
       */
      set(bottom) {
        field = bottom
        invalidateSelf()
      }

    var textColor: Int = 0
      /**
       * @return The color of the text being displayed.
       */
      get
      /**
       * Sets the color of the text to be displayed.
       *
       * @param textColor The color of the text to be displayed.
       */
      set(textColor) {
        field = textColor
        colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(textColor, SRC_IN)
        invalidateSelf()
      }

    override fun invalidateSelf() {
      super.invalidateSelf()
      view.invalidate()
    }

    override fun draw(canvas: Canvas) {
      canvas.withSave {

        // The threshold changes depending on the target font sizes. Because scaled-up
        // fonts look bad, we want to switch when closer to the smaller font size. This
        // algorithm ensures that null bitmaps (font size = 0) are never used.
        val threshold = startFontSize / (startFontSize + endFontSize)
        val fontSize = fontSize
        val progress = (fontSize - startFontSize) / (endFontSize - startFontSize)

        // The drawn text width is a more accurate scale than font size. This avoids
        // jump when switching bitmaps.
        val expectedWidth = interpolate(startWidth, endWidth, progress)

        if (progress < threshold) { // draw start bitmap

          val scale = expectedWidth / startWidth
          val tx = getTranslationPoint(horizontalGravity, left, right, startBitmap.width.f, scale)
          val ty = getTranslationPoint(verticalGravity, top, bottom, startBitmap.height.f, scale)

          translate(tx, ty)
          scale(scale, scale)
          drawBitmap(startBitmap, 0f, 0f, paint)

        } else { // draw end bitmap

          val scale = expectedWidth / endWidth
          val tx = getTranslationPoint(horizontalGravity, left, right, endBitmap.width.f, scale)
          val ty = getTranslationPoint(verticalGravity, top, bottom, endBitmap.height.f, scale)

          translate(tx, ty)
          scale(scale, scale)
          drawBitmap(endBitmap, 0f, 0f, paint)
        }
      }
    }

    override fun setAlpha(alpha: Int) = Unit

    override fun setColorFilter(colorFilter: ColorFilter?) {
      paint.colorFilter = colorFilter
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    @SuppressLint("RtlHardcoded")
    private fun getTranslationPoint(
      gravity: Int,
      start: Float,
      end: Float,
      dim: Float,
      scale: Float
    ): Float {

      return when (gravity) {

        Gravity.CENTER_HORIZONTAL,
        Gravity.CENTER_VERTICAL -> (start + end - dim * scale) / 2f

        Gravity.RIGHT,
        Gravity.BOTTOM -> end - dim * scale

        Gravity.LEFT,
        Gravity.TOP -> start

        else -> start
      }
    }
  }

  /**
   * Contains all the non-font-size data used by the TextResize transition.
   * None of these values should trigger the transition, so they are not listed
   * in PROPERTIES. These are captured together to avoid boxing of all the
   * primitives while adding to TransitionValues.
   */
  private class TextResizeData(textView: TextView) {
    val paddingLeft = textView.paddingLeft
    val paddingTop = textView.paddingTop
    val paddingRight = textView.paddingRight
    val paddingBottom = textView.paddingBottom
    val width = textView.width
    val height = textView.height
    val gravity = textView.gravity
    val textColor = textView.currentTextColor
  }

  companion object {
    private const val DATA = "TextResize:data"
    private const val FONT_SIZE = "TextResize:fontSize"

    // We only care about FONT_SIZE. If anything else changes, we don't
    // want this transition to be called to create an Animator.
    private val PROPERTIES = arrayOf(FONT_SIZE)

    @JvmStatic
    private fun setTextViewData(view: TextView, data: TextResizeData, fontSize: Float) {

      with(view) {

        setPadding(data.paddingLeft, data.paddingTop, data.paddingRight, data.paddingBottom)

        setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
        setTextColor(data.textColor)

        right = left + data.width
        bottom = top + data.height

        val widthSpec = makeMeasureSpec(width, EXACTLY)
        val heightSpec = makeMeasureSpec(height, EXACTLY)

        measure(widthSpec, heightSpec)
        layout(left, top, right, bottom)
      }
    }

    @JvmStatic
    private fun captureTextBitmap(view: TextView): Bitmap? {

      val background = view.background

      view.background = null

      val width = view.width - view.paddingLeft - view.paddingRight
      val height = view.height - view.paddingTop - view.paddingBottom

      if (width == 0 || height == 0) return null

      return createBitmap(width, height).applyCanvas {

        translate(
          -view.paddingLeft.f,
          -view.paddingTop.f
        )

        view.draw(this)

        view.background = background
      }
    }

    @JvmStatic
    private fun interpolate(start: Float, end: Float, fraction: Float): Float {
      return start + fraction * (end - start)
    }
  }
}