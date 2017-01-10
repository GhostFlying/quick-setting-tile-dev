package com.ghostflying.qstilesfordev.util

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import com.ghostflying.qstilesfordev.BuildConfig

/**
 * Created by ghostflying on 2017/1/9.
 */
class SecureSettingUtil {

    companion object {
        val TAG = "SecureSettingUtil"

        val instance : SecureSettingUtil by lazy { SecureSettingUtil() }

        val COMMAND_GRANT_PERMISSION = "pm grant %s %s"

        val ADB_USB_DEBUG_ENABLE_VALUE = "1"
        val ADB_USB_DEBUG_DISABLE_VALUE = "0"
    }

    private constructor() {
        Logger.d(TAG, "new SecureSettingUtil")
    }

    fun checkPermissionIsGranted(context: Context) : Boolean {
        return context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun grantPermissionIfNeeded(context : Context) {
        if (context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {
            // try to grant permission
            // TODO prompt user when no root permission
            CommandUtil.instance.runCommandWithRoot(
                    COMMAND_GRANT_PERMISSION.format(
                            BuildConfig.APPLICATION_ID,
                            Manifest.permission.WRITE_SECURE_SETTINGS
                    ),
                    true
            )
        }
    }

    fun enableUsbDebug(cr: ContentResolver) {
        Logger.d(TAG, "enable usb debug")
        setSecureSettingValue(cr, Settings.Global.ADB_ENABLED, ADB_USB_DEBUG_ENABLE_VALUE)
    }

    fun disableUsbDebug(cr: ContentResolver) {
        Logger.d(TAG, "disable usb debug")
        setSecureSettingValue(cr, Settings.Global.ADB_ENABLED, ADB_USB_DEBUG_DISABLE_VALUE)
    }

    private fun setSecureSettingValue(cr : ContentResolver, key : String, value : String) {
        Settings.Global.putString(cr, key, value)
    }
}