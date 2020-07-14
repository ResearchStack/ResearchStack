package org.researchstack.backbone.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.researchstack.backbone.R
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.step.*
import org.researchstack.backbone.task.Task
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout.KEY_SIGNATURE
import org.researchstack.backbone.ui.task.TaskActivity
import org.researchstack.backbone.ui.task.TaskViewModel
import org.researchstack.backbone.utils.LocalizationUtils
import org.researchstack.backbone.utils.RSHTMLPDFWriter
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

@Deprecated("Deprecated as part of the new handling for the branching logic",
        ReplaceWith("com.medable.axon.ui.taskrunner.NRSConsentTaskActivity"))
class ConsentTaskActivity : TaskActivity() {
    private val viewModel: TaskViewModel by viewModel { parametersOf(intent) }
    private var consentHtml: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var signatureBase64: String? = null
    private var savingConsentDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.saveStepEvent.observe(this, Observer {
            onSaveStep(it.first, it.second)
        })
    }

    override fun onStop() {
        savingConsentDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
            savingConsentDialog = null
        }
        super.onStop()
    }

    private fun onSaveStep(step: Step, result: StepResult<*>?) {
        if (step is FormStep && step.identifier.equals(CONSENT_FORM_ID, true)) {
            step.getFormSteps()?.forEach { question ->
                getResultForName(question, CONSENT_FORM_ID_FIRST_NAME, result)?.let { firstName = it }
                getResultForName(question, CONSENT_FORM_ID_LAST_NAME, result)?.let { lastName = it }
            }
        } else if (step is ConsentSignatureStep) {
            signatureBase64 = result?.getResultForIdentifier(KEY_SIGNATURE) as String
        } else if (step is ConsentDocumentStep) {
            consentHtml = step.consentHTML
        }
    }

    private fun getResultForName(question: QuestionStep, identifier: String, result: StepResult<*>?): String? {
        return if (question.identifier.equals(identifier, true)) {
            val nameResult = result?.getResultForIdentifier(identifier) as StepResult<*>?
            if (nameResult != null && nameResult.result != null) {
                nameResult.result as String
            } else {
                ""
            }
        } else {
            null
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
        savingConsentDialog = AlertDialog.Builder(this).setCancelable(false)
                .setTitle(
                        LocalizationUtils.getLocalizedString(this, R.string.rsb_saving_consent))
                .setMessage(
                        LocalizationUtils.getLocalizedString(this, R.string.rsb_please_wait)).create()

        savingConsentDialog?.show()

        val consentAssetsFolder = intent.getStringExtra(EXTRA_ASSETS_FOLDER)
        val role = LocalizationUtils.getLocalizedString(this, R.string.rsb_consent_role)
        val df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG,
                LocalizationUtils.getLocaleFromString(LocalizationUtils.getPreferredLocale(this)))
        df.timeZone = TimeZone.getTimeZone("UTC")
        val endDate = viewModel.taskResult.endDate ?: Date()
        consentHtml += getSignatureHtmlContent(getFormalName(firstName, lastName), role, signatureBase64,
                df.format(endDate))

        PDFWriteExposer().printPdfFile(this, getCurrentTaskId(), consentHtml!!, consentAssetsFolder) {
            savingConsentDialog?.dismiss()
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

        if (table.isEmpty().not()) {
            val columnName = getString(R.string.rsb_consent_doc_line_date)
            val columnValue = String.format(signatureElementWrapper, signatureDate!!)
            val column = arrayOf(columnValue, hr, columnName)
            table.add(column)

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

        private const val CONSENT_FORM_ID_FIRST_NAME = "user_info_form_first_name"
        private const val CONSENT_FORM_ID_LAST_NAME = "user_info_form_last_name"
        private const val CONSENT_FORM_ID = "user_info_form"
        private const val EXTRA_ASSETS_FOLDER = "extra_assets_folder"

        fun newIntent(context: Context, task: Task, assetsFolder: String): Intent {
            val intent = Intent(context, ConsentTaskActivity::class.java)
            intent.putExtra(EXTRA_TASK, task)
            intent.putExtra(EXTRA_ASSETS_FOLDER, assetsFolder)
            return intent
        }
    }
}
