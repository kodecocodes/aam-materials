package com.kodeco.android.aam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kodeco.android.aam.aipack.AiPackStatus
import com.kodeco.android.aam.chat.ChatRoute
import com.kodeco.android.aam.chat.ChatViewModel
import com.kodeco.android.aam.ui.theme.KodecoSampleTheme

class MainActivity : ComponentActivity() {

  private val mainViewModel: MainViewModel by viewModels()
  private val chatViewModel: ChatViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      KodecoSampleTheme {
        val navController = rememberNavController()
        val aiPackStatus by mainViewModel.aiPackStatus.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
          mainViewModel.checkPackStatus(MainViewModel.AI_PACK_NAME)
        }

        Scaffold { innerPadding ->
          NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues = innerPadding)
          ) {
            composable(route = "main") {
              // Handle the AI pack status
              when (val currentStatus = aiPackStatus) {

                is AiPackStatus.Unknown -> {
                  MainScreen(statusText = "Checking for AI Model...", isDownloading = true)
                }

                is AiPackStatus.NotInstalled -> {
                  MainScreen(statusText = "AI Model Not Installed.", isDownloading = false) {
                    mainViewModel.fetchAiPacks()
                  }
                }

                is AiPackStatus.RequestConfirmation -> {
                  MainScreen(statusText = "Requesting confirmation...", isDownloading = false) {
                    mainViewModel.showConfirmationDialog(activityResultLauncher)
                  }
                }

                is AiPackStatus.Downloading -> {
                  MainScreen(statusText = "Downloading AI Model...", isDownloading = true)
                }

                is AiPackStatus.Installed -> {
                  ChatRoute(viewModel = chatViewModel, modelPath = currentStatus.location)
                }

                is AiPackStatus.Failed -> {
                  MainScreen(
                    statusText = "Error downloading model. Code: ${currentStatus.errorCode}",
                    isDownloading = false
                  ) {
                    mainViewModel.fetchAiPacks()
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private val activityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartIntentSenderForResult()
  ) { result ->
    when (result.resultCode) {
      RESULT_OK -> {
        mainViewModel.fetchAiPacks()
      }

      RESULT_CANCELED -> {
        mainViewModel.cancelRequests()
      }

      else -> { // Do nothing }
      }
    }
  }
}
