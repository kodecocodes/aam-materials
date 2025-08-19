package com.kodeco.android.aam

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.InputImage.fromFilePath
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

/**
 * ViewModel for the main screen.
 *
 * This ViewModel is responsiblefor providing a list of cat breeds
 * to the UI. The list of breeds is initialized when the ViewModel
 * is created.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

  // 1: Prepare Document Scanning Client
  fun prepareScanner(): GmsDocumentScanner {
    val options = GmsDocumentScannerOptions.Builder()
      .setPageLimit(1)
      .setResultFormats(RESULT_FORMAT_JPEG)
      .setScannerMode(SCANNER_MODE_FULL)
      .build()
    return GmsDocumentScanning.getClient(options)
  }

  fun extractTextFromResult(scanResult: GmsDocumentScanningResult?) {
    scanResult?.pages?.let { pages ->
      for (page in pages) {
        Log.d("scannerLauncher", "Selected page URI: $page")
        try {
          val image = fromFilePath(application, page.imageUri)
          applyTextRecognition(image) { extractedText ->
            Log.d("scannerLauncher", "Extracted text: $extractedText")
          }
        } catch (e: IOException) {
          Log.e("scannerLauncher", "ERROR: ${e.printStackTrace()}")
        }
      }
    }
  }

  fun applyTextRecognition(image: InputImage, onCompleted: (String?) -> Unit) {
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