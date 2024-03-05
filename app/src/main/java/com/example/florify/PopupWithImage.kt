package com.example.florify

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PopupWithImage(
    onProceedWithRequest: () -> Unit,
    onDismissRequest: () -> Unit,
    image: Bitmap?
) {
    if (image != null) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(350.dp)
                    .padding(16.dp)
            ) {
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
                    Text(
                        text = "Image successfully taken and processed. Should we proceed with image classification?",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { onProceedWithRequest() },
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Text(text = "Proceed")
                        }
                        TextButton(
                            onClick = { onDismissRequest() },
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Text(text = "Dismiss")
                        }
                    }
                }
            }
        }
    }
}