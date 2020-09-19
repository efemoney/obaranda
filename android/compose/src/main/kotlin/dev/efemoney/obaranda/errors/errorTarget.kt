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

package dev.efemoney.obaranda.errors

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