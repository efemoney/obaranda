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

package com.efemoney.obaranda

import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.efemoney.obaranda.databinding.FragmentMainBinding
import com.efemoney.obaranda.fragment.BaseFragment
import com.efemoney.obaranda.fragment.Inflater
import javax.inject.Inject

class MainFragment @Inject constructor() : BaseFragment<FragmentMainBinding>() {

  override fun createBinding(inflater: Inflater, parent: ViewGroup?) =
    FragmentMainBinding.inflate(inflater, parent, false)

  override fun bind() {
    // initHacks(binding.navigation)
    binding.navigation.setupWithNavController(findNavController())
  }

  /*private fun initHacks(nav: BottomNavigationView) = with(nav) {

    enableItemShiftingMode(true)
    setIconSize(16f, 16f)
    setSmallTextSize(0f) // disable showing text when unselected
    setLargeTextSize(14f)

    // remove bottom margins on the icon views
    (0 until itemCount).forEach {
      getIconAt(it).updateLayoutParams<FrameLayout.LayoutParams> { bottomMargin = 0 }
    }
  }

  private fun moarHacks(nav: BottomNavigationViewEx, selectedMenuItem: MenuItem) = with(nav) {

    val selectedPosition = getMenuItemPosition(selectedMenuItem)
    val twelveDp = context!!.dpToPx(12)

    (0 until itemCount).forEach { position ->
      setIconMarginTop(position, if (position == selectedPosition) twelveDp else 0)
    }
  }*/
}
