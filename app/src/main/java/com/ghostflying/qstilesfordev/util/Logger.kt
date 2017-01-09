package com.ghostflying.qstilesfordev.util

import android.util.Log
import com.ghostflying.qstilesfordev.BuildConfig

/**
 * Created by ghostflying on 2017/1/9.
 */
class Logger {

    companion object {
        fun d(tag : String, msg : String) {
            if (BuildConfig.DEBUG) {
                Log.d(tag, msg)
            }
        }
    }
}