package com.kodeco.android.aam

import android.app.Application
import android.net.Uri
import android.util.Log
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

  // 1: Prepare Document Scanning Client
  fun prepareScanner(): GmsDocumentScanner {
    val options = GmsDocumentScannerOptions.Builder()
      .setPageLimit(3)
      .setResultFormats(RESULT_FORMAT_JPEG)
      .setScannerMode(SCANNER_MODE_FULL)
      .build()
    return GmsDocumentScanning.getClient(options)
  }

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

  fun getTextFromImage(image: Uri, onCompleted: (String?) -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      val image = fromFilePath(application, image)
      TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        .process(image)
        .addOnSuccessListener { visionText ->
          val resultText = visionText.text
          Log.d("scannerLauncher", "Extracted text: $resultText")
          onCompleted(resultText)
        }
        .addOnFailureListener { e ->
          Log.e("scannerLauncher", "Extracted text ERROR: ${e.printStackTrace()}")
          onCompleted(null)
        }
    }
  }
}