package com.ghostflying.qstilesfordev.util

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.io.OutputStream

/**
 * Created by ghostflying on 2017/1/9.
 */

class CommandUtil {
    companion object {
        val instance : CommandUtil by lazy { CommandUtil() }

        private val TAG = "CommandUtil"

        private val COMMAND_GET_PROP = "getprop "
        private val COMMAND_SET_PROP = "setprop %s %s"
    }

    private constructor() {
        Logger.d(TAG, "new command util")
    }

    fun getProp(propName : String) : String {
        return getResultForCommand(COMMAND_GET_PROP + propName)
    }

    fun setProp(propName: String, value : String) {
        runCommandWithRoot(COMMAND_SET_PROP.format(propName, value), false)
    }

    private fun getResultForCommand(command : String) : String {
        val p = Runtime.getRuntime().exec(command)

        val stdout = BufferedReader(InputStreamReader(p.inputStream))

        val retBuilder = StringBuilder()

        for (line in stdout.lines()) {
            Logger.d(TAG, line)
            if (!retBuilder.isEmpty()) {
                retBuilder.append("\n")
            }
            retBuilder.append(line)
        }

        return retBuilder.toString()
    }

    fun runCommandWithRoot(command : String, blockForExit : Boolean) {
        Logger.d(TAG, "run command: " + command)

        val p = Runtime.getRuntime().exec("su")

        val stdin = DataOutputStream(p.outputStream)

        stdin.writeBytes(command +"\n")
        stdin.writeBytes("exit\n")
        stdin.flush()

        if (blockForExit) {
            p.waitFor()
        }
    }
}