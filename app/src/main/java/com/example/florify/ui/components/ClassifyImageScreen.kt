package com.example.florify.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap

@Composable
fun ClassifyImageScreen(image: Bitmap) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = "Flower image"
        )
    }
}