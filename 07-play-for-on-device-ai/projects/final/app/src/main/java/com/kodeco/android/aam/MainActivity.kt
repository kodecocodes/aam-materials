/*
 * Copyright (c) 2025 Kodeco Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
