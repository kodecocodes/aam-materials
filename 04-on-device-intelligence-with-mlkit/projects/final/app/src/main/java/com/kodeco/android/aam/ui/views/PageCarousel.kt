/*
Copyright (c) 2025 Kodeco Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
distribute, sublicense, create a derivative work, and/or sell copies of the
Software in any work that is designed, intended, or marketed for pedagogical or
instructional purposes related to programming, coding, application development,
or information technology.  Permission for such use, copying, modification,
merger, publication, distribution, sublicensing, creation of derivative works,
or sale is expressly withheld.

This project and source code may use libraries or frameworks that are
released under various Open-Source licenses. Use of those libraries and
frameworks are governed by their own individual licenses.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.kodeco.android.aam.ui.views

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.kodeco.android.aam.ui.theme.Shapes
import com.kodeco.android.aam.ui.theme.carouselPadding

@Composable
fun BoxScope.PageCarousel(
  pages: List<Uri>,
  onItemClick: (Uri) -> Unit,
) {
  val pageHeight = 500.dp
  val pageWidth = LocalConfiguration.current.screenWidthDp.dp * 0.80F
  val shape = Shapes.large as RoundedCornerShape

  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .height(pageHeight)
      .align(Alignment.Companion.Center)
      .background(
        color = if (pages.isEmpty()) Color.DarkGray else Color.Transparent,
        shape = shape
      ),
    contentPadding = PaddingValues(horizontal = carouselPadding, vertical = carouselPadding),
    horizontalArrangement = Arrangement.spacedBy(carouselPadding)
  ) {
    items(pages) { uri ->
      PageItem(
        uri = uri,
        itemWidth = pageWidth,
        itemShape = shape,
        onItemClick = onItemClick,
      )
    }
  }
}