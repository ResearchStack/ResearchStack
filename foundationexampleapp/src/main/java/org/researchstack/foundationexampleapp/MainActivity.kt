package org.researchstack.kotlinbackbonesampleapp


import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.os.CancellationSignal
import org.researchstack.feature.authentication.pincode.PasscodeAuthenticator
import org.researchstack.feature.authentication.pincode.ui.PasscodeAuthenticationFragment
import org.researchstack.feature.storage.StorageAccess
import org.researchstack.feature.storage.file.StorageAccessListener
import org.researchstack.feature.consent.ui.layout.ConsentSignatureStepLayout
import org.researchstack.foundation.components.presentation.TaskPresentationCallback
import org.researchstack.foundation.components.presentation.compatibility.BackwardsCompatibleStepFragmentProvider
import org.researchstack.foundation.components.presentation.compatibility.BackwardsCompatibleStepLayoutProvider
import org.researchstack.foundation.components.presentation.compatibility.BackwardsCompatibleTaskPresentationFragment
import org.researchstack.foundation.components.utils.LogExt
import org.researchstack.foundation.core.models.result.TaskResult
import org.researchstack.foundation.core.models.task.Task
import org.researchstack.foundationexampleapp.R

class MainActivity : AppCompatActivity(), StorageAccessListener, PasscodeAuthenticator.PresentationDelegate {

    companion object {
        val TAG = MainActivity.javaClass.name
    }

    // Views
    private var consentButton: AppCompatButton? = null
    private var surveyButton: AppCompatButton? = null
    private var taskPresentationFragment: BackwardsCompatibleTaskPresentationFragment? = null

    val taskProvider = FoundationTaskProvider(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        this.supportActionBar!!.setDisplayShowTitleEnabled(true)

        this.consentButton = findViewById(R.id.consent_button) as AppCompatButton
        this.consentButton!!.setOnClickListener {
            launchConsent()
        }

        this.surveyButton = findViewById(R.id.survey_button) as AppCompatButton
        this.surveyButton!!.setOnClickListener {
            launchSurvey()
        }

        val passcodeAuthenticator: PasscodeAuthenticator = FoundationApplication.instance!!.passcodeAuthenticator!!
        passcodeAuthenticator.setPresentationDelegate(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_clear) {
            clearData()
            Toast.makeText(this, R.string.menu_data_cleared, Toast.LENGTH_SHORT).show()
            return true
        }
        else if (item.itemId == android.R.id.home) {
            this.taskPresentationFragment?.onBackPressed()
            return true
        }
        else {
            return super.onOptionsItemSelected(item)
        }
    }

    private fun clearData() {
        val appPrefs = AppPrefs.getInstance(this)
        appPrefs.setHasSurveyed(false)
        appPrefs.setHasConsented(false)

        initViews()
    }

    override fun onDataReady() {
        this.storageAccessUnregister()
        initViews()
    }


    override fun onDataAuth() {

    }

    override fun presentPasscodeAuthentication(authenticator: PasscodeAuthenticator, cancel: CancellationSignal, callback: PasscodeAuthenticator.PasscodeAuthenticationCallback) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        //present pin code fragment

        val context = this as Context

