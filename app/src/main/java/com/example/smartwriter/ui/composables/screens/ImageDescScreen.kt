package com.example.smartwriter.ui.composables.screens

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.smartwriter.ui.model.ImageDescUiState

@Composable
fun ImageDescScreen(
    uiState: ImageDescUiState,
    onImageSelected: (uri: Uri) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
    ) {
        val context = LocalContext.current
        val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
            uri?.let { onImageSelected(it) }
        }
        Button(onClick = {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }) {
            Text("Select photo")
        }
        if (uiState.imageUri == null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No image selected",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            val bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uiState.imageUri))
            Image(
                painter = BitmapPainter(bitmap.asImageBitmap()),
                contentDescription = "Selected image"
            )
        }
        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Blue)
        } else if (!uiState.description.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Image Description: ${uiState.description}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
