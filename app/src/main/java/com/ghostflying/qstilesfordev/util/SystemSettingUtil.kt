package com.ghostflying.qstilesfordev.util

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings

/**
 * Created by ghostflying on 2017/1/9.
 */

class SystemSettingUtil {
    companion object {
        val TAG = "SystemSettingUtil"

        val instance : SystemSettingUtil by lazy { SystemSettingUtil() }

        val ACTION_GRANT_PERMISSION = Settings.ACTION_MANAGE_WRITE_SETTINGS
    }

    private constructor() {
        Logger.d(TAG, "new SystemSettingUtil")
    }

    fun checkWriteSystemPermission(context : Context) : Boolean {
        return Settings.System.canWrite(context)
    }

    fun getIntSystemSetting(cr: ContentResolver, key: String) : Int {
        Logger.d(TAG, "getIntSystemSetting")
        return Settings.System.getInt(cr, key)
    }

    fun setIntSystemSetting(cr : ContentResolver, key : String, value : Int) {
        Logger.d(TAG, "setIntSystemSetting")
        Settings.System.putInt(cr, key, value)
    }
}