        val fragmentCallback = object: PasscodeAuthenticationFragment.AuthenticationCallback() {
            override fun onAuthenticationFailed() {
                callback.onAuthenticationFailed()
            }

            override fun onAuthenticationSucceeded() {
                callback.onAuthenticationSucceeded(PasscodeAuthenticator.PasscodeAuthenticationResult())

                supportFragmentManager.findFragmentByTag("passcode_fragment")?.let { fragment ->
                    supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.rsf_slide_in_up, R.anim.rsf_slide_out_down)
                            .remove(fragment)
                            .commit()
                }

                //need to request storage access again here!!
                StorageAccess.getInstance().requestStorageAccess(context)
            }
        }

        val passcodeFragment = PasscodeAuthenticationFragment.newInstance(authenticator, fragmentCallback)
        val transaction = supportFragmentManager.beginTransaction()
                .replace(R.id.authorization_container, passcodeFragment, "passcode_fragment")
                .commit()

        supportFragmentManager.executePendingTransactions()
    }

    override fun onDataFailed() {
        this.storageAccessUnregister()
    }

    fun requestStorageAccess() {
        LogExt.i(TAG, "requestStorageAccess()")
        val storageAccess = StorageAccess.getInstance()
        storageAccessRegister()
        storageAccess.requestStorageAccess(this)
    }

    fun storageAccessRegister() {
        LogExt.i(TAG, "storageAccessRegister()")
        val storageAccess = StorageAccess.getInstance()
        storageAccess.register(this)
    }

    fun storageAccessUnregister() {
        LogExt.i(TAG, "storageAccessUnregister()")
        val storageAccess = StorageAccess.getInstance()
        storageAccess.unregister(this)
    }

    override fun onResume() {
        super.onResume()

        val storageAccess = StorageAccess.getInstance()
        if (!storageAccess.hasPinCode(this)) {
            this.launchPinCodeRegistration()
        }
        else {
            this.requestStorageAccess()
        }

    }

    private fun launchTask(taskIdentifier:String) {

        val layoutProvider = BackwardsCompatibleStepLayoutProvider()
        val stepFragmentProvider = BackwardsCompatibleStepFragmentProvider(layoutProvider)

        val callback: TaskPresentationCallback<TaskResult, Task> = object: TaskPresentationCallback<TaskResult, Task>() {
            override fun onTaskPresentationFinished(task: Task, result: TaskResult?) {

                taskPresentationFragment = null
                supportFragmentManager.findFragmentByTag(taskIdentifier)?.let { fragment ->
                    supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.rsf_slide_in_up, R.anim.rsf_slide_out_down)
                            .remove(fragment)
                            .commit()
                }

                result?.let {
                    processTaskResult(task, it)
                }

            }
        }

        val taskPresentationFragment = BackwardsCompatibleTaskPresentationFragment.newInstance(taskIdentifier, stepFragmentProvider, callback)
        taskPresentationFragment.taskProvider = this.taskProvider

        this.taskPresentationFragment = taskPresentationFragment

        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.rsf_slide_in_up, R.anim.rsf_slide_out_down)
                .add(R.id.task_presentation_container, taskPresentationFragment, taskIdentifier)
                .commit()

    }

    private fun processTaskResult(task: Task, result: TaskResult) {
        if (task.identifier == FoundationTaskProvider.PIN_CODE) {
            this.processPinCodeResult(result)
        }
        else if (task.identifier == FoundationTaskProvider.CONSENT) {
            this.processConsentResult(result)
        }
        else if (task.identifier == FoundationTaskProvider.SAMPLE_SURVEY) {
            this.processSurveyResult(result)
        }
    }

    private fun launchPinCodeRegistration() {
        this.launchTask(FoundationTaskProvider.PIN_CODE)
    }

    private fun processPinCodeResult(result: TaskResult) {
        val pinCode: String = result.getStepResult(FoundationTaskProvider.PIN_CODE)!!.result as String
        val storageAccess = StorageAccess.getInstance()
        storageAccess.createPinCode(this, pinCode)
    }

    private fun initViews() {
        val prefs = AppPrefs.getInstance(this)

        val lblConsentedDate = findViewById(R.id.consented_date_lbl) as TextView
        val consentedDate = findViewById(R.id.consented_date) as TextView
        val consentedSig = findViewById(R.id.consented_signature) as ImageView

        if (prefs.hasConsented()) {
            this.consentButton!!.setVisibility(View.GONE)
            this.surveyButton!!.setEnabled(true)

            consentedSig.visibility = View.VISIBLE
            consentedDate.visibility = View.VISIBLE
            lblConsentedDate.setVisibility(View.VISIBLE)

            printConsentInfo(consentedDate, consentedSig)
        } else {
            this.consentButton!!.setVisibility(View.VISIBLE)
            this.surveyButton!!.setEnabled(false)

            consentedSig.visibility = View.INVISIBLE
            consentedSig.setImageBitmap(null)
            consentedDate.visibility = View.INVISIBLE
            lblConsentedDate.setVisibility(View.INVISIBLE)
        }

        val surveyAnswer = findViewById(R.id.survey_results) as TextView

        if (prefs.hasSurveyed()) {
            surveyAnswer.visibility = View.VISIBLE
            printSurveyInfo(surveyAnswer)
        } else {
            surveyAnswer.visibility = View.GONE
        }
    }

    // Consent stuff

    private fun launchConsent() {
        this.launchTask(FoundationTaskProvider.CONSENT)
    }

    private fun processConsentResult(result: TaskResult) {
        val consented = result.getStepResult(FoundationTaskProvider.CONSENT_DOC).result as Boolean

        if (consented) {
            StorageAccess.getInstance().appDatabase.saveTaskResult(result)

            val prefs = AppPrefs.getInstance(this)
            prefs.setHasConsented(true)

            initViews()
        }
    }

    private fun printConsentInfo(consentedDate: TextView, consentedSig: ImageView) {
        val result = StorageAccess.getInstance()
                .appDatabase
                .loadLatestTaskResult(FoundationTaskProvider.CONSENT)

        val signatureBase64 = result.getStepResult(FoundationTaskProvider.SIGNATURE)
                .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE) as String

        val signatureDate = result.getStepResult(FoundationTaskProvider.SIGNATURE)
                .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE_DATE) as String

        consentedDate.text = signatureDate

        val signatureBytes = Base64.decode(signatureBase64, Base64.DEFAULT)
        consentedSig.setImageBitmap(BitmapFactory.decodeByteArray(
                signatureBytes,
                0,
                signatureBytes.size))
    }


    // Survey Stuff

    private fun launchSurvey() {
        this.launchTask(FoundationTaskProvider.SAMPLE_SURVEY)
    }

    private fun processSurveyResult(result: TaskResult) {
        StorageAccess.getInstance().appDatabase.saveTaskResult(result)

        val prefs = AppPrefs.getInstance(this)
        prefs.setHasSurveyed(true)
        initViews()
    }

    private fun printSurveyInfo(surveyAnswer: TextView) {
        val taskResult = StorageAccess.getInstance()
                .appDatabase
                .loadLatestTaskResult(FoundationTaskProvider.SAMPLE_SURVEY)

        var results = ""
        for (id in taskResult.results.keys) {
            val stepResult = taskResult.getStepResult(id)
            results += id + ": " + stepResult.result.toString() + "\n"
        }

        surveyAnswer.text = results
    }



}
