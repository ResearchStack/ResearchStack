package org.researchstack.backbone.answerformat

import org.researchstack.backbone.ui.step.body.ContinuousScaleQuestionBody
import org.researchstack.backbone.ui.step.body.IntegerQuestionBody
import org.researchstack.backbone.ui.step.body.StepBody

class ContinuousScaleAnswerFormat(private val minVal: Int, private val maxVal: Int) : IntegerAnswerFormat(minVal, maxVal), AnswerFormat.QuestionType {


    override fun getQuestionType(): QuestionType {
        return this
    }

    override fun getStepBodyClass(): Class<out StepBody> {
        return if (maxVal - minVal > 10000) {
            IntegerQuestionBody::class.java
        } else {
            ContinuousScaleQuestionBody::class.java
        }
    }

}