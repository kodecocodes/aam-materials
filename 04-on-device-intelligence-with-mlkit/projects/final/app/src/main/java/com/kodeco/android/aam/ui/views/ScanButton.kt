package com.kodeco.android.aam.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.ScanButton(
  onClickScan: () -> Unit
) {
  Box(
    modifier = Modifier
      .align(Alignment.Companion.BottomCenter)
      .padding(bottom = 32.dp)
      .size(64.dp)
      .clip(CircleShape)
      .background(MaterialTheme.colors.primary)
      .clickable {
        onClickScan.invoke()
      }
      .wrapContentSize(Alignment.Companion.Center)
  ) {
    Icon(
      imageVector = Icons.Filled.DocumentScanner,
      contentDescription = "Scan Document",
      tint = Color.Companion.White
    )
  }
}