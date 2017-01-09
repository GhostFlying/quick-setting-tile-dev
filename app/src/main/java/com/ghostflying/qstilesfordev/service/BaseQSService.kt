package com.ghostflying.qstilesfordev.service

import android.service.quicksettings.TileService
import com.ghostflying.qstilesfordev.util.Logger

/**
 * Created by ghostflying on 2017/1/9.
 */
abstract class BaseQSService : TileService() {

    companion object {
        val TAG = "BaseQSService"
    }

    override fun onTileAdded() {
        super.onTileAdded()

        Logger.d(getTAG(), "onTileAdded")
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        Logger.d(getTAG(), "onTileRemoved")
    }

    override fun onStartListening() {
        super.onStartListening()

        Logger.d(getTAG(), "onStartListening")
    }

    override fun onStopListening() {
        super.onStopListening()

        Logger.d(getTAG(), "onStopListening")
    }

    override fun onClick() {
        super.onClick()

        Logger.d(getTAG(), "onClick")
    }

    fun collapseStatusBar() {
        // try to collapse status bar by reflection

        Logger.d(getTAG(), "try to collapse status bar")
        try {
            val statusManager = this.getSystemService("statusbar");
            val collapse = statusManager.javaClass.getMethod("collapsePanels")
            collapse.invoke(statusManager)
        }
        catch (e : Exception) {
            e.printStackTrace()
        }
    }

    open fun getTAG() : String {
        return TAG
    }
}