package com.fiap.rotalivre.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.rotalivre.data.database.AppDatabase
import com.fiap.rotalivre.data.entity.Ocorrencia
import com.fiap.rotalivre.data.entity.PerfilAfetado
import com.fiap.rotalivre.data.entity.StatusOcorrencia
import com.fiap.rotalivre.data.repository.OcorrenciaRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OcorrenciaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OcorrenciaRepository(
        AppDatabase.getDatabase(application).ocorrenciaDao()
    )

    /** Perfil selecionado no filtro da tela inicial (null = mostrar tudo). */
    private val _filtroPerfil = MutableStateFlow<String?>(null)
    val filtroPerfil: StateFlow<String?> = _filtroPerfil.asStateFlow()

    /**
     * Lista exibida na tela inicial, já filtrada pelo perfil escolhido.
     * Começa como null enquanto o Room carrega, para não piscar a mensagem de "lista vazia".
     */
    val ocorrencias: StateFlow<List<Ocorrencia>?> =
        combine(repository.ocorrencias, _filtroPerfil) { lista, filtro ->
            if (filtro == null) lista
            else lista.filter { it.perfilAfetado == filtro || it.perfilAfetado == PerfilAfetado.TODOS }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Mensagens de confirmação exibidas em Snackbar (salvou, alterou status, excluiu). */
    private val _mensagens = Channel<String>(Channel.BUFFERED)
    val mensagens: Flow<String> = _mensagens.receiveAsFlow()

    fun filtrarPorPerfil(perfil: String?) {
        _filtroPerfil.value = perfil
    }

    fun observarOcorrencia(id: Int): Flow<Ocorrencia?> = repository.observarPorId(id)

    fun salvarOcorrencia(tipo: String, descricao: String, localizacao: String, perfilAfetado: String) {
        viewModelScope.launch {
            repository.inserir(
                Ocorrencia(
                    tipo = tipo.trim(),
                    descricao = descricao.trim(),
                    localizacao = localizacao.trim(),
                    perfilAfetado = perfilAfetado,
                    status = StatusOcorrencia.PENDENTE
                )
            )
            _mensagens.send("Ocorrência registrada com sucesso!")
        }
    }

    fun alterarStatus(ocorrencia: Ocorrencia, novoStatus: String) {
        viewModelScope.launch {
            repository.atualizar(ocorrencia.copy(status = novoStatus))
            _mensagens.send("Status alterado para \"$novoStatus\".")
        }
    }

    fun excluir(ocorrencia: Ocorrencia) {
        viewModelScope.launch {
            repository.excluir(ocorrencia)
            _mensagens.send("Ocorrência excluída.")
        }
    }
}
