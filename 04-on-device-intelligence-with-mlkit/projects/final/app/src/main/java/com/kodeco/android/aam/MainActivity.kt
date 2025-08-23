package com.kodeco.android.aam

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.kodeco.android.aam.ui.theme.KodecoSampleTheme
import com.kodeco.android.aam.ui.theme.Shapes

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  val scannerLauncher = registerForActivityResult(
    contract = ActivityResultContracts.StartIntentSenderForResult()
  ) { result ->
    if (result.resultCode == RESULT_OK) {
      val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
      viewModel.extractTextFromResult(scanResult = scanResult)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)

    setContent {
      val context = LocalContext.current
      val activity = context as? Activity
      val screenWidth = LocalConfiguration.current.screenWidthDp.dp
      val carouselPadding = 16.dp

      KodecoSampleTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
          Box(modifier = Modifier.fillMaxSize()) {
            PageCarousel(
              viewModel = viewModel,
              screenWidth = screenWidth,
              carouselPadding = carouselPadding
            )
            ScanButton(
              viewModel = viewModel,
              scannerLauncher = scannerLauncher,
              activity = activity
            )
          }
        }
      }
    }
  }

  @Composable
  fun BoxScope.PageCarousel(
    viewModel: MainViewModel,
    screenWidth: androidx.compose.ui.unit.Dp,
    carouselPadding: androidx.compose.ui.unit.Dp,
    pageHeight: androidx.compose.ui.unit.Dp = 500.dp // Default page height
  ) {
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .height(pageHeight)
        .align(Alignment.Center),
      contentPadding = PaddingValues(
        horizontal = carouselPadding,
        vertical = carouselPadding
      ), // For peeking effect
      horizontalArrangement = Arrangement.spacedBy(carouselPadding) // Space between items
    ) {
      val roundedCornerShape = Shapes.large as RoundedCornerShape
      items(viewModel.recognizedImages) { uri ->
        Column(horizontalAlignment = CenterHorizontally) {
          Box( // Item wrapper for styling
            modifier = Modifier
              .width(screenWidth * 0.80F)
              .height(400.dp) // Adjusted height to make space for the button
              .background(Color.LightGray, roundedCornerShape)
              .border(BorderStroke(2.dp, Color.Gray), roundedCornerShape)
              .clip(roundedCornerShape)
          ) {
            AsyncImage(
              model = uri,
              contentDescription = "Scanned Document Page",
              modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)), // Match item shape
              contentScale = ContentScale.Fit // Match item shape
            )
          }
          Button(
            onClick = { /* TODO: Implement text extraction logic */ },
            modifier = Modifier
              .padding(top = 8.dp)
              .height(100.dp)
          ) {
            Icon(
              painter = painterResource(id = android.R.drawable.ic_menu_search), // Replace with your desired icon
              contentDescription = "Extract Text Icon",
              modifier = Modifier.size(24.dp)
            )
            Text(
              text = "Extract Text",
              modifier = Modifier.padding(start = 8.dp)
            )
          }
        }
      }
    }
  }

  @Composable
  fun BoxScope.ScanButton(
    viewModel: MainViewModel,
    scannerLauncher: androidx.activity.result.ActivityResultLauncher<IntentSenderRequest>,
    activity: Activity?
  ) {
    // Camera button at the bottom center
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter) // Align to bottom center of the parent Box
        .padding(bottom = 32.dp) // Padding from the bottom edge
        .size(64.dp) // Set a fixed size for the circular button
        .clip(CircleShape) // Clip to a circle
        .background(MaterialTheme.colors.primary) // Background color
        .clickable { // Click listener on the Box
          activity?.let {
            viewModel
              .prepareScanner()
              .getStartScanIntent(it)
              .addOnSuccessListener { intentSender ->
                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
              }
          }
        }
        .wrapContentSize(Alignment.Center) // Center the Icon within this Box
    ) {
      Icon(
        Icons.Filled.AutoAwesome,
        contentDescription = "Scan Document",
        tint = Color.White
      )
    }
  }
}

