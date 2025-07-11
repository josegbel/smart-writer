package com.example.smartwriter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.smartwriter.ui.composables.HomeScreen
import com.example.smartwriter.ui.composables.ImageDescScreen
import com.example.smartwriter.ui.composables.ProofreadingScreen
import com.example.smartwriter.ui.composables.SummarisationScreen
import com.example.smartwriter.ui.composables.TextRewritingScreen
import com.example.smartwriter.ui.theme.SmartWriterTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private data object HomeRoute

private data object SummarisationRoute

private data object ProofreadingRoute

private data object TextRewritingRoute

private data object ImageDescRoute

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

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Spacer(Modifier.height(12.dp))
                                Text(stringResource(R.string.main_activity_drawer_smart_writer), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                                HorizontalDivider()

                                Text(stringResource(R.string.main_activity_drawer_ai_tools), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.main_activity_drawer_text_summarization)) },
                                    selected = false,
                                    onClick = { /* Handle click */ }
                                )
                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.main_activity_drawer_proofreading)) },
                                    selected = false,
                                    onClick = { /* Handle click */ }
                                )
                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.main_activity_drawer_text_rewriting)) },
                                    selected = false,
                                    onClick = { /* Handle click */ }
                                )
                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.main_activity_drawer_image_description)) },
                                    selected = false,
                                    onClick = { /* Handle click */ }
                                )

                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                Text(stringResource(R.string.main_activity_drawer_other), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.main_activity_drawer_home)) },
                                    selected = false,
                                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                    onClick = { /* Handle click */ },
                                )
                                Spacer(Modifier.height(12.dp))
                            }
                        }
                    },
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("SmartWriter") },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            if (drawerState.isClosed) {
                                                drawerState.open()
                                            } else {
                                                drawerState.close()
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                }
                            )
                        }
                    ) { contentPadding ->
                        val backStack = remember {
                            mutableStateListOf(HomeRoute)
                        }
                        NavDisplay(
                            modifier = Modifier.padding(contentPadding),
                            backStack = backStack,
                            onBack = { backStack.removeLastOrNull() },
                            entryProvider = { key ->
                                when (key) {
                                    is HomeRoute ->
                                        NavEntry(key) {
                                            HomeScreen()
                                        }

                                    is SummarisationRoute ->
                                        NavEntry(key) {
                                            SummarisationScreen()
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
                            },
                        )
                    }
                }
            }
        }
    }
}
