package com.fiap.rotalivre.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fiap.rotalivre.data.entity.StatusOcorrencia

/** Etiqueta colorida com o status da ocorrência (usada na lista e nos detalhes). */
@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val cores = MaterialTheme.colorScheme
    val (fundo, texto) = when (status) {
        StatusOcorrencia.PENDENTE -> cores.errorContainer to cores.onErrorContainer
        StatusOcorrencia.VALIDADO -> cores.tertiaryContainer to cores.onTertiaryContainer
        StatusOcorrencia.RESOLVIDO -> cores.secondaryContainer to cores.onSecondaryContainer
        else -> cores.surfaceVariant to cores.onSurfaceVariant
    }

    Surface(
        color = fundo,
        contentColor = texto,
        shape = RoundedCornerShape(50),
        modifier = modifier.semantics { contentDescription = "Status: $status" }
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
