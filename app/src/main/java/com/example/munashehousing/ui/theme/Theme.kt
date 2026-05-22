package com.example.munashehousing.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = RealBlue,
    onPrimary = PureWhite,
    secondary = AccentBlue,
    onSecondary = PureWhite,
    background = OffWhite,
    surface = PureWhite,
    onBackground = SolidBlack,
    onSurface = SolidBlack,
    surfaceVariant = LightBlue,
    onSurfaceVariant = RealBlue
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = PureWhite,
    secondary = LightBlue,
    background = SolidBlack,
    surface = DarkGray,
    onBackground = PureWhite,
    onSurface = PureWhite
)

@Composable
fun MunasheHousingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}