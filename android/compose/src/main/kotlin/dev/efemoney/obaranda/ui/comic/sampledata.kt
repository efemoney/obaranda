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

package dev.efemoney.obaranda.ui.comic

import androidx.core.graphics.toColorInt
import com.efemoney.obaranda.data.model.Image
import com.efemoney.obaranda.data.model.Palette
import com.efemoney.obaranda.data.model.Size

val sampleImages = listOf(
  Image(
    url = "http://obaranda.com/assets/images/comics/201710231401297524381/Gidi1.jpg",
    alt = "thisis",
    size = Size(width = 896, height = 2058),
    palette = Palette(muted = "#687070".toColorInt(), vibrant = "#4088c0".toColorInt())
  ),
  Image(
    url = "http://obaranda.com/assets/images/comics/20171024075021456465/Gidi2.jpg",
    alt = "",
    size = Size(width = 897, height = 2074),
    palette = Palette(muted = "#d8e0c8".toColorInt(), vibrant = "#98b8d0".toColorInt())
  ),
  Image(
    url = "http://obaranda.com/assets/images/comics/20171024075033215520/Gidi3.jpg",
    alt = "",
    size = Size(width = 848, height = 1960),
    palette = Palette(muted = "#687880".toColorInt(), vibrant = "#1898f0".toColorInt())
  ),
  Image(
    url = "http://obaranda.com/assets/images/comics/20171024075043116568/Gidi4.jpg",
    alt = "",
    size = Size(width = 831, height = 2265),
    palette = Palette(vibrant = "#1098f0".toColorInt(), muted = null)
  ),
  Image(
    url = "http://obaranda.com/assets/images/comics/20171024075055406481/Gidi5.jpg",
    alt = "",
    size = Size(width = 777, height = 2400),
    palette = Palette(muted = "#9068a0".toColorInt(), null)
  ),
  Image(
    url = "http://obaranda.com/assets/images/comics/20171024075112139934/Gidi6.jpg",
    alt = "",
    size = Size(width = 886, height = 2444),
    palette = Palette(null, vibrant = "#1898f0".toColorInt())
  ),
  Image(
    url = "http://obaranda.com/assets/images/comics/20171024075134669475/Gidi7.gif",
    alt = "",
    size = Size(width = 967, height = 1644),
    palette = Palette(muted = "#d0d8e0".toColorInt(), vibrant = "#5860f8".toColorInt())
  )
)