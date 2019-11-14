package com.suntech.iot.rft

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.suntech.iot.rft.base.BaseActivity
import com.suntech.iot.rft.common.AppGlobal

class IntroActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        AppGlobal.instance.setContext(this)

        Log.e("settings", "Server IP = " + AppGlobal.instance.get_server_ip())
        Log.e("settings", "Mac addr = " + AppGlobal.instance.getMACAddress())
        Log.e("settings", "IP addr " + AppGlobal.instance.get_local_ip())


        Handler().postDelayed({
            moveToNext()
        }, 1000)
    }

    private fun moveToNext() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}