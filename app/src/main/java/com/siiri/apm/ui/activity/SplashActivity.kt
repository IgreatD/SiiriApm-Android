package com.siiri.apm.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.siiri.apm.extension.asyncAndAwait

/**
 * @author: dinglei
 * @date: 2020/9/12 10:25
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        asyncAndAwait({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 500)
    }

}