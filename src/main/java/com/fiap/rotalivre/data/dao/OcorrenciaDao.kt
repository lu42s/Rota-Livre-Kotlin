package com.fiap.rotalivre.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fiap.rotalivre.data.entity.Ocorrencia
import kotlinx.coroutines.flow.Flow

@Dao
interface OcorrenciaDao {

    @Insert
    suspend fun inserir(ocorrencia: Ocorrencia)

    @Query("SELECT * FROM ocorrencias ORDER BY dataRegistro DESC")
    fun listarTodas(): Flow<List<Ocorrencia>>

    /** Observa uma ocorrência: a tela de detalhes se atualiza sozinha quando o registro muda. */
    @Query("SELECT * FROM ocorrencias WHERE id = :id")
    fun observarPorId(id: Int): Flow<Ocorrencia?>

    @Update
    suspend fun atualizar(ocorrencia: Ocorrencia)

    @Delete
    suspend fun excluir(ocorrencia: Ocorrencia)
}
