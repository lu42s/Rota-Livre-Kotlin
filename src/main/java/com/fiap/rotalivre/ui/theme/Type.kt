package com.fiap.rotalivre.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp

private val base = Typography()

// Textos um pouco maiores que o padrão do Material 3, pensando em idosos e pessoas com baixa visão.
val Typography = Typography(
    headlineSmall = base.headlineSmall.copy(fontSize = 26.sp, lineHeight = 34.sp),
    titleLarge = base.titleLarge.copy(fontSize = 24.sp, lineHeight = 30.sp),
    titleMedium = base.titleMedium.copy(fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = base.bodyLarge.copy(fontSize = 18.sp, lineHeight = 26.sp),
    bodyMedium = base.bodyMedium.copy(fontSize = 16.sp, lineHeight = 22.sp),
    bodySmall = base.bodySmall.copy(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = base.labelLarge.copy(fontSize = 16.sp, lineHeight = 22.sp),
    labelMedium = base.labelMedium.copy(fontSize = 14.sp, lineHeight = 20.sp)
)
