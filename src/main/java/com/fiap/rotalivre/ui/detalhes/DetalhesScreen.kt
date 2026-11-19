package com.fiap.rotalivre.ui.detalhes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fiap.rotalivre.data.entity.Ocorrencia
import com.fiap.rotalivre.data.entity.StatusOcorrencia
import com.fiap.rotalivre.ui.components.StatusBadge
import com.fiap.rotalivre.viewmodel.OcorrenciaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.map

/** Estado da tela: ainda carregando, ou carregado (ocorrência pode ser null se não existir). */
private sealed interface EstadoDetalhe {
    object Carregando : EstadoDetalhe
    data class Pronto(val ocorrencia: Ocorrencia?) : EstadoDetalhe
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesScreen(
    viewModel: OcorrenciaViewModel,
    snackbarHostState: SnackbarHostState,
    id: Int,
    onVoltar: () -> Unit
) {
    // Observa o registro no Room: qualquer alteração de status aparece na hora
    val estado by remember(id) {
        viewModel.observarOcorrencia(id).map<Ocorrencia?, EstadoDetalhe> { EstadoDetalhe.Pronto(it) }
    }.collectAsState(initial = EstadoDetalhe.Carregando)

    var confirmarExclusao by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da ocorrência") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (val e = estado) {
            is EstadoDetalhe.Carregando -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is EstadoDetalhe.Pronto -> {
                val occ = e.ocorrencia
                if (occ == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Ocorrência não encontrada.",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    ConteudoDetalhe(
                        ocorrencia = occ,
                        modifier = Modifier.padding(padding),
                        onAlterarStatus = { novo -> viewModel.alterarStatus(occ, novo) },
                        onExcluir = { confirmarExclusao = true }
                    )

                    if (confirmarExclusao) {
                        AlertDialog(
                            onDismissRequest = { confirmarExclusao = false },
                            title = { Text("Excluir ocorrência?") },
                            text = { Text("\"${occ.tipo}\" será removida permanentemente.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    confirmarExclusao = false
                                    viewModel.excluir(occ)
                                    onVoltar()
                                }) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
                            },
                            dismissButton = {
                                TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConteudoDetalhe(
    ocorrencia: Ocorrencia,
    modifier: Modifier,
    onAlterarStatus: (String) -> Unit,
    onExcluir: () -> Unit
) {
    val formatoData = remember { SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.forLanguageTag("pt-BR")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    ocorrencia.tipo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatusBadge(ocorrencia.status)
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                InfoLinha("Local", ocorrencia.localizacao)
                InfoLinha("Perfil mais afetado", ocorrencia.perfilAfetado)
                InfoLinha("Registrada em", formatoData.format(Date(ocorrencia.dataRegistro)))
                InfoLinha("Descrição", ocorrencia.descricao)
            }
        }

        // Ação adicional exigida na rubrica: alterar status
        Text(
            "Ações da comunidade",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Confirme que o problema existe ou avise que ele já foi resolvido.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onAlterarStatus(StatusOcorrencia.VALIDADO) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                enabled = ocorrencia.status == StatusOcorrencia.PENDENTE
            ) { Text("Confirmar") }

            Button(
                onClick = { onAlterarStatus(StatusOcorrencia.RESOLVIDO) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                enabled = ocorrencia.status != StatusOcorrencia.RESOLVIDO,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) { Text("Resolvido") }
        }

        if (ocorrencia.status != StatusOcorrencia.PENDENTE) {
            TextButton(
                onClick = { onAlterarStatus(StatusOcorrencia.PENDENTE) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Voltar para \"Pendente\" (o problema voltou)") }
        }

        OutlinedButton(
            onClick = onExcluir,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(modifier = Modifier.padding(4.dp))
            Text("Excluir ocorrência")
        }
    }
}

@Composable
private fun InfoLinha(rotulo: String, valor: String) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            rotulo,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
