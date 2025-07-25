package com.example.smartwriter.viewmodel

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwriter.ui.model.ImageDescUiEvent
import com.example.smartwriter.ui.model.ImageDescUiState
import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.imagedescription.ImageDescriber
import com.google.mlkit.genai.imagedescription.ImageDescriberOptions
import com.google.mlkit.genai.imagedescription.ImageDescription
import com.google.mlkit.genai.imagedescription.ImageDescriptionRequest
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

class ImageDescViewModel
@Inject
constructor() : ViewModel() {
    companion object Companion {
        private val TAG = ImageDescViewModel::class.java.simpleName
    }

    private val _uiState = MutableStateFlow(ImageDescUiState(null))
    val uiState: StateFlow<ImageDescUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ImageDescUiEvent>()
    val uiEvent: SharedFlow<ImageDescUiEvent> = _uiEvent.asSharedFlow()

    private var imageDescriber: ImageDescriber? = null

    override fun onCleared() {
        imageDescriber?.close()
        super.onCleared()
    }

    fun describe(context: Context) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val options = ImageDescriberOptions.builder(context).build()
                imageDescriber = ImageDescription.getClient(options)
                prepareAndStartImageDesc(context)
            } catch (e: Exception) {
                Log.e(TAG, "Error in onRewriteClicked: ${e.message}", e)
                _uiEvent.emit(ImageDescUiEvent.Error(message = "Error: ${e.message}"))
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    suspend fun prepareAndStartImageDesc(context: Context) {
        val featureStatus = imageDescriber?.checkFeatureStatus()?.await()
        Log.d(TAG, "Feature status: $featureStatus")

        when (featureStatus) {
            FeatureStatus.DOWNLOADABLE -> {
                Log.d(TAG, "Feature DOWNLOADABLE – starting download")
                downloadFeature(context)
            }

            FeatureStatus.DOWNLOADING -> {
                Log.d(TAG, "Feature DOWNLOADING – will start once ready")
                imageDescriber?.let {
                    uiState.value.imageUri?.let { uri ->
                        startImageDescRequest(uri, context, it)
                    }
                }
            }

            FeatureStatus.AVAILABLE -> {
                Log.d(TAG, "Feature AVAILABLE – running inference")
                _uiState.update { it.copy(isLoading = true) }
                imageDescriber?.let {
                    Log.d(TAG, "starting image description request")
                    uiState.value.imageUri?.let { bitmap -> startImageDescRequest(bitmap, context, it) }
                }
            }

            FeatureStatus.UNAVAILABLE, null -> {
                Log.e(TAG, "Feature UNAVAILABLE")
                _uiEvent.emit(ImageDescUiEvent.Error(message = "Your device does not support this feature."))
            }
        }
    }

    private fun downloadFeature(context: Context) {
        imageDescriber?.downloadFeature(
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
                    imageDescriber?.let {
                        uiState.value.imageUri?.let { bitmap ->
                            startImageDescRequest(bitmap, context, it)
                        }
                    }
                }

                override fun onDownloadFailed(e: GenAiException) {
                    _uiState.update { it.copy(isLoading = false) }
                    Log.e(TAG, "Download failed: ${e.message}", e)
                    _uiEvent.tryEmit(
                        ImageDescUiEvent.Error(
                            message = "Download failed: ${e.message}",
                        ),
                    )
                }
            },
        )
    }

    fun startImageDescRequest(
        uri: Uri,
        context: Context,
        imageDescriber: ImageDescriber,
    ) {
        val bitmap = ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(context.contentResolver, uri)
        )
        val imageDescRequest = ImageDescriptionRequest.builder(bitmap).build()
        Log.d(TAG, "Starting image description request. Bitmap size: ${bitmap.byteCount} bytes")
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val description = imageDescriber.runInference(imageDescRequest).await().description
                _uiState.update { it.copy(description = description) }
            } catch (e: Exception) {
                _uiEvent.emit(
                    ImageDescUiEvent.Error(
                        message = "Error describing the image: ${e.message}",
                    ),
                )
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
