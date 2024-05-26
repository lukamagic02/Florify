package com.example.florify.domain

import android.graphics.Bitmap

interface Classifier {
    fun classify(image: Bitmap): List<Classification>
}