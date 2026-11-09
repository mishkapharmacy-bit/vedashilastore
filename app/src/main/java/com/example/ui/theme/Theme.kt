package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = VedashilaDarkGold,
  onPrimary = Color(0xFF1B1400),
  primaryContainer = VedashilaPrimaryLight,
  onPrimaryContainer = Color(0xFFD8F2E7),
  secondary = VedashilaDarkGold,
  onSecondary = Color(0xFF1F1600),
  secondaryContainer = Color(0xFF332600),
  onSecondaryContainer = Color(0xFFFFE088),
  background = VedashilaDarkBackground,
  onBackground = VedashilaDarkText,
  surface = VedashilaDarkSurface,
  onSurface = VedashilaDarkText,
  surfaceVariant = VedashilaDarkCard,
  onSurfaceVariant = VedashilaDarkMuted,
  outline = Color(0xFF2C4438)
)

private val LightColorScheme = lightColorScheme(
  primary = VedashilaPrimaryGreen,
  onPrimary = Color.White,
  primaryContainer = VedashilaContainerGreen,
  onPrimaryContainer = VedashilaPrimaryGreen,
  secondary = VedashilaGold,
  onSecondary = Color.White,
  secondaryContainer = VedashilaGoldContainer,
  onSecondaryContainer = Color(0xFF443300),
  background = VedashilaParchment,
  onBackground = VedashilaTextDark,
  surface = VedashilaSurface,
  onSurface = VedashilaTextDark,
  surfaceVariant = Color(0xFFF0F5F2),
  onSurfaceVariant = VedashilaTextMuted,
  outline = VedashilaBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep distinct brand colors
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
