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

import androidx.compose.material.BottomNavigation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dagger.hilt.android.AndroidEntryPoint
import dev.efemoney.obaranda.ComposableFragment
import dev.efemoney.obaranda.ui.latest.LatestScreen

@AndroidEntryPoint
class MainScreenFragment : ComposableFragment() {

  @Composable
  override fun content() = ObarandaTheme { MainScreen() }
}

@Composable
fun MainScreen() {

  val (selectedIndex, setSelected) = remember { mutableStateOf(0) }

  Scaffold(
    bottomBar = { MainNavBar(selectedIndex, setSelected) },
    bodyContent = { if (selectedIndex == 0) LatestScreen(it.bottom) }
  )
}

@Composable
private fun MainNavBar(selectedTabIndex: Int, setSelectedTab: (Int) -> Unit) {

  val items = listOf(
    "Home" to vectorResource(drawable.ic_home),
    "Favorites" to vectorResource(drawable.ic_fave),
    "App Info" to vectorResource(drawable.ic_app_info),
  )

  BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
    items.fastForEachIndexed { index, (title, icon) ->
      BottomNavigationItem(
        icon = { Icon(icon, Modifier.preferredSize(16.dp)) },
        label = { Text(title) },
        selected = index == selectedTabIndex,
        onClick = { setSelectedTab(index) },
        alwaysShowLabels = false,
        unselectedContentColor = Grey
      )
    }
  }
}
