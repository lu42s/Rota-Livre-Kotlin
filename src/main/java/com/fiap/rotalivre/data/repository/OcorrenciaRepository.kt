package com.fiap.rotalivre.data.repository

import com.fiap.rotalivre.data.dao.OcorrenciaDao
import com.fiap.rotalivre.data.entity.Ocorrencia
import kotlinx.coroutines.flow.Flow

class OcorrenciaRepository(private val dao: OcorrenciaDao) {

    val ocorrencias: Flow<List<Ocorrencia>> = dao.listarTodas()

    fun observarPorId(id: Int): Flow<Ocorrencia?> = dao.observarPorId(id)

    suspend fun inserir(ocorrencia: Ocorrencia) = dao.inserir(ocorrencia)

    suspend fun atualizar(ocorrencia: Ocorrencia) = dao.atualizar(ocorrencia)

    suspend fun excluir(ocorrencia: Ocorrencia) = dao.excluir(ocorrencia)
}
