package org.researchstack.backbone.ui.task

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.rsb_activity_full_screen_image.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.researchstack.backbone.R
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.ui.PinCodeActivity
import org.researchstack.backbone.ui.step.fragments.ReviewStepFragment

class ReviewStepFullScreenImageActivity : PinCodeActivity(), View.OnClickListener {
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
        setContentView(R.layout.rsb_activity_full_screen_image)

        initCallbacks()
    }

    private fun initCallbacks() {
        viewModel.displayImageEvent.observe(this, Observer {
            fullScreenImage.setImageBitmap(it)
        })
        fullScreenImageClose.setOnClickListener(this)
        fullScreenImageEditStep.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.id.let {
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
