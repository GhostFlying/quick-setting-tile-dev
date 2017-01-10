package com.ghostflying.qstilesfordev.service

import android.content.Context
import android.content.DialogInterface
import android.net.wifi.WifiManager
import android.service.quicksettings.Tile
import com.ghostflying.qstilesfordev.R
import com.ghostflying.qstilesfordev.util.CommandUtil
import com.ghostflying.qstilesfordev.util.DialogUtil
import com.ghostflying.qstilesfordev.util.Logger
import com.ghostflying.qstilesfordev.util.SecureSettingUtil


class AdbWirelessQSService : BaseQSService() {

    companion object {
        private val TAG  = "AdbWirelessQSService"

        private val STATE_NO_PERMISSION = -1
        private val STATE_DISABLED = 0
        private val STATE_USB = 1
        private val STATE_WIRELESS = 2

        private val PROP_ADB_TCP_PORT = "service.adb.tcp.port"
        private val PROP_ADB_TCP_PORT_DISABLED = "0"
        private val PROP_ADB_TCP_PORT_DEFAULT = "5555"

        private val PROP_ADB_IS_RUNNING = "init.svc.adbd"
        private val PROP_ADB_IS_RUNNING_FALSE = "stopped"

        private val COMMAND_RESTART_ADB = "stop adbd\nstart adbd\n"
    }

    private var mCurrentState = STATE_NO_PERMISSION

    override fun getTAG(): String {
        return TAG
    }

    override fun onTileAdded() {
        super.onTileAdded()
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
    }

    override fun onStartListening() {
        super.onStartListening()

        checkCurrentState()

    }

    override fun onStopListening() {
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()

        if (mCurrentState < -1) {
            Logger.d(TAG, "tile is not prepared")
            return
        }

        if (mCurrentState == STATE_NO_PERMISSION) {
            val dialog = DialogUtil.instance.getAlertDialog(
                    this,
                    R.string.adb_tile_alert_message,
                    R.string.adb_tile_alert_confirm,
                    DialogInterface.OnClickListener {
                        dialog,
                        which ->
                        dialog.dismiss()
                        SecureSettingUtil.instance.
                            grantPermissionIfNeeded(this@AdbWirelessQSService)
                    }
            )
            showDialog(dialog)
        }
        else if (mCurrentState == STATE_DISABLED) {
            CommandUtil.instance.setProp(PROP_ADB_TCP_PORT, PROP_ADB_TCP_PORT_DISABLED)
            SecureSettingUtil.instance.grantPermissionIfNeeded(this)
            SecureSettingUtil.instance.enableUsbDebug(contentResolver)
            markAdbUsb()
        }
        else if (mCurrentState == STATE_USB) {
            CommandUtil.instance.setProp(PROP_ADB_TCP_PORT, PROP_ADB_TCP_PORT_DEFAULT)
            CommandUtil.instance.runCommandWithRoot(COMMAND_RESTART_ADB, false)
            markAdbWireless(PROP_ADB_TCP_PORT_DEFAULT)
        }
        else {
            CommandUtil.instance.setProp(PROP_ADB_TCP_PORT, PROP_ADB_TCP_PORT_DISABLED)
            SecureSettingUtil.instance.grantPermissionIfNeeded(this)
            SecureSettingUtil.instance.disableUsbDebug(contentResolver)
            markAdbDisabled()
        }
    }

    private fun checkCurrentState() {
        Logger.d(TAG, "start checking")

        if (!SecureSettingUtil.instance.checkPermissionIsGranted(this)) {
            Logger.d(TAG, "need grant permission")
            markClickToStart()
            return
        }

        val adbState = CommandUtil.instance.getProp(PROP_ADB_IS_RUNNING)
        Logger.d(TAG, "current adb state is " + adbState)

        if (adbState == PROP_ADB_IS_RUNNING_FALSE) {
            markAdbDisabled()
            return
        }

        val portCurrent = CommandUtil.instance.getProp(PROP_ADB_TCP_PORT)
        Logger.d(TAG, "current adb tcp port is " + portCurrent)

        if (portCurrent == PROP_ADB_TCP_PORT_DISABLED || portCurrent.isBlank()) {
            markAdbUsb()
        } else {
            markAdbWireless(portCurrent)
        }
    }

    private fun markClickToStart() {
        qsTile.label = getString(R.string.adb_tile_label_click_to_start)
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
        mCurrentState = STATE_NO_PERMISSION
    }

    private fun markAdbWireless(portCurrent: String) {
        qsTile.label = getString(R.string.adb_tile_label_enabled).format(getIpAddr(), portCurrent)
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
        mCurrentState = STATE_WIRELESS
    }

    private fun markAdbUsb() {
        qsTile.label = getString(R.string.adb_tile_label_usb)
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
        mCurrentState = STATE_USB
    }

    private fun markAdbDisabled() {
        qsTile.label = getString(R.string.adb_tile_label_disabled)
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
        mCurrentState = STATE_DISABLED
    }

    private fun getIpAddr(): String {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val ip = wifiInfo.ipAddress

        val ipString = String.format(
                "%d.%d.%d.%d",
                ip and 0xff,
                ip shr 8 and 0xff,
                ip shr 16 and 0xff,
                ip shr 24 and 0xff)

        return ipString
    }
}
