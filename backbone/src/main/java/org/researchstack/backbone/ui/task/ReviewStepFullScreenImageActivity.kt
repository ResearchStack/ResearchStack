package org.researchstack.backbone.ui.task

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.rsb_activity_full_screen_image.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.researchstack.backbone.BuildConfig
import org.researchstack.backbone.R
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.ui.step.fragments.ReviewStepFragment
import org.researchstack.backbone.utils.LocalizationUtils


class ReviewStepFullScreenImageActivity : AppCompatActivity(), View.OnClickListener {
    private val viewModel: ReviewStepFullScreenImageViewModel by viewModel { parametersOf(intent) }

    companion object {
        private const val EXTRA_STEP = "full_screen_step"
        internal const val EXTRA_IMAGE_URL = "full_screen_image_url"
        fun getCallingIntent(context: Context, step: Step, imageUrl: String): Intent {
            val intent = Intent(context, ReviewStepFullScreenImageActivity::class.java)
            intent.putExtra(EXTRA_STEP, step)
            intent.putExtra(EXTRA_IMAGE_URL, imageUrl)
            return intent
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.USE_SECURE_FLAG) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        setContentView(R.layout.rsb_activity_full_screen_image)

        val editText = findViewById<TextView>(R.id.fullScreenImageEditStep)
        editText.text = LocalizationUtils.getLocalizedString(this, R.string.rsb_edit_step)

        initCallbacks()
    }

    private fun initCallbacks() {
        viewModel.displayImageEvent.observe(this, Observer {
            fullScreenImage.setImageBitmap(it)
        })
        fullScreenImageClose.setOnClickListener(this)
        fullScreenImageEditStep.setOnClickListener(this)

        fullScreenImageContainer.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int,
                                        oldTop: Int, oldRight: Int, oldBottom: Int) {
                val width = fullScreenImageContainer.measuredWidth
                val height = fullScreenImageContainer.measuredHeight
                viewModel.loadImage(width, height)
                fullScreenImageContainer.removeOnLayoutChangeListener(this)
            }

        })
    }

    override fun onClick(view: View?) {
        view?.id.let {
            when (it) {
                R.id.fullScreenImageClose -> {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
                R.id.fullScreenImageEditStep -> {
                    val step: Step? = intent.extras?.getSerializable(EXTRA_STEP) as Step?

                    val data = Intent()
                    data.putExtra(ReviewStepFragment.EXTRA_FULL_SCREEN_STEP, step)
                    setResult(ReviewStepFragment.RESULT_FULL_SCREEN_EDIT, data)
                    finish()
                }
            }
        }
    }
}
