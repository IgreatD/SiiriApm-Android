package com.apm.lib_fingerprint.listener

/**
 * @author: dinglei
 * @date: 2020/6/16 14:59
 */
interface BiometricPromptListener {
    fun start() {}
    fun success()
    fun failed() {}
    fun error(errorCode: Int, errString: CharSequence) {}
    fun cancel()
}