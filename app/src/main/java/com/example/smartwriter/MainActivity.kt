package com.example.smartwriter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.smartwriter.ui.composables.DrawerContent
import com.example.smartwriter.ui.composables.SmartWriterAppBar
import com.example.smartwriter.ui.composables.screens.HomeScreen
import com.example.smartwriter.ui.composables.screens.ImageDescScreen
import com.example.smartwriter.ui.composables.screens.ProofreadingScreen
import com.example.smartwriter.ui.composables.screens.RewritingScreen
import com.example.smartwriter.ui.composables.screens.SummarisationScreen
import com.example.smartwriter.ui.model.ImageDescUiEvent
import com.example.smartwriter.ui.model.ProofreadingUiEvent
import com.example.smartwriter.ui.model.RewritingUiEvent
import com.example.smartwriter.ui.model.SummarisationUiEvent
import com.example.smartwriter.ui.theme.SmartWriterTheme
import com.example.smartwriter.viewmodel.ImageDescViewModel
import com.example.smartwriter.viewmodel.MainActivityState
import com.example.smartwriter.viewmodel.MainActivityViewModel
import com.example.smartwriter.viewmodel.ProofreadingViewModel
import com.example.smartwriter.viewmodel.RewritingViewModel
import com.example.smartwriter.viewmodel.SelectedScreen
import com.example.smartwriter.viewmodel.SummarisationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface NavRoute

private data object HomeRoute : NavRoute

private data object SummarisationRoute : NavRoute

private data object ProofreadingRoute : NavRoute

private data object RewritingRoute : NavRoute

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
                                    onNavigationIconClick = {
                                        scope.launch {
                                            if (drawerState.isClosed) {
                                                drawerState.open()
                                            } else {
                                                drawerState.close()
                                            }
                                        }
                                    },
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
        LocalSnackbarHostState: ProvidableCompositionLocal<SnackbarHostState>,
    ): NavEntry<NavRoute> =
        when (key) {
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
                                            it.message,
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
                    val viewModel by viewModels<ProofreadingViewModel>()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    val localSnackBarState =
                        LocalSnackbarHostState.current

                    LaunchedEffect(Unit) {
                        viewModel.uiEvent.collectLatest {
                            when (it) {
                                is ProofreadingUiEvent.Error -> {
                                    if (it.message.isNotBlank()) {
                                        localSnackBarState.showSnackbar(
                                            it.message,
                                        )
                                    }
                                }
                            }
                        }
                    }
                    ProofreadingScreen(
                        uiState = uiState,
                        onInputTextChanged = viewModel::onInputTextChanged,
                        onProofreadClicked = viewModel::onProofreadClicked,
                    )
                }

            is RewritingRoute ->
                NavEntry(key) {
                    val viewModel by viewModels<RewritingViewModel>()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    val localSnackBarState =
                        LocalSnackbarHostState.current

                    LaunchedEffect(Unit) {
                        viewModel.uiEvent.collectLatest {
                            when (it) {
                                is RewritingUiEvent.Error -> {
                                    if (it.message.isNotBlank()) {
                                        localSnackBarState.showSnackbar(
                                            it.message,
                                        )
                                    }
                                }
                            }
                        }
                    }
                    RewritingScreen(
                        uiState = uiState,
                        onInputTextChanged = viewModel::onInputTextChanged,
                        onRewriteClicked = viewModel::onRewriteClicked,
                        onOutputTypeSelected = viewModel::onOutputTypeSelected,
                    )
                }

            is ImageDescRoute ->
                NavEntry(key) {
                    val localSnackBarState = LocalSnackbarHostState.current
                    val viewModel by viewModels<ImageDescViewModel>()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    LaunchedEffect(Unit) {
                        viewModel.uiEvent.collectLatest {
                            when (it) {
                                is ImageDescUiEvent.Error -> {
                                    if (it.message.isNotBlank()) {
                                        localSnackBarState.showSnackbar(
                                            it.message,
                                        )
                                    }
                                }
                            }
                        }
                    }
                    val context = LocalContext.current
                    ImageDescScreen(
                        uiState = uiState,
                        onImageSelected = { uri ->
                            viewModel.onImageSelected(uri)
                            viewModel.describe(context)
                        },
                    )
                }

            else -> throw IllegalArgumentException("Unknown route: $key")
        }

    private fun updateBackStack(
        uiState: MainActivityState,
        backStack: SnapshotStateList<NavRoute>,
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
                if (backStack.lastOrNull() !is RewritingRoute) {
                    backStack += RewritingRoute
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
