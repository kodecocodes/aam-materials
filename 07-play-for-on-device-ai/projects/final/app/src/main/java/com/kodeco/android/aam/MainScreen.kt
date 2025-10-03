package com.kodeco.android.aam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
  statusText: String,
  isDownloading: Boolean,
  onDownloadClick: (() -> Unit)? = null
) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (isDownloading) {
        CircularProgressIndicator()
      }
      Spacer(modifier = Modifier.height(16.dp))
      Text(text = statusText)
      Spacer(modifier = Modifier.height(16.dp))
      if (!isDownloading) {
        Button(onClick = {
          onDownloadClick?.invoke()
        }) {
          Text("Download Model")
        }
      }
    }
  }
}