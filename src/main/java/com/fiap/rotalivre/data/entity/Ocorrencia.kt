package com.fiap.rotalivre.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Ocorrência de acessibilidade reportada pela comunidade:
 * um obstáculo na calçada, uma estação sem rampa, um elevador ou escada rolante parados etc.
 */
@Entity(tableName = "ocorrencias")
data class Ocorrencia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipo: String,            // Ex.: "Calçada danificada", "Elevador quebrado"
    val descricao: String,       // Detalhes do problema
    val localizacao: String,     // Ex.: "Estação Sé - Saída 2"
    val perfilAfetado: String,   // Quem é mais prejudicado (ver PerfilAfetado)
    val status: String = StatusOcorrencia.PENDENTE,
    val dataRegistro: Long = System.currentTimeMillis()
)

/** Status possíveis de uma ocorrência. */
object StatusOcorrencia {
    const val PENDENTE = "Pendente"
    const val VALIDADO = "Validado"
    const val RESOLVIDO = "Resolvido"
}

/** Tipos de limitação de mobilidade atendidos pelo app. */
object PerfilAfetado {
    const val CADEIRA_DE_RODAS = "Cadeira de rodas"
    const val MULETAS_ANDADOR = "Muletas ou andador"
    const val CARRINHO_DE_BEBE = "Carrinho de bebê"
    const val IDOSOS_GESTANTES = "Idosos e gestantes"
    const val DEFICIENTE_VISUAL = "Deficiente visual"
    const val TODOS = "Todos os perfis"

    val opcoes = listOf(CADEIRA_DE_RODAS, MULETAS_ANDADOR, CARRINHO_DE_BEBE, IDOSOS_GESTANTES, DEFICIENTE_VISUAL, TODOS)
}

/** Sugestões rápidas de tipo de problema para facilitar o preenchimento. */
val TIPOS_SUGERIDOS = listOf(
    "Calçada danificada",
    "Falta de rampa",
    "Elevador quebrado",
    "Escada rolante parada",
    "Obstáculo na calçada",
    "Falta de piso tátil"
)
