package com.example.smartwriter.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwriter.ui.model.RewritingUiEvent
import com.example.smartwriter.ui.model.RewritingUiState
import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.rewriting.Rewriter
import com.google.mlkit.genai.rewriting.RewriterOptions
import com.google.mlkit.genai.rewriting.Rewriting
import com.google.mlkit.genai.rewriting.RewritingRequest
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

class RewritingViewModel
    @Inject
    constructor() : ViewModel() {
        companion object {
            private val TAG = RewritingViewModel::class.java.simpleName
        }

        private val _uiState = MutableStateFlow(RewritingUiState())
        val uiState: StateFlow<RewritingUiState> = _uiState.asStateFlow()

        private val _uiEvent = MutableSharedFlow<RewritingUiEvent>()
        val uiEvent: SharedFlow<RewritingUiEvent> = _uiEvent.asSharedFlow()

        private var rewriter: Rewriter? = null

        override fun onCleared() {
            rewriter?.close()
            super.onCleared()
        }

        fun onInputTextChanged(newText: String) {
            _uiState.update { it.copy(inputText = newText) }
        }

        fun onOutputTypeSelected(outputType: OutputType) {
            _uiState.update { it.copy(selectedOutputType = outputType) }
        }

    fun onRewriteClicked(context: Context) {
            viewModelScope.launch {
                try {
                    val options =
                        RewriterOptions
                            .builder(context)
                            .setOutputType(_uiState.value.selectedOutputType.value)
                            .setLanguage(RewriterOptions.Language.ENGLISH)
                            .build()

                    rewriter = Rewriting.getClient(options)

                    prepareAndStartRewriting()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in onRewriteClicked: ${e.message}", e)
                    _uiEvent.emit(RewritingUiEvent.Error(message = "Error: ${e.message}"))
                }
            }
        }

        suspend fun prepareAndStartRewriting() {
            val featureStatus = rewriter?.checkFeatureStatus()?.await()
            Log.d(TAG, "Feature status: $featureStatus")

            when (featureStatus) {
                FeatureStatus.DOWNLOADABLE -> {
                    Log.d(TAG, "Feature DOWNLOADABLE – starting download")
                    downloadFeature()
                }

                FeatureStatus.DOWNLOADING -> {
                    Log.d(TAG, "Feature DOWNLOADING – will start once ready")
                    rewriter?.let { startRewritingRequest(uiState.value.inputText, it) }
                }

                FeatureStatus.AVAILABLE -> {
                    Log.d(TAG, "Feature AVAILABLE – running inference")
                    _uiState.update { it.copy(isLoading = true) }
                    rewriter?.let {
                        Log.d(TAG, "starting rewriting request")
                        startRewritingRequest(uiState.value.inputText, it)
                    }
                }

                FeatureStatus.UNAVAILABLE, null -> {
                    Log.e(TAG, "Feature UNAVAILABLE")
                    _uiEvent.emit(RewritingUiEvent.Error(message = "Your device does not support this feature."))
                }
            }
        }

        private fun downloadFeature() {
            rewriter?.downloadFeature(
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
                        rewriter?.let { startRewritingRequest(uiState.value.inputText, it) }
                    }

                    override fun onDownloadFailed(e: GenAiException) {
                        _uiState.update { it.copy(isLoading = false) }
                        Log.e(TAG, "Download failed: ${e.message}", e)
                        _uiEvent.tryEmit(
                            RewritingUiEvent.Error(
                                message = "Download failed: ${e.message}",
                            ),
                        )
                    }
                },
            )
        }

        fun startRewritingRequest(
            text: String,
            rewriter: Rewriter,
        ) {
            val rewritingRequest = RewritingRequest.builder(text).build()
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                try {
                    val results = rewriter.runInference(rewritingRequest).await().results
                    _uiState.update { state ->
                        state.copy(correctionSuggestions = results.map { it.text })
                    }
                } catch (e: Exception) {
                    _uiEvent.emit(
                        RewritingUiEvent.Error(
                            message = "Error during rewriting: ${e.message}",
                        ),
                    )
                } finally {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

enum class OutputType(val value: Int, val displayName: String) {
    ELABORATE(RewriterOptions.OutputType.ELABORATE, "Elaborate"),
    EMOJIFY(RewriterOptions.OutputType.EMOJIFY, "Emojify"),
    SHORTEN(RewriterOptions.OutputType.SHORTEN, "Shorten"),
    FRIENDLY(RewriterOptions.OutputType.FRIENDLY, "Friendly"),
    PROFESSIONAL(RewriterOptions.OutputType.PROFESSIONAL, "Professional"),
    REPHRASE(RewriterOptions.OutputType.REPHRASE, "Rephrase"),
}
