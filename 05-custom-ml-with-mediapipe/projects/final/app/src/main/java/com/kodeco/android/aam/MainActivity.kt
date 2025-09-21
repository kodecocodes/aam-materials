package com.kodeco.android.aam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kodeco.android.aam.chat.ChatRoute
import com.kodeco.android.aam.chat.ChatViewModel
import com.kodeco.android.aam.ui.theme.KodecoSampleTheme

class MainActivity : ComponentActivity() {

  private val viewModel: ChatViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      KodecoSampleTheme {
        val navController = rememberNavController()

        Scaffold { innerPadding ->
          NavHost(
            navController = navController,
            startDestination = "chat",
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues = innerPadding)
          ) {
            composable(route = "chat") {
              ChatRoute(viewModel = viewModel)
            }
          }
        }
      }
    }
  }
}
