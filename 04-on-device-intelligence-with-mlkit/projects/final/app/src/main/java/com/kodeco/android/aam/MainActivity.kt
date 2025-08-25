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
