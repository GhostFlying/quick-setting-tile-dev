package com.ghostflying.qstilesfordev.service

import android.service.quicksettings.TileService
import android.widget.Toast
import com.ghostflying.qstilesfordev.R
import com.ghostflying.qstilesfordev.util.Logger

/**
 * Created by ghostflying on 2017/1/9.
 */
abstract class BaseQSService : TileService() {

    override fun onTileAdded() {
        super.onTileAdded()

        Logger.d(getTAG(), "onTileAdded")
        Toast.makeText(this, R.string.adb_tile_alert_message, Toast.LENGTH_LONG).show()
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

    abstract fun getTAG() : String
}