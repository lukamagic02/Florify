package com.example.florify.domain

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.lang.IllegalStateException

class MyImageClassifier(
    private val context: Context,
    private val scoreThreshold: Float = 0.5f,
    private val maxResults: Int = 1
): Classifier {

    private var classifier: ImageClassifier? = null
    private var imageProcessor: ImageProcessor = ImageProcessor.Builder().build()

    // method for image classification
    override fun classify(bitmapImage: Bitmap): List<Classification> {
        if (classifier == null) {
            initClassifier()
        }

        // transforming the given bitmap to a tensor image
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmapImage))
        val results = classifier?.classify(tensorImage)


        val finalResults = results?.flatMap { classifications ->
            classifications.categories.map { category ->
                Classification(
                    category.label,  // Changed from category.displayName
                    category.score
                )
            }
        } ?: emptyList()

        return finalResults
    }

    // function for classifier initialization
    private fun initClassifier() {

        // initializing the base options
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()

        // initializing options
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResults)
            .setScoreThreshold(scoreThreshold)
            .build()

        try {
            classifier = ImageClassifier.createFromFileAndOptions(
                context,
                "model_with_metadata.tflite",
                options
            )
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

    }

}