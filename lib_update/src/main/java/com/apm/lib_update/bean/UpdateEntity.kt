package com.apm.lib_update.bean

import com.google.gson.annotations.SerializedName


/**
 * @author: dinglei
 * @date: 2020/6/4 10:44
 */
data class UpdateEntity(
    @SerializedName("gmtCreate")
    val gmtCreate: String = "", // 2020-06-04T02:27:01.081Z
    @SerializedName("gmtModified")
    val gmtModified: String = "", // 2020-06-04T02:27:01.081Z
    @SerializedName("id")
    val id: Int = 0, // 0
    @SerializedName("isMandatory")
    val isMandatory: Boolean = false, // true
    @SerializedName("packageName")
    val packageName: String = "", // string
    @SerializedName("packageVersion")
    val packageVersion: String = "", // string
    @SerializedName("publishContent")
    val publishContent: String = "", // string
    @SerializedName("title")
    val title: String = "", // string
    @SerializedName("url")
    val url: String = "" // string
)