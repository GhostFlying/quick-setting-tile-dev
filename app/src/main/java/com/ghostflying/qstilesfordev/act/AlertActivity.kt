package com.ghostflying.qstilesfordev.act

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ghostflying.qstilesfordev.R
import com.ghostflying.qstilesfordev.util.Logger
import kotlinx.android.synthetic.main.activity_alert.*


class AlertActivity : AppCompatActivity() {

    companion object {
        val TAG = "AlertActivity"

        val ARG_NAME_ALERT_MESSAGE_RESID = "arg_alert_message_res_id"
        val ARG_NAME_ALERT_CONFIRM_JUMO_INTENT = "arg_confirm_jump_intent"

        fun startActivity(context : Context, messageResId : Int, intentToJump : String) {
            val intent = Intent(context, AlertActivity::class.java)
            intent.putExtra(ARG_NAME_ALERT_MESSAGE_RESID, messageResId)
            intent.putExtra(ARG_NAME_ALERT_CONFIRM_JUMO_INTENT, intentToJump)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)

        Logger.d(TAG, "onCreate")

        parseArgs(intent.extras)
    }

    fun parseArgs(bundle: Bundle?) {
        if (bundle == null) {
            throw IllegalArgumentException("must pass args by intent")
        }

        alert_tv.text = getString(bundle.getInt(ARG_NAME_ALERT_MESSAGE_RESID))
        confirm_btn.setOnClickListener {
            startActivity(Intent(bundle.getString(ARG_NAME_ALERT_CONFIRM_JUMO_INTENT)))
        }
    }
}
