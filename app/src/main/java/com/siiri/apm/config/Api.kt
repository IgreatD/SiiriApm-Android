package com.siiri.apm.config

import com.siiri.apm.BuildConfig

/**
 * @author: dinglei
 * @date: 2020/9/13 09:51
 */
interface Api {
    companion object{
        const val APP_DOMAIN = BuildConfig.API_DOMAIN
    }
}