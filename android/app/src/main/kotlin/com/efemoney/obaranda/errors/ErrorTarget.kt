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

package com.efemoney.obaranda.errors

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.efemoney.obaranda.R
import com.google.android.material.snackbar.Snackbar

interface ErrorTarget {
  fun error(error: Throwable): ErrorTarget
  fun title(title: CharSequence?): ErrorTarget
  fun message(msg: CharSequence): ErrorTarget
  fun action(action: ErrorAction?): ErrorTarget
  fun visible(visible: Boolean): ErrorTarget
}

abstract class AbstractErrorTarget : ErrorTarget {

  override fun error(error: Throwable): ErrorTarget = this.apply {
    title((error as? ThrowablePlusMeta)?.title)
    action((error as? ThrowablePlusMeta)?.action)
  }

  override fun title(title: CharSequence?): ErrorTarget = this.apply {
    titleVisible(title != null)
    title?.let { titleValue(it) }
  }

  override fun action(action: ErrorAction?): ErrorTarget = this.apply {
    actionVisible(action != null)
    action?.let { actionValue(it) }
  }

  protected open fun titleVisible(visible: Boolean) {}

  protected open fun actionVisible(visible: Boolean) {}

  protected open fun titleValue(title: CharSequence) {}

  protected open fun actionValue(action: ErrorAction) {}
}

class SnackBarErrorTarget(private val snack: Snackbar) : AbstractErrorTarget() {

  override fun visible(visible: Boolean) = apply {
    if (visible) snack.show() else snack.dismiss()
  }

  override fun message(msg: CharSequence) = apply {
    snack.setText(msg)
  }

  override fun actionValue(action: ErrorAction) {
    snack.setAction(action.label) { action.block() }
  }
}

class ViewErrorTarget(private val parent: View) : AbstractErrorTarget() {

  private val errorText: TextView? = parent.findViewById(R.id.error_text)
  private val errorAction: TextView? = parent.findViewById(R.id.error_action)

  override fun visible(visible: Boolean) = this.apply {
    parent.isVisible = visible
  }

  override fun message(msg: CharSequence) = this.apply {
    errorText?.text = msg
  }

  override fun actionVisible(visible: Boolean) {
    errorAction?.isVisible = visible
  }

  override fun actionValue(action: ErrorAction) {
    errorAction?.text = action.label
    errorAction?.setOnClickListener { action.block() }
  }
}