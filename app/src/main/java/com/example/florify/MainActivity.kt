@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.florify

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.florify.ui.theme.FlorifyTheme
import java.io.IOException

class MainActivity : ComponentActivity() {

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                0
            )
        } // add the else block later

        setContent {
            FlorifyTheme {
                // val scope = rememberCoroutineScope()

                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE
                        )
                    }
                }

                var imageHolder = remember { mutableStateOf<Bitmap?>(null) }

                val galleryLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.PickVisualMedia()
                ) { uri: Uri? ->
                    if (uri != null) {
                        try {
                            val inputStream = contentResolver.openInputStream(uri)
                            val image = BitmapFactory.decodeStream(inputStream)

                            val resizedImage = Bitmap.createScaledBitmap(
                                image,
                                224,
                                224,
                                true
                            )

                            imageHolder.value = resizedImage
                        } catch (e: IOException) {
                            Log.e("Gallery", "Failed to load image:", e)
                        }
                    }
                }

                MainContent(controller, imageHolder, galleryLauncher)
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    @Composable
    private fun MainContent(
        controller: LifecycleCameraController,
        imageHolder: MutableState<Bitmap?>,
        galleryLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            if (imageHolder.value != null) {
                PopupWithImage(
                    onProceedWithRequest = { imageHolder.value = null },
                    onDismissRequest = { imageHolder.value = null },
                    image = imageHolder.value,
                )
            }

            CameraPreview(
                controller = controller,
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
                        selectPictureFromPhoneGallery(galleryLauncher)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Open phone gallery"
                    )
                }

                IconButton(
                    onClick = {
                        takePicture(controller, imageHolder)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take photo"
                    )
                }

                IconButton(
                    onClick = {
                        controller.cameraSelector =
                            if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
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

    private fun takePicture(
        controller: LifecycleCameraController,
        imageHolder: MutableState<Bitmap?>
    ) {
        controller.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    preprocessImage(image, imageHolder)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)

                    Log.e("Camera", "Couldn't take photo: ", exception)
                }
            }
        )

    }

    private fun preprocessImage(
        image: ImageProxy,
        imageHolder: MutableState<Bitmap?>

    ) {
        val rotatedBitmap = Matrix().apply {
            postRotate(image.imageInfo.rotationDegrees.toFloat())
        }.let {
            Bitmap.createBitmap(
                image.toBitmap(),
                0,
                0,
                image.width,
                image.height,
                it,
                true
            )
        }

        // Add your image normalization logic here

        val resizedBitmap = Bitmap.createScaledBitmap(
            rotatedBitmap,
            224,
            224,
            true
        )

        imageHolder.value = resizedBitmap
    }

    private fun selectPictureFromPhoneGallery(
        galleryLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
    ) {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

}