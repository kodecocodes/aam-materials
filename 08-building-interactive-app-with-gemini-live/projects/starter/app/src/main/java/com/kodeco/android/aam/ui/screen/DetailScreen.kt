/*
 * Copyright (c) 2025 Kodeco Inc
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
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.kodeco.android.aam.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText
import com.kodeco.android.aam.MainViewModel
import com.kodeco.android.aam.live.LiveSessionState
import com.kodeco.android.aam.ui.theme.KodecoSampleTheme

@Composable
fun DetailScreen(breedName: String, viewModel: MainViewModel) {
  val isLoading by viewModel.isLoading.collectAsState()
  val catDescription by viewModel.catDescription.collectAsState()
  val liveSessionState by viewModel.liveSessionState.collectAsState()

  LaunchedEffect(breedName) {
    viewModel.describeCat(breedName)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top
    ) {
      if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Generating details for $breedName...")
      } else {
        RichText {
          Markdown(catDescription)
        }
      }
    }

    var text = ""
    var icon = Icons.Default.Info
    when (val buttonState = liveSessionState) {
      is LiveSessionState.Unknown -> {
        text = buttonState.message
        icon = Icons.Default.Info
      }

      is LiveSessionState.Ready -> {
        text = buttonState.message + " about $breedName"
        icon = Icons.Outlined.Mic
      }

      is LiveSessionState.Running -> {
        text = buttonState.message
        icon = Icons.Default.Mic
      }

      is LiveSessionState.Error -> {
        text = buttonState.message
        icon = Icons.Default.Warning
      }

    }

    Button(
      onClick = {
        viewModel.askAbout(breedName)
      },
      modifier = Modifier.fillMaxWidth()
    ) {
      Icon(
        imageVector = icon,
        contentDescription = "Ask"
      )
      Text(
        text = text,
        modifier = Modifier.padding(horizontal = 8.dp)
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
  KodecoSampleTheme {
    val previewViewModel: MainViewModel = viewModel()
    DetailScreen(breedName = "Persian", viewModel = previewViewModel)
  }
}
