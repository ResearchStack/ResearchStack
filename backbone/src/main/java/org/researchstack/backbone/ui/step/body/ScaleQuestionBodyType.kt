package org.researchstack.backbone.ui.step.body

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import org.researchstack.backbone.R
import org.researchstack.backbone.answerformat.ScaleAnswerFormat
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.step.QuestionStep

class ScaleQuestionBodyType(private val step: QuestionStep?,
                            private val result: StepResult<Int>?) : StepBody {

    private var currentNumber: Int = result?.result ?: -999
    private val format: ScaleAnswerFormat = this.step?.answerFormat as ScaleAnswerFormat
    override fun getStepResult(skipped: Boolean): StepResult<*> {
        if (skipped) {
            result?.setResult(null)
        } else {
            result?.result = currentNumber
        }

        return result!!
    }

    override fun getBodyAnswerState(): BodyAnswer {
        return format.validateAnswer(currentNumber.toString())

    }

    override fun getBodyView(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): View {
        val formItemView = inflater.inflate(R.layout.rsb_scale_question_layout, parent, false)
        val value = formItemView.findViewById<TextView>(R.id.value)
        value.text = currentNumber.toString()

        formItemView.findViewById<TextView>(R.id.label).text = step!!.title
        formItemView.findViewById<SeekBar>(R.id.seekBar).apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    // Write code to perform some action when progress is changed.
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // Write code to perform some action when touch is started.
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // Write code to perform some action when touch is stopped.
                    currentNumber = seekBar.progress
                    value.text = seekBar.progress.toString()
                }
            })

            progress = currentNumber
            max = format.maxValue
        }

        val res = parent.resources
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_left)
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_right)
        formItemView.layoutParams = layoutParams

        return formItemView
    }


}