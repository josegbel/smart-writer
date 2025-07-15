package com.example.smartwriter.ui.model

sealed interface SummarisationUiEvent {
    data class Error(
        val message: String,
    ) : SummarisationUiEvent
}
