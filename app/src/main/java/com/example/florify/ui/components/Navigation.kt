package com.example.florify.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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

                            /*
                            val resizedImage = Bitmap.createScaledBitmap(
                                image,
                                224,
                                224,
                                true
                            )

                            val normalizedBitmap = Bitmap.createBitmap(
                                resizedImage.width,
                                resizedImage.height,
                                resizedImage.config
                            )

                            for (x in 0 until resizedImage.width) {
                                for (y in 0 until resizedImage.height) {
                                    val pixel = resizedImage.getPixel(x, y)

                                    val red = Color.red(pixel) / 255.0f
                                    val green = Color.green(pixel) / 255.0f
                                    val blue = Color.blue(pixel) / 255.0f

                                    val normalizedPixel = Color.rgb(
                                        red,
                                        green,
                                        blue
                                    )

                                    normalizedBitmap.setPixel(x, y, normalizedPixel)
                                }
                            }
                             */

                            viewModel.storeImage(image)

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
                    ClassifyImageScreen(context, it1)
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