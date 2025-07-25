package com.example.smartwriter.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwriter.ui.model.ProofreadingUiEvent
import com.example.smartwriter.ui.model.ProofreadingUiState
import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.proofreading.Proofreader
import com.google.mlkit.genai.proofreading.ProofreaderOptions
import com.google.mlkit.genai.proofreading.Proofreading
import com.google.mlkit.genai.proofreading.ProofreadingRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProofreadingViewModel
    @Inject
    constructor() : ViewModel() {
        companion object {
            private val TAG = ProofreadingViewModel::class.java.simpleName
        }

        private val _uiState = MutableStateFlow(ProofreadingUiState())
        val uiState: StateFlow<ProofreadingUiState> = _uiState.asStateFlow()

        private val _uiEvent = MutableSharedFlow<ProofreadingUiEvent>()
        val uiEvent: SharedFlow<ProofreadingUiEvent> = _uiEvent.asSharedFlow()

        private var proofreader: Proofreader? = null

        override fun onCleared() {
            proofreader?.close()
            super.onCleared()
        }

        fun onInputTextChanged(newText: String) {
            _uiState.update { it.copy(inputText = newText) }
        }

        fun onProofreadClicked(context: Context) {
            viewModelScope.launch {
                try {
                    val options =
                        ProofreaderOptions
                            .builder(context)
                            // InputType can be KEYBOARD or VOICE. VOICE indicates that
                            // the user generated text based on audio input.
                            .setInputType(ProofreaderOptions.InputType.KEYBOARD)
                            .setLanguage(ProofreaderOptions.Language.ENGLISH)
                            .build()

                    proofreader = Proofreading.getClient(options)

                    prepareAndStartProofreading()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in onProofreadClicked: ${e.message}", e)
                    _uiEvent.emit(ProofreadingUiEvent.Error(message = "Error: ${e.message}"))
                }
            }
        }

        suspend fun prepareAndStartProofreading() {
            val featureStatus = proofreader?.checkFeatureStatus()?.await()
            Log.d(TAG, "Feature status: $featureStatus")

            when (featureStatus) {
                FeatureStatus.DOWNLOADABLE -> {
                    Log.d(TAG, "Feature DOWNLOADABLE – starting download")
                    downloadFeature()
                }

                FeatureStatus.DOWNLOADING -> {
                    Log.d(TAG, "Feature DOWNLOADING – will start once ready")
                    proofreader?.let { startProofreadingRequest(uiState.value.inputText, it) }
                }

                FeatureStatus.AVAILABLE -> {
                    Log.d(TAG, "Feature AVAILABLE – running inference")
                    _uiState.update { it.copy(isLoading = true) }
                    proofreader?.let {
                        Log.d(TAG, "starting proofreading request")
                        startProofreadingRequest(uiState.value.inputText, it)
                    }
                }

                FeatureStatus.UNAVAILABLE, null -> {
                    Log.e(TAG, "Feature UNAVAILABLE")
                    _uiEvent.emit(ProofreadingUiEvent.Error(message = "Your device does not support this feature."))
                }
            }
        }

        private fun downloadFeature() {
            proofreader?.downloadFeature(
                object : DownloadCallback {
                    override fun onDownloadStarted(bytesToDownload: Long) {
                        _uiState.update { it.copy(isLoading = true) }
                        Log.d(TAG, "Download started – bytesToDownload=$bytesToDownload")
                    }

                    override fun onDownloadProgress(totalBytesDownloaded: Long) {
                        _uiState.update { it.copy(isLoading = true) }
                        Log.d(TAG, "Download progress – totalBytesDownloaded=$totalBytesDownloaded")
                    }

                    override fun onDownloadCompleted() {
                        _uiState.update { it.copy(isLoading = false) }
                        Log.d(TAG, "Download completed – starting inference")
                        proofreader?.let { startProofreadingRequest(uiState.value.inputText, it) }
                    }

                    override fun onDownloadFailed(e: GenAiException) {
                        _uiState.update { it.copy(isLoading = false) }
                        Log.e(TAG, "Download failed: ${e.message}", e)
                        _uiEvent.tryEmit(
                            ProofreadingUiEvent.Error(
                                message = "Download failed: ${e.message}",
                            ),
                        )
                    }
                },
            )
        }

        fun startProofreadingRequest(
            text: String,
            proofreader: Proofreader,
        ) {
            val proofreadingRequest = ProofreadingRequest.builder(text).build()
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                try {
                    val results = proofreader.runInference(proofreadingRequest).await().results
                    _uiState.update { state ->
                        state.copy(correctionSuggestions = results.map { it.text })
                    }
                } catch (e: Exception) {
                    _uiEvent.emit(
                        ProofreadingUiEvent.Error(
                            message = "Error during proofreading: ${e.message}",
                        ),
                    )
                } finally {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
