package com.example.smartwriter.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.smartwriter.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartWriterAppBar(onNavigationIconClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.top_app_bar_title)) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
    )
}
