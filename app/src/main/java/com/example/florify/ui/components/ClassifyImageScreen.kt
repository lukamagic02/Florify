package com.example.florify.ui.components

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.florify.domain.MyImageClassifier

@Composable
fun ClassifyImageScreen(
    context: Context,
    image: Bitmap
) {

    // classifying the given image
    val classifier = MyImageClassifier(context)
    val results = classifier.classify(image)

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(red = 1.0f, green = 1.0f, blue = 1.0f, alpha = 1.0f))
        ) {

            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = "Flower image"
            )

            results.forEach {
                Text(
                    text = it.category,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }

        }
    }

}