package com.example.smartwriter.ui.model


data class ProofreadingUiState(
    val inputText: String = "The praject is compleet but needs too be reviewd",
    val correctionSuggestions: List<String> = emptyList(),
    val isLoading: Boolean = false,
)
