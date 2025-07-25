package com.example.smartwriter.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwriter.ui.model.SummarisationUiEvent
import com.example.smartwriter.ui.model.SummarisationUiState
import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.summarization.Summarization
import com.google.mlkit.genai.summarization.SummarizationRequest
import com.google.mlkit.genai.summarization.Summarizer
import com.google.mlkit.genai.summarization.SummarizerOptions
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

class SummarisationViewModel
    @Inject
    constructor() : ViewModel() {
        companion object {
            private val TAG = SummarisationViewModel::class.java.simpleName
        }

        private val _uiState = MutableStateFlow(SummarisationUiState())
        val uiState: StateFlow<SummarisationUiState> = _uiState.asStateFlow()

        private val _uiEvent = MutableSharedFlow<SummarisationUiEvent>()
        val uiEvent: SharedFlow<SummarisationUiEvent> = _uiEvent.asSharedFlow()

        private var summarizer: Summarizer? = null

        override fun onCleared() {
            summarizer?.close()
            super.onCleared()
        }

        fun onInputTextChanged(newText: String) {
            _uiState.update { it.copy(inputText = newText) }
        }

        fun onSummariseClicked(context: Context) {
            viewModelScope.launch {
                try {
                    val summarizerOptions =
                        SummarizerOptions
                            .builder(context)
                            .setInputType(SummarizerOptions.InputType.ARTICLE)
                            .setOutputType(SummarizerOptions.OutputType.ONE_BULLET)
                            .setLanguage(SummarizerOptions.Language.ENGLISH)
                            .build()

                    summarizer = Summarization.getClient(summarizerOptions)

                    prepareAndStartSummarization()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in onSummariseClicked: ${e.message}", e)
                    _uiEvent.emit(SummarisationUiEvent.Error(message = "Error: ${e.message}"))
                }
            }
        }

        // ------------------------------------------------------------
        // Summarization Flow
        // ------------------------------------------------------------
        suspend fun prepareAndStartSummarization() {
            val featureStatus = summarizer?.checkFeatureStatus()?.await()
            Log.d(TAG, "Feature status: $featureStatus")

            when (featureStatus) {
                FeatureStatus.DOWNLOADABLE -> {
                    Log.d(TAG, "Feature DOWNLOADABLE – starting download")
                    downloadFeature()
                }

                FeatureStatus.DOWNLOADING -> {
                    Log.d(TAG, "Feature DOWNLOADING – will start once ready")
                    summarizer?.let { startSummarizationRequest(uiState.value.inputText, it) }
                }

                FeatureStatus.AVAILABLE -> {
                    Log.d(TAG, "Feature AVAILABLE – running inference")
                    _uiState.update { it.copy(isLoading = true) }
                    summarizer?.let {
                        Log.d(TAG, "starting summarization request")
                        startSummarizationRequest(uiState.value.inputText, it)
                    }
                }

                FeatureStatus.UNAVAILABLE, null -> {
                    Log.e(TAG, "Feature UNAVAILABLE")
                    _uiEvent.emit(SummarisationUiEvent.Error(message = "Your device does not support this feature."))
                }
            }
        }

        private fun downloadFeature() {
            summarizer?.downloadFeature(
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
                        summarizer?.let { startSummarizationRequest(uiState.value.inputText, it) }
                    }

                    override fun onDownloadFailed(e: GenAiException) {
                        _uiState.update { it.copy(isLoading = false) }
                        Log.e(TAG, "Download failed: ${e.message}", e)
                        _uiEvent.tryEmit(
                            SummarisationUiEvent.Error(
                                message = "Download failed: ${e.message}",
                            ),
                        )
                    }
                },
            )
        }

        fun startSummarizationRequest(
            text: String,
            summarizer: Summarizer,
        ) {
            val summarizationRequest = SummarizationRequest.builder(text).build()
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                try {
                    val summary = summarizer.runInference(summarizationRequest).await().summary
                    _uiState.update { it.copy(summary = summary) }
                } catch (e: Exception) {
                    _uiEvent.emit(
                        SummarisationUiEvent.Error(
                            message = "Error during summarization: ${e.message}",
                        ),
                    )
                } finally {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
