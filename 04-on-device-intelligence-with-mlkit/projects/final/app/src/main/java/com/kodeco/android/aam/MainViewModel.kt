package com.kodeco.android.aam

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

/**
 * ViewModel for the main screen.
 *
 * This ViewModel is responsiblefor providing a list of cat breeds
 * to the UI. The list of breeds is initialized when the ViewModel
 * is created.
 */
class MainViewModel : ViewModel() {

  // 1: Prepare Document Scanning Client
  fun prepareScanner(): GmsDocumentScanner {
    val options = GmsDocumentScannerOptions.Builder()
      .setPageLimit(3)
      .setResultFormats(RESULT_FORMAT_JPEG)
      .setScannerMode(SCANNER_MODE_FULL)
      .build()
    return GmsDocumentScanning.getClient(options)
  }

  fun extractTextFromResult(scanResult: GmsDocumentScanningResult?) {
    scanResult?.pages?.let { pages ->
      for (page in pages) {
//              val imageUri = pages[0].imageUri
        Log.d("scannerLauncher", "Selected page URI: $page")
      }
    }
  }
}