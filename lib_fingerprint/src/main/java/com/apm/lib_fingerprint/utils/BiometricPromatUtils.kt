package com.apm.lib_fingerprint.utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.apm.lib_fingerprint.R
import com.apm.lib_fingerprint.bean.User
import com.apm.lib_fingerprint.config.CIPHERTEXT_WRAPPER
import com.apm.lib_fingerprint.config.SHARED_PREFS_FILENAME
import com.apm.lib_fingerprint.extensions.toast
import com.apm.lib_fingerprint.listener.BiometricPromptListener

/**
 * @author: dinglei
 * @date: 2020/5/8 13:57
 */
class BiometricPromptUtils(private val context: Context) {

    companion object BiometricPromptUtilsFactory {
        fun build(context: Context): BiometricPromptUtils {
            return BiometricPromptUtils(context)
        }
    }

    private lateinit var cryptographyManager: CryptographyManager

    private var onBiometricPromptListener: BiometricPromptListener? = null

    private val mBiometricManager: BiometricManager by lazy { BiometricManager.from(context) }

    /**
     * 判断设备是否支持生物识别
     */
    fun isSupportFingerprint(): Boolean {
        return mBiometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * 判断设备是否录入了指纹
     */
    fun isEnrolledFingerprint(): Boolean {
        return mBiometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
    }

    private fun createBiometricPrompt(
        processSuccess: (BiometricPrompt.AuthenticationResult) -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                processSuccess(result)
                onBiometricPromptListener?.success()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onBiometricPromptListener?.failed()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d("BiomericPromat", "errorCode: $errorCode , errorString: $errString")
                when (errorCode) {
                    BiometricConstants.ERROR_CANCELED -> {
                        onBiometricPromptListener?.cancel()
                    }
                    BiometricConstants.ERROR_LOCKOUT -> {
                        (context as AppCompatActivity).toast(errString.toString())
                        onBiometricPromptListener?.error(errorCode, errString)
                    }
                    else -> {
                        onBiometricPromptListener?.error(errorCode, errString)
                    }
                }
            }
        }
        return BiometricPrompt(context as AppCompatActivity, executor, callback)
    }

    private fun createPromptInfo(context: Context): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(context.getString(R.string.prompt_info_title))
            setConfirmationRequired(false)

            setNegativeButtonText(context.getString(R.string.prompt_info_use_app_password))
        }.build()

    /**
     * 指纹识别
     */
    fun showBiometricPromptForEncryption(): BiometricPromptUtils {
        val canAuthenticate = mBiometricManager.canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val secretKeyName = context.getString(R.string.secret_key_name)
            cryptographyManager = cryptographyManager()
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            val biometricPrompt = createBiometricPrompt(::encryptAndStoreServerToken)
            val promptInfo = createPromptInfo(context)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
        return this
    }

    fun setOnBiometricPromptListener(onBiometricPromptListener: BiometricPromptListener): BiometricPromptUtils {
        this.onBiometricPromptListener = onBiometricPromptListener
        return this
    }

    private fun encryptAndStoreServerToken(authResult: BiometricPrompt.AuthenticationResult) {
        authResult.cryptoObject?.cipher?.apply {
            User.fakeToken?.let { token ->
                val encryptedServerTokenWrapper = cryptographyManager.encryptData(token, this)
                cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedServerTokenWrapper,
                    context,
                    SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CIPHERTEXT_WRAPPER
                )
            }
        }
    }
}