package org.researchstack.backbone.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.researchstack.backbone.R
import org.researchstack.backbone.answerformat.BirthDateAnswerFormat
import org.researchstack.backbone.answerformat.TextAnswerFormat
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.step.ConsentDocumentStep
import org.researchstack.backbone.step.ConsentSignatureStep
import org.researchstack.backbone.step.FormStep
import org.researchstack.backbone.step.QuestionStep
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.task.Task
import androidx.lifecycle.Observer
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout.KEY_SIGNATURE
import org.researchstack.backbone.ui.task.TaskActivity
import org.researchstack.backbone.ui.task.TaskViewModel
import org.researchstack.backbone.utils.LocaleUtils
import org.researchstack.backbone.utils.RSHTMLPDFWriter
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class ConsentTaskActivity : TaskActivity() {
    private val viewModel: TaskViewModel by viewModel { parametersOf(intent) }
    private var consentHtml: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var signatureBase64: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.saveStepEvent.observe(this, Observer {
            onSaveStep(it.first, it.second)
        })
    }

    private fun onSaveStep(step: Step, result: StepResult<*>?) {
        if (step is FormStep && step.getIdentifier().equals(ID_FORM, true)) {
            for (question in step.getFormSteps()!!) {
                if (question.identifier.equals(ID_FORM_FIRST_NAME, true)) {
                    val nameResult = result?.getResultForIdentifier(ID_FORM_FIRST_NAME) as StepResult<*>?
                    if (nameResult != null && nameResult.result != null) {
                        firstName = nameResult.result as String
                    }
                }
                if (question.identifier.equals(ID_FORM_LAST_NAME, true)) {
                    val nameResult = result?.getResultForIdentifier(ID_FORM_LAST_NAME) as StepResult<*>?
                    if (nameResult != null && nameResult.result != null) {
                        lastName = nameResult.result as String
                    }
                }
            }
        }
        if (step is ConsentSignatureStep) {
            signatureBase64 = result?.getResultForIdentifier(KEY_SIGNATURE) as String
        } else if (step is ConsentDocumentStep) {
            consentHtml = step.consentHTML
        }
    }

    private fun getFormalName(firstName: String?, lastName: String?): String? {
        return if (lastName != null && firstName != null) {
            "$lastName, $firstName"
        } else {
            firstName ?: lastName
        }
    }

    override fun close(completed: Boolean) {
        // you can also set title / message
        val dialog = AlertDialog.Builder(this).setCancelable(false)
                .setTitle(LocaleUtils.getLocalizedString(this, R.string.rsb_saving_consent))
                .setMessage(LocaleUtils.getLocalizedString(this, R.string.rsb_please_wait)).create()

        dialog.show()

        val consentAssetsFolder = intent.getStringExtra(EXTRA_ASSETS_FOLDER)
        val role = LocaleUtils.getLocalizedString(this, R.string.rsb_consent_role)
        val df = DateFormat.getDateInstance(DateFormat.MEDIUM,
                LocaleUtils.getLocaleFromString(LocaleUtils.getPreferredLocale(this)))
        consentHtml += getSignatureHtmlContent(getFormalName(firstName, lastName), role, signatureBase64,
                df.format(Date()))

        PDFWriteExposer().printPdfFile(this, getCurrentTaskId(), consentHtml!!, consentAssetsFolder) {
            dialog.dismiss()
            super.close(completed)
        }
    }

    private fun getSignatureHtmlContent(completeName: String?, role: String?, signatureB64: String?,
                                        signatureDate: String?): String {
        val body = StringBuffer()

        val hr = "<hr align='left' width='100%' style='height:1px; border:none; color:#000; background-color:#000; margin-top: 5px; margin-bottom: 0px;'/>"
        val td = "<td style='vertical-align: bottom;" + "width: 33%;" + "padding-right: 10px;" + "padding-left: 10px;'>"

        val signatureElementWrapper = "<div class='sigbox'><div class='inbox'>%s</div></div>"
        val imageTag: String

        val table = ArrayList<Array<String>>()

        if (role == null) {
            throw RuntimeException("Consent role cannot be empty")
        }

        // Signature
        if (completeName != null) {
            val columnName = getString(R.string.rsb_consent_doc_line_printed_name, role)
            val columnValue = String.format(signatureElementWrapper, completeName)
            val column = arrayOf(columnValue, hr, columnName)
            table.add(column)
        }

        if (signatureB64 != null) {
            imageTag = "<img width='100%' alt='star' style='max-height:100px;max-width:200px;height:auto;width:auto;'" +
                    " src='data:image/png;base64,$signatureB64'/>"
            val columnName = getString(R.string.rsb_consent_doc_line_signature, role)
            val columnValue = String.format(signatureElementWrapper, imageTag)
            val column = arrayOf(columnValue, hr, columnName)
            table.add(column)
        }

        if (table.size > 0) {
            val columnName = getString(R.string.rsb_consent_doc_line_date)
            val columnValue = String.format(signatureElementWrapper, signatureDate!!)
            val column = arrayOf(columnValue, hr, columnName)
            table.add(column)
        }

        if (table.size > 0) {
            val columns = table[0].size
            body.append("<table width='100%'>")
            for (i in 0 until columns) {
                body.append("<tr>")
                for (j in table.indices) {
                    body.append(td).append(table[j][i]).append("</td>")
                }
                body.append("</tr>")
            }
            body.append("</table>")
        }

        return body.toString()
    }

    internal inner class PDFWriteExposer : RSHTMLPDFWriter() {
        public override fun printPdfFile(context: Activity, taskId: String?, htmlConsentDocument: String,
                                         assetsFolder: String?, callback: PDFFileReadyCallback) {
            super.printPdfFile(context, taskId, htmlConsentDocument, assetsFolder, callback)
        }
    }

    companion object {

        private const val ID_FORM_FIRST_NAME = "user_info_form_first_name"
        private const val ID_FORM_LAST_NAME = "user_info_form_last_name"
        private const val ID_FORM_DOB = "user_info_form_dob"
        private const val ID_FORM = "user_info_form"
        private const val EXTRA_ASSETS_FOLDER = "extra_assets_folder"

        fun newIntent(context: Context, task: Task, assetsFolder: String): Intent {
            val intent = Intent(context, ConsentTaskActivity::class.java)
            intent.putExtra(EXTRA_TASK, task)
            intent.putExtra(EXTRA_ASSETS_FOLDER, assetsFolder)
            return intent
        }

        /**
         * Returns the personal form steps related with the consent view task.
         *
         *
         * This new version is prepared to work with multilanguage.
         * Please use this method instead of
         * [getConsentPersonalInfoFormStep][.getConsentPersonalInfoFormStep]
         */
        fun getConsentPersonalInfoFormStep(context: Context?, requiresName: Boolean,
                                           requiresBirthDate: Boolean): FormStep? {
            if (requiresName || requiresBirthDate) {
                val formSteps = ArrayList<QuestionStep>()
                if (requiresName) {
                    val firstName = if (context != null) LocaleUtils.getLocalizedString(context,
                            R.string.rsb_name_first) else "First Name"
                    formSteps.add(QuestionStep(ID_FORM_FIRST_NAME, firstName, TextAnswerFormat()))

                    val lastName = if (context != null) LocaleUtils.getLocalizedString(context,
                            R.string.rsb_name_last) else "Last Name"
                    formSteps.add(QuestionStep(ID_FORM_LAST_NAME, lastName, TextAnswerFormat()))
                }

                if (requiresBirthDate) {
                    val maxDate = Calendar.getInstance()
                    maxDate.add(Calendar.YEAR, -18)

                    val dobFormat = BirthDateAnswerFormat(null, 18, 0)
                    val dobText = if (context != null) LocaleUtils.getLocalizedString(context,
                            R.string.rsb_consent_dob_full) else "Date of birth"
                    formSteps.add(QuestionStep(ID_FORM_DOB, dobText, dobFormat))
                }

                val formTitle = if (context != null) LocaleUtils.getLocalizedString(context,
                        R.string.rsb_consent) else "Consent"
                val formStep = FormStep(ID_FORM, formTitle, "")
                formStep.isOptional = false
                formStep.setFormSteps(formSteps)

                return formStep
            }

            return null
        }
    }
}
