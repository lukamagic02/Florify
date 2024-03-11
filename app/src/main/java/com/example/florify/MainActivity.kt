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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
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

                val navController = rememberNavController()
                NavHost(navController, "onboarding") {
                    navigation(startDestination = "pick_image", route = "onboarding") {
                        composable("pick_image") {
                            val viewModel = it.sharedViewModel<ImageViewModel>(navController)
                            val image by viewModel.image.collectAsStateWithLifecycle()

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

                                        viewModel.storeImage(resizedImage)
                                    } catch (e: IOException) {
                                        Log.e("Gallery", "Failed to load image:", e)
                                    }
                                }
                            }

                            PickImageScreen(
                                galleryLauncher = galleryLauncher,
                                image = image,
                                changeImage = {image: Bitmap? -> viewModel.storeImage(image)},
                                navigateTo = {destination: String -> navController.navigate(destination)}
                            )
                        }

                        composable("classify_image") {
                            val viewModel = it.sharedViewModel<ImageViewModel>(navController)
                            val image by viewModel.image.collectAsStateWithLifecycle()

                            image?.let { it1 ->
                                ClassifyImageScreen(it1)
                            }
                        }
                    }
                }
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
    private fun PickImageScreen(
        galleryLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
        image: Bitmap?,
        changeImage: (Bitmap?) -> Unit,
        navigateTo: (String) -> Unit
    ) {

        val camController = remember {
            LifecycleCameraController(applicationContext).apply {
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
                        takePicture(camController, changeImage)
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

    private fun takePicture(
        camController: LifecycleCameraController,
        changeImage: (Bitmap?) -> Unit
    ) {
        camController.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    preprocessImage(image, changeImage)
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
        changeImage: (Bitmap?) -> Unit
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

        changeImage(resizedBitmap)
    }

    private fun selectPictureFromPhoneGallery(
        galleryLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
    ) {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    @Composable
    private fun ClassifyImageScreen(image: Bitmap) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = "Flower image"
            )
        }
    }

    @Composable
    inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
        navController: NavHostController
    ) : T {
        val parentScreenRoute = destination.parent?.route ?: return viewModel()
        val parentEntry = remember(this) {
            navController.getBackStackEntry(parentScreenRoute)
        }
        return viewModel(parentEntry)
    }
}