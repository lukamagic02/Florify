package com.example.florify.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.florify.util.Util

@Composable
fun PickImageScreen(
    context: Context,
    galleryLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    image: Bitmap?,
    changeImage: (Bitmap?) -> Unit,
    navigateTo: (String) -> Unit
) {

    val camController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (image != null) {
            PopupWithImage(
                onProceedWithRequest = { navigateTo("classify_image") },
                onDismissRequest = { changeImage(null) },
                image = image,
            )
        }

        CameraPreview(
            camController = camController,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(red = 1.0f, green = 1.0f, blue = 1.0f, alpha = 1.0f)),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(
                onClick = {
                    Util.selectPictureFromPhoneGallery(galleryLauncher)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Open phone gallery"
                )
            }

            IconButton(
                onClick = {
                    Util.takePicture(context, camController, changeImage)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Take photo"
                )
            }

            IconButton(
                onClick = {
                    camController.cameraSelector =
                        if (camController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else
                            CameraSelector.DEFAULT_BACK_CAMERA
                },
            ) {
                Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = "Switch camera")
            }
        }
    }
}