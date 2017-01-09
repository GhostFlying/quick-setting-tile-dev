package com.ghostflying.qstilesfordev.service

import android.provider.Settings
import com.ghostflying.qstilesfordev.R
import com.ghostflying.qstilesfordev.util.Logger
import com.ghostflying.qstilesfordev.util.SystemSettingUtil

/**
 * Created by ghostflying on 2017/1/9.
 */
class ScreenOffTimeoutQSService : BaseQSService(){

    companion object {
        val TAG = "ScreenOffTimeoutQSService"

        val TIMEOUT_ARRAY = intArrayOf(2, 5, 30)
    }

    private var mCurrentIdx = -1

    override fun getTAG(): String {
        return TAG
    }

    override fun onTileAdded() {
        super.onTileAdded()
    }

    override fun onStartListening() {
        super.onStartListening()

        if (!SystemSettingUtil.instance.checkWriteSystemPermission(this)) {
            qsTile.label = getString(R.string.screen_time_out_click_to_start)
            qsTile.updateTile()
            return
        }

        updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()
    }

    private fun updateTile() {
        val timeoutMilliseconds = SystemSettingUtil.instance.getIntSystemSetting(
                contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT
        )

        Logger.d(TAG, "current timeout is " + timeoutMilliseconds)

        val timeoutInMin = timeoutMilliseconds / 1000 / 60
        qsTile.label = getString(R.string.screen_time_out_label).format(timeoutInMin)
        qsTile.updateTile()

        mCurrentIdx = findCurrentIdx(timeoutInMin)
    }

    override fun onClick() {
        super.onClick()

        if (!SystemSettingUtil.instance.checkWriteSystemPermission(this)) {

            collapseStatusBar()

            SystemSettingUtil.instance.promptUserToGrant(this)

            return
        }

        val idxToSet = (mCurrentIdx + 1) % TIMEOUT_ARRAY.size

        SystemSettingUtil.instance.setIntSystemSetting(
                contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT,
                TIMEOUT_ARRAY[idxToSet] * 1000 * 60
        )
        updateTile()
    }

    private fun findCurrentIdx(current : Int) : Int {
        val ret = TIMEOUT_ARRAY.indices
                .takeWhile { current >= TIMEOUT_ARRAY[it] }
                .lastOrNull()
                ?: 0

        return ret
    }
}