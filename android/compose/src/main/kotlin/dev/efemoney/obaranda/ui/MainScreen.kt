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

package dev.efemoney.obaranda.ui

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import dagger.hilt.android.AndroidEntryPoint
import dev.efemoney.obaranda.ComposableFragment
import dev.efemoney.obaranda.R.drawable
import dev.efemoney.obaranda.ui.latest.LatestScreen

@AndroidEntryPoint
class MainScreenFragment : ComposableFragment() {

  @Composable
  override fun content() = ObarandaTheme { MainScreen() }
}

@Composable
fun MainScreen() {

  var selectedIndex by remember { mutableStateOf(0) }

  Scaffold(bottomBar = {
    NavBar(
      selectedTabIndex = selectedIndex,
      setSelectedTab = { selectedIndex = it }
    )
  }) {
    if (selectedIndex == 0) LatestScreen(it)
  }
}

@Composable
private fun NavBar(selectedTabIndex: Int, setSelectedTab: (Int) -> Unit) {

  val items = listOf(
    "Home" to vectorResource(drawable.ic_home),
    "Favorites" to vectorResource(drawable.ic_fave),
    "App Info" to vectorResource(drawable.ic_app_info),
  )

  BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
    items.fastForEachIndexed { index, (title, icon) ->
      BottomNavigationItem(
        modifier = Modifier,
        icon = { Icon(icon, Modifier.preferredSize(16.dp)) },
        label = { Text(title) },
        selected = index == selectedTabIndex,
        onSelect = { setSelectedTab(index) },
        alwaysShowLabels = false,
        unselectedContentColor = Grey
      )
    }
  }
}
