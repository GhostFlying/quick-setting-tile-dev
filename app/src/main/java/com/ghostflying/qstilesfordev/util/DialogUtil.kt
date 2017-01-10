package com.ghostflying.qstilesfordev.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

/**
 * Created by ghostflying on 2017/1/10.
 */
class DialogUtil {

    companion object {
        val instance : DialogUtil by lazy { DialogUtil() }

        val TAG = "DialogUtil"
    }

    private constructor()

    fun getAlertDialog(
            context: Context,
            message: Int,
            positiveButtonText : Int,
            positiveHandler: DialogInterface.OnClickListener): AlertDialog {

        val builder = AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveHandler)

        return builder.create()
    }
}