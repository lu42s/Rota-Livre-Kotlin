package com.fiap.rotalivre.ui.cadastro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.fiap.rotalivre.data.entity.PerfilAfetado
import com.fiap.rotalivre.data.entity.TIPOS_SUGERIDOS
import com.fiap.rotalivre.viewmodel.OcorrenciaViewModel

private const val MIN_DESCRICAO = 10

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CadastroScreen(
    viewModel: OcorrenciaViewModel,
    snackbarHostState: SnackbarHostState,
    onVoltar: () -> Unit
) {
    // rememberSaveable: o formulário não se perde ao girar a tela
    var tipo by rememberSaveable { mutableStateOf("") }
    var localizacao by rememberSaveable { mutableStateOf("") }
    var descricao by rememberSaveable { mutableStateOf("") }
    var perfil by rememberSaveable { mutableStateOf<String?>(null) }

    var erroTipo by rememberSaveable { mutableStateOf<String?>(null) }
    var erroLocal by rememberSaveable { mutableStateOf<String?>(null) }
    var erroDescricao by rememberSaveable { mutableStateOf<String?>(null) }
    var erroPerfil by rememberSaveable { mutableStateOf<String?>(null) }

    fun validar(): Boolean {
        erroTipo = if (tipo.isBlank()) "Informe o tipo do problema." else null
        erroLocal = if (localizacao.isBlank()) "Informe onde fica o problema." else null
        erroDescricao = if (descricao.trim().length < MIN_DESCRICAO)
            "A descrição deve ter no mínimo $MIN_DESCRICAO caracteres." else null
        erroPerfil = if (perfil == null) "Selecione quem é mais afetado." else null
        return listOf(erroTipo, erroLocal, erroDescricao, erroPerfil).all { it == null }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar problema") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Campos com * são obrigatórios.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // --- Tipo do problema ---
            OutlinedTextField(
                value = tipo,
                onValueChange = {
                    tipo = it
                    if (it.isNotBlank()) erroTipo = null
                },
                label = { Text("Tipo do problema *") },
                placeholder = { Text("Ex.: Elevador quebrado") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = erroTipo != null,
                supportingText = { erroTipo?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
            Text("Sugestões rápidas:", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TIPOS_SUGERIDOS.forEach { sugestao ->
                    AssistChip(
                        onClick = {
                            tipo = sugestao
                            erroTipo = null
                        },
                        label = { Text(sugestao) }
                    )
                }
            }

            // --- Localização ---
            OutlinedTextField(
                value = localizacao,
                onValueChange = {
                    localizacao = it
                    if (it.isNotBlank()) erroLocal = null
                },
                label = { Text("Localização *") },
                placeholder = { Text("Ex.: Estação Sé, saída 2") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = erroLocal != null,
                supportingText = { erroLocal?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            // --- Perfil afetado (diferencial: o tipo de limitação da pessoa) ---
            Text(
                "Quem é mais afetado? *",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PerfilAfetado.opcoes.forEach { opcao ->
                    val ativo = perfil == opcao
                    FilterChip(
                        selected = ativo,
                        onClick = {
                            perfil = opcao
                            erroPerfil = null
                        },
                        label = { Text(opcao) },
                        leadingIcon = if (ativo) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                }
            }
            erroPerfil?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // --- Descrição ---
            OutlinedTextField(
                value = descricao,
                onValueChange = {
                    descricao = it
                    if (it.trim().length >= MIN_DESCRICAO) erroDescricao = null
                },
                label = { Text("Descrição detalhada *") },
                placeholder = { Text("Descreva o problema (mínimo $MIN_DESCRICAO caracteres)") },
                modifier = Modifier.fillMaxWidth(),
                isError = erroDescricao != null,
                supportingText = {
                    Text(erroDescricao ?: "${descricao.trim().length} caracteres")
                },
                minLines = 3,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Button(
                onClick = {
                    if (validar()) {
                        viewModel.salvarOcorrencia(tipo, descricao, localizacao, perfil!!)
                        onVoltar()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Registrar ocorrência")
            }
        }
    }
}
