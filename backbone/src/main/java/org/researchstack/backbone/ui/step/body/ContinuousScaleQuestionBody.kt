package org.researchstack.backbone.ui.step.body

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.rsb_scale_question_layout.view.*
import org.researchstack.backbone.R
import org.researchstack.backbone.answerformat.ContinuousScaleAnswerFormat
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.step.QuestionStep
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.utils.TextUtils

open class ContinuousScaleQuestionBody(step: Step, result: StepResult<*>?) : StepBody {
    protected var step: QuestionStep = step as QuestionStep
    protected var result: StepResult<Int> = result as StepResult<Int>? ?: StepResult(step)
    protected var format: ContinuousScaleAnswerFormat

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    protected var viewType: Int = 0
    protected var currentNumberTextView: TextView? = null

    init {
        this.format = this.step.answerFormat as ContinuousScaleAnswerFormat
    }

    override fun getBodyView(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): View {
        this.viewType = viewType

        return getViewForType(viewType, inflater, parent)
    }

    private fun getViewForType(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): View {
        return initViewCompact(inflater, parent)
    }

    private fun initViewCompact(inflater: LayoutInflater, parent: ViewGroup): View {
        val formItemView = inflater.inflate(R.layout.rsb_continuous_scale_question_layout, parent, false)

        currentNumberTextView = formItemView.findViewById(R.id.value)

        formItemView.rsbRangeStart.text = format.minValue.toString()

        formItemView.rsbRangeEnd.text = format.maxValue.toString()
        val mins = format.minValue.toInt();
        val maxs = format.maxValue.toInt();

//        New slider code to handle negative values
        if(maxs >= 0 && mins >= 0){
            formItemView.seekBar.max = maxs - mins
        }
        if(mins <= 0){
            formItemView.seekBar.max = maxs + (mins * -1)
        }

        if (format.maxDescription == null) {
            formItemView.maxDescription.visibility = View.GONE
        } else {
            formItemView.maxDescription.visibility = View.VISIBLE
            formItemView.maxDescription.text = format.maxDescription
        }

        if (format.minDescription == null) {
            formItemView.minDescription.visibility = View.GONE
        } else {
            formItemView.minDescription.visibility = View.VISIBLE
            formItemView.minDescription.text = format.minDescription
        }

        formItemView.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Write code to perform some action when progress is changed.

                currentNumberTextView?.text = calculateDisplayValue(progress).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
            }
        })

        currentNumberTextView!!.setSingleLine(true)


        if (result.result != null) {
            formItemView.seekBar.progress = calculateProgress(result.result)
            currentNumberTextView!!.text = result.result.toString()
        } else {
            currentNumberTextView!!.text = calculateDisplayValue(formItemView.seekBar.progress).toString()
        }

        return formItemView
    }

    private fun calculateDisplayValue(progress: Int): Int {
        return progress + format.minValue
    }

    private fun calculateProgress(value: Int): Int {
        return value - format.minValue
    }

    override fun getStepResult(skipped: Boolean): StepResult<*> {
        if (skipped) {
            result.setResult(null)
        } else {
            val numString = currentNumberTextView!!.text.toString()
            if (!TextUtils.isEmpty(numString)) {
                result.result = Integer.valueOf(currentNumberTextView!!.text.toString())
            }
        }

        return result
    }

    override fun getBodyAnswerState(): BodyAnswer {
        return if (currentNumberTextView == null) {
            BodyAnswer.INVALID
        } else format.validateAnswer(currentNumberTextView!!.text.toString())

    }

}
