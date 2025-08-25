package com.kodeco.android.aam.ui.views

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.kodeco.android.aam.ui.theme.Shapes
import com.kodeco.android.aam.ui.theme.carouselPadding

@Composable
fun BoxScope.PageCarousel(
  pages: List<Uri>,
  onItemClick: (Uri) -> Unit,
) {
  val pageHeight = 500.dp
  val pageWidth = LocalConfiguration.current.screenWidthDp.dp * 0.80F
  val shape = Shapes.large as RoundedCornerShape

  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .height(pageHeight)
      .align(Alignment.Companion.Center)
      .background(
        color = if (pages.isEmpty()) Color.DarkGray else Color.Transparent,
        shape = shape
      ),
    contentPadding = PaddingValues(horizontal = carouselPadding, vertical = carouselPadding),
    horizontalArrangement = Arrangement.spacedBy(carouselPadding)
  ) {
    items(pages) { uri ->
      PageItem(
        uri = uri,
        itemWidth = pageWidth,
        itemShape = shape,
        onItemClick = onItemClick,
      )
    }
  }
}