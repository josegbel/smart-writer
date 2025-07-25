package com.example.smartwriter.ui.model

import android.net.Uri

data class ImageDescUiState(
    val imageUri: Uri?,
    val description: String? = null,
    val isLoading: Boolean = false,
)
