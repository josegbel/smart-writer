package com.example.smartwriter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.smartwriter.R
import com.example.smartwriter.ui.composables.DrawerContent
import com.example.smartwriter.ui.composables.HomeScreen
import com.example.smartwriter.ui.composables.ImageDescScreen
import com.example.smartwriter.ui.composables.ProofreadingScreen
import com.example.smartwriter.ui.composables.SmartWriterAppBar
import com.example.smartwriter.ui.composables.SummarisationScreen
import com.example.smartwriter.ui.composables.TextRewritingScreen
import com.example.smartwriter.ui.model.SummarisationUiEvent
import com.example.smartwriter.ui.theme.SmartWriterTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface NavRoute

private data object HomeRoute : NavRoute

private data object SummarisationRoute : NavRoute

private data object ProofreadingRoute : NavRoute

private data object TextRewritingRoute : NavRoute

private data object ImageDescRoute : NavRoute

@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartWriterTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val uiState by viewModel.state.collectAsStateWithLifecycle()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            DrawerContent(
                                uiState = uiState,
                                onTextSummarisationClicked = {
                                    viewModel.onTextSummarizationClicked()
                                    scope.launch { drawerState.close() }
                                },
                                onProofreadingClicked = {
                                    viewModel.onProofreadingClicked()
                                    scope.launch { drawerState.close() }
                                },
                                onTextRewritingClicked = {
                                    viewModel.onTextRewritingClicked()
                                    scope.launch { drawerState.close() }
                                },
                                onImageDescriptionClicked = {
                                    viewModel.onImageDescriptionClicked()
                                    scope.launch { drawerState.close() }
                                },
                                onHomeClicked = {
                                    viewModel.onHomeClicked()
                                    scope.launch { drawerState.close() }
                                },
                            )
                        }
                    },
                ) {
                    val LocalSnackbarHostState =
                        staticCompositionLocalOf<SnackbarHostState> {
                            error("SnackbarHostState not provided")
                        }

                    val snackbarHostState = remember { SnackbarHostState() }
                    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                        Scaffold(
                            snackbarHost = {
                                SnackbarHost(
                                    hostState = snackbarHostState,
                                    modifier = Modifier.padding(8.dp),
                                )
                            },
                            topBar = {
                                SmartWriterAppBar(
                                    onNavigationIconClick =  {
                                        scope.launch {
                                            if (drawerState.isClosed) drawerState.open()
                                            else drawerState.close()
                                        }
                                    }
                                )
                            },
                        ) { contentPadding ->
                            val backStack =
                                remember {
                                    mutableStateListOf<NavRoute>(HomeRoute)
                                }

                            LaunchedEffect(uiState.selectedScreen) {
                                updateBackStack(uiState, backStack)
                            }

                            NavDisplay(
                                modifier = Modifier.padding(contentPadding),
                                backStack = backStack,
                                onBack = { backStack.removeLastOrNull() },
                                entryProvider = { key ->
                                    setNavigationEntry(key, LocalSnackbarHostState)
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setNavigationEntry(
        key: NavRoute,
        LocalSnackbarHostState: ProvidableCompositionLocal<SnackbarHostState>
    ): NavEntry<NavRoute> = when (key) {
        is HomeRoute ->
            NavEntry(key) {
                HomeScreen()
            }

        is SummarisationRoute ->
            NavEntry(key) {
                val viewModel by viewModels<SummarisationViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val localSnackBarState =
                    LocalSnackbarHostState.current

                LaunchedEffect(Unit) {
                    viewModel.uiEvent.collectLatest {
                        when (it) {
                            is SummarisationUiEvent.Error -> {
                                if (it.message.isNotBlank()) {
                                    localSnackBarState.showSnackbar(
                                        it.message
                                    )
                                }
                            }
                        }
                    }
                }
                SummarisationScreen(
                    uiState = uiState,
                    onInputTextChanged = viewModel::onInputTextChanged,
                    onSummariseClicked = viewModel::onSummariseClicked,
                )
            }

        is ProofreadingRoute ->
            NavEntry(key) {
                ProofreadingScreen()
            }

        is TextRewritingRoute ->
            NavEntry(key) {
                TextRewritingScreen()
            }

        is ImageDescRoute ->
            NavEntry(key) {
                ImageDescScreen()
            }

        else -> throw IllegalArgumentException("Unknown route: $key")
    }

    private fun updateBackStack(
        uiState: MainActivityState,
        backStack: SnapshotStateList<NavRoute>
    ) {
        when (uiState.selectedScreen) {
            SelectedScreen.HOME -> {
                backStack.clear() // reset to root
                backStack += HomeRoute
            }

            SelectedScreen.SUMMARIZATION -> {
                if (backStack.lastOrNull() !is SummarisationRoute) {
                    backStack += SummarisationRoute
                }
            }

            SelectedScreen.PROOFREADING -> {
                if (backStack.lastOrNull() !is ProofreadingRoute) {
                    backStack += ProofreadingRoute
                }
            }

            SelectedScreen.TEXT_REWRITING -> {
                if (backStack.lastOrNull() !is TextRewritingRoute) {
                    backStack += TextRewritingRoute
                }
            }

            SelectedScreen.IMAGE_DESCRIPTION -> {
                if (backStack.lastOrNull() !is ImageDescRoute) {
                    backStack += ImageDescRoute
                }
            }
        }
    }
}
