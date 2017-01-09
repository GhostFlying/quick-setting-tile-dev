package com.ghostflying.qstilesfordev.util

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import com.ghostflying.qstilesfordev.R
import com.ghostflying.qstilesfordev.act.AlertActivity

/**
 * Created by ghostflying on 2017/1/9.
 */

class SystemSettingUtil {
    companion object {
        val TAG = "SystemSettingUtil"

        val instance : SystemSettingUtil by lazy { SystemSettingUtil() }
    }

    private constructor() {
        Logger.d(TAG, "new SystemSettingUtil")
    }

    fun checkWriteSystemPermission(context : Context) : Boolean {
        return Settings.System.canWrite(context)
    }

    fun promptUserToGrant(context: Context) {
        AlertActivity.startActivity(context, R.string.screen_time_out_alert_message, Settings.ACTION_MANAGE_WRITE_SETTINGS)
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