package com.kodeco.android.aam.ui.views

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PageItem(
  uri: Uri,
  itemWidth: Dp,
  itemShape: RoundedCornerShape,
  onItemClick: (Uri) -> Unit,
) {
  Box(
    modifier = Modifier
      .width(itemWidth)
      .fillMaxHeight()
      .background(Color.Companion.DarkGray, itemShape)
      .border(BorderStroke(2.dp, Color.Companion.Gray), itemShape)
      .clip(itemShape)
  ) {
    // Image
    AsyncImage(
      model = uri,
      contentDescription = "Scanned Document Page",
      modifier = Modifier
        .fillMaxSize()
        .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
      contentScale = ContentScale.Companion.Fit
    )
    // Button
    Button(
      onClick = {
        onItemClick.invoke(uri)
      },
      modifier = Modifier.align(Alignment.Companion.TopEnd),
      colors = ButtonDefaults.buttonColors(backgroundColor = Color.Companion.Transparent),
      elevation = ButtonDefaults.elevation(
        defaultElevation = 0.dp,
        pressedElevation = 0.dp,
        hoveredElevation = 0.dp,
        focusedElevation = 0.dp
      )
    ) {
      Text(text = "Extract Text", color = Color.Companion.White)
      Icon(
        imageVector = Icons.Filled.AutoAwesome,
        contentDescription = "Extract Text Icon",
        modifier = Modifier
          .size(32.dp)
          .padding(horizontal = 8.dp),
        tint = Color.Companion.White
      )
    }
  }
}