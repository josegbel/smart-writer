package com.example.smartwriter.ui.model

data class ProofreadingUiState(
    val inputText: String = "My neighbor is nt a very good cook and he nevar clean his kitchen.",
    val correctionSuggestions: List<String> = emptyList(),
    val isLoading: Boolean = false,
)
