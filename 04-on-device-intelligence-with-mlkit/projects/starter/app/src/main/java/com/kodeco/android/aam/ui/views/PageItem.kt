/*
 * Copyright (c) 2025 Kodeco Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.kodeco.android.aam.ui.views

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kodeco.android.aam.ui.theme.colorAccent

@Composable
fun PageItem(
  uri: Uri,
  itemWidth: Dp,
  itemShape: RoundedCornerShape,
  onItemClick: (Uri) -> Unit,
) {
  Box(
    modifier = Modifier
      .width(itemWidth)
      .fillMaxHeight()
      .background(Color.Companion.DarkGray, itemShape)
      .border(BorderStroke(2.dp, Color.Companion.Gray), itemShape)
      .clip(itemShape)
  ) {
    // Image
    AsyncImage(
      model = uri,
      contentDescription = "Scanned Document Page",
      modifier = Modifier
        .fillMaxSize()
        .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
      contentScale = ContentScale.Companion.Fit
    )
    // Button
    Button(
      onClick = {
        onItemClick.invoke(uri)
      },
      modifier = Modifier.align(Alignment.Companion.TopEnd),
      colors = ButtonDefaults.buttonColors(backgroundColor = Color.Companion.Transparent),
      elevation = ButtonDefaults.elevation(
        defaultElevation = 0.dp,
        pressedElevation = 0.dp,
        hoveredElevation = 0.dp,
        focusedElevation = 0.dp
      )
    ) {
      Text(text = "Extract Text", color = colorAccent)
      Icon(
        imageVector = Icons.Filled.AutoAwesome,
        contentDescription = "Extract Text Icon",
        modifier = Modifier
          .size(32.dp)
          .padding(horizontal = 8.dp),
        tint = colorAccent
      )
    }
  }
}