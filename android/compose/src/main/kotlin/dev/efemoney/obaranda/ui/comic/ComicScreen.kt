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

@file:Suppress("LocalVariableName")

package dev.efemoney.obaranda.ui.comic

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.fragment.navArgs
import androidx.ui.tooling.preview.Preview
import com.efemoney.obaranda.data.model.Image
import com.efemoney.obaranda.ktx.d
import com.efemoney.obaranda.ktx.f
import dagger.hilt.android.AndroidEntryPoint
import dev.efemoney.obaranda.ComposableFragment
import dev.efemoney.obaranda.ui.ObarandaTheme
import dev.efemoney.obaranda.ui.bg

@AndroidEntryPoint
class ComicScreenFragment : ComposableFragment() {

  private val args by navArgs<ComicScreenFragmentArgs>()

  @Composable
  override fun content() = ObarandaTheme { ComicScreen(args.page) }
}

@Composable
fun ComicScreen(page: Int) {
  val vm = viewModel<ComicViewModel>()
  val model by vm.model.collectAsState()
  val state = remember { ComicScreenState(model) }

  onActive { vm.loadComic(page) }
  ComicScreen(state)
}

@Composable
private fun ComicScreen(state: ComicScreenState) {
  Stack(Modifier.fillMaxSize()) {
    ComicImages(state.images)
    ComicDetails()
    CommentsSheet()
  }
}

@Composable
private fun ComicImages(images: List<Image>) {
  Column(
    modifier = Modifier
  ) {
    images.fastForEach {
      Image(ComicImagePainter(it))
    }
  }
}

@Composable
private fun ComicDetails() {

}

@Composable
private fun CommentsSheet() {

}

data class ComicScreenState(
  val model: ComicViewModel.UiModel,
  val detailsVisible: Boolean = false,
  val commentsVisible: Boolean = false
) {
  val images get() = model.comic?.images.orEmpty()
  val isImmersive get() = !detailsVisible && !commentsVisible
}

private data class ComicImagesPainter(val images: List<Image>) : Painter() {

  private val painters = images.fastMap(::ComicImagePainter)

  override val intrinsicSize = run {
    val maxW = images.maxOf { it.size.width }
    val h = images.sumOf {
      val w = it.size.width
      val scale = maxW.d / w
      it.size.height * scale
    }

    Size(maxW.f, h.f)
  }

  override fun DrawScope.onDraw() {
    var offset = Offset.Zero

    with(painters[0]) {

    }
  }
}

private data class ComicImagePainter(val image: Image) : Painter() {
  override val intrinsicSize: Size = Size(image.size.width.f, image.size.height.f)
  override fun DrawScope.onDraw() = drawRect(image.palette.bg)
}

@Composable
@Preview()
fun PreviewImage() = ObarandaTheme {
  ComicImages(sampleImages)
}



