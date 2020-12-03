package com.apm.lib_fingerprint.extensions

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * @author: dinglei
 * @date: 2020/6/16 15:56
 */
fun AppCompatActivity.toast(toast: String) {
    Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
}