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

private val LightColorScheme =
  lightColorScheme(
    primary = TjBlue,
    onPrimary = Color.White,
    primaryContainer = TjBlueLight,
    onPrimaryContainer = TjBlueDark,
    secondary = ColorMrt,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2F1),
    onSecondaryContainer = Color(0xFF004D40),
    tertiary = TransitCaution,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = NeutralTextPrimary,
    surface = SurfaceLight,
    onSurface = NeutralTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = NeutralTextSecondary,
    outline = NeutralBorder
  )

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF82B1FF),
    onPrimary = Color(0xFF002B66),
    primaryContainer = Color(0xFF004098),
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = Color(0xFF80CBC4),
    background = Color(0xFF121417),
    surface = Color(0xFF1E2125),
    onBackground = Color(0xFFE2E2E6),
    onSurface = Color(0xFFE2E2E6)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
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
