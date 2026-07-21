package com.cactus.app.ui.theme

import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.foundation.shape.RoundedCornerShape

/* PRD §6 — Inter for Latin, Noto Sans Bengali for Bangla UI/translation.
 * In a shipped build you would bundle the TTF/OTF files under res/font and
 * reference them here (Font(resId)). For this prototype we use the platform
 * sans-serif families, which render equivalently on device. */
val InterFont = FontFamily.SansSerif
val BengaliFont = FontFamily.Default

val WeightLight = FontWeight.Light
val WeightNormal = FontWeight.Normal
val WeightMedium = FontWeight.Medium
val WeightBold = FontWeight.Bold
val WeightExtraBold = FontWeight.ExtraBold

/* PRD §4.2 / §10 — Soft, rounded corners (24dp+) signal approachability;
 * bottom sheets use a 32dp top radius. */
val CactusShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

/* PRD §6 type scale. Headlines ExtraBold 32–36sp, body 14–16sp,
 * captions 10–12sp, pronunciation Light 12sp. */
val CactusTypography = Typography(
    displaySmall = TextStyle(fontFamily = InterFont, fontWeight = WeightExtraBold, fontSize = 32.sp, lineHeight = 38.sp, platformStyle = PlatformTextStyle(includeFontPadding = false)),
    headlineMedium = TextStyle(fontFamily = InterFont, fontWeight = WeightExtraBold, fontSize = 28.sp, lineHeight = 34.sp, platformStyle = PlatformTextStyle(includeFontPadding = false)),
    headlineSmall = TextStyle(fontFamily = InterFont, fontWeight = WeightExtraBold, fontSize = 22.sp, lineHeight = 28.sp, platformStyle = PlatformTextStyle(includeFontPadding = false)),
    titleLarge = TextStyle(fontFamily = InterFont, fontWeight = WeightBold, fontSize = 18.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = InterFont, fontWeight = WeightMedium, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = InterFont, fontWeight = WeightNormal, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = InterFont, fontWeight = WeightNormal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = InterFont, fontWeight = WeightLight, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = InterFont, fontWeight = WeightMedium, fontSize = 14.sp, lineHeight = 18.sp),
    labelMedium = TextStyle(fontFamily = InterFont, fontWeight = WeightMedium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = InterFont, fontWeight = WeightMedium, fontSize = 10.sp, lineHeight = 14.sp),
)

/* Convenience styles for Bangla text (PRD §6). */
val BengaliBody = TextStyle(fontFamily = BengaliFont, fontWeight = WeightNormal, fontSize = 14.sp, lineHeight = 22.sp)
val BengaliTitle = TextStyle(fontFamily = BengaliFont, fontWeight = WeightNormal, fontSize = 20.sp, lineHeight = 28.sp)
val PronunciationStyle = TextStyle(fontFamily = InterFont, fontWeight = WeightLight, fontSize = 12.sp, lineHeight = 16.sp, color = onSurfaceVariant)
