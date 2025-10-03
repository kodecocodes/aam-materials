package com.kodeco.android.aam.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryDark,
  onPrimary = OnPrimaryDark,
  primaryContainer = PrimaryContainerDark,
)

private val LightColorScheme = lightColorScheme(
  primary = PrimaryLight,
  onPrimary = OnPrimaryLight,
  primaryContainer = PrimaryContainerLight,
)

@Composable
fun KodecoSampleTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) {
    DarkColorScheme
  } else {
    LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    content = content
  )
}