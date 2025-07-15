package com.example.smartwriter.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartwriter.MainActivityState
import com.example.smartwriter.R
import com.example.smartwriter.SelectedScreen

@Composable
fun DrawerContent(
    uiState: MainActivityState,
    onTextSummarisationClicked: () -> Unit,
    onProofreadingClicked: () -> Unit,
    onTextRewritingClicked: () -> Unit,
    onImageDescriptionClicked: () -> Unit,
    onHomeClicked: () -> Unit
) {
    Column(
        modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.main_activity_drawer_smart_writer),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge,
        )
        HorizontalDivider()

        Text(
            stringResource(R.string.main_activity_drawer_ai_tools),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.main_activity_drawer_text_summarization)) },
            selected = uiState.selectedScreen == SelectedScreen.SUMMARIZATION,
            onClick = onTextSummarisationClicked
            ,
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.main_activity_drawer_proofreading)) },
            selected = uiState.selectedScreen == SelectedScreen.PROOFREADING,
            onClick = onProofreadingClicked,
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.main_activity_drawer_text_rewriting)) },
            selected = uiState.selectedScreen == SelectedScreen.TEXT_REWRITING,
            onClick = onTextRewritingClicked,
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.main_activity_drawer_image_description)) },
            selected = uiState.selectedScreen == SelectedScreen.IMAGE_DESCRIPTION,
            onClick = onImageDescriptionClicked,
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            stringResource(R.string.main_activity_drawer_other),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.main_activity_drawer_home)) },
            selected = uiState.selectedScreen == SelectedScreen.HOME,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            onClick = onHomeClicked,
        )
        Spacer(Modifier.height(12.dp))
    }
}