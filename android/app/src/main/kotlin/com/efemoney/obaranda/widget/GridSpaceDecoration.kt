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

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpaceDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

    val pos = parent.getChildAdapterPosition(view)

    if (pos == RecyclerView.NO_POSITION) return

    val glm = parent.layoutManager as GridLayoutManager
    val spanLookup = glm.spanSizeLookup
    val spanCount = glm.spanCount

    val row = spanLookup.getSpanGroupIndex(pos, spanCount)
    val col = spanLookup.getSpanIndex(pos, spanCount)

    // top - 0 for first row and margin / 2 for others
    // left - margin for first column and margin / 2 for others
    // right - margin for last column and margin / 2 for others
    // bottom - margin for last row and margin / 2 for others

    val lastRow = spanLookup.getSpanGroupIndex(state.itemCount - 1, spanCount)
    val lastColumn = spanCount - 1

    val halfMargin = margin / 2

    outRect.top = if (row == 0) 0 else halfMargin
    outRect.left = if (col == 0) margin else halfMargin
    outRect.right = if (col == lastColumn) margin else halfMargin
    outRect.bottom = if (row == lastRow) margin else halfMargin
  }
}
