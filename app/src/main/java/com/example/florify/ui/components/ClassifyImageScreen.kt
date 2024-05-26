package com.example.florify.ui.components

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(red = 0.0f, green = 128 / 255f, blue = 0.0f, alpha = 1.0f))
        ) {

            Card(
                modifier = Modifier.padding(35.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White, // card background color
                    contentColor = Color.Black  // card content color (e.g. text)
                )
            ) {

                Image(
                    modifier = Modifier
                        .heightIn(max = 350.dp)
                        .widthIn(max = 350.dp),
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

                Button(onClick = {

                }) {
                    Text(text = "BACK")
                }

            }

        }
    }

}