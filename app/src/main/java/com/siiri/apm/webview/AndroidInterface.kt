package com.siiri.apm.webview

import android.webkit.JavascriptInterface
import com.apm.lib_fingerprint.listener.BiometricPromptListener
import com.apm.lib_fingerprint.utils.BiometricPromptUtils
import com.apm.lib_update.CheckUpdate
import com.blankj.utilcode.util.GsonUtils
import com.apm.lib_update.bean.UpdateEntity
import com.just.agentweb.AgentWeb
import com.siiri.apm.config.Api
import com.siiri.apm.ui.activity.MainActivity
import com.siiri.apm.utils.PhoneInfoCheck
import com.siiri.apm.utils.UserUtils
import java.lang.Exception

/**
 * @author: dinglei
 * @date: 2020/9/13 08:55
 */
class AndroidInterface(
    private val activity: MainActivity,
    private val agentWeb: AgentWeb
) {

    @JavascriptInterface
    fun saveUserInfo(userInfoStr: String) {
        UserUtils.userInfoStr = userInfoStr
        activity.setAliasPush(true)
    }

    /**
     * 清除用户信息
     */
    @JavascriptInterface
    fun clearUserInfo() {
        activity.setAliasPush(false)
        UserUtils.userInfoStr = ""
    }

    @JavascriptInterface
    fun startFingerprint() {
        PhoneInfoCheck.startFingerprint(activity, android.os.Build.BRAND)
    }

    @JavascriptInterface
    fun checkSupportFingerprint() {
        agentWeb.jsAccessEntrace?.quickCallJs(
            "isSupportFingerprint",
            BiometricPromptUtils.build(activity).isSupportFingerprint().toString()
        )
    }

    @JavascriptInterface
    fun setUpdateInfo(updateInfoStr: String) {
        try {
            val entity = GsonUtils.fromJson(updateInfoStr, UpdateEntity::class.java)
            CheckUpdate.check(entity, Api.APP_DOMAIN)
        } catch (e: Exception) {

        }
    }

    /**
     *  调用指纹识别
     */
    @JavascriptInterface
    fun showBiometricPrompt() {
        activity.runOnUiThread {
            BiometricPromptUtils
                .build(activity)
                .setOnBiometricPromptListener(object : BiometricPromptListener {
                    override fun success() {
                        activity.biometricPromptResult(true)
                    }

                    override fun failed() {
                        activity.biometricPromptResult(false)
                    }

                    override fun cancel() {
                        activity.biometricPromptResult(false)
                    }

                    override fun error(errorCode: Int, errString: CharSequence) {
                        activity.biometricPromptResult(false)
                    }
                })
                .showBiometricPromptForEncryption()
        }
    }

}