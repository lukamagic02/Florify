package com.example.florify.model

import android.graphics.Bitmap

interface Classifier {
    fun classify(image: Bitmap): List<Classification>
}