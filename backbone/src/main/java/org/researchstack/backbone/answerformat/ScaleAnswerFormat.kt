package org.researchstack.backbone.answerformat

import org.researchstack.backbone.ui.step.body.IntegerQuestionBody
import org.researchstack.backbone.ui.step.body.ScaleQuestionBody
import org.researchstack.backbone.ui.step.body.StepBody

class ScaleAnswerFormat(
    private val minVal: Int,
    private val maxVal: Int,
    val step: Int = 1,
    val minDescription: String? = null,
    val maxDescription: String? = null
) : IntegerAnswerFormat(minVal, maxVal), AnswerFormat.QuestionType {


    override fun getQuestionType(): QuestionType {
        return this
    }

    override fun getStepBodyClass(): Class<out StepBody> {
        return if (((maxVal - minVal) / step) > 13) {
            IntegerQuestionBody::class.java
        } else {
            ScaleQuestionBody::class.java
        }
    }

}