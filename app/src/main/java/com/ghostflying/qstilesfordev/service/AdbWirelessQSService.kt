package com.ghostflying.qstilesfordev.service

import android.content.Context
import android.net.wifi.WifiManager
import android.service.quicksettings.Tile
import android.widget.Toast
import com.ghostflying.qstilesfordev.R
import com.ghostflying.qstilesfordev.util.CommandUtil
import com.ghostflying.qstilesfordev.util.Logger
import com.ghostflying.qstilesfordev.util.SecureSettingUtil


class AdbWirelessQSService : BaseQSService() {

    companion object {
        private val TAG  = "AdbWirelessQSService"

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

    private var mCurrentState = -1

    override fun getTAG(): String {
        return TAG
    }

    override fun onTileAdded() {
        super.onTileAdded()

        Toast.makeText(this, R.string.adb_tile_alert_message, Toast.LENGTH_LONG).show()
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

        if (mCurrentState < 0) {
            Logger.d(TAG, "tile is not prepared")
            return
        }

        if (mCurrentState == STATE_DISABLED) {
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
        Logger.d(TAG, "start chcecking")

        val adbState = CommandUtil.instance.getProp(PROP_ADB_IS_RUNNING)
        Logger.d(TAG, "current adb state is " + adbState)

        if (adbState == PROP_ADB_IS_RUNNING_FALSE) {
            markAdbDisabled()
            return
        }


        val portCurrent = CommandUtil.instance.getProp(PROP_ADB_TCP_PORT)
        Logger.d(TAG, "current adb tcp port is " + portCurrent)

        if (portCurrent.isBlank()) {
            Logger.d(TAG, "check adb wireless current stat fail")
            return
        }

        if (portCurrent == PROP_ADB_TCP_PORT_DISABLED) {
            markAdbUsb()
        } else {
            markAdbWireless(portCurrent)
        }
    }

    private fun markChecking() {
        qsTile.label = getString(R.string.adb_tile_title_checking)
        qsTile.state = Tile.STATE_UNAVAILABLE
        qsTile.updateTile()
    }

    private fun markAdbWireless(portCurrent: String) {
        qsTile.label = getString(R.string.adb_tile_title_enabled).format(getIpAddr(), portCurrent)
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
        mCurrentState = STATE_WIRELESS
    }

    private fun markAdbUsb() {
        qsTile.label = getString(R.string.adb_tile_title_usb)
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
        mCurrentState = STATE_USB
    }

    private fun markAdbDisabled() {
        qsTile.label = getString(R.string.adb_tile_title_disabled)
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
