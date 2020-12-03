package com.siiri.push

import android.content.Context
import cn.jpush.android.api.JPushInterface

/**
 * @author: dinglei
 * @date: 2020/9/12 09:51
 */
object JPushUtils {

    fun init(context: Context) {
        JPushInterface.setDebugMode(BuildConfig.DEBUG)
        JPushInterface.init(context)
    }

    fun setAliasPush(context: Context, alias: String) {
        updateAliasPush(context, TagAliasOperatorHelper.ACTION_SET, alias)
    }

    fun delAliasPush(context: Context, alias: String) {
        updateAliasPush(context, TagAliasOperatorHelper.ACTION_DELETE, alias)
    }

    private fun updateAliasPush(context: Context, action: Int, alias: String) {
        val tagAliasBean = TagAliasOperatorHelper.TagAliasBean()
        tagAliasBean.action = action
        tagAliasBean.alias = alias
        tagAliasBean.isAliasAction = true
        TagAliasOperatorHelper
            .getInstance()
            ?.handleAction(context, 1, tagAliasBean)
    }

}