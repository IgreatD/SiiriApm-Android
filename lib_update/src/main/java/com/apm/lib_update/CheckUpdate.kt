package com.apm.lib_update

import com.apm.lib_update.bean.UpdateConfig
import com.apm.lib_update.bean.UpdateEntity
import com.apm.lib_update.listener.OnBtnClickListener
import com.apm.lib_update.update.UpdateAppUtils
import com.blankj.utilcode.util.AppUtils

/**
 * @author: dinglei
 * @date: 2020/9/13 09:29
 */
object CheckUpdate {

    fun check(entity: UpdateEntity, appDomain: String) {
        val serverVersion = entity.packageVersion
        val currentVersion = AppUtils.getAppVersionName()
        val compare = currentVersion.compareTo(serverVersion)
        if (compare < 0)
            updateApk(entity, appDomain)
    }

    private fun updateApk(entity: UpdateEntity, appDomain: String) {
        val apkUrl = "${appDomain}release/file?id=${entity.id}"
        UpdateAppUtils.getInstance()
            .apkUrl(apkUrl)
            .updateTitle("${entity.title}  V${entity.packageVersion}")
            .updateContent(entity.publishContent)
            .updateConfig(
                UpdateConfig(
                    force = entity.isMandatory
                )
            )
            .setBackPressBtnClickListener(object : OnBtnClickListener {
                override fun onClick(): Boolean {
                    AppUtils.exitApp()
                    return false
                }

            })
            .setCancelBtnClickListener(object : OnBtnClickListener {
                override fun onClick(): Boolean {
                    return false
                }

            })
            .update()
    }

}