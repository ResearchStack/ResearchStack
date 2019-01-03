package org.researchstack.feature.authentication.pincode.ui

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import org.researchstack.feature.authentication.R
import org.researchstack.feature.authentication.pincode.PasscodeAuthenticator
import org.researchstack.foundation.components.utils.ThemeUtils

open class PasscodeAuthenticationFragment: Fragment() {

    open class AuthenticationCallback {
        open fun onAuthenticationFailed() {}
        open fun onAuthenticationSucceeded() {}
    }

    companion object {
        fun newInstance(
                authenticator: PasscodeAuthenticator,
                callback: AuthenticationCallback
        ): PasscodeAuthenticationFragment {
            val fragment = PasscodeAuthenticationFragment()
            fragment.authenticator = authenticator
            fragment.callback = callback
            return fragment
        }
    }

    private var authenticator: PasscodeAuthenticator? = null
    private var callback: AuthenticationCallback? = null
    private var toggleKeyboardAction: ((Boolean) -> Unit)? = null

    override open fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Show pincode layout
        val authenticator = this.authenticator!!
        val config = authenticator.passcodeConfig

        val themeResId = ThemeUtils.getPassCodeTheme(this.activity as Context)
        val context = ContextThemeWrapper(this.activity, themeResId)
        val pinCodeLayout = PinCodeLayout(context)
        pinCodeLayout.setBackgroundColor(Color.WHITE)

        val errorColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(R.color.rsf_error, context.theme)
        } else {
            resources.getColor(R.color.rsf_error)
        }

        val summary = pinCodeLayout.findViewById(R.id.text) as TextView
        val pincode = pinCodeLayout.findViewById(R.id.pincode) as EditText

        this.toggleKeyboardAction = { enable ->
            pincode.isEnabled = enable
            pincode.setText("")
            pincode.requestFocus()
            if (enable) {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(pincode, InputMethodManager.SHOW_FORCED)
            }
        }

        val pinCodeWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (summary.currentTextColor == errorColor) {
                    summary.setTextColor(ThemeUtils.getTextColorPrimary(context))
                    pinCodeLayout.resetSummaryText()
                }

                val fullLengthPin: String? = s?.toString()?.let { pin ->
                    //check to see if pin is the right length
                    if (pin.length == config.pinLength) pin else null
                }

                if (fullLengthPin != null) {

                    pincode.isEnabled = false
                    pinCodeLayout.showProgress(true)
                    //check to see if pin matches

                    class CheckPinCodeTask(val pinCode: String) : AsyncTask<Void, Void, Boolean>() {
                        override fun doInBackground(vararg params: Void?): Boolean {
                            try {
                                authenticator.store.checkPasscode(pinCode)
                                return true
                            }
                            catch (e: PasscodeAuthenticator.PasscodeAuthenticatorException) {
                                e.printStackTrace()
                                return false
                            }
                        }

                        override fun onPostExecute(result: Boolean) {

                            if (result) {
                                callback!!.onAuthenticationSucceeded()
                            }
                            else {
                                toggleKeyboardAction?.invoke(true)
                                summary.setText(R.string.rsfa_pincode_enter_error)
                                summary.setTextColor(errorColor)
                                pinCodeLayout.showProgress(false)
                                callback!!.onAuthenticationFailed()
                            }
                        }

                    }

                    CheckPinCodeTask(fullLengthPin).execute()

                }
            }
        }

        pincode.addTextChangedListener(pinCodeWatcher)

        return pinCodeLayout
    }

}