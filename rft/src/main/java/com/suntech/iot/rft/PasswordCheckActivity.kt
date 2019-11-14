package com.suntech.iot.rft

import android.os.Bundle
import android.widget.Toast
import com.suntech.iot.rft.base.BaseActivity
import com.suntech.iot.rft.common.AppGlobal
import kotlinx.android.synthetic.main.activity_password_check.*

class PasswordCheckActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_check)

        btn_confirm.setOnClickListener { checkPassword() }
        btn_cancel.setOnClickListener { finish(false, 0, "ok", null) }
    }

    private fun checkPassword() {

        val current_password = et_password.text.toString()

//        if (current_password.trim() == "") {
//            Toast.makeText(this, "Enter current password", Toast.LENGTH_SHORT).show()
//            return
//        }

        var app_password = AppGlobal.instance.get_password()
//        if (app_password == "") app_password = "1234"

        if (current_password != app_password) {
            Toast.makeText(this, "Password is invalid", Toast.LENGTH_SHORT).show()
            return
        }

        finish(true, 1, "ok", null)
    }
}