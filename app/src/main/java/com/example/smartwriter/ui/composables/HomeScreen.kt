package com.example.smartwriter.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text(
            modifier = Modifier.align(alignment = Alignment.TopCenter),
            text = "Welcome to SmartWriter! \n\nWe're glad to have you. To get started, open the navigation drawer (the menu icon, usually at the top-left) to find all the different sections of the app.",
        )
    }
}
