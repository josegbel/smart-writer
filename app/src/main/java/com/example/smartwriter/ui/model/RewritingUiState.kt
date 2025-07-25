package com.example.smartwriter.ui.model

data class RewritingUiState(
    val inputText: String = "My neighbour is not a very good cook and he never cleans his kitchen.",
    val correctionSuggestions: List<String> = emptyList(),
    val isLoading: Boolean = false,
)
