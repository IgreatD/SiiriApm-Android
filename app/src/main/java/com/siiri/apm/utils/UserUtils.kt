package com.siiri.apm.utils

import com.blankj.utilcode.util.GsonUtils
import com.siiri.apm.entity.UserInfo

/**
 * @author: dinglei
 * @date: 2020/8/10 14:55
 */
object UserUtils {
    var userInfoStr by SharePreferenceDelegate("key_user_info", "")

    private fun getUserInfo(): UserInfo? {
        if (userInfoStr.isEmpty()) return null
        return GsonUtils.fromJson<UserInfo>(userInfoStr, UserInfo::class.java)
    }

    fun getPersonId(): Int? {
        return getUserInfo()?.personId
    }

    fun getUserPhone(): String? {
        return getUserInfo()?.userPhone
    }

}