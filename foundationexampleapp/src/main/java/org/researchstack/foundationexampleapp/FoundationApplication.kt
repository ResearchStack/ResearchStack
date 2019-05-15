package org.researchstack.kotlinbackbonesampleapp

import android.app.Application
import android.text.format.DateUtils
import androidx.core.os.CancellationSignal
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import net.sqlcipher.database.SQLiteDatabase
import org.researchstack.feature.storage.StorageAccess
import org.researchstack.feature.storage.database.sqlite.DatabaseHelper
import org.researchstack.feature.storage.database.sqlite.SqlCipherDatabaseHelper
import org.researchstack.feature.storage.database.sqlite.UpdatablePassphraseProvider
import org.researchstack.feature.storage.file.SimpleFileAccess
import org.researchstack.feature.storage.file.aes.AesProvider

import org.researchstack.feature.authentication.pincode.PasscodeAuthenticator
import org.researchstack.feature.authentication.pincode.PinCodeConfig
import org.researchstack.feature.authentication.pincode.PinCodeConfigProvider
import org.researchstack.feature.storage.StorageAccessPasscodeStore

public class FoundationLifecycleObserver: DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        val passcodeAuthenticator: PasscodeAuthenticator = FoundationApplication.instance.passcodeAuthenticator!!

        if (passcodeAuthenticator.isRegistered()) {
            val cancelationSignal = CancellationSignal()

            cancelationSignal.setOnCancelListener {

            }

            val callback = object: PasscodeAuthenticator.PasscodeAuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: PasscodeAuthenticator.PasscodeAuthenticationResult) {

                }
            }

            passcodeAuthenticator.authenticate(cancelationSignal, callback)
        }

    }

}

public class FoundationApplication : Application() {

    var passcodeAuthenticator: PasscodeAuthenticator? = null
    val lifecycleObserver = FoundationLifecycleObserver()

    companion object {

        val TAG = FoundationApplication.javaClass.name

        private var _instance: FoundationApplication? = null
        val instance: FoundationApplication
            get() = this._instance!!

        fun configure(instance: FoundationApplication) {
            this._instance = instance
        }
    }

    override fun onCreate() {
        super.onCreate()

        FoundationApplication.configure(this)

        val pinCodeConfig = PinCodeConfig(PinCodeConfig.PinCodeType.AlphaNumeric, 6, 5 * DateUtils.MINUTE_IN_MILLIS)
        PinCodeConfigProvider.config(pinCodeConfig)

        val encryptionProvider = AesProvider()
        val fileAccess = SimpleFileAccess()

        SQLiteDatabase.loadLibs(this)

        val database = SqlCipherDatabaseHelper(
                this,
                DatabaseHelper.DEFAULT_NAME,
                null,
                DatabaseHelper.DEFAULT_VERSION,
                UpdatablePassphraseProvider()
        )

        StorageAccess.getInstance().init(pinCodeConfig, encryptionProvider, fileAccess, database)

        val passcodeStore = StorageAccessPasscodeStore(this)
        this.passcodeAuthenticator = PasscodeAuthenticator(pinCodeConfig, passcodeStore)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this.lifecycleObserver)
    }

}