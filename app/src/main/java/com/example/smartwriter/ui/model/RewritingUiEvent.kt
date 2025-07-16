package com.example.smartwriter.ui.model

sealed interface RewritingUiEvent {
    data class Error(
        val message: String,
    ) : RewritingUiEvent
}
