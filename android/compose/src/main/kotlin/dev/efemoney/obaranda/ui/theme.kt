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

package dev.efemoney.obaranda.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.font
import androidx.compose.ui.text.font.fontFamily
import androidx.compose.ui.unit.dp
import dev.efemoney.obaranda.R.font

val SkyBlue = Color(0xff5ac8fa)
val BrightBlue = Color(0xff007aff)
val DarkGreyBlue = Color(0xff253858)
val Grey = Color(0xffceced2)
val OffWhite = Color(0xfff7f7f7)

val colors = lightColors(
  background = OffWhite,
  surface = Color.White,
  primary = DarkGreyBlue,
  onBackground = DarkGreyBlue,
  onSurface = DarkGreyBlue,
  onPrimary = Color.White,
)

val DinFamily = fontFamily(
  font(font.din_light, FontWeight.Light),
  font(font.din_regular, FontWeight.Normal),
  font(font.din_medium, FontWeight.Medium),
  font(font.din_bold, FontWeight.Bold)
)

val typography = Typography(
  defaultFontFamily = DinFamily,
)

val shapes = Shapes(
  small = RoundedCornerShape(percent = 50),
  medium = RoundedCornerShape(4.dp),
  large = RoundedCornerShape(topLeft = 4.dp, topRight = 4.dp)
)

@Composable
fun ObarandaTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colors = colors,
    typography = typography,
    shapes = shapes,
    content = content
  )
}
