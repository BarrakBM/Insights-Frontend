package com.nbk.insights.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val PrimaryBlue = Color(0xFF0D47A1)
val SuccessGreen = Color(0xFF43A047)
val WarningAmber = Color(0xFFFFC107)
val LightBg = Color(0xFFF5F5F5)
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)

// NBK Brand Colors
val NBKBlue = Color(0xFF1E3A8A)
val NBKBlueLight = Color(0xFF3B5998)
val NBKBlueDark = Color(0xFF0F1A44)

// Primary Colors
val Primary = NBKBlue
val PrimaryVariant = NBKBlueDark
val OnPrimary = Color.White

// Secondary Colors
val Secondary = Color(0xFF10B981)
val SecondaryVariant = Color(0xFF059669)
val OnSecondary = Color.White

// Background Colors
val Background = Color(0xFFFAFAFA)
val Surface = Color.White
val OnBackground = Color(0xFF1A1A1A)
val OnSurface = Color(0xFF1A1A1A)

// Error Colors
val Error = Color(0xFFEF4444)
val OnError = Color.White

// Success Colors
val Success = Color(0xFF10B981)
val Warning = Color(0xFFF59E0B)
val Info = Color(0xFF3B82F6)

// Neutral Colors
val Gray50 = Color(0xFFF9FAFB)
val Gray100 = Color(0xFFF3F4F6)
val Gray200 = Color(0xFFE5E7EB)
val Gray300 = Color(0xFFD1D5DB)
val Gray400 = Color(0xFF9CA3AF)
val Gray500 = Color(0xFF6B7280)
val Gray600 = Color(0xFF4B5563)
val Gray700 = Color(0xFF374151)
val Gray800 = Color(0xFF1F2937)
val Gray900 = Color(0xFF111827)

// Category Colors
val CategoryDining = Color(0xFFEF4444)
val CategoryShopping = Color(0xFF3B82F6)
val CategoryTransport = Color(0xFF10B981)
val CategoryEntertainment = Color(0xFF8B5CF6)
val CategoryUtilities = Color(0xFFF59E0B)
val CategoryHealthcare = Color(0xFFEC4899)
val CategoryOther = Color(0xFF6B7280)

// Additional colors used in composables
val BackgroundLight = Color(0xFFF5F5F5)
val NBKBlueAlpha10 = Color(0x1A1E3A8A)
val NBKBlueAlpha20 = Color(0x331E3A8A)
val LightGray = Color(0xFFF8F9FA)
val VeryLightGray = Color(0xFFF8FAFC)
val Purple = Color(0xFF7C3AED)
val PurpleAlpha10 = Color(0x1A7C3AED)
val Cyan = Color(0xFF06B6D4)
val Blue = Color(0xFF3B82F6)
val DarkBackground = Color(0xFF262E38)
val LightBackground = Color(0xFFF5F4FA)
val Teal = Color(0xFF2ED2C0)
val Red = Color(0xFFFF0000)

// Light Theme Colors
val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = Color(0xFFE1E7FF),
    onPrimaryContainer = Color(0xFF001A41),

    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF022C22),

    tertiary = Color(0xFF8B5CF6),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEDE9FE),
    onTertiaryContainer = Color(0xFF2E1065),

    error = Error,
    onError = OnError,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Background,
    onBackground = OnBackground,

    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray600,

    outline = Gray300,
    outlineVariant = Gray200,

    scrim = Color.Black,
    inverseSurface = Gray800,
    inverseOnSurface = Gray100,
    inversePrimary = Color(0xFF9CB4FF)
)

// Dark Theme Colors
val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = Color(0xFF9CB4FF),
    onPrimary = Color(0xFF001A41),
    primaryContainer = Color(0xFF0F2C5C),
    onPrimaryContainer = Color(0xFFE1E7FF),

    secondary = Color(0xFF6EE7B7),
    onSecondary = Color(0xFF022C22),
    secondaryContainer = Color(0xFF047857),
    onSecondaryContainer = Color(0xFFD1FAE5),

    tertiary = Color(0xFFC4B5FD),
    onTertiary = Color(0xFF2E1065),
    tertiaryContainer = Color(0xFF5B21B6),
    onTertiaryContainer = Color(0xFFEDE9FE),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF410002),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF0F172A),
    onBackground = Color(0xFFE2E8F0),

    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),

    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF475569),

    scrim = Color.Black,
    inverseSurface = Color(0xFFE2E8F0),
    inverseOnSurface = Color(0xFF1E293B),
    inversePrimary = Primary
)