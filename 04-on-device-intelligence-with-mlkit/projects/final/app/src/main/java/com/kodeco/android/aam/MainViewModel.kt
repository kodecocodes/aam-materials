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

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage.fromFilePath
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

  // State to hold the recognized images
  val pageUris = mutableStateListOf<Uri>()

  // 1.1: Prepare Document Scanning Client
  fun prepareScanner(): GmsDocumentScanner {
    val options = GmsDocumentScannerOptions.Builder()
      .setPageLimit(3)
      .setResultFormats(RESULT_FORMAT_JPEG)
      .setScannerMode(SCANNER_MODE_FULL)
      .build()
    return GmsDocumentScanning.getClient(options)
  }

  // 1.3: Extract the pages from the result
  fun extractPages(scanResult: GmsDocumentScanningResult?) {
    viewModelScope.launch(Dispatchers.IO) {
      scanResult?.pages?.let { pages ->
        pageUris.clear()

        for (page in pages) {
          pageUris.add(page.imageUri)
        }
      }
    }
  }

  // 2.1: Extract text from image
  fun getTextFromImage(image: Uri, onCompleted: (String?) -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      val image = fromFilePath(application, image)
      TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        .process(image)
        .addOnSuccessListener { visionText ->
          val resultText = visionText.text
          onCompleted(resultText)
        }
        .addOnFailureListener { e ->
          onCompleted(null)
        }
    }
  }
}