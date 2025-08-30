package com.kodeco.android.aam

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.kodeco.android.aam.ui.theme.KodecoSampleTheme
import com.kodeco.android.aam.ui.views.PageCarousel
import com.kodeco.android.aam.ui.views.ScanButton

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

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
                // Text Recognition and share on item click
              }
            )
            ScanButton(
              onClickScan = {
                // Launch Document Scanner on button click
              }
            )
          }
        }
      }
    }
  }

  // Launch Document Scanner
  private fun launchDocumentScanner() {

  }

  // Extract text from image, and then share with the Share Intent
  private fun shareTextFromImage(uri: Uri) {

  }
}
