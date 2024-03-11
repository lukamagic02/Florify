package com.example.florify

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ImageViewModel() : ViewModel() {

    private var _image = MutableStateFlow<Bitmap?>(null)
    val image = _image.asStateFlow()

    fun storeImage(image: Bitmap?) {
        _image.value = image
    }

}