package com.kodeco.android.aam

import android.app.Activity
import android.content.Intent
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.kodeco.android.aam.ui.theme.KodecoSampleTheme
import com.kodeco.android.aam.ui.theme.Shapes
import com.kodeco.android.aam.ui.theme.carouselPadding

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
      MainScreen(viewModel = viewModel)
    }
  }

  @Composable
  fun BoxScope.PageCarousel(
    viewModel: MainViewModel,
  ) {
    val pageHeight = 500.dp
    val pageWidth = LocalConfiguration.current.screenWidthDp.dp * 0.80F
    val shape = Shapes.large as RoundedCornerShape

    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .height(pageHeight)
        .align(Alignment.Center),
      contentPadding = PaddingValues(horizontal = carouselPadding, vertical = carouselPadding),
      horizontalArrangement = Arrangement.spacedBy(carouselPadding)
    ) {
      items(viewModel.pageUris) { uri ->
        PageItem(
          uri = uri,
          itemWidth = pageWidth,
          itemShape = shape,
        )
      }
    }
  }

  @Composable
  fun PageItem(
    uri: android.net.Uri,
    itemWidth: Dp,
    itemShape: RoundedCornerShape,
  ) {
    Box(
      modifier = Modifier
        .width(itemWidth)
        .fillMaxHeight()
        .background(Color.DarkGray, itemShape)
        .border(BorderStroke(2.dp, Color.Gray), itemShape)
        .clip(itemShape)
    ) {
      val context = LocalContext.current

      // Image
      AsyncImage(
        model = uri,
        contentDescription = "Scanned Document Page",
        modifier = Modifier
          .fillMaxSize()
          .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Fit
      )
      // Button
      Button(
        onClick = {
          viewModel.getTextFromImage(uri) { extractedText ->
            extractedText?.let {
              val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                  Intent.EXTRA_TEXT,
                  it
                )
                type = "text/plain"
              }
              val shareIntent = Intent.createChooser(sendIntent, "Text from Image")
              context.startActivity(shareIntent)
            }
          }
        },
        modifier = Modifier.align(Alignment.TopEnd),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        elevation = ButtonDefaults.elevation(
          defaultElevation = 0.dp,
          pressedElevation = 0.dp,
          hoveredElevation = 0.dp,
          focusedElevation = 0.dp
        )
      ) {
        Text(text = "Extract Text", color = Color.White)
        Icon(
          imageVector = Icons.Filled.AutoAwesome,
          contentDescription = "Extract Text Icon",
          modifier = Modifier
            .size(32.dp)
            .padding(horizontal = 8.dp),
          tint = Color.White
        )
      }
    }
  }

  @Composable
  fun BoxScope.ScanButton(
    viewModel: MainViewModel,
  ) {
    val context = LocalContext.current
    val activity = context as? Activity

    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 32.dp)
        .size(64.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colors.primary)
        .clickable {
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
        imageVector = Icons.Filled.DocumentScanner,
        contentDescription = "Scan Document",
        tint = Color.White
      )
    }
  }

  @Composable
  fun MainScreen(viewModel: MainViewModel) {
    KodecoSampleTheme {
      Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Box(modifier = Modifier.fillMaxSize()) {
          PageCarousel(viewModel = viewModel)
          ScanButton(viewModel = viewModel)
        }
      }
    }
  }

}
