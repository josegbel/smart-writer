package com.example.smartwriter.ui.composables.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartwriter.R
import com.example.smartwriter.ui.model.RewritingUiState
import com.example.smartwriter.viewmodel.OutputType

@Composable
fun RewritingScreen(
    uiState: RewritingUiState,
    onInputTextChanged: (String) -> Unit,
    onRewriteClicked: (context: Context) -> Unit,
    onOutputTypeSelected: (OutputType) -> Unit,
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

        Text("Select Output Type:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .selectableGroup(), // For accessibility
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Spacing between radio buttons
        ) {
            uiState.availableOutputTypes.forEach { outputType ->
                Column(
                    modifier = Modifier
                        .weight(1f) // Distribute space equally if needed, or remove for wrap_content
                        .selectable(
                            selected = (outputType == uiState.selectedOutputType),
                            onClick = { onOutputTypeSelected(outputType) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RadioButton(
                        selected = (outputType == uiState.selectedOutputType),
                        onClick = null // onClick is handled by the Row's selectable
                    )
                    Text(
                        text = outputType.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

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
                Text("- $index: $suggestion", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Preview
@Composable
fun RewritingScreenPreview() {
    RewritingScreen(
        uiState = RewritingUiState(),
        onInputTextChanged = {},
        onRewriteClicked = {},
        onOutputTypeSelected = {}
    )
}