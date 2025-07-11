package com.example.smartwriter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(): ViewModel() {


}

enum class SelectedScreen {
    TEXT_REWRITING,
    PROOFREADING,
    HOME,
    IMAGE_DESCRIPTION,
    SUMMARIZATION
}
