package com.blackcat.android.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val BlackCatColorScheme = darkColorScheme(
    primary = AccentPrimary,
    onPrimary = ShadowLevel1,
    primaryContainer = AccentSecondary,
    onPrimaryContainer = ShadowLevel1,
    secondary = PrimaryBlue,
    onSecondary = ShadowLevel1,
    secondaryContainer = BackgroundCard,
    onSecondaryContainer = ShadowLevel1,
    tertiary = PrimaryGreen,
    onTertiary = ShadowLevel1,
    background = BackgroundPrimary,
    onBackground = ShadowLevel1,
    surface = BackgroundSecondary,
    onSurface = ShadowLevel1,
    surfaceVariant = BackgroundCard,
    onSurfaceVariant = ShadowLevel3,
    error = NaturalRed,
    onError = ShadowLevel1,
    outline = ShadowLevel5
)

@Composable
fun BlackCatTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = BlackCatColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundPrimary.toArgb()
            window.navigationBarColor = BackgroundPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
