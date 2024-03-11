package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class ImageClassifierHelper(
    val context: Context,
    val thresold: Float = 0.1f,
    val maxResult: Int = 3,
    val modelName: String = "cancer_classification.tflite",
    val classifierListener: ClassifierListener?
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        // TODO: Menyiapkan Image Classifier untuk memproses gambar.
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(thresold)
            .setMaxResults(maxResult)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError("Image Classifier gagal di Inisiasi")
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        // TODO: mengklasifikasikan imageUri dari gambar statis.
        if (imageClassifier == null) {
            setupImageClassifier()
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.UINT8))
            .build()

//            .copy(Bitmap.Config.ARGB_8888, true)?.let { bitmap ->
//                val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
//            }
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(imageUri.toImageBitmap()))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .build()

        var inferenceTime = SystemClock.uptimeMillis()
        val result = imageClassifier?.classify(tensorImage, imageProcessingOptions)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        classifierListener?.onResult(result = result, inferenceTime = inferenceTime)
    }

    interface ClassifierListener {
        fun onResult(result: List<Classifications>?, inferenceTime: Long)
        fun onError(error: String)
    }

    private fun Uri.toImageBitmap(): Bitmap {
        val contentResolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, this)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, this)
        }
    }

    companion object {
        private const val TAG = "IMAGE_TAG"
    }
}