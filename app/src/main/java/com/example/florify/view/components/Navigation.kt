package com.example.florify.view.components

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
    // controls navigation between different screens in the app
    val navController = rememberNavController()

    // container for apps navigation graph, takes 3 arguments: navController,
    // startDestination (the initial screen to display when NavHost is first rendered)
    // and ... (still don't quite understand NavGraphBuilder, NavBackStackEntry, nested graphs...)
    NavHost(navController, "onboarding") {

        navigation(startDestination = "pick_image", route = "onboarding") {

            composable("pick_image") {

                val viewModel = it.sharedViewModel<ImageViewModel>(navController)
                val image by viewModel.image.collectAsStateWithLifecycle()

                val galleryLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.PickVisualMedia()
                ) { uri: Uri? ->

                    uri?.let {

                        try {
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val image = BitmapFactory.decodeStream(inputStream)

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
                // this image variable basically acts as an interface between a client
                // and the State<> object (?)
                ClassifyImageScreen(context, viewModel, navController)
            }

        }
    }
}

// function that enables propagating the same viewModel instance across different screens
@Composable
// NavBackStackEntry is a stack that contains the screens the user has already been on,
// where the top of the stack is the screen the user is currently on, while the entry below
// that one represents the destination that the user visited prior to the current one
private inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController
) : T {

    // destination is a NavBackStackEntry property accessible like this because we are currently
    // inside of a NavBackStackEntry instance
    val parentScreenRoute = destination?.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(parentScreenRoute)
    }

    return viewModel(parentEntry)
}