package com.example.iiushka.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LushSenseColorScheme = darkColorScheme(
    primary = AppColors.Accent,
    onPrimary = AppColors.Background,
    background = AppColors.Background,
    onBackground = AppColors.TextPrimary,
    surface = AppColors.Surface,
    onSurface = AppColors.TextPrimary,
    error = AppColors.Negative,
)

@Composable
fun LushSenseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LushSenseColorScheme,
        content = content,
    )
}
