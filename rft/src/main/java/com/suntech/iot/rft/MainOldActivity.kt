package com.suntech.iot.rft

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.suntech.iot.rft.base.BaseActivity
import com.suntech.iot.rft.common.AppGlobal
import kotlinx.android.synthetic.main.activity_main.btn_defect2
import kotlinx.android.synthetic.main.activity_main.btn_input2
import kotlinx.android.synthetic.main.activity_main.btn_setting
import kotlinx.android.synthetic.main.activity_main.et_qty2
import kotlinx.android.synthetic.main.activity_main.tv_line_title2
import kotlinx.android.synthetic.main.activity_main.tv_time2
import kotlinx.android.synthetic.main.activity_main_old.*
import kotlinx.android.synthetic.main.layout_top_menu.*
import java.util.*

class MainOldActivity : BaseActivity() {

    private var _selected_line1_time: String = ""
    private var _selected_line2_time: String = ""

    private var _doubleBackToExitPressedOnce = false

    private val _broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)){
                    btn_wifi_state.isSelected = true
                } else {
                    btn_wifi_state.isSelected = false
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_old)

        AppGlobal.instance.setContext(this)

        tv_time1.setOnClickListener {
            if (AppGlobal.instance.get_line1_idx() == "") {
                Toast.makeText(this, "Line not set", Toast.LENGTH_SHORT).show()
            } else {
                choiceTime(1)
            }
        }
        tv_time2.setOnClickListener {
            if (AppGlobal.instance.get_line2_idx() == "") {
                Toast.makeText(this, "Line not set", Toast.LENGTH_SHORT).show()
            } else {
                choiceTime(2)
            }
        }

        btn_input1.setOnClickListener { sendEditCount(1, "I") }
        btn_defect1.setOnClickListener { sendEditCount(1, "D") }

        btn_input2.setOnClickListener { sendEditCount(2, "I") }
        btn_defect2.setOnClickListener { sendEditCount(2, "D") }

        btn_setting.setOnClickListener { startActivity(Intent(this, SettingActivity::class.java)) }

        start_timer()
    }

    public override fun onResume() {
        super.onResume()

        val line1 = AppGlobal.instance.get_line1()
        if (line1 != "") {
            tv_line_title1.text = line1
            tv_line_title1.setTextColor(Color.parseColor("#f8ad13"))
            et_qty1.isEnabled = true
        } else {
            tv_line_title1.text = "LINE 1"
            tv_line_title1.setTextColor(Color.parseColor("#888888"))

            _selected_line1_time = ""
            tv_time1.text = "-"
            et_qty1.isEnabled = false
            et_qty1.setText("")
        }

        val line2 = AppGlobal.instance.get_line2()
        if (line2 != "") {
            tv_line_title2.text = line2
            tv_line_title2.setTextColor(Color.parseColor("#f8ad13"))
            et_qty2.isEnabled = true
        } else {
            tv_line_title2.text = "LINE 2"
            tv_line_title2.setTextColor(Color.parseColor("#888888"))

            _selected_line2_time = ""
            tv_time2.text = "-"
            et_qty2.isEnabled = false
            et_qty2.setText("")
        }

        registerReceiver(_broadcastReceiver, IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION))

        if (AppGlobal.instance.isOnline(this)) btn_wifi_state.isSelected = true
        else btn_wifi_state.isSelected = false
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(_broadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel_timer()
    }

    override fun onBackPressed() {
        if (_doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this._doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ _doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun choiceTime(kind: Int) {
        val line_idx = if (kind == 1) AppGlobal.instance.get_line1_idx() else AppGlobal.instance.get_line2_idx()
        if (line_idx == null || line_idx == "") {
            Toast.makeText(this, "Line not set", Toast.LENGTH_SHORT).show()
            return
        }

        var arr: ArrayList<String> = arrayListOf<String>()
        var lists : ArrayList<HashMap<String, String>> = arrayListOf()

        for (i in 7..11) {
            val text = (if (i<10) "0" else "") + i.toString() + " - " + (if (i<9) "0" else "") + (i+1).toString()
            var map=hashMapOf(
                "time" to i.toString(),
                "text" to text
            )
            lists.add(map)
            arr.add(text)
        }
        for (i in 13..19) {
            val text = (if (i<10) "0" else "") + i.toString() + " - " + (if (i<9) "0" else "") + (i+1).toString()
            var map=hashMapOf(
                "time" to i.toString(),
                "text" to text
            )
            lists.add(map)
            arr.add(text)
        }

        val intent = Intent(this, PopupSelectList::class.java)
        intent.putStringArrayListExtra("list", arr)
        startActivity(intent, { r, c, m, d ->
            if (r) {
                if (kind == 1) {
                    _selected_line1_time = lists[c]["time"] ?: ""
                    tv_time1.text = lists[c]["text"] ?: "Select a time"
                } else {
                    _selected_line2_time = lists[c]["time"] ?: ""
                    tv_time2.text = lists[c]["text"] ?: "Select a time"
                }
            }
        })
    }

    private fun sendEditCount(kind: Int, gubun: String = "") {
        if (gubun != "I" && gubun != "D") {
            Toast.makeText(this, "Invalid separator code", Toast.LENGTH_SHORT).show()
            return
        }
        val line_idx = if (kind == 1) AppGlobal.instance.get_line1_idx() else AppGlobal.instance.get_line2_idx()
        val time = if (kind == 1) _selected_line1_time else _selected_line2_time
        val qty = if (kind == 1) et_qty1.text.toString().trim() else et_qty2.text.toString().trim()

        if (line_idx == null || line_idx == "") {
            Toast.makeText(this, "Line not set", Toast.LENGTH_SHORT).show()
            return
        }
        if (time == "") {
            Toast.makeText(this, "No time selected", Toast.LENGTH_SHORT).show()
            return
        }
        if (qty == "") {
            Toast.makeText(this, "No quantity entered", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, PasswordCheckActivity::class.java)
        startActivity(intent, { r, c, m, d ->
            if (r) {
                val uri = "/Srft.php"
                var params = listOf(
                    "line_idx" to line_idx,
                    "time" to time,
                    "gubun" to gubun,
                    "qty" to qty)
//        Log.e("Srft params", params.toString())
                request(this, uri, true,true, params, { result ->
                    var code = result.getString("code")
                    var msg = result.getString("msg")

                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

                    if(code == "00") {
                        _selected_line1_time = ""
                        _selected_line2_time = ""
                        tv_time1.text = "-"
                        tv_time2.text = "-"
                        et_qty1.setText("")
                        et_qty2.setText("")
                    }
                })
            }
        })
    }


    /////// 쓰레드
    private val _timer_task1 = Timer()          // 서버 접속 체크 Ping test. Shift의 Target 정보

    private fun start_timer() {

        val task1 = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    sendPing()
                }
            }
        }
        _timer_task1.schedule(task1, 2000, 10000)   // 10초마다
    }

    private fun cancel_timer() {
        _timer_task1.cancel()
    }

    private fun sendPing() {
        tv_ms.text = "-" + " ms"
        if (AppGlobal.instance.get_server_ip() == "") return

        val currentTimeMillisStart = System.currentTimeMillis()
        val uri = "/ping.php"
        request(this, uri, false, false, null, { result ->
            val currentTimeMillisEnd = System.currentTimeMillis()
            val millis = currentTimeMillisEnd - currentTimeMillisStart

            var code = result.getString("code")
            if (code == "00") {
                btn_server_state.isSelected = true
                tv_ms.text = "" + millis + " ms"
            } else {
                Toast.makeText(this, result.getString("msg"), Toast.LENGTH_SHORT).show()
            }
        }, {
            btn_server_state.isSelected = false
        })
    }
}
