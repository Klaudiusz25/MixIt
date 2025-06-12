package com.apkmob.mixit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8B4513),  // brązowy (kolor whisky)
    secondary = Color(0xFFDAA520), // złoty
    tertiary = Color(0xFFA52A2A),  // brązowo-czerwony
    background = Color(0xFFFFF8DC), // kremowy
    surface = Color(0xFFFFF8DC),
    surfaceVariant = Color(0xFFF5E6C8), // jaśniejszy kremowy dla kart
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF333333),
    onSurface = Color(0xFF333333),
    onSurfaceVariant = Color(0xFF333333), // kolor tekstu na surfaceVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFDAA520),  // złoty
    secondary = Color(0xFF8B4513), // brązowy
    tertiary = Color(0xFFCD5C5C),  // czerwony
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2D2D2D), // ciemniejszy wariant dla kart
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFFE0E0E0), // kolor tekstu na surfaceVariant
)

val MixItTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)

@Composable
fun MixItTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MixItTypography,
        content = content
    )
}