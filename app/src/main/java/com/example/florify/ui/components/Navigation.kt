package com.example.florify.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.florify.viewmodel.ImageViewModel
import java.io.IOException

@Composable
fun Navigation(
    context: Context
) {
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
                            val inputStream = context.contentResolver.openInputStream(uri)
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
                    context = context,
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