package org.researchstack.kotlinbackbonesampleapp

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class AppPrefs {

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Statics
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    companion object {

        val HAS_CONSENTED = "HAS_CONSENTED"
        val CONSENT_NAME = "CONSENT_NAME"
        val CONSENT_SIGNATURE = "CONSENT_SIGNATURE"
        val HAS_SURVEYED = "HAS_SURVEYED"
        val SURVEY_RESULT = "SURVEY_RESULT"

        private var instance: AppPrefs? = null

        @Synchronized
        fun getInstance(context: Context): AppPrefs {
            return this.instance?.also { a ->
                return a
            } ?: run {
                val instance = AppPrefs(context)
                this.instance = instance
                return instance
            }

        }
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Field Vars
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private var prefs: SharedPreferences


    constructor(context: Context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun hasConsented(): Boolean {
        return this.prefs.getBoolean(HAS_CONSENTED, false)
    }

    fun setHasConsented(consented: Boolean) {
        this.prefs.edit().putBoolean(HAS_CONSENTED, consented).apply()
    }

    fun hasSurveyed(): Boolean {
        return this.prefs.getBoolean(HAS_SURVEYED, false)
    }

    fun setHasSurveyed(surveyed: Boolean) {
        this.prefs.edit().putBoolean(HAS_SURVEYED, surveyed).apply()
    }
}