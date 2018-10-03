package org.researchstack.backbone.answerformat

import org.researchstack.backbone.answerformat.AnswerFormat
import org.researchstack.backbone.answerformat.IntegerAnswerFormat
import org.researchstack.backbone.ui.step.body.IntegerQuestionBody
import org.researchstack.backbone.ui.step.body.ScaleQuestionBodyType
import org.researchstack.backbone.ui.step.body.StepBody

class ScaleAnswerFormat(private val minVal: Int, private val maxVal: Int, val step: Int = 1) : IntegerAnswerFormat(), AnswerFormat.QuestionType {


    override fun getQuestionType(): QuestionType {
        return this
    }

    override fun getStepBodyClass(): Class<out StepBody> {
        return if (maxVal > 15) {
            IntegerQuestionBody::class.java
        } else {
            ScaleQuestionBodyType::class.java
        }
    }

}