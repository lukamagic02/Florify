package com.example.florify.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat

object Util {

    fun selectPictureFromPhoneGallery(
        galleryLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
    ) {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    fun preprocessPicture(
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

    fun takePicture(
        context: Context,
        camController: LifecycleCameraController,
        changeImage: (Bitmap?) -> Unit
    ) {
        camController.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    preprocessPicture(image, changeImage)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)

                    Log.e("Camera", "Couldn't take photo: ", exception)
                }
            }
        )
    }

    fun hasRequiredPermissions(
        context: Context,
        permissions: Array<String>
    ): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}