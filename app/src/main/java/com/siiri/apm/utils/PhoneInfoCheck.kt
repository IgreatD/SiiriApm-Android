package com.siiri.apm.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.text.TextUtils

/**
 * @author: dinglei
 * @date: 2020/6/15 15:12
 */
object PhoneInfoCheck {

    fun startFingerprint(context: Context, brand: String) {
        var pcgName = ""
        var clsName = ""
        when (brand) {
            PhoneBrand.SONY.name -> {
                pcgName = "com.android.settings";
                clsName = "com.android.settings.Settings$" + "FingerprintEnrollSuggestionActivity";
            }
            PhoneBrand.HONOR.name -> {
                pcgName = "com.android.settings";
                clsName = "com.android.settings.fingerprint.FingerprintSettingsActivity";
            }
            PhoneBrand.HUAWEI.name -> {
                pcgName = "com.android.settings";
                clsName = "com.android.settings.fingerprint.FingerprintSettingsActivity";
            }
            PhoneBrand.OPPO.name -> {
                pcgName = "com.coloros.fingerprint";
                clsName = "com.coloros.fingerprint.FingerLockActivity";
            }
            else -> {
                pcgName = "com.android.settings";
                clsName = "com.android.settings.Settings";
            }
        }
        if (!TextUtils.isEmpty(pcgName) && !TextUtils.isEmpty(clsName)) {
            val intent = Intent()
            val componentName = ComponentName(pcgName, clsName)
            intent.action = Intent.ACTION_VIEW
            intent.component = componentName
            context.startActivity(intent)
        }
    }

}

enum class PhoneBrand {
    SONY, OPPO, HUAWEI, HONOR
}