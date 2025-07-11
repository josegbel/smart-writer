package com.example.smartwriter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel
    @Inject
    constructor() : ViewModel() {
        private val _state = MutableStateFlow(MainActivityState())
        val state: StateFlow<MainActivityState> = _state.asStateFlow()

        fun onTextSummarizationClicked() {
            _state.value = _state.value.copy(selectedScreen = SelectedScreen.SUMMARIZATION)
        }

        fun onTextRewritingClicked() {
            _state.value = _state.value.copy(selectedScreen = SelectedScreen.TEXT_REWRITING)
        }

        fun onProofreadingClicked() {
            _state.value = _state.value.copy(selectedScreen = SelectedScreen.PROOFREADING)
        }

        fun onImageDescriptionClicked() {
            _state.value = _state.value.copy(selectedScreen = SelectedScreen.IMAGE_DESCRIPTION)
        }

        fun onHomeClicked() {
            _state.value = _state.value.copy(selectedScreen = SelectedScreen.HOME)
        }
    }

data class MainActivityState(
    val selectedScreen: SelectedScreen = SelectedScreen.HOME,
)

enum class SelectedScreen {
    TEXT_REWRITING,
    PROOFREADING,
    HOME,
    IMAGE_DESCRIPTION,
    SUMMARIZATION,
}
