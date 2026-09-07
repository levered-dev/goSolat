package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GeminiDarkPrimary,
    onPrimary = GeminiDarkOnPrimary,
    primaryContainer = GeminiDarkPrimaryContainer,
    onPrimaryContainer = GeminiDarkOnPrimaryContainer,
    secondary = GeminiPurple,
    onSecondary = Color.White,
    secondaryContainer = GeminiDarkSecondaryContainer,
    onSecondaryContainer = GeminiDarkOnPrimaryContainer,
    tertiary = GeminiPink,
    background = GeminiDarkBackground,
    onBackground = GeminiDarkTextPrimary,
    surface = GeminiDarkSurface,
    onSurface = GeminiDarkTextPrimary,
    surfaceVariant = GeminiDarkSurfaceVariant,
    onSurfaceVariant = GeminiDarkTextSecondary,
    outline = GeminiDarkOutline,
    outlineVariant = GeminiDarkOutlineVariant
  )

private val LightColorScheme =
  lightColorScheme(
    primary = GeminiLightPrimary,
    onPrimary = Color.White,
    primaryContainer = GeminiLightPrimaryContainer,
    onPrimaryContainer = GeminiLightOnPrimaryContainer,
    secondary = GeminiIndigo,
    onSecondary = Color.White,
    secondaryContainer = GeminiLightSecondaryContainer,
    onSecondaryContainer = GeminiLightOnSecondaryContainer,
    tertiary = GeminiPink,
    background = GeminiLightBackground,
    onBackground = GeminiLightTextPrimary,
    surface = GeminiLightSurface,
    onSurface = GeminiLightTextPrimary,
    surfaceVariant = GeminiLightSurfaceVariant,
    onSurfaceVariant = GeminiLightTextSecondary,
    outline = GeminiLightOutline,
    outlineVariant = GeminiLightOutlineVariant
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our handcrafted Islamic color scheme
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
