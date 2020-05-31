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

package com.efemoney.obaranda.widget

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.efemoney.obaranda.ktx.NoWildcards
import com.efemoney.obaranda.model.Changes
import com.efemoney.obaranda.model.ObarandaDisplayItem

@NoWildcards
abstract class ViewHolder<in D : ObarandaDisplayItem>(binding: ViewBinding) : ViewHolder(binding.root) {

  open fun onRecycled() = Unit

  open fun bindChanges(item: D, changes: Changes) = Unit

  abstract fun bind(item: D)
}