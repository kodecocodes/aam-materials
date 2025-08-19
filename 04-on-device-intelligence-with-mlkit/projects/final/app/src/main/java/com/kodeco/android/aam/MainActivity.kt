package com.kodeco.android.aam

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.kodeco.android.aam.ui.theme.KodecoSampleTheme

/**
 * MainActivity is the main entry point of the application.
 * It uses Jetpack Compose to display a list of cat breeds.
 * The cat breeds data is fetched from a MainViewModel.
 *
 * This activity sets up the UI, observes the cat breeds from the ViewModel,
 * and displays them in a CatBreedsListScreen.
 * When a cat breed item is clicked, a Toast message with the breed name is shown.
 */
class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  // 2: Create Scanner Launcher
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

      KodecoSampleTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
          Button(
            content = {
              Text("Pick Image from Gallery")
            },
            onClick = {
              activity?.let {
                viewModel.prepareScanner()
                  .getStartScanIntent(it)
                  .addOnSuccessListener { intentSender ->
                    scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                  }
              }
            })
        }
      }
    }
  }
}

@Composable
fun CatBreedsListScreen(breeds: List<String>, onItemClick: (String) -> Unit) {
  if (breeds.isEmpty()) {
    Text(text = "No cat breeds available.", modifier = Modifier.padding(16.dp))
    return
  }
  LazyColumn(modifier = Modifier.padding(8.dp)) {
    items(breeds) { breed ->
      Text(
        text = breed,
        modifier = Modifier
          .fillParentMaxWidth()
          .clickable { onItemClick(breed) }
          .padding(vertical = 12.dp, horizontal = 16.dp)
      )
    }
  }
}

@Preview
@Composable
fun CatBreedsListScreenPreview() {
  val breeds = listOf("Abyssinian", "Aegean", "American Bobtail", "American Curl")
  CatBreedsListScreen(breeds = breeds, onItemClick = {})
}

// Greeting composable is removed




