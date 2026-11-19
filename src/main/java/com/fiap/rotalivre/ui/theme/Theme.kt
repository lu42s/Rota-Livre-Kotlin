package com.fiap.rotalivre.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = AzulContainer,
    onPrimaryContainer = AzulEscuroTexto,
    secondary = VerdeSecundario,
    onSecondary = Color.White,
    secondaryContainer = VerdeContainer,
    onSecondaryContainer = VerdeEscuroTexto,
    tertiary = AmbarTerciario,
    onTertiary = Color.White,
    tertiaryContainer = AmbarContainer,
    onTertiaryContainer = AmbarEscuroTexto
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulPrimarioEscuro,
    onPrimary = AzulEscuroTexto,
    primaryContainer = AzulContainerEscuro,
    onPrimaryContainer = AzulContainer,
    secondary = VerdeSecundarioEscuro,
    onSecondary = VerdeEscuroTexto,
    secondaryContainer = VerdeContainerEscuro,
    onSecondaryContainer = VerdeContainer,
    tertiary = AmbarTerciarioEscuro,
    onTertiary = AmbarEscuroTexto,
    tertiaryContainer = AmbarContainerEscuro,
    onTertiaryContainer = AmbarContainer
)

/**
 * Tema do app. As cores dinâmicas do Android 12+ foram desativadas de propósito,
 * para manter a identidade visual do RotaLivre em qualquer aparelho.
 */
@Composable
fun RotaLivreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
