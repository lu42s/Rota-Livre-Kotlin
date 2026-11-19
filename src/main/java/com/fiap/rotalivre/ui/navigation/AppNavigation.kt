package com.fiap.rotalivre.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fiap.rotalivre.ui.cadastro.CadastroScreen
import com.fiap.rotalivre.ui.detalhes.DetalhesScreen
import com.fiap.rotalivre.ui.home.HomeScreen
import com.fiap.rotalivre.viewmodel.OcorrenciaViewModel

/** Rotas de navegação do app. */
object Rotas {
    const val HOME = "home"
    const val CADASTRO = "cadastro"
    const val DETALHES = "detalhes/{id}"
    fun detalhes(id: Int) = "detalhes/$id"
}

/**
 * Evita navegação dupla (ex.: tocar duas vezes rápido no botão "voltar"),
 * que poderia tirar a tela inicial da pilha e deixar o app em branco.
 */
private fun NavBackStackEntry.estaAtiva() = lifecycle.currentState == Lifecycle.State.RESUMED

@Composable
fun AppNavigation() {
    // Um único ViewModel compartilhado pelas três telas
    val viewModel: OcorrenciaViewModel = viewModel()
    val navController = rememberNavController()

    // Snackbar compartilhado: a mensagem continua visível mesmo depois de trocar de tela
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.mensagens.collect { mensagem ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(mensagem)
        }
    }

    NavHost(navController = navController, startDestination = Rotas.HOME) {

        composable(Rotas.HOME) { entry ->
            HomeScreen(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                onNovoClick = {
                    if (entry.estaAtiva()) navController.navigate(Rotas.CADASTRO) { launchSingleTop = true }
                },
                onCardClick = { id ->
                    if (entry.estaAtiva()) navController.navigate(Rotas.detalhes(id)) { launchSingleTop = true }
                }
            )
        }

        composable(Rotas.CADASTRO) { entry ->
            CadastroScreen(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                onVoltar = { if (entry.estaAtiva()) navController.popBackStack() }
            )
        }

        composable(
            route = Rotas.DETALHES,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            DetalhesScreen(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                id = id,
                onVoltar = { if (entry.estaAtiva()) navController.popBackStack() }
            )
        }
    }
}
