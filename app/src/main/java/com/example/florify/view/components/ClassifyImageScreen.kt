package com.example.florify.view.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.florify.model.MyImageClassifier
import com.example.florify.viewmodel.ImageViewModel

@Composable
fun ClassifyImageScreen(
    context: Context,
    viewModel: ImageViewModel,
    navController: NavHostController
) {

    // classifying the given image
    val image by viewModel.image.collectAsStateWithLifecycle()
    val classifier = MyImageClassifier(context)
    val results = image?.let { classifier.classify(it) }

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

                image?.let {
                    Image(
                        modifier = Modifier
                            .heightIn(max = 350.dp)
                            .widthIn(max = 350.dp),
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Flower image"
                    )
                }

                results?.forEach {
                    Text(
                        text = it.category,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }
            }

            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    viewModel.storeImage(null)
                    navController.navigate("pick_image")
                }) {
                Text(text = "BACK")
            }
        }
    }

}