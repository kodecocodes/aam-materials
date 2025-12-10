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
package com.kodeco.android.aam

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.kodeco.android.aam.ui.theme.KodecoSampleTheme
import com.kodeco.android.aam.ui.views.PageCarousel
import com.kodeco.android.aam.ui.views.ScanButton

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  // 1.2: Launcher Document intent Scanner and handling the result
  val scannerLauncher = registerForActivityResult(
    contract = ActivityResultContracts.StartIntentSenderForResult()
  ) { result ->
    if (result.resultCode == RESULT_OK) {
      val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
      viewModel.extractPages(scanResult = scanResult)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)

    setContent {
      KodecoSampleTheme {
        Surface(
          modifier = Modifier.Companion.fillMaxSize(),
          color = MaterialTheme.colors.background
        ) {
          Box(modifier = Modifier.Companion.fillMaxSize()) {
            PageCarousel(
              pages = viewModel.pageUris,
              onItemClick = { uri ->
                // 2.4: Text Recognition and share on item click
                shareTextFromImage(uri = uri)
              }
            )
            ScanButton(
              onClickScan = {
                // 1.5: Launch Document Scanner on button click
                launchDocumentScanner()
              }
            )
          }
        }
      }
    }
  }

  // 1.4: Launch Document Scanner
  private fun launchDocumentScanner() {
    viewModel
      .prepareScanner()
      .getStartScanIntent(this@MainActivity)
      .addOnSuccessListener { it ->
        val scannerIntent = IntentSenderRequest.Builder(it).build()
        scannerLauncher.launch(scannerIntent)
      }
  }

  // 2.2: Extract text from image, and then share with the Share Intent
  private fun shareTextFromImage(uri: Uri) {
    viewModel.getTextFromImage(uri) { extractedText ->
      extractedText?.let {
        shareText(text = it)
      }
    }
  }

  // 2.3: Using Share Intent
  private fun shareText(text: String) {
    val intent = Intent().apply {
      action = Intent.ACTION_SEND
      type = "text/plain"
      putExtra(Intent.EXTRA_TEXT, text)
    }
    val shareIntent = Intent.createChooser(intent, "Text from Image")
    startActivity(shareIntent)
  }
}
