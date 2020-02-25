package org.researchstack.backbone.step

import org.researchstack.backbone.R
import org.researchstack.backbone.answerformat.FormAnswerFormat

/**
 * The FormStep class is a concrete subclass of [Step], used for presenting multiple questions
 * on a single scrollable page.
 *
 * To use FormStep, instantiate the object, give it a list of [QuestionStep]s, and include it
 * in a task.
 *
 * The result of a form step is an [org.researchstack.backbone.result.StepResult] object that
 * includes a child StepResult object for each form item.
 */
class FormStep(
    identifier: String,
    title: String?,
    text: String?
) : QuestionStep(identifier, title, FormAnswerFormat()) {

    private var formSteps: List<QuestionStep>? = null

    init {
        setText(text)
    }

    override fun getDestinationId(): Int {
        return R.id.rsb_form_step_fragment
    }

    /**
     * Returns the list of items in the form.
     *
     * @return a list of QuestionSteps in the form
     */
    fun getFormSteps(): List<QuestionStep>? {
        return formSteps
    }

    fun setFormSteps(vararg formSteps: QuestionStep) {
        setFormSteps(listOf(*formSteps))
    }

    fun setFormSteps(formSteps: List<QuestionStep>) {
        this.formSteps = formSteps
    }
}