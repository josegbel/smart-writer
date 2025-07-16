package com.example.smartwriter.ui.composables.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartwriter.R
import com.example.smartwriter.ui.model.RewritingUiState

@Composable
fun RewritingScreen(
    uiState: RewritingUiState,
    onInputTextChanged: (String) -> Unit,
    onRewriteClicked: (context: Context) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
    ) {
        OutlinedTextField(
            value = uiState.inputText,
            onValueChange = onInputTextChanged,
            label = { Text(stringResource(R.string.rewriting_screen_text_field_hint)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current
        Button(
            onClick = { onRewriteClicked(context) },
            enabled = uiState.inputText.isNotBlank(),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Rewrite")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.correctionSuggestions.isNotEmpty()) {
            Text("Suggested rewrites:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            uiState.correctionSuggestions.forEachIndexed { index, suggestion ->
                Text("- ${index}: $suggestion", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
