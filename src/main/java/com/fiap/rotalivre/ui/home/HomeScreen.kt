package com.fiap.rotalivre.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fiap.rotalivre.data.entity.Ocorrencia
import com.fiap.rotalivre.data.entity.PerfilAfetado
import com.fiap.rotalivre.ui.components.StatusBadge
import com.fiap.rotalivre.viewmodel.OcorrenciaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: OcorrenciaViewModel,
    snackbarHostState: SnackbarHostState,
    onNovoClick: () -> Unit,
    onCardClick: (Int) -> Unit
) {
    val ocorrencias by viewModel.ocorrencias.collectAsState()
    val filtro by viewModel.filtroPerfil.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RotaLivre", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNovoClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Reportar problema") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            // Espaço extra embaixo para o botão flutuante não cobrir o último card
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp)
        ) {
            item { CardApresentacao() }

            item {
                Text(
                    text = "Ocorrências da comunidade",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                FiltroPerfis(
                    selecionado = filtro,
                    onSelecionar = viewModel::filtrarPorPerfil
                )
            }

            val lista = ocorrencias
            when {
                lista == null -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }

                lista.isEmpty() -> item {
                    EstadoVazio(filtroAtivo = filtro != null)
                }

                else -> items(lista, key = { it.id }) { ocorrencia ->
                    OcorrenciaCard(
                        ocorrencia = ocorrencia,
                        onClick = { onCardClick(ocorrencia.id) }
                    )
                }
            }
        }
    }
}

/** Apresenta o app: nome, objetivo, problema e público beneficiado (critério 1 da rubrica). */
@Composable
private fun CardApresentacao() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Accessible,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Rotas sem obstáculos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Calçada quebrada, falta de rampa ou elevador parado podem impedir alguém " +
                    "de chegar ao destino. Aqui a comunidade reporta esses obstáculos e acompanha " +
                    "a acessibilidade de ruas e estações.",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Groups, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Para pessoas com deficiência, idosos, gestantes e quem usa carrinho de bebê.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ODS 10 · Redução das desigualdades   |   ODS 11 · Cidades sustentáveis",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

/** Filtro pelo tipo de limitação: mostra o que afeta cada perfil de mobilidade. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltroPerfis(selecionado: String?, onSelecionar: (String?) -> Unit) {
    val opcoes: List<String?> = listOf(null) + PerfilAfetado.opcoes.filter { it != PerfilAfetado.TODOS }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(opcoes) { perfil ->
            val ativo = perfil == selecionado
            FilterChip(
                selected = ativo,
                onClick = { onSelecionar(perfil) },
                label = { Text(perfil ?: "Todos") },
                leadingIcon = if (ativo) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                } else null
            )
        }
    }
}

@Composable
private fun EstadoVazio(filtroAtivo: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (filtroAtivo) "Nenhuma ocorrência para este perfil."
            else "Nenhuma ocorrência registrada ainda.",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (filtroAtivo) "Escolha outro filtro ou toque em \"Todos\"."
            else "Toque em \"Reportar problema\" para registrar o primeiro obstáculo.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OcorrenciaCard(ocorrencia: Ocorrencia, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "ver detalhes", onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ocorrencia.tipo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(ocorrencia.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = "Local",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = ocorrencia.localizacao, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Accessible,
                    contentDescription = "Perfil afetado",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = ocorrencia.perfilAfetado, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ocorrencia.descricao,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
