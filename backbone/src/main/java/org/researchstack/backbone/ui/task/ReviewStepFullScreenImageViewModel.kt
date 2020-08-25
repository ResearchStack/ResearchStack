package org.researchstack.backbone.ui.task

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

@Deprecated(message = "This is no longer needed as ResearchStack's ReviewStepLayout class is deprecated, please use Axon's ReviewStepFullScreenImageViewModel instead ")
class ReviewStepFullScreenImageViewModel(intent: Intent) : ViewModel() {
    private val imageUrl = intent.extras?.getString(ReviewStepFullScreenImageActivity.EXTRA_IMAGE_URL)

    val displayImageEvent = MutableLiveData<Bitmap?>()

    fun loadImage(width: Int, height: Int) {
        imageUrl?.let {
            var bitmap: Bitmap? = null
            try {
                bitmap = getBitmapFromFile(it, width, height)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            displayImageEvent.postValue(bitmap)
        }
    }

    @Throws(FileNotFoundException::class)
    private fun getBitmapFromFile(fileName: String, width: Int, height: Int): Bitmap? {
        val file = File(fileName)

        return resizeBitmap(file, width, height)
    }

    @Throws(FileNotFoundException::class)
    private fun resizeBitmap(file: File, width: Int, height: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(FileInputStream(file), null, options)
        var scale = 1
        while (options.outWidth / scale / 2 >= width && options.outHeight / scale / 2 >= height) {
            scale *= 2
        }
        options.inJustDecodeBounds = false
        options.inSampleSize = scale
        return BitmapFactory.decodeStream(FileInputStream(file), null, options)
    }
}