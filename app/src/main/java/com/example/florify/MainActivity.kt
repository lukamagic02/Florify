@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.florify

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.ActivityCompat
import com.example.florify.ui.components.Navigation
import com.example.florify.ui.theme.FlorifyTheme
import com.example.florify.util.Util.hasRequiredPermissions

class MainActivity : ComponentActivity() {

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (!hasRequiredPermissions(applicationContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                0
            )
        } // add the else block later

        setContent {
            FlorifyTheme {
                Navigation(context = applicationContext)
            }
        }
    }
}