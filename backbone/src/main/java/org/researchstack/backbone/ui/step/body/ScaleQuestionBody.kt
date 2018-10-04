package org.researchstack.backbone.ui.step.body

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.rsb_scale_question_layout.view.*
import org.researchstack.backbone.R
import org.researchstack.backbone.answerformat.IntegerAnswerFormat
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.step.QuestionStep
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.utils.TextUtils

class ScaleQuestionBody(step: Step, result: StepResult<*>?) : StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    protected var step: QuestionStep = step as QuestionStep
    protected var result: StepResult<Int> = result as StepResult<Int>? ?: StepResult(step)
    protected var format: IntegerAnswerFormat

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    protected var viewType: Int = 0
    protected var currentNumberTextView: TextView? = null

    init {
        this.format = this.step.answerFormat as IntegerAnswerFormat
    }

    override fun getBodyView(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): View {
        this.viewType = viewType

        return getViewForType(viewType, inflater, parent)
    }

    private fun getViewForType(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): View {
        return initViewCompact(inflater, parent)
    }

    protected fun initViewCompact(inflater: LayoutInflater, parent: ViewGroup): View {
        val formItemView = inflater.inflate(R.layout.rsb_scale_question_layout, parent, false)

        currentNumberTextView = formItemView.findViewById(R.id.value)
        setFilters(parent.context)
        formItemView.rsbRangeStart.text = format.minValue.toString()

        formItemView.rsbRangeEnd.text = format.maxValue.toString()

        formItemView.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Write code to perform some action when progress is changed.
                currentNumberTextView?.text = seekBar.progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
            }
        })

        return formItemView
    }

    protected fun setFilters(context: Context) {
        currentNumberTextView!!.setSingleLine(true)
        val minValue = format.minValue
        // allow any positive int if no max value is specified
        val maxValue = if (format.maxValue == 0) Integer.MAX_VALUE else format.maxValue

        if (result.result != null) {
            currentNumberTextView!!.text = result.result.toString()
        }

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
