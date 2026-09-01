package com.kyu.jiu_jitsu.data.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build

object DeviceIdUtil {

    /** Get ANDROID_ID **/
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        val androidId = android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )

        return androidId ?: ""
    }

    /** Get AndroidOs Version **/
    fun getOsVersion(): String = Build.VERSION.RELEASE

}