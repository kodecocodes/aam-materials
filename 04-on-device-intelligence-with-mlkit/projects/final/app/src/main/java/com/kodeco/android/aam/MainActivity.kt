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
                shareTextFromImage(uri = uri)
              }
            )
            ScanButton(
              onClickScan = {
                launchDocumentScanner()
              }
            )
          }
        }
      }
    }
  }

  private fun launchDocumentScanner() {
    viewModel
      .prepareScanner()
      .getStartScanIntent(this@MainActivity)
      .addOnSuccessListener { intentSender ->
        scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
      }
  }

  private fun shareTextFromImage(uri: Uri) {
    viewModel.getTextFromImage(uri) { extractedText ->
      extractedText?.let {
        shareText(text = it)
      }
    }
  }

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
