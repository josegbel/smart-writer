package com.example.smartwriter.ui.model

sealed interface ProofreadingUiEvent {
    data class Error(
        val message: String,
    ) : ProofreadingUiEvent
}
