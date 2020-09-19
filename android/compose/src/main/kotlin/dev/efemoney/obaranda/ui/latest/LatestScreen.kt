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

package dev.efemoney.obaranda.ui.latest

import androidx.annotation.DrawableRes
import androidx.compose.animation.animate
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.drawLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.viewinterop.viewModel
import com.efemoney.obaranda.data.model.Comic
import com.efemoney.obaranda.ktx.f
import com.efemoney.obaranda.util.getSimpleRelativeDate
import dev.efemoney.obaranda.R.drawable
import dev.efemoney.obaranda.ui.bg
import dev.efemoney.obaranda.ui.components.VerticalGrid
import dev.efemoney.obaranda.ui.inDp

@Composable
fun LatestScreen(innerPadding: InnerPadding) {
  val vm = viewModel<LatestViewModel>()
  val model by vm.model.collectAsState()

  val scroll = rememberScrollState()
  val appBarExpandedHeight = 72.dp
  val appBarCollapsedHeight = 56.dp
  val appBarHeight = (appBarExpandedHeight - scroll.value.inDp).coerceIn(appBarCollapsedHeight, appBarExpandedHeight)
  val appBarProgress = (appBarHeight - appBarCollapsedHeight) / (appBarExpandedHeight - appBarCollapsedHeight)
  val appBarTextSize = if (appBarProgress <= 0.5f) 16.sp else 24.sp
  val raisedElevation = animate(if (appBarHeight == appBarCollapsedHeight) 4.dp else 0.dp)

  Stack {
    TopAppBar(
      title = { Text("Latest Comics", fontSize = appBarTextSize) },
      modifier = Modifier.preferredHeight(appBarHeight).fillMaxWidth(),
      elevation = raisedElevation,
      backgroundColor = MaterialTheme.colors.background,
    )

    ScrollableColumn(Modifier.padding(innerPadding).padding(top = appBarHeight), scrollState = scroll) {
      VerticalGrid(Modifier.padding(InnerPadding(8.dp))) {
        model.comics.fastForEach { ComicCard(it, vm::comicClicked) }
      }
    }
  }

  onActive { vm.loadComics() }
}

@Composable
fun ComicCard(comic: Comic, onClick: (page: Int) -> Unit = {}) {
  val hero = comic.images[0]

  val interactions = remember { InteractionState() }
  val raisedElevation = animate(if (Interaction.Pressed in interactions) 8.dp else 1.dp)
  val scale = animate(if (Interaction.Pressed in interactions) 1.05f else 1.0f)

  Card(
    Modifier
      .padding(8.dp)
      .clickable(interactionState = interactions, onClick = { onClick(comic.page) })
      .aspectRatio(77.f / 71)
      .drawLayer(scaleX = scale, scaleY = scale),
    backgroundColor = hero.palette.bg,
    contentColor = Color.White,
    elevation = raisedElevation,
  ) {

    Column(Modifier.wrapContentHeight(Alignment.Bottom).padding(bottom = 8.dp)) {
      Text(
        text = comic.title,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        maxLines = 2,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
      )

      Spacer(modifier = Modifier.height(4.dp))

      Row(modifier = Modifier.padding(horizontal = 8.dp), verticalGravity = Alignment.CenterVertically) {
        TextIcon(text = comic.pubDate.getSimpleRelativeDate().toString(), iconRes = drawable.ic_date_16)
        Spacer(modifier = Modifier.weight(1f))
        TextIcon(comic.commentsCount.toString(), drawable.ic_chat_16)
      }
    }
  }
}

@Composable
private fun TextIcon(text: String, @DrawableRes iconRes: Int) {
  Icon(vectorResource(iconRes), Modifier.preferredSize(16.dp))
  Spacer(modifier = Modifier.preferredWidth(4.dp))
  Text(text, fontSize = 10.sp)
}
