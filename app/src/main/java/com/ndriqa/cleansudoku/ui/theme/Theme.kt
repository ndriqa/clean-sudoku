package com.ndriqa.cleansudoku.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = DeepPurple,
    onPrimary = Color.White,
    primaryContainer = DarkBlue,
    onPrimaryContainer = SoftSilver,
    secondary = SoftGold,
    onSecondary = Color.Black,
    background = DarkBlue,
    onBackground = SoftSilver,
    surface = DeepPurple,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = LightGradientStart,
    onPrimary = Color.Black,
    primaryContainer = LightGradientEnd,
    onPrimaryContainer = DarkBlue,
    secondary = SoftGold,
    onSecondary = Color.Black,
    background = LightGradientEnd,
    onBackground = DarkBlue,
    surface = LightGradientStart,
    onSurface = DarkBlue,
)

@Composable
fun CleanSudokuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NdriqaTypography,
        content = content
    )
}