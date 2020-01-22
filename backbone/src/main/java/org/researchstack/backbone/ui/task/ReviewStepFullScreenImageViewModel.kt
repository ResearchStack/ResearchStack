package org.researchstack.backbone.ui.task

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class ReviewStepFullScreenImageViewModel(intent: Intent) : ViewModel() {
    private val imageUrl = intent.extras?.getString(ReviewStepFullScreenImageActivity.EXTRA_IMAGE_URL)

    val displayImageEvent = MutableLiveData<Bitmap?>()

    init {
        loadImage()
    }

    private fun loadImage() {
        imageUrl?.let {
            var bitmap: Bitmap? = null
            try {
                bitmap = getBitmapFromFile(it)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            displayImageEvent.postValue(bitmap)
        }
    }

    @Throws(FileNotFoundException::class)
    private fun getBitmapFromFile(fileName: String): Bitmap? {
        val file = File(fileName)
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeStream(FileInputStream(file), null, options)
    }
}