package com.example.smartwriter.ui.composables

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartwriter.R
import com.example.smartwriter.ui.model.SummarisationUiState

@Composable
fun SummarisationScreen(
    uiState: SummarisationUiState,
    onInputTextChanged: (String) -> Unit,
    onSummariseClicked: (context: Context) -> Unit,
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
            label = { Text(stringResource(R.string.summarization_screen_text_field_hint)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current
        Button(
            onClick = { onSummariseClicked(context) },
            enabled = uiState.inputText.isNotBlank(),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Summarise")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.summary.isNotBlank()) {
            Text("Summary:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(uiState.summary)
        }
    }
}

@Preview(showBackground = true, name = "Default State")
@Composable
fun SummarisationScreenPreview_Default() {
    MaterialTheme {
        SummarisationScreen(
            uiState = SummarisationUiState(),
            onInputTextChanged = {},
            onSummariseClicked = {},
        )
    }
}

@Preview(showBackground = true, name = "With Input Text")
@Composable
fun SummarisationScreenPreview_WithInputText() {
    MaterialTheme {
        SummarisationScreen(
            uiState = SummarisationUiState(inputText = "This is some sample text to be summarized."),
            onInputTextChanged = {},
            onSummariseClicked = {},
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun SummarisationScreenPreview_Loading() {
    MaterialTheme {
        SummarisationScreen(
            uiState =
                SummarisationUiState(
                    inputText = "This is some sample text.",
                    isLoading = true,
                ),
            onInputTextChanged = {},
            onSummariseClicked = {},
        )
    }
}

@Preview(showBackground = true, name = "With Summary Displayed")
@Composable
fun SummarisationScreenPreview_WithSummary() {
    MaterialTheme {
        SummarisationScreen(
            uiState =
                SummarisationUiState(
                    inputText = "This was the original long text that was provided by the user.",
                    summary = "This is the concise and helpful summary.",
                ),
            onInputTextChanged = {},
            onSummariseClicked = {},
        )
    }
}

@Preview(showBackground = true, name = "Button Disabled (No Input)")
@Composable
fun SummarisationScreenPreview_ButtonDisabled() {
    MaterialTheme {
        SummarisationScreen(
            uiState = SummarisationUiState(inputText = ""), // Input text is blank
            onInputTextChanged = {},
            onSummariseClicked = {},
        )
    }
}
