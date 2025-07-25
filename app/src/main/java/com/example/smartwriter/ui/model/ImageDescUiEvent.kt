package com.example.smartwriter.ui.model

sealed interface ImageDescUiEvent {
    data class Error(
        val message: String,
    ) : ImageDescUiEvent
}
